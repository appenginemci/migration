package com.sogeti.mci.migration.model;

public class EventMember {
	
	private int id;
	private int eventId;
	private int userId;
	private String role;
	private boolean active;
	private String inProgressFolderId;
	private String forApprovalFolderId;	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public int getEventMemberId() {
		return userId;
	}
	public void setEventMemberId(int userId) {
		this.userId = userId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getInProgressFolderId() {
		return inProgressFolderId;
	}
	public void setInProgressFolderId(String inProgressFolderId) {
		this.inProgressFolderId = inProgressFolderId;
	}
	public String getForApprovalFolderId() {
		return forApprovalFolderId;
	}
	public void setForApprovalFolderId(String forApprovalFolderId) {
		this.forApprovalFolderId = forApprovalFolderId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	

}
