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

import java.util.List;
import net.ljcomputing.glue.entity.Trash;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class RestRoute extends RouteBuilder {
    private final Environment env;

    @Value("${camel.servlet.mapping.context-path}")
    private String contextPath;

    public RestRoute(@Autowired final Environment env) {
        this.env = env;
    }

    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .port(env.getProperty("server.port", "8080"))
                .contextPath(contextPath.substring(0, contextPath.length() - 2))
                // turn on openapi api-doc
                .apiContextPath("/api-docs")
                .apiContextRouteId("swagger")
                .apiProperty("api.title", "User API")
                .apiProperty("api.version", "0.0.1");

        rest("/log")
                .description("Log endpoint")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)

                // POST - log message
                .post()
                .description("Log a message")
                .type(Object.class)
                .param()
                .name("body")
                .description("Message to log")
                .endParam()
                .responseMessage()
                .code(200)
                .message("Message logged.")
                .endResponseMessage()
                .to("log:net.ljcomputing.glue.route.RestRoute?level=DEBUG&plain=true");

        rest("/trash")
                .description("Trash endpoint")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                // GET - find all
                .get()
                .description("Find all trash")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .outType(List.class)
                .responseMessage()
                .code(200)
                .message("All trash successfully returned")
                .responseModel(List.class)
                .endResponseMessage()
                .to("bean:pgTrashService?method=findAll")

                // POST - create
                .post()
                .description("Create trash")
                .type(Trash.class)
                .outType(Trash.class)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .responseMessage()
                .code(200)
                .message("Trash created successfully")
                .responseModel(Trash.class)
                .endResponseMessage()
                .param()
                .name("body")
                .description("The trash to update")
                .endParam()
                .to("direct:saveTrash")
                // .to("bean:trashService?method=save")
                // .to("jms:trash1")

                // DELETE - delete
                .delete()
                .description("Delete trash")
                .type(Trash.class)
                .responseMessage()
                .code(200)
                .message("Trash deleted successfully")
                .endResponseMessage()
                .param()
                .name("body")
                .description("The trash to delete")
                .endParam()
                .to("bean:pgTrashService?method=delete");

        from("direct:saveTrash")
                .multicast()
                .to("bean:pgTrashService?method=save", "direct:jmsTrash")
                .transform(simple("${body}"));
        from("direct:jmsTrash")
                .marshal()
                .json(JsonLibrary.Jackson)
                .setExchangePattern(ExchangePattern.InOnly)
                .to("activemq:trash1")
                .end();
        // from("direct:kafkaTrash")
        //         .marshal()
        //         .json(JsonLibrary.Jackson)
        //         .setExchangePattern(ExchangePattern.InOnly)
        //         .setBody(simple("${body}"))
        //         .setHeader(KafkaConstants.KEY, constant("Trash"))
        //         .to("kafka:trash-created?brokers=glue_broker:50002")
        //         .end();
    }
}
