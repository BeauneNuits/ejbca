package se.anatom.ejbca.authorization;

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

/**
 * For docs, see AdminGroupDataBean
 *
 * @version $Id: AdminGroupDataLocalHome.java,v 1.3 2003-09-03 14:49:55 herrvendil Exp $
 **/

public interface AdminGroupDataLocalHome extends javax.ejb.EJBLocalHome {

    public AdminGroupDataLocal create(String admingroupname, int caid)
        throws CreateException;

    public AdminGroupDataLocal findByPrimaryKey(AdminGroupPK pk)
        throws FinderException;

    public Collection findAll()
        throws FinderException;

}

