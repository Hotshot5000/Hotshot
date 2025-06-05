/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/24/18, 10:28 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ENG_DateUtils {

    public static String getCurrentDateTimestamp() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
    }
}
