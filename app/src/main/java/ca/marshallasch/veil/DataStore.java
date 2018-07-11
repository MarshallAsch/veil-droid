package ca.marshallasch.veil;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import ca.marshallasch.veil.comparators.CommentComparator;
import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.exceptions.TooManyResultsException;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.proto.Sync;
import io.left.rightmesh.id.MeshId;

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

    public void close() {
        db.close();
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
     * @return a list of comments
     */
    public List<DhtProto.Comment> getCommentsForPost(String postHash) {

        List<String> hashes = db.getCommentHashes(postHash);

        DhtProto.Comment comment;
        List<DhtProto.Comment> comments = new ArrayList<>();

        // get each post that is in the list
        for (String hash: hashes) {

            comment = null;
            try {
                comment = hashTableStore.findCommentByHash(hash);
            }
            catch (TooManyResultsException e) {
                e.printStackTrace();
            }

            // add the post to the list
            if (comment != null) {
                comments.add(comment);
            }
        }

        // make sure the list of comments are in chronological order
        Collections.sort(comments, new CommentComparator());

        return comments;

    }

    /**
     * This function will take a comment and associate it with the post in the data store.
     * The comment's UUID field must be set to the has for the comment.
     * The this will set the comments PostId field in the one that gets stored, but not in the one
     * that was passed in.
     *
     * @param comment the comment object to insert
     * @param forPost the post the the comment is associated with
     * @return the updated comment object
     */
    @Nullable
    public DhtProto.Comment saveComment(@Nullable DhtProto.Comment comment, DhtProto.Post forPost){

        // make sure args are given
        if (comment == null || forPost == null) {
            return comment;
        }

        // put the post ID into the comment
        comment = DhtProto.Comment.newBuilder(comment)
                .setPostId(forPost.getUuid())
                .build();

        // insert into the data store
        hashTableStore.insertComment(comment, forPost.getUuid());

        // insert the comment for mapping
        db.insertKnownPost(forPost.getUuid(), comment.getUuid());

        return comment;
    }


    public Sync.SyncMessage getSyncFor(MeshId peer) {

        // get time last sent data
        Sync.SyncMessage.Builder builder = Sync.SyncMessage.newBuilder();

        Date timeLastSentData = db.getTimeLastSentData(peer.toString());

        // get the list of comments and post hashes since the given time.
        List<Pair<String, String>> mapping = db.dumpKnownPosts(timeLastSentData);

        Set<String> hashes = new ArraySet<>();

        for (Pair<String, String> pair: mapping) {
            hashes.add(pair.first);
            hashes.add(pair.second);

            builder.addMappings(Sync.CommentMapping.newBuilder()
                    .setPostHash(pair.first)
                    .setCommentHash(pair.second)
                    .build());
        }
        // remove the empty string from the empty comment hashes
        hashes.remove("");

        DhtProto.DhtWrapper wrapper;

        for (String hash: hashes) {

            // search for the comment or post
            wrapper = hashTableStore.getPostOrComment(hash);

            // insert into the list
            if (wrapper != null) {
                builder.addEntries(Sync.HashPair.newBuilder()
                        .setHash(hash)
                        .setEntry(wrapper)
                        .build());
            }
        }

        return builder.build();
    }


    public void insertSync(Sync.SyncMessage syncMessage) {


        List<Sync.HashPair> entries = syncMessage.getEntriesList();

        // insert all of the mappings
        for (Sync.HashPair entry: entries) {
            Log.d("PAIRS", "e: " + entry.getEntry().getType().getNumber() + " :: " + entry.getHash());
            hashTableStore.insert (entry.getEntry(), entry.getHash());
        }

        List<Sync.CommentMapping> mapping = syncMessage.getMappingsList();
        List<Pair<String, String>> knownPosts = db.dumpKnownPosts();

        Log.d("MAPPING", "LEN: " + mapping.size());

        // insert all of the mappings
        for (Sync.CommentMapping entry: mapping) {

            // TODO: 2018-07-11 improve the efficiency of this
            // skip if we already have the entry
            if (knownPosts.contains(new Pair<>(entry.getPostHash(), entry.getCommentHash()))) {
                continue;
            }

            db.insertKnownPost(entry.getPostHash(), entry.getCommentHash());
        }

    }








}
