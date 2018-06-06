package ca.marshallasch.veil.utilities;

import android.support.annotation.NonNull;
import com.google.protobuf.Timestamp;

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
}
