package com.mltkm.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyHelper {
	private String propertyFilename = null;
	private  Properties properties = null;

	public PropertyHelper(String filename)
	throws Exception
	{
		propertyFilename = filename;
		properties = new Properties();
		InputStream input = null;
	 
		try {	 
			input = new FileInputStream(propertyFilename);
			properties.load(input);
		} catch (Exception e) {
	        throw new Exception("Could not open property file: "+ propertyFilename, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					//do nothing
				}
			}
		}
	}
	
	public String getProperty(String name)
	throws Exception{
		return(getProperty(name, false));
	}
	public  String getProperty(String name, String defaultValue)
	{
		try{
			return(properties.getProperty(name));
		}catch(Exception e){
			//do nothing, default value will be returned
		}
		return(defaultValue);
	}
	public  String getProperty(String name, boolean required)
	throws Exception{
		String value = properties.getProperty(name);
		if(required && (value == null || value.length() <=0)){
			throw new Exception("The property "+ name +" must be set in file: "+ propertyFilename);
		}
		return(value);
	}
	public int getIntProperty(String name)
	throws Exception{
		return(Integer.parseInt(getProperty(name, false)));
	}
					
	public  long getLongProperty(String name, long defaultValue)
	throws Exception{
		try{
			String value = getProperty(name);
			if(value != null){
				try {
					return(Long.parseLong(value));
				}catch(Exception e){
					//just keep the default time
					System.out.println(e.getMessage());
				}
			}
		}catch(Exception e){
			//could not convert property to an integer, use default value
			
		}
		return(defaultValue);
	}
	public boolean getBooleanProperty(String name)
	throws Exception{
		return(Boolean.parseBoolean(getProperty(name)));
	}
	
}
