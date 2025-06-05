/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

import headwayent.blackholedarksun.HudManager;
import headwayent.hotshotengine.ENG_Ray;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

import java.util.ArrayList;

public class ENG_RaySceneQuery implements
        ENG_RaySceneQueryListener, ENG_NativePointerWithSetter {

    public ENG_RaySceneQuery(ENG_SceneManager mgr) {
//        super(mgr);
        
    }



    public int getQueryMask() {
        return mQueryMask;
    }

    public void setQueryMask(int mQueryMask) {
        this.mQueryMask = mQueryMask;
    }

    public static class RaySceneQueryResultEntry implements Comparable<RaySceneQueryResultEntry> {

        /// Distance along the ray
        public float distance;
        /// The movable, or NULL if this is not a movable result
        public ENG_Item movable;
        public btCollisionObject collisionObject;
        /// The world fragment, or NULL if this is not a fragment result
//        public WorldFragment worldFragment;

        @Override
        public int compareTo(RaySceneQueryResultEntry rhs) {

            float res = distance - rhs.distance;
            return Float.compare(res, 0.0f);
        }

    }

    protected long ptr;
    protected final ENG_Ray mRay = new ENG_Ray();
    protected boolean mSortByDistance;
    protected final ArrayList<RaySceneQueryResultEntry> mResults = new ArrayList<>();
    protected int mQueryMask = 0xFFFFFFFF;
    protected boolean nativePointerSet;

    @Override
    public long getPointer() {
        return ptr;
    }

    @Override
    public void setPointer(long ptr) {
        this.ptr = ptr;
    }

    @Override
    public boolean isNativePointerSet() {
        return nativePointerSet;
    }

    @Override
    public void setNativePointer(boolean set) {
        nativePointerSet = set;
    }

    public void setResult(RaySceneQueryResultEntry entry) {
        mResults.add(entry);
    }

    public void setRay(ENG_Ray ray) {
        mRay.set(ray);
    }

    public ENG_Ray getRay() {
        return mRay;
    }

    public void setSortByDistance(boolean sort) {
        mSortByDistance = sort;
        ENG_NativeCalls.rayQuery_SetSortByDistance(getPointer(), true, sort);
    }

    public boolean getSortByDistance() {
        return mSortByDistance;
    }

    public ArrayList<RaySceneQueryResultEntry> execute(HudManager.RaySceneQueryPair raySceneQueryPair) {
        return ENG_NativeCalls.rayQuery_Execute(raySceneQueryPair, getPointer(), true);
//        mResults.clear();
//
//        execute(this);
//
//        if (mSortByDistance) {
//            Collections.sort(mResults);
//        }
//        return mResults;
    }

//    public abstract void execute(ENG_RaySceneQueryListener listener);

    public ArrayList<RaySceneQueryResultEntry> getLastResults() {
        return mResults;
    }

    public void clearResults() {
        mResults.clear();
    }

    @Override
    public boolean queryResult(ENG_MovableObject obj, float distance) {

        // Add to internal list
        RaySceneQueryResultEntry dets = new RaySceneQueryResultEntry();
        dets.distance = distance;
//        dets.movable = obj;
//        dets.worldFragment = null;
        mResults.add(dets);
        // Continue
        return true;
    }

//    @Override
//    public boolean queryResult(WorldFragment fragment, float distance) {
//
//        // Add to internal list
//        RaySceneQueryResultEntry dets = new RaySceneQueryResultEntry();
//        dets.distance = distance;
//        dets.movable = null;
//        dets.worldFragment = fragment;
//        mResults.add(dets);
//        // Continue
//        return true;
//    }

}
