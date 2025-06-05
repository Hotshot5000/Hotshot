/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;

import java.util.ArrayList;
import java.util.TreeMap;

public abstract class ENG_RenderableImpl implements ENG_Renderable {

    protected final TreeMap<ENG_Integer, ENG_Vector4D> mCustomParameters =
            new TreeMap<>();
    protected boolean mPolygonModeOverrideable = true;
    protected boolean mUseIdentityProjection;
    protected boolean mUseIdentityView;

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getMaterial()
     */
    @Override
    public abstract ENG_Material getMaterial();

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getTechnique()
     */
    @Override
    public ENG_Technique getTechnique() {
        return getMaterial().getBestTechnique(ENG_Material.defaultLodIndex, this);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getRenderOperation(headwayEnt.HotshotEngine.Renderer.ENG_RenderOperation)
     */
    @Override
    public abstract void getRenderOperation(ENG_RenderOperation op);

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#preRender(headwayEnt.HotshotEngine.Renderer.ENG_SceneManager, headwayEnt.HotshotEngine.Renderer.ENG_RenderSystem)
     */
    @Override
    public boolean preRender(ENG_SceneManager sm, ENG_RenderSystem rsys) {
        return true;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#postRender(headwayEnt.HotshotEngine.Renderer.ENG_SceneManager, headwayEnt.HotshotEngine.Renderer.ENG_RenderSystem)
     */
    @Override
    public boolean postRender(ENG_SceneManager sm, ENG_RenderSystem rsys) {
        return true;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getWorldTransforms(headwayEnt.HotshotEngine.ENG_Matrix4[])
     */
    @Override
    public abstract void getWorldTransforms(ENG_Matrix4[] xform);

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getNumWorldTransforms()
     */
    @Override
    public short getNumWorldTransforms() {
        return (short) 1;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#setUseIdentityProjection(boolean)
     */
    @Override
    public void setUseIdentityProjection(boolean useIdentityProjection) {
        mUseIdentityProjection = useIdentityProjection;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getUseIdentityProjection()
     */
    @Override
    public boolean getUseIdentityProjection() {
        return mUseIdentityProjection;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#setUseIdentityView(boolean)
     */
    @Override
    public void setUseIdentityView(boolean useIdentityView) {
        mUseIdentityView = useIdentityView;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getUseIdentityView()
     */
    @Override
    public boolean getUseIdentityView() {
        return mUseIdentityView;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getSquaredViewDepth(headwayEnt.HotshotEngine.Renderer.ENG_Camera)
     */
    @Override
    public abstract float getSquaredViewDepth(ENG_Camera cam); //{
    //return 0.0f;
    //}

    /** @noinspection deprecation*/ /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getLights()
     */
    @Override
    public abstract ArrayList<ENG_Light> getLights();

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getCastsShadows()
     */
    @Override
    public boolean getCastsShadows() {
        return false;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#setCustomParameter(int, headwayEnt.HotshotEngine.ENG_Vector4D)
     */
    @Override
    public void setCustomParameter(int index, ENG_Vector4D value) {
        mCustomParameters.put(new ENG_Integer(index), value);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#setCustomParameter(headwayEnt.HotshotEngine.BasicTypes.ENG_Integer, headwayEnt.HotshotEngine.ENG_Vector4D)
     */
    @Override
    public void setCustomParameter(ENG_Integer index, ENG_Vector4D value) {
        mCustomParameters.put(index, value);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getCustomParameter(int)
     */
    @Override
    public ENG_Vector4D getCustomParameter(int index) {
        ENG_Vector4D i = mCustomParameters.get(new ENG_Integer(index));
        if (i == null) {
            throw new IllegalArgumentException(
                    "Parameter at the given index was not found.");
        }
        return i;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getCustomParameter(headwayEnt.HotshotEngine.BasicTypes.ENG_Integer)
     */
    @Override
    public ENG_Vector4D getCustomParameter(ENG_Integer index) {
        ENG_Vector4D i = mCustomParameters.get(index);
        if (i == null) {
            throw new IllegalArgumentException(
                    "Parameter at the given index was not found.");
        }
        return i;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#_updateCustomGpuParameter(headwayEnt.HotshotEngine.Renderer.ENG_AutoConstantEntry, headwayEnt.HotshotEngine.Renderer.ENG_GpuProgramParameters)
     */
    @Override
    public void _updateCustomGpuParameter(ENG_AutoConstantEntry constantEntry,
                                          ENG_GpuProgramParameters params) {
        ENG_Vector4D i = mCustomParameters.get(new ENG_Integer(constantEntry.data));
        if (i != null) {
            params._writeRawConstant(constantEntry.physicalIndex, i,
                    constantEntry.elementCount);
        }
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#setPolygonModeOverrideable(boolean)
     */
    @Override
    public void setPolygonModeOverrideable(boolean override) {
        mPolygonModeOverrideable = override;
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_IRenderable#getPolygonModeOverrideable()
     */
    @Override
    public boolean getPolygonModeOverrideable() {
        return mPolygonModeOverrideable;
    }

    static abstract class Visitor {
        public abstract void visit(ENG_Renderable rend, short lodIndex, boolean isDebug);
    }
}
