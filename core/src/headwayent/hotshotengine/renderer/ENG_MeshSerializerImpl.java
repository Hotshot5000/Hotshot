/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Log;
import headwayent.hotshotengine.ENG_Serializer;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Byte;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.exception.ENG_ParsingException;
import headwayent.hotshotengine.renderer.ENG_EdgeData.Edge;
import headwayent.hotshotengine.renderer.ENG_EdgeData.EdgeGroupList;
import headwayent.hotshotengine.renderer.ENG_EdgeData.Triangle;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.LockOptions;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer.IndexType;
import headwayent.hotshotengine.renderer.ENG_RenderOperation.OperationType;
import headwayent.hotshotengine.renderer.ENG_VertexAnimationTrack.VertexAnimationType;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

@Deprecated
public class ENG_MeshSerializerImpl extends ENG_Serializer {

    public enum MeshChunkID {
        M_HEADER(0x1000),
        // char*          version           : Version number check
        M_MESH(0x3000),
        // bool skeletallyAnimated   // important flag which affects h/w buffer policies
        // Optional M_GEOMETRY chunk
        M_SUBMESH(0x4000),
        // char* materialName
        // bool useSharedVertices
        // unsigned int indexCount
        // bool indexes32Bit
        // unsigned int* faceVertexIndices (indexCount)
        // OR
        // unsigned short* faceVertexIndices (indexCount)
        // M_GEOMETRY chunk (Optional: present only if useSharedVertices = false)
        M_SUBMESH_OPERATION(0x4010), // optional, trilist assumed if missing
        // unsigned short operationType
        M_SUBMESH_BONE_ASSIGNMENT(0x4100),
        // Optional bone weights (repeating section)
        // unsigned int vertexIndex;
        // unsigned short boneIndex;
        // float weight;
        // Optional chunk that matches a texture name to an alias
        // a texture alias is sent to the submesh material to use this texture name
        // instead of the one in the texture unit with a matching alias name
        M_SUBMESH_TEXTURE_ALIAS(0x4200), // Repeating section
        // char* aliasName;
        // char* textureName;

        M_GEOMETRY(0x5000), // NB this chunk is embedded within M_MESH and M_SUBMESH
        // unsigned int vertexCount
        M_GEOMETRY_VERTEX_DECLARATION(0x5100),
        M_GEOMETRY_VERTEX_ELEMENT(0x5110), // Repeating section
        // unsigned short source;  	// buffer bind source
        // unsigned short type;    	// VertexElementType
        // unsigned short semantic; // VertexElementSemantic
        // unsigned short offset;	// start offset in buffer in bytes
        // unsigned short index;	// index of the semantic (for colours and texture coords)
        M_GEOMETRY_VERTEX_BUFFER(0x5200), // Repeating section
        // unsigned short bindIndex;	// Index to bind this buffer to
        // unsigned short vertexSize;	// Per-vertex size, must agree with declaration at this index
        M_GEOMETRY_VERTEX_BUFFER_DATA(0x5210),
        // raw buffer data
        M_MESH_SKELETON_LINK(0x6000),
        // Optional link to skeleton
        // char* skeletonName           : name of .skeleton to use
        M_MESH_BONE_ASSIGNMENT(0x7000),
        // Optional bone weights (repeating section)
        // unsigned int vertexIndex;
        // unsigned short boneIndex;
        // float weight;
        M_MESH_LOD(0x8000),
        // Optional LOD information
        // string strategyName;
        // unsigned short numLevels;
        // bool manual;  (true for manual alternate meshes, false for generated)
        M_MESH_LOD_USAGE(0x8100),
        // Repeating section, ordered in increasing depth
        // NB LOD 0 (full detail from 0 depth) is omitted
        // LOD value - this is a distance, a pixel count etc, based on strategy
        // float lodValue;
        M_MESH_LOD_MANUAL(0x8110),
        // Required if M_MESH_LOD section manual = true
        // String manualMeshName;
        M_MESH_LOD_GENERATED(0x8120),
        // Required if M_MESH_LOD section manual = false
        // Repeating section (1 per submesh)
        // unsigned int indexCount;
        // bool indexes32Bit
        // unsigned short* faceIndexes;  (indexCount)
        // OR
        // unsigned int* faceIndexes;  (indexCount)
        M_MESH_BOUNDS(0x9000),
        // float minx, miny, minz
        // float maxx, maxy, maxz
        // float radius

        // Added By DrEvil
        // optional chunk that contains a table of submesh indexes and the names of
        // the sub-meshes.
        M_SUBMESH_NAME_TABLE(0xA000),
        // Subchunks of the name table. Each chunk contains an index & string
        M_SUBMESH_NAME_TABLE_ELEMENT(0xA100),
        // short index
        // char* name

