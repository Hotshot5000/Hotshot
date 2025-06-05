/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;
import java.util.Map.Entry;

public class ENG_GpuSharedParametersUsage {

    protected ENG_GpuSharedParameters mSharedParams;// = new ENG_GpuSharedParameters();
    protected ENG_GpuProgramParameters mParams;

    static class CopyDataEntry {
        public ENG_GpuConstantDefinition srcDefinition;
        public ENG_GpuConstantDefinition dstDefinition;
    }

    protected final ArrayList<CopyDataEntry> mCopyDataList = new ArrayList<>();
    protected long mCopyDataVersion;


    protected void initCopyData() {
        mCopyDataList.clear();

        for (Entry<String, ENG_GpuConstantDefinition> entry : mSharedParams.getConstantDefinitions().map.entrySet()) {
            String name = entry.getKey();
            ENG_GpuConstantDefinition shareddef =
                    entry.getValue();
            ENG_GpuConstantDefinition instdef =
                    mParams._findNamedConstantDefinition(name, false);

            if (instdef != null) {
                if ((instdef.constType == shareddef.constType) &&
                        (instdef.arraySize == shareddef.arraySize)) {
                    CopyDataEntry e = new CopyDataEntry();
                    e.srcDefinition = shareddef;
                    e.dstDefinition = instdef;
                    mCopyDataList.add(e);
                }
            }
        }

        mCopyDataVersion = mSharedParams.getVersion();
    }

    public ENG_GpuSharedParametersUsage() {

    }

    public ENG_GpuSharedParametersUsage(ENG_GpuSharedParameters sharedParams,
                                        ENG_GpuProgramParameters params) {
        mSharedParams = sharedParams;
        mParams = params;
        initCopyData();
    }

    public void set(ENG_GpuSharedParameters sharedParams,
                    ENG_GpuProgramParameters params) {
        mSharedParams = sharedParams;
        mParams = params;
    }

    public void _copySharedParamsToTargetParams() {
        if (mCopyDataVersion != mSharedParams.getVersion()) {
            initCopyData();
        }

        int len = mCopyDataList.size();
        for (int i = 0; i < len; ++i) {
            CopyDataEntry e = mCopyDataList.get(i);
        }
    }

    public ENG_GpuSharedParameters getSharedParams() {
        return mSharedParams;
    }

    public ENG_GpuProgramParameters getTargetParams() {
        return mParams;
    }

    public String getName() {
        return mSharedParams.getName();
    }
}
