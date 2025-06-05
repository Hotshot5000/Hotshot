/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

import org.apache.commons.io.Charsets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.MainActivity;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Box;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Ray;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Double;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.gui.ENG_DynamicOverlayElement;
import headwayent.hotshotengine.renderer.ENG_BillboardNative;
import headwayent.hotshotengine.renderer.ENG_BillboardSet;
import headwayent.hotshotengine.renderer.ENG_BillboardSetNative;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_Common;
import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters;
import headwayent.hotshotengine.renderer.ENG_HlmsDatablock;
import headwayent.hotshotengine.renderer.ENG_Item;
import headwayent.hotshotengine.renderer.ENG_Light;
import headwayent.hotshotengine.renderer.ENG_LightNative;
import headwayent.hotshotengine.renderer.ENG_Node;
import headwayent.hotshotengine.renderer.ENG_Overlay;
import headwayent.hotshotengine.renderer.ENG_OverlayContainer;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;
import headwayent.hotshotengine.renderer.ENG_OverlayManager;
import headwayent.hotshotengine.renderer.ENG_PanelOverlayElement;
import headwayent.hotshotengine.renderer.ENG_ParticleSystemNative;
import headwayent.hotshotengine.renderer.ENG_RaySceneQuery;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderTarget;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.ENG_TextAreaOverlayElement;
import headwayent.hotshotengine.renderer.ENG_TextureNative;
import headwayent.hotshotengine.renderer.ENG_TiledAnimationNative;
import headwayent.hotshotengine.renderer.ENG_Workflows;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointer;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;

/**
 * Created by sebas on 11.02.2017.
 * <p>
 * Each call has the following format:
 * <p>
 * byte 0 = function number
 * each of the following parameters is written as little endian bytes.
 * A string has the first byte as the string length followed by the actual string bytes.
 */

public final class ENG_NativeCalls {

    private static final Charset DEFAULT_CHARSET = Charsets.US_ASCII;
    private static final int MAX_RAY_SCENE_QUERY_RESPONSE_SIZE = 32;
    private static final int TEST_CALL_FIRST_POS = 200;
    private static ByteBuffer buffer;
    private static final ENG_ByteBufferRing getAabbsBuffer = new ENG_ByteBufferRing(ENG_RenderingThread.BUFFER_COUNT, ENG_Item.AABB_SIZE_IN_BYTES * 300);//ENG_Utility.allocateDirect();
    private static byte order = 1;

    public enum NativeCallsList {
//        NEWROOT((byte) 1),
//        ROOT_INITIALIZE((byte) 2),
        ROOT_CREATERENDERWINDOW(order++),
//        ROOT_CREATESCENEMANAGER((byte) 4),
//        RENDERSYSTEM_SETCONFIGOPTION((byte) 5),
        SCREEN_CREATE(order++),
        SCREEN_DESTROY(order++),
        SCREEN_CREATELAYER(order++),
        SCREEN_DESTROYLAYER(order++),
        SCREEN_RENDERONCE(order++),
        SCREEN_UPDATEVERTEXLISTSIZE(order++),
        ROOT_RENDERONEFRAME(order++),
        SCREEN_SETVISIBLE(order++),

        CREATE_RAY_QUERY(order++),
        DESTROY_RAY_QUERY(order++),
        RAY_QUERY_SETSORTBYDISTANCE(order++),
        RAY_QUERY_EXECUTE(order++),

        GET_VIEWPORT(order++),

        SCENEMANAGER_ITEM_CREATE(order++),
        SCENEMANAGER_ITEM_CREATE_ALL_PARAMS(order++),
        SCENEMANAGER_ITEM_DESTROY(order++),
        SCENEMANAGER_ITEM_DESTROY_ALL(order++),
        SCENEMANAGER_CREATE_PARTICLE_SYSTEM_STRING(order++),
        SCENEMANAGER_CREATE_PARTICLE_SYSTEM_QUOTA_RESOURCE_GROUP(order++),
        SCENEMANAGER_DESTROY_PARTICLE_SYSTEM(order++),
        SCENEMANAGER_DESTROY_ALL_PARTICLE_SYSTEMS(order++),
        SCENEMANAGER_CREATE_BILLBOARD_SET(order++),
        SCENEMANAGER_DESTROY_BILLBOARD_SET(order++),
        SCENEMANAGER_DESTROY_ALL_BILLBOARD_SETS(order++),
        SCENEMANAGER_CLEAR_SCENE(order++),
        SCENEMANAGER_SET_AMBIENT_LIGHT(order++),
        SCENEMANAGER_SET_SKYBOX(order++),
        SCENEMANAGER_SET_SKYBOX_ALL_PARAMS(order++),
        SCENEMANAGER_SET_SKYBOX_ENABLED(order++),
        SCENEMANAGER_CREATE_SCENE_NODE(order++),
        SCENEMANAGER_CREATE_SCENE_NODE_ALL_PARAMS(order++),
        SCENEMANAGER_DESTROY_SCENE_NODE(order++),
        SCENEMANAGER_CREATE_LIGHT(order++),
        SCENEMANAGER_DESTROY_LIGHT(order++),
        SCENEMANAGER_DESTROY_ALL_LIGHTS(order++),
        SCENEMANAGER_NOTIFY_STATIC_DIRTY(order++),

        NODE_ADD_CHILD(order++),
        NODE_REMOVE_CHILD(order++),
//        NODE_HAS_CHILD(order++),
//        NODE_SET_PARENT(order++),
//        NODE_UNSET_PARENT(order++),

        SCENENODE_CREATE_CHILD(order++),
        SCENENODE_CREATE_CHILD_ALL_PARAMS(order++),
        SCENENODE_REMOVE_AND_DESTROY(order++),
        SCENENODE_REMOVE_AND_DESTROY_ALL_CHILDREN(order++),
        SCENENODE_ATTACH_OBJECT(order++),
        SCENENODE_ATTACH_PARTICLE_SYSTEM(order++), // Hack because we cannot cast a void* to a MovableObject* correctly since the particle system uses multiple inheritance.
        SCENENODE_DETACH_OBJECT(order++),
        SCENENODE_DETACH_ALL_OBJECTS(order++),
        SCENENODE_SETPOSITION_XYZ(order++),
        SCENENODE_SETPOSITION_VEC(order++),
        SCENENODE_SETORIENTATION(order++),
        SCENENODE_SETDERIVEDPOSITION_XYZ(order++),
        SCENENODE_SETDERIVEDPOSITION_VEC(order++),
        SCENENODE_SETDERIVEDORIENTATION(order++),
        SCENENODE_SETSCALING_XYZ(order++),
//        SCENENODE_CREATECHILDSCENENODE(order++),
        SCENENODE_LOOKAT(order++),
        SCENENODE_LOOKAT_ALL_PARAMS(order++),
        SCENENODE_SETDIRECTION(order++),
        SCENENODE_SETDIRECTION_ALL_PARAMS(order++),
        SCENENODE_SETVISIBLE(order++),
        SCENENODE_FLIPVISIBILITY(order++),
        SCENENODE_SETSTATIC(order++),
        SCENENODE_SETNAME(order++),

        LIGHT_SET_TYPE(order++),
        LIGHT_SET_SPOTLIGHT_RANGE(order++),
        LIGHT_SET_DIFFUSE_COLOUR(order++),
        LIGHT_SET_SPECULAR_COLOUR(order++),
        LIGHT_SET_ATTENUATION(order++),
        LIGHT_SET_POWER_SCALE(order++),
        LIGHT_SET_ATTENUATION_BASED_ON_RADIUS(order++),
//        LIGHT_SET_POSITION(order++),
        LIGHT_SET_DIRECTION(order++),

        ITEM_SET_DATABLOCK(order++),
        ITEM_SET_VISIBILITY_FLAGS(order++),

        COMPOSITOR_WORKSPACE_SET_ENABLED(order++),

        CAMERA_INVALIDATE_VIEW(order++),
        CAMERA_GET_PROJECTION_MATRIX(order++),
        CAMERA_GET_VIEW_MATRIX(order++),
        CAMERA_IS_VISIBLE_VEC(order++),
        CAMERA_IS_VISIBLE_AXIS_ALIGNED_BOX(order++),

        GET_ITEMS_AABBS(order++),

        MOVABLE_OBJECT_DETACH_FROM_PARENT(order++),

        OVERLAY_MANAGER_CREATE_OVERLAY(order++),
        OVERLAY_MANAGER_DESTROY_OVERLAY_BY_NAME(order++),
        OVERLAY_MANAGER_DESTROY_OVERLAY_BY_PTR(order++),
        OVERLAY_MANAGER_DESTROY_ALL(order++),
        OVERLAY_MANAGER_GET_BY_NAME(order++),
        OVERLAY_MANAGER_UPDATE_DATA(order++),

        OVERLAY_SHOW(order++),
        OVERLAY_HIDE(order++),

        OVERLAY_ELEMENT_SHOW(order++),
        OVERLAY_ELEMENT_HIDE(order++),
        OVERLAY_ELEMENT_SET_LEFT(order++),
        OVERLAY_ELEMENT_SET_TOP(order++),
        OVERLAY_ELEMENT_SET_WIDTH(order++),
        OVERLAY_ELEMENT_SET_HEIGHT(order++),
        OVERLAY_ELEMENT_SET_CAPTION(order++),
        OVERLAY_ELEMENT_SET_METRICS_MODE(order++),
//        OVERLAY_ELEMENT_UPDATE_DATA(order++),
        OVERLAY_ELEMENT_SET_MATERIAL_NAME(order++),
        OVERLAY_ELEMENT_SET_ALIGNMENT(order++),

        GPU_PROGRAM_PARAMS_GET_PROGRAM_PARAMS(order++),
        GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_COLOUR(order++),
        GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_INT(order++),
        GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_FLOAT(order++),

        TEXTURE_MANAGER_GET_BY_NAME(order++),
        TEXTURE_MANAGER_GET_BY_NAME_OVERLAY_ELEMENT(order++),

        FRAME_STATS_UPDATE(order++),

        UNLIT_DATABLOCK_SET_USE_COLOUR(order++),
        UNLIT_DATABLOCK_SET_COLOUR(order++),

        DYNAMIC_OVERLAY_ELEM_CTOR(order++),
        DYNAMIC_OVERLAY_ELEM_DTOR(order++),
        DYNAMIC_OVERLAY_ELEM_RESET_TO_INITIAL_TEXTURE(order++),
        DYNAMIC_OVERLAY_ELEM_UPDATE_FINAL_TEXTURE(order++),
        DYNAMIC_OVERLAY_ELEM_SET_POINT_IN_SCREEN_SPACE(order++),
        DYNAMIC_OVERLAY_ELEM_LOCK(order++),
        DYNAMIC_OVERLAY_ELEM_UNLOCK(order++),
        DYNAMIC_OVERLAY_ELEM_SET_AREA_VEC(order++),
        DYNAMIC_OVERLAY_ELEM_SET_AREA(order++),

        TILED_ANIMATION_CREATE(order++),
        TILED_ANIMATION_DESTROY(order++),
        TILED_ANIMATION_UPDATE_CURRENT_FRAME_NUM(order++),

        BILLBOARDSET_CREATE_BILLBOARD(order++),
        BILLBOARDSET_DESTROY_BILLBOARD(order++),
//        BILLBOARDSET_DESTROY_BILLBOARD_BY_ID(order++),
        BILLBOARDSET_SET_COMMON_UP_VECTOR(order++),
        BILLBOARDSET_SET_COMMON_DIRECTION(order++),
        BILLBOARDSET_SET_DEFAULT_DIMENSIONS(order++),
        BILLBOARDSET_SET_MATERIAL_NAME(order++),
        BILLBOARDSET_SET_DATABLOCK_NAME(order++),
        BILLBOARDSET_SET_BILLBOARD_ORIGIN(order++),
        BILLBOARDSET_SET_BILLBOARD_ROTATION_TYPE(order++),
        BILLBOARDSET_SET_BILLBOARD_TYPE(order++),

        BILLBOARD_SET_ROTATION(order++),

        MOVABLE_OBJECT_SET_RENDER_QUEUE_GROUP(order++),

        PARTICLE_SYSTEM_SET_MATERIAL_NAME(order++),

        SCENE_COMPOSITOR_INSERT_NODE(order++),
        SCENE_COMPOSITOR_REVERT_NODE(order++),

        TEST_CALL_RENDERING_THREAD_DATA_FLUSH1((byte) TEST_CALL_FIRST_POS),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH2((byte) 201),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH3((byte) 202),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH4((byte) 203),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY1((byte) 204),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY2((byte) 205),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY3((byte) 206),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_ARRAY4((byte) 207),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR1((byte) 215),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR2((byte) 216),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR3((byte) 217),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_PTR4((byte) 218),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY1((byte) 219),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY2((byte) 220),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY3((byte) 221),
        TEST_CALL_RENDERING_THREAD_DATA_FLUSH_INCOMPLETE_RENDER_CHECK_ARRAY4((byte) 222),
        
