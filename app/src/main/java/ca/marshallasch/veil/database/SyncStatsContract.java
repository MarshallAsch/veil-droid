package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-29
 */
public final class SyncStatsContract
{
    public static final int SYNC_MESSAGE_OTHER = 0;
    public static final int SYNC_MESSAGE_V1 = 1;
    public static final int SYNC_MESSAGE_V2 = 2;


    static final String SQL_CREATE_SYNC_STATS = "CREATE TABLE " + SyncStatsEntry.TABLE_NAME + " (" +
            SyncStatsEntry._ID + " INTEGER PRIMARY KEY," +
            SyncStatsEntry.COLUMN_DATA_SEND_ID + " VARCHAR UNIQUE," +
            SyncStatsEntry.COLUMN_PEER_ID + " VARCHAR," +
            SyncStatsEntry.COLUMN_PACKET_SIZE + " INTEGER," +
            SyncStatsEntry.COLUMN_NUM_RECORDS + " INTEGER," +
            SyncStatsEntry.COLUMN_TIMESTAMP_SENT + " DATETIME," +
            SyncStatsEntry.COLUMN_TIMESTAMP_RECEIVED + " DATETIME DEFAULT NULL," +
            SyncStatsEntry.COLUMN_MESSAGE_TYPE + " INTEGER DEFAULT 0" +
            ")";

    private SyncStatsContract() {}

    /* Inner class that defines the table contents */
    public static final class SyncStatsEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "stats";
        public static final String COLUMN_DATA_SEND_ID = "data_id";
        public static final String COLUMN_PEER_ID = "peer_id";
        public static final String COLUMN_PACKET_SIZE = "total_packet_size";
        public static final String COLUMN_NUM_RECORDS = "num_records_sent";
        public static final String COLUMN_TIMESTAMP_SENT = "time_sent";
        public static final String COLUMN_TIMESTAMP_RECEIVED = "time_received";
        public static final String COLUMN_MESSAGE_TYPE = "message_type";

    }
}
