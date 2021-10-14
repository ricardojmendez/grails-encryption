package cr.co.arquetipos

import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security

class BouncyCastleSpec extends Specification implements GrailsUnitTest {

    void testPGP() {

        Security.addProvider(new BouncyCastleProvider());

        // Generates a new keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        kpg.initialize(1024, new SecureRandom())
        KeyPair kp = kpg.generateKeyPair();

        assert kp

        // Obtains a PGPKeyPair object from the generated pair, and obtains the public and private keys
        PGPKeyPair pgpKp = new PGPKeyPair(PGPPublicKey.RSA_GENERAL, kp.getPublic(), kp.getPrivate(), new Date(), "BC");
        PGPPublicKey publicKey = pgpKp.getPublicKey();
        PGPPrivateKey privateKey = pgpKp.getPrivateKey();

        assert publicKey
        assert privateKey

        mixedTest(privateKey, publicKey)

        pgpKp = new PGPKeyPair(publicKey, null)

        assert pgpKp.publicKey
        assert pgpKp.privateKey == null

    }

    private void mixedTest(PGPPrivateKey pgpPrivKey, PGPPublicKey pgpPubKey) {
        // byte[]    text = { (byte)'h', (byte)'e', (byte)'l', (byte)'l', (byte)'o', (byte)' ', (byte)'w', (byte)'o', (byte)'r', (byte)'l', (byte)'d', (byte)'!', (byte)'\n' };
        String message = 'hello world!\n'
        byte[] text = message.getBytes()

        //
        // literal data
        //
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PGPLiteralDataGenerator literalGenerator = new PGPLiteralDataGenerator();

        OutputStream literalOut = literalGenerator.open(byteOut, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, text.length, new Date());
        literalOut.write(text);
        literalGenerator.close();

        byte[] bytes = byteOut.toByteArray();

        PGPObjectFactory f = new PGPObjectFactory(bytes);
        checkLiteralData((PGPLiteralData) f.nextObject(), text);

        ByteArrayOutputStream bcOut = new ByteArrayOutputStream();

        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(SymmetricKeyAlgorithmTags.AES_128, true, new SecureRandom(), "BC");

        // Encrypts the data with both the PGP key and a password
        encGen.addMethod(pgpPubKey);
        encGen.addMethod("password".toCharArray());

        OutputStream cOut = encGen.open(bcOut, bytes.length);
        cOut.write(bytes);
        cOut.close();

        byte[] encData = bcOut.toByteArray();

        assert encData.size()
        assert new String(encData) != message

        //
        // asymmetric
        //
        PGPObjectFactory pgpF = new PGPObjectFactory(encData);
        PGPEncryptedDataList encList = (PGPEncryptedDataList) pgpF.nextObject();
        PGPPublicKeyEncryptedData encP = (PGPPublicKeyEncryptedData) encList.get(0);
        InputStream clear = encP.getDataStream(pgpPrivKey, "BC");
        PGPObjectFactory pgpFact = new PGPObjectFactory(clear);
        checkLiteralData((PGPLiteralData) pgpFact.nextObject(), text);

        //
        // PBE
        //
        pgpF = new PGPObjectFactory(encData);
        encList = (PGPEncryptedDataList) pgpF.nextObject();
        PGPPBEEncryptedData encPbe = (PGPPBEEncryptedData) encList.get(1);
        clear = encPbe.getDataStream("password".toCharArray(), "BC");
        pgpF = new PGPObjectFactory(clear);
        checkLiteralData((PGPLiteralData) pgpF.nextObject(), text);
    }

    private void checkLiteralData(PGPLiteralData ld, byte[] data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        if (!ld.getFileName().equals(PGPLiteralData.CONSOLE)) {
            throw new RuntimeException("wrong filename in packet");
        }

        InputStream inLd = ld.getDataStream();
        int ch;

        while ((ch = inLd.read()) >= 0) {
            bOut.write(ch);
        }

        println new String(bOut.toByteArray())
        println new String(data)

        assertEquals bOut.toByteArray(), data
    }
}