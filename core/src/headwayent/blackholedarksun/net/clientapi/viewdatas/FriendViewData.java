/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.viewdatas;

import java.util.Objects;

public class FriendViewData {

    public enum Type {
        FRIEND, INVITATION
    }

    public String name;
    public String status;
    // This shouldn't be in the data view.
    public Type type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendViewData that = (FriendViewData) o;
        return Objects.equals(name, that.name);// &&
                // We don't check for status because we might have a user that was pending
                // invitation for which we accepted the invitation. If we differentiate
                // also using status we will have 2 users in the list.
//                Objects.equals(status, that.status) &&
//                type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(name/*, status, type*/);
    }
}
