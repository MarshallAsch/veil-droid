package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-29
 */
final class UserContract
{
    private static final String SQL_CREATE_USERS = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
            UserEntry._ID + " INTEGER PRIMARY KEY," +
            UserEntry.COLUMN_NAME_ID + " VARCHAR(36)," +
            UserEntry.COLUMN_NAME_FIRST_NAME + " TEXT)";

    private  UserContract() {}

    /* Inner class that defines the table contents */
    public static class UserEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_ID = "id";

        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_EMAIL_ADDRESS = "email";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_num";
        public static final String COLUMN_NAME_PASSWORD = "password";

    }
}
