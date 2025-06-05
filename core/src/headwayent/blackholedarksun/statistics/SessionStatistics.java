/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/2/21, 11:05 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.statistics;

import java.util.ArrayList;

public class SessionStatistics {

    public String sessionStartDate = "";
    public String sessionEndDate = "";
    public final ArrayList<LevelStatistics> levelStatisticsList = new ArrayList<>();
    public final ArrayList<MenuStatistics> menuStatisticsList = new ArrayList<>();

    public LevelStatistics getLatestLevelStatistics() {
        if (!levelStatisticsList.isEmpty()) {
            return levelStatisticsList.get(levelStatisticsList.size() - 1);
        }
        System.out.println("No level statistics");
        return null;
    }

    public MenuStatistics getLatestMenuStatistics() {
        if (!menuStatisticsList.isEmpty()) {
            return menuStatisticsList.get(menuStatisticsList.size() - 1);
        }
        System.out.println("No menu statistics");
        return null;
    }
}
