/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/1/17, 6:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

/**
 * Created by sebas on 02.01.2016.
 */
public abstract class MultiplayerEntityProcessingSystem extends EntityProcessingSystem {

//    private ComponentMapper<EntityProperties> entityPropertiesMapper;
//    protected final HashMap<Long, Entity> entityByIdMap = new HashMap<>();
//    protected final ArrayList<Long> entityIdsToRemove = new ArrayList<>();

    public MultiplayerEntityProcessingSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    @Override
    public void inserted(Entity e) {
        super.inserted(e);
//        EntityProperties entityProperties = entityPropertiesMapper.get(e);
//        Entity put = entityByIdMap.put(entityProperties.getEntityId(), e);
//        if (put != null) {
//            throw new IllegalArgumentException("Entity with entityId: " + entityProperties.getEntityId() + " with name: " + entityProperties.getName()
//            + " has already been added");
//        }
    }

    @Override
    public void removed(Entity e) {
//        EntityProperties entityProperties = entityPropertiesMapper.get(e);
//        Entity remove = entityByIdMap.remove(entityProperties.getEntityId());
//        if (remove == null) {
//            throw new IllegalArgumentException("Entity with entityId: " + entityProperties.getEntityId() + " with name: " + entityProperties.getName()
//                    + " is missing");
//        }
//        entityIdsToRemove.add(entityProperties.getEntityId());
        super.removed(e);
    }
}
