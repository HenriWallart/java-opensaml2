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

package org.opensaml.saml1.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.common.SAMLVersion;
import org.opensaml.saml1.core.Audience;
import org.opensaml.saml1.core.AudienceRestrictionCondition;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * Concrete implementation of the org.opensaml.saml1.core.AudienceRestrictionCondition
 */
public class AudienceRestrictionConditionImpl extends AbstractSignableAssertionSAMLObject implements
        AudienceRestrictionCondition {

    /** Audiences */
    private final List<Audience> audiences = new XMLObjectChildrenList<Audience>(this);

    /**
     * Constructor
     * @deprecated
     */
    private AudienceRestrictionConditionImpl() {
        super(AudienceRestrictionCondition.LOCAL_NAME, null);
    }
    protected AudienceRestrictionConditionImpl(SAMLVersion version) {
        super(AudienceRestrictionCondition.LOCAL_NAME, version);
    }

    /*
     * @see org.opensaml.saml1.core.AudienceRestrictionCondition#getAudiences()
     */
    public List<Audience> getAudiences() {
        return audiences;
    }

    /*
     * @see org.opensaml.common.SAMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {

        if (audiences.size() == 0) {
            return null;
        }
        ArrayList<XMLObject> children = new ArrayList<XMLObject>(audiences);
        return Collections.unmodifiableList(children);
    }
}