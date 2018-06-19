package ca.marshallasch.veil;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.marshallasch.veil.proto.DhtProto;


/**
 *
 * This class serves as an adapter for {TODO IN SUPPORTING CLASS}.
 * This class will hold the list of comments to display to the user in the list.
 *
 * @author Weihan Li
 * @version 1.0
 * @since 2018-06-18
 */
public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    private List<DhtProto.Comment> commentList;
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView content;
        private TextView authorName;
        private TextView commentHash;
        private TextView date;

        ViewHolder(View itemsView){
            super(itemsView);
            authorName = itemsView.findViewById(R.id.author_name);
            commentHash = itemsView.findViewById(R.id.comment_hash);
            date = itemsView.findViewById(R.id.date);
            content = itemsView.findViewById(R.id.comment_content);
        }
    }

    @NonNull
    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
