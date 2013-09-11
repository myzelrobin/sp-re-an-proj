/**
 * 
 */
package com.srandroid.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.srandroid.main.ActivityMain;
import com.srandroid.main.TestActivitySessionDetails;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
	
	
	public void connectToServer(
			String address, 
			String username, 
			String password)
	{
		Log.w(LOGTAG, "connectToServer(): will connect to server " 
				+ "address=" + address 
				+ ", username=" + username
				+ ", password=" + password);
		
		if(isDeviceOnline())
		{
			if(checkWifiConnection())
			{ // uses Wifi
				new ConnectToServerTask().execute(address, username, password);
			}
			else if(checkMobileConnection())
			{ // uses Mobile Network
				//new ConnectToServerTask().execute(address, username, password);
			}
		}
		else
		{
			Utils.UIutils.createSimpleAlertDialog(
					TestActivitySessionDetails.context, 
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
	
	
	private class ConnectToServerTask extends AsyncTask<String, Void, String>
	{
		private String address;
		private String username;
		private String password;
		

		@Override
		protected String doInBackground(String... array) 
		{
			String result = null;
			
			address = array[0];
			username = array[1];
			password = array[2];
			
			Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() get strings " 
					+ "address=" + address 
					+ ", username=" + username
					+ ", password=" + password);
			
			// first test connect to the server to get head infos
			try 
			{
				result = requestHead(address);
			} 
			catch (Exception e) 
			{
				Log.w(LOGTAG + "$ConnectToServerTask", 
						"doInBackground() calls requestHead() throws Exception=" + e.getMessage()); 
			}
			
			// second test connect to the server with username and password
			
			
			return result;
		}
		
		//displays the results of the AsyncTask
		@Override
        protected void onPostExecute(String result) 
		{
			Log.w(LOGTAG + "$ConnectToServerTask", "onPostExecute() get result=" + result);
		}
		
		
		private String requestHead(String address)
			throws IOException, MalformedURLException
		{
			Log.w(LOGTAG + "$ConnectToServerTask", 
					"requestHead() will request HEAD from address=" + address);
		
			HttpURLConnection conn = null;
			
			InputStream input = null;
			
		    // Only display the first 500 characters of the retrieved content.
		    int len = 500;

		    try 
		    {
		        URL url = new URL(address);
		        conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        conn.setRequestMethod("HEAD"); // GET
		        conn.setDoInput(true);
		        // Starts the query
		        conn.connect();
		        
		        int response = conn.getResponseCode();
		        Log.w(LOGTAG + "$ConnectToServerTask", "requestHead() get response=" + response);
		        
		        input = conn.getInputStream();
		        // Convert the InputStream into a string
		        String contentAsString = readInputStreamToString(input, len);
		        return contentAsString;
		        
		    } finally {
		        if (input != null) {
		        	input.close();
		        } 
		        if (conn != null) {
		        	conn.disconnect();
		        }
		    }
		}
		
		// Reads an InputStream and converts it to a String.
		private String readInputStreamToString(InputStream stream, int len) 
				throws IOException, UnsupportedEncodingException 
		{
		    Reader reader = null;
		    reader = new InputStreamReader(stream, "UTF-8");        
		    char[] buffer = new char[len];
		    reader.read(buffer);
		    return new String(buffer);
		}
	}

}
