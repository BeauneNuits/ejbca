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
 
package se.anatom.ejbca.ca.caadmin.extendedcaservices;

import java.io.Serializable;
import java.security.KeyPair;


/**
 * Class used when requesting key recovery related services from a CA.  
 *
 * @version $Id: KeyRecoveryCAServiceRequest.java,v 1.2 2004-04-16 07:38:57 anatom Exp $
 */
public class KeyRecoveryCAServiceRequest extends ExtendedCAServiceRequest implements Serializable {    
 
	public static final int COMMAND_ENCRYPTKEYS = 1;
	public static final int COMMAND_DECRYPTKEYS = 2;
	
    private int command;
    private byte[] keydata;
    private KeyPair keypair;
    /** Constructor for KeyRecoveryCAServiceRequest
     */                   
    public KeyRecoveryCAServiceRequest(int command, byte[] keydata) {
        this.command = command;
        this.keydata = keydata;
    }

    /** Constructor for KeyRecoveryCAServiceRequest
     */                   
    public KeyRecoveryCAServiceRequest(int command, KeyPair keypair) {
    	this.command = command;
    	this.keypair = keypair;
    }
    
    public int getCommand(){
    	return command;    	
    }
    
    /**
     *  Returns data beloning to the decrypt keys request, returns null oterwise.
     */
    
    public  byte[] getKeyData(){
    	if(command != COMMAND_DECRYPTKEYS)
    	  return null;
    	return keydata;
    }

    /**
     *  Returns data beloning to the encrypt keys request, returns null oterwise.
     */
    
    public  KeyPair getKeyPair(){
    	if(command != COMMAND_ENCRYPTKEYS)
    		return null;
    	return keypair;
    }
    
}
