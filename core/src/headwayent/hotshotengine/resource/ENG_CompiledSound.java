/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/24/21, 11:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

public class ENG_CompiledSound {

    public String name;
    public String filename;
    public long duration;
    public int priority;

    public ENG_CompiledSound() {

    }

    public ENG_CompiledSound(String name, String filename, long duration, int priority) {
        this.name = name;
        this.filename = filename;
        this.duration = duration;
        this.priority = priority;
    }
}
