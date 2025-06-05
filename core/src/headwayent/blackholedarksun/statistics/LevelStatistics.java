/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/1/21, 10:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.statistics;

import java.util.ArrayList;

public class LevelStatistics {

    public String levelName = "";
    public String levelStartDate = "";
    public String levelEndDate = "";
    public final ArrayList<LevelEventStatistics> levelEventStatisticsList = new ArrayList<>();
    public boolean multiplayer;

    public transient long currentLevelEventStartTime;

    public LevelEventStatistics getLatestLevelEventStatistics() {
        if (!levelEventStatisticsList.isEmpty()) {
            return levelEventStatisticsList.get(levelEventStatisticsList.size() - 1);
        }
        System.out.println("No level event statistics");
        return null;
    }
}
