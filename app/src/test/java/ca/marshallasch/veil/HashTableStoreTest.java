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
public class HashTableStoreTest
{
    @Test
    public void getInstance()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);

        // make sure that got a value
        Assert.assertNotNull(hashTableStore);

        // make sure that they are the same instance
        Assert.assertEquals(hashTableStore, HashTableStore.getInstance(null));
    }

    @Test
    public void insertPost()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .build();

        // if the post is null then the hash should be as well
        Assert.assertNull(hashTableStore.insertPost(null));

        // check that the empty post works
        String hash = hashTableStore.insertPost(p);
        Assert.assertNotNull(hash);
        DhtProto.Post p2 = hashTableStore.hashMap.get(hash).get(0).getPost();
        Assert.assertEquals(p, p2);


        p = DhtProto.Post.newBuilder()
                .setTitle("Post title 1")
                .build();

        // check that the post works
        hash = hashTableStore.insertPost(p);
        Assert.assertNotNull(hash);

        // check that the keyword item for the post got inserted
        String hash2 = hashTableStore.hashMap.get(Util.generateHash("post".getBytes())).get(0).getKeyword().getHash();
        Assert.assertEquals(hash, hash2);
    }

    @Test
    public void findPostByHash()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post2")
                .setMessage("This is a nice and short message in the post body")
                .build();


        // check that the post insert works
        String hash = hashTableStore.insertPost(p);
        Assert.assertNotNull(hash);
        Pair<String, DhtProto.Post> pair = null;

        try {
            pair = hashTableStore.findPostByHash(hash);
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
        hash = hashTableStore.insertPost(p);
        hash = hashTableStore.insertPost(p);       // insert a duplicate

        Assert.assertNotNull(hash);
        pair = null;

        try {
            pair = hashTableStore.findPostByHash(hash);        // this should throw an exception
        }
        catch (TooManyResultsException e) {
            assert true;
        }
        Assert.assertNull(pair);


        // check null search
        pair = null;
        try {
            pair = hashTableStore.findPostByHash(null);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }
        Assert.assertNull(pair);


        // check no matching key
        pair = null;
        try {
            pair = hashTableStore.findPostByHash("abc");
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
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post4")
                .setMessage("TMessage body does not matter")
                .addTags("Pop")
                .addTags("sprite")
                .build();


        // check that the post insert works
        String hash = hashTableStore.insertPost(p);
        Assert.assertNotNull(hash);

        DhtProto.Post p2 = DhtProto.Post.newBuilder()
                .setTitle("Post5")
                .setMessage("TMessage body does not matter")
                .addTags("coke")
                .addTags("sprite")
                .build();


        // check that the post insert works
        hash = hashTableStore.insertPost(p2);
        Assert.assertNotNull(hash);

        // check 2 search results
        ArrayList<Pair<String, DhtProto.Post>> posts = (ArrayList<Pair<String, DhtProto.Post>>) hashTableStore.findPostsByKeyword("sprite");
        Assert.assertNotNull(posts);
        Assert.assertEquals(2, posts.size());

        // check 1 search result
        posts = (ArrayList<Pair<String, DhtProto.Post>>) hashTableStore.findPostsByKeyword("coke");
        Assert.assertNotNull(posts);
        Assert.assertEquals(1, posts.size());

        // no results
        posts = (ArrayList<Pair<String, DhtProto.Post>>) hashTableStore.findPostsByKeyword("bob marley");
        Assert.assertNotNull(posts);
        Assert.assertEquals(0, posts.size());
    }

    @Test
    public void insertComment()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post6")
                .build();

        // check that the post works (it is a prereq
        String postHash = hashTableStore.insertPost(p);
        Assert.assertNotNull(postHash);


        DhtProto.Comment comment = DhtProto.Comment.newBuilder()
                .setMessage("Comment on the odd post")
                .build();

        // check that the comment works
        String hash = hashTableStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash);

        // check that the keyword item for the comment got inserted
        String hash2 = hashTableStore.hashMap.get(postHash).get(1).getKeyword().getHash();

        Assert.assertEquals(DhtProto.KeywordType.COMMENT_FOR, hashTableStore.hashMap.get(postHash).get(1).getKeyword().getType());
        Assert.assertEquals(hash, hash2);


        // check that a second comment for the post can be added
        comment = DhtProto.Comment.newBuilder()
                .setMessage("Another comment for the post")
                .build();

        // check that the comment works
        hash = hashTableStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash);

        Assert.assertEquals(3, hashTableStore.hashMap.get(postHash).size());
    }

    @Test
    public void findCommentsByPost()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post7")
                .build();

        // check that the post works (it is a prereq
        String postHash = hashTableStore.insertPost(p);
        Assert.assertNotNull(postHash);


        DhtProto.Comment comment = DhtProto.Comment.newBuilder()
                .setMessage("This is the first comment on post 7")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
                .build();

        // check that the comment works
        String hash1 = hashTableStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash1);

        comment = DhtProto.Comment.newBuilder()
                .setMessage("This is the second comment on post 7")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis() - 500))
                .build();

        // check that the comment works
        String hash2 = hashTableStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash2);

        // done prereqs

        List<Pair<String, DhtProto.Comment>> list = hashTableStore.findCommentsByPost(postHash);

        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(hash2, list.get(0).first);      // the second inserted post should be first
                                                        // since it has an earler timestamp
        Assert.assertEquals(hash1, list.get(1).first);



        p = DhtProto.Post.newBuilder()
                .setTitle("Post8")
                .build();

        // check that the post works (it is a prereq
        String postHash2 = hashTableStore.insertPost(p);
        Assert.assertNotNull(postHash2);

        // there should be no comments, empty list
        list = hashTableStore.findCommentsByPost(postHash2);

        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        // there should be no comments, empty list
        list = hashTableStore.findCommentsByPost(null);

        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void findCommentByHash()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = DhtProto.Post.newBuilder()
                .setTitle("Post9")
                .build();

        // check that the post works (it is a prereq)
        String postHash = hashTableStore.insertPost(p);
        Assert.assertNotNull(postHash);


        DhtProto.Comment comment = DhtProto.Comment.newBuilder()
                .setMessage("This is the first comment on post 9")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
                .build();

        // check that the comment works
        String hash1 = hashTableStore.insertComment(comment, postHash);
        Assert.assertNotNull(hash1);

        Pair<String, DhtProto.Comment> pair = null;
        try {
            pair = hashTableStore.findCommentByHash(hash1);
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
            pair = hashTableStore.findCommentByHash(null);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        Assert.assertNull(pair);

        // no matches
        pair = null;
        try {
            pair = hashTableStore.findCommentByHash(postHash);
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
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.User user = DhtProto.User.newBuilder()
                .setFirstName("bob")
                .setLastName("John")
                .setUuid(UUID.randomUUID().toString())
                .build();

        String hash = hashTableStore.insertUser(user);
        Assert.assertNotNull(hash);

        // check that the user is where it should be
        DhtProto.User u2 = hashTableStore.hashMap.get(hash).get(0).getUser();
        Assert.assertEquals(user, u2);

        // check that the first name search token is inserted
        String hash2 = hashTableStore.hashMap.get(Util.generateHash("bob".getBytes())).get(0).getKeyword().getHash();
        Assert.assertEquals(hash, hash2);


        // check insert of null user
        hash = hashTableStore.insertUser(null);
        Assert.assertNull(hash);
    }

    @Test
    public void findUserByHash()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);

        String uuid = UUID.randomUUID().toString();
        DhtProto.User user = DhtProto.User.newBuilder()
                .setFirstName("Jakob")
                .setLastName("Smith")
                .setUuid(uuid)
                .build();

        String hash = hashTableStore.insertUser(user);
        Assert.assertNotNull(hash);

        Pair<String, DhtProto.User> pair = null;
        try {
            pair = hashTableStore.findUserByHash(Util.generateHash(uuid.getBytes()));
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
            pair = hashTableStore.findUserByHash(Util.generateHash("userID".getBytes()));
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        Assert.assertNull(pair);

        // invalid hash
        pair = null;
        try {
            pair = hashTableStore.findUserByHash(null);
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
        HashTableStore hashTableStore = HashTableStore.getInstance(null);

        DhtProto.User user = DhtProto.User.newBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setUuid(UUID.randomUUID().toString())
                .build();

        String hash = hashTableStore.insertUser(user);
        Assert.assertNotNull(hash);

        DhtProto.User user2 = DhtProto.User.newBuilder()
                .setFirstName("John")
                .setLastName("Asch")
                .setUuid(UUID.randomUUID().toString())
                .build();

        hash = hashTableStore.insertUser(user2);
        Assert.assertNotNull(hash);

        List<Pair<String, DhtProto.User>> list = null;
        list = hashTableStore.findUsersByName("john doe");

        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(user, list.get(0).second);

        // check case insensitivity
        list = hashTableStore.findUsersByName("John Doe");

        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(user, list.get(0).second);

        // check no match
        list = hashTableStore.findUsersByName("john micheal");

        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        // check null search
        list = hashTableStore.findUsersByName(null);

        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        // check multiple results
        list = hashTableStore.findUsersByName("john");

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