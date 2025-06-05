/*
 * Created by Sebastian Bugiu on 4/9/23, 10:12 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.microedition.rms;


public interface RecordComparator {

    int EQUIVALENT = 0;
    int FOLLOWS = 1;
    int PRECEDES = -1;

    int compare(byte[] rec1,
                byte[] rec2);
}
