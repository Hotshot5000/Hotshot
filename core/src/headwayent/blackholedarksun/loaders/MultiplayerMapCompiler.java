/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.loaders;

import headwayent.blackholedarksun.net.clientapi.tables.Map;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_AbstractCompiler;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.io.DataInputStream;
import java.util.ArrayList;

/**
 * Created by sebas on 17.11.2015.
 */
public class MultiplayerMapCompiler extends ENG_AbstractCompiler<ArrayList<Map>> {

    public static final String MAP = "map";
    public static final String ID = "id";
    public static final String LOCAL_ID = "local_id";
    public static final String MAP_NAME = "map_name";

    @Override
    public ArrayList<Map> compileImpl(String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            ArrayList<Map> mapList = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(MAP)) {
                    mapList.add(parseMap(fp0));
                }
            }
            return mapList;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    private Map parseMap(DataInputStream fp0) {
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;
            Map map = new Map();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(ID)) {
                    map.setId(getLong(fp0));
                } else if (s.equalsIgnoreCase(LOCAL_ID)) {
                    map.setLocalId(getLong(fp0));
                } else if (s.equalsIgnoreCase(MAP_NAME)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    map.setMapName(s);
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            return map;
        }
        throw new ENG_InvalidFormatParsingException();
    }
}
