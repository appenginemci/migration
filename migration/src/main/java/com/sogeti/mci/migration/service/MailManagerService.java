package com.sogeti.mci.migration.service;

import java.util.List;

import com.google.api.services.drive.Drive;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.Message;
import com.sogeti.mci.migration.api.DriveAPI;
import com.sogeti.mci.migration.api.GmailAPI;
import com.sogeti.mci.migration.business.EventMigrator;
import com.sogeti.mci.migration.model.Document;
import com.sogeti.mci.migration.model.MultipleFormatMail;
import com.sogeti.mci.migration.security.CredentialLoader;

public class MailManagerService {
	
	public static final String LABEL = "Converted";
	
	private static String folderId;
	private static Gmail gmail;
	
	public static long doJob(String userId, Label label, String folderId) throws Exception {
		
		long total = 0L;

		setGmail(CredentialLoader.getGmailService(userId));
		
		setFolderId(folderId);

		List<com.google.api.services.gmail.model.Thread> threads = GmailAPI.listThreadsMatchingQuery(gmail, userId, "label:\""+label.getName()+"\" -label:"+LABEL);

		if (threads.size() == 0) {
			System.err.println("No message in "+label.getName());
		} else {
			System.out.println(threads.size()+" messages in "+label.getName());
			for (com.google.api.services.gmail.model.Thread thread : threads) {

				Message message = GmailAPI.getLastMessageFromThread(gmail, userId, thread);

				MultipleFormatMail multipleFormatMail = MultipleMailFormatService.create(userId, message);

				if (multipleFormatMail.getEvent() != null) {
					System.out.println("Converting message from Event : "+multipleFormatMail.getEvent().getMail());
					System.out.println(multipleFormatMail.getNameEmail());
				
					multipleFormatMail = MultipleMailFormatService.constructMailWithAttachments(multipleFormatMail, userId);
				    
					multipleFormatMail = MultipleMailFormatService.constructOutput(multipleFormatMail);
			    
					ConversionService.convertToDoc(multipleFormatMail);
					
					if (DocumentService.insertOrUpdateDocument(multipleFormatMail)) {
						if (!GmailAPI.labelizeThread(gmail, userId, message.getThreadId(), LABEL)) {
							System.err.println("Failed to labelize message");
						}
					} else {
						if (!deleteFiles(EventMigrator.getDrive(), multipleFormatMail.getDocument())) {
							System.err.println("Failed to delete corrupted files");
							// TODO LOG IN DB
						}
					}
				}else {
					System.err.println("No event found for email: " +multipleFormatMail.getNameEmail());
				} 
			}
			total += threads.size();
		}
		return total;
	}
	
	
	public static boolean deleteFiles(Drive drive, Document document) {
		
		boolean deleteSuccess = true;
		if (document.getdocumentId()!=null) {
			deleteSuccess = deleteSuccess && DriveAPI.deleteFile(drive, document.getdocumentId());
		}
		if (document.getAttachmentIds()!=null) {
			for (String id : document.getAttachmentIds()) {
				deleteSuccess = deleteSuccess && DriveAPI.deleteFile(drive, id);
			}
		}
		return deleteSuccess;
	}

	public static String getFolderId() {
		return folderId;
	}

	public static void setFolderId(String folderId) {
		MailManagerService.folderId = folderId;
	}

	public static Gmail getGmail() {
		return gmail;
	}

	public static void setGmail(Gmail gmail) {
		MailManagerService.gmail = gmail;
	}
}
