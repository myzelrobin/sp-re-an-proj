/**
 * 
 */
package com.srandroid.recording;

import com.srandroid.speechrecorder.R;
import com.srandroid.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 *
 */
public class StartRecording extends Activity 
{

	private Button bStart;
	private Button bAgreement;
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
				AlertDialog.Builder agreementDiaglog = new AlertDialog.Builder(getApplicationContext());
		 
					// set title
					agreementDiaglog.setTitle(getResources().getString(
							R.string.act_startrecording_dialog_agreement_title_text));
		 
					// set dialog message
					agreementDiaglog
						.setMessage(getResources().getString(
								R.string.act_startrecording_dialog_agreement_content_text))
						.setCancelable(true)
						.setPositiveButton(getResources().getString(
								R.string.act_startrecording_dialog_agreement_button_text),
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, close
								// current activity
								dialog.dismiss();;
							}
						  })
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						AlertDialog alertDialog = agreementDiaglog.create();
		 
						// show it
						alertDialog.show();
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
