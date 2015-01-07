package com.sogeti.mci.migration.business;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.gmail.model.Label;
import com.sogeti.mci.migration.helper.PropertiesManager;
import com.sogeti.mci.migration.model.Event;
import com.sogeti.mci.migration.model.Member;
import com.sogeti.mci.migration.model.Site;
import com.sogeti.mci.migration.model.UserRoleEnum;
import com.sogeti.mci.migration.security.CredentialLoader;
import com.sogeti.mci.migration.service.DirectoryService;
import com.sogeti.mci.migration.service.DriveService;
import com.sogeti.mci.migration.service.EventService;
import com.sogeti.mci.migration.service.MailManagerService;
import com.sogeti.mci.migration.service.MemberService;
import com.sogeti.mci.migration.service.SiteService;

public class Launcher {
	
	// Inputs provided by MCI
	private static final String EVENT_NAME = "EventMigration";
	private static final String SITE =  "MCH-Events";
	private static final String TEMPORARY_EVENT_MAILBOX = "mimoun.chikhi@demo.sogeti-reseller.com";
	private static final String EVENT_TYPE = "Unstructured";
	private static final String LEADER_NAME = "mimoun.chikhi@demo.sogeti-reseller.com";
	private static final String[] TEAM_MEMBERS = {"toto.toto@demo.sogeti-reseller.com","mci.project@demo.sogeti-reseller.com"};
	
	// Inputs depending on environment
	private static final String DOMAIN = PropertiesManager.getProperty("domain");
	private static final String EVENT_ACCOUNT = EVENT_NAME+"@"+DOMAIN;
	private static final String TECHNICAL_ACCOUNT = "mci.project@demo.sogeti-reseller.com";
	
	private static Drive drive;
	private static Directory directory;
	
	public static void main (String args[]) {

		setDrive(CredentialLoader.getDriveService(TECHNICAL_ACCOUNT));
		setDirectory(CredentialLoader.getDirectoryService(TECHNICAL_ACCOUNT));

		loadLicense();
		long startTime = System.nanoTime();
		one();
		long endTime = System.nanoTime();
		System.out.println("DURATION (s): "+(endTime - startTime)/1000000000);
    	
	}

	private static void one() {
		
		//******************************************************************************************//
		//									INIT													//
		// Caution : The folders'id (event and event member) must be filled after the process		//
		// The authorization has not been handled													//
		//******************************************************************************************//

		// Create the site folder in Drive and get the file
		File sitefolder = DriveService.createFolder(SITE, null);
		// Create the event folder in Drive and get the file
		File eventFolder = DriveService.createFolder(EVENT_NAME, sitefolder.getId());
		// Create the site in DB and get the obj
		Site site = SiteService.insertSite(SITE, sitefolder.getId());
		// Create the event in DB and get the obj
		Event event = EventService.insertEvent(EVENT_NAME, EVENT_ACCOUNT, EVENT_TYPE, site, eventFolder);
		// add the member to google group
		com.google.api.services.admin.directory.model.Member googleMember = DirectoryService.addUsersToGroup(event.getGroupId(), LEADER_NAME);
		if (googleMember!=null) {
			// Create the leader in DB and get the obj
			Member leader = MemberService.insertMember(LEADER_NAME);
			// Attach the leader to the event
			event = EventService.attachMemberToEvent(leader, UserRoleEnum.EVENTHEAD, event);
			// Share the event folder with the leader
			DriveService.shareFolderWithMember(eventFolder.getId(), leader);
		}
		// Create the members in DB 
		for (int i = 0; i < TEAM_MEMBERS.length; i++) {
			// add the member to google group
			googleMember = DirectoryService.addUsersToGroup(event.getGroupId(), TEAM_MEMBERS[i]);
			// if the member cannot be linked to a google group, it is not considered
			if (googleMember!=null) {
				Member member = MemberService.insertMember(TEAM_MEMBERS[i]);
				// Attach the members to the event
				event = EventService.attachMemberToEvent(member, UserRoleEnum.TEAMMEMBER, event);
				// Share the event folder with the leader
				DriveService.shareFolderWithMember(eventFolder.getId(), member);
			}			
		}	
		
		//******************************************************************************************//
		//								FOLDER CREATION												//
		//******************************************************************************************//		

    	Set<Label> folderSet = DriveService.getFoldersByEvent(TEMPORARY_EVENT_MAILBOX, SITE+"/"+EVENT_NAME);
    	HashMap<Label, String> map = DriveService.createFolders(folderSet, event);
    	long total = 0;
    	for (Label label : map.keySet()) {
			try {
				total += MailManagerService.doJob(TEMPORARY_EVENT_MAILBOX, label, map.get(label));		    	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	System.out.println("Number of threads: "+total);
		
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
		Launcher.drive = drive;
	}

	public static Directory getDirectory() {
		return directory;
	}

	public static void setDirectory(Directory directory) {
		Launcher.directory = directory;
	}
	
	public static String getEventAccount() {
		return EVENT_ACCOUNT;
	}
	


}
