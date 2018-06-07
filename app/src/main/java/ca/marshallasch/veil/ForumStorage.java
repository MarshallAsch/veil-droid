package ca.marshallasch.veil;

import android.util.Pair;

import java.util.List;

import ca.marshallasch.veil.proto.DhtProto;

/**
 * This interface relies on the data being put in some form or content addressed storage.
 * the internal ID's of fields only have meaning when for internal relationships. all lookups
 * need to be done with keywords or the Hashes (the hash is the ID in the storage implementation)
 * but does not need to map to one entry.
 *
 * For example you have a post with 5 comments, by doing a search for the post hash a DHT
 * could find the post and the comments indexes in the same bucket. but a database design may have
 * be organized differently
 *
 * Don't need to worry about hash collisions if using SHA256 for hashes, good enough for a
 * realistic amount of data.
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-06
 */
public interface ForumStorage
{

    /*******************************************************
     * Need to be able to do the following for posts:
     * results should include the the hash of the entry so it can be cached and not searched for
     * again.
     *
     * - insert a new post
     * - search for a post by hash directly
     * - search for a list of posts that match a keyword
     *
     *******************************************************/

    /**
     * Insert a new post into the data store. This should also add any necessary indexing records.
     * @param post the post item to store
     * @return the hash of the post used for its lookup in the future.
     */
    String insertPost(DhtProto.Post post);

    /**
     * Look up a single post entry using its hashed key. This could ether make use of a cache
     * or some sort of distributed store.
     * @param hash the unique SHA256 hash of the post
     * @return a key value pair containing the hash and the post object
     */
    Pair<String, DhtProto.Post> findPostByHash(String hash);

    /**
     * Look up a post by some keywords, how the keyword string is handled is implementation
     * dependant.
     * @param keyword the keyword string to look up
     * @return a list of key value pairs with the hash and the Post object, order is implementation
     *          dependant.
     */
    List<Pair<String, DhtProto.Post>> findPostsByKeyword(String keyword);


    /*******************************************************
     * Need to be able to do the following for comments:
     * results should include the the hash of the entry so it can be cached and not searched for
     * again.
     *
     * - insert a new comment
     * - search for a comment by hash directly
     * - search for a list of comments that are for a post
     *
     *******************************************************/

    /**
     * This will add a new comment to the data store.
     * @param comment the comment object that will be added
     * @param postHash the post identifier that the comment is being added to
     * @return the hash of the comment object
     */
    String insertComment(DhtProto.Comment comment, String postHash);

    /**
     * Look up a list comment entries using its associated post hash.
     * @param postHash the unique SHA256 hash of the post
     * @return a list of key value pairs containing the hash and the comment object
     */
    List<Pair<String, DhtProto.Comment>> findCommentsByPost(String postHash);

    /**
     * Look up a single comment entry using its hashed key. This could ether make use of a cache
     * or some sort of distributed store.
     * @param hash the unique SHA256 hash of the comment
     * @return a key value pair containing the hash and the comment object
     */
    Pair<String, DhtProto.Comment> findCommentByHash(String hash);


    /*******************************************************
     * Need to be able to do the following for users:
     *
     * - insert a new user
     * - search for a user by name or by email or by the hash directly.
     * - update a user entry
     *
     * Can there be multiple users with the same ID? there should not be
     *******************************************************/

    /**
     * Inserts a public user object into the data storage unit.
     * @param user the public user object to store (does not contain the password)
     * @return the hash to identify the user in the data store
     */
    String insertUser(DhtProto.User user);

    /**
     * Look up a single user entry using its hashed key.
     * @param userHash the unique SHA256 hash identifying the user
     * @return a key value pair containing the hash and the user object
     */
    Pair<String, DhtProto.User> findUserByHash(String userHash);

    /**
     * Search for a user object by the keyword. This could be their name or email/username. How the
     * search is done is implementation independent.
     * @param name the keyword to search for.
     * @return a list of matching key value pairs
     */
    List<Pair<String, DhtProto.User>> findUsersByName(String name);

    /**
     * Update a user item in the data store. Note that this will not update they key only the data.
     * Also note that this change will not propagate immediately
     * @param user the updated user object
     * @return true if the update was successful, false otherwise.
     */
    boolean updateUser(DhtProto.User user);
}
