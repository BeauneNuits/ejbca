/*************************************************************************
 *                                                                       *
 *  EJBCA: The OpenSource Certificate Authority                          *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
 
package se.anatom.ejbca.authorization;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

/**
 * For docs, see AccessRulesDataBean
 **/
public interface AccessRulesDataLocalHome extends javax.ejb.EJBLocalHome {

    public AccessRulesDataLocal create(String admingroupname, int caid, AccessRule accessrule)
        throws CreateException;
    public AccessRulesDataLocal findByPrimaryKey(AccessRulesPK pk)
        throws FinderException;
}
