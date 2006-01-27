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

package org.opensaml.common.impl;

import java.security.SignatureException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.opensaml.common.SAMLConfig;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLObjectManager;
import org.opensaml.common.SAMLObjectMarshaller;
import org.opensaml.common.xml.ParserPoolManager;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.xml.DOMCachingXMLObject;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.SignableXMLObject;
import org.opensaml.xml.SigningContext;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.DigitalSignatureHelper;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A thread safe, abstract implementation of the {@link org.opensaml.common.io.Marshaller} interface that handles most
 * of the boilerplate code:
 * <ul>
 * <li>Ensuring elements to be marshalled are of either the correct xsi:type or element QName</li>
 * <li>Setting the appropriate namespace and prefix for the marshalled element</li>
 * <li>Setting the xsi:type for the element if the element has an explicit type</li>
 * <li>Setting namespaces attributes declared for the element</li>
 * <li>Marshalling of child elements</li>
 * <li>Caching of created DOM for elements that implement {@link org.opensaml.common.impl.DOMCachingSAMLObject}
 * <li>Signing of elements that implement {@link org.opensaml.common.SignableObject}</li>
 * </ul>
 */
public abstract class AbstractSAMLObjectMarshaller implements SAMLObjectMarshaller {

    /** Logger */
    private static Logger log = Logger.getLogger(AbstractSAMLObjectMarshaller.class);

    /** The namespace URI of the SAMLObject type or element name */
    private String targetNamespace;
    
    /** The local name of the SAMLObject type or element name */
    private String targetLocalName;

