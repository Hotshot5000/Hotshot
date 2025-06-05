/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/19/20, 12:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.gamestatedebugger;

import com.google.gson.*;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by sebas on 20.12.2015.
 */
public class FrameDeserializer implements JsonDeserializer<Frame> {
    @Override
    public Frame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray frameIntervalList = jsonObject.getAsJsonArray("frameIntervalList");
        ArrayList<ENG_FrameInterval> frameIntervals = new ArrayList<>();
        for (int i = 0; i < frameIntervalList.size(); ++i) {
            JsonElement frameIntervalElem = frameIntervalList.get(i);
            FrameInterval frameInterval = context.deserialize(frameIntervalElem, FrameInterval.class);
            frameInterval.createLock();
            frameIntervals.add(frameInterval);
        }
        // Never fucking call it on the JsonElement sent as a param. It will result in an INFINITE LOOP!!!
//        Frame frame = context.deserialize(jsonObject, Frame.class);
//        frame._clearFrameIntervalList();
        Frame frame = (Frame) MainApp.getMainThread().getFrameFactory().createFrame(MainApp.getMainThread().getDebuggingState());
        frame._addFrameIntervalList(frameIntervals);
        frame.setFrameNum(context.<Long>deserialize(jsonObject.get("frameNum"), Long.class));
        frame.setTimeDelta(context.<Long>deserialize(jsonObject.get("timeDelta"), Long.class));
        return frame;
//        return frameIntervals;
    }
}
