/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import java.util.concurrent.locks.ReentrantLock;

//import org.openintents.sensorsimulator.hardware.Sensor;
//import org.openintents.sensorsimulator.hardware.SensorEvent;
//import org.openintents.sensorsimulator.hardware.SensorEventListener;
//import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;
/*
 * import org.openintents.sensorsimulator.hardware.Sensor; import
 * org.openintents.sensorsimulator.hardware.SensorEvent; import
 * org.openintents.sensorsimulator.hardware.SensorEventListener; import
 * org.openintents.sensorsimulator.hardware.SensorManagerSimulator;
 */
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;

/**
 * Android Orientation Sensor Manager Archetype
 *
 * @author antoine vianey under GPL v3 :
 *         http://www.gnu.org/licenses/gpl-3.0.html
 */
public class ENG_AccelerometerInput implements /*SensorEventListener,*/ ENG_IInput {

//    SensorManagerSimulator m_sensorManager;
    //    SensorManager m_sensorManager;
    float[] m_lastMagFields;
    float[] m_lastAccels;
    private final float[] m_tempRotationMatrix = new float[16];
    private final float[] m_rotationMatrix = new float[16];
    private final float[] m_remappedR = new float[16];
    private final float[] m_orientation = new float[4];
    private final String name;

    public static class Rotation {

        public float m_lastPitch = 0.f;
        public float m_lastYaw = 0.f;
        public float m_lastRoll = 0.f;

        public void set(float pitch, float yaw, float roll) {
            m_lastPitch = pitch;
            m_lastYaw = yaw;
            m_lastRoll = roll;
        }

        public void set(Rotation oth) {
            m_lastPitch = oth.m_lastPitch;
            m_lastYaw = oth.m_lastYaw;
            m_lastRoll = oth.m_lastRoll;
        }

        public void reset() {
            m_lastPitch = 0.f;
            m_lastYaw = 0.f;
            m_lastRoll = 0.f;
        }
    }

    private final Rotation rot = new Rotation();
    private final Rotation retrot = new Rotation();
    private final ReentrantLock lock = new ReentrantLock();

    /*
     * fix random noise by averaging tilt values
     */
    final static int AVERAGE_BUFFER = 30;
    float[] m_prevPitch = new float[AVERAGE_BUFFER];
    /*
     * current index int m_prevEasts
     */
    int m_pitchIndex = 0;
    float[] m_prevRoll = new float[AVERAGE_BUFFER];
    /*
     * current index into m_prevTilts
     */
    int m_rollIndex = 0;

    /*
     * center of the rotation
     */
    private final float m_tiltCentreX = 0.f;
    private final float m_tiltCentreY = 0.f;
    private final float m_tiltCentreZ = 0.f;
//        private Display display;

    public ENG_AccelerometerInput(String name) {
        //        setup(sensorManager, display);

        this.name = name;

    }

//    public ENG_AccelerometerInput(String name, final SensorManagerSimulator sensorManager/*, Display display*/) {
//        setup(sensorManager/*, display*/);
//
//        this.name = name;
//
//    }
//
//    public void setup(final SensorManagerSimulator sensorManager/*, Display display*/) {
//        m_sensorManager = sensorManager;
////                this.display = display;
//        //NetworkOnMainThreadException !!!
//        //        	sensorManager.connectSimulator();
//        //NO NEED FOR A REGISTER HERE!!!!!! NEVER EVER!!!
//
//         /*       final CountDownLatch cl = new CountDownLatch(1);
//                new Thread() {
//
//                        public void run() {
//                                sensorManager.connectSimulator(); //
//                        //        registerListenersThread();
//                                cl.countDown();
//                        }
//                }.start();
//                try {
//                        cl.await();
//                } catch (InterruptedException e) {
//
//                        e.printStackTrace();
//                }*/
//
//    }

    public void registerListeners() {
//                boolean mag = m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
//                boolean accel = m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void registerListenersThread() {
        /*        final CountDownLatch cl = new CountDownLatch(1);
                new Thread() {

                        @Override
                        public void run() {

                                boolean mag = m_sensorManager.registerListener(ENG_AccelerometerInput.this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
                                boolean accel = m_sensorManager.registerListener(ENG_AccelerometerInput.this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                                cl.countDown();
                        }
                }.start();
                try {
                        cl.await();
                } catch (InterruptedException e) {

                        e.printStackTrace();
                }*/
    }

    public void unregisterListenersThread() {
        /*        final CountDownLatch cl = new CountDownLatch(1);
                new Thread() {

                        @Override
                        public void run() {

                                m_sensorManager.unregisterListener(ENG_AccelerometerInput.this);
                                cl.countDown();
                        }
                }.start();
                try {
                        cl.await();
                } catch (InterruptedException e) {

                        e.printStackTrace();
                }*/
    }

    public void unregisterListeners() {
//                m_sensorManager.unregisterListener(this);
    }

