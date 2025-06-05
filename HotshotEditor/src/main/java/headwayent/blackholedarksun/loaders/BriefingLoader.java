/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package headwayent.blackholedarksun.loaders;

import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.util.ArrayList;

/**
 * @author sebi
 */
public class BriefingLoader {

    public static ArrayList<String> loadLevelTitleList(String fileName,
                                                       String path) {
        return ENG_CompilerUtil.loadListFromFileAsLines(
                fileName, path);
    }

    public static ArrayList<String> loadLevelMissionBriefingList(
            String fileName, String path) {
        ArrayList<String> levelList = ENG_CompilerUtil.loadListFromFileAsLines(
                fileName, path);
        ArrayList<String> ret = new ArrayList<>();
        for (String s : levelList) {
            String[] pathAndFileName = ENG_CompilerUtil.getPathAndFileName(s);
            ret.add(loadBriefing(pathAndFileName[1], pathAndFileName[0]));
        }
        return ret;
    }

    private static String loadBriefing(String fileName, String path) {

        return ENG_CompilerUtil.loadFileAsString(fileName, path);
    }
}
