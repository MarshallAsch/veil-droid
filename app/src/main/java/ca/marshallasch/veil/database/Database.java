package ca.marshallasch.veil.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.v4.util.Pair;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import ca.marshallasch.veil.ForumStorage;
import ca.marshallasch.veil.MemoryStore;
import ca.marshallasch.veil.R;
import ca.marshallasch.veil.database.BlockContract.BlockEntry;
import ca.marshallasch.veil.database.NotificationContract.NotificationEntry;
import ca.marshallasch.veil.database.UserContract.UserEntry;
import ca.marshallasch.veil.database.KnownPostsContract.KnownPostsEntry;
import ca.marshallasch.veil.exceptions.TooManyResultsException;
import ca.marshallasch.veil.proto.DhtProto;
import ca.marshallasch.veil.utilities.Util;


/**
 * This is the database helper class, all actions on the database Must go through this.
 * This class is a singleton and must be accessed through {@link #getInstance(Context)}.
 *
 * Note the at SQLite does not impose length restrictions.
 *
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-05-31
 */
public class Database extends SQLiteOpenHelper
{
    private static String DATABASE_NAME = "contentDiscoveryTables";
    private static final int DATABASE_VERSION = 4;

    // this is for the singleton
    private static Database instance = null;
    private static final AtomicInteger openCounter = new AtomicInteger();

    private Context context;

