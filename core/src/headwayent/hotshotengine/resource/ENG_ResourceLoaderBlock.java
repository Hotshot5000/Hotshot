/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import java.util.ArrayList;

/**
 * Created by sebas on 30.03.2016.
 */
public class ENG_ResourceLoaderBlock {

    private final ArrayList<ENG_ResourceLoader> resourceLoaderList = new ArrayList<>();
    private int priority;
    private boolean usingAutoPriority;

    public ENG_ResourceLoader createResourceLoader() {
        usingAutoPriority = true;
        return createResourceLoader(priority++);
    }

    public ENG_ResourceLoader createResourceLoader(int priority) {
        ENG_ResourceLoader resourceLoader = new ENG_ResourceLoader(priority);
        addResourceLoader(resourceLoader);
        return resourceLoader;
    }

    public void addResourceLoader(ENG_ResourceLoader resourceLoader) {
        if (usingAutoPriority && resourceLoader.getPriority() != priority - 1) {
            throw new IllegalArgumentException("You are using auto generated priorities. Cannot use manual priority now.");
        }
        resourceLoaderList.add(resourceLoader);
    }

    public void clear() {
        resourceLoaderList.clear();
        priority = 0;
        usingAutoPriority = false;
    }

    public void executeInBlock() {
        for (ENG_ResourceLoader resourceLoader : resourceLoaderList) {
            resourceLoader.execute();
        }
    }

    public ArrayList<ENG_ResourceLoader> getResourceLoaderList() {
        return resourceLoaderList;
    }
}
