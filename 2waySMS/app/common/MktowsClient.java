package common;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.DOMException;

import com.marketo.mktows.client.MktServiceException;
import com.marketo.mktows.client.MktowsClientException;
import com.marketo.mktows.client.MktowsUtil;
import com.marketo.mktows.client.Signature;
import com.marketo.mktows.wsdl.*;

/**
 * @author agha
 * 
 */
public class MktowsClient {

	// final static public ReqCampSourceType CAMPAIGN_SOURCE_MKTOWS =
	// ReqCampSourceType.MKTOWS;
	// final static public ReqCampSourceType CAMPAIGN_SOURCE_SALES =
	// ReqCampSourceType.SALES;

	public static final String API_VERSION = "2_2";
	public static final String W3C_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	protected String mktowsUserId = null;
	protected String encryptionKey;
	protected String endPoint = null;

	private MktowsPort proxy = null;

	/**
	 * @param mktowsUserId
	 * @param encryptionKey
	 * @param host
	 */
	public MktowsClient(String mktowsUserId, String encryptionKey, String host) {

		this(mktowsUserId, encryptionKey, host, 80);
	}

	/**
	 * @param mktowsUserId
	 * @param encryptionKey
	 * @param host
	 * @param port
	 */
	public MktowsClient(String mktowsUserId, String encryptionKey, String host,
			int port) {

		// Construct the endpoint URL
		String protocol = ("localhost".equals(host)) ? "http" : "https";
		if (port != 80) {
			host = host + ":" + port;
		}
		this.endPoint = protocol + "://" + host + "/soap/mktows/"
				+ MktowsClient.API_VERSION;

		this.mktowsUserId = mktowsUserId;
		this.encryptionKey = encryptionKey;
		MktowsUtil.objectFactory = new ObjectFactory();
		MktMktowsApiService service = null;
		try {
			service = new MktMktowsApiService(new URL(this.endPoint + "?WSDL"),
					new QName("http://www.marketo.com/mktows/",
							"MktMktowsApiService"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		if (service != null) {
			this.proxy = service.getMktowsApiSoapPort();
			Map<String, Object> ctxt = ((BindingProvider) this.proxy)
					.getRequestContext();
			ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.endPoint);
		}
	}

	/**
	 * @param dt
	 * @return
	 */
	static public String formatAsW3C(Date dt) {

		DateFormat df = new SimpleDateFormat(MktowsClient.W3C_DATE_TIME_FORMAT);
		String text = df.format(dt);
		String w3cValue = text.substring(0, 22) + ":" + text.substring(22);
		return w3cValue;
	}

	/**
	 * @param dt
	 * @return
	 */
	static public String formatAsWSDL(Date dt) {

		return MktowsClient.formatAsW3C(dt);
	}

	public MktowsPort getProxy() {
		return proxy;
	}

	protected String calcRequestTimestamp() {

		return MktowsClient.formatAsW3C(new Date());
	}

	protected String calcRequestSignature(String requestTimestamp)
			throws SignatureException {

		String encryptString = requestTimestamp + this.mktowsUserId;
		return Signature.calculateHMAC(encryptString, this.encryptionKey);
	}

	protected AuthenticationHeaderInfo createAuthenticationHeader()
			throws MktowsClientException {

		AuthenticationHeaderInfo authHeader = null;
		String requestTimestamp = this.calcRequestTimestamp();
		try {
			String requestSignature = this
					.calcRequestSignature(requestTimestamp);
			authHeader = MktowsUtil.objectFactory
					.createAuthenticationHeaderInfo();
			authHeader.setMktowsUserId(this.mktowsUserId);
			authHeader.setRequestSignature(requestSignature);
			authHeader.setRequestTimestamp(requestTimestamp);
		} catch (SignatureException e) {
			e.printStackTrace();
			throw new MktowsClientException(
					"Exception occurred while generating signature", e);
		}
		return authHeader;
	}

	/**
	 * @return
	 */
	public MktowsPort getSoapInterface() {

		return this.proxy;
	}

	/**
	 * @param paramsDescribeMObject
	 * @return
	 */
	public SuccessDescribeMObject describeMObject(
			ParamsDescribeMObject paramsDescribeMObject) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 * @throws DOMException
	 */
	public List<CampaignRecord> getCampaignsForSource()
			throws MktowsClientException, MktServiceException {

		List<CampaignRecord> listCampaignRecord = null;
		ParamsGetCampaignsForSource params = MktowsUtil.objectFactory
				.createParamsGetCampaignsForSource();
		params.setSource(ReqCampSourceType.MKTOWS);
		MktowsPort soap = this.getSoapInterface();
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			SuccessGetCampaignsForSource success = soap.getCampaignsForSource(
					params, authHdr);
			ResultGetCampaignsForSource result = success.getResult();
			ArrayOfCampaignRecord aoCampaignRecord = result
					.getCampaignRecordList();
			if (aoCampaignRecord != null) {
				listCampaignRecord = aoCampaignRecord.getCampaignRecord();
			}
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return listCampaignRecord;
	}

	/**
	 * @param keyType
	 * @param keyValue
	 * @return
	 * @throws MktServiceException
	 */
	public List<LeadRecord> getLead(LeadKeyRef keyType, String keyValue)
			throws MktowsClientException, MktServiceException {

		List<LeadRecord> listLeadRecord = null;
		LeadKey leadKey = MktowsUtil.objectFactory.createLeadKey();
		leadKey.setKeyType(keyType);
		leadKey.setKeyValue(keyValue);
		ParamsGetLead params = MktowsUtil.objectFactory.createParamsGetLead();
		params.setLeadKey(leadKey);
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetLead success = soap.getLead(params, authHdr);
			ResultGetLead result = success.getResult();
			ArrayOfLeadRecord aoLeadRecord = result.getLeadRecordList();
			if (aoLeadRecord != null) {
				listLeadRecord = aoLeadRecord.getLeadRecord();
			}
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return listLeadRecord;
	}

	/**
	 * @param batchSize
	 * @param filter
	 * @param posHolder
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 */
	public List<ActivityRecord> getLeadActivity(int batchSize,
			List<ActivityType> filter, StreamPostionHolder posHolder)
			throws MktowsClientException, MktServiceException {

		return this.getLeadActivity(null, null, batchSize, null, null, filter,
				posHolder);
	}

	/**
	 * @param keyType
	 * @param keyValue
	 * @param batchSize
	 * @param latestCreatedAt
	 * @param oldestCreatedAt
	 * @param filter
	 * @param posHolder
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 */
	public List<ActivityRecord> getLeadActivity(LeadKeyRef keyType,
			String keyValue, int batchSize, Date latestCreatedAt,
			Date oldestCreatedAt, List<ActivityType> filter,
			StreamPostionHolder posHolder) throws MktowsClientException,
			MktServiceException {

		List<ActivityRecord> listActivityRecord = null;
		ParamsGetLeadActivity params = MktowsUtil.objectFactory
				.createParamsGetLeadActivity();
		LeadKey leadKey = null;
		// If the holder has a stream position, then don't initialize a new
		// stream position.
		Object lastPos = posHolder.getStreamPosition();
		if (lastPos != null) {
			leadKey = posHolder.getLeadKey();
			params.setStartPosition((StreamPosition) lastPos);
		} else {
			leadKey = MktowsUtil.objectFactory.createLeadKey();
			leadKey.setKeyType(keyType);
			leadKey.setKeyValue(keyValue);
			StreamPosition initPos = MktowsUtil.objectFactory
					.createStreamPosition();
			if (latestCreatedAt != null) {
				initPos.setLatestCreatedAt(MktowsUtil
						.dateObjectToW3cDate(latestCreatedAt));
			}
			if (oldestCreatedAt != null) {
				initPos.setOldestCreatedAt(MktowsUtil
						.dateObjectToW3cDate(oldestCreatedAt));
			}
			params.setStartPosition(initPos);
		}
		params.setLeadKey(leadKey);
		params.setBatchSize(new Integer(batchSize));
		ArrayOfActivityType aoActType = MktowsUtil.objectFactory
				.createArrayOfActivityType();
		aoActType.getActivityType().addAll(filter);
		ActivityTypeFilter actTypeFilter = MktowsUtil.objectFactory
				.createActivityTypeFilter();
		actTypeFilter.setIncludeTypes(aoActType);
		params.setActivityFilter(actTypeFilter);
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetLeadActivity success = soap.getLeadActivity(params,
					authHdr);
			LeadActivityList result = success.getLeadActivityList();
			ArrayOfActivityRecord aoActivityRecord = result
					.getActivityRecordList();
			if (aoActivityRecord != null) {
				listActivityRecord = aoActivityRecord.getActivityRecord();
			}
			posHolder.setStreamPosition(result.getNewStartPosition());
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return listActivityRecord;
	}

	/**
	 * @param batchSize
	 * @param latestCreatedAt
	 * @param oldestCreatedAt
	 * @param filter
	 * @param posHolder
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 * @throws
	 */
	public List<LeadChangeRecord> getLeadChanges(int batchSize,
			Date latestCreatedAt, Date oldestCreatedAt,
			List<ActivityType> filter, StreamPostionHolder posHolder)
			throws MktowsClientException, MktServiceException {

		List<LeadChangeRecord> listChangeRecord = null;
		ParamsGetLeadChanges params = MktowsUtil.objectFactory
				.createParamsGetLeadChanges();
		// If the holder has a stream position, then don't initialize a new
		// stream position.
		Object lastPos = posHolder.getStreamPosition();
		if (lastPos != null) {
			params.setStartPosition((StreamPosition) lastPos);
		} else {
			StreamPosition initPos = MktowsUtil.objectFactory
					.createStreamPosition();
			if (latestCreatedAt != null) {
				initPos.setLatestCreatedAt(MktowsUtil
						.dateObjectToW3cDate(latestCreatedAt));
			}
			if (oldestCreatedAt != null) {
				initPos.setOldestCreatedAt(MktowsUtil
						.dateObjectToW3cDate(oldestCreatedAt));
			}
			params.setStartPosition(initPos);
		}
		params.setBatchSize(new Integer(batchSize));
		ArrayOfActivityType aoActType = MktowsUtil.objectFactory
				.createArrayOfActivityType();
		aoActType.getActivityType().addAll(filter);
		ActivityTypeFilter actTypeFilter = MktowsUtil.objectFactory
				.createActivityTypeFilter();
		actTypeFilter.setIncludeTypes(aoActType);
		params.setActivityFilter(actTypeFilter);
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetLeadChanges success = soap
					.getLeadChanges(params, authHdr);
			ResultGetLeadChanges result = success.getResult();
			ArrayOfLeadChangeRecord aoLeadChangeRecord = result
					.getLeadChangeRecordList();
			if (aoLeadChangeRecord != null) {
				listChangeRecord = aoLeadChangeRecord.getLeadChangeRecord();
			}
			posHolder.setStreamPosition(result.getNewStartPosition());
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return listChangeRecord;
	}

	public List<CustomObj> getCustomObjects(String objTypeName)
			throws MktowsClientException, MktServiceException {
		List<CustomObj> listCustomObjects = null;
		ParamsGetCustomObjects params = MktowsUtil.objectFactory
				.createParamsGetCustomObjects();
		params.setObjTypeName(objTypeName);
		ArrayOfAttribute attrList = MktowsUtil.objectFactory
				.createArrayOfAttribute();
		Attribute attrib = MktowsUtil.objectFactory.createAttribute();
		attrib.setAttrName("MKTOID");
		attrib.setAttrValue("1090177");
		attrList.getAttribute().add(attrib);
		
		Attribute attrib2 = MktowsUtil.objectFactory.createAttribute();
		attrib2.setAttrName("MKTOID");
		attrib2.setAttrValue("65106");
		attrList.getAttribute().add(attrib2);
		
		params.setCustomObjKeyList(attrList);
		
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetCustomObjects success = soap.getCustomObjects(params,
					authHdr);
			ResultGetCustomObjects result = success.getResult();
			ArrayOfCustomObj aoMObject = result.getCustomObjList();
			if (aoMObject != null) {
				listCustomObjects = aoMObject.getCustomObj();
			}

		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}
		return listCustomObjects;
	}

	public List<SyncCustomObjStatus> syncCustomObject(String objTypeName)
			throws MktowsClientException, MktServiceException {
		List<SyncCustomObjStatus> listCustomObjects = null;
		ParamsSyncCustomObjects params = MktowsUtil.objectFactory
				.createParamsSyncCustomObjects();
		params.setObjTypeName(objTypeName);
		params.setOperation(SyncOperationEnum.INSERT);

		ArrayOfCustomObj objArray = MktowsUtil.objectFactory
				.createArrayOfCustomObj();
		for (int i = 0; i < 5; i++) {
			CustomObj obj = MktowsUtil.objectFactory.createCustomObj();
			Attribute attrib = MktowsUtil.objectFactory.createAttribute();
			attrib.setAttrName("MKTOID");
			attrib.setAttrValue("1090177");
			ArrayOfAttribute attribKeyArr = MktowsUtil.objectFactory
					.createArrayOfAttribute();
			attribKeyArr.getAttribute().add(attrib);

			Attribute attrib2 = MktowsUtil.objectFactory.createAttribute();
			attrib2.setAttrName("rid");
			attrib2.setAttrValue("rid" + i);
			attribKeyArr.getAttribute().add(attrib2);
			obj.setCustomObjKeyList(attribKeyArr);
			
			ArrayOfAttribute attribCObjArr = MktowsUtil.objectFactory
					.createArrayOfAttribute();
			
			Attribute attrib3 = MktowsUtil.objectFactory.createAttribute();
			attrib3.setAttrName("city");
			attrib3.setAttrValue("city" + i);
			attribCObjArr.getAttribute().add(attrib3);

			Attribute attrib4 = MktowsUtil.objectFactory.createAttribute();
			attrib4.setAttrName("zip");
			attrib4.setAttrValue("zip" + i);
			attribCObjArr.getAttribute().add(attrib4);

			Attribute attrib5 = MktowsUtil.objectFactory.createAttribute();
			attrib5.setAttrName("state");
			attrib5.setAttrValue("state" + i);
			attribCObjArr.getAttribute().add(attrib5);

			obj.setCustomObjAttributeList(attribCObjArr);
			objArray.getCustomObj().add(obj);
			params.setCustomObjList(objArray);
		}
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessSyncCustomObjects success = soap.syncCustomObjects(params,
					authHdr);
			ResultSyncCustomObjects result = success.getResult();
			ArrayOfSyncCustomObjStatus aoMObject = result
					.getSyncCustomObjStatusList();
			if (aoMObject != null) {
				listCustomObjects = aoMObject.getSyncCustomObjStatus();
			}

		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}
		return listCustomObjects;
	}

