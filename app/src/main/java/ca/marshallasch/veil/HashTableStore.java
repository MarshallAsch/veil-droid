package ca.marshallasch.veil;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import ca.marshallasch.veil.comparators.CommentPairComparator;
import ca.marshallasch.veil.exceptions.TooManyResultsException;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;

import static ca.marshallasch.veil.proto.DhtProto.KeywordType.*;

/**
 * This class is a data store for forum. It is implemented as a local hash table. This implementation
 * should be fairly similar to the targeted implementation in a distributed hash table.
 * Inorder for the different devices to get a copy of the posts and the messages it will rely on
 * the peer discovery data exchange protocol for now.
 *
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-08
 */
public class HashTableStore implements ForumStorage
{
    // Made package-private so that it can be accessed for testing.
    HashMap<String, List<DhtProto.DhtWrapper>> hashMap;


    private static HashTableStore instance;
    private static final AtomicInteger openCounter = new AtomicInteger();

    private HashTableStore(@Nullable  Context c) {

        // if there is no context then do not load a persistent file
        if (c == null) {
            hashMap = new HashMap<>();
            return;
        }

        // try loading from mem or create new
        File mapFile = new File(c.getFilesDir(), "HASH_MAP");
        HashMap<String, List<DhtProto.DhtWrapper>> tempMap = null;

        if (mapFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(mapFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                tempMap = (HashMap<String, List<DhtProto.DhtWrapper>>) objectInputStream.readObject();

                objectInputStream.close();
                fileInputStream.close();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        // create a new map if one does not exist
        if (tempMap == null) {
            hashMap = new HashMap<>();
        } else {
            hashMap = tempMap;
        }
    }

    public static synchronized HashTableStore getInstance(@Nullable final Context c)
    {
        if (instance == null) {
            instance = new HashTableStore(c);
        }
        openCounter.incrementAndGet();
        return instance;
    }

    /**
     * This function will update the saved copy of the hash map to the disk.
     */
    public void save(@Nullable Context context) {

        // if there is no context then don't save the file
        if (context == null) {
            return;
        }

        File mapFile = new File(context.getFilesDir(), "HASH_MAP");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(mapFile, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(hashMap);

            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This function will insert the post object into the local hash table. This will also add
     * records for the tags and the title to help search for the post later
     * @param post the post item to store
     * @return the hash  identifying the post
     */
    @Override
    public String insertPost(DhtProto.Post post)
    {
        if (post == null) {
            return null;
        }

        String hash = Util.generateHash(post.toByteArray());

        DhtProto.DhtWrapper wrapper = DhtProto.DhtWrapper.newBuilder()
                .setPost(post)
                .setType(DhtProto.MessageType.POST)
                .build();

        insert(wrapper, hash);

        List<String> tags = post.getTagsList();
        String title = post.getTitle();
        DhtProto.DhtWrapper keyword;

        // add the tags to index the post
        for (String tag: tags) {
            tag = tag.toLowerCase(Locale.getDefault());
            keyword = generateKeyword(tag, hash, TAG);
            insert(keyword, Util.generateHash(tag.getBytes()));
        }

        // add the full text title to the index
        keyword = generateKeyword(title, hash, TITLE_FULL);
        insert(keyword, Util.generateHash(title.toLowerCase(Locale.getDefault()).getBytes()));

        // tokenize the title and add the tokens
        String[] titleKeywords = title.split(" ");
        for (String tag: titleKeywords) {
            tag = tag.toLowerCase(Locale.getDefault());
            keyword = generateKeyword(title, hash, TITLE_PARTIAL);
            insert(keyword, Util.generateHash(tag.getBytes()));
        }

        return hash;
    }

    /**
     * This will get a post object for a specific hash. This will throw an exception if
     * more then 1 resulting post is found with the same hash, this should not ever occur but
     * the exception is there to handle the possibility.
     *
     * @param hash the unique SHA256 hash of the post
     * @return null if no matching post is found or the result that contains the post object
     * @throws TooManyResultsException gets thrown if more then 1 post is found with the same hash.
     */
    @Override
    @Nullable
    public Pair<String, DhtProto.Post> findPostByHash(String hash) throws TooManyResultsException
    {
        ArrayList<DhtProto.DhtWrapper> entries = (ArrayList<DhtProto.DhtWrapper>)hashMap.get(hash);

        if (entries == null) {
            return null;
        }
        ArrayList<DhtProto.Post> posts = new ArrayList<>();

        for (DhtProto.DhtWrapper wrapper: entries) {

            // make sure that it is a post item
            if (wrapper.getType() == DhtProto.MessageType.POST) {
               posts.add(wrapper.getPost());
            }
        }

        // make sure that only 1 result was found
        if (posts.size() > 1) {
            throw new TooManyResultsException("Too many results for: " + hash);
        } else if (posts.size() == 1) {
            return new Pair<>(hash, posts.get(0));
        }

        // otherwise there were no results found.
        return null;
    }

    /**
     * This keyword must be a single token to search for, and it must match exactly.
     * The keyword will be normalized to lowercase.
     *
     * @param keyword the keyword string to look up
     * @return the list of results
     */
    @Override
    @Nullable
    public List<Pair<String, DhtProto.Post>> findPostsByKeyword(String keyword)
    {
        keyword = keyword.toLowerCase(Locale.getDefault());
        ArrayList<DhtProto.DhtWrapper> entries = (ArrayList<DhtProto.DhtWrapper>)hashMap.get(Util.generateHash(keyword.getBytes()));

        if (entries == null) {
            return new ArrayList<>();
        }
        ArrayList<String> postHashes = new ArrayList<>();

        for (DhtProto.DhtWrapper wrapper: entries) {

            // make sure that it is a post item
            if (wrapper.getType() == DhtProto.MessageType.KEYWORD) {

                // only include results that are for posts
                DhtProto.KeywordType type = wrapper.getKeyword().getType();
                if (type == TAG || type == TITLE_PARTIAL || type == TITLE_FULL) {
                    postHashes.add(wrapper.getKeyword().getHash());
                }
            }
        }

        ArrayList<Pair<String, DhtProto.Post>> posts = new ArrayList<>();

        // find all the post objects
        for(String hash: postHashes) {

            try {
                posts.add(findPostByHash(hash));
            }
            catch (TooManyResultsException e) {
                e.printStackTrace();
            }
        }

        return posts;
    }

    /**
     * This function will insert the comment object into the local hash table. This will also add
     * record for looking up the comment by the post it is part of.
     *
     * @param comment the comment object that will be added
     * @param postHash the post identifier that the comment is being added to
     * @return the hash  identifying the comment
     */
    @Override
    public String insertComment(@NonNull DhtProto.Comment comment, @NonNull String postHash)
    {
        String hash = Util.generateHash(comment.toByteArray());

        DhtProto.DhtWrapper wrapper = DhtProto.DhtWrapper.newBuilder()
                .setComment(comment)
                .setType(DhtProto.MessageType.COMMENT)
                .build();

        insert(wrapper, hash);

        // the keyword item is empty because it is unused in the search,
        // this is the search item so it can be associates with a post
        DhtProto.DhtWrapper keyword = generateKeyword(null, hash, COMMENT_FOR);
        insert(keyword, postHash);

        return hash;
    }

    /**
     * This function will get all the comments that are associated with the given post.
     * The comments are sorted by the time that they were created.
     *
     * @param postHash he unique SHA256 hash of the post
     * @return the list of results
     */
    @Override
    @NonNull
    public List<Pair<String, DhtProto.Comment>> findCommentsByPost(String postHash)
    {
        ArrayList<DhtProto.DhtWrapper> entries = (ArrayList<DhtProto.DhtWrapper>)hashMap.get(postHash);

        if (entries == null) {
            return new ArrayList<>();
        }
        ArrayList<String> commentHashes = new ArrayList<>();

        for (DhtProto.DhtWrapper wrapper: entries) {

            // make sure that it is a user item
            if (wrapper.getType() == DhtProto.MessageType.KEYWORD) {

                // only include results that are for comments
                DhtProto.KeywordType type = wrapper.getKeyword().getType();
                if (type == COMMENT_FOR) {
                    commentHashes.add(wrapper.getKeyword().getHash());
                }
            }
        }

        ArrayList<Pair<String, DhtProto.Comment>> comments = new ArrayList<>();

        // find all the user objects
        for(String hash: commentHashes) {

            try {
                comments.add(findCommentByHash(hash));
            }
            catch (TooManyResultsException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(comments, new CommentPairComparator());

        return comments;
    }

    /**
     * This will get a comment object for a specific hash. This will throw an exception if
     * more then 1 resulting comment is found with the same hash, this should not ever occur but
     * the exception is there to handle the possibility.
     *
     * @param hash the unique SHA256 hash of the comment
     * @return null if no matching comment is found or the result that contains the comment object
     * @throws TooManyResultsException gets thrown if more then 1 comment is found with the same hash.
     */
    @Override
    @Nullable
    public Pair<String, DhtProto.Comment> findCommentByHash(String hash) throws TooManyResultsException
    {
        ArrayList<DhtProto.DhtWrapper> entries = (ArrayList<DhtProto.DhtWrapper>)hashMap.get(hash);

        if (entries == null) {
            return null;
        }
        ArrayList<DhtProto.Comment> comments = new ArrayList<>();

        for (DhtProto.DhtWrapper wrapper: entries) {

            // make sure that it is a comment item
            if (wrapper.getType() == DhtProto.MessageType.COMMENT) {
                comments.add(wrapper.getComment());
            }
        }

        // make sure that only 1 result was found
        if (comments.size() > 1) {
            throw new TooManyResultsException("Too many results for: " + hash);
        } else if (comments.size() == 1) {
            return new Pair<>(hash, comments.get(0));
        }

        // otherwise there were no results found.
        return null;
    }

    /**
     * Insert a user entry into the hash table along with some indexing records to look up
     * the user. The users hash is currently the hash of the uuid
     *
     * @param user the public user object to store (does not contain the password)
     * @return the hash that is used to identify the user.
     */
    @Override
    public String insertUser(DhtProto.User user)
    {
        if (user == null) {
            return null;
        }

        String hash = Util.generateHash(user.getUuid().getBytes());
        String email = user.getEmail();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String fullName = firstName + " " + lastName;

        DhtProto.DhtWrapper wrapper = generateKeyword(email, hash, EMAIL);
        insert(wrapper, Util.generateHash(email.toLowerCase(Locale.getDefault()).getBytes()));

        // add indexes for the user in the table
        wrapper = generateKeyword(fullName, hash, NAME);
        insert(wrapper, Util.generateHash(firstName.toLowerCase(Locale.getDefault()).getBytes()));

        wrapper = generateKeyword(fullName, hash, NAME);
        insert(wrapper, Util.generateHash(lastName.toLowerCase(Locale.getDefault()).getBytes()));

        wrapper = generateKeyword(fullName, hash, NAME);
        insert(wrapper, Util.generateHash(fullName.toLowerCase(Locale.getDefault()).getBytes()));

        // insert the user item
        wrapper = DhtProto.DhtWrapper.newBuilder()
                .setType(DhtProto.MessageType.USER)
                .setUser(user)
                .build();
        insert(wrapper, hash);

        return hash;
    }

    /**
     * This will get a user object for a specific hash. This will throw an exception if
     * more then 1 resulting user is found with the same hash, this should not ever occur but
     * the exception is there to handle the possibility.
     *
     * @param userHash the unique SHA256 hash identifying the user
     * @return null if no matching user is found or the result that contains the user object
     * @throws TooManyResultsException gets thrown if more then 1 user is found with the same hash.
     */
    @Override
    public Pair<String, DhtProto.User> findUserByHash(String userHash) throws TooManyResultsException
    {
        ArrayList<DhtProto.DhtWrapper> entries = (ArrayList<DhtProto.DhtWrapper>)hashMap.get(userHash);

        if (entries == null) {
            return null;
        }
        ArrayList<DhtProto.User> users = new ArrayList<>();

        for (DhtProto.DhtWrapper wrapper: entries) {

            // make sure that it is a user item
            if (wrapper.getType() == DhtProto.MessageType.USER) {
                users.add(wrapper.getUser());
            }
        }

        // make sure that only 1 result was found
        if (users.size() > 1) {
            throw new TooManyResultsException("Too many results for: " + userHash);
        } else if (users.size() == 1) {
            return new Pair<>(userHash, users.get(0));
        }

        // otherwise there were no results found.
        return null;
    }

    /**
     * This keyword must be a single token to search for, and it must match exactly.
     * The keyword will be normalized to lowercase. It can be a name or email.
     *
     * @param name the keyword to search for.
     * @return the list of results
     */
    @Override
    @Nullable
    public List<Pair<String, DhtProto.User>> findUsersByName(String name)
    {
        if (name == null) {
            return new ArrayList<>();
        }

        name = name.toLowerCase(Locale.getDefault());
        ArrayList<DhtProto.DhtWrapper> entries = (ArrayList<DhtProto.DhtWrapper>)hashMap.get(Util.generateHash(name.getBytes()));

        if (entries == null) {
            return new ArrayList<>();
        }
        ArrayList<String> userHashes = new ArrayList<>();

        for (DhtProto.DhtWrapper wrapper: entries) {

            // make sure that it is a user item
            if (wrapper.getType() == DhtProto.MessageType.KEYWORD) {

                // only include results that are for posts
                DhtProto.KeywordType type = wrapper.getKeyword().getType();
                if (type == EMAIL || type == NAME) {
                    userHashes.add(wrapper.getKeyword().getHash());
                }
            }
        }

        ArrayList<Pair<String, DhtProto.User>> users = new ArrayList<>();

        // find all the user objects
        for(String hash: userHashes) {

            try {
                users.add(findUserByHash(hash));
            }
            catch (TooManyResultsException e) {
                e.printStackTrace();
            }
        }

        return users;
    }

    @Override
    public boolean updateUser(DhtProto.User user)
    {
        return false;
    }



    /**
     * This function will generate a keyword object used for indexing the objects in the table.
     * The keyword is normalized to lowercase to make future searches case insensitive.
     *
     * @param keyword the keyword it is mapping
     * @param dataHash the data it is mapping the key to
     * @param type the type of keyword it is. {@link DhtProto.KeywordType}
     * @return the wraper object to insert into the hashmap
     */
    @NonNull
    private DhtProto.DhtWrapper generateKeyword(@Nullable String keyword, String dataHash, DhtProto.KeywordType type) {

        // according to the protobuf spec, the default value for a string is empty not null
        if (keyword == null) {
            keyword = "";
        } else {
            keyword = keyword.toLowerCase(Locale.getDefault());
        }

        DhtProto.Keyword keywordObj = DhtProto.Keyword.newBuilder()
                .setHash(dataHash)
                .setKeyword(keyword)
                .setType(type)
                .build();

        // create the wrapper object
        return DhtProto.DhtWrapper.newBuilder()
                .setKeyword(keywordObj)
                .setType(DhtProto.MessageType.KEYWORD)
                .build();
    }

    /**
     * This function will insert the record into the local hash table
     * @param data the data to put into the table
     * @param key the key that it will go under
     */
    private void insert(@NonNull  DhtProto.DhtWrapper data, String key) {

        ArrayList<DhtProto.DhtWrapper> entries = (ArrayList<DhtProto.DhtWrapper>)hashMap.get(key);

        if (entries == null) {
            entries = new ArrayList<>();
        }

        entries.add(data);
        hashMap.put(key, entries);
    }

}
