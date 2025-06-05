/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.scriptcompiler;

import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.renderer.ENG_ParticleAffector;
import headwayent.hotshotengine.renderer.ENG_ParticleEmitter;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;
import headwayent.hotshotengine.renderer.ENG_ParticleSystemManager;
import headwayent.hotshotengine.resource.ENG_Resource;

import java.io.DataInputStream;
import java.util.ArrayList;

public class ENG_ParticleCompiler extends ENG_AbstractCompiler<Void> {

    private static final String PARTICLE_SYSTEM = "particle_system";
    private static final String MATERIAL = "material";
    private static final String EMITTER = "emitter";
    private static final String AFFECTOR = "affector";

    private static int particleName;


    public static void parseParticleEmitter(DataInputStream fp0,
                                            ENG_ParticleSystem system) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        if (s == null) {
            throw new ENG_InvalidFormatParsingException();
        }
        ENG_ParticleEmitter emitter = system.addEmitter(s);
        s = ENG_CompilerUtil.getNextWord(fp0);
        if (s == null) {
            throw new ENG_InvalidFormatParsingException();
        }
        if (!s.equals(BRACKET_OPEN)) {
            throw new ENG_InvalidFormatParsingException();
        }
        incrementBracketLevel();
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            if (s == null) {
                throw new ENG_InvalidFormatParsingException();
            }
            if (s.equals(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
            String paramName = s;
            String paramValue = getParamValues(fp0);
            if (!emitter.getStringInterface().setParameter(paramName, paramValue)) {
                throw new ENG_InvalidFormatParsingException("param " + paramName +
                        " does not exist");
            }
        }
    }

    public static void parseParticleAffector(DataInputStream fp0,
                                             ENG_ParticleSystem system) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        if (s == null) {
            throw new ENG_InvalidFormatParsingException();
        }
        ENG_ParticleAffector affector = system.addAffector(s);
        s = ENG_CompilerUtil.getNextWord(fp0);
        if (s == null) {
            throw new ENG_InvalidFormatParsingException();
        }
        if (!s.equals(BRACKET_OPEN)) {
            throw new ENG_InvalidFormatParsingException();
        }
        incrementBracketLevel();
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            if (s == null) {
                throw new ENG_InvalidFormatParsingException();
            }
            if (s.equals(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
            String paramName = s;
            String paramValue = getParamValues(fp0);
            if (!affector.getStringInterface().setParameter(paramName, paramValue)) {
                throw new ENG_InvalidFormatParsingException("param " + paramName +
                        " does not exist");
            }
        }
    }

    public static ENG_ParticleSystem parseParticleSystem(DataInputStream fp0) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        boolean defName = false;
        String name;
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            name = String.valueOf(particleName++);
            defName = true;
        } else {
            name = s;
        }
        checkNameParsed(fp0, defName);
        ENG_ParticleSystem system =
                ENG_ParticleSystemManager.getSingleton().createTemplate(name);
        system.removeAllAffectors();
        system.removeAllEmitters();
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            if (s == null) {
                throw new ENG_InvalidFormatParsingException();
            }
            if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            } else if (s.equalsIgnoreCase(EMITTER)) {
                parseParticleEmitter(fp0, system);
            } else if (s.equalsIgnoreCase(AFFECTOR)) {
                parseParticleAffector(fp0, system);
            } else if (s.equalsIgnoreCase(MATERIAL)) {

                s = ENG_CompilerUtil.getNextWord(fp0);
                if (s == null) {
                    throw new ENG_InvalidFormatParsingException();
                }
                if (!system.getStringInterface().setParameter("material", s)) {
                    if (!system.getRenderer().getStringInterface().setParameter(
                            "material", s)) {
                        throw new ENG_InvalidFormatParsingException(
                                "Could not set material" +
                                        " parameter in particle system with material " + s);
                    }
                }
            } else {
                String paramName = s;
                s = ENG_CompilerUtil.getNextWord(fp0);
                if (s == null) {
                    throw new ENG_InvalidFormatParsingException();
                }
                String paramValue = s;
                if (!system.getStringInterface().setParameter(paramName, paramValue)) {
                    if (system.getRenderer() != null) {
                        if (!system.getRenderer().getStringInterface().setParameter(
                                paramName, paramValue)) {
                            throw new ENG_InvalidFormatParsingException("Could not" +
                                    " set parameter " + paramName + " with " +
                                    "value " + paramValue);
                        }
                    }
                }

            }
        }
        return system;
    }

    public Void /*ArrayList<ENG_ParticleSystem>*/ compileImpl(
            String filename, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(filename, path, fromSDCard);
            String s;
            String dir = null;
            String currentDir = "";
            //	ArrayList<ENG_ParticleSystem> list = new ArrayList<ENG_ParticleSystem>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(PARTICLE_SYSTEM)) {
                    parseParticleSystem(fp0);
                    //list.add();
                }
            }
            //	return list;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
        return null;
    }

    public static void loadParticleSystemsFromFile(String fileName, String path, boolean fromSDCard) {
        ArrayList<String> particleList =
                ENG_CompilerUtil.loadListFromFile(fileName, path);
        ENG_ParticleCompiler particleCompiler = new ENG_ParticleCompiler();
        for (String particleSystem : particleList) {
            String[] pathAndFileName =
                    ENG_CompilerUtil.getPathAndFileName(particleSystem);
            particleCompiler.compile(pathAndFileName[1], pathAndFileName[0], fromSDCard);
        }
    }
}
