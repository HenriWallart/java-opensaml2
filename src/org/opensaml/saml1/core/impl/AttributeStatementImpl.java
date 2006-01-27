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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml1.core.Attribute;
import org.opensaml.saml1.core.AttributeStatement;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * A Concrete implementation of the {@link org.opensaml.saml1.core.AttributeStatement} Interface
 */
public class AttributeStatementImpl extends SubjectStatementImpl implements AttributeStatement {

    /** Contains the Attributes (in order) */
    private final List<Attribute> attributes;

    /**
     * Constructor
     */
    public AttributeStatementImpl() {
        super(SAMLConstants.SAML1_NS, AttributeStatement.LOCAL_NAME);
        setElementNamespacePrefix(SAMLConstants.SAML1_PREFIX);
        attributes = new XMLObjectChildrenList<Attribute>(this);
    }

    /*
     * @see org.opensaml.saml1.core.AttributeStatement#getAttributes()
     */
    public List<Attribute> getAttributes() {

        return attributes;
    }

    /*
     * @see org.opensaml.common.SAMLObject#getOrderedChildren()
     */
    public List<SAMLObject> getOrderedChildren() {

        List<SAMLObject> list = new ArrayList<SAMLObject>(attributes.size() + 1);

        if (getSubject() != null) {
            list.add(getSubject());
        }

        list.addAll(attributes);
        
        if (list.size() == 0) {
            return null;
        }
        
        return Collections.unmodifiableList(list);
    }

}