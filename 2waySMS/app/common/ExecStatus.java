package common;

public class ExecStatus {

	public String errMsg;
	public int numLeadsSynced;
	
	public ExecStatus (String msg, int nLeads) {
		errMsg = msg;
		numLeadsSynced = nLeads;
	}
}
