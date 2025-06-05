/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendType;
import headwayent.hotshotengine.renderer.ENG_Pass.IlluminationStage;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.GPUVendor;

import java.util.ArrayList;
import java.util.Iterator;

public class ENG_Technique {


    public enum IlluminationPassesState {
        IPS_COMPILE_DISABLED(-1),
        IPS_NOT_COMPILED(0),
        IPS_COMPILED(1);

        private final int state;

        IlluminationPassesState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    protected final ArrayList<ENG_Pass> mPasses = new ArrayList<>();
    protected final ArrayList<ENG_IlluminationPass> mIlluminationPasses =
            new ArrayList<>();
    protected ENG_Material mParent;
    protected boolean mIsSupported = true;
    protected IlluminationPassesState mIlluminationPassesCompilationPhase =
            IlluminationPassesState.IPS_NOT_COMPILED;
    protected short mLodIndex;

    /**
     * Scheme index, derived from scheme name but the names are held on
     * MaterialManager, for speed an index is used here.
     */
    protected short mSchemeIndex;
    protected String mName; // optional name for the technique

    /**
     * When casting shadow, if not using default Ogre shadow casting material, or
     * nor using fixed function casting, mShadowCasterMaterial let you customize per material
     * shadow caster behavior
     */
    protected ENG_Material mShadowCasterMaterial;
    /**
     * When casting shadow, if not using default Ogre shadow casting material, or
     * nor using fixed function casting, mShadowCasterMaterial let you customize per material
     * shadow caster behavior.There only material name is stored so that it can be loaded once all file parsed in a resource group.
     */
    protected String mShadowCasterMaterialName;
    /**
     * When receiving shadow, if not using default Ogre shadow receiving material, or
     * nor using fixed function texture projection receiving, mShadowReceiverMaterial let you customize per material
     * shadow caster behavior
     */
    protected ENG_Material mShadowReceiverMaterial;
    /**
     * When receiving shadow, if not using default Ogre shadow receiving material, or
     * nor using fixed function texture projection receiving, mShadowReceiverMaterial let you customize per material
     * shadow caster behavior. There only material name is stored so that it can be loaded once all file parsed in a resource group.
     */
    protected String mShadowReceiverMaterialName;

    protected ArrayList<GPUVendorRule> mGPUVendorRules = new ArrayList<>();
    protected ArrayList<GPUDeviceNameRule> mGPUDeviceNameRules =
            new ArrayList<>();

    /**
     * Directive used to manually control technique support based on the
     * inclusion or exclusion of some factor.
     */
    public enum IncludeOrExclude {
        /// Inclusive - only support if present
        INCLUDE(0),
        /// Exclusive - do not support if present
        EXCLUDE(1);

        private final int val;

        IncludeOrExclude(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }
    }

    static class GPUVendorRule {
        public GPUVendor vendor = GPUVendor.GPU_UNKNOWN;
        public IncludeOrExclude includeOrExclude = IncludeOrExclude.EXCLUDE;

        public GPUVendorRule() {

        }

        public GPUVendorRule(GPUVendor vendor, IncludeOrExclude includeOrExclude) {
            this.vendor = vendor;
            this.includeOrExclude = includeOrExclude;
        }
    }

    static class GPUDeviceNameRule {
        public String devicePattern;
        public IncludeOrExclude includeOrExclude = IncludeOrExclude.EXCLUDE;
        public boolean caseSensitive;

        public GPUDeviceNameRule() {

        }

        public GPUDeviceNameRule(String devicePattern,
                                 IncludeOrExclude includeOrExclude, boolean caseSensitive) {
            this.devicePattern = devicePattern;
            this.includeOrExclude = includeOrExclude;
            this.caseSensitive = caseSensitive;
        }
    }

    protected void clearIlluminationPasses() {

    }

    protected boolean checkManuallyOrganisedIlluminationPasses() {
        int len = mPasses.size();

        for (int i = 0; i < len; ++i) {
            if (mPasses.get(i).getIlluminationStage() == IlluminationStage.IS_UNKNOWN) {
                return false;
            }
        }

        for (int i = 0; i < len; ++i) {
            ENG_IlluminationPass iPass = new ENG_IlluminationPass();
            iPass.destroyOnShutdown = false;
            iPass.originalPass = iPass.pass = mPasses.get(i);
            iPass.stage = mPasses.get(i).getIlluminationStage();
            mIlluminationPasses.add(iPass);
        }

        return true;
    }

    public ENG_Technique(ENG_Material parent) {
        this.mParent = parent;
    }

    public ENG_Technique(ENG_Material parent, ENG_Technique oth) {

    }

    public void destroy() {
        removeAllPasses();
        clearIlluminationPasses();
    }

    public void set(ENG_Technique rhs) {
        mName = rhs.mName;
        this.mIsSupported = rhs.mIsSupported;
        this.mLodIndex = rhs.mLodIndex;
        this.mSchemeIndex = rhs.mSchemeIndex;
        this.mShadowCasterMaterial = rhs.mShadowCasterMaterial;
        this.mShadowCasterMaterialName = rhs.mShadowCasterMaterialName;
        this.mShadowReceiverMaterial = rhs.mShadowReceiverMaterial;
        this.mShadowReceiverMaterialName = rhs.mShadowReceiverMaterialName;
        this.mGPUVendorRules = rhs.mGPUVendorRules;
        this.mGPUDeviceNameRules = rhs.mGPUDeviceNameRules;

        removeAllPasses();

        int len = rhs.mPasses.size();
        for (int i = 0; i < len; ++i) {
            mPasses.add(new ENG_Pass(
                    this, rhs.mPasses.get(i).getIndex(), rhs.mPasses.get(i)));
        }
        clearIlluminationPasses();
        mIlluminationPassesCompilationPhase = IlluminationPassesState.IPS_NOT_COMPILED;
    }

