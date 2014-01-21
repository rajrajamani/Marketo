
public class MarketoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String requestId;

	public MarketoException (String requestId, String error) {
		super(error);
		this.requestId = requestId;
	}
	
	public MarketoException (String requestId) {
		this.requestId = requestId;
	}
	
	public String getRequestId() {
		return requestId;
	}
}
