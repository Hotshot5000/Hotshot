/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/26/22, 6:23 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import com.badlogic.gdx.pay.PurchaseManager;
import com.esotericsoftware.kryonet.Server;

import java.util.concurrent.CountDownLatch;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.net.ClientListener;
import headwayent.blackholedarksun.net.registeredclasses.ServerConnectionRequest;
//import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

/**
 * Created by sebas on 29.09.2015.
 */
public class ApplicationStartSettings {

    public static final String OUTPUT_DEBUGGING_STATE_DEFAULT_FILENAME = "debugging_state.txt";

    public Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    public MainApp.Mode applicationMode = MainApp.Mode.CLIENT;
    public String outputDebuggingStateDefaultFilename = OUTPUT_DEBUGGING_STATE_DEFAULT_FILENAME;
    public ServerConnectionRequest serverConnectionRequest;
    public Server server;
    public ClientListener clientListener;
    public int screenWidth, screenHeight;
    public CountDownLatch waitForServerToStart;
//    public IOSApplicationConfiguration iosConfig;
    public PurchaseManager purchaseManager;
}
