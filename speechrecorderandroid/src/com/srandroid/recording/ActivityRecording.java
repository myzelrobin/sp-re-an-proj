/**
 * 
 */
package com.srandroid.recording;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.srandroid.database.TableRecords.RecordItem;
import com.srandroid.database.TableRecords;
import com.srandroid.database.TableScripts;
import com.srandroid.database.TableScripts.ScriptItem;
import com.srandroid.database.TableSessions;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.main.ActivitySessionDetails;
import com.srandroid.recording.ActivityPreRecording.LocalAdapterForActPreRec;
import com.srandroid.speechrecorder.R;
import com.srandroid.util.SrmRecorder;
import com.srandroid.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ClipData.Item;
import android.content.UriMatcher;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 */
public class ActivityRecording extends Activity 
{

	private boolean isTestRecroding;
	
	private GridView gridView;
	

	
	private Button bRecord;
	private int isBRecordClicked = 0;
	private Button bPlay;
	private Button bNext;
	private Button bPrev;
	
	private TextView instrText;
	private TextView promptText;
	
	
	private ImageView imageCircle1;
	private ImageView imageCircle2;
	private ImageView imageCircle3;
	
	private LocalAdapterForActRecording adapter;
	
	private OnClickListenerForRecording listener;
	
	private SrmRecorder srmRecorder;
	
	private int recItemIndex = 0;
	
	private ScriptItem scriptItem;
	private List<RecordItem> recItemsList;
	
	private String recordFilepath;
	
	private Handler handler;
	
	private static Activity thisAct;
	/**
	 * 
	 */
	public ActivityRecording() 
	{
		// TODO Auto-generated constructor stub
	}
	
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		thisAct = this;
		listener = new OnClickListenerForRecording();
		
		scriptItem = Utils.ConstantVars.scriptItemForNewSession;
		recItemsList = Utils.ConstantVars.recordItemListForNewSession;
		
		Bundle extras = getIntent().getExtras(); 

		if (extras != null) 
		{
			// Act PreRecoding and Act StartRecording send an extra
	        isTestRecroding = extras.getBoolean("isTestRecording");
	        
	        Log.w(this.getClass().getName(), "receive extra isTestRecording=" + isTestRecroding);
		}
		
		// orientation changed
        if(savedInstanceState != null)
        {
        	//itemId = savedInstanceState.getString("itemId");
        }
        
