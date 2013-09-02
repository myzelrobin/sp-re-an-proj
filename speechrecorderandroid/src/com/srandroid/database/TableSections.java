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
public class TableSections 
{

	// Table Attributes
	public static final String TABLE_SECTIONS = "sections";
	public static final String COLUMN_ID = "_id"; // key
	public static final String COLUMN_ITEMCODE = "itemcode"; // text not null
	public static final String COLUMN_SCRIPT_ID = "script_id"; // foreign key reference scritps(_id)
	
	// SQL statement CREATE TABLE sections
	private static final String CREATE_TABLE_SECTIONS = 
		"create table "
		+ TABLE_SECTIONS
		+ " ( "
		+ COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_ITEMCODE + " text not null, "
		+ COLUMN_SCRIPT_ID + " integer, "
		+ " FOREIGN KEY (" + COLUMN_SCRIPT_ID + ") REFERENCES scripts(_id)"
		+ " );";
	
	public static ArrayList<String> ALL_COLUMNS = new ArrayList<String>();
	
	/**
	 * 
	 */
	public TableSections() 
	{
		
	}

	// create table sections
	public static void onCreate(SQLiteDatabase db)
	{
		Log.w(TableSections.class.getName(), "onCreate(): will create table: " + TABLE_SECTIONS);
		setALL_COLUMNS();
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
		values.put(COLUMN_ITEMCODE, sectionItemCode);
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
		ALL_COLUMNS.add(COLUMN_ID);
		ALL_COLUMNS.add(COLUMN_ITEMCODE);
		ALL_COLUMNS.add(COLUMN_SCRIPT_ID);
	}

}
