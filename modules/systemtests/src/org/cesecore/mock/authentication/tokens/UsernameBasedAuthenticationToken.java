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
package org.cesecore.mock.authentication.tokens;

import java.security.Principal;
import java.util.HashSet;

import org.cesecore.authentication.tokens.AuthenticationToken;
import org.cesecore.authentication.tokens.UsernamePrincipal;
import org.cesecore.authorization.user.AccessUserAspect;
import org.cesecore.authorization.user.matchvalues.AccessMatchValue;
import org.ejbca.ui.cli.CliAuthenticationToken;
import org.ejbca.ui.cli.CliUserAccessMatchValue;

/**
 * This mock of an authentication token is to provide a simple token to test authentication/authorization based
 * on a username. It shouldn't be used outside that context, and shouldn't be used to avoid proper authorization tests.
 * 
 * @version $Id$
 *
 */
public class UsernameBasedAuthenticationToken extends AuthenticationToken{
    
    private static final long serialVersionUID = -8284027258387832870L;

    private String username;
    
    public UsernameBasedAuthenticationToken(final UsernamePrincipal principal) {
        super(new HashSet<Principal>() {
            private static final long serialVersionUID = -5658805274530978504L;
            {
                add(principal);
            }
        }, null);
        this.username = principal.getName();
    }

    @Override
    public boolean matches(AccessUserAspect accessUser) {
        return username.equals(accessUser.getMatchValue());
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UsernameBasedAuthenticationToken other = (UsernameBasedAuthenticationToken) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public boolean matchTokenType(String tokenType) {        
        return CliAuthenticationToken.TOKEN_TYPE.equals(tokenType);
    }

    @Override
    public AccessMatchValue getDefaultMatchValue() {
        return CliUserAccessMatchValue.USERNAME;
    }

    @Override
    public AccessMatchValue getMatchValueFromDatabaseValue(Integer databaseValue) {
        if (databaseValue != CliUserAccessMatchValue.USERNAME.getNumericValue()) {
            return null;
        } else {
            return CliUserAccessMatchValue.USERNAME;
        }
    }

}
