/**
 * 
 */
package com.srandroid.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.srandroid.speechrecorder.R;
import com.srandroid.database.SrmContentProvider;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.database.TableRecords;
import com.srandroid.database.TableRecords.RecordItem;
import com.srandroid.database.TableScripts.ScriptItem;
import com.srandroid.database.TableServers.ServerItem;
import com.srandroid.database.TableSessions;
import com.srandroid.database.TableSpeakers;
import com.srandroid.network.SrmDropboxHandler;
import com.srandroid.network.SrmDropboxHandler.GetFileInfosTask;
import com.srandroid.network.SrmNetworkHandler;
import com.srandroid.recording.ActivityPreRecording;
import com.srandroid.util.Utils;

import android.support.v4.widget.StaggeredGridView;
import android.support.v4.widget.StaggeredGridView.LayoutParams;


/**
 *
 */
public class TestActivitySessionDetails extends Activity
{
		// log
		private final static String LOGTAG = TestActivitySessionDetails.class.getName();
		
		// context
		public static Context context;
		
		// state
		public static final String ITEM_URI = "ITEM_URI";
		
		
		// for UI
		private String sessionItemId = null;
		
		private String speakerIdForSession;
		private String scriptIdForSession;
		private boolean isSessionFinished;
		
		private CharSequence activity_title;
		
		protected StaggeredGridView stGridView;
		
		private TextView sessionid;
		private TextView datetime;
	    private TextView place;
	    private TextView isfinished;
	    private TextView speakers;
	    private TextView scripts;
	    
//	    private TextView recordItemItemcode;
//	    private TextView recordItemIntro;
//	    private TextView recordItemPrompt;
//	    private TextView recordItemComment;
//	    private TextView recordItemScriptid;
//	    private TextView recordItemIsuploaded;
//	    private Button bPlayrecord;
	    
	    private ArrayList<String> itemlist = new ArrayList<String> (); // for adapter
	    private ArrayList<String> recordItemIdList = new ArrayList<String> ();
	    
	    private int recItemsCount = 0;
	    
	    private LocalAdapterForSessionDetails localAdapter;
	    
	    
	    
	    // Network
	    private SrmDropboxHandler dropboxHandler;
	    private AsyncTask<Void, Long, Boolean> getFileInfosInDropbox;
	    
	    
		/**
		 * 
		 */
		public TestActivitySessionDetails() 
		{
			context = this;
		}
	
	
		// Creates View of this activity
		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			
			// start activity from main
			Bundle extras = getIntent().getExtras(); 

			if (extras != null) 
			{
			    sessionItemId = extras.getString("itemId");
			}
			
			// orientation changed
	        if(savedInstanceState != null)
	        {
	        	sessionItemId = savedInstanceState.getString("iemId");
	        }
	        
	        
	        
	        // start creating
	        Log.w(LOGTAG, "start creating, get sessionItemId=" + sessionItemId);
	        
	        setContentView(R.layout.gridviewlayout_test_act_sessiondetails);
	        
	        // create itemlist
	        getSessionItemInfos(sessionItemId);
			
	        if(isSessionFinished) getRecordsIDsForSession(sessionItemId);
	        else
	        {
	        	itemlist.add(0, "SESSION_ITEM");
	        }
			
	        
//	        itemlist = new ArrayList<String>();
//
//	        itemlist.add(0, "SESSION_ITEM");
//	        for(int i=1; i<recItemsCount+1; i++)
//	        {
//	        	itemlist.add(i, "RECORD_ITEM");
//	        }
	        
	        stGridView = (StaggeredGridView) findViewById(R.id.id_testact_sessiondetails_gridview);
	        
	        localAdapter = new  LocalAdapterForSessionDetails(this, itemlist);
	        
	        stGridView.setAdapter(localAdapter);
	        
	        
	        // enable home button
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	        getActionBar().setHomeButtonEnabled(true);
	        
