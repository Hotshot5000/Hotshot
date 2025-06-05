/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/26/21, 7:35 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.osspecific;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ApplicationStartSettings;
import headwayent.hotshotengine.android.AndroidRenderWindow;

import org.lwjgl.opengl.Display;

public final class Win32 {

    public static Method makePollDevicesMethodAccessible(ApplicationStartSettings settings) {
        if (MainApp.DesktopPlatform.isWin32Desktop() && settings.applicationMode == MainApp.Mode.CLIENT) {
            try {
                Method pollDevices = Display.class.getDeclaredMethod("pollDevices");
                pollDevices.setAccessible(true);
                return pollDevices;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void injectHandlers(long hwnd, long hdc) {
        // Since we are out of whack we might as well inject these values into lwjgl Display for fun and profit.
        // Mouse and keyboard should now be supported
        try {
            Field display_impl = Display.class.getDeclaredField("display_impl");
            Field window_created = Display.class.getDeclaredField("window_created");
            display_impl.setAccessible(true);
            window_created.setAccessible(true);
            Object windowsDisplay = /*(WindowsDisplay)*/ display_impl.get(null);
            // This is Windows only!!!
            Field hwndField = windowsDisplay.getClass().getDeclaredField("hwnd");
            Field hdcField = windowsDisplay.getClass().getDeclaredField("hdc");
            hwndField.setAccessible(true);
            hdcField.setAccessible(true);
            hwndField.setLong(windowsDisplay, hwnd);
            hdcField.setLong(windowsDisplay, hdc);
            window_created.setBoolean(null, true);
            Method initControls = Display.class.getDeclaredMethod("initControls");
            initControls.setAccessible(true);
            initControls.invoke(null);
            Method handleMessage = windowsDisplay.getClass().getDeclaredMethod("handleMessage",
                    long.class, int.class, long.class, long.class, long.class);
            AndroidRenderWindow.setWindowProc(windowsDisplay.getClass(), handleMessage);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