        TEST_CALL1((byte) 240),
        TEST_CALL2((byte) 241),
        TEST_CALL3((byte) 242),
        TEST_CALL4((byte) 243),
        TEST_CALL_W_RESPONSE1((byte) 244),
        TEST_CALL_W_RESPONSE2((byte) 245),
        TEST_CALL_W_RESPONSE3((byte) 246),
        TEST_CALL_W_RESPONSE4((byte) 247),


        FRAME_ID_POS((byte) 255)
        ;

        private static final boolean DEBUG = false;
        private static final boolean PRINT_NATIVE_CALL_LIST = false;

        static {
            int currentMaxPosition = ENG_Math.byteToUnsigned(order);
            if (currentMaxPosition > TEST_CALL_FIRST_POS) {
                throw new IllegalStateException("NativeCallsList calls have reached the test calls positions. order: " + order);
            }
            if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("NativeCallList.txt", false));
                    for (NativeCallsList call : NativeCallsList.values()) {
                        writer.write("callPos: " + call.callPos + " call name: " + call);
                        writer.newLine();
                    }
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (DEBUG || PRINT_NATIVE_CALL_LIST) {
                for (NativeCallsList call : NativeCallsList.values()) {
                    System.out.println("callPos: " + call.callPos + " call name: " + call);
                }
                System.out.println("CallList ended");
            }

        }


        private final byte callPos;

        NativeCallsList(byte callPos) {
            this.callPos = callPos;
        }

        public byte getCallPos() {
            if (DEBUG) {
                System.out.println("callPos: " + callPos + " call name: " +
                        NativeCallsList.values()[(callPos & 0xff) != 255 ? (callPos & 0xff) - 1 : NativeCallsList.values().length - 1]);
            }
            return callPos;
        }
    }

    public enum GpuProgramParametersType {
        GPU_VERTEX(1),
        GPU_FRAGMENT(2),
        GPU_GEOMETRY(3),
        GPU_COMPUTE(4),
        GPU_TESSELATION_DOMAIN(5),
        GPU_TESSELATION_HULL(6),
        GPU_SHADOW_CASTER_VERTEX(7),
        GPU_SHADOW_CASTER_FRAGMENT(8);

        private final byte pos;

        GpuProgramParametersType(int pos) {
            this.pos = (byte) pos;
        }

        public byte getPos() {
            return pos;
        }
    }
    
    public enum DataTypeList {
        BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, STRING
    }

    public static void initialize(int bufferSize) {
        buffer = ENG_Utility.allocateDirectMemoryAligned(bufferSize, 1)[0];
    }

    private static void resetBuffer(int length) {
        ENG_Utility.memset(buffer, (byte) 0, 0, length);
    }

    public static void writeString(String str) {
        byte[] bytes = str.getBytes(DEFAULT_CHARSET);
        if (MainActivity.isDebugmode()) {
            if (bytes.length > 255) {
                throw new IllegalArgumentException(str + " has more than 255 characters!");
            }
        }
        buffer.position(0);
        buffer.limit(buffer.capacity());
        buffer.put((byte) bytes.length);
        buffer.put(bytes);
        buffer.flip();
        ENG_RenderingThread.writeBytes(buffer);
    }

    public static String readString(ByteBuffer buf) {
        byte strlen = buf.get();
        byte[] bytes = new byte[strlen];
        buf.get(bytes);
        return new String(bytes, DEFAULT_CHARSET);
    }

    public static void writeBuffer(byte[] b) {
        if (MainActivity.isDebugmode()) {
            if (b.length > 255) {
                throw new IllegalArgumentException("buffer " + b.length + " has more than 255 elements!");
            }
        }
        buffer.position(0);
        buffer.limit(buffer.capacity());
        buffer.put((byte) b.length);
        buffer.put(b);
        buffer.flip();
        ENG_RenderingThread.writeBytes(buffer);
    }

    public static void writeBuffer(short[] b) {
        if (MainActivity.isDebugmode()) {
            if (b.length > 255) {
                throw new IllegalArgumentException("buffer " + b.length + " has more than 255 elements!");
            }
        }
        buffer.position(0);
        buffer.limit(buffer.capacity());
        buffer.put((byte) b.length);
        ShortBuffer newBuf = buffer.asShortBuffer();
        newBuf.put(b);
        buffer.position(ENG_Short.SIZE_IN_BYTES * b.length + 1);
        buffer.flip();
        ENG_RenderingThread.writeBytes(buffer);
    }

    public static void writeBuffer(int[] b) {
        if (MainActivity.isDebugmode()) {
            if (b.length > 255) {
                throw new IllegalArgumentException("buffer " + b.length + " has more than 255 elements!");
            }
        }
        buffer.position(0);
        buffer.limit(buffer.capacity());
        buffer.put((byte) b.length);
        IntBuffer newBuf = buffer.asIntBuffer();
        newBuf.put(b);
        buffer.position(ENG_Integer.SIZE_IN_BYTES * b.length + 1);
        buffer.flip();
        ENG_RenderingThread.writeBytes(buffer);
    }

    public static void writeBuffer(long[] b) {
        if (MainActivity.isDebugmode()) {
            if (b.length > 255) {
                throw new IllegalArgumentException("buffer " + b.length + " has more than 255 elements!");
            }
        }
        buffer.position(0);
        buffer.limit(buffer.capacity());
//        buffer.put((byte) b.length);
//        ENG_Utility.alignMemory(buffer, 4);
        ENG_RenderingThread.writeByte((byte) b.length);
        ENG_RenderingThread.alignMemory();
        LongBuffer newBuf = buffer.asLongBuffer();
        newBuf.put(b);
        buffer.position(ENG_Long.SIZE_IN_BYTES * b.length);
        buffer.flip();
        ENG_RenderingThread.writeBytes(buffer);
    }

    public static void writeBuffer(float[] b) {
        if (MainActivity.isDebugmode()) {
            if (b.length > 255) {
                throw new IllegalArgumentException("buffer " + b.length + " has more than 255 elements!");
            }
        }
        buffer.position(0);
        buffer.limit(buffer.capacity());
        buffer.put((byte) b.length);
        FloatBuffer newBuf = buffer.asFloatBuffer();
        newBuf.put(b);
        buffer.position(ENG_Float.SIZE_IN_BYTES * b.length + 1);
        buffer.flip();
        ENG_RenderingThread.writeBytes(buffer);
    }

    public static void writeBuffer(double[] b) {
        if (MainActivity.isDebugmode()) {
            if (b.length > 255) {
                throw new IllegalArgumentException("buffer " + b.length + " has more than 255 elements!");
            }
        }
        buffer.position(0);
        buffer.limit(buffer.capacity());
//        buffer.put((byte) b.length);
//        ENG_Utility.alignMemory(buffer, 4);
        ENG_RenderingThread.writeByte((byte) b.length);
        ENG_RenderingThread.alignMemory();
        DoubleBuffer newBuf = buffer.asDoubleBuffer();
        newBuf.put(b);
        buffer.position(ENG_Double.SIZE_IN_BYTES * b.length);
        buffer.flip();
        ENG_RenderingThread.writeBytes(buffer);
    }

    public static byte[] readByteBuffer(ByteBuffer buf) {
        byte[] b = new byte[buf.get()];
        buf.get(b);
        return b;
    }

    public static void readByteBuffer(ByteBuffer buf, byte[] b) {
        buf.position(buf.position() + 1);
        buf.get(b);
    }

    public static short[] readShortBuffer(ByteBuffer buf) {
        short[] b = new short[buf.get()];
        ShortBuffer newBuf = buf.asShortBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Short.SIZE_IN_BYTES * b.length);
        return b;
    }

    public static void readShortBuffer(ByteBuffer buf, short[] b) {
        buf.position(buf.position() + 1);
        ShortBuffer newBuf = buf.asShortBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Short.SIZE_IN_BYTES * b.length);
    }

    public static int[] readIntBuffer(ByteBuffer buf) {
        int[] b = new int[buf.get()];
        IntBuffer newBuf = buf.asIntBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Integer.SIZE_IN_BYTES * b.length);
        return b;
    }

    public static void readIntBuffer(ByteBuffer buf, int[] b) {
        buf.position(buf.position() + 1);
        IntBuffer newBuf = buf.asIntBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Integer.SIZE_IN_BYTES * b.length);
    }

    public static long[] readLongBuffer(ByteBuffer buf) {
        long[] b = new long[buf.get()];
        LongBuffer newBuf = buf.asLongBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Long.SIZE_IN_BYTES * b.length);
        return b;
    }

    public static void readLongBuffer(ByteBuffer buf, long[] b) {
        buf.position(buf.position() + 1);
        LongBuffer newBuf = buf.asLongBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Long.SIZE_IN_BYTES * b.length);
    }

    public static float[] readFloatBuffer(ByteBuffer buf) {
        float[] b = new float[buf.get()];
        FloatBuffer newBuf = buf.asFloatBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Float.SIZE_IN_BYTES * b.length);
        return b;
    }

    public static void readFloatBuffer(ByteBuffer buf, float[] b) {
        buf.position(buf.position() + 1);
        FloatBuffer newBuf = buf.asFloatBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Float.SIZE_IN_BYTES * b.length);
    }

    public static double[] readDoubleBuffer(ByteBuffer buf) {
        double[] b = new double[buf.get()];
        DoubleBuffer newBuf = buf.asDoubleBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Double.SIZE_IN_BYTES * b.length);
        return b;
    }

    public static void readDoubleBuffer(ByteBuffer buf, double[] b) {
        buf.position(buf.position() + 1);
        DoubleBuffer newBuf = buf.asDoubleBuffer();
        newBuf.get(b);
        buf.position(buf.position() + ENG_Double.SIZE_IN_BYTES * b.length);
    }
    
    public static void writeMap(Map map, DataTypeList keyDataType, DataTypeList valueDataType) {
        // Write everything as key -> value pairs.
        if (MainActivity.isDebugmode()) {
            if (map.size() > 255) {
                throw new IllegalArgumentException("Map with size: " + map.size() + " is larger than 255");
            }
        }
        ENG_RenderingThread.writeByte((byte) map.size());
        for (Object obj : map.entrySet()) {
            Map.Entry entry = (Map.Entry) obj;
            switch (keyDataType) {
                case BYTE: {
                    byte key = (byte) entry.getKey();
                    ENG_RenderingThread.writeByte(key);
                }
                break;
                case SHORT: {
                    short key = (short) entry.getKey();
                    ENG_RenderingThread.writeShort(key);
                }
                    break;
                case INT: {
                    int key = (int) entry.getKey();
                    ENG_RenderingThread.writeInt(key);
                }
                    break;
                case LONG: {
                    long key = (long) entry.getKey();
                    ENG_RenderingThread.writeLong(key);
                }
                    break;
                case FLOAT: {
                    float key = (float) entry.getKey();
                    ENG_RenderingThread.writeFloat(key);
                }
                    break;
                case DOUBLE: {
                    double key = (double) entry.getKey();
                    ENG_RenderingThread.writeDouble(key);
                }
                    break;
                case STRING: {
                    String  key = (String) entry.getKey();
                    writeString(key);
                }
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            switch (valueDataType) {
                case BYTE: {
                    byte value = (byte) entry.getValue();
                    ENG_RenderingThread.writeByte(value);
                }
                break;
                case SHORT: {
                    short value = (short) entry.getValue();
                    ENG_RenderingThread.writeShort(value);
                }
                break;
                case INT: {
                    int value = (int) entry.getValue();
                    ENG_RenderingThread.writeInt(value);
                }
                break;
                case LONG: {
                    long value = (long) entry.getValue();
                    ENG_RenderingThread.writeLong(value);
                }
                break;
                case FLOAT: {
                    float value = (float) entry.getValue();
                    ENG_RenderingThread.writeFloat(value);
                }
                break;
                case DOUBLE: {
                    double value = (double) entry.getValue();
                    ENG_RenderingThread.writeDouble(value);
                }
                break;
                case STRING: {
                    String  value = (String) entry.getValue();
                    writeString(value);
                }
                break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        
    }

    public static void writeColour(ENG_ColorValue c) {
        ENG_RenderingThread.writeFloat(c.r);
        ENG_RenderingThread.writeFloat(c.g);
        ENG_RenderingThread.writeFloat(c.b);
        ENG_RenderingThread.writeFloat(c.a);
    }

    public static void readColour(ByteBuffer buf, ENG_ColorValue ret) {
        float r = buf.getFloat();
        float g = buf.getFloat();
        float b = buf.getFloat();
        float a = buf.getFloat();
        ret.set(r, g, b, a);
    }

    public static void writeVector2D(ENG_Vector2D v) {
        ENG_RenderingThread.writeFloat(v.x);
        ENG_RenderingThread.writeFloat(v.y);
    }

    public static void writeVector3D(ENG_Vector3D v) {
        ENG_RenderingThread.writeFloat(v.x);
        ENG_RenderingThread.writeFloat(v.y);
        ENG_RenderingThread.writeFloat(v.z);
    }

    public static void writeVector3D(ENG_Vector4D v) {
        ENG_RenderingThread.writeFloat(v.x);
        ENG_RenderingThread.writeFloat(v.y);
        ENG_RenderingThread.writeFloat(v.z);
    }

    public static void writeVector4D(ENG_Vector4D v) {
        ENG_RenderingThread.writeFloat(v.x);
        ENG_RenderingThread.writeFloat(v.y);
        ENG_RenderingThread.writeFloat(v.z);
    }

    public static void writeVector4DFull(ENG_Vector4D v) {
        ENG_RenderingThread.writeFloat(v.x);
        ENG_RenderingThread.writeFloat(v.y);
        ENG_RenderingThread.writeFloat(v.z);
        ENG_RenderingThread.writeFloat(v.w);
    }

    public static void writeAxisAlignedBox(ENG_AxisAlignedBox box) {
        writeVector4D(box.getMin());
        writeVector4D(box.getMax());
    }

    public static void readAxisAlignedBox(ByteBuffer buf, ENG_AxisAlignedBox box) {
        ENG_Vector4D min = box.getMin();
        ENG_Vector4D max = box.getMax();
        readVector4D(buf, min);
        readVector4D(buf, max);
        box.setExtents(min, max);
    }

    public static ENG_AxisAlignedBox readAxisAlignedBox(ByteBuffer buf) {
        ENG_AxisAlignedBox box = new ENG_AxisAlignedBox();
        readAxisAlignedBox(buf, box);
        return box;
    }

    public static void readVector2D(ByteBuffer buf, ENG_Vector2D ret) {
        float x = buf.getFloat();
        float y = buf.getFloat();
        ret.set(x, y);
    }

    public static void readVector3D(ByteBuffer buf, ENG_Vector3D ret) {
        float x = buf.getFloat();
        float y = buf.getFloat();
        float z = buf.getFloat();
        ret.set(x, y, z);
    }

    public static void readVector4D(ByteBuffer buf, ENG_Vector4D ret) {
        float x = buf.getFloat();
        float y = buf.getFloat();
        float z = buf.getFloat();
        ret.set(x, y, z);
    }

    public static void readVector4DFull(ByteBuffer buf, ENG_Vector4D ret) {
        float x = buf.getFloat();
        float y = buf.getFloat();
        float z = buf.getFloat();
        float w = buf.getFloat();
        ret.set(x, y, z, w);
    }

    public static void writeQuaternion(ENG_Quaternion v) {
        ENG_RenderingThread.writeFloat(v.x);
        ENG_RenderingThread.writeFloat(v.y);
        ENG_RenderingThread.writeFloat(v.z);
        ENG_RenderingThread.writeFloat(v.w);
    }

    public static void readQuaternion(ByteBuffer buf, ENG_Quaternion ret) {
        float x = buf.getFloat();
        float y = buf.getFloat();
        float z = buf.getFloat();
        float w = buf.getFloat();
        ret.set(x, y, z, w);
    }

    public static void checkIfNullAndBlockPtr(final ENG_Long[] ptr, boolean dataAlreadyFlushed) {
        if (ptr[0].getValue() == 0) {
            // We haven't initialized the pointer yet. Assume it's in the pipeline and wait.
            ENG_RenderingThread.addEndFrameListener(() -> ptr[0].getValue() != 0, dataAlreadyFlushed);
        }
    }

    public static void checkIfNullAndBlockPtr(final long[] ptr, boolean dataAlreadyFlushed) {
        if (ptr[0] == 0) {
            // We haven't initialized the pointer yet. Assume it's in the pipeline and wait.
            ENG_RenderingThread.addEndFrameListener(() -> {
//                    final boolean[] b = new boolean[1];
//                    MainApp.getMainThread().runOnMainThread(new ENG_IRunOnMainThread() {
//                        @Override
//                        public void run() {
//                            b[0] = screenPtr[0] != 0;
//                        }
//                    });
//                    return b[0];
                return ptr[0] != 0;
            }, dataAlreadyFlushed);
        }
    }

    public static void checkIfNullAndBlockPtrArray(final ENG_Long[][] ptr, boolean dataAlreadyFlushed) {
        boolean nullFound = false;
        for (ENG_Long[] engLongs : ptr) {
            if (engLongs != null && engLongs[0].getValue() == 0) {
                nullFound = true;
                break;
            }
        }
        if (nullFound) {
            ENG_RenderingThread.addEndFrameListener(() -> {
                for (ENG_Long[] engLongs : ptr) {
                    if (engLongs != null && engLongs[0].getValue() == 0) {
                        return false;
                    }
                }
                return true;
            }, dataAlreadyFlushed);
        }
    }

    public static void checkIfNullAndBlockPtrArray(final long[] ptr, boolean dataAlreadyFlushed) {
        boolean nullFound = false;
        for (long l : ptr) {
            if (l == 0) {
                nullFound = true;
                break;
            }
        }
        if (nullFound) {
            ENG_RenderingThread.addEndFrameListener(() -> {
                for (long l : ptr) {
                    if (l == 0) {
                        return false;
                    }
                }
                return true;
            }, dataAlreadyFlushed);
        }
    }

    public static byte setPtrJustCreated(byte b, boolean justCreated) {
        if (justCreated) {
            b |= 0x1;
        }
        return b;
    }

    public static void setPtr(ENG_NativePointer nativePointer, boolean justCreated) {
        byte b = 0;
        ENG_RenderingThread.writeByte(setPtrJustCreated(b, justCreated));
        ENG_RenderingThread.writeLong(nativePointer.getPointer());
    }

    public static void setPtr(long nativePointer, boolean justCreated) {
        byte b = 0;
        ENG_RenderingThread.writeByte(setPtrJustCreated(b, justCreated));
        ENG_RenderingThread.writeLong(nativePointer);
    }

    public static long callNewRoot(String pluginFileName,
                                   String configFileName,
                                   String logFileName) {
        final long[] rootPtr = new long[1];
//        ENG_RenderingThread.writeByte(NativeCallsList.NEWROOT.getCallPos());
        writeString(pluginFileName);
        writeString(configFileName);
        writeString(logFileName);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener() {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                rootPtr[0] = responseBuffer.getLong();
            }
        });
        ENG_RenderingThread.flushPipeline(true);
        return rootPtr[0];
    }

    public static void callRoot_Initialize() {
//        ENG_RenderingThread.writeByte(NativeCallsList.ROOT_INITIALIZE.getCallPos());
    }

    public static class ViewportData {
//        public long renderWindowPtr;
//        public long viewportPtr;
        public int left, top, width, height;
    }

    private static void setFrameEndListenerForObjectCreation(final ENG_NativePointerWithSetter obj) {
        ENG_RenderingThread.addFrameEndListener(ENG_NativeCallsObjectPool.getFrameEndListenerForObjectCreation(obj));
    }

    public static long[] callRoot_CreateRenderWindow(long rootPointer, String name, int width, int height,
                                                     boolean fullScreen, TreeMap<String, String> params,
                                                     long uiViewController) {
//        final RenderWindowAndViewportData renderWindowAndViewportData = new RenderWindowAndViewportData();
        final long[] renderWindowPtr = new long[MainApp.Platform.isMobile() ? 1 :
                (MainApp.DesktopPlatform.isWin32Desktop() || MainApp.getApplicationMode() == MainApp.Mode.SERVER) ? 3 : -1 /*force a crash*/];
        ENG_RenderingThread.writeByte(NativeCallsList.ROOT_CREATERENDERWINDOW.getCallPos());
        ENG_RenderingThread.writeLong(rootPointer);
        if (MainApp.PLATFORM == MainApp.Platform.IOS) {
            ENG_RenderingThread.writeLong(uiViewController);
        }
        writeString(name);
        ENG_RenderingThread.writeInt(width);
        ENG_RenderingThread.writeInt(height);
        ENG_RenderingThread.writeBoolean(fullScreen);
        writeMap(params, DataTypeList.STRING, DataTypeList.STRING);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener() {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                ENG_Utility.alignMemory(responseBuffer, 4);
                renderWindowPtr[0] = responseBuffer.getLong();
                if (MainApp.DesktopPlatform.isWin32Desktop()) {
                    renderWindowPtr[1] = responseBuffer.getLong(); // HWND
                    renderWindowPtr[2] = responseBuffer.getLong(); // HDC
                }
//                renderWindowAndViewportData.renderWindowPtr = responseBuffer.getLong();
//                renderWindowAndViewportData.viewportPtr = responseBuffer.getLong();
//                renderWindowAndViewportData.left = responseBuffer.getInt();
//                renderWindowAndViewportData.top = responseBuffer.getInt();
//                renderWindowAndViewportData.width = responseBuffer.getInt();
//                renderWindowAndViewportData.height = responseBuffer.getInt();
            }
        });
        ENG_RenderingThread.flushPipeline(true);
