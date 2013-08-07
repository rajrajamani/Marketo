<?php

$debug = true;

$marketoSoapEndPoint     = "";  // CHANGE ME
$marketoUserId			 = "";	// CHANGE ME
$marketoSecretKey        = "";	// CHANGE ME
$marketoNameSpace        = "http://www.marketo.com/mktows/";

// Create Signature
$dtzObj = new DateTimeZone("America/Los_Angeles");
$dtObj  = new DateTime('now', $dtzObj);
$timeStamp = $dtObj->format(DATE_W3C);
$encryptString = $timeStamp . $marketoUserId;
$signature = hash_hmac('sha1', $encryptString, $marketoSecretKey);

// Create SOAP Header
$attrs = new stdClass();
$attrs->mktowsUserId = $marketoUserId;
$attrs->requestSignature = $signature;
$attrs->requestTimestamp = $timeStamp;

$authHdr = new SoapHeader($marketoNameSpace, 'AuthenticationHeader', $attrs);
$options = array("connection_timeout" => 15, "location" => $marketoSoapEndPoint);

if ($debug) {
  $options["trace"] = 1;
}

// Create Request
/*  
//Query by Email
$leadSel = new stdClass();
$leadSel->keyType = 'EMAIL';

$keyValues = array("formtest1@marketo.com", "joe@marketo.com");
$leadKeys = new stdClass();
$leadKeys->stringItem = $keyValues;
$leadSel->keyValues = $leadKeys;

$leadSelSoap = new stdClass();
$leadSelSoap = array("leadSelector" => $leadSel);

// $leadSelParams = array("leadSelector" => $leadSelSoap, "batchSize" => 10, "streamPosition" => $startPosition);
// $params = array("paramsGetMultipleLeads" => $leadSelParams);

$leadSelSoap = new SoapVar($leadSel, SOAP_ENC_OBJECT, "LeadKeySelector", "http://www.marketo.com/mktows/");
*/
/*
// Query by Update time
$leadSel = new stdClass();
$leadSel->latestUpdatedAt = "2013-08-06T15:45:00-07:00";
$leadSel->oldestUpdatedAt = "2013-08-06T12:00:00-07:00";

$leadSelSoap = new stdClass();
$leadSelSoap = array("leadSelector" => $leadSel);

$leadSelSoap = new SoapVar($leadSel, SOAP_ENC_OBJECT, "LastUpdateAtSelector", "http://www.marketo.com/mktows/");
*/

// Query from a Static List
$leadSel = new stdClass();
//ProgramName.ListName
$leadSel->staticListName = "SMSProgram.listForTesting";

$leadSelSoap = new stdClass();
$leadSelSoap = array("leadSelector" => $leadSel);

$leadSelSoap = new SoapVar($leadSel, SOAP_ENC_OBJECT, "StaticListSelector", "http://www.marketo.com/mktows/");

$params = new  stdClass();
$params->leadSelector = $leadSelSoap;
$params->batchSize = 100;

$soapClient = new SoapClient($marketoSoapEndPoint ."?WSDL", $options);
try {
  $leads = $soapClient->__soapCall('getMultipleLeads', array($params), $options, $authHdr);
}
catch(Exception $ex) {
  var_dump($ex);
}

if ($debug) {
  print "RAW request:\n" .$soapClient->__getLastRequest() ."\n";
  print "RAW response:\n" .$soapClient->__getLastResponse() ."\n";
}



?>
