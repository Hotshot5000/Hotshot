/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/19/15, 10:07 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package headwayent.blackholedarksun.components;

import com.artemis.Component;

/**
 * @author sebi
 */
public class BeaconProperties extends Component {

    private boolean reached;

    /**
     * Default empty constructor for Kryo serialization.
     */
    public BeaconProperties() {

    }

    /**
     * @return the reached
     */
    public boolean isReached() {
        return reached;
    }

    /**
     * @param reached the reached to set
     */
    public void setReached(boolean reached) {
        this.reached = reached;
    }
}
