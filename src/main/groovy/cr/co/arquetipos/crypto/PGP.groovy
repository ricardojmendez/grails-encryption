package cr.co.arquetipos.crypto

import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security

/**
 * Author: Ricardo
 * Date: Dec 18, 2007
 * Time: 10:45:58 PM
 */
class PGP {
    static final String BCProvider = 'BC'

    PGPKeyPair keyPair
    int encryptionAlgorithm = SymmetricKeyAlgorithmTags.BLOWFISH


    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    PGP(PGPKeyPair theKeyPair) {
        this.keyPair = theKeyPair
    }


    /**
     * Instantiates a new PGP object from encoded private and public keys
     *
     * @param sPublic Armored text for the public key
     * @param sPrivate Armored text containing the secret key
     * @param passphrase Passphrase the secret key was encrypted with, blank by default
     *
     */
    PGP(String sPublic, String sPrivate, String passphrase = '') {
        InputStream stream
        PGPPublicKey pubK
        PGPPrivateKey privK

        assert (sPublic || sPrivate)


        if (sPublic) {
            stream = PGPUtil.getDecoderStream(new ByteArrayInputStream(sPublic.getBytes()))
            PGPPublicKeyRing ring = new PGPPublicKeyRing(PGPUtil.getDecoderStream(stream))
            pubK = ring.publicKey
        }

        if (sPrivate) {
            stream = PGPUtil.getDecoderStream(new ByteArrayInputStream(sPrivate.getBytes()))
            PGPSecretKeyRing secRing = new PGPSecretKeyRing(PGPUtil.getDecoderStream(stream))
            privK = secRing.getSecretKey().extractPrivateKey(passphrase.toCharArray(), BCProvider)

            if (!pubK) {
                pubK = secRing.publicKey
            }
        }

        this.keyPair = new PGPKeyPair(pubK, privK)
    }


    /**
     * Generates a new PGP Key pair of the specific size, with a blank passphrase
     */
    static PGP generateKeyPair(int size = 1024) {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance('RSA', BCProvider);
        kpg.initialize(size, new SecureRandom());
        KeyPair kp = kpg.generateKeyPair();
        PGPKeyPair pgpKp = new PGPKeyPair(PGPPublicKey.RSA_GENERAL, kp.getPublic(), kp.getPrivate(), new Date(), BCProvider)

        return new PGP(pgpKp);
    }


    PGPPublicKey getPublicKey() {
        return keyPair.getPublicKey()
    }

    PGPPrivateKey getPrivateKey() {
        return keyPair.getPrivateKey()
    }

    /**
     *  Encrypts a string and returns it base64 encoded
     */
    String encryptBase64(String data) {
        return this.encrypt(data).encodeBase64().toString()
    }

    String encryptArmored(String data) {
        byte[] encrypted = this.encrypt(data)
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ArmoredOutputStream aOut = new ArmoredOutputStream(bOut);

        aOut.write(encrypted);
        aOut.close();

        byte[] out = bOut.toByteArray()
        return new String(out)
    }


    /**
     * Encrypts a string and returns the encrypted byte array
     */
    byte[] encrypt(String data) {
        this.encrypt(data.getBytes())
    }

    /**
     * Encrypts an array of data  and returns the encrypted byte array
     */
    byte[] encrypt(byte[] data) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PGPLiteralDataGenerator literalGenerator = new PGPLiteralDataGenerator();

        OutputStream literalOut = literalGenerator.open(byteOut, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, data.length, new Date());
        literalOut.write(data);
        literalGenerator.close();

        byte[] bytes = byteOut.toByteArray();

        ByteArrayOutputStream encOut = new ByteArrayOutputStream()
        OutputStream bcOut = encOut
        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(encryptionAlgorithm, true, new SecureRandom(), BCProvider);

        // Encrypts the data with both the PGP key and a password
        encGen.addMethod(publicKey);

        OutputStream cOut = encGen.open(bcOut, bytes.length);
        bcOut.close()
        cOut.write(bytes);
        cOut.close();

        return encOut.toByteArray();
    }

    /**
     * Decrypts base-64 encoded data with our current private key.  If there is an
     * error during decryption, it will return null
     */
    String decryptBase64(String data) {
        byte[] bytes = decrypt(data.decodeBase64())
        String str
        if (bytes) {
            str = new String(bytes)
        }
        return str
    }

    /**
     * Decrypts an array of data with our current private key.  If there is an
     * error during decryption, it will return null
     */
    byte[] decrypt(byte[] data) {
        assert keyPair.privateKey
        byte[] result = null

        try {
            PGPObjectFactory pgpF = new PGPObjectFactory(data);
            PGPEncryptedDataList encList = (PGPEncryptedDataList) pgpF.nextObject();
            PGPPublicKeyEncryptedData encP = (PGPPublicKeyEncryptedData) encList.get(0);
            // The following will raise an exception if wee attempting to decrypt with the wrong key
            InputStream clear = encP.getDataStream(keyPair.privateKey, BCProvider);

            PGPObjectFactory pgpFact = new PGPObjectFactory(clear);
            PGPLiteralData ld = (PGPLiteralData) pgpFact.nextObject()
            ByteArrayOutputStream bOut = new ByteArrayOutputStream()
            assert ld.fileName == PGPLiteralData.CONSOLE
            InputStream inLd = ld.getDataStream()
            int ch
            while ((ch = inLd.read()) >= 0) {
                bOut.write(ch)
            }
            result = bOut.toByteArray()
        } catch (Exception e) {
            result = null
        }
        return result
    }


    /**
     * Encodes a public key and returns it as an armored stream
     */
    static String encodePublicKey(PGPPublicKey theKey) {
        OutputStream stream = new ByteArrayOutputStream()
        ArmoredOutputStream secretOut = new ArmoredOutputStream(stream)
        theKey.encode(secretOut)
        secretOut.close()
        return new String(stream.toByteArray())
    }


    /**
     * Wraps the private key in a PGPSecretKey and returns it encoded as armored
     * text
     */
    static String encodePrivateKey(PGPKeyPair theKeyPair, String passphrase = '') {
        PGPSecretKey pgpskey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION,
                theKeyPair, '', PGPPublicKey.RSA_GENERAL, passphrase.toCharArray(), true,
                null, null, new SecureRandom(), BCProvider);
        OutputStream stream = new ByteArrayOutputStream()
        ArmoredOutputStream secretOut = new ArmoredOutputStream(stream)
        pgpskey.encode(secretOut)
        secretOut.close()
        return new String(stream.toByteArray())
    }

    /**
     * Returns the public key, encoded as armored text
     */
    String getEncodedPublicKey() {
        return encodePublicKey(keyPair.publicKey)
    }

    /**
     * Returns the private key, wrapped in a PGPSecretKey and encoded as armored
     * text
     */
    String getEncodedPrivateKey(String passphrase = '') {
        return encodePrivateKey(keyPair, passphrase)
    }
}