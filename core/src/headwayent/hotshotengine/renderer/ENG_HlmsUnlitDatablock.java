/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

/**
 * Created by sebas on 17-Aug-17.
 */

public class ENG_HlmsUnlitDatablock extends ENG_HlmsDatablock {

    private final ENG_ColorValue colour = new ENG_ColorValue(ENG_ColorValue.WHITE);
    private boolean useColour;

    public ENG_ColorValue getColour() {
        return colour;
    }

    public void setColour(ENG_ColorValue colour) {
        this.colour.set(colour);
    }

    public boolean isUseColour() {
        return useColour;
    }

    public void setUseColour(boolean useColour) {
        this.useColour = useColour;
    }
}
