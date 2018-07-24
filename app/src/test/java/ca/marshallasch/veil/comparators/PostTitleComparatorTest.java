package ca.marshallasch.veil.comparators;

import org.junit.Assert;
import org.junit.Test;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-07-23
 */
public class PostTitleComparatorTest
{
    @Test
    public void compare()
    {

        // set the author for the tests
        DhtProto.User author = DhtProto.User.newBuilder()
                .setFirstName("Bob")
                .setLastName("Smith")
                .build();


        // create the 3 posts to test, 2 having the same title
        DhtProto.Post a = Util.createPost("AAA title", "postMessage 1", author, null);
        DhtProto.Post b = Util.createPost("ABC title", "postMessage 2", author, null);
        DhtProto.Post c = Util.createPost("abc title", "postMessage 3", author, null);


        PostTitleComparator comparator = new PostTitleComparator();

        Assert.assertEquals(0, comparator.compare(b, c));
        Assert.assertEquals(0, comparator.compare(c, b));

        Assert.assertEquals(-1, comparator.compare(a, c));
        Assert.assertEquals(1, comparator.compare(c, a));

    }
}