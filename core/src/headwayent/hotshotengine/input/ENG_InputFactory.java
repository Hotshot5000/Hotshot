/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:45 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

public abstract class ENG_InputFactory {

    public abstract ENG_IInput createInstance(String instanceName);

    public abstract String getTypeName();
}
