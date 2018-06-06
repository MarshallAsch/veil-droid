package ca.marshallasch.veil;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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

    /**
     * This is the cell for each post in the list.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView contentPreview;

        /**
         * constructor for the ViewHolder class
         * @param itemsView the XML layout for the cell. Currently is a list_cell.xml
         */
        public ViewHolder(View itemsView){
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
    public PostListAdapter(String[] titles, String[] content) {
        this.titles = titles;
        this.content = content;
    }

    /**
     * Creates the cell view if there is no existing cells available for recycler view can reuse.
     * @param parent the list that it belongs to
     * @param viewType the type of view that is needed, incase there is more then one type in the list
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
