/*
 * Created by Sebastian Bugiu on 08/04/2025, 16:45
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 08/04/2025, 16:45
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.artemis.Entity;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class DebrisEntityRigidBody extends EntityRigidBody {

    public DebrisEntityRigidBody(btRigidBodyConstructionInfo constructionInfo, Entity entity) {
        super(constructionInfo, entity, PhysicsEntityType.DEBRIS_ENTITY_RIGID_BODY);
        // TODO we might need to have a special debris entity rigid body added using |.
    }
}
