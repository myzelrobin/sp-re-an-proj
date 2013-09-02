/**
 * 
 */
package com.srandroid.database;

import java.util.ArrayList;
import java.util.Calendar;

import com.srandroid.util.Utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;


/*
 *
 */
public class TableRecords 
{

	

	// Table Attributes
	public static final String TABLE_RECORDS = "records";
	public static final String COLUMN_ID = "_id"; // key
	public static final String COLUMN_FILEPATH = "filepath"; // text not null
	public static final String COLUMN_INSTRUCTION = "instruction"; // text not null
	public static final String COLUMN_PROMPT = "prompt"; // text not null
	public static final String COLUMN_COMMENT = "comment"; // text not null
	public static final String COLUMN_ITEMCODE = "itemcode"; // text not null
	public static final String COLUMN_ISUPLOADED = "is_uploaded"; //  text default: uploaded/unuploaded
	public static final String COLUMN_SCRIPT_ID = "script_id"; // foreign key reference scripts(_id)
	public static final String COLUMN_SPEAKER_ID = "speaker_id"; // foreign key reference speakers(_id)
	public static final String COLUMN_SESSION_ID = "session_id"; // foreign key reference sessions(_id)
	
	// SQL statement CREATE TABLE records
	private static final String CREATE_TABLE_RECORDS = 
		"create table "
		+ TABLE_RECORDS
		+ " ( "
		+ COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_FILEPATH + " text not null, "
		+ COLUMN_INSTRUCTION + " text, "
		+ COLUMN_PROMPT + " text, "
		+ COLUMN_COMMENT + " text, "
		+ COLUMN_ITEMCODE + " text not null, "
		+ COLUMN_ISUPLOADED + " text, "
		+ COLUMN_SCRIPT_ID + " integer, "
		+ COLUMN_SPEAKER_ID + " integer, "
		+ COLUMN_SESSION_ID + " integer, "
		+ "FOREIGN KEY (" + COLUMN_SCRIPT_ID + ") REFERENCES scripts(_id), "
		+ "FOREIGN KEY (" + COLUMN_SPEAKER_ID + ") REFERENCES speakers(_id), "
		+ "FOREIGN KEY (" + COLUMN_SESSION_ID + ") REFERENCES sessions(_id) "
		+ ");";
	

	
	public static ArrayList<String> ALL_COLUMNS = new ArrayList<String>();

	/**
	 * 
	 */
	public TableRecords() 
	{
		
	}
	
	
	
	// create table records
	public static void onCreate(SQLiteDatabase db)
	{
		Log.w(TableRecords.class.getName(), "onCreate(): will create table: " + TABLE_RECORDS);
		setALL_COLUMNS();
		db.execSQL(CREATE_TABLE_RECORDS);
	}
	
