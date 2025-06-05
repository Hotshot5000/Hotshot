/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public abstract class ENG_LodListener {

    static class MovableObjectLodChangedEvent {
        public ENG_MovableObject movableObject;
        public ENG_Camera camera;
    }

    static class EntityMeshLodChangedEvent {
        /** @noinspection deprecation*/
        public ENG_Entity entity;
        public ENG_Camera camera;
        public float lodValue;

        public short previousLodIndex;
        public short newLodIndex;
    }

    static class EntityMaterialLodChangedEvent {
        public ENG_SubEntity subEntity;
        public ENG_Camera camera;

        public float lodValue;

        public short previousLodIndex;
        public short newLodIndex;
    }

    public ENG_LodListener() {

    }

    public abstract boolean prequeueMovableObjectLodChanged(MovableObjectLodChangedEvent evt);

    public abstract boolean postqueueMovableObjectLodChanged(MovableObjectLodChangedEvent evt);

    public abstract boolean prequeueEntityMeshLodChanged(EntityMeshLodChangedEvent evt);

    public abstract boolean postqueueEntityMeshLodChanged(EntityMeshLodChangedEvent evt);

    public abstract boolean prequeueEntityMaterialLodChanged(EntityMaterialLodChangedEvent evt);

    public abstract boolean postqueueEntityMaterialLodChanged(EntityMaterialLodChangedEvent evt);
}
