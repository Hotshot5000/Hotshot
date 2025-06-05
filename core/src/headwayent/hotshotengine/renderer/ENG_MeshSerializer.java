/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Log;
import headwayent.hotshotengine.ENG_Serializer;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.resource.ENG_Resource.RandomAccessMode;

import java.util.TreeMap;

@Deprecated
public class ENG_MeshSerializer extends ENG_Serializer {

    public static final short HEADER_CHUNK_ID = 0x1000;

    protected static final String msCurrentVersion = "[MeshSerializer_v1.41]";
    /** @noinspection deprecation*/
    protected final TreeMap<String, ENG_MeshSerializerImpl> mImplementations =
            new TreeMap<>();
    /** @noinspection deprecation*/
    protected ENG_MeshSerializerListener mListener;

    /** @noinspection deprecation */
    public ENG_MeshSerializer() {
        super();

        mImplementations.put(msCurrentVersion, new ENG_MeshSerializerImpl());
        mImplementations.put("[MeshSerializer_v1.40]",
                new ENG_MeshSerializerImpl_v1_4());
    }

    /** @noinspection deprecation*/
    public void exportMesh(
            ENG_Mesh mesh,
            String filename) {
        exportMesh(mesh, filename, Endian.ENDIAN_NATIVE);
    }

    /** @noinspection deprecation*/
    public void exportMesh(
            ENG_Mesh mesh,
            String filename,
            Endian endianess) {
        openFile(filename, RandomAccessMode.READ_WRITE);
    }

    /** @noinspection deprecation */
    public void importMesh(String filename, ENG_Mesh mesh) {
        // We only read but the random access downwards cannot know that
        openFile(filename, RandomAccessMode.READ_WRITE);
        determineEndianness();
        short[] header = readShorts(1);
        if (header[0] != HEADER_CHUNK_ID) {
            throw new ENG_InvalidFormatParsingException("The file is not " +
                    "a valid mesh");
        }
        String readString = readString();
        buf.position(0);
        ENG_MeshSerializerImpl impl = mImplementations.get(readString);
        if (impl == null) {
            throw new ENG_InvalidFormatParsingException("mesh of version " +
                    readString + " not supported");
        }
        impl.importMesh(mpfFile, buf, mesh, mListener);

        if (!readString.equals(msCurrentVersion)) {
            ENG_Log.getInstance().log("The mesh " + mesh.getName() +
                            " is not " +
                            "of the latest mesh format version",
                    ENG_Log.TYPE_NOTIFICATION);
        }
        closeFile();
    }

    /** @noinspection deprecation*/
    public void setListener(ENG_MeshSerializerListener l) {
        mListener = l;
    }

    /** @noinspection deprecation*/
    public ENG_MeshSerializerListener getListener() {
        return mListener;
    }

}
