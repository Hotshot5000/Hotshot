/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:45 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import headwayent.hotshotengine.input.ENG_InputConvertor;

/**
 * Created by sebas on 07.04.2016.
 * Used in order to know if the user clicked an in game button. Used with InGameInputConvertor.
 */
public abstract class ENG_ButtonListenerWithState implements ENG_IButtonListener {
    private boolean clicked;

    /**
     * onClick() is a misnomer. It gets called whether there was a click or there was a release of a previous click.
     * @param x
     * @param y
     * @param type
     */
    @Override
    public void onClick(float x, float y, ENG_InputConvertor.TouchEventType type) {
        if (type == ENG_InputConvertor.TouchEventType.DOWN || type == ENG_InputConvertor.TouchEventType.DOWNED
                || type == ENG_InputConvertor.TouchEventType.MOVE) {
            clicked = true;
        } else if (type == ENG_InputConvertor.TouchEventType.UP) {
            clicked = false;
        }
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void resetClicked() {
        clicked = false;
    }
}
