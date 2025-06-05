/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.hotshotengine.resource.ENG_Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.TreeMultimap;

public class ENG_ConfigFile {

    private final TreeMap<String, TreeMultimap<String, String>> mSettings =
            new TreeMap<>();

    public ENG_ConfigFile() {

    }

    public void load(String filename, String path) {
        load(filename, path, "\t:=", true);
    }

    public void load(String filename, String path,
                     String separators) {
        load(filename, path, separators, true);
    }

    public void load(String filename, String path,
                     String separators, boolean trimWhitespaces) {
        load(ENG_Resource.getFileAsBufferedReader(filename, path), separators, trimWhitespaces);
    }

    public void load(BufferedReader reader) {
        load(reader, "\t:=", true);
    }

    public void load(BufferedReader reader, String separators) {
        load(reader, separators, true);
    }

    public void load(BufferedReader reader, String separators,
                     boolean trimWhitespaces) {
        clear();
        TreeMultimap<String, String> currentSettings = TreeMultimap.create();
        mSettings.put("", currentSettings);


        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty() &&
                        !line.startsWith("#") && !line.startsWith("@")) {
                    if (line.startsWith("[") && line.endsWith("]")) {
                        String currentSection =
                                line.substring(1, line.length() - 1);
                        TreeMultimap<String, String> foundSettings =
                                mSettings.get(currentSection);
                        if (foundSettings != null) {
                            currentSettings = foundSettings;
                        } else {
                            currentSettings = TreeMultimap.create();
                            mSettings.put(currentSection, currentSettings);
                        }
                    } else {
                        int ind = StringUtils.indexOfAny(line, separators);
                        if (ind != -1) {
                            String optName = line.substring(0, ind);
                            String sub = line.substring(ind);
                            int indexOfAnyBut =
                                    StringUtils.indexOfAnyBut(
                                            sub, separators);
                            String optVal = (indexOfAnyBut != -1) ?
                                    sub.substring(indexOfAnyBut) :
                                    "";
                            if (trimWhitespaces) {
                                optName = optName.trim();
                                optVal = optVal.trim();
                            }
                            currentSettings.put(optName, optVal);
                        }
                    }
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public String getSetting(String key, String section, String defaultValue) {
        TreeMultimap<String, String> multimap = mSettings.get(section);
        if (multimap != null) {
            NavigableSet<String> navigableSet = multimap.get(key);
            if (navigableSet != null) {
                return navigableSet.first();
            }
        }
        return defaultValue;
    }

    public ArrayList<String> getMultiSetting(String key, String section) {
        ArrayList<String> list = new ArrayList<>();
        TreeMultimap<String, String> multimap = mSettings.get(section);
        if (multimap != null) {
            NavigableSet<String> navigableSet = multimap.get(key);
            list.addAll(navigableSet);
        }
        return list;
    }

    public Iterator<Entry<String, String>> getSettingsIterator(String section) {
        TreeMultimap<String, String> multimap = mSettings.get(section);
        if (multimap == null) {
            throw new IllegalArgumentException(section + " is not a valid " +
                    "section");
        }
        return multimap.entries().iterator();
    }

    public Iterator<Entry<String, TreeMultimap<String, String>>>
    getSectionIterator() {
        return mSettings.entrySet().iterator();
    }

    public void clear() {
        mSettings.clear();
    }

}
