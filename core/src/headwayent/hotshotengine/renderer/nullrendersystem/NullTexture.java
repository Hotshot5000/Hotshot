/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/6/16, 9:43 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nullrendersystem;

import headwayent.hotshotengine.renderer.ENG_HardwarePixelBuffer;
import headwayent.hotshotengine.renderer.ENG_Texture;

/**
 * Created by sebas on 18.11.2015.
 */
public class NullTexture extends ENG_Texture {
    public NullTexture(String name) {
        super(name);
    }

    @Override
    public ENG_HardwarePixelBuffer getBuffer(int face, int mipmap) {
        return null;
    }

    @Override
    public void createInternalResourcesImpl() {

    }

    @Override
    public void freeInternalResourceImpl(boolean skipGLDelete) {

    }

    @Override
    public void prepareImpl() {

    }

    @Override
    public void loadImpl() {

    }
}
