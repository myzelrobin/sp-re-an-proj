/**
 * 
 */
package com.srandroid.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.dropbox.client2.session.TokenPair;

/**
 *
 */
public class SrmDropboxHandler 
{
	private static final String LOGTAG = SrmDropboxHandler.class.getName();

	private Context context;

	// dropbox
	public DropboxAPI<AndroidAuthSession> dropbox;
	
	
	private final static String DROPBOX_AUTHENKEY = "z0n6paty2uwi3ru";
	private final static String DROPBOX_AUTHENSECRET = "xrphn2nzodjnqmq";
	public final static AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	
	private static final int FILES_LIMIT = 1000;
	
	public boolean isLoggedIn;
	
	public static final String FOLDERROOT = "root";
	public static final String FOLDERROOTPATH = "/Apps/speechrecordermobile/";
	
	
	
	
	/**
	 * 
	 */
	public SrmDropboxHandler(Context context) 
	{
		this.context = context;
	}
	
	public void createDropboxHandler(Context context)
	{
		Log.w(LOGTAG, "createDropboxHandler() will create a dropbox handler");
		
		AndroidAuthSession session = buildSession(context);
		dropbox = new DropboxAPI<AndroidAuthSession>(session);
		
		checkDropboxKeySetup(context);
	}
	
	public boolean checkDropboxKeySetup(Context context) 
	{
		Log.w(LOGTAG, "checkDropboxKeySetup() will check dropbox keys");
		
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
	
	
	public String[] getAccessKeys(Context context) 
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
	
	public boolean storeAccessKeys(String key, String secret, Context context)
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
	
	public boolean clearAccessKeys(Context context) 
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
	
	
	
	public boolean finishAuthen(Context context, 
			DropboxAPI<AndroidAuthSession> dropbox)
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
                storeAccessKeys(tokens.key, tokens.secret, context);
                setLoggedIn(session.isLinked());
            } 
            catch (IllegalStateException e) 
            {
                Log.w(LOGTAG, "finishAuthen() throws IllegalStateException=" 
                		+ e.getLocalizedMessage());
                Log.i(LOGTAG, "Error authenticating", e);
            }
        }
		return true;
	}
	
	
	public AndroidAuthSession buildSession(Context context) 
	{
		Log.w(LOGTAG, "buildSession() will build a new session");
		
        AppKeyPair appKeyPair = new AppKeyPair(DROPBOX_AUTHENKEY, DROPBOX_AUTHENSECRET);
        AndroidAuthSession session;

        String[] stored = getAccessKeys(context);
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
	
	public void logOut(Context contex) 
	{
		Log.w(LOGTAG, "logOut() will logout current session");
		
        // Remove credentials from the session
        dropbox.getSession().unlink();

        // Clear our stored keys
        clearAccessKeys(context);
        
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

    public String[] listFilesInFolder(String folderName) throws DropboxException
	{
		Log.w(LOGTAG, "listFilesInFolder() will list files in " + folderName);
		
		String[] filenames = null;
		ArrayList<Entry> filesList = null;
		ArrayList<String> dirList = null;
		Entry dirEntry = null;
		
		if(folderName.equals(FOLDERROOT))
		{
			// metadata("/", FILENUMBERS, null, true, null)
			dirEntry = dropbox.metadata(FOLDERROOTPATH, 0, null, true, null);
			
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
	
	
	public void getSingleFile(String fileName) throws DropboxException
	{
		Log.w(LOGTAG, "getFileEntry() will get infos of file " + fileName);
		
//			if(fileName.equals("XXX"))
//			{
//				Entry existingEntry = dropbox.metadata("/", 0, null, true, null);
//				
//			}
	}
    
}
