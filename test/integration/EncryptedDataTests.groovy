class EncryptedDataTests extends GroovyTestCase {

    void testSave() {
        def cipher = new EncryptedData()
        cipher.id = 'newvalue'
        cipher.password = 'supersecret'
        cipher.decryptedData = "Don't tell anyone"

        assert cipher.save()

        cipher = new EncryptedData()
        shouldFail()
        {
            // Fails because a password has not been set
            cipher.decryptedData = "New data"
        }

        shouldFail(org.springframework.orm.hibernate4.HibernateSystemException)
        {
            cipher.password = 'newpass'
            cipher.decryptedData = 'New data'
            assert cipher.save() // Fails because we haven't set an ID
        }
    }
   


    void testPassword() {
        String message = 'Clark Kent is Superman'
        String password = 'Wayne Enterprises'
        def cipher = new EncryptedData()
        cipher.id = 'secret-message'
        cipher.password = password
        cipher.decryptedData = message
        assert cipher.save()

        assertEquals cipher.decryptedData, message

        // Forget the temporary passwords
        cipher.lockDown()

        shouldFail(AssertionError)
        {
            def str = cipher.decryptedData // Can't get the data without the password
        }

        shouldFail(AssertionError)
        {
            cipher.decrypt('BATMAN!')    
        }

        cipher.decrypt(password)
        assertEquals cipher.decryptedData, message
        // Lock it down before saving it to ensure we're not carrying over any unnecessary data
        cipher.lockDown()
        assert cipher.save()

        EncryptedData loaded = EncryptedData.get('secret-message')
        assert loaded, "Error loading data"
        assert loaded.dataItem, "Encrypted data not found"

        shouldFail(AssertionError)
        {
            loaded.decrypt('Selina XoXo')
        }
        cipher.decrypt(password)
        assertEquals loaded.decryptedData, message
    }
}
