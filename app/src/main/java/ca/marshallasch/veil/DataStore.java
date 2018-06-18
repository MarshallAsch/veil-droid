package ca.marshallasch.veil;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ca.marshallasch.veil.comparators.CommentPairComparator;
import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.exceptions.TooManyResultsException;
import ca.marshallasch.veil.proto.DhtProto;

/**
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
     * @return the list of posts pairs.
     */
    public List<Pair<String, DhtProto.Post>> getKnownPosts() {

        List<String> hashes = db.getPostHashes();

        Pair<String, DhtProto.Post> postPair;
        List<Pair<String, DhtProto.Post>> posts = new ArrayList<>();

        // get each post that is in the list
        for (String hash: hashes) {

            postPair = null;
            try {
                postPair = hashTableStore.findPostByHash(hash);
            }
            catch (TooManyResultsException e) {
                e.printStackTrace();
            }

            // add the post to the list
            if (postPair != null) {
                posts.add(postPair);
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
}
