/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public abstract class DelayedEvent extends Event {
    protected DelayStart delayStart;
    protected DelayEnd delayEnd;

    public DelayedEvent(String name) {
        super(name);
    }

    public DelayStart getDelayStart() {
        return delayStart;
    }

    public DelayEnd getDelayEnd() {
        return delayEnd;
    }
}
