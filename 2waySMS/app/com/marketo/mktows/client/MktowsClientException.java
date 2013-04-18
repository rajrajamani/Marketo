package com.marketo.mktows.client;

import javax.xml.soap.Detail;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

public class MktowsClientException extends Exception {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected String serviceDetailMessage = null;

	public MktowsClientException() {
		super();
	}

	public MktowsClientException(String message, Throwable cause) {
		super(message, cause);
		if (cause instanceof SOAPFaultException) {
      SOAPFault fault = ((SOAPFaultException)cause).getFault();
      if (fault != null) {
        Detail detail = fault.getDetail();
        if (detail != null) {
          this.serviceDetailMessage = detail.getTextContent();
        }
      }
		}
	}

  public MktowsClientException(String message, Throwable cause, String serviceDetailMessage) {
    super(message, cause);
    this.serviceDetailMessage = serviceDetailMessage;
  }

	public MktowsClientException(String message) {
		super(message);
	}

	public MktowsClientException(Throwable cause) {
		super(cause);
	}

  public String getServiceDetailMessage() {
    return serviceDetailMessage;
  }

}
