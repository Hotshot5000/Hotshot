/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.Comparator;

public class ENG_RadixSortFunctorPass implements Comparator<ENG_RenderablePass> {

    @Override
    public int compare(ENG_RenderablePass arg0, ENG_RenderablePass arg1) {

        int hasha = arg0.pass.getHash();
        int hashb = arg1.pass.getHash();

        if (hasha < hashb) {
            return -1;
        } else if (hasha > hashb) {
            return 1;
        }
        return 0;
    }

}