	// upgrade table records
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(TableRecords.class.getName(), "onUpgrade(): will upgrade table: " + TABLE_RECORDS);
		db.execSQL("drop table if exists " + TABLE_RECORDS  + ";\n");
		onCreate(db);
	}
	
	public static void setValuesForInsertRecordItem(ContentValues values, 
			String sessionId,
			String speakerId,
			String scriptId,
			String recFilepath,
			String instruction,
			String prompt,
			String comment,
			String itemcode)
	{
		// session id
		values.put(COLUMN_SESSION_ID, Integer.parseInt(sessionId));
		
		// speaker id
		values.put(COLUMN_SPEAKER_ID, Integer.parseInt(speakerId));
		
		//script_id
		values.put(COLUMN_SCRIPT_ID, Integer.parseInt(scriptId));

		// is_uploaded
		values.put(COLUMN_ISUPLOADED, "unuploaded");
		
		// record_path
		values.put(COLUMN_FILEPATH, recFilepath);
		
		values.put(COLUMN_INSTRUCTION, instruction);
		
		values.put(COLUMN_PROMPT, prompt);
		
		values.put(COLUMN_COMMENT, comment);
		
		values.put(COLUMN_ITEMCODE, itemcode);
		
	}
	
	
	// as a record text item in script
	public static class RecordItem {

		public String idInTable;
		public String filepath; 
		public String scriptId;
		public String isUploaded;
		
		// section information, important?
		// attributes
		public String sectionname;
		public String mode;
		public String order;
		public String promptphase;
		public String speakerdisplay;

		// recording elememt
		public String itemcode;
		
		public String postrecdelay;
		public String prerecdelay;
		public String recduration;
		
		public String recinstructions;
		public String recprompt;
		public String reccomment;
		
		public RecordItem() 
		{
			
		}

		/**
		 * @return the idInTable
		 */
		public String getIdInTable() {
			return idInTable;
		}

		/**
		 * @param idInTable the idInTable to set
		 */
		public void setIdInTable(String idInTable) {
			this.idInTable = idInTable;
		}

		/**
		 * @return the filepath
		 */
		public String getFilepath() {
			return filepath;
		}

		/**
		 * @param filepath the filepath to set
		 */
		public void setFilepath(String filepath) {
			this.filepath = filepath;
		}

		/**
		 * @return the scriptId
		 */
		public String getScriptId() {
			return scriptId;
		}

		/**
		 * @param scriptId the scriptId to set
		 */
		public void setScriptId(String scriptId) {
			this.scriptId = scriptId;
		}

		/**
		 * @return the isUploaded
		 */
		public String getIsUploaded() {
			return isUploaded;
		}

		/**
		 * @param isUploaded the isUploaded to set
		 */
		public void setIsUploaded(String isUploaded) {
			this.isUploaded = isUploaded;
		}

		/**
		 * @return the sectionname
		 */
		public String getSectionname() {
			return sectionname;
		}

		/**
		 * @param sectionname the sectionname to set
		 */
		public void setSectionname(String sectionname) {
			this.sectionname = sectionname;
		}

		/**
		 * @return the mode
		 */
		public String getMode() {
			return mode;
		}

		/**
		 * @param mode the mode to set
		 */
		public void setMode(String mode) {
			this.mode = mode;
		}

		/**
		 * @return the order
		 */
		public String getOrder() {
			return order;
		}

		/**
		 * @param order the order to set
		 */
		public void setOrder(String order) {
			this.order = order;
		}

		/**
		 * @return the promptphase
		 */
		public String getPromptphase() {
			return promptphase;
		}

		/**
		 * @param promptphase the promptphase to set
		 */
		public void setPromptphase(String promptphase) {
			this.promptphase = promptphase;
		}

		/**
		 * @return the speakerdisplay
		 */
		public String getSpeakerdisplay() {
			return speakerdisplay;
		}

		/**
		 * @param speakerdisplay the speakerdisplay to set
		 */
		public void setSpeakerdisplay(String speakerdisplay) {
			this.speakerdisplay = speakerdisplay;
		}

		/**
		 * @return the itemcode
		 */
		public String getItemcode() {
			return itemcode;
		}

		/**
		 * @param itemcode the itemcode to set
		 */
		public void setItemcode(String itemcode) {
			this.itemcode = itemcode;
		}

		/**
		 * @return the postrecdelay
		 */
		public String getPostrecdelay() {
			return postrecdelay;
		}

		/**
		 * @param postrecdelay the postrecdelay to set
		 */
		public void setPostrecdelay(String postrecdelay) {
			this.postrecdelay = postrecdelay;
		}

		/**
		 * @return the prerecdelay
		 */
		public String getPrerecdelay() {
			return prerecdelay;
		}

		/**
		 * @param prerecdelay the prerecdelay to set
		 */
		public void setPrerecdelay(String prerecdelay) {
			this.prerecdelay = prerecdelay;
		}

		/**
		 * @return the recduration
		 */
		public String getRecduration() {
			return recduration;
		}

		/**
		 * @param recduration the recduration to set
		 */
		public void setRecduration(String recduration) {
			this.recduration = recduration;
		}

		/**
		 * @return the recinstructions
		 */
		public String getRecinstructions() {
			return recinstructions;
		}

		/**
		 * @param recinstructions the recinstructions to set
		 */
		public void setRecinstructions(String recinstructions) {
			this.recinstructions = recinstructions;
		}

		/**
		 * @return the recprompt
		 */
		public String getRecprompt() {
			return recprompt;
		}

		/**
		 * @param recprompt the recprompt to set
		 */
		public void setRecprompt(String recprompt) {
			this.recprompt = recprompt;
		}

		/**
		 * @return the reccomment
		 */
		public String getReccomment() {
			return reccomment;
		}

		/**
		 * @param reccomment the reccomment to set
		 */
		public void setReccomment(String reccomment) {
			this.reccomment = reccomment;
		}
	}


	/**
	 * @return the aLL_COLUMNS
	 */
	public static ArrayList<String> getALL_COLUMNS() 
	{
		return ALL_COLUMNS;
	}

	/**
	 * @param aLL_COLUMNS the aLL_COLUMNS to set
	 */
	public static void setALL_COLUMNS() 
	{
		/*
		 + COLUMN_FILEPATH + " text not null, "
			+ COLUMN_INSTRUCTION + " text, "
			+ COLUMN_PROMPT + " text, "
			+ COLUMN_COMMENT + " text, "
			+ COLUMN_ITEMCODE + " text not null, "
			+ COLUMN_ISUPLOADED + " text, "
			+ COLUMN_SCRIPT_ID + " integer, "
			+ COLUMN_SPEAKER_ID + " integer, "
			+ COLUMN_SESSION_ID + " integer, "
		 */
		
		ALL_COLUMNS.add(COLUMN_ID);
		ALL_COLUMNS.add(COLUMN_FILEPATH);
		ALL_COLUMNS.add(COLUMN_INSTRUCTION);
		ALL_COLUMNS.add(COLUMN_PROMPT);
		ALL_COLUMNS.add(COLUMN_COMMENT);
		ALL_COLUMNS.add(COLUMN_ITEMCODE);
		ALL_COLUMNS.add(COLUMN_ISUPLOADED);
		ALL_COLUMNS.add(COLUMN_SCRIPT_ID);
		ALL_COLUMNS.add(COLUMN_SPEAKER_ID);
		ALL_COLUMNS.add(COLUMN_SESSION_ID);
	}

}
