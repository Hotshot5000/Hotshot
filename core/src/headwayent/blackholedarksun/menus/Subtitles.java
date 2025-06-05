/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/2/21, 11:05 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.Handler;
import headwayent.hotshotengine.Looper;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;

/**
 * For starters make sure that the text doesn't span multiple lines.
 */
public class Subtitles extends ENG_Container {

    public static class SubtitlesContainerFactory extends ENG_ContainerManager.ContainerFactory {

        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {

            return new Subtitles(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {

            c.destroy();
        }

    }

    private String text = "";
    private final ENG_TextView textView;
    private Handler subtitlesHandler;
    private boolean subtitlesShowing;

    public Subtitles(String name, Bundle bundle) {
        super(name, bundle);

        textView = (ENG_TextView) createView("text", "textview", 0.0f, 70.0f, 100.0f, 100.0f);

        textView.setText(text);

        // For now everything is only on one line and horizontally centred.
        textView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);

        textView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
    }

    @Override
    public void onRecreation(ENG_Container previousContainer) {
        super.onRecreation(previousContainer);
        recreateContainerListeners(previousContainer);
    }

    public static void showSubtitles(String text) {
        showSubtitles(text, 0);
    }

    public static void showSubtitles(String text, long showTime) {
        if (!(ENG_ContainerManager.getSingleton().getCurrentContainer() instanceof Subtitles)) {
            SimpleViewGameMenuManager.setCurrentMenu(SimpleViewGameMenuManager.SUBTITLES, true, false);
        }
        Subtitles subtitles = (Subtitles) ENG_ContainerManager.getSingleton().getCurrentContainer();
        subtitles.setText(text, showTime);
    }

    public static void hideSubtitles() {
        if (ENG_ContainerManager.getSingleton().getCurrentContainer() instanceof Subtitles) {
            Subtitles subtitles = (Subtitles) ENG_ContainerManager.getSingleton().getCurrentContainer();
            subtitles.removeSubtitlesIfShowing();
            subtitles.setText("");
            ENG_ContainerManager.getSingleton().removeCurrentContainer();
        }
    }

    public void removeSubtitlesIfShowing() {
        if (subtitlesShowing) {
            subtitlesHandler.removeCallbacksAndMessages(null);
            subtitlesShowing = false;
        }
    }

    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * @param duration if duration == 0 and there was no text shown previously it will simply show the text until changed
     *                 or until hideSubtitles() is called. If there was a text posted with duration > 0 and a new text
     *                 is posted with duration == 0 then the same already partially elapsed duration is used.
     *                 5 sec duration for first text then post text after 3 seconds means you have 2 more seconds
     *                 for the second message to be shown.
     */
    public void setText(String text, long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("duration cannot be < 0: " + duration);
        }
        if (duration > 0) {
            removeSubtitlesIfShowing();
            subtitlesShowing = true;
            subtitlesHandler = new Handler(Looper.getMainLooper());
            subtitlesHandler.postDelayed(() -> Subtitles.hideSubtitles(), duration);
        }
        setText(text);
    }

    public void setText(String text) {
        this.text = text;
        textView.setText(text);
    }
}
