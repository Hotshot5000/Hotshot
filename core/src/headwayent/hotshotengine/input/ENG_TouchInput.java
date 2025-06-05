/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/29/21, 9:58 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import headwayent.hotshotengine.input.ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents;

import java.util.ArrayList;

public class ENG_TouchInput extends ENG_QueueInput implements ENG_IInput {

    //	private ConcurrentLinkedQueue<ENG_TouchEvent> touchEvents =
//			new ConcurrentLinkedQueue<ENG_TouchEvent>();
    private final MouseAndKeyboardEvents events = new MouseAndKeyboardEvents();

    // In the case when we get too far behind we provide a clearData mechanism
    // to flush out the queue. The only problem is that there still might
    // exist some leftovers such as a down event that still produces
    // effects right now. So we must kill those events but in order to kill
    // them we must know which they are.
    // So we keep a list with all the pointerIds used since the last
    // getData() event.
    // There is no problem if an event started before a getData() and ended
    // before a flush or if it started after getData() and ended before
    // a flush (it just gets ignored), but what if we have an event
    // that started before the getData() and still continues after the
    // flush or started after a getData() and still continues?
    private final ArrayList<Integer> pointerIds = new ArrayList<>();

    public ENG_TouchInput(String name) {
//		this.name = name;
    }

    public ENG_TouchInput() {

    }

	
	
/*	public ENG_TouchInput() {
		this(100);
	}
	
	public ENG_TouchInput(int maxQueueLen) {
		this.maxQueueLen = maxQueueLen;
	}*/

    @Override
    public Object getData() {

		
	/*	if (touchEvents.size() >= maxQueueLen) {
			// Empty it and make sure we kill every event by setting it to up
			
			clearData();
		}*/
        //Add a last none object

        return events;//touchEvents;
    }

    @Override
    public void reset() {

        clearQueue();
    }

    public void clearQueue() {


//		touchEvents.clear();
        events.clearQueues();
        super.clearQueue();
	/*	pointerLock.lock();
		try {
			touchEvents.clear();
			for (int pointId : pointerIds) {
				touchEvents.add(new ENG_TouchEvent(0, 0, pointId, TouchAction.UP));
			}
			pointerIds.clear();
		} finally {
			pointerLock.unlock();
		}*/
    }

    public void addEvent(ENG_TouchEvent event) {
        incrementQueueLength();

        events.touchEvents.add(event);
//        System.out.println("addEvent currentTime: " + ENG_Utility.nanoTime());
	/*	pointerLock.lock();
		try {
			touchEvents.add(event);
			
			switch (event.action) {
			case DOWN:
				pointerIds.add(event.pointerId);
				break;
			case UP:
				pointerIds.remove(event.pointerId);
				break;
			}
		} finally {
			pointerLock.unlock();
		}*/
    }

    public void addEvent(ENG_KeyEvent event) {
        incrementQueueLength();

        events.keyEvents.add(event);
    }

}
