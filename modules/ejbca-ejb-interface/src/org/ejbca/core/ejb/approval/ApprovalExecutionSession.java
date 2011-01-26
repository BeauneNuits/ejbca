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

package org.ejbca.core.ejb.approval;

import org.ejbca.core.EjbcaException;
import org.ejbca.core.model.approval.AdminAlreadyApprovedRequestException;
import org.ejbca.core.model.approval.Approval;
import org.ejbca.core.model.approval.ApprovalRequestExecutionException;
import org.ejbca.core.model.approval.ApprovalRequestExpiredException;
import org.ejbca.core.model.authorization.AuthorizationDeniedException;
import org.ejbca.core.model.log.Admin;
import org.ejbca.core.model.ra.raadmin.GlobalConfiguration;

public interface ApprovalExecutionSession {

    /**
     * Method used to approve an approval requests. It does the follwing 1.
     * checks if the approval with the status waiting exists, throws an
     * ApprovalRequestDoesntExistException otherwise 2. check if the
     * administrator is authorized using the follwing rules: 2.1 if
     * getEndEntityProfile is ANY_ENDENTITYPROFILE then check if the admin is
     * authorized to AccessRulesConstants.REGULAR_APPROVECAACTION othervise
     * AccessRulesConstants.REGULAR_APPORVEENDENTITY and APPROVAL_RIGHTS for the
     * end entity profile. 2.2 Checks if the admin is authoried to the approval
     * requests getCAId() 3. looks upp the username of the administrator and
     * checks that no approval have been made by this user earlier. 4. Runs the
     * approval command in the end entity bean.
     * 
     * @param admin
     * @param approvalId
     * @param approval
     * @param gc
     *            is the GlobalConfiguration used for notification info
     * @throws ApprovalRequestExpiredException
     * @throws ApprovalRequestExecutionException
     * @throws AuthorizationDeniedException
     * @throws ApprovalRequestDoesntExistException
     * @throws AdminAlreadyApprovedRequestException
     * @throws EjbcaException
     */
    public void approve(Admin admin, int approvalId, Approval approval, GlobalConfiguration gc) throws ApprovalRequestExpiredException,
            ApprovalRequestExecutionException, AuthorizationDeniedException, AdminAlreadyApprovedRequestException, EjbcaException;


}
