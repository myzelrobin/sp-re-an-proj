/**
 * 
 */
package com.srandroid.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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

/**
 *
 */
public class SrmDropboxHandler 
{
	private static final String LOGTAG = SrmDropboxHandler.class.getName();

	private Context context;

	// dropbox
	public static DropboxAPI<AndroidAuthSession> dropbox;
	
	private final static String DROPBOX_AUTHENKEY = "z0n6paty2uwi3ru";
	private final static String DROPBOX_AUTHENSECRET = "xrphn2nzodjnqmq";
	public final static AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	
	public static boolean isFirstInit;
	public static boolean isAuthenFinished;
	
	private static final int FILES_LIMIT = 1000;
	
	
	/**
	 * 
	 */
	public SrmDropboxHandler(Context context) 
	{
		this.context = context;
	}
	
	
	
	
	public static void createDropboxHandler(SharedPreferences sharedPref)
	{
		Log.w(LOGTAG, "createDropboxHandler() will create a dropbox handler");
		
		AppKeyPair appKeys = new AppKeyPair(DROPBOX_AUTHENKEY, DROPBOX_AUTHENSECRET);
		AndroidAuthSession authSession = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		dropbox = new DropboxAPI<AndroidAuthSession>(authSession);
		
//			String key = sharedPref.getString(Utils.ConstantVars.KEY_DROPBOX_KEY, 
//					Utils.ConstantVars.KEY_DROPBOX_KEY_DEF);
//			String secret = sharedPref.getString(Utils.ConstantVars.KEY_DROPBOX_SECRET, 
//					Utils.ConstantVars.KEY_DROPBOX_SECRET_DEF);
//
//			if(key.equals(Utils.ConstantVars.KEY_DROPBOX_KEY_DEF) 
//					|| secret.equals(Utils.ConstantVars.KEY_DROPBOX_SECRET_DEF))
//			{
//				Log.w(LOGTAG_1, "createDropboxHandler(), key and secret are unknown, " +
//						"this app is not authenticated, will create a dropbox handler for the first time");
//				
//				// isFirstInit = true;
//				
//				AppKeyPair appKeys = new AppKeyPair(DROPBOX_AUTHENKEY, DROPBOX_AUTHENSECRET);
//				AndroidAuthSession authSession = new AndroidAuthSession(appKeys, ACCESS_TYPE);
//				mDBApi = new DropboxAPI<AndroidAuthSession>(authSession);
//			}
//			else
//			{
//
//				Log.w(LOGTAG_1, "createDropboxHandler(), key and secret are inserted, " +
//						"this app is already authenticated, will create a dropbox handler from sharedprefs");
//				
//				// isFirstInit = false;
//				
//				AppKeyPair appKeys = new AppKeyPair(key, secret);
//				AndroidAuthSession authSession = new AndroidAuthSession(appKeys, ACCESS_TYPE);
//				mDBApi = new DropboxAPI<AndroidAuthSession>(authSession);
//				
//				// String userID = mDBApi.getSession().finishAuthentication(); // this line makes error
//				
//				// isAuthenFinished = true;
//				// isTokensStored = true;
//				
//			}
		
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
	
	public static String[] listFilesInFolder(String folderName) throws DropboxException
	{
		Log.w(LOGTAG, "listFilesInFolder() will list files in " + folderName);
		
		String[] filenames = null;
		ArrayList<Entry> filesList = null;
		ArrayList<String> dirList = null;
		Entry dirEntry = null;
		
		if(folderName.equals("root"))
		{
			// metadata("/", FILENUMBERS, null, true, null)
			dirEntry = dropbox.metadata("/Apps/speechrecordermobile/", 0, null, true, null);
			
			filesList = new ArrayList<Entry>();
            dirList = new ArrayList<String>();
            int i = 0;
            
            
            for (Entry ent: dirEntry.contents) 
            {
                filesList.add(i, ent);                   
                //dir = new ArrayList<String>();
                dirList.add(new String(filesList.get(i).path));
                i++;
            }
            
		}
		filenames=dirList.toArray(new String[dirList.size()]);
        return filenames;
	}
	
	public static void getFileEntry(String fileName) throws DropboxException
	{
		Log.w(LOGTAG, "getFileEntry() will get infos of file " + fileName);
		
//			if(fileName.equals("XXX"))
//			{
//				Entry existingEntry = dropbox.metadata("/", 0, null, true, null);
//				
//			}
	}
	
	private boolean checkDropboxKeySetup(Context context) 
	{
        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + DROPBOX_AUTHENKEY;
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
	
	
	private String[] getAccessKeys(Context context) 
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
	
	public static boolean storeAccessKeys(String key, String secret, Context context)
	{
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
	
	
	private AndroidAuthSession buildSession(Context context) 
	{
		Log.w(LOGTAG, "buildSession() will build a new session");
		
        AppKeyPair appKeyPair = new AppKeyPair(DROPBOX_AUTHENKEY, DROPBOX_AUTHENSECRET);
        AndroidAuthSession session;

        String[] stored = getAccessKeys(context);
        if (stored != null) 
        {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }

}
