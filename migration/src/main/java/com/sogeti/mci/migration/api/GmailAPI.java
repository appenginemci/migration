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
import com.sogeti.mci.migration.helper.APIException;

public class GmailAPI {
	
	public static List<Label> getListLabel(Gmail service, String userId) {
		List<Label> list = new ArrayList<Label>();
		try {
			list= service.users().labels().list(userId).execute().getLabels();
		} catch (IOException e) {
			APIException.handleException(e, "Failed to get list of labels for "+userId, 0);
		}
		return list;
	} 
	
	public static List<com.google.api.services.gmail.model.Thread> listThreadsMatchingQuery(Gmail service, 
			String userId, String query)  {
		ListThreadsResponse response;
		List<com.google.api.services.gmail.model.Thread> threads = null;
		try {
			response = service.users().threads().list(userId)
					.setQ(query).execute();
			threads = new ArrayList<com.google.api.services.gmail.model.Thread>();
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
		} catch (IOException e) {
			APIException.handleException(e, "Failed to get list thread for "+userId+" with this query"+query, 0);
		}

		return threads;
	}
	

	public static Message getLastMessageFromThread(Gmail service, String userId, Thread thread) {
		Thread t;
		try {
			t = service.users().threads().get(userId, thread.getId()).execute();
			return t.getMessages().get(t.getMessages().size() - 1);
		} catch (IOException e) {
			APIException.handleException(e, "Failed to getLastMessageFromThread for "+userId+" with this thread "+thread.getId(), 0);
		}
		return null;
	}
	
	public static Message getMessageById(Gmail service, String userId, String id) {
		try {
			return service.users().messages().get(userId, id).execute();
		} catch (IOException e) {
			APIException.handleException(e, "Failed to getMessageById for "+userId+" with this message "+id, 0);
		}
		return null;
	}

	public static boolean labelizeThread(Gmail service, String userId, String threadId, String labelName) {
		boolean isThreadLabelized = false;
		List<String> labels = new ArrayList<String>();
		Label label = getLabel(service, userId, labelName);
		if (label==null) {
			System.err.println("Failed to create label "+labelName);
		} else {
			labels.add(label.getId());
		    ModifyThreadRequest mods1 = new ModifyThreadRequest().setAddLabelIds(labels);
		    try {
				service.users().threads().modify(userId, threadId, mods1).execute();
				isThreadLabelized = true;
			} catch (IOException e) {
				APIException.handleException(e, "Failed to labelizeThread for "+userId+" with this label "+labelName, 0);
			}
		}
		return isThreadLabelized;
	}
	
	private static Label getLabel(Gmail service, String userId, String newLabelName) {
	  	Label returnedLabel = null;
	    ListLabelsResponse response;
		try {
			response = service.users().labels().list(userId).execute();
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
		} catch (IOException e) {
			APIException.handleException(e, "Failed to get Label for "+userId+" with this label "+newLabelName, 0);
		}
	    return returnedLabel;
	  }
	
	public static Message getRawMessageById(Gmail service, String userId, String id)  {
		try {
			return service.users().messages().get(userId, id).setFormat("raw").execute();
		} catch (IOException e) {
			APIException.handleException(e, "Failed to getRawMessageById for "+userId+" with this message "+id, 0);
		}
		return null;
	}
	
	public static MessagePartBody getMessagePartBody(Gmail service, String userId, String id, String attId) {
		try {
			return service.users().messages().attachments().get(userId, id, attId).execute();
		} catch (IOException e) {
			APIException.handleException(e, "Failed to getMessagePartBody for "+userId+" with this message "+id, 0);
		}
		return null;
	}
}
