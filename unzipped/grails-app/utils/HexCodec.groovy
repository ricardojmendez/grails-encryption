import org.apache.commons.codec.binary.Hex

class HexCodec {
    static encode = { str ->
       def result = new Hex().encode(str)
       return new String(result)
    }

    static decode = {  str ->
        return new Hex().decode(str.bytes)
    }
}