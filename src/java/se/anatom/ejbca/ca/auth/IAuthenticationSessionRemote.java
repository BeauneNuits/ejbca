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
 
package se.anatom.ejbca.ca.auth;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;
import javax.ejb.ObjectNotFoundException;

import se.anatom.ejbca.ca.exception.AuthLoginException;
import se.anatom.ejbca.ca.exception.AuthStatusException;
import se.anatom.ejbca.log.Admin;


/**
 * Interface used for authenticating entities when issuing their certificates. Remote interface for
 * EJB.
 *
 * @version $Id: IAuthenticationSessionRemote.java,v 1.7 2004-04-16 07:39:00 anatom Exp $
 */
public interface IAuthenticationSessionRemote extends EJBObject {
    /**
     * Authenticates a user to the user database and returns the user DN.
     *
     * @param username unique username within the instance
     * @param password password for the user
     *
     * @return UserAuthData, never returns null
     *
     * @throws ObjectNotFoundException if the user does not exist.
     * @throws AuthStatusException If the users status is incorrect.
     * @throws AuthLoginException If the password is incorrect.
     * @throws EJBException if a communication or other error occurs.
     */
    public UserAuthData authenticateUser(Admin admin, String username, String password)
        throws RemoteException, ObjectNotFoundException, AuthStatusException, AuthLoginException;

    /**
     * Set the status of a user to finished, called when a user has been successfully processed. If
     * possible sets users status to UserData.STATUS_GENERATED, which means that the user cannot
     * be authenticated anymore. NOTE: May not have any effect of user database is remote.
     *
     * @param username unique username within the instance
     * @param password password for the user
     *
     * @throws ObjectNotFoundException if the user does not exist.
     * @throws EJBException if a communication or other error occurs.
     */
    public void finishUser(Admin admin, String username, String password)
        throws RemoteException, ObjectNotFoundException;
}
