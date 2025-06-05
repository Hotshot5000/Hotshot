/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/29/21, 9:58 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ENG_MouseAndKeyboardInput extends ENG_QueueInput implements ENG_IInput {

    public static class MouseAndKeyboardEvents {
        public final ConcurrentLinkedQueue<ENG_TouchEvent> touchEvents =
                new ConcurrentLinkedQueue<>();
        public final ConcurrentLinkedQueue<ENG_KeyEvent> keyEvents =
                new ConcurrentLinkedQueue<>();

        public void clearQueues() {
            touchEvents.clear();
            keyEvents.clear();
        }

        public void set(MouseAndKeyboardEvents events) {
            for (ENG_TouchEvent e : events.touchEvents) {
                touchEvents.add(new ENG_TouchEvent(e.x, e.y, e.dx, e.dy, e.pointerId, e.action));
            }
            for (ENG_KeyEvent e : events.keyEvents) {
                keyEvents.add(new ENG_KeyEvent(e.keyCode, e.character, e.keyAction));
            }
        }
    }

    private final MouseAndKeyboardEvents events = new MouseAndKeyboardEvents();

    public ENG_MouseAndKeyboardInput(String name) {

        super(name);
    }

    @Override
    public Object getData() {
        
        return events;
    }

    @Override
    public void reset() {
        
        clearQueue();
    }

    public void addEvent(ENG_TouchEvent event) {
        incrementQueueLength();

        events.touchEvents.add(event);
//        System.out.println("addEvent touch currentTime: " + ENG_Utility.nanoTime());
    }

    public void addEvent(ENG_KeyEvent event) {
        incrementQueueLength();

        events.keyEvents.add(event);
//        System.out.println("addEvent key currentTime: " + ENG_Utility.nanoTime());
    }

    @Override
    public void clearQueue() {
        
        super.clearQueue();
        events.clearQueues();
    }
}