	public List<MObject> getMObjects(String type, Integer id,
			Attrib externalKey, List<MObjCriteria> listMObjCriteria,
			List<MObjAssociation> listMObjAssociation,
			StreamPostionHolder posHolder) throws MktowsClientException,
			MktServiceException {

		List<MObject> listMObjects = null;
		ParamsGetMObjects params = MktowsUtil.objectFactory
				.createParamsGetMObjects();
		params.setType(type);
		if (id != null) {
			params.setId(id);
		} else if (externalKey != null) {
			params.setExternalKey(externalKey);
		} else {
			if (posHolder == null) {
				throw new MktowsClientException(
						"StreamPostionHolder parameter is required");
			}
			Object lastPos = posHolder.getStreamPosition();
			if (lastPos != null) {
				params.setStreamPosition((String) lastPos);
			} else {
				if (listMObjCriteria != null) {
					ArrayOfMObjCriteria aoMObjCriteria = MktowsUtil.objectFactory
							.createArrayOfMObjCriteria();
					aoMObjCriteria.getMObjCriteria().addAll(listMObjCriteria);
					params.setMObjCriteriaList(aoMObjCriteria);
				}
				if (listMObjAssociation != null) {
					ArrayOfMObjAssociation aoMObjAssociation = MktowsUtil.objectFactory
							.createArrayOfMObjAssociation();
					aoMObjAssociation.getMObjAssociation().addAll(
							listMObjAssociation);
					params.setMObjAssociationList(aoMObjAssociation);
				}
			}
		}
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetMObjects success = soap.getMObjects(params, authHdr);
			ResultGetMObjects result = success.getResult();
			if (posHolder != null) {
				posHolder.setStreamPosition(result.getNewStreamPosition());
			}
			ArrayOfMObject aoMObject = result.getMObjectList();
			if (aoMObject != null) {
				listMObjects = aoMObject.getMObject();
			}
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return listMObjects;
	}