    private Database(final Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * This is used to initiate an instance of the class so only one will ever exist at a time.
     *
     * @param c the applications context
     * @return the existing instance of the database, or creates one and returns that.
     */
    public static synchronized Database getInstance(final Context c)
    {
        if (instance == null) {
            instance = new Database(c.getApplicationContext());
        }
        openCounter.incrementAndGet();
        return instance;
    }

    /**
     * This is used to initiate an instance of the class so only one will ever exist at a time.
     * This is used for integration tests so that if the tests are run on a real device it will
     * use a separate database for testing.
     *
     * @param c the applications context
     * @return the existing instance of the database, or creates one and returns that.
     */
    static synchronized Database getInstance_TETSING(final Context c)
    {
        if (instance == null) {
            DATABASE_NAME += "_TESTING";
            instance = new Database(c.getApplicationContext());
        }
        openCounter.incrementAndGet();
        return instance;
    }

    /**
     * This will make sure that the database is only closed when it is actually finished.
     */
    @Override
    public void close()
    {
        if (openCounter.decrementAndGet() == 0) {
            super.close();
        }
    }

    /**
     * Create all of the tables in the database
     *
     * @param db the underlying database object
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(BlockContract.SQL_CREATE_BLOCK_USERS);
        db.execSQL(NotificationContract.SQL_CREATE_POST_NOTIFICATION);
        db.execSQL(UserContract.SQL_CREATE_USERS);
        db.execSQL(KnownPostsContract.SQL_CREATE_KNOWN_POSTS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1){
            Migrations.upgradeV3(db);
        }
        if (oldVersion < 4) {
            Migrations.upgradeV4(db);
        }
    }

    /**
     * Will add a row to the block user table. If a user is already in the table then it will
     * overwrite the previous entry.
     * <p>
     * Since this calls {@link #getWritableDatabase()}, do not call this from the main thread
     *
     * @param userID   the ID of the user to block, max
     * @param userHash this is the hash of the user for the DHT
     * @return true if the user was successfully added, false if something went wrong.
     */
    @WorkerThread
    public boolean blockUser(@Size(max = 36) String userID, @Size(max = 36) String userHash)
    {
        // make sure params are not null
        if (userHash == null || userID == null) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put(BlockEntry.COLUMN_USER_ID, userID);
        values.put(BlockEntry.COLUMN_USER_HASH, userHash);
        values.put(BlockEntry.COLUMN_TIMESTAMP, System.currentTimeMillis());

        // note this is a potentially long running operation.
        long id = getWritableDatabase().insertWithOnConflict(BlockEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        return id != -1;
    }

    /**
     * Will remove a row from the block user table. If the user was not blocked it will
     * return false.
     * <p>
     * Since this calls {@link #getWritableDatabase()}, do not call this from the main thread
     *
     * @param userID the ID of the user to unblock
     * @return true if 1 user was unblocked otherwise false
     */
    @WorkerThread
    public boolean unblockUser(@Size(max = 36) String userID)
    {
        if (userID == null) {
            return false;
        }

        // Define 'where' part of query.
        String selection = BlockEntry.COLUMN_USER_ID + " = ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = {userID};

        int numDeleted = getWritableDatabase().delete(BlockEntry.TABLE_NAME, selection, selectionArgs);

        // makes sure only 1 row was removed, anything else would be an error
        return numDeleted == 1;
    }

    /**
     * This function will check if a user is blocked
     * Since this calls {@link #getReadableDatabase()}, do not call this from the main thread
     *
     * @param userID the ID of the user to check
     * @return true if the user is blocked false otherwise
     */
    @WorkerThread
    public boolean checkBlocked(@Size(max = 36) String userID)
    {
        if (userID == null) {
            return false;
        }

        // Filter results WHERE userID = 'id'
        String where = BlockEntry.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {userID};

        int count = getCount(BlockEntry.TABLE_NAME, where, whereArgs);
        return count == 1;
    }

    // TODO: 2018-05-31 Create a function that will get the list of all blocked user objects


    /**
     * Register a to receive notifications of new posts from a user or for comments about a specific
     * post.
     *
     * Since this calls {@link #getWritableDatabase()}, do not call this from the main thread
     * @param hash the hash of the post or user in the DHT
     * @return true if it was successfully registered, false otherwise
     */
    @WorkerThread
    public boolean registerForNotification(@Size(max = 36) String hash) {
        if (hash == null) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put(NotificationEntry.COLUMN_HASH, hash);
        values.put(NotificationEntry.COLUMN_TIMESTAMP, System.currentTimeMillis());

        // note this is a potentially long running operation.
        long id = getWritableDatabase().insertWithOnConflict(NotificationEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        return id != -1;
    }

    /**
     * Unregister a to receive notifications of new posts from a user or for comments about a
     * specific post.
     *
     * Since this calls {@link #getWritableDatabase()}, do not call this from the main thread
     * @param hash the hash of the post or user in the DHT
     * @return true if it was successfully unregistered, false otherwise
     */
    @WorkerThread
    public boolean unregisterForNotification(@Size(max = 36) String hash) {

        if (hash == null) {
            return false;
        }

        // Define 'where' part of query.
        String selection = NotificationEntry.COLUMN_HASH + " = ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = {hash};

        int numDeleted = getWritableDatabase().delete(NotificationEntry.TABLE_NAME, selection, selectionArgs);

        // makes sure only 1 row was removed, anything else would be an error
        return numDeleted == 1;
    }

    /**
     * Check if it should give a notification for the new post or comment, allows you to specify
     * that you want to receive notifications for posts by specific users, or comments on a specific
     * post.
     *
     * Since this calls {@link #getReadableDatabase()}, do not call this from the main thread
     * @param hash the hash that is used to identify the user or post in the DHT
     * @return true if a notification should be given otherwise false
     */
    @WorkerThread
    public boolean checkGiveNotification(@Size(max = 36) String hash) {

        if (hash == null) {
            return false;
        }

        // check if notifications have been temporarily muted
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int status = preferences.getInt(context.getString(R.string.pref_notifications), 0);

        if (status == 1) {
            return true;
        } else if (status == 2) {
            return false;
        }

        // Filter results WHERE userHash = 'hash'
        String where = NotificationEntry.COLUMN_HASH + " = ?";
        String[] whereArgs = {hash};

        int count = getCount(NotificationEntry.TABLE_NAME, where, whereArgs);
        return count == 1;
    }

    /**
     * This will add a entry to the table to make it easier for look up later.
     * Since this calls {@link #getWritableDatabase()}, do not call this from the main thread
     *
     * @param posthash The SHA256 hash of the post object
     * @param commentHash the SHA256 hash of the comment object
     * @return
     */
    @WorkerThread
    public boolean insertKnownPost(@Size(max = 36) String posthash, @Nullable @Size(max = 36) String commentHash) {

        // the post hash has to be set but the comment hash can be null, if the post hash is empty return false too.
        if (posthash == null || posthash.isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put(KnownPostsEntry.COLUMN_POST_HASH, posthash);
        values.put(KnownPostsEntry.COLUMN_COMMENT_HASH, commentHash);

        // note this is a potentially long running operation.
        long id = getWritableDatabase().insertWithOnConflict(KnownPostsEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        return id != -1;
    }

    // TODO: 2018-06-04 Create accessors to get the information for different content types.
    // these functions may be used just to get ID's or they may delegate the the DHT to get the
    // actual content.

    /**
     * This will create a new user account and will assign them a new UUID that is
     * <a href="https://tools.ietf.org/html/rfc4122">RFC 4122</a> type 4 compliant.
     *
     * Since this calls {@link #getWritableDatabase()}, do not call this from the main thread.
     *
     * @see <a href="https://tools.ietf.org/html/rfc4122">RFC 4122</a>
     *
     * @param firstName the users first name
     * @param lastName the users last name
     * @param email the users email address (also their username
     * @param password the plain text password of the user
     * @return null on failure and the  {@link DhtProto.User} object on success
     */
    @WorkerThread
    public DhtProto.User createUser(String firstName, String lastName, String email, String password) {

        Date createdAt = new Date();
        UUID uuid = UUID.randomUUID();
        String passHash =  BCrypt.hashpw(password, BCrypt.gensalt(10));

        com.google.protobuf.Timestamp time = Util.millisToTimestamp(createdAt.getTime());

        // this is the object that would get sent to the DHT
        DhtProto.User user = DhtProto.User.newBuilder()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setUuid(uuid.toString())
                .setTimestamp(time)
                .build();

        ContentValues values = new ContentValues();

        values.put(UserEntry.COLUMN_EMAIL_ADDRESS, email);
        values.put(UserEntry.COLUMN_ID, uuid.toString());
        values.put(UserEntry.COLUMN_FIRST_NAME, firstName);
        values.put(UserEntry.COLUMN_LAST_NAME, lastName);
        values.put(UserEntry.COLUMN_PASSWORD, passHash);
        values.put(UserEntry.COLUMN_TIMESTAMP, createdAt.getTime());

        // note this is a potentially long running operation.
        long id = getWritableDatabase().insertWithOnConflict(UserEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // check if the creation was successful, return the user if it was
        return id != -1 ? user : null;
    }

    /**
     * This function will check if a user is able to login to this device. Note that if the email
     * address is associated with multiple accounts then the login will authenticate them on the
     * first account that it finds with a matching username + password combination.
     *
     * Since this calls {@link #getReadableDatabase()}, do not call this from the main thread
     * @param email the users email address for the account
     * @param password the users plain text password for the account
     * @return {@link DhtProto.User} object that contains all the account information for the user.
     */
    @WorkerThread
    public DhtProto.User login(String email, String password) {

        String[] projection = {
                UserEntry.COLUMN_ID,
                UserEntry.COLUMN_EMAIL_ADDRESS,
                UserEntry.COLUMN_FIRST_NAME,
                UserEntry.COLUMN_LAST_NAME,
                UserEntry.COLUMN_PASSWORD,
                UserEntry.COLUMN_TIMESTAMP
        };

        // Filter results WHERE "email address" = 'My email'
        String selection = UserEntry.COLUMN_EMAIL_ADDRESS + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = getReadableDatabase().query(
                UserEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                null           // don't sort the rows
        );

        DhtProto.User user = null;

        // check each of the accounts that have the same email address.
        while(cursor.moveToNext()) {
            String userID = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_ID));
            String passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_PASSWORD));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_LAST_NAME));

            long millis = cursor.getLong(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_TIMESTAMP));
            com.google.protobuf.Timestamp time = Util.millisToTimestamp(millis);

            // check if the passwords match, only until first match is found
            if (BCrypt.checkpw(password, passwordHash)) {
                user = DhtProto.User.newBuilder()
                        .setEmail(email)
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setUuid(userID)
                        .setTimestamp(time)
                        .build();
                break;
            }
        }

