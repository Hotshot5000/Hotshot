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

public class LobbyInvitationList extends GenericTransient {

    public final ArrayList<LobbyInvitation> lobbyInvitationList = new ArrayList<>();
    private int lobbyStatus; // We also need to update on the lobby status. Hack.

    public LobbyInvitationList() {

    }

    public LobbyInvitationList(List<LobbyInvitation> lobbyInvitationList) {
        this.lobbyInvitationList.addAll(lobbyInvitationList);
    }

    public int getLobbyStatus() {
        return lobbyStatus;
    }

    public void setLobbyStatus(int lobbyStatus) {
        this.lobbyStatus = lobbyStatus;
    }
}
