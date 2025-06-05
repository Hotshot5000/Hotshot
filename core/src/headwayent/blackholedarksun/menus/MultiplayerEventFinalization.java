/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/6/16, 9:49 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import java.util.ArrayList;
import java.util.HashMap;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.multiplayer.rmi.UserStats;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TableView;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

/**
 * Created by sebas on 05.07.2016.
 */
public abstract class MultiplayerEventFinalization extends ENG_Container {

    public static final int COLUMN_COUNT = 3;
    protected final ENG_TextView titleView;
    protected final ENG_TableView serverTableView;

    /** @noinspection deprecation */
    public MultiplayerEventFinalization(String name, Bundle bundle) {
        super(name, bundle);

        titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 15.0f);
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add(MULTIPLAYER_EVENT_FINALIZATION_NAME);
        columnNames.add(MULTIPLAYER_EVENT_FINALIZATION_KILLS);
        columnNames.add(MULTIPLAYER_EVENT_FINALIZATION_DEATHS);
        Bundle tableBundle = new Bundle();
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_COUNT, COLUMN_COUNT);
        tableBundle.putObject(ENG_TableView.BUNDLE_COLUMN_NAME_LIST, columnNames);
//        tableBundle.putFloat(ENG_TableView.BUNDLE_COLUMN_NAME_HEIGHT, 20.0f);
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_NAME_TEXT_SIZE, APP_Game.GORILLA_DEJAVU_MEDIUM);
        serverTableView = (ENG_TableView) createView("server_table", "tableview", 10.0f, 50.0f, 90.0f, 90.0f, tableBundle);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);

        serverTableView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

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
                serverTableView.setHorizontalAlignment(row, ENG_TextView.HorizontalAlignment.LEFT, ENG_TableView.OverwriteAllowType.NON_OVERWRITABLE);
                serverTableView.setBoxRectangleBorder(row, 5.0f, ENG_ColorValue.WHITE);
                return true;
            }

            @Override
            public boolean onUnselectedRow(int oldRow) {
                return false;
            }
        });
    }

    public void addUserStatsList(ArrayList<UserStats> userStatsList) {
        for (UserStats userStats : userStatsList) {
            addRow(userStats.getUsername(), String.valueOf(userStats.getKills()), String.valueOf(userStats.getDeaths()));
        }
    }

    private void addRow(String username, String kills, String deaths) {
        HashMap<Integer, String> columns = new HashMap<>();
        columns.put(0, username);
        columns.put(1, kills);
        columns.put(2, deaths);
        serverTableView.addRowByPosition(columns);
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
