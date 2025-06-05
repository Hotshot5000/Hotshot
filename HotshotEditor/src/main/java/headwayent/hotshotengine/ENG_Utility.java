package headwayent.hotshotengine;

import headwayent.hotshotengine.basictypes.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class ENG_Utility {

    private static final int LWJGL_LIMIT = 64;
    public static final float BYTE_TO_FLOAT = 1.0f / 255.0f;
    public static final int COMPARE_LESS_THAN = -1;
    public static final int COMPARE_EQUAL_TO = 0;
    public static final int COMPARE_GREATER_THAN = 1;
    public static final int BYTE_SIZE = 8;
    public static final NumberFormat FORMATTER_DEFAULT = new DecimalFormat("#0000.00");

    private static ENG_Random random;
    private static ReentrantLock idLock = new ReentrantLock();
    private static long id;

    /**
     * @return Unique Id per call. Not thread safe.
     */
    public static long getUniqueId() {
        return ++id;
    }

    /**
     * Favor needing unique ids from a single thread in order to reduce contention.
     *
     * @return
     */
    public static long getUniqueIdThreadSafe() {
        idLock.lock();
        try {
            return ++id;
        } finally {
            idLock.unlock();
        }
    }

//    static {
//        System.out.println("utility class loaded");
//        System.out.println(ENG_Utility.class.getClassLoader());
//    }
//
//    public static void test() {
//        System.out.println("test");
//    }

    public static void createRandomNumberGenerator(long seed) {
//        System.out.println(ENG_Utility.class.getClassLoader());
//        if (MainApp.getMainThread().isInputState()) {
//            String parameter = MainApp.getMainThread().getDebuggingState().getPredefinedParameter("seed", 0);
//            seed = Long.parseLong(parameter);
//        } else {

//        }
        if (seed == 0) {
            seed = currentTimeMillis();
        }
        random = new ENG_Random(seed);
//        if (MainApp.getMainThread().isInputState() || MainApp.isOutputDebuggingApplicationStateEnabled()) {
//            setWriteableRandom(true);
//        }
//        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
//            MainApp.getMainThread().getDebuggingState().addPredefinedParameter("seed", String.valueOf(seed));
//        }
    }

    public static void setWriteableRandom(boolean writeableRandom) {
        if (writeableRandom) {
            throw new IllegalArgumentException();
//            random = new ENG_DebuggingRandom(getRandom());
        } else {
            random = getRandom().getRandom();
        }
    }

    public static void swap(ENG_Byte b0, ENG_Byte b1) {
        byte b = b0.getValue();
        b0.setValue(b1);
        b1.setValue(b);
    }

    public static void swap(ENG_Short s0, ENG_Short s1) {
        short s = s0.getValue();
        s0.setValue(s1);
        s1.setValue(s);
    }

    public static void swap(ENG_Integer i0, ENG_Integer i1) {
        int i = i0.getValue();
        i0.setValue(i1);
        i1.setValue(i);
    }

    public static void swap(ENG_Long l0, ENG_Long l1) {
        long l = l0.getValue();
        l0.setValue(l1);
        l1.setValue(l);
    }

    public static void swap(ENG_Float f0, ENG_Float f1) {
        float f = f0.getValue();
        f0.setValue(f1);
        f1.setValue(f);
    }

    public static void swap(ENG_Double d0, ENG_Double d1) {
        double d = d0.getValue();
        d0.setValue(d1);
        d1.setValue(d);
    }

    public static void swap(ENG_Boolean b0, ENG_Boolean b1) {
        boolean b = b0.getValue();
        b0.setValue(b1);
        b1.setValue(b);
    }

    public static int strcmp(byte[] src, byte[] dst) {
        int diff = src.length - dst.length;
        if (diff < 0) {
            return -1;
        }
        if (diff > 0) {
            return 1;
        }
        int i = 0;
        int ret = 0;
        while ((i < src.length) && ((ret = src[i] - dst[i]) == 0)) {
            ++i;
        }
        if (ret < 0) {
            return -1;
        }
        if (ret > 0) {
            return 1;
        }
        return ret;

    }

    public static float mapByteToFloat(byte b) {
        return ((float) b * BYTE_TO_FLOAT);
    }

    public static byte mapFloatToByte(float f) {
        return (byte) (f * 255.0f);
    }

    public static ArrayList<String> split(String str, String delim) {
        ArrayList<String> list = new ArrayList<>();
        split(str, delim, list);
        return list;
    }

    public static void split(String str, String delim, ArrayList<String> list) {
        int pos;
        int lastPos = 0;
        int delimLen = delim.length();

        while ((pos = str.indexOf(delim)) != -1) {
            list.add(str.substring(lastPos, pos));
            lastPos = pos + delimLen;
            str = str.substring(lastPos);
        }

    }

    public static void memcpy(Buffer dest, Buffer src, int length) {
        // The old method is no entirely safe
        if ((dest instanceof ByteBuffer) && (src instanceof ByteBuffer)) {
        /*	byte[] d = ((ByteBuffer) dest).array();
            byte[] s = ((ByteBuffer) src).array();
			System.arraycopy(s, src.position(), d, dest.position(), length);*/
            ByteBuffer d = (ByteBuffer) dest;
            ByteBuffer s = (ByteBuffer) src;
            memcpy(d, s, length);
        } else if ((dest instanceof FloatBuffer)
                && (src instanceof FloatBuffer)) {
		/*	float[] d = ((FloatBuffer) dest).array();
			float[] s = ((FloatBuffer) src).array();
			System.arraycopy(s, src.position(), d, dest.position(), length);*/
            FloatBuffer d = (FloatBuffer) dest;
            FloatBuffer s = (FloatBuffer) src;
            memcpy(d, s, length);
        } else if ((dest instanceof IntBuffer) && (src instanceof IntBuffer)) {
		/*	int[] d = ((IntBuffer) dest).array();
			int[] s = ((IntBuffer) src).array();
			System.arraycopy(s, src.position(), d, dest.position(), length);*/
            IntBuffer d = (IntBuffer) dest;
            IntBuffer s = (IntBuffer) src;
            memcpy(d, s, length);
        } else if ((dest instanceof ShortBuffer)
                && (src instanceof ShortBuffer)) {
		/*	short[] d = ((ShortBuffer) dest).array();
			short[] s = ((ShortBuffer) src).array();
			System.arraycopy(s, src.position(), d, dest.position(), length);*/
            ShortBuffer d = (ShortBuffer) dest;
            ShortBuffer s = (ShortBuffer) src;
            memcpy(d, s, length);
        } else if ((dest instanceof DoubleBuffer)
                && (src instanceof DoubleBuffer)) {
		/*	double[] d = ((DoubleBuffer) dest).array();
			double[] s = ((DoubleBuffer) src).array();
			System.arraycopy(s, src.position(), d, dest.position(), length);*/
            DoubleBuffer d = (DoubleBuffer) dest;
            DoubleBuffer s = (DoubleBuffer) src;
            memcpy(d, s, length);
        } else if ((dest instanceof LongBuffer) && (src instanceof LongBuffer)) {
		/*	long[] d = ((LongBuffer) dest).array();
			long[] s = ((LongBuffer) src).array();
			System.arraycopy(s, src.position(), d, dest.position(), length);*/
            LongBuffer d = (LongBuffer) dest;
            LongBuffer s = (LongBuffer) src;
            memcpy(d, s, length);
        } else if ((dest instanceof CharBuffer) && (src instanceof CharBuffer)) {
		/*	char[] d = ((CharBuffer) dest).array();
			char[] s = ((CharBuffer) src).array();
			System.arraycopy(s, src.position(), d, dest.position(), length);*/
            CharBuffer d = (CharBuffer) dest;
            CharBuffer s = (CharBuffer) src;
            memcpy(d, s, length);
        } else {
            throw new IllegalArgumentException(
                    "The buffers must have the same type");
        }
    }

    public static void memcpy(CharBuffer d, CharBuffer s, int length) {
        int dpos = d.position();
        int spos = s.position();
        int dlim = d.limit();
        int slim = s.limit();
        s.limit(spos + length);
        d.limit(dpos + length);
        d.put(s);
        d.limit(dlim);
        s.limit(slim);
        d.position(dpos);
        s.position(spos);
    }

    public static void memcpy(LongBuffer d, LongBuffer s, int length) {
        int dpos = d.position();
        int spos = s.position();
        int dlim = d.limit();
        int slim = s.limit();
        s.limit(spos + length);
        d.limit(dpos + length);
        d.put(s);
        d.limit(dlim);
        s.limit(slim);
        d.position(dpos);
        s.position(spos);
    }

    public static void memcpy(DoubleBuffer d, DoubleBuffer s, int length) {
        int dpos = d.position();
        int spos = s.position();
        int dlim = d.limit();
        int slim = s.limit();
        s.limit(spos + length);
        d.limit(dpos + length);
        d.put(s);
        d.limit(dlim);
        s.limit(slim);
        d.position(dpos);
        s.position(spos);
    }

    public static void memcpy(ShortBuffer d, ShortBuffer s, int length) {
        int dpos = d.position();
        int spos = s.position();
        int dlim = d.limit();
        int slim = s.limit();
        s.limit(spos + length);
        d.limit(dpos + length);
        d.put(s);
        d.limit(dlim);
        s.limit(slim);
        d.position(dpos);
        s.position(spos);
    }

    public static void memcpy(IntBuffer d, IntBuffer s, int length) {
        int dpos = d.position();
        int spos = s.position();
        int dlim = d.limit();
        int slim = s.limit();
        s.limit(spos + length);
        d.limit(dpos + length);
        d.put(s);
        d.limit(dlim);
        s.limit(slim);
        d.position(dpos);
        s.position(spos);
    }

    public static void memcpy(FloatBuffer d, FloatBuffer s, int length) {
        int dpos = d.position();
        int spos = s.position();
        int dlim = d.limit();
        int slim = s.limit();
        s.limit(spos + length);
        d.limit(dpos + length);
        d.put(s);
        d.limit(dlim);
        s.limit(slim);
        d.position(dpos);
        s.position(spos);
    }

    public static void memcpy(ByteBuffer d, ByteBuffer s, int length) {
        int dpos = d.position();
        int spos = s.position();
        int dlim = d.limit();
        int slim = s.limit();
        s.limit(spos + length);
        d.limit(dpos + length);
        d.put(s);
        d.limit(dlim);
        s.limit(slim);
        d.position(dpos);
        s.position(spos);
    }

    public static ByteBuffer allocateDirect(int capacity) {
        ByteBuffer buf;
//        int tryNum = 3;
//        while ((tryNum--) > 0) {
        try {
            buf = ByteBuffer.allocateDirect(capacity);
//				System.out.println("Allocated " + capacity + " bytes");
//                break;
        } catch (OutOfMemoryError e) {
//                System.gc();
//                if (tryNum <= 0) {
            throw new OutOfMemoryError("Could not allocate the buffer. Fatal Error");
//                }
        }
//        }
        buf.order(ByteOrder.nativeOrder());
        return buf;
    }

    public static ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocate(capacity).order(ByteOrder.nativeOrder());
    }

