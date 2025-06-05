/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

/**
 * Created by sebas on 17.11.2015.
 */
public interface ENG_GameDescriptionEventsListener {

    void onGameStart();

    void onGameActivation(boolean activated);

    void onGameEnd();
}
