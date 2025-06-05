/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.rmi;

import headwayent.blackholedarksun.world.WorldManagerServerSide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sebas on 26.01.2016.
 */
public class UserStatsListImpl implements UserStatsList {

    private final HashMap<Long, UserStats> userStatsMap = new HashMap<>();

    public UserStatsListImpl() {

    }

    public UserStatsListImpl(UserStatsListImpl userStatsList) {
        set(userStatsList);
    }

    public void set(UserStatsListImpl userStatsList) {
        ArrayList<UserStats> list = userStatsList.getList();
        for (UserStats userStats : list) {
            this.userStatsMap.put(userStats.getUserId(), new UserStats(userStats));
        }
    }

    public void addUserStats(UserStats userStats) {
//        System.out.println("Adding userstats");
        userStatsMap.put(userStats.getUserId(), userStats);
    }

    public void clearUserStats() {
//        System.out.println("Clearing userstats list");
        userStatsMap.clear();
    }

    @Override
    public ArrayList<UserStats> getList() {
        WorldManagerServerSide worldManagerServerSide = WorldManagerServerSide.getSingleton();
        ReentrantLock userStatsUpdaterSystemLock = worldManagerServerSide.getUserStatsUpdaterSystemLock();
        userStatsUpdaterSystemLock.lock();
        try {
            //                System.out.println(userStats.toString());
            ArrayList<UserStats> userStatsList = new ArrayList<>(userStatsMap.values());
            Collections.sort(userStatsList);
            return userStatsList;
        } finally {
            userStatsUpdaterSystemLock.unlock();
        }
    }
}
