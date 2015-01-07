package com.sogeti.mci.migration.api;

import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;

public class DriveAPI {
	
	public static File getFolder(Drive drive, String folderName, String parentId)  {

		File toReturn = null;

		Drive.Files.List request;
		
		String withOrWithoutParent = (parentId==null?"'":"' AND '" + parentId + "' in parents");

		String query = "mimeType='application/vnd.google-apps.folder' AND trashed=false AND title='"
				+ folderName + withOrWithoutParent;
		
		try {
			request = drive.files().list();
			request = request.setQ(query);
			FileList files = request.execute();
			if (files.getItems().size() > 0) {
				toReturn = files.getItems().get(0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return toReturn;
	}

	public static File createFolder(Drive drive, String folderName,	String parentId)  {
		File toReturn = new File();
		toReturn.setTitle(folderName);
		toReturn.setMimeType("application/vnd.google-apps.folder");
		if (parentId!=null) {
			toReturn.setParents(Arrays.asList(new ParentReference().setId(parentId)));
		}
		try {
			toReturn = drive.files().insert(toReturn).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return toReturn;
	}
	
	public static File getFile(Drive drive, String id) {
		File file = null;
		try {
			file = drive.files().get(id).execute();
		} catch (IOException e) {
			// TODO LOG IN DB
			e.printStackTrace();
		}
		return file;
	}
	
	
	public static boolean deleteFile(Drive drive, String id) {
		boolean deleteOk = false;
		try {
			drive.files().delete(id).execute();
			deleteOk = true;
		} catch (IOException e) {
			// TODO LOG IN DB
			e.printStackTrace();
		}
		return deleteOk;
	}
	
	public static File copyFile(Drive service, String originFileId,
			File copy) {
		    try {
		      return service.files().copy(originFileId, copy).execute();
		    } catch (IOException e) {
		      System.out.println("An error occurred: " + e);e.printStackTrace();
		    }
		    return null;
	 }
	
	public static String getParentFolderId(Drive service, String fileId) {
		String folderId = null;
	    try {
	      ParentList parents = service.parents().list(fileId).execute();

	      for (ParentReference parent : parents.getItems()) {
	        //System.out.println("File Id: " + parent.getId());
	        folderId = parent.getId();
	      }
	    } catch (IOException e) {
	      System.out.println("An error occurred: " + e);
	    }
	    return folderId;
	}
	
	public static File updateFile(Drive service, String fileId, ByteArrayContent mediaContent) {
	    try {
	      // First retrieve the file from the API.
	      File file = service.files().get(fileId).execute();
	      file.getLabels().setViewed(false);
	      
	      // Send the request to the API.
	      File updatedFile = service.files().update(fileId, file, mediaContent).execute();
	      
	      
	      return updatedFile;
	    } catch (IOException e) {
	      System.out.println("An error occurred: " + e);
	      return null;
	    }
	}

	public static File insertFile(Drive drive, File body, ByteArrayContent mediaContent) {
		File file = null;
		try {
			file = drive.files().insert(body, mediaContent).execute();
		} catch (IOException e) {
			// TODO LOG IN DB
			e.printStackTrace();
		}
		return file;
	}
	
	public static Permission addPermission(Drive drive, String eventFolderId, Permission newPermission) {
		Permission p = null;
		try {
			p = drive.permissions().insert(eventFolderId, newPermission).setSendNotificationEmails(true).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

}
