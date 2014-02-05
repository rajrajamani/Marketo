package com.marketo.leadexplorer.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LeadListProvider extends ContentProvider {

    private LeadListDatabase mDB;

    private static final String AUTHORITY = "com.mamlambo.tutorial.tutlist.data.TutListProvider";
//    private static final String AUTHORITY = "com.marketo.leadexplorer.data.LeadListProvider";
    public static final int LEADS = 100;
    public static final int LEAD_ID = 110;

    private static final String LEADS_BASE_PATH = "leads";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + LEADS_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/mt-tutorial";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/mt-tutorial";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    private static final String DEBUG_TAG = "Marketo";
    static {
        sURIMatcher.addURI(AUTHORITY, LEADS_BASE_PATH, LEADS);
        sURIMatcher.addURI(AUTHORITY, LEADS_BASE_PATH + "/#", LEAD_ID);
    }

    @Override
    public boolean onCreate() {
        mDB = new LeadListDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(LeadListDatabase.TABLE_LEADS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case LEAD_ID:
            queryBuilder.appendWhere(LeadListDatabase.ID + "="
                    + uri.getLastPathSegment());
            break;
        case LEADS:
            // no filter
            break;
        default:
            throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
        case LEADS:
            rowsAffected = sqlDB.delete(LeadListDatabase.TABLE_LEADS,
                    selection, selectionArgs);
            break;
        case LEAD_ID:
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsAffected = sqlDB.delete(LeadListDatabase.TABLE_LEADS,
                        LeadListDatabase.ID + "=" + id, null);
            } else {
                rowsAffected = sqlDB.delete(LeadListDatabase.TABLE_LEADS,
                        selection + " and " + LeadListDatabase.ID + "=" + id,
                        selectionArgs);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case LEADS:
            return CONTENT_TYPE;
        case LEAD_ID:
            return CONTENT_ITEM_TYPE;
        default:
            return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        if (uriType != LEADS) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        try {
            long newID = sqlDB.insertOrThrow(LeadListDatabase.TABLE_LEADS,
                    null, values);
            if (newID > 0) {
                Uri newUri = ContentUris.withAppendedId(uri, newID);
                getContext().getContentResolver().notifyChange(uri, null);
                return newUri;
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }
        } catch (SQLiteConstraintException e) {
            Log.i(DEBUG_TAG, "Ignoring constraint failure.");
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        int rowsAffected;

        switch (uriType) {
        case LEAD_ID:
            String id = uri.getLastPathSegment();
            StringBuilder modSelection = new StringBuilder(LeadListDatabase.ID
                    + "=" + id);

            if (!TextUtils.isEmpty(selection)) {
                modSelection.append(" AND " + selection);
            }

            rowsAffected = sqlDB.update(LeadListDatabase.TABLE_LEADS,
                    values, modSelection.toString(), null);
            break;
        case LEADS:
            rowsAffected = sqlDB.update(LeadListDatabase.TABLE_LEADS,
                    values, selection, selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
}
