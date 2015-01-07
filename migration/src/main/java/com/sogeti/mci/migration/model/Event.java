package com.sogeti.mci.migration.model;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.annotations.Expose;


public class Event {
	@Expose
	private String name;
	@Expose
	private String type;
	@Expose
	private String mail;
	@Expose
	private Collection<EventMember> users = new ArrayList<EventMember>();
	private String eventFolderId;
	private String newFolderId;
	private String closedFolderId;
	private String attachmentsFolderId;
	private String groupId;
	private int dbId;
	private int siteId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public Collection<EventMember> getUsers() {
		return users;
	}
	public void setUsers(Collection<EventMember> users) {
		this.users = users;
	}
	public String getEventFolderId() {
		return eventFolderId;
	}
	public void setEventFolderId(String eventFolderId) {
		this.eventFolderId = eventFolderId;
	}
	public String getNewFolderId() {
		return newFolderId;
	}
	public void setNewFolderId(String newFolderId) {
		this.newFolderId = newFolderId;
	}
	public String getClosedFolderId() {
		return closedFolderId;
	}
	public void setClosedFolderId(String closedFolderId) {
		this.closedFolderId = closedFolderId;
	}
	public String getAttachmentsFolderId() {
		return attachmentsFolderId;
	}
	public void setAttachmentsFolderId(String attachmentsFolderId) {
		this.attachmentsFolderId = attachmentsFolderId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public int getDbId() {
		return dbId;
	}
	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	public void addEvent(EventMember eventMember) {
		users.add(eventMember);
	}
	
}
