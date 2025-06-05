/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class ENG_StringInterface {

    private static final TreeMap<String, ENG_ParamDictionary> msDictionary =
            new TreeMap<>();
    private static final ReentrantLock msDictionaryMutex = new ReentrantLock();

    private ENG_ParamDictionary mParamDict;
    private final Object objThis;

    public ENG_StringInterface(Object objThis) {
        this.objThis = objThis;
    }

    public boolean createParamDictionary(String className) {
        msDictionaryMutex.lock();
        try {
            ENG_ParamDictionary dictionary = msDictionary.get(className);
            String mParamDictName;
            if (dictionary == null) {
                mParamDictName = className;
                mParamDict = new ENG_ParamDictionary();
                msDictionary.put(className, mParamDict);
                return true;
            } else {
                mParamDictName = className;
                mParamDict = dictionary;
                return false;
            }
        } finally {
            msDictionaryMutex.unlock();
        }
    }

    public ENG_ParamDictionary getParamDictionary() {
        return mParamDict;
    }

    public ArrayList<ENG_ParameterDef> getParameters() {
        ENG_ParamDictionary dict = getParamDictionary();
        if (dict != null) {
            return dict.getParameterList();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean setParameter(String name, String value) {
        ENG_ParamDictionary dict = getParamDictionary();
        if (dict != null) {
            ENG_ParamCommand command = dict.getParamCommand(name);
            if (command != null) {
                command.doSet(objThis, value);
                return true;
            }
        }
        return false;
    }

    public void setParameterList(TreeMap<String, String> list) {
        for (Entry<String, String> s : list.entrySet()) {
            setParameter(s.getKey(), s.getValue());
        }
    }

    public String getParameter(String name) {
        ENG_ParamDictionary dict = getParamDictionary();
        if (dict != null) {
            ENG_ParamCommand command = dict.getParamCommand(name);
            if (command != null) {
                return command.doGet(objThis);
            }
        }
        return "";
    }

    public void copyParametersTo(ENG_StringInterface dest) {
        ENG_ParamDictionary dict = getParamDictionary();
        if (dict != null) {
            ArrayList<ENG_ParameterDef> list = dict.getParameterList();
            for (ENG_ParameterDef def : list) {
                dest.setParameter(def.name, getParameter(def.name));
            }
        }
    }

    public void cleanupDictionary() {
        msDictionaryMutex.lock();
        try {
            msDictionary.clear();
        } finally {
            msDictionaryMutex.unlock();
        }
    }
}
