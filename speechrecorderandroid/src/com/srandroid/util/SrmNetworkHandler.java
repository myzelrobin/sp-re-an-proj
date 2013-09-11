/**
 * 
 */
package com.srandroid.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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
	
	
	private HttpURLConnection conn;
	// ((HttpURLConnection) e).getErrorStream();
	
	
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
				new ConnectToServerTask().execute(address, username, password);
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
				if(requestHead(address))
				{
					Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks server(" 
							+ address + ") is available, will download file");
					// download file
					downloadSingleFile(Utils.ConstantVars.SERVER_TESTDOWNLOAD_FILEPATH);
					result = "downloaded file";
				}
				else 
				{
					Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks server(" 
							+ address + ") is UNavailable");
					result = "server unavailable";
				}
			} 
			catch (IOException e) 
			{
				Log.w(LOGTAG + "$ConnectToServerTask", 
						"doInBackground() calls requestHead() throws Exception=" + conn.getErrorStream()); 
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
		
		// HTTP, check if the server is available
		private boolean requestHead(String address)
			throws IOException, MalformedURLException
		{
			Log.w(LOGTAG + "$ConnectToServerTask", 
					"requestHead() will request HEAD from address=" + address);
			
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
		        Log.w(LOGTAG + "$ConnectToServerTask", "requestHead() get HeaderFields=" 
		        		+ conn.getHeaderFields().toString());
		        
		        if(200 <= response && response <= 399) return true;
		        else return false;
		    } 
		    finally 
		    {
		        if (conn != null) 
		        {
		        	conn.disconnect();
		        }
		    }
		}
		
		// HTTPS, check if the server is available
		private boolean requestHeadHTTPS(String address)
			throws IOException, MalformedURLException
		{
			Log.w(LOGTAG + "$ConnectToServerTask", 
					"requestHeadHTTPS() will request HEAD from address=" + address);
			
		    try 
		    {	
		        URL url = new URL(address);
		        conn = (HttpsURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        conn.setRequestMethod("HEAD"); // GET
		        conn.setDoInput(true);
		        // Starts the query
		        conn.connect();
		        
		        int response = conn.getResponseCode();
		        Log.w(LOGTAG + "$ConnectToServerTask", "requestHeadHTTPS() get response=" + response);
		        Log.w(LOGTAG + "$ConnectToServerTask", "requestHead() get HeaderFields=" 
		        		+ conn.getHeaderFields().toString());
		        
		        if(200 <= response && response <= 399) return true;
		        else return false;
		    } 
		    finally 
		    {
		        if (conn != null) 
		        {
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
		
		
		private void downloadSingleFile(String resFilepath)
				throws IOException, MalformedURLException
		{
			String destFilename = extractFileName(resFilepath);
			Log.w(LOGTAG + "$ConnectToServerTask", 
					"downloadSingleFile() will download RES=" + resFilepath 
					+ " to DEST=" + destFilename);
			
			InputStream input = null;
			FileOutputStream output = null;
			
		    try 
		    {
		    	// input stream
		        URL url = new URL(resFilepath);
		        conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        conn.setRequestMethod("GET"); 
		        conn.setDoInput(true);
		        conn.connect();
		        input = conn.getInputStream();
		        
		        // output stream
		        File scriptsFolder = new File(Utils.ConstantVars.DIR_EXT_SCRIPTS_PATH);
		        File outputFilePath = new File(scriptsFolder, destFilename);
		        output = new FileOutputStream(outputFilePath);
		        
		        // read & write
		        byte[] buffer = new byte[1024];
		        int bufferLength = 0;

		        while ( (bufferLength = input.read(buffer)) > 0 ) 
		        {
		        	output.write(buffer, 0, bufferLength);
		        }
		        
		    } 
		    finally 
		    {
		        if (input != null) 
		        {
		        	input.close();
		        } 
		        if(output != null)
		        {
		        	output.close();
		        }
		        if (conn != null) 
		        {
		        	conn.disconnect();
		        }
		    }
		}
		
		private String extractFileName(String filepath)
		{
			int start = filepath.lastIndexOf('/') + 1;
			int end = filepath.length();
			
			String filename = filepath.substring(start, end);
			
			Log.w(LOGTAG + "$ConnectToServerTask", 
					"getFileName() get file name=" + filename 
					+ " from filepath=" + filepath);
			
			return filename;
		}
		
		private void downloadAllFiles(String resFolder)
				throws IOException, MalformedURLException
		{
			
		}
		
		
	}

}
