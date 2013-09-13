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

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.srandroid.database.TableServers.ServerItem;
import com.srandroid.main.ActivityMain;
import com.srandroid.main.TestActivitySessionDetails;

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
	
	private static ConnectivityManager connManager;
	private static NetworkInfo networkInfo;
	
	public static boolean isWifiConnected;
	public static boolean isMobileConnected;
	
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
	
	public boolean checkNetworkConnection(Activity activeAct)
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
					activeAct, 
					"Network", 
					"Can not connect to internet!\n"
					+ "Check system network settings!", 
					"OK");
		}
		return false;
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
	
	public void downloadSingleFile(URL url)
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
	

	public void downloadAllFiles(URL url)
			throws IOException
	{
		
	}
	

	public String extractFileName(String filepath)
	{
		int start = filepath.lastIndexOf('/') + 1;
		int end = filepath.length();
		
		String filename = filepath.substring(start, end);
		
		Log.w(LOGTAG, "extractFileName() get file name=" + filename 
						+ " from filepath=" + filepath);
		
		return filename;
	}
	
	// check if the server is available
	public boolean requestHead(URL url)
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
	public String readInputStreamToString(InputStream stream, int charBufferSize) 
			throws IOException, UnsupportedEncodingException 
	{
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[charBufferSize];
	    reader.read(buffer);
	    return new String(buffer);
	}
	
	public int getAddressType(String address)
	{
		// 1: http
		// 2: https
		// 3: ssh ???
		// 4: dropbox
		int type = -1;
		String head = null;
		
		int start = 0;
		int end = address.indexOf(':');
		
		head = address.substring(start, end);
		
		Log.w(LOGTAG, "getAddressType() get address head=" + head);
		if(head.equals(ProtocolTypes.TYPE_DROPBOX))
		{
			type = 4;
		}
		else if(head.equals(ProtocolTypes.TYPE_SSH))
		{
			type = 3;
		}
		else if(head.equals(ProtocolTypes.TYPE_HTTPS))
		{
			type = 2;
		}
		else if(head.equals(ProtocolTypes.TYPE_HTTP))
		{
			type = 1;
		}
		
		Log.w(LOGTAG, "getAddressType() get address type=" + type);
		
		return type;
	}
	
	public void listFilesApacheIvy(URL url)
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
	
	public void listFiles(URL url)
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ProtocolTypes
	{
		public static final String TYPE_HTTP = "http";
		public static final String TYPE_HTTPS = "https";
		public static final String TYPE_SSH = "ssh"; // ???necessary
		public static final String TYPE_DROPBOX = "dropbox";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class DropboxHandler
	{

		// dropbox
		public static DropboxAPI<AndroidAuthSession> dropbox = null;
		
		private static final String LOGTAG_1 = DropboxHandler.class.getName();
		
		private final static String APP_KEY = "z0n6paty2uwi3ru";
		private final static String APP_SECRET = "xrphn2nzodjnqmq";
		public final static AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
		
		public static boolean isFirstInit;
		public static boolean isAuthenFinished;
		public static boolean isTokensStored;
		
		public static DropboxAPI<AndroidAuthSession> createDropboxHandler(SharedPreferences sharedPref)
		{
			Log.w(LOGTAG_1, "createDropboxHandler() will create a dropbox handler");
			
			DropboxAPI<AndroidAuthSession> mDBApi;
			
			String key = sharedPref.getString(Utils.ConstantVars.KEY_DROPBOX_KEY, 
					Utils.ConstantVars.KEY_DROPBOX_KEY_DEF);
			String secret = sharedPref.getString(Utils.ConstantVars.KEY_DROPBOX_SECRET, 
					Utils.ConstantVars.KEY_DROPBOX_SECRET_DEF);

			if(key.equals(Utils.ConstantVars.KEY_DROPBOX_KEY_DEF) 
					|| secret.equals(Utils.ConstantVars.KEY_DROPBOX_SECRET_DEF))
			{
				Log.w(LOGTAG_1, "createDropboxHandler(), key and secret are unknown, " +
						"this app is not authenticated, will create a dropbox handler for the first time");
				isFirstInit = true;
				
				// initialization
				AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
				AndroidAuthSession authSession = new AndroidAuthSession(appKeys, ACCESS_TYPE);
				mDBApi = new DropboxAPI<AndroidAuthSession>(authSession);
				
				return mDBApi;
			}
			
			Log.w(LOGTAG_1, "createDropboxHandler(), key and secret are inserted, " +
					"this app is already authenticated, will create a dropbox handler from sharedprefs");
			
			isFirstInit = false;
			isAuthenFinished = true;
			isTokensStored = true;
			
			AppKeyPair appKeys = new AppKeyPair(key, secret);
			AndroidAuthSession authSession = new AndroidAuthSession(appKeys, ACCESS_TYPE);
			mDBApi = new DropboxAPI<AndroidAuthSession>(authSession);
			
			Log.w(LOGTAG_1, "createDropboxHandler(), created a dropbox handler from sharedprefs, " +
					"authen isSuccessed=" + mDBApi.getSession().authenticationSuccessful());
			
			return mDBApi;
			
//			// start authentication
//			mDBApi.getSession().startAuthentication(MyActivity.this);
//			
//			// finish authentication
//			protected void onResume() {
//			    super.onResume();
	//
//			    if (mDBApi.getSession().authenticationSuccessful()) {
//			        try {
//			            // Required to complete auth, sets the access token on the session
//			            mDBApi.getSession().finishAuthentication();
	//
//			            AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
//						// these tokens should be stored in shared preference
//			        } catch (IllegalStateException e) {
//			            Log.i("DbAuthLog", "Error authenticating", e);
//			        }
//			    }
//			}
			
		}
		
		public static boolean storeTokens(String key, String secret, Context context)
		{
			
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		   SharedPreferences.Editor editor = settings.edit();
		   editor.putString(Utils.ConstantVars.KEY_DROPBOX_KEY, key);
		   editor.putString(Utils.ConstantVars.KEY_DROPBOX_SECRET, secret);
		   if(editor.commit()) isTokensStored = true;
		   
		   Log.w(LOGTAG_1, "storeTokens() stored toakens with result=" + isTokensStored);
		   
		   return isTokensStored;
		}
		
		public static void listFilesInFolder(String folderName) throws DropboxException
		{
			Log.w(LOGTAG_1, "listFiles() will list files in " + folderName);
			
			if(folderName.equals("root"))
			{
				Entry entry = dropbox.metadata("/", 0, null, true, null);
				Log.w(LOGTAG_1, "listFiles() get filelist " + entry.contents.toString());
			}
		}
		
		public static void getFileEntry(String fileName) throws DropboxException
		{
			Log.w(LOGTAG_1, "getFileEntry() will get infos of file " + fileName);
			
//			if(fileName.equals("XXX"))
//			{
//				Entry existingEntry = dropbox.metadata("/", 0, null, true, null);
//				
//			}
		}
		
	}
	
}
