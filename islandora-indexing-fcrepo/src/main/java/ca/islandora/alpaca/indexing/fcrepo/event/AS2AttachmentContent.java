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

package ca.islandora.alpaca.indexing.fcrepo.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for attachment content.  Part of a AS2Event.
 *
 * @author Danny Lamb
 */
public class AS2AttachmentContent {

    /**
     * @return  Fedora base url 
     */
    @JsonProperty(value = "fedora_base_url")
    public String getFedoraBaseUrl() {
        return fedoraBaseUrl;
    }

    /**
     * @param   fedoraBaseUrl    Fedora base url
     */
    public void setFedoraBaseUrl(final String fedoraBaseUrl) {
        this.fedoraBaseUrl = fedoraBaseUrl;
    }

    /**
     * @return  Drupal base url 
     */
    @JsonProperty(value = "drupal_base_url")
    public String getDrupalBaseUrl() {
        return drupalBaseUrl;
    }

    /**
     * @param   drupalBaseUrl    Drupal base url
     */
    public void setDrupalBaseUrl(final String drupalBaseUrl) {
        this.drupalBaseUrl = drupalBaseUrl;
    }

    /**
     * @return Source field
     */
    @JsonProperty(value = "source_field")
    public String getSourceField() {
        return sourceField;
    }

    /**
     * @param   sourceField   Source field
     */
    public void setSourceField(final String sourceField) {
        this.sourceField = sourceField;
    }

    private String fedoraBaseUrl;
    private String drupalBaseUrl;
    private String sourceField;

}
