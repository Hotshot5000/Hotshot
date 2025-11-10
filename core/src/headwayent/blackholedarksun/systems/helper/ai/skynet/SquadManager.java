/*
 * Created by Sebastian Bugiu on 22/10/2025, 17:42
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 22/10/2025, 17:42
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems.helper.ai.skynet;

import java.util.ArrayList;
import java.util.HashMap;

import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Long;

public class SquadManager {

    public static final boolean DEBUG = true;
    public static final boolean USE_SQUAD_TACTICS = true;
    private static final SquadManager squadManager = new SquadManager();
    private final ArrayList<SquadProperties> squadList = new ArrayList<>();
    private final HashMap<ENG_Integer, SquadProperties> squadMap = new HashMap<>();
    private final HashMap<ENG_Long, SquadMemberProperties> squadMemberPropertiesMap = new HashMap<>();
    private final HashMap<Integer, String> squadNameMap = new HashMap<>();

    private SquadManager() {
        squadNameMap.put(1, "Alpha");
        squadNameMap.put(2, "Beta");
        squadNameMap.put(3, "Gamma");
        squadNameMap.put(4, "Delta");
        squadNameMap.put(5, "Epsilon");
    }

    public void loadSquad(int squadId, boolean squadLeader, long id, float minDistance, float maxDistance, String squadName) {
        SquadProperties squadProperties = squadMap.get(new ENG_Integer(squadId));
        if (squadProperties == null) {
            squadProperties = new SquadProperties(squadId);
            squadMap.put(new ENG_Integer(squadId), squadProperties);
            squadList.add(squadProperties);
        }
        squadProperties.addId(id);
        if (!squadProperties.isDistanceSet()) {
            squadProperties.setMinDistanceAllowed(minDistance);
            squadProperties.setMaxDistanceAllowed(maxDistance);
            squadProperties.setDistanceSet(true);
        }
        if (squadProperties.getSquadName() == null) {
            if (squadName != null) {
                squadProperties.setSquadName(squadName);
            } else {
                String name = squadNameMap.get(squadId);
                if (name != null) {
                    squadProperties.setSquadName(name);
                } else {
                    System.out.println("squadId: " + squadId + " does not have an associated name!");
                }
            }
        }
        SquadMemberProperties squadMemberProperties = squadMemberPropertiesMap.get(new ENG_Long(id));
        if (squadMemberProperties == null) {
            squadMemberProperties = new SquadMemberProperties();
            squadMemberPropertiesMap.put(new ENG_Long(id), squadMemberProperties);
        }
        squadMemberProperties.setLeader(squadLeader);
        if (squadLeader) {
            if (squadProperties.getLeaderId() != -1) {
                throw new IllegalStateException(squadProperties.getSquadId() + " id with squad name: " +
                        squadProperties.getSquadName() + " already has leader with id: " + squadProperties.getLeaderId());
            }
            squadProperties.setLeaderId(id);
        }
    }

    public void removeSquad(int squadId) {
        SquadProperties remove = squadMap.remove(new ENG_Integer(squadId));
        if (remove == null) {
            throw new IllegalArgumentException(squadId + " is not a valid squadId!");
        }
        squadList.remove(remove);
        for (ENG_Long id : remove.getIds()) {
            SquadMemberProperties squadMemberProperties = squadMemberPropertiesMap.remove(id);
            if (squadMemberProperties == null) {
                throw new IllegalStateException(id + " is not part of squad: " + squadId);
            }
        }

    }

    public void reset() {
        squadList.clear();
        squadMap.clear();
        squadMemberPropertiesMap.clear();
    }

    public void update() {
        for (SquadProperties squadProperties : squadList) {
            squadProperties.update();
        }
    }

    public SquadProperties getSquadProperties(int squadId) {
        return getSquadProperties(new ENG_Integer(squadId));
    }

    public SquadProperties getSquadProperties(ENG_Integer squadId) {
        SquadProperties squadProperties = squadMap.get(squadId);
        if (squadProperties == null) {
            throw new IllegalArgumentException(squadId + " is not a squad!");
        }
        return squadProperties;
    }

    public SquadMemberProperties getSquadMemberProperties(long id) {
        return getSquadMemberProperties(new ENG_Long(id));
    }

    public SquadMemberProperties getSquadMemberProperties(ENG_Long id) {
        SquadMemberProperties squadMemberProperties = squadMemberPropertiesMap.get(id);
        if (squadMemberProperties == null) {
            throw new IllegalArgumentException(id + " is not a squad member!");
        }
        return squadMemberProperties;
    }

    public static SquadManager getInstance() {
        return squadManager;
    }
}
