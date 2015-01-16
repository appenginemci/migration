package com.sogeti.mci.migration.business;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.drive.Drive;
import com.google.api.services.gmail.model.Label;
import com.sogeti.mci.migration.helper.APIException;
import com.sogeti.mci.migration.helper.PropertiesManager;
import com.sogeti.mci.migration.helper.Validater;
import com.sogeti.mci.migration.model.Input;
import com.sogeti.mci.migration.security.CredentialLoader;
import com.sogeti.mci.migration.service.DriveService;
import com.sogeti.mci.migration.service.MailManagerService;

public class EventMigrator {
	
	// Inputs depending on environment
	static final String APPLICATION_ACCOUNT = PropertiesManager.getProperty("user.email");
	
	private static Drive drive;
	private static Directory directory;
	private static Input input;
	
	public static void main (String args[]) {
		
		setDrive(CredentialLoader.getDriveService(APPLICATION_ACCOUNT));
		setDirectory(CredentialLoader.getDirectoryService(APPLICATION_ACCOUNT));

		loadLicense();
		
		input = Validater.validateInput(args);
		if (input!=null) {
			System.out.println("\n---------- STARTING MIGRATION ------------------- "+input.getEventName());
			treatEvent();	
			System.out.println("---------- STOPING MIGRATION ------------------- "+input.getEventName()+"\n");
		} else {
			System.out.println("Invalid input");
		}
		
	}

	private static void treatEvent() {
		long startTime = System.nanoTime();			
			
		HashMap<Label, String> map = pairLabelsToFolders();
		
		if (map!=null && !map.isEmpty()) {
	    	long total = 0;
	    	for (Label label : map.keySet()) {
				try {
					total += MailManagerService.doJob(input.getTemporaryEventMailbox(), label, map.get(label));		    	
				} catch (Exception e) {
					APIException.handleException(e, "Failed to treat event", 0);
				}
			}
	    	System.out.println("Number of threads: "+total);
		}			
		long endTime = System.nanoTime();
		System.out.println("DURATION (s): "+(endTime - startTime)/1000000000);
	}



	private static HashMap<Label, String> pairLabelsToFolders() {
		
		//******************************************************************************************//
		//								FOLDER CREATION												//
		//******************************************************************************************//		

    	Set<Label> folderSet = DriveService.getFoldersByEvent(input.getTemporaryEventMailbox(), "Inbox/");//);
    	HashMap<Label, String> map = DriveService.createFolders(folderSet,input.getSite(),input.getEventName(),"Inbox");  
    	return map;
	}
	
	
	public static void loadLicense() {
		try
		 {
		   //Create a stream object containing the license file
		   InputStream email= MailManagerService.class.getResourceAsStream("/"+"Aspose.Email.lic");
		   InputStream words= MailManagerService.class.getResourceAsStream("/"+"Aspose.Words.lic");
		   
		   //Instantiate the License class
		   com.aspose.email.License licenseEmail=new com.aspose.email.License();
		   com.aspose.words.License licenseWords=new com.aspose.words.License();

		   //Set the license through the stream object
		   licenseEmail.setLicense(email);
		   licenseWords.setLicense(words);
		  
		   if(email != null)
			   email.close();
		   if(words != null)
			   words.close();
		 }
		 catch(Exception ex)
		 {
		   //Printing the exception, if it occurs
		   System.out.println(ex.toString());
		 }
	}
	

	public static Drive getDrive() {
		return drive;
	}

	public static void setDrive(Drive drive) {
		EventMigrator.drive = drive;
	}

	public static Directory getDirectory() {
		return directory;
	}

	public static void setDirectory(Directory directory) {
		EventMigrator.directory = directory;
	}

	public static Input getInput() {
		return input;
	}

}