        cursor.close();

        return user;
    }

    // TODO: 2018-06-05 Add functions to export the user profile
    // TODO: 2018-06-05 Add function to import a user profile
    // TODO: 2018-06-05 Add A function to update the account information


    @WorkerThread
    public List<DhtProto.Post> getAllPosts() {

        String[] projection = {
                KnownPostsEntry.COLUMN_POST_HASH
        };

        Cursor cursor = getReadableDatabase().query(
                true,
                KnownPostsEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                null,           // don't sort
                null                // no limit to the results
        );

        Pair<String, DhtProto.Post> postPair;
        ForumStorage dataStore = MemoryStore.getInstance(context);
        List<DhtProto.Post> posts = new ArrayList<>();

        // get each post that is in the list
        while(cursor.moveToNext()) {
            String hash = cursor.getString(cursor.getColumnIndexOrThrow(KnownPostsEntry.COLUMN_POST_HASH));

            postPair = null;
            try {
                postPair = dataStore.findPostByHash(hash);
            }
            catch (TooManyResultsException e) {
                e.printStackTrace();
            }

            // add the post to the list
            if (postPair != null) {
                posts.add(postPair.second);
            }
        }
        cursor.close();

        return posts;
    }

    /**
     * Count the number of matching rows in the table
     * Since this calls {@link #getReadableDatabase()}, do not call this from the main thread
     * @param tableName The name of the table to get the count for
     * @param where the conditions that it is counting
     * @param whereArgs the args
     * @return the number of matching rows
     */
    @WorkerThread
    public int getCount(@NonNull String tableName, @Nullable String where, @Nullable String[] whereArgs) {

        // what is being selected
        String[] selection = {"COUNT(*)"};

        Cursor c = getReadableDatabase().query(
                BlockEntry.TABLE_NAME,   // The table to query
                selection,               // The array of columns to return (pass null to get all)
                where,                   // The columns for the WHERE clause
                whereArgs,               // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // don't sort results
        );

        // get the count, if the count is missing then set it to 0
        int count = c.moveToFirst() ? c.getInt(0) : 0;

        c.close();
        return count;
    }

    /**
     *
     * This is a convenience method for {@link #getCount(String, String, String[])} that will count
     * all rows in the table.
     *
     * Since this calls {@link #getReadableDatabase()}, do not call this from the main thread
     * @param tableName The name of the table to get the count for
     * @return the number of matching rows
     */
    @WorkerThread
    public int getCount(@NonNull String tableName) {
        return getCount(tableName, null, null);
    }
}
