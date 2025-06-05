/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 9:41 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles.mtgles20;

import headwayent.blackholedarksun.MainActivity;
import headwayent.blackholedarksun.MainApp;

import java.util.concurrent.Semaphore;

@Deprecated
public abstract class GLRunnableEvent implements Runnable {

    private int intRet;
    private String stringRet;
    private boolean booleanRet;
    private final Semaphore valueSet = new Semaphore(1);

    public GLRunnableEvent() {
        try {
            valueSet.acquire();
        } catch (InterruptedException e) {

            //e.printStackTrace();
            if (MainActivity.isDebugmode()) {
                e.printStackTrace();
                MainApp.setFatalError();
            }
        }
    }

    public Semaphore _getSemaphore() {
        return valueSet;
    }

    public void checkSemaphoreReleased() {
        try {
            valueSet.acquire();

        } catch (InterruptedException e) {
            if (MainActivity.isDebugmode()) {
                e.printStackTrace();
                MainApp.setFatalError();
            }
        } finally {
            valueSet.release();
        }
    }

    public int getInt() {
        checkSemaphoreReleased();
        return intRet;
    }

    public String getString() {
        checkSemaphoreReleased();
        return stringRet;
    }

    public boolean getBoolean() {
        checkSemaphoreReleased();
        return booleanRet;
    }

    /**
     * @param intRet the intRet to set
     */
    public void _setIntRet(int intRet) {
        this.intRet = intRet;
    }

    /**
     * @param stringRet the stringRet to set
     */
    public void _setStringRet(String stringRet) {
        this.stringRet = stringRet;
    }

    /**
     * @param booleanRet the booleanRet to set
     */
    public void _setBooleanRet(boolean booleanRet) {
        this.booleanRet = booleanRet;
    }

}
