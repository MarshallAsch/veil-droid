package ca.marshallasch.veil.utilities;

import com.google.protobuf.Timestamp;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import ca.marshallasch.veil.proto.DhtProto;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-13
 */
public class UtilTest
{
    DhtProto.User author = DhtProto.User.newBuilder()
            .setFirstName("Marshall")
            .setLastName("Asch")
            .setEmail("maasch@rogers.com")
            .setUuid("userID")
            .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
            .build();

    @Test
    public void timestampToMillis()
    {
        long millis = System.currentTimeMillis();

        Timestamp t = Util.millisToTimestamp(millis);

        Assert.assertEquals(millis, Util.timestampToMillis(t));
    }

    @Test
    public void timestampToDate()
    {
        Date date = new Date();
        Timestamp timestamp = Util.millisToTimestamp(date.getTime());

        Assert.assertEquals(date, Util.timestampToDate(timestamp));

    }


    @Test
    public void createComment()
    {

        DhtProto.Comment comment = Util.createComment("This is the message", author);

        Assert.assertNotNull(comment);
        Assert.assertEquals("Marshall Asch", comment.getAuthorName());
        Assert.assertEquals("userID", comment.getAuthorId());
        Assert.assertEquals("This is the message", comment.getMessage());
        Assert.assertEquals(false, comment.getAnonymous());
        Assert.assertEquals("", comment.getPostId());

        // test no post hash set
        comment = Util.createComment("This is the second message", author, true);

        Assert.assertNotNull(comment);
        Assert.assertEquals("Marshall Asch", comment.getAuthorName());
        Assert.assertEquals("userID", comment.getAuthorId());
        Assert.assertEquals("This is the second message", comment.getMessage());
        Assert.assertEquals(true, comment.getAnonymous());
        Assert.assertEquals("", comment.getPostId());

        // test set postHash
        comment = Util.createComment("This is the third message", author, "postHash");

        Assert.assertNotNull(comment);
        Assert.assertEquals("Marshall Asch", comment.getAuthorName());
        Assert.assertEquals("userID", comment.getAuthorId());
        Assert.assertEquals("This is the third message", comment.getMessage());
        Assert.assertEquals(false, comment.getAnonymous());
        Assert.assertEquals("postHash", comment.getPostId());

        // test set postHash and anonymous
        comment = Util.createComment("This is the fourth message", author, "postHash", true);

        Assert.assertNotNull(comment);
        Assert.assertEquals("Marshall Asch", comment.getAuthorName());
        Assert.assertEquals("userID", comment.getAuthorId());
        Assert.assertEquals("This is the fourth message", comment.getMessage());
        Assert.assertEquals(true, comment.getAnonymous());
        Assert.assertEquals("postHash", comment.getPostId());


        // annotations say null can not be given for the message or the author
    }
}