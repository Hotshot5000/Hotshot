/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

public class ENG_MouseAndKeyboardInputFactory extends ENG_InputFactory {

    public static final String TYPE = "MouseAndKeyboard";

    @Override
    public ENG_IInput createInstance(String instanceName) {

        return new ENG_MouseAndKeyboardInput(instanceName);
    }

    @Override
    public String getTypeName() {

        return TYPE;
    }

}
