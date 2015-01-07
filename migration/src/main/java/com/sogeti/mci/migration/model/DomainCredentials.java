package com.sogeti.mci.migration.model;

public class DomainCredentials {
	
	private String userEmailAddress;
	private String serviceAccountEmail;
	private String certificatePath;
	
	public String getUserEmailAddress() {
		return userEmailAddress;
	}
	public void setUserEmailAddress(String userEmailAddress) {
		this.userEmailAddress = userEmailAddress;
	}
	public String getServiceAccountEmail() {
		return serviceAccountEmail;
	}
	public void setServiceAccountEmail(String serviceAccountEmail) {
		this.serviceAccountEmail = serviceAccountEmail;
	}
	public String getCertificatePath() {
		return certificatePath;
	}
	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}
}