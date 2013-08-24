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
				DialogAgreement dialog = new DialogAgreement(getApplicationContext());
				dialog.show();
				
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

	protected class DialogAgreement extends Dialog implements OnClickListener
	{
		TextView textViewAgreement;
		Button bYes;
		Button bNo;

		public DialogAgreement(Context context) 
		{
			super(context);
			setContentView(R.layout.dialog_act_startrec_agreement);
			this.setTitle(R.string.act_startrecording_dialog_agreement_title_text);
			
			 // button CANCEL
			 bYes = (Button) this.findViewById(R.id.act_startrec_dialog_agreemment_buttonyes);
			 bYes.setOnClickListener(this);
			 // button TESTMIC
			 bNo = (Button) this.findViewById(R.id.act_startrec_dialog_agreemment_buttonno);
			 bNo.setOnClickListener(this);
			 
			 
		}
		 /**
		  * handles click events on buttons
		  */
		 @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 int buttonID = v.getId();
			 
			 switch (buttonID) 
			 {
			 	// button CANCEL
				case R.id.act_startrec_dialog_agreemment_buttonyes:
					this.dismiss();
					break;
				
				// button MIC
				case R.id.act_startrec_dialog_agreemment_buttonno:
					this.cancel();
					 break;
				default:
					break;
			}
			
		}
		 
	}
}
