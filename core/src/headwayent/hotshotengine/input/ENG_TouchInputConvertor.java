/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.input.ENG_KeyEvent.KeyAction;
import headwayent.hotshotengine.input.ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents;
import headwayent.hotshotengine.input.ENG_TouchEvent.TouchAction;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class ENG_TouchInputConvertor extends ENG_InputConvertor {


    public static class LastPosition {
        public final float x;
        public final float y;
        //	public TouchEventType eventType;
        public final int pointerId;

        public LastPosition(float x, float y, int pointerId) {
            this.x = x;
            this.y = y;
            this.pointerId = pointerId;
        }
    }

    private static final boolean REPEAT_KEY_PRESSES = true;
    private final Queue<Event> queue = new LinkedList<>();
    private final TreeMap<Integer, LinkedList<Event>> currentFireEvents = new TreeMap<>();
    private final ArrayList<Integer> currentFireEventsToDelete = new ArrayList<>();
//    private final TreeSet<Integer> justDowned = new TreeSet<>();
    private final HashSet<Integer> pressedKeys = new HashSet<>();
    private ENG_TouchInput mouseAndKeyboardInput;
    private int maxQueueLen;
    private String name = "";
    private final HashSet<Integer> keysToRemoveNextFrame = new HashSet<>();
    private MouseAndKeyboardEvents events;
    private final HashSet<Integer> justPressed = new HashSet<>();
    private final ArrayList<Integer> toRemove = new ArrayList<>();
    private float inv_width, inv_height;

    public String getName() {
        return name;
    }

    public ENG_TouchInputConvertor(String instanceName) {
        this(instanceName, null, 100);
    }

    public ENG_TouchInputConvertor(String instanceName, int maxQueueLen) {
        this(instanceName, null, maxQueueLen);
    }

    public ENG_TouchInputConvertor(String instanceName, ENG_TouchInput touchInput, int maxQueueLen) {
        name = instanceName;
        setMouseAndKeyboardInput(touchInput);
        this.maxQueueLen = maxQueueLen;
        resetInvScreenCoords();
    }

    public Queue<Event> getQueue() {
        return queue;
    }

    public void resetInvScreenCoords() {
        inv_width = 1.0f / ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getWidth();//GLRenderSurface.getSingleton().getWidth();
        inv_height = 1.0f / ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getHeight();//GLRenderSurface.getSingleton().getHeight();
    }

    @Override
    public Object read() {
        if (mouseAndKeyboardInput != null) {
            queue.clear();
            //	System.out.println("queue cleared");

//            long invScreenBeginTime = ENG_Utility.currentTimeMillis();
            // It looks like calculating the inv width or height takes a long fucking time. Around 17 ms.
            // What the fuck does roboVM do? What the fuck is this?
            // Just calculate these once and save them. If the orientation changes don't forget to update.
            // Fuck this!!!
//            float inv_width = 1.0f / ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getWidth();//GLRenderSurface.getSingleton().getWidth();
//            float inv_height = 1.0f / ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow().getHeight();//GLRenderSurface.getSingleton().getHeight();

//            long invScreenEndTime = ENG_Utility.currentTimeMillis();
//            System.out.println("invScreen time: " + (invScreenEndTime - invScreenBeginTime));
//            long mouseAndKeyboardInputBeginTime = ENG_Utility.currentTimeMillis();
            // We already have a reference to what getData() would return.
//            mouseAndKeyboardInput.getData();
//            long mouseAndKeyboardInputEndTime = ENG_Utility.currentTimeMillis();
//            System.out.println("mouseAndKeyboardInput getData() time: " + (mouseAndKeyboardInputEndTime - mouseAndKeyboardInputBeginTime));

//            long touchInputConvertorBeginTime = ENG_Utility.currentTimeMillis();
            if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.setTouchMouseAndKeyboardEvents(events);
            }
            if (MainApp.getMainThread().isInputState()) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                if (currentFrameInterval.getTouchMouseAndKeyboardEvents() != null) {
                    events.clearQueues();
                    events.set(currentFrameInterval.getTouchMouseAndKeyboardEvents());
                }
            }

            ConcurrentLinkedQueue<ENG_TouchEvent> touchEvents = events.touchEvents;
            ConcurrentLinkedQueue<ENG_KeyEvent> keyEvents = events.keyEvents;



            // First handle the key events so we can decrement the queue.
            // In the end we still need the queue lock no matter what.
            // No we actually don't since we no longer use the overall
            // queue length but the length of each of the queues.
            ReentrantLock queueLock = mouseAndKeyboardInput.getLock();
//            long keyEventsQueueSizeBeginTime = ENG_Utility.currentTimeMillis();
            int keyEventsQueueLen = keyEvents.size();
//            long keyEventsQueueSizeEndTime = ENG_Utility.currentTimeMillis();
//            System.out.println("keyEventsQueueLen time: " + (keyEventsQueueSizeEndTime - keyEventsQueueSizeBeginTime));
            if (keyEventsQueueLen >= maxQueueLen) {
                System.out.println("Key events QUEUE LIMIT REACHED!!!");
                keyEvents.clear();
                mouseAndKeyboardInput.decrementQueueLengthConverted(keyEventsQueueLen);
                keyEventsQueueLen = 0;
//				mouseAndKeyboardInput.clearQueue();
//				currentFireEvents.clear();
            }
            justPressed.clear();
            if (keyEventsQueueLen > 0) {
                mouseAndKeyboardInput.addEvent(new ENG_KeyEvent(0, KeyAction.NONE));
                ENG_KeyEvent event;
                boolean endReached = false;
                int increments = 0;

//				HashSet<Integer> justReleased = new HashSet<Integer>();
                while ((!endReached) && (event = keyEvents.poll()) != null) {
                    if (event.keyAction == KeyAction.NONE) {
                        // We're done here get out
                        if (keyEventsQueueLen > maxQueueLen / 2 && keyEventsQueueLen < maxQueueLen * 2 / 3) {
                            mouseAndKeyboardInput.addEvent(new ENG_KeyEvent(0, KeyAction.NONE));
                            ++increments;
                            continue;
                        }
                        endReached = true;
                    } else {
                        if (event.keyAction != null) {
                            KeyEventType keyEventType = null;
                            if (event.keyAction == KeyAction.DOWNED || event.keyAction == KeyAction.DOWN) {
                                keyEventType = KeyEventType.DOWN;
                                justPressed.add(event.keyCode);
//								System.out.println("keyCode " + event.keyCode + " just pressed");
                            } else if (event.keyAction == KeyAction.UP) {
                                keyEventType = KeyEventType.UP;
                                if (!justPressed.contains(event.keyCode)) {
                                    pressedKeys.remove(event.keyCode);
                                } else {
                                    keysToRemoveNextFrame.add(event.keyCode);
                                }
//								System.out.println("keyCode " + event.keyCode + " removed");
                            }
                            queue.add(new Event(event.keyCode, keyEventType));
                        } else {
                            queue.add(new Event(event.character));
//							System.out.println("character " + event.character + " added");
                        }
                    }
                }

                // After handling decrement the queue to have everything as before
                // we added the key events to the touch input system.
                mouseAndKeyboardInput.decrementQueueLengthConverted(keyEventsQueueLen + 1 + increments);
            }
            // If it hasn't been released it means it's still pressed so
            // generate press event
            if (REPEAT_KEY_PRESSES) {
                for (int keyCode : pressedKeys) {
                    queue.add(new Event(keyCode, KeyEventType.DOWN));
//					System.out.println("keyCode " + keyCode + " repeated");
                }
            }
            // Add the new down events that have already generated a queue add
            pressedKeys.addAll(justPressed);
            toRemove.clear();
            for (Integer key : keysToRemoveNextFrame) {
                if (!justPressed.contains(key)) {
                    pressedKeys.remove(key);
                    toRemove.add(key);
                }
            }
            //noinspection SlowAbstractSetRemoveAll
            keysToRemoveNextFrame.removeAll(toRemove);


//			int queueLen = mouseAndKeyboardInput.getQueueLength();
            // We don't care about the whole length of the queue anymore
//            long touchEventsQueueLenBeginTime = ENG_Utility.currentTimeMillis();
            int touchEventsQueueLen = touchEvents.size();
//            long touchEventsQueueLenEndTIme = ENG_Utility.currentTimeMillis();
//            System.out.println("touchEventsQueueLen time: " + (touchEventsQueueLenEndTIme - touchEventsQueueLenBeginTime));
//            System.out.println("touchEventsQueueLen: " + touchEventsQueueLen + " touchInputConvertor process time: " +
//                    (ENG_Utility.currentTimeMillis() - touchInputConvertorBeginTime));
            if (touchEventsQueueLen == 0) {
                return queue;
            }
            if (touchEventsQueueLen >= maxQueueLen) {
                System.out.println("QUEUE LIMIT REACHED!!!");
                mouseAndKeyboardInput.clearQueue();
                currentFireEvents.clear();
            }
            // Add event with the standard way not with direct way in order
            // not to miss the incrementQueueLength().
//			touchEvents.add(new ENG_TouchEvent(0, 0, 0, TouchAction.NONE));
            mouseAndKeyboardInput.addEvent(new ENG_TouchEvent(0, 0, 0, 0, 0, TouchAction.NONE));

            int initialQueueLen = touchEventsQueueLen + 1;

//			System.out.println("Queue length: " + touchEventsQueueLen);
            int lenConsumed = 0;
            ENG_TouchEvent event;
            boolean endReached = false;
            while ((!endReached) && (event = touchEvents.poll()) != null) {
                --touchEventsQueueLen;
                ++lenConsumed;
                //	ENG_Vector2D vec = null;
                switch (event.action) {
                    case DOWN: {
//					ENG_Vector2D vec = new ENG_Vector2D(
//							event.x * inv_width, event.y * inv_height);

                        // If we already have a pointerId in currentFireEvent
                        // just add another DOWN. If it's the first,
                        // make it a DOWNED event.
                        LinkedList<Event> list = new LinkedList<>();
                        list.add(
                                new Event(
                                        event.x * inv_width,
                                        event.y * inv_height,
                                        TouchEventType.DOWNED));
                        currentFireEvents.put(event.pointerId, list);
//                        justDowned.add(event.pointerId);

                    }
                    break;
                    case UP: {
                        //	vec = new LastPosition(
                        //			event.x * inv_width, event.y * inv_height,
                        //			TouchEventType.UP );
                        currentFireEventsToDelete.add(event.pointerId);
                        LinkedList<Event> vec =
                                currentFireEvents.get(event.pointerId);
                        if (vec != null) {
                            vec.add(
//								new ENG_Vector2D(
//										event.x * inv_width, event.y * inv_height)
                                    new Event(event.x * inv_width,
                                            event.y * inv_height,
                                            TouchEventType.UP));
                        }
                    }
                    break;
                    case MOVE: {
                        //	vec = new LastPosition(
                        //			event.x * inv_width, event.y * inv_height,
                        //			TouchEventType.MOVE );
                        LinkedList<Event> vec =
                                currentFireEvents.get(event.pointerId);
                        if (vec != null) {
                            //After clearing the queue we may have leftovers
                            // so we must check for null
                            vec.add(
//								new ENG_Vector2D(
//										event.x * inv_width, event.y * inv_height)
                                    new Event(event.x * inv_width,
                                            event.y * inv_height,
                                            TouchEventType.MOVE));
                            //	vec.set(event.x * inv_width, event.y * inv_height);
                        }
                    }
                    break;
                    case NONE: {
                        if (touchEventsQueueLen > maxQueueLen / 2 && touchEventsQueueLen < maxQueueLen * 2 / 3) {
                            //We should attempt again
                            touchEvents.add(new ENG_TouchEvent(0, 0, 0, 0, 0, TouchAction.NONE));
                            System.out.println("TRYING AGAIN!!!");
                            continue;
                        }
//					System.out.println("End reached!");
                        endReached = true;
                        break;
                    }
                }
            /*	if (vec != null) {
					queue.add(vec);
				}*/

            }
//			System.out.println("lenConsumed: " + lenConsumed);
            mouseAndKeyboardInput.decrementQueueLengthConverted(lenConsumed);
            if (initialQueueLen < lenConsumed) {
                System.out.println("Diff: " + (lenConsumed - initialQueueLen));
            }
            for (LinkedList<Event> vec : currentFireEvents.values()) {
                Event last = vec.peekLast();
                if (last.touchEventType != TouchEventType.UP) {
                    // If we have only one element than we add it
                    // and leave it there because we may need it
                    // in the next frames to stretch the event (replicate it)
                    // If we have more than one event then it's safe to
                    // pull all of them except the last one, which we
                    // may need to replicate for the next frames
                    if (vec.peekFirst() == last) {
                        // Only one element
                        queue.add(last);
                        // If we have only one element and that element
                        // is a DOWNED event then from now on we will have
                        // just a DOWN event
                        //					System.out.println("Added one event of type: " + last.type);

                        if (last.touchEventType == TouchEventType.DOWNED) {
                            vec.clear();
                            vec.add(new Event(last.pos.x, last.pos.y,
                                    TouchEventType.DOWN));
                            //						last.type = TouchEventType.DOWN;
                        }

                    } else {
                        while (vec.peek() != last) {
                            Event e = vec.poll();
                            queue.add(e);
                            //	System.out.println("many vecs " + vec.size());
                            //						System.out.println(
                            //								"added in while event type: " + e.type);
                        }
                    }
                } else {
                    // If we finish with an UP then it's safe to push everything in
                    // the queue
                    Event e;
                    while ((e = vec.poll()) != null) {
                        queue.add(e);
                    }
                }
            }
            for (Integer i : currentFireEventsToDelete) {
                currentFireEvents.remove(i);
            }
            currentFireEventsToDelete.clear();
//            long touchInputConvertorEndTime = ENG_Utility.currentTimeMillis();
//            System.out.println("touchInputConvertor read() time: " + (touchInputConvertorEndTime - touchInputConvertorBeginTime));
            return queue;
        } else {
            throw new NullPointerException("You must set an input");
        }
    }

    public void setMouseAndKeyboardInput(ENG_TouchInput touchInput) {
        this.mouseAndKeyboardInput = touchInput;
        events = (MouseAndKeyboardEvents) mouseAndKeyboardInput.getData();
    }

    public ENG_QueueInput getMouseAndKeyboardInput() {
        return mouseAndKeyboardInput;
    }

    public int getMaxQueueLen() {
        return maxQueueLen;
    }

    public void setMaxQueueLen(int maxQueueLen) {
        this.maxQueueLen = maxQueueLen;
    }
}
