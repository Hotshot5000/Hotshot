/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/25/16, 4:35 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

public abstract class ENG_IInputListenerFactory {

    public abstract ENG_IInputListener createInstance(ENG_IInput input);

    public abstract String getTypeName();
}
