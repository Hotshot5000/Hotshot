/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.effects;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.renderer.ENG_Overlay;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;

import java.util.ArrayList;

public class LensFlare {

    public static class FlareData {
        public final String name;
        public ENG_OverlayElement element;
        // Between -1.0 and 1.0. -1.0 means above the lightpos 1.0 means symmetrical on
        // the other side of the centre.
        public final float lenPos;

        public FlareData(String name, float len) {
            this.name = name;
            lenPos = len;
        }
    }

    private final ENG_Overlay overlay;
    private final ArrayList<FlareData> flareList;
    private final ENG_Vector2D centre = new ENG_Vector2D(0.5f);
    private final ENG_Vector2D diff = new ENG_Vector2D();
    private final ENG_Vector2D currentPos = new ENG_Vector2D();

    public LensFlare(ENG_Overlay overlay, ArrayList<FlareData> flares) {
        this.overlay = overlay;
        for (FlareData data : flares) {
            if (data.name != null) {
                ENG_OverlayElement elem = overlay.getChild(data.name);
                if (elem == null) {
                    throw new IllegalArgumentException(data.name + " cannot be found " +
                            "in overlay " + overlay.getName());
                }
                data.element = elem;
            }
        }
        flareList = flares;
    }

    public void lightPos(float x, float y) {
        lightPos(new ENG_Vector2D(x, y));
    }

    public void lightPos(ENG_Vector2D pos) {
        centre.sub(pos, diff);
//		GLRenderSurface renderSurface = GLRenderSurface.getSingleton();
        ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
        int width = window.getWidth();
        int height = window.getHeight();
        for (FlareData data : flareList) {
            currentPos.set(diff);
            currentPos.mulInPlace(data.lenPos);
            currentPos.addInPlace(centre);
            // Translate the position from screen space to pixel space

            data.element.setLeft(ENG_Math.floor(currentPos.x * width) -
                    data.element.getWidth() * 0.5f);
            data.element.setTop(ENG_Math.floor(currentPos.y * height) -
                    data.element.getHeight() * 0.5f);
        }
    }

    public void show() {
        overlay.show();
    }

    public void hide() {
        overlay.hide();
    }

    public void show(int flareNum) {
        if (flareNum < 0 || flareNum >= flareList.size()) {
            throw new IllegalArgumentException("Invalid flare index. Only " +
                    flareList.size() + " flares in list");
        }
        flareList.get(flareNum).element.show();
    }

    public void hide(int flareNum) {
        if (flareNum < 0 || flareNum >= flareList.size()) {
            throw new IllegalArgumentException("Invalid flare index. Only " +
                    flareList.size() + " flares in list");
        }
        flareList.get(flareNum).element.hide();
    }
}
