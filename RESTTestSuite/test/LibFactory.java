import com.marketo.rest.network.IdentityClient;
import com.marketo.rest.network.LeadAPI;


public class LibFactory {

	private com.marketo.rest.network.IdentityClient networkIdClient;
	private com.marketo.rest.android.IdentityClient androidIdClient;
	
	private com.marketo.rest.network.LeadAPI networkLeadAPI;
	private com.marketo.rest.android.LeadAPI androidLeadAPI;
	
	private static LibFactory instance = null;
	
	protected static final int TYPE_NETWORK = 0;
	protected static final int TYPE_ANDROID = 1;
	
	protected LibFactory() {
		
		networkIdClient = new com.marketo.rest.network.IdentityClient();
		androidIdClient = new com.marketo.rest.android.IdentityClient();
		
		
		networkLeadAPI = new com.marketo.rest.network.LeadAPI();
		androidLeadAPI = new com.marketo.rest.android.LeadAPI();
	}
	
	public static LibFactory getInstance() {
		if (instance == null) {
			instance = new LibFactory();
		}
		return instance;
	}
	
	public IdentityClient getNetworkIdentityClient() {
		return networkIdClient;
	}

	public com.marketo.rest.android.IdentityClient getAndroidIdentityClient() {
		return androidIdClient;
	}

	public LeadAPI getNetworkLeadAPI() {
		return networkLeadAPI;
	}

	public com.marketo.rest.android.LeadAPI getAndroidLeadAPI() {
		return androidLeadAPI;
	}
}
