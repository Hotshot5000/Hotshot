/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.renderer.ENG_Common.FilterOptions;
import headwayent.hotshotengine.renderer.ENG_Common.FilterType;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ENG_MaterialManager {

//    private static ENG_MaterialManager materialManager;

    /// Default Texture filtering - minification
    protected FilterOptions mDefaultMinFilter;
    /// Default Texture filtering - magnification
    protected FilterOptions mDefaultMagFilter;
    /// Default Texture filtering - mipmapping
    protected FilterOptions mDefaultMipFilter;
    /// Default Texture anisotropy
    protected int mDefaultMaxAniso;

    protected ENG_Material mDefaultSettings;

    protected final TreeMap<String, ENG_Short> mSchemes = new TreeMap<>();

    /// Current material scheme
    protected String mActiveSchemeName;
    /// Current material scheme
    protected short mActiveSchemeIndex;

    public static final String DEFAULT_SCHEME_NAME = "Default";

    private final HashMap<String, ENG_Material> materialList = new HashMap<>();

    protected ENG_Material createImpl(String name) {
        ENG_Material material = new ENG_Material(name);
        ENG_Material put = materialList.put(name, material);
        if (put != null) {
            put.destroy();
        }
        return material;
    }

    public ENG_Material getByName(String name) {
        return materialList.get(name);
    }

    public ENG_MaterialManager() {
//        if (materialManager == null) {
//            materialManager = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        materialManager = this;

        mDefaultMinFilter = FilterOptions.FO_LINEAR;
        mDefaultMagFilter = FilterOptions.FO_LINEAR;
        mDefaultMipFilter = FilterOptions.FO_POINT;
        mDefaultMaxAniso = 1;

        // Default scheme
        mActiveSchemeIndex = 0;
        mActiveSchemeName = DEFAULT_SCHEME_NAME;
        mSchemes.put(mActiveSchemeName, null);

        // Should be somewhere else as neccessary..but for now let it here in
        // constructor
        // Moved after initializing the render root.
//        initialise();
    }

    public void setDefaultAnisotropy(int maxAniso) {
        mDefaultMaxAniso = maxAniso;
    }

    public int getDefaultAnisotropy() {
        return mDefaultMaxAniso;
    }

    public void setDefaultTextureFiltering(FilterType ftype, FilterOptions opts) {
        switch (ftype) {
            case FT_MIN:
                mDefaultMinFilter = opts;
                break;
            case FT_MAG:
                mDefaultMagFilter = opts;
                break;
            case FT_MIP:
                mDefaultMipFilter = opts;
                break;
        }
    }

    public void setDefaultTextureFiltering(FilterOptions minFilter,
                                           FilterOptions magFilter, FilterOptions mipFilter) {
        mDefaultMinFilter = minFilter;
        mDefaultMagFilter = magFilter;
        mDefaultMipFilter = mipFilter;
    }

    public FilterOptions getDefaultTextureFiltering(FilterType ftype) {
        switch (ftype) {
            case FT_MIN:
                return mDefaultMinFilter;
            case FT_MAG:
                return mDefaultMagFilter;
            case FT_MIP:
                return mDefaultMipFilter;
        }
        // to keep compiler happy
        return mDefaultMinFilter;
    }

    public short _getSchemeIndex(String schemeName) {
        short ret;
        ENG_Short it = mSchemes.get(schemeName);
        if (it != null) {
            ret = it.getValue();
        } else {
            ret = (short) mSchemes.size();
            mSchemes.put(schemeName, new ENG_Short(ret));
        }
        return ret;
    }

    public String _getSchemeName(short index) {
        for (Entry<String, ENG_Short> entry : mSchemes.entrySet()) {
            if (entry.getValue().getValue() == index) {
                return entry.getKey();
            }
        }
        return DEFAULT_SCHEME_NAME;
    }

    public short _getActiveSchemeIndex() {
        return mActiveSchemeIndex;
    }

    public String getActiveScheme() {
        return mActiveSchemeName;
    }

    public void setActiveScheme(String schemeName) {
        // Allow the creation of new scheme indexes on demand
        // even if they're not specified in any Technique
        mActiveSchemeIndex = _getSchemeIndex(schemeName);
        mActiveSchemeName = schemeName;
    }

    public static ENG_MaterialManager getSingleton() {
//        if (materialManager == null) {
//            if (MainActivity.isDebugmode()) {
//                throw new NullPointerException();
//            }
//        }
//        return materialManager;
        return MainApp.getGame().getRenderRoot().getMaterialManager();
    }

    public void initialise() {
        
        mDefaultSettings = create("DefaultSettings");
        mDefaultSettings.createTechnique().createPass();
        mDefaultSettings.setLodStrategy(
                ENG_LodStrategyManager.getSingleton().getDefaultStrategy());

        create("BaseWhite");
        ENG_Material material = create("BaseWhiteNoLighting");
        material.setLightingEnabled(false);
    }


    public ENG_Material create(String name) {
        return createImpl(name);
    }

    public void remove(String name) {
        

        ENG_Material remove = materialList.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " is not a valid material name");
        }
        remove.destroy();
//		remove.unload();
    }


}
