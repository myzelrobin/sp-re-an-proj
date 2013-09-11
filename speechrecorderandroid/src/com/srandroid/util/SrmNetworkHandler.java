/**
 * 
 */
package com.srandroid.util;

import com.srandroid.main.ActivityMain;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 *
 */
public class SrmNetworkHandler 
{
	private static final String LOGTAG = SrmNetworkHandler.class.getName();

	
	
	private Context context;
	
	private static ConnectivityManager connManager;
	private static NetworkInfo networkInfo;
	
	private static boolean isWifiConnected;
	private static boolean isMobileConnected;
	
	/**
	 * 
	 */
	public SrmNetworkHandler(Context context) 
	{
		this.context = context;
		
		connManager = 
	    		(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	
	public void connectToServer()
	{
		Log.w(LOGTAG, "connectToServer(): will connect to the server");
		
		if(isDeviceOnline())
		{
			if(checkWifiConnection())
			{ // uses Wifi
				
			}
			else if(checkMobileConnection())
			{ // uses Mobile Network
				
			}
		}
		else
		{
			Utils.UIutils.createSimpleAlertDialog(
					this.context, 
					"Network", 
					"Can not connect to internet!\n"
					+ "Check system network settings!", 
					"OK");
		}
		
	}
	
	public boolean checkWifiConnection()
	{
		networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		isWifiConnected = networkInfo.isConnected();
		
		Log.w(LOGTAG, "checkWifiConnection() checked wifi connection:" + isWifiConnected);
		return isWifiConnected;
	}
	
	public boolean checkMobileConnection()
	{
		networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		isMobileConnected = networkInfo.isConnected();
		
		Log.w(LOGTAG, "isMobileConnected() checked mobile connection: " + isMobileConnected);
		return isMobileConnected;
	}
	
	public boolean isDeviceOnline() 
	{
	    networkInfo = connManager.getActiveNetworkInfo();
	    Log.w(LOGTAG, "isDeviceOnline(): returns " 
	    		+ (networkInfo != null && networkInfo.isConnected()) );
	    return (networkInfo != null && networkInfo.isConnected());
	}  

}
