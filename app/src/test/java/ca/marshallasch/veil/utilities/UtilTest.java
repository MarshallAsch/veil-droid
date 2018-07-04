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


    @Test
    public void checkPasswords()
    {
        String pass1 = "password";
        String pass2 = "Password";
        String pass3 = "PASSWORD";
        String pass6 = "P@ssword1";
        String pass7 = "PASSWORd1!";

        Assert.assertEquals(PasswordState.TOO_SIMPLE, Util.checkPasswords(pass1, pass1));
        Assert.assertEquals(PasswordState.TOO_SIMPLE, Util.checkPasswords(pass2, pass2));
        Assert.assertEquals(PasswordState.TOO_SIMPLE, Util.checkPasswords(pass3, pass3));

        Assert.assertEquals(PasswordState.MISSING, Util.checkPasswords("", ""));
        Assert.assertEquals(PasswordState.MISSING, Util.checkPasswords("", pass1));

        Assert.assertEquals(PasswordState.MISMATCH, Util.checkPasswords(pass1, pass2));
        Assert.assertEquals(PasswordState.MISMATCH, Util.checkPasswords(pass6, pass7));

        Assert.assertEquals(PasswordState.GOOD, Util.checkPasswords(pass7, pass7));
        Assert.assertEquals(PasswordState.GOOD, Util.checkPasswords(pass6, pass6));
    }


    @Test
    public void checkEmail()
    {
        Assert.assertTrue(Util.checkEmail("A@b.c"));
        Assert.assertFalse(Util.checkEmail(null));
        Assert.assertFalse(Util.checkEmail(""));
        Assert.assertFalse(Util.checkEmail("email address"));
        Assert.assertFalse(Util.checkEmail("email address@com.ca"));
        Assert.assertFalse(Util.checkEmail("emailaddress@com"));
        Assert.assertTrue(Util.checkEmail("email_address@com.ca.net"));
        Assert.assertFalse(Util.checkEmail("email@address@com.ca"));
    }
}