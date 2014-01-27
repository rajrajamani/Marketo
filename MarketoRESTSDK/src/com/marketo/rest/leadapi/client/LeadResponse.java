package com.marketo.rest.leadapi.client;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class LeadResponse {
	public String requestId;
	public boolean success;
	public String nextPageToken;
	public Collection<Map<String, String>> result;
	public Collection<Map<String, String>> errors;
	private ArrayList<Lead> leads;
	
	public ArrayList<Lead> getLeads() {
		Map<String, String> ldAsMap = null;
		ArrayList<Lead> leadArr = new ArrayList<Lead>();
		Iterator<Map<String, String>> it = this.result.iterator();
		while (it.hasNext()) {
			ldAsMap = it.next();
			Lead ld = new Lead(ldAsMap);
			leadArr.add(ld);
		}
		this.leads = leadArr;

		return this.leads;
	}
}
