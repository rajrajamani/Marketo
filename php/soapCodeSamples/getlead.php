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
  $options["trace"] = true;
}

// Create Request
$leadKey = array("keyType" => "EMAIL", "keyValue" => "t@t.com");
$leadKeyParams = array("leadKey" => $leadKey);
$params = array("paramsGetLead" => $leadKeyParams);

$soapClient = new SoapClient($marketoSoapEndPoint ."?WSDL", $options);
try {
  $lead = $soapClient->__soapCall('getLead', $params, $options, $authHdr);
}
catch(Exception $ex) {
  var_dump($ex);
}

if ($debug) {
  print "RAW request:\n" .$soapClient->__getLastRequest() ."\n";
  print "RAW response:\n" .$soapClient->__getLastResponse() ."\n";
}

print_r($lead);

?>
