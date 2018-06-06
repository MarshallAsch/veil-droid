package ca.marshallasch.veil.utilities;

import com.google.protobuf.Timestamp;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-05
 */
public class Util
{

    public static Timestamp millisToTimestamp(long millis) {

        com.google.protobuf.Timestamp timestamp = com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000))
                .build();

        return timestamp;
    }
}
