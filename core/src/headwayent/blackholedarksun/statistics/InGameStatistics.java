/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.statistics;

import java.util.ArrayList;

import headwayent.hotshotengine.statistics.Statistics;

public class InGameStatistics extends Statistics {

    public enum MenuSection {
        IN_MENU, IN_GAME
    }

    public long timeSpentInGame;
    public long timeSpentInMenus;
    public final ArrayList<SessionStatistics> sessionStatisticsList = new ArrayList<>();
    public final InGamePerformanceStatistics performanceStatistics = new InGamePerformanceStatistics();

    public transient long currentMenuTimeBeginTime; // Used for measuring how long we were in menus/in game.
    public transient MenuSection currentMenuSection = MenuSection.IN_MENU;

    public SessionStatistics getLatestSessionStatistics() {
        if (!sessionStatisticsList.isEmpty()) {
            return sessionStatisticsList.get(sessionStatisticsList.size() - 1);
        }
        System.out.println("No session statistics");
        return null;
    }
}
