package com.srandroid.overflow;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.srandroid.speechrecorder.R;
import com.srandroid.util.Debugger;
import com.srandroid.util.SrmRecorder;
import com.srandroid.util.Utils;

public class DialogTestMicrophone 
	extends DialogPreference 
	implements OnClickListener
{
	private static final String LOGTAG = DialogTestMicrophone.class.getName();
	private Debugger debugger = new Debugger(LOGTAG);
	
	private Context context;
	
	private Button butCancel, butTestmic, butTestrecord;
	private int isButTestmicClicked = 0;
	private int isButTestrecordClicked = 0;
	private ProgressBar progressBar;
	
	private String volume_value = "-1"; 
	
	private SrmRecorder recorderForMic, recorderForTestRecording;
	
	/**
	 * @param context
	 * @param attrs
	 */
	public DialogTestMicrophone(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.context = context;
		setDialogLayoutResource(R.layout.dialog_settings_microphone);
	}
	
	/**
	 * initiate dialog
	 */
	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) 
	{
		 builder.setTitle(R.string.settings_microphone);
		 builder.setPositiveButton(null, null);
		 builder.setNegativeButton(null, null);
		 super.onPrepareDialogBuilder(builder);  
	}
	
	/**
	 * bind clicklistener to buttons
	 */
	 @Override
	 public void onBindDialogView(View view)
	 {
		 
		 // button CANCEL
		 butCancel = (Button) view.findViewById(R.id.button_cancel_in_dialog_mic);
		 butCancel.setOnClickListener(this);
		 // button TESTMIC
		 butTestmic = (Button) view.findViewById(R.id.button_testmic_in_dialog_mic);
		 butTestmic.setOnClickListener(this);
		 // button TESTREC
		 butTestrecord = (Button) view.findViewById(R.id.button_testrecord_in_dialog_mic);
		 butTestrecord.setOnClickListener(this);
		 
		 
		 setProgressBar((ProgressBar) view.findViewById(R.id.progressBarInDialogSetMic)); 
		 
		 super.onBindDialogView(view);
	 }
	 
	 /**
	  * handles click events on buttons
	  */
	 @Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		 int id = v.getId();
		 
		 switch (id) 
		 {
		 	// button CANCEL
			case R.id.button_cancel_in_dialog_mic:
				getDialog().dismiss();
				break;
			
			
			case R.id.button_testmic_in_dialog_mic: /* button MIC */
				if ( isButTestmicClicked == 0 )
				{ /* start recording for testing mic */
					debugger.outputInfo("settings:->dialog testmic: start testing MIC");
					
					butTestrecord.setEnabled(false);
					butCancel.setEnabled(false);
					
					recorderForMic = 
							new SrmRecorder(
									Utils.ConstantVars.DIR_EXT_TESTMIC_PATH, 
									"test_mic", this);
					debugger.outputInfo(SrmRecorder.TAG_TESTMIC 
							+ ": AudioRecord recorderForMic is created: " 
							+ "\nsampleRateHz=" + SrmRecorder.getSampleRateHz()
							+ "\nchannelConfig=" + SrmRecorder.getChannelConfig()
							+ "\nchannels=" + SrmRecorder.getChannels()
							+ "\nminBufferSize=" + recorderForMic.getMinBufferSize());
					try 
					{
						recorderForMic.startTestMicrophone();
					} 
					catch (IllegalStateException e)
					{
						e.printStackTrace();
					}
					
					isButTestmicClicked = 1;
					butTestmic.setText(R.string.stop);
				}
				else if ( isButTestmicClicked == 1 )
				{ /* stop recording for testing mic */
					debugger.outputInfo("settings:->dialog testmic: stop testing MIC");
					
					recorderForMic.stopTestMicrophone();
					debugger.outputInfo(SrmRecorder.TAG_TESTMIC 
							 + ": test mic audio file is deleted at: " 
							 + recorderForMic.getAudioFile());
					 
					// here needs a method to set the volume_value
					onDialogClosed(true);

					isButTestmicClicked = 0;
					getDialog().dismiss();
				}
				break;
				
			case R.id.button_testrecord_in_dialog_mic: /* button RECORD */
				
				if ( isButTestrecordClicked == 0 )
				{/* start recording for testing recording */
					butTestmic.setEnabled(false);
					butCancel.setEnabled(false);
					isButTestrecordClicked = 1;
					butTestrecord.setText(R.string.stop);
					
					recorderForTestRecording = 
							 new SrmRecorder(Utils.ConstantVars.DIR_EXT_TESTMIC_PATH, "test_record");
					debugger.outputInfo(SrmRecorder.TAG_TESTREC 
								+ ": AudioRecord recorderForTestRecording is created: " 
								+ "\nsampleRateHz=" + SrmRecorder.getSampleRateHz()
								+ "\nchannelConfig=" + SrmRecorder.getChannelConfig()
								+ "\nchannels=" + SrmRecorder.getChannels()
								+ "\nminBufferSize=" + recorderForTestRecording.getMinBufferSize());
					try 
					{
						recorderForTestRecording.startTestRecording();
					} 
					catch (IllegalStateException e) 
					{
						e.printStackTrace();
					}
				}
				else if ( isButTestrecordClicked == 1 )
				{/* play record for testing recording */
					 isButTestrecordClicked = 2;
					 butTestrecord.setText(R.string.close);
					
					// STOP the test recording
					 recorderForTestRecording.stopTestRecording();
					 debugger.outputInfo(SrmRecorder.TAG_TESTREC 
							 + ": test record audio file is saved: " 
							 + recorderForTestRecording.getAudioFile());
					 
					// play the record
					try 
					{
						Utils.playRecord(context, recorderForTestRecording.getAudioFile());
					} 
					catch (ActivityNotFoundException e) 
					{
						debugger.outputException("playing record", "Utils.playRecord()", 
								"ActivityNotFoundException", e);
					}
				 }
				 else if ( isButTestrecordClicked == 2 )
				 {/* finish recording for testing recording */
					recorderForTestRecording.finishedTestRecording(); /* delete record file */
					isButTestrecordClicked = 0;
					getDialog().dismiss();
				 }
				
				break;
			
			default:
				break;
		} /* switch-case */
		
	} /* onClick() */
	 
	 
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
	         volume_value = this.getPersistedString(Utils.ConstantVars.MICVOL);
	     } 
	     else 
	     {
	         // Set default state from the XML attribute
	    	 volume_value = (String) defaultValue;
	         // persistString(volume_value);
	     }
	 }
	 
	/**
	 * @return the progressBar
	 */
	public ProgressBar getProgressBar()
	{
		return progressBar;
	}
	
	
	/**
	 * @param progressBar the progressBar to set
	 */
	public void setProgressBar(ProgressBar progressBar) 
	{
		this.progressBar = progressBar;
	}
	
	 
}
