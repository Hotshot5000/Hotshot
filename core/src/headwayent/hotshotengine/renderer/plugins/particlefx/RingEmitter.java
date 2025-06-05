/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.plugins.particlefx;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_ParamCommand;
import headwayent.hotshotengine.ENG_ParamDictionary;
import headwayent.hotshotengine.ENG_ParameterDef;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.ENG_ParameterDef.ParameterType;
import headwayent.hotshotengine.renderer.ENG_Particle;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;

public class RingEmitter extends AreaEmitter {

    public static class CmdInnerX implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((RingEmitter) target).getInnerSizeX());
        }

        @Override
        public void doSet(Object target, String val) {

            ((RingEmitter) target).setInnerSizeX(Float.parseFloat(val));
        }

    }

    public static class CmdInnerY implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((RingEmitter) target).getInnerSizeY());
        }

        @Override
        public void doSet(Object target, String val) {

            ((RingEmitter) target).setInnerSizeY(Float.parseFloat(val));
        }

    }

    // See ParticleEmitter
    protected static final CmdInnerX msCmdInnerX = new CmdInnerX();
    protected static final CmdInnerY msCmdInnerY = new CmdInnerY();

    /// Size of 'clear' center area (> 0 and < 1.0)
    protected float mInnerSizex;
    protected float mInnerSizey;

    private final ENG_Vector4D temp = new ENG_Vector4D();
    private final ENG_Vector4D temp2 = new ENG_Vector4D();

    private final ENG_Radian alpha = new ENG_Radian();

    public RingEmitter(ENG_ParticleSystem p) {
        super(p);
        

        if (initDefaults("Ring")) {
            // Add custom parameters
            ENG_ParamDictionary pDict = getStringInterface().getParamDictionary();

            pDict.addParameter(new ENG_ParameterDef("inner_width",
                    "Parametric value describing the proportion of the " +
                            "shape which is hollow.", ParameterType.PT_REAL), msCmdInnerX);
            pDict.addParameter(new ENG_ParameterDef("inner_height",
                    "Parametric value describing the proportion of the " +
                            "shape which is hollow.", ParameterType.PT_REAL), msCmdInnerY);
        }
        // default is half empty
        setInnerSize(0.5f, 0.5f);
    }

    @Override
    public void _initParticle(ENG_Particle pParticle) {

        super._initParticle(pParticle);


        float a, b, x, y, z;

        // create two random angles alpha and beta
        // with these two angles, we are able to select any point on an
        // ellipsoid's surface
        alpha.set(ENG_Utility.rangeRandom(0, ENG_Math.TWO_PI));


        // create three random radius values that are bigger than the inner
        // size, but smaller/equal than/to the outer size 1.0 (inner size is
        // between 0 and 1)
        a = ENG_Utility.rangeRandom(mInnerSizex, 1.0f);
        b = ENG_Utility.rangeRandom(mInnerSizey, 1.0f);


        // with a,b,c we have defined a random ellipsoid between the inner
        // ellipsoid and the outer sphere (radius 1.0)
        // with alpha and beta we select on point on this random ellipsoid
        // and calculate the 3D coordinates of this point

        x = a * ENG_Math.sin(alpha.valueRadians());
        y = b * ENG_Math.cos(alpha.valueRadians());
        z = ENG_Utility.symmetricRandom();

        // scale the found point to the ellipsoid's size and move it
        // relatively to the center of the emitter point

        temp.set(ENG_Math.PT4_ZERO);
        temp2.set(ENG_Math.PT4_ZERO);

        mXRange.mul(x, temp);
        mPosition.add(temp, temp2);
        mYRange.mul(y, temp);
        temp2.addInPlace(temp);
        mZRange.mul(z, temp);
        temp2.addInPlace(temp);
        pParticle.position.set(temp);

        // Generate complex data by reference
        genEmissionColour(pParticle.colour);
        genEmissionDirection(pParticle.direction);
        genEmissionVelocity(pParticle.direction);

        // Generate simpler data
        pParticle.timeToLive = pParticle.totalTimeToLive = genEmissionTTL();
    }

    public float getInnerSizeX() {
        return mInnerSizex;
    }

    public float getInnerSizeY() {
        return mInnerSizey;
    }

    public void setInnerSizeX(float f) {
        if (f <= 0.0f || f >= 1.0f) {
            throw new IllegalArgumentException("Must be between 0 and 1");
        }
        mInnerSizex = f;
    }

    public void setInnerSizeY(float f) {
        if (f <= 0.0f || f >= 1.0f) {
            throw new IllegalArgumentException("Must be between 0 and 1");
        }
        mInnerSizey = f;
    }

    public void setInnerSize(float x, float y) {
        setInnerSizeX(x);
        setInnerSizeY(y);
    }

}
