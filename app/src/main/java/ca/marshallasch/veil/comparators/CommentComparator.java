package ca.marshallasch.veil.comparators;

import java.util.Comparator;

import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;


/**
 * This class is used to sort {@link DhtProto.Comment} objects by the time that they were created.
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-13
 */
public class CommentComparator implements Comparator<DhtProto.Comment>
{
    @Override
    public int compare(DhtProto.Comment a, DhtProto.Comment b)
    {
        long aMillis = Util.timestampToMillis(a.getTimestamp());
        long bMillis = Util.timestampToMillis(b.getTimestamp());
        return (int) (aMillis - bMillis);
    }
}