//        return renderWindowAndViewportData;
        return renderWindowPtr;
    }

    public static ViewportData getViewport(long renderWindowPtr) {
        final ViewportData viewportData = new ViewportData();
        ENG_RenderingThread.writeByte(NativeCallsList.GET_VIEWPORT.getCallPos());
        ENG_RenderingThread.writeLong(renderWindowPtr);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener() {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                viewportData.left = responseBuffer.getInt();
                viewportData.top = responseBuffer.getInt();
                viewportData.width = responseBuffer.getInt();
                viewportData.height = responseBuffer.getInt();
            }
        });
        ENG_RenderingThread.flushPipeline(true);
        return viewportData;
    }

    public static void callRoot_CreateSceneManager(ENG_SceneManager.SceneType sceneType, int numWorkerThreads,
                                                   ENG_SceneManager.InstancingThreadedCullingMethod threadedCullingMethod,
                                                   String name) {
//        ENG_RenderingThread.writeByte(NativeCallsList.ROOT_CREATESCENEMANAGER.getCallPos());
        ENG_RenderingThread.writeShort(sceneType.getType());
        ENG_RenderingThread.writeInt(numWorkerThreads);
        ENG_RenderingThread.writeInt(threadedCullingMethod.getCullingMethod());
        writeString(name);
    }

    public static void callRenderSystem_SetConfigOption(String key, String value) {
//        ENG_RenderingThread.writeByte(NativeCallsList.RENDERSYSTEM_SETCONFIGOPTION.getCallPos());
        writeString(key);
        writeString(value);
    }

    public static long[] callScreenNative_Create(long[] atlasPtr) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCREEN_CREATE.getCallPos());
        writeBuffer(atlasPtr);
//        ENG_RenderingThread.writeLong(atlasPtr[0]);
//        ENG_RenderingThread.writeLong(atlasPtr[1]);
        final long[] buf = new long[1];
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                buf[0] = responseBuffer.getLong();
            }
        });
