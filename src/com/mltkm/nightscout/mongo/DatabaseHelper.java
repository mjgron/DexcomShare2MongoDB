package com.mltkm.nightscout.mongo;

import java.net.URI;
import java.util.Hashtable;

import com.google.gson.Gson;
import com.mltkm.dexcom.share.DexcomRecord;
import com.mltkm.nightscout.NightscoutCGMRecord;
import com.mltkm.util.HttpRequestHelper;
import com.mltkm.util.PropertyHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class DatabaseHelper {
	private static String MONGO_URI = null;
	private static String MONGO_CGM_COLLECTION = null;
	private static String REST_MONGO_HOST = null;
	private static String MONGO_DATABASE = null; //not needed part of connection url
	private static String MONGO_DEVICE_COLLECTION = null; //device stats like uploader battery level are not support by Dexcom Share 
	private static boolean USE_REST_API = false;
	private static String REST_MONGO_COLLECTION_API = null;
	private static String REST_API_KEY = null;
	private static String REST_MONGO_SAVE_URL = null;
	private static Hashtable<String, String> REST_MONGO_HTTP_HEADERS = null;
	
	public DatabaseHelper(PropertyHelper properties) 
	throws Exception
	{
		MONGO_URI = properties.getProperty("MONGO_URI", true);
		MONGO_CGM_COLLECTION = properties.getProperty("MONGO_CGM_COLLECTION", true);
		MONGO_DEVICE_COLLECTION = properties.getProperty("MONGO_DEVICE_COLLECTION");
		MONGO_DATABASE = properties.getProperty("MONGO_DATABASE");
		REST_MONGO_HOST = properties.getProperty("REST_MONGO_HOST");
		REST_MONGO_COLLECTION_API = properties.getProperty("REST_MONGO_COLLECTION_API");
		REST_API_KEY = properties.getProperty("REST_API_KEY");
		REST_MONGO_SAVE_URL = "https://"+ REST_MONGO_HOST + REST_MONGO_COLLECTION_API +"?apiKey="+ REST_API_KEY;
		REST_MONGO_HTTP_HEADERS = new Hashtable<String, String>();
		REST_MONGO_HTTP_HEADERS.put("Content-Type", "application/json");
		USE_REST_API = properties.getBooleanProperty("USE_REST_API");
	}
	private DBCollection getCollection()
	throws Exception
	{
		MongoClientURI mongoURI  = new MongoClientURI(MONGO_URI);
	    MongoClient mongoClient = new MongoClient(mongoURI);;
	    DB db = mongoClient.getDB(mongoURI.getDatabase());
		return(db.getCollection(MONGO_CGM_COLLECTION));
	}
	public boolean selectByDateREST(long dateInMilliseconds)
	throws Exception
	{
		URI uri = new URI(
		        "https", 
		        REST_MONGO_HOST, 
		        REST_MONGO_COLLECTION_API,
		        "q={'date': "+ dateInMilliseconds +"}&apiKey="+ REST_API_KEY,
		        null);
		String resultSet = HttpRequestHelper.sendGet(uri);
		Gson gson = new Gson();
		NightscoutCGMRecord[] records = gson.fromJson(resultSet, NightscoutCGMRecord[].class);
		return(records != null & records.length > 0);
	}
	public boolean alreadyInDatabase(long date)
	throws Exception
	{
		if(USE_REST_API){
			return(selectByDateREST(date));
		}
		DBCollection collection = getCollection();
		BasicDBObject findQuery = new BasicDBObject("date", date);

		DBCursor docs = collection.find(findQuery).limit(1);
		return(docs.hasNext());
	}
    public void saveREST(long date, String dateString, int sgv, String direction)
    throws Exception
    {
    	NightscoutCGMRecord record = new NightscoutCGMRecord(date, dateString, sgv, direction);
    	Gson gson = new Gson();
    	String jsonRecord = gson.toJson(record);
//    	System.out.println(jsonRecord);
    	
    	String saveResults = HttpRequestHelper.sendPost(REST_MONGO_SAVE_URL, REST_MONGO_HTTP_HEADERS, jsonRecord, true);
    	//Assume this works if no exceptions are thrown
//    	System.out.println("OUT:\n"+ saveResults);
    }
	public void save(long date, String dateString, int sgv, String direction)
	throws Exception{
		if(USE_REST_API){
			saveREST(date, dateString, sgv, direction);
			return;
		}
		DBCollection collection = getCollection();
		BasicDBObject doc = new BasicDBObject("device", "dexcom")
        .append("date", date)
        .append("dateString", dateString)
        .append("sgv", sgv)
        .append("direction", direction)
        .append("type", "sgv")
        .append("filtered", 0)
        .append("unfiltered", 0)
        .append("rssi", 0);
		collection.insert(doc);
	}
	public void saveUnique(DexcomRecord[] records)
	throws Exception
	{
		for(int idx=records.length-1; idx >= 0 ; idx--){
			DexcomRecord record = records[idx];
//					System.out.println(record.getWTasDate().toLocaleString() +":"+record.getValue() +" "+ record.getDirection());
			if(!alreadyInDatabase(record.getWTasDate().getTime())){
				//not a duplicate so save it
				System.out.println("saved svg: "+ record.getValue() +" "+ record.getDirection() +"  "+ record.getWTasDate().toLocaleString());
				save(record.getWTasDate().getTime(), record.getWTasDate().toString(), record.getValue(), record.getDirection());
			}else{
				System.out.println("Duplicate: "+ record.getValue() +" "+ record.getDirection() +"  "+ record.getWTasDate().toLocaleString());
			}
		}
	}
}
