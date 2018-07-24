package ca.marshallasch.veil.tagList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import ca.marshallasch.veil.R;

/**
 * This class is the the adapter for the list of tags
 *
 * This was built based off of:
 * https://stackoverflow.com/questions/38417984/android-spinner-dropdown-checkbox/38418249#38418249
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-07-24
 */
public class TagListAdapter extends ArrayAdapter<ListState>
{
    private Context context;
    private ArrayList<ListState> tagList;
    private boolean isFromView = false;

    /**
     * This class will hold the view view for the cell.
     */
    private class ViewHolder {
        private TextView textView;
        private CheckBox checkBox;
    }

    /**
     * Constructor with the default list of tags.
     * @param context the application context
     */
    public TagListAdapter(@NonNull Context context)
    {
        super(context, 0);
        this.context = context;

        final String[] tagOptions = {
                "Select Tags", "Health", "Emergency", "Food", "Dogs",
                "Cats", "Tech"};

        tagList = new ArrayList<>();

        for (int i = 0; i < tagOptions.length; i++) {
            ListState item = new ListState();
            item.setTitle(tagOptions[i]);
            item.setChecked(false);
            tagList.add(item);
        }
    }

    /**
     * Pass in the list of tags to use
     * @param context the application context
     * @param objects the list of tags
     */
    public TagListAdapter(@NonNull Context context, @NonNull ArrayList<ListState> objects)
    {
        super(context, 0, objects);
        this.context = context;
        this.tagList = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    /**
     * This will create the view to be used for each cell. It is called by both
     * {@link #getView(int, View, ViewGroup)} and {@link #getDropDownView(int, View, ViewGroup)}
     *
     * @param position the position in the list that the view is needed for
     * @param convertView the possible old view to reuse
     * @param parent the view group
     * @return the view to display.
     */
    private View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        ListState data = tagList.get(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tag_list_item, null);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.tag_name);
            holder.checkBox = convertView.findViewById(R.id.tag_selected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.textView.setText(data.getTitle());
        isFromView  = true;
        holder.checkBox.setChecked(data.isChecked());
        isFromView = false;

        // hide the checkbox if it is the first option in the list (the title)
        if ((position == 0)) {
            holder.checkBox.setVisibility(View.INVISIBLE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
        }

        // set the tag so so that the click changed listener can know what position in the list its for
        holder.checkBox.setTag(position);

        // handle the event when the checkbox is checked / unchecked
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int getPosition = (Integer) buttonView.getTag();

            if (!isFromView) {
                tagList.get(getPosition).setChecked(isChecked);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    /**
     * Get a list of all the currently selected tags
     * @return an ArrayList of tags
     */
    public ArrayList<String> getSelectedTags() {

        ArrayList<String> tags = new ArrayList<>();

        // check which of the tags are selected
        for (ListState item: tagList) {
            if (item.isChecked()) {
                tags.add(item.getTitle());
            }
        }

        return tags;
    }
}
