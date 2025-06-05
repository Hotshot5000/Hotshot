/*
 * Created by Sebastian Bugiu on 13/04/2025, 11:35
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 13/04/2025, 11:35
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.ConsoleCmdHandler;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnKeyCodeListenerWithType;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Log;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TextField;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_Viewport;

public class Console extends ENG_Container {

//    private final ENG_Button back;
    private final ENG_TextView console;
    private boolean consoleInitialized;
    private boolean shouldForceConsoleUpdate;

    private final ConsoleCmdHandler.ConsoleCmdHandlerListener consoleListener = new ConsoleCmdHandler.ConsoleCmdHandlerListener() {
        @Override
        public void notifyConsoleTextUpdated() {
            if (!consoleInitialized) return;
            setConsoleText();
        }
    };

    public static class ConsoleContainerFactory extends ENG_ContainerManager.ContainerFactory {

        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new Console(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    public Console(String name, Bundle bundle) {
        super(name, bundle);

        console = (ENG_TextView) createView("console", "textview", 0.0f, 0.0f, 100.0f, 44.0f);
        ENG_TextField inputField = (ENG_TextField) createView("consoleInput", "textfield", 0.0f, 45.0f, 90.0f, 50.0f);

//        back = (ENG_Button) createView("back", "button", 91.0f, 42.0f, 100.0f, 50.0f);
        ENG_Button goToLast = (ENG_Button) createView("goToLast", "button", 91.0f, 45.0f, 100.0f, 50.0f);

        ConsoleCmdHandler cmdHandler = ConsoleCmdHandler.getInstance();
        console.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        inputField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
//        back.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        goToLast.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        console.setTextColor(ENG_ColorValue.WHITE);
        console.setBackgroundColor(ENG_ColorValue.BLACK);
        console.setBackgroundActive(true);
        console.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);

//        back.setTextColor(ENG_ColorValue.WHITE);
//        back.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
//        back.setText("back");

        goToLast.setTextColor(ENG_ColorValue.WHITE);
        goToLast.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        goToLast.setText("Go to last");

        setConsoleText();

        inputField.setAllowSpaceKey(true);
        setCurrentFocusedView(inputField);

        inputField.setOnTabPressedListener(text -> {
//            ENG_Log.getInstance().log(text, ENG_Log.TYPE_MESSAGE);
            String autoCompleteNext = cmdHandler.getAutoCompleteNext(text);
            if (autoCompleteNext != null) {
                inputField.setText(autoCompleteNext);
            }
        });

        inputField.setOnBackspacePressedListener(text -> {
            cmdHandler.resetLastCommandPosition();
            cmdHandler.resetAutoComplete();
        });

        inputField.setOnUpDownPressedListener(new ENG_TextField.ENG_TextFieldUpDownPressedListener() {
            @Override
            public void onUpPressed(String text) {
                String lastCommand = cmdHandler.getLastCommandBackward();
                if (lastCommand != null) {
                    inputField.setText(lastCommand);
                    cmdHandler.resetAutoComplete();
                }
            }

            @Override
            public void onDownPressed(String text) {
                String lastCommand = cmdHandler.getLastCommandForward();
                if (lastCommand != null) {
                    inputField.setText(lastCommand);
                    cmdHandler.resetAutoComplete();
                }
            }
        });

        inputField.setOnReturnPressedListener(text -> {
            System.out.println("return pressed: " + text);
            String output = cmdHandler.handleCommand(text);
            System.out.println("Command output: " + output);
            // Gets updated via the listener.
//            console.setText(cmdHandler.getOutput(), false);
            inputField.setText("");
            cmdHandler.resetAutoComplete();

//            forceConsoleUpdate();
        });

        goToLast.setOnClickListener((x, y) -> {
            shouldForceConsoleUpdate = true;
//            forceConsoleUpdate();
            return true;
        });

        consoleInitialized = true;
    }

    public void setConsoleText() {
        console.setText(ConsoleCmdHandler.getInstance().getOutput(), false);
        shouldForceConsoleUpdate = true;
//        forceConsoleUpdate();
    }

    public void forceConsoleUpdate() {
        if (!shouldForceConsoleUpdate) return;
        // Hack to advance the line so we are always on the last line. This is really stupid....
        // TODO there is still a bug lurking here, besides the terrible performance. The console still doesn't always go to the last line.
        ENG_Viewport viewport = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getViewport(0);
        int screenWidth = viewport.getActualWidth();
        int screenHeight = viewport.getActualHeight();
        console.update(screenWidth, screenHeight); // Force an update so that the next line is never null;
        if (console._getMarkupText() == null) return; // update() has never been called yet. Should never return anymore.
        int lineNum = console._getMarkupText().getLineNum();// Force line break recalculation.
        for (int i = 0; i < lineNum; ++i) {
            console._getMarkupText().nextLine();
            console.update(screenWidth, screenHeight);
        }
        shouldForceConsoleUpdate = false;
//        System.out.println("forceConsoleUpdate() lineNum: " + lineNum);
    }

    public void setOnKeyCodeListener(OnKeyCodeListenerWithType listener) {
        console.setOnKeyCodeListener(listener);
    }

    @Override
    public void destroy(boolean skipRecreation, boolean skipGLDelete) {
        consoleInitialized = false;
        super.destroy(skipRecreation, skipGLDelete);
    }

    @Override
    public void onRecreation(ENG_Container previousContainer) {
        super.onRecreation(previousContainer);
        Console console = (Console) previousContainer;

//        OnClickListenerWithType onClickListener = (OnClickListenerWithType) console.back.getOnClickListener();
        OnKeyCodeListenerWithType onKeyCodeListener = (OnKeyCodeListenerWithType) console.console.getOnKeyCodeListener();
//        OnCharacterListenerWithType onCharacterListener = (OnCharacterListenerWithType) console.back.getOnCharacterListener();

//        if (onClickListener != null) {
//            setOnClickListener(entry.getKey(), SimpleViewGameMenuManager.getSingleton().createOnClickListenerWithType(
//                    onClickListener.getType(), onClickListener.getBundle()));
//        }
        if (onKeyCodeListener != null) {
            setOnKeyCodeListener(SimpleViewGameMenuManager.getSingleton().createOnKeyCodeListenerWithType(
                    onKeyCodeListener.getType(), onKeyCodeListener.getBundle()));
        }
//        if (onCharacterListener != null) {
//            setOnCharacterListener(entry.getKey(), SimpleViewGameMenuManager.getSingleton().createOnCharacterListenerWithType(
//                    onCharacterListener.getType(), onCharacterListener.getBundle()));
//        }

        recreateContainerListeners(previousContainer);
    }

    public ConsoleCmdHandler.ConsoleCmdHandlerListener getConsoleListener() {
        return consoleListener;
    }
}
