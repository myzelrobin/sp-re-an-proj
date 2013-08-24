/**
 * 
 */
package com.srandroid.recording;

import java.util.ArrayList;
import java.util.List;

import com.srandroid.database.TableScripts;
import com.srandroid.database.TableSessions;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.recording.ActivityPreRecording.LocalAdapterForActPreRec;
import com.srandroid.speechrecorder.R;
import com.srandroid.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
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
	
	private ImageView imageCircle1;
	private ImageView imageCircle2;
	private ImageView imageCircle3;
	
	private LocalAdapterForActRecording adapter;
	
	private OnClickListenerForRecording listener;
	
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
        		else 
        		{
        			// In Act Recording
        			Utils.toastText(this, "finish recording, open 'signature' page ");
    	    		
//    	    		Intent newI = new Intent(this, ActivitySignature.class);
//    	        	this.startActivity(newI);
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
	
	private void fillControlArea(View areaView)
	{
		bRecord = (Button) areaView.findViewById(R.id.act_recording_control_button_record);
    	bPlay = (Button) areaView.findViewById(R.id.act_recording_control_button_play);
    	bPlay.setEnabled(false);
    	bNext = (Button) areaView.findViewById(R.id.act_recording_control_button_next);
    	bPrev = (Button) areaView.findViewById(R.id.act_recording_control_button_previous);
    	
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
			TextView tView1 = (TextView) parentView.findViewById(R.id.act_recording_text_intro_textvalue);
			tView1.setText(sIntro);
			TextView tView2 = (TextView) parentView.findViewById(R.id.act_recording_text_prompt_textvalue);
			tView2.setText(sContent);
    	}
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
					Utils.toastText(thisAct, "clicked Record");
					
					if(isBRecordClicked == 0)
					{
						// start recording
						isBRecordClicked = 1;
						bRecord.setText(getResources().getString(R.string.stop));
						
						imageCircle1.setImageResource(R.drawable.icon_circle_yellow);
						imageCircle2.setImageResource(R.drawable.icon_circle_yellow);
						imageCircle3.setImageResource(R.drawable.icon_circle_yellow);
					}
					else if (isBRecordClicked == 1)
					{
						// stop recording
						isBRecordClicked = 0;
						bRecord.setText(getResources().getString(R.string.record));
						
						imageCircle1.setImageResource(R.drawable.icon_circle_green);
						imageCircle2.setImageResource(R.drawable.icon_circle_green);
						imageCircle3.setImageResource(R.drawable.icon_circle_green);
					}
					
					break;
				
				case R.id.act_recording_control_button_play:
					Utils.toastText(thisAct, "clicked Play");
					break;
					
				case R.id.act_recording_control_button_previous:
					
					break;
				
				case R.id.act_recording_control_button_next:
					updateTextArea(gridView, "Intro", "Content");
					
					break;
				default:
					break;
			}
			
		}
		
	}
	
}
