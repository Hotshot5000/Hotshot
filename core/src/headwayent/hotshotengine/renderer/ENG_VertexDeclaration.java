/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class ENG_VertexDeclaration {//implements Comparable<ENG_VertexDeclaration> {

    protected final LinkedList<ENG_VertexElement> elementList =
            new LinkedList<>();

    /**
     * For JNI
     *
     * @param source
     * @param offset
     * @param theType
     * @param semantic
     * @return
     */
    public ENG_VertexElement addElement(short source, int offset,
                                        short theType, short semantic) {
        return addElement(source, offset,
                VertexElementType.getVertexElementType(theType),
                VertexElementSemantic.getVertexElementSemantic(semantic),
                (short) 0);
    }

    public ENG_VertexElement addElement(short source, int offset,
                                        VertexElementType theType, VertexElementSemantic semantic) {
        return addElement(source, offset, theType, semantic, (short) 0);
    }

    public ENG_VertexElement addElement(short source, int offset,
                                        short theType, short semantic,
                                        short index) {
        return addElement(source, offset,
                VertexElementType.getVertexElementType(theType),
                VertexElementSemantic.getVertexElementSemantic(semantic));
    }

    public ENG_VertexElement addElement(short source, int offset,
                                        VertexElementType theType, VertexElementSemantic semantic,
                                        short index) {
        if (theType == VertexElementType.VET_COLOUR) {
            theType = ENG_VertexElement.getBestColorVertexElementType();
        }
        elementList.add(new ENG_VertexElement(
                source, offset, theType, semantic, index));
        return elementList.getLast();
    }

    public ENG_VertexElement insertElement(int atPosition, short source, int offset,
                                           VertexElementType theType, VertexElementSemantic semantic, short index) {
        if (atPosition >= elementList.size()) {
            addElement(source, offset, theType, semantic, index);
        }
        ENG_VertexElement elem = new ENG_VertexElement(
                source, offset, theType, semantic, index);
        elementList.add(atPosition, elem);
        return elem;
    }

    public ENG_VertexElement getElement(int index) {
        return elementList.get(index);
    }

    public void removeElement(int index) {
        elementList.remove(index);
    }

    public void removeElement(VertexElementSemantic semantic, int index) {
        for (ENG_VertexElement it : elementList) {
            if ((it.getSemantic() == semantic) && (it.getIndex() == index)) {
                elementList.remove(it);
                break;
            }
        }
    }

    public void removeAllElement() {
        elementList.clear();
    }

    public void modifyElement(short elem_index, short source, int offset,
                              VertexElementType theType, VertexElementSemantic semantic, short index) {
        elementList.remove(elem_index);
        elementList.add(elem_index, new ENG_VertexElement(
                source, offset, theType, semantic, index));

    }

    public ENG_VertexElement findElementBySemantic(VertexElementSemantic semantic,
                                                   int index) {
        for (ENG_VertexElement it : elementList) {
            if ((it.getSemantic() == semantic) && (it.getIndex() == index)) {
                return it;
            }
        }
        return null;
    }

    public LinkedList<ENG_VertexElement> findElementsBySource(short source) {
        LinkedList<ENG_VertexElement> ret = new LinkedList<>();
        findElementsBySource(source, ret);
        return ret;
    }

    public void findElementsBySource(short source, LinkedList<ENG_VertexElement> ret) {
        for (ENG_VertexElement it : elementList) {
            if (it.getSource() == source) {
                ret.add(it);
            }
        }
    }

    public int getVertexSize(short source) {
        Iterator<ENG_VertexElement> elemIt = elementList.iterator();
        int sz = 0;
        while (elemIt.hasNext()) {
            ENG_VertexElement it = elemIt.next();
            if (it.getSource() == source) {
                sz += it.getSize();
            }
        }
        return sz;
    }

    public ENG_VertexDeclaration clone(ENG_HardwareBufferManagerBase mgr) {
        ENG_HardwareBufferManagerBase manager;
        if (mgr != null) {
            manager = mgr;
        } else {
            manager = ENG_HardwareBufferManager.getSingleton();
        }
        ENG_VertexDeclaration ret = manager.createVertexDeclaration();

        for (ENG_VertexElement elem : elementList) {
            ret.addElement(
                    elem.getSource(), elem.getOffset(), elem.getType(),
                    elem.getSemantic(), elem.getIndex());
        }
        return ret;
    }

    public void sort() {
        Collections.sort(elementList);
    }

    public void closeGapsInSource() {
        if (elementList.isEmpty()) {
            return;
        }
        sort();

		/*VertexElementList::iterator i, iend;
        iend = mElementList.end();
        unsigned short targetIdx = 0;
        unsigned short lastIdx = getElement(0)->getSource();
        unsigned short c = 0;
        for (i = mElementList.begin(); i != iend; ++i, ++c)
        {
            VertexElement& elem = *i;
            if (lastIdx != elem.getSource())
            {
                targetIdx++;
                lastIdx = elem.getSource();
            }
            if (targetIdx != elem.getSource())
            {
                modifyElement(c, targetIdx, elem.getOffset(), elem.getType(), 
                    elem.getSemantic(), elem.getIndex());
            }

        }*/
        short targetIdx = 0;
        short lastIdx = elementList.element().getSource();
        short c = 0;

        for (ENG_VertexElement elem : elementList) {
            if (lastIdx != elem.getSource()) {
                ++targetIdx;
                lastIdx = elem.getSource();
            }
            if (targetIdx != elem.getSource()) {
                modifyElement(c++, targetIdx, elem.getOffset(), elem.getType(),
                        elem.getSemantic(), elem.getIndex());
            }
        }
    }

    public ENG_VertexDeclaration getAutoOrganizedDeclaration(
            boolean skeletalAnimation, boolean vertexAnimation) {
		/*VertexDeclaration* newDecl = this->clone();
        // Set all sources to the same buffer (for now)
        const VertexDeclaration::VertexElementList& elems = newDecl->getElements();
        VertexDeclaration::VertexElementList::const_iterator i;
        unsigned short c = 0;
        for (i = elems.begin(); i != elems.end(); ++i, ++c)
        {
            const VertexElement& elem = *i;
            // Set source & offset to 0 for now, before sort
            newDecl->modifyElement(c, 0, 0, elem.getType(), elem.getSemantic(), elem.getIndex());
        }
        newDecl->sort();*/
        ENG_VertexDeclaration newDecl = clone(null);
        Iterator<ENG_VertexElement> i = elementList.iterator();
        short c = 0;
        while (i.hasNext()) {
            ENG_VertexElement elem = i.next();
            newDecl.modifyElement(c++, (short) 0, 0, elem.getType(), elem.getSemantic(),
                    elem.getIndex());
        }
        newDecl.sort();

        int offset = 0;
        c = 0;
        short buffer = 0;
        VertexElementSemantic prevSemantic =
                ENG_VertexElement.VertexElementSemantic.VES_POSITION;
        i = elementList.iterator();
        while (i.hasNext()) {
            ENG_VertexElement elem = i.next();
            boolean splitWithPrev = false;
            boolean splitWithNext = false;
            switch (elem.getSemantic()) {
                case VES_POSITION:
                    splitWithPrev = vertexAnimation;
                    splitWithNext = vertexAnimation;
                    break;
                case VES_NORMAL:
                    splitWithPrev = ((prevSemantic == VertexElementSemantic.VES_BLEND_WEIGHTS) ||
                            prevSemantic == VertexElementSemantic.VES_BLEND_INDICES);
                    splitWithNext = (skeletalAnimation || vertexAnimation);
                    break;
                case VES_BLEND_WEIGHTS:
                    splitWithPrev = true;
                    break;
                case VES_BLEND_INDICES:
                    splitWithNext = true;
                    break;
                case VES_DIFFUSE:
                case VES_SPECULAR:
                case VES_TEXTURE_COORDINATES:
                case VES_BINORMAL:
                case VES_TANGENT:
                    break;
            }
            if (splitWithPrev && (offset != 0)) {
                ++buffer;
                offset = 0;
            }
            prevSemantic = elem.getSemantic();
            newDecl.modifyElement(c++, buffer, offset, elem.getType(),
                    elem.getSemantic(), elem.getIndex());

            if (splitWithNext) {
                ++buffer;
                offset = 0;
            } else {
                offset += elem.getSize();
            }
        }
        return newDecl;
		/*// Now sort out proper buffer assignments and offsets
        size_t offset = 0;
        c = 0;
		unsigned short buffer = 0;
        VertexElementSemantic prevSemantic = VES_POSITION;
        for (i = elems.begin(); i != elems.end(); ++i, ++c)
        {
            const VertexElement& elem = *i;

            bool splitWithPrev = false;
            bool splitWithNext = false;
            switch (elem.getSemantic())
            {
            case VES_POSITION:
                // For morph animation, we need positions on their own
                splitWithPrev = vertexAnimation;
                splitWithNext = vertexAnimation;
                break;
            case VES_NORMAL:
                // Normals can't sharing with blend weights/indices
                splitWithPrev = (prevSemantic == VES_BLEND_WEIGHTS || prevSemantic == VES_BLEND_INDICES);
                // All animated meshes have to split after normal
                splitWithNext = (skeletalAnimation || vertexAnimation);
                break;
            case VES_BLEND_WEIGHTS:
                // Blend weights/indices can be sharing with their own buffer only
                splitWithPrev = true;
                break;
            case VES_BLEND_INDICES:
                // Blend weights/indices can be sharing with their own buffer only
                splitWithNext = true;
                break;
            case VES_DIFFUSE:
            case VES_SPECULAR:
            case VES_TEXTURE_COORDINATES:
            case VES_BINORMAL:
            case VES_TANGENT:
                break;
            }

            if (splitWithPrev && offset)
            {
                ++buffer;
                offset = 0;
            }

            prevSemantic = elem.getSemantic();
            newDecl->modifyElement(c, buffer, offset,
                elem.getType(), elem.getSemantic(), elem.getIndex());

            if (splitWithNext)
            {
                ++buffer;
                offset = 0;
            }
            else
            {
                offset += elem.getSize();
            }
        }*/
    }

    public short getMaxSource() {
        Iterator<ENG_VertexElement> elemIt = elementList.iterator();
        short ret = 0;
        while (elemIt.hasNext()) {
            ENG_VertexElement elem = elemIt.next();
            if (elem.getSource() > ret) {
                ret = elem.getSource();
            }
        }
        return ret;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ENG_VertexDeclaration) {
            ENG_VertexDeclaration decl = (ENG_VertexDeclaration) obj;
            if (decl.getElementList().size() != elementList.size()) {
                return false;
            }
            Iterator<ENG_VertexElement> i = elementList.iterator();
            Iterator<ENG_VertexElement> rhsi = decl.getElementList().iterator();
            while (i.hasNext() && rhsi.hasNext()) {
                if (!i.next().equals(rhsi.next())) {
                    return false;
                }
            }
            return true;
        } else {
            throw new IllegalArgumentException(
                    "Use only ENG_VertexDeclaration for comparison");
        }
    }

    public boolean notEquals(Object obj) {
        return (!equals(obj));
    }

    /**
     * @return the elementList
     */
    public LinkedList<ENG_VertexElement> getElementList() {
        return elementList;
    }


    public int compareTo(ENG_VertexDeclaration another) {
        
        return hashCode() - another.hashCode();
    }

    public short getElementCount() {
        
        return (short) elementList.size();
    }
}
