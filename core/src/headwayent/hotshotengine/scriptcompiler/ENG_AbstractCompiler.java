/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/14/18, 1:56 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.scriptcompiler;

import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.exception.ENG_ParsingException;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.io.DataInputStream;
import java.io.File;

public abstract class ENG_AbstractCompiler<T> {

    protected static final String BRACKET_OPEN = "{";
    protected static final String BRACKET_CLOSE = "}";
    private static int bracketLevel;

    public T compile(String fileName, String path, boolean fromSDCard) {
        long startTime = ENG_Utility.currentTimeMillis();
        T result = compileImpl(fileName, path, fromSDCard);
        long endTime = ENG_Utility.currentTimeMillis() - startTime;
        System.out.println("Compiling " + path + File.separator + fileName + " took " + endTime + " millis");
        return result;
    }

    public abstract T compileImpl(String fileName, String path, boolean fromSDCard);

    protected static void incrementBracketLevel(String s, String errorMsg) {
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException(errorMsg);
        }
    }


    protected static void incrementBracketLevel() {
        ++bracketLevel;
    }

    protected static void decrementBracketLevel() {
        --bracketLevel;
        if (bracketLevel < 0) {
            throw new ENG_ParsingException();
        }
    }

    protected static void checkNameParsed(DataInputStream fp0, boolean defName) {
        if (!defName) {
            String s = ENG_CompilerUtil.getNextWord(fp0);
            if (!s.equalsIgnoreCase(BRACKET_OPEN)) {
                throw new ENG_InvalidFormatParsingException();
            }
        }
        incrementBracketLevel();
    }

    public ENG_AbstractCompiler() {
        super();
    }

    public static ENG_Vector4D getVector3D(DataInputStream fp0) {
        ENG_Vector4D ret = new ENG_Vector4D();
        getVector3D(fp0, ret);
        return ret;
    }

    public static void getVector3D(DataInputStream fp0, ENG_Vector4D ret) {
        ret.x = getFloat(fp0);
        ret.y = getFloat(fp0);
        ret.z = getFloat(fp0);
    }

    public ENG_ColorValue getColourValue(DataInputStream fp0) {
        ENG_ColorValue colorValue = new ENG_ColorValue();
        getColourValue(fp0, colorValue);
        return colorValue;
    }

    public static void getColourValue(DataInputStream fp0, ENG_ColorValue col) {
        col.r = getFloat(fp0);
        col.g = getFloat(fp0);
        col.b = getFloat(fp0);
        col.a = getFloat(fp0);
    }

    public static void getQuaternionDeg(DataInputStream fp0, ENG_Quaternion ret) {
        float x = getFloat(fp0);
        float y = getFloat(fp0);
        float z = getFloat(fp0);
        float angle = getFloat(fp0);
        ENG_Quaternion.fromAngleAxisDeg(angle,
                new ENG_Vector4D(x, y, z, 0.0f).normalizedCopy(), ret);
    }

    public static void getQuaternionRad(DataInputStream fp0, ENG_Quaternion ret) {
        float x = getFloat(fp0);
        float y = getFloat(fp0);
        float z = getFloat(fp0);
        float angle = getFloat(fp0);
        ENG_Quaternion.fromAngleAxisRad(angle,
                new ENG_Vector4D(x, y, z, 0.0f).normalizedCopy(), ret);
    }

    public static String getParamValues(DataInputStream fp0) {
        // Since we have no idea how many param values the paramName will have
        // the script creator just has to use the first param as number of
        // params to read ahead
        String s = ENG_CompilerUtil.getNextWord(fp0);
        if (s == null) {
            throw new ENG_InvalidFormatParsingException();
        }
        int paramCount;
        try {
            paramCount = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ENG_InvalidFormatParsingException();
        }
        if (paramCount <= 0) {
            throw new ENG_InvalidFormatParsingException("paramCount must be larger" +
                    " than 0");
        }
        StringBuilder paramValue = null;
        if (paramCount == 1) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            if (s == null) {
                throw new ENG_InvalidFormatParsingException();
            }
            paramValue = new StringBuilder(s);
        } else {
            for (int i = 0; i < paramCount; ++i) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                if (s == null) {
                    throw new ENG_InvalidFormatParsingException();
                }
                if (i == 0) {
                    paramValue = new StringBuilder(s);
                } else {
                    paramValue.append(" ").append(s);
                }
            }
        }
        return paramValue.toString();
    }

    public static void checkNull(String s) {
        if (s == null) {
            throw new ENG_InvalidFormatParsingException();
        }
    }

    public static float getFloat(DataInputStream fp0) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new ENG_InvalidFormatParsingException();
        }
    }

    public static int getInt(DataInputStream fp0) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ENG_InvalidFormatParsingException();
        }
    }

    public static long getLong(DataInputStream fp0) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new ENG_InvalidFormatParsingException();
        }
    }

    /**
     * 0 or false is false anything different from 0 or true is true
     *
     * @param fp0
     * @return
     */
    public static boolean getBoolean(DataInputStream fp0) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase("true")) {
            return true;
        } else if (s.equalsIgnoreCase("false")) {
            return false;
        } else {
            try {
                return Integer.parseInt(s) != 0;
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
        }
    /*	try {
			return Boolean.parseBoolean(s);
		} catch (NumberFormatException e) {
			throw new ENG_InvalidFormatParsingException();
		}*/
    }


}