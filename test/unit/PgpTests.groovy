import cr.co.arquetipos.crypto.PGP
import cr.co.arquetipos.password.PasswordTools

class PgpTests extends GroovyTestCase {

    void testInits() {
        // Generates a PGP key pair
        def pgp = PGP.generateKeyPair()

        // Verifies that all values are present
        assert pgp
        assert pgp.keyPair
        assert pgp.publicKey
        assert pgp.privateKey

        assertEquals pgp.keyPair.publicKey, pgp.publicKey
        assertEquals pgp.keyPair.privateKey, pgp.privateKey

        // Obtains the public and private keys, encoded armored-encoded
        String encodedPublic = pgp.encodedPublicKey
        String encodedPrivate = pgp.encodedPrivateKey

        // Creates PGP objects which contain only the public or private keys
        PGP publicOnly = new PGP(encodedPublic, '')
        PGP privateOnly = new PGP('', encodedPrivate)

        // Verifies that the previously encoded public key matches the
        // one from the object we just created with it
        assertEquals encodedPublic, publicOnly.encodedPublicKey

        assert publicOnly
        assert privateOnly
        // Key created from the private key must have the public key as well
        assert privateOnly.encodedPrivateKey
        assert privateOnly.encodedPublicKey

        println "From privateOnly:"
        println publicOnly.encodedPublicKey
        println privateOnly.encodedPublicKey
        println privateOnly.encodedPrivateKey
    }

    /**
        Tests encryption and decryption through various paths.
     */
    void testEncryption(){
        def pgp = PGP.generateKeyPair()

        String encodedPublic = pgp.encodedPublicKey
        String encodedPrivate = pgp.encodedPrivateKey

        PGP publicOnly = new PGP(encodedPublic, '')
        PGP privateOnly = new PGP('', encodedPrivate)

        assert publicOnly
        assert privateOnly
        
        assertEquals encodedPublic, publicOnly.encodedPublicKey

        
        String message = 'Hush hush'
        /* Encrypts the message in three different ways: with the original
           key pair, with the one that only has the public key, and with
           the one that was generated from the private key.  They will later
           be decrypted in several ways to ensure that all are equivalent. */
        String encrypted = pgp.encryptBase64(message)
        String encrypted2 = publicOnly.encryptBase64(message)
        String encrypted3 = privateOnly.encryptBase64(message)

        assert encrypted
        assert encrypted2
        assert encrypted3

        println "Encrypted number one:"
        println encrypted
        println "Encrypted number two:"
        println encrypted2


        String decryptedPrivate = privateOnly.decryptBase64(encrypted)
        assert decryptedPrivate

        String decryptedBoth = pgp.decryptBase64(encrypted2)
        assert decryptedBoth

        assertEquals decryptedPrivate, decryptedBoth
        assertEquals decryptedPrivate, message


        decryptedPrivate = privateOnly.decryptBase64(encrypted2)
        assert decryptedPrivate

        decryptedBoth = pgp.decryptBase64(encrypted)
        assert decryptedBoth

        String decryptedPrivate3 = privateOnly.decryptBase64(encrypted3)
        assert decryptedPrivate3

        String decryptedBoth3 = pgp.decryptBase64(encrypted3)
        assert decryptedBoth3

        assertEquals decryptedPrivate, decryptedBoth
        assertEquals decryptedPrivate, message
        assertEquals decryptedBoth3, message
        assertEquals decryptedPrivate3, message
    }

    /**
        Verifies encryption output to an armored text
     */
    void testArmored() {
        def pgp = PGP.generateKeyPair()

        String encodedPublic = pgp.encodedPublicKey
        String encodedPrivate = pgp.encodedPrivateKey

        PGP publicOnly = new PGP(encodedPublic, '')
        PGP privateOnly = new PGP('', encodedPrivate)

        assert publicOnly
        assert privateOnly

        assertEquals encodedPublic, publicOnly.encodedPublicKey

        String message = 'Hush hush'
        String encrypted = pgp.encryptArmored(message)
        String encrypted2 = pgp.encryptArmored(message)

        println "Armored 1:\n$encrypted\n"
        println "Armored 2:\n$encrypted2\n"
        
    }

    /**
        Verifies decryption behavior when using the wrong keypair.
     */
    void testDecryptionFailure() {
        def pgp = PGP.generateKeyPair()
        def pgp2 = PGP.generateKeyPair()

        String message = 'Hush hush'
        String encrypted = pgp.encryptBase64(message)
        assert encrypted

        String decrypted = pgp2.decryptBase64(encrypted)
        assertEquals decrypted, null
    }
    

    /**
        Tests encryption and decryption with a private key that has an
        assigned passphrase. It also verifies that attempting to decrypt
        said private key without the passphrase (or with a wrong one)
        fails.
      */
    void testPassphrase() {
        def pgp = PGP.generateKeyPair()

        String passphrase = PasswordTools.generateRandomPassword(10)
        assert passphrase
        assertEquals passphrase.size(), 10

        String encodedPublic = pgp.encodedPublicKey
        String encodedPrivate = pgp.getEncodedPrivateKey(passphrase)

        PGP publicOnly = new PGP(encodedPublic, '')
        assert publicOnly

        PGP privateOnly = new PGP('', encodedPrivate, passphrase)
        assert privateOnly

        String message = 'Hush hush'
        /* Encrypts the message in three different ways: with the original
           key pair, with the one that only has the public key, and with
           the one that was generated from the private key.  They will later
           be decrypted in several ways to ensure that all are equivalent. */
        String encrypted = pgp.encryptBase64(message)
        String encrypted2 = publicOnly.encryptBase64(message)
        String encrypted3 = privateOnly.encryptBase64(message)

        assert encrypted
        assert encrypted2
        assert encrypted3

        String decryptedPrivate = privateOnly.decryptBase64(encrypted)
        assert decryptedPrivate

        String decryptedBoth = pgp.decryptBase64(encrypted2)
        assert decryptedBoth

        assertEquals decryptedPrivate, decryptedBoth
        assertEquals decryptedPrivate, message


        /* If so far, so good, then we attempt to re-create the private
           key with a wrong passphrase. */
        privateOnly = null
        shouldFail(org.bouncycastle.openpgp.PGPException) {
            privateOnly = new PGP('', encodedPrivate)
        }

        shouldFail(org.bouncycastle.openpgp.PGPException) {
            privateOnly = new PGP('', encodedPrivate, 'wrongPassphrase')
        }

        

    }
}