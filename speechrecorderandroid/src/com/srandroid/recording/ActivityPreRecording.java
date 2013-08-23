/**
 * 
 */
package com.srandroid.recording;

import java.util.ArrayList;
import java.util.List;

import com.srandroid.speechrecorder.R;
import com.srandroid.database.TableScripts;
import com.srandroid.database.TableSessions;
import com.srandroid.database.TableSpeakers;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.main.ActivityScriptDetails;
import com.srandroid.util.Utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView.FindListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 */
public class ActivityPreRecording extends Activity 
{
	//private String itemId = null;
	
	
	private GridView gridView = null;

	/**
	 * 
	 */
	public ActivityPreRecording() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		

		Bundle extras = getIntent().getExtras(); 

		if (extras != null) 
		{
		    //itemId = extras.getString("itemId");
		}
		
		// orientation changed
        if(savedInstanceState != null)
        {
        	//itemId = savedInstanceState.getString("itemId");
        }
        
        Log.w(this.getClass().getName(), "start creating");
        
        setContentView(R.layout.gridviewlayout_act_prerecording);
        
        gridView = (GridView) findViewById(R.id.id_gridview_act_prerecoding);
        
        LinearLayout llSpeakerItem = new LinearLayout(this);
        llSpeakerItem.inflate(this, R.layout.linearlayout_activity_speakerdetails, gridView);
        fillSpeakerItem(llSpeakerItem);
        
        LinearLayout llScriptItem = new LinearLayout(this);
        llScriptItem.inflate(this, R.layout.linearlayout_activity_scriptdetails, gridView);
        fillScriptItem(llScriptItem);
        
        gridView.addView(llSpeakerItem, 480, 480);
        gridView.addView(llScriptItem, 480, 480);
        
