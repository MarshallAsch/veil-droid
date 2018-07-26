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
public class PostAuthorComparatorTest
{
    @Test
    public void compare()
    {
        // set the author for the tests
        DhtProto.User author1 = DhtProto.User.newBuilder()
                .setFirstName("aaa")
                .setLastName("Smith")
                .build();

        DhtProto.User author2 = DhtProto.User.newBuilder()
                .setFirstName("AAA")
                .setLastName("Smith")
                .build();

        DhtProto.User author3 = DhtProto.User.newBuilder()
                .setFirstName("ABC")
                .setLastName("Smith")
                .build();

        // create the 3 posts to test, 2 having an author with the same name
        DhtProto.Post a = Util.createPost("AAA title", "postMessage 1", author1, null);
        DhtProto.Post b = Util.createPost("ABC title", "postMessage 2", author2, null);
        DhtProto.Post c = Util.createPost("ABC title", "postMessage 3", author3, null);


        PostAuthorComparator comparator = new PostAuthorComparator();

        Assert.assertEquals(0, comparator.compare(a, b));
        Assert.assertEquals(0, comparator.compare(b, a));


        Assert.assertEquals(-1, comparator.compare(a, c));
        Assert.assertEquals(1, comparator.compare(c, a));

    }
}