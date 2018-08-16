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
public class PostAgeComparatorTest
{

    @Test
    public void compare()
    {
        long millis = System.currentTimeMillis();
        DhtProto.Post a = DhtProto.Post.newBuilder()
                .setTimestamp(Util.millisToTimestamp(millis))
                .build();


        DhtProto.Post b = DhtProto.Post.newBuilder()
                .setTimestamp(Util.millisToTimestamp(millis))
                .build();

        DhtProto.Post c = DhtProto.Post.newBuilder()
                .setTimestamp(Util.millisToTimestamp(millis - 10))
                .build();


        PostAgeComparator comparator = new PostAgeComparator();

        Assert.assertEquals(0, comparator.compare(a, b));
        Assert.assertEquals(-10, comparator.compare(a, c));
        Assert.assertEquals(10, comparator.compare(c, a));

    }
}