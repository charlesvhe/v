package charles.v.pushdown;

public class ZipValue {
    private static final int META_0_BYTE = 0;   // 0b00 empty
    private static final int META_2_BYTE = 1;   // 0b01 short
    private static final int META_4_BYTE = 2;   // 0b10 int
    private static final int META_8_BYTE = 3;   // 0b11 long

    /**
     * 2 bit as 1 meta info unit; 0b00->0 byte, 0b01->2 byte, 0b10->4 byte, 0b11->8 byte
     * big endian: 0b01101100 -> [0]->0b01->2 byte, [1]->0b10->4 byte, [2]->0b11->8 byte, [3]->0b00->0 byte
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
            this.setMeta(i, this.getValueMeta(data[i]));
            dataLength += this.getDataSize(this.getValueMeta(data[i]));
        }
        return dataLength;
    }

    private void initValData(long[] data, int dataLength) {
        int off = 0;
        this.valData = new byte[dataLength];
        for (int i = 0; i < data.length; i++) {
            int meta = getMeta(this.valMeta[i / 4], (i % 4));
            this.putValue(meta, off, data[i]);
            off += this.getDataSize(meta);
        }
    }

    public long[] getValue() {
        long[] value = new long[this.getLength()];
        int off = 0;
        int index = 0;
        for (byte byteMeta : this.valMeta) {
            for (int bitMetaIndex = 0; bitMetaIndex < 4; bitMetaIndex++) {
                if (index >= this.getLength()) {
                    return value;
                }
                int meta = getMeta(byteMeta, bitMetaIndex);
                value[index] = getValue(meta, off);
                off += this.getDataSize(meta);
                index++;
            }
        }
        return value;
    }

    public long getValue(int index) {
        int[] metaAndDataOff = this.getMetaAndDataOff(index);
        return this.getValue(metaAndDataOff[0], metaAndDataOff[1]);
    }

    private long getValue(int meta, int off) {
        switch (meta) {
            case META_0_BYTE:
                return 0;
            case META_2_BYTE:
                return getShort(this.valData, off);
            case META_4_BYTE:
                return getInt(this.valData, off);
            case META_8_BYTE:
                return  getLong(this.valData, off);
            default:
                throw new IllegalArgumentException();
        }
    }

    public void putValue(long[] data) {
        this.length = data.length;
        int dataLength = this.initValMeta(data);
        this.initValData(data, dataLength);
    }

    public void putValue(long val, int index) {
        int[] metaAndDataOff = this.getMetaAndDataOff(index);
        int meta = getValueMeta(val);
        if (meta != metaAndDataOff[0]) {
            int dataSize = getDataSize(meta);
            int origDataSize = getDataSize(metaAndDataOff[0]);
            byte[] origData = this.valData;
            this.valData = new byte[origData.length + dataSize - origDataSize];
            this.setMeta(index, meta);
            System.arraycopy(origData, 0, this.valData, 0, metaAndDataOff[1]);
            System.arraycopy(origData, metaAndDataOff[1] + origDataSize,
                    this.valData, metaAndDataOff[1] + dataSize, origData.length - (metaAndDataOff[1]+origDataSize));
        }
        this.putValue(meta,metaAndDataOff[1], val);
    }

    private void putValue(int meta, int off, long value) {
        switch (meta) {
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

    public static int getValueMeta(long value) {
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

    private int[] getMetaAndDataOff(int index) {
        int[] metaAndDataOff = new int[2];
        int curIndex = 0;
        for (byte byteMeta : this.valMeta) {
            for (int m = 0; m < 4; m++) {
                metaAndDataOff[0] = getMeta(byteMeta, m);
                if (index == curIndex) {
                    return metaAndDataOff;
                }
                metaAndDataOff[1] += this.getDataSize(metaAndDataOff[0]);
                curIndex++;
            }
        }
        throw new IllegalArgumentException();
    }

    public static int getDataSize(int meta) {
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
    private void setMeta(int index, int meta) {
        int byteMetaIndex = index/4;
        int bitMetaIndex = index % 4;
        switch (bitMetaIndex) {
            case 0:
                this.valMeta[byteMetaIndex] = (byte) ((this.valMeta[byteMetaIndex] & 63) | ((meta & 3) << 6));
                break;
            case 1:
                this.valMeta[byteMetaIndex] = (byte) ((this.valMeta[byteMetaIndex] & 207) | ((meta & 3) << 4));
                break;
            case 2:
                this.valMeta[byteMetaIndex] = (byte) ((this.valMeta[byteMetaIndex] & 243) | ((meta & 3) << 2));
                break;
            case 3:
                this.valMeta[byteMetaIndex] =  (byte) ((this.valMeta[byteMetaIndex] & 252) | (meta & 3));
                break;
        }
    }

    /**
     * mask
     * 192 0b11000000
     * 48  0b00110000
     * 12  0b00001100
     * 3   0b00000011
     */
    public static int getMeta(byte byteMeta, int bitMetaIndex) {
        switch (bitMetaIndex) {
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