	/**
	 * @param batchSize
	 * @param lastUpdatedAt
	 * @param posHolder
	 * @return
	 * @throws MktowsClientException
	 */
	public List<LeadRecord> getMultipleLeads(int batchSize,
			Date oldestUpdatedAt, StreamPostionHolder posHolder)
			throws MktowsClientException, MktServiceException {

		List<LeadRecord> listLeadRecord = null;
		ParamsGetMultipleLeads params = MktowsUtil.objectFactory
				.createParamsGetMultipleLeads();
		// If the holder has a lead key, then don't initialize a new stream
		// position.
		Object lastPos = posHolder.getStreamPosition();
		if (lastPos != null) {
			params.setStreamPosition((String) lastPos);
		} else {
			LastUpdateAtSelector lastUpdateSel = MktowsUtil.objectFactory
					.createLastUpdateAtSelector();
			lastUpdateSel.setOldestUpdatedAt(MktowsUtil
					.dateObjectToW3cDate(oldestUpdatedAt));
			params.setLeadSelector(lastUpdateSel);
		}
		params.setBatchSize(new Integer(batchSize));
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetMultipleLeads success = soap.getMultipleLeads(params,
					authHdr);
			ResultGetMultipleLeads result = success.getResult();
			ArrayOfLeadRecord aoLeadRecord = result.getLeadRecordList();
			if (aoLeadRecord != null) {
				listLeadRecord = aoLeadRecord.getLeadRecord();
			}
			posHolder.setStreamPosition(result.getNewStreamPosition());
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return listLeadRecord;
	}

