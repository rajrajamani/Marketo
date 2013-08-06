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
$params = new stdClass();

$mObj = new stdClass();
$mObj->type = 'Program';
$mObj->id="1970";

$attrib1 = new stdClass();
$attrib1->name="Month";
$attrib1->value="2013-06";

$attrib2 = new stdClass();
$attrib2->name="Amount";
$attrib2->value="2000";

$attrib3 = new stdClass();
$attrib3->name="Id";
$attrib3->value="153";

$attribList = array ($attrib1, $attrib2, $attrib3);

$costAttrib = new stdClass();
$costAttrib->attrType="Cost";
$costAttrib->attrList = $attribList;

$mObj->typeAttribList= array($costAttrib);

$params->mObjectList = array($mObj);
$params->operation="UPDATE";
    
$soapClient = new SoapClient($marketoSoapEndPoint ."?WSDL", $options);
try {
  $leads = $soapClient->__soapCall('syncMObjects', array($params), $options, $authHdr);
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
