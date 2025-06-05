/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import headwayent.hotshotengine.input.ENG_InputConvertor.TouchEventType;

import java.util.ArrayList;

public class ENG_AbstractButton {

    private final ArrayList<ENG_IButtonListener> listeners =
            new ArrayList<>();

    public ENG_AbstractButton() {
        super();
    }

    public void addListener(ENG_IButtonListener listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("listener already added");
        }
        listeners.add(listener);
    }

    public void removeListener(ENG_IButtonListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

/*	protected Iterator<ENG_IButtonListener> getListenerIterator() {
		return listeners.iterator();
	}*/

    public void updateListeners(float x, float y, TouchEventType type) {
	/*	Iterator<ENG_IButtonListener> iterator = getListenerIterator();
		while (iterator.hasNext()) {
			iterator.next().onClick(x, y);
		}*/
        for (ENG_IButtonListener list : listeners) {
            list.onClick(x, y, type);
        }
    }

}