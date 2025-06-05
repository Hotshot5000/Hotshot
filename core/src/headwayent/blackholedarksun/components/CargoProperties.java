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
public class CargoProperties extends Component {

    private boolean scanned;

    /**
     * Default empty constructor for Kryo serialization.
     */
    public CargoProperties() {

    }

    /**
     * @return the scanned
     */
    public boolean isScanned() {
        return scanned;
    }

    /**
     * @param scanned the scanned to set
     */
    public void setScanned(boolean scanned) {
        this.scanned = scanned;
    }
}
