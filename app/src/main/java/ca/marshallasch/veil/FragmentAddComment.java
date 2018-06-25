package ca.marshallasch.veil;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.marshallasch.veil.utilities.Util;


/**
 * This class holds the view and logic for posting a comment.
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
        //setting a white background for overlaying the view post fragment
        view.setBackgroundColor(Color.WHITE);
        //retrieve passed bundle from the FragmentViewPost Class
        Bundle bundle = this.getArguments();

        TextView postTitle = view.findViewById(R.id.post_title);

        if(bundle != null){
            postTitle.setText(bundle.getString(getActivity().getString(R.string.post_title_key)));
        }
        else if(savedInstanceState != null){
            postTitle.setText(savedInstanceState.getString(getActivity().getString(R.string.post_title_key)));
        }

        MaterialButton cancelBtn = view.findViewById(R.id.cancel_button);
        MaterialButton postBtn = view.findViewById(R.id.post_comment_btn);

        //Handle cancel button
        cancelBtn.setOnClickListener(view1 -> {
            Util.hideKeyboard(view1, getActivity());
            Log.i("Fragment Add Comment", "back button pressed");
            getFragmentManager().popBackStack();
        });

        //Handle post button
        postBtn.setOnClickListener(view1 -> {
            //TODO: Handle logic for posting a comment
        });

        // Inflate the layout for this fragment
        return view;

    }

}
