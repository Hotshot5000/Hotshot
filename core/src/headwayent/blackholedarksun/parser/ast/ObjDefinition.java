/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import java.util.HashMap;
import java.util.Map;

import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.hotshotengine.ENG_Vector3D;

public class ObjDefinition extends InitialCondParam {

    public static final String TYPE = "ObjDefinition";
    private final String objName;
    private final HashMap<String, ObjectDefinitionParam> map;
    private MeshName meshName;
    private ObjType objType;
    private ObjPosition position;
    private Orientation orientation;
    private Speed speed;
    private Ai ai;
    private Team team;
    private Health health;
    private Behavior behavior;

    public ObjDefinition(String name, HashMap<String, ObjectDefinitionParam> map) {
        super(TYPE);
        this.objName = name;
        this.map = map;
    }

    @Override
    public void init() {
        super.init();
        for (Map.Entry<String, ObjectDefinitionParam> entry : map.entrySet()) {
            String s = entry.getKey();
            ObjectDefinitionParam param = entry.getValue();
            if (s.equalsIgnoreCase(MeshName.TYPE)) {
                meshName = (MeshName) param;
                param.init();
            } else if (s.equalsIgnoreCase(ObjType.TYPE)) {
                objType = (ObjType) param;
                param.init();
            } else if (s.equalsIgnoreCase(ObjPosition.TYPE)) {
                position = (ObjPosition) param;
                param.init();
            } else if (s.equalsIgnoreCase(Orientation.TYPE)) {
                orientation = (Orientation) param;
                param.init();
            } else if (s.equalsIgnoreCase(Speed.TYPE)) {
                speed = (Speed) param;
                param.init();
            } else if (s.equalsIgnoreCase(Ai.TYPE)) {
                ai = (Ai) param;
                param.init();
            } else if (s.equalsIgnoreCase(Team.TYPE)) {
                team = (Team) param;
                param.init();
            } else if (s.equalsIgnoreCase(Health.TYPE)) {
                health = (Health) param;
                param.init();
            } else if (s.equalsIgnoreCase(Behavior.TYPE)) {
                behavior = (Behavior) param;
                param.init();
            }
        }

        if (speed == null) {
            speed = new Speed(new ENG_Vector3D());
        }
        if (behavior == null) {
            behavior = new Behavior("neutral");
        }

    }

    public LevelObject getAsLevelObject() {
        LevelObject levelObject = new LevelObject();
        getAsLevelObject(levelObject);
        return levelObject;
    }

    public void getAsLevelObject(LevelObject obj) {
        obj.name = objName;
        obj.meshName = meshName.getMeshName();
        obj.type = objType.getShipType();
        obj.position.set(position.getPosition());
        obj.orientation.set(orientation.getOrientation());
        obj.velocity.set(speed.getSpeed());
        obj.ai = ai.isEnabled();
        obj.friendly = team.getShipTeam();
        obj.health = health.getHealth();
        obj.behavior = behavior.getBehavior();
    }

    public String getObjName() {
        return objName;
    }

    public HashMap<String, ObjectDefinitionParam> getMap() {
        return map;
    }

    public MeshName getMeshName() {
        return meshName;
    }

    public ObjType getObjType() {
        return objType;
    }

    public ObjPosition getPosition() {
        return position;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Speed getSpeed() {
        return speed;
    }

    public Ai getAi() {
        return ai;
    }

    public Team getTeam() {
        return team;
    }

    public Health getHealth() {
        return health;
    }

    public Behavior getBehavior() {
        return behavior;
    }
}
