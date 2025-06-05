/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/4/22, 4:56 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.hotshotengine.renderer.ENG_Font;
import headwayent.hotshotengine.renderer.ENG_FontManager;
import headwayent.hotshotengine.resource.ENG_FontResource;
import headwayent.hotshotengine.resource.ENG_FontResource.GlyphStats;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;
import headwayent.hotshotengine.scriptcompiler.ENG_FontCompiler;

import java.util.ArrayList;

@Deprecated
public class ENG_FontLoader {

    public static void loadFontList(String fileName, String path, boolean fromSDCard) {
        ArrayList<String> materialList =
                ENG_CompilerUtil.loadListFromFile(fileName, path);

        for (String mat : materialList) {
            String[] pathAndFileName = ENG_CompilerUtil.getPathAndFileName(mat);
            loadFont(pathAndFileName[1], pathAndFileName[0], fromSDCard);
        }
    }

    public static void loadFont(String fileName, String path, boolean fromSDCard) {
        loadCompiledResource(new ENG_FontCompiler().compile(fileName, path, fromSDCard));
    }

    private static void loadCompiledResource(ArrayList<ENG_FontResource> res) {
        for (ENG_FontResource fontRes : res) {
            ENG_Font font = ENG_FontManager.getSingleton().create(fontRes.name);
            font.setSource(fontRes.textureSource);
            font.setType(fontRes.type);
            if (fontRes.glyph != null) {
                for (GlyphStats g : fontRes.glyph) {
                    font.setGlyphTexCoords(g.c.charAt(0), g.u1, g.v1, g.u2, g.v2, 1.0f);
                }
            }
            font.load();
        }
    }
}