        // Optional chunk which stores precomputed edge data
        M_EDGE_LISTS(0xB000),
        // Each LOD has a separate edge list
        M_EDGE_LIST_LOD(0xB100),
        // unsigned short lodIndex
        // bool isManual			// If manual, no edge data here, loaded from manual mesh
        // bool isClosed
        // unsigned long numTriangles
        // unsigned long numEdgeGroups
        // Triangle* triangleList
        // unsigned long indexSet
        // unsigned long vertexSet
        // unsigned long vertIndex[3]
        // unsigned long sharedVertIndex[3]
        // float normal[4]

        M_EDGE_GROUP(0xB110),
        // unsigned long vertexSet
        // unsigned long triStart
        // unsigned long triCount
        // unsigned long numEdges
        // Edge* edgeList
        // unsigned long  triIndex[2]
        // unsigned long  vertIndex[2]
        // unsigned long  sharedVertIndex[2]
        // bool degenerate

        // Optional poses section, referred to by pose keyframes
        M_POSES(0xC000),
        M_POSE(0xC100),
        // char* name (may be blank)
        // unsigned short target	// 0 for shared geometry,
        // 1+ for submesh index + 1
        M_POSE_VERTEX(0xC111),
        // unsigned long vertexIndex
        // float xoffset, yoffset, zoffset
        // Optional vertex animation chunk
        M_ANIMATIONS(0xD000),
        M_ANIMATION(0xD100),
        // char* name
        // float length
        M_ANIMATION_TRACK(0xD110),
        // unsigned short type			// 1 == morph, 2 == pose
        // unsigned short target		// 0 for shared geometry,
        // 1+ for submesh index + 1
        M_ANIMATION_MORPH_KEYFRAME(0xD111),
        // float time
        // float x,y,z			// repeat by number of vertices in original geometry
        M_ANIMATION_POSE_KEYFRAME(0xD112),
        // float time
        M_ANIMATION_POSE_REF(0xD113), // repeat for number of referenced poses
        // unsigned short poseIndex
        // float influence

        // Optional submesh extreme vertex list chink
        M_TABLE_EXTREMES(0xE000),
        // unsigned short submesh_index;
        // float extremes [n_extremes][3];

        /* Version 1.2 of the .mesh format (deprecated)
        enum MeshChunkID {
            M_HEADER                = 0x1000,
                // char*          version           : Version number check
            M_MESH                = 0x3000,
                // bool skeletallyAnimated   // important flag which affects h/w buffer policies
                // Optional M_GEOMETRY chunk
                M_SUBMESH             = 0x4000,
                    // char* materialName
                    // bool useSharedVertices
                    // unsigned int indexCount
                    // bool indexes32Bit
                    // unsigned int* faceVertexIndices (indexCount)
                    // OR
                    // unsigned short* faceVertexIndices (indexCount)
                    // M_GEOMETRY chunk (Optional: present only if useSharedVertices = false)
                    M_SUBMESH_OPERATION = 0x4010, // optional, trilist assumed if missing
                        // unsigned short operationType
                    M_SUBMESH_BONE_ASSIGNMENT = 0x4100,
                        // Optional bone weights (repeating section)
                        // unsigned int vertexIndex;
                        // unsigned short boneIndex;
                        // float weight;
                M_GEOMETRY          = 0x5000, // NB this chunk is embedded within M_MESH and M_SUBMESH
                */
        // unsigned int vertexCount
        // float* pVertices (x, y, z order x numVertices)
        M_GEOMETRY_NORMALS(0x5100),    //(Optional)
        // float* pNormals (x, y, z order x numVertices)
        M_GEOMETRY_COLOURS(0x5200),    //(Optional)
        // unsigned long* pColours (RGBA 8888 format x numVertices)
        M_GEOMETRY_TEXCOORDS(0x5300);    //(Optional, REPEATABLE, each one adds an extra set)
        // unsigned short dimensions    (1 for 1D, 2 for 2D, 3 for 3D)
        // float* pTexCoords  (u [v] [w] order, dimensions x numVertices)
                /*
                M_MESH_SKELETON_LINK = 0x6000,
                    // Optional link to skeleton
                    // char* skeletonName           : name of .skeleton to use
                M_MESH_BONE_ASSIGNMENT = 0x7000,
                    // Optional bone weights (repeating section)
                    // unsigned int vertexIndex;
                    // unsigned short boneIndex;
                    // float weight;
                M_MESH_LOD = 0x8000,
                    // Optional LOD information
                    // unsigned short numLevels;
                    // bool manual;  (true for manual alternate meshes, false for generated)
                    M_MESH_LOD_USAGE = 0x8100,
                    // Repeating section, ordered in increasing depth
    				// NB LOD 0 (full detail from 0 depth) is omitted
                    // float fromSquaredDepth;
                        M_MESH_LOD_MANUAL = 0x8110,
                        // Required if M_MESH_LOD section manual = true
                        // String manualMeshName;
                        M_MESH_LOD_GENERATED = 0x8120,
                        // Required if M_MESH_LOD section manual = false
    					// Repeating section (1 per submesh)
                        // unsigned int indexCount;
                        // bool indexes32Bit
                        // unsigned short* faceIndexes;  (indexCount)
                        // OR
                        // unsigned int* faceIndexes;  (indexCount)
                M_MESH_BOUNDS = 0x9000
                    // float minx, miny, minz
                    // float maxx, maxy, maxz
                    // float radius

    			// Added By DrEvil
    			// optional chunk that contains a table of submesh indexes and the names of
    			// the sub-meshes.
    			M_SUBMESH_NAME_TABLE,
    				// Subchunks of the name table. Each chunk contains an index & string
    				M_SUBMESH_NAME_TABLE_ELEMENT,
    	                // short index
                        // char* name

    	*/
        public final short id;

