package ca.marshallasch.veil;

import java.util.List;

/**
 * This class holds the post object of the discussion forum list
 * A Post Item Should Hold:
 * - Post title (String)
 * - Post Content (String)
 * - Comments (List of Comment Object(s))
 * - Original Poster Name (String)
 * - Original Poster Hash (String)
 * - Timestamp (String)
 *
 * @author Weihan
 * @version 1.0
 * @since 2018-06-08
 */
public class PostItem {
    private String title, content, opName, opHash, timeStamp;
    private List<CommentItem> commentItemList;

    //constructor
    public PostItem(String _title, String _content, String _opName, String _opHash, List<CommentItem> _commentItemList,String _timeStamp){
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
