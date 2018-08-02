package ca.marshallasch.veil;

import android.content.Context;
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
 *
 * This class serves as an adapter for {@link FragmentViewPost}.
 * This class will hold the list of comments to display to the user in the list.
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-06-18
 */
public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DhtProto.Comment> commentList;

    private final Context context;

    /**
     *  This is the cell for each comment in the comment list
     */
    private static class ViewHolder1 extends RecyclerView.ViewHolder {
        private final TextView content;
        private final TextView authorName;
        private final TextView date;

        /**
         * constructor for the ViewHolder Class
         * @param itemsView the XML layout for the cell. Currently called comment_item.xml
         */
        ViewHolder1(View itemsView){
            super(itemsView);
            authorName = itemsView.findViewById(R.id.author_name);
            date = itemsView.findViewById(R.id.date);
            content = itemsView.findViewById(R.id.comment_content);
        }
    }

    /**
     * This is the cell for when there are no comments in the list.
     */
    private static class ViewHolder0 extends RecyclerView.ViewHolder {

        /**
         * constructor for the ViewHolder Class
         * @param itemsView the XML layout for the cell. Currently called comment_item.xml
         */
        ViewHolder0(View itemsView){
            super(itemsView);
        }
    }

    /**
     * Constructor for this current class. Will set the list content for the cells.
     *
     * @param comments the list of comments to display
     */
    public CommentListAdapter(Context context, List<DhtProto.Comment> comments){
        this.commentList = comments;
        this.context = context;
    }

    /**
     * This will refresh the comments that are in the list.
     * @param comments the new list of comments to display.
     */
    public void update(List<DhtProto.Comment> comments) {
        this.commentList = comments;
    }

    /**
     * This will check what type of view to generate depending on the number of items in the list.
     * @param position the position of the cell to check the type for
     * @return 0 for the no comment type, 1 otherwise
     */
    @Override
    public int getItemViewType(int position)
    {
        return commentList.size() == 0 ? 0 : 1;
    }

    /**
     * Creates the cell view if there is no exiting cells available for the recycler view to use
     * @param parent the list that this adapter belongs to
     * @param viewType the type of view that is needed ether the normal comment view or the no comment message
     * @return the viewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_comment_cell, parent, false);
            return new ViewHolder0(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
            return new ViewHolder1(view);
        }
    }

    /**
     * Binds new information to the cell of the list based on the position param
     * @param holder the UI cell item that is going to hold the data
     * @param position the position in the list of comments
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == 0) {
            return;
        }

        ViewHolder1 viewHolder = (ViewHolder1) holder;

        DhtProto.Comment comment = commentList.get(position);
        String authorName = comment.getAnonymous() ? context.getString(R.string.anonymous) : comment.getAuthorName();

        viewHolder.authorName.setText(authorName);
        viewHolder.content.setText(comment.getMessage());

        // generate a string that describes the age of the comment, 1s, 4 min, 5 hours, etc.
        CharSequence dateString = DateUtils.getRelativeTimeSpanString(
                Util.timestampToDate(comment.getTimestamp()).getTime(),
                System.currentTimeMillis(),
                0,
                DateUtils.FORMAT_ABBREV_RELATIVE);

        viewHolder.date.setText(dateString);
    }

    /**
     * Returns the total number of item in the data set held by this adapter
     * @return the size of the comments list
     */
    @Override
    public int getItemCount() {
        return commentList.size() == 0 ? 1 : commentList.size();
    }
}
