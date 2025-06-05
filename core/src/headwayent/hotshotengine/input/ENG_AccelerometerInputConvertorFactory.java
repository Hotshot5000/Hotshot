/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:45 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package headwayent.hotshotengine.input;

/**
 * @author sebi
 */
public class ENG_AccelerometerInputConvertorFactory extends ENG_InputConvertorFactory {

    public static final String TYPE = "AccelerometerConvertor";

    @Override
    public ENG_InputConvertor createInstance(String instanceName, ENG_IInput input) {
        return new ENG_AccelerometerInputConvertor(instanceName, (ENG_AccelerometerInput) input);
    }

    @Override
    public String getTypeName() {
        return TYPE;
    }

}
