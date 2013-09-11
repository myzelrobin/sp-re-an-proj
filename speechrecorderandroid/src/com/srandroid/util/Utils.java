package com.srandroid.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.srandroid.speechrecorder.R;
import com.srandroid.database.TableRecords.RecordItem;
import com.srandroid.database.TableScripts.ScriptItem;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings.Secure;



/**
 * 
 */
public class Utils
{
	// log
	public static final String LOGTAG = Utils.class.getName();
	
	// class contains constant variables 
	public static class ConstantVars
	{
		// application context
		public static Context appContext = null;
		
		
		
		
		// drawer item array
		public static String[] arrayDrawerItems = null;
		public static final int POS_SESSIONS = 0;
		public static final int POS_SCRIPTS = 1;
		public static final int POS_SPEAKERS = 2;
		
		public static int selectedItemIndex = 0;
		
		
		
		
		
		
		
		// SharedPreferece key and default values
		public static final String KEY_PREFSCREEN_RECVALUE = "prefscreen_recvalue";

		public static final String KEY_LANGUAGE = "lang";
		public static final String KEY_LANGUAGE_DEF = "en";
		public static String LANGUAGE = KEY_LANGUAGE_DEF;
		
		public static final String KEY_MICVOL = "mic_vol";
		public static final String KEY_MICVOL_DEF = "-1";
		public static String MICVOL = KEY_MICVOL_DEF;
		
		public static final String KEY_SAMPLE_RATE = "sample_rate";
		public static final String KEY_SAMPLE_RATE_DEF = "22050";
		public static String SAMPLE_RATE = KEY_SAMPLE_RATE_DEF;
		
		public static final String KEY_CHANNELS = "channels";
		public static final String KEY_CHANNELS_DEF = "2";
		public static String CHANNELS = KEY_CHANNELS_DEF;
		
		public static final String KEY_OVERWRITE = "overwrite"; // boolean value, default true
		public static boolean ALLOW_OVERWRITE = true;
		
		public static final String KEY_OVERWRITE_WARNING = "overwrite_warning"; // boolean value, default true
		public static boolean ALLOW_OVERWRITE_WARNING = true;
		
		
		// invisible, user can not change these preferences
		public static final String KEY_BYTE_ORDER = "byte_order";
		public static final String KEY_BYTE_ORDER_DEF = "Little Endian"; // Little Endian, Big Endian, Unknown
		
		public static final String KEY_ENCODING = "encoding";
		public static final String KEY_ENCODING_DEF = "PCM_16BIT"; // PCM_16BIT, PCM_8BIT
		
		public static final String KEY_SAMPLE_SIZE = "sample_size";
		public static final String KEY_SAMPLE_SIZE_DEF = "2"; // 0, 1, 2, 3, 4
		
		public static final String KEY_RRCORDING_TARGET = "recording_target";
		public static final String KEY_RECORDING_TARGET_DEF = "direct"; // direct, temp_raw_file
		
		public static final String KEY_AUTOPROGRESS = "auto_progress"; 
		public static boolean ALLOW_AUTOPROGRESS = true;
		// boolean, default true, autoprogress to next unrecorded item 
		
		public static final String KEY_RESET_PEAK = "reset_peak";
		public static boolean ALLOW_RESET_PEAK= true;
		// boolean, default true, reset peak at start of recording
		
		public static final String KEY_PRERECDELAY = "prerecdelay";
		public static final String KEY_PRERECDELAY_DEF = "1000"; // float number, millisecond
		
		public static final String KEY_POSTRECDELAY = "postrecdelay";
		public static final String KEY_POSTRECDELAY_DEF = "1000"; // float number, millisecond
		
		public static final String KEY_RECORDS_PATH = "records_path";
		public static final String KEY_RECORDS_PATH_DEF = "unknown";
		
		public static final String KEY_CITYNAME = "cityname";
		public static final String KEY_CITYNAME_DEF = "city unknown";
		
		public static final String KEY_GPS_DATA = "gps_data";
		public static final String KEY_GPS_DATA_DEF = "location unknown";
		
		public static final String KEY_DEVICE_DATA = "device_data";
		public static final String KEY_DEVICE_DATA_DEF = "device data unknown";
		
		
		// device informations
		public static String DEVICE_DATA = "device data unknow";
		
		// location infos
		public static String GPS_DATA = "gps data unknown";
		public static String CITYNAME = "city name unknown";
		
