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
	
	public void outputToLog(String outputText)
	{
		Log.w(this.logTag, outputText);
	}

}
