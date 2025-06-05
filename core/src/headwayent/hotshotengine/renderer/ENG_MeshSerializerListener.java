/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

@Deprecated
public interface ENG_MeshSerializerListener {

    /** @noinspection deprecation*/
    void processMaterialName(ENG_Mesh mesh, String name);

    /** @noinspection deprecation*/
    void processSkeletonName(ENG_Mesh mesh, String name);
}
