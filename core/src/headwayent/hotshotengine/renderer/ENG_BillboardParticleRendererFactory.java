/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

@Deprecated
public class ENG_BillboardParticleRendererFactory extends
        ENG_ParticleSystemRendererFactory {

    public ENG_BillboardParticleRendererFactory() {

    }

    @Override
    public String getType() {
        
        return "billboard";
    }

    /** @noinspection deprecation*/
    @Override
    public ENG_ParticleSystemRenderer createInstance(String name) {
        
        return new ENG_BillboardParticleRenderer();
    }

    @Override
    public void destroyInstance(ENG_ParticleSystemRenderer instance) {
        

    }

}
