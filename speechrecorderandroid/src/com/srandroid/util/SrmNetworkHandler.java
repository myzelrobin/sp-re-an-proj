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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.ivy.util.url.ApacheURLLister;

import com.srandroid.database.TableServers.ServerItem;
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
	
	private static final int BYTE_BUFFER_SIZE = 4*1024;
	private static final int CHAR_BUFFER_SIZE = 100; // 100 chars
	
	
	/**
	 * 
	 */
	public SrmNetworkHandler(Context context) 
	{
		this.context = context;
		
		connManager = 
	    		(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public void connectToServer(ServerItem serverItem)
	{
		
		String address = serverItem.address;
		String username = serverItem.username;
		String password = serverItem.password;
		String description = serverItem.description;
		
		Log.w(LOGTAG, "connectToServer(): will connect to server " 
				+ "address=" + address 
				+ ", username=" + username
				+ ", password=" + password
				+ ", desc=" + description);
		
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
	
	public void disconnectFromServer(HttpURLConnection conn)
	{
		if (conn != null) 
        {
			Log.w(LOGTAG, "disconnectFromServer() will disconnect from server " + conn.getURL());
        	conn.disconnect();
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
	
	private void downloadSingleFile(URL url)
			throws IOException
	{
		String destFileName = extractFileName(url.toString());
		Log.w(LOGTAG, 
				"downloadSingleFile() will download file RESOURCE=" + url 
				+ " to DEST=" + Utils.ConstantVars.DIR_EXT_SCRIPTS_PATH + "/" + destFileName);
		
		InputStream input = null;
		FileOutputStream output = null;
		HttpURLConnection conn = null;
		
	    try 
	    {
	    	// input stream
	    	if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTP))
				conn = (HttpURLConnection) url.openConnection();
			else if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTPS))
				conn = (HttpsURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET"); 
	        conn.setDoInput(true);
	        conn.connect();
	        int response = conn.getResponseCode();
	        Log.w(LOGTAG, "downloadSingleFile() get response=" + response);
	        input = conn.getInputStream();
	        
	        // output stream
	        File scriptsFolder = new File(Utils.ConstantVars.DIR_EXT_SCRIPTS_PATH);
	        if(!scriptsFolder.exists()) scriptsFolder.mkdir();
	        File outputFilePath = new File(scriptsFolder, destFileName);
	        if(!outputFilePath.exists())
	        {
	        	output = new FileOutputStream(outputFilePath);
		        
		        // read & write
		        byte[] buffer = new byte[BYTE_BUFFER_SIZE];
		        int bufferLength = 0;

		        while ( (bufferLength = input.read(buffer)) > 0 ) 
		        {
		        	output.write(buffer, 0, bufferLength);
		        }
	        }
	        
	    } 
	    finally 
	    {
	        if(input != null) input.close();
	        if(output != null) output.close();
	        if(conn != null) conn.disconnect();
	    }
	}
	

	private void downloadAllFiles(URL url)
			throws IOException
	{
		
	}
	

	private String extractFileName(String filepath)
	{
		int start = filepath.lastIndexOf('/') + 1;
		int end = filepath.length();
		
		String filename = filepath.substring(start, end);
		
		Log.w(LOGTAG, "extractFileName() get file name=" + filename 
						+ " from filepath=" + filepath);
		
		return filename;
	}
	
	// check if the server is available
	private boolean requestHead(URL url)
		throws IOException
	{
		Log.w(LOGTAG, "requestHead() will request HEAD from address=" + url);
		
		HttpURLConnection conn = null;
		
	    try 
	    {
	    	if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTP))
				conn = (HttpURLConnection) url.openConnection();
			else if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTPS))
				conn = (HttpsURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("HEAD"); // GET
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        
	        int response = conn.getResponseCode();
	        Log.w(LOGTAG, "requestHead() get response=" + response);
	        // Log.w(LOGTAG, "requestHead() get HeaderFields=" + conn.getHeaderFields().toString());
	        
	        if(200 <= response && response <= 399) return true;
	        else return false;
	    } 
	    finally 
	    {
	    	if(conn != null) conn.disconnect();
	    }
	}
	
	// Reads an InputStream and converts it to a String.
	private String readInputStreamToString(InputStream stream, int charBufferSize) 
			throws IOException, UnsupportedEncodingException 
	{
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[charBufferSize];
	    reader.read(buffer);
	    return new String(buffer);
	}
	
	private int getURLProtocolType(URL url)
	{
		// 1: http
		// 2: https
		// 3: ssh
		int type = -1;
		
		String protocol = url.getProtocol();
		
		Log.w(LOGTAG, "getURLProtocolType() get protocol=" + protocol);
		
		if( protocol.equals(ProtocolTypes.TYPE_HTTP) ) type = 1;
		else if( protocol.equals(ProtocolTypes.TYPE_HTTPS) ) type = 2;
		else if( protocol.equals(ProtocolTypes.TYPE_SSH) ) type = 3;
		
		Log.w(LOGTAG, "getURLProtocolType() get protocol type=" + type);
		
		return type;
	}
	
	private void listFilesApacheIvy(URL url)
			throws IOException
	{
		Log.w(LOGTAG, "listFilesIvy() will list files in server=" + url );
		
        try 
        {   
            ApacheURLLister lister = new ApacheURLLister();         
            List serverDir = lister.listAll(url);
            Log.w(LOGTAG, "listFilesIvy() lists files in server=" + url
            		+ ", files=" + serverDir.toString());      
        }
        finally
        {
        	
        }

	}
	
	private void listFiles(URL url)
			throws IOException
	{
		String destFileName = "response.html";
		String destFolderName = url.getHost();
		Log.w(LOGTAG, 
				"listFiles() will download response file RESOURCE=" + url 
				+ " to DEST=" + Utils.ConstantVars.DIR_EXT_SCRIPTS_PATH 
				+ "/" + destFolderName + "/" + destFileName);
		
		InputStream input = null;
		FileOutputStream output = null;
		HttpURLConnection conn = null;
		
	    try 
	    {
	    	// input stream
	    	if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTP))
				conn = (HttpURLConnection) url.openConnection();
			else if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTPS))
				conn = (HttpsURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET"); 
	        conn.setDoInput(true);
	        conn.connect();
	        int response = conn.getResponseCode();
	        Log.w(LOGTAG, "listFiles() get response=" + response);
	        input = conn.getInputStream();
	        
	        // output stream
	        File scriptsFolder = 
	        		new File(Utils.ConstantVars.DIR_EXT_SCRIPTS_PATH + File.separator + destFolderName);
	        if(!scriptsFolder.exists()) scriptsFolder.mkdir();
	        File outputFilePath = new File(scriptsFolder, destFileName);
	        if(!outputFilePath.exists())
	        {
	        	output = new FileOutputStream(outputFilePath);
		        
		        // read & write
		        byte[] buffer = new byte[BYTE_BUFFER_SIZE];
		        int bufferLength = 0;

		        while ( (bufferLength = input.read(buffer)) > 0 ) 
		        {
		        	output.write(buffer, 0, bufferLength);
		        }
	        }
	        
	    } 
	    finally 
	    {
	    	Log.w(LOGTAG, "listFiles() finished reading and writing file");
	    	
	        if(input != null) input.close();
	        if(output != null) output.close();
	        if(conn != null) conn.disconnect();
	    }

	}
	
	
	
	
	
	
	
	
	
	public class ProtocolTypes
	{
		public static final String TYPE_HTTP = "http";
		public static final String TYPE_HTTPS = "https";
		public static final String TYPE_SSH = "ssh";
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Class  
	 *
	 */
	private class ConnectToServerTask extends AsyncTask<String, Void, String>
	{
		private String address;
		private String username;
		private String password;
		
		@Override
		protected String doInBackground(String... array) 
		{
			String result = null;
			URL url = null;
			
			address = array[0];
			username = array[1];
			password = array[2];
			
			Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() get strings " 
					+ "address=" + address 
					+ ", username=" + username
					+ ", password=" + password);
			try 
			{
				url = new URL(address);
			} 
			catch (MalformedURLException e1) 
			{
				Log.w(LOGTAG + "$ConnectToServerTask", 
						"doInBackground() throws MalformedURLException=" + e1.getMessage());
			}
			
			// first test connect to the server to get head infos
			// second test connect to the server with username and password in https
			int protocolType = getURLProtocolType(url);
			switch (protocolType) 
			{
				case 1: // HTTP
						Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() connects to HTTP server");
						
						try 
						{
							if(requestHead(url))
							{
								Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks " +
										"server(" + url + ") is available");
								result = "http server available";
								
								// list files
								listFiles(url);
							}
							else 
							{
								Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks " +
										"server(" + url + ") is UNavailable");
								result = "http server unavailable";
							}
						} 
						catch (IOException e) 
						{
							Log.w(LOGTAG + "$ConnectToServerTask", 
									"doInBackground() HTTP throws Exception=" + e.getMessage()); 
						}
						
						break;
				
				case 2: // HTTPS
						Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() connects to HTTPS server");
						
						try 
						{
							if(requestHead(url))
							{
								Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks " +
										"server(" + url + ") is available");
								result = "https server available";
								
								// list files
								listFiles(url);
							}
							else 
							{
								Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks " +
										"server(" + url + ") is UNavailable");
								result = "https server unavailable";
							}
						} 
						catch (IOException e) 
						{
							Log.w(LOGTAG + "$ConnectToServerTask", 
									"doInBackground() HTTPS throws Exception=" + e.getMessage()); 
						}
						
						break;
				
				case 3: // SSH
						Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() connects to SSH server");
						result = "ssh server ???";
						break;
	
				default:
						Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() finds unsupported server");
						result = "unsupported server";
						break;
			}
			
			
			return result;
		}
		
		//displays the results of the AsyncTask
		@Override
        protected void onPostExecute(String result) 
		{
			Log.w(LOGTAG + "$ConnectToServerTask", "onPostExecute() get result=" + result);
		}
		
	}

}
