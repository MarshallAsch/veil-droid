package ca.marshallasch.veil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
        //retrieve passed bundle from the FragmentViewPost Class
        Bundle bundle = this.getArguments();

        TextView postTitle = view.findViewById(R.id.post_title);

        if(bundle != null){
            postTitle.setText(bundle.getString(getActivity().getString(R.string.post_title_key)));
        }
        else if(savedInstanceState != null){
            postTitle.setText(savedInstanceState.getString(getActivity().getString(R.string.post_title_key)));
        }

        // Inflate the layout for this fragment
        return view;

    }

}
