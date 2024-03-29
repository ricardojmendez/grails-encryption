package cr.co.arquetipos.password

import grails.util.Holders
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils

import java.security.SecureRandom

/**
 * Encapsulates several password-related utility functions
 * Some functions came originally from http://www.securitydocs.com/library/3439
 */
class PasswordTools {

    private static String allowedCharacters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@,;.-=+'
    private static String loginCharacters = '0123456789abcdefghijklmnopqrstuvwxyz.'
    private static codec = new Hex()
    private static final boolean HAS_SHA256 = Holders.config?.crypto?.useSha256 ?: false
    static {
        if (HAS_SHA256) {
            try {
                DigestUtils.sha256("foo")
            } catch (Throwable t) {
                NoSuchMethodError e = new NoSuchMethodError("DigestUtils#sha256 error: you probably have 'crypto.useSha256' but a pre-1.4 commons-codec being used. Be sure to check your classpath for duplicates")
                e.initCause(t)
                throw e
            }
        }
    }

    /**
     * Generates a random password out of a alowed characters
     * @param size Password size
     * @param allowed String comprising the allowed characters
     * @returns Random password
     */
    static String generateRandomPassword(int size = 10, allowed = allowedCharacters) {
        SecureRandom random = new SecureRandom()

        StringBuilder sb = new StringBuilder()
        size.times() {
            int rand = random.nextInt(allowed.size())
            sb << allowed[rand - 1]
        }
        return sb.toString()
    }

    /**
     * Returns a random string made up only of numbers, letters and the
     * period character
     */
    static String generateRandomLogin(int size = 10) {
        return generateRandomPassword(size, loginCharacters)
    }

    /**
     * Generates a random salt of a certain size
     * @param size How many bytes should be in the salt
     */
    static byte[] generateSalt(int size = 4) {
        SecureRandom random = new SecureRandom()
        def list = []
        size.times() {
            list << random.nextInt()
        }
        return list
    }

    /**
     * Combine two byte arrays
     *
     * @param l
     *      first byte array
     * @param r
     *      second byte array
     * @return byte[] combined byte array
     */
    private static byte[] concatenate(byte[] left, byte[] right) {
        byte[] b = new byte[left.length + right.length]
        System.arraycopy(left, 0, b, 0, left.length)
        System.arraycopy(right, 0, b, left.length, right.length)
        return b
    }

    /**
     * split a byte array in two
     *
     * @param src
     *      byte array to be split
     * @param n
     *      element at which to split the byte array
     * @return byte[][] two byte arrays that have been split
     */
    private static byte[][] split(byte[] src, int n) {
        byte[] l, r
        if (src == null || src.length <= n) {
            l = src
            r = new byte[0]
        } else {
            l = new byte[n]
            r = new byte[src.length - n]
            System.arraycopy(src, 0, l, 0, n)
            System.arraycopy(src, n, r, 0, r.length)
        }
        return [l, r]
    }

    /**
     * SHA password and a random salt.
     *
     * @param password
     *      String to hash
     * @return String Base64-encoded byte array concatenating the 32-byte hash and the salt
     *
     */
    static byte[] saltPassword(String password) {
        byte[] salt = generateSalt(4)
        byte[] pwdBytes = password.getBytes()
        byte[] hash = HAS_SHA256 ?
                DigestUtils.sha256(concatenate(pwdBytes, salt)) :
                DigestUtils.sha(concatenate(pwdBytes, salt))
        return concatenate(hash, salt)
    }

    /**
     * Returns a salted password base64 encoded.
     * @see PasswordTools#saltPassword(String)
     */
    static String saltPasswordBase64(String password) {
        return saltPassword(password).encodeBase64()
    }

    /**
     * Returns a salted password hex-encoded
     * @see PasswordTools#saltPassword(String)
     */
    static String saltPasswordHex(String password) {
        return new String(codec.encode(saltPassword(password)))
    }

    /**
     * Verifies a password against a hex-encoded digest
     * @see PasswordTools#checkDigest(String, byte [ ])
     */
    static boolean checkDigestHex(String password, String digestHex) {
        return checkDigest(password, codec.decode(digestHex.bytes))
    }

    /**
     * Verifies a password against a base64-encoded digest
     * @see PasswordTools#checkDigest(String, byte [ ])
     */
    static boolean checkDigestBase64(String password, String digestBase64) {
        return checkDigest(password, digestBase64.decodeBase64())
    }

    /**
     * Verifies a password against a digest
     *
     * @param password
     *      Value to verify
     * @param digest
     *      byte array concatenating the 32-byte hash and the salt
     * @return boolean True if successful
     *
     */
    static boolean checkDigest(String password, byte[] digest) {
        // First section will contain the original hash, the next the salt
        // SHA-1 hashes are 20-bytes in length
        // SHA-256 hashes are 32-bytes in length
        byte[][] hs = split(digest, HAS_SHA256 ? 32 : 20)

        // Update digest object with byte array of clear text string and salt
        byte[] concat = concatenate(password.getBytes(), hs[1])

        // Complete hash computation, this is now binary data
        byte[] pwhash = HAS_SHA256 ? DigestUtils.sha256(concat) : DigestUtils.sha(concat)
        return (hs[0] == pwhash)
    }
}