/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.tables;

public class UserFriendInvitation extends GenericTransient {

    private long id;
    private long userId;
    private long pendingInvitationFriendId;
    private String invitedFriendName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPendingInvitationFriendId() {
        return pendingInvitationFriendId;
    }

    public void setPendingInvitationFriendId(long pendingInvitationFriendId) {
        this.pendingInvitationFriendId = pendingInvitationFriendId;
    }

    public String getInvitedFriendName() {
        return invitedFriendName;
    }

    public void setInvitedFriendName(String invitedFriendName) {
        this.invitedFriendName = invitedFriendName;
    }
}
