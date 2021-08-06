package cr.co.arquetipos

import cr.co.arquetipos.crypto.Blowfish
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class BlowfishSpec extends Specification implements GrailsUnitTest {
    void testEncryption() {
        String message = 'Hush hush'
        String password = 'DeVitto'
        String badPassword = 'Danny'

        // Encrypts the message as a byte array
        byte[] encrypted = Blowfish.encrypt(message.getBytes(), password)
        assert encrypted

        // Decrypts with the correct password, and compares
        def decrypted = Blowfish.decrypt(encrypted, password)
        assert decrypted
        String decryptedString = new String(decrypted)

        // Decrypted strings may contain trailing char(0)s
        decryptedString = decryptedString.trim()

        println "$decryptedString ${decryptedString.getBytes()}"
        println "$message ${message.getBytes()}"

        assertEquals decryptedString, message

        // Decrypting with a bad password returns null
        decrypted = Blowfish.decrypt(encrypted, badPassword)
        assert decrypted == null
    }

    void testEncryptionBase64() {
        String message = 'Hush hush'
        String password = 'DeVitto'
        String badPassword = 'Danny'

        // Encrypts a message and returns it base64-encoded
        String encrypted = Blowfish.encryptBase64(message, password)
        assert encrypted
        String decrypted = Blowfish.decryptBase64(encrypted, password)
        assert decrypted

        assertEquals decrypted, message

        // There's an option to not trim the return value, even though the
        // default is to do so. In that case, the decrypted value may not
        // exactly match the original message
        decrypted = Blowfish.decryptBase64(encrypted, password, false)
        assert decrypted != message
        assertEquals decrypted.trim(), message

        println "The following two rows should be different:"
        println "$decrypted ${decrypted.getBytes()}"
        println "$message ${message.getBytes()}"


        // Decrypting with an invalid password returns null
        decrypted = Blowfish.decryptBase64(encrypted, badPassword)
        assertEquals decrypted, null
    }
}