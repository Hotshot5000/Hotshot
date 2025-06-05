/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ENG_Material {

    public static final ENG_Short defaultLodIndex = new ENG_Short((short) 0);

    protected String mName;

    protected final ArrayList<ENG_Technique> mTechniques = new ArrayList<>();

    protected final ArrayList<ENG_Technique> mSupportedTechniques = new ArrayList<>();

    protected final TreeMap<ENG_Short, TreeMap<ENG_Short, ENG_Technique>> mBestTechniquesBySchemeList = new TreeMap<>();

    protected ArrayList<ENG_Float> mUserLodValues = new ArrayList<>();
    protected ArrayList<ENG_Float> mLodValues = new ArrayList<>();
    protected ENG_LodStrategy mLodStrategy;
    protected boolean mReceiveShadows;
    protected boolean mTransparencyCastsShadows;
    /// Does this material require compilation?
    protected boolean mCompilationRequired = true;
    /// Text description of why any techniques are not supported
    protected String mUnsupportedReasons;

    protected boolean mLoaded;

    protected void insertSupportedTechnique(ENG_Technique t) {
        mSupportedTechniques.add(t);
        ENG_Short schemeIndex = new ENG_Short(t.mSchemeIndex);
        TreeMap<ENG_Short, ENG_Technique> tech = mBestTechniquesBySchemeList.get(schemeIndex);
        TreeMap<ENG_Short, ENG_Technique> lodtechs;
        if (tech == null) {
            lodtechs = new TreeMap<>();
            mBestTechniquesBySchemeList.put(schemeIndex, lodtechs);
        } else {
            lodtechs = tech;
        }
        lodtechs.put(new ENG_Short(t.mLodIndex), t);
    }

    protected void clearBestTechniqueList() {
        mBestTechniquesBySchemeList.clear();
    }

    protected void applyDefaults() {

    }

    public ENG_Material(String name) {
        mName = name;
        mLodStrategy = ENG_LodStrategyManager.getSingleton().getDefaultStrategy();
        mLodValues.add(new ENG_Float(0.0f));
        applyDefaults();
    }

    public void destroy() {
        removeAllTechniques();
        unload();
    }

    public ENG_Material clone(String newName) {
        ENG_Material material = ENG_MaterialManager.getSingleton().create(newName);
        material.set(this);
        material.mName = newName;
        return material;
    }

    public void set(ENG_Material rhs) {
        mName = rhs.mName;
    /*	mGroup = rhs.mGroup;
		mCreator = rhs.mCreator;
		mIsManual = rhs.mIsManual;
		mLoader = rhs.mLoader;
	    mHandle = rhs.mHandle;
        mSize = rhs.mSize;*/
        mReceiveShadows = rhs.mReceiveShadows;
        mTransparencyCastsShadows = rhs.mTransparencyCastsShadows;

        /*mLoadingState = rhs.mLoadingState;
		mIsBackgroundLoaded = rhs.mIsBackgroundLoaded;*/

        removeAllTechniques();
        int len = rhs.mTechniques.size();
        for (int i = 0; i < len; ++i) {
            ENG_Technique t = createTechnique();
            t.set(rhs.mTechniques.get(i));
            if (rhs.mTechniques.get(i).isSupported()) {
                insertSupportedTechnique(t);
            }
        }

        mUserLodValues = rhs.mUserLodValues;
        mLodValues = rhs.mLodValues;
        mLodStrategy = rhs.mLodStrategy;
        mCompilationRequired = rhs.mCompilationRequired;
    }

    public ENG_Technique createTechnique() {
        ENG_Technique t = new ENG_Technique(this);
        mTechniques.add(t);
        mCompilationRequired = true;
        return t;
    }

    public ENG_Technique getTechnique(short index) {
        if (index < 0 || index >= mTechniques.size()) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        return mTechniques.get(index);
    }

    public ENG_Technique getTechnique(String name) {
        int len = mTechniques.size();
        for (int i = 0; i < len; ++i) {
            ENG_Technique t = mTechniques.get(i);
            if (t.mName.equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int getNumTechniques() {
        return mTechniques.size();
    }

    public ENG_Technique getSupportedTechnique(short index) {
        if (index < 0 || index >= mSupportedTechniques.size()) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        return mSupportedTechniques.get(index);
    }

    public int getNumSupportedTechniques() {
        return mSupportedTechniques.size();
    }

    public short getNumLodLevels(short schemeIndex) {
        return getNumLodLevels(new ENG_Short(schemeIndex));
    }

    public short getNumLodLevels(ENG_Short schemeIndex) {
        if (mBestTechniquesBySchemeList.isEmpty()) {
            return 0;
        }
        TreeMap<ENG_Short, ENG_Technique> it = mBestTechniquesBySchemeList.get(schemeIndex);
        if (it == null) {
            it = mBestTechniquesBySchemeList.firstEntry().getValue();
        }
        return (short) it.size();
    }

    public short getNumLodLevels(String schemeName) {
        return getNumLodLevels(ENG_MaterialManager.getSingleton()._getSchemeIndex(schemeName));
    }
	
/*	public ENG_Technique getBestTechnique() {
		return getBestTechnique(new ENG_Short((short) 0), null);
	}*/

    public ENG_Technique getBestTechnique() {
        return getBestTechnique(defaultLodIndex, null);
    }

    public ENG_Technique getBestTechnique(ENG_Short lodIndex, ENG_Renderable rend) {
        if (lodIndex.getValue() < 0) {
            throw new IllegalArgumentException("lodIndex must be > 0");
        }
        if (mSupportedTechniques.isEmpty()) {
            return null;
        }
        ENG_MaterialManager matMgr = ENG_MaterialManager.getSingleton();
        TreeMap<ENG_Short, ENG_Technique> map = mBestTechniquesBySchemeList.get(new ENG_Short(matMgr._getActiveSchemeIndex()));
        if (map == null) {
            return mBestTechniquesBySchemeList.firstEntry().getValue().firstEntry().getValue();
        }
        ENG_Technique technique = map.get(lodIndex);
        if (technique == null) {
            // Use the next LOD level up
            short ind = -1;
            for (Entry<ENG_Short, ENG_Technique> entry : map.entrySet()) {
                if (entry.getValue().getLodIndex() < lodIndex.getValue()) {
                    ind = entry.getValue().getLodIndex();
                } else {
                    // We now have the lodIndex in ind
                    break;
                }
            }
            if (ind == -1) {
                throw new ENG_InvalidFieldStateException("the lodIndices in the mBestTechniquesBySchemeList are malformed");
            }
            technique = map.get(new ENG_Short(ind));
        }
        return technique;
    }

    public void removeTechnique(short index) {
        ENG_Technique technique = mTechniques.remove(index);
        technique.destroy();
        mSupportedTechniques.clear();
        clearBestTechniqueList();
        mCompilationRequired = true;
    }

    public void removeAllTechniques() {
        for (ENG_Technique t : mTechniques) {
            t.destroy();
        }
        mTechniques.clear();
        mSupportedTechniques.clear();
        clearBestTechniqueList();
        mCompilationRequired = true;
    }

    public Iterator<ENG_Technique> getTechniqueIterator() {
        return mTechniques.iterator();
    }

    public Iterator<ENG_Technique> getSupportedTechniqueIterator() {
        return mSupportedTechniques.iterator();
    }

    public boolean isTransparent() {
        int len = mTechniques.size();
        for (int i = 0; i < len; ++i) {
            if (mTechniques.get(i).isTransparent()) {
                return true;
            }
        }

        return false;
    }
	
/*	public void clearBestTechniqueList() {
		mBestTechniquesBySchemeList.clear();
	}*/
	
	
	
/*	public ENG_Technique getBestTechnique(ENG_Short lodindex, ENG_Renderable rend) {
		return null;
	}*/

    public ENG_Technique getBestTechnique(short lodindex, ENG_Renderable rend) {
        return getBestTechnique(new ENG_Short(lodindex), rend);
    }

    public void _notifyNeedsRecompile() {

    }

    public void setLodLevels(ArrayList<ENG_Float> lodValues) {
        mLodValues.clear();
        mUserLodValues.add(new ENG_Float(0.0f));
        mLodValues.add(new ENG_Float(mLodStrategy.getBaseValue()));
        int len = lodValues.size();
        for (int i = 0; i < len; ++i) {
            mUserLodValues.add(lodValues.get(i));
            if (mLodStrategy != null) {
                mLodValues.add(new ENG_Float(mLodStrategy.transformUserValue(lodValues.get(i).getValue())));
            }
        }
    }

    public short getLodIndex(float value) {
        return mLodStrategy.getIndexMaterial(value, mLodValues);
    }

    public Iterator<ENG_Float> getLodValueIterator() {
        return mLodValues.iterator();
    }

    public ENG_LodStrategy getLodStrategy() {
        return mLodStrategy;
    }

    public void setLodStrategy(ENG_LodStrategy lodStrategy) {
        mLodStrategy = lodStrategy;
        mLodValues.add(0, new ENG_Float(mLodStrategy.getBaseValue()));
        for (int i = 1; i < mUserLodValues.size(); ++i) {
            mLodValues.add(i, new ENG_Float(mLodStrategy.transformUserValue(mUserLodValues.get(i).getValue())));
        }
    }

    public void setReceiveShadows(boolean enabled) {
        mReceiveShadows = enabled;
    }

    public boolean getReceiveShadows() {
        return mReceiveShadows;
    }

    public boolean getTransparencyCastsShadows() {

        return false;
    }

    public void compile() {
        compile(true);
    }

    public void compile(boolean autoManageTextureUnits) {
        if (!mCompilationRequired) {
            return;
        }
        mSupportedTechniques.clear();
        mBestTechniquesBySchemeList.clear();
        mUnsupportedReasons = null;

        for (ENG_Technique t : mTechniques) {
            insertSupportedTechnique(t);
        }

        //HACK 	SINCE WE DONT HAVE A RESOURCE MANAGER IMPLEMENTATION!!!!!!!!!!!
        load();

        mCompilationRequired = false;
        prepareImpl();
    }

    public void loadImpl() {
        for (ENG_Technique t : mSupportedTechniques) {
            t._load();
        }
        mLoaded = true;
    }

    public void load() {
        if (!mLoaded) {
            loadImpl();
        }
    }

    public void prepareImpl() {
        if (mCompilationRequired) {
            compile();
        }

        for (ENG_Technique t : mSupportedTechniques) {
            t.prepare();
        }
    }

    public String getName() {

        return mName;
    }

    public void setLightingEnabled(boolean b) {

        for (ENG_Technique t : mTechniques) {
            t.setLightingEnabled(b);
        }
    }

    public void setDepthCheckEnabled(boolean b) {

        for (ENG_Technique t : mTechniques) {
            t.setDepthCheckEnabled(b);
        }
    }

    public void setDepthWriteEnabled(boolean b) {

        for (ENG_Technique t : mTechniques) {
            t.setDepthWriteEnabled(b);
        }
    }

    public void setSceneBlending(SceneBlendType type) {
        for (ENG_Technique t : mTechniques) {
            t.setSceneBlending(type);
        }
    }

    public void unload() {

        _unloadImpl();
    }

    public void _unloadImpl() {
        for (ENG_Technique tech : mTechniques) {
            tech._unload();
        }
    }
}
