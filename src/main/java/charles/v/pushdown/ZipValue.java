package charles.v.pushdown;

public class ZipValue {
    private static final int META_0_BYTE = 0;   // empty
    private static final int META_2_BYTE = 1;   // short
    private static final int META_4_BYTE = 2;   // int
    private static final int META_8_BYTE = 3;   // long

    private static final int SCALE = 2; // int=123, scale=2 -> value=1.23

    /**
     * 2 bit as 1 meta info unit; 0b00->0 byte, 0b01->2 byte, 0b10->4 byte, 0b11->8 byte
     * big endian: 0b0110 -> [0]->0b01->2 byte, [1]->ob10->4 byte
     */
    private byte[] meta;
    private byte[] data;

    public ZipValue(double[] data) {
        this.meta = new byte[(data.length + 1) / 2];
        for (int i = 0; i < data.length; i++) {
            double v = data[i];
            if (0 == v) {
                this.meta[i / 2] = this.getNewMeta(this.meta[i / 2], META_0_BYTE, (i % 2 == 0));
            }
        }
    }

    private byte getNewMeta(byte origByte, int meta, boolean little) {
        if(little){

        }else{

        }
        return 0;
    }

    public static short getShort(byte[] b, int off) {
        return (short) ((b[off + 1] & 0xFF) | (b[off    ] << 8));
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