	/**
	 * @param batchSize
	 * @param lastUpdatedAt
	 * @param posHolder
	 * @param leadAttrs
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 * @throws
	 */
	public List<LeadRecord> getMultipleLeads(int batchSize,
			Date oldestUpdatedAt, StreamPostionHolder posHolder,
			List<String> leadAttrs) throws MktowsClientException,
			MktServiceException {

		List<LeadRecord> listLeadRecord = null;
		ParamsGetMultipleLeads params = MktowsUtil.objectFactory
				.createParamsGetMultipleLeads();
		// If the holder has a lead key, then don't initialize a new stream
		// position.
		Object lastPos = posHolder.getStreamPosition();
		if (lastPos != null) {
			params.setStreamPosition((String) lastPos);
		} else {
			LastUpdateAtSelector lastUpdateSel = MktowsUtil.objectFactory
					.createLastUpdateAtSelector();
			lastUpdateSel.setOldestUpdatedAt(MktowsUtil
					.dateObjectToW3cDate(oldestUpdatedAt));
			params.setLeadSelector(lastUpdateSel);
		}
		params.setBatchSize(new Integer(batchSize));
		if (leadAttrs != null && leadAttrs.size() > 0) {
			ArrayOfString aoString = MktowsUtil.objectFactory
					.createArrayOfString();
			aoString.getStringItem().addAll(leadAttrs);
			params.setIncludeAttributes(aoString);
		}
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetMultipleLeads success = soap.getMultipleLeads(params,
					authHdr);
			ResultGetMultipleLeads result = success.getResult();
			ArrayOfLeadRecord aoLeadRecord = result.getLeadRecordList();
			if (aoLeadRecord != null) {
				listLeadRecord = aoLeadRecord.getLeadRecord();
			}
			posHolder.setStreamPosition(result.getNewStreamPosition());
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return listLeadRecord;
	}

