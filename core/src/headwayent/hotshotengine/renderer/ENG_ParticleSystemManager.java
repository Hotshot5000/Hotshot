/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/1/17, 6:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class ENG_ParticleSystemManager {

//    private static ENG_ParticleSystemManager mgr;

    protected final ReentrantLock mutex = new ReentrantLock();
    protected final TreeMap<String, ENG_ParticleSystem> mSystemTemplates = new TreeMap<>();
    protected final TreeMap<String, ENG_ParticleEmitterFactory> mEmitterFactories = new TreeMap<>();
    protected final TreeMap<String, ENG_ParticleAffectorFactory> mAffectorFactories = new TreeMap<>();
    protected final TreeMap<String, ENG_ParticleSystemRendererFactory> mRendererFactories = new TreeMap<>();

    protected ArrayList<String> mScriptPatterns = new ArrayList<>();

    protected final ENG_ParticleSystemFactory mFactory = new ENG_ParticleSystemFactory();

    public ENG_ParticleSystemManager() {
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
        ENG_RenderRoot.getRenderRoot().addMovableObjectFactory(mFactory);
    }

    public void addEmitterFactory(ENG_ParticleEmitterFactory factory) {
        mutex.lock();
        try {
            mEmitterFactories.put(factory.getName(), factory);
        } finally {
            mutex.unlock();
        }
    }

    public void addAffectorFactory(ENG_ParticleAffectorFactory factory) {
        mutex.lock();
        try {
            mAffectorFactories.put(factory.getName(), factory);
        } finally {
            mutex.unlock();
        }
    }

    public void addRendererFactory(ENG_ParticleSystemRendererFactory factory) {
        mutex.lock();
        try {
            mRendererFactories.put(factory.getType(), factory);
        } finally {
            mutex.unlock();
        }
    }

    public void addTemplate(String name, ENG_ParticleSystem pSystem) {
        mutex.lock();
        try {
            if (mSystemTemplates.containsKey(name)) {
                throw new IllegalArgumentException(
                        "ParticleSystem template with name '" + name +
                                "' already exists.");
            }
            mSystemTemplates.put(name, pSystem);
        } finally {
            mutex.unlock();
        }
    }

    public void removeTemplate(String name) {
        removeTemplate(name, true);
    }

    public void removeTemplate(String name, boolean deleteTemplate) {
        mutex.lock();
        try {
            ENG_ParticleSystem system = mSystemTemplates.get(name);
            if (system == null) {
                throw new IllegalArgumentException(
                        "ParticleSystem template with name '" + name +
                                "' cannot be found.");
            }
            mSystemTemplates.remove(name);
        } finally {
            mutex.unlock();
        }
    }

    public void removeAllTemplates() {
        removeAllTemplates(true);
    }

    public void removeAllTemplates(boolean deleteTemplate) {
        mutex.lock();
        try {
            mSystemTemplates.clear();
        } finally {
            mutex.unlock();
        }
    }

    public ENG_ParticleSystem createTemplate(String name) {
        mutex.lock();
        try {
            if (mSystemTemplates.containsKey(name)) {
                throw new IllegalArgumentException(
                        "ParticleSystem template with name '" + name +
                                "' already exists.");
            }

            ENG_ParticleSystem tpl = new ENG_ParticleSystem(name);
            addTemplate(name, tpl);
            return tpl;
        } finally {
            mutex.unlock();
        }
    }

    public ENG_ParticleSystem getTemplate(String name) {
        mutex.lock();
        try {
            return mSystemTemplates.get(name);
        } finally {
            mutex.unlock();
        }
    }

    protected ENG_ParticleSystem createSystemImpl(String name, int quota) {
        ENG_ParticleSystem sys = new ENG_ParticleSystem(name);
        sys.setParticleQuota(quota);
        return sys;
    }

    protected ENG_ParticleSystem createSystemImpl(String name, String templateName) {
        ENG_ParticleSystem pTemplate = getTemplate(templateName);
        if (pTemplate == null) {
            throw new IllegalArgumentException(
                    "Cannot find required template '" + templateName + "'");
        }
        ENG_ParticleSystem sys = createSystemImpl(name, pTemplate.getParticleQuota());
        sys.set(pTemplate);
        return sys;
    }

    public ENG_ParticleEmitter _createEmitter(String emitterType,
                                              ENG_ParticleSystem psys) {
        mutex.lock();
        try {
            ENG_ParticleEmitterFactory emitterFactory =
                    mEmitterFactories.get(emitterType);
            if (emitterFactory == null) {
                throw new IllegalArgumentException(
                        "Cannot find requested emitter type.");
            }
            return emitterFactory.createEmitter(psys);
        } finally {
            mutex.unlock();
        }
    }

    public ENG_ParticleAffector _createAffector(String affectorType,
                                                ENG_ParticleSystem psys) {
        mutex.lock();
        try {
            ENG_ParticleAffectorFactory affectorFactory =
                    mAffectorFactories.get(affectorType);
            if (affectorFactory == null) {
                throw new IllegalArgumentException(
                        "Cannot find requested affector type.");
            }
            return affectorFactory.createAffector(psys);
        } finally {
            mutex.unlock();
        }
    }

    public ENG_ParticleSystemRenderer _createRenderer(String rendererType) {
        mutex.lock();
        try {
            ENG_ParticleSystemRendererFactory systemFactory =
                    mRendererFactories.get(rendererType);
            if (systemFactory == null) {
                throw new IllegalArgumentException(
                        "Cannot find requested affector type.");
            }
            return systemFactory.createInstance(rendererType);
        } finally {
            mutex.unlock();
        }
    }

    /** @noinspection deprecation*/
    public void _initialise() {
        mutex.lock();
        try {
            addRendererFactory(new ENG_BillboardParticleRendererFactory());
        } finally {
            mutex.unlock();
        }
    }

    public Iterator<Entry<String, ENG_ParticleAffectorFactory>>
    getAffectorFactoryIterator() {
        return mAffectorFactories.entrySet().iterator();
    }

    public Iterator<Entry<String, ENG_ParticleEmitterFactory>>
    getEmitterFactoryIterator() {
        return mEmitterFactories.entrySet().iterator();
    }

    public Iterator<Entry<String, ENG_ParticleSystemRendererFactory>>
    getRendererFactoryIterator() {
        return mRendererFactories.entrySet().iterator();
    }

    public Iterator<Entry<String, ENG_ParticleSystem>> getTemplateIterator() {
        return mSystemTemplates.entrySet().iterator();
    }

    public ENG_ParticleSystemFactory _getFactory() {
        return mFactory;
    }

    public static ENG_ParticleSystemManager getSingleton() {
//        if (mgr == null && MainApp.DEV) {
//            throw new NullPointerException("ParticleSystemManager not initialized");
//        }
//        return mgr;
        return MainApp.getGame().getRenderRoot().getParticleManager();
    }
}
