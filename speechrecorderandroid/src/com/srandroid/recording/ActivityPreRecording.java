/**
 * 
 */
package com.srandroid.recording;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.srandroid.speechrecorder.R;
import com.srandroid.database.TableScripts;
import com.srandroid.database.TableSessions;
import com.srandroid.database.TableSpeakers;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.main.ActivityScriptDetails;
import com.srandroid.util.Utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 */
public class ActivityPreRecording extends Activity 
{
	//private String itemId = null;
	
	
	private GridView gridView;
	

	// speaker item
	private TextView name;
	private TextView accent;
	private TextView sex;
	private TextView birthday;
	private TextView sessions1;
	private TextView scripts1;
	
	
	// script item
	private TextView scriptid;
	private TextView scriptdesc;
	private TextView sessions2;
	private TextView speakers2;
	

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
        
        String[] itemlist = {"SPEAKER_ITEM", "SCRIPT_ITEM"};
        
        gridView.setAdapter(new LocalAdapterForActPreRec(this, itemlist));
        
        
        gridView.setClickable(false);
        
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
		     // actionbar buttons
        	case R.id.act_prerecording_button_test:
        		
        		// insert new session
        		Uri uriNewSessionitem = insertNewSession();
        		
        		Log.w(this.getClass().getName(), "inserted new session item:" + uriNewSessionitem);
        		
        		Utils.ConstantVars.sessionItemIdForNewSession = 
        				uriNewSessionitem.getLastPathSegment();
        		
        		Log.w(this.getClass().getName(), "new session item id=" 
        				+ Utils.ConstantVars.sessionItemIdForNewSession);
        		
        		// insert new sections, XMLPullParser has bugs! 
        		// Use fake ScriptItem and List<RecordItem> in Utils
        		Utils.prepareItemsForNewSessions();
        		
        		
//        		Utils.parseScript(Utils.ConstantVars.exampleScriptFilepath, 
//        				Utils.ConstantVars.scriptItemForNewSession, 
//        				Utils.ConstantVars.recordItemListForNewSession);

        		
	    		Utils.toastTextToUser(this, "start test recording");
	    		
	    		Intent newI = new Intent(this, ActivityRecording.class);
	    		newI.putExtra("isTestRecording", true);
	    		
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
	
	
	private Uri insertNewSession() 
	{
		ContentValues values = new ContentValues();
		
		TableSessions.setValuesForInsertSessionItem(values, 
				Utils.ConstantVars.scriptItemIdForNewSession, 
				Utils.ConstantVars.speakerItemIdForNewSession);
		
		Uri uriNewSessionItem = 
				getContentResolver().insert(SrmUriMatcher.CONTENT_URI_TABLE_SESSIONS, values);
		
		return uriNewSessionItem;
		
	}


	private void fillSpeakerItem(View view)
	{
		name = (TextView) view.findViewById(R.id.activity_speakerdetails_name_textvalue);
        accent = (TextView) view.findViewById(R.id.activity_speakerdetails_accent_textvalue);
        sex = (TextView) view.findViewById(R.id.activity_speakerdetails_sex_textvalue);
        birthday = (TextView) view.findViewById(R.id.activity_speakerdetails_birthday_textvalue);
        sessions1 = (TextView) view.findViewById(R.id.activity_speakerdetails_sessions_textvalue);
        scripts1 = (TextView) view.findViewById(R.id.activity_speakerdetails_scripts_textvalue);

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
			if(!(sessionlist.toString().contains("null"))) sessions1.setText(TextUtils.join(", ", sessionlist));
			if(!(scriptlist.toString().contains("null")))  scripts1.setText(TextUtils.join(", ", scriptlist));
			
		}
        
        cursor.close();
	}
	
	private void fillScriptItem(View view)
	{

		scriptid = (TextView) view.findViewById(R.id.activity_scriptdetails_scriptid_textvalue);
        scriptdesc = (TextView) view.findViewById(R.id.activity_scriptdetails_desc_textvalue);
        sessions2 = (TextView) view.findViewById(R.id.activity_scriptdetails_sessions_textvalue);
        speakers2 = (TextView) view.findViewById(R.id.activity_scriptdetails_speakers_textvalue);
        
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
			cursor.moveToFirst();
			
			String idText = cursor.getString(cursor.getColumnIndexOrThrow("script_key_id"));
			scriptid.setText("Script #" + idText);
			
			String descText = cursor.getString(cursor.getColumnIndexOrThrow(TableScripts.COLUMN_DESCRIPTION));
			scriptdesc.setText(descText);
			
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
			if(!(sessionlist.toString().contains("null"))) sessions2.setText(TextUtils.join(", ", sessionlist));
			if(!(speakerlist.toString().contains("null"))) speakers2.setText(TextUtils.join(", ", speakerlist));
			
		}
        
        cursor.close();
	}

	
	protected class LocalAdapterForActPreRec extends BaseAdapter
	{
		private Context context;
		private String[] itemlist;
		
		public LocalAdapterForActPreRec(Context context, String[] itemlist)
		{
			this.context = context;
			this.itemlist = itemlist;
			
		}

		@Override
		public int getCount() 
		{
			return itemlist.length;
		}

		@Override
		public Object getItem(int id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			
			LinearLayout itemView = null;
			
			if(itemlist[position].equals("SPEAKER_ITEM"))
			{
				itemView = 
						(LinearLayout) (convertView == null
						? LayoutInflater.from(context).inflate(R.layout.linearlayout_activity_speakerdetails, parent, false)
								: convertView);

		        try
		        {
		        	fillSpeakerItem(itemView);
		        }
		        catch (Exception e) 
		        {
		            e.printStackTrace();
		        }

			}
			else if(itemlist[position].equals("SCRIPT_ITEM"))
			{
				
				itemView = 
						(LinearLayout) (convertView == null
						? LayoutInflater.from(context).inflate(R.layout.linearlayout_activity_scriptdetails, parent, false)
								: convertView);

		        try
		        {
					fillScriptItem(itemView);
		        }
		        catch (Exception e) 
		        {
		            e.printStackTrace();
		        }
		        
			}
			
			
			return itemView;
		}
		
	}

}
