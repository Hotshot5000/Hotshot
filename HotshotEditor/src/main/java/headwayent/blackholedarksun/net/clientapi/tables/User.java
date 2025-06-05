package headwayent.blackholedarksun.net.clientapi.tables;

import java.util.Date;

/**
 * Created by Sebastian on 27.02.2015.
 */

public class User extends GenericTransient {

    private long id;
    private String username;
    private String password;
    private int sessionsPlayed;
    private int sessionsWon;
    private int sessionsLost;
    private int kills;
    private int deaths;
    private int online;
    private int inSession;
    private int elo;
    private Date lastOnlineTime;
    private Date lastInSessionTime;

    private String authToken;

    public User() {

    }


    public long getId() {
        return id;
    }

//    public void setId(int id) {
//        this.id = id;
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSessionsPlayed() {
        return sessionsPlayed;
    }

    public void setSessionsPlayed(int sessionsPlayed) {
        this.sessionsPlayed = sessionsPlayed;
    }

    public int getSessionsWon() {
        return sessionsWon;
    }

    public void setSessionsWon(int sessionsWon) {
        this.sessionsWon = sessionsWon;
    }

    public int getSessionsLost() {
        return sessionsLost;
    }

    public void setSessionsLost(int sessionsLost) {
        this.sessionsLost = sessionsLost;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setOnlineBool(boolean online) {
        this.online = online ? 1 : 0;
    }

    public boolean isOnlineBool() {
        return online == 1;
    }

    public void setInSessionBool(boolean inSession) {
        this.inSession = inSession ? 1 : 0;
    }

    public boolean isInSessionBool() {
        return inSession == 1;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getInSession() {
        return inSession;
    }

    public void setInSession(int inSession) {
        this.inSession = inSession;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Date lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public Date getLastInSessionTime() {
        return lastInSessionTime;
    }

    public void setLastInSessionTime(Date lastInSessionTime) {
        this.lastInSessionTime = lastInSessionTime;
    }
}
