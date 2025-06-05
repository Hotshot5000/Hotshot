/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.test;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.exception.ENG_MultipleSingletonConstructAttemptException;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_IdString;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderedFrameListener;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderedFrameListenerWithBufferCopy;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderedFrameListenerWithBufferCopyWithLock;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 13.02.2017.
 */

public class TestRenderingThread extends Thread {

    public static final boolean TEST = false;
    public static final boolean TEST_ENABLE_TRACING = false;
    private static final int REPEAT_NUM = 50;
    private static TestRenderingThread singleton;
    private final EnumSet<Tests> runningTests = EnumSet.of(

            Tests.BLOCKING_TEST, Tests.MULTIPLE_CALLS, Tests.MULTIPLE_CALLS_W_RESPONSE_BLOCKING,
            Tests.MULTIPLE_CALLS_W_RESPONSE, Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_BLOCKING,
            Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT, Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_1,
            Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_2, Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_3,
            Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_4, Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_5,
            Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_6,

//            Tests.IDSTRING
            Tests.STRESS_TEST,
//            Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_3,
//            Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_4,
//            Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_5,
//            Tests.MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_6

            Tests.FLUSH_AND_BLOCK_FOR_RESULT, Tests.FLUSH_INCOMPLETE_AND_BLOCK_FOR_RESULT,
            Tests.FLUSH_UNTIL_BLOCK_AND_WAIT_FOR_RESULT, Tests.FLUSH_UNTIL_BLOCK_AND_FLUSH_INCOMPLETE_AND_BLOCK_FOR_RESULT,
            Tests.FLUSH_WITH_WAIT_FOR_FIRST_BUFFER, Tests.FLUSH_WITH_WAIT_FOR_SECOND_BUFFER, Tests.FLUSH_WITH_WAIT_FOR_THIRD_BUFFER,
            Tests.FLUSH_WITH_END_FRAME_COMBINATIONS, Tests.FLUSH_WITH_END_FRAME_COMBINATIONS_WITH_ARRAYS,
            Tests.FLUSH_AND_BLOCK_FOR_RESULT_ADD_SLOW_CALL, Tests.FLUSH_INCOMPLETE_AND_BLOCK_AND_ADD_SLOW_CALL,
            Tests.CHECK_RENDERING_THREAD_SLEEP, Tests.FLUSH_INCOMPLETE_STRESS_TEST
    );//allOf(Tests.class);
    private final ReentrantLock waitTimeLock = new ReentrantLock();
    private long waitTime;
    private final ReentrantLock testEndLatchLock = new ReentrantLock();
    private CountDownLatch testEndLatch;

    private long[] ptr1, ptr2, ptr3, ptr4;
    private long[] ptrArray1, ptrArray2, ptrArray3, ptrArray4;

    private static class CallRenderedFrameListener extends ENG_RenderedFrameListener {

        public CallRenderedFrameListener(String s) {
        }

        @Override
        public void frameEnded(ByteBuffer responseBuffer) {
//            System.out.println("Call " + message + " returned");
        }
    }

    private enum Tests {
        BLOCKING_TEST, MULTIPLE_CALLS, MULTIPLE_CALLS_W_RESPONSE_BLOCKING,
        MULTIPLE_CALLS_W_RESPONSE, MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_BLOCKING,
        MULTIPLE_CALLS_W_RESPONSE_AND_WAIT, MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_1,
        MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_2, MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_3,
        MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_4, MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_5,
        MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_6,
        STRESS_TEST, IDSTRING,

        FLUSH_AND_BLOCK_FOR_RESULT, FLUSH_INCOMPLETE_AND_BLOCK_FOR_RESULT,
        FLUSH_UNTIL_BLOCK_AND_WAIT_FOR_RESULT, FLUSH_UNTIL_BLOCK_AND_FLUSH_INCOMPLETE_AND_BLOCK_FOR_RESULT,
        FLUSH_WITH_WAIT_FOR_FIRST_BUFFER, FLUSH_WITH_WAIT_FOR_SECOND_BUFFER, FLUSH_WITH_WAIT_FOR_THIRD_BUFFER,
        FLUSH_WITH_END_FRAME_COMBINATIONS, FLUSH_WITH_END_FRAME_COMBINATIONS_WITH_ARRAYS,
        FLUSH_AND_BLOCK_FOR_RESULT_ADD_SLOW_CALL, FLUSH_INCOMPLETE_AND_BLOCK_AND_ADD_SLOW_CALL,
        CHECK_RENDERING_THREAD_SLEEP, FLUSH_INCOMPLETE_STRESS_TEST
    }

