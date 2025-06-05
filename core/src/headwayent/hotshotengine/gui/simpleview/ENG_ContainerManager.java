/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/17/21, 8:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gorillagui.ENG_SilverBack;

import java.util.HashMap;
import java.util.TreeMap;

public class ENG_ContainerManager {

    public abstract static class ContainerFactory {
        /** @noinspection deprecation*/
        public abstract ENG_Container createContainer(String name, Bundle bundle);

        public abstract void destroyContainer(ENG_Container c);
    }

    public static class PreviousMenu {
        public PreviousMenu previousMenu;
        public String previousContainerName;
        public String previousContainerType;
        /** @noinspection deprecation*/
        public Bundle bundle;
        public boolean recreateable;

        public PreviousMenu() {

        }

        /** @noinspection deprecation*/
        public PreviousMenu(PreviousMenu previousMenu,
                            String name, String type, Bundle bundle, boolean recreate) {
            this(name, type, bundle, recreate);
            this.previousMenu = previousMenu;

//            this.previousContainerName = previousMenu.previousContainerName;
//            this.previousContainerType = previousMenu.previousContainerType;
//            this.recreateable = previousMenu.recreateable;
        }

        /** @noinspection deprecation*/
        public PreviousMenu(String name, String type, Bundle bundle, boolean recreate) {
            this.previousContainerName = name;
            this.previousContainerType = type;
            this.bundle = bundle;
            this.recreateable = recreate;
        }
    }

//    private static ENG_ContainerManager mgr;

    private ENG_Container currentContainer;
    private final TreeMap<String, ENG_Container> containerList = new TreeMap<>();
    private final TreeMap<String, ContainerFactory> factoryList = new TreeMap<>();
    private final TreeMap<String, ENG_Container.ContainerListenerFactory> containerListenerFactoryList = new TreeMap<>();
    private PreviousMenu previousMenu;

    public ENG_ContainerManager() {
        
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
    }

    public void addContainerListenerFactory(String type, ENG_Container.ContainerListenerFactory l) {
        ENG_Container.ContainerListenerFactory put = containerListenerFactoryList.put(type, l);
        if (put != null) {
            throw new IllegalArgumentException(type + " container factory has already been added");
        }
    }

    public void removeContainerListenerFactory(String type) {
        ENG_Container.ContainerListenerFactory remove = containerListenerFactoryList.remove(type);
        if (remove == null) {
            throw new IllegalArgumentException(type + " is not a valid installed container factory");
        }
    }

    public void removeAllContainerListenerFactories() {
        containerListenerFactoryList.clear();
    }

    public void addFactory(String type, ContainerFactory fact) {
        ContainerFactory put = factoryList.put(type, fact);
        if (put != null) {
            throw new IllegalArgumentException(type + " factory has already been added");
        }
    }

