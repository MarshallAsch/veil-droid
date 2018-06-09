package ca.marshallasch.veil;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



/**
 * This fragment holds the UI for the expanded view of {@link PostListAdapter}'s table cell
 *
 * //TODO: Jun 8, 2018: Add in support for comments and possibly add in UI for Original Poster's profile
 * @author Weihan
 * @version 1.0
 * @since 2018-06-17
 */
public class FragmentViewPost extends Fragment {
    private String postTitle, postContent;
    private PostItem postObject;

    public FragmentViewPost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);

        //retrieve passed bundle from the PostListAdapter Class
        Bundle bundle = this.getArguments();

        //if the bundle is not null then use the data from there else use the data from the saved instance
        if(bundle != null) {
            postObject = (PostItem) bundle.getSerializable(String.valueOf(R.string.post_object_key));
        } else if (savedInstanceState != null){
            postObject = (PostItem) savedInstanceState.getSerializable(String.valueOf(R.string.post_object_key));
        }

        //if the post object is not null set values of post else set filler values
        if(postObject != null){
            postTitle = postObject.getPostTitle();
            postContent = postObject.getPostContent();
        }
        else{
            postTitle = String.valueOf(R.string.failed_to_load_title);
            postContent = String.valueOf(R.string.failed_to_load_content);
        }


        TextView viewTitle = view.findViewById(R.id.title);
        TextView viewContent = view.findViewById(R.id.content);

        viewTitle.setText(postTitle);
        viewContent.setText(postContent);

        return view;

    }

}
