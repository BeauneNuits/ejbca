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
package org.ejbca.core.ejb.signer;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.List;

import org.cesecore.authentication.tokens.AuthenticationToken;
import org.cesecore.authorization.AuthorizationDeniedException;
import org.cesecore.keys.token.CryptoTokenOfflineException;

/**
 * Generic Management interface for InternalKeyBinding.
 * 
 * @version $Id$
 */
public interface InternalKeyBindingMgmtSession {

    /** @return a list of IDs for the specific type and that the caller is authorized to view */
    List<Integer> getInternalKeyBindingIds(AuthenticationToken authenticationToken, String internalKeyBindingType);

    /** @return the InternalKeyBinding for the requested Id or null if none was found */
    InternalKeyBinding getInternalKeyBinding(AuthenticationToken authenticationToken, int internalKeyBindingId) throws AuthorizationDeniedException;

    /** @return the internalKeyBindingId from the more user friendly name. Return null of there is no such InternalKeyBinding. */
    Integer getIdFromName(String internalKeyBindingName);

    /**
     * Create new (when the provided InternalKeyBinding has id 0) or merge existing InternalKeyBinding.
     * The caller must be authorized the modify the InternalKeyBinding and to CryptoTokenRules.USE the referenced CryptoToken.
     * @return the internalKeyBindingId that can be used for future reference if the object was created
     */
    int persistInternalKeyBinding(AuthenticationToken authenticationToken, InternalKeyBinding internalKeyBinding) throws AuthorizationDeniedException, InternalKeyBindingNameInUseException;

    /** @return true if the InternalKeyBinding existed before deletion */
    boolean deleteInternalKeyBinding(AuthenticationToken authenticationToken, int internalKeyBindingId) throws AuthorizationDeniedException;
    
    /**
     * @return the public key of the requested InternalKeyBinding in DER format 
     * @throws CryptoTokenOfflineException if the public key could not be retrieved from the referenced CryptoToken
     */
    byte[] getNextPublicKeyForInternalKeyBinding(AuthenticationToken authenticationToken, int internalKeyBindingId) throws AuthorizationDeniedException, CryptoTokenOfflineException;

    /** 
     * Update the key mapping if there is a newer certificate in the database or a certificate matching the nextKey.
     * This could normally be used in a setup where the certificate is published or made available be other means
     * in the database for this EJBCA instance.
     */
    void updateCertificateForInternalKeyBinding(AuthenticationToken authenticationToken, int internalKeyBindingId) throws AuthorizationDeniedException,
            CertificateImportException, CryptoTokenOfflineException;

    /**
     * Imports the certificate provided in DER format to the database and updates the InternalKeyBinding reference.
     * If the the certificates public key matches the current instance's keyPairAlias, the keyPairAlias will not be updated.
     * If the nextKey property is set and the certificates public key matches, the instance's keyPairAlias will also be updated.
     * 
     * @throws CertificateImportException if the provided certificate's public key does not match current or next key. This is also
     * thrown if the implementation cannot validate that the certificate is of the right type.
     */
    void importCertificateForInternalKeyBinding(AuthenticationToken authenticationToken, int internalKeyBindingId, byte[] certificate)
        throws AuthorizationDeniedException, CertificateImportException;

    /** Creates a new key pair with the same key specification as the current and a new alias. */
    void generateNextKeyPair(AuthenticationToken authenticationToken, int internalKeyBindingId) throws AuthorizationDeniedException,
            CryptoTokenOfflineException, InvalidKeyException, InvalidAlgorithmParameterException;
}

