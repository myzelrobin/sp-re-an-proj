/**
 * 
 */
package com.srandroid.recording;

import com.srandroid.speechrecorder.R;
import com.srandroid.util.SrmRecorder;
import com.srandroid.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *
 */
public class StartRecording extends Activity 
{

	private Button bStart;
	private Button bAgreement;
	
	private TextView textViewAgreement;
	private Button bYes;
	private Button bNo;
	/**
	 * 
	 */
	public StartRecording() 
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
        
        setContentView(R.layout.linearlayout_act_startrecording);
        
        bStart = (Button) findViewById(R.id.act_startrec_button_start);
        bStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Utils.toastText(getApplicationContext(), "start recording");
				
			}
		});
        
        bAgreement = (Button) findViewById(R.id.act_startrec_button_agreement);
        bAgreement.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
//				final Dialog agreentDialog = new Dialog(getApplicationContext());
//				agreentDialog.setContentView(R.layout.dialog_act_startrec_agreement);
//				agreentDialog.setTitle(R.string.act_startrecording_dialog_agreement_title_text);
//				
//				 // button CANCEL
//				 bYes = (Button) agreentDialog.findViewById(R.id.act_startrec_dialog_agreemment_buttonyes);
//				 bYes.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						agreentDialog.dismiss();
//						
//					}
//				});
//				 // button TESTMIC
//				 bNo = (Button) agreentDialog.findViewById(R.id.act_startrec_dialog_agreemment_buttonno);
//				 
//				 bNo.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							agreentDialog.cancel();
//							
//						}
//				});
				
				try {
					
				AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
				builder.setTitle(R.string.act_startrecording_dialog_agreement_title_text);
				builder.setMessage(R.string.act_startrecording_dialog_agreement_content_text)
				       .setCancelable(true)
				       .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								
								dialog.dismiss();
							}
						})
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								
								dialog.cancel();
							}
						});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
