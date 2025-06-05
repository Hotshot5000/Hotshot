/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_RenderableImpl.Visitor;

import java.util.ArrayList;
import java.util.TreeMap;

public class ENG_SimpleRenderable extends ENG_MovableObject implements
        ENG_Renderable {

    protected final ENG_RenderOperation mRenderOp = new ENG_RenderOperation();

    protected final ENG_Matrix4 m_matWorldTransform =
            new ENG_Matrix4(ENG_Math.MAT4_IDENTITY);
    protected final ENG_AxisAlignedBox mBox = new ENG_AxisAlignedBox();

    protected String m_strMatName;
    protected ENG_Material m_pMaterial;

    /// The scene manager for the current frame.
    protected ENG_SceneManager m_pParentSceneManager;

    /// The camera for the current frame.
    protected ENG_Camera m_pCamera;

    protected boolean mUseIdentityProjection;

    protected boolean mUseIdentityView;

    protected final TreeMap<ENG_Integer, ENG_Vector4D> mCustomParameters =
            new TreeMap<>();

    private boolean mPolygonOverrideable;

    /// Static member used to automatically generate names for SimpleRendaerable objects.
    protected static int ms_uGenNameCount;

    public ENG_SimpleRenderable() {
        mName = "ScreenRenderable" + (ms_uGenNameCount++);
    }

    public void setMaterial(String name) {
        m_strMatName = name;
        m_pMaterial = ENG_MaterialManager.getSingleton().getByName(name);
    }

    @Override
    public ENG_Material getMaterial() {
        
        return m_pMaterial;
    }

    @Override
    public ENG_Technique getTechnique() {
        
        return getMaterial().getBestTechnique(ENG_Material.defaultLodIndex, this);
    }

    public void setRenderOperation(ENG_RenderOperation op) {
        mRenderOp.set(op);
    }

    @Override
    public void getRenderOperation(ENG_RenderOperation op) {
        
        op.set(this.mRenderOp);
    }

    @Override
    public boolean preRender(ENG_SceneManager sm, ENG_RenderSystem rsys) {
        
        return true;
    }

    @Override
    public boolean postRender(ENG_SceneManager sm, ENG_RenderSystem rsys) {
        
        return true;
    }

    public void setWorldTransform(ENG_Matrix4 xform) {
        m_matWorldTransform.set(xform);
    }

    @Override
    public void getWorldTransforms(ENG_Matrix4[] xform) {
        
        xform[0].set(m_matWorldTransform.concatenate(mParentNode._getFullTransform()));
    }

    public void _notifyCurrentCamera(ENG_Camera cam) {
        super._notifyCurrentCamera(cam);

        m_pCamera = cam;
    }

    @Override
    public short getNumWorldTransforms() {
        
        return (short) 1;
    }

    @Override
    public void setUseIdentityProjection(boolean useIdentityProjection) {
        

        mUseIdentityProjection = useIdentityProjection;
    }

    @Override
    public boolean getUseIdentityProjection() {
        
        return mUseIdentityProjection;
    }

    @Override
    public void setUseIdentityView(boolean useIdentityView) {
        

        mUseIdentityView = useIdentityView;
    }

    @Override
    public boolean getUseIdentityView() {
        
        return mUseIdentityView;
    }

    @Override
    public float getSquaredViewDepth(ENG_Camera cam) {
        
        return 0;
    }

    /** @noinspection deprecation*/
    @Override
    public ArrayList<ENG_Light> getLights() {
        
        return queryLights();
    }

    @Override
    public boolean getCastsShadows() {
        
        return false;
    }

    @Override
    public void setCustomParameter(int index, ENG_Vector4D value) {
        

        mCustomParameters.put(new ENG_Integer(index), value);
    }

    @Override
    public void setCustomParameter(ENG_Integer index, ENG_Vector4D value) {
        

        mCustomParameters.put(index, value);
    }

    @Override
    public ENG_Vector4D getCustomParameter(int index) {
        
        ENG_Vector4D i = mCustomParameters.get(new ENG_Integer(index));
        if (i == null) {
            throw new IllegalArgumentException(
                    "Parameter at the given index was not found.");
        }
        return i;
    }

    @Override
    public ENG_Vector4D getCustomParameter(ENG_Integer index) {
        
        ENG_Vector4D i = mCustomParameters.get(index);
        if (i == null) {
            throw new IllegalArgumentException(
                    "Parameter at the given index was not found.");
        }
        return i;
    }

    @Override
    public void _updateCustomGpuParameter(ENG_AutoConstantEntry constantEntry,
                                          ENG_GpuProgramParameters params) {
        

        ENG_Vector4D i = mCustomParameters.get(new ENG_Integer(constantEntry.data));
        if (i != null) {
            params._writeRawConstant(constantEntry.physicalIndex, i,
                    constantEntry.elementCount);
        }
    }

    @Override
    public void setPolygonModeOverrideable(boolean override) {
        

        mPolygonOverrideable = override;
    }

    @Override
    public boolean getPolygonModeOverrideable() {
        
        return mPolygonOverrideable;
    }

    @Override
    public void _updateRenderQueue(ENG_RenderQueue queue) {
        

        queue.addRenderable(this, mRenderQueueID,
                ENG_RenderQueue.OGRE_RENDERABLE_DEFAULT_PRIORITY);
    }

    public void setBoundingBox(ENG_AxisAlignedBox box) {
        mBox.set(box);
    }

    @Override
    public void getBoundingBox(ENG_AxisAlignedBox ret) {
        

        ret.set(mBox);
    }

    @Override
    public float getBoundingRadius() {
        
        return 0;
    }

    @Override
    public void visitRenderables(Visitor visitor, boolean debugRenderables) {
        

        visitor.visit(this, (short) 0, false);
    }

    @Override
    public String getMovableType() {
        
        return "ScreenRenderable";
    }

}
