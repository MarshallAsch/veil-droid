package ca.marshallasch.veil;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ca.marshallasch.veil.comparators.CommentPairComparator;
import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.exceptions.TooManyResultsException;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.proto.Sync;

/**
 * This class is a delegate class for all of the data access to use this instead of the underling
 * storage objects.
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-15
 */
public class DataStore
{
    private Database db;
    private HashTableStore hashTableStore;

    private static DataStore instance;
    private static final AtomicInteger openCounter = new AtomicInteger();

    private DataStore(Context context) {

        db = Database.getInstance(context);
        hashTableStore = HashTableStore.getInstance(context);

    }

    public static synchronized DataStore getInstance(Context context){

        if (instance == null) {
            instance = new DataStore(context);
        }

        openCounter.getAndIncrement();
        return instance;
    }

    /**
     * Save the hash table to a persistent file.
     * @param context the application context required to save a file.
     */
    public void save(Context context) {
        hashTableStore.save(context);
    }

    /**
     * Save a post to the hash table and add its key to the database.
     * @param post the post to insert
     * @return true on success false oon failure.
     */
    public boolean savePost(@Nullable DhtProto.Post post) {

        if (post == null) {
            return false;
        }

        String postHash = hashTableStore.insertPost(post);

        // insert any necessary query keys in here.

        return db.insertKnownPost(postHash, null);
    }

    /**
     * Gets all the known posts in a the data store
     * @return the list of posts.
     */
    public List<DhtProto.Post> getKnownPosts() {

        List<String> hashes = db.getPostHashes();

        DhtProto.Post post;
        List<DhtProto.Post> posts = new ArrayList<>();

        // get each post that is in the list
        for (String hash: hashes) {

            post = null;
            try {
                post = hashTableStore.findPostByHash(hash);
            }
            catch (TooManyResultsException e) {
                e.printStackTrace();
            }

            // add the post to the list
            if (post != null) {
                posts.add(post);
            }
        }

        return posts;
    }

    /**
     * Gets the list of comments for a post sorted in by time posted for a given post.
     * @param postHash the hash of the post that the comments will be found for.
     * @return a list of comment pairs
     */
    public List<Pair<String, DhtProto.Comment>> getCommentsForPost(String postHash) {

        List<String> hashes = db.getCommentHashes(postHash);

        Pair<String, DhtProto.Comment> commentPair;
        List<Pair<String, DhtProto.Comment>> comments = new ArrayList<>();

        // get each post that is in the list
        for (String hash: hashes) {

            commentPair = null;
            try {
                commentPair = hashTableStore.findCommentByHash(hash);
            }
            catch (TooManyResultsException e) {
                e.printStackTrace();
            }

            // add the post to the list
            if (commentPair != null) {
                comments.add(commentPair);
            }
        }

        Collections.sort(comments, new CommentPairComparator());

        return comments;

    }


    /**
     * Generate the object for syncing the database between devices.
     * @return the mapping object
     */
    public Sync.MappingMessage getDatabase() {

        List<Pair<String, String>> knownPosts = db.dumpKnownPosts();

        Sync.MappingMessage.Builder builder = Sync.MappingMessage.newBuilder();

        Sync.CommentMapping.Builder commentBuilder;
        // add it to the list
        for (Pair<String, String> pair: knownPosts) {

            commentBuilder = Sync.CommentMapping.newBuilder();

            // handle nulls
            if (pair.first != null) {
                commentBuilder.setPostHash(pair.first);
            }

            if (pair.second != null) {
                commentBuilder.setCommentHash(pair.second);
            }

            builder.addMappings(commentBuilder.build());
        }

        // build the message to send to other devices
        return builder.build();
    }

    /**
     * Generate the syncing object for the data store. It will contain all of the objects
     * for the posts and the comments only.
     * @return the message object
     */
    public Sync.HashData getDataStore() {

        List<Pair<String, DhtProto.DhtWrapper>> data = hashTableStore.getData();


        Sync.HashData.Builder builder = Sync.HashData.newBuilder();

        // generate the list
        for(Pair<String, DhtProto.DhtWrapper> pair: data) {

            builder.addEntries(Sync.HashPair.newBuilder()
                    .setHash(pair.first)
                    .setEntry(pair.second)
                    .build());
        }

        return builder.build();
    }

    /**
     * Will insert the database sync object into the database.
     * @param message the message to insert
     */
    public void syncDatabase(Sync.MappingMessage message) {

        List<Sync.CommentMapping> mapping = message.getMappingsList();

        // insert all of the mappings
        for (Sync.CommentMapping entry: mapping) {
            db.insertKnownPost(entry.getPostHash(), entry.getCommentHash());
        }
    }

    /**
     * Will insert all of the synced data from another device.
     * @param message the data sync object.
     */
    public void syncData(Sync.HashData message) {

        List<Sync.HashPair> mapping = message.getEntriesList();

        // insert all of the mappings
        for (Sync.HashPair entry: mapping) {
            Log.d("PAIRS", "e: " + entry.getEntry().getType().getNumber() + " :: " + entry.getHash());
            hashTableStore.insert (entry.getEntry(), entry.getHash());
        }
    }

}
