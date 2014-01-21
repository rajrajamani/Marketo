package com.marketo.rest.leadapi.client;
import java.util.HashMap;
import java.util.Map;


public class Lead {

	private Map<String, String> attrMap;

	public Lead(Map<String, String> map) {
		attrMap = map;
	}
	
	public String getLeadAttrib(String key) {
		if (attrMap.containsKey(key)) {
			return attrMap.get(key);
		} else {
			return null;
		}
	}
	
	public void printLeadAttributes() {
		for (Map.Entry<String, String> entry : attrMap.entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue());
		}
	}
}
