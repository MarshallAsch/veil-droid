package ca.marshallasch.veil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;


/**
 * This class serves as an adapter for {@link FragmentDiscoverForums}. This class will hold the list
 * of posts to display to the user in the list.
 *
 * @author  Weihan Li
 * @version 1.0
 * @since 2018-06-04
 */
public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    private List<DhtProto.Post> posts;
    private Activity activity;

    /**
     * This is the cell for each post in the list.
     */
     static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView contentPreview;
        private TextView commentCount;
        private TextView authorName;
        private TextView timeStamp;

        /**
         * constructor for the ViewHolder class
         * @param itemsView the XML layout for the cell. Currently is a post_list_cell.xmll.xml
         */
        ViewHolder(View itemsView){
            super(itemsView);
            title = itemsView.findViewById(R.id.title);
            contentPreview = itemsView.findViewById(R.id.content_preview);
            commentCount = itemsView.findViewById(R.id.comments);
            authorName = itemsView.findViewById(R.id.author_name);
            timeStamp = itemsView.findViewById(R.id.time_stamp);
        }
    }

    /**
     * Constructor for this current class. Will set the list content for the cells.
     *
     * @param posts The list of posts to display
     */
    public PostListAdapter(List<DhtProto.Post> posts, Activity activity) {
        this.activity = activity;
        this.posts = posts;
    }


    /**
     * This will refresh the posts that are in the list.
     * @param posts the new list of posts to display.
     */
    public void update(List<DhtProto.Post> posts) {
        this.posts = posts;
    }

    /**
     * Creates the cell view if there is no existing cells available for recycler view can reuse.
     * @param parent the list that it belongs to
     * @param viewType the type of view that is needed, in case there is more then one type in the list
     * @return viewHolder
     */
    @NonNull
    @Override
    public PostListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_cell, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Binds new information to the cell of the list based on position.
     * @param viewHolder the UI cell item that is going to hold the data
     * @param position the position in the list that the cell is for.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        // Going to need to trim the content before it gets added here.
        DhtProto.Post post = posts.get(position);

        int numComments = DataStore.getInstance(activity).getNumCommentsFor(post.getUuid());

        viewHolder.title.setText(post.getTitle());
        viewHolder.contentPreview.setText(post.getMessage());
        viewHolder.commentCount.setText(activity.getString(R.string.num_comments, numComments));
        viewHolder.authorName.setText(post.getAuthorName());

        CharSequence dateString = DateUtils.getRelativeTimeSpanString(
                Util.timestampToDate(post.getTimestamp()).getTime(),
                System.currentTimeMillis(),
                0,
                DateUtils.FORMAT_ABBREV_RELATIVE);

        viewHolder.timeStamp.setText(dateString);

        viewHolder.itemView.findViewById(R.id.view_btn).setOnClickListener(view -> {
            FragmentViewPost fragViewPost = new FragmentViewPost();
            //create a bundle to pass data to the next fragment for viewing
            Bundle bundle = new Bundle();

            bundle.putByteArray(activity.getString(R.string.post_object_key), post.toByteArray());

            fragViewPost.setArguments(bundle);
            ((MainActivity) activity).navigateTo(fragViewPost, true);
        });
    }

    /**
     * Returns the total number of items in the data set held by this adapter.
     * @return posts.size()
     */
    @Override
    public int getItemCount() {
        return posts.size();
    }
}
