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
 
package se.anatom.ejbca.ca.caadmin;

import java.util.Collection;
import java.util.Date;

import se.anatom.ejbca.util.CertTools;

/**
 * Holds nonsensitive information about a X509CA.
 *
 * @version $Id: X509CAInfo.java,v 1.5 2004-04-16 07:38:58 anatom Exp $
 */
public class X509CAInfo extends CAInfo{
   
  String policyid;
  boolean useauthoritykeyidentifier;
  boolean authoritykeyidentifiercritical;
  boolean usecrlnumber;
  boolean crlnumbercritical;
  String subjectaltname;
    
    /**
     * Constructor that should be used when creating CA and retreiving CA info.
     */
    public X509CAInfo(String subjectdn, String name, int status, String subjectaltname, int certificateprofileid, 
                    int validity, Date expiretime, int catype, int signedby, Collection certificatechain, 
                    CATokenInfo catokeninfo, String description, int revokationreason, Date revokationdate, String policyid, int crlperiod, Collection crlpublishers,
                    boolean useauthoritykeyidentifier, boolean authoritykeyidentifiercritical,
                    boolean usecrlnumber, boolean crlnumbercritical, boolean finishuser,
                    Collection extendedcaserviceinfos) {
        this.subjectdn = CertTools.stringToBCDNString(subjectdn);
        this.caid = this.subjectdn.hashCode();
        this.name = name;
        this.status = status;
        this.validity = validity;
        this.expiretime = expiretime;
        this.catype = catype;
        this.signedby = signedby;
        this.certificatechain = certificatechain;        
        this.catokeninfo = catokeninfo; 
        this.description = description;
        this.revokationreason = revokationreason;
        this.revokationdate = revokationdate;
        this.policyid = policyid;
        this.crlperiod = crlperiod;
        this.crlpublishers = crlpublishers;
        this.useauthoritykeyidentifier = useauthoritykeyidentifier;
        this.authoritykeyidentifiercritical = authoritykeyidentifiercritical;
        this.usecrlnumber = usecrlnumber;
        this.crlnumbercritical = crlnumbercritical;
        this.finishuser = finishuser;                     
        this.subjectaltname = subjectaltname;
        this.certificateprofileid = certificateprofileid;
        this.extendedcaserviceinfos = extendedcaserviceinfos; 
    }

    /**
     * Constructor that should be used when updating CA data.
     */
    public X509CAInfo(int caid, int validity, CATokenInfo catokeninfo, String description,
                      int crlperiod, Collection crlpublishers,
                      boolean useauthoritykeyidentifier, boolean authoritykeyidentifiercritical,
                      boolean usecrlnumber, boolean crlnumbercritical, boolean finishuser, Collection extendedcaserviceinfos) {        
        this.caid = caid;
        this.validity=validity;
        this.catokeninfo = catokeninfo; 
        this.description = description;        
        this.crlperiod = crlperiod;
        this.crlpublishers = crlpublishers;
        this.useauthoritykeyidentifier = useauthoritykeyidentifier;
        this.authoritykeyidentifiercritical = authoritykeyidentifiercritical;
        this.usecrlnumber = usecrlnumber;
        this.crlnumbercritical = crlnumbercritical;
        this.finishuser = finishuser;
		this.extendedcaserviceinfos = extendedcaserviceinfos; 
    }  
  
  
  public X509CAInfo(){}
    
  public String getPolicyId(){ return policyid;}
 
  public boolean getUseCRLNumber(){ return usecrlnumber;}
  public void setUseCRLNumber(boolean usecrlnumber){ this.usecrlnumber=usecrlnumber;}
  
  public boolean getCRLNumberCritical(){ return crlnumbercritical;}
  public void setCRLNumberCritical(boolean crlnumbercritical){ this.crlnumbercritical=crlnumbercritical;}
  
  public boolean getUseAuthorityKeyIdentifier(){ return useauthoritykeyidentifier;}
  public void setUseAuthorityKeyIdentifier(boolean useauthoritykeyidentifier)
                {this.useauthoritykeyidentifier=useauthoritykeyidentifier;}
  
  public boolean getAuthorityKeyIdentifierCritical(){ return authoritykeyidentifiercritical;}
  public void setAuthorityKeyIdentifierCritical(boolean authoritykeyidentifiercritical)
                {this.authoritykeyidentifiercritical=authoritykeyidentifiercritical;}
  
  
  public String getSubjectAltName(){ return subjectaltname; }
  
}
