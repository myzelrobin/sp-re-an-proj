/**
 * 
 */
package com.srandroid.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.srandroid.speechrecorder.R;
import com.srandroid.database.TableScripts;
import com.srandroid.database.TableSessions;
import com.srandroid.database.TableSpeakers;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.main.ActivityScriptDetails;
import com.srandroid.network.SrmDropboxHandler;
import com.srandroid.network.SrmDropboxHandler.GetDropboxFileInfosTask;
import com.srandroid.network.SrmNetworkHandler;
import com.srandroid.util.Utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 */
public class ActivityDownloadScripts extends Activity 
{
	//private String itemId = null;
	
	private static final String LOGTAG = ActivityDownloadScripts.class.getName();
	private Context context;
	
	// GUI
	private GridView gridView;
	
	private LocalAdapterDownloadScripts adapter;
	
//	// script item
//	private TextView textScriptId;
//	private TextView textScriptDesc; // here shows file size
//	private TextView textIsDownloaded;
//	private Button butDownload;
	
	
	// network
	private SrmDropboxHandler dropboxHandler;
	
	
	// informations
	private ArrayList<Entry> itemList;
	
	AsyncTask<Void, Long, Boolean> getScriptFilesInfoTask;
	
	/**
	 * 
	 */
	public ActivityDownloadScripts() 
	{
		this.context = this;
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
        
        dropboxHandler = new SrmDropboxHandler(context, this);
        
        Log.w(LOGTAG, "start creating GUI of " + ActivityDownloadScripts.class.getSimpleName());
        
        setContentView(R.layout.gridviewlayout_act_download_scripts);
        
        gridView = (GridView) findViewById(R.id.id_gridview_layout_act_download_scripts);
        
        adapter = new LocalAdapterDownloadScripts(context, null);
        gridView.setAdapter(adapter);
        gridView.setClickable(false);
        
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
		
		if(dropboxHandler.dropbox != null 
				&& dropboxHandler.dropbox.getSession().authenticationSuccessful())
		{
			dropboxHandler.finishAuthen(dropboxHandler.dropbox.getSession());				
		}
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
		menu.setGroupVisible(R.id.bgroup_downloadscripts, true);
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
        
	        case android.R.id.home:
	        	Log.w(LOGTAG, "user clicked button home");
	        	
		        NavUtils.navigateUpFromSameTask(this);
		        return true;
        
		    // actionbar buttons
        	case R.id.act_downloadscripts_button_downloadall:
		    		
		    	Log.w(LOGTAG, "user clicked button download all, start download all scripts");
		    	

		        // get script files
		        dropboxHandler.createDropboxAPI();
		        
		        getScriptFilesInfoTask = new GetDropboxFileInfosTask(
		        								context,
		        								this,
		        								dropboxHandler.dropbox,
		        								SrmDropboxHandler.FOLDER_SCRIPTS_PATH,
		        								adapter)
		        							.execute();
		    		
	    		break;
        	
        	case R.id.act_downloadscripts_button_exit:
	    		
	    		Log.w(LOGTAG, "user clicked button exit, app exits");
	    		
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
	
//	private Uri insertNewScriptIntoDB() 
//	{
//		ContentValues values = new ContentValues();
//		
//		TableSessions.setValuesForInsertSessionItem(values, 
//				Utils.ConstantVars.scriptItemIdForNewSession, 
//				Utils.ConstantVars.speakerItemIdForNewSession);
//		
//		Uri uriNewSessionItem = 
//				getContentResolver().insert(SrmUriMatcher.CONTENT_URI_TABLE_SESSIONS, values);
//		
//		return uriNewSessionItem;
//		
//	}
	

	/**
	 * 
	 *
	 */
	public static class LocalAdapterDownloadScripts extends BaseAdapter
	{
		private final String LOGTAG = LocalAdapterDownloadScripts.class.getName();
		
		private Context context;
		private Entry dirEntry;
		private ArrayList<Entry> entryList;
		
		public LocalAdapterDownloadScripts(Context context, Entry dirEntry)
		{
			Log.w(LOGTAG, "Constructor will create a LocalAdapterDownloadScripts");
			
			this.context = context;
			this.dirEntry = dirEntry;
			if(dirEntry == null) entryList = null;
			else if(dirEntry.isDir)
			{
				entryList = new ArrayList<Entry>();
		        
		        int i = 0;
		        for (Entry entry: dirEntry.contents) 
		        {
		            entryList.add(i, entry);                   
		            //dir = new ArrayList<String>();
		            i++;
		        }
			}
		}

		@Override
		public int getCount() 
		{
			// need an ArrayList<Entry>
			return 0;
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
			
			Log.w(LOGTAG, "getView() will create itemview position=" + position);
			
			LinearLayout itemView = null;
			
			itemView = (LinearLayout) 
						(convertView == null
						? LayoutInflater.from(context)
								.inflate(R.layout.linearlayout_item_script_in_server, parent, false)
						: convertView);

		        try
		        {
					fillScriptItem(itemView, entryList, position);
		        }
		        catch (Exception e) 
		        {
		            e.printStackTrace();
		        }
			
			return itemView;
		}
		
		private void fillScriptItem(View view, ArrayList<Entry> entryList, int position)
		{
			Log.w(LOGTAG, "fillScriptItem() will fill itemview position=" + position);
			
			if(entryList != null)
			{
				Entry entry = entryList.get(position);

				TextView textScriptId = (TextView) view.findViewById(R.id.itemScriptInServer_textIdValue);
				textScriptId.setText(entry.fileName());
				
				TextView textScriptDesc = (TextView) view.findViewById(R.id.itemScriptInServer_textDesciptionValue);
		        textScriptDesc.setText("file size is " + entry.size);
		        
		        TextView textIsDownloaded = (TextView) view.findViewById(R.id.itemScriptInServer_textIsDownloadedValue);
		        textIsDownloaded.setText("undownlaoded");
		        
		        Button butDownload = (Button) view.findViewById(R.id.itemScriptInServer_buttonDownload);
		        // clicklistener?
			}
			else
			{
				Log.w(LOGTAG, "fillScriptItem() entryList=null, no fill");
			}
			
//	        
//	        
//			// query from db
//			String[] selectColumns = {
//					TableScripts.COLUMN_DESCRIPTION,
//					TableSessions.COLUMN_SCRIPT_ID,
//					TableSessions.COLUMN_SPEAKER_ID
//			};
//			
//			String wherePart = "script_key_id=" + Utils.ConstantVars.scriptItemIdForNewSession;
//			
//			Cursor cursor = 
//					getContentResolver().query(
//							SrmUriMatcher.CONTENT_URI_TABLE_SCRIPTS_LOJ_SESSIONS, 
//							selectColumns, wherePart, null, null);
//			
//	        if (cursor != null && cursor.getCount()!=0) 
//			{
//				cursor.moveToFirst();
//				
//				String idText = cursor.getString(cursor.getColumnIndexOrThrow("script_key_id"));
//				scriptid.setText("Script #" + idText);
//				
//				String descText = cursor.getString(
//						cursor.getColumnIndexOrThrow(TableScripts.COLUMN_DESCRIPTION));
//				scriptdesc.setText(descText);
//				
//				List<String> sessionsList = new ArrayList<String>();
//				List<String> speakersList = new ArrayList<String>();
//				
//				while(!cursor.isAfterLast())
//				{
//					
//					cursor.moveToNext();
//				}
//				
//			}
//	        
//	        cursor.close();
		}
		
	}

}
