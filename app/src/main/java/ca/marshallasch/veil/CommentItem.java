package ca.marshallasch.veil;

/**
 * This class holds the comment item which {@link PostItem} will hold a list of
 * Comment Item Holds:
 * - original poster's name (String)
 * - original poster's hash ID (String)
 * - comment content (String)
 * - timestamp (String)
 *
 * @author Weihan
 * @version 1.0
 * @since 2018-06-08
 */
public class CommentItem {
    private String opName, opHash, content, timeStamp;

    public CommentItem(String _opName, String _opHash, String _content, String _timeStamp){
        opName = _opName;
        opHash = _opHash;
        content = _content;
        timeStamp = _timeStamp;
    }

    public String getPosterName(){
        return this.opName;
    }

    public String getPosterHashId(){
        return this.opHash;
    }

    public String getCommentContent(){
        return this.content;
    }

    public String getCommentTimeStamp(){
        return this.timeStamp;
    }
}