    /**
     * Constructor
     * 
     * @param targetNamespaceURI the namespaceURI of elements that this marshaller operates on
     * @param targetLocalName the local name of elements that this marshaller operates on
     */
    protected AbstractSAMLObjectMarshaller(String targetNamespaceURI, String targetLocalName) throws IllegalArgumentException{
        if(DatatypeHelper.isEmpty(targetNamespaceURI)){
            throw new IllegalArgumentException("Target Namespace URI may not be null or an empty");
        }
        targetNamespace = targetNamespaceURI;
        
        if(DatatypeHelper.isEmpty(targetLocalName)){
            throw new IllegalArgumentException("Target Local Name may not be null or an empty");
        }
        this.targetLocalName = targetLocalName;
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(T, boolean)
     */
    public Element marshall(SAMLObject samlObject) throws MarshallingException, UnknownElementException{
        try {
            return marshall(samlObject, ParserPoolManager.getInstance().newDocument());
        } catch (XMLParserException e) {
            log.error("Unable to create XML Document to root marshalled element in");
            throw new MarshallingException("Unable to create XML Document to root marshalled element in", e);
        }
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(T, org.w3c.dom.Document)
     */
    public Element marshall(SAMLObject samlObject, Document document) throws MarshallingException, UnknownElementException {
        String samlElementNamespace = samlObject.getElementQName().getNamespaceURI();
        String samlElementLocalName = samlObject.getElementQName().getLocalPart();

        if (log.isDebugEnabled()) {
            log.debug("Creating DOM element for SAMLElement " + samlElementLocalName);
        }
        
        if(samlObject instanceof DOMCachingXMLObject){
            DOMCachingXMLObject domCachingObject = (DOMCachingXMLObject)samlObject;
            if(domCachingObject.getDOM() != null){
                log.debug("SAMLObject " + samlObject.getElementQName() + " has a cached DOM, using it.");
                return domCachingObject.getDOM();
            }
        }
        
        Element domElement = document.createElementNS(samlElementNamespace, samlElementLocalName);
        domElement.setPrefix(samlObject.getElementQName().getPrefix());
        
        // Plant the element as the document root if this SAMLObject is at the top of tree
        if(samlObject.getParent() == null) {
            Element docElement = document.getDocumentElement();
            if(document.getDocumentElement() != null) {
                document.replaceChild(domElement, docElement);
            }else {
                document.appendChild(domElement);
            }
        }

        marshallNamespaces(samlObject, domElement);

        marshallAttributes(samlObject, domElement);

        marshallChildElements(samlObject, domElement);

        marshallElementContent(samlObject, domElement);

        marshallElementType(samlObject, domElement);

        signElement(samlObject, domElement);

        if (samlObject instanceof DOMCachingXMLObject) {
            ((DOMCachingXMLObject) samlObject).setDOM(domElement);
        }

        return domElement;
    }

    /**
     * Checks to make sure the given SAMLObject's schema type or element QName matches the target parameters given at 
     * marshaller construction time.
     * 
     * @param samlObject the SAMLObject to marshall
     */
    protected void checkSAMLObjectIsTarget(SAMLObject samlObject) throws MarshallingException {
        String samlElementNamespace = samlObject.getElementQName().getNamespaceURI();
        String samlElementLocalName = samlObject.getElementQName().getLocalPart();
        QName type = samlObject.getSchemaType();

        // Check to make sure the element to be marshalled is or the right type or
        // has the correct element QName
        if (type != null) {
            if (!type.getNamespaceURI().equals(targetNamespace)
                    || !type.getLocalPart().equals(targetLocalName)) {
                throw new MarshallingException("Can not marshall DOM element of type " + type.getNamespaceURI() + ":"
                        + type.getLocalPart() + ".  This marshaller only operations on DOM elements of type "
                        + targetNamespace + ":" + targetLocalName);
            }
        } else {
            if (!samlElementNamespace.equals(targetNamespace)
                    || !samlElementLocalName.equals(targetLocalName)) {
                throw new MarshallingException("Can not marshall SAMLElement " + samlElementNamespace + ":"
                        + samlElementLocalName + ".  This marshaller only operates on SAMLElements "
                        + targetNamespace + ":" + targetLocalName);
            }
        }
    }
    
    /**
     * Marshalls a given SAMLElement into a W3C Element. The given signing context should be blindly passed to the
     * marshaller for child elements. The SAMLElement passed to this method is guaranteed to be of the target name
     * specified during this unmarshaller's construction.
     * 
     * @param samlObject the element to marshall
     * @param domElement the W3C DOM element
     * 
     * @throws MarshallingException thrown if there is a problem marshalling the element
     */
    protected abstract void marshallAttributes(SAMLObject samlObject, Element domElement) throws MarshallingException;

    /**
     * Marshalls the child elements of the given SAML element.
     * 
     * @param samlObject the SAML element whose children will be marshalled
     * @param domElement the DOM element that will recieved the marshalled children
     * 
     * @throws MarshallingException thrown if there is a problem marshalling a child element
     * @throws UnknownElementException thrown if the SAMLObject contains a child SAMLObject for which there is no marshaller and 
     * {@link SAMLConfig#ignoreUnknownElements()} is true
     */
    protected void marshallChildElements(SAMLObject samlObject, Element domElement) throws MarshallingException, UnknownElementException {

        if (log.isDebugEnabled()) {
            log.debug("Marshalling child elements for SAMLElement " + samlObject.getElementQName());
        }

        List<SAMLObject> childElements = samlObject.getOrderedChildren();
        if (childElements != null && childElements.size() > 0) {
            for (SAMLObject childElement : childElements) {
                if(childElement == null){
                    continue;
                }

                if (childElement instanceof DOMCachingXMLObject) {
                    DOMCachingXMLObject domCachingChildElement = (DOMCachingXMLObject) childElement;
                    if (domCachingChildElement.getDOM() != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Child element " + childElement.getElementQName()
                                    + " was previously marshalled, using cached DOM");
                        }
                        XMLHelper.appendChildElement(domElement, domCachingChildElement.getDOM());
                        continue; // no need to go futher, DOM was cached and does not need to be constructed
                    }
                }

                SAMLObjectMarshaller marshaller = SAMLObjectManager.getMarshaller((SAMLObject)childElement);
                if (marshaller == null) {
                    if (!SAMLConfig.ignoreUnknownElements()) {
                        log.error("No marshaller registered for child SAMLObject, "
                                + childElement.getElementQName().getLocalPart() + ", of SAMLObject "
                                + samlObject.getElementQName().getLocalPart());
                        throw new UnknownElementException("No marshaller registered for child SAMLObject, "
                                + childElement.getElementQName().getLocalPart() + ", of SAMLObject "
                                + samlObject.getElementQName().getLocalPart());
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Ingored child SAMLObject, " + childElement.getElementQName().getLocalPart()
                                    + ", of SAMLObject " + samlObject.getElementQName().getLocalPart()
                                    + " because it had no registered marshaller.");
                        }
                    }
                } else {
                    domElement.appendChild(marshaller.marshall((SAMLObject)childElement, domElement.getOwnerDocument()));
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No child elements to marshall for SAMLElement "
                        + samlObject.getElementQName().getLocalPart());
            }
        }
    }

    /**
     * Creates an xsi:type attribute, corresponding to the given type of the SAML element, on the DOM element.
     * 
     * @param samlObject the SAML element
     * @param domElement the DOM element
     * 
     * @throws MarshallingException thrown if the type on the SAML element is does contain a local name, local name
     *             prefix, and namespace URI
     */
    protected void marshallElementType(SAMLObject samlObject, Element domElement) throws MarshallingException {
        QName type = samlObject.getSchemaType();
        if (type != null) {
            if (log.isDebugEnabled()) {
                log.debug("Setting xsi:type attribute with for SAMLElement " + samlObject.getElementQName());
            }
            String typeLocalName = type.getLocalPart();
            String typePrefix = type.getPrefix();

            if (typeLocalName == null) {
                throw new MarshallingException("The type QName on SAMLElement "
                        + samlObject.getElementQName().getLocalPart() + " may not have a null local name");
            }

            if (typePrefix == null) {
                throw new MarshallingException("The type QName on SAMLElement "
                        + samlObject.getElementQName().getLocalPart() + " may not have a null prefix");
            }

            if (type.getNamespaceURI() == null) {
                throw new MarshallingException("The type URI QName on SAMLElement "
                        + samlObject.getElementQName().getLocalPart() + " may not have a null namespace URI");
            }

            domElement.setAttributeNS(SAMLConstants.XSI_NS, SAMLConstants.XSI_PREFIX + ":type", typePrefix + ":"
                    + typeLocalName);
            samlObject.addNamespace(new Namespace(SAMLConstants.XSI_NS, SAMLConstants.XSI_PREFIX));
        }
    }

    /**
     * Creates the xmlns attributes for any namespaces set on the given SAML object.
     * 
     * @param samlObject the saml object
     * @param domElement the DOM element the namespaces will be added to
     */
    protected void marshallNamespaces(SAMLObject samlObject, Element domElement) throws MarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Marshalling namespace for SAMLObject " + samlObject.getElementQName());
        }
        Set<Namespace> namespaces = samlObject.getNamespaces();
        for (Namespace namespace : namespaces) {
            domElement.setAttribute(SAMLConstants.XMLNS_PREFIX + ":" + namespace.getNamespacePrefix(), namespace
                    .getNamespaceURI());
        }
    }

    /**
     * Marshalls data from the SAML Object into content of the DOM Element.
     * 
     * @param samlObject the SAML object
     * @param domElement the DOM element recieving the content
     */
    protected void marshallElementContent(SAMLObject samlObject, Element domElement) throws MarshallingException{
        //DO NOTHING; most elements don't have content
    }

    /**
     * Signs a W3C DOM Element.
     * 
     * @param domElement the element to be signed
     * @param dsigCtx information needed to create the signature
     * @param inclusiveNamespacePrefixes namespace prefixes to include in the digital signature, even if they don't
     *            appear to be used by the signer
     * 
     * @throws SignatureException thrown if there is a problem creating the digital signature
     */
    protected void signElement(SAMLObject samlObject, Element domElement) throws MarshallingException {
        if (samlObject instanceof SignableXMLObject) {
            SignableXMLObject signableSAMLObject = (SignableXMLObject) samlObject;
            if (signableSAMLObject.getSigningContext() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Signing SAMLElement " + samlObject.getElementQName());
                }
                
                SigningContext dsigCtx = signableSAMLObject.getSigningContext();
                
                Set<String> inclusiveNamespacePrefixes = new HashSet<String>();
                for (Namespace namespace : samlObject.getNamespaces()) {
                    inclusiveNamespacePrefixes.add(namespace.getNamespacePrefix());
                }
                
                try {
                    DigitalSignatureHelper.signElement(domElement, dsigCtx, inclusiveNamespacePrefixes);
                } catch (SignatureException e) {
                    log.error("Error encountered signing SAMLObject " + domElement.getLocalName(), e);
                    throw new MarshallingException("Error encountered signing SAMLObject " + domElement.getLocalName(), e);
                }
            }
        }
    }
}