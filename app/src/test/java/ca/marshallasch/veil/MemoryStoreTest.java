package ca.marshallasch.veil;

import android.support.v4.util.Pair;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

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
    }

    @Test
    public void findCommentsByPost()
    {
    }

    @Test
    public void findCommentByHash()
    {
    }

    @Test
    public void insertUser()
    {
    }

    @Test
    public void findUserByHash()
    {
    }

    @Test
    public void findUsersByName()
    {
    }

    @Test
    public void updateUser()
    {
    }
}