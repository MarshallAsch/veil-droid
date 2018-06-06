package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-29
 */
final class NotificationContract
{
    static final String SQL_CREATE_POST_NOTIFICATION = "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
            NotificationEntry._ID + " INTEGER PRIMARY KEY," +
            NotificationEntry.COLUMN_HASH + " VARCHAR(36) UNIQUE," +
            NotificationEntry.COLUMN_TIMESTAMP + " DATETIME)";

    private NotificationContract() {}

    /* Inner class that defines the table contents */
    public static final class NotificationEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "notifications";
        public static final String COLUMN_HASH = "" + "hash";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