	/**
	 * 
	 * @param leadIds
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 */
	public List<LeadRecord> getMultipleLeads(List<String> leadEmailAddresses)
			throws MktowsClientException, MktServiceException {

		StreamPostionHolder posHolder = new StreamPostionHolder();
		ParamsGetMultipleLeads params = MktowsUtil.objectFactory
				.createParamsGetMultipleLeads();
		List<LeadRecord> listLeadRecord = null;
		// If the holder has a lead key, then don't initialize a new stream
		// position.
		Object lastPos = posHolder.getStreamPosition();
		if (lastPos != null) {
			params.setStreamPosition((String) lastPos);
		} else {
			LeadKeySelector leadKeySel = MktowsUtil.objectFactory
					.createLeadKeySelector();

			leadKeySel.setKeyType(LeadKeyRef.EMAIL);
			ArrayOfString values = MktowsUtil.objectFactory
					.createArrayOfString();
			for (String email : leadEmailAddresses) {
				values.getStringItem().add(email);
			}
			leadKeySel.setKeyValues(values);

			params.setLeadSelector(leadKeySel);
		}
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetMultipleLeads success = soap.getMultipleLeads(params,
					authHdr);
			ResultGetMultipleLeads result = success.getResult();
			ArrayOfLeadRecord aoLeadRecord = result.getLeadRecordList();
			if (aoLeadRecord != null) {
				listLeadRecord = aoLeadRecord.getLeadRecord();
			}
			posHolder.setStreamPosition(result.getNewStreamPosition());
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return listLeadRecord;
	}

