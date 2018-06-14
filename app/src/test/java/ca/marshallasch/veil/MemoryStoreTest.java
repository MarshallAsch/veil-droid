package ca.marshallasch.veil;

import android.support.v4.util.Pair;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.marshallasch.veil.exceptions.TooManyResultsException;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-12
 */
public class MemoryStoreTest
{
    @Test
    public void getInstance()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);

        // make sure that got a value
        Assert.assertNotNull(memoryStore);

        // make sure that they are the same instance
        Assert.assertEquals(memoryStore, MemoryStore.getInstance(null));
    }

    @Test
    public void insertPost()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .build();

        // if the post is null then the hash should be as well
        Assert.assertNull(memoryStore.insertPost(null));

        // check that the empty post works
        String hash = memoryStore.insertPost(p);
        Assert.assertNotNull(hash);
        DhtProto.Post p2 = memoryStore.hashMap.get(hash).get(0).getPost();
        Assert.assertEquals(p, p2);


        p = DhtProto.Post.newBuilder()
                .setTitle("Post title 1")
                .build();

        // check that the post works
        hash = memoryStore.insertPost(p);
        Assert.assertNotNull(hash);

        // check that the keyword item for the post got inserted
        String hash2 = memoryStore.hashMap.get(Util.generateHash("post".getBytes())).get(0).getKeyword().getHash();
        Assert.assertEquals(hash, hash2);
    }

    @Test
    public void findPostByHash()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post2")
                .setMessage("This is a nice and short message in the post body")
                .build();


        // check that the post insert works
        String hash = memoryStore.insertPost(p);
        Assert.assertNotNull(hash);
        Pair<String, DhtProto.Post> pair = null;

        try {
            pair = memoryStore.findPostByHash(hash);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }
        Assert.assertEquals(p, pair.second);
        Assert.assertEquals(hash, pair.first);



        p = DhtProto.Post.newBuilder()
                .setTitle("Post3")
                .setMessage("This is a nice and short message in the post body")
                .build();


        // check that the post insert works
        hash = memoryStore.insertPost(p);
        hash = memoryStore.insertPost(p);       // insert a duplicate

        Assert.assertNotNull(hash);
        pair = null;

        try {
            pair = memoryStore.findPostByHash(hash);        // this should throw an exception
        }
        catch (TooManyResultsException e) {
            assert true;
        }
        Assert.assertNull(pair);


        // check null search
        pair = null;
        try {
            pair = memoryStore.findPostByHash(null);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }
        Assert.assertNull(pair);


        // check no matching key
        pair = null;
        try {
            pair = memoryStore.findPostByHash("abc");
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }
        Assert.assertNull(pair);
    }

    @Test
    public void findPostsByKeyword()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post4")
                .setMessage("TMessage body does not matter")
                .addTags("Pop")
                .addTags("sprite")
                .build();


        // check that the post insert works
        String hash = memoryStore.insertPost(p);
        Assert.assertNotNull(hash);

        DhtProto.Post p2 = DhtProto.Post.newBuilder()
                .setTitle("Post5")
                .setMessage("TMessage body does not matter")
                .addTags("coke")
                .addTags("sprite")
                .build();


        // check that the post insert works
        hash = memoryStore.insertPost(p2);
        Assert.assertNotNull(hash);

        // check 2 search results
        ArrayList<Pair<String, DhtProto.Post>> posts = (ArrayList<Pair<String, DhtProto.Post>>) memoryStore.findPostsByKeyword("sprite");
        Assert.assertNotNull(posts);
        Assert.assertEquals(2, posts.size());

        // check 1 search result
        posts = (ArrayList<Pair<String, DhtProto.Post>>) memoryStore.findPostsByKeyword("coke");
        Assert.assertNotNull(posts);
        Assert.assertEquals(1, posts.size());

        // no results
        posts = (ArrayList<Pair<String, DhtProto.Post>>) memoryStore.findPostsByKeyword("bob marley");
        Assert.assertNotNull(posts);
        Assert.assertEquals(0, posts.size());
    }

    @Test
    public void insertComment()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post6")
                .build();

        // check that the post works (it is a prereq
        String postHash = memoryStore.insertPost(p);
        Assert.assertNotNull(postHash);


        DhtProto.Comment comment = DhtProto.Comment.newBuilder()
                .setMessage("Comment on the odd post")
                .build();

        // check that the comment works
        String hash = memoryStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash);

        // check that the keyword item for the comment got inserted
        String hash2 = memoryStore.hashMap.get(postHash).get(1).getKeyword().getHash();

        Assert.assertEquals(DhtProto.KeywordType.COMMENT_FOR, memoryStore.hashMap.get(postHash).get(1).getKeyword().getType());
        Assert.assertEquals(hash, hash2);


        // check that a second comment for the post can be added
        comment = DhtProto.Comment.newBuilder()
                .setMessage("Another comment for the post")
                .build();

        // check that the comment works
        hash = memoryStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash);

        Assert.assertEquals(3, memoryStore.hashMap.get(postHash).size());
    }

    @Test
    public void findCommentsByPost()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post7")
                .build();

        // check that the post works (it is a prereq
        String postHash = memoryStore.insertPost(p);
        Assert.assertNotNull(postHash);


        DhtProto.Comment comment = DhtProto.Comment.newBuilder()
                .setMessage("This is the first comment on post 7")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
                .build();

        // check that the comment works
        String hash1 = memoryStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash1);

        comment = DhtProto.Comment.newBuilder()
                .setMessage("This is the second comment on post 7")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis() - 500))
                .build();

        // check that the comment works
        String hash2 = memoryStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash2);

        // done prereqs

        List<Pair<String, DhtProto.Comment>> list = memoryStore.findCommentsByPost(postHash);

        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(hash2, list.get(0).first);      // the second inserted post should be first
                                                        // since it has an earler timestamp
        Assert.assertEquals(hash1, list.get(1).first);



        p = DhtProto.Post.newBuilder()
                .setTitle("Post8")
                .build();

        // check that the post works (it is a prereq
        String postHash2 = memoryStore.insertPost(p);
        Assert.assertNotNull(postHash2);

        // there should be no comments, empty list
        list = memoryStore.findCommentsByPost(postHash2);

        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        // there should be no comments, empty list
        list = memoryStore.findCommentsByPost(null);

        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void findCommentByHash()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post9")
                .build();

        // check that the post works (it is a prereq)
        String postHash = memoryStore.insertPost(p);
        Assert.assertNotNull(postHash);


        DhtProto.Comment comment = DhtProto.Comment.newBuilder()
                .setMessage("This is the first comment on post 9")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
                .build();

        // check that the comment works
        String hash1 = memoryStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash1);

        Pair<String, DhtProto.Comment> pair = null;
        try {
            pair = memoryStore.findCommentByHash(hash1);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        Assert.assertNotNull(pair);
        Assert.assertEquals(hash1, pair.first);


        // null search
        pair = null;
        try {
            pair = memoryStore.findCommentByHash(null);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        Assert.assertNull(pair);

        // no matches
        pair = null;
        try {
            pair = memoryStore.findCommentByHash(postHash);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        Assert.assertNull(pair);
    }

    @Test
    public void insertUser()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);
        DhtProto.User user = DhtProto.User.newBuilder()
                .setFirstName("bob")
                .setLastName("John")
                .setUuid(UUID.randomUUID().toString())
                .build();

        String hash = memoryStore.insertUser(user);
        Assert.assertNotNull(hash);

        // check that the user is where it should be
        DhtProto.User u2 = memoryStore.hashMap.get(hash).get(0).getUser();
        Assert.assertEquals(user, u2);

        // check that the first name search token is inserted
        String hash2 = memoryStore.hashMap.get(Util.generateHash("bob".getBytes())).get(0).getKeyword().getHash();
        Assert.assertEquals(hash, hash2);


        // check insert of null user
        hash = memoryStore.insertUser(null);
        Assert.assertNull(hash);
    }

    @Test
    public void findUserByHash()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);

        String uuid = UUID.randomUUID().toString();
        DhtProto.User user = DhtProto.User.newBuilder()
                .setFirstName("Jakob")
                .setLastName("Smith")
                .setUuid(uuid)
                .build();

        String hash = memoryStore.insertUser(user);
        Assert.assertNotNull(hash);

        Pair<String, DhtProto.User> pair = null;
        try {
            pair = memoryStore.findUserByHash(Util.generateHash(uuid.getBytes()));
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        Assert.assertNotNull(pair);
        Assert.assertEquals(user, pair.second);

        // invalid result
        pair = null;
        try {
            pair = memoryStore.findUserByHash(Util.generateHash("userID".getBytes()));
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        Assert.assertNull(pair);

        // invalid hash
        pair = null;
        try {
            pair = memoryStore.findUserByHash(null);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        Assert.assertNull(pair);
    }

    @Test
    public void findUsersByName()
    {
        MemoryStore memoryStore = MemoryStore.getInstance(null);

        DhtProto.User user = DhtProto.User.newBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setUuid(UUID.randomUUID().toString())
                .build();

        String hash = memoryStore.insertUser(user);
        Assert.assertNotNull(hash);

        DhtProto.User user2 = DhtProto.User.newBuilder()
                .setFirstName("John")
                .setLastName("Asch")
                .setUuid(UUID.randomUUID().toString())
                .build();

        hash = memoryStore.insertUser(user2);
        Assert.assertNotNull(hash);

        List<Pair<String, DhtProto.User>> list = null;
        list = memoryStore.findUsersByName("john doe");

        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(user, list.get(0).second);

        // check case insensitivity
        list = memoryStore.findUsersByName("John Doe");

        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(user, list.get(0).second);

        // check no match
        list = memoryStore.findUsersByName("john micheal");

        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        // check null search
        list = memoryStore.findUsersByName(null);

        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        // check multiple results
        list = memoryStore.findUsersByName("john");

        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());

    }

    @Test
    public void updateUser()
    {
        // this function is not yet implemented.
        assert false;
    }
}