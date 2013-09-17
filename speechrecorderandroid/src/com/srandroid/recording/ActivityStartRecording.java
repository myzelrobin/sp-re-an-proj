/**
 * 
 */
package com.srandroid.recording;

import com.srandroid.speechrecorder.R;
import com.srandroid.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 *
 */
public class ActivityStartRecording extends Activity 
{

	private Button bStart;
	private Button bAgreement;
	
	private TextView textViewAgreement;
	private Button bYes;
	private Button bNo;
	
	private Context context;
	//private Activity thisAct;
	
	public ActivityStartRecording() 
	{
		this.context = this;
		// this.thisAct = this;
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
        
        setContentView(R.layout.linearlayout_act_startrecording);
        
        bStart = (Button) findViewById(R.id.act_startrec_button_start);
        bStart.setOnClickListener(
	        new OnClickListener() 
	        {
				
				@Override
				public void onClick(View v) 
				{
					// Uils.toastTextToUser(getApplicationContext(), "start recording");
		    		
		    		Intent newI = new Intent((Activity) context, ActivityRecording.class);
		    		newI.putExtra("isTestRecording", false);
		    		
		        	((Activity) context).startActivity(newI);
					
				}
			});
        
        bAgreement = (Button) findViewById(R.id.act_startrec_button_agreement);
        bAgreement.setOnClickListener(
	        new OnClickListener() 
	        {
				@Override
				public void onClick(View v) 
				{
					try 
					{
						AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context);
						builder.setTitle(R.string.act_startrecording_dialog_agreement_title_text);
						builder.setMessage(R.string.act_startrecording_dialog_agreement_content_text)
						       .setCancelable(true)
						       .setPositiveButton(
						    		   R.string.act_startrecording_dialog_agreement_button_agree,
						    		   new DialogInterface.OnClickListener() 
						    		   {
											public void onClick(DialogInterface dialog,int id)
											{
												
												dialog.dismiss();
											}
						    		   })
								.setNegativeButton(
										R.string.act_startrecording_dialog_agreement_button_disagree,
										new DialogInterface.OnClickListener() 
										{
											public void onClick(DialogInterface dialog,int id) 
											{
												
												dialog.cancel();
											}
										});
						AlertDialog alertDialog = builder.create();
						alertDialog.show();
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
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
	
	@Override
	public void setTitle(CharSequence title) 
	{
	    getActionBar().setTitle(title);
	}
	
}
