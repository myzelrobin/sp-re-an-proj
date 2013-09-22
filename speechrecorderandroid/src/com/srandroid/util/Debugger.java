/**
 * 
 */
package com.srandroid.util;

import android.util.Log;

/**
 *
 */
public class Debugger 
{

	private String logTag;
	
	private boolean canOutput;
	
	/**
	 * 
	 */
	public Debugger(String className) 
	{
		this.logTag = className;
		this.canOutput = true;
	}
	
	public void outputInfo(String outputText)
	{
		Log.i(this.logTag, outputText);
	}
	
	public void outputException(
			String action, 
			String methodName, 
			String exceptionType, 
			Throwable tr)
	{
		String actionText = "Error " + action + "! ";
		String exceptionText = methodName + " throws " + exceptionType + ":";
		
		Log.i(this.logTag, actionText + exceptionText, tr);
	}
	
}
