package ca.marshallasch.veil.comparators;

import ca.marshallasch.veil.proto.DhtProto;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-19
 */
public class EntryComparator
{

    public static boolean entryEquals(DhtProto.DhtWrapper a, DhtProto.DhtWrapper b) {

        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.getType() != b.getType()) {
            return false;
        }

        switch (a.getType()){
            case POST:
                return a.getPost().getUuid().equals(b.getPost().getUuid());
            case COMMENT:
                return a.getComment().getUuid().equals(b.getComment().getUuid());
            case KEYWORD:
                return a.getKeyword().getHash().equals(b.getKeyword().getHash()) &&
                        a.getKeyword().getKeyword().equals(b.getKeyword().getKeyword());
            case USER:
                return a.getUser().getUuid().equals(b.getUser().getUuid());
            case UNKNOWN:
            default:
        }
        return false;
    }
}
