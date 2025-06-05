/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 4:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Float;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class ENG_LodStrategy {

    protected final String mName;

    protected abstract float getValueImpl(
            ENG_MovableObject movableObject, ENG_Camera camera);

    public ENG_LodStrategy(String name) {
        mName = name;
    }

    public abstract float getBaseValue();

    public abstract float transformBias(float factor);

    public float transformUserValue(float userValue) {
        return userValue;
    }

    public float getValue(ENG_MovableObject movableObject, ENG_Camera camera) {
        return getValueImpl(movableObject, camera.getLodCamera());
    }

    public abstract short getIndexMesh(
            float value, ArrayList<ENG_MeshLodUsage> meshLodUsageList);

    public abstract short getIndexMaterial(
            float value, ArrayList<ENG_Float> materialLodValueList);

    public abstract void sort(ArrayList<ENG_MeshLodUsage> meshLodUsageList);

    public abstract boolean isSorted(ArrayList<ENG_Float> values);

    public void assertSorted(ArrayList<ENG_Float> values) {
        if (!isSorted(values)) {
            throw new IllegalArgumentException("The lod values must be sorted");
        }
    }

    public String getName() {
        return mName;
    }

    protected static boolean isSortedAscending(ArrayList<ENG_Float> values) {
        int len = values.size();
        ENG_Float prev = values.get(0);
        for (int i = 1; i < len; ++i) {
            ENG_Float current = values.get(i);
            if (current.getValue() > prev.getValue()) {
                return false;
            }
            prev = current;
        }
        return true;
    }

    protected static boolean isSortedDescending(ArrayList<ENG_Float> values) {
        int len = values.size();
        ENG_Float prev = values.get(0);
        for (int i = 1; i < len; ++i) {
            ENG_Float current = values.get(i);
            if (current.getValue() < prev.getValue()) {
                return false;
            }
            prev = current;
        }
        return true;
    }

    private static class LodUsageSortLess implements Comparator<ENG_MeshLodUsage> {

        @Override
        public int compare(ENG_MeshLodUsage arg0, ENG_MeshLodUsage arg1) {

            if (arg0.value < arg1.value) {
                return -1;
            } else if (arg0.value > arg1.value) {
                return 1;
            }
            return 0;
        }

    }

    private final LodUsageSortLess less = new LodUsageSortLess();

    protected void sortAscending(ArrayList<ENG_MeshLodUsage> meshLodUsageList) {
        Collections.sort(meshLodUsageList, less);
    }

    private static class LodUsageSortGreater implements Comparator<ENG_MeshLodUsage> {

        @Override
        public int compare(ENG_MeshLodUsage arg0, ENG_MeshLodUsage arg1) {

            if (arg0.value < arg1.value) {
                return 1;
            } else if (arg0.value > arg1.value) {
                return -1;
            }
            return 0;
        }

    }

    private final LodUsageSortGreater greater = new LodUsageSortGreater();

    protected void sortDescending(ArrayList<ENG_MeshLodUsage> meshLodUsageList) {
        Collections.sort(meshLodUsageList, greater);
    }

    protected short getIndexAscendingMesh(float value,
                                          ArrayList<ENG_MeshLodUsage> meshLodUsageList) {
        short index = 0;
        int len = meshLodUsageList.size();
        for (int i = 0; i < len; ++i, ++index) {
            if (meshLodUsageList.get(i).value > value) {
                return (short) ((index != 0) ? (index - 1) : (0));
            }
        }
        return (short) (len - 1);
    }

    protected short getIndexDescendingMesh(float value,
                                           ArrayList<ENG_MeshLodUsage> meshLodUsageList) {
        short index = 0;
        int len = meshLodUsageList.size();
        for (int i = 0; i < len; ++i, ++index) {
            if (meshLodUsageList.get(i).value < value) {
                return (short) ((index != 0) ? (index - 1) : (0));
            }
        }
        return (short) (len - 1);
    }

    protected short getIndexAscendingMaterial(
            float value, ArrayList<ENG_Float> materialLodValueList) {
        short index = 0;
        int len = materialLodValueList.size();

        for (int i = 0; i < len; ++i, ++index) {
            if (materialLodValueList.get(i).getValue() > value) {
                return (short) ((index != 0) ? (index - 1) : (0));
            }
        }
        return (short) (len - 1);
    }

    protected short getIndexDescendingMaterial(
            float value, ArrayList<ENG_Float> materialLodValueList) {
        short index = 0;
        int len = materialLodValueList.size();

        for (int i = 0; i < len; ++i, ++index) {
            if (materialLodValueList.get(i).getValue() < value) {
                return (short) ((index != 0) ? (index - 1) : (0));
            }
        }
        return (short) (len - 1);
    }
}
