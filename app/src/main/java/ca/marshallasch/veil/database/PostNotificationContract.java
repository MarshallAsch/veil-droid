package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-29
 */
final class PostNotificationContract
{
    static final String SQL_CREATE_POST_NOTIFICATION = "CREATE TABLE " + PostNotificationEntry.TABLE_NAME + " (" +
            PostNotificationEntry._ID + " INTEGER PRIMARY KEY," +
            PostNotificationEntry.COLUMN_USER_HASH + " VARCHAR(36) UNIQUE," +
            PostNotificationEntry.COLUMN_TIMESTAMP + " DATETIME)";

    private PostNotificationContract() {}

    /* Inner class that defines the table contents */
    public static final class PostNotificationEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "postNotification";
        public static final String COLUMN_USER_HASH = "user_hash";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
