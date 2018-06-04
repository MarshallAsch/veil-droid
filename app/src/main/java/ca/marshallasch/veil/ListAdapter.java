package ca.marshallasch.veil;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private String[] titles;
    private String[] content;
    private Activity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView contentPreview;
        public ViewHolder(View itemsView){
            super(itemsView);
            title = itemsView.findViewById(R.id.title);
            contentPreview = itemsView.findViewById(R.id.content_preview);

        }
    }


    public ListAdapter(String[] titles, String[] content, Activity activity) {
        this.titles = titles;
        this.content = content;
        this.activity = activity;
    }



    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.title.setText(titles[position]);
        viewHolder.contentPreview.setText(content[position]);
    }

    @Override
    /**
     * Description:
     * Returns the size of the data set. Called by layout manager.
     */
    public int getItemCount() {
        return titles.length;
    }


}
