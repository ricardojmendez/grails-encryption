import cr.co.arquetipos.password.PasswordTools

class PasswordToolsTests extends GroovyTestCase {

    void testSalt() {
        int size = 4
        def salts = []
        100.times {
            String salt = PasswordTools.generateRandomPassword(size)
            assertEquals salts.indexOf(salt), -1
            salts << salt
        }
        println salts
        assertEquals salts.size(), 100
        salts.each { salt ->
            assertEquals salt.size(), size
        }

        size = 20
        salts = []
        10.times {
            salts << PasswordTools.generateSalt(size)
        }
        println salts
        assertEquals salts.size(), 10
        salts.each { salt ->
            assertEquals salt.size(), size
        }
    }

    void testSaltedPasswordHex() {
        String password = 'superSecret'
        String salted = PasswordTools.saltPasswordHex(password)
        String again = PasswordTools.saltPasswordHex(password)

        assert salted != again

        assert PasswordTools.checkDigestHex(password, salted)
        assert PasswordTools.checkDigestHex(password, again)
        assert !PasswordTools.checkDigestHex('SuperSecret', salted)

        def hashes = []
        100.times {
            // Verify we don't get duplicated hashes
            salted = PasswordTools.saltPasswordHex(password)
            assertEquals hashes.indexOf(salted), -1
            hashes << salted
        }

        hashes.each { hash ->
            assert PasswordTools.checkDigestHex(password, hash)
            println "Verified $hash"
        }

        hashes.each { hash ->
            assert !PasswordTools.checkDigestHex("SuperSecret", hash)
        }
    }

    void testSaltedPasswordBase64() {
        String password = 'superSecret'
        String salted = PasswordTools.saltPasswordBase64(password)
        String again = PasswordTools.saltPasswordBase64(password)

        assert salted != again

        assert PasswordTools.checkDigestBase64(password, salted)
        assert PasswordTools.checkDigestBase64(password, again)
        assert !PasswordTools.checkDigestBase64('SuperSecret', salted)

        def hashes = []
        100.times {
            // Verify we don't get duplicated hashes
            salted = PasswordTools.saltPasswordBase64(password)
            assertEquals hashes.indexOf(salted), -1
            hashes << salted
        }

        hashes.each { hash ->
            assert PasswordTools.checkDigestBase64(password, hash)
            println "Verified $hash"
        }

        hashes.each { hash ->
            assert !PasswordTools.checkDigestBase64("SuperSecret", hash)
        }
    }

}