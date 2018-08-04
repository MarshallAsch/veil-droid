package ca.marshallasch.veil.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used to contain all of the database migration functions.
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-06
 */
final class Migrations
{
    /**
     * In this version change the users table was added.
     * @param db the underlying database object
     */
    static void upgradeV3(SQLiteDatabase db) {
        db.execSQL(UserContract.SQL_CREATE_USERS);
    }

    /**
     * In this version change the known posts table was added and the known hashes table was dropped
     * @param db the underlying database object
     */
    static void upgradeV4(SQLiteDatabase db){
        db.execSQL(KnownPostsContract.SQL_CREATE_KNOWN_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + KnownPostsContract.KnownPostsEntry.TABLE_NAME);
    }

    static void upgradeV6(SQLiteDatabase db){

        // need to get the old values from the table
        List<Pair<String, String>> knownPosts = new ArrayList<>();


        String[] projection = {
                KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH,
                KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH
        };


        Cursor cursor = db.query(
                KnownPostsContract.KnownPostsEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                null          // don't sort
        );

        String postHash;
        String commentHash;

        // get each post hash that is in the list
        while(cursor.moveToNext()) {
            postHash = cursor.getString(cursor.getColumnIndexOrThrow(KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH));
            commentHash = cursor.getString(cursor.getColumnIndexOrThrow(KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH));

            // add the hash to the list
            knownPosts.add(new Pair<>(postHash, commentHash));
        }
        cursor.close();

        db.execSQL("DROP TABLE IF EXISTS " + KnownPostsContract.KnownPostsEntry.TABLE_NAME);
        db.execSQL(KnownPostsContract.SQL_CREATE_KNOWN_POSTS);


        // insert everything back into the table
        for (Pair<String, String> hash: knownPosts) {

            ContentValues values = new ContentValues();

            values.put(KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH, hash.first);
            values.put(KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH, hash.second);

            // note this is a potentially long running operation.
             db.insert(KnownPostsContract.KnownPostsEntry.TABLE_NAME, null, values);

        }
    }

    /**
     * The table for knownPosts was renamed and an extra column was added.
     * @param db the database object
     */
    static void upgradeV7(SQLiteDatabase db) {

        List<Pair<String, String>> knownPosts = new ArrayList<>();


        String[] projection = {
                KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH,
                KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH
        };


        Cursor cursor = db.query(
                "KnownPostsContract",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                null          // don't sort
        );

        String postHash;
        String commentHash;

        // get each post hash that is in the list
        while(cursor.moveToNext()) {
            postHash = cursor.getString(cursor.getColumnIndexOrThrow(KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH));
            commentHash = cursor.getString(cursor.getColumnIndexOrThrow(KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH));

            // add the hash to the list
            knownPosts.add(new Pair<>(postHash, commentHash));
        }
        cursor.close();

        db.execSQL("DROP TABLE IF EXISTS KnownPostsContract");
        db.execSQL(KnownPostsContract.SQL_CREATE_KNOWN_POSTS);
        db.execSQL(PeerListContract.SQL_CREATE_PEER_LIST);


        Date date = new Date();
        // insert everything back into the table
        for (Pair<String, String> hash: knownPosts) {

            ContentValues values = new ContentValues();

            values.put(KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH, hash.first);
            values.put(KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH, hash.second);
            values.put(KnownPostsContract.KnownPostsEntry.COLUMN_TIME_INSERTED, date.getTime());

            // note this is a potentially long running operation.
            db.insert(KnownPostsContract.KnownPostsEntry.TABLE_NAME, null, values);

        }
    }

    static void upgradeV8(SQLiteDatabase db) {

        String[] projection = {
                KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH,
                KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH,
                KnownPostsContract.KnownPostsEntry.COLUMN_TIME_INSERTED
        };

        Cursor cursor = db.query(
                KnownPostsContract.KnownPostsEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                null          // don't sort
        );

        List<ContentValues> knownPosts = new ArrayList<>();

        String postHash;
        String commentHash;
        long millis;

        // get each post hash that is in the list
        while(cursor.moveToNext()) {
            postHash = cursor.getString(cursor.getColumnIndexOrThrow(KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH));
            commentHash = cursor.getString(cursor.getColumnIndexOrThrow(KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH));
            millis = cursor.getLong(cursor.getColumnIndexOrThrow(KnownPostsContract.KnownPostsEntry.COLUMN_TIME_INSERTED));

            ContentValues contentValues = new ContentValues();
            contentValues.put(KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH, postHash);
            contentValues.put(KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH, commentHash);
            contentValues.put(KnownPostsContract.KnownPostsEntry.COLUMN_READ, 0);
            contentValues.put(KnownPostsContract.KnownPostsEntry.COLUMN_TIME_INSERTED, millis);

            // add the hash to the list
            knownPosts.add(contentValues);
        }
        cursor.close();

        db.execSQL("DROP TABLE IF EXISTS " + KnownPostsContract.KnownPostsEntry.TABLE_NAME);
        db.execSQL(KnownPostsContract.SQL_CREATE_KNOWN_POSTS);

        // insert everything back into the table
        for (ContentValues values: knownPosts) {

            // note this is a potentially long running operation.
            db.insert(KnownPostsContract.KnownPostsEntry.TABLE_NAME, null, values);

        }
    }

    static void upgradeV9(SQLiteDatabase db) {
        db.execSQL(SyncStatsContract.SQL_CREATE_SYNC_STATS);
    }

    static  void upgradeV10(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + SyncStatsContract.SyncStatsEntry.TABLE_NAME);
        db.execSQL(SyncStatsContract.SQL_CREATE_SYNC_STATS);
    }
}
