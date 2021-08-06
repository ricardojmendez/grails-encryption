package cr.co.arquetipos

import org.apache.commons.codec.binary.Hex

class HexCodec {
    static encode = { str ->
        return new String(new Hex().encode(str))
    }

    static decode = { str ->
        return new Hex().decode(str.bytes)
    }
}