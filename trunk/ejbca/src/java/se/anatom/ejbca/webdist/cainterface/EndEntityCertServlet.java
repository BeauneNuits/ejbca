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
 
package se.anatom.ejbca.webdist.cainterface;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.cert.X509Certificate;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import se.anatom.ejbca.apply.RequestHelper;
import se.anatom.ejbca.authorization.AvailableAccessRules;
import se.anatom.ejbca.ca.sign.ISignSessionLocalHome;
import se.anatom.ejbca.util.Base64;
import se.anatom.ejbca.util.ServiceLocator;
import se.anatom.ejbca.webdist.rainterface.CertificateView;
import se.anatom.ejbca.webdist.rainterface.RAInterfaceBean;
import se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean;

/**
 * Servlet used to distribute End Entity certificates through the "View Certificate" jsp page.
 * Checks that the administrator is authorized to view the user before sending the certificate<br>
 *
 * cert - returns certificate in PEM-format
 * nscert - returns certificate for Netscape/Mozilla
 * iecert - returns certificate for Internet Explorer
 *
 * cert, nscert and iecert also takes  parameters issuer and certificatesn were issuer is the DN of issuer and certificate serienumber 
 * is in hex format.
 *
 * @version $Id: EndEntityCertServlet.java,v 1.1 2005-05-02 13:06:02 herrvendil Exp $
 *
 * @web.servlet name = "EndEntityCert"
 *              display-name = "EndEntityCertServlet"
 *              description="Returns the specified end entity certificate"
 *              load-on-startup = "99"
 *
 * @web.servlet-mapping url-pattern = "/ca/endentitycert"
 *
 */
public class EndEntityCertServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(EndEntityCertServlet.class);

    private static final String COMMAND_PROPERTY_NAME = "cmd";
    private static final String COMMAND_NSCERT = "nscert";
    private static final String COMMAND_IECERT = "iecert";
    private static final String COMMAND_CERT = "cert";
   
    private static final String ISSUER_PROPERTY = "issuer";
    private static final String CERTIFICATEDN_PROPERTY = "certificatesn";

    private ISignSessionLocalHome signhome = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            signhome = (ISignSessionLocalHome) ServiceLocator.getInstance().getLocalHome(ISignSessionLocalHome.COMP_NAME);
        } catch( Exception e ) {
            throw new ServletException(e);
        }
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException {
        log.debug(">doPost()");
        doGet(req, res);
        log.debug("<doPost()");
    } //doPost

    public void doGet(HttpServletRequest req,  HttpServletResponse res) throws java.io.IOException, ServletException {
        log.debug(">doGet()");
        // Check if authorized
        EjbcaWebBean ejbcawebbean= (se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean)
                                   req.getSession().getAttribute("ejbcawebbean");
        
        RAInterfaceBean rabean =  (se.anatom.ejbca.webdist.rainterface.RAInterfaceBean)
                                   req.getSession().getAttribute("rabean");
        if ( ejbcawebbean == null ){
          try {
            ejbcawebbean = (se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean) java.beans.Beans.instantiate(this.getClass().getClassLoader(), "se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean");
           } catch (ClassNotFoundException exc) {
               throw new ServletException(exc.getMessage());
           }catch (Exception exc) {
               throw new ServletException (" Cannot create bean of class "+"se.anatom.ejbca.webdist.webconfiguration.EjbcaWebBean", exc);
           }
           req.getSession().setAttribute("ejbcawebbean", ejbcawebbean);
        }
        
        if ( rabean == null ){
            try {
              rabean = (se.anatom.ejbca.webdist.rainterface.RAInterfaceBean) java.beans.Beans.instantiate(this.getClass().getClassLoader(), "se.anatom.ejbca.webdist.rainterface.RAInterfaceBean");
             } catch (ClassNotFoundException exc) {
                 throw new ServletException(exc.getMessage());
             }catch (Exception exc) {
                 throw new ServletException (" Cannot create bean of class "+"se.anatom.ejbca.webdist.rainterface.RAInterfaceBean", exc);
             }
             req.getSession().setAttribute("rabean", ejbcawebbean);
          }

        try{
          ejbcawebbean.initialize(req,AvailableAccessRules.REGULAR_VIEWCERTIFICATE);
          rabean.initialize(req,ejbcawebbean);                    
        } catch(Exception e){
           throw new java.io.IOException("Authorization Denied");
        }
        
        String issuerdn = req.getParameter(ISSUER_PROPERTY);        
        String certificatesn = req.getParameter(CERTIFICATEDN_PROPERTY);

        String command;
        // Keep this for logging.
        log.debug("Got request from "+req.getRemoteAddr());
        command = req.getParameter(COMMAND_PROPERTY_NAME);
        if (command == null)
            command = "";
        if ((command.equalsIgnoreCase(COMMAND_NSCERT) || command.equalsIgnoreCase(COMMAND_IECERT) || command.equalsIgnoreCase(COMMAND_CERT)) 
        	 && issuerdn != null && certificatesn != null) {
        	
        	BigInteger certsn = new BigInteger(certificatesn,16);
        	        	        
        	// Fetch the certificate and at the samt time check that the user is authorized to it.
        	
        	try {
				rabean.loadCertificates(certsn, issuerdn);

				CertificateView certview = rabean.getCertificate(0);
				
				X509Certificate cert = certview.getCertificate();
				byte[] enccert = cert.getEncoded();
				if (res.containsHeader("Pragma")) {
					log.debug("Removing Pragma header to avoid caching issues in IE");
					res.setHeader("Pragma",null);
				}
				if (res.containsHeader("Cache-Control")) {
					log.debug("Removing Cache-Control header to avoid caching issues in IE");
					res.setHeader("Cache-Control",null);
				}
				if (command.equalsIgnoreCase(COMMAND_NSCERT)) {
					res.setContentType("application/x-x509-ca-cert");
					res.setContentLength(enccert.length);
					res.getOutputStream().write(enccert);
					log.debug("Sent CA cert to NS client, len="+enccert.length+".");
				} else if (command.equalsIgnoreCase(COMMAND_IECERT)) {
					res.setHeader("Content-disposition", "attachment; filename=" + certview.getUsername() + ".crt");
					res.setContentType("application/octet-stream");
					res.setContentLength(enccert.length);
					res.getOutputStream().write(enccert);
					log.debug("Sent CA cert to IE client, len="+enccert.length+".");
				} else if (command.equalsIgnoreCase(COMMAND_CERT)) {
					byte[] b64cert = Base64.encode(enccert);
					String out = RequestHelper.BEGIN_CERTIFICATE_WITH_NL;                   
					out += new String(b64cert);
					out += RequestHelper.END_CERTIFICATE_WITH_NL;
					res.setHeader("Content-disposition", "attachment; filename=" + certview.getUsername() + ".pem");
					res.setContentType("application/octet-stream");
					res.setContentLength(out.length());
					res.getOutputStream().write(out.getBytes());
					log.debug("Sent CA cert to client, len="+out.length()+".");
				} else {
					res.setContentType("text/plain");
					res.getOutputStream().println("Commands="+COMMAND_NSCERT+" || "+COMMAND_IECERT+" || "+COMMAND_CERT);
					return;
				}
            } catch (Exception e) {
                PrintStream ps = new PrintStream(res.getOutputStream());
                e.printStackTrace(ps);
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Error getting certificates.");
                log.error("Error getting certificates.");
                log.error(e);
                return;
            }
        }
        else {
            res.setContentType("text/plain");
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request format");
            return;
        }

    } // doGet

}
