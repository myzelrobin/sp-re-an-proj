/**
 * 
 */
package com.srandroid.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.srandroid.database.DBAccessor;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 *
 */
public class SrmContentProvider extends ContentProvider 
{
	// database 
	private DBAccessor dbAccesor;
	private SQLiteDatabase srmDB;

	/**
	 * 
	 */
	public SrmContentProvider() 
	{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onCreate() 
	{
		dbAccesor = new DBAccessor(getContext());
		Log.w(SrmContentProvider.class.getName(), 
				"onCreate(): created content provider for database: " + 
						dbAccesor.getDatabaseName());
		return true;
	}
	
	@Override
	public String getType(Uri arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}

/*
	// an example of ITEM_ID
	// append itemid to where part, only for select from single table
	case SrmUriMatcher.SPEAKER_ITEM_ID:
		// Adding the wherePart = wherePartValues to the query
		queryBuilder.appendWhere(TableSpeakers.COLUMN_ID 
				+ "=" + uri.getLastPathSegment());
		break;
*/
	@Override
	public Cursor query(Uri uri,       // FROM
			String[] selectColumns,    // SELECT
			String wherePart,      // WHERE
			String[] wherePartValues,   // WHERE =
			String sortOrder)           // ORDER BY
	{
		Cursor cursor = null;
		
		String table = null;
		
		checkColumns(selectColumns);
		
		// switcher for query actions
		// for LOJ must include _id column
		// 1: query from an existing table
		// 2: query from sessions LOJ speakers
		// 3: query from speakers LOJ sessions
		// 4: query from scripts LOJ sessions
		int switcher = 0;
		
		int uriType = SrmUriMatcher.uriMatcher.match(uri);
		switch(uriType)
		{
			default:
				throw new IllegalArgumentException(
						SrmContentProvider.class.getName() 
						+ "query(): Unknown URI: " + uri);

			case SrmUriMatcher.TABLE_SPEAKERS:
				table = TableSpeakers.TABLE_SPEAKERS;
				switcher = 1;
				break;

			case SrmUriMatcher.TABLE_SCRIPTS:
				table = TableScripts.TABLE_SCRIPTS;
				switcher = 1;
				break;
			
			case SrmUriMatcher.TABLE_SERVERS:
				table = TableServers.TABLE_SERVERS;
				switcher = 1;
				break;
			
			case SrmUriMatcher.TABLE_SESSIONS:
				table = TableSessions.TABLE_SESSIONS;
				switcher = 1;
				break;
			
			case SrmUriMatcher.TABLE_SECTIONS:
				table = TableSections.TABLE_SECTIONS;
				switcher = 1;
				break;
			
			case SrmUriMatcher.TABLE_RECORDS:
				table = TableRecords.TABLE_RECORDS;
				switcher = 1;
				break;
			
			case SrmUriMatcher.TABLE_SESSIONS_LEFTJOIN_SPEAKERS:
				switcher = 2;
				return cursor;
			
			case SrmUriMatcher.TABLE_SPEAKERS_LEFTJOIN_SESSIONS:
				switcher = 3;
				break;
				
			case SrmUriMatcher.TABLE_SCRIPTS_LOJ_SESSIONS:
				switcher = 4;
				break;
		}
		
		srmDB = dbAccesor.getReadableDatabase();
		
		StringBuilder sBuiderTemp = new StringBuilder();
		String sSelectColumns = null;
		String sRawQuery = null;
		
		switch (switcher) 
		{
			default:
				Log.w(SrmContentProvider.class.getName(), "query() switcher has a wrong value!");
				break;
			
			case 1: // from existing tables
				// buildQueryString(boolean distinct, String tables, String[] columns, String where, 
				// 					String groupBy, String having, String orderBy, String limit)
				sRawQuery = SQLiteQueryBuilder.buildQueryString(
						false, table, selectColumns, wherePart, 
						null, null, sortOrder, null);
				Log.w(SrmContentProvider.class.getName(), 
						"query(): will query: " + sRawQuery);
				cursor = srmDB.rawQuery(sRawQuery, null);
//				cursor = queryBuilder.query(srmDB, 
//											selectColumns,  // select
//											wherePart, // where
//											null,  // where =
//											null,  // group by
//											null,  // having
//											sortOrder);  // sort by
				break;
			
			case 2: // sessions left outer join speakers
				sBuiderTemp.append(selectColumns[0]);
				for (int i = 1; i < selectColumns.length; i++) {
					sBuiderTemp.append("," + selectColumns[i]);
				}
				sSelectColumns = sBuiderTemp.toString();
				if(wherePart == null)
				{
					sRawQuery = "SELECT " + sSelectColumns 
							+ ", sessions._id, sessions._id as session_key_id, speakers._id as speaker_key_id"
							+ " FROM sessions LEFT OUTER JOIN speakers ON sessions.speaker_id=speakers._id;";
				}
				else
				{
					sRawQuery = "SELECT " + sSelectColumns 
							+ ", sessions._id, sessions._id as session_key_id, speakers._id as speaker_key_id"
							+ " FROM sessions LEFT OUTER JOIN speakers ON sessions.speaker_id=speakers._id"
							+ " WHERE " + wherePart;
				}
				
				Log.w(SrmContentProvider.class.getName(), "query(): will query: " + sRawQuery);
				cursor = srmDB.rawQuery(sRawQuery, null);
				break;
			
			case 3: // speakers left outer join sessions
				sBuiderTemp.append(selectColumns[0]);
				for (int i = 1; i < selectColumns.length; i++) {
					sBuiderTemp.append("," + selectColumns[i]);
				}
				sSelectColumns = sBuiderTemp.toString();
				if(wherePart == null)
				{
					sRawQuery = "SELECT " + sSelectColumns 
							+ ", speakers._id, speakers._id AS speaker_key_id, sessions._id AS session_key_id "
							+ " FROM speakers LEFT OUTER JOIN sessions ON sessions.speaker_id=speakers._id;";
				}
				else
				{
					sRawQuery = "SELECT " + sSelectColumns 
							+ ", speakers._id, speakers._id AS speaker_key_id, sessions._id AS session_key_id "
							+ " FROM speakers LEFT OUTER JOIN sessions ON sessions.speaker_id=speakers._id"
							+ " WHERE " + wherePart + ";";
				}
				
				Log.w(SrmContentProvider.class.getName(), "query(): will query: " + sRawQuery);
				cursor = srmDB.rawQuery(sRawQuery, null);
				break;
			
			case 4: // scripts left outer join sessions
				sBuiderTemp.append(selectColumns[0]);
				for (int i = 1; i < selectColumns.length; i++) {
					sBuiderTemp.append("," + selectColumns[i]);
				}
				sSelectColumns = sBuiderTemp.toString();
				if(wherePart == null)
				{
					sRawQuery = "SELECT " + sSelectColumns 
							+ ", scripts._id, scripts._id AS script_key_id, sessions._id AS session_key_id "
							+ " FROM scripts LEFT OUTER JOIN sessions ON sessions.script_id=scripts._id;";
				}
				else
				{
					sRawQuery = "SELECT " + sSelectColumns 
							+ ", scripts._id, scripts._id AS script_key_id, sessions._id AS session_key_id "
							+ " FROM scripts LEFT OUTER JOIN sessions ON sessions.script_id=scripts._id"
							+ " WHERE " + wherePart + ";";
				}
				
				Log.w(SrmContentProvider.class.getName(), "query(): will query: " + sRawQuery);
				cursor = srmDB.rawQuery(sRawQuery, null);
				break;
		}
		
		// By default, CursorAdapter objects will get this notification.
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	
	@Override
	public int delete(Uri uri, 
			String wherePart, 
			String[] wherePartValues) 
	{
		
		// table
		String table = null;
		
		int uriType = SrmUriMatcher.uriMatcher.match(uri);
		switch(uriType)
		{
			default:
				throw new IllegalArgumentException(
						SrmContentProvider.class.getName() 
						+ "delete(): Unknown URI: " + uri);
				
			case SrmUriMatcher.TABLE_SPEAKERS: 
				table = TableSpeakers.TABLE_SPEAKERS;
				break;
				
			case SrmUriMatcher.TABLE_SCRIPTS:
				table = TableScripts.TABLE_SCRIPTS;
				break;
				
				
			case SrmUriMatcher.TABLE_SERVERS:
				table = TableServers.TABLE_SERVERS;
				break;
				
				
			case SrmUriMatcher.TABLE_SESSIONS:
				table = TableSessions.TABLE_SESSIONS;
				break;
				
			case SrmUriMatcher.TABLE_SECTIONS:
				table = TableSections.TABLE_SECTIONS;
				break;
				
			case SrmUriMatcher.TABLE_RECORDS:
				table = TableRecords.TABLE_RECORDS;
				break;
				
		}
		
		srmDB = dbAccesor.getWritableDatabase();
		
		int rowDeleted = 0;
		
		Log.w(SrmContentProvider.class.getName(), 
						"delete(): will delete items from table {" + table 
						+ "} with WHERE {" + wherePart + "}");
		rowDeleted = srmDB.delete(table, wherePart, wherePartValues);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return rowDeleted;
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) 
	{
		// table
		// String table = null;
				
		Uri uriTemp = null;
		
		srmDB = dbAccesor.getWritableDatabase();
		
		long id = 0;
		
		int uriType = SrmUriMatcher.uriMatcher.match(uri);
		switch(uriType)
		{
			default:
				throw new IllegalArgumentException(
						SrmContentProvider.class.getName() 
						+ "insert(): Unknown URI: " + uri);
				
			case SrmUriMatcher.TABLE_SPEAKERS:
				Log.w(SrmContentProvider.class.getName(), 
						"insert(): will insert item(" + values.toString() 
						+ ") into table " + TableSpeakers.TABLE_SPEAKERS);
				id = srmDB.insert(TableSpeakers.TABLE_SPEAKERS, null, values);
				uriTemp = Uri.parse(SrmUriMatcher.PATH_TABLE_SPEAKERS + "/" + id);
				break;
				
			case SrmUriMatcher.TABLE_SCRIPTS:
				Log.w(SrmContentProvider.class.getName(), 
						"insert(): will insert item(" + values.toString() 
						+ ") into table " + TableScripts.TABLE_SCRIPTS);
				id = srmDB.insert(TableScripts.TABLE_SCRIPTS, null, values);
				uriTemp = Uri.parse(SrmUriMatcher.PATH_TABLE_SCRIPTS + "/" + id);
				break;
				
			case SrmUriMatcher.TABLE_SERVERS:
				Log.w(SrmContentProvider.class.getName(), 
						"insert(): will insert item(" + values.toString() 
						+ ") into table " + TableServers.TABLE_SERVERS);
				id = srmDB.insert(TableServers.TABLE_SERVERS, null, values);
				uriTemp = Uri.parse(SrmUriMatcher.PATH_TABLE_SERVERS + "/" + id);
				break;
				
			case SrmUriMatcher.TABLE_SESSIONS:
				Log.w(SrmContentProvider.class.getName(), 
						"insert(): will insert item(" + values.toString() 
						+ ") into table " + TableSessions.TABLE_SESSIONS);
				id = srmDB.insert(TableSessions.TABLE_SESSIONS, null, values);
				uriTemp = Uri.parse(SrmUriMatcher.PATH_TABLE_SESSIONS + "/" + id);
				break;
				
			case SrmUriMatcher.TABLE_SECTIONS:
				Log.w(SrmContentProvider.class.getName(), 
						"insert(): will insert item(" + values.toString() 
						+ ") into table " + TableSections.TABLE_SECTIONS);
				id = srmDB.insert(TableSections.TABLE_SECTIONS, null, values);
				uriTemp = Uri.parse(SrmUriMatcher.PATH_TABLE_SECTIONS + "/" + id);
				break;
				
			case SrmUriMatcher.TABLE_RECORDS:
				Log.w(SrmContentProvider.class.getName(), 
						"insert(): will insert item(" + values.toString() 
						+ ") into table " + TableRecords.TABLE_RECORDS);
				id = srmDB.insert(TableRecords.TABLE_RECORDS, null, values);
				uriTemp = Uri.parse(SrmUriMatcher.PATH_TABLE_RECORDS + "/" + id);
				break;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return uriTemp;
	}

	

	@Override
	public int update(Uri uri, 
			ContentValues values, 
			String wherePart, 
			String[] wherePartValues) 
	{
		// table
		String table = null;
		
		int uriType = SrmUriMatcher.uriMatcher.match(uri);
		switch(uriType)
		{
			default:
				throw new IllegalArgumentException(
						SrmContentProvider.class.getName() 
						+ "update(): Unknown URI: " + uri);
			
			case SrmUriMatcher.TABLE_SPEAKERS:
				table = TableSpeakers.TABLE_SPEAKERS;
				break;
				
			case SrmUriMatcher.TABLE_SCRIPTS:
				table = TableScripts.TABLE_SCRIPTS;
				break;
				
			case SrmUriMatcher.TABLE_SERVERS:
				table = TableServers.TABLE_SERVERS;
				break;
				
			case SrmUriMatcher.TABLE_SESSIONS:
				table = TableSessions.TABLE_SESSIONS;
				break;
				
			case SrmUriMatcher.TABLE_SECTIONS:
				table = TableSections.TABLE_SECTIONS;
				break;
				
			case SrmUriMatcher.TABLE_RECORDS:
				table = TableRecords.TABLE_RECORDS;
				break;
		}
		
		srmDB = dbAccesor.getWritableDatabase();
		
		int rowUpdated = 0;
		
		Log.w(SrmContentProvider.class.getName(), 
				"update() will update item in table " + table 
				+ " WHERE{" + wherePart + "}");
		rowUpdated = srmDB.update(table, values, wherePart, wherePartValues);
		
		//
		getContext().getContentResolver().notifyChange(uri, null);
		return rowUpdated;
	}
	
	private void checkColumns(String[] inputColumns)
	{
		ArrayList<String> availableCols = DBAccessor.getAllTableColumns(); // add tableColumns
		if (inputColumns != null)
		{
			HashSet<String> requestedColsTemp = 
					new HashSet<String> (Arrays.asList(inputColumns));
			HashSet<String> availableColsTemp = 
					new HashSet<String> (availableCols);
			
			if(!availableColsTemp.containsAll(requestedColsTemp))
			{
				throw new IllegalArgumentException(
						SrmContentProvider.class.getName() 
						+ " checkColumns(): "
						+ "Unknown requested column in selectColumns!");
			}
			
		}
	}
	
	
	/**
	 * UriMatch class
	 * 
	 * 
	 */
	public static class SrmUriMatcher
	{

		// UriMatcher
		
		// com.srandroid.contentprovider
		private static final String AUTHORITY = "com.srandroid.contentprovider";
		
		// table speakers
		private static final String PATH_TABLE_SPEAKERS = "speakers";
		public static final Uri CONTENT_URI_TABLE_SPEAKERS = 
				Uri.parse("content://" + AUTHORITY + "/" + PATH_TABLE_SPEAKERS);
		
		public static final String CONTENT_TYPE_TABLE_SPEAKERS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/speakers";
		public static final String CONTENT_ITEM_TYPE_SPEAKER = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/speaker_item";
		
		private static final int TABLE_SPEAKERS = 10;
		private static final int SPEAKER_ITEM_ID = 11; //_id
		
		// table scripts
		private static final String PATH_TABLE_SCRIPTS = "scripts";
		public static final Uri CONTENT_URI_TABLE_SCRIPTS = 
				Uri.parse("content://" + AUTHORITY + "/" + PATH_TABLE_SCRIPTS);
		
		public static final String CONTENT_TYPE_TABLE_SCRIPTS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/scripts";
		public static final String CONTENT_ITEM_TYPE_SCRIPT = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/script_item";
		
		private static final int TABLE_SCRIPTS = 20;
		private static final int SCRIPT_ITEM_ID = 21; //_id
		
		// table servers
		private static final String PATH_TABLE_SERVERS = "servers";
		public static final Uri CONTENT_URI_TABLE_SERVERS = 
				Uri.parse("content://" + AUTHORITY + "/" + PATH_TABLE_SERVERS);
		
		public static final String CONTENT_TYPE_TABLE_SERVERS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/servers";
		public static final String CONTENT_ITEM_TYPE_SERVER = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/server_item";
		
		private static final int TABLE_SERVERS = 30;
		private static final int SERVER_ITEM_ID = 31; //_id
		
		// table sessions
		private static final String PATH_TABLE_SESSIONS = "sessions";
		public static final Uri CONTENT_URI_TABLE_SESSIONS = 
				Uri.parse("content://" + AUTHORITY + "/" + PATH_TABLE_SESSIONS);
		
		public static final String CONTENT_TYPE_TABLE_SESSIONS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/sessions";
		public static final String CONTENT_ITEM_TYPE_SESSION = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/session_item";
		
		private static final int TABLE_SESSIONS = 40;
		private static final int SESSION_ITEM_ID = 41; //_id
		
		// table sections
		private static final String PATH_TABLE_SECTIONS = "sections";
		public static final Uri CONTENT_URI_TABLE_SECTIONS = 
				Uri.parse("content://" + AUTHORITY + "/" + PATH_TABLE_SECTIONS);
		
		public static final String CONTENT_TYPE_TABLE_SECTIONS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/sections";
		public static final String CONTENT_ITEM_TYPE_SECTION = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/section_item";
		
		private static final int TABLE_SECTIONS = 50;
		private static final int SECTION_ITEM_ID = 51; //_id
		
		// table records
		private static final String PATH_TABLE_RECORDS = "records";
		public static final Uri CONTENT_URI_TABLE_RECORDS = 
				Uri.parse("content://" + AUTHORITY + "/" + PATH_TABLE_RECORDS);
		
		public static final String CONTENT_TYPE_TABLE_REORDS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/records";
		public static final String CONTENT_ITEM_TYPE_RECORD = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/record_item";
		
		private static final int TABLE_RECORDS = 60;
		private static final int RECORD_ITEM_ID = 61; //_id
		
		
		// other tables
		
		// some special URIs, like Join, for what queries the queryBuilder can not build
		
		// sessions left join speakers
		private static final String PATH_TABLE_SESSIONS_LEFTJOIN_SPEAKERS = "table_sessions_leftjoin_speakers";
		public static final Uri CONTENT_URI_TABLE_SESSIONS_LEFTJOIN_SPEAKERS = 
				Uri.parse("content://" + AUTHORITY + "/" + PATH_TABLE_SESSIONS_LEFTJOIN_SPEAKERS);
		
		public static final String CONTENT_TYPE_TABLE_SESSIONS_LFETJOIN_SPEAKERS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/table_sessions_leftjoin_speakers";
		public static final String CONTENT_ITEM_TYPE_SESSIONS_LFETJOIN_SPEAKERS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/item_sessions_leftjoin_speakers";
		
		private static final int TABLE_SESSIONS_LEFTJOIN_SPEAKERS = 101;
		
		
		// speakers left join sessions
		private static final String PATH_TABLE_SPEAKERS_LEFTJOIN_SESSIONS = "table_speakers_leftjoin_sessions";
		public static final Uri CONTENT_URI_TABLE_SPEAKERS_LEFTJOIN_SESSIONS  = 
				Uri.parse("content://" + AUTHORITY + "/" + PATH_TABLE_SPEAKERS_LEFTJOIN_SESSIONS);
		
		public static final String CONTENT_TYPE_TABLE_SPEAKERS_LEFTJOIN_SESSIONS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/table_speakers_leftjoin_sessions";
		public static final String CONTENT_ITEM_TYPE_SPEAKERS_LEFTJOIN_SESSIONS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/item_speakers_leftjoin_sessions";
		
		private static final int TABLE_SPEAKERS_LEFTJOIN_SESSIONS = 102;
		
		
		// scripts left outer join speakers sessions
		private static final String PATH_TABLE_SCRIPTS_LOJ_SESSIONS = "table_sessions_loj_sessions";
		public static final Uri CONTENT_URI_TABLE_SCRIPTS_LOJ_SESSIONS = 
				Uri.parse("content://" + AUTHORITY + "/" + PATH_TABLE_SCRIPTS_LOJ_SESSIONS);
		
		public static final String CONTENT_TYPE_TABLE_SCRIPTS_LOJ_SESSIONS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/table_sessions_loj_sessions";
		public static final String CONTENT_ITEM_TYPE_SCRIPTS_LOJ_SESSIONS = 
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/item_sessions_loj_sessions";
		
		private static final int TABLE_SCRIPTS_LOJ_SESSIONS = 103;
		
		
		// if necessary, define Strings above and add uri below, like SELECT JOIN
		
		private static final UriMatcher  uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		static 
		{
			// table speakers
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SPEAKERS, TABLE_SPEAKERS);
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SPEAKERS + "/#", SPEAKER_ITEM_ID);
			
			// table scripts
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SCRIPTS, TABLE_SCRIPTS);
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SCRIPTS + "/#", SCRIPT_ITEM_ID);
			
			// table servers
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SERVERS, TABLE_SERVERS);
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SERVERS + "/#", SERVER_ITEM_ID);
			
			// table sessions
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SESSIONS, TABLE_SESSIONS);
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SESSIONS + "/#", SESSION_ITEM_ID);
			
			// table sections
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SECTIONS, TABLE_SECTIONS);
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SECTIONS + "/#", SECTION_ITEM_ID);
			
			// table records
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_RECORDS, TABLE_RECORDS);
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_RECORDS + "/#", RECORD_ITEM_ID);
			
			
			// other tables
			
			// sessions left outer join speakers
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SESSIONS_LEFTJOIN_SPEAKERS, 
					TABLE_SESSIONS_LEFTJOIN_SPEAKERS);
			
			// speakers left outer join sessions
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SPEAKERS_LEFTJOIN_SESSIONS, 
					TABLE_SPEAKERS_LEFTJOIN_SESSIONS);
			
			// scripts left outer join sessions
			uriMatcher.addURI(AUTHORITY, PATH_TABLE_SCRIPTS_LOJ_SESSIONS, 
					TABLE_SCRIPTS_LOJ_SESSIONS);
			
			
		}
		
	}

}
