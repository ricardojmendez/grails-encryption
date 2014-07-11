import grails.test.spock.IntegrationSpec

class SimplePasswordManagerServiceIntegrationSpec extends IntegrationSpec {

    def simplePasswordManagerService

    void "testStore"() {
        setup:
        // Store a single item, test
        assert simplePasswordManagerService

        when:
        EncryptedData item = simplePasswordManagerService.store("aKey", "aValue", "hush")

        then:
        assert item
    }

    void "testRetrieve"() {
        setup:
        assert simplePasswordManagerService

        when:
        simplePasswordManagerService.store("aKey", "aValue", "hush")
        def item = simplePasswordManagerService.retrieve("aKey", "hush")

        then:
        assert item
    }

    void "testCache"() {
        setup:
        assert simplePasswordManagerService

        when:
        simplePasswordManagerService.store("aKey", "aValue", "hush")
        // Retrieving without a password
        def decrypted = simplePasswordManagerService.retrieve("aKey")

        then:
        assert decrypted
    }

    void "testExpiration"() {
        setup:
        assert simplePasswordManagerService

        when:
        simplePasswordManagerService.store("aKey", "aValue", "hush")
        // Retrieving without a password
        def decrypted = simplePasswordManagerService.retrieve("aKey")

        then:
        assert decrypted

        when:
        simplePasswordManagerService.cacheDuration = -1 // Disable cache
        // Fails because we're not allowing it to cache the key
        decrypted = simplePasswordManagerService.retrieve("aKey")

        then:
        thrown(AssertionError)

        when:
        simplePasswordManagerService.cacheDuration = 5
        decrypted = simplePasswordManagerService.retrieve("aKey", "hush")

        then:
        assert decrypted, "aValue"

        when:
        // Check that the item is still cached
        String secondTry = simplePasswordManagerService.retrieve("aKey")

        then:
        assert secondTry, decrypted

        when:
        simplePasswordManagerService.flushCache()
        // Fails because we've flushed the cache
        decrypted = simplePasswordManagerService.retrieve("aKey")

        then:
        thrown(AssertionError)
    }
}
