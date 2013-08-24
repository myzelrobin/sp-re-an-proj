/**
 * 
 */
package com.srandroid.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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
	public static final String COLUMN_ISUPLOADED = "is_uploaded"; //  text default: false
	public static final String COLUMN_SECTION_ID = "section_id"; // foreign key reference sections(_id)
	
	
	
	// SQL statement CREATE TABLE records
	private static final String CREATE_TABLE_RECORDS = 
		"create table "
		+ TABLE_RECORDS
		+ " ( "
		+ COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_FILEPATH + " text not null, "
		+ COLUMN_ISUPLOADED + " text, "
		+ COLUMN_SECTION_ID + " integer, "
		+ " FOREIGN KEY (" + COLUMN_SECTION_ID + ") REFERENCES sections(_id)"
		+ " );";
	
	// create table records
	public static void onCreate(SQLiteDatabase db)
	{
		Log.w(TableRecords.class.getName(), "onCreate(): will create table: " + TABLE_RECORDS);
		db.execSQL(CREATE_TABLE_RECORDS);
		insertRecordExamples(db);
	}
	
	// upgrade table records
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(TableRecords.class.getName(), "onUpgrade(): will upgrade table: " + TABLE_RECORDS);
		db.execSQL("drop table if exists " + TABLE_RECORDS  + ";\n");
		onCreate(db);
	}
	
	public static void insertRecordExamples(SQLiteDatabase db) 
	{
		Log.w(TableRecords.class.getName(), "insertRecordExamples() will insert examples");
		ContentValues values = new ContentValues(); 
		for(int i=1; i<11; i++)
		{
			values.put(COLUMN_FILEPATH, "/mnt/sdcard/APP_FOLDER/recrods/section_id/" + i);
			values.put(COLUMN_SECTION_ID, i);
			if( i % 2 == 0) values.put(COLUMN_ISUPLOADED, "uploaded");
			else values.put(COLUMN_ISUPLOADED, "unuploaded");
			db.insert(TABLE_RECORDS, null, values);
		}
		
	}

	public class RecordItem {

		private String item_id = "record#";
		private String key_id = null; 
		private String filepath = null; 
		private String section_id = null;
		private String isUploaded = null;
		public RecordItem() {
			
		}
		/**
		 * @return the item_id
		 */
		public String getItem_id() {
			return item_id;
		}
		/**
		 * @param item_id the item_id to set
		 */
		public void setItem_id(String item_id) {
			this.item_id = item_id;
		}
		/**
		 * @return the key_id
		 */
		public String getKey_id() {
			return key_id;
		}
		/**
		 * @param key_id the key_id to set
		 */
		public void setKey_id(String key_id) {
			this.key_id = key_id;
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
		 * @return the section_id
		 */
		public String getSection_id() {
			return section_id;
		}
		/**
		 * @param section_id the section_id to set
		 */
		public void setSection_id(String section_id) {
			this.section_id = section_id;
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

	}

}
