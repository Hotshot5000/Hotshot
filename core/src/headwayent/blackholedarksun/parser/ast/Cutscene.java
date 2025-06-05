/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import java.util.ArrayList;

//import java_cup.Lexer;


public class Cutscene {

    public enum CutsceneType {
        LEVEL_BEGINNING,
        DURING_LEVEL,
        LEVEL_ENDING,
        STORY
    }

    private final String name;
    private final ArrayList<Event> eventList;
    private InitialConds initialConds;
    private final ArrayList<ObjectEvent> objectEventList = new ArrayList<>();
    private final ArrayList<CameraEvent> cameraEventList = new ArrayList<>();
    private final ArrayList<CameraAttachEvent> cameraAttachEventList = new ArrayList<>();
    private final ArrayList<CameraDetachEvent> cameraDetachEventList = new ArrayList<>();
    private final ArrayList<ParallelTask> parallelTaskList = new ArrayList<>();
    private final ArrayList<Event> inGameEventList = new ArrayList<>();
    private final ArrayList<Event> currentEventList = new ArrayList<>();
    private boolean useSkyboxDataFromLevel;

    public Cutscene(String name, ArrayList<Event> eventList) {
        this.name = name;
        this.eventList = eventList;
    }

    public void init() {
        boolean initialCondsRead = false;
        for (Event event : eventList) {
            if (event.name.equalsIgnoreCase(InitialConds.TYPE)) {
                initialConds = (InitialConds) event;
                initialConds.init();
                initialCondsRead = true;
                continue;
            } else if (event.name.equalsIgnoreCase(ObjectEvent.TYPE)) {

            } else if (event.name.equalsIgnoreCase(CameraEvent.TYPE)) {

            } else if (event.name.equalsIgnoreCase(CameraAttachEvent.TYPE)) {

            } else if (event.name.equalsIgnoreCase(CameraDetachEvent.TYPE)) {

            } else if (event.name.equalsIgnoreCase(ParallelTask.TYPE)) {

            }
            event.init();
            if (initialCondsRead) {
                inGameEventList.add(event);
            }
        }

    }

    public String getName() {
        return name;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public boolean isUseSkyboxDataFromLevel() {
        return useSkyboxDataFromLevel;
    }

    public void setUseSkyboxDataFromLevel(boolean useSkyboxDataFromLevel) {
        this.useSkyboxDataFromLevel = useSkyboxDataFromLevel;
    }

    public InitialConds getInitialConds() {
        return initialConds;
    }

    public ArrayList<Event> getInGameEventList() {
        return inGameEventList;
    }
}
