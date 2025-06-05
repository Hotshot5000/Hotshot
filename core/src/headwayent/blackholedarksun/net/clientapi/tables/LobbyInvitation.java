/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.tables;

import java.util.Date;

public class LobbyInvitation extends GenericTransient {

    public enum InvitedUserStatus {
        NOT_JOINED(0), JOINED(1), CANCELLED(2);

        private final int pos;

        InvitedUserStatus(int i) {
            this.pos = i;
        }

        public int getPos() {
            return pos;
        }

        public static InvitedUserStatus getInvitedUserStatus(int pos) {
            switch (pos) {
                case 0: return NOT_JOINED;
                case 1: return JOINED;
                case 2: return CANCELLED;
                default:
                    throw new IllegalArgumentException(pos + " is an invalid InvitedUserStatus");
            }
        }

        public static String getInvitedUserStatusAsString(int pos) {
            switch (pos) {
                case 0: return "Not joined";
                case 1: return "Joined";
                case 2: return "Cancelled";
                default:
                    throw new IllegalArgumentException(pos + " is an invalid InvitedUserStatus");
            }
        }
    }

    private long id;
    private long lobbyId;
    private long invitedUserId;
    private long userJoined;
    private Date creationDate;
    private Date userJoinedDate;
    private String invitedUserName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public long getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(long invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public long getUserJoined() {
        return userJoined;
    }

    public void setUserJoined(long userJoined) {
        this.userJoined = userJoined;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUserJoinedDate() {
        return userJoinedDate;
    }

    public void setUserJoinedDate(Date userJoinedDate) {
        this.userJoinedDate = userJoinedDate;
    }

    public String getInvitedUserName() {
        return invitedUserName;
    }

    public void setInvitedUserName(String invitedUserName) {
        this.invitedUserName = invitedUserName;
    }
}
