/*
 * Generated by XDoclet - Do not edit!
 */
package se.anatom.ejbca.authorization;

/**
 * Local interface for AdminEntityData.
 */
public interface AdminEntityDataLocal
   extends javax.ejb.EJBLocalObject
{

   public java.lang.String getAdminGroupName(  ) ;

   public int getCaId(  ) ;

   public int getMatchWith(  ) ;

   public int getMatchType(  ) ;

   public java.lang.String getMatchValue(  ) ;

   public se.anatom.ejbca.authorization.AdminEntity getAdminEntity( int caid ) ;

}
