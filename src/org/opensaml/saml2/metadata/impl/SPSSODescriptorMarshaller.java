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

/**
 * 
 */

package org.opensaml.saml2.metadata.impl;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

/**
 * A thread safe Marshaller for {@link org.opensaml.saml2.metadata.SPSSODescriptor} objects.
 */
public class SPSSODescriptorMarshaller extends SSODescriptorMarshaller {

    /**
     * Constructor
     */
    public SPSSODescriptorMarshaller() {
        super(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME);
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected SPSSODescriptorMarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }

    /*
     * @see org.opensaml.xml.io.AbstractXMLObjectMarshaller#marshallAttributes(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Element)
     */
    protected void marshallAttributes(XMLObject samlObject, Element domElement) throws MarshallingException {
        SPSSODescriptor descriptor = (SPSSODescriptor) samlObject;

        if (descriptor.isAuthnRequestsSignedXSBoolean() != null) {
            domElement.setAttributeNS(null, SPSSODescriptor.AUTH_REQUETS_SIGNED_ATTRIB_NAME, descriptor.isAuthnRequestsSignedXSBoolean().toString());
        }

        if (descriptor.getWantAssertionsSignedXSBoolean() != null) {
            domElement.setAttributeNS(null, SPSSODescriptor.WANT_ASSERTIONS_SIGNED_ATTRIB_NAME, descriptor.getWantAssertionsSignedXSBoolean().toString());
        }

        super.marshallAttributes(samlObject, domElement);
    }
}