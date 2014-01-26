package com.marketo.leadexplorer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LeadListDatabase extends SQLiteOpenHelper {
	private static final String DEBUG_TAG = "LeadListDatabase";
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "marketo";

	public static final String TABLE_LEADS = "leads";
	public static final String ID = "_id";
	public static final String COL_MID = "mktoId";
	public static final String COL_FN = "fn";
	public static final String COL_LN = "ln";
	public static final String COL_EMAIL = "email";
	public static final String COL_FB_URL = "fburl";
	public static final String COL_LI_URL = "liurl";
	public static final String COL_SCORE = "score";

	private static final String CREATE_TABLE_leads = "CREATE TABLE "
			+ TABLE_LEADS + " (" + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_MID
			+ " INTEGER UNIQUE NOT NULL, " + COL_FN + " TEXT, " + COL_LN
			+ " TEXT, " + COL_EMAIL + " TEXT, " + COL_FB_URL + " TEXT, "
			+ COL_LI_URL + " TEXT, " + COL_SCORE + " INTEGER " + ");";
	// + " INTEGER NOT NULL DEFAULT (strftime('%s','now'))" + ");";

	private static final String DB_SCHEMA = CREATE_TABLE_leads;

	public LeadListDatabase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_SCHEMA);
		seedData(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 2 && newVersion == 3) {
			// keep the data that's there, add the two new columns

			// sqlite has restrictions on add column -- no expressions or
			// current time values, this value is mid february 2011
//			db.execSQL("alter table " + TABLE_LEADS + " add column "
//					+ COL_DATE + " INTEGER NOT NULL DEFAULT '1297728000' ");

		} else {
			Log.w(DEBUG_TAG,
					"Upgrading database. Existing contents will be lost. ["
							+ oldVersion + "]->[" + newVersion + "]");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADS);
			onCreate(db);
		}
	}

	/**
	 * Create sample data to use
	 * 
	 * @param db
	 *            The open database
	 */
	private void seedData(SQLiteDatabase db) {
		db.execSQL("insert into leads (mktoId,fn,ln,email,fburl,liurl,score) values (1, 'John', 'Doe', 'jdoe@mk.com', 'www.fb.com', 'www.li.com',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,email,fburl,liurl,score) values (2, 'Jane', 'Doe', 'jdoe@mk.com', 'www.fb.com', 'www.li.com',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,email,fburl,liurl,score) values (3, 'Mark', 'Doe', 'jdoe@mk.com', 'www.fb.com', 'www.li.com',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,email,fburl,liurl,score) values (4, 'Shaun', 'Doe', 'jdoe@mk.com', 'www.fb.com', 'www.li.com',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,email,fburl,liurl,score) values (5, 'Glen', 'Doe', 'jdoe@mk.com', 'www.fb.com', 'www.li.com',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,email,fburl,liurl,score) values (6, 'Rajiv', 'Doe', 'jdoe@mk.com', 'www.fb.com', 'www.li.com',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,email,fburl,liurl,score) values (7, 'Raj', 'Doe', 'jdoe@mk.com', 'www.fb.com', 'www.li.com',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,email,fburl,liurl,score) values (8, 'James', 'Doe', 'jdoe@mk.com', 'www.fb.com', 'www.li.com',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,email,fburl,liurl,score) values (9, 'Miky', 'Doe', 'jdoe@mk.com', 'www.fb.com', 'www.li.com',5)");
		
	}
}
