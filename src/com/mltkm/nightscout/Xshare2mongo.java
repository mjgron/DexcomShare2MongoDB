package com.mltkm.nightscout;

import com.mltkm.dexcom.share.DexcomRecord;
import com.mltkm.dexcom.share.ShareConnectionInfo;
import com.mltkm.nightscout.mongo.DatabaseHelper;
import com.mltkm.util.HttpRequestHelper;
import com.mltkm.util.PropertyHelper;

public class Xshare2mongo {
	public static void displayUsageAndExit(){
		System.err.println("Usage: java "+ Xshare2mongo.class.getCanonicalName() +" /path/to/properties/file");
		System.exit(2);
	}
	
	public static void configureProxy(PropertyHelper properties){
		String proxyHostname;
		try {
			proxyHostname = properties.getProperty("PROXY_HOSTNAME");
			String proxyPortNumber = properties.getProperty("PROXY_PORT_NUMBER");
			if(proxyHostname != null && proxyHostname.length() > 0 && proxyPortNumber != null && proxyPortNumber.length() > 0){
				System.setProperty("https.proxyHost", proxyHostname);
				System.setProperty("https.proxyPort", proxyPortNumber);
//				System.out.println("Using https proxy: "+ proxyHostname +":"+ proxyPortNumber);
			}
		} catch (Exception e) {
			//do nothing, a proxy is not required (for most users)
		}
	}
	
	public static void main(String[] args){
		if(args == null || args.length <= 0){
			displayUsageAndExit();
		}
		DatabaseHelper mongoDB = null;
		PropertyHelper properties = null;
		ShareConnectionInfo xShare = null;
		String propertyFilename = args[0];
		
		try{
			properties = new PropertyHelper(propertyFilename);
			configureProxy(properties);
			xShare = new ShareConnectionInfo();
			xShare.init(properties);
			mongoDB = new DatabaseHelper(properties);
		}catch(Exception e){
			e.printStackTrace();
			//force exit here if login info cannot be found no sense retrying 
			System.exit(1);
		}
		String resultSet = null;
		while(true){
			try{
				String url = xShare.getLatestBGUrlWithParameters();
				resultSet  = HttpRequestHelper.sendPost(url, ShareConnectionInfo.LATEST_BG_HEADERS, null, false);
				DexcomRecord[] records = xShare.parseResultSet(resultSet, mongoDB);
				mongoDB.saveUnique(records);
				System.out.flush();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(e.getMessage());
			}finally{
				try {
					if(xShare.pollingFequencyMinutes == 0){
						System.out.println("Finished, shutting down because POLLING_FREQUENCY_MINUTES was set to 0.");
						System.exit(0);
					}
					System.out.println("Sleeping "+ xShare.pollingFequencyMinutes +" minutes");
					Thread.sleep(xShare.pollingFequencyMinutes * 60 * 1000);
				} catch (InterruptedException e) {
					//do nothing
				}
			}
		}
	}
}
