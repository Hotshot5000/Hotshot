/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/26/21, 12:33 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import headwayent.blackholedarksun.input.InGameInputConvertorListener;
import headwayent.blackholedarksun.menus.language.Language;
import headwayent.blackholedarksun.net.clientapi.tables.Map;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.hotshotengine.SharedPreferences;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Sebastian on 21.04.2015.
 */
public class Preferences {

    public static final String PREF_MAP_LIST = "mapList";
    public static final String PREF_USER = "user";
    public static final String PREF_DATA_DOWNLOADED = "data_downloaded";
    public static final String PREF_DOWNLOADED_DATA_VERSION = "downloaded_data_version";
    public static final String PREF_GAME_RESOURCES_VERSION = "game_resources_version";
    public static final String PREF_MULTIPLAYER_ALLOWED = "multiplayer_allowed";
    public static final String PREF_DATA_UNPACKED = "data_unpacked";
    public static final String PREF_DATA_COPIED = "data_copied";
    public static final String PREF_LAST_SHUTDOWN_SUCCESSFUL = "data_copied";
    public static final String PREF_SHIP_SENSITIVITY = "ship_sensitivity";
    public static final String PREF_AIM_ASSIST = "aim_assist";
    public static final String PREF_LANGUAGE = "language";

    private final Gson gson;
    private final SharedPreferences prefs;
    /** @noinspection UnstableApiUsage */
    private final Type mapListType = new TypeToken<List<Map>>() {
    }.getType();

    public Preferences(SharedPreferences prefs, Gson gson) {
        this.prefs = prefs;
        this.gson = gson;
    }

    public void setMapList(List<Map> mapList) {
        String s = gson.toJson(mapList, mapListType);
        commitString(PREF_MAP_LIST, s);
    }

    public List<Map> getMapList() {
        String string = getPreference(PREF_MAP_LIST);
        if (string == null) {
            return null;
        }
        return gson.fromJson(string, mapListType);
    }

    public void setUser(User user) {
        String s = gson.toJson(user);
        commitString(PREF_USER, s);
    }

    public User getUser() {
        String s = getPreference(PREF_USER);
        if (s == null) {
            return null;
        }
        return gson.fromJson(s, User.class);
    }

    public void setDataDownloaded(boolean b) {
        prefs.edit().putBoolean(PREF_DATA_DOWNLOADED, b).commit();
    }

    public boolean isDataDownloaded() {
        return prefs.getBoolean(PREF_DATA_DOWNLOADED, false);
    }

    public void setDownloadedDataVersion(int version) {
        prefs.edit().putInt(PREF_DOWNLOADED_DATA_VERSION, version).commit();
    }

    public int getDownloadedDataVersion() {
        return prefs.getInt(PREF_DOWNLOADED_DATA_VERSION, -1);
    }

    /**
     * This is only for development versions in order to update the game data live.
     * @param version
     */
    public void setGameResourcesVersion(int version) {
        prefs.edit().putInt(PREF_GAME_RESOURCES_VERSION, version).commit();
    }

    /**
     * This is only for development versions in order to update the game data live.
     * @return
     */
    public int  getGameResourcesVersion() {
        return prefs.getInt(PREF_GAME_RESOURCES_VERSION, -1);
    }

    public void setMultiplayerAllowed(boolean b) {
        prefs.edit().putBoolean(PREF_MULTIPLAYER_ALLOWED, b).commit();
    }

    public boolean isMultiplayerAllowed() {
        return prefs.getBoolean(PREF_MULTIPLAYER_ALLOWED, false);
    }

    public void setDataUnpacked(boolean b) {
        prefs.edit().putBoolean(PREF_DATA_UNPACKED, b).commit();
    }

    public boolean isDataUnpacked() {
        return prefs.getBoolean(PREF_DATA_UNPACKED, false);
    }

    public void setDataCopied(boolean b) {
        prefs.edit().putBoolean(PREF_DATA_COPIED, b).commit();
    }

    public boolean isDataCopied() {
        return prefs.getBoolean(PREF_DATA_COPIED, false);
    }

    public void setLastShutdownSuccessful(boolean b) {
        prefs.edit().putBoolean(PREF_LAST_SHUTDOWN_SUCCESSFUL, b).commit();
    }

    public boolean isLastShutdownSuccessful() {
        return prefs.getBoolean(PREF_LAST_SHUTDOWN_SUCCESSFUL, false);
    }

    public void setShipSensitivity(float sensitivity) {
        prefs.edit().putFloat(PREF_SHIP_SENSITIVITY, sensitivity).commit();
    }

    public float getShipSensitivity() {
        return prefs.getFloat(PREF_SHIP_SENSITIVITY, InGameInputConvertorListener.DEFAULT_SHIP_SENSITIVITY);
    }

    public void setAimAssistEnabled(boolean aimAssist) {
        prefs.edit().putBoolean(PREF_AIM_ASSIST, aimAssist).commit();
    }

    public boolean isAimAssistEnabled() {
        return prefs.getBoolean(PREF_AIM_ASSIST, true);
    }

    public void setLanguage(String language) {
        prefs.edit().putString(PREF_LANGUAGE, language);
    }

    public String getLanguage() {
        return prefs.getString(PREF_LANGUAGE, Language.SupportedLanguages.ENGLISH.getLanguage());
    }

    private String getPreference(String pref) {
        return prefs.getString(pref, null);
    }

    private void commitString(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }
}