//        final long[] buf = new long[atlasPtr.length];
//        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(atlasPtr.length + 1) {
//            @Override
//            public void runOnMainThread(ByteBuffer responseBuffer) {
//                readLongBuffer(responseBuffer, buf);
//            }
//        });
        return buf;
    }

    public static void callScreenNative_Destroy(final long[] screenPtr, boolean dataAlreadyFlushed) {
        checkIfNullAndBlockPtr(screenPtr, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(NativeCallsList.SCREEN_DESTROY.getCallPos());
        ENG_RenderingThread.writeLong(screenPtr[0]);
    }

    public static ENG_Long[] callScreenNative_CreateLayer(long[] screenPtr, long texturePtr,
                                                          long sceneManager,
                                                          byte queueGroupId, boolean dataAlreadyFlushed) {
        checkIfNullAndBlockPtr(screenPtr, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(NativeCallsList.SCREEN_CREATELAYER.getCallPos());
        ENG_RenderingThread.writeLong(screenPtr[0]);
        ENG_RenderingThread.writeLong(texturePtr);
        ENG_RenderingThread.writeLong(sceneManager);
        ENG_RenderingThread.writeByte(queueGroupId);
        final ENG_Long[] screenRenderable = new ENG_Long[1];
        screenRenderable[0] = new ENG_Long();
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                screenRenderable[0].setValue(responseBuffer.getLong());
            }
        });
        return screenRenderable;
    }

    public static void callScreenNative_DestroyLayer(long[] screenPtr, long[] screenRenderablePtrList,
                                                     byte[] queueGroupIds, boolean dataAlreadyFlushed) {
        checkIfNullAndBlockPtr(screenPtr, dataAlreadyFlushed);
        // We don't actually send the screenRenderablePtrList because the queueGroupIds is enough
        // but we still check if the screen renderables have come back from the native code and
        // are correctly initialized. If not, we should block or else the queueGroupIds might be
        // referring to inexistent screen renderables.
        checkIfNullAndBlockPtrArray(screenRenderablePtrList, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(NativeCallsList.SCREEN_DESTROYLAYER.getCallPos());
//        writeBuffer(screenRenderablePtrList);
        ENG_RenderingThread.writeLong(screenPtr[0]);
        writeBuffer(queueGroupIds);
    }

    public static void callScreenNative_RenderOnce() {
        ENG_RenderingThread.writeByte(NativeCallsList.SCREEN_RENDERONCE.getCallPos());
    }

    public static void callScreenNative_UpdateVertexListSize(long[] screenPtr, ENG_Long[][] screenRenderablePtrList,
                                                             int size, byte bufferNum, byte queueGroupId,
                                                             boolean dataAlreadyFlushed) {
        checkIfNullAndBlockPtr(screenPtr, dataAlreadyFlushed);
        // We don't actually send the screenRenderablePtrList because the queueGroupIds is enough
        // but we still check if the screen renderables have come back from the native code and
        // are correctly initialized. If not, we should block or else the queueGroupIds might be
        // referring to inexistent screen renderables.
        checkIfNullAndBlockPtrArray(screenRenderablePtrList, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(NativeCallsList.SCREEN_UPDATEVERTEXLISTSIZE.getCallPos());
        ENG_RenderingThread.writeLong(screenPtr[0]);
        ENG_RenderingThread.writeInt(size);
        ENG_RenderingThread.writeByte(bufferNum);
        ENG_RenderingThread.writeByte(queueGroupId);
    }

    public static void callRoot_RenderOneFrame(long ptr) {
        ENG_RenderingThread.writeByte(NativeCallsList.ROOT_RENDERONEFRAME.getCallPos());
        ENG_RenderingThread.writeLong(ptr);
    }

//    public static void callScreenRenderable_SetVisible(long ptr, boolean visible) {
//        ENG_RenderingThread.writeByte(NativeCallsList.SCREENRENDERABLE_SETVISIBLE.getCallPos());
//        ENG_RenderingThread.writeLong(ptr);
//        ENG_RenderingThread.writeBoolean(visible);
//    }

    public static void callScreen_SetVisible(long[] ptr, boolean visible, boolean dataAlreadyFlushed) {
        checkIfNullAndBlockPtr(ptr, dataAlreadyFlushed);
        ENG_RenderingThread.writeByte(NativeCallsList.SCREEN_SETVISIBLE.getCallPos());
        ENG_RenderingThread.writeLong(ptr[0]);
        ENG_RenderingThread.writeBoolean(visible);
    }

    public static ENG_RaySceneQuery createRayQuery(ENG_Ray ray, boolean justCreated) {
        ENG_RenderingThread.writeByte(NativeCallsList.CREATE_RAY_QUERY.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        final ENG_RaySceneQuery rayQuery = sceneManager.createRayQuery(ray);
        rayQuery.setPointer(ENG_Utility.getUniqueId());
        setPtr(rayQuery, justCreated);
        writeVector4D(ray.getOrigin());
        writeVector4D(ray.getDir());
        setFrameEndListenerForObjectCreation(rayQuery);
        return rayQuery;
    }

    /**
     * The destruction now happens after execution of the ray query in the frame end listener in the native side.
     * @param raySceneQuery
     * @param justCreated
     */
    @Deprecated
    public static void destroyRayQuery(ENG_RaySceneQuery raySceneQuery, boolean justCreated) {
        ENG_RenderingThread.writeByte(NativeCallsList.DESTROY_RAY_QUERY.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(raySceneQuery, justCreated);
    }

    public static void rayQuery_SetSortByDistance(long raySceneQueryPtr, boolean justCreated, boolean sort) {
        ENG_RenderingThread.writeByte(NativeCallsList.RAY_QUERY_SETSORTBYDISTANCE.getCallPos());
        setPtr(raySceneQueryPtr, justCreated);
        ENG_RenderingThread.writeBoolean(sort);
    }

    public static ArrayList<ENG_RaySceneQuery.RaySceneQueryResultEntry> rayQuery_Execute(final HudManager.RaySceneQueryPair raySceneQueryPair, long raySceneQueryPtr, boolean justCreated) {
        ENG_RenderingThread.writeByte(NativeCallsList.RAY_QUERY_EXECUTE.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(raySceneQueryPtr, justCreated);
        final ArrayList<ENG_RaySceneQuery.RaySceneQueryResultEntry> resultEntries = new ArrayList<>();
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(
                (ENG_Float.SIZE_IN_BYTES + ENG_Long.SIZE_IN_BYTES) * MAX_RAY_SCENE_QUERY_RESPONSE_SIZE + ENG_Integer.SIZE_IN_BYTES,
                false, true, "RAY_QUERY_EXECUTE") {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
                int resultNum = responseBuffer.getInt();
//                System.out.println("rayQuery_Execute return resultNum: " + resultNum);
//                if (resultNum == 0) {
//                    float distance = responseBuffer.getFloat();
//                    ENG_Utility.alignMemory(responseBuffer, 4);
//                    ENG_NativePointer movableObject = sceneManager.getNativeObjectFromNativePtr(responseBuffer.getLong());
//                    System.out.println();
//                }
                for (int i = 0; i < resultNum; ++i) {
                    ENG_RaySceneQuery.RaySceneQueryResultEntry dets = new ENG_RaySceneQuery.RaySceneQueryResultEntry();
                    dets.distance = responseBuffer.getFloat();
                    ENG_Utility.alignMemory(responseBuffer, 4);
                    // We must take into account the fact that sometimes we send a destroyItem()
                    // message from the java side which causes the native object to be removed
                    // from the list. But since the rayQuery execution happens later on the native
                    // side but before getting to ITEM_DESTROY message it means that we can get
                    // back in java side the pointer for an object that has been destroyed
                    // and no longer exists on both sides. In which case we should ignore
                    // that pointer and not throw.
                    ENG_NativePointer movableObject = sceneManager.getNativeObjectFromNativePtr(responseBuffer.getLong(), false);
                    if (movableObject == null) {
                        if (MainActivity.isDebugmode()) {
                            System.out.println("Received native pointer for which there is no native object");
                        }
                        continue;
                    }
                    if (MainActivity.isDebugmode()) {
                        if (!(movableObject instanceof ENG_Item)) {
                            throw new IllegalArgumentException("Item ptr: " + movableObject.getPointer());
                        }
                    }
                    dets.movable = (ENG_Item) movableObject;
                    resultEntries.add(dets);

                }
                raySceneQueryPair.rayQueryResultsArrived = true;
            }
        });
        return resultEntries;
    }

    private static void setItemData(ByteBuffer responseBuffer, ENG_Item item, int subItemCount) {
        item.setPointer(responseBuffer.getLong());
        item.setNativePointer(true);
        ENG_RenderRoot.getRenderRoot().getSceneManager().addNativePtrToNativeObject(item.getPointer(), item);
        for (int i = 0; i < subItemCount; ++i) {
            item.setSubItemPtr(responseBuffer.getLong());
        }
    }

    public static void sceneManager_createItem(final ENG_Item item, String meshName, final int subItemCount) {
        sceneManager_createItem(item, meshName, subItemCount, ENG_Workflows.SpecularWorkflow);
    }

    public static void sceneManager_createItem(final ENG_Item item, String meshName, final int subItemCount, ENG_Workflows workflow) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_ITEM_CREATE.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        item.setPointer(ENG_Utility.getUniqueId());
        setPtr(item, true);
        writeString(meshName);
        ENG_RenderingThread.writeByte(workflow.getWorkflow());
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(
                ENG_Long.SIZE_IN_BYTES * (subItemCount + 1), true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                setItemData(responseBuffer, item, subItemCount);
            }
        });
    }

    public static void sceneManager_createItem(final ENG_Item item, String meshName, String groupName,
                                               ENG_SceneManager.SceneMemoryMgrTypes type, final int subItemCount) {
        sceneManager_createItem(item, meshName, groupName, type, subItemCount, ENG_Workflows.SpecularWorkflow);
    }

    public static void sceneManager_createItem(final ENG_Item item, String meshName, String groupName,
                                               ENG_SceneManager.SceneMemoryMgrTypes type,
                                               final int subItemCount, ENG_Workflows workflow) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_ITEM_CREATE_ALL_PARAMS.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        item.setPointer(ENG_Utility.getUniqueId());
        setPtr(item, true);
        writeString(meshName);
        writeString(groupName);
        ENG_RenderingThread.writeByte(type.getType());
        ENG_RenderingThread.writeByte(workflow.getWorkflow());
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(
                ENG_Long.SIZE_IN_BYTES * (subItemCount + 1), true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                if (!item.isDestroyed()) {
                    setItemData(responseBuffer, item, subItemCount);
                } else {
                    if (MainActivity.isDebugmode()) {
                        System.out.println("Trying to add item data for already destroyed item.");
                    }
                }
            }
        });
    }

    public static void sceneManager_destroyItem(ENG_Item item) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_ITEM_DESTROY.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(item, !item.isNativePointerSet());
        if (item.isNativePointerSet()) {
//            System.out.println("removeNativePtrToNativeObject item: " + item.getName() + " pointer: " + item.getPointer());
            sceneManager.removeNativePtrToNativeObject(item.getPointer());
        }
    }

    /**
     * Make sure that all the items have been created on the main thread so there is no pointer in
     * the internal native ptr map. The item cannot be justCreated.
     */
    public static void sceneManager_destroyAllItems() {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_ITEM_DESTROY_ALL.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        sceneManager.clearNativePtrToMovableObjectMap();
    }

    public static void sceneManager_createParticleSystem(final ENG_ParticleSystemNative particleSystem, String templateName) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_CREATE_PARTICLE_SYSTEM_STRING.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        particleSystem.setPointer(ENG_Utility.getUniqueId());
        setPtr(particleSystem, true);
        writeString(templateName);
        setFrameEndListenerForObjectCreation(particleSystem);
    }

    public static void sceneManager_createParticleSystem(final ENG_ParticleSystemNative particleSystem, int quota, String resourceGroup) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_CREATE_PARTICLE_SYSTEM_QUOTA_RESOURCE_GROUP.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        particleSystem.setPointer(ENG_Utility.getUniqueId());
        setPtr(particleSystem, true);
        ENG_RenderingThread.writeInt(quota);
        writeString(resourceGroup);
        setFrameEndListenerForObjectCreation(particleSystem);
    }

    public static void sceneManager_destroyParticleSystem(ENG_ParticleSystemNative particleSystem) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_DESTROY_PARTICLE_SYSTEM.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(particleSystem, !particleSystem.isNativePointerSet());
    }

    /**
     * Make sure that all the items have been created on the main thread so there is no pointer in
     * the internal native ptr map. The item cannot be justCreated.
     */
    public static void sceneManager_destroyAllParticleSystems() {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_DESTROY_ALL_PARTICLE_SYSTEMS.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
    }

    public static void sceneManager_createBillboardSet(final ENG_BillboardSetNative billboardSet, int poolSize, String name) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_CREATE_BILLBOARD_SET.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        billboardSet.setPointer(ENG_Utility.getUniqueId());
        setPtr(billboardSet, true);
        ENG_RenderingThread.writeInt(poolSize);
        writeString(name);
        setFrameEndListenerForObjectCreation(billboardSet);
    }

    public static void sceneManager_destroyBillboardSet(ENG_BillboardSetNative billboardSet) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_DESTROY_BILLBOARD_SET.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(billboardSet, !billboardSet.isNativePointerSet());
    }

    /**
     * Make sure that all the items have been created on the main thread so there is no pointer in
     * the internal native ptr map. The item cannot be justCreated.
     */
    public static void sceneManager_destroyAllBillboardSets() {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_DESTROY_ALL_BILLBOARD_SETS.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
    }

    /**
     * Make sure that all the items have been created on the main thread so there is no pointer in
     * the internal native ptr map. The item cannot be justCreated.
     */
    public static void sceneManager_clearScene() {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_CLEAR_SCENE.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
    }

    public static void sceneManager_setAmbientLight(ENG_ColorValue upperHemisphere, ENG_ColorValue lowerHemisphere,
                                                    ENG_Vector3D hemisphereDir) {
        sceneManager_setAmbientLight(upperHemisphere, lowerHemisphere, hemisphereDir, 1.0f);
    }

    public static void sceneManager_setAmbientLight(ENG_ColorValue upperHemisphere, ENG_ColorValue lowerHemisphere,
                                                    ENG_Vector3D hemisphereDir, float envmapScale) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_SET_AMBIENT_LIGHT.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        writeColour(upperHemisphere);
        writeColour(lowerHemisphere);
        writeVector3D(hemisphereDir);
        ENG_RenderingThread.writeFloat(envmapScale);
    }

    public static void sceneManager_setSkybox(boolean enable, String materialName) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_SET_SKYBOX.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        ENG_RenderingThread.writeBoolean(enable);
        writeString(materialName);
        throw new IllegalStateException("No longer implemented");
    }

    public static void sceneManager_setSkybox(boolean enable, String materialName, float distance,
                                              boolean drawFirst, ENG_Quaternion orientation, String groupName) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_SET_SKYBOX_ALL_PARAMS.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        ENG_RenderingThread.writeBoolean(enable);
        writeString(materialName);
        ENG_RenderingThread.writeFloat(distance);
        ENG_RenderingThread.writeBoolean(drawFirst);
        writeQuaternion(orientation);
        writeString(groupName);
        throw new IllegalStateException("No longer implemented");
    }

    public static void sceneManager_setSkyboxEnabled(boolean enable) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_SET_SKYBOX_ENABLED.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        ENG_RenderingThread.writeBoolean(enable);
        throw new IllegalStateException("No longer implemented");
    }

    public static void sceneManager_createSceneNode(final ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_CREATE_SCENE_NODE.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        node.setPointer(ENG_Utility.getUniqueId());
        setPtr(node, true);
        setFrameEndListenerForObjectCreation(node);
    }

    public static void sceneManager_createSceneNode(final ENG_SceneNode node, ENG_SceneManager.SceneMemoryMgrTypes type) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_CREATE_SCENE_NODE_ALL_PARAMS.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        node.setPointer(ENG_Utility.getUniqueId());
        setPtr(node, true);
        ENG_RenderingThread.writeByte(type.getType());
        setFrameEndListenerForObjectCreation(node);
    }

    public static void sceneManager_destroySceneNode(ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_DESTROY_SCENE_NODE.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(node, !node.isNativePointerSet());
    }

    public static void sceneManager_createLight(ENG_LightNative light) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_CREATE_LIGHT.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        light.setPointer(ENG_Utility.getUniqueId());
        setPtr(light, true);
        setFrameEndListenerForObjectCreation(light);
    }

    public static void sceneManager_destroyLight(ENG_LightNative light) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_DESTROY_LIGHT.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(light, !light.isNativePointerSet());
    }

    public static void sceneManager_destroyAllLights() {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_DESTROY_ALL_LIGHTS.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
    }

    public static void sceneManager_notifyStaticDirty(ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENEMANAGER_NOTIFY_STATIC_DIRTY.getCallPos());
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(node, !node.isNativePointerSet());
    }

    public static void node_addChild(final ENG_SceneNode parentNode, ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.NODE_ADD_CHILD.getCallPos());
        setPtr(parentNode, !parentNode.isNativePointerSet());
        setPtr(node, !node.isNativePointerSet());
    }

    public static void node_removeChild(final ENG_SceneNode parentNode, ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.NODE_REMOVE_CHILD.getCallPos());
        setPtr(parentNode, !parentNode.isNativePointerSet());
        setPtr(node, !node.isNativePointerSet());
    }

