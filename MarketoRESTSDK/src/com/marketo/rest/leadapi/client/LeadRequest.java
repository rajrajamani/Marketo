package com.marketo.rest.leadapi.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LeadRequest {
	private String action;
	private String lookupField;
	private Collection<Map<String, String>> input;

	public static final int CREATE_ONLY = 1;
	public static final int UPDATE_ONLY = 2;
	public static final int CREATE_OR_UPDATE = 3;
	public static final int CREATE_DUPLICATE = 4;

	public LeadRequest(int type, String lfld, ArrayList<Lead> leads) {
		lookupField = lfld;
		switch (type) {
		case CREATE_ONLY:
			action = "createOnly";
			break;
		case UPDATE_ONLY:
			action = "updateOnly";
			break;
		case CREATE_DUPLICATE:
			action = "createDuplicate";
			break;
		default:
		case CREATE_OR_UPDATE:
			action = "createOrUpdate";
			break;
		}

		input = new ArrayList<Map<String, String>>();
		for (Lead ld : leads) {
			Map<String, String> hm = ld.getAllAttributes();
			input.add(hm);
		}
	}
	
	public String getJsonString() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);

	}
}
