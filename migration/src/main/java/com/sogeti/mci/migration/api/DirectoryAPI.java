package com.sogeti.mci.migration.api;

import java.io.IOException;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.User;

public class DirectoryAPI {
	
	public static Group createGroup(Directory directory, String groupName, String groupEmail) {
		Group addedGroup = null;
		try {
			addedGroup = new Group();
			addedGroup.setName(groupName);
			addedGroup.setEmail(groupEmail);
			addedGroup = directory.groups().insert(addedGroup).execute();
		} catch (GoogleJsonResponseException ge) {
			if (ge.getStatusCode()==409) {
				System.err.println("Entity already exists:"+groupEmail);
			} else {
				System.err.println("error while trying to add group");
				ge.printStackTrace();
				addedGroup = null;
			}
			addedGroup = null;
		} catch (IOException e) {
			System.err.println("error while trying to add group");
			e.printStackTrace();
			addedGroup = null;
		}
		return addedGroup;
	}
	

	public static Groups listGroup(Directory directory, String domain) {
		Groups groups = null;
		try {
			 groups = directory.groups().list().setDomain(domain).execute();
		} catch (IOException e) {
			System.err.println("Error while retrieving groups");
			e.printStackTrace();
		}
		return groups;
	}
	
	public static User getUser(Directory directory, String userId) {
		User user = null;
		try {
			 user = directory.users().get(userId).execute();
		} catch (GoogleJsonResponseException ge) {
			if (ge.getStatusCode()==404) {
				System.err.println("User not found: "+userId);
			} else {
				System.err.println("Error while retrieving user");
				ge.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println("Error while retrieving user");
			e.printStackTrace();
		}
		return user;
	}
	
	public static Member insertMemberToGroup(Directory directory, String groupId,  Member memberToAdd)  {
		Member member = null;
		try {
			member = directory.members().insert(groupId, memberToAdd).execute();
		} catch (IOException e) {
			System.err.println("Error while inserting user");
			e.printStackTrace();
		}
		return member;
	}
	
	public static Member getMemberFromGroup(Directory directory, String groupId,  String userId)  {
		Member member = null;
		try {
			member = directory.members().get(groupId, userId).execute();
		} catch (GoogleJsonResponseException ge) {
			if (ge.getStatusCode()==404) {
				System.err.println("User not member for this group : "+userId);
			} else {
				System.err.println("Error while retrieving user");
				ge.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println("Error while inserting user");
			e.printStackTrace();
		}
		return member;
	}

}
