package ca.marshallasch.veil.utilities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.protobuf.Timestamp;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ca.marshallasch.veil.MainActivity;
import ca.marshallasch.veil.R;
import ca.marshallasch.veil.proto.DhtProto;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-13
 */
//@RunWith(RobolectricTestRunner.class)
public class UtilTest
{
    private final DhtProto.User author = DhtProto.User.newBuilder()
            .setFirstName("Marshall")
            .setLastName("Asch")
            .setEmail("maasch@rogers.com")
            .setUuid("userID")
            .setTimestamp(Util.millisToTimestamp(System.currentTimeMillis()))
            .build();

    @Test
    public void timestampToMillis()
    {
        long millis = System.currentTimeMillis();

        Timestamp t = Util.millisToTimestamp(millis);

        Assert.assertEquals(millis, Util.timestampToMillis(t));
    }

    @Test
    public void timestampToDate()
    {
        Date date = new Date();
        Timestamp timestamp = Util.millisToTimestamp(date.getTime());

        Assert.assertEquals(date, Util.timestampToDate(timestamp));

    }


    @Test
    public void createComment()
    {

        DhtProto.Comment comment = Util.createComment("This is the message", author, "POSTHASH");

        Assert.assertNotNull(comment);
        Assert.assertEquals("Marshall Asch", comment.getAuthorName());
        Assert.assertEquals("userID", comment.getAuthorId());
        Assert.assertEquals("This is the message", comment.getMessage());
        Assert.assertEquals(false, comment.getAnonymous());
        Assert.assertEquals("POSTHASH", comment.getPostId());

        // test no post hash set
        comment = Util.createComment("This is the second message", author, "POSTHASH", true);

        Assert.assertNotNull(comment);
        Assert.assertEquals("Marshall Asch", comment.getAuthorName());
        Assert.assertEquals("userID", comment.getAuthorId());
        Assert.assertEquals("This is the second message", comment.getMessage());
        Assert.assertEquals(true, comment.getAnonymous());
        Assert.assertEquals("POSTHASH", comment.getPostId());

        // test set postHash
        comment = Util.createComment("This is the third message", author, "postHash");

        Assert.assertNotNull(comment);
        Assert.assertEquals("Marshall Asch", comment.getAuthorName());
        Assert.assertEquals("userID", comment.getAuthorId());
        Assert.assertEquals("This is the third message", comment.getMessage());
        Assert.assertEquals(false, comment.getAnonymous());
        Assert.assertEquals("postHash", comment.getPostId());

        // test set postHash and anonymous
        comment = Util.createComment("This is the fourth message", author, "postHash", true);

        Assert.assertNotNull(comment);
        Assert.assertEquals("Marshall Asch", comment.getAuthorName());
        Assert.assertEquals("userID", comment.getAuthorId());
        Assert.assertEquals("This is the fourth message", comment.getMessage());
        Assert.assertEquals(true, comment.getAnonymous());
        Assert.assertEquals("postHash", comment.getPostId());


        // annotations say null can not be given for the message or the author
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
  
    @Ignore
    public void rememberUserName()
    {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);


        Util.rememberUserName(activity, "userName", "Password");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        String userName = sharedPref.getString(activity.getString(R.string.pref_username), null);
        String password = sharedPref.getString(activity.getString(R.string.pref_passwords), null);

        Assert.assertEquals("userName", userName);
        Assert.assertEquals("Password", password);
    }

    @Test
    @Ignore
    public void getKnownUsername()
    {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);


        // make sure the shared preferences has a value
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(activity.getString(R.string.pref_username), "UserName1");
        editor.apply();

        String userName = Util.getKnownUsername(activity);

        Assert.assertEquals("UserName1", userName);
    }


    @Test
    @Ignore
    public void getKnownPassword()
    {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);


        // make sure the shared preferences has a value
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(activity.getString(R.string.pref_passwords), "password1");
        editor.apply();

        String password = Util.getKnownPassword(activity);

        Assert.assertEquals("password1", password);
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

    @Ignore
    public void clearKnownUser()
    {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        SharedPreferences.Editor editor = sharedPref.edit();

        // make sure that the user is set
        editor.putString(activity.getString(R.string.pref_username), "userName2");
        editor.putString(activity.getString(R.string.pref_passwords), "password2");
        editor.apply();

        // clear the known user
        Util.clearKnownUser(activity);

        // check the values that are stored
        String userName = sharedPref.getString(activity.getString(R.string.pref_username), null);
        String password = sharedPref.getString(activity.getString(R.string.pref_passwords), null);

        Assert.assertNull(userName);
        Assert.assertNull(password);
    }

    @Test
    public void generateHash(){
        String strTestData = "Test Data";
        String strTestData2 = "Test Data 2";
        byte[] testData = strTestData.getBytes();
        byte[] testData2 = strTestData2.getBytes();

        //Assert that hashing is consistent for same data
        Assert.assertEquals(Util.generateHash(testData), Util.generateHash(testData));
        //Assert that hashing is different for different data
        Assert.assertNotEquals(Util.generateHash(testData), Util.generateHash(testData2));
        //Assert null for null input
        Assert.assertNull(Util.generateHash(null));
        //Assert non-null for empty byte array input
        Assert.assertNotNull(Util.generateHash("".getBytes()));
    }

    @Test
    public void createPost(){
        String title = "Test Title";
        String message = "Test message";
        DhtProto.User testUser = DhtProto.User.newBuilder()
                .setEmail("test@gmail.com")
                .setFirstName("John")
                .setLastName("Doe")
                .setUuid(UUID.randomUUID().toString())
                .build();

        List<String> testTags = new ArrayList<>();
        testTags.add("sample tag 1");
        testTags.add("sample tag 2");

        //create post w/ tags for testing
        DhtProto.Post post = Util.createPost(title, message, testUser, testTags);

        //Assert that post exists
        Assert.assertNotNull(post);
        //Assert that title equals
        Assert.assertEquals(title, post.getTitle());
        //Assert that author names equal
        Assert.assertEquals(testUser.getFirstName() + " " + testUser.getLastName(),post.getAuthorName());
        //Assert that Author UUID equals
        Assert.assertEquals(testUser.getUuid(), post.getAuthorId());
        //Assert that tags counts are equal
        Assert.assertEquals(testTags.size(), post.getTagsCount());
        //Assert that each tag is equal
        for(int i = 0; i < testTags.size(); i++){
            Assert.assertEquals(testTags.get(i),post.getTags(i));
        }
    }


}