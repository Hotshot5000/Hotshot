/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.parser.dispatchers.AbstractEventDispatcher;

public class ObjectEvent extends DelayedEvent {

    public static final String TYPE = "ObjectEvent";
    private final HashMap<String, ArrayList<Param>> map;
    private Position position;
    private GameObject gameObject;
    private SetSpeed setSpeed;
    private ChangeSpeed changeSpeed;
    private ChangePosition changePosition;
    private ChangeOrientation changeOrientation;
    private CompletionTime completionTime;
    private final ArrayList<ObjDefinition> objDefinitionList = new ArrayList<>();
    private final ArrayList<Spawn> spawnList = new ArrayList<>();
    private final ArrayList<Exit> exitList = new ArrayList<>();

    public ObjectEvent(HashMap<String, ArrayList<Param>> map) {
        super(TYPE);
        this.map = map;
    }

    @Override
    public void init() {
        super.init();
        for (Map.Entry<String, ArrayList<Param>> entry : map.entrySet()) {
            String s = entry.getKey();
            Param param0 = entry.getValue().get(0);
            if (s.equalsIgnoreCase(Position.TYPE)) {
                position = (Position) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(GameObject.TYPE)) {
                gameObject = (GameObject) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(SetSpeed.TYPE)) {
                setSpeed = (SetSpeed) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(ChangeSpeed.TYPE)) {
                changeSpeed = (ChangeSpeed) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(ChangePosition.TYPE)) {
                changePosition = (ChangePosition) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(ChangeOrientation.TYPE)) {
                changeOrientation = (ChangeOrientation) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(CompletionTime.TYPE)) {
                completionTime = (CompletionTime) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(DelayStart.TYPE)) {
                delayStart = (DelayStart) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(DelayEnd.TYPE)) {
                delayEnd = (DelayEnd) param0;
                param0.init();
            } else if (s.equalsIgnoreCase(ObjDefinition.TYPE)) {
                for (Param param : entry.getValue()) {
                    objDefinitionList.add((ObjDefinition) param);
                    param.init();
                }
            } else if (s.equalsIgnoreCase(Spawn.TYPE)) {
                spawnList.add((Spawn) param0);
                param0.init();
            } else if (s.equalsIgnoreCase(Exit.TYPE)) {
                exitList.add((Exit) param0);
                param0.init();
            }
        }

    }

    private ObjDefinition isObj(String name) {
        for (ObjDefinition objDefinition : objDefinitionList) {
            if (objDefinition.getObjName().equals(name)) {
                return objDefinition;
            }
        }
        return null;
    }

    public LevelEvent getAsLevelEvent() {
        LevelEvent levelEvent = new LevelEvent();
        getAsLevelEvent(levelEvent);
        return levelEvent;
    }

    public LevelEvent getAsLevelEvent(ArrayList<ObjDefinition> objDefinitionList) {
        LevelEvent levelEvent = new LevelEvent();
        getAsLevelEvent(levelEvent, objDefinitionList);
        return levelEvent;
    }

    public void getAsLevelEvent(LevelEvent levelEvent) {
        getAsLevelEvent(levelEvent, this.objDefinitionList);
    }

    public void getAsLevelEvent(LevelEvent levelEvent, ArrayList<ObjDefinition> objDefinitionList) {
        for (ObjDefinition objDefinition : objDefinitionList) {
            for (Spawn spawn : spawnList) {
                if (isObj(spawn.getSpawnName()) != null) {
                    levelEvent.spawn.add(objDefinition.getAsLevelObject());
                }
            }
            for (Exit exit : exitList) {
                if (isObj(exit.getExitName()) != null) {
                    levelEvent.exitObjects.add(objDefinition.getObjName());
                }
            }

        }
    }

    @Override
    public boolean accept(AbstractEventDispatcher dispatcher) {
        return dispatcher.dispatch(this);
    }

    public HashMap<String, ArrayList<Param>> getMap() {
        return map;
    }

    public Position getPosition() {
        return position;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public SetSpeed getSetSpeed() {
        return setSpeed;
    }

    public ChangeSpeed getChangeSpeed() {
        return changeSpeed;
    }

    public ChangePosition getChangePosition() {
        return changePosition;
    }

    public ChangeOrientation getChangeOrientation() {
        return changeOrientation;
    }

    public CompletionTime getCompletionTime() {
        return completionTime;
    }

    public ArrayList<ObjDefinition> getObjDefinitionList() {
        return objDefinitionList;
    }

    public ArrayList<Spawn> getSpawnList() {
        return spawnList;
    }

    public ArrayList<Exit> getExitList() {
        return exitList;
    }
}
