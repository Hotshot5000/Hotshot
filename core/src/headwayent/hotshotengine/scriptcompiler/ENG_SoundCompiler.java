/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/21, 1:53 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.scriptcompiler;

import headwayent.hotshotengine.resource.ENG_CompiledSound;
import headwayent.hotshotengine.resource.ENG_Resource;

import java.io.DataInputStream;
import java.util.ArrayList;

public class ENG_SoundCompiler extends ENG_AbstractCompiler<ArrayList<ENG_CompiledSound>> {

    public ArrayList<ENG_CompiledSound> compileImpl(
            String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            ArrayList<ENG_CompiledSound> list = new ArrayList<>();
            String s = null;
            while (true) {
                String name = ENG_CompilerUtil.getNextWord(fp0);
                String filename = ENG_CompilerUtil.getNextWord(fp0);
                if (name != null && filename != null) {
                    int priority = getInt(fp0);
                    long duration = getLong(fp0);
                    list.add(new ENG_CompiledSound(name, filename, duration, priority));
                } else {
                    break;
                }
            }
            return list;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }
}
