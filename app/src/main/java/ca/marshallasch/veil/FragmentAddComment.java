package ca.marshallasch.veil;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 *
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-06-22
 */
public class FragmentAddComment extends Fragment {


    public FragmentAddComment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_comment, container, false);
    }

}
