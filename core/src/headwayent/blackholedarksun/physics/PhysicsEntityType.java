/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

/**
 * Created by sebas on 05-Oct-17.
 */

enum PhysicsEntityType {
    ENTITY_RIGID_BODY(1),
    STATIC_ENTITY_RIGID_BODY(2),
    DEBRIS_ENTITY_RIGID_BODY(4),
    INVISIBLE_WALL(8);

    private final int type;

    PhysicsEntityType(int i) {
        type = i;
    }

    public int getType() {
        return type;
    }

    public static PhysicsEntityType getType(long userPointer) {
        if (userPointer == ENTITY_RIGID_BODY.getType()) {
            return ENTITY_RIGID_BODY;
        } else if (userPointer == STATIC_ENTITY_RIGID_BODY.getType()) {
            return STATIC_ENTITY_RIGID_BODY;
        } else if (userPointer == DEBRIS_ENTITY_RIGID_BODY.getType()) {
            return DEBRIS_ENTITY_RIGID_BODY;
        } else if (userPointer == INVISIBLE_WALL.getType()) {
            return INVISIBLE_WALL;
        }
        throw new IllegalArgumentException(userPointer + " is not a valid physics entity type!");
    }
}
