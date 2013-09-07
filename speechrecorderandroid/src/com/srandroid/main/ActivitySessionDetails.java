/**
 * 
 */
package com.srandroid.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Spinner;

import com.srandroid.speechrecorder.R;
import com.srandroid.database.SrmContentProvider;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.database.TableScripts.ScriptItem;
import com.srandroid.database.TableSessions;
import com.srandroid.database.TableSpeakers;
import com.srandroid.recording.ActivityPreRecording;
import com.srandroid.util.Utils;

/**
 *
 */
public class ActivitySessionDetails extends Activity
{
		// state
		public static final String ITEM_URI = "ITEM_URI";
		private String sessionItemId = null;
		
		private String speakerId = null;
		private String scriptId = null;
		
		private CharSequence activity_title = null;
		
		
		TextView sessionid = null;
	    TextView datetime = null;
	    TextView place = null;
	    TextView isfinished = null;
	    TextView speakers = null;
	    TextView scripts = null;
	
	/**
	 * 
	 */
	public ActivitySessionDetails() {
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
			    sessionItemId = extras.getString("sessionItemId");
			}
			
			// orientation changed
	        if(savedInstanceState != null)
	        {
	        	sessionItemId = savedInstanceState.getString("sessionItemId");
	        }
	        
	        Log.w(this.getClass().getName(), "start creating, get sessionItemId=" + sessionItemId);
	        
	        
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
			
			Cursor cursor = getContentResolver().query(SrmUriMatcher.CONTENT_URI_TABLE_SESSIONS_LEFTJOIN_SPEAKERS, 
					selectColumns, wherePart, null, null);
			
	        if (cursor != null && cursor.getCount()!=0) 
			{
	        	cursor.moveToFirst();
				
				Log.w(this.getClass().getName(), " will create view of this activity.");
				
				setContentView(R.layout.linearlayout_activity_sessiondetails);
				
		        sessionid = (TextView) findViewById(R.id.act_sessiondetails_sessionid_textvalue);
		        datetime = (TextView) findViewById(R.id.act_sessionsdetails_datetime_textvalue);
		        place = (TextView) findViewById(R.id.act_sessiondetails_place_textvalue);
		        isfinished = (TextView) findViewById(R.id.act_sessiondetails_isfinished_textvalue);
		        speakers = (TextView) findViewById(R.id.act_sessiondetails_speakers_textvalue);
		        scripts = (TextView) findViewById(R.id.act_sessiondetails_scripts_textvalue);
		        
				String idText = cursor.getString(cursor.getColumnIndexOrThrow("session_key_id"));
				sessionid.setText("Session #" + idText);
				setTitle("Session #" + idText);
				
				String sDate = cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_DATE));
				String sTime = cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_TIME));
				datetime.setText(sDate + " " + sTime);
				
				place.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_PLACE)));
				
				isfinished.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_IS_FINISHED)));
				
				// here for isuploaded, this column is not in db
				
				scriptId = cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SCRIPT_ID));
				scripts.setText(scriptId);
				
				speakerId = cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SPEAKER_ID));
				String firstname = cursor.getString(cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_FIRSTNAME));
				String surname = cursor.getString(cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_SURNAME));
				speakers.setText(firstname + " " + surname);
				
			}
	        
	        cursor.close();
	        
	        
	        
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
		        		
		        		Utils.ConstantVars.speakerItemIdForNewSession = speakerId;
		        		Utils.ConstantVars.scriptItemIdForNewSession = scriptId;
		        		
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
			savedInstanceState.putString("sessionItemId", sessionItemId);
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



}
