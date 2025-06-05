/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.registeredclasses;

import java.util.ArrayList;

/**
 * Created by Sebi on 01.06.2014.
 */
public class ServerListResponse extends NetBase {

    public final ArrayList<ArrayList<String>> response = new ArrayList<>();

    public ServerListResponse() {
        super(Type.SERVER_LIST_RESPONSE);
    }

    public void addRow(String serverName, int playerNum, int totalPlayerNum, int ping) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(serverName);
        strings.add(playerNum + "/" + totalPlayerNum);
        strings.add(String.valueOf(ping));
        response.add(strings);
    }

}
