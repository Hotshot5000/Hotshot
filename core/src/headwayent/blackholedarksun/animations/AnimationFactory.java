/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import headwayent.blackholedarksun.Animation;

import com.artemis.Entity;

public abstract class AnimationFactory {

    private final String prefix;
    private int instanceNum;

    public AnimationFactory(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getInstanceNum() {
        return instanceNum++;
    }

    public abstract Animation createInstance(Entity entity);

    public abstract void destroyInstance(Animation anim);
}
