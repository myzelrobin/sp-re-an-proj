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
public class TableSections 
{

	// Table Attributes
	public static final String TABLE_SECTIONS = "sections";
	public static final String COLUMN_ID = "_id"; // key
	public static final String COLUMN_SCRIPT_ID = "script_id"; // foreign key reference scritps(_id)
	
	// SQL statement CREATE TABLE sections
	private static final String CREATE_TABLE_SECTIONS = 
		"create table "
		+ TABLE_SECTIONS
		+ " ( "
		+ COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_SCRIPT_ID + " integer, "
		+ " FOREIGN KEY (" + COLUMN_SCRIPT_ID + ") REFERENCES scripts(_id)"
		+ " );";
	
	// create table sections
	public static void onCreate(SQLiteDatabase db)
	{
		Log.w(TableSections.class.getName(), "onCreate(): will create table: " + TABLE_SECTIONS);
		db.execSQL(CREATE_TABLE_SECTIONS);
	}
	
	// upgrade table sections
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(TableSections.class.getName(), "onUpgrade(): will upgrade table: " + TABLE_SECTIONS);
		db.execSQL("drop table if exists " + TABLE_SECTIONS  + ";\n");
		onCreate(db);
	}
	
	public static void setValuesForInsertSectionItem(ContentValues values, 
			String scriptId,
			String sectionItemCode)
	{
		values.put(COLUMN_SCRIPT_ID, Integer.parseInt(scriptId));
	}
	
	
	// a method to read xml file and insert data into database
	// need xml parser

	public static class SectionItem {

		
		private String idInTable;
		
		
		public SectionItem() 
		{
			// TODO Auto-generated constructor stub
		}


	}

}
