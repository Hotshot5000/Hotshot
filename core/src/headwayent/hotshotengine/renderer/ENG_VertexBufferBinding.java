/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Short;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ENG_VertexBufferBinding {// implements Comparable<ENG_VertexBufferBinding> {

    protected TreeMap<ENG_Short, ENG_HardwareVertexBuffer> bindingMap =
            new TreeMap<>();
    protected short highIndex;

    public void setBinding(short index, ENG_HardwareVertexBuffer buffer) {
        setBinding(new ENG_Short(index), buffer);
    }

    public void setBinding(ENG_Short index, ENG_HardwareVertexBuffer buffer) {
        buffer.incrementBindingCount();
        bindingMap.put(index, buffer);
        highIndex = (short) Math.max(highIndex, index.getValue() + 1);
    }

    public void unsetBinding(short index) {
        unsetBinding(new ENG_Short(index));
    /*	Iterator<ENG_Short> it = bindingMap.keySet().iterator();
		while (it.hasNext()) {
			ENG_Short v = it.next();
			if (index == v.getValue()) {
				bindingMap.remove(v);
			}
		}*/ //Slow
    }

    public void unsetBinding(ENG_Short index) {
        unsetBinding(index, false);
    }

    public void unsetBinding(ENG_Short index, boolean skipGLDelete) {
        ENG_HardwareVertexBuffer remove = bindingMap.remove(index);
        if (remove != null) {
            if (remove.decrementBindingCount() == 0) {
                remove.destroy(skipGLDelete);
            }
        }
    }

    public void unsetAllBindings(boolean skipGLDelete) {
        for (ENG_HardwareVertexBuffer buf : bindingMap.values()) {
            if (buf.decrementBindingCount() == 0) {
                buf.destroy(skipGLDelete);
            }
        }
        bindingMap.clear();
        highIndex = 0;
    }

    public ENG_HardwareVertexBuffer getBuffer(short index) {
        return bindingMap.get(new ENG_Short(index));
    }

    public ENG_HardwareVertexBuffer getBuffer(ENG_Short index) {
        return bindingMap.get(index);
    }

    public boolean isBufferBound(short index) {
        return isBufferBound(new ENG_Short(index));
    }

    public boolean isBufferBound(ENG_Short index) {
        return bindingMap.containsKey(index);
    }

    public short getLastIndexBound() {
        if (bindingMap.isEmpty()) {
            return 0;
        }
	/*	Iterator<ENG_Short> it = bindingMap.keySet().iterator();
		ENG_Short last = null;
		while (it.hasNext()) {
			last = it.next();
		}*/ //Too slow
        return (short) (bindingMap.lastKey().getValue() + 1);
    }

    public boolean hasGaps() {
        return !bindingMap.isEmpty() && (bindingMap.lastKey().getValue() + 1) != bindingMap.size();
    }

    public void closeGaps(TreeMap<ENG_Short, ENG_Short> bindingIndexMap) {
        bindingIndexMap.clear();
        TreeMap<ENG_Short, ENG_HardwareVertexBuffer> newBindingMap =
                new TreeMap<>();
        short targetIndex = 0;
        Iterator<Entry<ENG_Short, ENG_HardwareVertexBuffer>> it =
                bindingMap.entrySet().iterator();
        Entry<ENG_Short, ENG_HardwareVertexBuffer> entry;
        ENG_Short index = null;
        while (it.hasNext()) {
            entry = it.next();
            //	index = new ENG_Short(targetIndex);
            bindingIndexMap.put(entry.getKey(), new ENG_Short(targetIndex));
            newBindingMap.put(new ENG_Short(targetIndex), entry.getValue());
            ++targetIndex;
        }
        bindingMap = newBindingMap;
        highIndex = targetIndex;
    }

    public int getBufferCount() {
        return bindingMap.size();
    }

    public short getNextIndex() {
        return highIndex++;
    }

    /**
     * @return the bindingMap
     */
    public TreeMap<ENG_Short, ENG_HardwareVertexBuffer> getBindingMap() {
        return bindingMap;
    }


    public int compareTo(ENG_VertexBufferBinding another) {
        

        return hashCode() - another.hashCode();
    }

//    public boolean equals(Object o) {
//        return (this == o);
//    }

    public void destroy(boolean skipGLDelete) {
        
        unsetAllBindings(skipGLDelete);
    }
}
