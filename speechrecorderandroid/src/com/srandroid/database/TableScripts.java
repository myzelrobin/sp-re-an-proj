/**
 * 
 */
package com.srandroid.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/*
 *
 */
public class TableScripts 
{

	// Table Attributes
	public static final String TABLE_SCRIPTS = "scripts";
	public static final String COLUMN_ID = "_id"; // key
	public static final String COLUMN_FILEPATH = "filepath";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_COUNT = "count"; // integer
	public static final String COLUMN_SERVER_ID = "server_id"; // foreign key reference servers(id)
	
	
	// SQL statement CREATE TABLE scripts
	private static final String CREATE_TABLE_SCRIPTS = 
		"create table "
		+ TABLE_SCRIPTS
		+ " ( "
		+ COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_FILEPATH + " text not null, "
		+ COLUMN_DESCRIPTION + " text not null, "
		+ COLUMN_COUNT + " integer, "
		+ COLUMN_SERVER_ID + " integer, "
		+ " FOREIGN KEY (" + COLUMN_SERVER_ID + ") REFERENCES servers(_id)"
		+ " );";
	
	
	public static ArrayList<String> ALL_COLUMNS = new ArrayList<String>();
	/**
	 * 
	 */
	public TableScripts() 
	{
		
	}

	// create table scripts
	public static void onCreate(SQLiteDatabase db)
	{
		Log.w(TableScripts.class.getName(), "onCreate(): will create table: " + TABLE_SCRIPTS);
		setALL_COLUMNS();
		db.execSQL(CREATE_TABLE_SCRIPTS);
		insertScriptExamples(db);
	}
	
	// upgrade table scripts
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(TableScripts.class.getName(), "onUpgrade(): will upgrade table: " + TABLE_SCRIPTS);
		db.execSQL("drop table if exists " + TABLE_SCRIPTS  + ";\n");
		onCreate(db);
	}
	
	public static void insertScriptExamples(SQLiteDatabase db) 
	{
		Log.w(TableScripts.class.getName(), "insertScriptExamples() will insert examples");
		ContentValues values = new ContentValues(); 
		for(int i=1; i<11; i++)
		{
			values.put(COLUMN_FILEPATH, "/mnt/sdcard/APP_FOLDER/files/scripts/" + i + ".xml");
			values.put(COLUMN_DESCRIPTION, "Example script #"+i);
			values.put(COLUMN_COUNT, i);
			values.put(COLUMN_SERVER_ID, i);
			db.insert(TABLE_SCRIPTS, null, values);
		}
		
	}
	
	public static void setValuesForInsertScriptItem(ContentValues values, 
			String serverId,
			String scriptFilepath,
			String description,
			String count)
	{
		// section_id
		values.put(COLUMN_SERVER_ID, Integer.parseInt(serverId));
		
		// record_path
		values.put(COLUMN_FILEPATH, scriptFilepath);
		
		// description
		values.put(COLUMN_DESCRIPTION, description);
		
		// is_uploaded
		values.put(COLUMN_COUNT, Integer.parseInt(count));
		
	}


	public static class ScriptItem {

		public String idInTable ;
		public String filepath;
		public String description; // key "ScriptName"
		public String serverId;
		
		public String filename;
		public String databaseName;
		public String scriptName;
		public String scriptAuthor;
		public String EmailAuthor;
		
		private String recordingItemsCount;
		
		
		public ScriptItem() {
			// TODO Auto-generated constructor stub
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
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}


		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}


		/**
		 * @return the serverId
		 */
		public String getServerId() {
			return serverId;
		}


		/**
		 * @param serverId the serverId to set
		 */
		public void setServerId(String serverId) {
			this.serverId = serverId;
		}


		/**
		 * @return the filename
		 */
		public String getFilename() {
			return filename;
		}


		/**
		 * @param filename the filename to set
		 */
		public void setFilename(String filename) {
			this.filename = filename;
		}


		/**
		 * @return the databaseName
		 */
		public String getDatabaseName() {
			return databaseName;
		}


		/**
		 * @param databaseName the databaseName to set
		 */
		public void setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
		}


		/**
		 * @return the scriptName
		 */
		public String getScriptName() {
			return scriptName;
		}


		/**
		 * @param scriptName the scriptName to set
		 */
		public void setScriptName(String scriptName) {
			this.scriptName = scriptName;
		}


		/**
		 * @return the scriptAuthor
		 */
		public String getScriptAuthor() {
			return scriptAuthor;
		}


		/**
		 * @param scriptAuthor the scriptAuthor to set
		 */
		public void setScriptAuthor(String scriptAuthor) {
			this.scriptAuthor = scriptAuthor;
		}


		/**
		 * @return the emailAuthor
		 */
		public String getEmailAuthor() {
			return EmailAuthor;
		}


		/**
		 * @param emailAuthor the emailAuthor to set
		 */
		public void setEmailAuthor(String emailAuthor) {
			EmailAuthor = emailAuthor;
		}


		/**
		 * @return the recordingItemsCount
		 */
		public String getRecordingItemsCount() {
			return recordingItemsCount;
		}


		/**
		 * @param recordingItemsCount the recordingItemsCount to set
		 */
		public void setRecordingItemsCount(String recordingItemsCount) {
			this.recordingItemsCount = recordingItemsCount;
		}



	}


	/**
	 * @return the aLL_COLUMNS
	 */
	public static ArrayList<String> getALL_COLUMNS() {
		return ALL_COLUMNS;
	}

	/**
	 * @param aLL_COLUMNS the aLL_COLUMNS to set
	 */
	public static void setALL_COLUMNS() 
	{
		ALL_COLUMNS.add(COLUMN_ID);
		ALL_COLUMNS.add(COLUMN_FILEPATH);
		ALL_COLUMNS.add(COLUMN_DESCRIPTION);
		ALL_COLUMNS.add(COLUMN_COUNT);
		ALL_COLUMNS.add(COLUMN_SERVER_ID);
	}

}
