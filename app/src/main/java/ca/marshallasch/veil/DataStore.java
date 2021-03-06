package ca.marshallasch.veil;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import ca.marshallasch.veil.comparators.CommentComparator;
import ca.marshallasch.veil.comparators.PostAgeComparator;
import ca.marshallasch.veil.database.Database;
import ca.marshallasch.veil.database.KnownPostsContract;
import ca.marshallasch.veil.database.PeerListContract;
import ca.marshallasch.veil.database.SyncStatsContract;
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
    private final Database db;
    private final HashTableStore hashTableStore;

    private static DataStore instance;
    private static final AtomicInteger openCounter = new AtomicInteger();

    private DataStore(Context context) {

        db = Database.getInstance(context);
        hashTableStore = HashTableStore.getInstance(context);

        // start a timer to schedule the saving task to run every 10 minutes
        Timer timer = new Timer();
        timer.schedule(new TimerTask(), 0, 1000*60*10);

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
     */
    public void save() {
        hashTableStore.save();
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
     * Gets all the known posts in a the data store.
     * The list will be sorted, newest posts first
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

        // make sure the list of posts are in chronological order
        Collections.sort(posts, new PostAgeComparator());

        return posts;
    }


    /**
     * Gets all the known posts in a the data store.
     * The list will be sorted, newest posts first
     * @return the list of posts.
     */
    public List<DhtProto.Post> getPostsByAuthorId(String authorHash) {

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
            if (post != null && post.getAuthorId().equals(authorHash)) {
                posts.add(post);
            }
        }

        // make sure the list of posts are in chronological order
        Collections.sort(posts, new PostAgeComparator());

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
     * The comments postID field must be set for the comment.
     *
     * @param comment the comment object to insert
     * @return the updated comment object
     */
    public boolean saveComment(@Nullable DhtProto.Comment comment){

        // make sure args are given
        if (comment == null || comment.getPostId().isEmpty()  || comment.getUuid().isEmpty()) {
            return false;
        }

        // insert into the data store
        hashTableStore.insertComment(comment, comment.getPostId());

        // insert the comment for mapping
        return db.insertKnownPost(comment.getPostId(), comment.getUuid());
    }

    /**
     * This will get the number of comments that exist for given post and will return the number.
     *
     * @param postHash the string identifying the particular post
     * @return the number of comments for the post, 0 if none are found
     */
    @IntRange(from=0)
    public int getNumCommentsFor(@Nullable String postHash) {

        if (postHash == null) {
            return 0;
        }

        String select = KnownPostsContract.KnownPostsEntry.COLUMN_POST_HASH + " = ? AND " + KnownPostsContract.KnownPostsEntry.COLUMN_COMMENT_HASH + " != \"\" ";
        String[] selectArgs = {postHash};

        return db.getCount(KnownPostsContract.KnownPostsEntry.TABLE_NAME, select, selectArgs);
    }

    /**
     * This will check if a post has been read. Will call {@link Database#isRead(String)}
     * @param postHash the post to check
     * @return true if it is read, otherwise false.
     */
    public boolean isRead(String postHash) {
        return db.isRead(postHash);
    }

    /**
     * Function will update the post status where
     * 0 = normal {@link KnownPostsContract#POST_NORMAL}
     * 1 = protected {@link KnownPostsContract#POST_PROTECTED}
     * 2 = dead {@link KnownPostsContract#POST_DEAD}
     *
     * @param postHash the hash of the post object
     * @param status the status 0, 1, 2 as described in the comment above
     * @return <code>true</code> if the update was successful <code>false</code> otherwise
     */
    public boolean setPostStatus(String postHash, int status) {
        return db.setPostStatus(postHash, status);
    }

    /**
     * Function will ask the db to retrieve the post status of the
     * given post.
     *
     * @param postHash the post you wish to check
     * @return the memory status of the post, see {@link KnownPostsContract#POST_NORMAL}
     */
    public int getPostStatus(String postHash) {
        return db.getPostStatus(postHash);
    }

    /**
     * Mark the post as read or unread. This will call to  {@link Database#markRead(String, boolean)}
     * @param postHash the post to mark as read
     * @param read true if the post is being marked as read, otherwise false.
     * @return true on success
     */
    public boolean markRead(String postHash, boolean read) {
        return  db.markRead(postHash, read);
    }

    /**
     * This will generate a data synchronization message for the given peer.
     * This is the either versions message of the data synchronization protocol.
     * @param peer the {@link MeshId} for the peer to send the sync message too
     * @param version specify the version that it should get the sync message for
     * @return a sync message that is filled with the data for that peer
     */
    @NonNull
    public Sync.SyncMessage getSync(MeshId peer, int version) {

        // get time last sent data
        Sync.SyncMessage.Builder builder = Sync.SyncMessage.newBuilder();


        // get the list of comments and post hashes since the given time.
        List<Pair<String, String>> mapping;

        if (version == SyncStatsContract.SYNC_MESSAGE_V2 ) {
            Date timeLastSentData = db.getTimeLastSentData(peer.toString());
            mapping = db.dumpKnownPosts(timeLastSentData);
            db.updateTimeLastSentData(peer.toString());
        } else {
            mapping = db.dumpKnownPosts();
        }

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

    /**
     * This will insert the data sync message into the data store and the database.
     * This is for the version 2 message.
     *
     * @param syncMessage the message from the remote peer to save.
     */
    public void insertSync(@Nullable Sync.SyncMessage syncMessage) {

        if (syncMessage == null) {
            return;
        }

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

        hashTableStore.save();
    }

    /**
     * Clear all the entries from the hash table and all of the entries from the
     * {@link KnownPostsContract} table.
     */
    public void clearEntries() {

        synchronized (hashTableStore.hashMap) {
            hashTableStore.hashMap.clear();
        }

        db.clearKnownPosts();
    }

    /**
     * This is a delegate method to clear the {@link SyncStatsContract} table
     */
    public void clearSyncStats() {
        db.clearSyncStats();
    }

    /**
     * This is a delegate method to clear the {@link PeerListContract} table
     */
    public void clearPeers() {
        db.clearPeers();
    }

    /**
     * This function will clear all posts and comments that are marked for deletion.
     */
    public void runDataSaver(){
        List<String> toBeDeletePostHashes = db.getAllHashesByStatus(KnownPostsContract.POST_NORMAL);

        // delete all the post hashes from the hash table along with its comments
        for(String str: toBeDeletePostHashes) {
            hashTableStore.deleteByHash(str);
        }

        //delete all the hashes with status normal from the db
        db.dataSaverClear();
    }

    /**
     * Gets the number of bytes in the file for the hash table.
     * @return num bytes in the hash table file
     */
    public long getHashTableSize() {
        return hashTableStore.getFileSize();
    }

    /**
     * Gets the number of bytes in the file for the database.
     * @return num bytes in the database file
     */
    public long getDatabaseSize() {
        return db.getSize();
    }

    /**
     * This task will be run every 10 minutes to try to save the has table store if it has been modified.
     */
    private class TimerTask extends java.util.TimerTask {
        @Override
        public void run()
        {
            new SaveHashTable().execute();
        }

        /**
         * Save the Hash table if it has been modified in a worker thread.
         */
        private class SaveHashTable extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids)
            {
                Log.d("SAVE", "SAVING_FILE");
                hashTableStore.save();
                return null;
            }
        }
    }
}
