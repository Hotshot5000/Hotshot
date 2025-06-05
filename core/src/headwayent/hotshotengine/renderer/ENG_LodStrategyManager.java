/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/1/17, 6:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ENG_LodStrategyManager {

//    private static ENG_LodStrategyManager lodStrategyManager;
    private final TreeMap<String, ENG_LodStrategy> mStrategies = new TreeMap<>();
    private final ENG_DistanceLodStrategy distanceStrategy;
    private final ENG_PixelCountLodStrategy pixelCountStrategy;
    private ENG_LodStrategy mDefaultStrategy;

    public ENG_LodStrategyManager() {
//        if (lodStrategyManager == null) {
//            lodStrategyManager = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        lodStrategyManager = this;

        distanceStrategy = new ENG_DistanceLodStrategy();
        addStrategy(distanceStrategy);

        pixelCountStrategy = new ENG_PixelCountLodStrategy();
        addStrategy(pixelCountStrategy);

        setDefaultStrategy(distanceStrategy);
    }

    public void addStrategy(ENG_LodStrategy strategy) {
        if (strategy.getName().equals("default")) {
            throw new IllegalArgumentException(
                    "Lod strategy name must not be \"default\".");
        }
        mStrategies.put(strategy.getName(), strategy);
    }

    public ENG_LodStrategy removeStrategy(String name) {
        return mStrategies.remove(name);
    }

    public void removeAllStrategies() {
        mStrategies.clear();
    }

    public ENG_LodStrategy getStrategy(String name) {
        if (name.equals("default")) {
            return mDefaultStrategy;
        }
        return mStrategies.get(name);
    }

    public void setDefaultStrategy(ENG_LodStrategy strategy) {
        mDefaultStrategy = strategy;
    }

    public void setDefaultStrategy(String name) {
        setDefaultStrategy(getStrategy(name));
    }

    public ENG_LodStrategy getDefaultStrategy() {
        return mDefaultStrategy;
    }

    public Iterator<Entry<String, ENG_LodStrategy>> getIterator() {
        return mStrategies.entrySet().iterator();
    }

    public ENG_DistanceLodStrategy getDistanceStrategy() {
        return distanceStrategy;
    }

    public ENG_PixelCountLodStrategy getPixelCountStrategy() {
        return pixelCountStrategy;
    }

    public static ENG_LodStrategyManager getSingleton() {
//        if (MainActivity.isDebugmode() && lodStrategyManager == null) {
//            throw new NullPointerException("lodStrategyManager not initialized!");
//        }
//
//        return lodStrategyManager;
        return MainApp.getGame().getRenderRoot().getLodManager();
    }
}
