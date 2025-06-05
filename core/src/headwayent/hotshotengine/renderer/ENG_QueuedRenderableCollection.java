/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ENG_QueuedRenderableCollection {

    public enum OrganisationMode {
        /// Group by pass
        OM_PASS_GROUP((byte) 1),
        /// Sort descending camera distance
        OM_SORT_DESCENDING((byte) 2),
        /**
         * Sort ascending camera distance
         * Note value overlaps with descending since both use same sort
         */
        OM_SORT_ASCENDING((byte) 6);

        private final byte mode;

        OrganisationMode(byte mode) {
            this.mode = mode;
        }

        public byte getMode() {
            return mode;
        }

        public static OrganisationMode get(byte b) {
            switch (b) {
                case 1:
                    return OM_PASS_GROUP;
                case 2:
                    return OM_SORT_DESCENDING;
                case 6:
                    return OM_SORT_ASCENDING;
            }
            return null;
        }
    }

    static class PassGroupLess implements Comparator<ENG_Pass> {

        @Override
        public int compare(ENG_Pass arg0, ENG_Pass arg1) {
            
            int hasha = arg0.getHash();
            int hashb = arg1.getHash();

            if (hasha == hashb) {
                //return arg0.toString().compareTo(arg1.toString());
//				return arg0.hashCode() < arg1.hashCode() ? -1 : 1;//-1;
//				return 0;
//				return arg0.hashCode() - arg1.hashCode();
                return arg0.getId() - arg1.getId();
            } else {
                return (hasha < hashb) ? -1 : 1;
            }
        }

    }

    static class DepthSortDescendingLess implements Comparator<ENG_RenderablePass> {

        public ENG_Camera camera;

        public DepthSortDescendingLess() {

        }

        public DepthSortDescendingLess(ENG_Camera camera) {
            this.camera = camera;
        }

        public void set(ENG_Camera camera) {
            this.camera = camera;
        }

        @Override
        public int compare(ENG_RenderablePass a, ENG_RenderablePass b) {
            
            if (a.renderable == b.renderable) {
                int p0 = a.pass.getHash();
                int p1 = b.pass.getHash();

                return Integer.compare(p0, p1);
            } else {
                float adepth = a.renderable.getSquaredViewDepth(camera);
                float bdepth = b.renderable.getSquaredViewDepth(camera);

                if ((adepth - bdepth) < ENG_Math.FLOAT_EPSILON) {
                    return -1;
                } else {
                    return Float.compare(bdepth, adepth);
                }
            }
        }

    }

    protected final TreeMap<ENG_Pass, ArrayList<ENG_Renderable>> mGrouped =
            new TreeMap<>(new PassGroupLess());

    protected final ArrayList<ENG_RenderablePass> mSortedDescending =
            new ArrayList<>();

    protected final ENG_RadixSortFunctorPass mRadixSortFunctorPass =
            new ENG_RadixSortFunctorPass();
    protected final ENG_RadixSortFunctorDistance mRadixSortFunctorDistance =
            new ENG_RadixSortFunctorDistance();
    protected final DepthSortDescendingLess mDepthSortDescendingLess =
            new DepthSortDescendingLess();

    protected byte mOrganisationMode;

    protected void acceptVisitorGrouped(ENG_QueuedRenderableVisitor visitor) {
        for (Entry<ENG_Pass, ArrayList<ENG_Renderable>> ipass : mGrouped.entrySet()) {
            if (ipass.getValue().isEmpty()) {
                continue;
            }

            if (!visitor.visit(ipass.getKey())) {
                continue;
            }

            ArrayList<ENG_Renderable> rendList = ipass.getValue();
            int len = rendList.size();
            for (int i = 0; i < len; ++i) {
                visitor.visit(rendList.get(i));
            }
        }
    }

    protected void acceptVisitorDescending(ENG_QueuedRenderableVisitor visitor) {
        int len = mSortedDescending.size();
        for (int i = 0; i < len; ++i) {
            visitor.visit(mSortedDescending.get(i));
        }
    }

    protected void acceptVisitorAscending(ENG_QueuedRenderableVisitor visitor) {
        int len = mSortedDescending.size();
        for (int i = len - 1; i >= len; --i) {
            visitor.visit(mSortedDescending.get(i));
        }
    }

    public ENG_QueuedRenderableCollection() {

    }

    public void clear() {
    /*	Iterator<Entry<ENG_Pass, ArrayList<ENG_Renderable>>> it =
			mGrouped.entrySet().iterator();
		
		while (it.hasNext()) {
			it.next().getValue().clear();
		}*/

        for (ArrayList<ENG_Renderable> it : mGrouped.values()) {
            it.clear();
        }

        mSortedDescending.clear();
    }

    public void resetOrganisationModes() {
        mOrganisationMode = 0;
    }

    public void addOrganisationMode(OrganisationMode om) {
        mOrganisationMode |= om.getMode();
    }

    public void removePassGroup(ENG_Pass p) {
        if (!mGrouped.isEmpty()) {
            mGrouped.remove(p);
        }
//        ArrayList<ENG_Renderable> remove = mGrouped.remove(p);
//		if (remove == null) {
//			System.out.println("pass with hash " + p.getHash() + " from material "
//					+ p.mParent.mParent.mName + " could not be removed");
//		}
    }

    public void addRenderable(ENG_Pass pass, ENG_Renderable rend) {
        if ((mOrganisationMode & OrganisationMode.OM_SORT_DESCENDING.getMode()) != 0) {
            mSortedDescending.add(new ENG_RenderablePass(rend, pass));
        }

        if ((mOrganisationMode & OrganisationMode.OM_PASS_GROUP.getMode()) != 0) {
            if (!mGrouped.containsKey(pass)) {
                ArrayList<ENG_Renderable> list =
                        new ArrayList<>();
                list.add(rend);
                mGrouped.put(pass, list);
            } else {
                mGrouped.get(pass).add(rend);
            }
        }
    }

    public void acceptVisitor(ENG_QueuedRenderableVisitor visitor, OrganisationMode om) {
        if ((om.getMode() & mOrganisationMode) == 0) {
            if ((om.getMode() & OrganisationMode.OM_PASS_GROUP.getMode()) != 0) {
                om = OrganisationMode.OM_PASS_GROUP;
            } else if ((om.getMode() & OrganisationMode.OM_SORT_ASCENDING.getMode()) != 0) {
                om = OrganisationMode.OM_SORT_ASCENDING;
            } else if ((om.getMode() & OrganisationMode.OM_SORT_DESCENDING.getMode()) != 0) {
                om = OrganisationMode.OM_SORT_DESCENDING;
            } else {
                throw new IllegalArgumentException(
                        "Organisation mode requested in acceptVistor was not notified " +
                                "to this class ahead of time, therefore may not be supported.");
            }
        }

        switch (om) {
            case OM_PASS_GROUP:
                acceptVisitorGrouped(visitor);
                break;
            case OM_SORT_DESCENDING:
                acceptVisitorDescending(visitor);
                break;
            case OM_SORT_ASCENDING:
                acceptVisitorAscending(visitor);
                break;
        }
    }

    public void merge(ENG_QueuedRenderableCollection rhs) {
        mSortedDescending.addAll(rhs.mSortedDescending);

        for (Entry<ENG_Pass, ArrayList<ENG_Renderable>> entry : rhs.mGrouped.entrySet()) {
            if (!mGrouped.containsKey(entry.getKey())) {
                ArrayList<ENG_Renderable> list = new ArrayList<>(entry.getValue());
                mGrouped.put(entry.getKey(), list);
            } else {
                mGrouped.get(entry.getKey()).addAll(entry.getValue());
            }
        }
    }

    public void sort(ENG_Camera cam) {
        if ((mOrganisationMode & OrganisationMode.OM_SORT_DESCENDING.getMode()) != 0) {
            if (mSortedDescending.size() > 2000) {
                Collections.sort(mSortedDescending, mRadixSortFunctorPass);
                mRadixSortFunctorDistance.set(cam);
                Collections.sort(mSortedDescending, mRadixSortFunctorDistance);
            } else {
                mDepthSortDescendingLess.set(cam);
                Collections.sort(mSortedDescending, mDepthSortDescendingLess);
            }
        }
    }
}
