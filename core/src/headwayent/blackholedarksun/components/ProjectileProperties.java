/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/19/21, 4:11 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import headwayent.blackholedarksun.entitydata.WeaponData.WeaponType;

public class ProjectileProperties extends MultiplayerComponent {

    private WeaponType type;
    private transient String parentName;
//    private long parentUserId;
    // Projectile id from WeaponProperties to know which one to
    // delete from the idList
    private long id;
    private long parentId;
    private transient float distanceTraveled;
    // When a projectile is created by the client it is also controlled
    // by the client until the updates for that projectile
    // start coming from the server.
    private transient boolean updateHandledByServer;

    /**
     * Default empty constructor for Kryo serialization.
     */
    public ProjectileProperties() {

    }

    public ProjectileProperties(WeaponType type, String parentName, long parentId, long id) {
        this.type = type;
        this.parentName = parentName;
        this.parentId = parentId;
        this.id = id;
    }

    public ProjectileProperties(WeaponType type, /*long parentUserId,*/ long id) {
        this.type = type;
//        this.parentUserId = parentUserId;
        this.id = id;
    }

    public void set(ProjectileProperties oth) {

    }

    public void setType(WeaponType type) {
        this.type = type;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public WeaponType getType() {
        return type;
    }

    public String getParentName() {
        return parentName;
    }

//    public long getParentUserId() {
//        return parentUserId;
//    }
//
//    public void setParentUserId(long parentUserId) {
//        this.parentUserId = parentUserId;
//    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    public float getDistanceTraveled() {
        return distanceTraveled;
    }

    public void addToDistanceTraveled(float dist) {
        distanceTraveled += dist;
    }

    public boolean isUpdateHandledByServer() {
        return updateHandledByServer;
    }

    public void setUpdateHandledByServer() {
        if (!this.updateHandledByServer) {
            System.out.println("updateHandledByServer = true");
        }
        this.updateHandledByServer = true;
    }
}
