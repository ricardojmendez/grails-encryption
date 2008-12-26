package cr.co.arquetipos.password
import org.apache.commons.codec.binary.Hex

import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

// Some functions came originally from http://www.securitydocs.com/library/3439
class PasswordTools {
    private static String allowedCharacters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@,;.-=+'
	private static String loginCharacters = '0123456789abcdefghijklmnopqrstuvwxyz.'
	private static codec = new Hex()

    static String generateRandomPassword(int size=10, allowed = allowedCharacters) {
        SecureRandom random = new SecureRandom()

        StringBuilder sb = new StringBuilder()
        size.times() {
            int rand = random.nextInt(allowed.size())
            sb << allowed[rand-1]
        }
        return sb.toString()
    }

	/**
	 * Returns a random string made up only of numbers, letters and the
	 * period character
	 */
	static String generateRandomLogin(int size=10) {
		return generateRandomPassword(size, loginCharacters)
	}

	static byte[] generateSalt(int size=4) {
        SecureRandom random = new SecureRandom()
        def list = []
        size.times() {
            list << random.nextInt()
        }
        byte[] array = list
        return array
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
        byte[] b = new byte[left.length + right.length];
        System.arraycopy(left, 0, b, 0, left.length);
        System.arraycopy(right, 0, b, left.length, right.length);
        return b;
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
        byte[] l, r;
        if (src == null || src.length <= n) {
            l = src;
            r = new byte[0];
        } else {
            l = new byte[n];
            r = new byte[src.length - n];
            System.arraycopy(src, 0, l, 0, n);
            System.arraycopy(src, n, r, 0, r.length);
        }
        byte[][] lr = [l , r];
        return lr;
    }



    /**
    * SHA-256 a password and a random salt.
    *
    * @param password
    *      String to hash
    * @return String Base64-encoded byte array concatenating the 32-byte hash and the salt
    *
    */
    static byte[] saltPassword(String password) {
        byte[] salt = generateSalt(4)
        byte[] pwdBytes = password.getBytes()
        byte[] hash = DigestUtils.sha256(concatenate(pwdBytes, salt))

        return concatenate(hash, salt)
    }

    static String saltPasswordBase64(String password) {
        String encoded = saltPassword(password).encodeBase64()
        return encoded
    }

    static String saltPasswordHex(String password) {
        String encoded = new String(codec.encode(saltPassword(password)))
        return encoded
    }

    static boolean checkDigestHex(String password, String digestHex) {
        byte[] digest = codec.decode(digestHex.bytes)
        return checkDigest(password, digest)
    }

    static boolean checkDigestBase64(String password, String digestBase64) {
        byte[] digest = digestBase64.decodeBase64()
        return checkDigest(password, digest)
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
        boolean valid = true


        // First section will contain the original hash, the next the salt
        // SHA-256 hashes are 32-bytes in length
        byte[][] hs = split(digest, 32);
		byte[] hash = hs[0];
		byte[] salt = hs[1];

		// Update digest object with byte array of clear text string and salt
        byte[] concat = concatenate(password.getBytes(), salt)

		// Complete hash computation, this is now binary data
		byte[] pwhash = DigestUtils.sha256(concat)

        valid = (hash == pwhash)

        return valid;
    }


}