//    public static void node_hasChild(final ENG_SceneNode parentNode, ENG_SceneNode node,
//                                     final ENG_Boolean hasChildValue, final ENG_Boolean retSet) {
//        ENG_RenderingThread.writeByte(NativeCallsList.NODE_ADD_CHILD.getCallPos());
//        setPtr(parentNode, !parentNode.isNativePointerSet());
//        setPtr(node, !node.isNativePointerSet());
//        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Boolean.SIZE_IN_BYTES, false) {
//            @Override
//            public void runOnMainThread(ByteBuffer responseBuffer) {
//                byte hasChild = responseBuffer.get();
//                hasChildValue.setValue(hasChild == 1);
//                retSet.setValue(true);
//            }
//        });
//    }

//    public static void node_setParent(final ENG_SceneNode node, ENG_SceneNode parentNode) {
//        ENG_RenderingThread.writeByte(NativeCallsList.NODE_UNSET_PARENT.getCallPos());
//        setPtr(node, !node.isNativePointerSet());
//        setPtr(parentNode, !parentNode.isNativePointerSet());
//    }
//
//    public static void node_unsetParent(final ENG_SceneNode node) {
//        ENG_RenderingThread.writeByte(NativeCallsList.NODE_UNSET_PARENT.getCallPos());
//        setPtr(node, !node.isNativePointerSet());
//    }

    /**
     * This assumes that the parent node already has an actual pointer and not a dummy temporary pointer.
     * Cannot create nodes and subnodes in one frame.
     * @param parentNode
     * @param node
     */
    public static void sceneNode_createChildSceneNode(final ENG_SceneNode parentNode, ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_CREATE_CHILD.getCallPos());
//        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
//        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(parentNode, !parentNode.isNativePointerSet());
        node.setPointer(ENG_Utility.getUniqueId());
        setPtr(node, true);
        setFrameEndListenerForObjectCreation(node);
    }

    /**
     * This assumes that the parent node already has an actual pointer and not a dummy temporary pointer.
     * Cannot create nodes and subnodes in one frame.
     * @param parentNode
     * @param node
     * @param type
     * @param translate
     * @param rotate
     */
    public static void sceneNode_createChildSceneNode(final ENG_SceneNode parentNode, ENG_SceneNode node,
                                                      ENG_SceneManager.SceneMemoryMgrTypes type,
                                                      ENG_Vector3D translate, ENG_Quaternion rotate) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_CREATE_CHILD_ALL_PARAMS.getCallPos());
//        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
//        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(parentNode, !parentNode.isNativePointerSet());
        node.setPointer(ENG_Utility.getUniqueId());
        setPtr(node, true);
        ENG_RenderingThread.writeByte(type.getType());
        writeVector3D(translate);
        writeQuaternion(rotate);
        setFrameEndListenerForObjectCreation(node);
    }

    public static void sceneNode_removeAndDestroyChild(ENG_SceneNode parentNode, ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_REMOVE_AND_DESTROY.getCallPos());
//        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
//        ENG_RenderingThread.writeLong(sceneManager.getPointer());
//        System.out.println("SCENENODE_REMOVE_AND_DESTROY parentNode: " + parentNode.getName() + " child: " + node.getName());
        ENG_RenderingThread.writeLong(parentNode.getPointer());
        setPtr(node, !node.isNativePointerSet());
    }

    public static void sceneNode_removeAndDestroyAllChildren(ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_REMOVE_AND_DESTROY_ALL_CHILDREN.getCallPos());
//        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
//        ENG_RenderingThread.writeLong(sceneManager.getPointer());
//        System.out.println("SCENENODE_REMOVE_AND_DESTROY_ALL_CHILDREN parentNode: " + node.getName());
        setPtr(node, !node.isNativePointerSet());
    }

    public static void sceneNode_attachObject(ENG_SceneNode node, ENG_NativePointerWithSetter item) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_ATTACH_OBJECT.getCallPos());
//        ENG_RenderingThread.writeLong(node.getPointer());
        setPtr(node, !node.isNativePointerSet());
        setPtr(item, !item.isNativePointerSet());
    }

    public static void sceneNode_attachParticleSystem(ENG_SceneNode node, ENG_NativePointerWithSetter item) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_ATTACH_PARTICLE_SYSTEM.getCallPos());
//        ENG_RenderingThread.writeLong(node.getPointer());
        setPtr(node, !node.isNativePointerSet());
        setPtr(item, !item.isNativePointerSet());
    }

    /**
     * Hack for supporting cameras.
     * @param node
     * @param item
     */
    public static void sceneNode_attachObject(ENG_SceneNode node, long item) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_ATTACH_OBJECT.getCallPos());
//        ENG_RenderingThread.writeLong(node.getPointer());
        setPtr(node, !node.isNativePointerSet());
        setPtr(item, false);
    }

    public static void sceneNode_detachObject(ENG_SceneNode node, ENG_NativePointerWithSetter item) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_DETACH_OBJECT.getCallPos());
//        ENG_RenderingThread.writeLong(node.getPointer());
        setPtr(node, !node.isNativePointerSet());
        setPtr(item, !item.isNativePointerSet());
    }

    /**
     * Hack for supporting cameras.
     * @param node
     * @param item
     */
    public static void sceneNode_detachObject(ENG_SceneNode node, long item) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_DETACH_OBJECT.getCallPos());
//        ENG_RenderingThread.writeLong(node.getPointer());
        setPtr(node, !node.isNativePointerSet());
        setPtr(item, false);
    }

    public static void sceneNode_detachAllObjects(ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_DETACH_ALL_OBJECTS.getCallPos());
//        ENG_RenderingThread.writeLong(node.getPointer());
        setPtr(node, !node.isNativePointerSet());
    }

    public static void sceneNode_setPosition(ENG_SceneNode node, float x, float y, float z) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETPOSITION_XYZ.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        ENG_RenderingThread.writeFloat(x);
        ENG_RenderingThread.writeFloat(y);
        ENG_RenderingThread.writeFloat(z);
    }

    public static void sceneNode_setPosition(ENG_SceneNode node, ENG_Vector3D pos) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETPOSITION_VEC.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(pos);
    }

    public static void sceneNode_setPosition(ENG_SceneNode node, ENG_Vector4D pos) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETPOSITION_VEC.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(pos);
    }

    public static void sceneNode_setOrientation(ENG_SceneNode node, ENG_Quaternion rotate) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETORIENTATION.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeQuaternion(rotate);
    }

    public static void sceneNode_setScaling(ENG_SceneNode node, float x, float y, float z) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETSCALING_XYZ.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        ENG_RenderingThread.writeFloat(x);
        ENG_RenderingThread.writeFloat(y);
        ENG_RenderingThread.writeFloat(z);
    }

    public static void sceneNode_setScaling(ENG_SceneNode node, ENG_Vector3D scale) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETSCALING_XYZ.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(scale);
    }

    public static void sceneNode_setScaling(ENG_SceneNode node, ENG_Vector4D scale) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETSCALING_XYZ.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(scale);
    }

    public static void sceneNode_setDerivedPosition(ENG_SceneNode node, float x, float y, float z) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETDERIVEDPOSITION_XYZ.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        ENG_RenderingThread.writeFloat(x);
        ENG_RenderingThread.writeFloat(y);
        ENG_RenderingThread.writeFloat(z);
    }

    public static void sceneNode_setDerivedPosition(ENG_SceneNode node, ENG_Vector3D pos) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETDERIVEDPOSITION_VEC.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(pos);
    }

    public static void sceneNode_setDerivedPosition(ENG_SceneNode node, ENG_Vector4D pos) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETDERIVEDPOSITION_VEC.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(pos);
    }

    public static void sceneNode_setDerivedOrientation(ENG_SceneNode node, ENG_Quaternion rotate) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETDERIVEDORIENTATION.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeQuaternion(rotate);
    }

    public static void sceneNode_lookAt(ENG_SceneNode node, ENG_Vector3D targetPoint, ENG_Node.TransformSpace relativeTo) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_LOOKAT.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(targetPoint);
        ENG_RenderingThread.writeByte(relativeTo.getPos());
    }

    public static void sceneNode_lookAt(ENG_SceneNode node, ENG_Vector3D targetPoint, ENG_Node.TransformSpace relativeTo,
                                        ENG_Vector3D localDirectionVector) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_LOOKAT_ALL_PARAMS.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(targetPoint);
        ENG_RenderingThread.writeByte(relativeTo.getPos());
        writeVector3D(localDirectionVector);
    }

    public static void sceneNode_setDirection(ENG_SceneNode node, ENG_Vector3D targetPoint, ENG_Node.TransformSpace relativeTo) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETDIRECTION.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(targetPoint);
        ENG_RenderingThread.writeByte(relativeTo.getPos());
    }

    public static void sceneNode_setDirection(ENG_SceneNode node, ENG_Vector3D targetPoint, ENG_Node.TransformSpace relativeTo,
                                        ENG_Vector3D localDirectionVector) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETDIRECTION_ALL_PARAMS.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeVector3D(targetPoint);
        ENG_RenderingThread.writeByte(relativeTo.getPos());
        writeVector3D(localDirectionVector);
    }

    public static void sceneNode_setVisible(ENG_SceneNode node, boolean visible) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETVISIBLE.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        ENG_RenderingThread.writeBoolean(visible);
    }

    public static void sceneNode_flipVisibility(ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_FLIPVISIBILITY.getCallPos());
        setPtr(node, !node.isNativePointerSet());
    }

    public static void sceneNode_setStatic(ENG_SceneNode node, boolean isStatic) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETSTATIC.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        ENG_RenderingThread.writeBoolean(isStatic);
    }

    public static void sceneNode_setName(ENG_SceneNode node) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENENODE_SETNAME.getCallPos());
        setPtr(node, !node.isNativePointerSet());
        writeString(node.getName());
    }

    /** @noinspection deprecation*/
    public static void light_setType(ENG_LightNative light, ENG_Light.LightTypes type) {
        ENG_RenderingThread.writeByte(NativeCallsList.LIGHT_SET_TYPE.getCallPos());
        setPtr(light, !light.isNativePointerSet());
        ENG_RenderingThread.writeByte((byte) type.getType());
    }

    public static void light_setSpotlightRange(ENG_LightNative light, float innerAngle, float outerAngle,
                                               float falloff) {
        ENG_RenderingThread.writeByte(NativeCallsList.LIGHT_SET_SPOTLIGHT_RANGE.getCallPos());
        setPtr(light, !light.isNativePointerSet());
        ENG_RenderingThread.writeFloat(innerAngle);
        ENG_RenderingThread.writeFloat(outerAngle);
        ENG_RenderingThread.writeFloat(falloff);
    }

    public static void light_setDiffuseColour(ENG_LightNative light, float r, float g, float b) {
        ENG_RenderingThread.writeByte(NativeCallsList.LIGHT_SET_DIFFUSE_COLOUR.getCallPos());
        setPtr(light, !light.isNativePointerSet());
        ENG_RenderingThread.writeFloat(r);
        ENG_RenderingThread.writeFloat(g);
        ENG_RenderingThread.writeFloat(b);
    }

    public static void light_setSpecularColour(ENG_LightNative light, float r, float g, float b) {
        ENG_RenderingThread.writeByte(NativeCallsList.LIGHT_SET_SPECULAR_COLOUR.getCallPos());
        setPtr(light, !light.isNativePointerSet());
        ENG_RenderingThread.writeFloat(r);
        ENG_RenderingThread.writeFloat(g);
        ENG_RenderingThread.writeFloat(b);
    }

    public static void light_setAttenuation(ENG_LightNative light, float range,
                                            float constant, float linear, float quadratic) {
        ENG_RenderingThread.writeByte(NativeCallsList.LIGHT_SET_ATTENUATION.getCallPos());
        setPtr(light, !light.isNativePointerSet());
        ENG_RenderingThread.writeFloat(range);
        ENG_RenderingThread.writeFloat(constant);
        ENG_RenderingThread.writeFloat(linear);
        ENG_RenderingThread.writeFloat(quadratic);
    }

    public static void light_setPowerScale(ENG_LightNative light, float power) {
        ENG_RenderingThread.writeByte(NativeCallsList.LIGHT_SET_POWER_SCALE.getCallPos());
        setPtr(light, !light.isNativePointerSet());
        ENG_RenderingThread.writeFloat(power);
    }

    public static void light_setAttenuationBasedOnRange(ENG_LightNative light,
                                                        float radius, float lumThreshold) {
        ENG_RenderingThread.writeByte(NativeCallsList.LIGHT_SET_ATTENUATION_BASED_ON_RADIUS.getCallPos());
        setPtr(light, !light.isNativePointerSet());
        ENG_RenderingThread.writeFloat(radius);
        ENG_RenderingThread.writeFloat(lumThreshold);
    }

