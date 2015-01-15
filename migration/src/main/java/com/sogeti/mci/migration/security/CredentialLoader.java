package com.sogeti.mci.migration.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.groupssettings.GroupssettingsScopes;
import com.sogeti.mci.migration.model.DomainCredentials;

public class CredentialLoader {
	
		private static DomainCredentials domainCredentials = DomainCredentialsDAO.loadDomainCredentials();

		public static Gmail getGmailService(String userId){
			Gmail service = null;	
			GoogleCredentialItem googleCredentialItem = generateGoogleCredentialItem(getScopes(), userId);
			if(googleCredentialItem != null){
				service = new Gmail.Builder(googleCredentialItem.getHttpTransport(), googleCredentialItem.getJsonFactory(), null)
			      .setHttpRequestInitializer(setHttpTimeout(googleCredentialItem.getGoogleCredential())).setApplicationName("MCI").build();
			}				  
		    return service;
		}
		
		public static Drive getDriveService(String userId){
			Drive service = null;	
			GoogleCredentialItem googleCredentialItem = generateGoogleCredentialItem(getScopes(), userId);
			if(googleCredentialItem != null){
				service = new Drive.Builder(googleCredentialItem.getHttpTransport(), googleCredentialItem.getJsonFactory(), null)
			      .setHttpRequestInitializer(setHttpTimeout(googleCredentialItem.getGoogleCredential())).setApplicationName("MCI").build();
			}				  
		    return service;
		}

		public static Directory getDirectoryService(String userId) {
			Directory service = null;	
			GoogleCredentialItem googleCredentialItem = generateGoogleCredentialItem(getScopes(), userId);
			if(googleCredentialItem != null){
				service = new Directory.Builder(googleCredentialItem.getHttpTransport(), googleCredentialItem.getJsonFactory(), null)
			      .setHttpRequestInitializer(googleCredentialItem.getGoogleCredential()).setApplicationName("MCI").build();
			}				  
		    return service;
		}

		private static GoogleCredentialItem generateGoogleCredentialItem(ArrayList<String> scopes, String userId) {
			  HttpTransport httpTransport = new NetHttpTransport();
			  JacksonFactory jsonFactory = new JacksonFactory();
			  
			  GoogleCredential googleCredential = null;
			  GoogleCredentialItem googleCredentialItem = null;
			
			  File fp12 = getP12File(CredentialLoader.class.getResourceAsStream("/" + domainCredentials.getCertificatePath()));
			  //.setServiceAccountPrivateKeyFromP12File(new File(CredentialLoader.class.getResource("/" + domainCredentials.getCertificatePath()).toURI()))
			     
			  
			  try {
				googleCredential = new GoogleCredential.Builder()
				      .setTransport(httpTransport)
				      .setJsonFactory(jsonFactory)
				      .setServiceAccountId(domainCredentials.getServiceAccountEmail())
				      .setServiceAccountScopes(scopes)
				      .setServiceAccountUser(userId)		      
				      .setServiceAccountPrivateKeyFromP12File(fp12)
				      .build();
				
				googleCredentialItem = new GoogleCredentialItem();
				googleCredentialItem.setGoogleCredential(googleCredential);
				
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return googleCredentialItem;
		}
		
		private static File getP12File(InputStream  is) {
	        try {	             
	            OutputStream os = new FileOutputStream("is.p12");
	             
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            //read from is to buffer
	            while((bytesRead = is.read(buffer)) !=-1){
	                os.write(buffer, 0, bytesRead);
	            }
	            is.close();
	            //flush OutputStream to write any buffered data to file
	            os.flush();
	            os.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			return new File("is.p12");
		}
		

		  
		private static ArrayList<String> getScopes(){
			ArrayList<String> scopes = new ArrayList<String>();
			scopes.add(GmailScopes.GMAIL_MODIFY);
			scopes.add(DriveScopes.DRIVE);	
			scopes.add(DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY);
			scopes.add(DirectoryScopes.ADMIN_DIRECTORY_USER_ALIAS);
			scopes.add(DirectoryScopes.ADMIN_DIRECTORY_GROUP);
			scopes.add(GroupssettingsScopes.APPS_GROUPS_SETTINGS);
			return scopes;
		}
		
		private static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
			  return new HttpRequestInitializer() {
			    @Override
			    public void initialize(HttpRequest httpRequest) throws IOException {
			      requestInitializer.initialize(httpRequest);
			      httpRequest.setConnectTimeout(5 * 60000);  // 3 minutes connect timeout
			      httpRequest.setReadTimeout(5 * 60000);  // 3 minutes read timeout
			    }
			  };
		}
		
}