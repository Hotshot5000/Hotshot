/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;
import java.util.Iterator;

public class ENG_CompositionTargetPass {

    /**
     * Input mode of a TargetPass
     */
    public enum InputMode {
        IM_NONE,        // No input
        IM_PREVIOUS     // Output of previous Composition in chain
    }

    /// Parent technique
    private final ENG_CompositionTechnique mParent;
    /// Input name
    private InputMode mInputMode = InputMode.IM_NONE;
    /// (local) output texture
    private String mOutputName;
    /// Passes
    private final ArrayList<ENG_CompositionPass> mPasses =
            new ArrayList<>();
    /// This target pass is only executed initially after the effect
    /// has been enabled.
    private boolean mOnlyInitial;
    /// Visibility mask for this render
    private int mVisibilityMask = 0xFFFFFFFF;
    /// LOD bias of this render
    private float mLodBias = 1.0f;
    /// Material scheme name
    private String mMaterialScheme = ENG_MaterialManager.DEFAULT_SCHEME_NAME;
    /// Shadows option
    private boolean mShadowsEnabled = true;

    public ENG_CompositionTargetPass(ENG_CompositionTechnique parent) {
        mParent = parent;
    }

    public void setInputMode(InputMode m) {
        mInputMode = m;
    }

    public InputMode getInputMode() {
        return mInputMode;
    }

    public void setOutputName(String name) {
        mOutputName = name;
    }

    public String getOutputName() {
        return mOutputName;
    }

    public void setOnlyInitial(boolean b) {
        mOnlyInitial = b;
    }

    public boolean getOnlyInitial() {
        return mOnlyInitial;
    }

    public void setVisibilityMask(int mask) {
        mVisibilityMask = mask;
    }

    public int getVisibilityMask() {
        return mVisibilityMask;
    }

    public void setLodBias(float b) {
        mLodBias = b;
    }

    public float getLodBias() {
        return mLodBias;
    }

    public void setMaterialScheme(String scheme) {
        mMaterialScheme = scheme;
    }

    public String getMaterialScheme() {
        return mMaterialScheme;
    }

    public void setShadowsEnabled(boolean b) {
        mShadowsEnabled = b;
    }

    public boolean getShadowsEnabled() {
        return mShadowsEnabled;
    }

    public ENG_CompositionPass createPass() {
        ENG_CompositionPass pass = new ENG_CompositionPass(this);
        mPasses.add(pass);
        return pass;
    }

    public void removePass(int index) {
        if (index < 0 || index >= mPasses.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        mPasses.remove(index);
    }

    public ENG_CompositionPass getPass(int index) {
        if (index < 0 || index >= mPasses.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        return mPasses.get(index);
    }

    public int getNumPasses() {
        return mPasses.size();
    }

    public void removeAllPasses() {
        mPasses.clear();
    }

    public Iterator<ENG_CompositionPass> getPassIterator() {
        return mPasses.iterator();
    }

    public ENG_CompositionTechnique getParent() {
        return mParent;
    }

    public boolean _isSupported() {
        for (ENG_CompositionPass pass : mPasses) {
            if (!pass._isSupported()) {
                return false;
            }
        }
        return true;
    }
}
