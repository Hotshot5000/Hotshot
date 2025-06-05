/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.tables;

public class UserFriend extends GenericTransient {

    private long id;
    private long userId;
    private long friendId;
    private String friendName;
    private int status; // 0 offline 1 online 2 pending acceptance.

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

    public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static String getStatusAsText(UserFriend friend) {
        String statusText = null;
        switch (friend.getStatus()) {
            case 0:
                statusText = "offline";
                break;
            case 1:
                statusText = "online";
                break;
            case 2:
                statusText = "pending invitation";
                break;
            default:
                throw new IllegalStateException("status:" + friend.getStatus());
        }
        return statusText;
    }
}
