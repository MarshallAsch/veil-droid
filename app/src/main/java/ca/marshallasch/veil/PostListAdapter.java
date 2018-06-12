package ca.marshallasch.veil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.marshallasch.veil.proto.DhtProto;


/**
 * This class serves as an adapter for {@link FragmentDiscoverForums}. This class will hold the list
 * of posts to display to the user in the list.
 *
 * @author  Weihan Li
 * @version 1.0
 * @since 2018-06-04
 */
public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    private String[] titles;
    private String[] content;
    private Activity activity;

    /**
     * This is the cell for each post in the list.
     */
     static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView contentPreview;

        /**
         * constructor for the ViewHolder class
         * @param itemsView the XML layout for the cell. Currently is a list_cell.xml
         */
        ViewHolder(View itemsView){
            super(itemsView);
            title = itemsView.findViewById(R.id.title);
            contentPreview = itemsView.findViewById(R.id.content_preview);

        }
    }

    /**
     * Constructor for this current class. Will set the list content for the cells.
     *
     * @param titles The list of post titles
     * @param content The content of each post.
     */
    public PostListAdapter(String[] titles, String[] content, Activity activity) {
        this.titles = titles;
        this.content = content;
        this.activity = activity;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell, parent, false);

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
        viewHolder.title.setText(titles[position]);
        viewHolder.contentPreview.setText(content[position]);

        viewHolder.itemView.findViewById(R.id.view_btn).setOnClickListener(view -> {
            FragmentViewPost fragViewPost = new FragmentViewPost();
            //create a bundle to pass data to the next fragment for viewing
            Bundle bundle = new Bundle();
            //TODO: Make a real list of Comments
            //TODO: take out fillers for the constructor of post Item
            DhtProto.Post post = DhtProto.Post.newBuilder()
                    .setTitle(titles[position])
                    .setMessage(content[position])
                    .build();

            bundle.putByteArray(String.valueOf(R.string.post_object_key), post.toByteArray());


            fragViewPost.setArguments(bundle);
            ((MainActivity) activity).navigateTo(fragViewPost, true);
        });
    }

    /**
     * Returns the total number of items in the data set held by this adapter.
     * @return titles.length
     */
    @Override
    public int getItemCount() {
        return titles.length;
    }
}
