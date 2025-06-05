/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:13 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import org.apache.commons.io.Charsets;

import headwayent.hotshotengine.basictypes.ENG_Byte;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.basictypes.ENG_PrimitiveType;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;

public class ENG_Serializer {

    private static final int STREAM_OVERHEAD_SIZE =
            ENG_Short.SIZE_IN_BYTES + ENG_Integer.SIZE_IN_BYTES;
    private static final short HEADER_STREAM_ID = 0x1000;
    private static final short OTHER_ENDIAN_HEADER_STREAM_ID = 0x0010;


    protected int mCurrentstreamLen;
    protected RandomAccessFile mpfFile;
    protected MappedByteBuffer buf;
    protected String mVersion = "[Serializer_v1.00]";
    protected boolean mFlipEndian; // default to native endian, derive from header
    protected boolean mBufFlipped;

    public ENG_Serializer() {

    }

    public void openFile(
            String filename, ENG_Resource.RandomAccessMode accMode) {
        String[] pathAndFileName =
                ENG_CompilerUtil.getPathAndFileName(filename);
        try {
            openFile(ENG_Resource.getRandomAccessFile(pathAndFileName[1],
                    pathAndFileName[0], accMode));
        } catch (FileNotFoundException e) {

            e.printStackTrace();
            ENG_Log.getInstance().log("Could not find file : " +
                            pathAndFileName[1] + " with path: " + pathAndFileName[0],
                    ENG_Log.TYPE_ERROR);
        }
    }

    public void openFile(RandomAccessFile file) {
        mpfFile = file;
        try {
            FileChannel channel = mpfFile.getChannel();
            buf = channel.map(MapMode.READ_WRITE, 0,
                    channel.size());
        } catch (IOException e) {

            e.printStackTrace();
        }
        mBufFlipped = false;
    }

    public void closeFile() {
        try {
            mpfFile.close();
        } catch (IOException e) {

            e.printStackTrace();
            ENG_Log.getInstance().log("IOException when closing file",
                    ENG_Log.TYPE_WARNING);
        }
    }

    private void advanceBuffer(int num, ENG_PrimitiveType type) {
        buf.position(
                buf.position() + ENG_PrimitiveType.getSizeInBytes(type) * num);

    }

    protected void writeFileHeader() {
        short[] val = {HEADER_STREAM_ID};
        writeShorts(val, 0, 1);

        writeString(mVersion);
    }

    protected void writeChunkHeader(short id, int size) {
        short[] idv = {id};
        int[] sizev = {size};
        writeShorts(idv, 0, 1);
        writeInts(sizev, 0, 1);
    }

    protected void writeFloats(float[] data, int offs, int count) {
        if (mFlipEndian) {
            flipEndianess(buf);
        }
        buf.asFloatBuffer().put(data, offs, count);
        advanceBuffer(count, ENG_PrimitiveType.FLOAT);
    }

    protected void writeFloats(double[] data, int offs, int count) {
        if (mFlipEndian) {
            flipEndianess(buf);
        }
        float[] d = new float[count - offs];
        for (int i = 0; i < d.length; ++i) {
            d[i] = (float) data[i + offs];
        }
        buf.asFloatBuffer().put(d);
        advanceBuffer(count, ENG_PrimitiveType.FLOAT);
    }

    protected void writeShorts(short[] data, int offs, int count) {
        if (mFlipEndian) {
            flipEndianess(buf);
        }
        buf.asShortBuffer().put(data, offs, count);
        advanceBuffer(count, ENG_PrimitiveType.SHORT);
    }

    protected void writeInts(int[] data, int offs, int count) {
        if (mFlipEndian) {
            flipEndianess(buf);
        }
        buf.asIntBuffer().put(data, offs, count);
        advanceBuffer(count, ENG_PrimitiveType.INTEGER);
    }

