/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.GL20;

public class GLRenderBuffer extends GLHardwarePixelBuffer {

    protected final int mRenderbufferID;

    /** @noinspection deprecation */
    public GLRenderBuffer(int format, int width, int height, int numSamples) {
        super(width, height, 1, GLPixelUtil.getClosestOGREFormat(format),
                Usage.HBU_WRITE_ONLY.getUsage());
        mGLInternalFormat = format;

        //	int[] id = new int[1];
        IntBuffer id = ENG_Utility.allocateDirect(4).asIntBuffer();
        MTGLES20.glGenRenderbuffersImmediate(1, id);

        mRenderbufferID = id.get();

        MTGLES20.glBindRenderbuffer(GL20.GL_RENDERBUFFER, mRenderbufferID);

        if (numSamples > 0) {
            throw new UnsupportedOperationException("MRT not supported");
        } else {
            MTGLES20.glRenderbufferStorage(GL20.GL_RENDERBUFFER, format, width, height);
        }
    }

    /** @noinspection deprecation*/
    public void bindToFramebuffer(int attachment, int zoffset) {
        if ((zoffset < 0) || (zoffset >= depth)) {
            throw new IllegalArgumentException("zoffset out of range");
        }
        MTGLES20.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, attachment,
                GL20.GL_RENDERBUFFER, mRenderbufferID);
    }

    public void destroy(boolean skipGLDelete) {
        

    }
}
