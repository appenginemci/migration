package com.sogeti.mci.migration.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import com.aspose.email.MailMessageSaveType;
import com.aspose.words.Document;
import com.aspose.words.LoadFormat;
import com.aspose.words.LoadOptions;
import com.aspose.words.SaveFormat;
import com.google.api.services.drive.model.File;
import com.sogeti.mci.migration.helper.ByteArrayInOutStream;
import com.sogeti.mci.migration.model.MultipleFormatMail;

public class ConversionService {

	public static File convertToDoc(MultipleFormatMail multipleFormatMail) throws Exception {
	    ByteArrayInOutStream outputAsposeMessage = new ByteArrayInOutStream();
	    multipleFormatMail.getAsposeMessage().save(outputAsposeMessage, MailMessageSaveType.getMHtmlFormat());	
	    
	    LoadOptions lo = new LoadOptions();
	    lo.setLoadFormat(LoadFormat.MHTML);
	    Document doc = new Document(outputAsposeMessage.getInputStream(),lo);
	    
	    ByteArrayOutputStream outputDoc = new ByteArrayOutputStream();
	    doc.save(outputDoc, SaveFormat.DOC);
	    
		File finalFile = write(outputDoc, multipleFormatMail);	
		
		if (finalFile==null || finalFile.isEmpty()) {
			System.err.println("Failed to create final file : "+multipleFormatMail.getNameEmail());
		} else {
			multipleFormatMail.getDocument().setdocumentId(finalFile.getId());
		}
		
		return finalFile;
	}

	private static File write(ByteArrayOutputStream baos, MultipleFormatMail multipleFormatMail) {
		File file = null;
		try {
			file = DriveService.storeToDrive(baos, multipleFormatMail,
					"An email converted to a document",
					"application/vnd.google-apps.document", "application/msword",
					"doc");
		} catch (IOException | GeneralSecurityException | URISyntaxException e) {
			// TODO LOG IN DB
			e.printStackTrace();
		}	
		return file;
	}

}
