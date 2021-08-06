package cr.co.arquetipos

import cr.co.arquetipos.crypto.Blowfish

/**
 * <p>Simple record-based storage for encrypted data.   This was created with
 * the intention of storing encrypted passwords, so there are some design
 * decisions that were taken because of that:
 *
 * <ol>
 * <li> It assumes that the data is a string
 * <li>The space alloted for the encrypted data is limited
 * <li> The id is assigned, since it's meant to store password lists, which
 * will likely be retrieved by their name
 * </ol>
 *
 * <p>It should be easy enough to modify to handle binary data.
 *
 * <p>Finally, see this bug report when dealing with domain classes with
 * assigned ids: http://jira.codehaus.org/browse/GRAILS-1984
 *
 */
class EncryptedData {

    String id
    String dataItem

    private String tempData = ''
    private String tempPassword = ''

    static transients = ['decryptedData', 'password']

    static constraints = {
        id(size:1..32)
        dataItem(size:1..512)
    }

    static mapping = {
        id generator:'assigned'
    }

    /**
     * Password setter.  If there is a currently existing data item, it
     * requires the previous password to be cached so that it can re-
     * encrypt the data.
     */
    void setPassword(String newPassword)
    {
        if (dataItem)
        {
            // Re-encrypt the data with the new password
            assert tempPassword, "We don't have a temporary password, so we can't re-encrypt the data. Failing."
            changePassword(tempPassword, newPassword)
        }
        else
            tempPassword = newPassword
    }

    /**
     * Decrypts the current data item and re-encrypts it with a new password.
     * Will raise assertion errors if the dataItem has not been set or if
     * there is a password error.
     */
    void changePassword(String oldPassword, String newPassword)
    {
        assert dataItem, "No data to change the password for."
        String decrypted = Blowfish.decryptBase64(dataItem, oldPassword)
        assert decrypted, "Invalid old password"
        dataItem = Blowfish.encryptBase64(decrypted, newPassword)
        tempPassword = newPassword
    }

    /**
     * Encrypts a data item and stores it.
     */
    void encrypt(String theData, String thePassword)
    {
        assert thePassword, "Need a password to continue"
        assert theData, "No data to encrypt"
        dataItem = Blowfish.encryptBase64(theData, thePassword)
        assert dataItem, "Encryption failed"
        tempData = theData
        tempPassword = thePassword
    }

    /**
     * Decrypts the currently stored data item.  Will raise an assertion error
     * if there isn't an encrypted data item or the password is wrong.
     */
    String decrypt(String thePassword)
    {
        assert dataItem, "No data to decrypt"
        assert thePassword, "Empty password not allowed"
        tempData = Blowfish.decryptBase64(dataItem, thePassword)
        assert tempData, "Decryption failed"
        tempPassword = thePassword
        return tempData
    }

    /**
     * Getter for the decryptedData property.  Will attempt to decrypt it with
     * the cached password.
     */
    String getDecryptedData()
    {
        if (!tempData)
        {
            assert tempPassword, 'Unknown password, assign it first'
            tempData = Blowfish.decryptBase64(dataItem, tempPassword)
        }
        return tempData
    }

    /**
     * Sets the value for the decrypted data item and encrypts it. It requires
     * that a password was previously set and cached, otherwise call "encrypt"
     * directly.
     */
    void setDecryptedData(String newData)
    {
        assert tempPassword, 'Unknown password, assign it first'
        tempData = newData
        dataItem = Blowfish.encryptBase64(tempData, tempPassword)
    }


    /**
     * Forget all stored passwords and temporary information
     */
    void lockDown()
    {
        tempPassword = ''
        tempData = ''
    }

    /**
     * Attempts to load a record of EncryptedData with the id, and if it
     * cannot find it then creates and returns an empty one.
     */
    static EncryptedData getOrCreate(String theId) {
        def item
        try
        {
            item = EncryptedData.get(theId)
        }
        catch(Throwable t)
        {
            // For some reason this call to get is raising an exception if
            // the key does not exist, when it should simply be returning
            // null. Handling the exception for now, will test and create a
            // grails bug report.  Might be caused by the id being assigned.

            // TODO: Test, create bug report
            item = null
        }
        if (!item)
        {
            item = new EncryptedData()
            item.id = theId
        }
        return item
    }
}
