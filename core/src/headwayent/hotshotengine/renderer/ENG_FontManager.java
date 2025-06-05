/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_FontManagerNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointer;

import java.util.TreeMap;

public class ENG_FontManager implements ENG_IDisposable, ENG_NativePointer {

    private final ENG_FontManagerNativeWrapper wrapper;
    private final TreeMap<String, ENG_Font> map = new TreeMap<>();
//    private static ENG_FontManager fontManager;

    public ENG_FontManager() {
        wrapper = new ENG_FontManagerNativeWrapper();
//        if (fontManager == null) {
//            fontManager = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
    }

    public ENG_Font getByName(String name) {
        return map.get(name);
    }

    public ENG_Font create(String name) {
        ENG_Font font = getByName(name);
        if (font == null) {
            font = createImpl(name);
        }
        return font;
    }

    public void destroyFont(String name) {
        ENG_Font font = map.remove(name);
        if (font == null) {
            throw new IllegalArgumentException(name + " is not in the font list");
        }
    }

    public void destroyAllFonts() {
        map.clear();
    }

    protected ENG_Font createImpl(String name) {
        ENG_Font font = new ENG_Font(name);
        map.put(name, font);
        return font;
    }

    public static ENG_FontManager getSingleton() {
//        if (fontManager == null && MainApp.DEV) {
//            throw new NullPointerException("font manager not initialized");
//        }
//        return fontManager;
        return MainApp.getGame().getRenderRoot().getFontManager();
    }

    @Override
    public void destroy() {

    }

    @Override
    public long getPointer() {
        return wrapper.getPtr();
    }
}
