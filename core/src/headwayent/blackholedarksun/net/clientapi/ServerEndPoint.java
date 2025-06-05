/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/19/21, 2:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi;

import retrofit.Endpoint;

/**
 * Created by Sebastian on 10.04.2015.
 */
public class ServerEndPoint implements Endpoint {

    // Switch between these when running local vs mobile.
//    private static final String SERVER_IP = "http://127.0.0.1";
    private static final String SERVER_IP = "http://5.12.167.48";
    private static final String URL = /*APP_Game.APP_SERVER_IP*/ SERVER_IP + ":8080/HotshotWebServer5_war_exploded/rs/blackhole_darksun";

    @Override
    public String getUrl() {
        return URL;
    }

    @Override
    public String getName() {
        return "default";
    }
}
