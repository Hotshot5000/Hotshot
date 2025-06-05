/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;

import java.util.ArrayList;

public interface ENG_Renderable {

    ENG_Material getMaterial();

    ENG_Technique getTechnique();

    void getRenderOperation(ENG_RenderOperation op);

    boolean preRender(ENG_SceneManager sm, ENG_RenderSystem rsys);

    boolean postRender(ENG_SceneManager sm, ENG_RenderSystem rsys);

    void getWorldTransforms(ENG_Matrix4[] xform);

    short getNumWorldTransforms();

    void setUseIdentityProjection(boolean useIdentityProjection);

    boolean getUseIdentityProjection();

    void setUseIdentityView(boolean useIdentityView);

    boolean getUseIdentityView();

    float getSquaredViewDepth(ENG_Camera cam); //{
    //return 0.0f;
    //}

    /** @noinspection deprecation*/
    ArrayList<ENG_Light> getLights();

    boolean getCastsShadows();

    void setCustomParameter(int index, ENG_Vector4D value);

    void setCustomParameter(ENG_Integer index, ENG_Vector4D value);

    ENG_Vector4D getCustomParameter(int index);

    ENG_Vector4D getCustomParameter(ENG_Integer index);

    void _updateCustomGpuParameter(ENG_AutoConstantEntry constantEntry,
                                   ENG_GpuProgramParameters params);

    void setPolygonModeOverrideable(boolean override);

    boolean getPolygonModeOverrideable();

}