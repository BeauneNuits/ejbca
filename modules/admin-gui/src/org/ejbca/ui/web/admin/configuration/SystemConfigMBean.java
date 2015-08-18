/*************************************************************************
 *                                                                       *
 *  EJBCA Community: The OpenSource Certificate Authority                *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.ejbca.ui.web.admin.configuration;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.cesecore.certificates.ca.CADoesntExistsException;
import org.cesecore.certificates.ca.CaSessionLocal;
import org.cesecore.certificates.certificatetransparency.CTLogInfo;
import org.cesecore.keys.util.KeyTools;
import org.cesecore.util.CertTools;
import org.ejbca.config.GlobalConfiguration;
import org.ejbca.core.model.ra.raadmin.AdminPreference;
import org.ejbca.ui.web.admin.BaseManagedBean;

public class SystemConfigMBean extends BaseManagedBean implements Serializable {

    private static final long serialVersionUID = -6653610614851741905L;
    private static final Logger log = Logger.getLogger(SystemConfigMBean.class);

    /** GUI table representation of a SCEP alias that can be interacted with. */
    public class GuiInfo {
        private String title;
        private String headBanner;
        private String footBanner;
        private boolean enableEndEntityProfileLimitations;
        private boolean enableKeyRecovery;
        private boolean issueHardwareToken;
        private int hardTokenDataEncryptCA;
        private boolean useApprovalNotifications;
        private String approvalAdminEmail;
        private String approvalNoteFromAddress;
        private boolean useAutoEnrollment;
        private int autoEnrollmentCA;
        private boolean autoEnrollUseSSLConnection;
        private String autoEnrollAdServer;
        private int autoEnrollAdServerPort;
        private String autoEnrollConnectionDN;
        private String autoEnrollUserBaseDN;
        private String autoEnrollConnectionPassword;
        private Set<String> nodesInCluster;
        private boolean enableCommandLine;
        private boolean enableCommandLineDefaultUser;
        private List<CTLogInfo> ctLogs;
        private boolean publicWebCertChainOrderRootFirst;
        
        //Admin Preferences
        private int preferedLanguage;
        private int secondaryLanguage;
        private String theme;
        private int entriesPerPage;
        
        
        private GuiInfo(GlobalConfiguration globalConfig, AdminPreference adminPreference) {
            if(globalConfig == null) {
                globalConfig = getEjbcaWebBean().getGlobalConfiguration();
            }
            
            try {
                this.title = globalConfig.getEjbcaTitle();
                this.headBanner = globalConfig.getHeadBanner();
                this.footBanner = globalConfig.getFootBanner();
                this.enableEndEntityProfileLimitations = globalConfig.getEnableEndEntityProfileLimitations();
                this.enableKeyRecovery = globalConfig.getEnableKeyRecovery();
                this.issueHardwareToken = globalConfig.getIssueHardwareTokens();
                this.hardTokenDataEncryptCA = globalConfig.getHardTokenEncryptCA();
                this.useApprovalNotifications = globalConfig.getUseApprovalNotifications();
                this.approvalAdminEmail = globalConfig.getApprovalAdminEmailAddress();
                this.approvalNoteFromAddress = globalConfig.getApprovalNotificationFromAddress();
                this.useAutoEnrollment = globalConfig.getAutoEnrollUse();
                this.autoEnrollmentCA = globalConfig.getAutoEnrollCA();
                this.autoEnrollUseSSLConnection = globalConfig.getAutoEnrollSSLConnection();
                this.autoEnrollAdServer = globalConfig.getAutoEnrollADServer();
                this.autoEnrollAdServerPort = globalConfig.getAutoEnrollADPort();
                this.autoEnrollConnectionDN = globalConfig.getAutoEnrollConnectionDN();
                this.autoEnrollUserBaseDN = globalConfig.getAutoEnrollBaseDNUser();
                this.autoEnrollConnectionPassword = globalConfig.getAutoEnrollConnectionPwd();
                this.nodesInCluster = globalConfig.getNodesInCluster();
                this.enableCommandLine = globalConfig.getEnableCommandLineInterface();
                this.enableCommandLineDefaultUser = globalConfig.getEnableCommandLineInterfaceDefaultUser();
                this.publicWebCertChainOrderRootFirst = globalConfig.getPublicWebCertChainOrderRootFirst();
                
                ArrayList<CTLogInfo> ctlogs = new ArrayList<CTLogInfo>();
                Map<Integer, CTLogInfo> availableCTLogs = globalConfig.getCTLogs();
                for(int logid : availableCTLogs.keySet()) {
                    ctlogs.add(availableCTLogs.get(logid));
                }
                this.ctLogs = ctlogs;
                
                // Admin Preferences
                if(adminPreference == null) {
                    adminPreference = getEjbcaWebBean().getAdminPreference();
                }
                this.preferedLanguage = adminPreference.getPreferedLanguage();
                this.secondaryLanguage = adminPreference.getSecondaryLanguage();
                this.theme = adminPreference.getTheme();
                this.entriesPerPage = adminPreference.getEntriesPerPage();
            } catch (CADoesntExistsException e) {
                log.error(e.getLocalizedMessage(), e);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        
        public String getTitle() { return this.title; }
        public void setTitle(String title) { this.title=title; }
        public String getHeadBanner() { return this.headBanner; }
        public void setHeadBanner(String banner) { this.headBanner=banner; }
        public String getFootBanner() { return this.footBanner; }
        public void setFootBanner(String banner) { this.footBanner=banner; }
        public boolean getEnableEndEntityProfileLimitations() { return this.enableEndEntityProfileLimitations; }
        public void setEnableEndEntityProfileLimitations(boolean enableLimitations) { this.enableEndEntityProfileLimitations=enableLimitations; }
        public boolean getEnableKeyRecovery() { return this.enableKeyRecovery; }
        public void setEnableKeyRecovery(boolean enableKeyRecovery) { this.enableKeyRecovery=enableKeyRecovery; }
        public boolean getIssueHardwareToken() { return this.issueHardwareToken; }
        public void setIssueHardwareToken(boolean issueHWtoken) { this.issueHardwareToken=issueHWtoken; }
        public int getHardTokenDataEncryptCA() { return hardTokenDataEncryptCA; }
        public void setHardTokenDataEncryptCA(int caid) { this.hardTokenDataEncryptCA=caid; }
        public boolean getUseApprovalNotifications() { return useApprovalNotifications; }
        public void setUseApprovalNotifications(boolean useApprovalNotifications) { this.useApprovalNotifications=useApprovalNotifications; }
        public String getApprovalAdminEmail() { return approvalAdminEmail; }
        public void setApprovalAdminEmail(String email) { this.approvalAdminEmail=email; }
        public String getApprovalNoteFromAddress() { return approvalNoteFromAddress; }
        public void setApprovalNoteFromAddress(String email) { this.approvalNoteFromAddress=email; }
        public boolean getUseAutoEnrollment() { return this.useAutoEnrollment; }
        public void setUseAutoEnrollment(boolean useAutoEnrollment) { this.useAutoEnrollment=useAutoEnrollment; }
        public int getAutoEnrollmentCA() { return this.autoEnrollmentCA; }
        public void setAutoEnrollmentCA(int caid) {this.autoEnrollmentCA=caid; }
        public boolean getAutoEnrollUseSSLConnection() { return autoEnrollUseSSLConnection; }
        public void setAutoEnrollUseSSLConnection(boolean useSSLConnection) { this.autoEnrollUseSSLConnection=useSSLConnection; }
        public String getAutoEnrollAdServer() { return this.autoEnrollAdServer; }
        public void setAutoEnrollAdServer(String server) { this.autoEnrollAdServer=server; }
        public int getAutoEnrollAdServerPort() { return this.autoEnrollAdServerPort; }
        public void setAutoEnrollAdServerPort(int port) { this.autoEnrollAdServerPort=port; }
        public String getAutoEnrollConnectionDN() { return this.autoEnrollConnectionDN; }
        public void setAutoEnrollConnectionDN(String dn) { this.autoEnrollConnectionDN=dn; }
        public String getAutoEnrollUserBaseDN() { return this.autoEnrollUserBaseDN; }
        public void setAutoEnrollUserBaseDN(String dn) { this.autoEnrollUserBaseDN=dn; }
        public String getAutoEnrollConnectionPassword() { return this.autoEnrollConnectionPassword; }
        public void setAutoEnrollConnectionPassword(String password) { this.autoEnrollConnectionPassword=password; }
        public Set<String> getNodesInCluster() { return this.nodesInCluster; }
        public void setNodesInCluster(Set<String> nodes) { this.nodesInCluster=nodes; }
        public boolean getEnableCommandLine() { return this.enableCommandLine; }
        public void setEnableCommandLine(boolean enableCommandLine) { this.enableCommandLine=enableCommandLine; }
        public boolean getEnableCommandLineDefaultUser() { return this.enableCommandLineDefaultUser; }
        public void setEnableCommandLineDefaultUser(boolean enableCommandLineDefaultUser) { this.enableCommandLineDefaultUser=enableCommandLineDefaultUser; }
        public List<CTLogInfo> getCtLogs() {return this.ctLogs; }
        public void setCtLogs(List<CTLogInfo> ctlogs) { this.ctLogs=ctlogs; }
        public boolean getPublicWebCertChainOrderRootFirst() { return this.publicWebCertChainOrderRootFirst; }
        public void setPublicWebCertChainOrderRootFirst(boolean publicWebCertChainOrderRootFirst) { this.publicWebCertChainOrderRootFirst=publicWebCertChainOrderRootFirst; }
        
        // Admin Preferences
        public int getPreferedLanguage() { return this.preferedLanguage; }
        public void setPreferedLanguage(int preferedLanguage) { this.preferedLanguage=preferedLanguage; }
        public int getSecondaryLanguage() { return this.secondaryLanguage; }
        public void setSecondaryLanguage(int secondaryLanguage) { this.secondaryLanguage=secondaryLanguage; }
        public String getTheme() { return this.theme; }
        public void setTheme(String theme) { this.theme=theme; }
        public int getEntriesPerPage() { return this.entriesPerPage; }
        public void setEntriesPerPage(int entriesPerPage) { this.entriesPerPage=entriesPerPage; }
    }

    private String selectedTab = null;
    private GlobalConfiguration globalConfig = null;
    private AdminPreference adminPreference = null;
    private GuiInfo currentConfig = null;
    private ListDataModel nodesInCluster = null;
    private String currentNode = null;
    private ListDataModel ctLogs = null;
    private String currentCTLogURL = null;
    private int currentCTLogTimeout;
    private UploadedFile currentCTLogPublicKeyFile = null;
    private boolean excludeActiveCryptoTokensFromClearCaches = false;
    
    private final CaSessionLocal caSession = getEjbcaWebBean().getEjb().getCaSession();
    
    public SystemConfigMBean() {
        super();
    }
    
    public GlobalConfiguration getGlobalConfiguration() {
        if(globalConfig == null) {
            globalConfig = getEjbcaWebBean().getGlobalConfiguration();
        }
        return globalConfig;
    }
    
    public AdminPreference getAdminPreference() throws Exception {
        if(adminPreference == null) {
            adminPreference = getEjbcaWebBean().getDefaultAdminPreference();
        }
        return adminPreference;
    }
    
    /** @return cached or populate a new system configuration GUI representation for view or edit */
    public GuiInfo getCurrentConfig() {
        if(this.currentConfig == null) {
            try {
                this.currentConfig = new GuiInfo(getGlobalConfiguration(), getAdminPreference());
            } catch (Exception e) {
                String msg = "Cannot read Administrator Preferences.";
                log.info(msg + e.getLocalizedMessage());
                super.addNonTranslatedErrorMessage(msg);
            }
        }
        return this.currentConfig;
    }
    
    public String getSelectedTab() {
        final String tabHttpParam = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("tab");
        // First, check if the user has requested a valid tab
        if (tabHttpParam != null && getAvailableTabs().contains(tabHttpParam)) {
            // The requested tab is an existing tab. Flush caches so we reload the page content
            flushCache();
            selectedTab = tabHttpParam;
        }
        if (selectedTab == null) {
            // If no tab was requested, we use the first available tab as default
            selectedTab = getAvailableTabs().get(0);
        }
        return selectedTab;
    }
    
    public String getCurrentNode() {
        return this.currentNode;
    }
    public void setCurrentNode(String node) {
        this.currentNode = node;
    }
    
    public String getCurrentCTLogURL() {
        return currentCTLogURL;
    }
    
    public void setCurrentCTLogURL(String url) {
        this.currentCTLogURL = url;
    }
    
    public int getCurrentCTLogTimeout() {
        return this.currentCTLogTimeout;
    }
    
    public void setCurrentCTLogTimeout(int timeout) {
        this.currentCTLogTimeout = timeout;
    }
    
    public UploadedFile getCurrentCTLogPublicKeyFile() {
        return this.currentCTLogPublicKeyFile;
    }

    public void setCurrentCTLogPublicKeyFile(UploadedFile publicKeyFile) {
        this.currentCTLogPublicKeyFile = publicKeyFile;
    }
    
    public boolean getExcludeActiveCryptoTokensFromClearCaches() {
        return this.excludeActiveCryptoTokensFromClearCaches;
    }
    public void setExcludeActiveCryptoTokensFromClearCaches(boolean exclude) {
        this.excludeActiveCryptoTokensFromClearCaches = exclude;
    }
    public void clearAllCaches() {
        boolean execludeActiveCryptoTokens = getExcludeActiveCryptoTokensFromClearCaches();
        try {
            getEjbcaWebBean().clearClusterCache(execludeActiveCryptoTokens);
        } catch (Exception e) {
            String msg = "Cannot clear caches.";
            log.info(msg + e.getLocalizedMessage());
            super.addNonTranslatedErrorMessage(msg);
        }
    }
    
    
    /** Invoked when admin saves the configurations */
    public void saveCurrentConfig() {
        if(currentConfig != null) {
            try {
                globalConfig.setEjbcaTitle(currentConfig.getTitle());
                globalConfig.setHeadBanner(currentConfig.getHeadBanner());
                globalConfig.setFootBanner(currentConfig.getFootBanner());
                globalConfig.setEnableEndEntityProfileLimitations(currentConfig.getEnableEndEntityProfileLimitations());
                globalConfig.setEnableKeyRecovery(currentConfig.getEnableKeyRecovery());
                globalConfig.setIssueHardwareTokens(currentConfig.getIssueHardwareToken());
                globalConfig.setHardTokenEncryptCA(currentConfig.getHardTokenDataEncryptCA());
                globalConfig.setUseApprovalNotifications(currentConfig.getUseApprovalNotifications());
                globalConfig.setApprovalAdminEmailAddress(currentConfig.getApprovalAdminEmail());
                globalConfig.setApprovalNotificationFromAddress(currentConfig.getApprovalNoteFromAddress());
                globalConfig.setAutoEnrollUse(currentConfig.getUseAutoEnrollment());
                globalConfig.setAutoEnrollCA(currentConfig.getAutoEnrollmentCA());
                globalConfig.setAutoEnrollSSLConnection(currentConfig.getAutoEnrollUseSSLConnection());
                globalConfig.setAutoEnrollADServer(currentConfig.getAutoEnrollAdServer());
                globalConfig.setAutoEnrollADPort(currentConfig.getAutoEnrollAdServerPort());
                globalConfig.setAutoEnrollConnectionDN(currentConfig.getAutoEnrollConnectionDN());
                globalConfig.setAutoEnrollBaseDNUser(currentConfig.getAutoEnrollUserBaseDN());
                globalConfig.setAutoEnrollConnectionPwd(currentConfig.getAutoEnrollConnectionPassword());
                globalConfig.setNodesInCluster(currentConfig.getNodesInCluster());
                globalConfig.setEnableCommandLineInterface(currentConfig.getEnableCommandLine());
                globalConfig.setEnableCommandLineInterfaceDefaultUser(currentConfig.getEnableCommandLineDefaultUser());
                globalConfig.setPublicWebCertChainOrderRootFirst(currentConfig.getPublicWebCertChainOrderRootFirst());
                Map<Integer, CTLogInfo> ctlogsMap = new HashMap<Integer, CTLogInfo>();
                for(CTLogInfo ctlog : currentConfig.getCtLogs()) {
                    ctlogsMap.put(ctlog.getLogId(), ctlog);
                }
                globalConfig.setCTLogs(ctlogsMap);

                getEjbcaWebBean().saveGlobalConfiguration(globalConfig);
            } catch (Exception e) {
                String msg = "Cannot save System Configuration. " + e.getLocalizedMessage();
                log.info(msg);
                super.addNonTranslatedErrorMessage(msg);
            }
            
            try {
                adminPreference.setPreferedLanguage(currentConfig.getPreferedLanguage());
                adminPreference.setSecondaryLanguage(currentConfig.getSecondaryLanguage());
                adminPreference.setTheme(currentConfig.getTheme());
                adminPreference.setEntriesPerPage(currentConfig.getEntriesPerPage());
                
                getEjbcaWebBean().saveDefaultAdminPreference(adminPreference);
            } catch (Exception e) {
                String msg = "Cannot save Administrator Preferences. " + e.getLocalizedMessage();
                log.info(msg);
                super.addNonTranslatedErrorMessage(msg);
            }
        }
    }

    /** Invoked when admin saves the admin preferences */
    public void saveCurrentAdminPreferences() {
        if(currentConfig != null) {
            try {
                adminPreference.setPreferedLanguage(currentConfig.getPreferedLanguage());
                adminPreference.setSecondaryLanguage(currentConfig.getSecondaryLanguage());
                adminPreference.setTheme(currentConfig.getTheme());
                adminPreference.setEntriesPerPage(currentConfig.getEntriesPerPage());
                
                getEjbcaWebBean().saveDefaultAdminPreference(adminPreference);
            } catch (Exception e) {
                String msg = "Cannot save Administrator Preferences. " + e.getLocalizedMessage();
                log.info(msg);
                super.addNonTranslatedErrorMessage(msg);
            }
        }
    }

    
    public void flushCache() {
        globalConfig = null;
        adminPreference = null;
        currentConfig = null;
        nodesInCluster = null;
        ctLogs = null;
        excludeActiveCryptoTokensFromClearCaches = false;
    }
    
    public void toggleUseApprovalNotification() {
        currentConfig.setUseApprovalNotifications(!currentConfig.getUseApprovalNotifications());
    }
    
    public void toggleUseAutoEnrollment() {
        currentConfig.setUseAutoEnrollment(!currentConfig.getUseAutoEnrollment());
    }
    
    /** @return a list of all currently connected nodes in a cluster */
    public ListDataModel getNodesInCluster() {
        if (nodesInCluster == null) {
            List<String> nodesList = getListFromSet(currentConfig.getNodesInCluster());
            nodesInCluster = new ListDataModel(nodesList);
        }
        return nodesInCluster;
    }

    /** Invoked when the user wants to a add a new node to the cluster */
    @SuppressWarnings("unchecked")
    public void addNode() {
        final String nodeToAdd = getCurrentNode();
        Set<String> nodes = currentConfig.getNodesInCluster(); 
        nodes.add(nodeToAdd);
        currentConfig.setNodesInCluster(nodes);
        nodesInCluster = new ListDataModel(getListFromSet(nodes));
    }

    /** Invoked when the user wants to remove a node from the cluster */
    @SuppressWarnings("unchecked")
    public void removeNode() {
        final String nodeToRemove = (String) nodesInCluster.getRowData();
        Set<String> nodes = currentConfig.getNodesInCluster(); 
        nodes.remove(nodeToRemove);
        currentConfig.setNodesInCluster(nodes);
        nodesInCluster = new ListDataModel(getListFromSet(nodes));
    }
    
    private List<String> getListFromSet(Set<String> set) {
        List<String> list = new ArrayList<String>();
        if(set!=null && !set.isEmpty()) {
            for(String entry : set) {
                list.add(entry);
            }
        }
        return list;
    }
    
    public String getCtLogUrl() {
        return ((CTLogInfo) ctLogs.getRowData()).getUrl();
    }
    
    public int getCtLogTimeout() {
        return ((CTLogInfo) ctLogs.getRowData()).getTimeout();
    }
    
    public String getCtLogPublicKeyID() {
        return ((CTLogInfo) ctLogs.getRowData()).getLogKeyIdString();
    }
    
    public ListDataModel getCtLogs() {
        if(ctLogs == null) {
            List<CTLogInfo> logs = getCurrentConfig().getCtLogs();
            ctLogs = new ListDataModel(logs);
        }
        return ctLogs;
    }
    
    public void addCTLog() {
        
        if (currentCTLogURL == null) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No CTLog URL is set.", null));
            return;
        }
        if (currentCTLogPublicKeyFile == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload of CTLog public key file failed.", null));
            return;
        }
        
        CTLogInfo ctlogToAdd = null;
        try {
            byte[] uploadedFileBytes = currentCTLogPublicKeyFile.getBytes();
            byte[] keybytes = KeyTools.getBytesFromPEM(new String(uploadedFileBytes), CertTools.BEGIN_PUBLIC_KEY, CertTools.END_PUBLIC_KEY);
            ctlogToAdd = new CTLogInfo(currentCTLogURL, keybytes);
            ctlogToAdd.setTimeout(getCurrentCTLogTimeout());
        } catch (IOException e) {
            String msg = "Cannot parse the public key file " + getCurrentCTLogPublicKeyFile().getName() + ". " + e.getLocalizedMessage();
            log.info(msg);
            super.addNonTranslatedErrorMessage(msg);
        } catch (Exception e) {
            String msg = "Cannot add CTLog. " + e.getLocalizedMessage();
            log.info(msg);
            super.addNonTranslatedErrorMessage(msg);
        }

        if(ctlogToAdd != null) {
            List<CTLogInfo> ctlogs = currentConfig.getCtLogs(); 
            ctlogs.add(ctlogToAdd);
            currentConfig.setCtLogs(ctlogs);
            ctLogs = new ListDataModel(ctlogs);
        }
        
        saveCurrentConfig();
    }

    public void removeCTLog() {
        final CTLogInfo ctlogToRemove = ((CTLogInfo) ctLogs.getRowData());
        List<CTLogInfo> ctlogs = currentConfig.getCtLogs(); 
        ctlogs.remove(ctlogToRemove);
        currentConfig.setCtLogs(ctlogs);
        ctLogs = new ListDataModel(ctlogs);
        saveCurrentConfig();
    }
    
    public void uploadCTLogPublicKeyFile() {
        
    }
    
    
    /** @return a list of all CA names */
    public List<SelectItem> getAvailableCAsAndNoEncryptionOption() {
        final List<SelectItem> ret = getAvailableCAs();
        ret.add(new SelectItem(0, "No encryption"));
        return ret;
    }
    
    /** @return a list of all CA names and caids */
    public List<SelectItem> getAvailableCAs() {
        final List<SelectItem> ret = new ArrayList<SelectItem>();
        Map<Integer, String> caidToName = getEjbcaWebBean().getInformationMemory().getCAIdToNameMap();
        List<Integer> allCaIds = caSession.getAllCaIds();
        for(int caid : allCaIds) {
            String caname = caidToName.get(caid);
            ret.add(new SelectItem(caid, caname));
        }
        return ret;
    }
    
    public List<SelectItem> getAvailableLanguages() {
        final List<SelectItem> ret = new ArrayList<SelectItem>();
        final String[] availableLanguages = getEjbcaWebBean().getAvailableLanguages();
        final String[] availableLanguagesEnglishNames = getEjbcaWebBean().getLanguagesEnglishNames();
        final String[] availableLanguagesNativeNames = getEjbcaWebBean().getLanguagesNativeNames();
        for(int i=0; i<availableLanguages.length; i++) {
            String output = availableLanguagesEnglishNames[i];
            if((availableLanguagesEnglishNames != null) && (availableLanguagesNativeNames[i] != null)) {
                output += " - ";
            }
            output += availableLanguagesNativeNames[i];
            if((availableLanguagesEnglishNames != null) || (availableLanguagesNativeNames[i] != null)) {
                output += " ";
            }
            output += "[" + availableLanguages[i] + "]";
            ret.add(new SelectItem(i, output));
        }
        return ret;
    }
    
    public List<SelectItem> getAvailableThemes() {
        final List<SelectItem> ret = new ArrayList<SelectItem>();
        final String[] themes = globalConfig.getAvailableThemes();
        for(String theme : themes) {
            ret.add(new SelectItem(theme, theme));
        }
        return ret;
    }
    
    public List<SelectItem> getPossibleEntriesPerPage() {
        final List<SelectItem> ret = new ArrayList<SelectItem>();
        final String[] possibleValues = globalConfig.getPossibleEntiresPerPage();
        for(String value : possibleValues) {
            ret.add(new SelectItem(Integer.parseInt(value), value));
        }
        return ret;
    }
    
    public List<String> getAvailableTabs() {
        final List<String> availableTabs = new ArrayList<String>();
        availableTabs.add("Basic Configurations");
        availableTabs.add("CTLogs");
        availableTabs.add("Administrator Preferences");
        return availableTabs;
    }
    
}