	        // if there are records, toast hint to user to scroll down
	        if(recItemsCount > 0) toastSwipeHint();
	        
	        
	        
	    }
		

		@Override
	    protected void onStart()
		{
			super.onStart();
		}
		
		@Override
	    protected void onRestart()
	    {
			super.onRestart();
		}
		
		@Override
	    protected void onResume()
	    {
			super.onResume();
			if(dropboxHandler != null)
			{
				dropboxHandler.finishAuthen(dropboxHandler.dropbox);				
			}
		}
		
		@Override
	    protected void onPause()
	    {
			//if(localAdapter.getCursor() != null) localAdapter.getCursor().close();
			
			super.onPause();
		}
		
		@Override
	    protected void onStop()
	    {
			
			super.onStop();
		}
		
		@Override
	    protected void onDestroy()
	    {
			super.onDestroy();
		}
		
		// This is what gets called on finishing a media piece to import
	    @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//	        if (requestCode == NEW_PICTURE) {
//	            // return from file upload
//	            if (resultCode == Activity.RESULT_OK) {
//	                Uri uri = null;
//	                if (data != null) {
//	                    uri = data.getData();
//	                }
//	                if (uri == null && mCameraFileName != null) {
//	                    uri = Uri.fromFile(new File(mCameraFileName));
//	                }
//	                File file = new File(mCameraFileName);
//
//	                if (uri != null) {
//	                    UploadPicture upload = new UploadPicture(this, mApi, PHOTO_DIR, file);
//	                    upload.execute();
//	                }
//	            } else {
//	                Log.w(TAG, "Unknown Activity Result from mediaImport: "
//	                        + resultCode);
//	            }
//	        }
	    }
		
		@Override
	    protected void onPostCreate(Bundle savedInstanceState) 
		{
	        super.onPostCreate(savedInstanceState);
	    }
		
	    @Override
	    public void onConfigurationChanged(Configuration newConfig) 
	    {
	        super.onConfigurationChanged(newConfig);
	    }
	    
		/**
		 * Creates menu items in action bar
		 * 
		 * @param menu
		 * @return
		 */
		@Override
		public boolean onCreateOptionsMenu(Menu menu) 
		{
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.menu_items, menu);
			return true;
		}
		
		/**
		 * Called whenever we call invalidateOptionsMenu()
		 * Updates the menu items in action bar when the "drawer items" is closed
		 * 
		 * @param menu
		 * @return
		 */
		@Override
	    public boolean onPrepareOptionsMenu(Menu menu) 
		{
			menu.setGroupVisible(R.id.bgroup_sessiondetails, true);
	        return super.onPrepareOptionsMenu(menu);
	    }
		
		/**
		 * Handles click events on app icon and menu items in actionbar and overflow
		 */
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) 
		{
	       
	        switch (item.getItemId()) 
	        {
			    // Respond to the action bar's Up/Home button
			    case android.R.id.home:
			        NavUtils.navigateUpFromSameTask(this);
			        return true;
			        
			     // actionbar buttons
	        	case R.id.act_sessiondetails_button_start:
	        		
	        			Log.w(LOGTAG, "user clicked button start"); 
		        		// Utils.toastTextToUser(this, "prepare recording");
		        		
		        		Utils.ConstantVars.speakerItemIdForNewSession = speakerIdForSession;
		        		Utils.ConstantVars.scriptItemIdForNewSession = scriptIdForSession;
		        		
		        		Log.w(LOGTAG, " set Utils.speakerID=" 
		        				+ Utils.ConstantVars.speakerItemIdForNewSession 
		        				+ " and Utils.scriptID=" 
		        				+ Utils.ConstantVars.scriptItemIdForNewSession);
		        		
		        		if(isSessionFinished)
		        		{
		        			// start a new session
		        			Intent newI = new Intent(this, ActivityPreRecording.class);
			        		this.startActivity(newI);
		        		}
		        		else
		        		{
		        			// start current session from act 
		        			Utils.toastTextToUser(this, "this session is not finished yet!");
		        		}
	        		break;
	        	
	        	case R.id.act_sessiondetails_button_upload:
	        		
	        			Log.w(LOGTAG, "user clicked button upload"); 
		        		//Utils.toastTextToUser(this, "start uploading");
		        		break;
		        		
	        	case R.id.act_sessiondetails_button_testauthen:
		        		
	        			Log.w(LOGTAG, "user clicked button test authen"); 

	        			dropboxHandler = new SrmDropboxHandler(context, this);
	        			
	        			dropboxHandler.createDropboxAPI();
	        			
	        			dropboxHandler.dropbox.getSession().startAuthentication(context);
	        			
	        			break;
	        	
	        	case R.id.act_sessiondetails_button_testlistfile:
	        			
	        			Log.w(LOGTAG, "user clicked button test listfile");
	        			
	        			dropboxHandler = new SrmDropboxHandler(context, this);
	        			
	        			dropboxHandler.createDropboxAPI();
	        			
						getFileInfosInDropbox = 
								new GetFileInfosTask(
										context, 
										this, 
										dropboxHandler.dropbox, 
										SrmDropboxHandler.FOLDER_SCRIPT_PATH)
						.execute();
	        			
    					break;
	        			
	        	case R.id.act_sessiondetails_button_testdownload:
	        			Log.w(LOGTAG, "user clicked button test download");
	        			
        				break;
        				
	        	case R.id.act_sessiondetails_button_testupload:
	        			Log.w(LOGTAG, "user clicked button test upload");
	        			
    					break;
	        		
	        	default:
	        		break;
	    	}
	        
		    return super.onOptionsItemSelected(item);
	    }
		
		@Override
		protected void onSaveInstanceState(Bundle savedInstanceState) 
		{
			savedInstanceState.putString("itemId", sessionItemId);
		    super.onSaveInstanceState(savedInstanceState);
		}


		@Override
		public void onRestoreInstanceState(Bundle savedInstanceState) 
		{
		  super.onRestoreInstanceState(savedInstanceState);
		}
		
		/**
		 * 
		 * @param title
		 */
		@Override
		public void setTitle(CharSequence title) 
		{
		    getActionBar().setTitle(title);
		}


		
		private void getSessionItemInfos(String sessionItemId)
		{
			Log.w(LOGTAG, 
					"getSessionItemInfos() will query from table sessions"
					+ " with sessionItemId=" + sessionItemId);
			
			// query from db
	        String[] selectColumns = {
	        		TableSessions.COLUMN_ID,
					TableSessions.COLUMN_SCRIPT_ID,
					TableSessions.COLUMN_SPEAKER_ID,
					TableSessions.COLUMN_IS_FINISHED};
			
	        String wherePart = "sessions._id=" + sessionItemId;
	        
			Cursor cursor = getApplicationContext().getContentResolver().query(
					SrmUriMatcher.CONTENT_URI_TABLE_SESSIONS, 
					selectColumns, wherePart, null, null);
			cursor.moveToFirst();
			
			if (cursor != null && cursor.getCount()!=0) 
			{
				
				
				int scriptIdTemp = 
						cursor.getInt(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SCRIPT_ID));
				scriptIdForSession = Integer.toString(scriptIdTemp);
				
				int speakerIdTemp =
						cursor.getInt(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SPEAKER_ID));
				speakerIdForSession = Integer.toString(speakerIdTemp);
				
				String isFinishedValue = 
						cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_IS_FINISHED));
				if(isFinishedValue.equals("finished")) isSessionFinished = true;
				else isSessionFinished = false;
				
				Log.w(LOGTAG, 
						"getSessionItemInfos() find scriptIdForSession=" + scriptIdForSession);
				
				Log.w(LOGTAG, 
						"getSessionItemInfos() find speakerIdForSession=" + speakerIdForSession);
			}
			
			
			cursor.close();
		}
		
		private int getRecordsIDsForSession(String sessionId)
		{
			Log.w(LOGTAG, 
					"getRecordsCountForScript() will query item count from table records "
					+ " sessionId=" + sessionId);
			
			int count = 0;
			
			// query from db
	        String[] selectColumns = {
	        		TableRecords.COLUMN_ID,
	        		TableRecords.COLUMN_SESSION_ID};
			
	        String wherePart = "session_id=" + sessionId;
			
			Cursor cursor = getApplicationContext().getContentResolver().query(
					SrmUriMatcher.CONTENT_URI_TABLE_RECORDS, 
					selectColumns, wherePart, null, null);
			cursor.moveToFirst();
			
			Log.w(LOGTAG, 
					"getRecordsIDsForSession() gets count=" + cursor.getCount());
			
			if (cursor != null && cursor.getCount()!=0) 
			{
				count = cursor.getCount();
				
				Log.w(LOGTAG, "getRecordsIDsForSession() gets count=" + count);
				
				recItemsCount = cursor.getCount();
				
				itemlist.add(0, "SESSION_ITEM");
				for(int i = 1; i < recItemsCount + 1; i++)
				{
					itemlist.add(i, "RECORD_ITEM");
				}
				
				recordItemIdList.add(0, Integer.toString(-1));
				for(int i = 1; i < recItemsCount + 1; i++)
				{
					recordItemIdList.add(i, 
							cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ID)));
				}
				
			}
			
			cursor.close();
			
			return count;
		}

		private String getRecItemIdFromList(int position)
		{
			return recordItemIdList.get(position);
		}

		private Cursor queryAllRecordsForScript()
		{
			Log.w(LOGTAG, 
					"queryAllRecordsForScript() will query record items with sessionId=" + sessionItemId);
			
			// query from db
	        String[] selectColumns = {
					TableRecords.COLUMN_ID,
					TableRecords.COLUMN_SESSION_ID,
					TableRecords.COLUMN_SPEAKER_ID,
					TableRecords.COLUMN_SCRIPT_ID,
					TableRecords.COLUMN_FILEPATH,
					TableRecords.COLUMN_INSTRUCTION,
					TableRecords.COLUMN_PROMPT,
					TableRecords.COLUMN_COMMENT,
					TableRecords.COLUMN_ITEMCODE,
					TableRecords.COLUMN_ITEMTYPE,
					TableRecords.COLUMN_ISUPLOADED};
			
			String wherePart = "session_id=" + sessionItemId;
			
			Cursor cursor = getApplicationContext().getContentResolver().query(
					SrmUriMatcher.CONTENT_URI_TABLE_RECORDS, 
					selectColumns, wherePart, null, null);
			cursor.moveToFirst();
			if (cursor != null && cursor.getCount() !=0)
			{
				Log.w(LOGTAG, 
						"queryAllRecordsForScript() queried record items count=" + cursor.getCount());
				
				return cursor;
			}
			else return null;
		}
		
		
		private void fillSessionItem(View view)
		{

			Log.w(LOGTAG, 
					"fillSessionItem() will fill session item id=" + sessionItemId);
	        // query from db
	        String[] selectColumns = {
					TableSessions.COLUMN_DATE,
					TableSessions.COLUMN_TIME,
					TableSessions.COLUMN_PLACE,
					TableSessions.COLUMN_IS_FINISHED,
					TableSessions.COLUMN_SPEAKER_ID,
					TableSessions.COLUMN_SCRIPT_ID,
					TableSpeakers.COLUMN_FIRSTNAME,
					TableSpeakers.COLUMN_SURNAME};
			
			String wherePart = "session_key_id=" + sessionItemId;
			
			Cursor cursor = getApplicationContext().getContentResolver().query(
					SrmUriMatcher.CONTENT_URI_TABLE_SESSIONS_LEFTJOIN_SPEAKERS, 
					selectColumns, wherePart, null, null);
			cursor.moveToFirst();
			
	        if (cursor != null && cursor.getCount()!=0) 
			{
		        sessionid = (TextView) view.findViewById(R.id.act_sessiondetails_sessionid_textvalue);
		        datetime = (TextView) view.findViewById(R.id.act_sessionsdetails_datetime_textvalue);
		        place = (TextView) view.findViewById(R.id.act_sessiondetails_place_textvalue);
		        isfinished = (TextView) view.findViewById(R.id.act_sessiondetails_isfinished_textvalue);
		        speakers = (TextView) view.findViewById(R.id.act_sessiondetails_speakers_textvalue);
		        scripts = (TextView) view.findViewById(R.id.act_sessiondetails_scripts_textvalue);
		        
					String idText = cursor.getString(cursor.getColumnIndexOrThrow("session_key_id"));
					sessionid.setText("Session #" + idText);
					setTitle("Session #" + idText);
					
					String sDate = cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_DATE));
					String sTime = cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_TIME));
					datetime.setText(sDate + " " + sTime);
					
					place.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_PLACE)));
					
					isfinished.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_IS_FINISHED)));
					
					// here for isuploaded, this column is not in db
					
					int scriptIdTemp = cursor.getInt(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SCRIPT_ID));
					scripts.setText(Integer.toString(scriptIdTemp));
					
					String firstname = cursor.getString(cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_FIRSTNAME));
					String surname = cursor.getString(cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_SURNAME));
					speakers.setText(firstname + " " + surname);
				
			}
	        
	        cursor.close();
	        
		}
		
		private void fillRecordItem(View view, Cursor cursor, int position)
		{
			//if (cursor != null && cursor.getCount()!=0) 
			
				
				cursor.moveToPosition(position - 1);
				
				Log.w(LOGTAG, 
						"fillRecordItem() will create record item, item_position=" + position 
						+ ", cursor_position=" + cursor.getPosition());
				
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ID));
				Log.w(LOGTAG, "fillRecordItem() will fill record item with idInTable=" + id);
				
		    	TextView recordItemItemcode = (TextView) view.findViewById(R.id.recorditem_itemcode_textvalue);
		    	TextView recordItemScriptid = (TextView) view.findViewById(R.id.recorditem_script_id_textvalue);
		    	TextView recordItemIsuploaded = (TextView) view.findViewById(R.id.recorditem_isuploaded_textvalue);
		    	TextView recordItemIntro = (TextView) view.findViewById(R.id.recorditem_intro_textvalue);
		    	TextView recordItemComment = (TextView) view.findViewById(R.id.recorditem_comment_textvalue);
		    	TextView recordItemPrompt = (TextView) view.findViewById(R.id.recorditem_prompt_textvalue);
		        
			    String itemcode = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ITEMCODE));
			    recordItemItemcode.setText(itemcode);
		       
			    
			    String scriptidTemp = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_SCRIPT_ID));
			    recordItemScriptid.setText("Script #" + scriptidTemp);
		        
			    
			    String isuploaded = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ISUPLOADED));
			    recordItemIsuploaded.setText(isuploaded);
		        
			    
			    String instruction = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_INSTRUCTION));
			    recordItemIntro.setText(instruction);
		        
			    
			    String comment = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_COMMENT));
			    recordItemComment.setText(comment);
		        
			    
			    String prompt = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_PROMPT));
			    recordItemPrompt.setText(prompt);
		        
			    
		    	final String recFilepath = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_FILEPATH));

			    Button bPlayrecord = (Button) view.findViewById(R.id.recorditem_button_play_record);
			    bPlayrecord.setOnClickListener(
			    	new OnClickListener() 
				    {
						@Override
						public void onClick(View v) 
						{
							// play the record
							try 
							{
								Utils.playRecord(context, recFilepath);
							} 
							catch (ActivityNotFoundException e) 
							{
								Log.w(LOGTAG, "Utils.playRecord() throws Exceptions " 
										+ e.getLocalizedMessage());
								
							}
						}
					});
			    
			    
			    
			
			
		}
		
