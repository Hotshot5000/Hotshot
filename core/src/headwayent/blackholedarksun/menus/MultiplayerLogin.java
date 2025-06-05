/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 9:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.automationframework.MultiPlayerCreateSessionWithFriendsAutomation;
import headwayent.blackholedarksun.automationframework.MultiPlayerJoinSessionWithFriendsAutomation;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ContainerListenerWithBus;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerLoginContainerListener;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.*;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

/**
 * Created by Sebastian on 26.04.2015.
 */
public class MultiplayerLogin extends ENG_Container {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String LOGIN_BUTTON = "login";
    private final ENG_Button login;

    public static class MultiplayerLoginContainerFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerLogin(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection UnstableApiUsage, deprecation */
    public MultiplayerLogin(String name, Bundle bundle) {
        super(name, bundle);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 20.0f);
        ENG_TextView usernameTextView = (ENG_TextView) createView("usernameText", "textview", 0.0f, 22.0f, 100.0f, 27.0f);
        final ENG_TextField usernameTextField = (ENG_TextField) createView(USERNAME, "textfield", 30.0f, 29.0f, 70.0f, 34.0f);
        ENG_TextView passwordTextView = (ENG_TextView) createView("passwordText", "textview", 0.0f, 36.0f, 100.0f, 41.0f);
        final ENG_TextField passwordTextField = (ENG_TextField) createView(PASSWORD, "textfield", 30.0f, 43.0f, 70.0f, 48.0f);
        login = (ENG_Button) createView(LOGIN_BUTTON, "button", 0.0f, 82.0f, 100.0f, 90.0f);
        ENG_Button cancel = (ENG_Button) createView("cancel", "button", 0.0f, 92.0f, 100.0f, 100.0f);

        titleView.setText(MULTIPLAYER_LOGIN_TITLE);
        usernameTextView.setText(MULTIPLAYER_LOGIN_USERNAME1);
        passwordTextView.setText(MULTIPLAYER_LOGIN_PASSWORD1);
        usernameTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        passwordTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        passwordTextField.setPassword(true);

        login.setText(MULTIPLAYER_LOGIN_LOGIN);
        cancel.setText(MULTIPLAYER_LOGIN_CANCEL);
        login.setTextColor(ENG_ColorValue.WHITE);
        cancel.setTextColor(ENG_ColorValue.WHITE);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        usernameTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        usernameTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        passwordTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        passwordTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        login.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        cancel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        setCurrentFocusedView(usernameTextField);

        login.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        cancel.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        login.setOnClickListener((x, y) -> {
            String username = usernameTextField.getText();
            String password = passwordTextField.getPasswordText();
            if (username.isEmpty() && password.isEmpty()) {
                showToast(MULTIPLAYER_LOGIN_USERNAME_AND_PASSWORD_MISSING);
            } else if (username.isEmpty()) {
                showToast(MULTIPLAYER_LOGIN_USERNAME_IS_MISSING);
            } else if (password.isEmpty()) {
                showToast(MULTIPLAYER_LOGIN_PASSWORD_IS_MISSING);
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerLoginContainerListener) {
                    ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.LoginEvent(username, password));
                    break;
                }
            }
            login.setClickListenerEnabled(false);
            showToast(MULTIPLAYER_LOGIN_PLEASE_WAIT_SIGNING_IN);
            return true;
        });
        cancel.setOnClickListener((x, y) -> {
            onBackPressed();
            return true;
        });
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onAccountLoggedIn(ClientAPI.LoggedInEvent event) {
        MainApp.getGame().setUser(event.user);
        login.setClickListenerEnabled(true);
        // Hack to remove a previous -> previous menu.
        onBackPressed();
        SimpleViewGameMenuManager.getSingleton().recreateMainMenuMultiplayerOnClickListener();
        SimpleViewGameMenuManager.startActivity(SimpleViewGameMenuManager.MULTIPLAYER_LOGGED_IN_MENU,
                true, true, false);

        if (MainApp.getMainThread().isAutomationEnabled(MultiPlayerCreateSessionWithFriendsAutomation.NAME)) {
            MainApp.getMainThread().setParameterForAutomation(
                    MultiPlayerCreateSessionWithFriendsAutomation.NAME, MultiPlayerCreateSessionWithFriendsAutomation.PARAM_LOGGED_IN, true);
        }
        if (MainApp.getMainThread().isAutomationEnabled(MultiPlayerJoinSessionWithFriendsAutomation.NAME)) {
            MainApp.getMainThread().setParameterForAutomation(
                    MultiPlayerJoinSessionWithFriendsAutomation.NAME, MultiPlayerJoinSessionWithFriendsAutomation.PARAM_LOGGED_IN, true);
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onAccountLoginError(ClientAPI.LoginErrorEvent event) {
        showToast(event.error);
    }

    @Override
    public void onRecreation(ENG_Container previousContainer) {
        super.onRecreation(previousContainer);
        recreateContainerListeners(previousContainer);
    }

    private void onBackPressed() {
        ENG_ContainerManager.getSingleton().setPreviousContainer();
    }
}