    /*@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        if (event.type == Sensor.TYPE_ACCELEROMETER) {
            accel(event);
        }
        //        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
        if (event.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mag(event);
        }
    }

    private void accel(SensorEvent event) {
        if (m_lastAccels == null) {
            m_lastAccels = new float[3];
        }

        System.arraycopy(event.values, 0, m_lastAccels, 0, 3);

                *//*
                 * if (m_lastMagFields != null) { computeOrientation(); }
                 *//*
    }

    private void mag(SensorEvent event) {
        if (m_lastMagFields == null) {
            m_lastMagFields = new float[3];
        }

        System.arraycopy(event.values, 0, m_lastMagFields, 0, 3);

        if (m_lastAccels != null) {
//                        computeOrientation();
        }
    }*/

    Filter[] m_filters = {new Filter(), new Filter(), new Filter()};

    public Object getData() {
        lock.lock();
        try {
            retrot.m_lastPitch = rot.m_lastPitch;
            retrot.m_lastYaw = rot.m_lastYaw;
            retrot.m_lastRoll = rot.m_lastRoll;
        } finally {
            lock.unlock();
        }
        return retrot;
    }

    @Override
    public void reset() {

        // For now. If we use a filter we might need to reset the elements
        // there.
        rot.reset();
    }

    private static class Filter {

        static final int AVERAGE_BUFFER = 10;
        final float[] m_arr = new float[AVERAGE_BUFFER];
        int m_idx = 0;

        public float append(float val) {
            m_arr[m_idx] = val;
            m_idx++;
            if (m_idx == AVERAGE_BUFFER) {
                m_idx = 0;
            }
            return avg();
        }

        public float avg() {
            float sum = 0;
            for (float x : m_arr) {
                sum += x;
            }
            return sum / AVERAGE_BUFFER;
        }
    }

//        private void computeOrientation() {
//        /*        if (SensorManager.getRotationMatrix(m_tempRotationMatrix, null,
//                        m_lastAccels, m_lastMagFields)) {
//                        int rotation = display.getRotation();
//                        int xAxis = -1, yAxis = -1;
//                        switch (rotation) {
//                                case Surface.ROTATION_0:
//                                        xAxis = SensorManager.AXIS_X;
//                                        yAxis = SensorManager.AXIS_Y;
//                                        break;
//                                case Surface.ROTATION_90:
//                                        xAxis = SensorManager.AXIS_Y;
//                                        yAxis = SensorManager.AXIS_MINUS_X;
//                                        break;
//                                case Surface.ROTATION_180:
//                                        xAxis = SensorManager.AXIS_MINUS_X;
//                                        yAxis = SensorManager.AXIS_MINUS_Y;
//                                        break;
//                                case Surface.ROTATION_270:
//                                        xAxis = SensorManager.AXIS_MINUS_Y;
//                                        yAxis = SensorManager.AXIS_X;
//                                        break;
//                                default:
//                                        throw new IllegalArgumentException("invalid rotation: " + rotation);
//                        }
//                        SensorManager.remapCoordinateSystem(m_tempRotationMatrix,
//                                xAxis, yAxis, m_rotationMatrix);
//                        SensorManager.getOrientation(m_rotationMatrix, m_orientation);
//
//                        /*
//                         * 1 radian = 57.2957795 degrees
//                         */
//                        /*
//                         * [0] : yaw, rotation around z axis [1] : pitch,
//                         * rotation around x axis [2] : roll, rotation around y
//                         * axis
//                         */
//                        float yaw = m_orientation[0] * 57.2957795f;
//                        float pitch = m_orientation[1] * 57.2957795f;
//                        float roll = m_orientation[2] * 57.2957795f;
//
//                        //         synchronized (this) {
//                        lock.lock();
//                        try {
//                                rot.m_lastYaw = yaw;//m_filters[0].append(yaw);
//                                rot.m_lastPitch = pitch;//m_filters[1].append(pitch);
//                                rot.m_lastRoll = roll;//m_filters[2].append(roll);
//                        } finally {
//                                lock.unlock();
//                        }
//                        //         }
//
//                        /*
//                         * TextView rt = (TextView) findViewById(R.id.roll);
//                         * TextView pt = (TextView) findViewById(R.id.pitch);
//                         * TextView yt = (TextView) findViewById(R.id.yaw);
//                         * yt.setText("azi z: " + m_lastYaw); pt.setText("pitch
//                         * x: " + m_lastPitch); rt.setText("roll y: " +
//                         * m_lastRoll);
//                         */
//                }
//        }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the m_lastPitch
     */
//        public synchronized float getLastPitch() {
//                return m_lastPitch;
//        }
    /**
     * @return the m_lastYaw
     */
//        public synchronized float getLastYaw() {
//                return m_lastYaw;
//        }
    /**
     * @return the m_lastRoll
     */
//        public synchronized float getLastRoll() {
//                return m_lastRoll;
//        }
}
