/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 6:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

import java.util.ArrayList;
import java.util.Iterator;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.animations.AnimationFactory;
import headwayent.blackholedarksun.animations.ShipHitWithoutRenderingAnimation;
import headwayent.blackholedarksun.components.AIProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.StaticEntityProperties;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_Utility;

/**
 * Created by sebas on 18-Sep-17.
 */

public class EntityContactListener extends ContactListener {

    /**
     * This runs on the gameLoop() thread.
     * @param colObj0
     * @param colObj1
     */
    @Override
    public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
        // Never call super since it goes recursive until stack overflow.
//        super.onContactStarted(colObj0, colObj1);

//        System.out.println("collision detected");

        try {
            if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
                System.out.println("collision detected ptr: " + colObj0.getUserPointer() + " ptr2: " + colObj1.getUserPointer());
            }

            long colObj0UserPointer = colObj0.getUserPointer();
            long colObj1UserPointer = colObj1.getUserPointer();

            boolean eDestroyed = false;
            boolean nextDestroyed = false;

            // The entity which is not the wall is always on the first position.
            if (isInvisibleWall(colObj0UserPointer)) {
                nextDestroyed = checkCollidedWithInvisibleWall(colObj1UserPointer, colObj0UserPointer, colObj1, colObj0, 0);
            }
            if (isInvisibleWall(colObj1UserPointer)) {
                eDestroyed = checkCollidedWithInvisibleWall(colObj0UserPointer, colObj1UserPointer, colObj0, colObj1, 1);
            }

            // The entity which is not the static entity is always on the first position.
            if ((!eDestroyed && !nextDestroyed) && isStaticEntityRigidBody(colObj0UserPointer)) {
                nextDestroyed = checkCollisionWithStaticEntity(colObj1UserPointer, colObj0UserPointer, colObj1, colObj0, 0);
            }
            if ((!eDestroyed && !nextDestroyed) && isStaticEntityRigidBody(colObj1UserPointer)) {
                eDestroyed = checkCollisionWithStaticEntity(colObj0UserPointer, colObj1UserPointer, colObj0, colObj1, 1);
            }

            // The entity which is not the debris entity is always on the first position.
            if ((!eDestroyed && !nextDestroyed) && isDebrisEntityRigidBody(colObj0UserPointer)) {
                /*nextDestroyed = */checkCollisionWithDebrisEntity(colObj1UserPointer, colObj0UserPointer, colObj1, colObj0, 0);
            }
            if ((!eDestroyed && !nextDestroyed) && isDebrisEntityRigidBody(colObj1UserPointer)) {
                /*eDestroyed = */checkCollisionWithDebrisEntity(colObj0UserPointer, colObj1UserPointer, colObj0, colObj1, 1);
            }

            if ((!eDestroyed && !nextDestroyed) && isEntityRigidBody(colObj0UserPointer)) {
                eDestroyed = updateCollision(colObj0UserPointer, colObj1UserPointer, colObj0, colObj1, 0);
            }
            if ((!nextDestroyed) && isEntityRigidBody(colObj1UserPointer)) {
                nextDestroyed = updateCollision(colObj1UserPointer, colObj0UserPointer, colObj1, colObj0, 1);
            }