//    public static ByteBuffer[] allocateDirectMemoryAligned(int capacity, int num) {
//        ByteBuffer[] byteBuffersNative = ENG_SilverBack.createByteBuffersNative(capacity, num);
//        for (int i = 0; i < num; ++i) {
//            byteBuffersNative[i].order(ByteOrder.nativeOrder());
//        }
//
//        return byteBuffersNative;
//    }

    /**
     * Will create a new buffer if needed, else return the same buffer.
     *
     * @param buf
     * @param newSize
     */
    public static ByteBuffer reallocBufferDirect(ByteBuffer buf, int newSize) {
        if (buf.capacity() < newSize) {
            return allocateDirect(newSize);
        }
        return buf;
    }

    /**
     * Will create a new buffer if needed, else return the same buffer.
     * @param buf
     * @param newSize
     */
//    public static void reallocBufferDirect(ENG_ByteBufferRing bufRing, int newSize) {
//        ByteBuffer[] bufList = bufRing.getBufList();
//        int length = bufList.length;
//        if (bufList[0].capacity() < newSize) {
//            for (int i = 0; i < length; ++i) {
//                bufList[i] = allocateDirect(newSize);
//            }
//        }
//    }

    /**
     * Will create a new buffer if needed, else return the same buffer.
     *
     * @param buf
     * @param newSize
     */
    public static ByteBuffer reallocBuffer(ByteBuffer buf, int newSize) {
        if (buf.capacity() < newSize) {
            return allocate(newSize);
        }
        return buf;
    }

    /**
     * Will create a new buffer if needed, else return the same buffer.
     *
     * @param buf
     * @param newSize
     */
