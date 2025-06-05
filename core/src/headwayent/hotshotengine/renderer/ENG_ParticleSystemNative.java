/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 24.06.2017.
 */

public class ENG_ParticleSystemNative extends ENG_AttachableObject {

    private String materialName;

    public ENG_ParticleSystemNative(long id, String name, String templateName) {
        this.id = id;
        this.name = name;
        ENG_NativeCalls.sceneManager_createParticleSystem(this, templateName);
    }

    public ENG_ParticleSystemNative(long id, String name, String templateName, int quota) {
        this.id = id;
        this.name = name;
        ENG_NativeCalls.sceneManager_createParticleSystem(this, templateName);
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
        ENG_NativeCalls.particleSystem_setMaterialName(this, materialName);
    }

    public void destroy() {
        ENG_NativeCalls.sceneManager_destroyParticleSystem(this);
        destroyed = true;
    }
}
