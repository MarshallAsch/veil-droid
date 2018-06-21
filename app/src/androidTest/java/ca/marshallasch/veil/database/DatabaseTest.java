package ca.marshallasch.veil.database;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Testing class for the database
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-31
 */
public class DatabaseTest
{

    private Database database;
    @Before
    public void setUp()
    {
        database = Database.getInstance_TETSING(InstrumentationRegistry.getTargetContext());
        database.clear();
    }

    @After
    public void tearDown()
    {
        database.close();
    }

    @Test
    public void blockUser()
    {
        assertTrue(database.blockUser("user_block0", "user_block0_hash"));  // insert once
        assertTrue(database.blockUser("user_block0", "user_block0_hash"));  // insert again
        assertTrue(database.blockUser("user_block1_hash", "123456789012345678901234567890123456"));  // max len field


        assertFalse(database.blockUser("abc", null));  // invalid insert
        assertFalse(database.blockUser(null, null));  // invalid insert
        assertFalse(database.blockUser(null, "abcd"));  // invalid insert

        // make sure there are only 2 entries in the table
        assertEquals(2, database.getCount(BlockContract.BlockEntry.TABLE_NAME, null, null));
    }

    @Test
    public void unblockUser()
    {
        // this is not yet implemented
        assert false;
    }

    @Test
    public void checkBlocked()
    {
        // insert the prereqs
        assertTrue(database.blockUser("user_block2", "user_block2_hash"));  // insert once
        assertTrue(database.blockUser("user_block2", "user_block2_hash"));  // insert again
        assertTrue(database.blockUser("user_block3", "user_block3_hash_max_len_of_36_chars"));  // max len field

        // check the check that they are blocked
        assertTrue(database.checkBlocked("user_block2"));
        assertTrue(database.checkBlocked("user_block3"));
        assertFalse(database.checkBlocked("user_fake_1"));

        assertFalse(database.checkBlocked(null));

    }

    @Test
    public void createUser()
    {
        assertNotNull(database.createUser("Marshall", "Asch", "email_01@email.com", "abc123"));

        //a duplicate user is defined as a duplicate userID, not duplicate usernames
        assertNotNull(database.createUser("Marshall", "Asch", "email_01@email.com", "abc123"));
        assertNotNull(database.createUser("Marshall", "Asch", "email_03@email.com", "abc1234"));
    }

    @Test
    public void login()
    {
        assertNotNull(database.createUser("userFname1", "userLname1", "email_04@email.com", "abc123"));

        assertNotNull(database.login("email_04@email.com", "abc123"));
        assertNull(database.login("email_04@email.com", "*"));      // check some injections
        assertNull(database.login("email_04@email.com", "%"));      // check another one
        assertNull(database.login("non_existant_email_01@email.com", "abc123"));
    }

    @Test
    public void insertKnownPost()
    {
        assertTrue(database.insertKnownPost("123456789012345678901234567890123456", null));
        // insert a duplicate should fail
        assertFalse(database.insertKnownPost("123456789012345678901234567890123456", null));
        //empty comment string should return true
        assertTrue(database.insertKnownPost("HASH_2789012345678901234567890123456", ""));
       //should be able to insert a comment hash if there is also a post hash
        assertTrue(database.insertKnownPost("HASH_2789012345678901234567890123456", "43"));
        //null comment + post hash insert should return false
        assertFalse(database.insertKnownPost(null, null));
        //empty post hash and null comment should return false
        assertFalse(database.insertKnownPost("", null));
        //empty post hash and instantiated comment hash should return false
        assertFalse(database.insertKnownPost("", "123456789012345678901234567890123456"));
        //null post has and instantiated comment hash should return false
        assertFalse(database.insertKnownPost(null, "123456789012345678901234567890123456"));
        //empty comment hash and empty post hash should return false
        assertFalse(database.insertKnownPost("", ""));

        assertEquals(3, database.getCount(KnownPostsContract.KnownPostsEntry.TABLE_NAME));

    }

}