    public boolean isSupported() {
        return mIsSupported;
    }

    public ENG_Pass createPass() {
        ENG_Pass p = new ENG_Pass(this, (short) mPasses.size());
        mPasses.add(p);
        return p;
    }

    public ENG_Pass getPass(short index) {
        if (index < 0 || index >= mPasses.size()) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return mPasses.get(index);
    }

    public ENG_Pass getPass(String name) {
        int len = mPasses.size();
        for (int i = 0; i < len; ++i) {
            if (mPasses.get(i).getName().equals(name)) {
                return mPasses.get(i);
            }
        }
        return null;
    }

    public short getNumPasses() {
        return (short) mPasses.size();
    }

    public void removePass(int index) {
        if (index < 0 || index >= mPasses.size()) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        mPasses.remove(index);
    }

    public void removeAllPasses() {
        for (ENG_Pass pass : mPasses) {
            pass.queueForDeletion();
        }
        mPasses.clear();
    }

    public Iterator<ENG_Pass> getPassIterator() {
        return mPasses.iterator();
    }

    public Iterator<ENG_IlluminationPass> getIlluminationPassIterator() {
        return mIlluminationPasses.iterator();
    }

    public void _notifyNeedsRecompile() {
        // Disable require to recompile when splitting illumination passes
        if (mIlluminationPassesCompilationPhase != IlluminationPassesState.IPS_COMPILE_DISABLED) {
            mParent._notifyNeedsRecompile();
        }
    }

    public boolean movePass(short sourceIndex, short destinationIndex) {
        boolean moveSuccessful = false;

        if (sourceIndex == destinationIndex) {
            return true;
        }

        if ((sourceIndex < mPasses.size()) && (destinationIndex < mPasses.size())) {
            ENG_Pass p = mPasses.remove(sourceIndex);
            mPasses.add(destinationIndex, p);

            short beginIndex, endIndex;
            if (destinationIndex > sourceIndex) {
                beginIndex = sourceIndex;
                endIndex = destinationIndex;
            } else {
                beginIndex = destinationIndex;
                endIndex = sourceIndex;
            }
            for (short index = beginIndex; index <= endIndex; ++index) {
                mPasses.get(index)._notifyIndex(index);
            }
            moveSuccessful = true;
        }
        return moveSuccessful;
    }

    public boolean isTransparent() {
        return !mPasses.isEmpty() && mPasses.get(0).isTransparent();
    }

    public boolean isTransparentSortingEnabled() {
        return mPasses.isEmpty() || mPasses.get(0).getTransparentSortingEnabled();
    }

    public boolean isTransparentSortingForced() {
        return !mPasses.isEmpty() && mPasses.get(0).getTransparentSortingEnabledForced();
    }

    public boolean isDepthWriteEnabled() {
        return !mPasses.isEmpty() && mPasses.get(0).getDepthWriteEnabled();
    }

    public boolean isDepthCheckEnabled() {
        return !mPasses.isEmpty() && mPasses.get(0).getDepthCheckEnabled();
    }

    public boolean hasColourWriteDisabled() {
        return mPasses.isEmpty() || !mPasses.get(0).getColourWriteEnabled();
    }

    public void setName(String name) {
        mName = name;
    }

    public ENG_Material getParent() {
        return mParent;
    }

    public void setLodIndex(short lod) {
        mLodIndex = lod;
        _notifyNeedsRecompile();
    }

    public short getLodIndex() {
        return mLodIndex;
    }

    public void setSchemeName(String name) {
        mSchemeIndex = ENG_MaterialManager.getSingleton()._getSchemeIndex(name);
    }

    public String getSchemeName() {
        return ENG_MaterialManager.getSingleton()._getSchemeName(mSchemeIndex);
    }

    public short _getSchemeIndex() {
        return mSchemeIndex;
    }

    public void _load() {

        for (ENG_Pass p : mPasses) {
            p._load();
        }
    }

    public void prepare() {

        for (ENG_Pass p : mPasses) {
            p._prepare();
        }
    }

    public void setLightingEnabled(boolean b) {

        for (ENG_Pass p : mPasses) {
            p.setLightingEnabled(b);
        }
    }

    public void setDepthCheckEnabled(boolean b) {

        for (ENG_Pass p : mPasses) {
            p.setDepthCheckEnabled(b);
        }
    }

    public void setSceneBlending(SceneBlendType type) {

        for (ENG_Pass p : mPasses) {
            p.setSceneBlending(type);
        }
    }

    public void setDepthWriteEnabled(boolean b) {

        for (ENG_Pass p : mPasses) {
            p.setDepthWriteEnabled(b);
        }
    }

    public void _unload() {

        for (ENG_Pass pass : mPasses) {
            pass._unload();
        }
    }


}
