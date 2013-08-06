<?php

$debug = true;

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
$params = new stdClass();
$params->type = 'Opportunity';
// $params->id = "1003";

$mObjCriteria1 = new stdClass();
$mObjCriteria1->attrName="Id";
$mObjCriteria1->comparison="GT";
$mObjCriteria1->attrValue="1";

// $mObjCriteria2 = new stdClass();
// $mObjCriteria2->attrName="Name";
// $mObjCriteria2->comparison="NE";
// $mObjCriteria2->attrValue="elizprogramtest";

$params->mObjCriteriaList=array($mObjCriteria1);

$soapClient = new SoapClient($marketoSoapEndPoint ."?WSDL", $options);
try {
  $leads = $soapClient->__soapCall('getMObjects', array($params), $options, $authHdr);
  // 	  print_r($leads);
}
catch(Exception $ex) {
  var_dump($ex);
}

if ($debug) {
  print "RAW request:\n" .$soapClient->__getLastRequest() ."\n";
  print "RAW response:\n" .$soapClient->__getLastResponse() ."\n";
}



?>
