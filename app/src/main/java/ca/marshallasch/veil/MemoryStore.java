package ca.marshallasch.veil;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ca.marshallasch.veil.proto.DhtProto;

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


    @Override
    public String insertPost(DhtProto.Post post)
    {
        try {
            MessageDigest dm = MessageDigest.getInstance("SHA-256");
            byte[] hash = dm.digest(post.toByteArray());

            String hashStr = dm.toString();
            Log.d("HASH", hashStr);

        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public Pair<String, DhtProto.Post> findPostByHash(String hash)
    {
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
}
