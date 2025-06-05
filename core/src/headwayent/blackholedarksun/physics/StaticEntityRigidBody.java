/*
 * Created by Sebastian Bugiu on 07/04/2025, 19:32
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 07/04/2025, 19:32
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.artemis.Entity;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class StaticEntityRigidBody extends btRigidBody {

    private final Entity entity;

    public StaticEntityRigidBody(btRigidBodyConstructionInfo constructionInfo, Entity entity) {
        super(constructionInfo);
        this.entity = entity;
        // TODO we might need to have a special static entity rigid body added using |.
        setUserPointer(PhysicsEntityType.STATIC_ENTITY_RIGID_BODY.getType());
    }

    public Entity getEntity() {
        return entity;
    }
}
