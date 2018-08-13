package ca.marshallasch.veil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-08-13
 */
public class FragmentDeletion extends Fragment
{
    public FragmentDeletion()
    {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_data_clear, container, false);

        MaterialButton clearEntries = view.findViewById(R.id.clear_entries);
        MaterialButton clearSyncStats = view.findViewById(R.id.clear_logs);
        MaterialButton clearPeers = view.findViewById(R.id.clear_peers);


        clearEntries.setOnClickListener(view1 -> DataStore.getInstance(getActivity()).clearEntries());
        clearSyncStats.setOnClickListener(view1 -> DataStore.getInstance(getActivity()).clearSyncStats());
        clearPeers.setOnClickListener(view1 -> DataStore.getInstance(getActivity()).clearPeers());


        return view;
    }
}
