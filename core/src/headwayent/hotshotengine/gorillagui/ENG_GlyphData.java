/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import java.util.ArrayList;

public class ENG_GlyphData {

    final ArrayList<ENG_Glyph> mGlyphs = new ArrayList<>();
    public int mRangeBegin, mRangeEnd;
    public float mSpaceLength,
            mLineHeight,
            mBaseline,
            mLetterSpacing,
            mMonoWidth;

    ENG_GlyphData() {
        
    }

    ENG_Glyph getGlyph(int ch) {
        int sc = ch - mRangeBegin;
        if (sc >= 0 && sc < mGlyphs.size()) {
            return mGlyphs.get(sc);
        }
        return null;
    }


}
