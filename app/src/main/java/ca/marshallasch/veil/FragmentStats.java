package ca.marshallasch.veil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.marshallasch.veil.database.Database;

import static ca.marshallasch.veil.database.SyncStatsContract.SYNC_MESSAGE_V1;
import static ca.marshallasch.veil.database.SyncStatsContract.SYNC_MESSAGE_V2;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-08-03
 */
public class FragmentStats extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_sync_stats, container, false);

        TextView numPacketsV1 = view.findViewById(R.id.num_packet_v1);
        TextView totalSizeV1 = view.findViewById(R.id.total_size_v1);
        TextView averageSizeV1 = view.findViewById(R.id.average_size_v1);
        TextView totalEntriesReceivedV1 = view.findViewById(R.id.total_entries_received_v1);
        TextView totalEntriesSavedV1 = view.findViewById(R.id.total_entries_stored_v1);
        TextView numPeersV1 = view.findViewById(R.id.num_peers_v1);
        TextView slowestTimeV1 = view.findViewById(R.id.slow_time_v1);
        TextView fastestTimeV1 = view.findViewById(R.id.fast_time_v1);
        TextView averageTimeV1 = view.findViewById(R.id.avg_time_v1);
        TextView numLostV1 = view.findViewById(R.id.num_lost_v1);

        TextView numPacketsV2 = view.findViewById(R.id.num_packet_v2);
        TextView totalSizeV2 = view.findViewById(R.id.total_size_v2);
        TextView averageSizeV2 = view.findViewById(R.id.average_size_v2);
        TextView totalEntriesReceivedV2 = view.findViewById(R.id.total_entries_received_v2);
        TextView totalEntriesSavedV2 = view.findViewById(R.id.total_entries_stored_v2);
        TextView numPeersV2 = view.findViewById(R.id.num_peers_v2);
        TextView slowestTimeV2 = view.findViewById(R.id.slow_time_v2);
        TextView fastestTimeV2 = view.findViewById(R.id.fast_time_v2);
        TextView averageTimeV2 = view.findViewById(R.id.avg_time_v2);
        TextView numLostV2 = view.findViewById(R.id.num_lost_v2);


        Database db = Database.getInstance(getActivity());

        // set stats for V1 protocol
        numPacketsV1.setText(getString(R.string.num_packets, db.getNumMessages(SYNC_MESSAGE_V1)));
        totalSizeV1.setText(getString(R.string.total_size, db.getTotalMessageSize(SYNC_MESSAGE_V1)));
        averageSizeV1.setText(getString(R.string.average_size, db.getAverageMessageSize(SYNC_MESSAGE_V1)));
        totalEntriesReceivedV1.setText(getString(R.string.total_entries_received, db.getTotalEntries(SYNC_MESSAGE_V1)));
        totalEntriesSavedV1.setText(getString(R.string.total_entries_stored, db.getTotalEntries()));
        numPeersV1.setText(getString(R.string.num_peers, db.getTotalNumPeers(SYNC_MESSAGE_V1)));
        slowestTimeV1.setText(getString(R.string.slowest_time, db.getSlowestTime(SYNC_MESSAGE_V1)));
        fastestTimeV1.setText(getString(R.string.fastest_time, db.getFastestTime(SYNC_MESSAGE_V1)));
        averageTimeV1.setText(getString(R.string.average_time, db.getAverageTime(SYNC_MESSAGE_V1)));
        numLostV1.setText(getString(R.string.num_packet_lost, db.getNumLost(SYNC_MESSAGE_V1)));

        // set stats for V2 protocol
        numPacketsV2.setText(getString(R.string.num_packets, db.getNumMessages(SYNC_MESSAGE_V2)));
        totalSizeV2.setText(getString(R.string.total_size, db.getTotalMessageSize(SYNC_MESSAGE_V2)));
        averageSizeV2.setText(getString(R.string.average_size, db.getAverageMessageSize(SYNC_MESSAGE_V2)));
        totalEntriesReceivedV2.setText(getString(R.string.total_entries_received, db.getTotalEntries(SYNC_MESSAGE_V2)));
        totalEntriesSavedV2.setText(getString(R.string.total_entries_stored, db.getTotalEntries()));
        numPeersV2.setText(getString(R.string.num_peers, db.getTotalNumPeers(SYNC_MESSAGE_V2)));
        slowestTimeV2.setText(getString(R.string.slowest_time, db.getSlowestTime(SYNC_MESSAGE_V2)));
        fastestTimeV2.setText(getString(R.string.fastest_time, db.getFastestTime(SYNC_MESSAGE_V2)));
        averageTimeV2.setText(getString(R.string.average_time, db.getAverageTime(SYNC_MESSAGE_V2)));
        numLostV2.setText(getString(R.string.num_packet_lost, db.getNumLost(SYNC_MESSAGE_V2)));

        db.close();
        return view;
    }
}
