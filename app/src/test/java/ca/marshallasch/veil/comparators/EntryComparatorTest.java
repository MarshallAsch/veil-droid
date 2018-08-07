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
public class EntryComparatorTest
{

    private final DhtProto.User author = DhtProto.User.newBuilder()
            .setFirstName("Marshall")
            .setLastName("Asch")
            .setEmail("maasch@rogers.com")
            .setUuid("userID")
            .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
            .build();

    @Test
    public void entryEquals()
    {
        DhtProto.Post postA = Util.createPost("Awesome Post", "Message of the post", author, null);
        DhtProto.Post postB = Util.createPost("Second non match", "Message of the post", author, null);
        DhtProto.Post postC = DhtProto.Post.newBuilder(postA).build();

        DhtProto.Comment commentA = Util.createComment("Good comment", author, "POSTHASH");

        DhtProto.Comment commentB = Util.createComment("Second comment", author, "POSTHASH");


        DhtProto.DhtWrapper wrapperA = DhtProto.DhtWrapper.newBuilder()
                .setType(DhtProto.MessageType.POST)
                .setPost(postA)
                .build();

        DhtProto.DhtWrapper wrapperB = DhtProto.DhtWrapper.newBuilder()
                .setType(DhtProto.MessageType.POST)
                .setPost(postB)
                .build();

        DhtProto.DhtWrapper wrapperC = DhtProto.DhtWrapper.newBuilder()
                .setType(DhtProto.MessageType.POST)
                .setPost(postC)
                .build();

        DhtProto.DhtWrapper wrapperD = DhtProto.DhtWrapper.newBuilder()
                .setType(DhtProto.MessageType.COMMENT)
                .setComment(commentA)
                .build();

        DhtProto.DhtWrapper wrapperE = DhtProto.DhtWrapper.newBuilder()
                .setType(DhtProto.MessageType.COMMENT)
                .setComment(commentB)
                .build();

        Assert.assertTrue(EntryComparator.entryEquals(null, null));
        Assert.assertFalse(EntryComparator.entryEquals(wrapperA, null));
        Assert.assertFalse(EntryComparator.entryEquals(wrapperA, wrapperB));
        Assert.assertTrue(EntryComparator.entryEquals(wrapperA, wrapperC));


        // compare a comment to a post
        Assert.assertFalse(EntryComparator.entryEquals(wrapperA, wrapperD));
        Assert.assertFalse(EntryComparator.entryEquals(wrapperE, wrapperD));




    }
}