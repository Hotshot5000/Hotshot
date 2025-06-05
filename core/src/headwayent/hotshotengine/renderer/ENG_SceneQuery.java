/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Plane;
import headwayent.hotshotengine.ENG_Vector4D;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.LinkedList;

public class ENG_SceneQuery {

    enum WorldFragmentType {
        /// Return no world geometry hits at all
        WFT_NONE,
        /// Return pointers to convex plane-bounded regions
        WFT_PLANE_BOUNDED_REGION,
        /// Return a single intersection point (typically RaySceneQuery only)
        WFT_SINGLE_INTERSECTION,
        /// Custom geometry as defined by the SceneManager
        WFT_CUSTOM_GEOMETRY,
        /// General RenderOperation structure
        WFT_RENDER_OPERATION
    }

    public static class WorldFragment {
        public WorldFragmentType fragmentType;
        public ENG_Vector4D singleIntersection = new ENG_Vector4D(true);
        public LinkedList<ENG_Plane> planes;
        public ByteBuffer geometry;
        public ENG_RenderOperation renderOp;
    }

    protected final ENG_SceneManager mParentSceneMgr;
    protected int mQueryMask = 0xFFFFFFFF;
    protected int mQueryTypeMask = (0xFFFFFFFF & ~ENG_SceneManager.FX_TYPE_MASK)
            & ~ENG_SceneManager.LIGHT_TYPE_MASK;
    protected final EnumSet<WorldFragmentType> mSupportedWorldFragments =
            EnumSet.noneOf(WorldFragmentType.class);
    protected WorldFragmentType mWorldFragmentType = WorldFragmentType.WFT_NONE;

    public ENG_SceneQuery(ENG_SceneManager mgr) {
        mParentSceneMgr = mgr;
    }

    public int getmQueryMask() {
        return mQueryMask;
    }

    public void setQueryMask(int mQueryMask) {
        this.mQueryMask = mQueryMask;
    }

    public int getQueryTypeMask() {
        return mQueryTypeMask;
    }

    public void setQueryTypeMask(int mQueryTypeMask) {
        this.mQueryTypeMask = mQueryTypeMask;
    }

    public WorldFragmentType getWorldFragmentType() {
        return mWorldFragmentType;
    }

    public void setWorldFragmentType(WorldFragmentType mWorldFragmentType) {
        this.mWorldFragmentType = mWorldFragmentType;
    }

    public EnumSet<WorldFragmentType> getSupportedWorldFragments() {
        return mSupportedWorldFragments;
    }
}
