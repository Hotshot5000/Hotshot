/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.blackholedarksun.entitydata.ShipData;

public class Team extends ObjectDefinitionParam {

    public static final String TYPE = "Team";

    private final ShipData.ShipTeam shipTeam;

    public Team(int i) {
        super(TYPE);
        this.shipTeam = ShipData.ShipTeam.getTeamByNum(i);
    }

    public ShipData.ShipTeam getShipTeam() {
        return shipTeam;
    }
}
