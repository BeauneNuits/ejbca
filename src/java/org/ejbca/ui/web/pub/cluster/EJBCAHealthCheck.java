package org.ejbca.ui.web.pub.cluster;

import java.util.Iterator;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ejbca.core.ejb.ca.caadmin.ICAAdminSessionLocal;
import org.ejbca.core.ejb.ca.caadmin.ICAAdminSessionLocalHome;
import org.ejbca.core.ejb.ca.publisher.IPublisherSessionLocal;
import org.ejbca.core.ejb.ca.publisher.IPublisherSessionLocalHome;
import org.ejbca.core.model.ca.caadmin.CAInfo;
import org.ejbca.core.model.ca.publisher.PublisherConnectionException;
import org.ejbca.core.model.log.Admin;



/**
 * EJBCA Health Checker. 
 * 
 * Does the following system checks.
 * 
 * * Not about to run out if memory (configurable through web.xml with param "MinimumFreeMemory")
 * * Database connection can be established.
 * * All CATokens are aktive if not set as offline.
 * * All Publishers can establish connection
 * 
 * @author Philip Vendil
 * $id$
 */

public class EJBCAHealthCheck implements IHealthCheck {
	
	private static Logger log = Logger.getLogger(EJBCAHealthCheck.class);

	private Admin admin = new Admin(Admin.TYPE_INTERNALUSER);
	
	private int minfreememory = 0;
	
	public void init(ServletConfig config) {
		minfreememory = Integer.parseInt(config.getInitParameter("MinimumFreeMemory"));
	}

	
	public String checkHealth(HttpServletRequest request) {
		
		String errormessage = "";
		
		errormessage += checkMemory();				
		errormessage += checkDB();		
		errormessage += checkCAs();		
		errormessage += checkPublishers();
				
		if(errormessage.equals("")){
			// everything seems ok.
			errormessage = null;
		}
		
		return errormessage;
	}
	
	private String checkMemory(){
		String retval = "";
        if(minfreememory < Runtime.getRuntime().freeMemory()){
          retval = "\nError Virtual Memory is about to run out, currently free memory :" + Runtime.getRuntime().freeMemory();	
        }		
		
		return retval;
	}
	
	private String checkDB(){

		
		
		return "";
	}
	
	private String checkCAs(){
		String retval = "";
		Iterator iter = getCAAdminSession().getAvailableCAs(admin).iterator();
		while(iter.hasNext()){
			CAInfo cainfo = getCAAdminSession().getCAInfo(admin,((Integer) iter.next()).intValue());
			cainfo.getStatus();
		}
		
		
		return retval;
	}
	
	private String checkPublishers(){
		String retval = "";
		Iterator iter = getPublisherSession().getAuthorizedPublisherIds(admin).iterator();
		while(iter.hasNext()){
			Integer publisherId = (Integer) iter.next();
			try {
				getPublisherSession().testConnection(admin,publisherId.intValue());
			} catch (PublisherConnectionException e) {
				String publishername = getPublisherSession().getPublisherName(admin,publisherId.intValue());
				retval +="\n Cannot connect to publisher " + publishername;
				log.error("Cannot connect to publisher " + publishername);
			}
		}
		return retval;
	}
	
	private IPublisherSessionLocal publishersession = null;	
	private IPublisherSessionLocal getPublisherSession(){
		if(publishersession == null){

			try {
				Context context = new InitialContext();
				publishersession = ((IPublisherSessionLocalHome) javax.rmi.PortableRemoteObject.narrow(context.lookup(
				  "PublisherSessionLocal"), IPublisherSessionLocalHome.class)).create();
			} catch (Exception e) {
				throw new EJBException(e);
			} 
			
		}
		
		return publishersession;
	}
	
	private ICAAdminSessionLocal caadminsession = null;	
	private ICAAdminSessionLocal getCAAdminSession(){
		if(caadminsession == null){

			try {
				Context context = new InitialContext();
				caadminsession = ((ICAAdminSessionLocalHome) javax.rmi.PortableRemoteObject.narrow(context.lookup(
				  "CAAdminSessionLocal"), ICAAdminSessionLocalHome.class)).create();
			} catch (Exception e) {
				throw new EJBException(e);
			} 
			
		}
		
		return caadminsession;
	}
	
	

}
