package com.sogeti.mci.migration.model;

public class Input {
	
	private String eventName;
	private String site;
	private String temporaryEventMailbox;
	private String eventType;
	private String leaderName;
	private String teamMembers;
	
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getTemporaryEventMailbox() {
		return temporaryEventMailbox;
	}
	public void setTemporaryEventMailbox(String temporaryEventMailbox) {
		this.temporaryEventMailbox = temporaryEventMailbox;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getLeaderName() {
		return leaderName;
	}
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
	public String getTeamMembers() {
		return teamMembers;
	}
	public void setTeamMembers(String teamMembers) {
		this.teamMembers = teamMembers;
	}
	public String[] getTeamMembersInArray() {
		return teamMembers.split(",");
	}
	
	  @Override
	   public String toString()
	   {
	      return "[eventName=" + eventName + 
	    		  ", site=" + site + 
	    		  ", temporaryEventMailbox=" + temporaryEventMailbox + 
	    		  ", eventType=" + eventType + 
	    		  ", leaderName=" + leaderName + 
	    		  ", teamMembers=" + teamMembers + "]";
	   }

}