    protected void writeBools(boolean[] data, int offs, int count) {
        byte[] b = new byte[count - offs];
        for (int i = 0; i < b.length; ++i) {
            b[i] = (byte) (data[i + offs] ? 1 : 0);
        }
        writeData(b, 0, b.length);
    }

    protected void writeObject(ENG_Vector3D vec) {
        float[] f = {vec.x, vec.y, vec.z};
        writeFloats(f, 0, f.length);
    }

    protected void writeObject(ENG_Quaternion q) {
        float[] f = {q.x, q.y, q.z, q.w};
        writeFloats(f, 0, f.length);
    }

    protected void writeString(String s) {
        byte[] bytes = s.getBytes(Charsets.US_ASCII);
        writeData(bytes, 0, bytes.length);
    }

    protected void writeData(byte[] b, int offs, int count) {
        buf.put(b, offs, count);
    }

    protected String readString() {
        ArrayList<ENG_Byte> list = new ArrayList<>();
        while (buf.hasRemaining()) {
            byte b = buf.get();
            if (b == '\n' || b == '\r' || b == '\0') {
            /*	byte[] ret = new byte[list.size()];
				int i = 0;
				for (Byte bt : list) {
					ret[i++] = bt;
				}
				return new String(ret);*/
                break;
            }
            list.add(new ENG_Byte(b));
        }
        return ENG_CompilerUtil.fromByteArrayToString(list);
    }

    protected String readString(int numChars) {
        if (numChars > buf.remaining()) {
            numChars = buf.remaining();
        }

        if (numChars == 0) {
            return "";
        }
        byte[] b = new byte[numChars];
        buf.get(b);
        return new String(b);
    }

    protected float[] readFloats(int num) {
        int remaining = buf.remaining() / ENG_Float.SIZE_IN_BYTES;
        if (num > remaining) {
            if (remaining == 0) {
                return new float[num];
            }
            num = remaining;
        }
        float[] ret = new float[num];
        buf.asFloatBuffer().get(ret);
        advanceBuffer(num, ENG_PrimitiveType.FLOAT);
        return ret;
    }

    protected boolean[] readBools(int num) {
        int remaining = buf.remaining() / ENG_Byte.SIZE_IN_BYTES;
        if (num > remaining) {
            if (remaining == 0) {
                return new boolean[num];
            }
            num = remaining;
        }
        byte[] ret = new byte[num];
        buf.get(ret);
        boolean[] b = new boolean[num];
        for (int i = 0; i < b.length; ++i) {
            b[i] = ret[i] == 1;
        }

        return b;
    }

    protected byte[] readBytes(int num) {
        int remaining = buf.remaining() / ENG_Byte.SIZE_IN_BYTES;
        if (num > remaining) {
            if (remaining == 0) {
                return new byte[num];
            }
            num = remaining;
        }
        byte[] ret = new byte[num];
        buf.get(ret);
        return ret;
    }

