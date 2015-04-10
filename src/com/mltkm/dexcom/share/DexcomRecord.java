package com.mltkm.dexcom.share;

import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DexcomRecord {
/* sample record
 {
	"DT":"\/Date(1428039126000-0700)\/",
	"ST":"\/Date(1428042726000)\/",
	"Trend":2,
	"Value":152,
	"WT":"\/Date(1428028327000)\/"
 }
*/
	public static final Hashtable<Integer,String> trend2Direction = getTrendDirections();
	private String DT;
	private String ST;
	private int Trend;
	private int Value;
	private String WT;
	private String direction;
	
	public static  Hashtable<Integer,String> getTrendDirections(){
		Hashtable<Integer,String> trends = new Hashtable<Integer,String>();
		trends.put(0, "NONE");
		trends.put(1, "DoubleUp");
		trends.put(2, "SingleUp");
		trends.put(3, "FortyFiveUp");
		trends.put(4, "Flat");
		trends.put(5, "FortyFiveDown");
		trends.put(6, "SingleDown");
		trends.put(7, "DoubleDown");
		trends.put(8, "NOT COMPUTABLE");
		trends.put(9, "RATE OUT OF RANGE");
		return(trends);
	}
	public String parseMilliseondsFromDateField(String dateField){
		//sample dateField: /Date(1428039126000-0700)/
		String pattern = "(\\d+)";
	    Pattern regex = Pattern.compile(pattern);
	    Matcher matcher = regex.matcher(dateField);
	    String milliseconds = (matcher.find()) ? matcher.group(0) : "0";
	    return(milliseconds);
	}
	public Date createDateFromDexcomDate(String dexDate){
		Date date = new Date();
		date.setTime(Long.parseLong(parseMilliseondsFromDateField(dexDate)));
		return(date);
	}
	public String getDT() {
		return DT;
	}
	public Date getDTasDate(){
		return(createDateFromDexcomDate(DT));
	}
	public void setDT(String dT) {
		DT = dT;
	}
	public String getST() {
		return ST;
	}
	public Date getSTasDate(){
		return(createDateFromDexcomDate(ST));
	}
	public void setST(String sT) {
		ST = sT;
	}
	public int getTrend() {
		return Trend;
	}
	public void setTrend(int trend) {
		Trend = trend;
		setDirection();
	}
	public String getDirection() {
		if(direction == null){
			setDirection();
		}
		return direction;
	}
	public void setDirection() {
		this.direction = (trend2Direction.get(Trend) != null) ? trend2Direction.get(Trend) : trend2Direction.get(0);
	}
	public int getValue() {
		return Value;
	}
	public void setValue(int value) {
		Value = value;
	}
	public String getWT() {
		return WT;
	}
	public Date getWTasDate(){
		return(createDateFromDexcomDate(WT));
	}
	public void setWT(String wT) {
		WT = parseMilliseondsFromDateField(wT);
	}
	
}
