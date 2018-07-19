package ca.marshallasch.veil.database;

import android.provider.BaseColumns;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-07-06
 */
public class PeerListContract
{
    static final String SQL_CREATE_PEER_LIST = "CREATE TABLE " +  PeerListEntry.TABLE_NAME +
            "(" +
            PeerListEntry._ID + " INTEGER PRIMARY KEY," +
            PeerListEntry.COLUMN_PEER_MESH_ID + " VARCHAR(43) UNIQUE," +
            PeerListEntry.COLUMN_TIME_LAST_SENT + " DATETIME" +
            ")";

    // Constructor made private to eliminate people from accidentally instantiating this contract class
    private PeerListContract(){}

    // Inner class for defining the tables contents
    public static final class PeerListEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "PeerList";
        public static final String COLUMN_PEER_MESH_ID = "peer_mesh_id";
        public static final String COLUMN_TIME_LAST_SENT = "time_last_sent_data";
    }
}
