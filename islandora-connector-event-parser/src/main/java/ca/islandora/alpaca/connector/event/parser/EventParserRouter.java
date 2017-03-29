/*
 * Licensed to Islandora Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * The Islandora Foundation licenses this file to you under the MIT License.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.islandora.alpaca.connector.event.parser;

import static org.apache.camel.LoggingLevel.ERROR;

import com.jayway.jsonpath.JsonPathException;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A content router distributing messages to multiple endpoints.
 *
 * @author Daniel Lamb
 */
public class EventParserRouter extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventParserRouter.class);

    /**
     * Configure the message route workflow.
     */
    public void configure() throws Exception {

        // Distribute message based on headers.
        from("{{input.stream}}")
                .routeId("IslandoraConnectorEventParser")
                .description("Parses Islandora AS2 event into exchange properties.")
              // Custom exception handler.  Doesn't retry if event is malformed.
              .onException(JsonPathException.class)
                .maximumRedeliveries(0)
                .handled(true)
                .log(
                   ERROR,
                   LOGGER,
                   "Error extracting properties from event: ${exception.message}\n\n${exception.stacktrace}\n\n" +
                       "Ignoring malformed event."
                )
                .end()
              .setProperty("IslandoraAction").jsonpath("$.type")
              .setProperty("IslandoraUri").jsonpath("$.object")
              .setProperty("IslandoraAuthorization").simple("${headers['Authorization']}")
              .to("{{output.stream}}");
    }
}

