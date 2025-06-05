/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.viewlisteners;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_View;

/**
 * Created by Sebi on 24.05.2014.
 */
public abstract class OnCharacterListenerWithType implements ENG_View.OnCharacterListener {

    private final String type;
    /** @noinspection deprecation*/
    private final Bundle bundle;

    /** @noinspection deprecation*/
    public OnCharacterListenerWithType(String type, Bundle bundle) {
        this.type = type;
        this.bundle = bundle;
    }

    public String getType() {
        return type;
    }

    /** @noinspection deprecation*/
    public Bundle getBundle() {
        return bundle;
    }
}
