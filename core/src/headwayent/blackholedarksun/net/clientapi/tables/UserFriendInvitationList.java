/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.tables;

import java.util.ArrayList;
import java.util.List;

public class UserFriendInvitationList extends GenericTransient {

    public final ArrayList<UserFriendInvitation> userFriendInvitationList = new ArrayList<>();

    public UserFriendInvitationList() {

    }

    public UserFriendInvitationList(List<UserFriendInvitation> userFriendInvitationList) {
        this.userFriendInvitationList.addAll(userFriendInvitationList);
    }
}
