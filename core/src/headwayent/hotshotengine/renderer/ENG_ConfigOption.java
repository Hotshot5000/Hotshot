/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 10:27 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;

public class ENG_ConfigOption {

    public String name;
    public String currentValue;
    public ArrayList<String> possibleValues = new ArrayList<>();
    public boolean immutable;
}
