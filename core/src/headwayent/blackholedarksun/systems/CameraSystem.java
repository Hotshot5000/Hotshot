/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.components.CameraProperties.CameraType;
import headwayent.hotshotengine.renderer.ENG_Camera;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

public class CameraSystem extends EntityProcessingSystem {

    private ComponentMapper<CameraProperties> cameraPropertiesMapper;
    private CameraType currentType;

    public CameraSystem(ENG_Camera camera) {
        super(Aspect.all(CameraProperties.class));

    }

    @Override
    protected void process(Entity e) {


        CameraType type = cameraPropertiesMapper.get(e).getType();
        if (currentType == null) {
            currentType = type;
        } else {
            if (currentType != type) {

            }
        }
    }

}
