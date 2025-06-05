/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/2/21, 10:36 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.statistics;

import java.util.ArrayList;

import headwayent.blackholedarksun.levelresource.LevelEvent;

public class LevelEventStatistics {

    public String name = "";
    public String state = LevelEvent.EventState.NONE.toString();
    public String levelEventStartDate = "";
    public String levelEventEndDate = "";
    public long levelEventDuration;
    public int healthEventBegin;
    public int healthEventEnd;
    public int countermeasuresLaunchedNum;
    public int afterburnerStartNum;
    public int reloaderCalledNum;
    public final ArrayList<WeaponTypeStatistics> weaponTypeStatisticsEventBeginList = new ArrayList<>();
    public final ArrayList<WeaponTypeStatistics> weaponTypeStatisticsEventEndList = new ArrayList<>();

    public transient long levelEventBeginTime;
}
