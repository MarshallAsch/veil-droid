package ca.marshallasch.veil.utilities;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.protobuf.Timestamp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import ca.marshallasch.veil.proto.DhtProto;

/**
 * This class is for application wide static utility functions to help reduce the amount of repeated
 * code.
 * 
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-05
 */
public class Util
{
    // this class can not be instantiated
    private Util() {}

    /**
     * This utility function will convert the number of milliseconds since the epoch,
     * (January 1, 1970, 00:00:00 GMT) to a Protobuf Timestamp object.
     *
     * @param millis the number of milliseconds
     * @return the created timestamp object
     */
    @NonNull
    public static Timestamp millisToTimestamp(long millis) {

        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000))
                .build();
    }

    public static long timestampToMillis(@NonNull Timestamp time) {

        long seconds = time.getSeconds();
        long nanos = time.getNanos();

        return seconds * 1000 + (nanos / 1000000);
    }


    /**
     * Creates a {@link Date} item from the {@link Timestamp} object.
     * @param timestamp the timestamp to convert
     * @return a new Date object with the same time.
     */
    @NonNull
    public static Date timestampToDate (@NonNull Timestamp timestamp) {

        long seconds = timestamp.getSeconds();
        long nanos = timestamp.getNanos();
        long millis = seconds * 1000 + (nanos / 1000000);

        return new Date(millis);
    }

    /**
     * Hides Android's soft keyboard.
     *
     * @param view referring to the root view of the layout
     */
    public static void hideKeyboard(View view, @NonNull  Activity activity) {
        InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(in != null){
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Generates a SHA256 hash of the data. the hsh digest is returned as a string.
     *
     * Example:
     * "e4896ed08d7620 3c2266092 2487c 296304956d863f462 c34c34de5a625a9"
     * @param data the data to hash
     * @return the hash on success, null on failure.
     */
    @Nullable
    public static String generateHash(byte[] data) {

        byte[] hash;
        StringBuilder hashStr = new StringBuilder();
        try {
            MessageDigest dm = MessageDigest.getInstance("SHA-256");
            hash = dm.digest(data);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        // convert the hash byte array to a string
        for (byte b : hash) {
            hashStr.append(String.format("%2x", b));
        }

        return  hashStr.toString();
    }


    /**
     * This will create a new post object with the <code>UUID</code> field set to the hash of the
     * object. To verify the the hash of the object, remove the uuid field then hash the post.
     *
     * @param title string title of the post
     * @param message the message body of the post
     * @param author the {@link ca.marshallasch.veil.proto.DhtProto.User} object
     * @param tags a list of tag strings
     * @return a post object
     */
    public static DhtProto.Post createPost(String title, String message, @NonNull DhtProto.User author, @Nullable List<String> tags) {

        DhtProto.Post.Builder postBuilder = DhtProto.Post.newBuilder();

        // set attributes
        postBuilder.setTitle(title);
        postBuilder.setMessage(message);
        postBuilder.setAuthorName(author.getFirstName() + " " + author.getLastName());
        postBuilder.setAuthorId(author.getUuid());
        postBuilder.setTimestamp(millisToTimestamp(System.currentTimeMillis()));

        // add the tags if there are any
        if (tags != null) {
            postBuilder.addAllTags(tags);
        }

        DhtProto.Post post = postBuilder.build();

        String hash = generateHash(post.toByteArray());

        post = DhtProto.Post.newBuilder(post)
                .setUuid(hash)
                .build();

        return post;

    }
}
