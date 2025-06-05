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

public class UserFriendList extends GenericTransient {

    public final ArrayList<UserFriend> userFriends = new ArrayList<>();

    public UserFriendList(List<UserFriend> userFriends) {
        this.userFriends.addAll(userFriends);
    }

    public UserFriendList() {

    }
}
