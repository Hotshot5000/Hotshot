/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/17/21, 8:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import headwayent.blackholedarksun.MainApp;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;

/**
 * The idea of this class is that we have a low level input that adds events from
 * the device and gets data in raw. That data is transformed in the input convertor
 * to an usable format to be used by the engine.
 *
 * @author sebi
 */
public class ENG_InputManager {

    private final TreeMap<String, ENG_InputConvertor> inputConvertors = new TreeMap<>();
    private final TreeMap<String, ENG_IInput> inputs = new TreeMap<>();
    private final TreeMap<String, ENG_IInputListener> inputListeners = new TreeMap<>();
    private final TreeMap<String, ENG_IInputListenerFactory> inputListenerFactories = new TreeMap<>();
    private final TreeMap<String, ENG_InputConvertorFactory> inputConvertorFactories = new TreeMap<>();
    private final TreeMap<String, ENG_InputFactory> inputFactories = new TreeMap<>();
    private final TreeMap<String, ENG_InputConvertorListener> inputConvertorListeners = new TreeMap<>();
    private final TreeMap<String, ENG_InputStack> inputStacks = new TreeMap<>();
    private ENG_InputStack currentInputStack;
//    private static ENG_InputManager mgr;

    public ENG_InputManager() {
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
    }

    public ENG_IInput createInput(String instanceName, String type) {
        ENG_IInput input = inputFactories.get(type).createInstance(instanceName);
        inputs.put(instanceName, input);
        return input;
    }

    public ENG_IInput getInput(String name) {
        return inputs.get(name);
    }

    public void destroyInput(String instanceName) {
        inputs.remove(instanceName);
    }

    public void destroyAllInputs() {
        inputs.clear();
    }

    public ENG_IInputListener createInputListener(String instanceName, String input,
                                                  String type) {
        return createInputListener(instanceName, getInput(input), type);
    }

    public ENG_IInputListener createInputListener(String instanceName, ENG_IInput input,
                                                  String type) {
        ENG_IInputListener instance = inputListenerFactories.get(type)
                .createInstance(input);
        ENG_IInputListener listener = inputListeners.put(instanceName, instance);
        if (listener != null) {
            throw new IllegalArgumentException(instanceName + " is already an instance " +
                    "in inputListeners");
        }
        return instance;
    }

    public ENG_IInputListener getInputListener(String name) {
        return inputListeners.get(name);
    }