    protected double[] readDouble(int num) {
        int remaining = buf.remaining() / ENG_Float.SIZE_IN_BYTES;
        if (num > remaining) {
            if (remaining == 0) {
                return new double[num];
            }
            num = remaining;
        }
        double[] ret = new double[num];
        float[] dst = new float[num];
        buf.asFloatBuffer().get(dst);
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = dst[i];
        }
        advanceBuffer(num, ENG_PrimitiveType.FLOAT);
        return ret;
    }

    protected short[] readShorts(int num) {
        int remaining = buf.remaining() / ENG_Short.SIZE_IN_BYTES;
        if (num > remaining) {
            if (remaining == 0) {
                return new short[num];
            }
            num = remaining;
        }
        short[] ret = new short[num];
        buf.asShortBuffer().get(ret);
        advanceBuffer(num, ENG_PrimitiveType.SHORT);
		/*int[] r = new int[num];
		for (int i = 0; i < num; ++i) {
			r[i] = ret[i] >= 0 ? ret[i] : 0x10000 + ret[i]; 
		}
		return r;*/
        return ret;
    }

    protected int[] readInts(int num) {
        int remaining = buf.remaining() / ENG_Integer.SIZE_IN_BYTES;
        if (num > remaining) {
            if (remaining == 0) {
                return new int[num];
            }
            num = remaining;
        }
        int[] ret = new int[num];
        buf.asIntBuffer().get(ret);
        advanceBuffer(num, ENG_PrimitiveType.INTEGER);
        return ret;
    }

    protected ENG_Vector3D readVector3() {
        int remaining = buf.remaining() / ENG_Float.SIZE_IN_BYTES * 3;
        if (remaining == 0) {
            throw new ENG_InvalidFormatParsingException(
                    "Cannot read a vector3. Not enought bytes left");
        }
        float[] f = new float[3];
        buf.asFloatBuffer().get(f);
        advanceBuffer(3, ENG_PrimitiveType.FLOAT);
        return new ENG_Vector3D(f);
    }

    protected ENG_Quaternion readQuat() {
        int remaining = buf.remaining() / ENG_Float.SIZE_IN_BYTES * 4;
        if (remaining == 0) {
            throw new ENG_InvalidFormatParsingException(
                    "Cannot read a vector3. Not enought bytes left");
        }
        float[] f = new float[4];
        buf.asFloatBuffer().get(f);
        advanceBuffer(4, ENG_PrimitiveType.FLOAT);
        return new ENG_Quaternion(f);
    }

    protected void readFileHeader() {
        if (readShorts(1)[0] == HEADER_STREAM_ID) {
            String ver = readString();
            if (!mVersion.equals(ver)) {
                throw new ENG_InvalidFormatParsingException(
                        "Incompatible file version " + ver +
                                " with current version " + mVersion);
            }
        } else {
            throw new ENG_InvalidFormatParsingException(
                    "Invalid file: no header");
        }
    }

    protected short readChunk() {
        short id = readShorts(1)[0];
        mCurrentstreamLen = readInts(1)[0];
        return id;
    }

    protected void determineEndianness() {
        buf.position(0);
        buf.order(ByteOrder.nativeOrder());
        short s = buf.getShort();
        buf.position(0);
        if (s == HEADER_STREAM_ID) {
            mFlipEndian = false;
        } else if (s == OTHER_ENDIAN_HEADER_STREAM_ID) {
            mFlipEndian = true;
        } else {
            throw new ENG_InvalidFormatParsingException(
                    "Header chunk didn't match either endian: " +
                            "Corrupted stream?");
        }
    }

    public /// The endianness of written files
    enum Endian {
        /// Use the platform native endian
        ENDIAN_NATIVE,
        /// Use big endian (0x1000 is serialised as 0x10 0x00)
        ENDIAN_BIG,
        /// Use little endian (0x1000 is serialised as 0x00 0x10)
        ENDIAN_LITTLE
    }

    protected void determineEndianness(Endian order) {

        switch (order) {
            case ENDIAN_NATIVE:
                mFlipEndian = false;
                break;
            case ENDIAN_BIG:
                mFlipEndian = ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN;
                break;
            case ENDIAN_LITTLE:
                mFlipEndian = ByteOrder.nativeOrder() != ByteOrder.LITTLE_ENDIAN;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    protected void flipEndianess(MappedByteBuffer buf) {
        if (!mBufFlipped) {
            buf.order(buf.order() == ByteOrder.BIG_ENDIAN ?
                    ByteOrder.LITTLE_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            mBufFlipped = true;
        }
    }

    protected void flipEndian(ByteBuffer buf, int pos, int size, int count) {
        for (int i = 0; i < count; ++i) {
            flipEndian(buf, pos + i * size, size);
        }
    }

    protected void flipEndian(ByteBuffer buf, int pos, int size) {
        for (int i = 0; i < size / 2; ++i) {
            byte b = buf.get(pos);
            buf.put(pos + i, buf.get(pos + size - i - 1));
            buf.put(pos + size - i - 1, b);
        }
    }
}
