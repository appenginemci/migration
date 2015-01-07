package com.sogeti.mci.migration.model;

import javax.mail.internet.MimeMessage;

import com.aspose.email.MailMessage;
import com.google.api.services.gmail.model.Message;

public class MultipleFormatMail {
	
	private MimeMessage mimeMessage;
	private Message gmailMessage;
	private MailMessage asposeMessage;
	private Message gmailRawMessage;
	private String nameEmail;
	private boolean newEmail = true;
	private Event event;
	private Document document;
	
	public MimeMessage getMimeMessage() {
		return mimeMessage;
	}
	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}
	public Message getGmailMessage() {
		return gmailMessage;
	}
	public void setGmailMessage(Message gmailMessage) {
		this.gmailMessage = gmailMessage;
	}
	public MailMessage getAsposeMessage() {
		return asposeMessage;
	}
	public void setAsposeMessage(MailMessage asposeMessage) {
		this.asposeMessage = asposeMessage;
	}
	public Message getGmailRawMessage() {
		return gmailRawMessage;
	}
	public void setGmailRawMessage(Message gmailRawMessage) {
		this.gmailRawMessage = gmailRawMessage;
	}
	public String getNameEmail() {
		return nameEmail;
	}
	public void setNameEmail(String nameEmail) {
		this.nameEmail = nameEmail;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public boolean isNewEmail() {
		return newEmail;
	}
	public void setNewEmail(boolean newEmail) {
		this.newEmail = newEmail;
	}	

}