        MeshChunkID(int id) {
            // Be careful with the conversions
            // from short to int since it keeps the 1 in
            // two's complement
            this.id = (short) id;
        }

        public short getID() {
            return id;
        }
    }

    private static final long STREAM_OVERHEAD_SIZE =
            ENG_Short.SIZE_IN_BYTES + ENG_Integer.SIZE_IN_BYTES;

    public ENG_MeshSerializerImpl() {
        
        super();
        mVersion = "[MeshSerializer_v1.41]";
    }

    /** @noinspection deprecation */
    public void importMesh(
            RandomAccessFile acc,
            MappedByteBuffer buf,
            ENG_Mesh mesh,
            ENG_MeshSerializerListener listener) {
        this.mpfFile = acc;
        this.buf = buf;

        determineEndianness();
        readFileHeader();

        while (buf.hasRemaining()) {
            short readChunk = readChunk();
            if (readChunk == MeshChunkID.M_MESH.getID()) {
                readMesh(mesh, listener);
            }
        }
    }

    /** @noinspection deprecation */
    public void readMesh(ENG_Mesh mesh, ENG_MeshSerializerListener listener) {

        mesh.mAutoBuildEdgeLists = false;

        boolean[] bSkel = readBools(1);
        boolean skeletallyAnimated = bSkel[0];

        if (buf.hasRemaining()) {
            short streamID = readChunk();
            while (buf.hasRemaining() &&
                    (streamID == MeshChunkID.M_GEOMETRY.getID() ||
                            streamID == MeshChunkID.M_SUBMESH.getID() ||
                            streamID == MeshChunkID.M_MESH_SKELETON_LINK.getID() ||
                            streamID == MeshChunkID.M_MESH_BONE_ASSIGNMENT.getID() ||
                            streamID == MeshChunkID.M_MESH_LOD.getID() ||
                            streamID == MeshChunkID.M_MESH_BOUNDS.getID() ||
                            streamID == MeshChunkID.M_SUBMESH_NAME_TABLE.getID() ||
                            streamID == MeshChunkID.M_EDGE_LISTS.getID() ||
                            streamID == MeshChunkID.M_POSES.getID() ||
                            streamID == MeshChunkID.M_ANIMATIONS.getID() ||
                            streamID == MeshChunkID.M_TABLE_EXTREMES.getID())) {
                if (streamID == MeshChunkID.M_GEOMETRY.getID()) {
                    mesh.sharedVertexData = new ENG_VertexData();
                    try {
                        readGeometry(mesh, mesh.sharedVertexData);
                    } catch (ENG_InvalidFormatParsingException e) {
                        mesh.sharedVertexData = null;
                        buf.position(
                                (int) (mCurrentstreamLen -
                                        STREAM_OVERHEAD_SIZE));

                    }
                } else if (streamID == MeshChunkID.M_SUBMESH.getID()) {
                    readSubMesh(mesh, listener);
                } else if (streamID == MeshChunkID.M_MESH_SKELETON_LINK.getID()) {
                    readSkeletonLink(mesh, listener);
                } else if (streamID == MeshChunkID.M_MESH_BONE_ASSIGNMENT.getID()) {
                    readMeshBoneAssignment(mesh);
                } else if (streamID == MeshChunkID.M_MESH_LOD.getID()) {
                    readMeshLodInfo(mesh);
                } else if (streamID == MeshChunkID.M_MESH_BOUNDS.getID()) {
                    readBoundsInfo(mesh);
                } else if (streamID == MeshChunkID.M_SUBMESH_NAME_TABLE.getID()) {
                    readSubMeshNameTable(mesh);
                } else if (streamID == MeshChunkID.M_EDGE_LISTS.getID()) {
                    readEdgeList(mesh);
                } else if (streamID == MeshChunkID.M_POSES.getID()) {
                    readPoses(mesh);
                } else if (streamID == MeshChunkID.M_ANIMATIONS.getID()) {
                    readAnimations(mesh);
                } else if (streamID == MeshChunkID.M_TABLE_EXTREMES.getID()) {
                    readExtremes(mesh);
                }
                if (buf.hasRemaining()) {
                    streamID = readChunk();
                }
            }
            if (buf.hasRemaining()) {
                buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
            }
        }

    }

