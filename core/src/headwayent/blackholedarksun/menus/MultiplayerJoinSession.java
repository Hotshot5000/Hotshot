/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 9:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.eventbus.Subscribe;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ContainerListenerWithBus;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerJoinSessionContainerListener;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.Server;
import headwayent.blackholedarksun.net.clientapi.tables.Session;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.net.clientapi.viewdatas.SessionViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.*;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sebastian on 28.04.2015.
 */
public class MultiplayerJoinSession extends ENG_Container {

    public static class MultiplayerJoinSessionContainerFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerJoinSession(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /**
     * 0 - row count
     * 1 - server name
     * 2 - player num / max player count
     * 3 - map
     * 4 - ping
     */
    public static final int COLUMN_COUNT = 5;
    private final ENG_TableView serverTableView;
    private int rowNum = 1;
    private int selectedRowNum = -1;
    private List<Session> sessionList;
    private Session currentSelectedSession;
    private final Multimap<String, Integer> sessionNameToRowNumMap = TreeMultimap.create();
    private boolean clicked;

    /** @noinspection UnstableApiUsage, deprecation */
    public MultiplayerJoinSession(String name, Bundle bundle) {
        super(name, bundle);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 20.0f);
        ENG_TextView sessionSearchTextView = (ENG_TextView) createView("sessionSearchText", "textview", 0.0f, 22.0f, 100.0f, 27.0f);
        final ENG_TextField sessionSearchTextField = (ENG_TextField) createView("sessionSearch", "textfield", 10.0f, 29.0f, 35.0f, 35.0f);
        ENG_TextView teamSelectionTextView = (ENG_TextView) createView("teamSelectionText", "textview", 0.0f, 36.0f, 100.0f, 42.0f);
        final ENG_DropdownList teamSelectionDropdownList = (ENG_DropdownList) createView("teamSelection", "dropdownlist",
                        10.0f, 43.0f, 35.0f, 49.0f, bundle.getBundle(MultiplayerCreateSession.TEAM_LIST));
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add(MULTIPLAYER_JOIN_SESSION_E);
        columnNames.add(MULTIPLAYER_JOIN_SESSION_NAME);
        columnNames.add(MULTIPLAYER_JOIN_SESSION_PLAYERS_MAX);
        columnNames.add(MULTIPLAYER_JOIN_SESSION_MAP);
        columnNames.add(MULTIPLAYER_JOIN_SESSION_LATENCY);
        Bundle tableBundle = new Bundle();
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_COUNT, COLUMN_COUNT);
        tableBundle.putObject(ENG_TableView.BUNDLE_COLUMN_NAME_LIST, columnNames);
//        tableBundle.putFloat(ENG_TableView.BUNDLE_COLUMN_NAME_HEIGHT, 20.0f);
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_NAME_TEXT_SIZE, MainApp.Platform.isMobile() ? APP_Game.GORILLA_DEJAVU_SMALL : APP_Game.GORILLA_DEJAVU_MEDIUM);
        serverTableView = (ENG_TableView) createView("server_table", "tableview", 10.0f, 50.0f, 90.0f, 80.0f, tableBundle);
        ENG_Button ok = (ENG_Button) createView("ok", "button", 0.0f, 82.0f, 100.0f, 90.0f);
        ENG_Button cancel = (ENG_Button) createView("cancel", "button", 0.0f, 92.0f, 100.0f, 100.0f);

        titleView.setText(MULTIPLAYER_JOIN_SESSION_TITLE);
//		titleView.setEllipsize(ENG_TextView.Ellipsize.END);
//        titleView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        sessionSearchTextView.setText(MULTIPLAYER_JOIN_SESSION_SEARCH_FOR_SESSION_BY_NAME);
        sessionSearchTextField.setText("");
        teamSelectionTextView.setText(MULTIPLAYER_JOIN_SESSION_SELECT_TEAM);
        ok.setText(MULTIPLAYER_JOIN_SESSION_OK);
        cancel.setText(MULTIPLAYER_JOIN_SESSION_CANCEL);
        ok.setTextColor(ENG_ColorValue.WHITE);
        cancel.setTextColor(ENG_ColorValue.WHITE);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        sessionSearchTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        sessionSearchTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        teamSelectionTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        teamSelectionDropdownList.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        serverTableView.setTextSize(MainApp.Platform.isMobile() ? APP_Game.GORILLA_DEJAVU_SMALL : APP_Game.GORILLA_DEJAVU_MEDIUM);
        ok.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        cancel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        ok.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        cancel.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        serverTableView.setRowHeightType(ENG_TableView.RowHeightType.TABLE_BASED);
        serverTableView.setTextFieldNumInTable(3);

        serverTableView.setColumnNamesHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        serverTableView.setColumnNamesVerticalAlignment(ENG_TextView.VerticalAlignment.CENTER);
        serverTableView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        serverTableView.setVerticalAlignment(ENG_TextView.VerticalAlignment.CENTER);
