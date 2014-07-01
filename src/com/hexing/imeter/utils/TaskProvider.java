package com.hexing.imeter.utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.util.Log;

public class TaskProvider extends ContentProvider {
	
	public static final String AUTHORITY = "com.hexing.imeter.utils.TaskProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"
            + TaskProvider.TABLE);
    private static final int GROUP_BY = 1;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, "GroupBy/*", GROUP_BY);
        //sURIMatcher.addURI(AUTHORITY, "points/#", TRACKPOINT_ID);
        //sURIMatcher.addURI(AUTHORITY, "points/live", TRACKPOINTS_LIVE);
    }
    

	private TaskDatabase mDB;
	
	static final String TAG = "DbHelper";

	static final String DB_NAME = "mission.db"; //http://dev.icybear.net/learning-android-cn/images/2.png

	static final int DB_VERSION = 1; //http://dev.icybear.net/learning-android-cn/images/3.png

	public static final String TABLE = "mission"; //http://dev.icybear.net/learning-android-cn/images/4.png
	//Main Key
	public static final String C_ID = BaseColumns._ID; //'_id'
	/*These keys below are in the database of the server*/
	public static final String C_TASK_ID = "Task_ID";
	
	public static final String C_TASK_INDEX = "Task_Index";
	
	public static final String C_POD = "Device_POD";
	
	public static final String C_TASK_ISSUE_TIME = "Task_Issue_Time";
	
	public static final String C_OPERATOR_NAME = "Operator_Name";
	
	public static final String C_TASK_TYPE = "Task_Type";

	public static final String C_TASK_STATUS = "Task_Status";
		
	public static final String C_CUSTOMER_NAME = "Customer_Name";
	
	public static final String C_CUSTOMER_NO = "Customer_No";
	
	public static final String C_DEVICE_TYPE = "Device_Type";
	
	public static final String C_INSTALL_DEVICE_NO = "Device_Install_No";
	
	public static final String C_ADDRESS = "Device_Address";
	
	public static final String C_LONGITUDE = "Device_Longitude";
	
	public static final String C_LATITUDE = "Device_Latitude";
	
	public static final String C_NEW_START_ENERGY = "Device_Install_Energy";
	
	public static final String C_REMOVE_DEVICE_NO = "Device_Remove_No";
	
	public static final String C_UPLINK_CONCENTRATOR = "Device_Uplink_Concentrator";
	
	public static final String C_OLD_END_ENERGY = "Device_Remove_Energy";
	
	public static final String C_PREPAID_OLD_BALANCE = "Device_Remove_Balance";
	
	public static final String C_TRANSFORMA_NAME = "Device_Transformer_Name";

	public static final String C_TASK_ANOMALY_REASON = "Task_Anomaly_Reason";
	/*
	public static final String C_REMOVE_LONGITUDE = "Remove_Longitude";
	
	public static final String C_REMOVE_LATITUDE = "Remove_Latitude";
	*/
	/*These keys below are only in the database of the mobile*/
	

	public static final String C_TASK_DEVICE_NO = "Task_Device_No";
	//useless
	public static final String C_WORK_FLOW_STEP = "Work_Flow_Step";
	//useless
	public static final String C_ABNORMAL = "Abnormal";
	
	public static final String C_UPLOAD_TEXT = "Upload_Info";
	
	public static final String C_UPLOAD_SIGN = "Upload_Sign";
	
	public static final String C_UPLOAD_PIC = "Upload_Pic";
	//important 
	public static final String C_ONSITE_DATE = "Onsite_Date";
	
	public static final String C_FOLDER_NAME = "Folder_Name";
	
	public static final String C_SIGNATURE_FILE = "Sign_File";
	
	public static final String C_PHOTO_FILE = "Photo_File";
	
	public static final String C_INSTALL_ISSPECIFIED = "Install_isSpecified";
	
	public static final String C_INSTALL_ISRIGHT = "Install_isRight";

	public static final String C_REMOVE_ISSPECIFIED = "Remove_isSpecified";
	
	public static final String C_REMOVE_ISRIGHT = "Remove_isRight";
	//Mission Type
	public static final String MISSION_INSTALL = "01";
	
	public static final String MISSION_REMOVE = "03";

	public static final String MISSION_REPLACE = "02";
	
	//Mission Status
	public static final String STATUS_INITIAL = "0";
	
	public static final String STATUS_PENDING = "3";

	public static final String STATUS_SUCCEED = "1";

	public static final String STATUS_FAILED = "2";
	
	//Device Type
	public static final String DEVICE_METER = "01";
	
	public static final String DEVICE_CONCENTRATOR = "02";
	
	public static final String DEVICE_COLLECTOR = "03";
	
	//Upload Status
	public static final String UPLOAD_SUCCEED = "Upload_Succeed";
	
	public static final String UPLOAD_FAILED  = "Upload_Failed";
	
	public static final String UPLOAD_WARNING = "Upload_Warning";
	
	//TRUE FALSE
	public static final String TRUE  = "True";
	
	public static final String FALSE = "False";
	
	

	
	
	private static class TaskDatabase extends SQLiteOpenHelper {
		Context context;
        public TaskDatabase(Context context) {
    		super(context, DB_NAME, null, DB_VERSION);
    		// TODO Auto-generated constructor stub
    		
    		this.context = context;

    	}

        @Override
    	public void onCreate(SQLiteDatabase db) {
    		// TODO Auto-generated method stub
    		String sql = "create table " + TABLE + " (" + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
    			+ C_TASK_INDEX 			+ " INTEGER, " 
        		+ C_TASK_ID 			+ " text, " 
        		+ C_TASK_ISSUE_TIME 	+ " text, " 
        		+ C_POD 				+ " text, " 
        		+ C_OPERATOR_NAME 		+ " text, "
        		+ C_CUSTOMER_NAME 		+ " text, " 
        		+ C_CUSTOMER_NO 		+ " text, " 
        		+ C_TASK_TYPE 			+ " text, " 
        		+ C_ONSITE_DATE			+ " text, "
        		+ C_FOLDER_NAME			+ " text, "
        		+ C_SIGNATURE_FILE		+ " text, "
        		+ C_PHOTO_FILE			+ " text, "
        		+ C_DEVICE_TYPE 		+ " text, " 
        		+ C_TASK_STATUS 		+ " text, "
        		+ C_TASK_DEVICE_NO 		+ " text, "
        		+ C_INSTALL_DEVICE_NO 	+ " text, "
        		+ C_REMOVE_DEVICE_NO 	+ " text, "
        		+ C_ADDRESS 			+ " text, "	
        		+ C_LONGITUDE 			+ " text, " 
        		+ C_LATITUDE 			+ " text, " 
        		+ C_NEW_START_ENERGY 	+ " text, "
        		+ C_PREPAID_OLD_BALANCE	+ " text, " 
        		+ C_OLD_END_ENERGY 		+ " text, "
        		+ C_UPLINK_CONCENTRATOR	+ " text, "	
        		+ C_TRANSFORMA_NAME 	+ " text, "
        		+ C_WORK_FLOW_STEP 		+ " INTEGER, " 
        		+ C_ABNORMAL 			+ " text, " 
        		+ C_UPLOAD_TEXT			+ " text, "
        		+ C_UPLOAD_SIGN			+ " text, "
        		+ C_UPLOAD_PIC 			+ " text, "
        		+ C_INSTALL_ISSPECIFIED + " text, "
        		+ C_INSTALL_ISRIGHT		+ " text, "
                + C_REMOVE_ISSPECIFIED 	+ " text, "
                + C_REMOVE_ISRIGHT		+ " text, "
        		+ C_TASK_ANOMALY_REASON + " text)"; 
    		
    		db.execSQL(sql);  

    		Log.d(TAG, "onCreated sql: " + sql);

    	}

        @Override
    	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
    		// TODO Auto-generated method stub
    		db.execSQL("drop table if exists " + TABLE); // drops the old database
    		

    	    Log.d(TAG, "onUpgraded");

    	    onCreate(db); // run onCreate to get new database

    	}
    }

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mDB = new TaskDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        // Set the table we're querying.
        qBuilder.setTables(TABLE);
        
        int match = sURIMatcher.match(uri);
        
        Cursor c;
        
        switch (match) {
		case GROUP_BY:
			String column = uri.getLastPathSegment();
			if (selection!=null) {
				qBuilder.appendWhere(selection
						+ ") GROUP BY ("+
						column);
				// Make the query.
		        c = qBuilder.query(mDB.getReadableDatabase(), projection, null, selectionArgs, null, null,
		                sortOrder, null);
	        } else {
	        	qBuilder.appendWhere("0==0) GROUP BY ("+
						column);
				// Make the query.
		        c = qBuilder.query(mDB.getReadableDatabase(), projection, selection, selectionArgs, null, null,
		                sortOrder, null);
	        }
			
			break;

		default:
			// Make the query.
	        c = qBuilder.query(mDB.getReadableDatabase(), projection, selection, selectionArgs, null, null,
	                sortOrder, null);
			break;
		}
        
        // Make the query.
        //Cursor c = qBuilder.query(mDB.getReadableDatabase(), projection, selection, selectionArgs, null, null,
        //       sortOrder, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        long newID = sqlDB.insert(TABLE, null, values);
        if (newID > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, newID);
            getContext().getContentResolver().notifyChange(uri, null);
            //sqlDB.close();
            return newUri;
        }
        //sqlDB.close();
        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase sqlDB = mDB.getWritableDatabase();
		int rowsAffected = sqlDB.delete(TABLE, selection, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		///sqlDB.close();
		return rowsAffected;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsAffected;
        
        rowsAffected = sqlDB.update(TABLE, values, selection, selectionArgs);
       
        getContext().getContentResolver().notifyChange(uri, null);
        //sqlDB.close();
        return rowsAffected;
	}
	
	
	

}

