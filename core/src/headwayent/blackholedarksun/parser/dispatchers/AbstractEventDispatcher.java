/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.dispatchers;

import headwayent.blackholedarksun.parser.ast.CameraAttachEvent;
import headwayent.blackholedarksun.parser.ast.CameraDetachEvent;
import headwayent.blackholedarksun.parser.ast.CameraEvent;
import headwayent.blackholedarksun.parser.ast.ObjectEvent;
import headwayent.blackholedarksun.parser.ast.ParallelTask;

public abstract class AbstractEventDispatcher {
    
    public abstract void begin();
    public abstract void end();

    public abstract boolean dispatch(ObjectEvent event);
    public abstract boolean dispatch(CameraEvent event);
    public abstract boolean dispatch(CameraAttachEvent event);
    public abstract boolean dispatch(CameraDetachEvent event);
    public abstract boolean dispatch(ParallelTask event);
}
