/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/25/16, 4:35 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import com.badlogic.gdx.InputProcessor;

public interface ENG_IInputListener extends InputProcessor {

    boolean isCursorGrabbed();

    boolean isBackKeyCaught();

    boolean isMenuKeyCaught();
}