//    public static ByteBuffer reallocBufferMemoryAligned(ByteBuffer buf, int newSize) {
//        if (buf.capacity() < newSize) {
//            return allocateDirectMemoryAligned(newSize, 1)[0];
//        }
//        return buf;
//    }
    public static ByteBuffer cloneBufferFull(ByteBuffer original) {
        int position = original.position();
        int limit = original.limit();
        original.position(0);
        original.limit(original.capacity());
        ByteBuffer clone = allocateDirect(original.capacity());
        clone.put(original);
        original.position(position);
        original.limit(limit);
        clone.position(position);
        clone.limit(limit);
        return clone;
    }

    public static ShortBuffer cloneBufferFull(ShortBuffer original) {
        int position = original.position();
        int limit = original.limit();
        original.position(0);
        original.limit(original.capacity());
        ShortBuffer clone = allocateDirect(original.capacity()).asShortBuffer();
        clone.put(original);
        original.position(position);
        original.limit(limit);
        clone.position(position);
        clone.limit(limit);
        return clone;
    }

    public static IntBuffer cloneBufferFull(IntBuffer original) {
        int position = original.position();
        int limit = original.limit();
        original.position(0);
        original.limit(original.capacity());
        IntBuffer clone = allocateDirect(original.capacity()).asIntBuffer();
        clone.put(original);
        original.position(position);
        original.limit(limit);
        clone.position(position);
        clone.limit(limit);
        return clone;
    }

    public static LongBuffer cloneBufferFull(LongBuffer original) {
        int position = original.position();
        int limit = original.limit();
        original.position(0);
        original.limit(original.capacity());
        LongBuffer clone = allocateDirect(original.capacity()).asLongBuffer();
        clone.put(original);
        original.position(position);
        original.limit(limit);
        clone.position(position);
        clone.limit(limit);
        return clone;
    }

    public static FloatBuffer cloneBufferFull(FloatBuffer original) {
        int position = original.position();
        int limit = original.limit();
        original.position(0);
        original.limit(original.capacity());
        FloatBuffer clone = allocateDirect(original.capacity()).asFloatBuffer();
        clone.put(original);
        original.position(position);
        original.limit(limit);
        clone.position(position);
        clone.limit(limit);
        return clone;
    }

    public static DoubleBuffer cloneBufferFull(DoubleBuffer original) {
        int position = original.position();
        int limit = original.limit();
        original.position(0);
        original.limit(original.capacity());
        DoubleBuffer clone = allocateDirect(original.capacity()).asDoubleBuffer();
        clone.put(original);
        original.position(position);
        original.limit(limit);
        clone.position(position);
        clone.limit(limit);
        return clone;
    }

    public static ByteBuffer cloneBuffer(ByteBuffer original) {
//		ByteBuffer clone = allocateDirect(original.capacity());
//		int position = original.position();
//		int limit = original.limit();
//		original.rewind();
//		original.limit(original.capacity());
//		clone.put(original);
//		original.position(position);
//		original.limit(limit);
//		clone.flip();
//		return clone;
        ByteBuffer clone = allocateDirect(original.remaining());
        int position = original.position();
        clone.put(original);
        original.position(position);
        clone.flip();
        return clone;
    }

    public static FloatBuffer cloneBuffer(FloatBuffer original) {
//		ByteBuffer temp = allocateDirect(original.capacity()
//				* ENG_Float.SIZE_IN_BYTES);
//		temp.order(original.order());
//		FloatBuffer clone = temp.asFloatBuffer();
//		int position = original.position();
//		int limit = original.limit();
//		original.rewind();
//		original.limit(original.capacity());
//		clone.put(original);
//		original.position(position);
//		original.limit(limit);
//		clone.flip();
//		return clone;
        ByteBuffer temp = allocateDirect(original.remaining()
                * ENG_Float.SIZE_IN_BYTES);
        temp.order(original.order());
        FloatBuffer clone = temp.asFloatBuffer();
        int position = original.position();
        clone.put(original);
        original.position(position);
        clone.flip();
        return clone;
    }

    public static IntBuffer cloneBuffer(IntBuffer original) {
//		ByteBuffer temp = allocateDirect(original.capacity()
//				* ENG_Integer.SIZE_IN_BYTES);
//		temp.order(original.order());
//		IntBuffer clone = temp.asIntBuffer();
//		int position = original.position();
//		int limit = original.limit();
//		original.rewind();
//		original.limit(original.capacity());
//		clone.put(original);
//		original.position(position);
//		original.limit(limit);
//		clone.flip();
//		return clone;
        ByteBuffer temp = allocateDirect(original.remaining()
                * ENG_Integer.SIZE_IN_BYTES);
        temp.order(original.order());
        IntBuffer clone = temp.asIntBuffer();
        int position = original.position();
        clone.put(original);
        original.position(position);
        clone.flip();
        return clone;
    }

    public static LongBuffer cloneBuffer(LongBuffer original) {
//		ByteBuffer temp = allocateDirect(original.capacity()
//				* ENG_Long.SIZE_IN_BYTES);
//		temp.order(original.order());
//		LongBuffer clone = temp.asLongBuffer();
//		int position = original.position();
//		int limit = original.limit();
//		original.rewind();
//		original.limit(original.capacity());
//		clone.put(original);
//		original.position(position);
//		original.limit(limit);
//		clone.flip();
//		return clone;
        ByteBuffer temp = allocateDirect(original.remaining()
                * ENG_Long.SIZE_IN_BYTES);
        temp.order(original.order());
        LongBuffer clone = temp.asLongBuffer();
        int position = original.position();
        clone.put(original);
        original.position(position);
        clone.flip();
        return clone;
    }

    public static ShortBuffer cloneBuffer(ShortBuffer original) {
//		ByteBuffer temp = allocateDirect(original.capacity()
//				* ENG_Short.SIZE_IN_BYTES);
//		temp.order(original.order());
//		ShortBuffer clone = temp.asShortBuffer();
//		int position = original.position();
//		int limit = original.limit();
//		original.rewind();
//		original.limit(original.capacity());
//		clone.put(original);
//		original.position(position);
//		original.limit(limit);
//		clone.flip();
//		return clone;
        ByteBuffer temp = allocateDirect(original.remaining()
                * ENG_Short.SIZE_IN_BYTES);
        temp.order(original.order());
        ShortBuffer clone = temp.asShortBuffer();
        int position = original.position();
        clone.put(original);
        original.position(position);
        clone.flip();
        return clone;
    }

    public static DoubleBuffer cloneBuffer(DoubleBuffer original) {
//		ByteBuffer temp = allocateDirect(original.capacity()
//				* ENG_Double.SIZE_IN_BYTES);
//		temp.order(original.order());
//		DoubleBuffer clone = temp.asDoubleBuffer();
//		int position = original.position();
//		int limit = original.limit();
//		original.rewind();
//		original.limit(original.capacity());
//		clone.put(original);
//		original.position(position);
//		original.limit(limit);
//		clone.flip();
//		return clone;
        ByteBuffer temp = allocateDirect(original.remaining()
                * ENG_Double.SIZE_IN_BYTES);
        temp.order(original.order());
        DoubleBuffer clone = temp.asDoubleBuffer();
        int position = original.position();
        clone.put(original);
        original.position(position);
        clone.flip();
        return clone;
    }

    public static byte[] getSubBuffer(byte[] b, int start, int end) {
        int len = end - start;
        byte[] ret = new byte[len];
        System.arraycopy(b, start, ret, 0, len);
        return ret;
    }

    public static short[] getSubBuffer(short[] b, int start, int end) {
        int len = end - start;
        short[] ret = new short[len];
        System.arraycopy(b, start, ret, 0, len);
        return ret;
    }

    public static int[] getSubBuffer(int[] b, int start, int end) {
        int len = end - start;
        int[] ret = new int[len];
        System.arraycopy(b, start, ret, 0, len);
        return ret;
    }

    public static long[] getSubBuffer(long[] b, int start, int end) {
        int len = end - start;
        long[] ret = new long[len];
        System.arraycopy(b, start, ret, 0, len);
        return ret;
    }

    public static float[] getSubBuffer(float[] b, int start, int end) {
        int len = end - start;
        float[] ret = new float[len];
        System.arraycopy(b, start, ret, 0, len);
        return ret;
    }

    public static double[] getSubBuffer(double[] b, int start, int end) {
        int len = end - start;
        double[] ret = new double[len];
        System.arraycopy(b, start, ret, 0, len);
        return ret;
    }

    public static boolean[] getSubBuffer(boolean[] b, int start, int end) {
        int len = end - start;
        boolean[] ret = new boolean[len];
        System.arraycopy(b, start, ret, 0, len);
        return ret;
    }

    public static ByteBuffer getByteArrayAsBuffer(byte[] b) {
        return getByteArrayAsBuffer(b, 0, b.length);
    }

    public static ShortBuffer getShortArrayAsBuffer(short[] b) {
        return getShortArrayAsBuffer(b, 0, b.length);
    }

    public static IntBuffer getIntArrayAsBuffer(int[] b) {
        return getIntArrayAsBuffer(b, 0, b.length);
    }

    public static LongBuffer getLongArrayAsBuffer(long[] b) {
        return getLongArrayAsBuffer(b, 0, b.length);
    }

    public static FloatBuffer getFloatArrayAsBuffer(float[] b) {
        return getFloatArrayAsBuffer(b, 0, b.length);
    }

    public static DoubleBuffer getDoubleArrayAsBuffer(double[] b) {
        return getDoubleArrayAsBuffer(b, 0, b.length);
    }

    public static ByteBuffer getBooleanArrayAsBuffer(boolean[] b) {
        return getBooleanArrayAsBuffer(b, 0, b.length);
    }

    public static ByteBuffer getByteArrayAsBuffer(byte[] b, int start, int end) {
        return wrapBuffer(getSubBuffer(b, start, end), false);
    }

    public static ShortBuffer getShortArrayAsBuffer(short[] b, int start, int end) {
        return wrapBuffer(getSubBuffer(b, start, end), false);
    }

    public static IntBuffer getIntArrayAsBuffer(int[] b, int start, int end) {
        return wrapBuffer(getSubBuffer(b, start, end), false);
    }

    public static LongBuffer getLongArrayAsBuffer(long[] b, int start, int end) {
        return wrapBuffer(getSubBuffer(b, start, end), false);
    }

    public static FloatBuffer getFloatArrayAsBuffer(float[] b, int start, int end) {
        return wrapBuffer(getSubBuffer(b, start, end), false);
    }

    public static DoubleBuffer getDoubleArrayAsBuffer(double[] b, int start, int end) {
        return wrapBuffer(getSubBuffer(b, start, end), false);
    }

    public static ByteBuffer getBooleanArrayAsBuffer(boolean[] b, int start, int end) {
        return wrapBuffer(getSubBuffer(b, start, end), false);
    }

    public static ByteBuffer checkBufferLen(ByteBuffer b) {
        if (b.remaining() / ENG_Byte.SIZE_IN_BYTES < LWJGL_LIMIT) {
            ByteBuffer buffer = allocateDirect(LWJGL_LIMIT);
            int p = b.position();
            buffer.put(b);
//			buffer.flip();
            buffer.position(0);
            b.position(p);
            b = buffer;
        }
        return b;
    }

    public static ShortBuffer checkBufferLen(ShortBuffer b) {
        if (b.remaining() / ENG_Short.SIZE_IN_BYTES < LWJGL_LIMIT) {
            ShortBuffer buffer = allocateDirect(LWJGL_LIMIT).asShortBuffer();
            int p = b.position();
            buffer.put(b);
//			buffer.flip();
            buffer.position(0);
            b.position(p);
            b = buffer;
        }
        return b;
    }

    public static IntBuffer checkBufferLen(IntBuffer b) {
        if (b.remaining() / ENG_Integer.SIZE_IN_BYTES < LWJGL_LIMIT) {
            IntBuffer buffer = allocateDirect(LWJGL_LIMIT).asIntBuffer();
            int p = b.position();
            buffer.put(b);
//			buffer.flip();
            buffer.position(0);
            b.position(p);
            b = buffer;
        }
        return b;
    }

    public static LongBuffer checkBufferLen(LongBuffer b) {
        if (b.remaining() / ENG_Long.SIZE_IN_BYTES < LWJGL_LIMIT) {
            LongBuffer buffer = allocateDirect(LWJGL_LIMIT).asLongBuffer();
            int p = b.position();
            buffer.put(b);
//			buffer.flip();
            buffer.position(0);
            b.position(p);
            b = buffer;
        }
        return b;
    }

    public static FloatBuffer checkBufferLen(FloatBuffer b) {
        if (b.remaining() / ENG_Float.SIZE_IN_BYTES < LWJGL_LIMIT) {
            FloatBuffer buffer = allocateDirect(LWJGL_LIMIT).asFloatBuffer();
            int p = b.position();
            buffer.put(b);
//			buffer.flip();
            buffer.position(0);
            b.position(p);
            b = buffer;
        }
        return b;
    }

    public static DoubleBuffer checkBufferLen(DoubleBuffer b) {
        if (b.remaining() / ENG_Double.SIZE_IN_BYTES < LWJGL_LIMIT) {
            DoubleBuffer buffer = allocateDirect(LWJGL_LIMIT).asDoubleBuffer();
            int p = b.position();
            buffer.put(b);
//			buffer.flip();
            buffer.position(0);
            b.position(p);
            b = buffer;
        }
        return b;
    }

    public static ByteBuffer wrapBuffer(byte[] b) {
        return wrapBuffer(b, true);
    }

    public static ByteBuffer wrapBuffer(byte[] b, boolean lwjglFix) {
        // Fuck this shit lwjgl idiots require a size of 16 at minimum because
        // they can't be bothered to write a switch() FUUUCK
        // Completely wrongly done here to override their retardness
        int len;
        if (lwjglFix) {
            len = b.length < LWJGL_LIMIT ? LWJGL_LIMIT : b.length;
        } else {
            len = b.length;
        }
        ByteBuffer buffer = allocateDirect(len);
        buffer.put(b);
//		buffer.flip();
        buffer.rewind();
        return buffer;
    }

    public static ByteBuffer wrapBuffer(boolean[] b) {
        return wrapBuffer(b, true);
    }

    public static ByteBuffer wrapBuffer(boolean[] b, boolean lwjglFix) {
        int len;
        if (lwjglFix) {
            len = b.length < LWJGL_LIMIT ? LWJGL_LIMIT : b.length;
        } else {
            len = b.length;
        }
        ByteBuffer buffer = allocateDirect(len);
        for (int i = 0; i < b.length; ++i) {
            buffer.put((byte) (b[i] ? 1 : 0));
        }
//		buffer.flip();
        buffer.rewind();
        return buffer;
    }

    public static ShortBuffer wrapBuffer(short[] b) {
        return wrapBuffer(b, true);
    }

    public static ShortBuffer wrapBuffer(short[] b, boolean lwjglFix) {
        int size = b.length * ENG_Short.SIZE_IN_BYTES;
        int len;
        if (lwjglFix) {
            len = size < LWJGL_LIMIT ? LWJGL_LIMIT : size;
        } else {
            len = size;
        }
        ShortBuffer buffer =
                allocateDirect(len).asShortBuffer();
        buffer.put(b);
//		buffer.flip();
        buffer.rewind();
        return buffer;
    }

    public static IntBuffer wrapBuffer(int[] b) {
        return wrapBuffer(b, true);
    }

    public static IntBuffer wrapBuffer(int[] b, boolean lwjglFix) {
        int size = b.length * ENG_Integer.SIZE_IN_BYTES;
        int len;
        if (lwjglFix) {
            len = size < LWJGL_LIMIT ? LWJGL_LIMIT : size;
        } else {
            len = size;
        }
        IntBuffer buffer =
                allocateDirect(len).asIntBuffer();
        buffer.put(b);
//		buffer.flip();
        buffer.rewind();
        return buffer;
    }

    public static LongBuffer wrapBuffer(long[] b) {
        return wrapBuffer(b, true);
    }

    public static LongBuffer wrapBuffer(long[] b, boolean lwjglFix) {
        int size = b.length * ENG_Long.SIZE_IN_BYTES;
        int len;
        if (lwjglFix) {
            len = size < LWJGL_LIMIT ? LWJGL_LIMIT : size;
        } else {
            len = size;
        }
        LongBuffer buffer =
                allocateDirect(len).asLongBuffer();
        buffer.put(b);
//		buffer.flip();
        buffer.rewind();
        return buffer;
    }

    public static FloatBuffer wrapBuffer(float[] b) {
        return wrapBuffer(b, true);
    }

    public static FloatBuffer wrapBuffer(float[] b, boolean lwjglFix) {
        int size = b.length * ENG_Float.SIZE_IN_BYTES;
        int len;
        if (lwjglFix) {
            len = size < LWJGL_LIMIT ? LWJGL_LIMIT : size;
        } else {
            len = size;
        }
        FloatBuffer buffer =
                allocateDirect(len).asFloatBuffer();
        buffer.put(b);
//		buffer.flip();
        buffer.rewind();
        return buffer;
    }

    public static DoubleBuffer wrapBuffer(double[] b) {
        return wrapBuffer(b, true);
    }

    public static DoubleBuffer wrapBuffer(double[] b, boolean lwjglFix) {
        int size = b.length * ENG_Double.SIZE_IN_BYTES;
        int len;
        if (lwjglFix) {
            len = size < LWJGL_LIMIT ? LWJGL_LIMIT : size;
        } else {
            len = size;
        }
        DoubleBuffer buffer =
                allocateDirect(len).asDoubleBuffer();
        buffer.put(b);
//		buffer.flip();
        buffer.rewind();
        return buffer;
    }

    public static String[] getStringAsPrimitiveArray(ArrayList<String> list) {
        int len = list.size();
        String[] array = new String[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static int[] getIntAsPrimitiveArray(ArrayList<ENG_Integer> list) {
        int len = list.size();
        int[] array = new int[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list.get(i).getValue();
        }
        return array;
    }

    public static IntBuffer getIntArrayAsBuffer(ArrayList<ENG_Integer> list) {
        int len = list.size();
        IntBuffer buffer =
                allocateDirect(len * ENG_Integer.SIZE_IN_BYTES).asIntBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list.get(i).getValue());
        }
        return buffer;
    }

    public static int[] getIntAsPrimitiveArray(ENG_Integer[] list) {
        int len = list.length;
        int[] array = new int[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[i].getValue();
        }
        return array;
    }

    public static IntBuffer getIntArrayAsBuffer(ENG_Integer[] list) {
        int len = list.length;
        IntBuffer buffer =
                allocateDirect(len * ENG_Integer.SIZE_IN_BYTES).asIntBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[i].getValue());
        }
        return buffer;
    }

    public static int[] getIntAsPrimitiveArray(ENG_Integer[] list,
                                               int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        int[] array = new int[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[beginOffset + i].getValue();
        }
        return array;
    }

    public static IntBuffer getIntArrayAsBuffer(ENG_Integer[] list,
                                                int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        IntBuffer buffer =
                allocateDirect(len * ENG_Integer.SIZE_IN_BYTES).asIntBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[beginOffset + i].getValue());
        }
        return buffer;
    }

    public static short[] getShortAsPrimitiveArray(ArrayList<ENG_Short> list) {
        int len = list.size();
        short[] array = new short[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list.get(i).getValue();
        }
        return array;
    }

    public static ShortBuffer getShortArrayAsBuffer(ArrayList<ENG_Short> list) {
        int len = list.size();
        ShortBuffer buffer =
                allocateDirect(len * ENG_Short.SIZE_IN_BYTES).asShortBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list.get(i).getValue());
        }
        return buffer;
    }

    public static short[] getShortAsPrimitiveArray(ENG_Short[] list) {
        int len = list.length;
        short[] array = new short[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[i].getValue();
        }
        return array;
    }

    public static ShortBuffer getShortArrayAsBuffer(ENG_Short[] list) {
        int len = list.length;
        ShortBuffer buffer =
                allocateDirect(len * ENG_Short.SIZE_IN_BYTES).asShortBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[i].getValue());
        }
        return buffer;
    }

    public static short[] getShortAsPrimitiveArray(ENG_Short[] list,
                                                   int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        short[] array = new short[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[beginOffset + i].getValue();
        }
        return array;
    }

    public static ShortBuffer getShortArrayAsBuffer(ENG_Short[] list,
                                                    int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        ShortBuffer buffer =
                allocateDirect(len * ENG_Short.SIZE_IN_BYTES).asShortBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[beginOffset + i].getValue());
        }
        return buffer;
    }

    public static byte[] getByteAsPrimitiveArray(ArrayList<ENG_Byte> list) {
        int len = list.size();
        byte[] array = new byte[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list.get(i).getValue();
        }
        return array;
    }

    public static ByteBuffer getByteArrayAsBuffer(ArrayList<ENG_Byte> list) {
        int len = list.size();
        ByteBuffer buffer =
                allocateDirect(len * ENG_Byte.SIZE_IN_BYTES);
        for (int i = 0; i < len; ++i) {
            buffer.put(list.get(i).getValue());
        }
        return buffer;
    }

    public static byte[] getByteAsPrimitiveArray(ENG_Byte[] list) {
        int len = list.length;
        byte[] array = new byte[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[i].getValue();
        }
        return array;
    }

    public static ByteBuffer getByteArrayAsBuffer(ENG_Byte[] list) {
        int len = list.length;
        ByteBuffer buffer =
                allocateDirect(len * ENG_Byte.SIZE_IN_BYTES);
        for (int i = 0; i < len; ++i) {
            buffer.put(list[i].getValue());
        }
        return buffer;
    }

    public static byte[] getByteAsPrimitiveArray(ENG_Byte[] list,
                                                 int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        byte[] array = new byte[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[beginOffset + i].getValue();
        }
        return array;
    }

    public static ByteBuffer getByteArrayAsBuffer(ENG_Byte[] list,
                                                  int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        ByteBuffer buffer =
                allocateDirect(len * ENG_Byte.SIZE_IN_BYTES);
        for (int i = 0; i < len; ++i) {
            buffer.put(list[beginOffset + i].getValue());
        }
        return buffer;
    }

    public static boolean[] getBooleanAsPrimitiveArray(
            ArrayList<ENG_Boolean> list) {
        int len = list.size();
        boolean[] array = new boolean[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list.get(i).getValue();
        }
        return array;
    }

    public static ByteBuffer getBooleanArrayAsBuffer(
            ArrayList<ENG_Boolean> list) {
        int len = list.size();
        ByteBuffer buffer =
                allocateDirect(len * ENG_Byte.SIZE_IN_BYTES);
        for (int i = 0; i < len; ++i) {
            buffer.put((byte) (list.get(i).getValue() ? 1 : 0));
        }
        return buffer;
    }

    public static boolean[] getBooleanAsPrimitiveArray(ENG_Boolean[] list) {
        int len = list.length;
        boolean[] array = new boolean[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[i].getValue();
        }
        return array;
    }

    public static ByteBuffer getBooleanArrayAsBuffer(
            ENG_Boolean[] list) {
        int len = list.length;
        ByteBuffer buffer =
                allocateDirect(len * ENG_Byte.SIZE_IN_BYTES);
        for (int i = 0; i < len; ++i) {
            buffer.put((byte) (list[i].getValue() ? 1 : 0));
        }
        return buffer;
    }

    public static boolean[] getBooleanAsPrimitiveArray(ENG_Boolean[] list,
                                                       int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        boolean[] array = new boolean[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[beginOffset + i].getValue();
        }
        return array;
    }

    public static ByteBuffer getBooleanArrayAsBuffer(
            ENG_Boolean[] list, int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        ByteBuffer buffer =
                allocateDirect(len * ENG_Byte.SIZE_IN_BYTES);
        for (int i = 0; i < len; ++i) {
            buffer.put((byte) (list[beginOffset + i].getValue() ? 1 : 0));
        }
        return buffer;
    }

    public static long[] getLongAsPrimitiveArray(ArrayList<ENG_Long> list) {
        int len = list.size();
        long[] array = new long[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list.get(i).getValue();
        }
        return array;
    }

    public static LongBuffer getLongArrayAsBuffer(
            ArrayList<ENG_Long> list) {
        int len = list.size();
        LongBuffer buffer =
                allocateDirect(len * ENG_Long.SIZE_IN_BYTES).asLongBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list.get(i).getValue());
        }
        return buffer;
    }

    public static long[] getLongAsPrimitiveArray(ENG_Long[] list) {
        int len = list.length;
        long[] array = new long[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[i].getValue();
        }
        return array;
    }

    public static LongBuffer getLongArrayAsBuffer(
            ENG_Long[] list) {
        int len = list.length;
        LongBuffer buffer =
                allocateDirect(len * ENG_Long.SIZE_IN_BYTES).asLongBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[i].getValue());
        }
        return buffer;
    }

    public static long[] getLongAsPrimitiveArray(ENG_Long[] list,
                                                 int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        long[] array = new long[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[beginOffset + i].getValue();
        }
        return array;
    }

    public static LongBuffer getLongArrayAsBuffer(
            ENG_Long[] list, int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        LongBuffer buffer =
                allocateDirect(len * ENG_Long.SIZE_IN_BYTES).asLongBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[beginOffset + i].getValue());
        }
        return buffer;
    }

    public static float[] getFloatAsPrimitiveArray(ArrayList<ENG_Float> list) {
        int len = list.size();
        float[] array = new float[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list.get(i).getValue();
        }
        return array;
    }

    public static FloatBuffer getFloatArrayAsBuffer(
            ArrayList<ENG_Float> list) {
        int len = list.size();
        FloatBuffer buffer =
                allocateDirect(len * ENG_Float.SIZE_IN_BYTES).asFloatBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list.get(i).getValue());
        }
        return buffer;
    }

    public static float[] getFloatAsPrimitiveArray(ENG_Float[] list) {
        int len = list.length;
        float[] array = new float[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[i].getValue();
        }
        return array;
    }

    public static FloatBuffer getFloatArrayAsBuffer(
            ENG_Float[] list) {
        int len = list.length;
        FloatBuffer buffer =
                allocateDirect(len * ENG_Float.SIZE_IN_BYTES).asFloatBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[i].getValue());
        }
        return buffer;
    }

    public static float[] getFloatAsPrimitiveArray(ENG_Float[] list,
                                                   int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        float[] array = new float[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[beginOffset + i].getValue();
        }
        return array;
    }

    public static FloatBuffer getFloatArrayAsBuffer(
            ENG_Float[] list, int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        FloatBuffer buffer =
                allocateDirect(len * ENG_Float.SIZE_IN_BYTES).asFloatBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[beginOffset + i].getValue());
        }
        return buffer;
    }

    public static double[] getDoubleAsPrimitiveArray(ArrayList<ENG_Double> list) {
        int len = list.size();
        double[] array = new double[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list.get(i).getValue();
        }
        return array;
    }

    public static DoubleBuffer getDoubleArrayAsBuffer(
            ArrayList<ENG_Double> list) {
        int len = list.size();
        DoubleBuffer buffer =
                allocateDirect(len * ENG_Double.SIZE_IN_BYTES).asDoubleBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list.get(i).getValue());
        }
        return buffer;
    }

    public static double[] getDoubleAsPrimitiveArray(ENG_Double[] list) {
        int len = list.length;
        double[] array = new double[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[i].getValue();
        }
        return array;
    }

    public static DoubleBuffer getDoubleArrayAsBuffer(
            ENG_Double[] list) {
        int len = list.length;
        DoubleBuffer buffer =
                allocateDirect(len * ENG_Double.SIZE_IN_BYTES).asDoubleBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[i].getValue());
        }
        return buffer;
    }

    public static double[] getDoubleAsPrimitiveArray(ENG_Double[] list,
                                                     int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        double[] array = new double[len];

        for (int i = 0; i < len; ++i) {
            array[i] = list[beginOffset + i].getValue();
        }
        return array;
    }

    public static DoubleBuffer getDoubleArrayAsBuffer(
            ENG_Double[] list, int beginOffset, int endOffset) {
        int len = endOffset - beginOffset;
        DoubleBuffer buffer =
                allocateDirect(len * ENG_Double.SIZE_IN_BYTES).asDoubleBuffer();
        for (int i = 0; i < len; ++i) {
            buffer.put(list[beginOffset + i].getValue());
        }
        return buffer;
    }

    public static void getIntPrimitiveArrayAsIntObjArray(int[] src,
                                                         int srcOffset, ENG_Integer[] dst, int dstOffset) {
        for (int i = srcOffset; i < src.length; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getIntPrimitiveArrayAsIntObjArray(IntBuffer src,
                                                         int srcOffset, ENG_Integer[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        for (int i = srcOffset; i < remaining; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getIntPrimitiveArrayAsIntObjArray(IntBuffer src,
                                                         int srcOffset, int[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        src.get(dst, dstOffset, dst.length - dstOffset);
    }

    public static void getIntPrimitiveArrayAsIntObjArray(int[] src,
                                                         int srcOffset, ENG_Integer[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getIntPrimitiveArrayAsIntObjArray(IntBuffer src,
                                                         int srcOffset, ENG_Integer[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getIntPrimitiveArrayAsIntObjArray(IntBuffer src,
                                                         int srcOffset, int[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        src.get(dst, dstOffset, len);
    }

    public static void getShortPrimitiveArrayAsShortObjArray(short[] src,
                                                             int srcOffset, ENG_Short[] dst, int dstOffset) {
        for (int i = srcOffset; i < src.length; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getShortPrimitiveArrayAsShortObjArray(ShortBuffer src,
                                                             int srcOffset, ENG_Short[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        for (int i = srcOffset; i < remaining; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getShortPrimitiveArrayAsShortObjArray(ShortBuffer src,
                                                             int srcOffset, short[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        src.get(dst, dstOffset, dst.length - dstOffset);
    }

    public static void getShortPrimitiveArrayAsShortObjArray(short[] src,
                                                             int srcOffset, ENG_Short[] dst, int dstOffset, int len) {
        /*
         * if (src.length - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getShortPrimitiveArrayAsShortObjArray(ShortBuffer src,
                                                             int srcOffset, ENG_Short[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getShortPrimitiveArrayAsShortObjArray(ShortBuffer src,
                                                             int srcOffset, short[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        src.get(dst, dstOffset, len);
    }

    public static void getBytePrimitiveArrayAsByteObjArray(byte[] src,
                                                           int srcOffset, ENG_Byte[] dst, int dstOffset) {
        for (int i = srcOffset; i < src.length; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getBytePrimitiveArrayAsByteObjArray(ByteBuffer src,
                                                           int srcOffset, ENG_Byte[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        for (int i = srcOffset; i < remaining; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getBytePrimitiveArrayAsByteObjArray(ByteBuffer src,
                                                           int srcOffset, byte[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        src.get(dst, dstOffset, dst.length - dstOffset);
    }

    public static void getBytePrimitiveArrayAsByteObjArray(byte[] src,
                                                           int srcOffset, ENG_Byte[] dst, int dstOffset, int len) {
        /*
         * if (src.length - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getBytePrimitiveArrayAsByteObjArray(ByteBuffer src,
                                                           int srcOffset, ENG_Byte[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getBytePrimitiveArrayAsByteObjArray(ByteBuffer src,
                                                           int srcOffset, byte[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        src.get(dst, dstOffset, len);
    }

    public static void getBooleanPrimitiveArrayAsBooleanObjArray(boolean[] src,
                                                                 int srcOffset, ENG_Boolean[] dst, int dstOffset) {
        for (int i = srcOffset; i < src.length; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getBooleanPrimitiveArrayAsBooleanObjArray(ByteBuffer src,
                                                                 int srcOffset, ENG_Boolean[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        for (int i = srcOffset; i < remaining; ++i) {
            dst[dstOffset++].setValue(src.get() != 0);
        }
    }

    public static void getBooleanPrimitiveArrayAsBooleanObjArray(ByteBuffer src,
                                                                 int srcOffset, boolean[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        for (int i = srcOffset; i < remaining; ++i) {
            dst[dstOffset++] = src.get() != 0;
        }
    }

    public static void getBooleanPrimitiveArrayAsBooleanObjArray(boolean[] src,
                                                                 int srcOffset, ENG_Boolean[] dst, int dstOffset, int len) {
        /*
         * if (src.length - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getBooleanPrimitiveArrayAsBooleanObjArray(ByteBuffer src,
                                                                 int srcOffset, ENG_Boolean[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src.get() != 0);
        }
    }

    public static void getBooleanPrimitiveArrayAsBooleanObjArray(ByteBuffer src,
                                                                 int srcOffset, boolean[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++] = src.get() != 0;
        }
    }

    public static void getLongPrimitiveArrayAsLongObjArray(long[] src,
                                                           int srcOffset, ENG_Long[] dst, int dstOffset) {
        for (int i = srcOffset; i < src.length; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getLongPrimitiveArrayAsLongObjArray(LongBuffer src,
                                                           int srcOffset, ENG_Long[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        for (int i = srcOffset; i < remaining; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getLongPrimitiveArrayAsLongObjArray(LongBuffer src,
                                                           int srcOffset, long[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        src.get(dst, dstOffset, dst.length - dstOffset);
    }

    public static void getLongPrimitiveArrayAsLongObjArray(long[] src,
                                                           int srcOffset, ENG_Long[] dst, int dstOffset, int len) {
        /*
         * if (src.length - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getLongPrimitiveArrayAsLongObjArray(LongBuffer src,
                                                           int srcOffset, ENG_Long[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getLongPrimitiveArrayAsLongObjArray(LongBuffer src,
                                                           int srcOffset, long[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        src.get(dst, dstOffset, len);
    }

    public static void getFloatPrimitiveArrayAsFloatObjArray(float[] src,
                                                             int srcOffset, ENG_Float[] dst, int dstOffset) {
        for (int i = srcOffset; i < src.length; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getFloatPrimitiveArrayAsFloatObjArray(FloatBuffer src,
                                                             int srcOffset, ENG_Float[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        for (int i = srcOffset; i < remaining; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getFloatPrimitiveArrayAsFloatObjArray(FloatBuffer src,
                                                             int srcOffset, float[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        src.get(dst, dstOffset, dst.length - dstOffset);
    }

    public static void getFloatPrimitiveArrayAsFloatObjArray(float[] src,
                                                             int srcOffset, ENG_Float[] dst, int dstOffset, int len) {
        /*
         * if (src.length - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getFloatPrimitiveArrayAsFloatObjArray(FloatBuffer src,
                                                             int srcOffset, ENG_Float[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getFloatPrimitiveArrayAsFloatObjArray(FloatBuffer src,
                                                             int srcOffset, float[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        src.get(dst, dstOffset, len);
    }

    public static void getDoublePrimitiveArrayAsDoubleObjArray(double[] src,
                                                               int srcOffset, ENG_Double[] dst, int dstOffset) {
        for (int i = srcOffset; i < src.length; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void geDoublePrimitiveArrayAsDoubleObjArray(DoubleBuffer src,
                                                              int srcOffset, ENG_Double[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        for (int i = srcOffset; i < remaining; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void geDoublePrimitiveArrayAsDoubleObjArray(DoubleBuffer src,
                                                              int srcOffset, double[] dst, int dstOffset) {
        src.position(srcOffset);
        int remaining = src.remaining();
        src.get(dst, dstOffset, dst.length - dstOffset);
    }

    public static void getDoublePrimitiveArrayAsDoubleObjArray(double[] src,
                                                               int srcOffset, ENG_Double[] dst, int dstOffset, int len) {
        /*
         * if (src.length - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src[i]);
        }
    }

    public static void getDoublePrimitiveArrayAsDoubleObjArray(DoubleBuffer src,
                                                               int srcOffset, ENG_Double[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src.get());
        }
    }

    public static void getDoublePrimitiveArrayAsDoubleObjArray(DoubleBuffer src,
                                                               int srcOffset, double[] dst, int dstOffset, int len) {
        /*
         * if (srcLen - srcOffset != dst.length - dstOffset) { throw new
         * IllegalArgumentException("src - srcOffset must be equal " +
         * "to dst - dstOffset"); }
         */
        src.position(srcOffset);
        src.get(dst, dstOffset, len);
    }

    public static ENG_Integer[] convertArrayFromBooleanPrimitiveToIntObj(
            boolean[] src, int srcOffset, int len) {
        ENG_Integer[] array = ENG_Integer.createArray(len);
        convertArrayFromBooleanPrimitiveToIntObj(src, srcOffset, array, 0, len);
        return array;
    }

    public static void convertArrayFromBooleanPrimitiveToIntObj(boolean[] src,
                                                                int srcOffset, ENG_Integer[] dst, int dstOffset, int len) {
        for (int i = srcOffset; i < srcOffset + len; ++i) {
            dst[dstOffset++].setValue(src[i] ? 1 : 0);
        }
    }

    public static ByteBuffer extendArrayDirect(ByteBuffer buf, int newLen) {
        ByteBuffer newBuf = allocateDirect(newLen);
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static ShortBuffer extendArrayDirect(ShortBuffer buf, int newLen) {
        ShortBuffer newBuf = allocateDirect(newLen).asShortBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static IntBuffer extendArrayDirect(IntBuffer buf, int newLen) {
        IntBuffer newBuf = allocateDirect(newLen).asIntBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static LongBuffer extendArrayDirect(LongBuffer buf, int newLen) {
        LongBuffer newBuf = allocateDirect(newLen).asLongBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static FloatBuffer extendArrayDirect(FloatBuffer buf, int newLen) {
        FloatBuffer newBuf = allocateDirect(newLen).asFloatBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static DoubleBuffer extendArrayDirect(DoubleBuffer buf, int newLen) {
        DoubleBuffer newBuf = allocateDirect(newLen).asDoubleBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static ByteBuffer extendArray(ByteBuffer buf, int newLen) {
        ByteBuffer newBuf = allocate(newLen);
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static ShortBuffer extendArray(ShortBuffer buf, int newLen) {
        ShortBuffer newBuf = allocate(newLen).asShortBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static IntBuffer extendArray(IntBuffer buf, int newLen) {
        IntBuffer newBuf = allocate(newLen).asIntBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static LongBuffer extendArray(LongBuffer buf, int newLen) {
        LongBuffer newBuf = allocate(newLen).asLongBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static FloatBuffer extendArray(FloatBuffer buf, int newLen) {
        FloatBuffer newBuf = allocate(newLen).asFloatBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    public static DoubleBuffer extendArray(DoubleBuffer buf, int newLen) {
        DoubleBuffer newBuf = allocate(newLen).asDoubleBuffer();
        int oldPos = buf.position();
        int oldLimit = buf.limit();
        buf.position(0);
        buf.limit(buf.capacity());
        newBuf.put(buf);
        buf.position(oldPos);
        buf.limit(oldLimit);
        newBuf.position(oldPos);
        newBuf.limit(oldLimit);
        return newBuf;
    }

    // For all types
    public static float[] extendArray(float[] f, int newLen) {
        float[] newf = new float[newLen];
        System.arraycopy(f, 0, newf, 0, f.length);
        return newf;
    }

    public static double[] extendArray(double[] i, int newLen) {
        double[] newi = new double[newLen];
        System.arraycopy(i, 0, newi, 0, i.length);
        return newi;
    }

    public static int[] extendArray(int[] i, int newLen) {
        int[] newi = new int[newLen];
        System.arraycopy(i, 0, newi, 0, i.length);
        return newi;
    }

    public static long[] extendArray(long[] i, int newLen) {
        long[] newi = new long[newLen];
        System.arraycopy(i, 0, newi, 0, i.length);
        return newi;
    }

    public static byte[] extendArray(byte[] i, int newLen) {
        byte[] newi = new byte[newLen];
        System.arraycopy(i, 0, newi, 0, i.length);
        return newi;
    }

    public static short[] extendArray(short[] i, int newLen) {
        short[] newi = new short[newLen];
        System.arraycopy(i, 0, newi, 0, i.length);
        return newi;
    }

    public static boolean[] extendArray(boolean[] i, int newLen) {
        boolean[] newi = new boolean[newLen];
        System.arraycopy(i, 0, newi, 0, i.length);
        return newi;
    }

    public static float rangeRandom(float min, float max) {
        return rangeRandom(null, min, max);
    }

    public static float rangeRandom(String s, float min, float max) {
        return getRandom().nextFloat(s) * (max - min) + min;
    }

    /**
     * @return the random number between -1.0f and 1.0f.
     */
    public static float symmetricRandom() {
        return symmetricRandom(null);
    }

    /**
     * @param s
     * @return the random number between -1.0f and 1.0f.
     */
    public static float symmetricRandom(String s) {
        return 2.0f * getRandom().nextFloat(s) - 1.0f;
    }

    public static ENG_Random getRandom() {
        return random;
    }

    public static String extractThreadNameWithStacktrace() {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stackTrace) {
            sb.append(e.toString()).append("\n");
        }
        return Thread.currentThread().getName() + " " + sb.toString();
    }

    public enum Endianness {
        LITTLE_ENDIAN, BIG_ENDIAN
    }

    public static Endianness getEndianness() {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            return Endianness.LITTLE_ENDIAN;
        }
        return Endianness.BIG_ENDIAN;
    }

    // Since we can end up using hundreds of textures
    // for an animation and 3ds max exports the anim
    // in multiple pics with format _0039 we have
    // to add 0 in front to get the correct texture name
    // It's used in ENG_MaterialLoader and ENG_TextureUnitState to determine
    // animation
    // names
    public static String getAheadZeros(int numZeros) {
        String aheadZeros = "000";
        // int numZeros = i / 10;
        while (numZeros / 10 != 0) {
            numZeros /= 10;
            aheadZeros = aheadZeros.substring(0, aheadZeros.length() - 1);
        }
        return aheadZeros;
    }

    public static boolean hasRandomChanceHit(int val) {
        return hasRandomChanceHit(null, val);
    }

    public static boolean hasRandomChanceHit(String s, int val) {
//        if (s != null && MainApp.getMainThread().isInputState()) {
//            ENG_Frame currentFrame = MainApp.getMainThread().getDebuggingState().getCurrentFrame();
//            ENG_FrameInterval currentFrameInterval = currentFrame.getCurrentFrameInterval();
//            Object object = currentFrameInterval.getObject(s);
//            // Haven't reached a frame where this might be true.
//            return object != null && (boolean) object;
//        }
        boolean b = getRandom().nextInt(val) == 0;
//        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
//            if (s != null) {
//                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
//                currentFrameInterval.addObject(s, b);
//            }
//        }
        return b;
    }

    public static boolean hasTimePassed(long beginTime, long waitTime) {
        return hasTimePassed(null, beginTime, waitTime);
    }

    public static boolean hasTimePassed(String s, long beginTime, long waitTime) {
//        if (s != null && MainApp.getMainThread().isInputState()) {
//            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
//            return (boolean) currentFrameInterval.getObject(s);
//        }
        boolean b = currentTimeMillis() - beginTime > waitTime;
//        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
//            if (s != null) {
//                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
//                currentFrameInterval.addObject(s, b);
//            }
//        }
        return b;
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long nanoTime() {
        return System.nanoTime();
    }

    private static final ENG_Vector4D transformedEntityVelocity = new ENG_Vector4D();
    private static final ENG_Vector4D finalEntityVelocity = new ENG_Vector4D();
    // private ENG_Vector4D targetPos4D = new ENG_Vector4D();
    private static final ENG_Vector4D finalTargetPos4D = new ENG_Vector4D();
    private static final ENG_Vector4D tempVelocity = new ENG_Vector4D();

    // private ENG_Vector4D entityVelocity = new ENG_Vector4D();
    // private ENG_Quaternion entityOrientation = new ENG_Quaternion();

    public static boolean calculateCollisionPosition(float projectileSpeed,
                                                     ENG_Matrix4 inverseWorldMatrix, ENG_Vector4D crossPosition,
                                                     ENG_Vector4D entityVelocity, ENG_Quaternion entityOrientation,
                                                     ENG_Vector4D targetPos4D) {
        return calculateCollisionPosition(projectileSpeed, inverseWorldMatrix,
                crossPosition, entityVelocity, entityOrientation, targetPos4D,
                transformedEntityVelocity, finalEntityVelocity,
                finalTargetPos4D, tempVelocity);
    }

    public static boolean calculateCollisionPosition(
            // EntityProperties entityProperties,
            double projectileSpeed, ENG_Matrix4 inverseWorldMatrix,
            ENG_Vector4D crossPosition, ENG_Vector4D entityVelocity,
            ENG_Quaternion entityOrientation, ENG_Vector4D targetPos4D,

            // Temp vectors
            ENG_Vector4D transformedEntityVelocity,
            ENG_Vector4D finalEntityVelocity,

            ENG_Vector4D finalTargetPos4D, ENG_Vector4D tempVelocity) {
        // double projectileSpeed = weaponData.maxSpeed;
        // entityProperties.getVelocityAsVec(entityVelocity);
        // entityProperties.getNode().getOrientation(entityOrientation);
        // entityProperties.getNode().getPosition(targetPos4D);
        entityOrientation.mul(entityVelocity, transformedEntityVelocity);
        inverseWorldMatrix.transform(targetPos4D, finalTargetPos4D);
        inverseWorldMatrix.transform(transformedEntityVelocity,
                finalEntityVelocity);

        double a = finalEntityVelocity.dotProduct(finalEntityVelocity)
                - ENG_Math.sqr(projectileSpeed);
        double b = 2.0f * finalEntityVelocity.dotProduct(finalTargetPos4D);
        double c = finalTargetPos4D.dotProduct(finalTargetPos4D);

        double p = -b / (2 * a);
        double result = (b * b) - 4 * a * c;
        if (result < 0.0f) {
            return false;
        }
        double q = ENG_Math.sqrt(result) / (2 * a);

        double t1 = p - q;
        double t2 = p + q;
        double t;

        if (t1 > t2 && t2 > 0) {
            t = t2;
        } else {
            t = t1;
        }
        if (t < 0.0f) {
            return false;
        }
//        if (MainActivity.isDebugmode()) {
//            if (Float.isInfinite(t) || Float.isNaN(t)) {
//                System.out.println("time is " + t + " a: " + a + " b: " + b
//                        + " c: " + c + " entityVelocity: " + entityVelocity
//                        + " transformedEntityVelocity: "
//                        + transformedEntityVelocity + " targetPos4D: "
//                        + targetPos4D + " finalTargetPos4D: "
//                        + finalTargetPos4D + " finalEntityVelocity: "
//                        + finalEntityVelocity);
//            }
//        }
        tempVelocity.set(finalEntityVelocity);
        tempVelocity.mul(t);
        finalTargetPos4D.add(tempVelocity, crossPosition);
        return true;
    }

    public static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    public static void memset(ByteBuffer buf, byte v, int pos, int limit) {
        int oldLimit = buf.limit();
        buf.limit(limit);
        int oldPos = buf.position();
        buf.position(pos);
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.limit(oldLimit);
        buf.position(oldPos);
    }

    public static void memset(ShortBuffer buf, short v, int pos, int limit) {
        int oldLimit = buf.limit();
        buf.limit(limit);
        int oldPos = buf.position();
        buf.position(pos);
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.limit(oldLimit);
        buf.position(oldPos);
    }

    public static void memset(IntBuffer buf, int v, int pos, int limit) {
        int oldLimit = buf.limit();
        buf.limit(limit);
        int oldPos = buf.position();
        buf.position(pos);
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.limit(oldLimit);
        buf.position(oldPos);
    }

    public static void memset(LongBuffer buf, long v, int pos, int limit) {
        int oldLimit = buf.limit();
        buf.limit(limit);
        int oldPos = buf.position();
        buf.position(pos);
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.limit(oldLimit);
        buf.position(oldPos);
    }

    public static void memset(FloatBuffer buf, float v, int pos, int limit) {
        int oldLimit = buf.limit();
        buf.limit(limit);
        int oldPos = buf.position();
        buf.position(pos);
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.limit(oldLimit);
        buf.position(oldPos);
    }

    public static void memset(DoubleBuffer buf, double v, int pos, int limit) {
        int oldLimit = buf.limit();
        buf.limit(limit);
        int oldPos = buf.position();
        buf.position(pos);
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.limit(oldLimit);
        buf.position(oldPos);
    }

    public static void memset(ByteBuffer buf, byte v) {
        buf.rewind();
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.rewind();
    }

    public static void memset(IntBuffer buf, int v) {
        buf.rewind();
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.rewind();
    }

    public static void memset(LongBuffer buf, long v) {
        buf.rewind();
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.rewind();
    }

    public static void memset(ShortBuffer buf, short v) {
        buf.rewind();
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.rewind();
    }

    public static void memset(FloatBuffer buf, float v) {
        buf.rewind();
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.rewind();
    }

    public static void memset(DoubleBuffer buf, double v) {
        buf.rewind();
        while (buf.hasRemaining()) {
            buf.put(v);
        }
        buf.rewind();
    }

    public static void alignMemory(ByteBuffer buf, int align) {
        int offset = buf.position() % align;
        if (offset > 0) {
            buf.position(buf.position() + align - offset);
        }

    }

//    public static void convertFromScreenSpaceToPixels(float x, float y, ENG_Vector2D ret) {
//        ENG_RenderWindow renderWindow = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
//        int width = renderWindow.getWidth();
//        int height = renderWindow.getHeight();
//        float xPos = (x * width);
//        float yPos;
//        if (MainApp.PLATFORM == MainApp.Platform.DESKTOP && MainApp.DESKTOP_PLATFORM == MainApp.DesktopPlatform.WIN32) {
//            // There is some weird thing going on where y is reversed for some reason. Investigate if you have time.
//            yPos = height - (-y * height);
//        } else {
//            yPos = (y * height);
//        }
//        xPos = ENG_Math.clamp(xPos, 0, width);
//        yPos = ENG_Math.clamp(yPos, 0, height);
//        ret.set(xPos, yPos);
//    }

//    public static ENG_Vector2D convertFromScreenSpaceToPixels(float x, float y) {
//        ENG_Vector2D ret = new ENG_Vector2D();
//        convertFromScreenSpaceToPixels(x, y, ret);
//        return ret;
//    }
//
//    public static ENG_Vector2D convertPixelsToScreenSpace(float x, float y) {
//        ENG_Vector2D ret = new ENG_Vector2D();
//        convertPixelsToScreenSpace(x, y, ret);
//        return ret;
//    }
//
//    public static void convertPixelsToScreenSpace(float x, float y, ENG_Vector2D ret) {
//        ENG_RenderWindow renderWindow = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
//        float inv_width = 1.0f / ((float) renderWindow.getWidth());
//        float inv_height = 1.0f / ((float) renderWindow.getHeight());
//        convertPixelsToScreenSpace(x, y, inv_width, inv_height, ret);
//    }
//
//    public static ENG_Vector2D convertPixelsToScreenSpace(float x, float y, float inv_width, float inv_height) {
//        ENG_Vector2D ret = new ENG_Vector2D();
//        convertPixelsToScreenSpace(x, y, inv_width, inv_height, ret);
//        return ret;
//    }
//
//    public static void convertPixelsToScreenSpace(float x, float y, float inv_width, float inv_height, ENG_Vector2D ret) {
//        ret.x = x * inv_width;
//        ret.y = y * inv_height;
//        ret.x = ENG_Math.clamp(ret.x, 0.0f, 1.0f);
//        ret.y = ENG_Math.clamp(ret.y, 0.0f, 1.0f);
//    }

    public static class Timer {
        public long beginTime, endTime, timeDiff;

        public Timer() {

        }

        public Timer(boolean start) {
            if (start) {
                beginTime = currentTimeMillis();
            }
        }
    }

    public static Timer createTimerAndStart() {
        return new Timer(true);
    }

    public static String stopTimer(Timer timer) {
        return stopTimer(timer, null);
    }

    public static String stopTimer(Timer timer, String s) {
        timer.endTime = currentTimeMillis();
        timer.timeDiff = timer.endTime - timer.beginTime;
        String t;
        if (s != null) {
            t = s + " timeDiff: " + timer.timeDiff;
        } else {
            t = "timeDiff: " + timer.timeDiff;
        }
        System.out.println(t);
        return t;
    }

//    public static Rect convertFromScreenPercentageToActualPixels(ENG_RealRect position) {
//        Rect rect = new Rect();
//        convertFromScreenPercentageToActualPixels(position, rect);
//        return rect;
//    }
//
//    public static void convertFromScreenPercentageToActualPixels(ENG_RealRect position, Rect ret) {
//        ENG_Viewport viewport =
//                ENG_RenderRoot.getRenderRoot()
//                        .getCurrentRenderWindow().getViewport(0);
//        ENG_Vector2D screenSize = new ENG_Vector2D(viewport.getActualWidth(), viewport.getActualHeight());
//        convertFromScreenPercentageToActualPixels(position, screenSize, ret);
//    }
//
//    public static void convertFromScreenPercentageToActualPixels(ENG_RealRect position, ENG_Vector2D screenSize, Rect ret) {
//        float screenWidth = screenSize.x;
//        float screenHeight = screenSize.y;
//        ret.left = (int) ENG_Math.floor(screenWidth * position.left / 100.0f);
//        ret.right = (int) ENG_Math.floor(screenWidth * position.right / 100.0f);
//        ret.top = (int) ENG_Math.floor(screenHeight * position.top / 100.0f);
//        ret.bottom = (int) ENG_Math.floor(screenHeight * position.bottom / 100.0f);
//    }
//
//    public static ENG_RealRect convertFromActualPixelsToScreenPercentage(Rect position) {
//        ENG_RealRect ret = new ENG_RealRect();
//        convertFromActualPixelsToScreenPercentage(position, ret);
//        return ret;
//    }
//
//    public static void convertFromActualPixelsToScreenPercentage(Rect position, ENG_RealRect ret) {
//        ENG_Viewport viewport = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getViewport(0);
//        ENG_Vector2D screenSize = new ENG_Vector2D(viewport.getActualWidth(), viewport.getActualHeight());
//        convertFromActualPixelsToScreenPercentage(position, screenSize, ret);
//    }
//
//    public static void convertFromActualPixelsToScreenPercentage(Rect position, ENG_Vector2D screenSize, ENG_RealRect ret) {
//        float screenWidth = screenSize.x;
//        float screenHeight = screenSize.y;
//        ret.left = position.left / screenWidth * 100.0f;
//        ret.right = position.right / screenWidth * 100.0f;
//        ret.top = position.top / screenHeight * 100.0f;
//        ret.bottom = position.bottom / screenHeight * 100.0f;
//    }

    public static int getNumberOfCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int shiftBitToLeft(int pos) {
        return 1 << pos;
    }

    public static int shiftBitToRightSignExtended(int val, int shiftNum) {
        return val >> shiftNum;
    }

    public static int shiftBitToRightNoSignExtension(int val, int shiftNum) {
        return val >>> shiftNum;
    }

    private static PrintStream consolePrintWriter;

//    public static void setPrintlnOut(String outputFile) {
//        FileHandle handle = Gdx.files.local(outputFile);
//        PrintStream printStream = new PrintStream(handle.write(false));
//        if (consolePrintWriter != null) {
//            throw new IllegalStateException("setOut() has already been set once!");
//        }
//        consolePrintWriter = System.out;
//        System.setOut(printStream);
//    }

    public static void restorePrintlnOut() {
        if (consolePrintWriter == null) {
            throw new IllegalStateException("Nothing to restore");
        }
        System.setOut(consolePrintWriter);
        consolePrintWriter = null;
    }

    public static boolean isConsolePrintWriterOverwritten() {
        return consolePrintWriter != null;
    }

    public static ENG_Vector2D interpolate(long beginTime, long duration, long currentTime, ENG_Vector2D beginPos, ENG_Vector2D endPos) {
        ENG_Vector2D ret = new ENG_Vector2D();
        interpolate(beginTime, duration, currentTime, beginPos, endPos, ret);
        return ret;
    }

    public static void interpolate(long beginTime, long duration, long currentTime, ENG_Vector2D beginPos, ENG_Vector2D endPos, ENG_Vector2D ret) {
        long timeDiff = currentTime - beginTime;
        if (timeDiff < 0) {
            timeDiff = 0;
        }
        float percent = (float) timeDiff / (float) duration;
        endPos.sub(beginPos, ret);
        ret.mulInPlace(percent);
        ret.addInPlace(beginPos);
    }

    public static ENG_Vector3D interpolate(long beginTime, long duration, long currentTime, ENG_Vector3D beginPos, ENG_Vector3D endPos) {
        ENG_Vector3D ret = new ENG_Vector3D();
        interpolate(beginTime, duration, currentTime, beginPos, endPos, ret);
        return ret;
    }

    public static void interpolate(long beginTime, long duration, long currentTime, ENG_Vector3D beginPos, ENG_Vector3D endPos, ENG_Vector3D ret) {
        long timeDiff = currentTime - beginTime;
        if (timeDiff < 0) {
            timeDiff = 0;
        }
        float percent = (float) timeDiff / (float) duration;
        endPos.sub(beginPos, ret);
        ret.mulInPlace(percent);
        ret.addInPlace(beginPos);
    }

    public static ENG_Vector4D interpolate(long beginTime, long duration, long currentTime, ENG_Vector4D beginPos, ENG_Vector4D endPos) {
        ENG_Vector4D ret = new ENG_Vector4D();
        interpolate(beginTime, duration, currentTime, beginPos, endPos, ret);
        return ret;
    }

    public static void interpolate(long beginTime, long duration, long currentTime, ENG_Vector4D beginPos, ENG_Vector4D endPos, ENG_Vector4D ret) {
        long timeDiff = currentTime - beginTime;
        if (timeDiff < 0) {
            timeDiff = 0;
        }
        float percent = (float) timeDiff / (float) duration;
        endPos.sub(beginPos, ret);
        ret.mul(percent);
        ret.addInPlace(beginPos);
    }

    public static ENG_Quaternion interpolateSlerp(long beginTime, long duration, long currentTime,
                                                  ENG_Quaternion beginOrientation, ENG_Quaternion endOrientation) {
        long timeDiff = currentTime - beginTime;
        if (timeDiff < 0) {
            timeDiff = 0;
        }
        float percent = (float) timeDiff / (float) duration;
        return ENG_Quaternion.slerp(percent, beginOrientation, endOrientation, true);
    }

    public static void interpolateSlerp(long beginTime, long duration, long currentTime,
                                        ENG_Quaternion beginOrientation, ENG_Quaternion endOrientation, ENG_Quaternion ret) {
        long timeDiff = currentTime - beginTime;
        if (timeDiff < 0) {
            timeDiff = 0;
        }
        float percent = (float) timeDiff / (float) duration;
        ENG_Quaternion.slerp(percent, beginOrientation, endOrientation, true, ret);
    }
}
