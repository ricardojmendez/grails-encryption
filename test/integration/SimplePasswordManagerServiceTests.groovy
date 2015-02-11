class SimplePasswordManagerServiceTests extends GroovyTestCase {
    def simplePasswordManagerService

    void testStore() {
        // Store a single item, test
        assert simplePasswordManagerService

        EncryptedData item = simplePasswordManagerService.store("aKey", "aValue", "hush")
        assert item
    }

    void testRetrieve() {
        assert simplePasswordManagerService

        simplePasswordManagerService.store("aKey", "aValue", "hush")
        def item = simplePasswordManagerService.retrieve("aKey", "hush")
        assert item
    }

    void testCache()
    {
        assert simplePasswordManagerService

        simplePasswordManagerService.store("aKey", "aValue", "hush")
        // Retrieving without a password
        def decrypted
        shouldFail(AssertionError) {
            decrypted = simplePasswordManagerService.retrieve("aKey")
        }
    }

    void testExpiration()
    {
        assert simplePasswordManagerService



        simplePasswordManagerService.store("aKey", "aValue", "hush")
        // Retrieving without a password
        def decrypted
        shouldFail(AssertionError) {
            decrypted = simplePasswordManagerService.retrieve("aKey")
        }

        
        simplePasswordManagerService.cacheDuration = -1 // Disable cache
        shouldFail(AssertionError)
        {
            // Fails because we're not allowing it to cache the key
            decrypted = simplePasswordManagerService.retrieve("aKey")
        }

        simplePasswordManagerService.cacheDuration = 5
        decrypted = simplePasswordManagerService.retrieve("aKey", "hush")
        assertEquals decrypted, "aValue"

        // Check that the item is still cached
        String secondTry = simplePasswordManagerService.retrieve("aKey")
        assertEquals secondTry, decrypted

        simplePasswordManagerService.flushCache()
        shouldFail(AssertionError)
        {
            // Fails because we've flushed the cache
            decrypted = simplePasswordManagerService.retrieve("aKey")
        }
    }
}
