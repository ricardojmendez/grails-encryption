package cr.co.arquetipos

import cr.co.arquetipos.HexCodec
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

import java.security.SecureRandom

class HexCodecSpec extends Specification implements GrailsUnitTest {

    private codec = new HexCodec()

    void testEncoder() {
        String message = "The Encoded Message"
        String encoded = codec.encode(message)
        println encoded
        assert message != encoded
        String decoded = new String(codec.decode(encoded))
        assert decoded
        assertEquals decoded, message

        int len = 50
        byte[] bytes = new byte[len]
        SecureRandom random = new SecureRandom()
        len.times { int i ->
            byte b = (byte) random.nextInt(255)
            bytes[i] = b
        }
        encoded = codec.encode(bytes)
        println encoded
        assert encoded
        byte[] second = codec.decode(encoded)
        len.times { int i ->
            assertEquals bytes[i], second[i]
        }
    }
}
