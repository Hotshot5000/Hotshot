/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/1/16, 11:19 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.components;

import com.artemis.Component;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.TrackerProperties;

/**
 * Created by sebas on 20.11.2015.
 */
public class MultiplayerEntityTCP extends Component {

    private EntityProperties entityProperties;
    private ShipProperties shipProperties;
    private ProjectileProperties projectileProperties;
    private TrackerProperties trackerProperties;

    /**
     * For serialization.
     */
    public MultiplayerEntityTCP() {

    }

    /**
     * Only for multiplayer.
     * @param entityId
     * @param oth
     */
    public MultiplayerEntityTCP(long entityId, MultiplayerEntityTCP oth) {
//        super(entityId);
        entityProperties = new EntityProperties(entityId, oth.getEntityProperties());
        shipProperties = oth.getShipProperties();
        projectileProperties = oth.getProjectileProperties();
        trackerProperties = oth.getTrackerProperties();
    }

//    public MultiplayerEntityTCP(long entityId) {
//        super(entityId);
//    }

//    public MultiplayerEntityTCP(long entityId, String entityName) {
//        super(entityId);
//        setEntityName(entityName);
//    }

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }

    public ShipProperties getShipProperties() {
        return shipProperties;
    }

    public void setShipProperties(ShipProperties shipProperties) {
        this.shipProperties = shipProperties;
    }

    public ProjectileProperties getProjectileProperties() {
        return projectileProperties;
    }

    public void setProjectileProperties(ProjectileProperties projectileProperties) {
        this.projectileProperties = projectileProperties;
    }

    public TrackerProperties getTrackerProperties() {
        return trackerProperties;
    }

    public void setTrackerProperties(TrackerProperties trackerProperties) {
        this.trackerProperties = trackerProperties;
    }
}
