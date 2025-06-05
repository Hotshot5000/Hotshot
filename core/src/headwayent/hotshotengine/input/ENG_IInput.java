/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

/**
 * Interface for getting data from an input. The data is then converted to actual usable
 * information in InputConvertor
 *
 * @author sebi
 */
public interface ENG_IInput {

    Object getData();

    /**
     * For resetting the state of the input when setting it on
     */
    void reset();
}
