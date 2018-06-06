package ca.marshallasch.veil;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * This class serves as an adapter for {@link FragmentDiscoverForums}.
 *
 * @author  Weihan Li
 * @version 1.0
 * @since 2018-06-04
 */
public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {
    private String[] titles;
    private String[] content;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView contentPreview;

        /**
         * constructor for the ViewHolder class
         * @param itemsView
         */
        public ViewHolder(View itemsView){
            super(itemsView);
            title = itemsView.findViewById(R.id.title);
            contentPreview = itemsView.findViewById(R.id.content_preview);

        }
    }

    /**
     * Constructor for this current class
     * @param titles
     * @param content
     */

    public PostListAdapter(String[] titles, String[] content) {
        this.titles = titles;
        this.content = content;
    }


    /**
     * Creates the view holder if there is no existing view holders for recycler view can reuse.
     * @param parent
     * @param viewType
     * @return viewHolder
     */

    @NonNull
    @Override
    public PostListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    /**
     * Binds new information to the cell of the list based on position.
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
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
