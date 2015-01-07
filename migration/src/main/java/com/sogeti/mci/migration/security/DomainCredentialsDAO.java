package com.sogeti.mci.migration.security;

import com.sogeti.mci.migration.helper.PropertiesManager;
import com.sogeti.mci.migration.model.DomainCredentials;



public class DomainCredentialsDAO {

	public static DomainCredentials loadDomainCredentials(){
		DomainCredentials domainCredentials = new DomainCredentials();
		
		domainCredentials.setUserEmailAddress(PropertiesManager.getProperty("user.email"));
		domainCredentials.setServiceAccountEmail(PropertiesManager.getProperty("service.account.email"));
		domainCredentials.setCertificatePath(PropertiesManager.getProperty("service.certificate.path"));

		return domainCredentials;
	}

}