	/**
	 * @param batchSize
	 * @param lastUpdatedAt
	 * @param posHolder
	 * @return
	 * @throws MktowsClientException
	 */
	public ResultGetMultipleLeads getMultipleLeads(int batchSize,
			String staticListName, StreamPostionHolder posHolder,
			List<LeadRecord> listLeadRecord, List<String> leadAttrs)
			throws MktowsClientException, MktServiceException {

		if (listLeadRecord == null) {
			return null;
		}
		ParamsGetMultipleLeads params = MktowsUtil.objectFactory
				.createParamsGetMultipleLeads();
		// If the holder has a lead key, then don't initialize a new stream
		// position.
		Object lastPos = posHolder.getStreamPosition();
		if (lastPos != null) {
			params.setStreamPosition((String) lastPos);
		} else {
			StaticListSelector staticListSel = MktowsUtil.objectFactory
					.createStaticListSelector();
			staticListSel.setStaticListName(staticListName);
			params.setLeadSelector(staticListSel);
		}
		params.setBatchSize(new Integer(batchSize));
		if (leadAttrs != null && leadAttrs.size() > 0) {
			ArrayOfString aoString = MktowsUtil.objectFactory
					.createArrayOfString();
			aoString.getStringItem().addAll(leadAttrs);
			params.setIncludeAttributes(aoString);
		}
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetMultipleLeads success = soap.getMultipleLeads(params,
					authHdr);
			ResultGetMultipleLeads result = success.getResult();
			ArrayOfLeadRecord aoLeadRecord = result.getLeadRecordList();
			if (aoLeadRecord != null) {
				List<LeadRecord> retList = aoLeadRecord.getLeadRecord();
				if (retList != null) {
					listLeadRecord.addAll(retList);
				}
			}
			posHolder.setStreamPosition(result.getNewStreamPosition());
			return result;
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

	}

	/**
	 * @param importMode
	 * @param programName
	 * @param listName
	 * @param importFileHeader
	 * @param importFileRows
	 * @param campaignName
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 */
	public ImportToListStatusEnum importToList(ImportToListModeEnum importMode,
			String programName, String listName, String importFileHeader,
			List<String> importFileRows, String campaignName)
			throws MktowsClientException, MktServiceException {

		// bulkNewLeadImport
		ImportToListStatusEnum retVal = ImportToListStatusEnum.FAILED;

		ParamsImportToList params = MktowsUtil.objectFactory
				.createParamsImportToList();
		params.setProgramName(programName);
		params.setCampaignName(campaignName);

		params.setClearList(false);
		params.setImportListMode(importMode);
		params.setListName(listName);
		params.setImportFileHeader(importFileHeader);
		ArrayOfString rows = MktowsUtil.objectFactory.createArrayOfString();
		rows.getStringItem().addAll(importFileRows);
		params.setImportFileRows(rows);

		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessImportToList success = soap.importToList(params, authHdr);
			ResultImportToList result = success.getResult();
			retVal = result.getImportStatus();
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return retVal;
	}

	/**
	 * @param programName
	 * @param listName
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 */
	public ResultGetImportToListStatus getImportToListStatus(
			String programName, String listName) throws MktowsClientException,
			MktServiceException {

		ResultGetImportToListStatus result = null;

		ParamsGetImportToListStatus params = MktowsUtil.objectFactory
				.createParamsGetImportToListStatus();
		params.setProgramName(programName);
		params.setListName(listName);

		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessGetImportToListStatus success = soap.getImportToListStatus(
					params, authHdr);
			result = success.getResult();
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return result;
	}

	/**
	 * @return
	 */
	public List<String> listMObjects() throws MktowsClientException,
			MktServiceException {

		List<String> objectNames = null;
		ParamsListMObjects params = MktowsUtil.objectFactory
				.createParamsListMObjects();
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessListMObjects success = soap.listMObjects(params, authHdr);
			ResultListMObjects result = success.getResult();
			objectNames = result.getObjects();
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return objectNames;
	}

	/**
	 * @param listop
	 * @param listName
	 * @param leadList
	 * @param opStatus
	 * @return
	 */
	public boolean listOperation(ListOperationType listop, String listName,
			List<LeadKey> leadList, HashMap<String, Boolean> opStatus)
			throws MktowsClientException, MktServiceException {

		boolean retVal = false;
		if (opStatus != null) {
			opStatus.clear();
		}
		opStatus.clear();
		ArrayOfLeadKey aoLeadKey = MktowsUtil.objectFactory
				.createArrayOfLeadKey();
		aoLeadKey.getLeadKey().addAll(leadList);
		ListKey listKey = MktowsUtil.objectFactory.createListKey();
		listKey.setKeyType(ListKeyType.MKTOLISTNAME);
		listKey.setKeyValue(listName);
		ParamsListOperation params = MktowsUtil.objectFactory
				.createParamsListOperation();
		params.setListKey(listKey);
		params.setListMemberList(aoLeadKey);
		params.setListOperation(listop);
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessListOperation success = soap.listOperation(params, authHdr);
			ResultListOperation result = success.getResult();
			retVal = result.isSuccess();
			if (opStatus != null) {
				List<LeadStatus> statusList = result.getStatusList()
						.getLeadStatus();
				for (LeadStatus leadStatus : statusList) {
					opStatus.put(leadStatus.getLeadKey().getKeyValue(),
							new Boolean(leadStatus.isStatus()));
				}
			}
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return retVal;
	}

