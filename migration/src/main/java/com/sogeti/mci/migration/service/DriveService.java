package com.sogeti.mci.migration.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.sogeti.mci.migration.api.DriveAPI;
import com.sogeti.mci.migration.api.GmailAPI;
import com.sogeti.mci.migration.business.EventMigrator;
import com.sogeti.mci.migration.dao.SettingsDAO;
import com.sogeti.mci.migration.model.Member;
import com.sogeti.mci.migration.model.MultipleFormatMail;
import com.sogeti.mci.migration.security.CredentialLoader;


public class DriveService 
{    
	static Drive drive = EventMigrator.getDrive();
 
	public static File getFolder(String emailId, String name, String parentFolderId) {
		File file = DriveAPI.getFolder(drive, name, parentFolderId);
		return file;
	}

	public static Set<Label> getFoldersByEvent(String emailId, String labelRoot) {
    	Gmail gmail = CredentialLoader.getGmailService(emailId);
    	List<Label> list = GmailAPI.getListLabel(gmail, emailId);
    	Set<Label> set = new HashSet<Label>();
    	if (list.size()>0) {
    		for (Label label : list) {
        		if (label.getName().startsWith(labelRoot)) {
        			set.add(label);
        		} 
    		}     		
    	}	   	
    	return set;
    }
    
    public static HashMap<Label, String> createFolders(Set<Label> set, String site, String eventName, String labelRoot) {
    	HashMap<Label,String> map = new HashMap<Label,String>();
    	for (Label elt : set) {
			String[] folders = elt.getName().replaceFirst(labelRoot, site+"/"+eventName).split("/");
			String parentFolderId = null;
			for (int i = 0; i < folders.length; i++) {
				String folderName= folders[i];
		    	File file = createFolder(folderName, parentFolderId);
		    	if (file!=null) {
			    	parentFolderId = file.getId();	
			    	map.put(elt, parentFolderId);
		    	}
			}			
		}
    	return map;
    }

	public static File createFolder(String name, String parentFolderId) {
		File file = DriveAPI.getFolder(drive, name, parentFolderId);
		if (file==null) {
    		file = DriveAPI.createFolder(drive, name, parentFolderId);
    	} 
    	return file;
	}
	
	public static File storeToDrive(ByteArrayOutputStream baos, MultipleFormatMail multipleFormatEmail,
			String description, String mimetypeBody, String mimetypeFile,
			String extension) throws IOException, GeneralSecurityException,
			URISyntaxException {
		System.out.print("Storing file " + multipleFormatEmail.getNameEmail() + " in folder Id ");
		
		
		ByteArrayContent mediaContent = new ByteArrayContent(mimetypeFile,baos.toByteArray());
		String folderId = MailManagerService.getFolderId();
		File body = null;
		 
		if (multipleFormatEmail.isNewEmail()) {
			//Create a new empty file for receiving the copy 
			body = createGoogleFile(multipleFormatEmail.getNameEmail(), description, mimetypeBody, folderId);
			//Copy the reply add-on on the targeted location
			body = DriveAPI.copyFile(drive, SettingsDAO.getInstance().getSetting("templateDocId"),body);
		} else {
			body = DriveAPI.getFile(drive, multipleFormatEmail.getDocument().getdocumentId());
		}

		System.out.println(" ("+folderId+")");
		
		return DriveAPI.updateFile(drive, body.getId(), mediaContent);	
	}
	
	
	private static File createGoogleFile(String name, String description,
			String mimetypeBody, String folderId) {
		// Insert a file
		File body = new File();
		body.setTitle(name);
		body.setDescription(description);
		body.setMimeType(mimetypeBody);

		if (folderId != null && folderId.length() > 0) {
			body.setParents(Arrays.asList(new ParentReference().setId(folderId)));
		}
		return body;
	}
	
	static File storeAttachmentToDrive(ByteArrayOutputStream baos,
			MultipleFormatMail multipleFormatEmail, String name, String description,
			String mimetypeBody, String mimetypeFile, String extension)
			 {

		System.out.println("Storing attachment file " + name + " in folder "
				+ multipleFormatEmail.getNameEmail());

		return doAttachementInsertion(baos, name, description, mimetypeBody, mimetypeFile,
				extension, multipleFormatEmail.getEvent().getAttachmentsFolderId());
	}
	
	private static File doAttachementInsertion(ByteArrayOutputStream baos, String name,
			String description, String mimetypeBody, String mimetypeFile,
			String extension, String folderId)  {
		if (folderId==null) {
			// If no attachement drive is defined, store attachment near the doc
			folderId = MailManagerService.getFolderId();
		}
		File body = createGoogleFile(name, description, mimetypeBody, folderId);
				
		ByteArrayContent mediaContent = new ByteArrayContent(mimetypeFile,	baos.toByteArray());

		File file = DriveAPI.insertFile(drive, body, mediaContent);

		return file;
	}	
	
	public static Permission shareFolderWithMember(String eventFolderId, Member member) {
		//String adminUser = PropertiesManager.getProperty("admin_user");
		Permission newPermission = null;
		//if (!adminUser.equals(member.getUserId())) {
			newPermission = new Permission();
			newPermission.setValue(member.getUserId());
			newPermission.setType("user");
			newPermission.setRole("writer");
			newPermission = DriveAPI.addPermission(drive, eventFolderId, newPermission);
		//}
		return newPermission;
	}


    
}
