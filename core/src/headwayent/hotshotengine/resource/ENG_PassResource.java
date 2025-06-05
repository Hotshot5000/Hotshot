/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendFactor;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendOperation;

import java.util.ArrayList;

public class ENG_PassResource {

    public String name;
    public String vertexProgramID;
    public String fragmentProgramID;
    public int vertexProgramParamListID = -1;
    public int fragmentProgramParamListID = -1;
    public final ArrayList<ENG_TextureUnitResource> textureUnits = new ArrayList<>();
    public boolean depthCheck;
    public SceneBlendFactor sourceBlendFactor;
    public SceneBlendFactor destBlendFactor;
    public SceneBlendFactor sourceBlendFactorAlpha;
    public SceneBlendFactor destBlendFactorAlpha;
    public SceneBlendOperation blendOperation;
    public SceneBlendOperation alphaBlendOperation;
    public boolean depthWrite;
}
