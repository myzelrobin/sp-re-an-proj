/**
 * 
 */
package com.srandroid.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.srandroid.main.TestActivitySessionDetails;
import com.srandroid.util.Utils;
import com.srandroid.util.Utils.ConstantVars;

/**
 *
 */
public class SrmDropboxHandler 
{
	private static final String LOGTAG = SrmDropboxHandler.class.getName();

	private Context context;
	private Context activity;

	private SrmNetworkHandler networkHandler;
	
	
	// dropbox
	public DropboxAPI<AndroidAuthSession> dropbox;
	
	
	private final static String DROPBOX_APPKEY = "z0n6paty2uwi3ru";
	private final static String DROPBOX_APPSECRET = "xrphn2nzodjnqmq";
	public final static AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	
	private static final int FILES_LIMIT = 1000;
	
	public boolean isLoggedIn;
	
	public static final String FOLDERROOT = "root";
	public static final String FOLDERROOTPATH = "/";
	
	
	
	
	/**
	 * 
	 */
	public SrmDropboxHandler(Context context, Activity activity) 
	{
		this.context = context;
		this.activity = activity;
		
		this.networkHandler = new SrmNetworkHandler(context, activity);
	}
	
	public void createDropboxAPI()
	{
		if(networkHandler.checkNetworkConnection())
		{
			Log.w(LOGTAG, "createDropboxAPI() will create a dropbox api object");
			
			AndroidAuthSession session = buildSession();
			dropbox = new DropboxAPI<AndroidAuthSession>(session);
			
			checkDropboxKeySetup();
		}
	}
	
	public boolean checkDropboxKeySetup() 
	{
		Log.w(LOGTAG, "checkDropboxKeySetup() will check dropbox keys");
		
        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + DROPBOX_APPKEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = context.getPackageManager();
        
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) 
        {
            Utils.toastDebuggingText(context, 
            		"URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            return false;
        }
        
        return true;
    }
	
	
	public String[] getAccessKeys() 
	{
		Log.w(LOGTAG, "getAccessKeys() will access keys from sharedprefs ");
		
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String key = prefs.getString(Utils.ConstantVars.KEY_DROPBOX_KEY, 
        		Utils.ConstantVars.KEY_DROPBOX_KEY_DEF);
        String secret = prefs.getString(Utils.ConstantVars.KEY_DROPBOX_SECRET, 
        		Utils.ConstantVars.KEY_DROPBOX_SECRET_DEF);
        if (key != Utils.ConstantVars.KEY_DROPBOX_KEY_DEF 
        		&& secret != Utils.ConstantVars.KEY_DROPBOX_SECRET_DEF) {
        	String[] accessKeys = new String[2];
        	accessKeys[0] = key;
        	accessKeys[1] = secret;
        	return accessKeys;
        } else {
        	return null;
        }
    }
	
	public boolean storeAccessKeys(String key, String secret)
	{
		Log.w(LOGTAG, "storeAccessKeys() will strore access keys in sharedprefs ");
		
		boolean isTokensStored = false;
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
	   SharedPreferences.Editor editor = settings.edit();
	   editor.putString(Utils.ConstantVars.KEY_DROPBOX_KEY, key);
	   editor.putString(Utils.ConstantVars.KEY_DROPBOX_SECRET, secret);
	   if(editor.commit()) isTokensStored = true;
	   
	   Log.w(LOGTAG, "storeTokens() stored toakens with result=" + isTokensStored);
	   
	   return isTokensStored;
	   // in sharedprefs, after every new authen, key and secret are different, why?
	}
	
