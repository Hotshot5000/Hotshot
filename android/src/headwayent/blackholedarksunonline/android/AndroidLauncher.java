/*
 * Created by Sebastian Bugiu on 4/18/23, 1:21 AM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/9/23, 10:13 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksunonline.android;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Surface;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import headwayent.blackholedarksun.BlackholeDarksunMain;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ApplicationStartSettings;

public class AndroidLauncher extends AndroidApplication {

    static {
        System.loadLibrary("native-lib");
    }

    /** @noinspection deprecation*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApp.PLATFORM = MainApp.Platform.ANDROID;
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.r = 8;
        config.g = 8;
        config.b = 8;
        config.a = 8;
        config.depth = 24;
        config.stencil = 8;
        config.useWakelock = true;
        config.hideStatusBar = true;
        config.useImmersiveMode = true;
        config.useGL30 = true;

        ApplicationStartSettings applicationStartSettings = new ApplicationStartSettings();
        applicationStartSettings.uncaughtExceptionHandler = new AndroidUncaughtExceptionHandler(getContext());
        applicationStartSettings.applicationMode = MainApp.Mode.CLIENT;

        AssetManager assetManager = getResources().getAssets();
        setAssetManager(assetManager);


        initialize(new BlackholeDarksunMain(null, applicationStartSettings), config);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Surface surfaceView = ((GLSurfaceView) ((AndroidGraphics) Gdx.graphics).getView()).getHolder().getSurface();
//        setGLSurfaceView(surfaceView);
    }

    public native void setAssetManager(AssetManager assetManager);
    public native void setGLSurfaceView(Surface surfaceView);
}
