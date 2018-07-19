package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

/**
 * This table is currently only used to used to keep track of the users who can login on the device
 * The user is able to be exported to transferred to a different device.
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-29
 */
final class
UserContract
{
    public static final String SQL_CREATE_USERS = "CREATE TABLE " + UserEntry.TABLE_NAME +
            "(" +
            UserEntry._ID + " INTEGER PRIMARY KEY," +
            UserEntry.COLUMN_ID + " VARCHAR(36) UNIQUE," +
            UserEntry.COLUMN_FIRST_NAME + " VARCHAR," +
            UserEntry.COLUMN_LAST_NAME + " VARCHAR," +
            UserEntry.COLUMN_EMAIL_ADDRESS + " VARCHAR," +
            UserEntry.COLUMN_PASSWORD + " VARCHAR," +
            UserEntry.COLUMN_TIMESTAMP + " DATETIME" +
            ")";

    private  UserContract() {}

    /* Inner class that defines the table contents */
    public static class UserEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_ID = "id";

        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_EMAIL_ADDRESS = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_TIMESTAMP = "timestamp";


    }
}
