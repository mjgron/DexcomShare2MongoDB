package com.mltkm.dexcom.share;

import java.util.Hashtable;

import com.google.gson.Gson;
import com.mltkm.nightscout.mongo.DatabaseHelper;
import com.mltkm.util.HttpRequestHelper;
import com.mltkm.util.PropertyHelper;

public class ShareConnectionInfo {
	public static final String SHARE_LOGIN_URL = "https://share1.dexcom.com/ShareWebServices/Services/General/LoginPublisherAccountByName";
	public static final String FOLLOW_LOGIN_URL = "https://share1.dexcom.com/ShareWebServices/Services/General/LoginSubscriberAccount";
	public static final Hashtable<String, String> LOGIN_HTTP_HEADERS = getLoginHttpHeaders(); 
	public static final String FOLLOW_LATEST_BG_URL = "https://share1.dexcom.com/ShareWebServices/Services/Subscriber/ReadSubscriptionLatestGlucoseValues";
	public static final String SHARE_LATEST_BG_URL  = "https://share1.dexcom.com/ShareWebServices/Services/Publisher/ReadPublisherLatestGlucoseValues";
	public static final Hashtable<String, String> LATEST_BG_HEADERS = getLatestBGHttpHeaders(); 
	
	//keep these hidden in properties file
	private String shareUsername = null;
	private String sharePassword = null;
	private String accountId = null; 
	private String applicationId = null;
	private String followPassword = null; 
	private String subscriptionId = null; 
	private String shareLoginJson = null;
	private String followLoginJson = null;
	private boolean USE_FOLLOW = false;
	private static String sessionId = null;
	
	//unsecure and/or optional settings in properties file
	public String bgLookBackminutes = null;
	public String bgMaxRecordCount = null;
	public long pollingFequencyMinutes = 5;
	
	public static Hashtable<String, String> getLoginHttpHeaders(){
		Hashtable<String, String> headers = getCommonShareHeaders();
		headers.put("Content-Type", "application/json");
		return(headers);
	}
	public static Hashtable<String, String> getLatestBGHttpHeaders(){
		Hashtable<String, String> headers = getCommonShareHeaders();
		return(headers);
	}
	public static Hashtable<String, String> getCommonShareHeaders(){
		Hashtable<String, String> headers = new Hashtable<String, String>();
		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Accept-Language", "en-us");
		headers.put("Accept", "application/json");
		headers.put("User-Agent", "DexcomRM/2.0.3.15 CFNetwork/711.1.16 Darwin/14.0.0");
		return(headers);
	}
	public void init(PropertyHelper properties) 
	throws Exception
	{
		shareUsername = properties.getProperty("SHARE_USERNAME");
		sharePassword = properties.getProperty("SHARE_PASSWORD");
		accountId = properties.getProperty("FOLLOW_ACCOUNT_ID");
		applicationId = properties.getProperty("FOLLOW_APPLICATION_ID");
		followPassword = properties.getProperty("FOLLOW_PASSWORD");
		subscriptionId = properties.getProperty("FOLLOW_SUBSCRIPTION_ID");
		shareLoginJson  = "{\"accountName\":\""+ shareUsername +"\", \"applicationId\":\""+ applicationId +"\", \"password\":\""+ sharePassword +"\" }";
		followLoginJson = "{\"accountId\":\""+ accountId +"\",\"applicationId\":\""+ applicationId +"\",\"password\":\""+ followPassword +"\"}";
		USE_FOLLOW = (accountId != null && applicationId !=null && followPassword != null && subscriptionId != null );
		
		bgLookBackminutes = properties.getProperty("BG_LOOKBACK_MINUTES", "10");
		bgMaxRecordCount = properties.getProperty("BG_MAX_RECORD_COUNT", "2");
		pollingFequencyMinutes = properties.getLongProperty("POLLING_FREQUENCY_MINUTES", pollingFequencyMinutes);
		
		//verify that either a share or follow login was found
		if(!USE_FOLLOW && (shareUsername == null || sharePassword == null)){
			throw new Exception("Login information was missing.");
		}
	}

	public boolean isValidSession(){
		//TODO reuse sessionId until it fails, but haven't figure out best way to do that
		return(false);
	}
	public String getLatestBGUrlWithParameters()
	throws Exception{
		if(sessionId == null || !isValidSession()){
			sessionId = getSessionID();
		}
		String sessionIdFiltered = sessionId.replace("\"", "");
		if(USE_FOLLOW){
        	return(FOLLOW_LATEST_BG_URL +"?sessionId="+ sessionIdFiltered +"&subscriptionId="+ subscriptionId +"&minutes="+ bgLookBackminutes +"&maxCount="+ bgMaxRecordCount);
        }else{
        	return(SHARE_LATEST_BG_URL +"?sessionId="+ sessionIdFiltered +"&minutes="+ bgLookBackminutes +"&maxCount="+ bgMaxRecordCount);
        }
	}

	public DexcomRecord[] parseResultSet(String resultSet, DatabaseHelper mongoDB)
	throws Exception
	{
		Gson gson = new Gson();
		DexcomRecord[] records = gson.fromJson(resultSet, DexcomRecord[].class);
		return(records);
	}
	
	public String getSessionID()
	throws Exception{
		if(USE_FOLLOW){
			return(getSessionIDFollow());
		}else{
			return(getSessionIDShare());
		}
	}
	public String getSessionIDShare()
	throws Exception{
		return(HttpRequestHelper.sendPost(SHARE_LOGIN_URL, LOGIN_HTTP_HEADERS, shareLoginJson, true));
	}
	public String getSessionIDFollow()
	throws Exception{
		return(HttpRequestHelper.sendPost(FOLLOW_LOGIN_URL, LOGIN_HTTP_HEADERS, followLoginJson, true));
	}
}
