/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.input;

import headwayent.hotshotengine.input.ENG_IInput;
import headwayent.hotshotengine.input.ENG_InputConvertor;
import headwayent.hotshotengine.input.ENG_InputConvertorFactory;
import headwayent.hotshotengine.input.ENG_MouseAndKeyboardInput;

public class InGameInputConvertorFactory extends ENG_InputConvertorFactory {

    public static final String TYPE = "InGameConvertor";

    @Override
    public ENG_InputConvertor createInstance(String instanceName,
                                             ENG_IInput input) {
        
        return new InGameInputConvertor(instanceName,
                (ENG_MouseAndKeyboardInput) input, 100);
    }

    @Override
    public String getTypeName() {
        
        return TYPE;
    }

}
