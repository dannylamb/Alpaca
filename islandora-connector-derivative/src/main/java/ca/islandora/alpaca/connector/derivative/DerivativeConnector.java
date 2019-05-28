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

package ca.islandora.alpaca.connector.derivative;

import static org.apache.camel.LoggingLevel.ERROR;
import static org.slf4j.LoggerFactory.getLogger;

import ca.islandora.alpaca.connector.derivative.event.AS2Event;
import com.jayway.jsonpath.JsonPathException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;

/**
 * @author dhlamb
 */
public class DerivativeConnector extends RouteBuilder {

    private static final Logger LOGGER = getLogger(DerivativeConnector.class);

    @Override
    public void configure() {
        // Global exception handlers for the indexer.

        // Don't retry if you can't parse the json.
        onException(JsonPathException.class)
            .maximumRedeliveries(0)
            .log(
                ERROR,
                LOGGER,
                "Error extracting file path from JSON: ${exception.message}\n\n${exception.stacktrace}"
            );

        // Just logs after retrying X number of times for everything else.
        onException(Exception.class)
            .maximumRedeliveries("{{error.maxRedeliveries}}")
            .log(
                ERROR,
                LOGGER,
                "Error connecting generating derivative with {{derivative.service.url}}: " +
                "${exception.message}\n\n${exception.stacktrace}"
            );

        // The route.
        from("{{in.stream}}")
            .routeId("IslandoraConnectorDerivative")

            // Parse the event into a POJO.
            .unmarshal().json(JsonLibrary.Jackson, AS2Event.class)

            // Stash the event on the exchange.
            .setProperty("event").simple("${body}")
            .setProperty("jsonUrl").simple("${exchangeProperty.event.attachment.content.fileJsonUrl}")

            // Get file url from its REST endpoint.
            .removeHeaders("*", "Authorization")
            .setHeader(Exchange.HTTP_METHOD, constant("GET"))
            .setBody(simple("${null}"))
            .toD("${exchangeProperty.jsonUrl}&connectionClose=true")

            // Extract the file path from the file json
            .transform().jsonpath("$.uri[0].url")
            .setProperty("drupalUrl").simple("${exchangeProperty.event.attachment.content.drupalBaseUrl}${body}")
            
            // Make the Crayfish request.
            .setHeader("Accept", simple("${exchangeProperty.event.attachment.content.mimetype}"))
            .setHeader("X-Islandora-Args", simple("${exchangeProperty.event.attachment.content.args}"))
            .setHeader("Apix-Ldp-Resource", simple("${exchangeProperty.drupalUrl}"))
            .setBody(simple("${null}"))
            .to("{{derivative.service.url}}?connectionClose=true")

            // PUT the media.
            .removeHeaders("*", "Authorization", "Content-Type")
            .setHeader("Content-Location", simple("${exchangeProperty.event.attachment.content.fileUploadUri}"))
            .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
            .toD("${exchangeProperty.event.attachment.content.destinationUri}?connectionClose=true");
    }

}
