/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

public abstract class ENG_InputConvertorFactory {

    public abstract ENG_InputConvertor createInstance(String instanceName, ENG_IInput input);

    public abstract String getTypeName();
}