		public static SrmLocationListener locListener;
		
		
		// path variables
		public static String APP_DIR_INT_PATH; // app internal root folder
		public static String APP_FILES_DIR_INT_PATH; // app internal folder "files"
		public static String APP_DIR_EXT_PATH; // app external root foler
		public static String APP_FILES_DIR_EXT_PATH; // app external foler "files"
		public static String REC_FILES_DIR_EXT_PATH; // folder "records" in external "files"
		public static String TEST_MIC_DIR_EXT_PATH; // folder "test_mic" in external "records"
		public static String SCRIPTS_DIR_EXT_PATH; // folder "scripts" in external files 
		
		
		
		// layout values
		// item in Fragment in ActivityMain
		public static final int ITEMWIDTH = 460;
		public static final int ITEMHEIGHT = 160;
		public static int screenWidth = 0;
		public static int screenHeight = 0;
		public static int marginItemBGInVerticalMode = 0;
		public static int marginItemBGInHorizontalMode = 0;
		public static int widthLLWrapItem = 0;
		public static int heightLLWrapItem = 0;
		public static int columnCount = 0;
		
		
		// objects for recording
		public static String speakerItemIdForNewSession = null;
		public static String scriptItemIdForNewSession = null;
		public static String sessionItemIdForNewSession = null;
		public static ScriptItem scriptItemForNewSession = null;
		public static List<RecordItem> recordItemListForNewSession = null;
		
		public static String exampleScriptFilepath = null;
		
		// database
		public static final String TESTDB_FOLDER_PATH = 
				"/mnt/sdcard/srandroid_testfolder";
		//public  static DBAccessor dbAccessor;
		
		// network
		public static final String SERVER_ADDRESS = "http://www.dropbox.com/";
		public static final String SERVER_USERNAME = "null";
		public static final String SERVER_PASSWORD = "null";
		
		
		
		
		
		public static boolean isPreStartInitialized = false;
		
		/**
		 * @return the lANGUAGE
		 */
		public static String getLANGUAGE() {
			return LANGUAGE;
		}
		/**
		 * @param lANGUAGE the lANGUAGE to set
		 */
		public static void setLANGUAGE(String lANGUAGE) {
			LANGUAGE = lANGUAGE;
		}
		/**
		 * @return the mICVOL
		 */
		public static String getMICVOL() {
			return MICVOL;
		}
		/**
		 * @param mICVOL the mICVOL to set
		 */
		public static void setMICVOL(String mICVOL) {
			MICVOL = mICVOL;
		}
		/**
		 * @return the sAMPLE_RATE
		 */
		public static String getSAMPLE_RATE() {
			return SAMPLE_RATE;
		}
		/**
		 * @param sAMPLE_RATE the sAMPLE_RATE to set
		 */
		public static void setSAMPLE_RATE(String sAMPLE_RATE) {
			SAMPLE_RATE = sAMPLE_RATE;
		}
		/**
		 * @return the cHANNELS
		 */
		public static String getCHANNELS() {
			return CHANNELS;
		}
		/**
		 * @param cHANNELS the cHANNELS to set
		 */
		public static void setCHANNELS(String cHANNELS) {
			CHANNELS = cHANNELS;
		}
		/**
		 * @return the aLLOW_OVERWRITE
		 */
		public static boolean isALLOW_OVERWRITE() {
			return ALLOW_OVERWRITE;
		}
		/**
		 * @param aLLOW_OVERWRITE the aLLOW_OVERWRITE to set
		 */
		public static void setALLOW_OVERWRITE(boolean aLLOW_OVERWRITE) {
			ALLOW_OVERWRITE = aLLOW_OVERWRITE;
		}
		/**
		 * @return the aLLOW_OVERWRITE_WARNING
		 */
		public static boolean isALLOW_OVERWRITE_WARNING() {
			return ALLOW_OVERWRITE_WARNING;
		}
		/**
		 * @param aLLOW_OVERWRITE_WARNING the aLLOW_OVERWRITE_WARNING to set
		 */
		public static void setALLOW_OVERWRITE_WARNING(boolean aLLOW_OVERWRITE_WARNING) {
			ALLOW_OVERWRITE_WARNING = aLLOW_OVERWRITE_WARNING;
		}
		