	/**
	 * Wrapper for mergeLeads API.
	 * 
	 * @param winningLead
	 * @param losingLeadList
	 * @return
	 */
	public MergeStatus mergeLeads(List<Attribute> winningLead,
			List<List<Attribute>> losingLeadList) throws MktowsClientException,
			MktServiceException {

		MergeStatus status = null;
		ParamsMergeLeads params = MktowsUtil.objectFactory
				.createParamsMergeLeads();

		// The winning lead
		params.setWinningLeadKeyList(MktowsUtil.objectFactory
				.createArrayOfAttribute());
		params.getWinningLeadKeyList().getAttribute().addAll(winningLead);

		// The losing leads
		params.setLosingLeadKeyLists(MktowsUtil.objectFactory
				.createArrayOfKeyList());
		ArrayOfAttribute loser = null;
		for (List<Attribute> losingLead : losingLeadList) {
			loser = MktowsUtil.objectFactory.createArrayOfAttribute();
			loser.getAttribute().addAll(losingLead);
			params.getLosingLeadKeyLists().getKeyList().add(loser);
		}
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessMergeLeads success = soap.mergeLeads(params, authHdr);
			ResultMergeLeads result = success.getResult();
			status = result.getMergeStatus();
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return status;
	}

	/**
	 * @param campId
	 * @param leadList
	 * @return
	 */
	public boolean requestCampaign(int campId, List<LeadKey> leadList)
			throws MktowsClientException, MktServiceException {

		boolean retVal = false;
		ArrayOfLeadKey aoLeadKey = MktowsUtil.objectFactory
				.createArrayOfLeadKey();
		aoLeadKey.getLeadKey().addAll(leadList);
		ParamsRequestCampaign params = MktowsUtil.objectFactory
				.createParamsRequestCampaign();
		params.setCampaignId(campId);
		params.setLeadList(aoLeadKey);
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessRequestCampaign success = soap.requestCampaign(params,
					authHdr);
			ResultRequestCampaign result = success.getResult();
			retVal = result.isSuccess();
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return retVal;
	}

	/**
	 * @param programName
	 * @param campaignName
	 * @param leadKeyList
	 * @param tokenList
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 */
	public boolean requestCampaign(String programName, String campaignName,
			List<LeadKey> leadKeyList, List<Attrib> tokenList)
			throws MktowsClientException, MktServiceException {

		boolean retVal = false;
		ParamsRequestCampaign params = MktowsUtil.objectFactory
				.createParamsRequestCampaign();
		params.setSource(ReqCampSourceType.MKTOWS);
		params.setProgramName(programName);
		params.setCampaignName(campaignName);
		ArrayOfLeadKey aoLeadKeys = MktowsUtil.objectFactory
				.createArrayOfLeadKey();
		aoLeadKeys.getLeadKey().addAll(leadKeyList);
		params.setLeadList(aoLeadKeys);
		if (tokenList != null) {
			ArrayOfAttrib aoTokens = MktowsUtil.objectFactory
					.createArrayOfAttrib();
			aoTokens.getAttrib().addAll(tokenList);
			params.setProgramTokenList(aoTokens);
		}

		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessRequestCampaign success = soap.requestCampaign(params,
					authHdr);
			ResultRequestCampaign result = success.getResult();
			retVal = result.isSuccess();
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new MktowsClientException("Exception here: ", ex);
		}

