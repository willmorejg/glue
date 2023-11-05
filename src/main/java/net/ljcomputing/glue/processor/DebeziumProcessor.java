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
package net.ljcomputing.glue.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.ljcomputing.glue.entity.Trash;
import net.ljcomputing.glue.service.TrashService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DebeziumProcessor implements Processor {
    private static final String PAYLOAD = "payload";
    private static final String BEFORE = "before";
    private static final String AFTER = "after";
    private static final String OP = "op";

    private enum Operation {
        INSERT("c"),
        DELETE("d"),
        PERSIST("u");

        private final String op;

        private Operation(final String op) {
            this.op = op;
        }

        public static Operation findByValues(final String op) {
            for (final Operation current : values()) {
                if (current.op.equals(op)) {
                    return current;
                }
            }

            return null;
        }
    }

    @Autowired
    @Qualifier("db2TrashService")
    private TrashService db2TrashService;

    @Override
    public void process(Exchange exchange) throws Exception {
        final String body =
                exchange != null && exchange.getIn() != null && exchange.getIn().getBody() != null
                        ? exchange.getIn().getBody().toString()
                        : "";
        log.debug("exchange - in - body: {}", body);

        if (!body.isEmpty()) {
            final JsonNode jsonNode = new ObjectMapper().readTree(body);
            final JsonNode payload = jsonNode.get(PAYLOAD);
            final JsonNode before = payload.get(BEFORE);
            final boolean beforeNull = before.isNull();
            final JsonNode after = payload.get(AFTER);
            final boolean afterNull = after.isNull();
            final String op = payload.get(OP).asText();
            final Operation operation = Operation.findByValues(op);

            log.debug("beforeNull: {}", beforeNull);
            log.debug("afterNull: {}", afterNull);
            log.debug("op: {}", op);
            log.debug("operation: {}", operation);

            final Trash changed =
                    afterNull
                            ? new ObjectMapper().convertValue(before, Trash.class)
                            : new ObjectMapper().convertValue(after, Trash.class);

            log.debug("changed: {}", changed);

            switch (operation) {
                case INSERT:
                case PERSIST:
                    db2TrashService.save(changed);
                    break;
                case DELETE:
                    db2TrashService.delete(changed);
                    break;
                default:
                    log.warn("Cannot determine operation for \"{}\"", op);
                    break;
            }
        }
    }
}
