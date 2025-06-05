/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;

import java.util.TreeMap;

public class ENG_SkeletonManager {

//    private static ENG_SkeletonManager mgr;
    private final TreeMap<String, ENG_Skeleton> skeletonList = new TreeMap<>();

    public ENG_SkeletonManager() {

//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
    }

    public ENG_Skeleton create(String name) {
        ENG_Skeleton skeleton = skeletonList.get(name);
        if (skeleton == null) {
            skeleton = new ENG_Skeleton(name);
            skeletonList.put(name, skeleton);
        } else {
            throw new IllegalArgumentException(name + " skeleton already " +
                    "exists");
        }
        return skeleton;
    }

    public ENG_Skeleton load(String name) {
        ENG_Skeleton skel = create(name);
        skel.loadImpl();
        return skel;
    }

    public void destroySkeleton(String name) {
        ENG_Skeleton remove = skeletonList.remove(name);
        if (remove != null) {

        } else {
            throw new IllegalArgumentException(name + " is not a loaded " +
                    "skeleton");
        }
    }

    public ENG_Skeleton getByName(String name) {
        return skeletonList.get(name);
    }

    public void destroyAllSkeletons() {
        skeletonList.clear();
    }

    public static ENG_SkeletonManager getSingleton() {
//        if (MainActivity.isDebugmode() && mgr == null) {
//            throw new NullPointerException("Skeleton Manager not initialized");
//        }
//        return mgr;
        return MainApp.getGame().getRenderRoot().getSkeletonManager();
    }

}
