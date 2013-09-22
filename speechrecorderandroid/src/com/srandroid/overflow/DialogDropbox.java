package com.srandroid.overflow;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.srandroid.speechrecorder.R;
import com.srandroid.util.Debugger;
import com.srandroid.util.SrmRecorder;
import com.srandroid.util.Utils;

public class DialogDropbox extends DialogPreference 
{
	private static final String LOGTAG = DialogDropbox.class.getName();
	private Debugger debugger = new Debugger(LOGTAG);
	
	private Context context;
	
	private TextView textViewInfos;
	private ProgressBar progressBar;
	
	/**
	 * @param context
	 * @param attrs
	 */
	public DialogDropbox(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.context = context;
		setDialogLayoutResource(R.layout.dialog_settings_dropbox);
	}
	
	/**
	 * initiate dialog
	 */
	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) 
	{
		 builder.setTitle(R.string.settings_dropbox);
		 builder.setIcon(null);
		 
		 builder.setPositiveButton(R.string.connect, this);
		 builder.setNegativeButton(R.string.close, 
				 new DialogInterface.OnClickListener() 
		 		{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						getDialog().dismiss();
					}
		 		});
		 
		 super.onPrepareDialogBuilder(builder);  
	}
	
	/**
	 * bind clicklistener to buttons
	 */
	 @Override
	 public void onBindDialogView(View view)
	 {
		 // textview 
		 textViewInfos = (TextView) view.findViewById(R.id.dialog_dropbox_textview_value);
		 
		 // progressbar
		 progressBar = (ProgressBar) view.findViewById(R.id.dialog_dropbox_progressbar);
		 
		 super.onBindDialogView(view);
	 }
	 /**
	  * 
	  */
	 @Override
	protected void showDialog(Bundle state)
	{       
	    super.showDialog(state);    //Call show on default first so we can override the handlers
	
	    final AlertDialog dialog = (AlertDialog) getDialog();
	    
	    // set a new listener for positive button, 
	    // so that after clicking positive button, dialog is not dismissed
	    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
	    		new View.OnClickListener()
	            {            
	                @Override
	                public void onClick(View v)
	                {
	                	clickPosButton();
	                }
	            });
	}
	 

//	 @Override
//	public void onClick(View v) 
//	{
//		// TODO Auto-generated method stub
//		 int id = v.getId();
//		 
//		 switch (id) 
//		 {
//			case 0:
//				break;
//				
//			
//			default:
//				break;
//		} /* switch-case */
//		
//	} /* onClick() */
	 
	 
	/**
	  * Saves new value for this preference key from xml into the SharedPreference
	  */
	 @Override
	 protected void onDialogClosed(boolean positiveResult) 
	 {
	     // When the user selects "OK", persist the new value
	     if (positiveResult) 
	     {
	         // persistString(volume_value);
	     }
	 }
	 
	 /**
	  * Saves the default value  for this preference key in the SharedPreferences.
	  */
	 @Override
	 protected Object onGetDefaultValue(TypedArray a, int index) 
	 {
	     return a.getString(index);
	 }
	 
	 /**
	  * Initialize the default value
	  */
	 @Override
	 protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) 
	 {
	     if (restorePersistedValue) 
	     {
	         // Restore existing state
	         // volume_value = this.getPersistedString(Utils.ConstantVars.MICVOL);
	     } 
	     else 
	     {
	         // Set default state from the XML attribute
	    	 // volume_value = (String) defaultValue;
	         // persistString(volume_value);
	     }
	 }
	 
	 private void clickPosButton()
	 {
		 textViewInfos.setVisibility(View.INVISIBLE);
		 progressBar.setVisibility(View.VISIBLE);
		 ( (AlertDialog) getDialog() ).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		 debugger.outputInfo("user clicked positive button, execute asynctask to authorize app");
	 }
}
