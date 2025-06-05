/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/10/16, 9:20 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.util.Objects;

import headwayent.hotshotengine.basictypes.ENG_Float;

/**
 * Created by sebas on 09.01.2016.
 */
public class ENG_ClosestObjectData implements Comparable<ENG_ClosestObjectData> {
    public float minDist;
    public Long objectId;

    public ENG_ClosestObjectData() {

    }

    public ENG_ClosestObjectData(float minDist, Long objectId) {
        this.minDist = minDist;
        this.objectId = objectId;
    }

    @Override
    public int compareTo(ENG_ClosestObjectData o) {
        return this.minDist < o.minDist ? -1 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ENG_ClosestObjectData)) return false;
        ENG_ClosestObjectData data = (ENG_ClosestObjectData) o;
        return ENG_Float.compareTo(minDist, data.minDist) == 0 && Objects.equals(objectId, data.objectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minDist, objectId);
    }
}
