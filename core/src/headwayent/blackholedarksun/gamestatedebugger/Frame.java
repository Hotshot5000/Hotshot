/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/21/15, 9:24 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.gamestatedebugger;

import headwayent.hotshotengine.statedebugger.ENG_Frame;
import headwayent.hotshotengine.statedebugger.ENG_State;

/**
 * Created by sebas on 18.09.2015.
 * <p>
 * If you add any field here remember to deserialize it in FrameDeserializer.
 */
public class Frame extends ENG_Frame {

    public Frame(ENG_State state) {
        super(state);
    }

    public static class GameFrameFactory extends FrameFactory {

        @Override
        public ENG_Frame createFrame(ENG_State state) {
            return new Frame(state);
        }
    }

    static {
        ENG_State.addClass(ENG_State.FRAME, Frame.class);
    }

    public static void loadClass() {
        try {
//            Class<?> frame = Class.forName("headwayent.blackholedarksun.gamestatedebugger.Frame");
            // Use load class for osgi compatibility
            Frame.class.getClassLoader().loadClass("headwayent.blackholedarksun.gamestatedebugger.Frame");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
