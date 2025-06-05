/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 4:59 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.rmi;

/**
 * Created by sebas on 26.01.2016.
 */
public class UserStats implements Comparable<UserStats> {

    private long userId;
    private String username;
    private int kills;
    private int deaths;

    public UserStats() {

    }

    public UserStats(UserStats userStats) {
        set(userStats);
    }

    public void set(UserStats userStats) {
        userId = userStats.userId;
        username = userStats.username;
        kills = userStats.kills;
        deaths = userStats.deaths;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long id) {
        this.userId = id;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void incrementDeaths() {
        ++deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void incrementKills() {
        ++kills;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserStats with username: " + username + " kills: " + kills + " deaths: " + deaths;
    }

    @Override
    public int compareTo(UserStats userStats) {
        return Integer.compare(kills, userStats.kills) * -1; // The largest number of kills is first.
    }
}