    public void destroyInputListener(String name) {
        ENG_IInputListener remove = inputListeners.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name +
                    " already exists in inputListeners");
        }
    }

    public void destroyAllInputListeners() {
        inputListeners.clear();
    }

    public void addInputListenerFactory(ENG_IInputListenerFactory factory) {
        ENG_IInputListenerFactory put = inputListenerFactories.put(
                factory.getTypeName(), factory);
        if (put != null) {
            throw new IllegalArgumentException(factory.getTypeName() + " already " +
                    "exists as an input listener factory");
        }
    }

    public void removeInputListenerFactory(ENG_IInputListenerFactory factory) {
        ENG_IInputListenerFactory remove =
                inputListenerFactories.remove(factory.getTypeName());
        if (remove == null) {
            throw new IllegalArgumentException(factory.getTypeName() +
                    " does not exist as an input listener factory");
        }
    }

    public void removeAllInputListenerFactories() {
        inputListenerFactories.clear();
    }

    public ENG_InputConvertor createInputConvertor(String instanceName,
                                                   ENG_IInput input, int maxQueueLen, String type) {
        ENG_InputConvertor ic =
                inputConvertorFactories.get(type).createInstance(
                        instanceName, input);
        inputConvertors.put(instanceName, ic);
        return ic;
    }

    public void destroyInputConvertors(String instanceName) {
        inputConvertors.remove(instanceName);
    }

    public void destroyAllInputConvertors() {
        inputConvertors.clear();
    }

    public ENG_InputConvertor getByName(String instanceName) {
        return inputConvertors.get(instanceName);
    }

    public Iterator<Entry<String, ENG_IInput>> getInputIterator() {
        return inputs.entrySet().iterator();
    }

    public Iterator<Entry<String, ENG_InputConvertor>> getInputConvertorIterators() {
        return inputConvertors.entrySet().iterator();
    }

    public void addInputFactory(ENG_InputFactory factory) {
        if (inputFactories.containsKey(factory.getTypeName())) {
            throw new IllegalArgumentException("factory already exists");
        }
        inputFactories.put(factory.getTypeName(), factory);
    }

    public void removeInputFactory(ENG_InputFactory factory) {
        ENG_InputFactory f =
                inputFactories.remove(factory.getTypeName());
        if (f == null) {
            throw new IllegalArgumentException("factory " + factory.getTypeName() +
                    " not is not in this manager!");
        }
    }

    public void removeAllInputFactories() {
        inputFactories.clear();
    }

    public void addInputConvertorFactory(ENG_InputConvertorFactory factory) {
        if (inputConvertorFactories.containsKey(factory.getTypeName())) {
            throw new IllegalArgumentException("factory already exists");
        }
        inputConvertorFactories.put(factory.getTypeName(), factory);
    }

    public void removeInputConvertorFactory(ENG_InputConvertorFactory factory) {
        ENG_InputConvertorFactory f =
                inputConvertorFactories.remove(factory.getTypeName());
        if (f == null) {
            throw new IllegalArgumentException("factory " + factory.getTypeName() +
                    " not is not in this manager!");
        }
    }

    public void removeAllInputConvertorFactories() {
        inputConvertorFactories.clear();
    }

    /**
     * This is the main update command from which all the data gathered from the
     * input is sent to all per converter registered listeners
     */
    public void sendInputData() {
//		for (ENG_InputConvertorListener list : inputConvertorListeners.values()) {
//			list.routeInput();
//		}
        if (currentInputStack != null) {
            getInputConvertorListener(
                    currentInputStack.getInputConvertorListener()).routeInput();
        }
    }

    public ENG_InputConvertorListener getInputConvertorListener(String name) {
        return inputConvertorListeners.get(name);
    }

    public void registerInputConvertorListener(String name,
                                               ENG_InputConvertorListener listener) {
        if (inputConvertorListeners.containsKey(name)) {
            throw new IllegalArgumentException("listener already exists");
        }
        inputConvertorListeners.put(name, listener);
    }

    public void unregisterInputConvertorListener(
            String listener) {
        inputConvertorListeners.remove(listener);
    }

    public void unregisterAllInputConvertorListeners() {
        inputConvertorListeners.clear();
    }

    /** @noinspection deprecation*/
    public void setInputListener(ENG_IInputListener inputListener) {
        Gdx.input.setInputProcessor(inputListener);
        if (inputListener != null) {
            Gdx.input.setCursorCatched(inputListener.isCursorGrabbed());
            Gdx.input.setCatchBackKey(inputListener.isBackKeyCaught());
        }
    }

    public void setCursorGrabbed(boolean grabbed) {
        Gdx.input.setCursorCatched(grabbed);
    }

    public void createInputStack(
            String name,
            String input,
            String inputListener,
            String inputConvertor,
            String inputConvertorListener) {
        if (inputStacks.containsKey(name)) {
            throw new IllegalArgumentException(name + " already exists as an " +
                    "input stack");
        }
        inputStacks.put(name,
                new ENG_InputStack(name, input, inputListener,
                        inputConvertor, inputConvertorListener));
    }

    public void destroyInputStack(String name) {
        ENG_InputStack remove = inputStacks.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " is not in the " +
                    "input stacks list");
        }
    }

    public void destroyAllInputStacks() {
        inputStacks.clear();
    }

    public void setInputStack(String name) {
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
//			MainApp.getMainThread().getDebuggingState().addPredefinedParameter("inputStack", name);
        }
        if (name != null) {
            ENG_InputStack inputStack = inputStacks.get(name);
            if (inputStack == null) {
                throw new IllegalArgumentException(name + " is not in the input stacks list");
            }
            if (currentInputStack != null) {
                resetCurrentInputStack();
            }
            currentInputStack = inputStack;
            // Reset all data that might have been left from a previous
            // use of this input (events etc.).
            getInput(currentInputStack.getInput()).reset();
            setInputListener(getInputListener(currentInputStack.getInputListener()));
        } else {
            if (currentInputStack != null) {
                setInputListener(null);
            }
            currentInputStack = null;
        }
    }

    public void resetCurrentInputStack() {
        resetInputStack(currentInputStack);
    }

    public void resetInputStack(ENG_InputStack inputStack) {
        if (inputStack != null) {
            ENG_InputConvertor inputConvertor = getByName(inputStack.getInputConvertor());
            if (inputConvertor != null) {
                inputConvertor.reset();
            } else {
                System.out.println("Could not find inputConvertor: " + inputStack.getInputConvertor());
            }
            ENG_IInput input = getInput(inputStack.getInput());
            if (input != null) {
                input.reset();
            } else {
                System.out.println("Could not find input: " + inputStack.getInput());
            }
        } else {
            System.out.println("No input stack available");
        }
    }

    public ENG_InputStack getCurrentInputStack() {
        return currentInputStack;
    }

    public static ENG_InputManager getSingleton() {
//        return mgr;
        return MainApp.getGame().getInputManager();
    }
}
