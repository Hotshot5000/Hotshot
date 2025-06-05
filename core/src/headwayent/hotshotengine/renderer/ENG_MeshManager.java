/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;

import java.util.HashMap;

@Deprecated
public class ENG_MeshManager {

//    private static ENG_MeshManager meshManager;
    private float mBoundsPaddingFactor = 0.01f;
    /** @noinspection deprecation*/
    private final HashMap<String, ENG_Mesh> meshList = new HashMap<>();

    public ENG_MeshManager() {
//        if (meshManager == null) {
//            meshManager = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        meshManager = this;
    }

    public float getBoundsPaddingFactor() {
        return mBoundsPaddingFactor;
    }

    public void setBoundsPaddingFactor(float f) {
        mBoundsPaddingFactor = f;
    }

    /** @noinspection deprecation*/
    public static ENG_MeshManager getSingleton() {
//        if (MainActivity.isDebugmode() && meshManager == null) {
//            throw new NullPointerException("MeshManager not initialized!");
//        }
//        return meshManager;
        return MainApp.getGame().getRenderRoot().getMeshManager();
    }

    public void _initialise() {


    }

    /** @noinspection deprecation */
    public ENG_Mesh createManual(String name) {
        ENG_Mesh mesh = new ENG_Mesh(name);
        ENG_Mesh put = meshList.put(name, mesh);
        if (put != null) {
            throw new IllegalArgumentException(name + " mesh is already created");
        }
        return mesh;
    }

    /** @noinspection deprecation*/
    public ENG_Mesh getByName(String name) {
        return meshList.get(name);
    }

    public void destroyMesh(String name) {
        destroyMesh(name, false);
    }

    /** @noinspection deprecation*/
    public void destroyMesh(String name, boolean skipGLDelete) {
        ENG_Mesh mesh = meshList.remove(name);
        if (mesh != null) {
            mesh.destroy(skipGLDelete);
        } else {
            throw new IllegalArgumentException(name + " not found in mesh manager");
        }
    }

    /** @noinspection deprecation*/
    public void destroyAllMeshes(boolean skipGLDelete) {
        for (ENG_Mesh mesh : meshList.values()) {
            mesh.destroy(skipGLDelete);
        }
        meshList.clear();
    }
}
