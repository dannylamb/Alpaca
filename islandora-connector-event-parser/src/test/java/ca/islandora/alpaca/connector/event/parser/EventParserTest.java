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

package ca.islandora.alpaca.indexing.triplestore;

import java.util.Properties;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.apache.camel.util.ObjectHelper.loadResourceAsStream;

/**
 * @author dannylamb
 */
public class EventParserTest extends CamelBlueprintTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    public boolean isUseRouteBuilder() {
        return false;
    }

    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/blueprint-test.xml";
    }

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        final Properties props = new Properties();
        props.put("input.stream", "seda:foo");
        props.put("output.stream", "seda:bar");
        return props;
    }

    @Test
    public void testParseEvent() throws Exception {
        final String route = "IslandoraConnectorEventParser";
        context.getRouteDefinition(route).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:start");
                mockEndpointsAndSkip("*");
            }
        });
        context.start();

        final Exchange exchange = template.send(xchange -> {
            xchange.getIn().setBody(IOUtils.toString(loadResourceAsStream("create-event.json"), "UTF-8"));
            xchange.getIn().setHeader("Authentication", "some_token");
        });

        this.assertPredicate(
            exchangeProperty("IslandoraUri").isEqualTo("http://localhost:8000/fedora_resource/1"),
            exchange,
            true
        );
        this.assertPredicate(
            exchangeProperty("IslandoraAction").isEqualTo("Create"),
            exchange,
            true
        );
        this.assertPredicate(
            exchangeProperty("IslandoraAuthentication").isEqualTo("some_token"),
            exchange,
            true
        );
    }

}
