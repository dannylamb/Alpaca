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

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Creates a default HttpClient using the provided interceptors.
 * 
 * @author dannylamb 
 */
public class DefaultClientFactory {

    /**
     * Convenience factory method.
     * 
     * @param tokenInterceptor              an instance of {@link StaticTokenRequestInterceptor}
     * @param wwwAuthenticateInterceptor    an instance of {@link WWWAuthenticateRequestInterceptor}
     * @return a default-configuration {@link HttpClient} that is wrapped with this interceptor
     */
    public static HttpClient create(
        final StaticTokenRequestInterceptor tokenInterceptor,
        final WWWAuthenticateRequestInterceptor wwwAuthenticateInterceptor
    ) {
        return HttpClientBuilder.create()
            .addInterceptorFirst(tokenInterceptor)
            .addInterceptorFirst(wwwAuthenticateInterceptor)
            .build();
    }
}

