package ca.marshallasch.veil.database;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-31
 */
public class DatabaseTest
{

    private Database database;
    @Before
    public void setUp() throws Exception
    {
        database = Database.getInstance_TETSING(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() throws Exception
    {
        database.close();
    }

    @Test
    public void blockUser()
    {
        assertTrue(database.blockUser("abc", "1234"));  // insert once
        assertTrue(database.blockUser("abc", "1234"));  // insert again
        assertTrue(database.blockUser("maxlen", "123456789012345678901234567890123456"));  // max len field


        assertFalse(database.blockUser("abc", null));  // invalid insert
        assertFalse(database.blockUser(null, null));  // invalid insert
        assertFalse(database.blockUser(null, "abcd"));  // invalid insert
    }


    @Test
    public void unblockUser()
    {
    }

    @Test
    public void checkBlocked()
    {
        assertTrue(database.checkBlocked("abc"));
        assertTrue(database.checkBlocked("maxlen"));
        assertFalse(database.checkBlocked("bob"));

        assertFalse(database.checkBlocked(null));

    }

    @Test
    public void createUser()
    {
        assertNotNull(database.createUser("Marshall", "Asch", "email@email.com", "abc123"));
        assertNotNull(database.createUser("Marshall", "Asch", "email@email.com", "abc123"));
        assertNotNull(database.createUser("Marshall", "Asch", "email2@email.com", "abc1234"));
    }

    @Test
    public void login()
    {
        assertNotNull(database.login("email@email.com", "abc123"));
        assertNotNull(database.login("email2@email.com", "abc1234"));
        assertNull(database.login("email2@email.com", "abc123"));
        assertNull(database.login("email46@email.com", "abc123"));
    }

}