package ca.marshallasch.veil;


import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * TODO add in description
 *
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-06-22
 */
public class FragmentAddComment extends android.support.v4.app.Fragment {


    public FragmentAddComment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_comment, container, false);


        // Inflate the layout for this fragment
        return view;

    }

}
