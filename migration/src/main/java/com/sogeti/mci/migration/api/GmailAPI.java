package com.sogeti.mci.migration.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.ModifyThreadRequest;
import com.google.api.services.gmail.model.Thread;

public class GmailAPI {
	
	public static List<Label> getListLabel(Gmail service, String userId) {
		List<Label> list = new ArrayList<Label>();
		try {
			list= service.users().labels().list(userId).execute().getLabels();
		} catch (IOException e) {
			System.err.println("Login Required: "+userId);
			e.printStackTrace();
		}
		return list;
	} 
	
	public static List<com.google.api.services.gmail.model.Thread> listThreadsMatchingQuery(Gmail service, 
			String userId, String query) throws IOException {
		ListThreadsResponse response = service.users().threads().list(userId)
				.setQ(query).execute();
		List<com.google.api.services.gmail.model.Thread> threads = new ArrayList<com.google.api.services.gmail.model.Thread>();
		while (response.getThreads() != null) {
			threads.addAll(response.getThreads());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().threads().list(userId).setQ(query)
						.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}

		return threads;
	}
	

	public static Message getLastMessageFromThread(Gmail service, String userId, Thread thread) throws IOException {
		Thread t = service.users().threads().get(userId, thread.getId()).execute();
		return t.getMessages().get(t.getMessages().size() - 1);
	}
	
	public static Message getMessageById(Gmail service, String userId, String id) throws IOException {
		return service.users().messages().get(userId, id).execute();
	}

	public static boolean labelizeThread(Gmail service, String userId, String threadId, String labelName) throws IOException {
		boolean isThreadLabelized = false;
		List<String> labels = new ArrayList<String>();
		Label label = getLabel(service, userId, labelName);
		if (label==null) {
			System.err.println("Failed to create label "+labelName);
		} else {
			labels.add(label.getId());
		    ModifyThreadRequest mods1 = new ModifyThreadRequest().setAddLabelIds(labels);
		    service.users().threads().modify(userId, threadId, mods1).execute();
		    isThreadLabelized = true;
		}
		return isThreadLabelized;
	}
	
	private static Label getLabel(Gmail service, String userId, String newLabelName) throws IOException {
	  	Label returnedLabel = null;
	    ListLabelsResponse response = service.users().labels().list(userId).execute();
	    List<Label> labels = response.getLabels();
	    boolean notFound = true;
	    for (Label label : labels) {
	    	if (label.getName().equals(newLabelName)) {
	    		notFound = false;
	    		returnedLabel = label;
	    		break;
	    	}
	    }
	    if (notFound) {
	    	returnedLabel = new Label().setName(newLabelName).setMessageListVisibility("show").setLabelListVisibility("labelShow");
	    	returnedLabel = service.users().labels().create(userId, returnedLabel).execute();
	    }
	    return returnedLabel;
	  }
	
	public static Message getRawMessageById(Gmail service, String userId, String id) throws IOException {
		return service.users().messages().get(userId, id).setFormat("raw").execute();
	}
	
	public static MessagePartBody getMessagePartBody(Gmail service, String userId, String id, String attId) throws IOException {
		return service.users().messages().attachments().get(userId, id, attId).execute();
	}
}