        gridView.setClickable(false);
	}
	
	private void fillSpeakerItem(LinearLayout speakerItem)
	{
        // query from db
		String[] selectColumns = {
				TableSpeakers.COLUMN_FIRSTNAME,
				TableSpeakers.COLUMN_SURNAME,
				TableSpeakers.COLUMN_ACCENT,
				TableSpeakers.COLUMN_SEX,
				TableSpeakers.COLUMN_BIRTHDAY,
				TableSessions.COLUMN_SCRIPT_ID,
		};
		
		String wherePart = "speaker_key_id=" + Utils.ConstantVars.speakerItemIdForNewSession;
		
		Cursor cursor = getContentResolver().query(SrmUriMatcher.CONTENT_URI_TABLE_SPEAKERS_LEFTJOIN_SESSIONS, 
				selectColumns, wherePart, null, null);
		
		
		
        if (cursor != null && cursor.getCount()!=0) 
		{
	        TextView name = (TextView) findViewById(R.id.activity_speakerdetails_name_textvalue);
	        TextView accent = (TextView) findViewById(R.id.activity_speakerdetails_accent_textvalue);
	        TextView sex = (TextView) findViewById(R.id.activity_speakerdetails_sex_textvalue);
	        TextView birthday = (TextView) findViewById(R.id.activity_speakerdetails_birthday_textvalue);
	        TextView sessions = (TextView) findViewById(R.id.activity_speakerdetails_sessions_textvalue);
	        TextView scripts = (TextView) findViewById(R.id.activity_speakerdetails_scripts_textvalue);
	        
        	
			cursor.moveToFirst();
			
			String firstname = cursor.getString(cursor.getColumnIndex(TableSpeakers.COLUMN_FIRSTNAME));
			String surname = cursor.getString(cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_SURNAME));
			String fullName = firstname + " " + surname;
			name.setText(fullName);
			
			
			accent.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_ACCENT)));
			
			sex.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_SEX)));
			
			birthday.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableSpeakers.COLUMN_BIRTHDAY)));
			
			List<String> sessionlist = new ArrayList<String>();
			List<String> scriptlist = new ArrayList<String>();
			
			while(!cursor.isAfterLast())
			{
				String s1 = cursor.getString(cursor.getColumnIndexOrThrow("session_key_id"));
				if(!sessionlist.contains(s1)) sessionlist.add(s1);
				
				String s2 = cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SCRIPT_ID));
				if(!scriptlist.contains(s2)) scriptlist.add(s2);
				cursor.moveToNext();
			}
			if(!(sessionlist.toString().contains("null"))) sessions.setText(TextUtils.join(", ", sessionlist));
			if(!(scriptlist.toString().contains("null")))  scripts.setText(TextUtils.join(", ", scriptlist));
			
		}
        
        cursor.close();
	}
	
	private void fillScriptItem(LinearLayout scriptItem)
	{
		// query from db
		String[] selectColumns = {
				TableScripts.COLUMN_DESCRIPTION,
				TableSessions.COLUMN_SCRIPT_ID,
				TableSessions.COLUMN_SPEAKER_ID
		};
		
		String wherePart = "script_key_id=" + Utils.ConstantVars.scriptItemIdForNewSession;
		
		Cursor cursor = getContentResolver().query(SrmUriMatcher.CONTENT_URI_TABLE_SCRIPTS_LOJ_SESSIONS, 
				selectColumns, wherePart, null, null);
		
		
		
        if (cursor != null && cursor.getCount()!=0) 
		{
			
			Log.w(ActivityScriptDetails.class.getName(), " will create view of this activity.");
			
			setContentView(R.layout.linearlayout_activity_scriptdetails);
			
	        
	        TextView scriptid = (TextView) findViewById(R.id.activity_scriptdetails_scriptid_textvalue);
	        TextView scriptdesc = (TextView) findViewById(R.id.activity_scriptdetails_desc_textvalue);
	        TextView sessions = (TextView) findViewById(R.id.activity_scriptdetails_sessions_textvalue);
	        TextView speakers = (TextView) findViewById(R.id.activity_scriptdetails_speakers_textvalue);
	        
        	
			cursor.moveToFirst();
			
			String idText = cursor.getString(cursor.getColumnIndexOrThrow("script_key_id"));
			scriptid.setText("Script #" + idText);
			
			scriptdesc.setText(cursor.getString(cursor.getColumnIndexOrThrow(TableScripts.COLUMN_DESCRIPTION)));
			
			List<String> sessionlist = new ArrayList<String>();
			List<String> speakerlist = new ArrayList<String>();
			
			while(!cursor.isAfterLast())
			{
				String s1 = cursor.getString(cursor.getColumnIndexOrThrow("session_key_id"));
				if(!sessionlist.contains(s1)) sessionlist.add(s1);
				
				String s2 = cursor.getString(cursor.getColumnIndexOrThrow(TableSessions.COLUMN_SPEAKER_ID));
				if(!speakerlist.contains(s2)) speakerlist.add(s2);
				
				cursor.moveToNext();
			}
			if(!(sessionlist.toString().contains("null"))) sessions.setText(TextUtils.join(", ", sessionlist));
			if(!(speakerlist.toString().contains("null"))) speakers.setText(TextUtils.join(", ", speakerlist));
			
		}
        
        cursor.close();
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
		menu.setGroupVisible(R.id.bgroup_prerecording, true);
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
		    	break;
		     // actionbar buttons
        	case R.id.act_prerecording_button_test:
        		
	        		Utils.toastTextToUser(this, "start test recording");
	        		
	        		// send identiy to next activity
	        		// Intent newI = new Intent(this.getClass().getName(), ActivityStartRecording.class);
	        		// newI.putExtra("ACTIVITY_NAME", "session_details"); 
	        		// newI.putExtra("ITEM_ID", itemId);
	        		// (ActivityScriptDetails.this.startActivity(newI);
	        		
        		break;
        	default:
        		break;
    	}
	    return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) 
	{
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
