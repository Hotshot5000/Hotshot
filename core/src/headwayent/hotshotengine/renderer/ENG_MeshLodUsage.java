/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_MeshLodUsage {

    public float userValue;
    public float value;

    /// Only relevant if mIsLodManual is true, the name of the alternative mesh to use
    public String manualName;
    /// Only relevant if mIsLodManual is true, the name of the group of the alternative mesh
    public String manualGroup;
    /** @noinspection deprecation*/ /// Hard link to mesh to avoid looking up each time
    public ENG_Mesh manualMesh;

    public ENG_EdgeData edgeData;
}
