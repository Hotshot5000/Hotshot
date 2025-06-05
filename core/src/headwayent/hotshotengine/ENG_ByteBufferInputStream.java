/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.blackholedarksun.MainActivity;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

/**
 * Needed for GIWS. Nothing to see here move along....
 *
 * @author sebi
 */
public class ENG_ByteBufferInputStream {

    private ByteArrayInputStream in;

    /**
     * Empty constructor for GIWS
     */
    public ENG_ByteBufferInputStream() {

    }

    /**
     * Call from GIWS as a C array. It should be converted to ByteBuffer
     * automagically.
     *
     * @param buf
     */
    public void set(ByteBuffer buf) {
        byte[] b = new byte[buf.capacity()];
        // Is the buf set to 0 and limit at capacity? Test GIWS before assuming
        buf.get(b);
        in = new ByteArrayInputStream(b);
    }

    public ByteArrayInputStream getByteBufferInputStream() {
        if (MainActivity.isDebugmode() && in == null) {
            throw new NullPointerException("set the bytebuffer before " +
                    "getting it");
        }
        return in;
    }

}
