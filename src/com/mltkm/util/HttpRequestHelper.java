package com.mltkm.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequestHelper {
	public static String sendGet(URI uri)
	throws Exception{
		URL url = new URL(uri.toASCIIString());
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(false);
		conn.setUseCaches(false);
		conn.setRequestMethod("GET");
 
		//add request headers here if needed
//		con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = conn.getResponseCode();
		if(responseCode != 200){
			throw new Exception(" HTTP response "+ responseCode);
		}
//		System.out.println("\nSending 'GET' request to URL : " + url);
//		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		return(response.toString());
	}
	
	public static String sendPost(String endpoint, Map<String, String> headerMap, String parameters, boolean asContent)
	throws Exception {
//		System.out.println("url="+ endpoint +"\nparameters="+ parameters);
		if(parameters == null && asContent){
			//avoid null pointer Exceptions when asContent is true when no parameters are sent 
			asContent = false;
		}
		URL url = new URL(endpoint);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
 
		//add request headers
		con.setRequestMethod("POST");
		int contentLength = (asContent) ? parameters.length() : 0;
		con.setRequestProperty("Content-Length", "\""+ contentLength +"\"");
		Iterator<String> iter = headerMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String value = headerMap.get(key);
			con.setRequestProperty(key, value);
		}
				
		// Send post request
		String urlParameters = (asContent) ? parameters : "";
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		if(responseCode != 200){
			throw new Exception(" HTTP response "+ responseCode);
		}
 
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return(response.toString());
	}
}
