/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

James G Willmore - LJ Computing - (C) 2023
*/
package net.ljcomputing.glue.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"net.ljcomputing.glue.repository.gluedb2"},
        entityManagerFactoryRef = "gluedb2EntityManagerFactory",
        transactionManagerRef = "gluedb2TransactionManager")
public class JpaDb2Config {
    @Bean(name = "gluedb2DataSourceProperties")
    @ConfigurationProperties("spring.datasource.gluedb2")
    public DataSourceProperties gluedb2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "gluedb2DataSource")
    public DataSource gluedb2DataSource() {
        return gluedb2DataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "gluedb2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean gluedb2EntityManagerFactory(
            @Qualifier("gluedb2DataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        final Map<String, String> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.hbm2ddl.show-sql", "true");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.DB2Dialect");

        return builder.dataSource(dataSource)
                .packages("net.ljcomputing.glue.entity")
                .persistenceUnit("gluedb2DataSource")
                .properties(jpaProperties)
                .build();

        // return builder.dataSource(dataSource).packages(Trash.class).build();
    }

    @Bean(name = "gluedb2TransactionManager")
    public PlatformTransactionManager gluedb2TransactionManager(
            @Qualifier("gluedb2EntityManagerFactory")
                    LocalContainerEntityManagerFactoryBean gluedb2EntityManagerFactory) {
        return new JpaTransactionManager(
                Objects.requireNonNull(gluedb2EntityManagerFactory.getObject()));
    }
}
