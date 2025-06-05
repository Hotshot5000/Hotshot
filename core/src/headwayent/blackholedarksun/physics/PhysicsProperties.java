/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

/**
 * Created by sebas on 13-Sep-17.
 */

public class PhysicsProperties {

    public enum CollisionGroup {
        NOTHING(0),
        STANDARD(1),
        TRANSPARENT(1 << 1);

        private final short val;

        CollisionGroup(int i) {
            val = (short) i;
        }

        public short getVal() {
            return val;
        }
    }

    public enum CollisionMask {
        STANDARD(CollisionGroup.STANDARD.getVal()),
        TRANSPARENT(CollisionGroup.TRANSPARENT.getVal() | CollisionGroup.STANDARD.getVal());

        private final short val;

        CollisionMask(int val) {
            this.val = (short) val;
        }

        public short getVal() {
            return val;
        }
    }

    public enum RigidBodyType {
        ENTITY, DEBRIS;
    }

    public enum CollisionShape {
        BOX((byte) 0),
        CAPSULE((byte) 1),
        CAPSULE_X((byte) 2),
        CAPSULE_Z((byte) 3),
        CYLINDER((byte) 4),
        BVH_TRIANGLE_MESH((byte) 5);

        private byte collisionShape;

        CollisionShape(byte shape) {
            collisionShape = shape;
        }

        public byte getCollisionShape() {
            return collisionShape;
        }

        public static CollisionShape getCollisionShape(String shape) {
            if (shape.equalsIgnoreCase("box")) {
                return BOX;
            } else if (shape.equalsIgnoreCase("capsule")) {
                return CAPSULE;
            } else if (shape.equalsIgnoreCase("capsule_x")) {
                return CAPSULE_X;
            } else if (shape.equalsIgnoreCase("capsule_z")) {
                return CAPSULE_Z;
            } else if (shape.equalsIgnoreCase("cylinder")) {
                return CYLINDER;
            } else if (shape.equalsIgnoreCase("bvh_triangle_mesh")) {
                return BVH_TRIANGLE_MESH;
            }
            throw new IllegalArgumentException(shape + " is not a valid shape!");
        }
    }
}
