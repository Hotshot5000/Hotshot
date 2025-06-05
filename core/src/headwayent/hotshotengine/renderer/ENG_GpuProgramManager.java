/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.renderer.opengles.GLRenderSystem;

import java.util.TreeMap;
import java.util.TreeSet;

public abstract class ENG_GpuProgramManager {

//    protected static ENG_GpuProgramManager pm;// = new ENG_GpuProgramManager();
    protected final TreeMap<String, ENG_GpuSharedParameters> mSharedParametersMap = new TreeMap<>();

    protected abstract ENG_GpuProgram createImpl();

    public ENG_GpuProgramManager() {
//        if (pm == null) {
//            pm = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        pm = this;
    }

    public TreeSet<String> getSupportedSyntax() {
        return ENG_RenderRoot.getRenderRoot().getRenderSystem().getCapabilities().getSupportedShaderProfile();
    }

    public boolean isSyntaxSupported(String syntax) {
        return ENG_RenderRoot.getRenderRoot().getRenderSystem().getCapabilities().isShaderProfileSupported(syntax);
    }

    public ENG_GpuProgramParameters createParameters() {
        return new ENG_GpuProgramParameters();
    }

    public ENG_GpuSharedParameters createSharedParameters(String name) {
        if (mSharedParametersMap.containsKey(name)) {
            throw new IllegalArgumentException("The shared parameter set '" + name + "' already exists!");
        }
        ENG_GpuSharedParameters param = new ENG_GpuSharedParameters(name);
        mSharedParametersMap.put(name, param);
        return param;
    }

    public ENG_GpuSharedParameters getSharedParameters(String name) {
        ENG_GpuSharedParameters param = mSharedParametersMap.get(name);
        if (param == null) {
            throw new IllegalArgumentException("No shared parameter set with name '" + name + "'!");
        }
        return param;
    }

    public TreeMap<String, ENG_GpuSharedParameters> getAvailableSharedParameters() {
        return mSharedParametersMap;
    }

    public static ENG_GpuProgramManager getSingleton() {
//        if (MainActivity.isDebugmode() && (pm == null)) {
//            throw new NullPointerException();
//        }
//        return pm;
        return ((GLRenderSystem) MainApp.getGame().getRenderRoot().getActiveRenderSystem()).getGpuProgramManager();
    }

    public ENG_GpuProgram getByName(String name) {
        
        // Since we have no concept of low level gpu programs it means that we will
        // always go for the high level
        return ENG_HighLevelGpuProgramManager.getSingleton().getByName(name);
    }


}
