/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.tables;

import java.util.Date;

public class Lobby extends GenericTransient {

    public enum LobbyStatus {
        CREATED(0), READY_TO_START_SERVER(1), READY_TO_JOIN_SERVER(2), IN_GAME(3), EXITED(4);


        private final int pos;

        LobbyStatus(int i) {
            this.pos = i;
        }

        public int getPos() {
            return pos;
        }

        public static LobbyStatus getLobbyStatus(int pos) {
            switch (pos) {
                case 0: return CREATED;
                case 1: return READY_TO_START_SERVER;
                case 2: return READY_TO_JOIN_SERVER;
                case 3: return IN_GAME;
                case 4: return EXITED;
                default:
                    throw new IllegalArgumentException(pos + " is not a supported Lobby status");
            }
        }

        public static String getLobbyStatusAsString(int pos) {
            switch (pos) {
                case 0: return "Created";
                case 1: return "Ready to start";
                case 2: return "Ready to join";
                case 3: return "In game";
                case 4: return "Closed";
                default:
                    throw new IllegalArgumentException(pos + " is not a supported Lobby status");
            }
        }
    }

    private long id;
    private long lobbyLeader;
    private long lobbyInvitations;
    private int status; // 0 - lobby created 1 - ready to start server 2 - ready to join 3 - in game 4 - lobby closed.
    private int expectedPlayerNum;
    private int joinedPlayerNum;
    private Date creationDate;
    private String lobbyLeaderName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLobbyLeader() {
        return lobbyLeader;
    }

    public void setLobbyLeader(long lobbyLeader) {
        this.lobbyLeader = lobbyLeader;
    }

    public long getLobbyInvitations() {
        return lobbyInvitations;
    }

    public void setLobbyInvitations(long lobbyInvitations) {
        this.lobbyInvitations = lobbyInvitations;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getExpectedPlayerNum() {
        return expectedPlayerNum;
    }

    public void setExpectedPlayerNum(int expectedPlayerNum) {
        this.expectedPlayerNum = expectedPlayerNum;
    }

    public int getJoinedPlayerNum() {
        return joinedPlayerNum;
    }

    public void setJoinedPlayerNum(int joinedPlayerNum) {
        this.joinedPlayerNum = joinedPlayerNum;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getLobbyLeaderName() {
        return lobbyLeaderName;
    }

    public void setLobbyLeaderName(String lobbyLeaderName) {
        this.lobbyLeaderName = lobbyLeaderName;
    }
}
