package com.sogeti.mci.migration.api;

import java.io.IOException;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.User;
import com.sogeti.mci.migration.helper.APIException;

public class DirectoryAPI {
	
	public static Group createGroup(Directory directory, String groupName, String groupEmail) {
		Group addedGroup = null;
		try {
			addedGroup = new Group();
			addedGroup.setName(groupName);
			addedGroup.setEmail(groupEmail);
			addedGroup = directory.groups().insert(addedGroup).execute();
		} catch (IOException ge) {
			APIException.handleException(ge, "Failed to create group "+groupName, 409);
			addedGroup = null;
		} 
		return addedGroup;
	}
	

	public static Groups listGroup(Directory directory, String domain) {
		Groups groups = null;
		try {
			 groups = directory.groups().list().setDomain(domain).execute();
		} catch (IOException e) {
			APIException.handleException(e, "Failed to list group for this domain "+domain, 0);
		}
		return groups;
	}
	
	public static User getUser(Directory directory, String userId) {
		User user = null;
		try {
			 user = directory.users().get(userId).execute();
		} catch (IOException ge) {
			APIException.handleException(ge, "Failed to get "+userId, 404);
		}
		return user;
	}
	
	public static Member insertMemberToGroup(Directory directory, String groupId,  Member memberToAdd)  {
		Member member = null;
		try {
			member = directory.members().insert(groupId, memberToAdd).execute();
		} catch (IOException e) {
			APIException.handleException(e,"Error while inserting user",0);
		}
		return member;
	}
	
	public static Member getMemberFromGroup(Directory directory, String groupId,  String userId)  {
		Member member = null;
		try {
			member = directory.members().get(groupId, userId).execute();
		} catch (IOException ge) {
			APIException.handleException(ge, "Failed to add "+userId+" to "+groupId, 404);
		}
		return member;
	}

}
