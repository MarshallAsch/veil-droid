package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

/**
 * This class holds the db query for creating the known posts table. The known posts
 * table holds all the hashes of the known posts along with its associated comments.
 * 
 * @author Weihan
 * @since 2018-06-14
 * @version 1.0
 */
public final class KnownPostsContract {

    // DB query string
    static final String SQL_CREATE_KNOWN_POSTS = "CREATE TABLE " +  KnownPostsEntry.TABLE_NAME + "(" +
            KnownPostsEntry._ID + " INTERGER PRIMARY KEY," +
            KnownPostsEntry.COLUMN_COMMENT_HASH + " VARCHAR(36) UNIQUE," +
            KnownPostsEntry.COLUMN_POST_HASH + " VARCHAR(36))";

    // Constructor made private to eliminate people from accidentally instantiating this contract class
    private KnownPostsContract(){}

    // Inner class for defining the tables contents
    public static final class KnownPostsEntry implements BaseColumns {
        public static final String TABLE_NAME = "KnownPostsContract";
        public static final String COLUMN_POST_HASH = "post_hash";
        public static final String COLUMN_COMMENT_HASH = "comment_hash";
    }
}