    public void removeFactory(String name) {
        ContainerFactory remove = factoryList.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " is not a valid installed factory");
        }
    }

    public void removeAllFactories() {
        factoryList.clear();
    }

    public ENG_Container getContainer(String name) {
        return containerList.get(name);
    }

    /** @noinspection deprecation*/
    public ENG_Container createContainer(
            String name, String type, Bundle bundle) {
        return createContainer(name, type, bundle, false);
    }

    /** @noinspection deprecation*/
    public ENG_Container createContainer(
            String name, String type, Bundle bundle, boolean autoRecreate) {
        return createContainer(name, type, bundle, autoRecreate, (ContainerListenerObject[]) null);
    }

    public static class ContainerListenerObject {
        public String name;
        /** @noinspection deprecation*/
        public Bundle bundle;

        public ContainerListenerObject() {

        }

        public ContainerListenerObject(String name) {
            this.name = name;
        }

        /** @noinspection deprecation*/
        public ContainerListenerObject(String name, Bundle bundle) {
            this(name);
            this.bundle = bundle;
        }
    }

    /** @noinspection deprecation */
    public ENG_Container createContainer(
            String name, String type, Bundle bundle, boolean autoRecreate,
            ContainerListenerObject... listenerList) {
        ContainerFactory factory = factoryList.get(type);
        if (factory == null) {
            throw new IllegalArgumentException(type + " is not a valid installed container type");
        }
        if (autoRecreate) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean(ENG_Container.RECREATE_AFTER_DESTRUCTION, true);
            bundle.putString(ENG_Container.CONTAINER_TYPE, type);
        }
        ENG_Container container = factory.createContainer(name, bundle);
        ENG_Container put = containerList.put(name, container);
        if (put != null) {
            throw new IllegalArgumentException(name + " is already listed as a container");
        }
        if (listenerList != null) {
            for (ContainerListenerObject s : listenerList) {
                createContainerListener(container, s);
            }
        }
        return container;
    }

    /** @noinspection deprecation*/
    public void createContainerListener(String container, String type, Bundle bundle) {
        createContainerListener(getContainer(container), type, bundle);
    }

    /** @noinspection deprecation*/
    public void createContainerListener(ENG_Container container, String type, Bundle bundle) {
        ENG_Container.ContainerListenerFactory f = containerListenerFactoryList.get(type);
        if (f == null) {
            throw new IllegalArgumentException(type + " is not a valid installed container listener factory type");
        }
        container.addListener(f.createContainerListener(container, bundle));
    }

    public void createContainerListener(String container, ContainerListenerObject s) {
        createContainerListener(getContainer(container), s);
    }

    public void createContainerListener(ENG_Container container, ContainerListenerObject s) {
        createContainerListener(container, s.name, s.bundle);
    }

    public void removeContainerListener(String container, String type) {
        removeContainerListener(getContainer(container), type);
    }

    public void removeContainerListener(ENG_Container container, String type) {
        ENG_Container.ContainerListenerFactory f = containerListenerFactoryList.get(type);
        if (f == null) {
            throw new IllegalArgumentException(type + " is not a valid installed container listener factory type");
        }
        container.removeListener(type);
    }

    public void destroyContainer(String name) {
        destroyContainer(name, false, false);
    }

    public void destroyContainer(String name, boolean skipRecreation, boolean skipGLDelete) {
        ENG_Container remove = containerList.remove(name);
        if (remove != null) {
            remove.destroy(skipRecreation, skipGLDelete);
        } else {
            throw new IllegalArgumentException(name + " is not a valid container");
        }
    }

    public void destroyAllContainers() {
        destroyAllContainers(false, false);
    }

    public void destroyAllContainers(boolean skipRecreation, boolean skipGLDelete) {
        HashMap<String, ENG_Container> tempContainerList = new HashMap<>(containerList);
        for (String name : tempContainerList.keySet()) {
            destroyContainer(name, skipRecreation, skipGLDelete);
        }
        currentContainer = null;
//        containerList.clear();
    }

    public void update() {
        ENG_SilverBack.getSingleton().frameStarted(null);
        if (currentContainer != null) {
            if (currentContainer.isDestroyed()) {
                currentContainer = null;
            } else {
                currentContainer.update();
            }
        }
    }

    public ENG_Container getCurrentContainer() {
        return currentContainer;
    }

    public boolean isCurrentContainerActive() {
        return currentContainer != null && !currentContainer.isDestroyed();
    }

    private void checkContainerNotNull(String name) {
        if (!containerList.containsKey(name)) {
            throw new IllegalArgumentException(name + " is not a valid container name");
        }
    }

    private ENG_Container getContainerCheckNull(String name) {
        checkContainerNotNull(name);
        return getContainer(name);
    }

    public void removeCurrentContainer() {
//        setCurrentContainer((ENG_Container) null);
        if (currentContainer != null) {
            destroyContainer(currentContainer.getName());
        }
    }

    public void setCurrentContainer(String name) {
        setCurrentContainer(name, true);
    }

    public void setCurrentContainer(String name, boolean destroyPrevious) {

        setCurrentContainer(getContainerCheckNull(name), destroyPrevious);
    }

    public void setCurrentContainer(String name, boolean destroyPrevious, boolean savePreviousMenu) {
        setCurrentContainer(getContainerCheckNull(name), destroyPrevious, savePreviousMenu);
    }

    public void setCurrentContainer(ENG_Container currentContainer) {
        setCurrentContainer(currentContainer, true);
    }

    public void setCurrentContainer(ENG_Container currentContainer, boolean destroyPrevious) {
        setCurrentContainer(currentContainer, destroyPrevious, true);
    }

    public void setCurrentContainer(ENG_Container currentContainer, boolean destroyPrevious, boolean savePreviousMenu) {
        if (destroyPrevious && getCurrentContainer() != null) {
            destroyContainer(getCurrentContainer().getName());
        }
        if (this.currentContainer != null) {
            if (savePreviousMenu) {
                // Save it for previous
                if (previousMenu != null) {
                    previousMenu = new PreviousMenu(previousMenu,
                            this.currentContainer.getName(),
                            this.currentContainer.getType(),
                            this.currentContainer.getBundle(),
                            this.currentContainer.isRecreate());
                } else {
                    previousMenu = new PreviousMenu(
                            this.currentContainer.getName(),
                            this.currentContainer.getType(),
                            this.currentContainer.getBundle(),
                            this.currentContainer.isRecreate());
                }
            }
        } else {
            previousMenu = null;
        }
        this.currentContainer = currentContainer;
    }

    public void setPreviousContainer() {
        setPreviousContainer(null);
    }

    /**
     * Only works for non recreatable container or else the bundle is
     * ignored and the previously used one is reused.
     *
     * @param bundle
     * @noinspection deprecation
     */
    public void setPreviousContainer(Bundle bundle) {
        if (previousMenu != null) {
            ENG_Container previousContainer;
//            previousMenu = previousMenu.previousMenu;
            if (previousMenu.recreateable) {
                previousContainer = getContainer(previousMenu.previousContainerName);
            } else {
                previousContainer = createContainer(
                        previousMenu.previousContainerName,
                        previousMenu.previousContainerType,
                        bundle != null ? bundle : previousMenu.bundle);
            }
            previousMenu = previousMenu.previousMenu;
            if (currentContainer != null) {
                destroyContainer(currentContainer.getName());
            }
            this.currentContainer = previousContainer;
        } else {
            throw new NullPointerException("There is no previous container to set");
        }
    }

    public static ENG_ContainerManager getSingleton() {
//        if (mgr == null && MainActivity.isDebugmode()) {
//            throw new NullPointerException("ENG_ContainerManager not initialized");
//        }
//        return mgr;
        return MainApp.getGame().getContainerManager();
    }

}
