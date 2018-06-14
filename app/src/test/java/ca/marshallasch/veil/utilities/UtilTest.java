package ca.marshallasch.veil.utilities;

import com.google.protobuf.Timestamp;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-13
 */
public class UtilTest
{

    @Test
    public void timestampToMillis()
    {
        long millis = System.currentTimeMillis();

        Timestamp t = Util.millisToTimestamp(millis);

        Assert.assertEquals(millis, Util.timestampToMillis(t));

    }
}