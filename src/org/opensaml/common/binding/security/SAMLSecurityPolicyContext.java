/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.common.binding.security;

import org.joda.time.DateTime;
import org.opensaml.saml2.metadata.RoleDescriptor;
import org.opensaml.ws.security.SecurityPolicyContext;

/**
 * SAML-specific class which stores state that is maintained by {@link SAMLSecurityPolicy} instances, 
 * and which is used in the evaluation of {@link SecurityPolicyRule}'s.
 */
public class SAMLSecurityPolicyContext extends SecurityPolicyContext {
    
    /** Source of metadata about message issuer, as determined by security policy rules. */
    private RoleDescriptor issuerMetadata;
    
    /** Date and time of message issuance. */
    private DateTime issueInstant;
    
    /** Message identifier. */
    private String messageID;
    
    /**
     * Get the metadata for the issuer for the role in which they are operating.
     * 
     * @return the issuer's metadata
     */
    public RoleDescriptor getIssuerMetadata() {
        return issuerMetadata;
    }
    
    /**
     * Set the metadata for the issuer for the role in which they are operating.
     * 
     * @param newIssuerMetadata the issuer's new metadta
     */
    public void setIssuerMetadata(RoleDescriptor newIssuerMetadata) {
        issuerMetadata = newIssuerMetadata;
    }
    
    /**
     * Get the date and time of a message's issuance.
     * 
     * @return the date/time a message was issued
     */
    public DateTime getIssueInstant() {
        return issueInstant;
    }
    
    /**
     * Set the date and time of a message's issuance.
     * 
     * @param newIssueInstant the date and time of the message's issuance
     */
    public void setIssueInstant(DateTime newIssueInstant) {
        issueInstant = newIssueInstant;
    }
    
    /**
     * Gets the ID of the SAML request/response.
     * 
     * @return ID of the SAML request/response
     */
    public String getMessageID() {
        return messageID;
    }
    
    /**
     * Sets the ID of the SAML request/response.
     * 
     * @param id ID of the SAML request/response
     */
    public void setMessageID(String id) {
        messageID = id;
    }
}