        if(isTestRecroding)
        {
        	// Test Recording
        	Log.w(this.getClass().getName(), "from PreRecording, start creating Test Recording");
        	
        	setContentView(R.layout.gridviewlayout_act_recording);
        	
        	setTitle(getResources().getString(R.string.act_recording_title_testrec));
        	
        	recItemIndex = 0;
        	
        	gridView = (GridView) findViewById(R.id.id_gridview_act_recoding);
            
            String[] arealist = {"TEXT_AREA", "CONTROL_AREA_FOR_USER"};
            
            adapter = new LocalAdapterForActRecording(this, arealist);
            
            gridView.setAdapter(adapter);
            
            gridView.setClickable(false);
        }
        else
    	{
        	// Recording
        	Log.w(this.getClass().getName(), "from StartRecording, start creating Recording");
        	
        	setContentView(R.layout.gridviewlayout_act_recording);
        	
        	setTitle(getResources().getString(R.string.act_recording_title));
        	
        	recItemIndex = 1;
        	
        	gridView = (GridView) findViewById(R.id.id_gridview_act_recoding);
            
            String[] arealist = {"TEXT_AREA", "CONTROL_AREA_FOR_SPEAKER"};
            
            adapter = new LocalAdapterForActRecording(this, arealist);
            
            gridView.setAdapter(adapter);
            
            gridView.setClickable(false);
            
    	}
        
        
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
		if(isTestRecroding)
			getMenuInflater().inflate(R.menu.menu_items, menu);
		return true;
	}
	
	/**
	 * Updates the menu items in action bar when the "drawer items" is closed
	 * 
	 * @param menu
	 * @return
	 */
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) 
	{
		menu.setGroupVisible(R.id.bgroup_recording, true);
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
        	case R.id.act_recoding_button_done:
        		
        		// a method to insert new session
        		if(isTestRecroding)
        		{
        			// In Act TestRecoding
    	    		Intent newI = new Intent(this, ActivityStartRecording.class);
    	        	this.startActivity(newI);
    	        	
        		}
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
	
	private void fillTextArea()
	{
		instrText = (TextView) gridView.findViewById(R.id.act_recording_text_intro_textvalue);
		instrText.setText(recItemsList.get(recItemIndex).recinstructions);
		
		promptText = (TextView) gridView.findViewById(R.id.act_recording_text_prompt_textvalue);
		promptText.setText(recItemsList.get(recItemIndex).recprompt);
		
	}
	
	private void fillControlArea(View areaView)
	{
		bRecord = (Button) areaView.findViewById(R.id.act_recording_control_button_record);
		bRecord.setEnabled(true);
    	bPlay = (Button) areaView.findViewById(R.id.act_recording_control_button_play);
    	bPlay.setEnabled(false);
    	bNext = (Button) areaView.findViewById(R.id.act_recording_control_button_next);
    	bPrev = (Button) areaView.findViewById(R.id.act_recording_control_button_previous);
		bPrev.setEnabled(true);
		bNext.setEnabled(true);
    	
    	imageCircle1 = (ImageView) areaView.findViewById(R.id.act_recording_control_circle1);
    	imageCircle2 = (ImageView) areaView.findViewById(R.id.act_recording_control_circle2);
    	imageCircle3 = (ImageView) areaView.findViewById(R.id.act_recording_control_circle3);
    	
    	if(!isTestRecroding)
    	{
    		// Act Recording
    		bPrev.setVisibility(View.INVISIBLE);
    		bNext.setVisibility(View.INVISIBLE);
    		bPlay.setVisibility(View.INVISIBLE);
    		
    		bRecord.setOnClickListener(listener);
    	}
    	else
    	{
    		bRecord.setOnClickListener(listener);
    		bPlay.setOnClickListener(listener);
    		bNext.setOnClickListener(listener);
    		bPrev.setOnClickListener(listener);
    	}
    	
	}
	
	private void updateTextArea(View parentView, String sIntro, String sContent)
	{
		if((sIntro.length() != 0) && (sContent.length() != 0))
    	{
			instrText = (TextView) parentView.findViewById(R.id.act_recording_text_intro_textvalue);
			instrText.setText(sIntro);
			promptText = (TextView) parentView.findViewById(R.id.act_recording_text_prompt_textvalue);
			promptText.setText(sContent);
    	}
	}
	
	

	private Uri insertRecord(RecordItem recItem, String recFilepath) 
	{
		Log.w(ActivityRecording.class.getName(), "insertRecord() will insert a record");
		
		ContentValues values = new ContentValues();
		
		TableRecords.setValuesForInsertRecordItem(values,
				Utils.ConstantVars.sessionItemIdForNewSession,
				Utils.ConstantVars.speakerItemIdForNewSession,
				Utils.ConstantVars.scriptItemIdForNewSession, 
				recFilepath, 
				recItem.recinstructions, 
				recItem.recprompt, 
				recItem.reccomment, 
				recItem.itemcode);
		
		Uri uriNewRecItem = getContentResolver().insert(SrmUriMatcher.CONTENT_URI_TABLE_RECORDS, values);
		
		Log.w(ActivityRecording.class.getName(), "insertRecord() inserted a record with uri=" + uriNewRecItem);
		
		return uriNewRecItem;
	}
	
	protected class LocalAdapterForActRecording extends BaseAdapter
	{
		private Context context;
		private String[] arealist;
		
		public LocalAdapterForActRecording(Context context, String[] arealist)
		{
			this.context = context;
			this.arealist = arealist;
			
		}

		@Override
		public int getCount() 
		{
			return arealist.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}
		

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LinearLayout areaView = null;
			
			if(arealist[position].equals("TEXT_AREA"))
			{
				areaView = 
						(LinearLayout) (convertView == null
						? LayoutInflater.from(context).
								inflate(R.layout.linearlayout_act_recording_text_area, parent, false)
						: convertView);
				
				try
		        {
		        	fillTextArea();
		        }
		        catch (Exception e) 
		        {
		            e.printStackTrace();
		        }

			}
			else if(arealist[position].equals("CONTROL_AREA_FOR_USER"))
			{
				
				areaView = 
						(LinearLayout) (convertView == null
						? LayoutInflater.from(context).
								inflate(R.layout.linearlayout_act_recording_control_area, parent, false)
						: convertView);

		        try
		        {
		        	fillControlArea(areaView);
		        }
		        catch (Exception e) 
		        {
		            e.printStackTrace();
		        }
		        
			}
			else if(arealist[position].equals("CONTROL_AREA_FOR_SPEAKER"))
			{
				
				areaView = 
						(LinearLayout) (convertView == null
						? LayoutInflater.from(context).
								inflate(R.layout.linearlayout_act_recording_control_area, parent, false)
								: convertView);

		        try
		        {
		        	fillControlArea(areaView);
		        }
		        catch (Exception e) 
		        {
		            e.printStackTrace();
		        }
		        
		        
			}
			
			
			return areaView;
		}
		
	}

	
	protected class OnClickListenerForRecording implements OnClickListener
	{
		
		
		
		public OnClickListenerForRecording() 
		{
			super();
		}

		@Override
		public void onClick(View v) 
		{
			int viewId = v.getId();
			
			switch (viewId) 
			{
				case R.id.act_recording_control_button_record:
					
					if(isBRecordClicked == 0)
					{
						
						
						
						if(isTestRecroding) // test recoding
							Log.w(ActivityRecording.class.getName(), "test recording click record");
						else
							Log.w(ActivityRecording.class.getName(), "recording click record");
						
						// start recording
						
						// new recorder
						if(isTestRecroding)
							srmRecorder = new SrmRecorder(Utils.ConstantVars.REC_FILES_DIR_EXT_PATH 
								+ File.separator + "sessionID-" + Utils.ConstantVars.sessionItemIdForNewSession + "_"
								+ scriptItem.scriptName + File.separator + "test", 
								recItemsList.get(recItemIndex).itemcode);
						else 
							srmRecorder = new SrmRecorder(Utils.ConstantVars.REC_FILES_DIR_EXT_PATH 
									+ File.separator + "sessionID-" + Utils.ConstantVars.sessionItemIdForNewSession + "_"
									+ scriptItem.scriptName, 
									recItemsList.get(recItemIndex).itemcode);
						
						
						
						
						
						srmRecorder.startRecording();
						
						recordFilepath = srmRecorder.getAudioFile();
						Log.w(ActivityRecording.class.getName(), "created new Recorder, start recording");
						Log.w(ActivityRecording.class.getName(), "created new Recorder:" + recordFilepath);
						

						// update ui
						isBRecordClicked = 1;						
						
						// buttons
						bRecord.setText(getResources().getString(R.string.stop));
						bRecord.setEnabled(false);
						bPlay.setEnabled(false);
						bPrev.setEnabled(false);
						bNext.setEnabled(false);
						
						// change images
			        	imageCircle1.setImageResource(R.drawable.icon_circle_red);
						imageCircle2.setImageResource(R.drawable.icon_circle_red);
						imageCircle3.setImageResource(R.drawable.icon_circle_red);
						
						//update images
						handler = new Handler(); 
						    handler.postDelayed(new Runnable() 
						    { 
						         public void run() 
						         { 
						        	imageCircle1.setImageResource(R.drawable.icon_circle_yellow);
									imageCircle2.setImageResource(R.drawable.icon_circle_yellow);
									imageCircle3.setImageResource(R.drawable.icon_circle_yellow);
									
									Handler handler2 = new Handler(); 
								    handler2.postDelayed(new Runnable() 
								    { 
								         public void run() 
								         { 
								        	imageCircle1.setImageResource(R.drawable.icon_circle_green);
											imageCircle2.setImageResource(R.drawable.icon_circle_green);
											imageCircle3.setImageResource(R.drawable.icon_circle_green);
											bRecord.setEnabled(true);
								         } 
								    }, (Integer.parseInt(recItemsList.get(recItemIndex).prerecdelay)) / 2);
						         } 
						    }, (Integer.parseInt(recItemsList.get(recItemIndex).prerecdelay)) / 2); 
					}
					else if (isBRecordClicked == 1)
					{
						// stop recording
						isBRecordClicked = 0;
						bRecord.setEnabled(false);
						
					    if(isTestRecroding)
					    {
					    	// test recording
					    	
					    	Log.w(ActivityRecording.class.getName(), "test recording: click stop");
					    	
					    	
						    handler = new Handler(); 
						    handler.postDelayed(new Runnable() 
						    { 
						         public void run() 
						         {
						        	 srmRecorder.stopRecording();
								    
						        	 // update GUI
						        	recItemIndex++;
								    if(recItemIndex > (recItemsList.size() -1)) recItemIndex = recItemsList.size() -1;
								    
							    	imageCircle1.setImageResource(R.drawable.icon_circle_red);
							    	imageCircle2.setImageResource(R.drawable.icon_circle_yellow);
							    	imageCircle3.setImageResource(R.drawable.icon_circle_green); 
							    	
						        	 updateTextArea(gridView, 
												recItemsList.get(recItemIndex).recinstructions, 
												recItemsList.get(recItemIndex).recprompt);

									bRecord.setText(getResources().getString(R.string.record));
									bRecord.setEnabled(true);
									bPlay.setEnabled(true);
									bPrev.setEnabled(true);
									bNext.setEnabled(true);
						         } 
						    }, Integer.parseInt(recItemsList.get(recItemIndex).postrecdelay));
						}
					    else 
					    {
					    	// recording 
					    	
					    	Log.w(ActivityRecording.class.getName(), "recording: click stop");
					    	
					    	bRecord.setEnabled(false);
					    	
					    	handler = new Handler(); 
						    handler.postDelayed(new Runnable() 
						    { 
						         public void run() 
						         { 
						        	 

							    	srmRecorder.stopRecording();
									
							    	// insert record
							    	Uri uriNewRecordItem = insertRecord(recItemsList.get(recItemIndex), 
								    		srmRecorder.getAudioFile());
							    	
							    	Log.w(ActivityRecording.class.getName(), "recording click stop, inserted record id="
							    			+ uriNewRecordItem.getLastPathSegment());
							    	
							    	
							    	recItemIndex++;
									if(recItemIndex > recItemsList.size() -1)
								    {
								    	Utils.toastTextToUser(thisAct, "finished recording!");
								    	
								    	Intent newI = new Intent(thisAct, ActivityFinishRecording.class);
							        	thisAct.startActivity(newI);
								    }
									else 
									{

								    	// update GUI
								    	imageCircle1.setImageResource(R.drawable.icon_circle_red);
								    	imageCircle2.setImageResource(R.drawable.icon_circle_yellow);
								    	imageCircle3.setImageResource(R.drawable.icon_circle_green);
								    	
								    	 updateTextArea(gridView, 
													recItemsList.get(recItemIndex).recinstructions, 
													recItemsList.get(recItemIndex).recprompt);

										bRecord.setText(getResources().getString(R.string.record));
										bRecord.setEnabled(true);
										bPlay.setEnabled(true);
										bPrev.setEnabled(true);
										bNext.setEnabled(true);
									}
							    	
									
						         } 
						    }, Integer.parseInt(recItemsList.get(recItemIndex).postrecdelay));
					    	
					    }
					    
					}
					
					break;
				
				case R.id.act_recording_control_button_play:
					// intent 
					Log.w(ActivityRecording.class.getName(), "click play");
					// play the record
					try 
					{
						Utils.playRecord(thisAct, srmRecorder.getAudioFile());
					} 
					catch (ActivityNotFoundException e) 
					{
						Log.w(ActivityRecording.class.getName(), 
								"Utils.playRecord() throws Exceptions " + e.getMessage());
					}
					
					bPlay.setEnabled(false);
					break;
					
				case R.id.act_recording_control_button_previous:
					// new recording
					Log.w(ActivityRecording.class.getName(), "click <<");
					recItemIndex--;
					Log.w(ActivityRecording.class.getName(), "<<, recItemIndex=" + recItemIndex);
					if(recItemIndex<0) recItemIndex = 0;
					Log.w(ActivityRecording.class.getName(), "<<, recItemIndex=" + recItemIndex);
					bPlay.setEnabled(false);
					
					updateTextArea(gridView, 
							recItemsList.get(recItemIndex).recinstructions, 
							recItemsList.get(recItemIndex).recprompt);
					break;
				
				case R.id.act_recording_control_button_next:
					// new recording
					
					Log.w(ActivityRecording.class.getName(), "click >>");
					recItemIndex++;
					Log.w(ActivityRecording.class.getName(), ">>, recItemIndex=" + recItemIndex);
					if(recItemIndex>(recItemsList.size() -1)) recItemIndex = recItemsList.size() -1;
					Log.w(ActivityRecording.class.getName(), ">>, recItemIndex=" + recItemIndex);
					bPlay.setEnabled(false);
					
					updateTextArea(gridView, 
							recItemsList.get(recItemIndex).recinstructions, 
							recItemsList.get(recItemIndex).recprompt);
					
					break;
				default:
					break;
			}
			
		}
		
	}


}
