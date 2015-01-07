package com.sogeti.mci.migration.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.sogeti.mci.migration.model.DomainCredentials;

public class Credentials {
	
	  private static final String APP_NAME = "Email Converter";
	  // Path to the client_secret.json file downloaded from the Developer Console
	  private static final String CLIENT_SECRET_PATH = "client_secret.json";
	  
	  private static GoogleClientSecrets clientSecrets;
	  
	  private static final GoogleCredential credential = getCredential();
	  
	  private static DomainCredentials domainCredentials = DomainCredentialsDAO.loadDomainCredentials();
      private static GoogleCredentialItem googleCredentialItem = generateGoogleCredentialItem(getScopes());
	  
	  public static GoogleCredential getCredential()  {
		 
		  	GoogleCredential credential = null;
		  	
		  	try {

		    HttpTransport httpTransport = new NetHttpTransport();
		    JsonFactory jsonFactory = new JacksonFactory();

		    clientSecrets = GoogleClientSecrets.load(jsonFactory,new FileReader("D:\\Actelion\\workspace\\convertmail\\src\\main\\resources\\"+CLIENT_SECRET_PATH));

		    // Allow user to authorize via url.
		    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
		        httpTransport, jsonFactory, clientSecrets, getScopes())
		        .setAccessType("online")
		        .setApprovalPrompt("auto").build();

		    String url = flow.newAuthorizationUrl().setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI)
		        .build();
		    System.out.println("Please open the following URL in your browser then type"
		                       + " the authorization code:\n" + url);

		    // Read code entered by user.
		    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		    String code = br.readLine();

		    // Generate Credential using retrieved code.
		    GoogleTokenResponse response = flow.newTokenRequest(code)
		        .setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI).execute();
		    credential = new GoogleCredential()
		        .setFromTokenResponse(response);
		    
		  	} catch (Exception e) {
		  		e.printStackTrace();
		  	}
		  	
		    return credential;
		  
	  }
	  
	  private static GoogleCredentialItem generateGoogleCredentialItem(ArrayList<String> scopes){
		  HttpTransport httpTransport = new NetHttpTransport();
		  JacksonFactory jsonFactory = new JacksonFactory();
		  
		  GoogleCredential googleCredential = null;
		  GoogleCredentialItem googleCredentialItem = null;
		try {
			googleCredential = new GoogleCredential.Builder()
			      .setTransport(httpTransport)
			      .setJsonFactory(jsonFactory)
			      .setServiceAccountId(domainCredentials.getServiceAccountEmail())
			      .setServiceAccountScopes(scopes)
			      .setServiceAccountUser(domainCredentials.getUserEmailAddress())
			      .setServiceAccountPrivateKeyFromP12File(new File(CredentialLoader.class.getResource("/" + domainCredentials.getCertificatePath()).toURI()))
			      .build();
			
			googleCredentialItem = new GoogleCredentialItem();
			googleCredentialItem.setGoogleCredential(googleCredential);
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return googleCredentialItem;
	}
	  
	private static ArrayList<String> getScopes(){
		ArrayList<String> scopes = new ArrayList<String>();
		scopes.add(GmailScopes.GMAIL_READONLY); //"https://www.googleapis.com/auth/gmail.readonly"
		scopes.add(DriveScopes.DRIVE);		
		return scopes;
	}	
	  

	public static Gmail getGmailService() throws FileNotFoundException, IOException {		
		Gmail service = null;
	    HttpTransport httpTransport = new NetHttpTransport();
	    JsonFactory jsonFactory = new JacksonFactory();
	    
		service = new Gmail.Builder(httpTransport, jsonFactory, credential).setApplicationName(APP_NAME).build();
		return service;
	}
	   
	   
	   public static Drive getDriveService() throws FileNotFoundException, IOException {		
			Drive service = null;
		    HttpTransport httpTransport = new NetHttpTransport();
		    JsonFactory jsonFactory = new JacksonFactory();
		    
		    service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName(APP_NAME).build();
			return service;
		}   
}
