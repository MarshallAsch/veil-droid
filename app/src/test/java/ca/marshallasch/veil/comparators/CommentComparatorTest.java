package ca.marshallasch.veil.comparators;

import org.junit.Assert;
import org.junit.Test;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-29
 */
public class CommentComparatorTest
{

    @Test
    public void compare()
    {
        long millis = System.currentTimeMillis();
        DhtProto.Comment a = DhtProto.Comment.newBuilder()
                .setTimestamp(Util.millisToTimestamp(millis))
                .build();


        DhtProto.Comment b = DhtProto.Comment.newBuilder()
                .setTimestamp(Util.millisToTimestamp(millis))
                .build();

        DhtProto.Comment c = DhtProto.Comment.newBuilder()
                .setTimestamp(Util.millisToTimestamp(millis - 10))
                .build();


        CommentComparator comparator = new CommentComparator();

        Assert.assertEquals(0, comparator.compare(a, b));
        Assert.assertEquals(-10, comparator.compare(a, c));
        Assert.assertEquals(10, comparator.compare(c, a));

    }
}