		return retVal;
	}

	/**
	 * @param programName
	 * @param campaignName
	 * @param runAt
	 * @param tokenList
	 * @return
	 * @throws MktowsClientException
	 * @throws MktServiceException
	 */
	public boolean scheduleCampaign(String programName, String campaignName,
			XMLGregorianCalendar runAt, List<Attrib> tokenList)
			throws MktowsClientException, MktServiceException {

		boolean retVal = false;
		ParamsScheduleCampaign params = MktowsUtil.objectFactory
				.createParamsScheduleCampaign();
		params.setProgramName(programName);
		params.setCampaignName(campaignName);
		params.setCampaignRunAt(runAt);
		if (tokenList != null) {
			ArrayOfAttrib aoTokens = MktowsUtil.objectFactory
					.createArrayOfAttrib();
			aoTokens.getAttrib().addAll(tokenList);
			params.setProgramTokenList(aoTokens);
		}

		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessScheduleCampaign success = soap.scheduleCampaign(params,
					authHdr);
			ResultScheduleCampaign result = success.getResult();
			retVal = result.isSuccess();
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return retVal;
	}

	/**
	 * @param leadRec
	 * @param marketoCookie
	 * @param returnLead
	 * @return
	 * @throws InterruptedException
	 * @throws MktServiceException
	 * @throws MktowsClientException
	 */
	public ResultSyncLead syncLead(LeadRecord leadRec, String marketoCookie,
			boolean returnLead) throws MktowsClientException,
			MktServiceException {

		ResultSyncLead result = null;
		ParamsSyncLead params = MktowsUtil.objectFactory.createParamsSyncLead();
		params.setLeadRecord(leadRec);
		if (marketoCookie != null) {
			params.setMarketoCookie(marketoCookie);
		}
		params.setReturnLead(new Boolean(returnLead));
		int iRetry = 0;
		while (iRetry < 3) {
			++iRetry;
			try {
				AuthenticationHeaderInfo authHdr = this
						.createAuthenticationHeader();
				MktowsPort soap = this.getSoapInterface();
				SuccessSyncLead success = soap.syncLead(params, authHdr, null);
				result = success.getResult();
				break;
			} catch (SOAPFaultException ex) {
				SOAPFault fault = ex.getFault();
				throw new MktServiceException(ex.getMessage() + ", caused by "
						+ fault.getDetail().getTextContent(), ex);
			} catch (WebServiceException ex) {
				throw new MktowsClientException(
						"Web service exception occurred: " + ex.getMessage(),
						ex);
			}
		}
		return result;
	}

	/**
	 * @param leadRecList
	 * @param dedupEnabled
	 * @return
	 * @throws MktServiceException
	 * @throws
	 */
	public List<SyncStatus> syncMultipleLeads(List<LeadRecord> leadRecList,
			boolean dedupEnabled) throws MktowsClientException,
			MktServiceException {

		List<SyncStatus> retVal = null;
		ArrayOfLeadRecord aoLeadRec = MktowsUtil.objectFactory
				.createArrayOfLeadRecord();
		aoLeadRec.getLeadRecord().addAll(leadRecList);
		ParamsSyncMultipleLeads params = MktowsUtil.objectFactory
				.createParamsSyncMultipleLeads();
		params.setLeadRecordList(aoLeadRec);
		params.setDedupEnabled(new Boolean(dedupEnabled));
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessSyncMultipleLeads success = soap.syncMultipleLeads(params,
					authHdr);
			retVal = success.getResult().getSyncStatusList().getSyncStatus();
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return retVal;
	}

	public List<SyncCustomObjStatus> syncCustomObjects(String objTypeName,
			List<CustomObj> customObjList, SyncOperationEnum operation)
			throws MktowsClientException, MktServiceException {

		List<SyncCustomObjStatus> retVal = null;
		ArrayOfCustomObj aoCustObj = MktowsUtil.objectFactory
				.createArrayOfCustomObj();
		aoCustObj.getCustomObj().addAll(customObjList);
		ParamsSyncCustomObjects params = MktowsUtil.objectFactory
				.createParamsSyncCustomObjects();
		params.setCustomObjList(aoCustObj);
		params.setObjTypeName(objTypeName);
		params.setOperation(operation);
		try {
			AuthenticationHeaderInfo authHdr = this
					.createAuthenticationHeader();
			MktowsPort soap = this.getSoapInterface();
			SuccessSyncCustomObjects success = soap.syncCustomObjects(params,
					authHdr);
			retVal = success.getResult().getSyncCustomObjStatusList()
					.getSyncCustomObjStatus();
		} catch (SOAPFaultException ex) {
			SOAPFault fault = ex.getFault();
			throw new MktServiceException(ex.getMessage() + ", caused by "
					+ fault.getDetail().getTextContent(), ex);
		} catch (WebServiceException ex) {
			throw new MktowsClientException("Web service exception occurred: "
					+ ex.getMessage(), ex);
		}

		return retVal;
	}
}
