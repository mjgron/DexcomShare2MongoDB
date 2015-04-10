package com.mltkm.nightscout;

public class NightscoutCGMRecord {
	/* sample record 
	 { "_id" : { "$oid" : "541a448eacdbe06c2cafa7de"} ,
	   "device" : "dexcom" ,
	   "date" : 1410956887000 , 
	   "dateString" : "09/17/2014 08:28:07 AM" , 
	   "sgv" : "164" , 
	   "direction" : "Flat",
	   "type": "sgv",
       "filtered": 0,
       "unfiltered": 0,
       "rssi": 0 } 	   
	 */
	private String device;
	private long date;
	private String dateString;
	private int sgv;
	private String direction;
	private String type;
	private int filtered;
	private int unfiltered;
	private int rssi;

	//constructor to use with Dexcom's share receiver 
	public NightscoutCGMRecord(long date, String dateString, int sgv, String direction)
    {
    	setDevice("dexcom");
    	setDate(date);
    	setDateString(dateString);
    	setSgv(sgv);
    	setDirection(direction);
    	setType("sgv");
    	setFiltered(0);
    	setUnfiltered(0);
    	setRssi(0);
    }
	
	//generic constructors
	public NightscoutCGMRecord(){
	}
	public NightscoutCGMRecord(String device, long date, String dateString, int sgv, String direction, String type, int filtered, int unfiltered, int rssi)
    {
    	setDevice(device);
    	setDate(date);
    	setDateString(dateString);
    	setSgv(sgv);
    	setDirection(direction);
    	setType(type);
    	setFiltered(filtered);
    	setUnfiltered(unfiltered);
    	setRssi(rssi);
    }
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	public int getSgv() {
		return sgv;
	}
	public void setSgv(int sgv) {
		this.sgv = sgv;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getFiltered() {
		return filtered;
	}
	public void setFiltered(int filtered) {
		this.filtered = filtered;
	}
	public int getUnfiltered() {
		return unfiltered;
	}
	public void setUnfiltered(int unfiltered) {
		this.unfiltered = unfiltered;
	}
	public int getRssi() {
		return rssi;
	}
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
	
	
}
