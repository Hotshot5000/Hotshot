/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;

import java.util.ArrayList;

public abstract class ENG_MultiRenderTarget extends ENG_RenderTarget {

    protected final ArrayList<ENG_RenderTexture> mBoundSurfaces =
            new ArrayList<>();

    protected abstract void bindSurfaceImpl(int attachment, ENG_RenderTexture target);

    protected abstract void unbindSurfaceImpl(int attachment);

    public ENG_MultiRenderTarget(String name) {
        mPriority = OGRE_REND_TO_TEX_RT_GROUP;
        mName = name;
        /// Width and height is unknown with no targets attached
        mWidth = mHeight = 0;
    }

    public void bindSurface(int attachment, ENG_RenderTexture target) {
        for (int i = mBoundSurfaces.size(); i <= attachment; ++i) {
            mBoundSurfaces.add(null);
        }
        mBoundSurfaces.add(attachment, target);
        bindSurfaceImpl(attachment, target);
    }

    public void unbindSurface(int attachment) {
        if (attachment < mBoundSurfaces.size()) {
            mBoundSurfaces.add(attachment, null);
        }
        unbindSurfaceImpl(attachment);
    }

    public PixelFormat suggestPixelFormat() {
        return PixelFormat.PF_UNKNOWN;
    }

    public ArrayList<ENG_RenderTexture> getBoundSurfaceList() {
        return mBoundSurfaces;
    }

    public ENG_RenderTexture getBoundSurface(int index) {
        if (index >= mBoundSurfaces.size()) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        return mBoundSurfaces.get(index);
    }

    public void copyContentsToMemory(ENG_PixelBox dst, FrameBuffer buffer) {
        throw new UnsupportedOperationException("Cannot get MultiRenderTargets pixels");
    }
}