//    public static void light_setPosition(ENG_LightNative light, float x, float y, float z) {
//        ENG_RenderingThread.writeByte(NativeCallsList.LIGHT_SET_POSITION.getCallPos());
//        setPtr(light, !light.isNativePointerSet());
//        ENG_RenderingThread.writeFloat(x);
//        ENG_RenderingThread.writeFloat(y);
//        ENG_RenderingThread.writeFloat(z);
//    }

    public static void light_setDirection(ENG_LightNative light, float x, float y, float z) {
        ENG_RenderingThread.writeByte(NativeCallsList.LIGHT_SET_DIRECTION.getCallPos());
        setPtr(light, !light.isNativePointerSet());
        ENG_RenderingThread.writeFloat(x);
        ENG_RenderingThread.writeFloat(y);
        ENG_RenderingThread.writeFloat(z);
    }

    public static void item_setDatablock(ENG_Item item, String datablockName) {
        ENG_RenderingThread.writeByte(NativeCallsList.ITEM_SET_DATABLOCK.getCallPos());
        setPtr(item, !item.isNativePointerSet());
        writeString(datablockName);
    }

    public static void item_setVisibilityFlag(ENG_Item item, int flag) {
        ENG_RenderingThread.writeByte(NativeCallsList.ITEM_SET_VISIBILITY_FLAGS.getCallPos());
        setPtr(item, !item.isNativePointerSet());
        ENG_RenderingThread.writeInt(flag);
    }

    public static void compositorWorkspace_setEnabled(long compositorWorkspace, boolean enable) {
        ENG_RenderingThread.writeByte(NativeCallsList.COMPOSITOR_WORKSPACE_SET_ENABLED.getCallPos());
        ENG_RenderingThread.writeLong(compositorWorkspace);
        ENG_RenderingThread.writeBoolean(enable);
    }

    public static void camera_invalidateView(long cameraPtr) {
        ENG_RenderingThread.writeByte(NativeCallsList.CAMERA_INVALIDATE_VIEW.getCallPos());
        ENG_RenderingThread.writeLong(cameraPtr);
    }

    public static void camera_getProjectionMatrix(final ENG_Camera cameraPtr, final ENG_Matrix4 ret, final ENG_Vector4D temp, final ENG_Boolean matrixSet, final int writeableBuffer) {
        ENG_RenderingThread.writeByte(NativeCallsList.CAMERA_GET_PROJECTION_MATRIX.getCallPos());
        ENG_RenderingThread.writeLong(cameraPtr.getPointer());
        ENG_RenderingThread.addFrameEndListener(ENG_NativeCallsObjectPool.getProjectionMatrixListener(cameraPtr, ret, matrixSet, writeableBuffer));
    }

    public static void camera_getViewMatrix(final ENG_Camera cameraPtr, final ENG_Matrix4 ret, final ENG_Vector4D temp, final ENG_Boolean matrixSet, final int writeableBuffer) {
        ENG_RenderingThread.writeByte(NativeCallsList.CAMERA_GET_VIEW_MATRIX.getCallPos());
        ENG_RenderingThread.writeLong(cameraPtr.getPointer());
        ENG_RenderingThread.addFrameEndListener(ENG_NativeCallsObjectPool.getViewMatrixListener(cameraPtr, ret, matrixSet, writeableBuffer));
    }

    public static void camera_isVisibleVec(final ENG_Camera cameraPtr, ENG_Vector4D vec, final ENG_Boolean visibility, final ENG_Boolean retSet) {
        ENG_RenderingThread.writeByte(NativeCallsList.CAMERA_IS_VISIBLE_VEC.getCallPos());
        ENG_RenderingThread.writeLong(cameraPtr.getPointer());
        writeVector4D(vec);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Boolean.SIZE_IN_BYTES, false) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                byte visible = responseBuffer.get();
                visibility.setValue(visible == 1);
                retSet.setValue(true);
            }
        });
    }

    public static void camera_isVisibleAxisAlignedBox(final ENG_Camera cameraPtr, ENG_AxisAlignedBox box,
                                                      final ENG_Boolean visibility, final ENG_Boolean retSet) {
        ENG_RenderingThread.writeByte(NativeCallsList.CAMERA_IS_VISIBLE_AXIS_ALIGNED_BOX.getCallPos());
        ENG_RenderingThread.writeLong(cameraPtr.getPointer());
        writeAxisAlignedBox(box);
//        System.out.println(box);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(ENG_Boolean.SIZE_IN_BYTES, false) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                byte visible = responseBuffer.get();
                visibility.setValue(visible == 1);
                retSet.setValue(true);
            }
        });
    }

    public static void getItemsAabbs() {
        final ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
        final int itemListSize = sceneManager.getMovableObjectListSize();
        if (itemListSize == 0) {
            return;
        }
        ENG_RenderingThread.writeByte(NativeCallsList.GET_ITEMS_AABBS.getCallPos());
        int aabbsListSize = ENG_Item.AABB_SIZE_IN_BYTES * itemListSize;
        ENG_Utility.reallocBufferDirect(getAabbsBuffer, aabbsListSize);
//        System.out.println("current buffer: " + getAabbsBuffer.getCurrentBuf());
        ByteBuffer nextBuffer = getAabbsBuffer.getNextBuffer();
        nextBuffer.position(0);
        nextBuffer.limit(aabbsListSize);
        ENG_RenderedFrameListenerWithBufferCopyWithLock listener = new ENG_RenderedFrameListenerWithBufferCopyWithLock(
                nextBuffer, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                for (int i = 0; i < itemListSize; ++i) {
//                    System.out.println("responseBuffer position: " + responseBuffer.position() + " limit: " + responseBuffer.limit());
                    float xMin = responseBuffer.getFloat();
                    float yMin = responseBuffer.getFloat();
                    float zMin = responseBuffer.getFloat();
                    float xMax = responseBuffer.getFloat();
                    float yMax = responseBuffer.getFloat();
                    float zMax = responseBuffer.getFloat();
                    sceneManager.updateItemAabb(getCurrentFrame(), i, xMin, yMin, zMin, xMax, yMax, zMax);
                }
            }
        };
        int currentBuf = getAabbsBuffer.getPreviousBuf(); // The buffer has been advanced that is why we need the previous one.
        listener.setCurrentFrame(currentBuf);
        sceneManager.setMovableObjectRingListCurrentFrame(currentBuf);
        ENG_RenderingThread.addFrameEndListener(listener);
    }

    public static void movableObject_detachFromParent(ENG_NativePointerWithSetter ptr) {
        ENG_RenderingThread.writeByte(NativeCallsList.MOVABLE_OBJECT_DETACH_FROM_PARENT.getCallPos());
        setPtr(ptr, !ptr.isNativePointerSet());
    }

    public static void overlayManager_createOverlay(/*String name, */final ENG_Overlay overlay) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_MANAGER_CREATE_OVERLAY.getCallPos());
        ENG_RenderingThread.writeLong(ENG_OverlayManager.getSingleton().getPointer());
//        writeString(name);
        writeString(overlay.getName());
        extractOverlayData(overlay);
        ENG_RenderingThread.flushPipeline(true);
    }

    private static void extractOverlayData(final ENG_Overlay overlay) {
        ENG_RenderedFrameListener listener = new ENG_RenderedFrameListener() { /*new ENG_RenderedFrameListenerWithBufferCopyWithLock(
                1024, true) {*/
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                int overlayNum = responseBuffer.getInt();
                for (int i = 0; i < overlayNum; ++i) {
//                    String overlayName = readString(responseBuffer);
//                    overlay.setName(overlayName);
                    ENG_Utility.alignMemory(responseBuffer, 4);
                    long overlayPtr = responseBuffer.getLong();
                    overlay.setPointer(overlayPtr);
                    overlay.setNativePointer(true);
                    int overlayContainerNum = responseBuffer.getInt();
                    for (int j = 0; j < overlayContainerNum; ++j) {
//                        for (int k = 0; k < 2; ++k) {
//                            System.out.println(String.format("0x%016X", responseBuffer.getLong()));
//                        }
//                        for (int k = 0; k < 2; ++k) {
//                            System.out.println(String.format("0x%02X", responseBuffer.get()));
//                        }
                        ENG_Utility.alignMemory(responseBuffer, 4);
                        long overlayContainerPtr = responseBuffer.getLong();
                        long datablockPtr = responseBuffer.getLong();
                        byte type = responseBuffer.get(); // Ignored for now. Used because of recursion on native side.
                        String elemName = readString(responseBuffer);
                        String materialName = readString(responseBuffer);
                        ENG_PanelOverlayElement elem = new ENG_PanelOverlayElement(elemName);
                        elem.setPointer(overlayContainerPtr);
                        elem.setNativePointer(true);
                        elem.setMaterialName(materialName, false);
                        // Not all containers have materials assigned to them but all overlay elements do.
                        if (datablockPtr != 0) {
                            elem.getDatablock().setPointer(datablockPtr);
                            elem.getDatablock().setNativePointer(true);
                        }
                        overlay.add2DNative(elem);
                        setMetricsAndPosition(responseBuffer, elem);
                        addOverlayElement(responseBuffer, elem);
                    }
                }
            }

            private void setMetricsAndPosition(ByteBuffer responseBuffer, ENG_OverlayElement elem) {
                byte metricsMode = responseBuffer.get();
                float left = responseBuffer.getFloat();
                float top = responseBuffer.getFloat();
                float width = responseBuffer.getFloat();
                float height = responseBuffer.getFloat();
                float clippingRectLeft = responseBuffer.getFloat();
                float clippingRectTop = responseBuffer.getFloat();
                float clippingRectRight = responseBuffer.getFloat();
                float clippingRectBottom = responseBuffer.getFloat();
                elem.setMetricsMode(ENG_OverlayElement.GuiMetricsMode.get(metricsMode));
                elem.setLeft(left);
                elem.setTop(top);
                elem.setWidth(width);
                elem.setHeight(height);
                elem._setClippingRegion(new ENG_RealRect(clippingRectLeft, clippingRectTop, clippingRectRight, clippingRectBottom));
            }

            private void addOverlayElement(ByteBuffer responseBuffer, ENG_OverlayContainer container) {
                int overlayElemNum = responseBuffer.getInt();
                for (int i = 0; i < overlayElemNum; ++i) {
                    ENG_Utility.alignMemory(responseBuffer, 4);
                    long elementPtr = responseBuffer.getLong();
                    long datablockPtr = responseBuffer.getLong();
//                    container.setPointer(elementPtr);
//                    container.setNativePointer(true);
                    byte type = responseBuffer.get();
                    String elemName = readString(responseBuffer);
                    String materialName = readString(responseBuffer);
                    ENG_OverlayElement elem = null;
                    switch (type) {
                        case 0: // PanelOverlayContainer
                            elem = new ENG_PanelOverlayElement(elemName);
                            break;
                        case 1: // TextAreaOverlayElement
                            elem = new ENG_TextAreaOverlayElement(elemName);
                            break;
                        default:
                            throw new IllegalArgumentException(type + " is an invalid overlay type");
                    }
                    container.addChildNative(elem);
                    elem.setPointer(elementPtr);
                    elem.setNativePointer(true);
                    elem.getDatablock().setPointer(datablockPtr);
                    elem.getDatablock().setNativePointer(true);
                    elem.setMaterialName(materialName, false);
                    setMetricsAndPosition(responseBuffer, elem);
                    if (elem.isContainer()) {
                        //noinspection DataFlowIssue
                        addOverlayElement(responseBuffer, (ENG_OverlayContainer) elem);
                    } else {
                        // We are still writing an overlayElemNum even if it's zero and the element
                        // is not a container. The native side doesn't make a distinction between
                        // a container and an element.
                        int pointless = responseBuffer.getInt();
                    }
                }
            }
        };
        ENG_RenderingThread.addFrameEndListener(listener);
    }

    public static void overlayManager_destroyOverlay(String name) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_MANAGER_DESTROY_OVERLAY_BY_NAME.getCallPos());
        ENG_RenderingThread.writeLong(ENG_OverlayManager.getSingleton().getPointer());
        writeString(name);

    }

    public static void overlayManager_destroyOverlay(ENG_Overlay overlay) {
        if (!overlay.isNativePointerSet()) {
            throw new IllegalArgumentException("Overlay: " + overlay.getName() +
                    " cannot be destroyed in the same frame that was instantiated");
        }
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_MANAGER_DESTROY_OVERLAY_BY_PTR.getCallPos());
        ENG_RenderingThread.writeLong(ENG_OverlayManager.getSingleton().getPointer());
        ENG_RenderingThread.writeLong(overlay.getPointer());

    }

    public static void overlayManager_destroyAll() {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_MANAGER_DESTROY_ALL.getCallPos());
        ENG_RenderingThread.writeLong(ENG_OverlayManager.getSingleton().getPointer());

    }

    public static void overlayManager_getByName(ENG_Overlay overlay) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_MANAGER_GET_BY_NAME.getCallPos());
        ENG_RenderingThread.writeLong(ENG_OverlayManager.getSingleton().getPointer());
        writeString(overlay.getName());
        extractOverlayData(overlay);
        ENG_RenderingThread.flushPipeline(true);
    }

    public static void overlayManager_updateData() {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_MANAGER_UPDATE_DATA.getCallPos());
        ENG_RenderingThread.writeLong(ENG_OverlayManager.getSingleton().getPointer());
        ENG_RenderedFrameListenerWithBufferCopyWithLock listener = new ENG_RenderedFrameListenerWithBufferCopyWithLock(
                1024, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {

            }
        };
        ENG_RenderingThread.addFrameEndListener(listener);
    }

    public static void overlay_show(ENG_Overlay overlay) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_SHOW.getCallPos());
        ENG_RenderingThread.writeLong(overlay.getPointer());