		public static void initializeApp(Context context)
		{
			Log.w(LOGTAG, "initializeApp(): will initialize data before app starts");
			
			if(isPreStartInitialized) return;  // is initialized
			
			appContext = context;
			
			// drawer item array
			arrayDrawerItems = context.getResources().getStringArray(R.array.array_drawer_items);

			// database
			//dbAccessor = new DBAccessor(context);
			//dbAccessor.getWritableDatabase();
			
			// get application folder path (/data/data/APP_PACKAGE/)
			try {
				APP_DIR_INT_PATH = getAppInternalDir(context);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.w(LOGTAG, "APP_DIR_INT=" + APP_DIR_INT_PATH);
			
			// get files folder path (/data/data/APP_PACKAGE/files)
			try {
				APP_FILES_DIR_INT_PATH = getAppInternalFilesDir(context);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.w(LOGTAG, "APP_FILES_DIR_INT=" + APP_FILES_DIR_INT_PATH);
			
			// get app folder path in sdcard(/mnt/sdcard/Android/APP_PACKAGE/)
			try {
				APP_DIR_EXT_PATH = getAppExternalDir(context);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.w(LOGTAG, "APP_DIR_EXT=" + APP_DIR_EXT_PATH);
			
			// make folder path in sdcard(/mnt/sdcard/Android/APP_PACKAGE/records/)
			REC_FILES_DIR_EXT_PATH = makeDir(APP_DIR_EXT_PATH, "records");
			Log.w(LOGTAG, "REC_FILES_DIR_EXT=" + REC_FILES_DIR_EXT_PATH);
			
			// make folder path in sdcard(/mnt/sdcard/Android/APP_PACKAGE/records/test)
			TEST_MIC_DIR_EXT_PATH = makeDir(REC_FILES_DIR_EXT_PATH, "test");
			Log.w(LOGTAG, "REC_TEST_DIR_EXT=" + TEST_MIC_DIR_EXT_PATH);
			
			// make folder path in sdcard()
			SCRIPTS_DIR_EXT_PATH = makeDir(APP_DIR_EXT_PATH, "scripts");
			Log.w(LOGTAG, "SCRIPTS_DIR_EXT_PATH=" + SCRIPTS_DIR_EXT_PATH);
			
			getScreenSize(context);
			setLayoutValuesInVerticalMode();
			setLayoutValuesInHorizontalMode();
			
			// device info
			getDeviceId(context);
			
			// location listener
			initLocationListener(context);
			
		    isPreStartInitialized = true;
			Log.w(LOGTAG, "initializeApp(): finished initializing data before app starts");
		}
	}
	
	public static class UIutils
	{
		public static final boolean canToastDebugText = true;
		public static final boolean canToastTextToUser = true;
		
		public static AlertDialog createSimpleAlertDialog(
				Context context, 
				String title, 
				String message,
				String buttonText)
		{
			AlertDialog.Builder alertDialogBuilder = 
					new AlertDialog.Builder(context);
	 
				// set title
				alertDialogBuilder.setTitle(title);
	 
				// set dialog message
				alertDialogBuilder
					.setMessage(message)
					.setCancelable(false)
					.setNeutralButton(buttonText,
				            new DialogInterface.OnClickListener() 
							{
						        public void onClick(DialogInterface dialog, int id) 
						        {
						            dialog.cancel();
						        }
							});
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
					
					return alertDialog;
					// show it
					// alertDialog.show();
		}
		
		// Toast some text for debugging
		public static void toastDebuggingText(Context context, String s)
		{
			if(Utils.UIutils.canToastDebugText) 
				Toast.makeText(context, s, Toast.LENGTH_LONG).show();
		}
		
		public static void toastTextToUser(Context context, String s)
		{
			if(Utils.UIutils.canToastTextToUser) 
				Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public static void initSharedPreference(SharedPreferences sharedPreferences) 
	{
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		editor.putString(Utils.ConstantVars.KEY_LANGUAGE, Utils.ConstantVars.KEY_LANGUAGE_DEF);
        editor.commit(); 
		
		editor.putString(Utils.ConstantVars.KEY_MICVOL, Utils.ConstantVars.KEY_MICVOL_DEF);
        editor.commit(); 
		
		editor.putString(Utils.ConstantVars.KEY_SAMPLE_RATE, Utils.ConstantVars.KEY_SAMPLE_RATE_DEF);
        editor.commit(); 
		
		editor.putString(Utils.ConstantVars.KEY_CHANNELS, Utils.ConstantVars.KEY_CHANNELS_DEF);
        editor.commit(); 
		
		editor.putBoolean(Utils.ConstantVars.KEY_OVERWRITE, Utils.ConstantVars.ALLOW_OVERWRITE);
        editor.commit(); 
		
		editor.putBoolean(Utils.ConstantVars.KEY_OVERWRITE_WARNING, Utils.ConstantVars.ALLOW_OVERWRITE_WARNING);
        editor.commit(); 
		
		editor.putString(Utils.ConstantVars.KEY_BYTE_ORDER, Utils.ConstantVars.KEY_BYTE_ORDER_DEF);
        editor.commit(); 
		
		editor.putString(Utils.ConstantVars.KEY_ENCODING, Utils.ConstantVars.KEY_ENCODING_DEF);
        editor.commit(); 
		
		editor.putString(Utils.ConstantVars.KEY_SAMPLE_SIZE, Utils.ConstantVars.KEY_SAMPLE_SIZE_DEF);
        editor.commit(); 
		
		editor.putString(Utils.ConstantVars.KEY_RRCORDING_TARGET, Utils.ConstantVars.KEY_RECORDING_TARGET_DEF);
        editor.commit(); 
		
		editor.putBoolean(Utils.ConstantVars.KEY_AUTOPROGRESS, Utils.ConstantVars.ALLOW_AUTOPROGRESS);
        editor.commit(); 
		
		editor.putBoolean(Utils.ConstantVars.KEY_RESET_PEAK, Utils.ConstantVars.ALLOW_RESET_PEAK);
        editor.commit(); 
        
		editor.putString(Utils.ConstantVars.KEY_PRERECDELAY, Utils.ConstantVars.KEY_PRERECDELAY_DEF);
        editor.commit(); 
		
		editor.putString(Utils.ConstantVars.KEY_POSTRECDELAY, Utils.ConstantVars.KEY_POSTRECDELAY_DEF);
        editor.commit(); 
		
		editor.putString(Utils.ConstantVars.KEY_RECORDS_PATH, Utils.ConstantVars.KEY_RECORDS_PATH_DEF);
        editor.commit(); 
        
        editor.putString(Utils.ConstantVars.KEY_DEVICE_DATA, Utils.ConstantVars.DEVICE_DATA);
        editor.commit(); 
		
        editor.putString(Utils.ConstantVars.KEY_GPS_DATA, Utils.ConstantVars.GPS_DATA);
        editor.commit(); 
        
        editor.putString(Utils.ConstantVars.KEY_CITYNAME, Utils.ConstantVars.CITYNAME);
        editor.commit(); 
	}
	
	public static void updatePreference(SharedPreferences sharedPreferences, 
			String key, String value)
	{ 
		Log.w(LOGTAG, "updatePreference() will update key=" + key 
				+ " from oldvalue=" + sharedPreferences.getString(key, null)
				+ " to newvalue=" + value);
		
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value).commit(); 
	}
	
	public static String retirevePreference(SharedPreferences sharedPreferences, 
			String key)
	{ 
		Log.w(LOGTAG, "retirevePreference() will get value with key=" + key);
		
		return sharedPreferences.getString(key, "unable to get value!"); 
	}
	
	public static void updateGlobalVarsForNewSession()
	{
		if((ConstantVars.scriptItemIdForNewSession != null)
				&& (ConstantVars.speakerItemIdForNewSession != null))
		{
			ConstantVars.scriptItemIdForNewSession = null;
			ConstantVars.speakerItemIdForNewSession = null;
		}
		
		ConstantVars.sessionItemIdForNewSession = null;
	}
	
	public static boolean checkItemsForNewSession(Activity activity)
	{
		if(ConstantVars.scriptItemIdForNewSession == null)
		{
			toastTextToUser(activity.getApplicationContext(), "No Script, choose a new Script!");
			// choose a script 
			ConstantVars.selectedItemIndex = ConstantVars.POS_SCRIPTS;
			return false;
		}
		if(ConstantVars.speakerItemIdForNewSession == null)
		{
			toastTextToUser(activity.getApplicationContext(), "No Speaker, choose a new Speaker!");
			// choose a script 
			ConstantVars.selectedItemIndex = ConstantVars.POS_SPEAKERS;
			return false;
		}
		return true;
	}
	
	public static void copyScriptFilesToAppExtFolder(String fileName, AssetManager assetsManager)
	{
		Log.w(LOGTAG, "copyScriptFilesToAppExtFolder()" 
				+ "will copy script " + fileName + " to " + Utils.ConstantVars.SCRIPTS_DIR_EXT_PATH);
		
		// better  not hard code storage directory . use Environment.getExternalStorageDirectory()
		String destFilePath = Utils.ConstantVars.SCRIPTS_DIR_EXT_PATH + File.separator + fileName;
		try 
		{

	        File destFile = new File(destFilePath);
	        if(!destFile.exists())
	        {
	        	InputStream in = assetsManager.open(fileName);
		        OutputStream out = new FileOutputStream(destFile);

		        byte[] buf = new byte[1024];
		        int len;
		        while ((len = in.read(buf)) > 0) {
		            out.write(buf, 0, len);
		        }
		        in.close();
		        out.close();
		        
		        Log.w(LOGTAG, "copyScriptFilesToAppExtFolder()" 
						+ "finished copying script " + fileName 
						+ " to " + Utils.ConstantVars.SCRIPTS_DIR_EXT_PATH);
		       
	        }
	        
	        
	        Utils.ConstantVars.exampleScriptFilepath = destFile.getAbsolutePath();
	        
	        Log.w(LOGTAG, "copyScriptFilesToAppExtFolder()" 
					+ " file exists " + destFile.getAbsolutePath()) ;
	        
	    } catch (FileNotFoundException ex) {
	    	Log.w(LOGTAG, "copyScriptFilesToAppExtFolder() error: " 
	    			+ ex.getMessage() + " in " 
	    			+ Utils.ConstantVars.SCRIPTS_DIR_EXT_PATH);
	    } catch (IOException e) {
	    	Log.w(LOGTAG, "copyScriptFilesToAppExtFolder() error:" 
	    			+ e.getMessage());
	    }
	}
	
	public static void prepareItemsForNewSessions()
	{
		ScriptItem scriptItem = new ScriptItem();
		scriptItem.databaseName = "SpeechRecorder Demonstration";
		scriptItem.scriptName = "SpeechRecorder Sample Recording Script";
		scriptItem.scriptAuthor = "Chr. Draxler, Phonetics Institute, University of Munich";
		scriptItem.EmailAuthor = "draxler@phonetik.uni-muenchen.de";
		scriptItem.serverId = "unknown";
		scriptItem.idInTable = Utils.ConstantVars.scriptItemIdForNewSession;
		scriptItem.filepath = Utils.ConstantVars.exampleScriptFilepath;
		Utils.ConstantVars.scriptItemForNewSession = scriptItem;
		
		Log.w(LOGTAG, "prepareItemsForNewSessions()" 
				+ " added new scriptItemForNewSession") ;
		
		
		List<RecordItem> recItemsList = new ArrayList<RecordItem> ();
		
		RecordItem recordItem1 = new RecordItem();
		recordItem1.mode = "manual";
		recordItem1.sectionname = "Introduction";
		recordItem1.order = "sequential";
		recordItem1.promptphase = "idle";
		recordItem1.speakerdisplay = "false";
		recordItem1.itemcode = "demo_000";
		recordItem1.prerecdelay = "2000";
		recordItem1.postrecdelay = "500";
		recordItem1.recduration = "20000";
		recordItem1.recinstructions = "Welcome to the SpeechRecorder Demo Script.\nHere you'll find the instructions...";
		recordItem1.recprompt = "and here is the prompt: a text to read, a question to answer, a picture to describe, or something completely different.";
		recordItem1.reccomment = "This is a comment";
		recItemsList.add(recordItem1);
		
		RecordItem recordItem2 = new RecordItem();
		recordItem2.mode = "manual";
		recordItem2.sectionname = "Introduction";
		recordItem2.order = "sequential";
		recordItem2.promptphase = "idle";
		recordItem2.speakerdisplay = "false";
		recordItem2.itemcode = "demo_001";
		recordItem2.prerecdelay = "2000";
		recordItem2.postrecdelay = "500";
		recordItem2.recduration = "20000";
		recordItem2.recinstructions = "Please read the digits";
		recordItem2.recprompt = "2 7 4 1 6 8 3 9 5 0";
		recordItem2.reccomment = "This is a comment";
		recItemsList.add(recordItem2);
		
		RecordItem recordItem3 = new RecordItem();
		recordItem3.mode = "manual";
		recordItem3.sectionname = "Introduction";
		recordItem3.order = "sequential";
		recordItem3.promptphase = "idle";
		recordItem3.speakerdisplay = "false";
		recordItem3.itemcode = "demo_002";
		recordItem3.prerecdelay = "2000";
		recordItem3.postrecdelay = "500";
		recordItem3.recduration = "20000";
		recordItem3.recinstructions = "Please read the bi-lingual sentence";
		recordItem3.recprompt = "And then he said to me: 'もう一度ダイヤルします'";
		recordItem3.reccomment = "Expected pronunciation: mou ichido DIAL shimasu";
		recItemsList.add(recordItem3);
		
		RecordItem recordItem4 = new RecordItem();
		recordItem4.mode = "manual";
		recordItem4.sectionname = "Introduction";
		recordItem4.order = "sequential";
		recordItem4.promptphase = "idle";
		recordItem4.speakerdisplay = "false";
		recordItem4.itemcode = "demo_003";
		recordItem4.prerecdelay = "2000";
		recordItem4.postrecdelay = "500";
		recordItem4.recduration = "20000";
		recordItem4.recinstructions = "Répondez s'il vous plaît";
		recordItem4.recprompt = "Qu'est-ce que vous avez fait hier soir?";
		recordItem4.reccomment = "Answer expected in French; one minute recording time";
		recItemsList.add(recordItem4);

		RecordItem recordItem5 = new RecordItem();
		recordItem5.mode = "manual";
		recordItem5.sectionname = "Introduction";
		recordItem5.order = "sequential";
		recordItem5.promptphase = "idle";
		recordItem5.speakerdisplay = "false";
		recordItem5.itemcode = "demo_004";
		recordItem5.prerecdelay = "2000";
		recordItem5.postrecdelay = "500";
		recordItem5.recduration = "20000";
		recordItem5.recinstructions = "Answer the question";
		recordItem5.recprompt = "How is the weather today?";
		recordItem5.reccomment = "Expect an answer about weather";
		recItemsList.add(recordItem5);
		
		RecordItem recordItem6 = new RecordItem();
		recordItem6.mode = "manual";
		recordItem6.sectionname = "Introduction";
		recordItem6.order = "sequential";
		recordItem6.promptphase = "idle";
		recordItem6.speakerdisplay = "false";
		recordItem6.itemcode = "demo_005";
		recordItem6.prerecdelay = "2000";
		recordItem6.postrecdelay = "500";
		recordItem6.recduration = "20000";
		recordItem6.recinstructions = "Describe the picture";
		recordItem6.recprompt = Utils.ConstantVars.SCRIPTS_DIR_EXT_PATH 
				+ File.separator + "example_boy.jpg";
		recordItem6.reccomment = "Expect some descriptions";
		recordItem6.itemType = RecordItem.TYPE_IMAGE;
		recItemsList.add(recordItem6);
		
		RecordItem recordItem7 = new RecordItem();
		recordItem7.mode = "manual";
		recordItem7.sectionname = "Introduction";
		recordItem7.order = "sequential";
		recordItem7.promptphase = "idle";
		recordItem7.speakerdisplay = "false";
		recordItem7.itemcode = "demo_006";
		recordItem7.prerecdelay = "2000";
		recordItem7.postrecdelay = "500";
		recordItem7.recduration = "20000";
		recordItem7.recinstructions = "Describe the picture";
		recordItem7.recprompt = Utils.ConstantVars.SCRIPTS_DIR_EXT_PATH 
				+ File.separator + "example_food.jpg";
		recordItem7.reccomment = "Expect some descriptions";
		recordItem7.itemType = RecordItem.TYPE_IMAGE;
		recItemsList.add(recordItem7);
		
		Utils.ConstantVars.recordItemListForNewSession = recItemsList;
		Log.w(LOGTAG, "prepareItemsForNewSessions()" 
				+ " added new recordItemListForNewSession") ;
	}
	
	
//	public static void parseScript(
//			String scriptFilepath, 
//			ScriptItem scriptItem, 
//			List<RecordItem> recordItemList)
//	{
//		Log.w(LOGTAG, "parseScript() will parse file!");
//		
//		ScriptXMLParser scriptParser = new ScriptXMLParser();
//		
//		try {
//			File xmlFile = new File(scriptFilepath); 
//			
//			InputStream scriptFile = new FileInputStream(xmlFile);
//			
//			scriptItem = scriptParser.parseScriptMetadata(scriptFile);
//			
//			scriptFile.close();
//			
//			InputStream scriptFile2 = new FileInputStream(scriptFilepath);
//			
//			recordItemList = scriptParser.parseScriptRecordings(scriptFile2);
//			
//			scriptFile.close();
//			
//			Log.w(LOGTAG, "parseScript() finished parsing, "
//					+ "\nscriptItem databasename =" + scriptItem.getDatabaseName()
//					+ "\nscriptItem AuthorEmail =" + scriptItem.getEmailAuthor()
//					+ "\nList RecordItems count=" + recordItemList.size()
//					+ "\nList RecordItem test id=2 getIntro=" + recordItemList.get(1).getRecinstructions()
//					+ "\nEND.");
//			
//			
//		} catch (FileNotFoundException e) {
//			Log.w(LOGTAG, "parseScript() error:" 
//					+ e.getMessage());
//		} catch (XmlPullParserException e) {
//			Log.w(LOGTAG, "parseScript() error:" 
//					+ e.getMessage());
//		} catch (IOException e) {
//			Log.w(LOGTAG, "parseScript() error:" 
//					+ e.getMessage());
//		}
//	}
	

	public static void getDeviceId(Context context) 
	{
		Log.w(LOGTAG, "getDeviceId() will get device id");
		Utils.ConstantVars.DEVICE_DATA = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
	}
	

	public static void initLocationListener(Context context)
	{
		Log.w(LOGTAG, "initLocationListener() will initiate location listener");
		Utils.ConstantVars.locListener = new SrmLocationListener(context);
		Utils.ConstantVars.locListener.getLocation();
	}
	
	// Toast some text for debugging
	public static void toastDebuggingText(Context context, String s)
	{
		if(Utils.UIutils.canToastDebugText) 
			Toast.makeText(context, s, Toast.LENGTH_LONG).show();
	}
	
	public static void toastTextToUser(Context context, String s)
	{
		if(Utils.UIutils.canToastTextToUser) 
			Toast.makeText(context, s, Toast.LENGTH_LONG).show();
	}
	
	
	public static String getAppInternalDir(Context context) throws Exception 
	{
	    return context.getPackageManager()
	            .getPackageInfo(context.getPackageName(), 0)
	            .applicationInfo.dataDir;
	}
	
	public static String getAppInternalFilesDir(Context context) throws Exception 
	{
	    return context.getFilesDir().getAbsolutePath();
	}
	
	public static String getAppExternalDir(Context context) throws Exception 
	{
	    String app_dir_ext_path_temp = Environment.getExternalStorageDirectory().getAbsolutePath()
										+ File.separator + "Android" + File.separator + "data" + File.separator
										+ context.getApplicationContext().getPackageName();
		File dir = new File(app_dir_ext_path_temp);
		if(dir.exists())
		{
			return app_dir_ext_path_temp;
		}
		else if(dir.mkdir())
		{
			return app_dir_ext_path_temp;
		}
		else Log.w(LOGTAG, 
				"getAppExternalDir(): Can NOT make directory: " 
				+ app_dir_ext_path_temp);
		return null;
	}
	
	
	public static String makeDir(String parentFolderPath, String folderName)
	{
		if(parentFolderPath != null)
		{
			String path = parentFolderPath + File.separator + folderName;
			File dir = new File(path);
			if(!dir.exists())
			{
				dir.mkdir();
			}
			return dir.getAbsolutePath();
		}
		Log.w(LOGTAG, 
				"makeDir(): Can NOT make directory, parent folder path is null: " 
				+ parentFolderPath);
		return null;
	}
	
	public static void playRecord(Context context, String audioFileName) 
			throws ActivityNotFoundException
	 {
		 Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
         Uri data = Uri.parse("file://" + audioFileName);
         intent.setDataAndType(data, "audio/*");
         context.startActivity(intent);
         //Log.w(this.getClass().getName(), "playRecord(): startActivity(intent) throws Exception " + e.getMessage());
	}
	
	public static void getScreenSize(Context context)
	{
		// get screen size in dp
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density; 
		float screenHeightDp = displayMetrics.heightPixels / displayMetrics.density;
		Log.w(LOGTAG, 
				"getScreenSize() gets the screen size in DP width=" 
				+ screenWidthDp + " height=" + screenHeightDp);
		
		// use width and height as in vertical mode, so width < height
		if(screenWidthDp > screenHeightDp)
		{
			float mid = screenWidthDp;
			screenWidthDp = screenHeightDp;
			screenHeightDp = mid; 
		}
		
		int screenWidthInt = (int) screenWidthDp;
		int screenHeightInt = (int) screenHeightDp;
		
		if((screenWidthInt%2) != 0)
		{
			screenWidthInt--;
		}
		if((screenHeightInt%2) != 0)
		{
			screenHeightInt--;
		}
		
		Utils.ConstantVars.screenWidth = screenWidthInt;
		Utils.ConstantVars.screenHeight = screenHeightInt;
		
		Log.w(LOGTAG, "getScreenSize() optimized the screen size in integer width=" 
				+ Utils.ConstantVars.screenWidth + " height=" + Utils.ConstantVars.screenHeight);
				
	}
	
	public static void setLayoutValuesInVerticalMode()
	{
		Utils.ConstantVars.columnCount = 
				Utils.ConstantVars.screenWidth % Utils.ConstantVars.ITEMWIDTH;
		Utils.ConstantVars.marginItemBGInVerticalMode = 
				(int) ((Utils.ConstantVars.screenWidth - Utils.ConstantVars.ITEMWIDTH) / 2);
		Utils.ConstantVars.widthLLWrapItem = 
				Utils.ConstantVars.ITEMWIDTH + 2 * Utils.ConstantVars.marginItemBGInVerticalMode;
		Utils.ConstantVars.heightLLWrapItem = 
				Utils.ConstantVars.ITEMHEIGHT + 2 * Utils.ConstantVars.marginItemBGInVerticalMode;
		
		
		Log.w(LOGTAG, "setLayoutValuesInVerticalMode() set the item margin " 
				+ "Utils.ConstantVars.marginItemBGInVerticalMode=" 
				+ Utils.ConstantVars.marginItemBGInVerticalMode);
		
	}
	
	public static void setLayoutValuesInHorizontalMode()
	{
		Utils.ConstantVars.columnCount = 
				Utils.ConstantVars.screenHeight % Utils.ConstantVars.ITEMWIDTH;
		Utils.ConstantVars.marginItemBGInHorizontalMode = 
				(int) ((Utils.ConstantVars.screenHeight - (Utils.ConstantVars.ITEMWIDTH * 2)) / 4); 
		Utils.ConstantVars.widthLLWrapItem = 
				Utils.ConstantVars.ITEMWIDTH + 2 * Utils.ConstantVars.marginItemBGInVerticalMode;
		Utils.ConstantVars.heightLLWrapItem = 
				Utils.ConstantVars.ITEMHEIGHT + 2 * Utils.ConstantVars.marginItemBGInVerticalMode;
		
		
		Log.w(LOGTAG, "setLayoutValuesInHorizontalMode() set the item margin "
				+ "Utils.ConstantVars.marginItemBGInHorizontalMode=" 
				+ Utils.ConstantVars.marginItemBGInHorizontalMode);
	}
	
	public static String getCurrentActiveActivity(Context context)
	{
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

	     // get the info from the currently running task
	     List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1); 
	     
	     Log.w(LOGTAG, "getCurrentActiveActivity() gets act:" 
	    		 + taskInfo.get(0).topActivity.getClassName() );
	     
	     ComponentName componentInfo = taskInfo.get(0).topActivity;
	     return componentInfo.getPackageName();
	}
	

}



// example codes for changing theme in runtime, not work, unused
//	// fields for themes
//     private static int sTheme;
//
//     public final static int THEME_LIGHT = 0;
//     public final static int THEME_DARK = 1;
//     public final static int THEME_TEST = 2;
//     public final static int THEME_DEFAULT = 3;
//     
//     
//
//     /**
//      * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
//      */
//     public static void changeToTheme(Activity activity, int theme)
//     {
//          sTheme = theme;
//          
//          activity.finish();
//
//          activity.startActivity(new Intent(activity, activity.getClass()));
//
//     }
//
//     /**
//      *  Set the theme of the activity, according to the configuration. 
//      */
//     public static void onActivityCreateSetTheme(Activity activity)
//     {
//          switch (sTheme)
//          {
//          default:
//        	  break;
//          case THEME_DEFAULT:
//              //activity.setTheme(R.style.?);
//              break;
//          case THEME_LIGHT:
//              activity.setTheme(R.style.theme_light);
//              break;
//          case THEME_DARK:
//              activity.setTheme(R.style.theme_dark);
//              break;
//          case THEME_TEST:
//        	  activity.setTheme(R.style.theme_test);
//              break;
//          }
//     }