//		private void fillRecordItem2(View view, String recItemId)
//		{
//			Log.w(LOGTAG, 
//				"fillRecordItem2() will query record item with recItemId=" + recItemId);
//			
//			// query from db
//	        String[] selectColumns = {
//					TableRecords.COLUMN_ID,
//					TableRecords.COLUMN_SESSION_ID,
//					TableRecords.COLUMN_SPEAKER_ID,
//					TableRecords.COLUMN_SCRIPT_ID,
//					TableRecords.COLUMN_FILEPATH,
//					TableRecords.COLUMN_INSTRUCTION,
//					TableRecords.COLUMN_PROMPT,
//					TableRecords.COLUMN_COMMENT,
//					TableRecords.COLUMN_ITEMCODE,
//					TableRecords.COLUMN_ITEMTYPE,
//					TableRecords.COLUMN_ISUPLOADED};
//			
//			String wherePart = "records._id=" + recItemId;
//			
//			Cursor cursor = getApplicationContext().getContentResolver().query(
//					SrmUriMatcher.CONTENT_URI_TABLE_RECORDS, 
//					selectColumns, wherePart, null, null);
//			cursor.moveToFirst();
//			
//			
//			Log.w(LOGTAG, 
//					"fillRecordItem2() will fill record item with recItemId=" + recItemId);
//			
//			if (cursor != null && cursor.getCount()!=0) 
//			{
//				int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ID));
//				Log.w(LOGTAG, "fillRecordItem2() will fill record item idInTable=" + id);
//				
//				recordItemItemcode = (TextView) view.findViewById(R.id.recorditem_itemcode_textvalue);
//				recordItemScriptid = (TextView) view.findViewById(R.id.recorditem_script_id_textvalue);
//				recordItemIsuploaded = (TextView) view.findViewById(R.id.recorditem_isuploaded_textvalue);
//			    recordItemIntro = (TextView) view.findViewById(R.id.recorditem_intro_textvalue);
//			    recordItemComment = (TextView) view.findViewById(R.id.recorditem_comment_textvalue);
//			    recordItemPrompt = (TextView) view.findViewById(R.id.recorditem_prompt_textvalue);
//		        
//			    String itemcode = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ITEMCODE));
//			    recordItemItemcode.setText(itemcode);
//			    
//			    String scriptidTemp = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_SCRIPT_ID));
//			    recordItemScriptid.setText("Script #" + scriptidTemp);
//			    
//			    String isuploaded = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ISUPLOADED));
//			    recordItemIsuploaded.setText(isuploaded);
//			    
//			    
//			    String itemtype = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ITEMTYPE));
//			    
//			    String instruction = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_INSTRUCTION));
//			    recordItemIntro.setText(instruction);
//			    
//			    String comment = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_COMMENT));
//			    recordItemComment.setText(comment);
//			    
//			    String prompt = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_PROMPT));
//			    // recordItemPrompt.setText(prompt);
//			    if (itemtype == RecordItem.TYPE_TEXT) recordItemPrompt.setText(prompt);
//			    else if(itemtype == RecordItem.TYPE_IMAGE) recordItemPrompt.setText("#IMAGE " + prompt);
//			    else if(itemtype == RecordItem.TYPE_SOUND) recordItemPrompt.setText("#SOUND " + prompt);
//			    
//			    final String recFilepath = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_FILEPATH));
//
//			    bPlayrecord = (Button) view.findViewById(R.id.recorditem_button_play_record);
//			    bPlayrecord.setOnClickListener(new OnClickListener() 
//			    {
//					
//					@Override
//					public void onClick(View v) 
//					{
//						// play the record
//						try {
//							Utils.playRecord(context, recFilepath);
//						} catch (ActivityNotFoundException e) {
//							Log.w(LOGTAG, 
//									"Utils.playRecord() throws Exceptions " + e.getMessage());
//						}
//					}
//				});
//			}
//			
//			cursor.close();
//		}
		
		private void toastSwipeHint()
		{
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.hintlayout_in_act_sessiondetails,
			                               (ViewGroup) findViewById(R.id.linearlayout_act_sessiondetails_toasthint));

			Toast toast = new Toast(getApplicationContext());
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG * 3);
			toast.setView(layout);
			toast.show();
		}
		
