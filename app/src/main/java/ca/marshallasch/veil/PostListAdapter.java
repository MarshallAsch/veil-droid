package ca.marshallasch.veil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.marshallasch.veil.comparators.PostAgeComparator;
import ca.marshallasch.veil.comparators.PostAuthorComparator;
import ca.marshallasch.veil.comparators.PostTitleComparator;
import ca.marshallasch.veil.database.KnownPostsContract;
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
public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable{

    private List<DhtProto.Post> posts;
    private List<DhtProto.Post> postsFiltered;

    private final Activity activity;

    private CharSequence lastFilter = "";

    /**
     * Sort options.
     */
    public enum SortOption {
        ALPHA_TITLE_ASC,
        ALPHA_TITLE_DESC,
        ALPHA_AUTH_ASC,
        ALPHA_AUTH_DESC,
        AGE_ASC,
        AGE_DESC
    }

    /**
     * This is the cell for if there are no posts in the list
     */
     private static class ViewHolder0 extends RecyclerView.ViewHolder {

        /**
         * constructor for the ViewHolder class
         * @param itemsView the XML layout for the cell. Currently is a post_list_cell.xml
         */
        ViewHolder0(View itemsView){
            super(itemsView);
        }
    }

    /**
     * This is the cell for each post in the list.
     */
    private static class ViewHolder1 extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView contentPreview;
        private final TextView commentCount;
        private final TextView authorName;
        private final TextView timeStamp;
        private final ImageView readMarker;
        private final ImageView protectedMarker;

        /**
         * constructor for the ViewHolder class
         * @param itemsView the XML layout for the cell. Currently is a post_list_cell.xml
         */
        ViewHolder1(View itemsView){
            super(itemsView);
            title = itemsView.findViewById(R.id.title);
            contentPreview = itemsView.findViewById(R.id.content_preview);
            commentCount = itemsView.findViewById(R.id.comments);
            authorName = itemsView.findViewById(R.id.author_name);
            timeStamp = itemsView.findViewById(R.id.time_stamp);
            readMarker = itemsView.findViewById(R.id.unread_marker);
            protectedMarker = itemsView.findViewById(R.id.protected_image);
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
        this.postsFiltered = posts;
    }


    /**
     * This will refresh the posts that are in the list. This will also re-filter the results.
     * It will eventually call {@link #notifyDataSetChanged()} so must be called from the UI thread.
     * @param posts the new list of posts to display.
     */
    @UiThread
    public void update(List<DhtProto.Post> posts) {
        this.posts = posts;
        getFilter().filter(lastFilter);
    }

    /**
     * This will check what type of view to generate depending on the number of items in the list.
     * @param position the position of the cell to check the type for
     * @return 0 for the no post type, 1 otherwise
     */
    @Override
    public int getItemViewType(int position)
    {
        return postsFiltered.size() == 0 ? 0 : 1;
    }

    /**
     * Creates the cell view if there is no existing cells available for recycler view can reuse.
     * @param parent the list that it belongs to
     * @param viewType the type of view that is needed, in case there is more then one type in the list
     * @return viewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_posts_cell, parent, false);

            return  new ViewHolder0(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_cell, parent, false);
            return  new ViewHolder1(view);
        }
    }

    /**
     * Binds new information to the cell of the list based on position.
     * @param holder the UI cell item that is going to hold the data
     * @param position the position in the list that the cell is for.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == 0) {
            return;
        }

        ViewHolder1 viewHolder = (ViewHolder1) holder;

        // Going to need to trim the content before it gets added here.
        DhtProto.Post post = postsFiltered.get(position);

        int numComments = DataStore.getInstance(activity).getNumCommentsFor(post.getUuid());
        boolean read = DataStore.getInstance(activity).isRead(post.getUuid());
        String authorName = post.getAnonymous() ? activity.getString(R.string.anonymous) : post.getAuthorName();

        viewHolder.title.setText(post.getTitle());
        viewHolder.contentPreview.setText(post.getMessage());
        viewHolder.commentCount.setText(activity.getString(R.string.num_comments, numComments));
        viewHolder.authorName.setText(authorName);

        //set or unset the protected marker
        updatePostStatus(viewHolder, post);


        // show or hide the read marker
        viewHolder.readMarker.setVisibility(read ? View.INVISIBLE : View.VISIBLE);

        // generate a string that describes the age of the post, 1s, 4 min, 5 hours, etc.
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

        viewHolder.itemView.findViewById(R.id.protect_button).setOnClickListener(view -> {
            int postStatus = DataStore.getInstance(activity).getPostStatus(post.getUuid());

            if(postStatus == KnownPostsContract.POST_PROTECTED){
                DataStore.getInstance(activity).setPostStatus(post.getUuid(), KnownPostsContract.POST_NORMAL);
                updatePostStatus(viewHolder, post);
            }
            else if(postStatus == KnownPostsContract.POST_NORMAL){
                DataStore.getInstance(activity).setPostStatus(post.getUuid(), KnownPostsContract.POST_PROTECTED);
                updatePostStatus(viewHolder, post);
            }

        });
    }

    /**
     * This function will sort the list of posts and will update the UI. Because this will update
     * the UI it <b>MUST</b> be run on the UI thread
     * @param option the way that the list should be sorted
     */
    @UiThread
    public void sort(SortOption option) {

        switch (option) {
            case ALPHA_TITLE_ASC:
                Collections.sort(postsFiltered, new PostTitleComparator());
                break;
            case ALPHA_TITLE_DESC:
                Collections.sort(postsFiltered, new PostTitleComparator());
                Collections.reverse(postsFiltered);
                break;
            case ALPHA_AUTH_ASC:
                Collections.sort(postsFiltered, new PostAuthorComparator());
                break;
            case ALPHA_AUTH_DESC:
                Collections.sort(postsFiltered, new PostAuthorComparator());
                Collections.reverse(postsFiltered);
                break;
            case AGE_ASC:
                Collections.sort(postsFiltered, new PostAgeComparator());
                break;
            case AGE_DESC:
                Collections.sort(postsFiltered, new PostAgeComparator());
                Collections.reverse(postsFiltered);
                break;
            default:
        }

        notifyDataSetChanged();
    }

    /**
     * Returns the total number of items in the data set held by this adapter.
     * @return posts.size()
     */
    @Override
    public int getItemCount() {
        return postsFiltered.size() == 0 ? 1 : postsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        /*
         * This class is the tag filter. The CharSequence must be a list of tags that are
         * denominated with a ':' character.
         */
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                lastFilter = charSequence;
                List<DhtProto.Post> filteredList = new ArrayList<>();

                if (charSequence.length() == 0) {
                    filteredList = posts;
                } else {
                    String[] tokens = charSequence.toString().split(":");

                    List<String> tagList = new ArrayList<>();

                    for (String tag: tokens){
                        tagList.add(tag.trim());
                    }

                    // Check if post contains all specified tags
                    for (DhtProto.Post post: posts) {
                        if (post.getTagsList().containsAll(tagList)) {
                            filteredList.add(post);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                postsFiltered = (ArrayList<DhtProto.Post>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void updatePostStatus(ViewHolder1 viewHolder, DhtProto.Post post){
        int postStatus = DataStore.getInstance(activity).getPostStatus(post.getUuid());

        if(postStatus == KnownPostsContract.POST_NORMAL){
            viewHolder.protectedMarker.setImageResource(R.drawable.ic_unprotected);
        } else if(postStatus == KnownPostsContract.POST_PROTECTED){
            viewHolder.protectedMarker.setImageResource(R.drawable.ic_protected);
        }
    }
}
