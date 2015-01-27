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
    private byte[] valMeta;
    private byte[] valData;
    private int length; // TODO will remove and make an abstract getLength()

    public int getLength() {
        return length;
    }

    public ZipValue(long[] data) {
        this.putValue(data);
    }

    private int initValMeta(long[] data) {
        int dataLength = 0;
        this.valMeta = new byte[(this.getLength() + 3) / 4];
        for (int i = 0; i < data.length; i++) {
            int bitsMeta = this.getBitsMeta(data[i]);
            this.valMeta[i / 4] = setBitsMeta(this.valMeta[i / 4], bitsMeta, (i % 4));
            dataLength += getByteSize(bitsMeta);
        }
        return dataLength;
    }

    private int getBitsMeta(long value) {
        if (0 == value) {
            return META_0_BYTE;
        } else if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
            return META_2_BYTE;
        } else if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE) {
            return META_4_BYTE;
        } else {
            return META_8_BYTE;
        }
    }

    private void initValData(long[] data, int dataLength) {
        int off = 0;
        this.valData = new byte[dataLength];
        for (int i = 0; i < data.length; i++) {
            int bitsMeta = getBitsMeta(this.valMeta[i / 4], (i % 4));
            this.putValue(bitsMeta, off, data[i]);
            off += getByteSize(bitsMeta);
        }
    }

    private void putValue(int bitsMeta, int off, long value) {
        switch (bitsMeta) {
            case META_0_BYTE:
                // do nothing
                break;
            case META_2_BYTE:
                putShort(this.valData, off, (short) value);
                break;
            case META_4_BYTE:
                putInt(this.valData, off, (int) value);
                break;
            case META_8_BYTE:
                putLong(this.valData, off, value);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private int[] getBitsMetaAndOff(int index) {
        int[] bitsMetaAndOff = new int[2];
        int curIndex = 0;
        for (byte byteMeta : this.valMeta) {
            for (int m = 0; m < 4; m++) {
                bitsMetaAndOff[0] = getBitsMeta(byteMeta, m);
                if (index == curIndex) {
                    return bitsMetaAndOff;
                }
                bitsMetaAndOff[1] += getByteSize(bitsMetaAndOff[0]);
                curIndex++;
            }
        }
        throw new IllegalArgumentException();
    }

    public void putValue(long[] data) {
        this.length = data.length;
        int dataLength = this.initValMeta(data);
        this.initValData(data, dataLength);
    }

    public void putValue(long val, int index) {
        int[] bitsMetaAndOff = this.getBitsMetaAndOff(index);

    }

    public long[] getValue() {
        long[] value = new long[this.getLength()];
        int off = 0;
        int index = 0;
        for (byte byteMeta : this.valMeta) {
            for (int m = 0; m < 4; m++) {
                if (index >= this.getLength()) {
                    return value;
                }
                int bitsMeta = getBitsMeta(byteMeta, m);
                switch (bitsMeta) {
                    case META_0_BYTE:
                        // do nothing
                        break;
                    case META_2_BYTE:
                        value[index] = getShort(this.valData, off);
                        break;
                    case META_4_BYTE:
                        value[index] = getInt(this.valData, off);
                        break;
                    case META_8_BYTE:
                        value[index] = getLong(this.valData, off);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                off += getByteSize(bitsMeta);
                index++;
            }
        }
        return value;
    }

    public long getValue(int index) {
        int[] bitsMetaAndOff = this.getBitsMetaAndOff(index);
        switch (bitsMetaAndOff[0]) {
            case META_0_BYTE:
                return 0;
            case META_2_BYTE:
                return getShort(this.valData, bitsMetaAndOff[1]);
            case META_4_BYTE:
                return getInt(this.valData, bitsMetaAndOff[1]);
            case META_8_BYTE:
                return getLong(this.valData, bitsMetaAndOff[1]);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static int getByteSize(int meta) {
        switch (meta) {
            case META_0_BYTE:
                return 0;
            case META_2_BYTE:
                return 2;
            case META_4_BYTE:
                return 4;
            case META_8_BYTE:
                return 8;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * mask
     * 63  0b00111111
     * 207 0b11001111
     * 243 0b11110011
     * 252 0b11111100
     */
    public static byte setBitsMeta(byte byteMeta, int bitsMeta, int off) {
        switch (off) {
            case 0:
                return (byte) ((byteMeta & 63) | ((bitsMeta & 3) << 6));
            case 1:
                return (byte) ((byteMeta & 207) | ((bitsMeta & 3) << 4));
            case 2:
                return (byte) ((byteMeta & 243) | ((bitsMeta & 3) << 2));
            case 3:
                return (byte) ((byteMeta & 252) | (bitsMeta & 3));
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * mask
     * 192 0b11000000
     * 48  0b00110000
     * 12  0b00001100
     * 3   0b00000011
     */
    public static int getBitsMeta(byte byteMeta, int off) {
        switch (off) {
            case 0:
                return (byteMeta & 192) >>> 6;
            case 1:
                return (byteMeta & 48) >>> 4;
            case 2:
                return (byteMeta & 12) >>> 2;
            case 3:
                return byteMeta & 3;
            default:
                throw new IllegalArgumentException();
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