//		private OnScrollListener	mScrollListener = new OnScrollListener() {
//	    	
//			@Override
//			public void onScrollStateChanged(ViewGroup view, int scrollState) {
//				Log.d(TAG, "[onScrollStateChanged] scrollState:" + scrollState);
//				switch (scrollState) {
//				case SCROLL_STATE_IDLE:
//					setTitle("SCROLL_STATE_IDLE");
//					break;
//					
//				case SCROLL_STATE_FLING:
//					setTitle("SCROLL_STATE_FLING");
//					break;
//					
//				case SCROLL_STATE_TOUCH_SCROLL:
//					setTitle("SCROLL_STATE_TOUCH_SCROLL");
//					break;
//
//				default:
//					break;
//				}
//				
//			}
//
//			@Override
//			public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//				Log.d(TAG, "[onScroll] firstVisibleItem:" + firstVisibleItem + " visibleItemCount:"+visibleItemCount + " totalItemCount:" + totalItemCount);
//				
//			}
//		};
		
		private final class LocalAdapterForSessionDetails extends BaseAdapter
		{
			

			private Context context;
			private ArrayList<String> itemlist;
			
			
			private Cursor cursor;

			public LocalAdapterForSessionDetails(Context context,
					ArrayList itemlist) 
			{
				super();
				this.context = context;
				this.itemlist = itemlist;
				
				if(recItemsCount != 0)
				{
					
					cursor = queryAllRecordsForScript();
				}
				
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return itemlist.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) 
			{
				LayoutParams lp;
				
				LinearLayout itemView = null;
				
				if(itemlist.get(position).equals("SESSION_ITEM"))
				{
					Log.w(LOGTAG + ".Adapter", 
							"getView() will create SESSIONITEM at position=" + position);
					
					itemView = (LinearLayout) (convertView == null
								? LayoutInflater
										.from(context)
										.inflate(R.layout.linearlayout_activity_sessiondetails, parent, false)
										: convertView);
					
			        try
			        {
			        	lp = new LayoutParams(itemView.getLayoutParams());
	                    lp.span = stGridView.getColumnCount();
	                    itemView.setLayoutParams(lp);
	                    itemView.setPadding(10, 10, 10, 10);
	                    
			        	fillSessionItem(itemView);
			        }
			        catch (Exception e) 
			        {
			        	Log.w(LOGTAG + ".Adapter", 
								"getView() created SESSIONITEM at position=" + position 
								+ " throws error:" + e.getMessage());
			        }

				}
				else if(itemlist.get(position).equals("RECORD_ITEM"))
				{
					Log.w(LOGTAG + ".Adapter", 
							"getView() will create RECORDITEM at position=" + position);
					
					itemView = (LinearLayout) (convertView == null
								? LayoutInflater
									.from(context)
									.inflate(R.layout.linearlayout_act_sessiondetails_recorditem, parent, false)
								: convertView);
					
					if(cursor != null && cursor.getCount() != 0)
					{
						try
				        {
							lp = new LayoutParams(itemView.getLayoutParams());
		                    lp.span = 1;
		                    itemView.setLayoutParams(lp);
		                    itemView.setPadding(10, 10, 10, 10);
							
							fillRecordItem(itemView, cursor, position);
		                    //fillRecordItem2(itemView, getRecItemIdFromList(position));
				        }
				        catch (Exception e) 
				        {
				            Log.w(LOGTAG + ".Adapter", 
									"getView() created RECORDITEM at position=" + position 
									+ " throws error:" + e.getMessage());
				        }
					}
					
					
				}
				
				return itemView;
			}

			/**
			 * @return the cursor
			 */
			public Cursor getCursor() {
				return cursor;
			}

			/**
			 * @param cursor the cursor to set
			 */
			public void setCursor(Cursor cursor) {
				this.cursor = cursor;
			}
			
		}
		

}
