/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:12 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.scriptcompiler;

import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.renderer.ENG_Font.FontType;
import headwayent.hotshotengine.resource.ENG_FontResource;
import headwayent.hotshotengine.resource.ENG_FontResource.GlyphStats;
import headwayent.hotshotengine.resource.ENG_Resource;

import java.io.DataInputStream;
import java.util.ArrayList;

public class ENG_FontCompiler extends ENG_AbstractCompiler<ArrayList<ENG_FontResource>> {

    private static final String TYPE = "type";
    private static final String SOURCE = "source";
    private static final String GLYPH = "glyph";

    private static final String IMAGE = "image";
    private static final String TRUE_TYPE = "truetype";


    public ArrayList<ENG_FontResource> compileImpl(
            String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            ArrayList<ENG_FontResource> res = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                ENG_FontResource font = new ENG_FontResource();
                res.add(font);
                font.name = s;
                s = ENG_CompilerUtil.getNextWord(fp0);
                if ((s != null) && (s.equalsIgnoreCase(BRACKET_OPEN))) {
                    incrementBracketLevel();
                } else {
                    throw new ENG_InvalidFormatParsingException();
                }
                boolean textureSourceSet = false;
                while (true) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(TYPE)) {
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        if (s.equalsIgnoreCase(IMAGE)) {
                            font.type = FontType.FT_IMAGE;
                        } else if (s.equalsIgnoreCase(TRUE_TYPE)) {
                            font.type = FontType.FT_TRUETYPE;
                        } else {
                            throw new ENG_InvalidFormatParsingException();
                        }
                    } else if (s.equalsIgnoreCase(SOURCE)) {
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        font.textureSource = ENG_CompilerUtil.trimFile(s);
                        textureSourceSet = true;
                    } else if (s.equalsIgnoreCase(GLYPH)) {
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        String glyph = s;

                        float u1 = getFloat(fp0);
                        float v1 = getFloat(fp0);
                        float u2 = getFloat(fp0);
                        float v2 = getFloat(fp0);

                        if (font.glyph == null) {
                            font.glyph = new ArrayList<>();
                        }
                        font.glyph.add(new GlyphStats(glyph, u1, v1, u2, v2));
                    } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                        decrementBracketLevel();
                        break;
                    }
                }
                if (!textureSourceSet) {
                    throw new ENG_InvalidFormatParsingException(
                            "texture source must be set");
                }
            }
            return res;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }
}
