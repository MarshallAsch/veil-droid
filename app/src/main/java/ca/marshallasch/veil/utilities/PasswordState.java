package ca.marshallasch.veil.utilities;

/**
 * This is returned from {@link Util#checkPasswords(String, String)} to determine what was wrong
 * with the password
 */
public enum PasswordState
{
    TOO_SIMPLE,
    MISMATCH,
    MISSING,
    GOOD
}