            if (eDestroyed) {
                destroyEntity(colObj0UserPointer, colObj1UserPointer, colObj0, colObj1);
            }
            if (nextDestroyed) {
                destroyEntity(colObj1UserPointer, colObj0UserPointer, colObj1, colObj0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkCollisionWithDebrisEntity(long ePtr, long nextPtr, btCollisionObject eColObj, btCollisionObject nextColObj,
                                                       int updateCall) {
        if (!isEntityRigidBody(ePtr)) return;
        EntityRigidBody eEntityRigidBody = (EntityRigidBody) eColObj;
        Entity e = eEntityRigidBody.getEntity();

        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        ComponentMapper<EntityProperties> entityPropertiesMapper = worldManager.getEntityPropertiesComponentMapper();

        EntityProperties entityProperties = entityPropertiesMapper.get(e);

        // TODO Should we also make sounds when hitting static objects?
        WorldManager.getSingleton().playSoundBasedOnDistance(entityProperties, APP_Game.getDebrisHitSoundName(ENG_Utility.getRandom()
                .nextInt(FrameInterval.DEBRIS_HIT_SOUND + entityProperties.getEntityId(), APP_Game.DEBRIS_HIT_SOUND_NUM)));
    }

    private static boolean checkCollisionWithStaticEntity(long ePtr, long nextPtr, btCollisionObject eColObj, btCollisionObject nextColObj,
                                                int updateCall) {
        boolean entityDestroyed = false;
        if (!isEntityRigidBody(ePtr)) return false;
        EntityRigidBody eEntityRigidBody = (EntityRigidBody) eColObj;
        Entity e = eEntityRigidBody.getEntity();

        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        ComponentMapper<EntityProperties> entityPropertiesMapper = worldManager.getEntityPropertiesComponentMapper();
        ComponentMapper<ShipProperties> shipPropertiesMapper = worldManager.getShipPropertiesComponentMapper();
        ComponentMapper<ProjectileProperties> projectilePropertiesMapper = worldManager.getProjectilePropertiesComponentMapper();
        ComponentMapper<AIProperties> aIPropertiesMapper = worldManager.getAiPropertiesComponentMapper();

        EntityProperties entityProperties = entityPropertiesMapper.get(e);
        ProjectileProperties projectileProperties = projectilePropertiesMapper.getSafe(e);

        if (projectileProperties != null) {
            entityProperties.decreaseHealth(entityProperties.getHealth());
            entityDestroyed = true;
        }

        AIProperties aiComponent = aIPropertiesMapper.getSafe(e);
        if (aiComponent != null) {
            StaticEntityRigidBody nextEntityRigidBody = (StaticEntityRigidBody) nextColObj;
            Entity nextE = nextEntityRigidBody.getEntity();

            ComponentMapper<StaticEntityProperties> staticEntityPropertiesMapper = worldManager.getStaticEntityPropertiesComponentMapper();

            StaticEntityProperties staticEntityProperties = staticEntityPropertiesMapper.get(nextE);

            // Find a way to get the collision point so we know where to turn around against instead of guessing.
            // This is now handled in EntityInternalTickCallback.
        }
        return entityDestroyed;
    }

    private static boolean checkCollidedWithInvisibleWall(long ePtr, long nextPtr, btCollisionObject eColObj, btCollisionObject nextColObj,
                                                   int updateCall) {
        boolean entityDestroyed = false;
        if (!isEntityRigidBody(ePtr)) return false;
        EntityRigidBody eEntityRigidBody = (EntityRigidBody) eColObj;
        Entity e = eEntityRigidBody.getEntity();

        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        ComponentMapper<EntityProperties> entityPropertiesMapper = worldManager.getEntityPropertiesComponentMapper();
        ComponentMapper<ShipProperties> shipPropertiesMapper = worldManager.getShipPropertiesComponentMapper();
        ComponentMapper<ProjectileProperties> projectilePropertiesMapper = worldManager.getProjectilePropertiesComponentMapper();
        ComponentMapper<AIProperties> aIPropertiesMapper = worldManager.getAiPropertiesComponentMapper();

        EntityProperties entityProperties = entityPropertiesMapper.get(e);
        ProjectileProperties projectileProperties = projectilePropertiesMapper.getSafe(e);

        System.out.println("Invisible wall collided with: " + entityProperties.getNode().getName());

        if (projectileProperties != null) {
            entityProperties.decreaseHealth(entityProperties.getHealth());
            entityDestroyed = true;
        }

        ShipProperties shipProperties = shipPropertiesMapper.getSafe(e);
        AIProperties aiComponent = aIPropertiesMapper.getSafe(e);
        if (aiComponent != null) {
            // Set the AI to turn around its ship.
            InvisibleWallRigidBody invisibleWallRigidBody = (InvisibleWallRigidBody) nextColObj;
            invisibleWallRigidBody.getInvisibleWall().getWallType().getLimitReached(entityProperties.getLimitsReachedOriginal());
            System.out.println("Level limit: " + entityProperties.getLimitsReached() + " reached");
        }

        return entityDestroyed;
    }

    private static boolean updateCollision(long ePtr, long nextPtr, btCollisionObject eColObj, btCollisionObject nextColObj,
                                    int updateCall) {
        // We know we are an entity but we can't be sure about the next object.
        EntityRigidBody eEntityRigidBody = (EntityRigidBody) eColObj;
        EntityRigidBody nextEntityRigidBody = null;
        Entity e = eEntityRigidBody.getEntity();
        Entity next = null;
        if (isEntityRigidBody(nextPtr)) {
            nextEntityRigidBody = (EntityRigidBody) nextColObj;
            next = nextEntityRigidBody.getEntity();
        }

        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        ComponentMapper<EntityProperties> entityPropertiesMapper = worldManager.getEntityPropertiesComponentMapper();
        ComponentMapper<ShipProperties> shipPropertiesMapper = worldManager.getShipPropertiesComponentMapper();
        ComponentMapper<ProjectileProperties> projectilePropertiesMapper = worldManager.getProjectilePropertiesComponentMapper();
        ComponentMapper<AIProperties> aIPropertiesMapper = worldManager.getAiPropertiesComponentMapper();

        boolean entityDestroyed = false;

        // For now we have no consequence for ships hitting objects that are not entities. Right now the only such entities are the
        // invisible walls that limit the level size. The projectiles must get destroyed if they hit the level limits though.
        if (next != null) {

            EntityProperties entityProperties = entityPropertiesMapper.get(e);
            EntityProperties othEntityProp = entityPropertiesMapper.get(next);

            ShipProperties shipProperties = shipPropertiesMapper.getSafe(e);
            ShipProperties othShipProperties = shipPropertiesMapper.getSafe(next);

            ProjectileProperties projectileProperties = projectilePropertiesMapper.getSafe(e);
            ProjectileProperties othProjectileProperties = projectilePropertiesMapper.getSafe(next);

//            if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//                System.out.println("entityProperties name: " + entityProperties.getNode().getName() + " othEntity name: " + othEntityProp.getNode().getName());
//            }

            AIProperties aiComponent = aIPropertiesMapper.getSafe(e);
            if (aiComponent != null && othProjectileProperties == null) {
                aiComponent.setCollided(true);
            }

//            if (projectileProperties != null) {
//                System.out.println("Projectile: " + entityProperties.getName() + " collided");
//            }
//            if (othProjectileProperties != null) {
//                System.out.println("Projectile: " + othEntityProp.getName() + " collided");
//            }

            boolean decreaseHealth = true;
            boolean collisionResolved = true;
            boolean showHitAnimation = true;
            // Check if this projectile didn't collide with the ship that launched it
            if (projectileProperties != null && othShipProperties != null) {
                if (othEntityProp.getEntityId() == projectileProperties.getParentId()) {
                    decreaseHealth = false;
                    collisionResolved = false;
                    showHitAnimation = false;
//                    System.out.println("Projectile: " + entityProperties.getName() + " collided with mother ship: " + othEntityProp.getName());
                }
            }
            if (shipProperties != null && othProjectileProperties != null) {
                if (entityProperties.getEntityId() == othProjectileProperties.getParentId()) {
                    decreaseHealth = false;
                    collisionResolved = false;
                    showHitAnimation = false;
//                    System.out.println("Projectile: " + othEntityProp.getName() + " collided with mother ship: " + entityProperties.getName());
                }
            }
            // Projectile launched from the same ship are immune to one another
            if (projectileProperties != null && othProjectileProperties != null) {
                if (projectileProperties.getParentId() == othProjectileProperties.getParentId()) {
                    decreaseHealth = false;
                    collisionResolved = false;
                    showHitAnimation = false;
//                    System.out.println("Projectiles collided with each other: " + entityProperties.getName() + " " + othEntityProp.getName());
                }
            }

            // Check for collision between projectile and ship.
            if ((projectileProperties != null && othShipProperties != null) || (shipProperties != null && othProjectileProperties != null)) {
                ProjectileProperties projProp = projectileProperties != null ? projectileProperties : othProjectileProperties;
                EntityProperties projEntityProp = projectileProperties != null ? entityProperties : othEntityProp;
//                System.out.println("Projectile: " + projEntityProp.getUniqueName() + " collided");
            }

            if (aiComponent != null && othProjectileProperties == null) {
                decreaseHealth = false;
                showHitAnimation = false;
            }

            if (MainApp.getGame().getGameMode() == APP_Game.GameMode.MP) {
                decreaseHealth = false;
            }

//            System.out.println("entity: " + entityProperties.getUniqueName() + " decreaseHealth: " + decreaseHealth + " currentHealth: " + entityProperties.getHealth() + " isInvincible: " + entityProperties.isInvincible() + " isDestroyed: " + entityProperties.isDestroyed());

            // Check only if we have some health. It is possible to animate
            // a dying ship so that it is still in the scene and still getting hit.
            // That means we will kill it multiple times registering multiple kills.
            // Don't check if the other entity is destroyed. It will no longer destroy the projectile when an object is destroyed and set as destroyed.
            // We no longer destroy it immediately. We postpone the actual destruction until we have checked the collision from the POV of both entities.
            if (decreaseHealth && entityProperties.getHealth() > 0 && !entityProperties.isInvincible()
                    && !entityProperties.isDestroyed()/* && !othEntityProp.isDestroyed()*/) {
                // Take into account that ship collision shouldn't drain life too fast
                boolean shouldDealDamage = true;
                if (othEntityProp.isTimedDamage()) {
                    if (entityProperties.getUniqueName().equals(othEntityProp.getLastEntityTimedDamage())) {
                        if (ENG_Utility.hasTimePassed(
                                FrameInterval.UPDATE_COLLISION_TIMED_DAMAGE + entityProperties.getNode().getName()
                                        + " " + othEntityProp.getNode().getName()
                                        + " updateCall: " + updateCall,
//                                    + " sectionNum " + sectionNum
//                                    + " entityListIterator " + entitiListIterator,
                                othEntityProp.getTimedDamageCurrentTime(),
                                othEntityProp.getTimedDamageTime())) {
                            othEntityProp.setTimedDamageCurrentTime();
                        } else {
                            shouldDealDamage = false;
//						System.out.println("Ignoring damage from ship: " +
//						(shipProperties != null ? shipProperties.getName() : "") +
//						" and other ship: " +
//						(othShipProperties != null ? othShipProperties.getName() : ""));
                        }
                    } else {
//                        System.out.println("setLastEntityTimedDamage for: " + othEntityProp.getUniqueName() + " with: " + entityProperties.getUniqueName());
                        othEntityProp.setLastEntityTimedDamage(entityProperties.getUniqueName());
                        othEntityProp.setTimedDamageCurrentTime();
                    }
                }

//                System.out.println("entity: " + entityProperties.getUniqueName() + " shouldDealDamage: " + shouldDealDamage + " invincible: " + entityProperties.isInvincible());
                if (shouldDealDamage) {
//                    System.out.println("Decreasing object: " + entityProperties.getName() + " health with: " + othEntityProp.getDamage());
                    entityProperties.decreaseHealth(othEntityProp.getDamage());
//                    System.out.println(entityProperties.getName() + " entity health remaining: " + entityProperties.getHealth());
                }
                if (entityProperties.getHealth() <= 0) {
                    collisionResolved = false;
                    entityDestroyed = true;
//                    System.out.println("Entity: " + entityProperties.getName() + " destroyed");
                }

            }
            if (showHitAnimation && MainApp.getApplicationMode() == MainApp.Mode.CLIENT) {
                AnimationFactory factory = entityProperties.getHitAnimationFactory();
                if (factory != null) {
//                    if (MainApp.getApplicationMode() == MainApp.Mode.CLIENT) {
//                    WorldManager worldManager = (WorldManager) MainApp.getGame().getWorldManager();
                        if (!entityProperties.isInvincible()) {
                            if (((WorldManager) worldManager).getPlayerShipEntityId() == entityProperties.getEntityId()) {
                                if (ENG_Utility.hasTimePassed(FrameInterval.UPDATE_COLLISION_PLAYER_SHIP_HIT_ANIMATION,
                                        entityProperties.getPlayerShipHitAnimationDelay(),
                                        ShipHitWithoutRenderingAnimation.TOTAL_ANIM_TIME + 1000)) {
                                    worldManager.startAnimation(entityProperties.getEntityId(), factory.createInstance(e));
                                    entityProperties.setPlayerShipHitAnimationDelay();
//                            System.out.println("hit animation started");
                                }
                            } else {
                                worldManager.startAnimation(entityProperties.getEntityId(), factory.createInstance(e));
                            }
                        }
                        if (((WorldManager) worldManager).getPlayerShipEntityId() == entityProperties.getEntityId()) {
                            MainApp.getGame().vibrate(APP_Game.VibrationEvent.PLAYER_HIT);
                            if (othProjectileProperties != null) {
                                HudManager.getSingleton().hit(othProjectileProperties.getParentId());
                            }
                            HudManager.getSingleton().vibrate(HudManager.HudVibrationType.MISSILE_HIT);
                        }
//                    }
                }
            }
        } else {
            // Check if we are a projectile and destroy it since we have hit the level limit.
        }

        return entityDestroyed;
    }

    private static void destroyEntity(long ePtr, long nextPtr, btCollisionObject eColObj, btCollisionObject nextColObj) {

        EntityRigidBody eEntityRigidBody = (EntityRigidBody) eColObj;
        EntityRigidBody nextEntityRigidBody = null;
        Entity e = eEntityRigidBody.getEntity();
        Entity next = null;
        if (isEntityRigidBody(nextPtr)) {
            nextEntityRigidBody = (EntityRigidBody) nextColObj;
            next = nextEntityRigidBody.getEntity();
        }

        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        ComponentMapper<EntityProperties> entityPropertiesMapper = worldManager.getEntityPropertiesComponentMapper();
        ComponentMapper<ShipProperties> shipPropertiesMapper = worldManager.getShipPropertiesComponentMapper();
        ComponentMapper<ProjectileProperties> projectilePropertiesMapper = worldManager.getProjectilePropertiesComponentMapper();
        ComponentMapper<AIProperties> aIPropertiesMapper = worldManager.getAiPropertiesComponentMapper();

        EntityProperties entityProperties = entityPropertiesMapper.get(e);
        ShipProperties shipProperties = shipPropertiesMapper.getSafe(e);

//        System.out.println("Destroying entity EntityContactListener: " + entityProperties.getName());
        worldManager.destroyEntity(entityProperties);

        // Do things related to the next entity from here on so we can separate with an early exit!
        if (next == null) {
            return;
        }

        ProjectileProperties othProjectileProperties = projectilePropertiesMapper.getSafe(next);


        if (othProjectileProperties != null) {
            // A projectile killed us so we must increment the kills for the
            // firing ship
            Entity ship = worldManager.getShipByGameEntityId(othProjectileProperties.getParentId());
            if (ship != null) {
                ShipProperties component = shipPropertiesMapper.get(ship);
                component.incrementKills();
//                System.out.println(entityProperties.getName() + " kills: " + component.getKills());
            }
        }
        // If we're a ship and died also kill all the chasing projectiles
        if (shipProperties != null) {
//					System.out.println(
//							"ship " + shipProperties.getName() + " died");
            ArrayList<Long> list = new ArrayList<>();
            Iterator<Long> iterator = shipProperties.getChasingProjectilesIterator();
            while (iterator.hasNext()) {
                Long chasingProjectileName = iterator.next();
                list.add(chasingProjectileName);


            }
            // Avoid a ConcurrentModificationException when setDestroyed
            // might mess with the chasing projectiles list
            for (Long chasingProjectileId : list) {
//						System.out.println(
//								"removing projectile : " + chasingProjectileName);
                Entity projectile = worldManager.getEntityByGameEntityId(chasingProjectileId);
                // Must check as a projectile can be long dead from other
                // reasons such as max distance exceeded or another ship hit
                // by mistake
                if (projectile != null) {
                    System.out.println("Destroying entity EntityContactListener chasingProjectileId: " + entityProperties.getName());
                    entityPropertiesMapper.get(projectile).setDestroyed(true);
                }
            }
        }
    }

    public static boolean isEntityRigidBody(long userPointer) {
        return userPointer == PhysicsEntityType.ENTITY_RIGID_BODY.getType();
    }

    public static boolean isStaticEntityRigidBody(long userPointer) {
        return userPointer == PhysicsEntityType.STATIC_ENTITY_RIGID_BODY.getType();
    }

    public static boolean isDebrisEntityRigidBody(long userPointer) {
        return userPointer == PhysicsEntityType.DEBRIS_ENTITY_RIGID_BODY.getType();
    }

    public static boolean isInvisibleWall(long userPointer) {
        return userPointer == PhysicsEntityType.INVISIBLE_WALL.getType();
    }
}
