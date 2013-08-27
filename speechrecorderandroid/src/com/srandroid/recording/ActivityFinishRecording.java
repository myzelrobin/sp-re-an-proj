/**
 * 
 */
package com.srandroid.recording;

import com.srandroid.database.TableSessions;
import com.srandroid.database.SrmContentProvider.SrmUriMatcher;
import com.srandroid.main.ActivitySessionDetails;
import com.srandroid.main.TestActivitySessionDetails;
import com.srandroid.speechrecorder.R;
import com.srandroid.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.test.ActivityTestCase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 *
 */
public class ActivityFinishRecording extends Activity 
{

	private Button bDone;
	
	private Activity thisAct;
	/**
	 * 
	 */
	public ActivityFinishRecording() 
	{
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
        
        setContentView(R.layout.linearlayout_act_finishrecording);
        
        thisAct = this;
        
        bDone = (Button) findViewById(R.id.act_finishrec_button_done);
        bDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				//Utils.toastTextToUser(getApplicationContext(), "check finished session");
	    		
				// update session
				
				Log.w(this.getClass().getName(), " will update session item as finished, id="
						+ Utils.ConstantVars.sessionItemIdForNewSession);
				Uri uriTemp = Uri.parse(SrmUriMatcher.CONTENT_ITEM_TYPE_SESSION 
						+ "/" + Utils.ConstantVars.sessionItemIdForNewSession);
				
				ContentValues valuesTemp = new ContentValues();
				valuesTemp.put(TableSessions.COLUMN_IS_FINISHED, "finished");
				
				int updatedItemId = getContentResolver().update(uriTemp, valuesTemp, null, null);
				
				Log.w(this.getClass().getName(), " updated session item as finished, id=" + updatedItemId);
				
				
	    		Intent newI = new Intent(thisAct, TestActivitySessionDetails.class);
	    		newI.putExtra("itemId", Utils.ConstantVars.sessionItemIdForNewSession);
	    		
	        	thisAct.startActivity(newI);
				
			}
		});
        
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
