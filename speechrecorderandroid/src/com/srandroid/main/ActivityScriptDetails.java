/**
 * 
 */
package com.srandroid.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.srandroid.recording.ActivityPreRecording;
import com.srandroid.speechrecorder.R;
import com.srandroid.database.TableScripts;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.database.TableScripts.ScriptItem;
import com.srandroid.database.TableSessions;
import com.srandroid.database.TableSpeakers;
import com.srandroid.util.Utils;

/**
 *
 */
public class ActivityScriptDetails extends Activity
{
	
	private static final String LOGTAG = ActivityScriptDetails.class.getName();
	
	// state
	public static final String ITEM_URI = "ITEM_URI";
	private String scriptItemId = null;
	
	
	
	private TextView scriptid = null;
	private TextView scriptdesc = null;
	private TextView sessionsList = null;
	private TextView speakersList = null;
	
	/**
	 * 
	 */
	public ActivityScriptDetails() {
		// TODO Auto-generated constructor stub
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
			    scriptItemId = extras.getString("itemId");
			}
			
			// orientation changed
	        if(savedInstanceState != null)
	        {
	        	scriptItemId = savedInstanceState.getString("scriptItemId");
	        }
	        
	        Log.w(LOGTAG, "start creating script details, get scriptItemId=" + scriptItemId);
	        
	        Log.w(LOGTAG, " will create view of this activity.");
			
			setContentView(R.layout.linearlayout_activity_scriptdetails);
			
			scriptid = (TextView) findViewById(R.id.activity_scriptdetails_scriptid_textvalue);
	        scriptdesc = (TextView) findViewById(R.id.activity_scriptdetails_desc_textvalue);
	        sessionsList = (TextView) findViewById(R.id.activity_scriptdetails_sessions_textvalue);
	        speakersList = (TextView) findViewById(R.id.activity_scriptdetails_speakers_textvalue);
	        
	        fillScriptDetails();
	        
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
			menu.setGroupVisible(R.id.bgroup_scriptdetails, true);
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
	        	case R.id.activity_scriptdetails_button_start:
	        		
		        		// Utils.toastTextToUser(this, "start recording");
	        		
	        			// save script item to Utils
	        			Utils.ConstantVars.scriptItemIdForNewSession = scriptItemId;
	        		
		        		if(Utils.checkItemsForNewSession(this))
		        		{
		        			Intent newI = new Intent(this, ActivityPreRecording.class);
		        			this.startActivity(newI);
		        			break;
		        			
		        		}
		        		NavUtils.navigateUpFromSameTask(this);
				        return true;
		        		
	        	default:
	        		break;
	    	}
		    return super.onOptionsItemSelected(item);
	    }
		
		@Override
		protected void onSaveInstanceState(Bundle savedInstanceState) 
		{
			savedInstanceState.putString("scriptItemId", scriptItemId);
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

		
		private void fillScriptDetails()
		{
			Log.w(LOGTAG, "fillScriptDetails() will fill script details.");
			
	        // query from db
			String[] selectColumns = {
					TableScripts.COLUMN_DESCRIPTION,
					TableSessions.COLUMN_SCRIPT_ID,
					TableSessions.COLUMN_SPEAKER_ID
			};
			
			String wherePart = "script_key_id=" + scriptItemId;
			
			Cursor cursor = getContentResolver().query(
					SrmUriMatcher.CONTENT_URI_TABLE_SCRIPTS_LOJ_SESSIONS, 
					selectColumns, wherePart, null, null);
			
	        if (cursor != null && cursor.getCount()!=0) 
			{
		        
				cursor.moveToFirst();
				
				String idText = cursor.getString(cursor.getColumnIndexOrThrow("script_key_id"));
				scriptItemId = idText;
				scriptid.setText("Script #" + idText);
				setTitle("Script #" + idText);
				
				scriptdesc.setText(cursor.getString(
						cursor.getColumnIndexOrThrow(TableScripts.COLUMN_DESCRIPTION)));
				
				List<String> sessionsArraylist = new ArrayList<String>();
				List<String> speakersArraylist = new ArrayList<String>();
				
				while(!cursor.isAfterLast())
				{
					String sessionItemId = cursor.getString(
							cursor.getColumnIndexOrThrow("session_key_id"));
					if(!sessionsArraylist.contains(sessionItemId)) 
						sessionsArraylist.add(sessionItemId);
					
					String name = querySpeakerName(cursor.getString(
							cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SPEAKER_ID)));
					if(!speakersArraylist.contains(name)) 
						speakersArraylist.add(name);
					
					cursor.moveToNext();
				}
				if(!(sessionsArraylist.toString().contains("null"))) 
					sessionsList.setText(TextUtils.join(", ", sessionsArraylist));
				if(!(speakersArraylist.toString().contains("null"))) 
					speakersList.setText(TextUtils.join(", ", speakersArraylist));
				
			}
	        
	        cursor.close();
		}
		
		private String querySpeakerName(String speakerItemId)
		{
			Log.w(LOGTAG, "querySpeakerName() will find speaker name with id=" 
					+ speakerItemId);
			
			String name = "no name";
			
			// query from db
			String[] selectColumns = {
					TableSpeakers.COLUMN_ID,
					TableSpeakers.COLUMN_FIRSTNAME,
					TableSpeakers.COLUMN_SURNAME
			};
			
			String wherePart = "speakers._id=" + speakerItemId;
			
			Cursor cursor = getContentResolver().query(
					SrmUriMatcher.CONTENT_URI_TABLE_SPEAKERS, 
					selectColumns, wherePart, null, null);
			 if (cursor != null && cursor.getCount()!=0) 
			{
		        
				cursor.moveToFirst();
				
				String firstname = cursor.getString(
						cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_FIRSTNAME));
				String surname = cursor.getString(
						cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_SURNAME));
				
				name = firstname + " " + surname;
			}
		        
		    cursor.close();
					
			return name;
		}


}
