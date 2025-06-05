/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import com.badlogic.gdx.files.FileHandle;

import org.apache.http.NameValuePair;

import java.util.List;

public class ENG_CrashData {

    private final String stacktrace;
    private final FileHandle stacktraceOutputFile;
    private final FileHandle zippedTraces;
    private final List<NameValuePair> additionalData;

    public ENG_CrashData(String stacktrace, FileHandle stacktraceOutputFile, FileHandle zippedTraces, List<NameValuePair> additionalData) {
        this.stacktrace = stacktrace;
        this.stacktraceOutputFile = stacktraceOutputFile;
        this.zippedTraces = zippedTraces;
        this.additionalData = additionalData;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public FileHandle getStacktraceOutputFile() {
        return stacktraceOutputFile;
    }

    public FileHandle getZippedTraces() {
        return zippedTraces;
    }

    public List<NameValuePair> getAdditionalData() {
        return additionalData;
    }
}
