/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_IndexData {

    public ENG_HardwareIndexBuffer indexBuffer;
    public int indexStart;
    public int indexCount;

    public ENG_IndexData() {

    }

    public ENG_IndexData clone() {
        return clone(true, null);
    }

    public ENG_IndexData clone(boolean copyData, ENG_HardwareBufferManager mgr) {
        /*HardwareBufferManagerBase* pManager = mgr ? mgr : HardwareBufferManager::getSingletonPtr();
		IndexData* dest = OGRE_NEW IndexData();
		if (indexBuffer.get())
		{
            if (copyData)
            {
			    dest->indexBuffer = pManager->createIndexBuffer(indexBuffer->getType(), indexBuffer->getNumIndexes(),
				    indexBuffer->getUsage(), indexBuffer->hasShadowBuffer());
			    dest->indexBuffer->copyData(*indexBuffer, 0, 0, indexBuffer->getSizeInBytes(), true);
            }
            else
            {
                dest->indexBuffer = indexBuffer;
            }
        }
		dest->indexCount = indexCount;
		dest->indexStart = indexStart;
		return dest;*/

        ENG_HardwareBufferManagerBase pManager = (mgr != null) ?
                mgr : ENG_HardwareBufferManager.getSingleton();
        ENG_IndexData dest = new ENG_IndexData();

        if (indexBuffer != null) {
            if (copyData) {
                dest.indexBuffer = pManager.createIndexBuffer(indexBuffer.getIndexType(),
                        indexBuffer.getNumIndexes(), indexBuffer.getUsage(),
                        indexBuffer.hasShadowBuffer());
                dest.indexBuffer.copyData(indexBuffer, 0, 0,
                        indexBuffer.getSizeInBytes(), true);
            } else {
                dest.indexBuffer = indexBuffer;
            }
        }
        dest.indexCount = indexCount;
        dest.indexStart = indexStart;
        return dest;
    }
}
