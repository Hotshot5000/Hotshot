/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class ENG_VertexData {

    private final ENG_HardwareBufferManagerBase mgr;
    public final ENG_VertexDeclaration vertexDeclaration;
    public final ENG_VertexBufferBinding vertexBufferBinding;
    public boolean deleteDclBinding;
    public int vertexStart;
    public int vertexCount;

    static class HardwareAnimationData {
        public ENG_VertexElement targetVertexElement;
        public float parametric;
    }

    public ArrayList<HardwareAnimationData> hwAnimationDataList =
            new ArrayList<>();
    public int hwAnimDataItemsUsed;

    public ENG_VertexData() {
        this(null);
    }

    public ENG_VertexData(ENG_HardwareBufferManagerBase mgr) {
        this.mgr = (mgr != null) ? mgr : ENG_HardwareBufferManager.getSingleton();
        vertexBufferBinding = this.mgr.createVertexBufferBinding();
        vertexDeclaration = this.mgr.createVertexDeclaration();
        deleteDclBinding = true;
    }

    public ENG_VertexData(ENG_VertexDeclaration dcl, ENG_VertexBufferBinding bind) {
        this.mgr = ENG_HardwareBufferManager.getSingleton();
        vertexDeclaration = dcl;
        vertexBufferBinding = bind;
    }

    public void destroy(boolean skipGLDelete) {
        if (deleteDclBinding) {
            mgr.destroyVertexBufferBinding(vertexBufferBinding, skipGLDelete);
            mgr.destroyVertexDeclaration(vertexDeclaration);
        }
    }

    public ENG_VertexData clone() {
        return clone(true, null);
    }

    public ENG_VertexData clone(boolean copyData, ENG_HardwareBufferManagerBase mgr) {
        ENG_HardwareBufferManagerBase manager = (mgr != null) ? mgr : this.mgr;

        ENG_VertexData dest = new ENG_VertexData(mgr);

        /*HardwareVertexBufferSharedPtr srcbuf = vbi->second;
            HardwareVertexBufferSharedPtr dstBuf;
            if (copyData)
            {
			    // create new buffer with the same settings
			    dstBuf = pManager->createVertexBuffer(
					    srcbuf->getVertexSize(), srcbuf->getNumVertices(), srcbuf->getUsage(),
					    srcbuf->hasShadowBuffer());

			    // copy data
			    dstBuf->copyData(*srcbuf, 0, 0, srcbuf->getSizeInBytes(), true);
            }
            else
            {
                // don't copy, point at existing buffer
                dstBuf = srcbuf;
            }*/

        for (Entry<ENG_Short, ENG_HardwareVertexBuffer> entry : vertexBufferBinding.getBindingMap().entrySet()) {
            ENG_HardwareVertexBuffer srcBuf = entry.getValue();
            ENG_HardwareVertexBuffer dstBuf;
            if (copyData) {
                dstBuf = manager.createVertexBuffer(srcBuf.getVertexSize(),
                        srcBuf.getNumVertices(), srcBuf.getUsage(),
                        srcBuf.hasShadowBuffer());
                dstBuf.copyData(srcBuf, 0, 0, srcBuf.getSizeInBytes(), true);
            } else {
                dstBuf = srcBuf;
            }

			/*// Copy binding
			dest->vertexBufferBinding->setBinding(vbi->first, dstBuf);*/
            dest.vertexBufferBinding.setBinding(entry.getKey(), dstBuf);
        }
		
		/*// Basic vertex info
        dest->vertexStart = this->vertexStart;
		dest->vertexCount = this->vertexCount;*/
        dest.vertexStart = vertexStart;
        dest.vertexCount = vertexCount;

        for (ENG_VertexElement ei : vertexDeclaration.getElementList()) {
            /*dest->vertexDeclaration->addElement(
                ei->getSource(),
                ei->getOffset(),
                ei->getType(),
                ei->getSemantic(),
                ei->getIndex() );*/
            dest.vertexDeclaration.addElement(ei.getSource(),
                    ei.getOffset(), ei.getType(), ei.getSemantic(), ei.getIndex());
        }

        dest.hwAnimationDataList = hwAnimationDataList;
        dest.hwAnimDataItemsUsed = hwAnimDataItemsUsed;

        return dest;
    }

    public void closeGapsInBindings() {
        if (!vertexBufferBinding.hasGaps()) {
            return;
        }
		
		/*// Check for error first
        const VertexDeclaration::VertexElementList& allelems = 
            vertexDeclaration->getElements();
        VertexDeclaration::VertexElementList::const_iterator ai;
        for (ai = allelems.begin(); ai != allelems.end(); ++ai)
        {
            const VertexElement& elem = *ai;
            if (!vertexBufferBinding->isBufferBound(elem.getSource()))
            {
                OGRE_EXCEPT(Exception::ERR_ITEM_NOT_FOUND,
                    "No buffer is bound to that element source.",
                    "VertexData::closeGapsInBindings");
            }
        }*/

        Iterator<ENG_VertexElement> ai =
                vertexDeclaration.getElementList().iterator();

        while (ai.hasNext()) {
            if (!vertexBufferBinding.isBufferBound(ai.next().getSource())) {
                throw new ENG_InvalidFieldStateException(
                        "No buffer is bound to that element source.");
            }
        }

        TreeMap<ENG_Short, ENG_Short> bindingIndexMap =
                new TreeMap<>();
        vertexBufferBinding.closeGaps(bindingIndexMap);

        short elemIndex = 0;
		 
		 /*for (ai = allelems.begin(); ai != allelems.end(); ++ai, ++elemIndex)
        {
            const VertexElement& elem = *ai;
            VertexBufferBinding::BindingIndexMap::const_iterator it =
                bindingIndexMap.find(elem.getSource());
            assert(it != bindingIndexMap.end());
            ushort targetSource = it->second;
            if (elem.getSource() != targetSource)
            {
                vertexDeclaration->modifyElement(elemIndex, 
                    targetSource, elem.getOffset(), elem.getType(), 
                    elem.getSemantic(), elem.getIndex());
            }
        }*/

        ai = vertexDeclaration.getElementList().iterator();

        while (ai.hasNext()) {
            ENG_VertexElement elem = ai.next();
            ENG_Short it = bindingIndexMap.get(new ENG_Short(elem.getSource()));
            if (it == null) {
                throw new ENG_InvalidFieldStateException("Could not get source index");
            }
            short targetSource = it.getValue();
            if (elem.getSource() != targetSource) {
                vertexDeclaration.modifyElement(elemIndex, targetSource,
                        elem.getOffset(), elem.getType(), elem.getSemantic(),
                        elem.getIndex());
            }
            ++elemIndex;
        }
    }

    public void removeUnusedBuffers() {
        TreeSet<ENG_Short> usedBuffers = new TreeSet<>();

        for (ENG_VertexElement eng_vertexElement : vertexDeclaration.getElementList()) {
            usedBuffers.add(new ENG_Short(eng_vertexElement.getSource()));
        }
		
		/*// Unset unused buffer bindings
        ushort count = vertexBufferBinding->getLastBoundIndex();
        for (ushort index = 0; index < count; ++index)
        {
            if (usedBuffers.find(index) == usedBuffers.end() &&
                vertexBufferBinding->isBufferBound(index))
            {
                vertexBufferBinding->unsetBinding(index);
            }
        }*/

        short count = vertexBufferBinding.getLastIndexBound();
        for (short index = 0; index < count; ++index) {
            if ((!usedBuffers.contains(new ENG_Short(index))) &&
                    (vertexBufferBinding.isBufferBound(index))) {
                vertexBufferBinding.unsetBinding(index);
            }
        }
        closeGapsInBindings();
    }

    public void convertPackedColour(VertexElementType srcType,
                                    VertexElementType destType) {
        if (destType != VertexElementType.VET_COLOUR_ABGR &&
                destType != VertexElementType.VET_COLOUR_ARGB) {
            throw new IllegalArgumentException("Invalid destType parameter");
        }
        if (srcType != VertexElementType.VET_COLOUR_ABGR &&
                srcType != VertexElementType.VET_COLOUR_ARGB) {
            throw new IllegalArgumentException("Invalid srcType parameter");
        }

        Iterator<Entry<ENG_Short, ENG_HardwareVertexBuffer>> bindi =
                vertexBufferBinding.getBindingMap().entrySet().iterator();
        int[] RGBA = new int[1];
        while (bindi.hasNext()) {
            Entry<ENG_Short, ENG_HardwareVertexBuffer> it = bindi.next();
			/*VertexDeclaration::VertexElementList elems = 
				vertexDeclaration->findElementsBySource(bindi->first);
			bool conversionNeeded = false;
			VertexDeclaration::VertexElementList::iterator elemi;
			for (elemi = elems.begin(); elemi != elems.end(); ++elemi)
			{
				VertexElement& elem = *elemi;
				if (elem.getType() == VET_COLOUR || 
					((elem.getType() == VET_COLOUR_ABGR || elem.getType() == VET_COLOUR_ARGB) 
					&& elem.getType() != destType))
				{
					conversionNeeded = true;
				}
			}*/
            LinkedList<ENG_VertexElement> elems =
                    vertexDeclaration.findElementsBySource(it.getKey().getValue());
            boolean conversionNeeded = false;
            for (ENG_VertexElement elem : elems) {
                if (elem.getType() == VertexElementType.VET_COLOUR ||
                        ((elem.getType() == VertexElementType.VET_COLOUR_ABGR ||
                                elem.getType() == VertexElementType.VET_COLOUR_ARGB)
                                && elem.getType() != destType)) {
                    conversionNeeded = true;
                    break;
                }
            }

            if (conversionNeeded) {
                int base = it.getValue().lock(
                        ENG_HardwareBuffer.LockOptions.HBL_NORMAL).position();
				/*for (size_t v = 0; v < bindi->second->getNumVertices(); ++v)
				{

					for (elemi = elems.begin(); elemi != elems.end(); ++elemi)
					{
						VertexElement& elem = *elemi;
						VertexElementType currType = (elem.getType() == VET_COLOUR) ?
							srcType : elem.getType();
						if (elem.getType() == VET_COLOUR || 
							((elem.getType() == VET_COLOUR_ABGR || elem.getType() == VET_COLOUR_ARGB) 
							&& elem.getType() != destType))
						{
							uint32* pRGBA;
							elem.baseVertexPointerToElement(pBase, &pRGBA);
							VertexElement::convertColourValue(currType, destType, pRGBA);
						}
					}
					pBase = static_cast<void*>(
						static_cast<char*>(pBase) + bindi->second->getVertexSize());
				}*/
                for (int v = 0; v < it.getValue().getNumVertices(); ++v) {
                    for (ENG_VertexElement elem : elems) {
                        VertexElementType currType =
                                (elem.getType() == VertexElementType.VET_COLOUR) ?
                                        srcType : elem.getType();
                        if (elem.getType() == VertexElementType.VET_COLOUR ||
                                ((elem.getType() == VertexElementType.VET_COLOUR_ABGR ||
                                        elem.getType() == VertexElementType.VET_COLOUR_ARGB)
                                        && elem.getType() != destType)) {
                            RGBA[0] = elem.baseVertexPointerToElement(base);
                            ENG_VertexElement.convertColorValue(
                                    currType, destType, RGBA);
                        }
                    }
                    base += it.getValue().getVertexSize();
                }
                it.getValue().unlock();
				
				/*const VertexDeclaration::VertexElementList& allelems = 
					vertexDeclaration->getElements();
				VertexDeclaration::VertexElementList::const_iterator ai;
				unsigned short elemIndex = 0;
				for (ai = allelems.begin(); ai != allelems.end(); ++ai, ++elemIndex)
				{
					const VertexElement& elem = *ai;
					if (elem.getType() == VET_COLOUR || 
						((elem.getType() == VET_COLOUR_ABGR || elem.getType() == VET_COLOUR_ARGB) 
						&& elem.getType() != destType))
					{
						vertexDeclaration->modifyElement(elemIndex, 
							elem.getSource(), elem.getOffset(), destType, 
							elem.getSemantic(), elem.getIndex());
					}
				}*/

                Iterator<ENG_VertexElement> ai =
                        vertexDeclaration.getElementList().iterator();
                short elemIndex = 0;
                while (ai.hasNext()) {
                    ENG_VertexElement elem = ai.next();
                    if (elem.getType() == VertexElementType.VET_COLOUR ||
                            ((elem.getType() == VertexElementType.VET_COLOUR_ABGR ||
                                    elem.getType() == VertexElementType.VET_COLOUR_ARGB)
                                    && elem.getType() != destType)) {
                        vertexDeclaration.modifyElement(elemIndex, elem.getSource(),
                                elem.getOffset(), destType, elem.getSemantic(),
                                elem.getIndex());
                    }
                    ++elemIndex;
                }
            }
        }
    }

    public void allocateHardwareAnimationElements(short count) {
        short texCoord = 0;
        for (ENG_VertexElement elem : vertexDeclaration.getElementList()) {
            if (elem.getSemantic() ==
                    VertexElementSemantic.VES_TEXTURE_COORDINATES) {
                ++texCoord;
            }
        }
        assert (texCoord <= ENG_Config.MAX_TEXTURE_COORD_SETS);

        for (int c = hwAnimationDataList.size(); c < count; ++c) {
            HardwareAnimationData data = new HardwareAnimationData();
            data.targetVertexElement =
                    vertexDeclaration.addElement(
                            vertexBufferBinding.getNextIndex(),
                            0,
                            VertexElementType.VET_FLOAT3,
                            VertexElementSemantic.VES_TEXTURE_COORDINATES,
                            texCoord++);

            hwAnimationDataList.add(data);
        }
    }

    /**
     * Only for GIWS
     *
     * @param vertexStart
     */
    public void setVertexStart(int vertexStart) {
        this.vertexStart = vertexStart;
    }

    /**
     * Only for GIWS
     *
     * @param vertexCount
     */
    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }
}
