package headwayent.blackholedarksun.loaders;

import java.io.DataInputStream;
import java.util.ArrayList;

import headwayent.blackholedarksun.entitydata.AsteroidData;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_AbstractCompiler;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

/**
 * Created by sebas on 02-Oct-17.
 */

public class AsteroidDataCompiler extends ENG_AbstractCompiler<ArrayList<AsteroidData>> {

    public static final String ASTEROID_DATA = "asteroid_data";
    public static final String OBJECT = "obj";

    @Override
    public ArrayList<AsteroidData> compileImpl(String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            ArrayList<AsteroidData> asteroidDataList = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(EntityDataCompiler.DATA)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(ASTEROID_DATA)) {
                        parseDataList(fp0, asteroidDataList);
                    }
                }
            }
            return asteroidDataList;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    private void parseDataList(DataInputStream fp0, ArrayList<AsteroidData> asteroidDataList) {
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;

            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(OBJECT)) {
                    asteroidDataList.add(parseAsteroidData(fp0));
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
        }
    }

    private AsteroidData parseAsteroidData(DataInputStream fp0) {
        AsteroidData asteroidData = new AsteroidData();
        String objName = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(objName);
        asteroidData.inGameName = objName;
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (EntityDataCompiler.parseEntityData(fp0, s, asteroidData)) {
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            return asteroidData;
        }
        throw new ENG_InvalidFormatParsingException();
    }
}
