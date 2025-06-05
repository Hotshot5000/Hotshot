/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.compositor.SceneCompositor;
import headwayent.blackholedarksun.world.WorldManager;

import com.artemis.Entity;

public class ShipHitAnimation extends ShipHitWithoutRenderingAnimation {

    public ShipHitAnimation(String name, Entity entity) {
        super(name, TOTAL_ANIM_TIME);

        WorldManager worldManager = WorldManager.getSingleton();
        CameraProperties cameraProp = worldManager.getCameraPropertiesComponentMapper().getSafe(entity);
        if (cameraProp == null) {
            throw new IllegalArgumentException("Entity " + worldManager.getEntityPropertiesComponentMapper().get(entity).getItem().getName() +
                    " is not a playership since it doesn't have a camera property");
        }
    }

    @Override
    public void start() {
        SceneCompositor.getSingleton().addColoredCompositor(SceneCompositor.CompositorColor.RED, SceneCompositor.redCompositorId, TOTAL_ANIM_TIME);
        super.start();
    }

    @Override
    public void update() {
    }

    @Override
    public void animationFinished() {
        destroyResources();
    }

    @Override
    public void reloadResources() {
    }

    @Override
    public void destroyResourcesImpl() {
        SceneCompositor.getSingleton().removeColoredCompositor(SceneCompositor.redCompositorId);
    }

}
