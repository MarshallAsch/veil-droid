package ca.marshallasch.veil.comparators;

import java.util.Comparator;

import ca.marshallasch.veil.proto.DhtProto;


/**
 * This class is used to sort {@link DhtProto.Post} objects in alphabetical order by author name.
 * This will sort it in decreasing order, (newest first)
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-07-23
 */
public class PostTitleComparator implements Comparator<DhtProto.Post>
{
    @Override
    public int compare(DhtProto.Post a, DhtProto.Post b)
    {
        String aTitle = a.getTitle();
        String bTitle = b.getTitle();

        return aTitle.compareToIgnoreCase(bTitle);
    }
}
