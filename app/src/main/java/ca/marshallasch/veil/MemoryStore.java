package ca.marshallasch.veil;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ca.marshallasch.veil.exceptions.TooManyResultsException;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-08
 */
public class MemoryStore implements ForumStorage
{
    private HashMap<String, List<DhtProto.DhtWrapper>> hashMap;


    private static MemoryStore instance;
    private static final AtomicInteger openCounter = new AtomicInteger();

    private Context context;

    private MemoryStore(Context c) {

        context = c;
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

    public static synchronized MemoryStore getInstance(final Context c)
    {
        if (instance == null) {
            instance = new MemoryStore(c);
        }
        openCounter.incrementAndGet();
        return instance;
    }

    /**
     * This function will update the saved copy of the hash map to the disk.
     */
    public void close() {

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
            keyword = generateKeyword(tag, hash, DhtProto.KeywordType.TAG);
            insert(keyword, Util.generateHash(tag.getBytes()));
        }

        // add the full text title to the index
        keyword = generateKeyword(title, hash, DhtProto.KeywordType.TITLE_FULL);
        insert(keyword, Util.generateHash(title.getBytes()));

        // tokenize the title and add the tokens
        String[] titleKeywords = title.split(" ");
        for (String tag: titleKeywords) {
            keyword = generateKeyword(tag, hash, DhtProto.KeywordType.TITLE_PARTIAL);
            insert(keyword, Util.generateHash(tag.getBytes()));
        }

        return hash;
    }

    /**
     * This will get a post object for a specific hash. This will throw an exception if
     * more then 1 resulting post is found with the same hash, this should not ever occur but
     * the exception is there to handle the possibility.
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

    @Override
    public List<Pair<String, DhtProto.Post>> findPostsByKeyword(String keyword)
    {
        return null;
    }

    @Override
    public String insertComment(DhtProto.Comment comment, String postHash)
    {
        return null;
    }

    @Override
    public List<Pair<String, DhtProto.Comment>> findCommentsByPost(String postHash)
    {
        return null;
    }

    @Override
    public Pair<String, DhtProto.Comment> findCommentByHash(String hash)
    {
        return null;
    }

    @Override
    public String insertUser(DhtProto.User user)
    {
        return null;
    }

    @Override
    public Pair<String, DhtProto.User> findUserByHash(String userHash)
    {
        return null;
    }

    @Override
    public List<Pair<String, DhtProto.User>> findUsersByName(String name)
    {
        return null;
    }

    @Override
    public boolean updateUser(DhtProto.User user)
    {
        return false;
    }



    /**
     * This function will generate a keyword object used for indexing the objects in the table.
     * @param keyword the keyword it is mapping
     * @param dataHash the data it is mapping the key to
     * @param type the type of keyword it is. {@link DhtProto.KeywordType}
     * @return the wraper object to insert into the hashmap
     */
    private DhtProto.DhtWrapper generateKeyword(String keyword, String dataHash, DhtProto.KeywordType type) {

        String hash = Util.generateHash(keyword.getBytes());

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
