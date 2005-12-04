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

package org.opensaml.common;

import java.util.List;

import javax.security.cert.X509Certificate;

/**
 * A functional interface for SAML elements which can be signed.
 * 
 * Implementations of this interface should be careful to properly 
 * remove an existing signature before changing a SAML element.
 */
public interface SignableObject extends SAMLObject {

    /**
     * Gets the ID of the signed element.
     * 
     * @return the ID of the signed element
     */
    public String getId();
        
    /**
     * Checks to see if the element has been signed.
     * 
     * @return true if this element is signed, false if not
     */
    public boolean isSigned();

    /**
     * Gets the algorithim used to get the message digest.
     * 
     * @return the algorithim used to get the message digest
     */
    public String getDigestAlgorithm();
    
    /**
     * Gets the signing algorithim used to create the signature.
     * 
     * @return the signing algorithim used to create the signature
     */
    public String getSignatureAlgorithm();
   
    /**
     * Gets the list of X.509 certificates included with the signature.
     * 
     * @return the list of X.509 certificates included with the signature
     */
    public List<X509Certificate> getX509Certificates();
        
    /**
     * Removes an existing signature from this SAML element.  If the is no 
     * existing signature the method just returns.
     */
    public void removeSignature();

    /**
     * Gets the information need to construct the digital signature for this element.
     * 
     * @return the information need to construct the digital signature for this element
     */
    public SigningContext getSigningContext();

    /**
     * Sets the information need to construct the digital signature for this element.
     * 
     * @param signingContext the information need to construct the digital signature for this element
     */
    public void setSigningContext(SigningContext signingContext);
}