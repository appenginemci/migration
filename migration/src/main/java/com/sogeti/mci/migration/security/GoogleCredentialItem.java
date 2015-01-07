package com.sogeti.mci.migration.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GoogleCredentialItem {
	
	private HttpTransport httpTransport = new NetHttpTransport();
	private JacksonFactory jsonFactory = new JacksonFactory();
	private GoogleCredential googleCredential;
	
	public GoogleCredential getGoogleCredential() {
		return googleCredential;
	}
	public void setGoogleCredential(GoogleCredential googleCredential) {
		this.googleCredential = googleCredential;
	}
	public HttpTransport getHttpTransport() {
		return httpTransport;
	}
	public JacksonFactory getJsonFactory() {
		return jsonFactory;
	}
	
}
