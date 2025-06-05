/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;
import java.util.HashMap;

public class ENG_AnimableObject {

    public final HashMap<String, ArrayList<String>> msAnimableDictionary =
            new HashMap<>();

    public String getAnimableDictionaryName() {
        return "";
    }

    public void createAnimableDictionary() {
        if (!msAnimableDictionary.containsKey(getAnimableDictionaryName())) {
            ArrayList<String> list = new ArrayList<>();
            initialiseAnimableDictionary(list);
            msAnimableDictionary.put(getAnimableDictionaryName(), list);
        }
    }

    public void initialiseAnimableDictionary(ArrayList<String> list) {
        

    }

    public ArrayList<String> _getAnimableValueNames() {
        ArrayList<String> list =
                msAnimableDictionary.get(getAnimableDictionaryName());
        if (list == null) {
            throw new IllegalArgumentException(
                    "Animable value list not found for " +
                            getAnimableDictionaryName());
        }
        return list;
    }

    public ArrayList<String> getAnimableValueNames() {
        createAnimableDictionary();
        ArrayList<String> list =
                msAnimableDictionary.get(getAnimableDictionaryName());
        if (list == null) {
            throw new IllegalArgumentException(
                    "Animable value list not found for " +
                            getAnimableDictionaryName());
        }
        return list;
    }

    public ENG_AnimableValue createAnimableValue(String name) {
        throw new UnsupportedOperationException();
    }
}
