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
 
package se.anatom.ejbca.ca.exception;

import se.anatom.ejbca.exception.EjbcaException;

/**
 * Is throw when connection to a publisher have failed i some way.
 *
 * @author  Philip Vendil
 * @version $Id: PublisherConnectionException.java,v 1.2 2004-04-16 07:38:55 anatom Exp $
 */
public class PublisherConnectionException extends EjbcaException {
    
    /**
     * Creates a new instance of <code>PublisherConnectionException</code> without detail message.
     */
    public PublisherConnectionException() {
        super();
    }
    
    
    /**
     * Constructs an instance of <code>PublisherConnectionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PublisherConnectionException(String msg) {    	
        super(msg);        
    }
}
