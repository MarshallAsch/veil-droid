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
public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    private List<DhtProto.Comment> commentList;

    private Context context;

    /**
     *  This is the cell for each comment in the comment list
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView content;
        private TextView authorName;
        private TextView date;

        /**
         * constuctor for the ViewHolder Class
         * @param itemsView the XML layout for the cell. Currently called comment_item.xml
         */
        ViewHolder(View itemsView){
            super(itemsView);
            authorName = itemsView.findViewById(R.id.author_name);
            date = itemsView.findViewById(R.id.date);
            content = itemsView.findViewById(R.id.comment_content);
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

    public void update(List<DhtProto.Comment> comments) {
        this.commentList = comments;
    }

    /**
     * Creates the cell view if there is no exiting cells available for the recycler view to use
     * @param parent the list that this adapter belongs to
     * @param viewType the type of view that is needed in case there is more than one type in the list
     * @return veiwHolder
     */
    @NonNull
    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds new information to the cell of the list based on the position param
     * @param holder the UI cell item that is going to hold the data
     * @param position the position in the list of comments
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        DhtProto.Comment comment = commentList.get(position);
        String authorName = comment.getAnonymous() ? context.getString(R.string.anonymous) : comment.getAuthorName();

        holder.authorName.setText(authorName);
        holder.content.setText(comment.getMessage());

        // generate a string that describes the age of the comment, 1s, 4 min, 5 hours, etc.
        CharSequence dateString = DateUtils.getRelativeTimeSpanString(
                Util.timestampToDate(comment.getTimestamp()).getTime(),
                System.currentTimeMillis(),
                0,
                DateUtils.FORMAT_ABBREV_RELATIVE);

        holder.date.setText(dateString);
    }

    /**
     * Returns the total number of item in the data set held by this adapter
     * @return the size of the comments list
     */
    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
