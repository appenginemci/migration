package com.sogeti.mci.migration.model;

import java.util.ArrayList;
import java.util.Collection;

public class Document {
	
	private String documentId;
	private Collection<String> attachmentIds;
	
	public String getdocumentId() {
		return documentId;
	}
	public void setdocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public Collection<String> getAttachmentIds() {
		return attachmentIds;
	}
	public void setAttachmentIds(Collection<String> attachmentIds) {
		this.attachmentIds = attachmentIds;
	}
	public void addAttachmentId(String attachmentId) {
		if (attachmentIds==null) {
			attachmentIds = new ArrayList<String>();
		}
		this.attachmentIds.add(attachmentId);
	}
	

}
