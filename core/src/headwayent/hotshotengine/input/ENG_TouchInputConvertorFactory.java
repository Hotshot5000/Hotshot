/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

public class ENG_TouchInputConvertorFactory extends ENG_InputConvertorFactory {

    public static final String TYPE = "TouchConvertor";

    @Override
    public ENG_InputConvertor createInstance(String instanceName, ENG_IInput input) {

        return new ENG_TouchInputConvertor(instanceName, (ENG_TouchInput) input, 100);
    }

    @Override
    public String getTypeName() {

        return TYPE;
    }


}
