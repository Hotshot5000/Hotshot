/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;
import java.util.Iterator;

public class ENG_RenderQueueInvocationSequence {

    protected final String mName;
    protected final ArrayList<ENG_RenderQueueInvocation> mInvocations =
            new ArrayList<>();

    public ENG_RenderQueueInvocationSequence(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public ENG_RenderQueueInvocation add(byte renderQueueGroupID,
                                         String invocationName) {
        ENG_RenderQueueInvocation ret = new ENG_RenderQueueInvocation(
                renderQueueGroupID, invocationName);
        mInvocations.add(ret);
        return ret;
    }

    public void add(ENG_RenderQueueInvocation inv) {
        mInvocations.add(inv);
    }

    public void clear() {
        mInvocations.clear();
    }

    public ENG_RenderQueueInvocation get(int index) {
        if ((index < 0) || (index >= mInvocations.size())) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return mInvocations.get(index);
    }

    public void remove(int index) {
        if ((index < 0) || (index >= mInvocations.size())) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        mInvocations.remove(index);
    }

    public Iterator<ENG_RenderQueueInvocation> iterator() {
        return mInvocations.iterator();
    }
}
