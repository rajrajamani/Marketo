package com.marketo.rest.leadapi.client;

import java.util.Collection;
import java.util.Map;

public class MarketoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String requestId;
	Collection<Map<String, String>> errors;

	public MarketoException (String requestId, Collection<Map<String, String>> errors) {
		this.errors = errors;
		this.requestId = requestId;
	}
	
	public MarketoException (String requestId) {
		this.requestId = requestId;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public Collection<Map<String, String>> getErrors() {
		return errors;
	}
	
	public String getErrorsAsString() {
		String retVal = "";
		for (Map<String, String> mp : errors) {
			retVal += "code : " + mp.get("code");
			retVal += " ";
			retVal += "message : " + mp.get("message");
		}
		return retVal;
	}
}
