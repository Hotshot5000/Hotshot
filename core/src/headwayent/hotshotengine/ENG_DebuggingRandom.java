/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;
import headwayent.hotshotengine.statedebugger.ENG_State;

import java.util.ArrayList;

/**
 * Created by sebas on 05.10.2015.
 */
public class ENG_DebuggingRandom extends ENG_Random implements ENG_State.OnFrameIntervalChangeListener {

    private final ENG_Random random;

    public ENG_DebuggingRandom(ENG_Random random) {
        this.random = random;
    }

    @Override
    public ENG_Random getRandom() {
        return random;
    }

    @Override
    public boolean nextBoolean(String s) {
        if (s != null && MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            return (boolean) currentFrameInterval.getObject(s);
        }
        boolean ret = random.nextBoolean();
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            if (s != null) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.addObject(s, ret);
            }
        }
        return ret;
    }

    @Override
    public void nextBytes(String s, byte[] bytes) {
        if (s != null && MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            ArrayList<Double> object = (ArrayList<Double>) currentFrameInterval.getObject(s);
            if (bytes.length != object.size()) {
                throw new IllegalArgumentException(bytes.length + " array length provided different from the read " + object.size());
            }
            for (int i = 0; i < bytes.length; ++i) {
                bytes[i] = object.get(i).byteValue();
            }
        } else {
            random.nextBytes(bytes);
        }
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            if (s != null) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.addObject(s, bytes);
            }
        }
    }

    @Override
    public double nextDouble(String s) {
        if (s != null && MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            return (double) currentFrameInterval.getObject(s);
        }
        double ret = random.nextDouble();
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            if (s != null) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.addObject(s, ret);
            }
        }
        return ret;
    }

    @Override
    public float nextFloat(String s) {
        if (s != null && MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            return ((Double) currentFrameInterval.getObject(s)).floatValue();
        }
        float ret = random.nextFloat();
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            if (s != null) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.addObject(s, ret);
            }
        }
        return ret;
    }

    @Override
    public double nextGaussian(String s) {
        if (s != null && MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            return (double) currentFrameInterval.getObject(s);
        }
        double ret = random.nextGaussian();
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            if (s != null) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.addObject(s, ret);
            }
        }
        return ret;
    }

    @Override
    public int nextInt(String s) {
        if (s != null && MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            return ((Double) currentFrameInterval.getObject(s)).intValue();
        }
        int ret = random.nextInt();
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            if (s != null) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.addObject(s, ret);
            }
        }
        return ret;
    }

    @Override
    public int nextInt(String s, int bound) {
        if (s != null && MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            return ((Double) currentFrameInterval.getObject(s)).intValue();
        }
        int ret = random.nextInt(bound);
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            if (s != null) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.addObject(s, ret);
            }
        }
        return ret;
    }

    @Override
    public long nextLong(String s) {
        if (s != null && MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            return ((Double) currentFrameInterval.getObject(s)).longValue();
        }
        long ret = random.nextLong();
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            if (s != null) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.addObject(s, ret);
            }
        }
        return ret;
    }

    @Override
    public void onFrameIntervalChanged(ENG_FrameInterval frameInterval) {
    }
}
