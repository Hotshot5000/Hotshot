/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public class ENG_NameGenerator {

    private final String baseName;
    private int nameNum;

    public ENG_NameGenerator(String baseName) {
        this.baseName = baseName;
    }

    public String generateName() {
        return (baseName + nameNum++);
    }
}
