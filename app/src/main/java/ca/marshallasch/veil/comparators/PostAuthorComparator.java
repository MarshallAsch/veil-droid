package ca.marshallasch.veil.comparators;

import java.util.Comparator;

import ca.marshallasch.veil.proto.DhtProto;

/**
 * This class is used to sort {@link DhtProto.Post} objects by their title in alphabetical order.
 * This will sort it in decreasing order, (newest first)
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-07-23
 */
public class PostAuthorComparator implements Comparator<DhtProto.Post>
{
    @Override
    public int compare(DhtProto.Post a, DhtProto.Post b)
    {
        String aAuthor = a.getAuthorName();
        String bAuthor = b.getAuthorName();

        return aAuthor.compareToIgnoreCase(bAuthor);
    }
}
