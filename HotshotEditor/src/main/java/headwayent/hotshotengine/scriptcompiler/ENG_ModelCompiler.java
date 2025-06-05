/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.scriptcompiler;

import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.resource.ENG_ModelLink;
import headwayent.hotshotengine.resource.ENG_ModelResource;
import headwayent.hotshotengine.resource.ENG_Resource;

import java.io.DataInputStream;
import java.util.ArrayList;

/**
 * Created by sebas on 20.10.2015.
 */
public class ENG_ModelCompiler extends ENG_AbstractCompiler<ArrayList<ENG_ModelResource>> {

    public static final String MODEL = "model";
    public static final String MODEL_FILE = "model_file";
    public static final String MATERIAL_FILE = "material_file";
    public static final String LINK = "link";
    public static final String LINK_GROUP = "group";

    private static ENG_ModelLink parseModelLink(DataInputStream fp0) {
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;
            ENG_ModelLink modelLink = new ENG_ModelLink();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(LINK_GROUP)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    modelLink.linkGroup = s;
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    modelLink.linkMaterial = s;
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            return modelLink;
        }
        return null;
    }

    private static ENG_ModelResource parseModel(DataInputStream fp0, String currentDir, boolean fromSDCard, String modelName) {
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;
            String dir;
            ENG_ModelResource modelResource = new ENG_ModelResource();
            modelResource.name = modelName;
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                dir = ENG_CompilerUtil.checkDirChange(s, fp0);
                if (dir != null) {
                    currentDir = dir;
                }
                if (s.equalsIgnoreCase(MODEL_FILE)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    modelResource.modelFilename = currentDir + s;
                } else if (s.equalsIgnoreCase(MATERIAL_FILE)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    modelResource.materialFilename = currentDir + s;
                } else if (s.equalsIgnoreCase(LINK)) {
                    modelResource.linkList.add(parseModelLink(fp0));
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            return modelResource;
        }
        return null;
    }

    public ArrayList<ENG_ModelResource> compileImpl(String fileName, String path,
                                                    boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            String dir;
            String currentDir = "";
            ArrayList<ENG_ModelResource> modelResources = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                dir = ENG_CompilerUtil.checkDirChange(s, fp0);
                if (dir != null) {
                    currentDir = dir;
                }
                if (s.equalsIgnoreCase(MODEL)) {
                    String modelName = ENG_CompilerUtil.getNextWord(fp0);
                    if (modelName != null) {
                        modelResources.add(parseModel(fp0, currentDir, fromSDCard, modelName));
                    } else {
                        throw new ENG_InvalidFormatParsingException();
                    }
                }
            }
            return modelResources;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }


}
