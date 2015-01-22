package charles.v.pushdown;

public class ZipValue {
    private static final int META_0_BYTE = 0;   // 0b00 empty
    private static final int META_2_BYTE = 1;   // 0b01 short
    private static final int META_4_BYTE = 2;   // 0b10 int
    private static final int META_8_BYTE = 3;   // 0b11 long

    /**
     * 2 bit as 1 meta info unit; 0b00->0 byte, 0b01->2 byte, 0b10->4 byte, 0b11->8 byte
     * big endian: 0b0110 -> [0]->0b01->2 byte, [1]->ob10->4 byte
     */
    private byte[] meta;
    private byte[] data;

    public ZipValue(long[] data) {
        int dataLength = 0;
        // initial meta[]
        this.meta = new byte[(data.length + 3) / 4];
        for (int i = 0; i < data.length; i++) {
            double v = data[i];
            int bitsMeta = 0;
            if (0 == v) {
                bitsMeta = META_0_BYTE;
            }else if(Short.MIN_VALUE <= v && v <= Short.MAX_VALUE){
                bitsMeta = META_2_BYTE;
            }else if(Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE){
                bitsMeta = META_4_BYTE;
            }else{
                bitsMeta = META_8_BYTE;
            }
            this.meta[i / 2] = setBitsMeta(this.meta[i / 2], bitsMeta, (i % 4));
            dataLength += getLength(bitsMeta);
        }
        // initial data[]
        int off = 0;
        this.data = new byte[dataLength];
        for (int i = 0; i < data.length; i++) {
            switch (this.meta[i]){
                case META_2_BYTE:
                    putShort(this.data, off, (short) data[i]);
                    break;
                case META_4_BYTE:
                    putInt(this.data, off, (int) data[i]);
                    break;
                case META_8_BYTE:
                    putLong(this.data, off, data[i]);
                    break;
                default:
                    break;
            }
            off += getLength(this.meta[i]);
        }
    }

    public static int getLength(int meta){
        switch (meta){
            case META_0_BYTE:
                return 0;
            case META_2_BYTE:
                return 2;
            case META_4_BYTE:
                return 4;
            default:
                return 8;
        }
    }

    /**
     * mask
     * 63  0b00111111
     * 207 0b11001111
     * 243 0b11110011
     * 252 0b11111100
     */
    public static byte setBitsMeta(byte origByte, int bitsMeta, int off) {
        switch (off){
            case 0:
                return (byte) ((origByte & 63) | ((bitsMeta & 3) << 6));
            case 1:
                return (byte) ((origByte & 207) | ((bitsMeta & 3) << 4));
            case 2:
                return (byte) ((origByte & 243) | ((bitsMeta & 3) << 2));
            default:
                return (byte) ((origByte & 252) | (bitsMeta & 3));
        }
    }

    /**
     * mask
     * 192 0b11000000
     * 48  0b00110000
     * 12  0b00001100
     * 3   0b00000011
     */
    public static int getBitsMeta(byte origByte, int off){
        switch (off){
            case 0:
                return origByte & 192;
            case 1:
                return origByte & 48;
            case 2:
                return origByte & 12;
            default:
                return origByte & 3;
        }
    }

    public static short getShort(byte[] b, int off) {
        return (short) ((b[off + 1] & 0xFF) |
                        (b[off    ] << 8));
    }

    public static void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val      );
        b[off    ] = (byte) (val >>> 8);
    }

    public static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF)       ) |
                ((b[off + 2] & 0xFF) <<  8) |
                ((b[off + 1] & 0xFF) << 16) |
                ((b[off    ]       ) << 24);
    }

    public static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val       );
        b[off + 2] = (byte) (val >>>  8);
        b[off + 1] = (byte) (val >>> 16);
        b[off    ] = (byte) (val >>> 24);
    }

    public static long getLong(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL)       ) |
                ((b[off + 6] & 0xFFL) <<  8) |
                ((b[off + 5] & 0xFFL) << 16) |
                ((b[off + 4] & 0xFFL) << 24) |
                ((b[off + 3] & 0xFFL) << 32) |
                ((b[off + 2] & 0xFFL) << 40) |
                ((b[off + 1] & 0xFFL) << 48) |
                (((long) b[off])      << 56);
    }

    public static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val       );
        b[off + 6] = (byte) (val >>>  8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off    ] = (byte) (val >>> 56);
    }
}
