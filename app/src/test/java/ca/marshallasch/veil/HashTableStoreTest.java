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

import static org.junit.Assert.*;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-12
 */
public class HashTableStoreTest
{

    DhtProto.User author = DhtProto.User.newBuilder()
            .setFirstName("User")
            .setLastName("LastName")
            .build();
    @Test
    public void getInstance()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);

        // make sure that got a value
        assertNotNull(hashTableStore);

        // make sure that they are the same instance
        assertEquals(hashTableStore, HashTableStore.getInstance(null));
    }

    @Test
    public void insertPost()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = Util.createPost("Post0", "This is a nice and short message in the post body", author, null);


        // if the post is null then the hash should be as well
        assertNull(hashTableStore.insertPost(null));

        // check that the empty post works
        String hash = hashTableStore.insertPost(p);
        assertNotNull(hash);
        DhtProto.Post p2 = hashTableStore.hashMap.get(hash).get(0).getPost();
        assertEquals(p, p2);

        ArrayList<String> tags = new ArrayList<String>(){{add("post1-tag");}};


        p = Util.createPost("Post1", "This is a nice and short message in the post body", author, tags);

        // check that the post works
        hash = hashTableStore.insertPost(p);
        assertNotNull(hash);

        // check that the keyword item for the post got inserted
        String hash2 = hashTableStore.hashMap.get(Util.generateHash("post1-tag".getBytes())).get(0).getKeyword().getHash();
        Assert.assertEquals(hash, hash2);
    }

    @Test
    public void findPostByHash()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = Util.createPost("Post2", "This is a nice and short message in the post body", author, null);


        // check that the post insert works
        String hash = hashTableStore.insertPost(p);
        assertNotNull(hash);
        DhtProto.Post post = null;

        try {
            post = hashTableStore.findPostByHash(hash);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }
        assertEquals(p, post);
        assertEquals(hash, post.getUuid());


        Util.createPost("Post3", "This is a nice and short message in the post body", author, null);


        // check that the post insert works
        hash = hashTableStore.insertPost(p);
        hash = hashTableStore.insertPost(p);       // insert a duplicate

        assertNotNull(hash);
        post = null;

        try {
            post = hashTableStore.findPostByHash(hash);        // this should throw an exception
        }
        catch (TooManyResultsException e) {
            assert false;
        }
        assertNotNull(post);


        // check null search
        post = null;
        try {
            post = hashTableStore.findPostByHash(null);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }
        assertNull(post);


        // check no matching key
        post = null;
        try {
            post = hashTableStore.findPostByHash("abc");
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }
        assertNull(post);
    }

    @Test
    public void findPostsByKeyword()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);

        List<String> tags = new ArrayList<String>(){{add("pop"); add("sprite");}};

        DhtProto.Post  p = Util.createPost("Post4", "This is a nice and short message in the post body", author, tags);



        // check that the post insert works
        String hash = hashTableStore.insertPost(p);
        assertNotNull(hash);

        tags = new ArrayList<String>(){{add("coke"); add("sprite");}};
        DhtProto.Post p2 = Util.createPost("Post5", "This is a nice and short message in the post body", author, tags);


        // check that the post insert works
        hash = hashTableStore.insertPost(p2);
        assertNotNull(hash);

        // check 2 search results
        ArrayList<DhtProto.Post> posts =  (ArrayList<DhtProto.Post>) hashTableStore.findPostsByKeyword("sprite");
        assertNotNull(posts);
        assertEquals(2, posts.size());

        // check 1 search result
        posts = (ArrayList<DhtProto.Post>) hashTableStore.findPostsByKeyword("coke");
        assertNotNull(posts);
        assertEquals(1, posts.size());

        // no results
        posts = (ArrayList<DhtProto.Post>) hashTableStore.findPostsByKeyword("bob marley");
        assertNotNull(posts);
        assertEquals(0, posts.size());
    }

    @Test
    public void insertComment()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = Util.createPost("Post6", "This is a nice and short message in the post body", author, null);


        // check that the post works (it is a prereq
        String postHash = hashTableStore.insertPost(p);
        assertNotNull(postHash);


        DhtProto.Comment comment = DhtProto.Comment.newBuilder()
                .setMessage("Comment on the odd post")
                .build();

        // check that the comment works
        String hash = hashTableStore.insertComment(comment, postHash);
        assertNotNull(hash);

        // check that the keyword item for the comment got inserted
        String hash2 = hashTableStore.hashMap.get(postHash).get(1).getKeyword().getHash();

        assertEquals(DhtProto.KeywordType.COMMENT_FOR, hashTableStore.hashMap.get(postHash).get(1).getKeyword().getType());
        assertEquals(hash, hash2);


        // check that a second comment for the post can be added
        comment = DhtProto.Comment.newBuilder()
                .setMessage("Another comment for the post")
                .build();

        // check that the comment works
        hash = hashTableStore.insertComment(comment, postHash);
        assertNotNull(hash);

        assertEquals(3, hashTableStore.hashMap.get(postHash).size());
    }

    @Test
    public void findCommentsByPost()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = Util.createPost("Post7", "This is a nice and short message in the post body", author, null);


        // check that the post works (it is a prereq
        String postHash = hashTableStore.insertPost(p);
        assertNotNull(postHash);


        DhtProto.Comment comment = DhtProto.Comment.newBuilder()
                .setMessage("This is the first comment on post 7")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
                .build();

        // check that the comment works
        String hash1 = hashTableStore.insertComment(comment, postHash);
        assertNotNull(hash1);

        comment = DhtProto.Comment.newBuilder()
                .setMessage("This is the second comment on post 7")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis() - 500))
                .build();

        // check that the comment works
        String hash2 = hashTableStore.insertComment(comment, postHash);
        assertNotNull(hash2);

        // done prereqs

        List<Pair<String, DhtProto.Comment>> list = hashTableStore.findCommentsByPost(postHash);

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(hash2, list.get(0).first);      // the second inserted post should be first
                                                        // since it has an earler timestamp
        assertEquals(hash1, list.get(1).first);



        p = Util.createPost("Post8", "This is a nice and short message in the post body", author, null);


        // check that the post works (it is a prereq
        String postHash2 = hashTableStore.insertPost(p);
        assertNotNull(postHash2);

        // there should be no comments, empty list
        list = hashTableStore.findCommentsByPost(postHash2);

        assertNotNull(list);
        assertEquals(0, list.size());

        // there should be no comments, empty list
        list = hashTableStore.findCommentsByPost(null);

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    public void findCommentByHash()
    {
        HashTableStore hashTableStore = HashTableStore.getInstance(null);
        DhtProto.Post p = Util.createPost("Post9", "This is a nice and short message in the post body", author, null);


        // check that the post works (it is a prereq)
        String postHash = hashTableStore.insertPost(p);
        assertNotNull(postHash);


        DhtProto.Comment comment = DhtProto.Comment.newBuilder()
                .setMessage("This is the first comment on post 9")
                .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
                .build();

        // check that the comment works
        String hash1 = hashTableStore.insertComment(comment, postHash);
        assertNotNull(hash1);

        Pair<String, DhtProto.Comment> pair = null;
        try {
            pair = hashTableStore.findCommentByHash(hash1);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        assertNotNull(pair);
        assertEquals(hash1, pair.first);


        // null search
        pair = null;
        try {
            pair = hashTableStore.findCommentByHash(null);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        assertNull(pair);

        // no matches
        pair = null;
        try {
            pair = hashTableStore.findCommentByHash(postHash);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        assertNull(pair);
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
        assertNotNull(hash);

        // check that the user is where it should be
        DhtProto.User u2 = hashTableStore.hashMap.get(hash).get(0).getUser();
        assertEquals(user, u2);

        // check that the first name search token is inserted
        String hash2 = hashTableStore.hashMap.get(Util.generateHash("bob".getBytes())).get(0).getKeyword().getHash();
        assertEquals(hash, hash2);


        // check insert of null user
        hash = hashTableStore.insertUser(null);
        assertNull(hash);
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
        assertNotNull(hash);

        Pair<String, DhtProto.User> pair = null;
        try {
            pair = hashTableStore.findUserByHash(Util.generateHash(uuid.getBytes()));
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        assertNotNull(pair);
        assertEquals(user, pair.second);

        // invalid result
        pair = null;
        try {
            pair = hashTableStore.findUserByHash(Util.generateHash("userID".getBytes()));
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        assertNull(pair);

        // invalid hash
        pair = null;
        try {
            pair = hashTableStore.findUserByHash(null);
        }
        catch (TooManyResultsException e) {
            e.printStackTrace();
            assert false;
        }

        assertNull(pair);
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
        assertNotNull(hash);

        DhtProto.User user2 = DhtProto.User.newBuilder()
                .setFirstName("John")
                .setLastName("Asch")
                .setUuid(UUID.randomUUID().toString())
                .build();

        hash = hashTableStore.insertUser(user2);
        assertNotNull(hash);

        List<Pair<String, DhtProto.User>> list = null;
        list = hashTableStore.findUsersByName("john doe");

        assertNotNull(list);
        assertEquals(user, list.get(0).second);

        // check no match
        list = hashTableStore.findUsersByName("john micheal");

        assertNotNull(list);
        assertEquals(0, list.size());

        // check null search
        list = hashTableStore.findUsersByName(null);

        assertNotNull(list);
        assertEquals(0, list.size());

        // check multiple results
        list = hashTableStore.findUsersByName("john");

        assertNotNull(list);
        assertEquals(2, list.size());

    }

    @Test
    public void updateUser()
    {
        // this function is not yet implemented.
        assert false;
    }
}