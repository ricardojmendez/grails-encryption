import cr.co.arquetipos.crypto.Blowfish
import cr.co.arquetipos.crypto.PasswordTools

/*
    Simple record-based storage for encrypted data.   This was created with
    the intention of storing encrypted passwords, so there are some design
    decisions that were taken because of that:

    1) It assumes that the data is a string
    2) The space alloted for the encrypted data is limited
    3) The id is assigned, since it's meant to store password lists, which
    will likely be retrieved by their name

    It should be easy enough to modify to handle binary data.

    Finally, see this bug report when dealing with domain classes with
    assigned ids: http://jira.codehaus.org/browse/GRAILS-1984
 */
class EncryptedData {
    String id
    String encryptedData

    private String tempData = ''
    private String tempPassword = ''


    static transients = ['data', 'password']

    static constraints = {
        id(size:1..32)
        encryptedData(size:1..512)
    }

    static mapping = {
        id generator:'assigned', params:[type:'string']
    }


    public setPassword(String newPassword)
    {
        if (encryptedData)
        {
            // Re-encrypt the data with the new password
            assert tempPassword, "We don't have a temporary password, so we can't re-encrypt the data. Failing."
            changePassword(tempPassword, newPassword)
        }
        else
            tempPassword = newPassword
    }

    // Changes the password for the current encrypted data, and re-encrypts
    public changePassword(String oldPassword, String newPassword)
    {
        assert encryptedData, "No data to change the password for."
        String decrypted = Blowfish.decryptBase64(encryptedData, oldPassword)
        assert decrypted, "Invalid old password"
        encryptedData = Blowfish.encryptBase64(decrypted, newPassword)
        tempPassword = newPassword
    }

    public encrypt(String theData, String thePassword)
    {
        assert thePassword, "Need a password to continue"
        assert theData, "No data to encrypt"
        encryptedData = Blowfish.encryptBase64(theData, thePassword)
        assert encryptedData, "Encryption failed"
        tempData = theData
        tempPassword = thePassword
    }

    public String decrypt(String thePassword)
    {
        assert encryptedData, "No data to decrypt"
        tempData = Blowfish.decryptBase64(encryptedData, thePassword)
        assert tempData, "Decryption failed"
        tempPassword = thePassword
        return tempData
    }


    public getData()
    {
        if (!tempData)
        {
            assert tempPassword, 'Unknown password, assign it first'
            tempData = Blowfish.decryptBase64(encryptedData, tempPassword)
        }
        return tempData
    }

    public setData(String newData)
    {
        assert tempPassword, 'Unknown password, assign it first'
        tempData = newData
        encryptedData = Blowfish.encryptBase64(tempData, tempPassword)
    }


    // Forget all stored passwords and temporary information
    public lockDown()
    {
        tempPassword = ''
        tempData = ''
    }
    
}
