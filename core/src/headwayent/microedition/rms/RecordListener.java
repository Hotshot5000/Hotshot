/*
 * Created by Sebastian Bugiu on 4/9/23, 10:12 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.microedition.rms;


public interface RecordListener {

    void recordAdded(RecordStore recordStore,
                     int recordId);

    void recordChanged(RecordStore recordStore,
                       int recordId);

    void recordDeleted(RecordStore recordStore,
                       int recordId);
}