//        System.out.println("overlay_show: " + overlay.getName());
    }

    public static void overlay_hide(ENG_Overlay overlay) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_HIDE.getCallPos());
        ENG_RenderingThread.writeLong(overlay.getPointer());
//        System.out.println("overlay_hide: " + overlay.getName());
    }

    public static void overlayElement_show(ENG_OverlayElement overlayElement) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_SHOW.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
//        System.out.println("overlay_element_show: " + overlayElement.getName());
    }

    public static void overlayElement_hide(ENG_OverlayElement overlayElement) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_HIDE.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
//        System.out.println("overlay_element_hide: " + overlayElement.getName());
    }

    public static void overlayElement_setLeft(ENG_OverlayElement overlayElement, float left) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_SET_LEFT.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
        ENG_RenderingThread.writeFloat(left);
    }

    public static void overlayElement_setTop(ENG_OverlayElement overlayElement, float top) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_SET_TOP.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
        ENG_RenderingThread.writeFloat(top);
    }

    public static void overlayElement_setWidth(ENG_OverlayElement overlayElement, float width) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_SET_WIDTH.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
        ENG_RenderingThread.writeFloat(width);
    }

    public static void overlayElement_setHeight(ENG_OverlayElement overlayElement, float height) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_SET_HEIGHT.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
        ENG_RenderingThread.writeFloat(height);
    }

    public static void overlayElement_setCaption(ENG_OverlayElement overlayElement, String caption) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_SET_CAPTION.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
        writeString(caption);
    }

    public static void overlayElement_setMetricsMode(ENG_OverlayElement overlayElement, ENG_OverlayElement.GuiMetricsMode metricsMode) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_SET_METRICS_MODE.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
        ENG_RenderingThread.writeByte(metricsMode.getPos());
    }

    public static void overlayElement_setMaterialName(ENG_OverlayElement overlayElement, String materialName) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_SET_MATERIAL_NAME.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
        writeString(materialName);
    }

    public static void overlayElement_setAlignment(ENG_TextAreaOverlayElement overlayElement, ENG_TextAreaOverlayElement.Alignment alignment) {
        ENG_RenderingThread.writeByte(NativeCallsList.OVERLAY_ELEMENT_SET_ALIGNMENT.getCallPos());
        ENG_RenderingThread.writeLong(overlayElement.getPointer());
        ENG_RenderingThread.writeByte(alignment.getPos());
    }

    public static void gpuProgramParams_getProgramParams(final ENG_GpuProgramParameters params, //ENG_NativePointerWithSetter ptr,
                                                         ENG_NativePointerWithSetter renderable, short technique, short pass,
                                                         GpuProgramParametersType gpuProgramParametersType) {
        ENG_RenderingThread.writeByte(NativeCallsList.GPU_PROGRAM_PARAMS_GET_PROGRAM_PARAMS.getCallPos());
        setPtr(params, !params.isNativePointerSet());
        ENG_RenderingThread.writeLong(renderable.getPointer());
        ENG_RenderingThread.writeShort(technique);
        ENG_RenderingThread.writeShort(pass);
        ENG_RenderingThread.writeByte(gpuProgramParametersType.getPos());
        switch (gpuProgramParametersType) {
            case GPU_SHADOW_CASTER_VERTEX:
            case GPU_SHADOW_CASTER_FRAGMENT:
                throw new IllegalArgumentException(gpuProgramParametersType + " is not supported");
        }
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener() {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                ENG_Utility.alignMemory(responseBuffer, 4);
                long paramsPtr = responseBuffer.getLong();
                params.setPointer(paramsPtr);
                params.setNativePointer(true);
            }
        });
        ENG_RenderingThread.flushPipeline(true);
    }

    public static void gpuProgramParams_setNamedConstant(final ENG_GpuProgramParameters params, String name, ENG_ColorValue val) {
        ENG_RenderingThread.writeByte(NativeCallsList.GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_COLOUR.getCallPos());
        setPtr(params, !params.isNativePointerSet());
        writeString(name);
        writeColour(val);
    }

    public static void gpuProgramParams_setNamedConstant(final ENG_GpuProgramParameters params, String name, int val) {
        ENG_RenderingThread.writeByte(NativeCallsList.GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_INT.getCallPos());
        setPtr(params, !params.isNativePointerSet());
        writeString(name);
        ENG_RenderingThread.writeInt(val);
    }

    public static void gpuProgramParams_setNamedConstant(final ENG_GpuProgramParameters params, String name, float val) {
        ENG_RenderingThread.writeByte(NativeCallsList.GPU_PROGRAM_PARAMS_SET_NAMED_CONSTANT_FLOAT.getCallPos());
        setPtr(params, !params.isNativePointerSet());
        writeString(name);
        ENG_RenderingThread.writeFloat(val);
    }

    public static void textureManager_getByName(final ENG_TextureNative texture, ENG_NativePointerWithSetter renderable,
                                                short technique, short pass, int textureUnitState) {
        if (!renderable.isNativePointerSet()) {
            throw new IllegalArgumentException("renderable ptr should be set");
        }
        ENG_RenderingThread.writeByte(NativeCallsList.TEXTURE_MANAGER_GET_BY_NAME.getCallPos());
        ENG_RenderingThread.writeLong(renderable.getPointer());
//        ENG_RenderingThread.writeShort(technique);
//        ENG_RenderingThread.writeShort(pass);
//        ENG_RenderingThread.writeInt(textureUnitState);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener() {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                ENG_Utility.alignMemory(responseBuffer, 4);
                long paramsPtr = responseBuffer.getLong();
                int width = responseBuffer.getInt();
                int height = responseBuffer.getInt();
                texture.setPointer(paramsPtr);
                texture.setNativePointer(true);
                texture.setWidth(width);
                texture.setHeight(height);
            }
        });
        ENG_RenderingThread.flushPipeline(true);
    }

    public static void textureManager_getByNameOverlayElement(final ENG_TextureNative texture, ENG_NativePointerWithSetter renderable,
                                                short technique, short pass, int textureUnitState) {
        if (!renderable.isNativePointerSet()) {
            throw new IllegalArgumentException("renderable ptr should be set");
        }
        ENG_RenderingThread.writeByte(NativeCallsList.TEXTURE_MANAGER_GET_BY_NAME_OVERLAY_ELEMENT.getCallPos());
        ENG_RenderingThread.writeLong(renderable.getPointer());
//        ENG_RenderingThread.writeShort(technique);
//        ENG_RenderingThread.writeShort(pass);
//        ENG_RenderingThread.writeInt(textureUnitState);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener() {
            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                ENG_Utility.alignMemory(responseBuffer, 4);
                long paramsPtr = responseBuffer.getLong();
                int width = responseBuffer.getInt();
                int height = responseBuffer.getInt();
                texture.setPointer(paramsPtr);
                texture.setNativePointer(true);
                texture.setWidth(width);
                texture.setHeight(height);
            }
        });
        ENG_RenderingThread.flushPipeline(true);
    }


    private static final int OGRE_FRAME_STATS_SAMPLES = 60;
    private static final long[] mFrameTimes = new long[OGRE_FRAME_STATS_SAMPLES];

    // Only for all data
