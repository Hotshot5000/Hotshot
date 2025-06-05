/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/24/21, 11:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.audio.ENG_SoundManager;
import headwayent.hotshotengine.resource.ENG_CompiledSound;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;
import headwayent.hotshotengine.scriptcompiler.ENG_SoundCompiler;

import java.io.File;
import java.util.ArrayList;

public class ENG_SoundLoader {


    public static void loadSoundList(String fileName, String path, boolean fromSDCard) {
        ArrayList<ENG_CompiledSound> compiledSound = new ENG_SoundCompiler().compile(fileName, path, fromSDCard);
        for (ENG_CompiledSound snd : compiledSound) {
            String[] pathAndFileName = ENG_CompilerUtil.getPathAndFileName(snd.filename);
            loadSound(snd.name, snd.duration, snd.priority, pathAndFileName[1], pathAndFileName[0], fromSDCard);
        }
    }

    public static void loadSound(String name, long duration, int priority, String fileName, String path, boolean fromSDCard) {
        if (fromSDCard) {
            String filename = null;
            if (ENG_SoundManager.isMiniAudioBased()) {
                if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
                    filename = (path + File.separator + fileName);
                    filename = filename.replace("\\", "/");
                } else if (MainApp.PLATFORM == MainApp.Platform.ANDROID || MainApp.PLATFORM == MainApp.Platform.IOS) {
                    filename = (path + File.separator + fileName).substring(4);
                }
            } else {
                filename = path + File.separator + fileName;
            }
            MainApp.getGame().getSound().loadSound(name, filename, duration, priority);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