    public TestRenderingThread() {
        if (singleton == null) {
            singleton = this;
        } else {
            throw new ENG_MultipleSingletonConstructAttemptException();
        }
    }

    private void call1() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL1.getCallPos());
        ENG_RenderingThread.addFrameEndListener(new CallRenderedFrameListener("1"));
    }

    private void call2() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL2.getCallPos());
        ENG_RenderingThread.addFrameEndListener(new CallRenderedFrameListener("2"));
    }

    private void call3() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL3.getCallPos());
        ENG_RenderingThread.addFrameEndListener(new CallRenderedFrameListener("3"));
    }

    private void call4() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL4.getCallPos());
        ENG_RenderingThread.addFrameEndListener(new CallRenderedFrameListener("4"));
    }

    private int[] callWithResponse1() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_W_RESPONSE1.getCallPos());
        ENG_RenderingThread.writeInt(300);
        ENG_NativeCalls.writeString("callWithResponse1");
        ENG_NativeCalls.writeString("callWithResponse1SecondCall");
        final int[] anInt = new int[1];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener() {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                anInt[0] = responseBuffer.getInt();
                if (anInt[0] != 300) {
                    System.out.println("write int should be 300 but is " + anInt[0]);
                }
            }
        });
        return anInt;
    }

    private double[] callWithResponse2() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_W_RESPONSE2.getCallPos());
        ENG_RenderingThread.writeDouble(500.0);
        ENG_RenderingThread.writeByte((byte) 10);
        ENG_RenderingThread.writeLong(200);
        ENG_NativeCalls.writeString("callWithResponse2");
        ENG_NativeCalls.writeString("callWithResponse2SecondCall");
        final double[] aDouble = new double[1];
        final byte[] aByte = new byte[1];
        final long[] aLong = new long[1];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener() {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                ENG_Utility.alignMemory(responseBuffer, 4);
                aDouble[0] = responseBuffer.getDouble();
                aByte[0] = responseBuffer.get();
                ENG_Utility.alignMemory(responseBuffer, 4);
                aLong[0] = responseBuffer.getLong();
                if (aDouble[0] != 500) {
                    System.out.println("write double should be 500 but is " + aDouble[0]);
                }
                if (aByte[0] != 10) {
                    System.out.println("write byte should be 10 but is " + aByte[0]);
                }
                if (aLong[0] != 200) {
                    System.out.println("write long should be 200 but is " + aLong[0]);
                }
            }
        });
        return aDouble;
    }

    private ENG_Vector4D callWithResponse3() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_W_RESPONSE3.getCallPos());
        final ENG_Vector4D v = new ENG_Vector4D(3.0f, 100.0f, -500.0f, 1.0f);
        ENG_NativeCalls.writeVector4DFull(v);
        final String callWithResponse3 = "callWithResponse3";
        ENG_NativeCalls.writeString(callWithResponse3);
        ENG_NativeCalls.writeString("callWithResponse3SecondCall");
        final ENG_Vector4D ret = new ENG_Vector4D();
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener() {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                ENG_NativeCalls.readVector4DFull(responseBuffer, ret);
                String s = ENG_NativeCalls.readString(responseBuffer);
                if (!ret.equalsFull(v)) {
                    System.out.println("v is " + v + " but ret is " + ret);
                }
                if (!callWithResponse3.equals(s)) {
                    System.out.println("string " + callWithResponse3 + " has been returned as: " + s);
                }
            }
        });
        return ret;
    }

    private ENG_Vector4D callWithResponse4() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_W_RESPONSE4.getCallPos());
        final HashMap<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        ENG_NativeCalls.writeMap(map, ENG_NativeCalls.DataTypeList.STRING, ENG_NativeCalls.DataTypeList.INT);
        final ENG_Vector4D v = new ENG_Vector4D(1.0f, 2.0f, -3.0f, 4.0f);
        ENG_NativeCalls.writeVector4D(v);
        ENG_NativeCalls.writeString("callWithResponse3");
        ENG_NativeCalls.writeString("callWithResponse3SecondCall");
        final ENG_Vector4D ret = new ENG_Vector4D();
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopy(16, false) {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                int mapSize = responseBuffer.getInt();
                ENG_NativeCalls.readVector4D(responseBuffer, ret);
                ret.w = mapSize;
                if (mapSize != map.size()) {
                    System.out.println("map size is " + map.size() + " but returned is " + mapSize);
                }
                if (!ret.equals(v)) {
                    System.out.println("v is " + v + " but ret is " + ret);
                }
            }
        });
        return ret;
    }

    private long[] callRenderingThread1() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH1.getCallPos());
        ENG_RenderingThread.writeLong(1234);
        final long[] l = new long[1];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                l[0] = responseBuffer.getLong();
            }
        });
        return l;
    }

    private long[] callRenderingThread2() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH2.getCallPos());
        ENG_RenderingThread.writeLong(2345);
        final long[] l = new long[1];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                l[0] = responseBuffer.getLong();
            }
        });
        return l;
    }

    private long[] callRenderingThread3() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH3.getCallPos());
        ENG_RenderingThread.writeLong(3456);
        final long[] l = new long[1];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                l[0] = responseBuffer.getLong();
            }
        });
        return l;
    }

    private long[] callRenderingThread4() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH4.getCallPos());
        ENG_RenderingThread.writeLong(4567);
        final long[] l = new long[1];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                l[0] = responseBuffer.getLong();
            }
        });
        return l;
    }

    private long[] callRenderingThreadGenerateArray1() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY1.getCallPos());
        ENG_RenderingThread.writeLong(4321);
        ENG_RenderingThread.writeLong(5432);
        ENG_RenderingThread.writeLong(6543);
        ENG_RenderingThread.writeLong(7654);
        final long[] l = new long[4];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES * l.length, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                for (int i = 0; i < l.length; ++i) {
                    l[i] = responseBuffer.getLong();
                }
            }
        });
        return l;
    }

    private long[] callRenderingThreadGenerateArray2() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY2.getCallPos());
        ENG_RenderingThread.writeLong(43210);
        ENG_RenderingThread.writeLong(54321);
        ENG_RenderingThread.writeLong(65432);
        ENG_RenderingThread.writeLong(76543);
        final long[] l = new long[4];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES * l.length, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                for (int i = 0; i < l.length; ++i) {
                    l[i] = responseBuffer.getLong();
                }
            }
        });
        return l;
    }

    private long[] callRenderingThreadGenerateArray3() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY3.getCallPos());
        ENG_RenderingThread.writeLong(1111);
        ENG_RenderingThread.writeLong(2222);
        ENG_RenderingThread.writeLong(3333);
        ENG_RenderingThread.writeLong(4444);
        final long[] l = new long[4];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES * l.length, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                for (int i = 0; i < l.length; ++i) {
                    l[i] = responseBuffer.getLong();
                }
            }
        });
        return l;
    }

    private long[] callRenderingThreadGenerateArray4() {
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY4.getCallPos());
        ENG_RenderingThread.writeLong(5555);
        ENG_RenderingThread.writeLong(6666);
        ENG_RenderingThread.writeLong(7777);
        ENG_RenderingThread.writeLong(8888);
        final long[] l = new long[4];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES * l.length, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                for (int i = 0; i < l.length; ++i) {
                    l[i] = responseBuffer.getLong();
                }
            }
        });
        return l;
    }

    private void callRenderingThreadIncomplete1(long[] l, boolean dataAlreadyFlushed) {
        ENG_NativeCalls.checkIfNullAndBlockPtr(l, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR1.getCallPos());
        if (TEST_ENABLE_TRACING) {
            System.out.println("callRenderingThreadIncomplete1 data arrived");
        }
    }

    private void callRenderingThreadIncomplete2(long[] l, boolean dataAlreadyFlushed) {
        ENG_NativeCalls.checkIfNullAndBlockPtr(l, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR2.getCallPos());
        if (TEST_ENABLE_TRACING) {
            System.out.println("callRenderingThreadIncomplete2 data arrived");
        }
    }

    private void callRenderingThreadIncomplete3(long[] l, boolean dataAlreadyFlushed) {
        ENG_NativeCalls.checkIfNullAndBlockPtr(l, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR3.getCallPos());
        if (TEST_ENABLE_TRACING) {
            System.out.println("callRenderingThreadIncomplete3 data arrived");
        }
    }

    private void callRenderingThreadIncomplete4(long[] l, boolean dataAlreadyFlushed) {
        ENG_NativeCalls.checkIfNullAndBlockPtr(l, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR4.getCallPos());
        if (TEST_ENABLE_TRACING) {
            System.out.println("callRenderingThreadIncomplete4 data arrived");
        }
    }

    private void callRenderingThreadIncompleteAsArray1(long[] l, boolean dataAlreadyFlushed) {
        ENG_NativeCalls.checkIfNullAndBlockPtrArray(l, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY1.getCallPos());
        if (TEST_ENABLE_TRACING) {
            System.out.println("callRenderingThreadIncompleteAsArray1 data arrived");
        }
    }

    private void callRenderingThreadIncompleteAsArray2(long[] l, boolean dataAlreadyFlushed) {
        ENG_NativeCalls.checkIfNullAndBlockPtrArray(l, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY2.getCallPos());
        if (TEST_ENABLE_TRACING) {
            System.out.println("callRenderingThreadIncompleteAsArray2 data arrived");
        }
    }

    private void callRenderingThreadIncompleteAsArray3(long[] l, boolean dataAlreadyFlushed) {
        ENG_NativeCalls.checkIfNullAndBlockPtrArray(l, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY3.getCallPos());
        if (TEST_ENABLE_TRACING) {
            System.out.println("callRenderingThreadIncompleteAsArray3 data arrived");
        }
    }

    private void callRenderingThreadIncompleteAsArray4(long[] l, boolean dataAlreadyFlushed) {
        ENG_NativeCalls.checkIfNullAndBlockPtrArray(l, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(ENG_NativeCalls.NativeCallsList.TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY4.getCallPos());
        if (TEST_ENABLE_TRACING) {
            System.out.println("callRenderingThreadIncompleteAsArray4 data arrived");
        }
    }

    @Override
    public void run() {
        try {
            for (int repeatNum = 0; repeatNum < REPEAT_NUM; ++repeatNum) {
                for (Tests test : runningTests) {
                    switch (test) {
                        case BLOCKING_TEST: {
//                    setWaitTime(5000);
                            call1();
                            ENG_RenderingThread.flushPipeline();
                            call2();
                            ENG_RenderingThread.flushPipeline();
                            call3();
                            ENG_RenderingThread.flushPipeline();
                            call4();
                            ENG_RenderingThread.flushPipeline();
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS: {
//                    setWaitTime(5000);
                            call1();
                            call2();
                            call3();
                            call4();
                            ENG_RenderingThread.flushPipeline();
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE_BLOCKING: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse2();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse3();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline();
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            callWithResponse2();
                            callWithResponse3();
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline();
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_BLOCKING: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            ENG_RenderingThread.flushPipeline(true);
                            callWithResponse2();
                            ENG_RenderingThread.flushPipeline(true);
                            callWithResponse3();
                            ENG_RenderingThread.flushPipeline(true);
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline(true);
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE_AND_WAIT: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            callWithResponse2();
                            callWithResponse3();
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline(true);
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_1: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            ENG_RenderingThread.flushPipeline(true);
                            callWithResponse2();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse3();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline();
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_2: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse2();
                            ENG_RenderingThread.flushPipeline(true);
                            callWithResponse3();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline();
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_3: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse2();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse3();
                            ENG_RenderingThread.flushPipeline(true);
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline();
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_4: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            callWithResponse2();
                            callWithResponse3();
                            ENG_RenderingThread.flushPipeline(true);
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline();
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_5: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            ENG_RenderingThread.flushPipeline(true);
                            callWithResponse2();
                            callWithResponse3();
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline(true);
                            waitForTestEnd();
                        }
                        break;
                        case MULTIPLE_CALLS_W_RESPONSE_AND_WAIT_MIXED_6: {
//                    setWaitTime(5000);
                            callWithResponse1();
                            callWithResponse2();
                            ENG_RenderingThread.flushPipeline();
                            callWithResponse3();
                            ENG_RenderingThread.flushPipeline(true);
                            callWithResponse4();
                            ENG_RenderingThread.flushPipeline();
                            waitForTestEnd();
                        }
                        break;
                        case STRESS_TEST: {
                            int flushFunCallCount = 0;
                            boolean flushAdded = false;
                            boolean stallPipeline = false;
                            long seed = System.currentTimeMillis();
                            System.out.println("random seed: " + seed);
                            long beginTime = System.currentTimeMillis();
                            Random random = new Random(seed);
                            for (int i = 0; i < 18000; ++i) {
                                for (int j = 0; j < 50; ++j) {
                                    int funCall = random.nextInt(8);

                                    switch (funCall) {
                                        case 0:
                                            call1();
                                            break;
                                        case 1:
                                            call2();
                                            break;
                                        case 2:
                                            call3();
                                            break;
                                        case 3:
                                            call4();
                                            break;
                                        case 4:
                                            callWithResponse1();
                                            break;
                                        case 5:
                                            callWithResponse2();
                                            break;
                                        case 6:
                                            callWithResponse3();
                                            break;
                                        case 7:
                                            callWithResponse4();
                                            break;
                                    }
                                }
                                if (!flushAdded) {
                                    flushFunCallCount = 0;//random.nextInt(3) + 1;
                                    stallPipeline = random.nextBoolean();
                                    flushAdded = true;
                                }
                                if ((flushFunCallCount--) == 0) {
                                    ENG_RenderingThread.flushPipeline(stallPipeline);
                                    flushAdded = false;
                                }
                            }
                            long endTime = System.currentTimeMillis() - beginTime;
                            System.out.println("stress test time: " + endTime);
                            waitForTestEnd();

                        }
                        break;
                        case IDSTRING: {
                            ENG_IdString mainString = new ENG_IdString("mainString");
                            System.out.println("mainString: " + mainString);
                            mainString.append("withAddedString");
                            System.out.println("appendedString: " + mainString);
                            ENG_IdString newString = mainString.concatenate("newString");
                            System.out.println("concatenatedString: " + newString);
                            ENG_IdString compareTest1 = new ENG_IdString("compareTest");
                            ENG_IdString compareTest2 = new ENG_IdString("compareTest");
                            System.out.println((compareTest1.equals(compareTest2)) ? "equal strings" : "error");
                            waitForTestEnd();
                        }
                        break;

                        case FLUSH_AND_BLOCK_FOR_RESULT: {
                            System.out.println("Starting FLUSH_AND_BLOCK_FOR_RESULT");
                            long[] ptr = callRenderingThread1();
                            ENG_RenderingThread.flushPipeline(false);
                            callRenderingThreadIncomplete1(ptr, true);
                            waitForTestEnd();
                        }
                        break;
                        case FLUSH_INCOMPLETE_AND_BLOCK_FOR_RESULT: {
                            System.out.println("Starting FLUSH_INCOMPLETE_AND_BLOCK_FOR_RESULT");
                            long[] ptr = callRenderingThread2();
                            callRenderingThreadIncomplete2(ptr, false);
                            ENG_RenderingThread.flushPipeline(false);
                            waitForTestEnd();
                        }
                        break;
                        case FLUSH_UNTIL_BLOCK_AND_WAIT_FOR_RESULT: {
                            System.out.println("Starting FLUSH_UNTIL_BLOCK_AND_WAIT_FOR_RESULT");
                            long[] ptr1 = callRenderingThread1();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr2 = callRenderingThread2();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr3 = callRenderingThread3();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr4 = callRenderingThread4();
                            ENG_RenderingThread.flushPipeline(false);
                            callRenderingThreadIncomplete1(ptr1, true);
                            callRenderingThreadIncomplete2(ptr2, true);
                            callRenderingThreadIncomplete3(ptr3, true);
                            callRenderingThreadIncomplete4(ptr4, true);
                            ENG_RenderingThread.flushPipeline(false);
                            waitForTestEnd();
                        }
                        break;
                        case FLUSH_UNTIL_BLOCK_AND_FLUSH_INCOMPLETE_AND_BLOCK_FOR_RESULT: {
                            System.out.println("Starting FLUSH_UNTIL_BLOCK_AND_FLUSH_INCOMPLETE_AND_BLOCK_FOR_RESULT");
                            long[] ptr1 = callRenderingThread1();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr2 = callRenderingThread2();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr3 = callRenderingThread3();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr4 = callRenderingThread4();
//                    ENG_RenderingThread.flushPipeline(false);
                            callRenderingThreadIncomplete4(ptr4, false);
                            ENG_RenderingThread.flushPipeline(false);
                            waitForTestEnd();
                        }
                        break;
                        case FLUSH_WITH_WAIT_FOR_FIRST_BUFFER: {
                            System.out.println("Starting FLUSH_WITH_WAIT_FOR_FIRST_BUFFER");
                            long[] ptr1 = callRenderingThread1();
                            ENG_RenderingThread.flushPipeline(true);
                            callRenderingThreadIncomplete1(ptr1, true);
                            waitForTestEnd();
                        }
                        break;
                        case FLUSH_WITH_WAIT_FOR_SECOND_BUFFER: {
                            System.out.println("Starting FLUSH_WITH_WAIT_FOR_SECOND_BUFFER");
                            long[] ptr1 = callRenderingThread1();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr2 = callRenderingThread2();
                            ENG_RenderingThread.flushPipeline(true);
                            callRenderingThreadIncomplete1(ptr1, true);
                            callRenderingThreadIncomplete2(ptr2, true);
                            waitForTestEnd();
                        }
                        break;
                        case FLUSH_WITH_WAIT_FOR_THIRD_BUFFER: {
                            System.out.println("Starting FLUSH_WITH_WAIT_FOR_THIRD_BUFFER");
                            long[] ptr1 = callRenderingThread1();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr2 = callRenderingThread2();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr3 = callRenderingThread3();
                            ENG_RenderingThread.flushPipeline(true);
                            callRenderingThreadIncomplete1(ptr1, true);
                            callRenderingThreadIncomplete2(ptr2, true);
                            callRenderingThreadIncomplete3(ptr3, true);
                            waitForTestEnd();
                        }
                        break;
                        case FLUSH_WITH_END_FRAME_COMBINATIONS: {
                            System.out.println("Starting FLUSH_WITH_END_FRAME_COMBINATIONS");
                            long[] ptr1 = callRenderingThread1();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr2 = callRenderingThread2();
                            long[] ptr3 = callRenderingThread3();
                            callRenderingThreadIncomplete1(ptr1, true);
                            callRenderingThreadIncomplete2(ptr2, false);
                            callRenderingThreadIncomplete3(ptr3, false);
                            // It makes no sense to have a data already flushed param used after
                            // we have flushed with an incomplete command list.
                            long[] ptr4 = callRenderingThread4();
                            ENG_RenderingThread.flushPipeline(false);
                            callRenderingThreadIncomplete4(ptr4, true);
                            waitForTestEnd();
                        }
                        break;
                        case FLUSH_WITH_END_FRAME_COMBINATIONS_WITH_ARRAYS: {
                            System.out.println("Starting FLUSH_WITH_END_FRAME_COMBINATIONS_WITH_ARRAYS");
                            long[] ptr1 = callRenderingThreadGenerateArray1();
                            ENG_RenderingThread.flushPipeline(false);
                            long[] ptr2 = callRenderingThreadGenerateArray2();
                            long[] ptr3 = callRenderingThreadGenerateArray3();
                            callRenderingThreadIncompleteAsArray1(ptr1, true);
                            callRenderingThreadIncompleteAsArray2(ptr2, false);
                            callRenderingThreadIncompleteAsArray3(ptr3, false);
                            // It makes no sense to have a data already flushed param used after
                            // we have flushed with an incomplete command list.
                            long[] ptr4 = callRenderingThreadGenerateArray4();
                            ENG_RenderingThread.flushPipeline(false);
                            callRenderingThreadIncompleteAsArray4(ptr4, true);
                            waitForTestEnd();
                        }
                        break;
                        case FLUSH_AND_BLOCK_FOR_RESULT_ADD_SLOW_CALL: {
                            System.out.println("Starting FLUSH_AND_BLOCK_FOR_RESULT_ADD_SLOW_CALL");
                            long[] ptr1 = callRenderingThread1();
                            ENG_RenderingThread.flushPipeline(true);
                            callRenderingThreadIncomplete1(ptr1, true);
                            long[] ptr2 = callRenderingThread2();
                            long[] ptr3 = callRenderingThread3();
                            callSlowCall1();
                            callRenderingThreadIncomplete2(ptr2, false);
                            callRenderingThreadIncomplete3(ptr3, false);
                            callSlowCall2();
                            callSlowCall3();
                            long[] ptr4 = callRenderingThread4();
                            ENG_RenderingThread.flushPipeline(false);
                            callRenderingThreadIncomplete4(ptr4, true);
                            waitForTestEnd();
                        }
                            break;
                        case FLUSH_INCOMPLETE_AND_BLOCK_AND_ADD_SLOW_CALL: {
                            System.out.println("Starting FLUSH_INCOMPLETE_AND_BLOCK_AND_ADD_SLOW_CALL");
                        }
                            break;
                        case CHECK_RENDERING_THREAD_SLEEP: {
                            System.out.println("Starting CHECK_RENDERING_THREAD_SLEEP");
                        }
                            break;
                        case FLUSH_INCOMPLETE_STRESS_TEST: {
                            System.out.println("Starting FLUSH_INCOMPLETE_STRESS_TEST");

                            // Make sure that after a flushPipeline() if we have a
                            // dataAlreadyFlushed == false call, there can be no immediate
                            // dataAlreadyFlushed == true call since it makes no sense.
                            // And causes a deadlock.

                            int flushFunCallCount = 0;
                            boolean addFlushPipeline = false;
                            boolean stallPipeline = false;
                            // If an element is true then it means we have called
                            // callRenderingThreadX(). Now we must call the corresponding
                            // callRenderingThreadIncompleteX().
                            boolean[] callWithRet = new boolean[8];
                            long seed = System.currentTimeMillis();
                            System.out.println("random seed: " + seed);
                            long beginTime = System.currentTimeMillis();
                            Random random = new Random(seed);
                            for (int i = 0; i < 18000; ++i) {
                                // Give a chance for having no callRenderingThreadX calls
                                // and only 'classical' calls.
                                int callRenderingThreadFunCall = random.nextInt(16);
                                if (callRenderingThreadFunCall >= 0 && callRenderingThreadFunCall < 8) {
                                    callWithRet[callRenderingThreadFunCall] = true;
                                }
                                switch (callRenderingThreadFunCall) {
                                    case 0:
                                        ptr1 = callRenderingThread1();
                                        break;
                                    case 1:
                                        ptr2 = callRenderingThread2();
                                        break;
                                    case 2:
                                        ptr3 = callRenderingThread3();
                                        break;
                                    case 3:
                                        ptr4 = callRenderingThread4();
                                        break;
                                    case 4:
                                        ptrArray1 = callRenderingThreadGenerateArray1();
                                        break;
                                    case 5:
                                        ptrArray2 = callRenderingThreadGenerateArray2();
                                        break;
                                    case 6:
                                        ptrArray3 = callRenderingThreadGenerateArray3();
                                        break;
                                    case 7:
                                        ptrArray4 = callRenderingThreadGenerateArray4();
                                        break;
                                    default:
                                        // Do nothing.
                                }
                                for (int j = 0; j < 16; ++j) {
                                    int funCall = random.nextInt(8);

                                    switch (funCall) {
                                        case 0:
                                            call1();
                                            break;
                                        case 1:
                                            call2();
                                            break;
                                        case 2:
                                            call3();
                                            break;
                                        case 3:
                                            call4();
                                            break;
                                        case 4:
                                            callWithResponse1();
                                            break;
                                        case 5:
                                            callWithResponse2();
                                            break;
                                        case 6:
                                            callWithResponse3();
                                            break;
                                        case 7:
                                            callWithResponse4();
                                            break;
                                    }
                                }
                                addFlushPipeline = random.nextBoolean();
                                if (addFlushPipeline) {
                                    flushFunCallCount = 0;//random.nextInt(3) + 1;
                                    stallPipeline = random.nextBoolean();
                                    ENG_RenderingThread.flushPipeline(stallPipeline);
                                    completeCall(callWithRet, true);
                                } else {
                                    // Add an EndFrameListener with dataAlreadyFlushed == false.
                                    // If we haven't flushed it makes no sense to assume that the
                                    // data is available.
                                    completeCall(callWithRet, false);
                                }
//                                if ((flushFunCallCount--) == 0) {
//                                    ENG_RenderingThread.flushPipeline(stallPipeline);
//                                    addFlushPipeline = false;
//                                }
                            }
                            long endTime = System.currentTimeMillis() - beginTime;
                            System.out.println("flush incomplete stress test time: " + endTime);
                            waitForTestEnd();
                        }
                            break;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("Tests ended");

    }

    private void completeCall(boolean[] callWithRet, boolean dataAlreadyFlushed) {
        for (int callWithRetPos = 0; callWithRetPos < 8; ++callWithRetPos) {
            if (callWithRet[callWithRetPos]) {
                // We need to add the appropiate callRenderingThreadIncompleteX().
                switch (callWithRetPos) {
                    case 0:
                        callRenderingThreadIncomplete1(ptr1, dataAlreadyFlushed);
                        break;
                    case 1:
                        callRenderingThreadIncomplete2(ptr2, dataAlreadyFlushed);
                        break;
                    case 2:
                        callRenderingThreadIncomplete3(ptr3, dataAlreadyFlushed);
                        break;
                    case 3:
                        callRenderingThreadIncomplete4(ptr4, dataAlreadyFlushed);
                        break;
                    case 4:
                        callRenderingThreadIncompleteAsArray1(ptrArray1, dataAlreadyFlushed);
                        break;
                    case 5:
                        callRenderingThreadIncompleteAsArray2(ptrArray2, dataAlreadyFlushed);
                        break;
                    case 6:
                        callRenderingThreadIncompleteAsArray3(ptrArray3, dataAlreadyFlushed);
                        break;
                    case 7:
                        callRenderingThreadIncompleteAsArray4(ptrArray4, dataAlreadyFlushed);
                        break;
                }
                callWithRet[callWithRetPos] = false;
            }
        }
    }

    private void waitForTestEnd() {
        testEndLatchLock.lock();
        try {
            testEndLatch = new CountDownLatch(1);
        } finally {
            testEndLatchLock.unlock();
        }
        try {
            testEndLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void checkTestEnd() {
        testEndLatchLock.lock();
        try {
            if (testEndLatch != null) {
                testEndLatch.countDown();
//                testEndLatch = null;
            }
        } finally {
            testEndLatchLock.unlock();
        }
    }

    public long getWaitTime() {
        waitTimeLock.lock();
        try {
            return waitTime;
        } finally {
            waitTimeLock.unlock();
        }
    }

    public void setWaitTime(long waitTime) {
        waitTimeLock.lock();
        try {
            this.waitTime = waitTime;
        } finally {
            waitTimeLock.unlock();
        }
    }

    private void callSlowCall1() {
        ENG_SlowCallExecutor.execute(() -> {
            testSlowCall1();
            return 0;
        });
    }

    private void callSlowCall2() {
        ENG_SlowCallExecutor.execute(() -> {
            int ret = testSlowCall2(4321);
            if (ret == 1234) {
                System.out.println("ret == 1234");
            } else {
                System.out.println("ret != 1234. ret: " + ret);
            }
            return 0;
        });
    }

    private void callSlowCall3() {
        ENG_SlowCallExecutor.execute(() -> {
            long ret = testSlowCall3(4321, 87654321);
            if (ret == 12345678) {
                System.out.println("ret == 12345678");
            } else {
                System.out.println("ret != 12345678. ret: " + ret);
            }
            return 0;
        });
    }

    private void callSlowCall4() {
        ENG_SlowCallExecutor.execute(() -> {
            boolean ret = testSlowCall4(1234, (byte) 64, false, (short) 16384);
            System.out.println("ret == " + ret);
            return 0;
        });
    }

    public static TestRenderingThread getSingleton() {
        return singleton;
    }

    private static native void testSlowCall1();
    private static native int testSlowCall2(int p);
    private static native long testSlowCall3(int p, long l);
    private static native boolean testSlowCall4(int p, byte b, boolean bool, short s);
}
