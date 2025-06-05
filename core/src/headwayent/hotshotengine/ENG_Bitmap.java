/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

public class ENG_Bitmap {

    public static final int BYTES_PER_PIXEL = 4;

    private static ENG_BitmapManager bitmapManager;
    //	private Bitmap bitmap;
    private final Pixmap bitmap;
    private final String name;

    public ENG_Bitmap(String name, String filename) {
        this.bitmap = new Pixmap(Gdx.files.local(filename));
        //new Texture(Gdx.files.local(filename),
        //Format.RGBA8888, false);// BitmapFactory.decodeFile(filename);
        checkNull(filename);
        this.name = name;
    }

    public void destroy() {
        bitmap.dispose();
    }

    public ENG_Bitmap(int id, String name) {
        throw new UnsupportedOperationException();
//		this.bitmap = 
//			BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), id);
//		checkNull(name);
//		this.name = name;
        // Do not add to the manager since we never use it anyway
    /*	if (MainActivity.isDebugmode()) {
			if (bitmapManager != null) {
				bitmapManager.add(this);
			} else {
				MainApp.setFatalError();
			}
		} else {
			bitmapManager.add(this);
		}*/
    }

    private void checkNull(String name) {
        if (this.bitmap == null) {
            throw new IllegalArgumentException(name + " does not represent a valid texture");
        }
    }

    public Pixmap getBitmap() {
        return bitmap;
    }

    public String getName() {
        return name;
    }

    public static void setBitmapManager(ENG_BitmapManager bitmapManager) {
        ENG_Bitmap.bitmapManager = bitmapManager;
    }

    public static ENG_BitmapManager getBitmapManager() {
        return bitmapManager;
    }
}