	public boolean clearAccessKeys() 
	{
		Log.w(LOGTAG, "clearAccessKeys() will clear access keys in sharedprefs ");
		
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Utils.ConstantVars.KEY_DROPBOX_KEY, 
        		Utils.ConstantVars.KEY_DROPBOX_KEY_DEF);
        editor.putString(Utils.ConstantVars.KEY_DROPBOX_SECRET, 
        		Utils.ConstantVars.KEY_DROPBOX_SECRET_DEF);
        return editor.commit();
    }
	
	
	
	public boolean finishAuthen(DropboxAPI<AndroidAuthSession> dropbox)
	{
		AndroidAuthSession session = dropbox.getSession();

		Log.w(LOGTAG, "finishAuthen() will finish authen from an authen activity");
		
        if (session.authenticationSuccessful()) 
        {
            try 
            {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                storeAccessKeys(tokens.key, tokens.secret);
                setLoggedIn(session.isLinked());
            } 
            catch (IllegalStateException e) 
            {
                Log.w(LOGTAG, "finishAuthen() throws IllegalStateException=" 
                		+ e.getLocalizedMessage());
                Log.i(LOGTAG, "Error authenticating dropbox", e);
            }
        }
		return true;
	}
	
	
	public AndroidAuthSession buildSession() 
	{
		Log.w(LOGTAG, "buildSession() will build a new session");
		
        AppKeyPair appKeyPair = new AppKeyPair(DROPBOX_APPKEY, DROPBOX_APPSECRET);
        
        AndroidAuthSession session;

        String[] stored = getAccessKeys();
        
        if (stored != null) 
        {
        	// with accesss token
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
        	// without access token
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }
	
	public void logOut() 
	{
		Log.w(LOGTAG, "logOut() will logout current session");
		
        // Remove credentials from the session
        dropbox.getSession().unlink();

        // Clear our stored keys
        clearAccessKeys();
        
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    public void setLoggedIn(boolean loggedIn) 
    {
    	isLoggedIn = loggedIn;
    	if (loggedIn) 
    	{
    		
    	} 
    	else 
    	{

    	}
    	
    	Log.w(LOGTAG, "setLoggedIn() set isLoggedIn=" + isLoggedIn);
    }

    
    
    
    
	
	/**
	 * Class  
	 *
	 */
	public static class GetFileInfosTask extends AsyncTask<Void, Long, Boolean>
	{
		private final String LOGTAG_1 = LOGTAG + "$" + GetFileInfosTask.class.getSimpleName();
	
		private Context context;
		private Activity activity;
		private DropboxAPI<AndroidAuthSession> dropbox;
		private String filePath;
		
		private ArrayList<String> dirList;
		
		public GetFileInfosTask(Context context, 
				Activity activity, 
				DropboxAPI<AndroidAuthSession> dropbox,
				String filePath)
		{
			this.context = context;
			this.activity = activity;
			this.dropbox = dropbox;
			this.filePath = filePath;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			Log.w(LOGTAG_1, "doInBackground() starts ");
			
			boolean result = false;
			
			try {
				dirList = listFilesInFolder(dropbox, filePath);
			} catch (DropboxException e) {
				Log.w(LOGTAG_1, "doInBackground() throws DropboxException=" + e.getLocalizedMessage());
                Log.i(LOGTAG_1, "Error listing files in dropbox", e);
				e.printStackTrace();
			}
			
			if(dirList != null)
			{
				Log.w(LOGTAG_1, "doInBackground() get file list=" + dirList.toString());
				result = true;
			}
			
			return result;
		}
		
//		@Override
//	    protected void onProgressUpdate(Long... progress) {
//	        int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
//	        mDialog.setProgress(percent);
//	    }
		
		//displays the results of the AsyncTask
		@Override
        protected void onPostExecute(Boolean result) 
		{
			Log.w(LOGTAG_1, "onPostExecute() get result=" + result);
			// if(result == ?)
		}
		
		

		
		public ArrayList<String> listFilesInFolder(
				DropboxAPI<AndroidAuthSession> dropbox, String filePath) 
						throws DropboxException
		{
			Log.w(LOGTAG, "listFilesInFolder() will list files in " + filePath);
			
			ArrayList<Entry> entryList = null;
			ArrayList<String> dirList = null;
			Entry dirEntry = null;
			
			// metadata("/", FILENUMBERS, null, true, null)
			dirEntry = dropbox.metadata(filePath, FILES_LIMIT, null, true, null);
			
			if(dirEntry.isDir)
			{
				entryList = new ArrayList<Entry>();
		        dirList = new ArrayList<String>();
		        int i = 0;
		        
		        for (Entry ent: dirEntry.contents) 
		        {
		            entryList.add(i, ent);                   
		            //dir = new ArrayList<String>();
		            dirList.add(new String(entryList.get(i).path));
		            i++;
		        }
			}
			else Log.w(LOGTAG, "listFilesInFolder() finds " + filePath + " is not a folder!");
			
	        return dirList;
		}
		
		
		public void getSingleFileInfos(String filePath) throws DropboxException
		{
			Log.w(LOGTAG, "getFileEntry() will get infos of file " + filePath);
			
			
			
		}
		
		
		
		
	}
    
}
