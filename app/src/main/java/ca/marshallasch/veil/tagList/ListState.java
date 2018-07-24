package ca.marshallasch.veil.tagList;

/**
 * This class is used to represent the items in the tag list drop down menu when marking a post
 * with tags from the list.
 *
 * This was built based off of:
 * https://stackoverflow.com/questions/38417984/android-spinner-dropdown-checkbox/38418249#38418249
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-07-24
 */
public class ListState
{
    private String title;
    private boolean checked;


    /**
     * Default constructor set the title to the empty string, and checked to false.
     */
    public ListState()
    {
        title = "";
        checked = false;
    }

    public ListState(String title, boolean checked)
    {
        this.title = title;
        this.checked = checked;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }
}
