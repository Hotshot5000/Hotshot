/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import headwayent.blackholedarksun.parser.ast.Cutscene;
import headwayent.hotshotengine.resource.ENG_Resource;
import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.Symbol;

/**
 * This thing exists only for the Lexer class to be in the same package since it's not public.
 */
public class CutsceneLoader {

    /** @noinspection deprecation*/
    public static Cutscene loadCutscene(String file, String path) {
        try {
            File cutsceneFile = ENG_Resource.getFile(file, path);
            parser parser = new parser(new headwayent.blackholedarksun.parser.Lexer(new FileReader(cutsceneFile)), new DefaultSymbolFactory());
            Symbol parse = parser.debug_parse();
            Cutscene cutscene = (Cutscene) parse.value;
            return cutscene;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