//        serverTableView.setBoxRectangleBorder(0, 5.0f, ENG_ColorValue.RED);

        serverTableView.setOnRowClick(new ENG_TableView.OnRowClick() {
            @Override
            public boolean onSelectedRow(int row) {
//                serverTableView.setHorizontalAlignment(row, ENG_TextView.HorizontalAlignment.LEFT, ENG_TableView.OverwriteAllowType.NON_OVERWRITABLE);
                serverTableView.setBoxRectangleBorder(row, 5.0f, ENG_ColorValue.WHITE);
                selectedRowNum = row;
                return true;
            }

            @Override
            public boolean onUnselectedRow(int oldRow) {
                return false;
            }
        });

        ok.setOnClickListener((x, y) -> {

            if (clicked) {
                return true;
            }
            clicked = true;
            String sessionSearchName = sessionSearchTextField.getText();
            if (selectedRowNum == -1 && sessionSearchName.isEmpty()) {
                showToast(MULTIPLAYER_JOIN_SESSION_PLEASE_SELECT_A_SESSION_IN_THE_SESSION_TABLE);
                clicked = false;
                return true;
            }
            // We shouldn't be able to get here since there should be no selected row but just for safety.
            if (sessionList == null || sessionList.isEmpty()) {
                showToast(MULTIPLAYER_JOIN_SESSION_NO_ELEMENTS_IN_THE_SESSION_TABLE);
                clicked = false;
                return true;
            }
            APP_Game game = MainApp.getGame();
            User user = game.getUser();
            if (sessionSearchName.isEmpty()) {
                currentSelectedSession = sessionList.get(selectedRowNum);
            } else {
                Collection<Integer> sessionRowNum = sessionNameToRowNumMap.get(sessionSearchName);
                if (sessionRowNum.isEmpty()) {
                    showToast(MULTIPLAYER_JOIN_SESSION_PROVIDED_SESSION_NAME_NOT_FOUND);
                    clicked = false;
                    return true;
                }
                if (sessionRowNum.size() > 1) {
                    showToast(MULTIPLAYER_JOIN_SESSION_MORE_THAN_ONE_SESSION_WITH_THAT_NAME_FOUND);
                    clicked = false;
                    return true;
                }
                currentSelectedSession = sessionList.get(sessionRowNum.iterator().next());
            }


            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerJoinSessionContainerListener) {
                    ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.JoinSessionEvent(user.getAuthToken(), currentSelectedSession));
                    break;
                }
            }
            return true;
        });

        cancel.setOnClickListener((x, y) -> {

            onBackPressed();
            return true;
        });
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionJoined(ClientAPI.SessionJoinedEvent event) {
        Server server = event.session;
        if (clicked) {
            MultiplayerCreateSession.onSessionAccepted(server, currentSelectedSession.getMapId(), currentSelectedSession.getSessionName(), false);
        }
        clicked = false;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionJoinedError(ClientAPI.SessionJoinErrorEvent event) {
        clicked = false;
        showToast(MULTIPLAYER_JOIN_SESSION_COULD_NOT_JOIN_THE_SELECTED_SESSION);
    }

    public ENG_TableView getServerTableView() {
        return serverTableView;
    }

    public void clearRows() {
        serverTableView.removeAllRows();
    }

    public void addSessionViewDataList(List<SessionViewData> sessionList) {
        for (SessionViewData data : sessionList) {
            addRow(data.rowNum, data.name, data.players, data.map, data.latency);
        }
    }

    public void addRows(ArrayList<ArrayList<String>> rows) {
        for (ArrayList<String> list : rows) {
            addRow(list);
        }
    }

    public void addRow(ArrayList<String> row) {
        addRow(String.valueOf(rowNum++), row.get(0), row.get(1), row.get(2), row.get(3));
    }

    public void addRow(String rowNum, String serverName, String players, String map, String latency) {
        HashMap<Integer, String> columns = new HashMap<>();
        columns.put(0, rowNum);
        columns.put(1, serverName);
        columns.put(2, players);
        columns.put(3, map);
        columns.put(4, latency);
        serverTableView.addRowByPosition(columns);
    }

    public void addSessionList(List<Session> sessionList) {
        this.sessionList = sessionList;
        sessionNameToRowNumMap.clear();
        int row = 0;
        for (Session session : sessionList) {
            sessionNameToRowNumMap.put(session.getSessionName(), row++);
        }

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