    /** @noinspection deprecation*/
    public void readExtremes(ENG_Mesh mesh) {

        ENG_SubMesh subMesh = mesh.getSubMesh(readShorts(1)[0]);

        int len = (int) ((mCurrentstreamLen -
                STREAM_OVERHEAD_SIZE - ENG_Short.SIZE_IN_BYTES) /
                ENG_Float.SIZE_IN_BYTES);

        assert (len % 3 == 0);
        float[] readFloats = readFloats(len);
        for (int i = 0; i < len; i += 3) {
            subMesh.extremityPoints.add(new ENG_Vector3D(readFloats, i));
        }
    }

    /** @noinspection deprecation*/
    public void readAnimations(ENG_Mesh mesh) {

        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk == MeshChunkID.M_ANIMATION.getID()) {
                readAnimation(mesh);
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
        }
    }

    /** @noinspection deprecation*/
    public void readAnimation(ENG_Mesh mesh) {

        String animName = readString();
        ENG_Animation anim =
                mesh.createAnimation(animName, readFloats(1)[0]);
        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk == MeshChunkID.M_ANIMATION_TRACK.getID()) {
                readAnimationTrack(anim, mesh);
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
        }
    }

    /** @noinspection deprecation*/
    public void readAnimationTrack(ENG_Animation anim, ENG_Mesh mesh) {

        short[] readShorts = readShorts(2);
        ENG_VertexAnimationTrack track =
                anim.createVertexTrack(readShorts[0],
                        mesh.getVertexDataByTrackHandle(readShorts[0]),
                        VertexAnimationType.getVertexAnimationType(
                                readShorts[1]));
        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    (readChunk == MeshChunkID.M_ANIMATION_MORPH_KEYFRAME.getID() ||
                            readChunk == MeshChunkID.M_ANIMATION_POSE_KEYFRAME.getID())) {
                if (readChunk == MeshChunkID.M_ANIMATION_MORPH_KEYFRAME.getID()) {
                    readMorphKeyFrame(track);
                } else if (readChunk == MeshChunkID.M_ANIMATION_POSE_KEYFRAME.getID()) {
                    readPoseKeyFrame(track);
                }
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
        }
    }

    public void readPoseKeyFrame(ENG_VertexAnimationTrack track) {

        ENG_VertexPoseKeyFrame kf =
                track.createVertexPoseKeyFrame(readFloats(1)[0]);
        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk == MeshChunkID.M_ANIMATION_POSE_REF.getID()) {
                short poseIndex = readShorts(1)[0];
                float influence = readFloats(1)[0];
                kf.addPoseReference(poseIndex, influence);
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
        }
    }

    public void readMorphKeyFrame(ENG_VertexAnimationTrack track) {

        ENG_VertexMorphKeyFrame kf =
                track.createVertexMorphKeyFrame(readFloats(1)[0]);
        int vertexCount = track.getAssociatedVertexData().vertexCount;
        ENG_HardwareVertexBuffer buffer =
                ENG_HardwareBufferManager.getSingleton().createVertexBuffer(
                        ENG_VertexElement.getTypeSize(
                                VertexElementType.VET_FLOAT3),
                        vertexCount,
                        Usage.HBU_STATIC.getUsage(),
                        true);
        FloatBuffer lock =
                ((ByteBuffer) buffer.lock(LockOptions.HBL_DISCARD))
                        .asFloatBuffer();
        float[] readFloats = readFloats(vertexCount * 3);
        lock.put(readFloats);
        buffer.unlock();
        kf.setBuffer(buffer);
    }

    /** @noinspection deprecation*/
    public void readPoses(ENG_Mesh mesh) {

        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk == MeshChunkID.M_POSE.getID()) {
                readPose(mesh);
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
        }
    }

    /** @noinspection deprecation*/
    public void readPose(ENG_Mesh mesh) {

        String name = readString();
        short target = readShorts(1)[0];

        ENG_Pose pose = mesh.createPose(target, name);

        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk == MeshChunkID.M_POSE_VERTEX.getID()) {
                int vertex = readInts(1)[0];
                pose.addVertex(vertex, new ENG_Vector3D(readFloats(3)));

                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
        }
    }

    /** @noinspection deprecation*/
    public void readEdgeList(ENG_Mesh mesh) {


        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk == MeshChunkID.M_EDGE_LIST_LOD.getID()) {
                short lodIndex = readShorts(1)[0];
                boolean isManual = readBools(1)[0];
                if (!isManual) {
                    ENG_MeshLodUsage usage = mesh.getLodLevel(lodIndex);
                    usage.edgeData = new ENG_EdgeData();
                    readEdgeListLodInfo(usage.edgeData);
                    for (EdgeGroupList edgeGroup : usage.edgeData.edgeGroups) {
                        if (mesh.sharedVertexData != null) {
                            if (edgeGroup.vertexSet == 0) {
                                edgeGroup.vertexData = mesh.sharedVertexData;
                            } else {
                                edgeGroup.vertexData =
                                        mesh.getSubMesh(
                                                (short) (edgeGroup.vertexSet - 1))
                                                .vertexData;
                            }
                        } else {
                            edgeGroup.vertexData =
                                    mesh.getSubMesh(
                                            (short) (edgeGroup.vertexSet))
                                            .vertexData;
                        }
                    }
                }
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
        }
        mesh.mAutoBuildEdgeLists = true;
    }

    public void readEdgeListLodInfo(ENG_EdgeData edgeData) {

        edgeData.isClosed = readBools(1)[0];
        int numTriangles = readInts(1)[0];
        for (int i = 0; i < numTriangles; ++i) {
            edgeData.triangles.add(new Triangle());
            edgeData.triangleFaceNormals.add(new ENG_Vector4D());
            edgeData.triangleLightFacings.add(new ENG_Byte());
        }
        int numEdgeGroups = readInts(1)[0];
        for (int i = 0; i < numTriangles; ++i) {
            Triangle triangle = edgeData.triangles.get(i);
            int[] readInts = readInts(8);
            triangle.indexSet = readInts[0];
            triangle.vertexSet = readInts[1];
            triangle.vertIndex[0] = readInts[2];
            triangle.vertIndex[1] = readInts[3];
            triangle.vertIndex[2] = readInts[4];
            triangle.sharedVertIndex[0] = readInts[5];
            triangle.sharedVertIndex[1] = readInts[6];
            triangle.sharedVertIndex[2] = readInts[7];
            edgeData.triangleFaceNormals.get(i).set(readFloats(4));
        }
        for (int i = 0; i < numEdgeGroups; ++i) {
            if (readChunk() != MeshChunkID.M_EDGE_GROUP.getID()) {
                throw new ENG_InvalidFormatParsingException(
                        "Missing edge group stream");
            }
            EdgeGroupList edgeList = edgeData.edgeGroups.get(i);
            int[] readInts = readInts(4);
            edgeList.vertexSet = readInts[0];
            edgeList.triStart = readInts[1];
            edgeList.triCount = readInts[2];
            int numEdges = readInts[3];
            for (int j = 0; j < numEdges; ++j) {
                Edge edge = new Edge();
                edgeList.edges.add(edge);
                int[] ints = readInts(6);
                edge.triIndex[0] = ints[0];
                edge.triIndex[1] = ints[1];
                edge.vertIndex[0] = ints[2];
                edge.vertIndex[1] = ints[3];
                edge.sharedVertIndex[0] = ints[4];
                edge.sharedVertIndex[1] = ints[5];
                edge.degenerate = readBools(1)[0];
            }
        }
    }

    /** @noinspection deprecation*/
    public void readSubMeshNameTable(ENG_Mesh mesh) {

        TreeMap<ENG_Short, String> subMeshNames =
                new TreeMap<>();

        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk ==
                            MeshChunkID.M_SUBMESH_NAME_TABLE_ELEMENT.getID()) {
                short idx = readShorts(1)[0];
                subMeshNames.put(new ENG_Short(idx), readString());
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
        }
        for (Entry<ENG_Short, String> entry : subMeshNames.entrySet()) {
            mesh.nameSubMesh(entry.getValue(), entry.getKey().getValue());
        }
    }

    /** @noinspection deprecation*/
    public void readBoundsInfo(ENG_Mesh mesh) {

        ENG_Vector3D min = new ENG_Vector3D(readFloats(3));
        ENG_Vector3D max = new ENG_Vector3D(readFloats(3));
        mesh._setBounds(new ENG_AxisAlignedBox(min, max), true);

        mesh._setBoundingSphereRadius(readFloats(1)[0]);
    }

    /** @noinspection deprecation*/
    public void readMeshLodInfo(ENG_Mesh mesh) {

        String strategyName = readString();
        ENG_LodStrategy strategy = ENG_LodStrategyManager.getSingleton()
                .getStrategy(strategyName);
        mesh.setLodStrategy(strategy);

        mesh.mNumLods = readShorts(1)[0];
        mesh.mIsLodManual = readBools(1)[0];

        if (!mesh.mIsLodManual) {
            short numSubMeshes = mesh.getNumSubMeshes();

            for (short i = 0; i < numSubMeshes; ++i) {
                ENG_SubMesh subMesh = mesh.getSubMesh(i);
                subMesh.mLodFaceList.ensureCapacity(mesh.mNumLods - 1);
			/*	for (int j = 0; j < mesh.mNumLods - 1; ++ j) {
					subMesh.mLodFaceList.add(new ENG_IndexData());
				}*/
            }
        }

        for (int i = 1; i < mesh.mNumLods; ++i) {
            short readChunk = readChunk();
            if (readChunk != MeshChunkID.M_MESH_LOD_USAGE.getID()) {
                throw new ENG_InvalidFormatParsingException("Mesh lod usage " +
                        "missing in " + mesh.getName());
            }
            ENG_MeshLodUsage usage = new ENG_MeshLodUsage();
            usage.userValue = readFloats(1)[0];

            if (mesh.isLodManual()) {
                readMeshLodUsageManual(mesh, i, usage);
            } else {
                readMeshLodUsageGenerated(mesh, i, usage);
            }
            usage.edgeData = null;

            mesh.mMeshLodUsageList.add(usage);
        }

    }

    /** @noinspection deprecation*/
    public void readMeshLodUsageGenerated(ENG_Mesh mesh, int numLod,
                                          ENG_MeshLodUsage usage) {

        usage.manualName = "";
        usage.manualMesh = null;

        short numSubMeshes = mesh.getNumSubMeshes();
        for (short i = 0; i < numSubMeshes; ++i) {
            short readChunk = readChunk();
            if (readChunk != MeshChunkID.M_MESH_LOD_GENERATED.getID()) {
                throw new ENG_InvalidFormatParsingException("Missing " +
                        "mesh lod generated in " + mesh.getName());
            }

            ENG_SubMesh subMesh = mesh.getSubMesh(i);
            ENG_IndexData indexData = new ENG_IndexData();
            subMesh.mLodFaceList.add(indexData);

            indexData.indexCount = readInts(1)[0];
            boolean idx32Bit = readBools(1)[0];
            if (idx32Bit) {
                indexData.indexBuffer = ENG_HardwareBufferManager.getSingleton()
                        .createIndexBuffer(
                                IndexType.IT_32BIT,
                                subMesh.indexData.indexCount,
                                mesh.mIndexBufferUsage.getUsage(),
                                mesh.mIndexBufferShadowBuffer);
                ByteBuffer lock = (ByteBuffer) indexData.indexBuffer
                        .lock(0, indexData.indexBuffer.sizeInBytes,
                                LockOptions.HBL_DISCARD);
                int[] readInts = readInts(subMesh.indexData.indexCount);
                lock.asIntBuffer().put(readInts);
                indexData.indexBuffer.unlock();
            } else {
                indexData.indexBuffer = ENG_HardwareBufferManager.getSingleton()
                        .createIndexBuffer(
                                IndexType.IT_16BIT,
                                subMesh.indexData.indexCount,
                                mesh.mIndexBufferUsage.getUsage(),
                                mesh.mIndexBufferShadowBuffer);
                ByteBuffer lock = (ByteBuffer) indexData.indexBuffer
                        .lock(0, indexData.indexBuffer.sizeInBytes,
                                LockOptions.HBL_DISCARD);
                short[] readShorts = readShorts(subMesh.indexData.indexCount);
                lock.asShortBuffer().put(readShorts);
                indexData.indexBuffer.unlock();
            }
        }
    }

    /** @noinspection deprecation*/
    public void readMeshLodUsageManual(ENG_Mesh mesh, int i,
                                       ENG_MeshLodUsage usage) {

        if (readChunk() != MeshChunkID.M_MESH_LOD_MANUAL.getID()) {
            throw new ENG_InvalidFormatParsingException("Missing " +
                    "mesh lod manual in " + mesh.getName());
        }
        usage.manualName = readString();
        usage.manualMesh = null;
    }

    /** @noinspection deprecation*/
    public void readMeshBoneAssignment(ENG_Mesh mesh) {

        ENG_VertexBoneAssignment assig = new ENG_VertexBoneAssignment();

        assig.vertexIndex = readInts(1)[0];
        assig.boneIndex = readShorts(1)[0];
        assig.weight = readFloats(1)[0];

        mesh.addBoneAssignment(assig);
    }

    /** @noinspection deprecation */
    public void readSkeletonLink(ENG_Mesh mesh,
                                 ENG_MeshSerializerListener listener) {

        String skelName = readString();
        if (listener != null) {
            listener.processSkeletonName(mesh, skelName);
        }
        mesh.setSkeletonName(skelName);
    }

    /** @noinspection deprecation */
    public void readSubMesh(ENG_Mesh mesh, ENG_MeshSerializerListener l) {

        ENG_SubMesh subMesh = mesh.createSubMesh();

        String materialName = readString();
        if (l != null) {
            l.processMaterialName(mesh, materialName);
        }

        subMesh.setMaterialName(materialName);

        subMesh.useSharedVertices = readBools(1)[0];

        subMesh.indexData.indexStart = 0;
        subMesh.indexData.indexCount = readInts(1)[0];

        boolean idx32Bit = readBools(1)[0];

        ENG_HardwareIndexBuffer ibuf = null;

        if (subMesh.indexData.indexCount > 0) {
            if (idx32Bit) {
                ibuf = ENG_HardwareBufferManager.getSingleton()
                        .createIndexBuffer(
                                IndexType.IT_32BIT,
                                subMesh.indexData.indexCount,
                                mesh.mIndexBufferUsage.getUsage(),
                                mesh.mIndexBufferShadowBuffer);
                ByteBuffer lock = (ByteBuffer) ibuf.lock(LockOptions.HBL_DISCARD);
                int[] readInts = readInts(subMesh.indexData.indexCount);
                lock.asIntBuffer().put(readInts);
                ibuf.unlock();
            } else {
                ibuf = ENG_HardwareBufferManager.getSingleton()
                        .createIndexBuffer(
                                IndexType.IT_16BIT,
                                subMesh.indexData.indexCount,
                                mesh.mIndexBufferUsage.getUsage(),
                                mesh.mIndexBufferShadowBuffer);
                ByteBuffer lock = (ByteBuffer) ibuf.lock(LockOptions.HBL_DISCARD);
                short[] readShorts = readShorts(subMesh.indexData.indexCount);
                lock.asShortBuffer().put(readShorts);
                ibuf.unlock();
            }
        }
        subMesh.indexData.indexBuffer = ibuf;

        if (!subMesh.useSharedVertices) {
            short readChunk = readChunk();
            if (readChunk != MeshChunkID.M_GEOMETRY.getID()) {
                throw new ENG_InvalidFormatParsingException("Missing geometry " +
                        "data in mesh file");
            }
            subMesh.vertexData = new ENG_VertexData();
            readGeometry(mesh, subMesh.vertexData);
        }

        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    (readChunk == MeshChunkID.M_SUBMESH_BONE_ASSIGNMENT.getID() ||
                            readChunk == MeshChunkID.M_SUBMESH_OPERATION.getID() ||
                            readChunk == MeshChunkID.M_SUBMESH_TEXTURE_ALIAS.getID())) {

                if (readChunk == MeshChunkID.M_SUBMESH_BONE_ASSIGNMENT.getID()) {
                    readSubMeshBoneAssignment(subMesh);
                } else if (readChunk == MeshChunkID.M_SUBMESH_OPERATION.getID()) {
                    readSubMeshOperation(subMesh);
                } else if (readChunk == MeshChunkID.M_SUBMESH_TEXTURE_ALIAS.getID()) {
                    readSubMeshTextureAlias(subMesh);
                }
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            if (buf.hasRemaining()) {
                buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
            }
        }
    }

    public void readSubMeshTextureAlias(ENG_SubMesh subMesh) {

        String aliasName = readString();
        String textureName = readString();
        subMesh.addTextureAlias(aliasName, textureName);
    }

    public void readSubMeshOperation(ENG_SubMesh subMesh) {

        short op = readShorts(1)[0];

        subMesh.operationType = OperationType.getOperationType(op);
    }

    public void readSubMeshBoneAssignment(ENG_SubMesh subMesh) {

        ENG_VertexBoneAssignment assig = new ENG_VertexBoneAssignment();

        assig.vertexIndex = readInts(1)[0];
        assig.boneIndex = readShorts(1)[0];
        assig.weight = readFloats(1)[0];

//		System.out.println("vertexIndex: " + assig.vertexIndex + 
//				" boneIndex: " + assig.boneIndex +
//				" weight: " + assig.weight);
        subMesh.addBoneAssignment(assig);
    }

    /** @noinspection deprecation*/
    public void readGeometry(ENG_Mesh mesh, ENG_VertexData dest) {

        dest.vertexStart = 0;

        dest.vertexCount = readInts(1)[0];

        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    (readChunk == MeshChunkID.M_GEOMETRY_VERTEX_DECLARATION.getID() ||
                            readChunk == MeshChunkID.M_GEOMETRY_VERTEX_BUFFER.getID())) {
                if (readChunk == MeshChunkID.M_GEOMETRY_VERTEX_DECLARATION.getID()) {
                    readGeometryVertexDeclaration(mesh, dest);
                } else if (readChunk == MeshChunkID.M_GEOMETRY_VERTEX_BUFFER.getID()) {
                    readGeometryVertexBuffer(mesh, dest);
                }
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            if (buf.hasRemaining()) {
                buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
            }
        }

        if (ENG_RenderRoot.getRenderRoot().getRenderSystem() != null) {
            dest.convertPackedColour(VertexElementType.VET_COLOUR_ARGB,
                    ENG_VertexElement.getBestColorVertexElementType());
        }
    }

    /** @noinspection deprecation*/
    public void readGeometryVertexDeclaration(ENG_Mesh mesh,
                                              ENG_VertexData dest) {

        if (buf.hasRemaining()) {
            short readChunk = readChunk();
            while (buf.hasRemaining() &&
                    readChunk ==
                            MeshChunkID.M_GEOMETRY_VERTEX_ELEMENT.getID()) {
                readGeometryVertexElement(mesh, dest);
                if (buf.hasRemaining()) {
                    readChunk = readChunk();
                }
            }
            if (buf.hasRemaining()) {
                buf.position((int) (buf.position() - STREAM_OVERHEAD_SIZE));
            }
        }
    }

    /** @noinspection deprecation*/
    public void readGeometryVertexElement(ENG_Mesh mesh, ENG_VertexData dest) {

        short[] readShorts = readShorts(5);
        short source = readShorts[0];

        VertexElementType vertexElementType =
                VertexElementType.getVertexElementType(readShorts[1]);
        VertexElementSemantic vertexElementSemantic =
                VertexElementSemantic.getVertexElementSemantic(readShorts[2]);
        short offset = readShorts[3];
        short index = readShorts[4];

        dest.vertexDeclaration.addElement(
                source,
                offset,
                vertexElementType,
                vertexElementSemantic,
                index);

        if (vertexElementType == VertexElementType.VET_COLOUR) {
            ENG_Log.getInstance().log("VET_COLOUR is deprecated. Update " +
                    "mesh ASAP", ENG_Log.TYPE_WARNING);
        }
    }

    /** @noinspection deprecation*/
    public void readGeometryVertexBuffer(ENG_Mesh mesh, ENG_VertexData dest) {

        short[] readShorts = readShorts(2);
        short bindIndex = readShorts[0];
        short vertexSize = readShorts[1];

        short headerID = readChunk();
        if (headerID != MeshChunkID.M_GEOMETRY_VERTEX_BUFFER_DATA.getID()) {
            throw new ENG_InvalidFormatParsingException("Can't find " +
                    "the vertex buffer");
        }
        if (dest.vertexDeclaration.getVertexSize(bindIndex) != vertexSize) {
            throw new ENG_ParsingException("Buffer vertex size does not " +
                    "agree with vertex declaration");
        }
        ENG_HardwareVertexBuffer buffer =
                ENG_HardwareBufferManager.getSingleton()
                        .createVertexBuffer(
                                vertexSize,
                                dest.vertexCount,
                                mesh.mVertexBufferUsage.getUsage(),
                                mesh.mVertexBufferShadowBuffer);
        ByteBuffer lock = (ByteBuffer) buffer.lock(LockOptions.HBL_DISCARD);
	/*	int oldPos = lock.position();
		int oldLimit = lock.limit();
		lock.limit(oldPos + vertexSize * dest.vertexCount);
		
		try {
			mpfFile.getChannel().read(lock);
		} catch (IOException e) {

			e.printStackTrace();
			ENG_Log.getInstance().log("Cannot read into vertex buffer " +
					"from file for mesh " + mesh.getName(), 
					ENG_Log.TYPE_FATAL_ERROR);
		}
		
		lock.position(oldPos);
		lock.limit(oldLimit);*/
        byte[] readBytes = readBytes(vertexSize * dest.vertexCount);
        lock.put(readBytes);

        flipFromLittleEndian(lock, dest.vertexCount, vertexSize,
                dest.vertexDeclaration.findElementsBySource(bindIndex));
        buffer.unlock();

        dest.vertexBufferBinding.setBinding(bindIndex, buffer);
    }

    public void flipFromLittleEndian(ByteBuffer pData, int vertexCount,
                                     int vertexSize, LinkedList<ENG_VertexElement> elems) {
        if (mFlipEndian) {
            flipEndian(pData, vertexCount, vertexSize, elems);
        }
    }

    public void flipEndian(ByteBuffer pData, int vertexCount,
                           int vertexSize, LinkedList<ENG_VertexElement> elems) {
        int pBase = pData.position();
        int oldPos = pBase;

        for (int i = 0; i < vertexCount; ++i) {
            for (ENG_VertexElement elem : elems) {
                int pElem = elem.baseVertexPointerToElement(pBase);
                int typeSize = 0;
                switch (ENG_VertexElement.getBaseType(elem.getType())) {
                    case VET_FLOAT1:
                        typeSize = ENG_Float.SIZE_IN_BYTES;
                        break;
                    case VET_SHORT1:
                        typeSize = ENG_Short.SIZE_IN_BYTES;
                        break;
                    case VET_COLOUR:
                    case VET_COLOUR_ABGR:
                    case VET_COLOUR_ARGB:
                        typeSize = 4 * ENG_Byte.SIZE_IN_BYTES;
                        break;
                    case VET_UBYTE4:
                        break;
                    default:
                        throw new ENG_InvalidFormatParsingException();
                }
                flipEndian(pData, pElem, typeSize,
                        ENG_VertexElement.getTypeCount(elem.getType()));
            }
            pBase += vertexSize;
        }
        pData.position(oldPos);
    }

}