//    private static final int FRAME_STATS_SIZE_IN_BYTES = ENG_Integer.SIZE_IN_BYTES + 3 * ENG_Long.SIZE_IN_BYTES +
//            OGRE_FRAME_STATS_SAMPLES * ENG_Long.SIZE_IN_BYTES + ENG_Integer.SIZE_IN_BYTES;
    private static final int FRAME_STATS_SIZE_IN_BYTES = ENG_Float.SIZE_IN_BYTES * 6;

    public static void frameStats_update(long renderRootPtr, final ENG_RenderTarget.FrameStats frameStats) {
        ENG_RenderingThread.writeByte(NativeCallsList.FRAME_STATS_UPDATE.getCallPos());
        ENG_RenderingThread.writeLong(renderRootPtr);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(FRAME_STATS_SIZE_IN_BYTES, false) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                // Don't need so much detail for now.
//                int mNextFrame = responseBuffer.getInt();
//                ENG_Utility.alignMemory(buffer, 4);
//                long mBestFrameTime = responseBuffer.getLong();
//                long mWorstFrameTime = responseBuffer.getLong();
//                long mLastTime = responseBuffer.getLong();
//                for (int i = 0; i < OGRE_FRAME_STATS_SAMPLES; ++i) {
//                    mFrameTimes[i] = responseBuffer.getLong();
//                }
//                int mFramesSampled = responseBuffer.getInt();
                float fps = responseBuffer.getFloat();
                float avgFps = responseBuffer.getFloat();
                float bestTime = responseBuffer.getFloat();
                float worstTime = responseBuffer.getFloat();
                float lastTime = responseBuffer.getFloat();
                float avgTime = responseBuffer.getFloat();
                frameStats.lastFPS = fps;
                frameStats.avgFPS = avgFps;
                frameStats.bestFrameTimeFloat = bestTime;
                frameStats.worstFrameTimeFloat = worstTime;
                frameStats.lastTime = lastTime;
                frameStats.avgTime = avgTime;
            }
        });
    }

    public static void unlitDatablock_setUseColour(ENG_HlmsDatablock datablock, boolean useColour) {
        ENG_RenderingThread.writeByte(NativeCallsList.UNLIT_DATABLOCK_SET_USE_COLOUR.getCallPos());
        if (!datablock.isNativePointerSet()) {
            throw new IllegalArgumentException("datablock must have its pointer set");
        }
        ENG_RenderingThread.writeLong(datablock.getPointer());
        ENG_RenderingThread.writeBoolean(useColour);
    }

    public static void unlitDatablock_setColour(ENG_HlmsDatablock datablock, ENG_ColorValue colorValue) {
        ENG_RenderingThread.writeByte(NativeCallsList.UNLIT_DATABLOCK_SET_COLOUR.getCallPos());
        if (!datablock.isNativePointerSet()) {
            throw new IllegalArgumentException("datablock must have its pointer set");
        }
        ENG_RenderingThread.writeLong(datablock.getPointer());
        writeColour(colorValue);
    }

    public static void dynamicOverlayELement_Ctor(final ENG_DynamicOverlayElement dynamicOverlayElement, ENG_OverlayElement elem,
                                                  String textureName, String groupName) {
        ENG_RenderingThread.writeByte(NativeCallsList.DYNAMIC_OVERLAY_ELEM_CTOR.getCallPos());
//        setPtr(dynamicOverlayElement, !dynamicOverlayElement.isNativePointerSet());
        ENG_RenderingThread.writeLong(elem.getPointer());
        writeString(textureName);
        writeString(groupName);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListener()/*WithBufferCopyWithLock(ENG_Long.SIZE_IN_BYTES, true)*/ {

            @Override
            public void frameEnded(ByteBuffer responseBuffer) {
                ENG_Utility.alignMemory(responseBuffer, 4);
                long dynamicOverlayElementPtr = responseBuffer.getLong();
                dynamicOverlayElement.setPointer(dynamicOverlayElementPtr);
                dynamicOverlayElement.setNativePointer(true);
            }
        });
        ENG_RenderingThread.flushPipeline(true);
    }

    public static void dynamicOverlayELement_Dtor(final ENG_DynamicOverlayElement dynamicOverlayElement) {
        ENG_RenderingThread.writeByte(NativeCallsList.DYNAMIC_OVERLAY_ELEM_DTOR.getCallPos());
        ENG_RenderingThread.writeLong(dynamicOverlayElement.getPointer());
    }

    public static void dynamicOverlayElement_resetTexture(final ENG_DynamicOverlayElement dynamicOverlayElement) {
        ENG_RenderingThread.writeByte(NativeCallsList.DYNAMIC_OVERLAY_ELEM_RESET_TO_INITIAL_TEXTURE.getCallPos());
        ENG_RenderingThread.writeLong(dynamicOverlayElement.getPointer());
    }

    public static void dynamicOverlayElement_updateFinalTexture(final ENG_DynamicOverlayElement dynamicOverlayElement) {
        ENG_RenderingThread.writeByte(NativeCallsList.DYNAMIC_OVERLAY_ELEM_UPDATE_FINAL_TEXTURE.getCallPos());
        ENG_RenderingThread.writeLong(dynamicOverlayElement.getPointer());
    }

    public static void dynamicOverlayElement_setPointScreenSpace(final ENG_DynamicOverlayElement dynamicOverlayElement,
            float x, float y, int pixelLen,
            ENG_ColorValue val, boolean overwriteTransparentPixels) {
        ENG_RenderingThread.writeByte(NativeCallsList.DYNAMIC_OVERLAY_ELEM_SET_POINT_IN_SCREEN_SPACE.getCallPos());
        ENG_RenderingThread.writeLong(dynamicOverlayElement.getPointer());
        ENG_RenderingThread.writeFloat(x);
        ENG_RenderingThread.writeFloat(y);
        ENG_RenderingThread.writeInt(pixelLen);
        writeColour(val);
        ENG_RenderingThread.writeBoolean(overwriteTransparentPixels);
    }

    public static void dynamicOverlayElement_lock(final ENG_DynamicOverlayElement dynamicOverlayElement) {
        ENG_RenderingThread.writeByte(NativeCallsList.DYNAMIC_OVERLAY_ELEM_LOCK.getCallPos());
        ENG_RenderingThread.writeLong(dynamicOverlayElement.getPointer());
    }

    public static void dynamicOverlayElement_unlock(final ENG_DynamicOverlayElement dynamicOverlayElement) {
        ENG_RenderingThread.writeByte(NativeCallsList.DYNAMIC_OVERLAY_ELEM_UNLOCK.getCallPos());
        ENG_RenderingThread.writeLong(dynamicOverlayElement.getPointer());
    }

    public static void dynamicOverlayElement_setAreaVec(final ENG_DynamicOverlayElement dynamicOverlayElement,
                                                        ENG_Box elem, ArrayList<ENG_ColorValue> list, boolean overwriteTransparentPixels) {
        ENG_RenderingThread.writeByte(NativeCallsList.DYNAMIC_OVERLAY_ELEM_SET_AREA_VEC.getCallPos());
        ENG_RenderingThread.writeLong(dynamicOverlayElement.getPointer());
        ENG_RenderingThread.writeInt(elem.left);
        ENG_RenderingThread.writeInt(elem.top);
        ENG_RenderingThread.writeInt(elem.right);
        ENG_RenderingThread.writeInt(elem.bottom);
        ENG_RenderingThread.writeInt(elem.front);
        ENG_RenderingThread.writeInt(elem.back);
        int size = list.size();
        ENG_RenderingThread.writeInt(size);
        for (int i = 0; i < size; ++i) {
            writeColour(list.get(i));
        }
        ENG_RenderingThread.writeBoolean(overwriteTransparentPixels);
    }

    public static void dynamicOverlayElement_setArea(final ENG_DynamicOverlayElement dynamicOverlayElement,
                                                     ENG_Box elem, ENG_ColorValue val, boolean overwriteTransparentPixels) {
        ENG_RenderingThread.writeByte(NativeCallsList.DYNAMIC_OVERLAY_ELEM_SET_AREA.getCallPos());
        ENG_RenderingThread.writeLong(dynamicOverlayElement.getPointer());
        ENG_RenderingThread.writeInt(elem.left);
        ENG_RenderingThread.writeInt(elem.top);
        ENG_RenderingThread.writeInt(elem.right);
        ENG_RenderingThread.writeInt(elem.bottom);
        ENG_RenderingThread.writeInt(elem.front);
        ENG_RenderingThread.writeInt(elem.back);
        writeColour(val);
        ENG_RenderingThread.writeBoolean(overwriteTransparentPixels);
    }

    public static void sceneManager_createTiledAnimation(final ENG_TiledAnimationNative tiledAnimationNative,
                                                         final ENG_BillboardSetNative billboardSetNative,
                                                         String name, String unlitMaterialName,
                                               final float speed, final int horizontalFramesNum, final int verticalFramesNum) {
        ENG_RenderingThread.writeByte(NativeCallsList.TILED_ANIMATION_CREATE.getCallPos());
//        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
//        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        tiledAnimationNative.setPointer(ENG_Utility.getUniqueId());
        setPtr(tiledAnimationNative, !tiledAnimationNative.isNativePointerSet());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        writeString(name);
        writeString(unlitMaterialName);
        ENG_RenderingThread.writeFloat(speed);
        ENG_RenderingThread.writeInt(horizontalFramesNum);
        ENG_RenderingThread.writeInt(verticalFramesNum);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(
                ENG_Long.SIZE_IN_BYTES, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
//                ENG_Utility.alignMemory(responseBuffer, 4);
                long tiledAnimationPtr = responseBuffer.getLong();
                tiledAnimationNative.setPointer(tiledAnimationPtr);
                tiledAnimationNative.setNativePointer(true);
            }
        });
    }

    public static void sceneManager_destroyTiledAnimation(ENG_TiledAnimationNative tiledAnimationNative) {
        ENG_RenderingThread.writeByte(NativeCallsList.TILED_ANIMATION_DESTROY.getCallPos());
//        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager();
//        ENG_RenderingThread.writeLong(sceneManager.getPointer());
        setPtr(tiledAnimationNative, !tiledAnimationNative.isNativePointerSet());
//        if (tiledAnimationNative.isNativePointerSet()) {
//            sceneManager.removeNativePtrToNativeObject(tiledAnimationNative.getPointer());
//        }
    }

    public static void tiledAnimation_updateCurrentFrameNum(final ENG_TiledAnimationNative tiledAnimationNative) {
        ENG_RenderingThread.writeByte(NativeCallsList.TILED_ANIMATION_UPDATE_CURRENT_FRAME_NUM.getCallPos());
        setPtr(tiledAnimationNative, !tiledAnimationNative.isNativePointerSet());
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(
                ENG_Integer.SIZE_IN_BYTES, false) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                int currentFrameNum = responseBuffer.getInt();
                tiledAnimationNative.setCurrentFrameNum(currentFrameNum);
            }
        });
    }

    public static void billboardSet_createBillboard(ENG_BillboardSetNative billboardSetNative, final ENG_BillboardNative billboardNative,
                                                    ENG_Vector3D pos, ENG_ColorValue color) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_CREATE_BILLBOARD.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        billboardNative.setPointer(ENG_Utility.getUniqueId());
        setPtr(billboardNative, !billboardNative.isNativePointerSet());
        writeVector3D(pos);
        writeColour(color);
        setFrameEndListenerForObjectCreation(billboardNative);
//        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(
//                ENG_Long.SIZE_IN_BYTES, true) {
//            @Override
//            public void runOnMainThread(ByteBuffer responseBuffer) {
////                ENG_Utility.alignMemory(responseBuffer, 4);
//                long billboardPtr = responseBuffer.getLong();
//                billboardNative.setPointer(billboardPtr);
//                billboardNative.setNativePointer(true);
//            }
//        });
    }

    public static void billboardSet_destroyBillboard(ENG_BillboardSetNative billboardSetNative, ENG_BillboardNative billboardNative) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_DESTROY_BILLBOARD.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        setPtr(billboardNative, !billboardNative.isNativePointerSet());
    }

//    public static void billboardSet_destroyBillboard(ENG_BillboardSetNative billboardSetNative, int pos) {
//        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_DESTROY_BILLBOARD_BY_ID.getCallPos());
//        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
//        ENG_RenderingThread.writeInt(pos);
//    }

    public static void billboardSet_setCommonUpVector(ENG_BillboardSetNative billboardSetNative, ENG_Vector4D upVec) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_SET_COMMON_UP_VECTOR.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        writeVector4D(upVec);
    }

    public static void billboardSet_setCommonDirection(ENG_BillboardSetNative billboardSetNative, ENG_Vector4D commonDir) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_SET_COMMON_DIRECTION.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        writeVector4D(commonDir);
    }

    public static void billboardSet_setDefaultDimensions(ENG_BillboardSetNative billboardSetNative, float xDim, float yDim) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_SET_DEFAULT_DIMENSIONS.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        ENG_RenderingThread.writeFloat(xDim);
        ENG_RenderingThread.writeFloat(yDim);
    }

    public static void billboardSet_setMaterialName(ENG_BillboardSetNative billboardSetNative, String materialName, String groupName) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_SET_MATERIAL_NAME.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        writeString(materialName);
        writeString(groupName);
    }

    public static void billboardSet_setDatablockName(ENG_BillboardSetNative billboardSetNative, String materialName) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_SET_DATABLOCK_NAME.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        writeString(materialName);
    }

    /** @noinspection deprecation*/
    public static void billboardSet_setBillboardOrigin(ENG_BillboardSetNative billboardSetNative, ENG_BillboardSet.BillboardOrigin origin) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_SET_BILLBOARD_ORIGIN.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        ENG_RenderingThread.writeByte((byte) origin.ordinal());
    }

    /** @noinspection deprecation*/
    public static void billboardSet_setBillboardRotationType(ENG_BillboardSetNative billboardSetNative, ENG_BillboardSet.BillboardRotationType rotationType) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_SET_BILLBOARD_ROTATION_TYPE.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        ENG_RenderingThread.writeByte((byte) rotationType.ordinal());
    }

    /** @noinspection deprecation*/
    public static void billboardSet_setBillboardType(ENG_BillboardSetNative billboardSetNative, ENG_BillboardSet.BillboardType billboardType) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARDSET_SET_BILLBOARD_TYPE.getCallPos());
        setPtr(billboardSetNative, !billboardSetNative.isNativePointerSet());
        ENG_RenderingThread.writeByte((byte) billboardType.ordinal());
    }

    public static void billboard_setRotation(ENG_BillboardNative billboardNative, float rotation) {
        ENG_RenderingThread.writeByte(NativeCallsList.BILLBOARD_SET_ROTATION.getCallPos());
        setPtr(billboardNative, !billboardNative.isNativePointerSet());
        ENG_RenderingThread.writeFloat(rotation);
    }

    public static void movableObject_setRenderQueueGroup(ENG_NativePointerWithSetter ptr, byte renderQueueGroup) {
        ENG_RenderingThread.writeByte(NativeCallsList.MOVABLE_OBJECT_SET_RENDER_QUEUE_GROUP.getCallPos());
        setPtr(ptr, !ptr.isNativePointerSet());
        ENG_RenderingThread.writeByte(renderQueueGroup);
    }

    public static void particleSystem_setMaterialName(ENG_ParticleSystemNative particleSystemNative, String materialName) {
        ENG_RenderingThread.writeByte(NativeCallsList.PARTICLE_SYSTEM_SET_MATERIAL_NAME.getCallPos());
        setPtr(particleSystemNative, !particleSystemNative.isNativePointerSet());
        writeString(materialName);
    }

    public static void sceneCompositor_insertNode(long sceneCompositorPtr, long workspace, String workspaceName,
                                                  String baseNodeName, String nodeToInsertName,
                                                  String previousNodeName, ENG_ColorValue startColor, float scaleStep,
                                                  final ENG_Long sceneCompositorId) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENE_COMPOSITOR_INSERT_NODE.getCallPos());
        ENG_RenderingThread.writeLong(sceneCompositorPtr);
        ENG_RenderingThread.writeLong(workspace);
        writeString(workspaceName);
        writeString(baseNodeName);
        writeString(nodeToInsertName);
        writeString(previousNodeName);
        writeColour(startColor);
        ENG_RenderingThread.writeFloat(scaleStep);
        ENG_RenderingThread.addFrameEndListener(new ENG_RenderedFrameListenerWithBufferCopyWithLock(
                ENG_Long.SIZE_IN_BYTES, true) {
            @Override
            public void runOnMainThread(ByteBuffer responseBuffer) {
                long compositorId = responseBuffer.getLong();
                sceneCompositorId.setValue(compositorId);
            }
        });
    }

    public static void sceneCompositor_revertNode(long sceneCompositorPtr, long sceneCompositorId) {
        ENG_RenderingThread.writeByte(NativeCallsList.SCENE_COMPOSITOR_REVERT_NODE.getCallPos());
        ENG_RenderingThread.writeLong(sceneCompositorPtr);
        ENG_RenderingThread.writeLong(sceneCompositorId);
    }

}
