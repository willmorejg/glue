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
package net.ljcomputing.glue.route;

import net.ljcomputing.glue.processor.DebeziumProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaRoute extends RouteBuilder {
    private final DebeziumProcessor debeziumProcessor;

    private final String loggerClassname;

    public KafkaRoute(@Autowired final DebeziumProcessor debeziumProcessor) {
        this.debeziumProcessor = debeziumProcessor;
        this.loggerClassname = this.getClass().getName();
    }

    @Override
    public void configure() throws Exception {
        from("kafka:debezium-trash.public.trash?brokers=glue_broker:50002&pollTimeoutMs=60000")
                .log(LoggingLevel.DEBUG, loggerClassname, "Message received from Kafka : \n${body}")
                .log(LoggingLevel.DEBUG, loggerClassname, "   ==>>\n headers ${headers} \n<<==")
                .process(debeziumProcessor);
    }
}
