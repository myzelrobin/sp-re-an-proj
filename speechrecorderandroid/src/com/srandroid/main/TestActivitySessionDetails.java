/**
 * 
 */
package com.srandroid.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ClipData.Item;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;

import com.srandroid.speechrecorder.R;
import com.srandroid.database.SrmContentProvider;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.database.TableRecords;
import com.srandroid.database.TableScripts.ScriptItem;
import com.srandroid.database.TableSessions;
import com.srandroid.database.TableSpeakers;
import com.srandroid.recording.ActivityPreRecording;
import com.srandroid.util.Utils;

/**
 *
 */
public class TestActivitySessionDetails extends Activity
	implements OnItemClickListener
{
		// state
		public static final String ITEM_URI = "ITEM_URI";
		private String sessionItemId = null;
		
		private String speakerIdForSession;
		private String scriptIdForSession;
		
		private CharSequence activity_title;
		
		GridView gridView;
		
		private TextView sessionid;
		private TextView datetime;
	    private TextView place;
	    private TextView isfinished;
	    private TextView speakers;
	    private TextView scripts;
	    
	    private TextView recordItemId;
	    private  TextView recordItemIntro;
	    private TextView recordItemPrompt;
	    
	    private String[] itemlist; // for adapter
	    private String[] filepathList; // list of record filepathes
	    
	    private Activity thisAct;
	
	    private int recItemsCount;
	/**
	 * 
	 */
	public TestActivitySessionDetails() 
	{
		
	}
	

	// Creates View of this activity
		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			
			thisAct = this;
			
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
	        
	        
	        
	        
	        Log.w(this.getClass().getName(), "start creating, get sessionItemId=" + sessionItemId);
	        
	        setContentView(R.layout.gridviewlayout_test_act_sessiondetails);
	        
	        
	        gridView = (GridView) findViewById(R.id.id_gridview_testact_sessiondetails);
	        
	        
	        // create itemlist
	        try 
	        {
				getIDsForSessionItem(sessionItemId);
				recItemsCount = getRecordsCountForScript(scriptIdForSession);
			} 
	        catch (Exception e) 
	        {
				// TODO Auto-generated catch block
				Log.w(this.getClass().getName(), " getIDsForSessionItem() throws " 
				+ e.getLocalizedMessage());
			}
	        
	        itemlist = new String[recItemsCount+1];
	        filepathList = new String[recItemsCount+1];
	        itemlist[0] = "SESSION_ITEM";
	        for(int i=1; i<recItemsCount+1; i++)
	        {
	        	itemlist[i] = "RECORD_ITEM";
	        }
	        
	        gridView.setAdapter(new LocalAdapterForSessionDetails(this, itemlist));
	        
	        gridView.setClickable(true);
	        
	        // enable home button
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	        getActionBar().setHomeButtonEnabled(true);
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
		}
		
		@Override
	    protected void onPause()
	    {
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
	        		
		        		Utils.toastTextToUser(this, "prepare recording");
		        		
		        		Utils.ConstantVars.speakerItemIdForNewSession = speakerIdForSession;
		        		Utils.ConstantVars.scriptItemIdForNewSession = scriptIdForSession;
		        		
		        		Log.w(this.getClass().getName(), " set Utils.speakerID=" 
		        				+ Utils.ConstantVars.speakerItemIdForNewSession 
		        				+ " and Utils.scriptID=" 
		        				+ Utils.ConstantVars.scriptItemIdForNewSession);
		        		
		        		Intent newI = new Intent(this, ActivityPreRecording.class);
		        		this.startActivity(newI);
		        		
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




		@Override
		public void onItemClick(AdapterView<?> parent, 
				View itemView, 
				int position,
				long rowId) 
		{
			Utils.toastText(thisAct, "clicked item position=" + position + " rowId=" + rowId);
			
//			// play the record
//			try {
//				Utils.playRecord(this, filepathList[position]);
//			} catch (ActivityNotFoundException e) {
//				Log.w(this.getClass().getName(), 
//						"Utils.playRecord() throws Exceptions " + e.getMessage());
//			}
		}
		
		private void getIDsForSessionItem(String sessionItemId)
		{
			Log.w(TestActivitySessionDetails.class.getName(), 
					"getIDsForSessionItem() will query scriptIdForSession and speakerIdForSession from table sessions"
					+ " with sessionItemId=" + sessionItemId);
			
//			// query from db
//	        String[] selectColumns = {
//	        		TableSessions.COLUMN_ID,
//					TableSessions.COLUMN_SCRIPT_ID,
//					TableSessions.COLUMN_SPEAKER_ID};
			
	        Uri uri = Uri.parse(SrmUriMatcher.CONTENT_URI_TABLE_SESSIONS + "/" + sessionItemId);
	        
			Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
			cursor.moveToFirst();
			
			if (cursor != null && cursor.getCount()!=0) 
			{
				
				
				int scriptIdTemp = 
						cursor.getInt(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SCRIPT_ID));
				scriptIdForSession = Integer.toString(scriptIdTemp);
				
				int speakerIdTemp =
						cursor.getInt(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SPEAKER_ID));
				speakerIdForSession = Integer.toString(speakerIdTemp);
				
				
				Log.w(TestActivitySessionDetails.class.getName(), 
						"getIDsForSessionItem() find scriptIdForSession=" + scriptIdForSession);
				
				Log.w(TestActivitySessionDetails.class.getName(), 
						"getIDsForSessionItem() find speakerIdForSession=" + speakerIdForSession);
			}
			
			
			cursor.close();
		}
		
		private int getRecordsCountForScript(String scriptIdTemp)
		{
			Log.w(TestActivitySessionDetails.class.getName(), 
					"getRecordsCountForScript() will query item count from table records "
					+ "with scriptIdForSession=" + scriptIdTemp);
			
			int count = 0;
			
			// query from db
	        String[] selectColumns = {
	        		TableRecords.COLUMN_ID,
	        		TableRecords.COLUMN_SCRIPT_ID};
			
	        String wherePart = "script_id=" + scriptIdTemp;
			
			Cursor cursor = getApplicationContext().getContentResolver().query(
					SrmUriMatcher.CONTENT_URI_TABLE_RECORDS, 
					selectColumns, wherePart, null, null);
			cursor.moveToFirst();
			
			Log.w(TestActivitySessionDetails.class.getName(), 
					"getRecordsCountForScript() gets count=" + cursor.getCount());
			
			if (cursor != null && cursor.getCount()!=0) 
			{
				count = cursor.getCount();
				
				Log.w(TestActivitySessionDetails.class.getName(), 
						"getRecordsCountForScript() gets count=" + count);
			}
			
			cursor.close();
			
			return count;
		}

		

		private Cursor queryAllRecordsForScript(String scriptId)
		{
			Log.w(TestActivitySessionDetails.class.getName(), 
					"queryAllRecordsForScript() will query record items with scriptIdForSession=" + scriptId);
			
			// query from db
	        String[] selectColumns = {
					TableRecords.COLUMN_ID,
					TableRecords.COLUMN_SCRIPT_ID,
					TableRecords.COLUMN_FILEPATH,
					TableRecords.COLUMN_INSTRUCTION,
					TableRecords.COLUMN_PROMPT,
					TableRecords.COLUMN_COMMENT,
					TableRecords.COLUMN_ITEMCODE,
					TableRecords.COLUMN_ISUPLOADED};
			
			String wherePart = "script_id=" + scriptId;
			
			Cursor cursor = getApplicationContext().getContentResolver().query(
					SrmUriMatcher.CONTENT_URI_TABLE_RECORDS, 
					selectColumns, wherePart, null, null);
			cursor.moveToFirst();
			if (cursor != null && cursor.getCount() !=0)
			{
				
				
				Log.w(TestActivitySessionDetails.class.getName(), 
						"queryAllRecordsForScript() queried record items count=" + cursor.getCount());
				
				return cursor;
			}
			else return null;
		}
		
		
		private void fillSessionItem(View view)
		{

			Log.w(TestActivitySessionDetails.class.getName(), 
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
					
					int speakerIdTemp = cursor.getInt(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SPEAKER_ID));
					speakers.setText(Integer.toString(speakerIdTemp));
				
			}
	        
	        cursor.close();
	        
		}
		
		private void fillRecordItem(View view, Cursor cursor)
		{
			Log.w(TestActivitySessionDetails.class.getName(), 
					"fillRecordItem() will fill record item");
			
			if (cursor != null && cursor.getCount()!=0) 
			{
				
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ID));
				Log.w(TestActivitySessionDetails.class.getName(), 
								"fillRecordItem() will fill record item idInTable=" + id);
				
				recordItemId = (TextView) view.findViewById(R.id.id_item_record_id_textvalue);
			    recordItemIntro = (TextView) view.findViewById(R.id.id_item_record_intro_textvalue);
			    recordItemPrompt = (TextView) view.findViewById(R.id.id_item_record_prompt_textvalue);
			    // recordItemButtonPlay = (ImageButton) view.findViewById(R.id.id_item_record_button_play);
		        
			    String itemcode = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ITEMCODE));
			    recordItemId.setText(itemcode);
			    
			    String instruction = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_INSTRUCTION));
			    recordItemIntro.setText(instruction);
			    
			    String prompt = cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_PROMPT));
			    recordItemPrompt.setText(prompt);
			    
			    // a method in adapter to setOnClickListener to 
			}
			
		}
		
		
		
		protected class LocalAdapterForSessionDetails extends BaseAdapter
		{
			

			private Context context;
			private String[] itemlist;
			
			
			private Cursor cursor;

			public LocalAdapterForSessionDetails(Context context,
					String[] itemlist) 
			{
				super();
				this.context = context;
				this.itemlist = itemlist;
				
				if(recItemsCount != 0)
				{
					
					cursor = queryAllRecordsForScript(scriptIdForSession);
				}
				
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return itemlist.length;
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
				
				LinearLayout itemView = null;
				
				if(itemlist[position].equals("SESSION_ITEM"))
				{
					Log.w(LocalAdapterForSessionDetails.class.getName(), 
							"getView() will create sessionitem at position=" + position);
					
					itemView = 
							(LinearLayout) (convertView == null
							? LayoutInflater.from(context).inflate(R.layout.linearlayout_activity_sessiondetails, parent, false)
									: convertView);

			        try
			        {
			        	fillSessionItem(itemView);
			        	itemView.setClickable(false);
			        }
			        catch (Exception e) 
			        {
			            e.printStackTrace();
			        }

				}
				else if(itemlist[position].equals("RECORD_ITEM"))
				{
					
					itemView = 
							(LinearLayout) (convertView == null
							? LayoutInflater.from(context).inflate(R.layout.linearlayout_item_record, parent, false)
									: convertView);
					if(cursor != null)
					{
						
						Log.w(LocalAdapterForSessionDetails.class.getName(), 
								"getView() will create recorditem at position=" + position
								+ " and record_key_id=" + 
								cursor.getInt(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_ID)));
						
						fillRecordItem(itemView, cursor);
						cursor.moveToNext();
						if(cursor.isAfterLast()) cursor.close();
					}
					
					// use arraylist is better
					 filepathList[position] = 
							cursor.getString(cursor.getColumnIndexOrThrow(TableRecords.COLUMN_FILEPATH));
					
					
				}
				
				return itemView;
			}
			
		}


}
