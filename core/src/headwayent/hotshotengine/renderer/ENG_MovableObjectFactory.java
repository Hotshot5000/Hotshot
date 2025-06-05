/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/29/17, 8:31 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.TreeMap;

public abstract class ENG_MovableObjectFactory {

    protected long mTypeFlag = 0xFFFFFFFF;

    protected abstract Object createInstanceImpl(String name,
                                                            TreeMap<String, String> params);

    protected Object createInstanceImpl(String name) {
        return createInstanceImpl(name, null);
    }

    public ENG_MovableObjectFactory() {

    }

    public abstract String getType();

    public ENG_MovableObject createInstance(String name, ENG_SceneManager manager,
                                            TreeMap<String, String> params) {
        ENG_MovableObject m = (ENG_MovableObject) createInstanceImpl(name, params);
        m._notifyCreator(this);
        m._notifyManager(manager);
        return m;
    }

    public ENG_MovableObject createInstance(String name, ENG_SceneManager manager) {
        ENG_MovableObject m = (ENG_MovableObject) createInstanceImpl(name);
        m._notifyCreator(this);
        m._notifyManager(manager);
        return m;
    }

    public abstract void destroyInstance(Object obj, boolean skipGLDelete);

    public boolean requestTypeFlags() {
        return false;
    }

    public void _notifyTypeFlags(long flag) {
        mTypeFlag = flag;
    }

    public long getTypeFlags() {
        return mTypeFlag;
    }
}
