/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/1/17, 6:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.renderer.ENG_GpuProgram.GpuProgramType;

import java.util.HashMap;
import java.util.TreeMap;

public class ENG_HighLevelGpuProgramManager {

//    private static ENG_HighLevelGpuProgramManager pm;

    protected final TreeMap<String, ENG_HighLevelGpuProgramFactory> mFactories = new TreeMap<>();

    /// Factory for dealing with programs for languages we can't create
    protected ENG_HighLevelGpuProgramFactory mNullFactory;
    /// Factory for unified high-level programs
    protected ENG_HighLevelGpuProgramFactory mUnifiedFactory;

    private final HashMap<String, ENG_HighLevelGpuProgram> programList = new HashMap<>();

    public ENG_HighLevelGpuProgramManager() {
//        if (pm == null) {
//            pm = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        pm = this;
    }

    public void destroyHighLevelGpuProgram(String name) {
        destroyHighLevelGpuProgram(name, false);
    }

    public void destroyHighLevelGpuProgram(String name, boolean skipGLDelete) {
        ENG_HighLevelGpuProgram remove = programList.remove(name);
        if (remove != null) {
            remove.destroy(skipGLDelete);
        } else {
            throw new IllegalArgumentException(name + " is not a valid " +
                    "HighLevelGpuProgram");
        }
    }

    public void destroyAllHighLevelGpuPrograms() {
        destroyAllHighLevelGpuPrograms(false);
    }

    public void destroyAllHighLevelGpuPrograms(boolean skipGLDelete) {
        for (ENG_HighLevelGpuProgram prg : programList.values()) {
            prg.destroy(skipGLDelete);
        }
        programList.clear();
    }

    public ENG_HighLevelGpuProgram getByName(String name) {
        return programList.get(name);
    }

    public ENG_HighLevelGpuProgram createProgram(String name, String language,
                                                 int gptype) {
        return createProgram(name, language,
                GpuProgramType.getGpuProgramType(gptype));
    }

    public ENG_HighLevelGpuProgram createProgram(String name, String language,
                                                 GpuProgramType gtype) {
        ENG_HighLevelGpuProgram prg;
        //	prg = programList.get(name);
        //	if (prg == null) {
        prg = getFactory(language).create(name);
        prg.setType(gtype);
        prg.setSyntaxCode(language);
        ENG_HighLevelGpuProgram program = programList.put(name, prg);
        if (program != null) {
            throw new IllegalArgumentException(name + " is already in the high level " +
                    "program list");
        }
        //	}
        return prg;
    }

    public void addFactory(ENG_HighLevelGpuProgramFactory factory) {
        mFactories.put(factory.getLanguage(), factory);
    }

    public void removeFactory(ENG_HighLevelGpuProgramFactory factory) {
        mFactories.remove(factory.getLanguage());
    }

    public ENG_HighLevelGpuProgramFactory getFactory(String language) {
        ENG_HighLevelGpuProgramFactory factory = mFactories.get(language);
        if (factory == null) {
            throw new IllegalArgumentException("Language not supported!");
        }
        return factory;
    }

    public boolean isLanguageSupported(String language) {
        return mFactories.containsKey(language);
    }

    public static ENG_HighLevelGpuProgramManager getSingleton() {
//        if (MainActivity.isDebugmode() && (pm == null)) {
//            throw new NullPointerException();
//        }
//        return pm;
        return MainApp.getGame().getRenderRoot().getHighLevelGpuProgramManager();
    }
}
