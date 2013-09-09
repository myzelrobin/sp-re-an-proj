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
	private static final int DATABASE_VERSION = 2013090901; // last 2013090900
	
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
	
	
	public static String[] ALLTABLECOLS = 
		{
			TableRecords.COLUMN_COMMENT, // table records
			TableRecords.COLUMN_FILEPATH,
			TableRecords.COLUMN_ID,
			TableRecords.COLUMN_INSTRUCTION,
			TableRecords.COLUMN_ISUPLOADED,
			TableRecords.COLUMN_ITEMCODE,
			TableRecords.COLUMN_PROMPT,
			TableRecords.COLUMN_SCRIPT_ID,
			TableRecords.COLUMN_SESSION_ID,
			TableRecords.COLUMN_SPEAKER_ID,
			TableRecords.COLUMN_ITEMTYPE,
			TableScripts.COLUMN_COUNT, // table scripts
			TableScripts.COLUMN_DESCRIPTION,
			TableScripts.COLUMN_FILEPATH,
			TableScripts.COLUMN_ID,
			TableScripts.COLUMN_SERVER_ID,
			TableSections.COLUMN_ID, // table sections
			TableSections.COLUMN_ITEMCODE,
			TableSections.COLUMN_SCRIPT_ID,
			TableServers.COLUMN_ADDRESS, // table servers
			TableServers.COLUMN_DESCRIPTION,
			TableServers.COLUMN_ID,
			TableServers.COLUMN_PASSWORD,
			TableServers.COLUMN_USERNAME,
			TableSessions.COLUMN_DATE, // table sessions
			TableSessions.COLUMN_DEVICE_DATA,
			TableSessions.COLUMN_GPS_DATA,
			TableSessions.COLUMN_ID,
			TableSessions.COLUMN_IS_FINISHED,
			TableSessions.COLUMN_IS_UPLOADED,
			TableSessions.COLUMN_LAST_SECTION,
			TableSessions.COLUMN_PLACE,
			TableSessions.COLUMN_SCRIPT_ID,
			TableSessions.COLUMN_SPEAKER_ID,
			TableSessions.COLUMN_TIME,
			TableSpeakers.COLUMN_ACCENT, // table speakers
			TableSpeakers.COLUMN_BIRTHDAY,
			TableSpeakers.COLUMN_FIRSTNAME,
			TableSpeakers.COLUMN_ID,
			TableSpeakers.COLUMN_SEX,
			TableSpeakers.COLUMN_SURNAME
		};
	
}
