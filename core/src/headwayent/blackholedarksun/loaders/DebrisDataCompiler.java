/*
 * Created by Sebastian Bugiu on 08/04/2025, 21:52
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 08/04/2025, 21:52
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.loaders;

import java.io.DataInputStream;
import java.util.ArrayList;

import headwayent.blackholedarksun.entitydata.AsteroidData;
import headwayent.blackholedarksun.entitydata.DebrisData;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_AbstractCompiler;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

public class DebrisDataCompiler extends ENG_AbstractCompiler<ArrayList<DebrisData>> {

    private static final String LIFETIME = "lifetime";
    private static final String OBJECT_TYPE = "type";
    private static final String DEBRIS_SIZE = "size";
    public static final String DEBRIS_DATA = "debris_data";
    public static final String OBJECT = "obj";

    @Override
    public ArrayList<DebrisData> compileImpl(String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            ArrayList<DebrisData> debrisDataList = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(EntityDataCompiler.DATA)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(DEBRIS_DATA)) {
                        parseDataList(fp0, debrisDataList);
                    }
                }
            }
            return debrisDataList;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    private void parseDataList(DataInputStream fp0, ArrayList<DebrisData> debrisDataList) {
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;

            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(OBJECT)) {
                    debrisDataList.add(parseDebrisData(fp0));
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
        }
    }

    private DebrisData parseDebrisData(DataInputStream fp0) {
        DebrisData debrisData = new DebrisData();
        String objName = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(objName);
        debrisData.inGameName = objName;
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (EntityDataCompiler.parseEntityData(fp0, s, debrisData)) {
                } else if (s.equalsIgnoreCase(LIFETIME)) {
                    debrisData.lifetime = getLong(fp0);
                } else if (s.equalsIgnoreCase(OBJECT_TYPE)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    ENG_AbstractCompiler.checkNull(s);
                    debrisData.type = LevelObject.LevelObjectType.getLevelObjectType(s);
                } else if (s.equalsIgnoreCase(DEBRIS_SIZE)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    ENG_AbstractCompiler.checkNull(s);
                    debrisData.debrisType = DebrisData.DebrisType.getType(s);
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            return debrisData;
        }
        throw new ENG_InvalidFormatParsingException();
    }
}
