package com.sogeti.mci.migration.business;

import java.util.List;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.sogeti.mci.migration.helper.Validater;
import com.sogeti.mci.migration.model.Event;
import com.sogeti.mci.migration.model.EventTypeEnum;
import com.sogeti.mci.migration.model.Input;
import com.sogeti.mci.migration.model.Member;
import com.sogeti.mci.migration.model.Site;
import com.sogeti.mci.migration.model.UserRoleEnum;
import com.sogeti.mci.migration.security.CredentialLoader;
import com.sogeti.mci.migration.service.CsvService;
import com.sogeti.mci.migration.service.DirectoryService;
import com.sogeti.mci.migration.service.DriveService;
import com.sogeti.mci.migration.service.EventService;
import com.sogeti.mci.migration.service.MemberService;
import com.sogeti.mci.migration.service.SiteService;

public class EventCreator {

	public static void main(String args[]) {
		
      String csvFilename = "events.csv";
      csvFilename = "."+java.io.File.separator+"src"+java.io.File.separator+"main"+java.io.File.separator+"resources"+java.io.File.separator+csvFilename;
	  EventMigrator.setDrive(CredentialLoader.getDriveService(EventMigrator.APPLICATION_ACCOUNT));
	  EventMigrator.setDirectory(CredentialLoader.getDirectoryService(EventMigrator.APPLICATION_ACCOUNT));    
	  List<Input> inputs = CsvService.getEvents(csvFilename);
		
		for (Input input : inputs) {
			if (Validater.validateInput(input)) {
				System.out.println("Valid input : "+input.toString());
				if (EventTypeEnum.UNSTRUCTURED.getConsumerType().equals(input.getEventType())) {
					setUpUnstructuredEvent(input);
				}
			} else {
				System.out.println("Invalid input : "+input.toString());
			}
		}
	}
	
	private static void setUpUnstructuredEvent(Input input) {
		
		//******************************************************************************************//
		//									INIT													//
		// Caution : The folders'id (event and event member) must be filled after the process		//
		// The authorization has been partially handled, access granted to root event for users		//
		// Create the alias too
		//******************************************************************************************//
		System.out.println("\n---------- STARTING CREATION -------------------");
		// Create the site folder in Drive and get the file
		File sitefolder = DriveService.createFolder(input.getSite(), null);
		if (sitefolder==null) {
			System.out.println("Failed to create site Folder : "+input.getSite());
		} else {
			System.out.println("Successfully created site Folder : "+input.getSite());
			// Create the event folder in Drive and get the file
			File eventFolder = DriveService.createFolder(input.getEventName(), sitefolder.getId());
			if (eventFolder==null) {
				System.out.println("Failed to create event Folder : "+input.getEventName());
			} else {
				System.out.println("Successfully created event Folder : "+input.getEventName());
				// Create the site in DB and get the obj
				Site site = SiteService.insertSite(input.getSite(), sitefolder.getId());
				if (site==null) {
					System.out.println("Failed to create site in DB : "+input.getSite());
				} else {
					System.out.println("Successfully created site in DB : "+input.getSite());
					// Create the event in DB and get the obj
					Event event = EventService.insertEvent(input.getEventName(), input.getEventEmailAddress(), input.getEventType(), site, eventFolder);
					if (event==null) {
						System.out.println("Failed to create event in DB : "+input.getEventName());
					} else {
						System.out.println("Successfully created event in DB : "+input.getEventName());
						// add the member to google group
						com.google.api.services.admin.directory.model.Member googleMember = DirectoryService.addUsersToGroup(event.getGroupId(), input.getLeaderName());
						if (googleMember!=null) {
							System.out.println("Successfully created leader : "+input.getLeaderName());
							// Create the leader in DB and get the obj
							Member leader = MemberService.insertMember(input.getLeaderName());
							// Attach the leader to the event
							event = EventService.attachMemberToEvent(leader, UserRoleEnum.EVENTHEAD, event);
							// Share the event folder with the leader
							Permission permission = DriveService.shareFolderWithMember(eventFolder.getId(), leader);
							if (leader==null || event==null || permission==null) {
								System.out.println("Failed to attach leader : leader :"+leader+" event : "+event+" permission : "+permission);
							} else {
								System.out.println("Successfully attached leader : "+leader.getUserId());
							}
						} else {
							System.out.println("Failed to create leader : "+input.getLeaderName());
						}
						// Create the members in DB 
						for (int i = 0; i < input.getTeamMembersInArray().length; i++) {
							// add the member to google group
							googleMember = DirectoryService.addUsersToGroup(event.getGroupId(), input.getTeamMembersInArray()[i]);
							// if the member cannot be linked to a google group, it is not considered
							if (googleMember!=null) {
								System.out.println("Successfully created member : "+input.getTeamMembersInArray()[i]);
								Member member = MemberService.insertMember(input.getTeamMembersInArray()[i]);
								// Attach the members to the event
								event = EventService.attachMemberToEvent(member, UserRoleEnum.TEAMMEMBER, event);
								// Share the event folder with the leader
								Permission permission = DriveService.shareFolderWithMember(eventFolder.getId(), member);
								if (member==null || event==null || permission==null) {
									System.out.println("Failed to attach member : member :"+member+" event : "+event+" permission : "+permission);
								} else {
									System.out.println("Successfully attached member : "+member.getUserId());
								}								
							} else {
								System.out.println("Failed to create member : "+input.getLeaderName());
							}			
						}
						// Create the alias
						//DirectoryService.addAlias(EventMigrator.APPLICATION_ACCOUNT, input.getEventEmailAddress());
					}
				}
			}
		}
		System.out.println("---------- ENDING CREATION -------------------\n");

		 
	}
	
}
