package se.anatom.ejbca.ra.authorization;

import java.util.Vector;
import java.util.Collections;
import java.io.Serializable;

/**
 * A class that represents a group of users and their access rules.
 *
 * @version $Id: AdminGroup.java,v 1.2 2003-01-19 09:40:13 herrvendil Exp $
 */
public class AdminGroup implements Serializable {
                               
/*    public static final String SPECIALADMINGROUP_PUBLICWEBADMIN        = "SPECIAL_PUBLIC_WEBADMIN";
    public static final String SPECIALADMINGROUP_CACOMMANDLINEADMIN    = "SPECIAL_CA_COMMANDLINEADMIN";
    public static final String SPECIALADMINGROUP_RACOMMANDLINEADMIN    = "SPECIAL_RA_COMMANDLINEADMIN";
    public static final String SPECIALADMINGROUP_BATCHCOMMANDLINEADMIN = "SPECIAL_BATCH_COMMANDLINEADMIN"; */
    
    /** Creates a new instance of AdminGroup */
    public AdminGroup() {
      accessrules = new Vector();
      adminentities = new Vector();
    }

    public AdminGroup(Vector accessrules, Vector adminentities){
      this.accessrules=accessrules;
      this.adminentities=adminentities;
    }

    // Public methods
    /** Returns the number of accessrules applied to this admingroup */
    public int getNumberOfAccessRules() {
      return accessrules.size();
    }

    /** Returns an array containing all the admingroup's accessrules.*/
    public AccessRule[] getAccessRules() {
      AccessRule[] dummy={};
      return (AccessRule[]) accessrules.toArray(dummy);
    }

    /** Returns the number of user entities in this admingroup */
    public int getNumberAdminEntities() {
      return adminentities.size();
    }

    /** Returns an array containing all the admingroup's user entities.*/
    public AdminEntity[] getAdminEntities() {
      AdminEntity[] dummy = {};
      return (AdminEntity[]) adminentities.toArray(dummy);
    }

    /** Method that given an array of available resources returns which isn't already
     * in use by the rule set. */
    public String[] nonUsedResources(String[] availableresources){
      Vector nonusedresources = new Vector();
      String[] dummy = {};
      int result;
      Collections.sort(accessrules);
      if(availableresources != null){
        for( int i=0; i < availableresources.length;i++){
          result=java.util.Collections.binarySearch(accessrules,new AccessRule(availableresources[i],0,false));
          if(result < 0){
            // Resource isn't in use.
            nonusedresources.addElement(availableresources[i]);
          }
        }
      }
      return  (String[]) nonusedresources.toArray(dummy);
    }
    // Private methods

    // Private fields
    private Vector accessrules;
    private Vector adminentities;
 }
