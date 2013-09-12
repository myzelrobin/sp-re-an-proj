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
public class TableServers 
{

	// Table Attributes
	public static final String TABLE_SERVERS = "servers";
	public static final String COLUMN_ID = "_id"; // key
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_PASSWORD = "password"; 
	public static final String COLUMN_DESCRIPTION = "description";
	

	
	// SQL statement CREATE TABLE servers
	private static final String CREATE_TABLE_SERVERS = 
		"create table "
		+ TABLE_SERVERS
		+ " ( "
		+ COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_ADDRESS + " text not null, "
		+ COLUMN_USERNAME + " text, "
		+ COLUMN_PASSWORD + " text, "
		+ COLUMN_DESCRIPTION + " text "
		+ " );";
	
	public static ArrayList<String> ALL_COLUMNS = new ArrayList<String>();
	
	
	/**
	 * 
	 */
	public TableServers() 
	{
		
	}

	// create table servers
	public static void onCreate(SQLiteDatabase db)
	{
		Log.w(TableServers.class.getName(), "onCreate(): will create table: " + TABLE_SERVERS);
		setALL_COLUMNS();
		db.execSQL(CREATE_TABLE_SERVERS);
		insertServerExamples(db);
	}
	
	// upgrade table servers
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(TableServers.class.getName(), "onUpgrade(): will upgrade table: " + TABLE_SERVERS);
		db.execSQL("drop table if exists " + TABLE_SERVERS + ";\n");
		onCreate(db);
	}
	
	public static  void insertServerExamples(SQLiteDatabase db) 
	{
		Log.w(TableServers.class.getName(), "insertServerExamples() will insert examples");
		ContentValues values = new ContentValues(); 
		for (int i = 1; i < 11; i++)
		{
			values.put(COLUMN_ADDRESS, "1.1.1.1:8080");
			values.put(COLUMN_DESCRIPTION, "Example server " + i);
			values.put(COLUMN_USERNAME, "testuser");
			values.put(COLUMN_PASSWORD, "11111111");
			db.insert(TABLE_SERVERS, null, values);
		}
		
		
	}
	
	
	public static void setValuesForInsertServerItem(ContentValues values, 
			String address,
			String username,
			String password,
			String description)
	{
		
		values.put(COLUMN_ADDRESS, address);
		
		values.put(COLUMN_USERNAME, username);
		
		values.put(COLUMN_PASSWORD, password);
		
		values.put(COLUMN_DESCRIPTION, description);
		
	}

	public static class ServerItem 
	{
		public String idInTable = null; 
		public String address = null;
		public String username = null;
		public String password = null; 
		public String description= null;
		
		public ServerItem(
				String id,
				String address, 
				String username, 
				String password, 
				String description) 
		{
			this.idInTable = id;
			this.address = address;
			this.username = username;
			this.password = password;
			this.description = description;
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
		ALL_COLUMNS.add(COLUMN_ADDRESS);
		ALL_COLUMNS.add(COLUMN_USERNAME);
		ALL_COLUMNS.add(COLUMN_PASSWORD);
		ALL_COLUMNS.add(COLUMN_DESCRIPTION);
	}

}
