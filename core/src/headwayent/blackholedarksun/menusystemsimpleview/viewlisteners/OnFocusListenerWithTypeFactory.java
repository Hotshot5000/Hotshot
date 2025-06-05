/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/6/15, 1:48 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.viewlisteners;

import headwayent.hotshotengine.Bundle;

/**
 * Created by Sebi on 24.05.2014.
 */
public abstract class OnFocusListenerWithTypeFactory {

    /** @noinspection deprecation*/
    public abstract OnFocusListenerWithType createOnFocusListener(String type, Bundle bundle);
}
