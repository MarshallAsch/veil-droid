package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-29
 */
final class BlockContract
{
    static final String SQL_CREATE_BLOCK_USERS = "CREATE TABLE " + BlockEntry.TABLE_NAME + " (" +
            BlockEntry._ID + " INTEGER PRIMARY KEY," +
            BlockEntry.COLUMN_USER_HASH + " VARCHAR(36)," +
            BlockEntry.COLUMN_USER_ID + " VARCHAR(36) UNIQUE," +
            BlockEntry.COLUMN_TIMESTAMP + " DATETIME)";

    private BlockContract() {}

    /* Inner class that defines the table contents */
    public static final class BlockEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "blocked";
        public static final String COLUMN_USER_HASH = "user_hash";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
