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

package org.opensaml.saml1.core.impl;

import org.opensaml.common.SAMLObjectBaseTestCase;
import org.opensaml.common.xml.ParserPoolManager;
import org.opensaml.saml1.core.AuthorizationDecisionStatement;
import org.opensaml.saml1.core.DecisionType;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * 
 */
public class AuthorizationDecisionStatementTest extends SAMLObjectBaseTestCase {

    /** Value for Resource attribute specified in test file with attributes */
    private final String expectedResource = "resource";

    /** Value for Resource attribute specified in test file with attributes */
    private final DecisionType expectedDecision = DecisionType.Permit;

    /** File with the AuthorizationDecisionStatement with children */
    private final String fullElementsFile;

    /** The DOM to hold the AuthorizationDecisionStatement with children */
    private Document expectedFullDOM;

    /** File with the AuthorizationDecisionStatement with illegal Decision type */
    private String illegalAttributesFile;

    /**
     * Constructor
     */
    public AuthorizationDecisionStatementTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml1/singleAuthorizationDecisionStatement.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml1/singleAuthorizationDecisionStatementAttributes.xml";
        fullElementsFile = "/data/org/opensaml/saml1/AuthorizationDecisionStatementWithChildren.xml";
        illegalAttributesFile = "/data/org/opensaml/saml1/singleAuthorizationDecisionStatementAttributesInvalid.xml";
    }

    /*
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ParserPoolManager ppMgr = ParserPoolManager.getInstance();

        expectedFullDOM = ppMgr.parse(new InputSource(SAMLObjectBaseTestCase.class
                .getResourceAsStream(fullElementsFile)));
    }

    /*
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementUnmarshall()
     */
    @Override
    public void testSingleElementUnmarshall() {

        AuthorizationDecisionStatement authorizationDecisionStatement;
        authorizationDecisionStatement = (AuthorizationDecisionStatement) unmarshallElement(singleElementFile);

        assertNull("Decision attribute null", authorizationDecisionStatement.getDecision());
        assertNull("Resource attribute null", authorizationDecisionStatement.getResource());
        assertEquals("<Actions> elements present", 0, authorizationDecisionStatement.getActions().size());
        assertNull("<Evidence> element present", authorizationDecisionStatement.getEvidence());
    }

    /*
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementOptionalAttributesUnmarshall()
     */
    @Override
    public void testSingleElementOptionalAttributesUnmarshall() {

        AuthorizationDecisionStatement authorizationDecisionStatement;
        authorizationDecisionStatement = (AuthorizationDecisionStatement) unmarshallElement(singleElementOptionalAttributesFile);

        assertEquals("Resource attribute ", expectedResource, authorizationDecisionStatement.getResource());
        assertEquals("Decision attribute ", expectedDecision, authorizationDecisionStatement.getDecision());

        ParserPoolManager ppMgr = ParserPoolManager.getInstance();

        try {
            Document doc = ppMgr.parse(new InputSource(SAMLObjectBaseTestCase.class
                    .getResourceAsStream(illegalAttributesFile)));
            Element samlElement = doc.getDocumentElement();

            authorizationDecisionStatement = (AuthorizationDecisionStatement) new AuthorizationDecisionStatementUnmarshaller()
                    .unmarshall(samlElement);

            fail("illegal attribute successfully parsed");
        } catch (UnmarshallingException e) {
            ;
        } catch (XMLParserException e) {
            fail("couldn't parse file" + e);
            e.printStackTrace();
        }
    }

    /**
     * Test an XML file with children
     */

    public void testFullElementsUnmarshall() {
        AuthorizationDecisionStatement authorizationDecisionStatement;

        authorizationDecisionStatement = (AuthorizationDecisionStatement) unmarshallElement(fullElementsFile);

        assertNotNull("<Subject> element not present", authorizationDecisionStatement.getSubject());
        assertNotNull("<Evidence> element not present", authorizationDecisionStatement.getEvidence());
        assertNotNull("<Action> elements not present", authorizationDecisionStatement.getActions());
        assertEquals("Count of <Action> elements ", 3, authorizationDecisionStatement.getActions().size());
    }

    /*
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementMarshall()
     */
    @Override
    public void testSingleElementMarshall() {
        assertEquals(expectedDOM, new AuthorizationDecisionStatementImpl());
    }

    /*
     * @see org.opensaml.common.SAMLObjectBaseTestCase#testSingleElementOptionalAttributesMarshall()
     */
    @Override
    public void testSingleElementOptionalAttributesMarshall() {
        AuthorizationDecisionStatement authorizationDecisionStatement;

        authorizationDecisionStatement = new AuthorizationDecisionStatementImpl();
        authorizationDecisionStatement.setDecision(expectedDecision);
        authorizationDecisionStatement.setResource(expectedResource);

        assertEquals(expectedOptionalAttributesDOM, authorizationDecisionStatement);
    }

    /**
     * Test creating a DOM with child elements
     */

    public void testFullElementsMarshall() {
        AuthorizationDecisionStatement authorizationDecisionStatement;

        authorizationDecisionStatement = new AuthorizationDecisionStatementImpl();

        authorizationDecisionStatement.setSubject(new SubjectImpl());
        authorizationDecisionStatement.getActions().add(new ActionImpl());
        authorizationDecisionStatement.getActions().add(new ActionImpl());
        authorizationDecisionStatement.getActions().add(new ActionImpl());
        authorizationDecisionStatement.setEvidence(new EvidenceImpl());

        authorizationDecisionStatement.setEvidence(new EvidenceImpl());

        assertEquals(expectedFullDOM, authorizationDecisionStatement);
    }

}