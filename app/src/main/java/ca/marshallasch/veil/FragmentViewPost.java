package ca.marshallasch.veil;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import ca.marshallasch.veil.proto.DhtProto;


/**
 * This fragment holds the UI for the expanded view of {@link PostListAdapter}'s table cell
 *
 * //TODO: Jun 8, 2018: Add in support for comments and possibly add in UI for Original Poster's profile
 * @author Weihan
 * @version 1.0
 * @since 2018-06-17
 */
public class FragmentViewPost extends Fragment {
    private DhtProto.Post postObject;
    private String postHash;

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
           try{
               postObject = DhtProto.Post.parseFrom(bundle.getByteArray(getString(R.string.post_object_key)));
           } catch (InvalidProtocolBufferException e){
               e.printStackTrace();
           }
        } else if (savedInstanceState != null){
            try{
                postObject = DhtProto.Post.parseFrom(savedInstanceState.getByteArray(getString(R.string.post_object_key)));
            } catch (InvalidProtocolBufferException e){
                e.printStackTrace();
            }
        }



        //if the post object is not null set values of post else set filler values
        String postTitle;
        String postContent;
        if(postObject != null){
            postTitle = postObject.getTitle();
            postContent = postObject.getMessage();
            postHash = postObject.getUuid();
        }
        else{
            postTitle = getString(R.string.failed_to_load_title);
            postContent = getString(R.string.failed_to_load_content);
        }


        TextView viewTitle = view.findViewById(R.id.title);
        TextView viewContent = view.findViewById(R.id.content);

        viewTitle.setText(postTitle);
        viewContent.setText(postContent);

        return view;

    }

}
