/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.artemis.Entity;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

/**
 * Created by sebas on 05-Oct-17.
 */

public class EntityRigidBody extends btRigidBody {

    private final Entity entity;

    public EntityRigidBody(btRigidBodyConstructionInfo constructionInfo, Entity entity) {
        super(constructionInfo);
        this.entity = entity;
        setUserPointer(PhysicsEntityType.ENTITY_RIGID_BODY.getType());
    }

    EntityRigidBody(btRigidBodyConstructionInfo constructionInfo, Entity entity, PhysicsEntityType type) {
        super(constructionInfo);
        this.entity = entity;
        setUserPointer(type.getType());
    }

    public Entity getEntity() {
        return entity;
    }
}
