/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public abstract class ENG_HardwareOcclusionQuery {

    protected int mPixelCount;
    protected boolean mIsQueryResultStillOutstanding;

    public ENG_HardwareOcclusionQuery() {

    }

    public abstract void beginOcclusionQuery();

    public abstract void endOcclusionQuery();

    public abstract boolean pullOcclusionQuery(int[] NumOfFragments);

    public abstract boolean isStillOutstanding();

    public int getLastQuerysPixelcount() {
        return mPixelCount;
    }
}
