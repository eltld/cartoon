package com.cutv.mobile.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyDB {
	private static MyDB myDBInstance = null;
	
	private SQLiteDatabase mDatabase = null;
	private String mDBFilename = "my.db";
	
	public MyDB()
	{
	}
	
	public static MyDB getInstance()
	{
		if( myDBInstance == null )
		{
			myDBInstance = new MyDB();
		}
		
		return myDBInstance;
	}
	
	public int db_open(Context context)
	{
		mDatabase = context.openOrCreateDatabase(mDBFilename, Context.MODE_PRIVATE, null);
		if( mDatabase == null )
			return 1;

		create();
		
		return 0;
	}
	
	public void db_close()
	{
		mDatabase.close();
	}
	
	public void create()
	{
		
		/*
	//	execSQL("drop table downloadtask");
		String sql = "create table if not exists downloadtask(" +
					"sid integer primary key," +
				   "taskid varchar(40) not null," +
				   "videoid integer not null,"+
				   "title varchar(256) not null,"+
				   "source varchar(256) null,"+
				   "duration integer,"+
				   "thumb varchar(255),"+
				   "playurl varchar(256)," + 
				   "downloadurl varchar(256)," + 
				   "videofile varchar(256),"+
				   "totalsize integer,"+
				   "read integer,"+
				   "state integer,"+ // 0_download 1_download ok
				   
				   "desc varchar(512) null,"+
				   "publishtime varchar(50) null,"+
				   "viewtime TimeStamp NOT NULL DEFAULT (datetime('now','localtime'))"+
				   ");";
				Log.v("", sql);   

				   mDatabase.execSQL(sql);

			sql = "create table if not exists watchhistory(" + 
				   "sid integer primary key," + 
				   "videoid integer not null," + 
				   "title varchar(100) not null," + 
				   "thumb varchar(256) not null," + 
				   "source varchar(256) not null," +
				   "publishtime varchar(100) not null,"+
				   "watchtime varchar(50) not null," + 
				   "videostore integer not null," +  // 0_local 1_server
				   "duration real not null default(0)," + 
				   "videofile varchar(256) null" +  // videostore=1 对应的playurl videostore=0时�?对应本地视频文件
				   ");";
			
			mDatabase.execSQL(sql);
			
			
			sql = "create table if not exists favorites(" + 
					   "sid integer primary key," + 
					   "videoid integer not null," + 
					   "model integer not null," + 
					   "title varchar(100) not null," + 
					   "thumb varchar(256) not null," + 
					   "source varchar(256) not null," +
					   "publishtime varchar(100) not null,"+
					   "duration real not null default(0)," + 
					   "downloadurl varchar(256) null," +
					   "playurl varchar(256) null" +
					   ");";
				
				mDatabase.execSQL(sql);
			
			*/
	}
	
	public boolean execSQL(String sql)
	{
		try
		{
			mDatabase.execSQL(sql);
			return true;
		}
		catch(Exception e)
		{
			Log.v("error", sql);
			return false;
		}
	}
	
	
	public Cursor query(String sql)
	{
		return mDatabase.rawQuery(sql, null);
	}
	
}
