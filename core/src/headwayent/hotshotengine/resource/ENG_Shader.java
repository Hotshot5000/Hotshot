/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

/**
 *
 */
package headwayent.hotshotengine.resource;

import java.util.ArrayList;

/**
 * @author Sebi
 */
public class ENG_Shader {

    public static final int VERTEX_PROGRAM = 1;
    public static final int FRAGMENT_PROGRAM = 2;

    public String name;
    public String fileName;
    public String path;
    public ENG_ShaderParam defaultParam;
    public final ArrayList<ENG_ShaderParam> paramList = new ArrayList<>();
    public int id;    // No longer used
    public int glID;
    public int type;
    public boolean skeletalAnimation;
    public boolean morphAnimation;
    //	public boolean poseAnimation;
    public int poseAnimationNum;
    public boolean useVertexTextureFetch;
    public boolean useAdjacencyInfo;
}
