/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.renderer.ENG_Frustum.OrientationMode;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointer;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_OverlayManagerNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_OverlaySystemNativeWrapper;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ENG_OverlayManager implements ENG_IDisposable, ENG_NativePointer {

//    private static ENG_OverlayManager mgr;

    protected final ENG_OverlaySystemNativeWrapper overlaySystemWrapper = new ENG_OverlaySystemNativeWrapper();
    protected final ENG_OverlayManagerNativeWrapper overlayManagerWrapper;
    protected final TreeMap<String, ENG_Overlay> mOverlayMap = new TreeMap<>();
//    protected ArrayList<String> mLoadedScripts = new ArrayList<String>();

    protected int mLastViewportWidth, mLastViewportHeight;
    protected boolean mViewportDimensionsChanged;
    protected OrientationMode mLastViewportOrientationMode;

    protected final TreeMap<String, ENG_OverlayElementFactory> mFactories = new TreeMap<>();

    protected final TreeMap<String, ENG_OverlayElement> mInstances = new TreeMap<>();

    protected final TreeMap<String, ENG_OverlayElement> mTemplates = new TreeMap<>();

    public ENG_OverlayManager() {
        overlayManagerWrapper = new ENG_OverlayManagerNativeWrapper();
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
    }

    @Override
    public void destroy() {
        overlaySystemWrapper.destroy();
    }

    public ENG_Overlay create(String name) {
        ENG_Overlay overlay = mOverlayMap.get(name);
        if (overlay == null) {
            overlay = new ENG_Overlay(name);
            mOverlayMap.put(name, overlay);
            return overlay;
        } else {
            throw new IllegalArgumentException(name + " overlay already exists");
        }
    }

    public ENG_Overlay getByName(String name) {
        return mOverlayMap.get(name);
    }

    public void destroy(String name, boolean skipGLDelete) {
        ENG_Overlay remove = mOverlayMap.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " overlay does not exist");
        }
        remove.destroy(skipGLDelete);
    }

    public void destroy(ENG_Overlay o, boolean skipGLDelete) {
        destroy(o.getName(), skipGLDelete);
    }

    public void destroyOverlayAndChildren(String name, boolean skipGLDelete) {
        ENG_Overlay remove = mOverlayMap.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " is not a valid overlay");
        }
        Iterator<ENG_OverlayContainer> iterator = remove.get2DContainerIterator();
        while (iterator.hasNext()) {
            destroyOverlayContainer(iterator.next(), skipGLDelete);
        }
    }

    public void destroyOverlayContainer(ENG_OverlayContainer container, boolean skipGLDelete) {
        Iterator<Entry<String, ENG_OverlayContainer>> iterator =
                container.getChildContainerIterator();
    /*	while (iterator.hasNext()) {
			destroyOverlayContainer(iterator.next().getValue());
		}*/
        Iterator<Entry<String, ENG_OverlayElement>> iterator2 =
                container.getChildIterator();
        while (iterator2.hasNext()) {
            destroyOverlayElement(iterator2.next().getValue(), skipGLDelete);
        }
        destroyOverlayElement(container, skipGLDelete);
    }

    public void destroyAll(boolean skipGLDelete) {
        for (ENG_Overlay overlay : mOverlayMap.values()) {
            overlay.destroy(skipGLDelete);
        }

        mOverlayMap.clear();
//        mLoadedScripts.clear();
    }

    public void destroyOverlayElement(ENG_OverlayElement elem, boolean skipGLDelete) {
        destroyOverlayElement(elem, false, skipGLDelete);
    }

    public void destroyOverlayElement(ENG_OverlayElement elem, boolean isTemplate, boolean skipGLDelete) {
        destroyOverlayElement(elem.getName(), isTemplate, skipGLDelete);

    }

    public void destroyOverlayElement(String name, boolean isTemplate, boolean skipGLDelete) {
        TreeMap<String, ENG_OverlayElement> elementMap = getElementMap(isTemplate);
        ENG_OverlayElement remove = elementMap.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " is not a valid" +
                    " instance in this list");
        }
        remove.destroy(skipGLDelete);
    }

    public void destroyAllOverlayElements() {
        destroyAllOverlayElements(false);
    }

    public void destroyAllOverlayElements(boolean isTemplate) {
        destroyAllOverlayElements(getElementMap(isTemplate));
    }

    private void destroyAllOverlayElements(
            TreeMap<String, ENG_OverlayElement> elementMap) {

        for (ENG_OverlayElement elem : elementMap.values()) {
            ENG_OverlayContainer parent = elem.getParent();
            if (parent != null) {
                parent.removeChild(elem.getName());
            }
        }
        elementMap.clear();
    }

    public Iterator<Entry<String, ENG_Overlay>> getOverlayIterator() {
        return mOverlayMap.entrySet().iterator();
    }

    public void _queueOverlaysForRendering(ENG_Camera cam,
                                           ENG_RenderQueue pQueue, ENG_Viewport vp) {
        boolean orientationModeChanged = false;

        if (mLastViewportWidth != vp.getActualWidth() ||
                mLastViewportHeight != vp.getActualHeight() ||
                orientationModeChanged) {
            mViewportDimensionsChanged = true;
            mLastViewportWidth = vp.getActualWidth();
            mLastViewportHeight = vp.getActualHeight();
        } else {
            mViewportDimensionsChanged = false;
        }

        for (Entry<String, ENG_Overlay> it : mOverlayMap.entrySet()) {
            it.getValue()._findVisibleObjects(cam, pQueue);
        }
    }

    public boolean hasViewportChanged() {
        return mViewportDimensionsChanged;
    }

    public float getViewportWidth() {
        return mLastViewportWidth;
    }

    public float getViewportHeight() {
        return mLastViewportHeight;
    }

    public float getViewportAspectRatio() {
        return ((float) mLastViewportWidth) / ((float) mLastViewportHeight);
    }

    public OrientationMode getViewportOrientationMode() {
        throw new UnsupportedOperationException();
    }

    public ENG_OverlayElement createOverlayElement(String typeName,
                                                   String instanceName) {
        return createOverlayElement(typeName, instanceName, false);
    }

    public ENG_OverlayElement createOverlayElement(String typeName,
                                                   String instanceName, boolean isTemplate) {
        return createOverlayElementImpl(typeName, instanceName,
                getElementMap(isTemplate));
    }

    protected ENG_OverlayElement createOverlayElementImpl(String typeName,
                                                          String instanceName, TreeMap<String, ENG_OverlayElement> elementMap) {
        ENG_OverlayElement element = elementMap.get(instanceName);
        if (element != null) {
            throw new IllegalArgumentException(instanceName + " already exists");
        }

        element = createOverlayElementFromFactory(typeName, instanceName);

        elementMap.put(instanceName, element);

        return element;
    }

    protected ENG_OverlayElement createOverlayElementFromFactory(String typeName,
                                                                 String instanceName) {

        ENG_OverlayElementFactory factory = mFactories.get(typeName);
        if (factory == null) {
            throw new IllegalArgumentException(typeName + " factory does not exist");
        }
        return factory.createInstance(instanceName);
    }

    protected TreeMap<String, ENG_OverlayElement> getElementMap(boolean isTemplate) {
        return isTemplate ? mTemplates : mInstances;
    }

    public ENG_OverlayElement getOverlayElement(String name) {
        return getOverlayElement(name, false);
    }

    public ENG_OverlayElement getOverlayElement(String name, boolean isTemplate) {
        return getOverlayElementImpl(name, getElementMap(isTemplate));
    }

    public boolean hasOverlayElement(String name) {
        return hasOverlayElement(name, false);
    }

    public boolean hasOverlayElement(String name, boolean isTemplate) {
        return hasOverlayElementImpl(name, getElementMap(isTemplate));
    }

    protected boolean hasOverlayElementImpl(String name,
                                            TreeMap<String, ENG_OverlayElement> elementMap) {
        return elementMap.get(name) != null;
    }

    protected ENG_OverlayElement getOverlayElementImpl(String name,
                                                       TreeMap<String, ENG_OverlayElement> elementMap) {
        ENG_OverlayElement element = elementMap.get(name);
        if (element == null) {
            throw new IllegalArgumentException(name + " does not exist");
        }
        return element;
    }

    public void addOverlayElementFactory(ENG_OverlayElementFactory elemFactory) {
        mFactories.put(elemFactory.getTypeName(), elemFactory);
    }

    public TreeMap<String, ENG_OverlayElementFactory> getOverlayElementFactoryMap() {
        return mFactories;
    }

    public ENG_OverlayElement createOverlayElementFromTemplate(String templateName,
                                                               String typeName, String instanceName) {
        return createOverlayElementFromTemplate(templateName, typeName, instanceName,
                false);
    }

    public ENG_OverlayElement createOverlayElementFromTemplate(String templateName,
                                                               String typeName, String instanceName, boolean isTemplate) {
        ENG_OverlayElement newObj;

        if (templateName.isEmpty()) {
            newObj = createOverlayElement(typeName, instanceName, isTemplate);
        } else {
            // no template
            ENG_OverlayElement templateGui = getOverlayElement(templateName, true);
            String typeNameToCreate;
            if (typeName.isEmpty()) {
                typeNameToCreate = templateGui.getTypeName();
            } else {
                typeNameToCreate = typeName;
            }

            newObj = createOverlayElement(typeNameToCreate, instanceName, isTemplate);

            newObj.copyFromTemplate(templateGui);
        }

        return newObj;
    }

    ENG_OverlayElement cloneOverlayElementFromTemplate(String templateName,
                                                       String instanceName) {
        ENG_OverlayElement templateGui = getOverlayElement(templateName, true);
        return templateGui.clone(instanceName);
    }

    public Iterator<Entry<String, ENG_OverlayElement>> getTemplateIterator() {
        return mTemplates.entrySet().iterator();
    }

    public boolean isTemplate(String name) {
        return mTemplates.get(name) != null;
    }

    public static ENG_OverlayManager getSingleton() {
//        if (MainActivity.isDebugmode() && mgr == null) {
//            throw new NullPointerException("OverlayManager not initialized");
//        }
//        return mgr;
        return MainApp.getGame().getRenderRoot().getOverlayManager();
    }

    @Override
    public long getPointer() {
        return overlayManagerWrapper.getPtr();
    }

    public ENG_OverlaySystemNativeWrapper getOverlaySystemWrapper() {
        return overlaySystemWrapper;
    }
}
