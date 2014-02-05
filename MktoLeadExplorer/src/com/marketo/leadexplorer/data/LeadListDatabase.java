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
	public static final String COL_EMAIL = "em";
	public static final String COL_FB_URL = "fburl";
	public static final String COL_LI_URL = "liurl";
	public static final String COL_SCORE = "score";

	private static final String CREATE_TABLE_LEADS = "CREATE TABLE "
			+ TABLE_LEADS + " (" + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_MID
			+ " INTEGER UNIQUE NOT NULL, " + COL_FN + " TEXT, " + COL_LN
			+ " TEXT, " + COL_EMAIL + " TEXT, " + COL_FB_URL + " TEXT, "
			+ COL_LI_URL + " TEXT, " + COL_SCORE + " INTEGER " + ");";

	private static final String DB_SCHEMA = CREATE_TABLE_LEADS;

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
		db.execSQL("insert into leads (mktoId,fn,ln,em,fburl,liurl,score) values (1, 'Phil', 'Fernandez', 'pmf@mk.com', null, 'http://m.c.lnkd.licdn.com/media/p/3/000/006/059/31a002e.jpg',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,em,fburl,liurl,score) values (2, 'Jane', 'Doe', 'jdoe@mk.com', null,null,5)");
		db.execSQL("insert into leads (mktoId,fn,ln,em,fburl,liurl,score) values (3, 'Mark', 'Doe', 'jdoe@mk.com', null,null,5)");
		db.execSQL("insert into leads (mktoId,fn,ln,em,fburl,liurl,score) values (4, 'Shaun', 'Klopfenstein', 'shaun@mk.com', null, 'http://m.c.lnkd.licdn.com/media/p/6/000/1ee/17b/30e7016.jpg',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,em,fburl,liurl,score) values (5, 'Glen', 'Lipka', 'glen@mk.com', null, 'http://m.c.lnkd.licdn.com/mpr/mpr/shrink_200_200/p/4/000/182/34e/1ae3653.jpg',5)");
		db.execSQL("insert into leads (mktoId,fn,ln,em,fburl,liurl,score) values (6, 'Rajiv', 'Doe', 'jdoe@mk.com', null,null,5)");
		db.execSQL("insert into leads (mktoId,fn,ln,em,fburl,liurl,score) values (7, 'Raj', 'Rajamani', 'jdoe@mk.com', null, 'http://m.c.lnkd.licdn.com/mpr/mpr/shrink_200_200/p/2/000/03c/0e3/010f950.jpg',7)");
		db.execSQL("insert into leads (mktoId,fn,ln,em,fburl,liurl,score) values (8, 'James', 'Doe', 'jdoe@mk.com', null,null,5)");
		db.execSQL("insert into leads (mktoId,fn,ln,em,fburl,liurl,score) values (9, 'Miky', 'Doe', 'jdoe@mk.com', null,null,5)");
		
	}
}
