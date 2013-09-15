/**
 * 
 */
package com.srandroid.network;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.ivy.util.url.ApacheURLLister;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.srandroid.database.TableServers.ServerItem;
import com.srandroid.main.ActivityMain;
import com.srandroid.main.TestActivitySessionDetails;
import com.srandroid.util.Utils;
import com.srandroid.util.Utils.ConstantVars;
import com.srandroid.util.Utils.UIutils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 *
 */
public class SrmNetworkHandler
{
	private static final String LOGTAG = SrmNetworkHandler.class.getName();
	
	private Context context;
	private Activity activity;
	
	private static ConnectivityManager connManager;
	private static NetworkInfo networkInfo;
	
	public static boolean isWifiConnected;
	public static boolean isMobileConnected;
	
//	private static final int BYTE_BUFFER_SIZE = 4*1024;
//	private static final int CHAR_BUFFER_SIZE = 100; // 100 chars
	
	/**
	 * 
	 */
	public SrmNetworkHandler(Context context, Activity activity) 
	{
		this.context = context;
		this.activity = activity;
		
		connManager = 
	    		(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public boolean checkNetworkConnection()
	{
		if(isDeviceOnline())
		{
			if(checkWifiConnection())
			{ // uses Wifi
				Log.w(LOGTAG, "checkNetworkConnection() finds wifi available");
				return true;
			}
			else if(checkMobileConnection())
			{ // uses Mobile Network
				Log.w(LOGTAG, "checkNetworkConnection() finds mobile network available");
				return true;
			}
		}
		else
		{
			Utils.UIutils.createSimpleAlertDialog(
					activity, 
					"Network", 
					"Can not connect to internet!\n"
					+ "Check system network settings!", 
					"OK");
		}
		return false;
	}
	
	public void disconnectFromServer()
	{

		
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
	
//	public int getAddressType(String address)
//	{
//		// 1: http
//		// 2: https
//		// 3: ssh ???
//		// 4: dropbox
//		int type = -1;
//		String head = null;
//		
//		int start = 0;
//		int end = address.indexOf(':');
//		
//		head = address.substring(start, end);
//		
//		Log.w(LOGTAG, "getAddressType() get address head=" + head);
//		if(head.equals(ProtocolTypes.TYPE_DROPBOX))
//		{
//			type = 4;
//		}
//		else if(head.equals(ProtocolTypes.TYPE_SSH))
//		{
//			type = 3;
//		}
//		else if(head.equals(ProtocolTypes.TYPE_HTTPS))
//		{
//			type = 2;
//		}
//		else if(head.equals(ProtocolTypes.TYPE_HTTP))
//		{
//			type = 1;
//		}
//		
//		Log.w(LOGTAG, "getAddressType() get address type=" + type);
//		
//		return type;
//	}
//	
//	public void connectToServer(ServerItem server)
//	{
//		String result = null;
//		URL url = null;
//		
//		String address = server.address;
//		String username = server.username;
//		String password = server.password;
//		String desc = server.description;
//		
//		
//		Log.w(LOGTAG, "connectToServer() will connect to server: " 
//				+ "address=" + address 
//				+ ", username=" + username
//				+ ", password=" + password
//				+ ", description=" + desc);
//		
//		
//		int type = getAddressType(address);
//		switch (type) 
//		{
//			case 1: // HTTP
//					Log.w(LOGTAG, "connectToServer() will connect to HTTP server");
//	
////					// first test connect to the server to get head infos
////					// second test connect to the server with username and password in https
////					try 
////					{
////						url = new URL(address);
////						
////						if(requestHead(url))
////						{
////							Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks server(" + url + ") is available");
////							result = "http server available";
////							
////							// list files
////							listFiles(url);
////						}
////						else 
////						{
////							Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks " +
////									"server(" + url + ") is UNavailable");
////							result = "http server unavailable";
////						}
////					} 
////					catch (MalformedURLException e) 
////					{
////						Log.w(LOGTAG + "$ConnectToServerTask", 
////								"doInBackground() HTTP throws MalformedURLException=" + e.getMessage());
////					}
////					catch (IOException e) 
////					{
////						Log.w(LOGTAG + "$ConnectToServerTask", 
////								"doInBackground() HTTP throws IOException=" + e.getMessage()); 
////					}
//					
//					
//					result = "case http no codes";
//					break;
//			
//			case 2: // HTTPS
//					Log.w(LOGTAG, "connectToServer() will connect to HTTPS server");
//					
////					try 
////					{
////						if(requestHead(url))
////						{
////							Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks " +
////									"server(" + url + ") is available");
////							result = "https server available";
////							
////							// list files
////							listFiles(url);
////						}
////						else 
////						{
////							Log.w(LOGTAG + "$ConnectToServerTask", "doInBackground() checks " +
////									"server(" + url + ") is UNavailable");
////							result = "https server unavailable";
////						}
////					} 
////					catch (MalformedURLException e) 
////					{
////						Log.w(LOGTAG + "$ConnectToServerTask", 
////								"doInBackground() HTTPS throws MalformedURLException=" + e.getMessage());
////					}
////					catch (IOException e) 
////					{
////						Log.w(LOGTAG + "$ConnectToServerTask", 
////								"doInBackground() HTTPS throws IOException=" + e.getMessage()); 
////					}
//					
//					result = "case https no codes";
//					break;
//			
//			case 3: // SSH
//					Log.w(LOGTAG, "connectToServer() will connect to SSH server");
//					result = "case ssh server ???";
//					break;
//					
//			case 4: // dropbox
//					Log.w(LOGTAG, "connectToServer() will connect to dropbox server");
//					
//					dropboxHandler = new SrmDropboxHandler(context);
//					dropboxHandler.createDropboxHandler(context);
//					
//					// if loggedin, logs out or vice versa
//					if (dropboxHandler.isLoggedIn) 
//					{
//	                    dropboxHandler.logOut(context);
//	                } 
//					else 
//					{
//	                    // Start the remote authentication
//	                    dropboxHandler.dropbox
//	                    	.getSession()
//	                    	.startAuthentication(TestActivitySessionDetails.this);
//	                }
//					
//					result = "dropbox server available";
//					break;
//
//			default:
//					Log.w(Log.w(LOGTAG, "connectToServer() get unsupported server address");
//					result = "server address unsupported";
//					break;
//		}
//		
//		Log.w(Log.w(LOGTAG, "connectToServer() get result=" + result);
//	}
//	
	
	
	public static class ProtocolTypes
	{
		public static final String TYPE_HTTP = "http";
		public static final String TYPE_HTTPS = "https";
		public static final String TYPE_SSH = "ssh"; // ???necessary
		public static final String TYPE_DROPBOX = "dropbox";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
//	public void disconnectFromHTTPServer(HttpURLConnection conn)
//	{
//		if (conn != null) 
//        {
//			Log.w(LOGTAG, "disconnectFromHTTPServer() will disconnect from server " + conn.getURL());
//        	conn.disconnect();
//        }
//	}
//	
//	
//	public void downloadSingleFile(URL url)
//			throws IOException
//	{
//		String destFileName = extractFileName(url.toString());
//		Log.w(LOGTAG, 
//				"downloadSingleFile() will download file RESOURCE=" + url 
//				+ " to DEST=" + Utils.ConstantVars.DIR_EXT_SCRIPTS_PATH + "/" + destFileName);
//		
//		InputStream input = null;
//		FileOutputStream output = null;
//		HttpURLConnection conn = null;
//		
//	    try 
//	    {
//	    	// input stream
//	    	if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTP))
//				conn = (HttpURLConnection) url.openConnection();
//			else if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTPS))
//				conn = (HttpsURLConnection) url.openConnection();
//	        conn.setReadTimeout(10000 /* milliseconds */);
//	        conn.setConnectTimeout(15000 /* milliseconds */);
//	        conn.setRequestMethod("GET"); 
//	        conn.setDoInput(true);
//	        conn.connect();
//	        int response = conn.getResponseCode();
//	        Log.w(LOGTAG, "downloadSingleFile() get response=" + response);
//	        input = conn.getInputStream();
//	        
//	        // output stream
//	        File scriptsFolder = new File(Utils.ConstantVars.DIR_EXT_SCRIPTS_PATH);
//	        if(!scriptsFolder.exists()) scriptsFolder.mkdir();
//	        File outputFilePath = new File(scriptsFolder, destFileName);
//	        if(!outputFilePath.exists())
//	        {
//	        	output = new FileOutputStream(outputFilePath);
//		        
//		        // read & write
//		        byte[] buffer = new byte[BYTE_BUFFER_SIZE];
//		        int bufferLength = 0;
//
//		        while ( (bufferLength = input.read(buffer)) > 0 ) 
//		        {
//		        	output.write(buffer, 0, bufferLength);
//		        }
//	        }
//	        
//	    } 
//	    finally 
//	    {
//	        if(input != null) input.close();
//	        if(output != null) output.close();
//	        if(conn != null) conn.disconnect();
//	    }
//	}
//	
//
//	public void downloadAllFiles(URL url)
//			throws IOException
//	{
//		
//	}
//	
//
//	public String extractFileName(String filepath)
//	{
//		int start = filepath.lastIndexOf('/') + 1;
//		int end = filepath.length();
//		
//		String filename = filepath.substring(start, end);
//		
//		Log.w(LOGTAG, "extractFileName() get file name=" + filename 
//						+ " from filepath=" + filepath);
//		
//		return filename;
//	}
//	
//	// check if the server is available
//	public boolean requestHead(URL url)
//		throws IOException
//	{
//		Log.w(LOGTAG, "requestHead() will request HEAD from address=" + url);
//		
//		HttpURLConnection conn = null;
//		
//	    try 
//	    {
//	    	if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTP))
//				conn = (HttpURLConnection) url.openConnection();
//			else if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTPS))
//				conn = (HttpsURLConnection) url.openConnection();
//	        conn.setReadTimeout(10000 /* milliseconds */);
//	        conn.setConnectTimeout(15000 /* milliseconds */);
//	        conn.setRequestMethod("HEAD"); // GET
//	        conn.setDoInput(true);
//	        // Starts the query
//	        conn.connect();
//	        
//	        int response = conn.getResponseCode();
//	        Log.w(LOGTAG, "requestHead() get response=" + response);
//	        // Log.w(LOGTAG, "requestHead() get HeaderFields=" + conn.getHeaderFields().toString());
//	        
//	        if(200 <= response && response <= 399) return true;
//	        else return false;
//	    } 
//	    finally 
//	    {
//	    	if(conn != null) conn.disconnect();
//	    }
//	}
//	
//	// Reads an InputStream and converts it to a String.
//	public String readInputStreamToString(InputStream stream, int charBufferSize) 
//			throws IOException, UnsupportedEncodingException 
//	{
//	    Reader reader = null;
//	    reader = new InputStreamReader(stream, "UTF-8");        
//	    char[] buffer = new char[charBufferSize];
//	    reader.read(buffer);
//	    return new String(buffer);
//	}
//	
//	
//	public void listFilesApacheIvy(URL url)
//			throws IOException
//	{
//		Log.w(LOGTAG, "listFilesIvy() will list files in server=" + url );
//		
//        try 
//        {   
//            ApacheURLLister lister = new ApacheURLLister();         
//            List serverDir = lister.listAll(url);
//            Log.w(LOGTAG, "listFilesIvy() lists files in server=" + url
//            		+ ", files=" + serverDir.toString());      
//        }
//        finally
//        {
//        	
//        }
//
//	}
//	
//	public void listFiles(URL url)
//			throws IOException
//	{
//		String destFileName = "response.html";
//		String destFolderName = url.getHost();
//		Log.w(LOGTAG, 
//				"listFiles() will download response file RESOURCE=" + url 
//				+ " to DEST=" + Utils.ConstantVars.DIR_EXT_SCRIPTS_PATH 
//				+ "/" + destFolderName + "/" + destFileName);
//		
//		InputStream input = null;
//		FileOutputStream output = null;
//		HttpURLConnection conn = null;
//		
//	    try 
//	    {
//	    	// input stream
//	    	if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTP))
//				conn = (HttpURLConnection) url.openConnection();
//			else if(url.getProtocol().equals(ProtocolTypes.TYPE_HTTPS))
//				conn = (HttpsURLConnection) url.openConnection();
//	        conn.setReadTimeout(10000 /* milliseconds */);
//	        conn.setConnectTimeout(15000 /* milliseconds */);
//	        conn.setRequestMethod("GET"); 
//	        conn.setDoInput(true);
//	        conn.connect();
//	        int response = conn.getResponseCode();
//	        Log.w(LOGTAG, "listFiles() get response=" + response);
//	        input = conn.getInputStream();
//	        
//	        // output stream
//	        File scriptsFolder = 
//	        		new File(Utils.ConstantVars.DIR_EXT_SCRIPTS_PATH + File.separator + destFolderName);
//	        if(!scriptsFolder.exists()) scriptsFolder.mkdir();
//	        File outputFilePath = new File(scriptsFolder, destFileName);
//	        if(!outputFilePath.exists())
//	        {
//	        	output = new FileOutputStream(outputFilePath);
//		        
//		        // read & write
//		        byte[] buffer = new byte[BYTE_BUFFER_SIZE];
//		        int bufferLength = 0;
//
//		        while ( (bufferLength = input.read(buffer)) > 0 ) 
//		        {
//		        	output.write(buffer, 0, bufferLength);
//		        }
//	        }
//	        
//	    } 
//	    finally 
//	    {
//	    	Log.w(LOGTAG, "listFiles() finished reading and writing file");
//	    	
//	        if(input != null) input.close();
//	        if(output != null) output.close();
//	        if(conn != null) conn.disconnect();
//	    }
//
//	}
	
}