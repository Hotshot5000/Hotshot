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
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ContainerListenerWithBus;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerCreateAccountContainerListener;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.*;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

/**
 * Created by Sebastian on 28.04.2015.
 */
public class MultiplayerCreateAccount extends ENG_Container {

    public static final int MINIMUM_PASSWORD_LENGTH = 6;
    private final ENG_Button createAccount;

    public static class MultiplayerCreateAccountContainerFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerCreateAccount(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection deprecation*/
    public MultiplayerCreateAccount(String name, Bundle bundle) {
        super(name, bundle);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 20.0f);
        ENG_TextView usernameTextView = (ENG_TextView) createView("usernameText", "textview", 0.0f, 22.0f, 100.0f, 27.0f);
        final ENG_TextField usernameTextField = (ENG_TextField) createView("username", "textfield", 30.0f, 29.0f, 70.0f, 34.0f);
        ENG_TextView passwordTextView = (ENG_TextView) createView("passwordText", "textview", 0.0f, 36.0f, 100.0f, 41.0f);
        final ENG_TextField passwordTextField = (ENG_TextField) createView("password", "textfield", 30.0f, 43.0f, 70.0f, 48.0f);
        ENG_TextView confirmPasswordTextView = (ENG_TextView) createView("confirmPasswordText", "textview", 0.0f, 50.0f, 100.0f, 55.0f);
        final ENG_TextField confirmPasswordTextField = (ENG_TextField) createView("confirmPassword", "textfield", 30.0f, 57.0f, 70.0f, 62.0f);
        createAccount = (ENG_Button) createView("createAccount", "button", 0.0f, 82.0f, 100.0f, 90.0f);
        ENG_Button cancel = (ENG_Button) createView("cancel", "button", 0.0f, 92.0f, 100.0f, 100.0f);

        titleView.setText(MULTIPLAYER_CREATE_ACCOUNT_TITLE);
        usernameTextView.setText(MULTIPLAYER_CREATE_ACCOUNT_USERNAME);
        passwordTextView.setText(MULTIPLAYER_CREATE_ACCOUNT_PASSWORD);
        confirmPasswordTextView.setText(MULTIPLAYER_CREATE_ACCOUNT_CONFIRM_PASSWORD);
        usernameTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        passwordTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        confirmPasswordTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        passwordTextField.setPassword(true);
        confirmPasswordTextField.setPassword(true);

        createAccount.setText(MULTIPLAYER_CREATE_ACCOUNT_CREATE_ACCOUNT);
        cancel.setText(MULTIPLAYER_CREATE_ACCOUNT_CANCEL);
        createAccount.setTextColor(ENG_ColorValue.WHITE);
        cancel.setTextColor(ENG_ColorValue.WHITE);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        usernameTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        usernameTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        passwordTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        passwordTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        confirmPasswordTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        confirmPasswordTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        createAccount.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        cancel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        setCurrentFocusedView(usernameTextField);

        createAccount.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        cancel.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        createAccount.setOnClickListener(new ENG_View.OnClickListener() {
            /** @noinspection UnstableApiUsage*/
            @Override
            public boolean onClick(int x, int y) {
                String username = usernameTextField.getText();
                String password = passwordTextField.getPasswordText();
                String confirmPassword = confirmPasswordTextField.getPasswordText();
                if (username.isEmpty()) {
                    showToast(MULTIPLAYER_CREATE_ACCOUNT_USERNAME_BOX_CANNOT_BE_EMPTY);
                    return true;
                }
                if (password.isEmpty() && confirmPassword.isEmpty()) {
                    showToast(MULTIPLAYER_CREATE_ACCOUNT_YOU_MUST_PROVIDE_A_PASSWORD);
                    return true;
                }
                if (!password.equals(confirmPassword)) {
                    showToast(MULTIPLAYER_CREATE_ACCOUNT_PASSWORDS_DO_NOT_MATCH);
                    resetPasswordTextFields();
                    return true;
                } else if (password.length() < MINIMUM_PASSWORD_LENGTH) {
                    showToast(MULTIPLAYER_CREATE_ACCOUNT_MINIMUM_PASSWORD_LENGTH_IS + MINIMUM_PASSWORD_LENGTH + MULTIPLAYER_CREATE_ACCOUNT_CHARACTERS);
                    resetPasswordTextFields();
                    return true;
                }

                ArrayList<ContainerListener> listeners = getListeners();
                for (ContainerListener listener : listeners) {
                    if (listener instanceof MultiplayerCreateAccountContainerListener) {
                        ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.CreateUserEvent(username, password));
                        break;
                    }
                }
                createAccount.setClickListenerEnabled(false);
                showToast(MULTIPLAYER_CREATE_ACCOUNT_PLEASE_WAIT_CREATING_ACCOUNT);
                return true;
            }

            private void resetPasswordTextFields() {
                passwordTextField.setText("");
                confirmPasswordTextField.setText("");
            }
        });
        cancel.setOnClickListener((x, y) -> {
            onBackPressed();
            return true;
        });
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onAccountCreated(ClientAPI.UserCreatedEvent event) {
        MainApp.getGame().setUser(event.user);
        createAccount.setClickListenerEnabled(true);
        // Hack to remove a previous -> previous menu.
        onBackPressed();
        SimpleViewGameMenuManager.getSingleton().recreateMainMenuMultiplayerOnClickListener();
        SimpleViewGameMenuManager.startActivity(SimpleViewGameMenuManager.MULTIPLAYER_LOGGED_IN_MENU, true, true, false);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onAccountCreationError(ClientAPI.UserCreationErrorEvent event) {
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
