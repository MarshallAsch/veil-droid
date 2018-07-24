package ca.marshallasch.veil.comparators;

import java.util.Comparator;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;


/**
 * This class is used to sort {@link DhtProto.Post} objects by the time that they were created.
 * This will sort it in decreasing order, (newest first)
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-13
 */
public class PostAgeComparator implements Comparator<DhtProto.Post>
{
    @Override
    public int compare(DhtProto.Post a, DhtProto.Post b)
    {
        long aMillis = Util.timestampToMillis(a.getTimestamp());
        long bMillis = Util.timestampToMillis(b.getTimestamp());
        return (int) (bMillis - aMillis);
    }
}
