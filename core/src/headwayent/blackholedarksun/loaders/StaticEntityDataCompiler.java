/*
 * Created by Sebastian Bugiu on 08/04/2025, 21:53
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 08/04/2025, 21:53
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.loaders;

import java.io.DataInputStream;
import java.util.ArrayList;

import headwayent.blackholedarksun.entitydata.DebrisData;
import headwayent.blackholedarksun.entitydata.StaticEntityData;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_AbstractCompiler;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

public class StaticEntityDataCompiler extends ENG_AbstractCompiler<ArrayList<StaticEntityData>> {

    public static final String DEBRIS_DATA = "static_entity_data";
    public static final String OBJECT = "obj";

    @Override
    public ArrayList<StaticEntityData> compileImpl(String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            ArrayList<StaticEntityData> staticEntityDataList = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(EntityDataCompiler.DATA)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(DEBRIS_DATA)) {
                        parseDataList(fp0, staticEntityDataList);
                    }
                }
            }
            return staticEntityDataList;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    private void parseDataList(DataInputStream fp0, ArrayList<StaticEntityData> staticEntityDataList) {
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;

            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(OBJECT)) {
                    staticEntityDataList.add(parseDebrisData(fp0));
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
        }
    }

    private StaticEntityData parseDebrisData(DataInputStream fp0) {
        StaticEntityData staticEntityData = new StaticEntityData();
        String objName = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(objName);
        staticEntityData.inGameName = objName;
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (EntityDataCompiler.parseEntityData(fp0, s, staticEntityData)) {
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            return staticEntityData;
        }
        throw new ENG_InvalidFormatParsingException();
    }
}
