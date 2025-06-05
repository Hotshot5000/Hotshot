/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/29/17, 3:53 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles.glsl;

import headwayent.hotshotengine.exception.ENG_GLException;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import com.badlogic.gdx.graphics.GL20;

//import org.lwjgl.util.glu.GLU;

public class GLUtility {

    public static void checkForGLSLError(String method, String prefixText) {
        checkForGLSLError(method, prefixText, -1, false, false);
    }

    /** @noinspection deprecation */
    public static void checkForGLSLError(String method, String prefixText, int GLObj,
                                         boolean forceInfoLog, boolean forceException) {
        int glErr = MTGLES20.glGetError();
        boolean errorsFound = false;
        int errorNum = 0;
        StringBuilder prefixTextBuilder = new StringBuilder(prefixText);
        while (glErr != GL20.GL_NO_ERROR) {
            errorsFound = true;
			String msg = "";//GLU.gluErrorString(glErr);
			if (msg != null && !msg.isEmpty()) {
				prefixTextBuilder.append(" ").append(msg);
			}
            prefixTextBuilder.append(" error num: ").append(errorNum).append(" GL error: ").append(glErr);
            ++errorNum;
            glErr = MTGLES20.glGetError();
        }
        prefixText = prefixTextBuilder.toString();

        if (errorsFound && forceException) {
            throw new ENG_GLException(prefixText);
        }
        if (errorsFound) {
            System.out.println(prefixText);
        }
    }
}
