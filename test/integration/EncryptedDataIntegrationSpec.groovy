import grails.test.spock.IntegrationSpec
import org.springframework.orm.hibernate4.HibernateSystemException

class EncryptedDataIntegrationSpec extends IntegrationSpec {

    void "testSave"() {
        setup:
        def cipher = new EncryptedData()
        cipher.id = 'newvalue'
        cipher.password = 'supersecret'
        cipher.decryptedData = "Don't tell anyone"

        expect:
        assert cipher.save()

        when:
        cipher = new EncryptedData()
        // Fails because a password has not been set
        cipher.decryptedData = "New data"

        then:
        thrown(AssertionError)

        when:
        cipher.password = 'newpass'
        cipher.decryptedData = 'New data'
        cipher.save() // Fails because we haven't set an ID

        then:
        thrown(HibernateSystemException)
    }

    void "testPassword"() {
        setup:
        String message = 'Clark Kent is Superman'
        String password = 'Wayne Enterprises'
        def cipher = new EncryptedData()
        cipher.id = 'secret-message'
        cipher.password = password
        cipher.decryptedData = message

        expect:
        assert cipher.save()
        assert cipher.decryptedData, message

        when:
        // Forget the temporary passwords
        cipher.lockDown()
        def str = cipher.decryptedData // Can't get the data without the password

        then:
        thrown(AssertionError)

        when:
        cipher.decrypt('BATMAN!')

        then:
        thrown(AssertionError)

        when:
        cipher.decrypt(password)

        then:
        assert cipher.decryptedData, message

        when:
        // Lock it down before saving it to ensure we're not carrying over any unnecessary data
        cipher.lockDown()

        then:
        assert cipher.save()

        when:
        EncryptedData loaded = EncryptedData.get('secret-message')

        then:
        assert loaded, "Error loading data"
        assert loaded.dataItem, "Encrypted data not found"

        when:
        loaded.decrypt('Selina XoXo')

        then:
        thrown(AssertionError)

        when:
        cipher.decrypt(password)

        then:
        assert loaded.decryptedData, message
    }
}
