/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.viewdatas;

import java.util.Objects;

public class LobbyViewData {
    public String name;
    public String lobbyLeader;
//    public String expectedUserNum;
//    public String currentUserNum;
    public String userStatus; // currentUserNum / expectedUserNum
    public String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyViewData that = (LobbyViewData) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(lobbyLeader, that.lobbyLeader) &&
                Objects.equals(userStatus, that.userStatus) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, lobbyLeader, userStatus, status);
    }
}
