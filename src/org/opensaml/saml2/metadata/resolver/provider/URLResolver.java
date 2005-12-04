/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml2.metadata.resolver.provider;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.opensaml.saml2.metadata.resolver.MetadataResolver;
import org.opensaml.saml2.metadata.resolver.ResolutionException;
import org.w3c.dom.Document;

/**
 * This metadata resolver takes a URL pointing to a SAML2 Metadata 
 * document and constructs a DOM document from it.
 * 
 * This resolver is completely stateless.
 */
public class URLResolver extends AbstractMetadataResolver implements MetadataResolver {
    
    private String errorMessage = "Unable to obtain metadata document from URL ";

    /**
     * Resolves a URL pointing to a metadata document and creates a DOM document from it.
     * 
     * @param metadataURL the URL
     * 
     * @return the DOM document
     * 
     * @throws ResolutionException thrown if the URL is malformed, information can 
     * not be fetched from the server, the information fetched is not proper SAML2
     * metadata, or the DOM, level 3, parser can not be created
     */
    public Document doResolve(String metadataURL) throws ResolutionException {
        URL metadata;
        try {
            metadata = new URL(metadataURL);
            DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domBuilderFactory.newDocumentBuilder();
            return domBuilder.parse(metadata.openStream());
        } catch (Exception e) {
            throw new ResolutionException(errorMessage + metadataURL, e);
        }
    }
}