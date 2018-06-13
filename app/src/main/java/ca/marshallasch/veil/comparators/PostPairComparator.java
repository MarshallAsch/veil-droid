package ca.marshallasch.veil.comparators;

import android.support.v4.util.Pair;

import java.util.Comparator;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;


/**
 * This class is used to sort {@link DhtProto.Post} objects by the time that they were created.
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-13
 */
public class PostPairComparator implements Comparator<Pair<String, DhtProto.Post>>
{
    @Override
    public int compare(Pair<String, DhtProto.Post> a, Pair<String, DhtProto.Post> b)
    {
        long aMillis = Util.timestampToMillis(a.second.getTimestamp());
        long bMillis = Util.timestampToMillis(b.second.getTimestamp());
        return (int) (aMillis - bMillis);
    }
}
