/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/2/18, 10:54 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;

/**
 * Created by sebas on 09.07.2017.
 */

public interface ENG_AttachableObjectIntf extends ENG_NamedObject, ENG_NativePointerWithSetter, ENG_IdObject {

    boolean isAttached();

//    void setAttached(boolean attached);

    void _notifyAttached(ENG_Node node);

    ENG_Node getParentNode();

    void detachFromParent();

    void setWorldAabb(float xCenter, float yCenter, float zCenter,
                      float xHalfSize, float yHalfSize, float zHalfSize);

    ENG_AxisAlignedBox getWorldAABB();

    boolean isDestroyed();
}
