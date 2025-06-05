/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/21, 10:04 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class InvisibleWallRigidBody extends btRigidBody {

    private InvisibleWall invisibleWall;

    public InvisibleWallRigidBody(float mass, btMotionState motionState, btCollisionShape collisionShape, Vector3 localInertia) {
        super(mass, motionState, collisionShape, localInertia);
    }


    public InvisibleWall getInvisibleWall() {
        return invisibleWall;
    }

    public void setInvisibleWall(InvisibleWall invisibleWall) {
        this.invisibleWall = invisibleWall;
    }
}
