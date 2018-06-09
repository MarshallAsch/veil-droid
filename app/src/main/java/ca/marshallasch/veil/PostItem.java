package ca.marshallasch.veil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the post object of the discussion forum list
 * A Post Item Should Hold:
 * - Post title (String)
 * - Post Content (String)
 * - Comments (ArrayList of Comment Object(s))
 * - Original Poster Name (String)
 * - Original Poster Hash (String)
 * - Timestamp (String)
 *
 * @author Weihan
 * @version 1.0
 * @since 2018-06-08
 */
public class PostItem implements Serializable {
    private String title, content, opName, opHash, timeStamp;
    private ArrayList<CommentItem> commentItemList;

    /**
     * Constructor for post item
     * @param _title the title of the post
     * @param _content the content of the post
     * @param _opName the name of the original poster as a string
     * @param _opHash the hash Id of the original poster as a string
     * @param _commentItemList the comments on the post as a list of {@link CommentItem}
     * @param _timeStamp the time stamp of the post as a string
     */
    public PostItem(String _title, String _content, String _opName, String _opHash, ArrayList<CommentItem> _commentItemList, String _timeStamp){
        title = _title;
        content = _content;
        opName = _opName;
        opHash = _opHash;
        timeStamp = _timeStamp;
        commentItemList = _commentItemList;
    }

    /**
     * Gets the title of the post object
     * @return title of the post object as a string
     */
    public String getPostTitle(){
        return this.title;
    }

    /**
     * Gets the content of the post
     * @return content of post as a string
     */
    public String getPostContent(){
        return this.content;
    }

    /**
     * Gets the Poster's name as a string
     * @return posters name
     */
    public String getPosterName(){
        return this.opName;
    }

    /**
     * Gets the poster's hash Id
     * @return the poster's hash Id as a string
     */
    public String getPosterHashId(){
        return this.opHash;
    }

    /**
     * returns the timestamp in which the post was made
     * @return time stamp as a string
     */
    public String getTimeStamp(){
        return this.timeStamp;
    }

    /** Gets a list of comment items
     *
     * @return list of {@link CommentItem}s or an empty list if there is none
     */
    public List<CommentItem> getComments(){
        return this.commentItemList;
    }






}
