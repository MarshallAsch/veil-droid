package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

/**
 * The purpose of this table is to keep track of the keys to the data in the DHT.
 * This will keep track of the keys for posts, comments and users.
 *
 * The {@link KnownHashesEntry#COLUMN_READ} flag is only for the local device and is for posts and
 * comments, it has an undefined behaviour for users. a value of 0 means unread, 1 is read.
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-29
 */
public final class KnownHashesContract
{
    static final String SQL_CREATE_KNOWN_HASHES = "CREATE TABLE " + KnownHashesEntry.TABLE_NAME + " (" +
            KnownHashesEntry._ID + " INTEGER PRIMARY KEY," +
            KnownHashesEntry.COLUMN_HASH + " VARCHAR(36) UNIQUE," +
            KnownHashesEntry.COLUMN_ID + " VARCHAR(36)," +
            KnownHashesEntry.COLUMN_TYPE + " INTEGER," +
            KnownHashesEntry.COLUMN_READ + " INTEGER(1)," +
            KnownHashesEntry.COLUMN_TIMESTAMP + " DATETIME)";

    private KnownHashesContract() {}

    /* Inner class that defines the table contents */
    public static final class KnownHashesEntry implements BaseColumns
    {
        public static final int UNREAD = 0;
        public static final int READ = 1;

        public static final int TYPE_USER = 0;
        public static final int TYPE_POST = 1;
        public static final int TYPE_COMMENT = 2;


        public static final String TABLE_NAME = "KnownHashes";
        public static final String COLUMN_HASH = "hash";
        public static final String COLUMN_ID = "uuid";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_READ = "read";

    }
}
