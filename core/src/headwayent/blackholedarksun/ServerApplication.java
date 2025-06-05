/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/16/18, 3:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Preferences;
//import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
//import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.utils.Clipboard;
import org.lwjgl.opengl_copy.Sync;

/**
 * Created by sebas on 29.10.2015.
 */
public class ServerApplication implements Application {

    private final ApplicationListener listener;
    private final LwjglApplicationConfiguration config;
    protected Thread mainLoopThread;

    public ServerApplication(ApplicationListener listener, LwjglApplicationConfiguration config) {

        this.listener = listener;
        String preferencesdir = config.preferencesDirectory;
        this.config = config;

        Gdx.app = this;
        Gdx.files = new LwjglFiles();
        initialize();

    }

    private void initialize() {
        listener.create();
//        mainLoopThread = new Thread("LWJGL Application") {
//            @Override
//            public void run () {
//                try {
//                    listener.create();
//                    ServerApplication.this.mainLoop();
//                } catch (Throwable t) {
//                    if (t instanceof RuntimeException)
//                        throw (RuntimeException)t;
//                    else
//                        throw new GdxRuntimeException(t);
//                }
//            }
//        };
//        mainLoopThread.start();
    }

    public long mainLoop() {
        listener.render();
        Sync.sync(config.foregroundFPS);
        return config.foregroundFPS;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return listener;
    }

    @Override
    public Graphics getGraphics() {
        return null;
    }

    @Override
    public Audio getAudio() {
        return null;
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Files getFiles() {
        return null;
    }

    @Override
    public Net getNet() {
        return null;
    }

    @Override
    public void log(String tag, String message) {

    }

    @Override
    public void log(String tag, String message, Throwable exception) {

    }

    @Override
    public void error(String tag, String message) {

    }

    @Override
    public void error(String tag, String message, Throwable exception) {

    }

    @Override
    public void debug(String tag, String message) {

    }

    @Override
    public void debug(String tag, String message, Throwable exception) {

    }

    @Override
    public void setLogLevel(int logLevel) {

    }

    @Override
    public int getLogLevel() {
        return 0;
    }

    @Override
    public void setApplicationLogger(ApplicationLogger applicationLogger) {

    }

    @Override
    public ApplicationLogger getApplicationLogger() {
        return null;
    }

    @Override
    public ApplicationType getType() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        return 0;
    }

    @Override
    public long getNativeHeap() {
        return 0;
    }

    @Override
    public Preferences getPreferences(String name) {
        return null;
    }

    @Override
    public Clipboard getClipboard() {
        return null;
    }

    @Override
    public void postRunnable(Runnable runnable) {

    }

    @Override
    public void exit() {

    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }
}
