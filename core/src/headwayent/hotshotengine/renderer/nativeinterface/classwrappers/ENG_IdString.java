/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.blackholedarksun.MainActivity;

/**
 * Created by sebas on 17.02.2017.
 */

public class ENG_IdString implements Comparable<ENG_IdString> {

    private static final boolean DEBUG = MainActivity.isDebugmode();

    private int hash;

    // ONLY FOR DEBUGGING COMMENT THIS ON RELEASE!!!
    private String s;

    public ENG_IdString() {
        if (DEBUG) {
            s = "";
        }
    }

    public ENG_IdString(String s) {
        hash = createIdString(s);
        if (DEBUG) {
            this.s = s;
        }
    }

    public ENG_IdString(int val) {
        hash = createIdString(val);
        if (DEBUG) {
            s = String.valueOf(val);
        }
    }

    public ENG_IdString(ENG_IdString copy) {
        hash = copy.hash;
        if (DEBUG) {
            s = copy.s;
        }
    }

    public void append(String oth) {
        hash = append(hash, oth);
        if (DEBUG) {
            s += oth;
        }
    }

    public ENG_IdString concatenate(String oth) {
        ENG_IdString ret = new ENG_IdString(this);
        ret.append(oth);
        return ret;
    }

    public int getHash() {
        return hash;
    }

    public String getString() {
        if (DEBUG) {
            return s;
        } else {
            return null;
        }
    }

    public static native int createIdString(String s);
    public static native int createIdString(int val);
    public static native int append(int hash, String oth);

    @Override
    public int compareTo(ENG_IdString o) {
        return hash - o.hash;
    }

    /** @noinspection EqualsWhichDoesntCheckParameterClass*/
    @Override
    public boolean equals(Object obj) {
        return hash == ((ENG_IdString) obj).hash;
    }

    @Override
    public String toString() {
        return DEBUG ? (hash + "[" + s + "]") : String.valueOf(hash);
    }
}
