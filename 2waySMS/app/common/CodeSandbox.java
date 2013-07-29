package common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import play.Logger;

import com.marketo.mktows.client.MktServiceException;
import com.marketo.mktows.client.MktowsClientException;
import com.marketo.mktows.client.MktowsUtil;
import com.marketo.mktows.wsdl.ArrayOfAttribute;
import com.marketo.mktows.wsdl.LeadRecord;
import com.marketo.mktows.wsdl.SyncStatus;

public class CodeSandbox {

	private MktowsClient client;
	private String munchkinAccountId;
	private Long campaignId;

	public CodeSandbox(String accessKey, String secretKey,
			String munchkinAccountId, Long campaignId) {
		this.munchkinAccountId = munchkinAccountId;
		this.campaignId = campaignId;
		String hostName = munchkinAccountId + ".mktoapi.com";
		client = new MktowsClient(accessKey, secretKey, hostName);
	}

	public List<LeadRecord> mktoCapitalizeName(List<LeadRecord> inflightList,
			boolean syncImmediate) {
		List<LeadRecord> retList = new ArrayList<LeadRecord>();
		LeadRecord retLead = null;
		for (LeadRecord lr : inflightList) {
			retLead = null;
			retLead = mktoCapitalizeName(lr, syncImmediate);
			if (retLead != null) {
				retList.add(retLead);
			}
		}
		return retList;
	}

	public LeadRecord mktoCapitalizeName(LeadRecord leadRecord,
			boolean syncImmediate) {
		if (leadRecord == null) {
			return null;
		}
		Map<String, Object> attrMap = null;
		ArrayOfAttribute aoAttribute = leadRecord.getLeadAttributeList();
		if (aoAttribute != null) {
			attrMap = MktowsUtil.getLeadAttributeMap(aoAttribute);
			if (attrMap != null && !attrMap.isEmpty()) {
				Set<String> keySet = attrMap.keySet();
				HashMap<String, String> newAttrs = new HashMap<String, String>();
				if (keySet.contains("FirstName")) {
					String fn = attrMap.get("FirstName").toString();
					fn = Character.toUpperCase(fn.charAt(0)) + fn.substring(1);
					newAttrs.put("FirstName", fn);
					Logger.debug("Capitalizing First Name to : %s", fn);
				}
				if (keySet.contains("LastName")) {
					String ln = attrMap.get("LastName").toString();
					ln = Character.toUpperCase(ln.charAt(0)) + ln.substring(1);
					newAttrs.put("LastName", ln);
					Logger.debug("Capitalizing Last Name to : %s", ln);
				}

				LeadRecord newLeadRecord = MktowsUtil.newLeadRecord(
						leadRecord.getId(), null, null, null, newAttrs);
				try {
					if (syncImmediate) {
						client.syncLead(newLeadRecord, null, false);
					}
				} catch (MktowsClientException e) {
					Logger.error(
							"Unable to sync lead with capitalized name:%s",
							e.getMessage());
				} catch (MktServiceException e) {
					Logger.error(
							"Unable to sync lead with capitalized name:%s",
							e.getMessage());
				}
				return newLeadRecord;
			}
		}
		return null;
	}

	public CtClass createClass(String className) {
		ClassPool pool = ClassPool.getDefault();
		CtClass mktoClass = null;
		try {
			pool.importPackage("com.marketo.mktows.wsdl");
			pool.importPackage("com.marketo.mktows.client");
			pool.importPackage("java.util");
			mktoClass = pool.get(className);
		} catch (NotFoundException e) {
			Logger.debug("Did not find class :%s.  Will create new one",
					className);
			mktoClass = pool.makeClass(className);

		}
		return mktoClass;
	}

	public boolean methodExists(CtClass mktoClass, String methodName) {
		CtMethod[] methods = mktoClass.getMethods();
		for (CtMethod method : methods) {
			if (method.getName().equals(methodName)) {
				Logger.debug("Found method %s in class", methodName);
				return true;
			}
		}
		return false;
	}

	public String addMethod(CtClass mktoClass, String methodDefinition) {
		try {
			// only call if method does NOT exist
			String methodName = getMethodName();
			String fullMethodDefn = "public LeadRecord " + methodName
					+ "(LeadRecord leadRecord) {" + methodDefinition + "}";
			Logger.debug("Adding new method to class:%s", fullMethodDefn);
			mktoClass.addMethod(CtNewMethod.make(fullMethodDefn, mktoClass));
			return methodName;
		} catch (CannotCompileException e) {
			Logger.error("Unable to compile : %s", e.getMessage());
			return null;
		}
	}

	public List<LeadRecord> executeMethod(Class clazz, String methodName,
			List<LeadRecord> inflightList) {
		List<LeadRecord> retList = new ArrayList<LeadRecord>();
		LeadRecord retLead = null;
		for (LeadRecord lr : inflightList) {
			retLead = null;
			retLead = executeMethod(clazz, methodName, lr);
			if (retLead != null) {
				retList.add(retLead);
			}
		}
		return retList;
	}

	public LeadRecord executeMethod(Class clazz, String methodName,
			LeadRecord leadRecord) {
		try {
			Object obj = clazz.newInstance();

			Class[] formalParams = new Class[] { LeadRecord.class };
			Method meth = clazz.getDeclaredMethod(methodName, formalParams);
			
			Object[] actualParams = new Object[] { leadRecord };
			LeadRecord result = ((LeadRecord) meth.invoke(obj, actualParams));
			return result;

		} catch (InstantiationException e) {
			Logger.error("Unable to instantiate : %s", e.getMessage());
		} catch (IllegalAccessException e) {
			Logger.error("Illegal Access : %s", e.getMessage());
		} catch (NoSuchMethodException e) {
			Logger.error("No Such Method : %s", e.getMessage());
		} catch (SecurityException e) {
			Logger.error("Security Exception : %s", e.getMessage());
		} catch (IllegalArgumentException e) {
			Logger.error("Illegal Argument : %s", e.getMessage());
		} catch (InvocationTargetException e) {
			Logger.error("Invocation Target Exception : %s", e.getMessage());
		}
		return null;
	}

	public List<SyncStatus> syncMultipleLeads(
			List<LeadRecord> processedLeadList, boolean dedupEnabled) {
		try {
			Logger.debug("About to sync %d leads", processedLeadList.size());
			List<SyncStatus> status = client.syncMultipleLeads(
					processedLeadList, dedupEnabled);
			Logger.debug("Finished sync-ing %d leads", processedLeadList.size());
			return status;
		} catch (MktowsClientException e) {
			Logger.error("Marketo Client Exception : %s", e.getMessage());
		} catch (MktServiceException e) {
			Logger.error("Marketo Server Exception : %s", e.getMessage());
		}
		return null;
	}

	public String getMethodName() {
		return ("eval" + this.munchkinAccountId + this.campaignId).replace("-", "_");
	}

}
