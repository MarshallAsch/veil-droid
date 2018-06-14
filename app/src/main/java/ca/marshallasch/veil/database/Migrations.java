package ca.marshallasch.veil.database;

import android.database.sqlite.SQLiteDatabase;

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
    /*static void upgradeV3(SQLiteDatabase db) {
        db.execSQL(UserContract.SQL_CREATE_USERS);
    }*/

    /**
     * In this version change the known posts table was added and the known hashes table was dropped
     * @param db the underlying database object
     */
    static void upgradeV4(SQLiteDatabase db){
        db.execSQL(KnownPostsContract.SQL_CREATE_KNOWN_HASHES);
    }
}
