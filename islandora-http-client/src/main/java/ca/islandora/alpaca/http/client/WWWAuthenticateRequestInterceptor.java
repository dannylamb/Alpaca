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

package ca.islandora.alpaca.http.client;

import static java.util.Objects.requireNonNull;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

/**
 * Adds a single WWW-Authenticate header to any request that does not
 * already have at least one.
 * 
 * @author dannylamb 
 *
 */
public class WWWAuthenticateRequestInterceptor implements HttpRequestInterceptor {

    public static final String WWW_AUTH_HEADER = "WWW-Authenticate";

    private Header header;

    /**
     * Default constructor
     */
    public WWWAuthenticateRequestInterceptor() {
    }

    /**
     * @param value the www-authenticate value to use
     */
    public WWWAuthenticateRequestInterceptor(final String value) {
        this.header = makeHeader(value);
    }

    /**
     * @param value the www-authenticate value to use
     */
    public void setValue(final String value) {
        this.header = makeHeader(value);
    }

    private static Header makeHeader(final String value) {
        return new BasicHeader(WWW_AUTH_HEADER, requireNonNull(value, "WWW-Authenticate value must be non-null!"));
    }

    @Override
    public void process(final HttpRequest request, final HttpContext context) {
        // we do not inject if www-authenticate headers present
        if (request.getFirstHeader(WWW_AUTH_HEADER) == null) {
            request.addHeader(header);
        }
    }
}
