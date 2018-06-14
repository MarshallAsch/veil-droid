package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

public class KnownPosts {

    static final String SQL_CREATE_KNOWN_HASHES = "CREATE TABLE " +  KnownPostsEntry.TABLE_NAME + "(" +
            KnownPostsEntry.COLUMN_COMMENT_HASH + "VARCHAR(36)" +
            KnownPostsEntry.COLUMN_POST_HASH + "VARCHAR(36) UNIQUE)";

    public static final class KnownPostsEntry implements BaseColumns {
        public static final String TABLE_NAME = "KnownPosts";
        public static final String COLUMN_POST_HASH = "post hash";
        public static final String COLUMN_COMMENT_HASH = "comment hash";
    }
}
