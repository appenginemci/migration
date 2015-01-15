package com.sogeti.mci.migration.helper;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.sogeti.mci.migration.dao.LoggerDAO;

public class APIException {
	
	private static LoggerDAO logger = LoggerDAO.getInstance();
	
	public static void handleException (Exception e, String message, int code) {	
		if (e instanceof GoogleJsonResponseException && ((GoogleJsonResponseException) e).getStatusCode()==code) {
				System.out.println(message);
				logger.info(message, e);
			} else {
				System.err.println();
				e.printStackTrace();
				logger.error(message, e);
			}
		} 
}


