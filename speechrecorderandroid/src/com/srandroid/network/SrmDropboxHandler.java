/**
 * 
 */
package com.srandroid.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
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
	
	public boolean isAppAuthorized;
	
	public static final String FOLDERROOT = "root";
	public static final String FOLDER_ROOT_PATH = "/";
	public static final String FOLDER_SCRIPTS_PATH = "/scripts/";
	public static final String FOLDER_UPLOADS_PATH = "/uploads/";
	
	// files for test
	public static final String FDB_SCRIPT_PATH = "/scripts/script_text_exp_01.txt";
	public static final String FLOCAL_SCRIPT_PATH = 
			Utils.ConstantVars.DIR_EXT_RECFILES_PATH + "/script_example_upload.xml.txt";
	
	
	
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
			
			if(!isAppAuthorized)
			{
				if(isLoggedIn)	logOut();
				else dropbox.getSession().startAuthentication(context);
			}
			
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
        	
        	isAppAuthorized = true;
        	Log.w(LOGTAG, "getAccessKeys() finds app isAppAuthorized=" + isAppAuthorized);
        	
        	String[] accessKeys = new String[2];
        	accessKeys[0] = key;
        	accessKeys[1] = secret;
        	return accessKeys;
        } else {
        	isAppAuthorized = false;
        	Log.w(LOGTAG, "getAccessKeys() finds app isAppAuthorized=" + isAppAuthorized);
        	
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
	
	
	
	public void finishAuthen(AndroidAuthSession session)
	{
		Log.w(LOGTAG, "finishAuthen() will finish dropbox authen from an authen activity");
		
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
	
	
	public AndroidAuthSession buildSession() 
	{
		Log.w(LOGTAG, "buildSession() will build a new session");
		
        AppKeyPair appKeyPair = new AppKeyPair(DROPBOX_APPKEY, DROPBOX_APPSECRET);
        
        AndroidAuthSession session;

        String[] storedAuthenKeyPairs = getAccessKeys();
        
        if (storedAuthenKeyPairs != null) 
        {
        	// with accesss token
            AccessTokenPair accessToken = new AccessTokenPair(
            		storedAuthenKeyPairs[0], 
            		storedAuthenKeyPairs[1]);
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
	 * need a View param for showing the script files in the View object
	 * need a Entry param for sending the infos to View
	 */
	public static class GetFileInfosTask extends AsyncTask<Void, Long, Boolean>
	{
		private final String LOGTAG = GetFileInfosTask.class.getName();
	
		private Context context;
		private Activity activity;
		private DropboxAPI<AndroidAuthSession> dropbox;
		private String filePath;
		
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
			Log.w(LOGTAG, " get file infos doInBackground() starts ");
			
			boolean result = false;
			
			Entry entry;
			
			
			try 
			{
				entry = getFileInfos(dropbox, filePath);
				
				if( !entry.path.equals(null) ) result = true;
			} 
			catch (DropboxException e) 
			{
				Log.w(LOGTAG, "doInBackground() throws DropboxException=" + e.getLocalizedMessage());
                Log.i(LOGTAG, "Error listing files in dropbox", e);
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
			Log.w(LOGTAG, "onPostExecute() get result=" + result);
			// if(result == ?)
		}
		
		

		
		private Entry getFileInfos(
				DropboxAPI<AndroidAuthSession> dropbox, String filePath) 
						throws DropboxException
		{
			Log.w(LOGTAG, "getFileInfos() will list files in " + filePath);
			
			Entry fileEntry = null;
			ArrayList<Entry> entryList = null;
			ArrayList<String> filesList = null;
			
			// metadata("/", FILENUMBERS, null, true, null)
			fileEntry = dropbox.metadata(filePath, FILES_LIMIT, null, true, null);
			
			if(fileEntry.isDir)
			{
				Log.w(LOGTAG, "getFileInfos() finds " + filePath + " is a FOLDER, " +
						"will get child files infos in this folder!");
				
				entryList = new ArrayList<Entry>();
		        filesList = new ArrayList<String>();
		        int i = 0;
		        
		        for (Entry ent: fileEntry.contents) 
		        {
		            entryList.add(i, ent);                   
		            //dir = new ArrayList<String>();
		            filesList.add(new String(entryList.get(i).path));
		            i++;
		        }
		        
		        Log.w(LOGTAG, "getFileInfos() get child files list=" + filesList.toString());
			}
			else
			{
				Log.w(LOGTAG, "getFileInfos() finds " + filePath + " is a FILE," +
						" will get infos filename=" + fileEntry.fileName() + 
						", filesize=" + fileEntry.size +
						", filepath=" + fileEntry.path);
			}
			
	        return fileEntry;
		}
		
		
		
	}
	
	/**
	 * Class  
	 * need a View parameter for find record file path, if null for script path
	 * use a SessionItem better?
	 */
	public static class UploadFileTask extends AsyncTask<Void, Long, Boolean>
	{
		private final String LOGTAG = UploadFileTask.class.getName();
	
		private Context context;
		private Activity activity;
		private DropboxAPI<AndroidAuthSession> dropbox;
		private String locFilePath;
		
		private UploadRequest uploadReq;
		
		
		public UploadFileTask(
				Context context, 
				Activity activity, 
				DropboxAPI<AndroidAuthSession> dropbox,
				String locFilePath)
		{
			this.context = context;
			this.activity = activity;
			this.dropbox = dropbox;
			this.locFilePath = locFilePath;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			Log.w(LOGTAG, " upload file doInBackground() starts ");
			
			boolean result = false;
			
			Entry entry;
			
			try 
			{
				File file = new File(locFilePath);
				
				FileInputStream fis = new FileInputStream(file);

				entry = uploadFileToDropbox(dropbox, fis, file, null);
				
				if( !entry.path.equals(null) ) result = true;
			} 
			catch (FileNotFoundException e) 
			{
				Log.w(LOGTAG, "doInBackground() throws FileNotFoundException=" + e.getLocalizedMessage());
                Log.i(LOGTAG, "Error uploading files into dropbox", e);
			}
			catch (DropboxException e) 
			{
				Log.w(LOGTAG, "doInBackground() throws DropboxException=" + e.getLocalizedMessage());
                Log.i(LOGTAG, "Error uploading files into dropbox", e);
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
			Log.w(LOGTAG, "onPostExecute() get result=" + result);
			// if(result == ?)
		}
		
		private Entry uploadFileToDropbox(
				DropboxAPI<AndroidAuthSession> dropbox, 
				FileInputStream fis, 
				File file,
				ProgressListener progListener) 
						throws DropboxException
		{
			Log.w(LOGTAG, "uploadFileToDropbox() will upload a file into dropbox, file=" +
					file.getAbsolutePath());
			
			Entry uploadedFileEntry = null;
			
			String filePath = file.getAbsolutePath();
			long fileSize = file.length();
			
			uploadReq = dropbox.putFileOverwriteRequest(
					filePath, 
					fis, 
					fileSize, 
					progListener);
			
			if(uploadReq != null) uploadedFileEntry = uploadReq.upload();
			
			return uploadedFileEntry;
		}
		
	}
	
	
//	/**
//	 * Class  
//	 *
//	 */
//	public static class DownloadFileTask extends AsyncTask<Void, Long, Boolean>
//	{
//		private final String LOGTAG = DownloadFileTask.class.getName();
//	
//		private Context context;
//		private Activity activity;
//		private DropboxAPI<AndroidAuthSession> dropbox;
//		private String filePath;
//		
//		private ArrayList<String> dirList;
//		
//		public DownloadFileTask(
//				Context context, 
//				Activity activity, 
//				DropboxAPI<AndroidAuthSession> dropbox,
//				String filePath)
//		{
//			this.context = context;
//			this.activity = activity;
//			this.dropbox = dropbox;
//			this.filePath = filePath;
//		}
//		
//		@Override
//		protected Boolean doInBackground(Void... params) 
//		{
//			Log.w(LOGTAG, " get file infos doInBackground() starts ");
//			
//			boolean result = false;
//			
//			try 
//			{
//				
//			} 
//			catch (DropboxException e) 
//			{
//				Log.w(LOGTAG, "doInBackground() throws DropboxException=" + e.getLocalizedMessage());
//                Log.i(LOGTAG, "Error listing files in dropbox", e);
//			}
//			
//			if(dirList != null)
//			{
//				Log.w(LOGTAG, "doInBackground() get file list=" + dirList.toString());
//				result = true;
//			}
//			
//			return result;
//		}
//		
////		@Override
////	    protected void onProgressUpdate(Long... progress) {
////	        int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
////	        mDialog.setProgress(percent);
////	    }
//		
//		//displays the results of the AsyncTask
//		@Override
//        protected void onPostExecute(Boolean result) 
//		{
//			Log.w(LOGTAG, "onPostExecute() get result=" + result);
//			// if(result == ?)
//		}
//		
//		
//	}
	
    
}