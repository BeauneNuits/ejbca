package se.anatom.ejbca.ra.authorization;

import java.util.Vector;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.naming.*;
import javax.ejb.CreateException;
import java.rmi.RemoteException;

import se.anatom.ejbca.ra.GlobalConfiguration;
import se.anatom.ejbca.ra.raadmin.IRaAdminSessionHome;
import se.anatom.ejbca.ra.raadmin.IRaAdminSessionRemote;
import se.anatom.ejbca.log.Admin;

/**
 * 
 *
 * @version $Id: AvailableResources.java,v 1.3 2003-01-12 17:16:30 anatom Exp $
 */
public class AvailableResources {
        
    /** Creates a new instance of AvailableResources */
    public AvailableResources(GlobalConfiguration globalconfiguration) throws NamingException, CreateException, RemoteException {   
      this.profileendings=GlobalConfiguration.ENDENTITYPROFILE_ENDINGS;
      this.profileprefix= GlobalConfiguration.ENDENTITYPROFILEPREFIX;
      this.usestrongauthentication = globalconfiguration.getEnableEndEntityProfileLimitations();

      InitialContext jndicontext = new InitialContext();     
      Object objl = jndicontext.lookup("RaAdminSession");
      IRaAdminSessionHome raadminsessionhome = (IRaAdminSessionHome) javax.rmi.PortableRemoteObject.narrow(objl, 
                                                                       IRaAdminSessionHome.class);
      raadminsession = raadminsessionhome.create();
      
      objl = jndicontext.lookup("AuthorizationSession");
      IAuthorizationSessionHome authorizationsessionhome = (IAuthorizationSessionHome) javax.rmi.PortableRemoteObject.narrow(objl, 
                                                                       IAuthorizationSessionHome.class);
      authorizationsession = authorizationsessionhome.create();
      authorizationsession.init(globalconfiguration);
    }
    
    // Public methods 
    /** Returns all the resources and subresources from the given subresource */
    public String[] getResources()  {
      Vector resources = new Vector();
      String[] dummy = {};
      
      insertAvailableRules(resources);
      if(usestrongauthentication) 
        insertAvailableProfileRules(resources);
      
      Collections.sort(resources);
      return (String[]) resources.toArray(dummy);  
    }
    
    // Private methods
    private void insertAvailableRules(Vector resources) {
      try{  
        resources.addAll(authorizationsession.getAvailableAccessRules(new Admin(Admin.TYPE_INTERNALUSER)));  
      }catch(RemoteException e){}
    }
    
    private void insertAvailableProfileRules(Vector resources){
      Admin admin = new Admin(Admin.TYPE_INTERNALUSER);  
      try{  
        Collection profilenames = raadminsession.getEndEntityProfileNames(admin);
        if(profilenames != null){
          Iterator i = profilenames.iterator();
      
          while(i.hasNext()){
            String name = (String) i.next();
            int id = raadminsession.getEndEntityProfileId(admin, name);
            resources.addElement(profileprefix + id);
            for(int j=0;j < profileendings.length; j++){     
              resources.addElement(profileprefix + id +profileendings[j]);             
            }        
          }
        }
      }catch(RemoteException e){}  
    }
    // Private fields
    private String[] profileendings;
    private String profileprefix;
    private IRaAdminSessionRemote raadminsession;
    private IAuthorizationSessionRemote authorizationsession;
    private boolean usestrongauthentication;
}
