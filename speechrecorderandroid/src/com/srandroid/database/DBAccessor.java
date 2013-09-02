/**
 * 
 */
package com.srandroid.database;

import java.util.ArrayList;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 */
public class DBAccessor extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "srandroid_database.db";
	private static final int DATABASE_VERSION = 14; // last 13
	

	public DBAccessor(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// create database, create tables
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		Log.w(DBAccessor.class.getName(), "onCreate() will create database");
		TableSpeakers.onCreate(db);
		TableServers.onCreate(db);
		TableScripts.onCreate(db);
		TableSessions.onCreate(db);
		TableSections.onCreate(db);
		TableRecords.onCreate(db);
	}

	// upgrade database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		Log.w(DBAccessor.class.getName(), 
				"will upgrade database from version " 
				+ oldVersion + " to " + newVersion 
				+ ", which will destroy all old data");
			TableSpeakers.onUpgrade(db, oldVersion, newVersion);
			TableServers.onUpgrade(db, oldVersion, newVersion);
			TableScripts.onUpgrade(db, oldVersion, newVersion);
			TableSessions.onUpgrade(db, oldVersion, newVersion);
			TableSections.onUpgrade(db, oldVersion, newVersion);
			TableRecords.onUpgrade(db, oldVersion, newVersion);
	}
	
	
	public static ArrayList<String> getAllTableColumns()
	{
		ArrayList<String> listTemp = new ArrayList<String>();
		
		listTemp.addAll(TableRecords.ALL_COLUMNS);
		listTemp.addAll(TableScripts.ALL_COLUMNS);
		listTemp.addAll(TableSections.ALL_COLUMNS);
		listTemp.addAll(TableServers.ALL_COLUMNS);
		listTemp.addAll(TableSessions.ALL_COLUMNS);
		listTemp.addAll(TableSpeakers.ALL_COLUMNS);
		
		
		// not include columns for cross join
		return listTemp;
	}
	
}
