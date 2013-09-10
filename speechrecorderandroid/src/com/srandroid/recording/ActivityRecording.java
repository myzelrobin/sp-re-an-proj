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

import android.R.drawable;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 */
public class ActivityRecording extends Activity 
	implements OnClickListener
{
	
	private static final String LOGTAG = ActivityRecording.class.getName();

	private boolean isTestRecroding;
	
	// private Button bRecord;
	private int isBRecordClicked = 0;
	private Button bPlay;
	private Button bNext;
	private Button bPrev;
	
	private ImageButton imageButtonRecord;
	
	private TextView instrText;
	private TextView promptText;
	private ImageView promptImage;
	
	private static int heightInstrTextView = 100;
	
	private static int widthPromptTextView = 800;
	private static int heightPromtTextView = 300;
	private static int heightPromtTextView2 = 400;
	
	
	// private ImageView imageCircle1;
	
	private SrmRecorder srmRecorder;
	
	private int recItemIndex = 0;
	
	private ScriptItem scriptItem;
	private List<RecordItem> recItemsList;
	
	private String recordFilepath;
	
	
	private static Activity thisAct;
	/**
	 * 
	 */
	public ActivityRecording() 
	{
		
	}
	
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		thisAct = this;
		
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
        	
        	setContentView(R.layout.linearlayout_act_recording);
        	
        	setTitle(getResources().getString(R.string.act_recording_title_testrec));
        	
        	fillOneArea();
        }
        else
    	{
        	// Recording
        	Log.w(this.getClass().getName(), "from StartRecording, start creating Recording");
        	
        	setContentView(R.layout.linearlayout_act_recording);
        	
        	setTitle(getResources().getString(R.string.act_recording_title));
        	
        	fillOneArea();
        	
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
	
	

	@Override
	public void onClick(View v) 
	{
		int id = v.getId();
		
		
		switch (id) 
		{
			//case R.id.act_recording_button_record:
		
			case R.id.act_recording_imagebutton_record:
				if(isTestRecroding)
				{// test recording
					if(isBRecordClicked == 0)
					{
						startTestRecording();
					}
					else if (isBRecordClicked == 1)
					{
						stopTestRecording();
					}
				}
				else 
				{// recording
					if(isBRecordClicked == 0)
					{
						startRecording();
					}
					else if (isBRecordClicked == 1)
					{
						stopRecording();
					}
					
				}
				break;
				
			case R.id.act_recording_button_play:
				
				Log.w(ActivityRecording.class.getName(), "test recording, clicked play");
				// intent play the record
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
			
			case R.id.act_recording_button_previous:
				
				Log.w(ActivityRecording.class.getName(), "test recording, clicked <<, prev recording item");
				recItemIndex--;
				if(recItemIndex<0)
				{
					recItemIndex = 0;
					Utils.toastTextToUser(thisAct, "the first record item");
				}
				Log.w(ActivityRecording.class.getName(), "<<, recItemIndex=" + recItemIndex);
				
				bPlay.setEnabled(false);
				
				updateInstrAndPrompt(recItemsList.get(recItemIndex));
				
				//instrText.setText(recItemsList.get(recItemIndex).recinstructions);
				//promptText.setText(recItemsList.get(recItemIndex).recprompt);
				
				break;
				
			case R.id.act_recording_button_next:
				
				Log.w(ActivityRecording.class.getName(), "test recording, clicked >>, next recording item");
				recItemIndex++;
				if(recItemIndex>(recItemsList.size() -1))
				{
					recItemIndex = recItemsList.size() -1;
					Utils.toastTextToUser(thisAct, "the last record item");
				}
				Log.w(ActivityRecording.class.getName(), ">>, recItemIndex=" + recItemIndex);
				
				bPlay.setEnabled(false);
				
				updateInstrAndPrompt(recItemsList.get(recItemIndex));
				
				//instrText.setText(recItemsList.get(recItemIndex).recinstructions);
				//promptText.setText(recItemsList.get(recItemIndex).recprompt);
				
				break;
		
			default:
				break;
		}
		
	}

	private void fillOneArea() 
	{
		if(isTestRecroding)
		{
			recItemIndex = 0;
			
			bPlay = (Button) findViewById(R.id.act_recording_button_play);
			bPlay.setEnabled(false);
			bPlay.setOnClickListener(this);
			
			bPrev = (Button) findViewById(R.id.act_recording_button_previous);
			bPrev.setEnabled(true);
			bPrev.setOnClickListener(this);
			
			bNext = (Button) findViewById(R.id.act_recording_button_next);
			bNext.setEnabled(true);
			bNext.setOnClickListener(this);
			
		}
		else 
		{
			recItemIndex = 1;
			
			bPlay = (Button) findViewById(R.id.act_recording_button_play);
			bPlay.setVisibility(View.INVISIBLE);
			
			bPrev = (Button) findViewById(R.id.act_recording_button_previous);
			bPrev.setVisibility(View.INVISIBLE);
			
			bNext = (Button) findViewById(R.id.act_recording_button_next);
			bNext.setVisibility(View.INVISIBLE);
		}
		
		instrText = (TextView) findViewById(R.id.act_recording_text_intro_textvalue);
		promptText = (TextView) findViewById(R.id.act_recording_text_prompt_textvalue);
		instrText.setText(recItemsList.get(recItemIndex).recinstructions);
		promptText.setText(recItemsList.get(recItemIndex).recprompt);
		
		promptImage = (ImageView) findViewById(R.id.act_recording_prompt_image);
		
//		bRecord = (Button) findViewById(R.id.act_recording_button_record);
//		bRecord.setEnabled(true);
//		bRecord.setOnClickListener(this);
//		
//		imageCircle1 = (ImageView) findViewById(R.id.act_recording_signal_circle);
//    	imageCircle1.setImageResource(R.drawable.icon_circle_red);
    	
		imageButtonRecord = (ImageButton) findViewById(R.id.act_recording_imagebutton_record);
		imageButtonRecord.setEnabled(true);
		imageButtonRecord.setOnClickListener(this);
	}

	private Uri insertRecordItemToDB(RecordItem recItem, String recFilepath) 
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
				recItem.itemcode,
				recItem.itemType);
		
		Uri uriNewRecItem = getContentResolver().insert(SrmUriMatcher.CONTENT_URI_TABLE_RECORDS, values);
		
		Log.w(ActivityRecording.class.getName(), "insertRecord() inserted a record with uri=" + uriNewRecItem);
		
		return uriNewRecItem;
	}
	
	private void updateInstrAndPrompt(RecordItem recItem)
	{
		LinearLayout.LayoutParams whNewPrompt = 
			new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 
				heightPromtTextView);
		LinearLayout.LayoutParams whZero = new LinearLayout.LayoutParams(0, 0);
		
		if(recItem.itemType == RecordItem.TYPE_TEXT)
		{
			instrText.setText(recItem.recinstructions);
			promptText.setText(recItem.recprompt);
			
			promptText.setLayoutParams(whNewPrompt);
			promptImage.setLayoutParams(whZero);
		}
		else if(recItem.itemType == RecordItem.TYPE_IMAGE)
		{
			instrText.setText(recItem.recinstructions);
			
			File imgFile = new  File(recItem.recprompt);
			if(imgFile.exists())
			{
				Log.w(LOGTAG, "updateInstrAndPrompt() will update prompt with image, filepath:" 
						+ recItem.recprompt); 
				
			    Bitmap myBitmap = BitmapFactory.decodeFile(recItem.recprompt);

			    promptImage.setImageBitmap(myBitmap);
			}
			else 
			{
				Log.w(LOGTAG, "updateInstrAndPrompt() image file does not exist, filepath=" 
						+ recItem.recprompt); 
			}
			
			promptText.setLayoutParams(whZero);
			promptImage.setLayoutParams(whNewPrompt);
		}
		else if(recItem.itemType == RecordItem.TYPE_SOUND)
		{
			
		}
		
	}
	
	
	private void enlargePromptImage()
	{
		float fromXscale = 1;
		float toXscale = heightPromtTextView2 / heightPromtTextView;
		float fromYscale = 1;
		float toYscale = heightPromtTextView2 / heightPromtTextView;
		
		Animation scale = new ScaleAnimation(fromXscale, toXscale, fromYscale, toYscale, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scale.setDuration( Integer.parseInt(recItemsList.get(recItemIndex).prerecdelay) );
		
		float fromX = promptImage.getX();
		float toX = promptImage.getX();
		float fromY = promptImage.getY();
		float toY = promptImage.getY() - heightInstrTextView;
		
		Animation translate = new TranslateAnimation(fromX, toX, fromY, toY);
		translate.setDuration( Integer.parseInt(recItemsList.get(recItemIndex).prerecdelay) );
		
		AnimationSet animSet = new AnimationSet(true);
		animSet.setFillEnabled(true);
		animSet.addAnimation(scale);
		animSet.addAnimation(translate);
		
		
		
		LinearLayout.LayoutParams whZero = new LinearLayout.LayoutParams(0, 0);
		instrText.setLayoutParams(whZero);
		
		//LinearLayout.LayoutParams whNewLarge = new LinearLayout.LayoutParams(
		//		LinearLayout.LayoutParams.MATCH_PARENT, 
		//		LinearLayout.LayoutParams.MATCH_PARENT);
		// promptImage.setLayoutParams(whNewLarge);
		
		// Launching animation set
		promptImage.startAnimation(animSet);
	}
	
	private void shrinkPromptImage()
	{
		float fromXscale = 1;
		float toXscale = 1;
		float fromYscale = 1;
		float toYscale = heightPromtTextView / heightPromtTextView2;
		
		Animation scale = new ScaleAnimation(fromXscale, toXscale, fromYscale, toYscale, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scale.setDuration( Integer.parseInt(recItemsList.get(recItemIndex).postrecdelay) );
		
		float fromX = promptImage.getX();
		float toX = promptImage.getX();
		float fromY = promptImage.getY();
		float toY = promptImage.getY() + heightInstrTextView;
		
		Animation translate = new TranslateAnimation(fromX, toX, fromY, toY);
		translate.setDuration( Integer.parseInt(recItemsList.get(recItemIndex).postrecdelay) );
		
		AnimationSet animSet = new AnimationSet(true);
		animSet.setFillEnabled(true);
		animSet.addAnimation(scale);
		animSet.addAnimation(translate);
		
		
		//LinearLayout.LayoutParams whOriginPromt = new LinearLayout.LayoutParams(
		//		LinearLayout.LayoutParams.MATCH_PARENT, 
		//		heightPromtTextView);
		// promptImage.setLayoutParams(whOriginPromt);
		
		promptImage.startAnimation(animSet);
		
		LinearLayout.LayoutParams whOriginInstr = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 
				heightInstrTextView);
		instrText.setLayoutParams(whOriginInstr);
	}
	
	
	private void startTestRecording()
	{
		Log.w(ActivityRecording.class.getName(), "test recording, clicked record, start test recording");
		
		// new recorder
		srmRecorder = new SrmRecorder(Utils.ConstantVars.REC_FILES_DIR_EXT_PATH 
				+ File.separator + "sessionID-" + Utils.ConstantVars.sessionItemIdForNewSession + "_"
				+ scriptItem.scriptName + File.separator + "test", 
				recItemsList.get(recItemIndex).itemcode);
		
		srmRecorder.startRecording();
		
		recordFilepath = srmRecorder.getAudioFile();
		Log.w(ActivityRecording.class.getName(), "test recording, created new Recorder:" + recordFilepath);
		

		// update ui
		isBRecordClicked = 1;						
		
		// buttons
		// bRecord.setText(getResources().getString(R.string.stop));
		// bRecord.setEnabled(false);
		
		imageButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.icon_circle_red));
		imageButtonRecord.setEnabled(false);
		
		bPlay.setEnabled(false);
		bPrev.setEnabled(false);
		bNext.setEnabled(false);
		
		// change images
    	// imageCircle1.setImageResource(R.drawable.icon_circle_red);
		
		//update images
		Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() 
	    { 
	         public void run() 
	         { 
	        	// imageCircle1.setImageResource(R.drawable.icon_circle_yellow);
				imageButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.icon_circle_yellow));
	        	
				Handler handler2 = new Handler(); 
			    handler2.postDelayed(new Runnable() 
			    { 
			         public void run() 
			         { 
			        	// imageCircle1.setImageResource(R.drawable.icon_circle_green);
						// bRecord.setEnabled(true);
			        	imageButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.icon_circle_green));
			        	imageButtonRecord.setEnabled(true); 
			         } 
			    }, ( Integer.parseInt(recItemsList.get(recItemIndex).prerecdelay)) / 2 );
	         } 
	    }, ( Integer.parseInt(recItemsList.get(recItemIndex).prerecdelay)) / 2 ); 
	}
	
	private void stopTestRecording()
	{
    	Log.w(ActivityRecording.class.getName(), "test recording, click stop, stop test recording");
    	
    	// update ui
    	isBRecordClicked = 0;
		
    	// buttons
    	// bRecord.setEnabled(false);
    	imageButtonRecord.setEnabled(false); 
    	
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() 
	    { 
	         public void run() 
	         {
	        	srmRecorder.stopRecording();
			    
	        	// update GUI
	        	recItemIndex++;
			    if(recItemIndex > (recItemsList.size() -1)) recItemIndex = recItemsList.size() -1;
			    
		    	updateInstrAndPrompt(recItemsList.get(recItemIndex));
		    	
		    	//instrText.setText(recItemsList.get(recItemIndex).recinstructions);
				//promptText.setText(recItemsList.get(recItemIndex).recprompt);

		    	// imageCircle1.setImageResource(R.drawable.icon_circle_red);
		    	
				// bRecord.setText(getResources().getString(R.string.record));
				// bRecord.setEnabled(true);
				
		    	imageButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.icon_circle_grey));
		    	imageButtonRecord.setEnabled(true);
		    	
				bPlay.setEnabled(true);
				bPrev.setEnabled(true);
				bNext.setEnabled(true);
	         } 
	    }, Integer.parseInt(recItemsList.get(recItemIndex).postrecdelay));
	}
	
	private void startRecording()
	{
		// start recording

		Log.w(ActivityRecording.class.getName(), "recording, clicked record, start recording");
		
		// new recorder
		srmRecorder = new SrmRecorder(Utils.ConstantVars.REC_FILES_DIR_EXT_PATH 
					+ File.separator + "sessionID-" + Utils.ConstantVars.sessionItemIdForNewSession + "_"
					+ scriptItem.scriptName, 
					recItemsList.get(recItemIndex).itemcode);
		
		srmRecorder.startRecording();
		
		recordFilepath = srmRecorder.getAudioFile();
		Log.w(ActivityRecording.class.getName(), "recording, created new Recorder:" + recordFilepath);
		
		// update ui
		isBRecordClicked = 1;						
		
		// buttons
		// bRecord.setText(getResources().getString(R.string.stop));
		// bRecord.setEnabled(false);
		
    	imageButtonRecord.setEnabled(false);
    	imageButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.icon_circle_red));
		
    	// enlarge prompt area
    	if(recItemsList.get(recItemIndex).itemType == RecordItem.TYPE_IMAGE)
    		enlargePromptImage();
    	
		// change images
    	// imageCircle1.setImageResource(R.drawable.icon_circle_red);
		
		//update images
		Handler handler = new Handler(); 
		    handler.postDelayed(new Runnable() 
		    { 
		         public void run() 
		         { 
		        	// imageCircle1.setImageResource(R.drawable.icon_circle_yellow);

		     		imageButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.icon_circle_yellow));
		        	 
					Handler handler2 = new Handler(); 
				    handler2.postDelayed(new Runnable() 
				    { 
				         public void run() 
				         { 
				        	// imageCircle1.setImageResource(R.drawable.icon_circle_green);
							// bRecord.setEnabled(true);
							
				     		imageButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.icon_circle_green));
				     		imageButtonRecord.setEnabled(true);
				     		
//							Handler handler3 = new Handler(); 
//						    handler3.postDelayed(new Runnable() 
//						    { 
//						         public void run() 
//						         { 
//						        	 imageCircle1.setVisibility(View.INVISIBLE);
//						         } 
//						    }, (Integer.parseInt(recItemsList.get(recItemIndex).prerecdelay)) / 2);
				         } 
				    }, (Integer.parseInt(recItemsList.get(recItemIndex).prerecdelay)) / 2);
		         } 
		    }, (Integer.parseInt(recItemsList.get(recItemIndex).prerecdelay)) / 2); 
	}
	
	private void stopRecording()
	{
		// stop recording
		
    	Log.w(ActivityRecording.class.getName(), "recording, clicked stop, stop recording");
    	
    	// update ui
    	isBRecordClicked = 0;
		
    	// buttons
    	// bRecord.setEnabled(false);
    	
    	imageButtonRecord.setEnabled(false);
    	
    	// shrink prompt text
		if(recItemsList.get(recItemIndex).itemType == RecordItem.TYPE_IMAGE)
    		shrinkPromptImage();
    	
    	Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() 
	    { 
	         public void run() 
	         {
		    	srmRecorder.stopRecording();
				
		    	// insert record
		    	Uri uriNewRecordItem = insertRecordItemToDB(recItemsList.get(recItemIndex), 
			    		srmRecorder.getAudioFile());
		    	
		    	Log.w(ActivityRecording.class.getName(), "recording click stop, inserted record id="
		    			+ uriNewRecordItem.getLastPathSegment());
		    	
		    	recItemIndex++;
				if(recItemIndex > recItemsList.size() -1)
			    {
			    	//Utils.toastTextToUser(thisAct, "finished all recordings, start act finish recording!");
			    	
			    	Intent newI = new Intent(thisAct, ActivityFinishRecording.class);
		        	thisAct.startActivity(newI);
			    }
				else 
				{
			    	// update UI
			    	// imageCircle1.setImageResource(R.drawable.icon_circle_red);
			    	// imageCircle1.setVisibility(View.VISIBLE);
			    	
			    	updateInstrAndPrompt(recItemsList.get(recItemIndex));
			    	
			    	// instrText.setText(recItemsList.get(recItemIndex).recinstructions);
					// promptText.setText(recItemsList.get(recItemIndex).recprompt);

					// bRecord.setText(getResources().getString(R.string.record));
					// bRecord.setEnabled(true);
					
					imageButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.icon_circle_grey));
		     		imageButtonRecord.setEnabled(true);
				}
		    } 
	    }, Integer.parseInt(recItemsList.get(recItemIndex).postrecdelay));
	}
	
}
