/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

//import com.badlogic.gdx.physics.bullet.linearmath.btMatrix3x3;

import headwayent.hotshotengine.ENG_Plane.Side;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Float;

import java.util.ArrayList;

public class ENG_Math {

    public static final float PI = (float) Math.PI;
    public static final float TWO_PI = 2.0f * PI;
    public static final float FOUR_PI = 4.0f * PI;
    public static final float HALF_PI = 0.5f * PI;
    public static final float QUARTER_PI = 0.25f * PI;
    public static final float DEGREES_TO_RADIANS = 0.0174532925f;
    public static final float RADIANS_TO_DEGREES = 57.295779513f;
    public static final float FLOAT_EPSILON = 0.00001f;
    public static final float FLOAT_EPSILON_SQUARED = FLOAT_EPSILON * FLOAT_EPSILON;
    public static final double DOUBLE_EPSILON = 0.00001;
    public static final double DOUBLE_EPSILON_SQUARED = DOUBLE_EPSILON * DOUBLE_EPSILON;
    public static final float MAX_DIST = 3.40282e+38f;

    public static final ENG_Radian PI_RAD = new ENG_Radian(PI);
    public static final ENG_Radian TWO_PI_RAD = new ENG_Radian(TWO_PI);
    public static final ENG_Radian FOUR_PI_RAD = new ENG_Radian(FOUR_PI);
    public static final ENG_Radian HALF_PI_RAD = new ENG_Radian(HALF_PI);
    public static final ENG_Radian QUARTER_PI_RAD = new ENG_Radian(QUARTER_PI);

    public static final ENG_Degree PI_DEG = new ENG_Degree(PI_RAD);
    public static final ENG_Degree TWO_PI_DEG = new ENG_Degree(TWO_PI_RAD);
    public static final ENG_Degree FOUR_PI_DEG = new ENG_Degree(FOUR_PI_RAD);
    public static final ENG_Degree HALF_PI_DEG = new ENG_Degree(HALF_PI_RAD);
    public static final ENG_Degree QUARTER_PI_DEG = new ENG_Degree(QUARTER_PI_RAD);

    public static final ENG_Vector2D VEC2_ZERO = new ENG_Vector2D();
    public static final ENG_Vector2D VEC2_X_UNIT = new ENG_Vector2D(1.0f, 0.0f);
    public static final ENG_Vector2D VEC2_Y_UNIT = new ENG_Vector2D(0.0f, 1.0f);
    public static final ENG_Vector2D VEC2_NEGATIVE_X_UNIT = new ENG_Vector2D(-1.0f, 0.0f);
    public static final ENG_Vector2D VEC2_NEGATIVE_Y_UNIT = new ENG_Vector2D(0.0f, -1.0f);
    public static final ENG_Vector2D VEC2_SCALE = new ENG_Vector2D(1.0f, 1.0f);

    public static final ENG_Vector3D VEC3_ZERO = new ENG_Vector3D();
    public static final ENG_Vector3D VEC3_X_UNIT = new ENG_Vector3D(1.0f, 0.0f, 0.0f);
    public static final ENG_Vector3D VEC3_Y_UNIT = new ENG_Vector3D(0.0f, 1.0f, 0.0f);
    public static final ENG_Vector3D VEC3_Z_UNIT = new ENG_Vector3D(0.0f, 0.0f, 1.0f);
    public static final ENG_Vector3D VEC3_NEGATIVE_X_UNIT = new ENG_Vector3D(-1.0f, 0.0f, 0.0f);
    public static final ENG_Vector3D VEC3_NEGATIVE_Y_UNIT = new ENG_Vector3D(0.0f, -1.0f, 0.0f);
    public static final ENG_Vector3D VEC3_NEGATIVE_Z_UNIT = new ENG_Vector3D(0.0f, 0.0f, -1.0f);
    public static final ENG_Vector3D VEC3_SCALE = new ENG_Vector3D(1.0f, 1.0f, 1.0f);

    public static final ENG_Vector4D VEC4_ZERO = new ENG_Vector4D();
    public static final ENG_Vector4D PT4_ZERO = new ENG_Vector4D(true);
    public static final ENG_Vector4D VEC4_X_UNIT = new ENG_Vector4D(1.0f, 0.0f, 0.0f, 0.0f);
    public static final ENG_Vector4D VEC4_Y_UNIT = new ENG_Vector4D(0.0f, 1.0f, 0.0f, 0.0f);
    public static final ENG_Vector4D VEC4_Z_UNIT = new ENG_Vector4D(0.0f, 0.0f, 1.0f, 0.0f);
    public static final ENG_Vector4D VEC4_NEGATIVE_X_UNIT = new ENG_Vector4D(-1.0f, 0.0f, 0.0f, 0.0f);
    public static final ENG_Vector4D VEC4_NEGATIVE_Y_UNIT = new ENG_Vector4D(0.0f, -1.0f, 0.0f, 0.0f);
    public static final ENG_Vector4D VEC4_NEGATIVE_Z_UNIT = new ENG_Vector4D(0.0f, 0.0f, -1.0f, 0.0f);
    public static final ENG_Vector4D PT4_X_UNIT = new ENG_Vector4D(1.0f, 0.0f, 0.0f, 1.0f);
    public static final ENG_Vector4D PT4_Y_UNIT = new ENG_Vector4D(0.0f, 1.0f, 0.0f, 1.0f);
    public static final ENG_Vector4D PT4_Z_UNIT = new ENG_Vector4D(0.0f, 0.0f, 1.0f, 1.0f);
    public static final ENG_Vector4D PT4_UNIT = new ENG_Vector4D(1.0f, 1.0f, 1.0f, 1.0f);
    public static final ENG_Vector4D PT4_NEGATIVE_X_UNIT = new ENG_Vector4D(-1.0f, 0.0f, 0.0f, 1.0f);
    public static final ENG_Vector4D PT4_NEGATIVE_Y_UNIT = new ENG_Vector4D(0.0f, -1.0f, 0.0f, 1.0f);
    public static final ENG_Vector4D PT4_NEGATIVE_Z_UNIT = new ENG_Vector4D(0.0f, 0.0f, -1.0f, 1.0f);
    public static final ENG_Vector4D PT4_NEGATIVE_UNIT = new ENG_Vector4D(-1.0f, -1.0f, -1.0f, 1.0f);
    public static final ENG_Vector4D VEC4_SCALE = new ENG_Vector4D(1.0f, 1.0f, 1.0f, 0.0f);

    public static final ENG_Matrix3 MAT3_ZERO = new ENG_Matrix3(0.0f);
    public static final ENG_Matrix3 MAT3_IDENTITY = new ENG_Matrix3();

    public static final ENG_Matrix4 MAT4_ZERO = new ENG_Matrix4(0.0f);
    public static final ENG_Matrix4 MAT4_IDENTITY = new ENG_Matrix4();
    public static final ENG_Matrix4 MAT4_CLIPSPACE2DTOIMAGESPACE = new ENG_Matrix4(
            0.5f, 0.0f, 0.0f, 0.5f,
            0.0f, -0.5f, 0.0f, 0.5f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);

    public static final ENG_Quaternion QUAT_ZERO = new ENG_Quaternion();
    public static final ENG_Quaternion QUAT_IDENTITY = new ENG_Quaternion(true);

    public static final int DBL_DIG = 15;                      /* # of decimal digits of precision */
    public static final double DBL_EPSILON = 2.2204460492503131e-016; /* smallest such that 1.0+DBL_EPSILON != 1.0 */
    public static final int DBL_MANT_DIG = 53;                      /* # of bits in mantissa */
    public static final double DBL_MAX = 1.7976931348623158e+308; /* max value */
    public static final int DBL_MAX_10_EXP = 308;                     /* max decimal exponent */
    public static final int DBL_MAX_EXP = 1024;                    /* max binary exponent */
    public static final double DBL_MIN = 2.2250738585072014e-308; /* min positive value */
    public static final int DBL_MIN_10_EXP = (-307);                  /* min decimal exponent */
    public static final int DBL_MIN_EXP = (-1021);                 /* min binary exponent */
    public static final int _DBL_RADIX = 2;                       /* exponent radix */
    public static final int _DBL_ROUNDS = 1;                       /* addition rounding: near */

    public static final int FLT_DIG = 6;                       /* # of decimal digits of precision */
    public static final float FLT_EPSILON = 1.192092896e-07F;        /* smallest such that 1.0+FLT_EPSILON != 1.0 */
    public static final int FLT_GUARD = 0;
    public static final int FLT_MANT_DIG = 24;                      /* # of bits in mantissa */
    public static final float FLT_MAX = 3.402823466e+38F;        /* max value */
    public static final int FLT_MAX_10_EXP = 38;                      /* max decimal exponent */
    public static final int FLT_MAX_EXP = 128;                     /* max binary exponent */
    public static final float FLT_MIN = 1.175494351e-38F;        /* min positive value */
    public static final int FLT_MIN_10_EXP = (-37);                   /* min decimal exponent */
    public static final int FLT_MIN_EXP = (-125);                  /* min binary exponent */
    public static final int FLT_NORMALIZE = 0;
    public static final int FLT_RADIX = 2;                       /* exponent radix */
    public static final int FLT_ROUNDS = 1;                       /* addition rounding: near */

    //Used in makeViewMatrix()
    private static final ENG_Matrix4 temp = new ENG_Matrix4();
    private static final ENG_Matrix4 temp2 = new ENG_Matrix4();
    private static final ENG_Vector4D tempVec = new ENG_Vector4D(true);

    /**
     * For giws
     *
     * @return
     */
    public static ENG_Matrix4 getIdentityMatrix() {
        return MAT4_IDENTITY;
    }

    public static ENG_Matrix4 getZeroMatrix() {
        return MAT4_ZERO;
    }

    public static float acos(float f) {
        return (float) Math.acos(f);
    }

    public static float asin(float f) {
        return (float) Math.asin(f);
    }

    public static float asin(double f) {
        return (float) Math.asin(f);
    }

    public static float atan(float f) {
        return (float) Math.atan(f);
    }

    public static float atan2(float y, float x) {
        return (float) Math.atan2(y, x);
    }

    public static float atan2(double y, double x) {
        return (float) Math.atan2(y, x);
    }

    public static float cbrt(float f) {
        return (float) Math.cbrt(f);
    }

    public static float ceil(float f) {
        return (float) Math.ceil(f);
    }

    public static float cos(float f) {
        return (float) Math.cos(f);
    }

    public static float cosh(float f) {
        return (float) Math.cosh(f);
    }

    public static float exp(float f) {
        return (float) Math.exp(f);
    }

    public static float expm1(float f) {
        return (float) Math.expm1(f);
    }

    public static float floor(float f) {
        return (float) Math.floor(f);
    }

    public static float hypot(float x, float y) {
        return (float) Math.hypot(x, y);
    }

    public static float log(float f) {
        return (float) Math.log(f);
    }

    public static float log10(float f) {
        return (float) Math.log10(f);
    }

    public static float log1p(float f) {
        return (float) Math.log1p(f);
    }

    public static float pow(float x, float y) {
        return (float) Math.pow(x, y);
    }

    public static float random() {
        return (float) Math.random();
    }

    public static float rint(float f) {
        return (float) Math.rint(f);
    }

    public static float sin(float f) {
        return (float) Math.sin(f);
    }

    public static float sinh(float f) {
        return (float) Math.sinh(f);
    }

    public static float sqrt(float f) {
        return (float) Math.sqrt(f);
    }

    public static float rsqrt(float f) {
        return (1.0f / (float) Math.sqrt(f));
    }

    public static float sqr(float f) {
        return (f * f);
    }

    public static float tan(float f) {
        return (float) Math.tan(f);
    }

    public static float tanh(float f) {
        return (float) Math.tanh(f);
    }

    public static float toDegrees(float angrad) {
        return (float) Math.toDegrees(angrad);
    }

    public static float toRadians(float angdeg) {
        return (float) Math.toRadians(angdeg);
    }

    public static byte clamp(byte val, byte min, byte max) {
        if (min > max) {
            throw new IllegalArgumentException();
        }
        return (byte) Math.max(Math.min(val, max), min);
    }

    public static short clamp(short val, short min, short max) {
        if (min > max) {
            throw new IllegalArgumentException();
        }
        return (short) Math.max(Math.min(val, max), min);
    }

    public static int clamp(int val, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException();
        }
        return Math.max(Math.min(val, max), min);
    }

    public static long clamp(long val, long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException();
        }
        return Math.max(Math.min(val, max), min);
    }

    public static float clamp(float val, float min, float max) {
        if (min > max) {
            throw new IllegalArgumentException();
        }
        return Math.max(Math.min(val, max), min);
    }

    public static double clamp(double val, double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException();
        }
        return Math.max(Math.min(val, max), min);
    }

    public static void byteArrayFill(byte[] array, byte value) {
        int len = array.length;
        if (len > 0) {
            array[0] = value;
        }
        for (int i = 1; i < len; i += i) {
            System.arraycopy(array, 0, array, i, Math.min((len - i), i));
        }
    }

    public static boolean intersects(ENG_Sphere sph, ENG_AxisAlignedBox a) {
        if (a.isNull()) {
            return false;
        }
        if (a.isInfinite()) {
            return true;
        }

        float s;
        float d = 0.0f;
        for (int i = 0; i < 3; ++i) {
            if (sph.center.get(i) < a.getMin().get(i)) {
                s = sph.center.get(i) - a.getMin().get(i);
                d += s * s;
            } else if (sph.center.get(i) > a.getMax().get(i)) {
                s = sph.center.get(i) - a.getMax().get(i);
                d += s * s;
            }
        }
        return (d <= (ENG_Math.sqr(sph.radius)));
    }

    public static boolean intersects(ENG_Plane p, ENG_AxisAlignedBox a) {
        return (p.getSide(a) == Side.BOTH_SIDE);
    }

    public static boolean intersects(ENG_Vector4D v, ENG_AxisAlignedBox a) {
        switch (a.getExtent()) {
            case EXTENT_NULL:
                return false;
            case EXTENT_FINITE:
                return (v.compareGreaterThan(a.getMin()) && v.compareLessThan(a.getMax()));
            case EXTENT_INFINITE:
                return true;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static boolean intersects(ENG_Sphere s, ENG_Plane p) {
        return (Math.abs(p.getDistance(s.center)) <= s.radius);
    }

    public static void intersects(ENG_Plane p, ENG_Ray r, ENG_Boolean b, ENG_Float dist) {
        float denom = p.normal.dotProduct(r.dir);
        if (Math.abs(denom) < FLOAT_EPSILON) {
            b.setValue(false);
        } else {
            float nom = p.normal.dotProduct(r.origin) + p.d;
            float t = -(nom / denom);
            b.setValue(t >= 0.0f);
            dist.setValue(t);
        }
    }

    public static void intersects(ENG_Ray r, ENG_Sphere s, ENG_Boolean _b, ENG_Float dist,
                                  boolean discardInside, ENG_Vector4D rayorig) {
        r.origin.sub(s.center, rayorig);
        if ((rayorig.squaredLength() <= (ENG_Math.sqr(s.radius))) && (discardInside)) {
            _b.setValue(true);
            dist.setValue(0.0f);
            return;
        }
        float a = r.dir.dotProduct(r.dir);
        float b = 2.0f * r.origin.dotProduct(r.dir);
        float c = r.origin.dotProduct(r.origin) - ENG_Math.sqr(s.radius);

        float d = (ENG_Math.sqr(b) - (4.0f * a * c));
        if (d < 0.0f) {
            _b.setValue(false);
            dist.setValue(0.0f);
        } else {
            float t = (-b - ENG_Math.sqrt(d)) / (2.0f * a);
            if (t < 0.0f) {
                t = (-b + ENG_Math.sqrt(d)) / (2.0f * a);
                _b.setValue(true);
                dist.setValue(t);
            }
        }


    }

    public static void intersects(ENG_Ray r, ENG_AxisAlignedBox a, ENG_Boolean b,
                                  ENG_Float dist, ENG_Vector4D hitpoint) {
        if (a.isNull()) {
            b.setValue(false);
            dist.setValue(0.0f);
            return;
        }
        if (a.isInfinite()) {
            b.setValue(true);
            dist.setValue(0.0f);
            return;
        }
        float lowt = 0.0f;
        float t;
        boolean hit = false;
        if ((r.origin.compareGreaterThan(a.getMin())) &&
                (r.origin.compareLessThan(a.getMax()))) {
            b.setValue(true);
            dist.setValue(0.0f);
            return;
        }
        if ((r.origin.x <= a.getMin().x) && (r.dir.x > 0.0f)) {
            t = (a.getMin().x - r.origin.x) / r.dir.x;
            if (t >= 0.0f) {
                r.dir.mul(t, hitpoint);
                hitpoint.addInPlace(r.origin);
                if ((hitpoint.y >= a.getMin().y) && (hitpoint.y <= a.getMax().y) &&
                        (hitpoint.z >= a.getMin().z) && (hitpoint.z <= a.getMax().z) &&
                        ((!hit) || (t < lowt))) {
                    hit = true;
                    lowt = t;
                }
            }
        }
        if ((r.origin.x >= a.getMax().x) && (r.dir.x < 0.0f)) {
            t = (a.getMax().x - r.origin.x) / r.dir.x;
            if (t >= 0.0f) {
                r.dir.mul(t, hitpoint);
                hitpoint.addInPlace(r.origin);
                if ((hitpoint.y >= a.getMin().y) && (hitpoint.y <= a.getMax().y) &&
                        (hitpoint.z >= a.getMin().z) && (hitpoint.z <= a.getMax().z) &&
                        ((!hit) || (t < lowt))) {
                    hit = true;
                    lowt = t;
                }
            }
        }
        if ((r.origin.y <= a.getMin().y) && (r.dir.y > 0.0f)) {
            t = (a.getMin().y - r.origin.y) / r.dir.y;
            if (t >= 0.0f) {
                r.dir.mul(t, hitpoint);
                hitpoint.addInPlace(r.origin);
                if ((hitpoint.x >= a.getMin().x) && (hitpoint.x <= a.getMax().x) &&
                        (hitpoint.z >= a.getMin().z) && (hitpoint.z <= a.getMax().z) &&
                        ((!hit) || (t < lowt))) {
                    hit = true;
                    lowt = t;
                }
            }
        }
        if ((r.origin.y >= a.getMax().y) && (r.dir.y < 0.0f)) {
            t = (a.getMax().y - r.origin.y) / r.dir.y;
            if (t >= 0.0f) {
                r.dir.mul(t, hitpoint);
                hitpoint.addInPlace(r.origin);
                if ((hitpoint.x >= a.getMin().x) && (hitpoint.x <= a.getMax().x) &&
                        (hitpoint.z >= a.getMin().z) && (hitpoint.z <= a.getMax().z) &&
                        ((!hit) || (t < lowt))) {
                    hit = true;
                    lowt = t;
                }
            }
        }
        if ((r.origin.z <= a.getMin().z) && (r.dir.z > 0.0f)) {
            t = (a.getMin().z - r.origin.z) / r.dir.z;
            if (t >= 0.0f) {
                r.dir.mul(t, hitpoint);
                hitpoint.addInPlace(r.origin);
                if ((hitpoint.x >= a.getMin().x) && (hitpoint.x <= a.getMax().x) &&
                        (hitpoint.y >= a.getMin().y) && (hitpoint.y <= a.getMax().y) &&
                        ((!hit) || (t < lowt))) {
                    hit = true;
                    lowt = t;
                }
            }
        }
        if ((r.origin.z >= a.getMax().z) && (r.dir.z < 0.0f)) {
            t = (a.getMax().z - r.origin.z) / r.dir.z;
            if (t >= 0.0f) {
                r.dir.mul(t, hitpoint);
                hitpoint.addInPlace(r.origin);
                if ((hitpoint.x >= a.getMin().x) && (hitpoint.x <= a.getMax().x) &&
                        (hitpoint.y >= a.getMin().y) && (hitpoint.y <= a.getMax().y) &&
                        ((!hit) || (t < lowt))) {
                    hit = true;
                    lowt = t;
                }
            }
        }
        b.setValue(hit);
        dist.setValue(lowt);


    }

    public static boolean intersects(ENG_Ray ray, ArrayList<ENG_Plane> planesList,
                                     boolean normalIsOutside, ENG_Boolean b, ENG_Float f) {
        /*Plane::Side outside = normalIsOutside ? Plane::POSITIVE_SIDE : Plane::NEGATIVE_SIDE;*/
        Side outside = normalIsOutside ? Side.POSITIVE_SIDE : Side.NEGATIVE_SIDE;
        boolean allInside = true;
        boolean retFirst = false;
        float retSecond = 0.0f;
        boolean endFirst = false;
        float endSecond = 0.0f;
        int num = planesList.size();
        //	ENG_Boolean b = new ENG_Boolean();
        //	ENG_Float f = new ENG_Float();
        for (int i = 0; i < num; ++i) {
            ENG_Plane plane = planesList.get(i);
            if (plane.getSide(ray.origin) == outside) {
                allInside = false;

                ray.intersects(plane, b, f);
                if (b.getValue()) {
                    retFirst = true;
                    retSecond = Math.max(retSecond, f.getValue());
                } else {
                    return false;
                }
            } else {
                ray.intersects(plane, b, f);
                if (b.getValue()) {
                    if (!endFirst) {
                        endFirst = true;
                        endSecond = f.getValue();
                    } else {
                        endSecond = Math.min(endSecond, f.getValue());
                    }
                }
            }
        }

        if (allInside) {
            return true;
        }

        if (endFirst) {
            if (endSecond < retSecond) {
                return false;
            }
        }

        return retFirst;
    }

    public static float sign(float v) {
        if (v > 0.0f) {
            return 1.0f;
        }
        if (v < 0.0f) {
            return -1.0f;
        }
        return 0.0f;
    }

    public static int sign(int v) {
        if (v > 0.0f) {
            return 1;
        }
        if (v < 0.0f) {
            return -1;
        }
        return 0;
    }

    public static ENG_Matrix4 makeViewMatrix(ENG_Vector4D position,
                                             ENG_Quaternion orientation, ENG_Matrix4 reflectMatrix) {
        ENG_Matrix4 viewMatrix = new ENG_Matrix4();
        makeViewMatrix(position, orientation, viewMatrix, reflectMatrix);
        return viewMatrix;
    }

    public static void makeViewMatrix(ENG_Vector4D position, ENG_Quaternion orientation,
                                      ENG_Matrix4 viewMatrix, ENG_Matrix4 reflectMatrix) {
        makeViewMatrix(position, orientation, viewMatrix, reflectMatrix,
                temp, temp2, tempVec);
    }

    public static void makeViewMatrix(ENG_Vector4D position, ENG_Quaternion orientation,
                                      ENG_Matrix4 viewMatrix, ENG_Matrix4 reflectMatrix, ENG_Matrix4 temp,
                                      ENG_Matrix4 temp2, ENG_Vector4D tempVec) {
        /*// View matrix is:
		//
		//  [ Lx  Uy  Dz  Tx  ]
		//  [ Lx  Uy  Dz  Ty  ]
		//  [ Lx  Uy  Dz  Tz  ]
		//  [ 0   0   0   1   ]
		//
		// Where T = -(Transposed(Rot) * Pos)

		// This is most efficiently done using 3x3 Matrices
		Matrix3 rot;
		orientation.ToRotationMatrix(rot);

		// Make the translation relative to new axes
		Matrix3 rotT = rot.Transpose();
		Vector3 trans = -rotT * position;*/
        temp.setIdentity();
        //	tempVec.set(ENG_Math.PT4_ZERO);
        orientation.toRotationMatrix(temp);
        temp.transpose(temp2);
        //	temp2.negateInPlace3x3();

        tempVec.set(position);
        temp2.transform(tempVec);

        tempVec.invertInPlace();
    	
    	/*// Make final matrix
		viewMatrix = Matrix4::IDENTITY;
		viewMatrix = rotT; // fills upper 3x3
		viewMatrix[0][3] = trans.x;
		viewMatrix[1][3] = trans.y;
		viewMatrix[2][3] = trans.z;

		// Deal with reflections
		if (reflectMatrix)
		{
			viewMatrix = viewMatrix * (*reflectMatrix);
		}

		return viewMatrix;*/
        viewMatrix.setIdentity();
        viewMatrix.set3x3(temp2);
        viewMatrix.setTrans(tempVec);

        if (reflectMatrix != null) {
            viewMatrix.postMultiply(reflectMatrix);
        }
    }

    public static float boundingRadiusFromAABB(ENG_AxisAlignedBox aabb) {
        ENG_Vector4D min = aabb.getMin();
        ENG_Vector4D max = aabb.getMax();

        ENG_Vector4D magnitude = new ENG_Vector4D(max);
        magnitude.makeCeil(max.invert());
        magnitude.makeCeil(min);
        magnitude.makeCeil(min.invert());

        return magnitude.length();
    }

    public static ENG_Matrix4 buildReflectionMatrix(ENG_Plane p) {
        ENG_Matrix4 ret = new ENG_Matrix4();
        buildReflectionMatrix(p, ret);
        return ret;
    }

    public static void buildReflectionMatrix(ENG_Plane p, ENG_Matrix4 ret) {
        ret.set(-2.0f * p.normal.x * p.normal.x + 1.0f, -2.0f * p.normal.x * p.normal.y, -2.0f * p.normal.x * p.normal.z, -2.0f * p.normal.x * p.d,
                -2.0f * p.normal.y * p.normal.x, -2.0f * p.normal.y * p.normal.y + 1.0f, -2.0f * p.normal.y * p.normal.z, -2.0f * p.normal.y * p.d,
                -2.0f * p.normal.z * p.normal.x, -2.0f * p.normal.z * p.normal.y, -2.0f * p.normal.z * p.normal.z + 1.0f, -2.0f * p.normal.z * p.d,
                0.0f, 0.0f, 0.0f, 1.0f);
    }

    public static float DegreesToRadians(float degrees) {
        return degrees * DEGREES_TO_RADIANS;
    }

    public static float RadiansToDegrees(float radians) {
        return radians * RADIANS_TO_DEGREES;
    }

    public static class QuadraticEquationResult {
        public float x1, x2;
        public boolean discNegative;
        public boolean discZero;
    }

    public static void solveQuadraticEquation(float a, float b, float c,
                                              QuadraticEquationResult result) {
        float disc = sqr(b) - 4 * a * c;
        if (disc < 0.0f) {
            result.discNegative = true;
            return;
        }
        if (ENG_Float.compareTo(disc, 0.0f) == ENG_Utility.COMPARE_EQUAL_TO) {
            result.discZero = true;
            result.x1 = result.x2 = -b / (2 * a);
        } else {
            result.x1 = (-b + sqrt(disc)) / (2 * a);
            result.x2 = (-b - sqrt(disc)) / (2 * a);
            result.discNegative = false;
            result.discZero = false;
        }
    }

    public static QuadraticEquationResult solveQuadraticEquation(float a, float b,
                                                                 float c) {
        QuadraticEquationResult result = new QuadraticEquationResult();
        solveQuadraticEquation(a, b, c, result);
        return result;
    }

    public static boolean isVectorInvalid(ENG_Vector4D temp1) {
        return Float.isNaN(temp1.x) ||
                Float.isNaN(temp1.y) ||
                Float.isNaN(temp1.z) ||
                Float.isInfinite(temp1.x) ||
                Float.isInfinite(temp1.y) ||
                Float.isInfinite(temp1.z);
    }

    public static boolean isVectorInvalid(ENG_Vector3D temp1) {
        return Float.isNaN(temp1.x) ||
                Float.isNaN(temp1.y) ||
                Float.isNaN(temp1.z) ||
                Float.isInfinite(temp1.x) ||
                Float.isInfinite(temp1.y) ||
                Float.isInfinite(temp1.z);
    }

    public static boolean isVectorInvalid(ENG_Vector2D temp1) {
        return Float.isNaN(temp1.x) ||
                Float.isNaN(temp1.y) ||
                Float.isInfinite(temp1.x) ||
                Float.isInfinite(temp1.y);
    }

    private static final ENG_Vector4D rotateTowardTempDir = new ENG_Vector4D();
    private static final ENG_Vector4D rotateTowardTempCrossProduct = new ENG_Vector4D();

    public static void rotateTowardPositionRad(ENG_Vector4D targetPos, ENG_Vector4D objPos,
                                               ENG_Vector4D objDir, ENG_Vector4D objUp,
                                               ENG_Quaternion ret, float maxTurnAngle, // in Rad
                                               ENG_Vector4D tempDir, ENG_Vector4D tempCrossProduct) {
        if (maxTurnAngle > PI) {
            maxTurnAngle = PI;
        }
        targetPos.sub(objPos, tempDir);
        //	System.out.println("distance " + tempDir.length());
        tempDir.normalize();
        float rotAngle = objDir.angleBetween(tempDir);// * ENG_Math.RADIANS_TO_DEGREES;
        if (rotAngle < ENG_Math.FLOAT_EPSILON) {
            ret.set(ENG_Math.QUAT_IDENTITY);
            return;
        } else if (Math.abs(rotAngle - ENG_Math.PI) < ENG_Math.FLOAT_EPSILON &&
                Math.abs(maxTurnAngle - ENG_Math.PI) < ENG_Math.FLOAT_EPSILON) {
            ENG_Quaternion.fromAngleAxisRad(Math.min(ENG_Math.PI, maxTurnAngle), objUp, ret);
            return;
        }
        //	EntityProperties entityProperties = WorldManager.getSingleton().getPlayerShip().getComponent(EntityProperties.class);
        //	System.out.println("ship pos: " + entityProperties.getNode().getPosition());
        //	CameraProperties cameraProperties = WorldManager.getSingleton().getPlayerShip().getComponent(CameraProperties.class);
        //	System.out.println("camera pos: " + cameraProperties.getNode().getPosition());
//        rotAngle *= ENG_Math.RADIANS_TO_DEGREES;
        //	System.out.println("rotAngle " + rotAngle);
        //	float rotDir = Math.signum(rotAngle);
        objDir.crossProduct(tempDir, tempCrossProduct);
        //	System.out.println("tempCrossProduct " + tempCrossProduct);
        //	if (tempCrossProduct.isZeroLength()) {
        tempCrossProduct.normalize();
        //	}
/*        if (MainActivity.isDebugmode()) {
//                if () {
//                        throw new IllegalArgumentException(tempCrossProduct.toString());
//                }
                if (ENG_Math.isVectorInvalid(tempCrossProduct) ||
                        Float.isInfinite(rotAngle) || Float.isNaN(rotAngle)) {
                        throw new IllegalArgumentException("rotAngle: " + rotAngle +
                                " tempCrossProduct: " + tempCrossProduct +
                                " targetPos: " + targetPos +
                                " objPos: " + objPos +
                                " objDir: " + objDir +
                                " objUp: " + objUp +
                                " maxRotationAngle: " + maxTurnAngle);
                }
        }*/

        ENG_Quaternion.fromAngleAxisRad(Math.min(rotAngle, maxTurnAngle), tempCrossProduct, ret);
//        ENG_Vector3D axis = new ENG_Vector3D();
//        float angle = ENG_Quaternion.toAngleAxisDeg(ret, axis);
//        System.out.println("rotation axis: " + axis + " angle: " + angle);
//        System.out.println("Complete rotation angle: " + (rotAngle * RADIANS_TO_DEGREES));
    }

    /**
     * It assumes vectors are normalized. Seems to require TS_WORLD to rotate
     * correctly. Still investigating the issue...
     *
     * @param targetPos
     * @param objPos
     * @param objDir
     * @param ret
     */
    public static void rotateTowardPositionDeg(ENG_Vector4D targetPos, ENG_Vector4D objPos,
                                               ENG_Vector4D objDir, ENG_Vector4D objUp,
                                               ENG_Quaternion ret, float maxTurnAngle, // in Deg
                                               ENG_Vector4D tempDir, ENG_Vector4D tempCrossProduct) {
        rotateTowardPositionRad(targetPos, objPos, objDir, objUp, ret, maxTurnAngle * DEGREES_TO_RADIANS, tempDir, tempCrossProduct);
    }

    /**
     * It assumes vectors are normalized. This is not thread safe! If you want it
     * to be thread safe then provide your own temp vectors
     *
     * @param targetPos
     * @param objPos
     * @param objDir
     * @param ret
     * @param maxTurnAngle
     */
    public static void rotateTowardPositionDeg(ENG_Vector4D targetPos, ENG_Vector4D objPos,
                                               ENG_Vector4D objDir, ENG_Vector4D objUp,
                                               ENG_Quaternion ret, float maxTurnAngle) {
        rotateTowardPositionDeg(targetPos, objPos, objDir, objUp, ret, maxTurnAngle, rotateTowardTempDir, rotateTowardTempCrossProduct);
    }

    public static void rotateTowardPositionRad(ENG_Vector4D targetPos, ENG_Vector4D objPos,
                                               ENG_Vector4D objDir, ENG_Vector4D objUp,
                                               ENG_Quaternion ret, float maxTurnAngle) {
        rotateTowardPositionRad(targetPos, objPos, objDir, objUp, ret, maxTurnAngle, rotateTowardTempDir, rotateTowardTempCrossProduct);
    }

    private static final ENG_Vector4D crossProductVec = new ENG_Vector4D();
    private static final ENG_Vector4D positionDiffVec = new ENG_Vector4D();

    /**
     * This is not thread safe. use the one where you provide the temp vectors for
     * thread safety.
     *
     * @param targetPos
     * @param objPos
     * @param objFront
     * @param maxTurnAngleRad
     * @param rotation
     * @noinspection deprecation
     */
    public static void rotateAwayFromPositionDeg(ENG_Vector4D targetPos,
                                                 ENG_Vector4D objPos, ENG_Vector4D objFront, float maxTurnAngleDeg,
                                                 ENG_Quaternion rotation) {
        rotateAwayFromPositionDeg(targetPos, objPos, objFront, maxTurnAngleDeg, rotation,
                positionDiffVec, crossProductVec);
    }

    /**
     * This is not thread safe. use the one where you provide the temp vectors for
     * thread safety.
     *
     * @param targetPos
     * @param objPos
     * @param objFront
     * @param maxTurnAngleRad
     * @param rotation
     * @noinspection deprecation
     */
    public static void rotateAwayFromPositionRad(ENG_Vector4D targetPos,
                                                 ENG_Vector4D objPos, ENG_Vector4D objFront, float maxTurnAngleRad,
                                                 ENG_Quaternion rotation) {
        rotateAwayFromPositionRad(targetPos, objPos, objFront, maxTurnAngleRad, rotation,
                positionDiffVec, crossProductVec);
    }

    /**
     * Also not thread safe!
     *
     * @param targetPos
     * @param objPos
     * @param objFront
     * @param maxTurnAngleRad
     * @return
     */
    @Deprecated
    public static ENG_Quaternion rotateAwayFromPositionDeg(ENG_Vector4D targetPos,
                                                           ENG_Vector4D objPos, ENG_Vector4D objFront, float maxTurnAngleDeg) {
        ENG_Quaternion ret = new ENG_Quaternion();
        rotateAwayFromPositionDeg(targetPos, objPos, objFront, maxTurnAngleDeg, ret);
        return ret;
    }

    /**
     * Also not thread safe!
     *
     * @param targetPos
     * @param objPos
     * @param objFront
     * @param maxTurnAngleRad
     * @return
     */
    @Deprecated
    public static ENG_Quaternion rotateAwayFromPositionRad(ENG_Vector4D targetPos,
                                                           ENG_Vector4D objPos, ENG_Vector4D objFront, float maxTurnAngleRad) {
        ENG_Quaternion ret = new ENG_Quaternion();
        rotateAwayFromPositionRad(targetPos, objPos, objFront, maxTurnAngleRad, ret);
        return ret;
    }

    /** @noinspection deprecation*/
    @Deprecated
    public static void rotateAwayFromPositionDeg(ENG_Vector4D targetPos,
                                                 ENG_Vector4D objPos, ENG_Vector4D objFront, float maxTurnAngleDeg,
                                                 ENG_Quaternion rotation,
                                                 ENG_Vector4D tempVec, ENG_Vector4D crossProductVec) {
        rotateAwayFromPositionRad(targetPos, objPos, objFront,
                maxTurnAngleDeg * ENG_Math.DEGREES_TO_RADIANS, rotation,
                tempVec, crossProductVec);
    }

    @Deprecated
    public static void rotateAwayFromPositionRad(ENG_Vector4D targetPos,
                                                 ENG_Vector4D objPos, ENG_Vector4D objFront, float maxTurnAngleRad,
                                                 ENG_Quaternion rotation,
                                                 ENG_Vector4D tempVec, ENG_Vector4D crossProductVec) {
        targetPos.sub(objPos, tempVec);
        tempVec.normalize();
        tempVec.crossProduct(objFront, crossProductVec);
        crossProductVec.normalize();
        float angleBetween = objFront.angleBetween(tempVec);
        float diffAngle = PI - angleBetween;
        if (diffAngle > maxTurnAngleRad) {
            diffAngle = maxTurnAngleRad;
        }
        ENG_Quaternion.fromAngleAxisRad(diffAngle, crossProductVec, rotation);
    }

    private static final ENG_Vector4D crossProductRotateToDir = new ENG_Vector4D();

    /**
     * Not thread safe!!!
     *
     * @param otherFrontDir
     * @param frontDir
     * @param maxTurnAngleRad
     * @return
     * @noinspection deprecation
     */
    @Deprecated
    public static ENG_Quaternion rotateToDirectionDeg(ENG_Vector4D otherFrontDir,
                                                      ENG_Vector4D frontDir, float maxTurnAngleRad) {
        ENG_Quaternion ret = new ENG_Quaternion();
        rotateToDirectionDeg(otherFrontDir, frontDir, maxTurnAngleRad, ret);
        return ret;
    }

    /**
     * Not thread safe!!!
     *
     * @param otherFrontDir
     * @param frontDir
     * @param maxTurnAngleRad
     * @return
     * @noinspection deprecation
     */
    @Deprecated
    public static ENG_Quaternion rotateToDirectionRad(ENG_Vector4D otherFrontDir,
                                                      ENG_Vector4D frontDir, float maxTurnAngleRad) {
        ENG_Quaternion ret = new ENG_Quaternion();
        rotateToDirectionRad(otherFrontDir, frontDir, maxTurnAngleRad, ret);
        return ret;
    }

    /**
     * Not thread safe!!!
     *
     * @param otherFrontDir
     * @param frontDir
     * @param maxTurnAngleRad
     * @param rotation
     * @noinspection deprecation
     */
    @Deprecated
    public static void rotateToDirectionRad(ENG_Vector4D otherFrontDir,
                                            ENG_Vector4D frontDir, float maxTurnAngleRad, ENG_Quaternion rotation) {
        rotateToDirectionRad(otherFrontDir, frontDir, maxTurnAngleRad, rotation,
                crossProductRotateToDir);
    }

    /**
     * The thread safe version
     *
     * @param otherFrontDir
     * @param frontDir
     * @param maxTurnAngleDeg
     * @param rotation
     * @noinspection deprecation
     */
    @Deprecated
    public static void rotateToDirectionDeg(ENG_Vector4D otherFrontDir,
                                            ENG_Vector4D frontDir, float maxTurnAngleDeg, ENG_Quaternion rotation) {
        rotateToDirectionDeg(otherFrontDir, frontDir,
                maxTurnAngleDeg,
                rotation, crossProductRotateToDir);
    }

    /**
     * The thread safe version
     *
     * @param otherFrontDir
     * @param frontDir
     * @param maxTurnAngleDeg
     * @param rotation
     * @param crossProductTemp
     * @noinspection deprecation
     */
    @Deprecated
    public static void rotateToDirectionDeg(ENG_Vector4D otherFrontDir,
                                            ENG_Vector4D frontDir, float maxTurnAngleDeg, ENG_Quaternion rotation,
                                            ENG_Vector4D crossProductTemp) {
        rotateToDirectionRad(otherFrontDir, frontDir,
                maxTurnAngleDeg * ENG_Math.DEGREES_TO_RADIANS,
                rotation, crossProductTemp);
    }

    /**
     * The thread safe version
     *
     * @param otherFrontDir
     * @param frontDir
     * @param maxTurnAngleRad
     * @param rotation
     * @param crossProductTemp
     */
    @Deprecated
    public static void rotateToDirectionRad(ENG_Vector4D otherFrontDir,
                                            ENG_Vector4D frontDir, float maxTurnAngleRad, ENG_Quaternion rotation,
                                            ENG_Vector4D crossProductTemp) {
        frontDir.crossProduct(otherFrontDir, crossProductTemp);
        crossProductTemp.normalize();
        float angleBetween = frontDir.angleBetween(otherFrontDir);
        if (angleBetween > maxTurnAngleRad) {
            angleBetween = maxTurnAngleRad;
        }
        ENG_Quaternion.fromAngleAxisRad(angleBetween, crossProductTemp, rotation);
    }

    /**
     * NOT THREAD SAFE!!!
     * @param currentPos
     * @param destPos
     * @param dt
     * @param torqueRet
     * @param maxAngularVelocity
     */
    public static void rotateToPositionTorque(ENG_Vector4D currentPos, ENG_Vector4D destPos, float dt,
                                     ENG_Vector4D torqueRet, float maxAngularVelocity) {
        rotateToPositionAngularVelocity(currentPos, destPos, dt, torqueRet);
//        if (torqueRet.length() > maxAngularVelocity) {
            torqueRet.normalize();
            torqueRet.mul(maxAngularVelocity);
//        }
    }

    private static final ENG_Vector4D rotateToPositionAngularVelocityTempVec1 = new ENG_Vector4D();
    private static final ENG_Vector4D rotateToPositionAngularVelocityTempVec2 = new ENG_Vector4D();
    private static final ENG_Vector4D rotateToPositionAngularVelocityTempVec3 = new ENG_Vector4D();

    /**
     * NOT THREAD SAFE!
     * @param currentPos
     * @param destPos
     * @param dt
     * @param angularVelocityRet
     */
    public static void rotateToPositionAngularVelocity(ENG_Vector4D currentPos, ENG_Vector4D destPos, float dt, ENG_Vector4D angularVelocityRet) {
        rotateToPositionAngularVelocity(currentPos, destPos, dt, angularVelocityRet,
                rotateToPositionAngularVelocityTempVec1, rotateToPositionAngularVelocityTempVec2, rotateToPositionAngularVelocityTempVec3);
    }

    /**
     * THREAD SAFE!
     * @param currentPos
     * @param destPos
     * @param dt
     * @param angularVelocityRet
     */
    public static void rotateToPositionAngularVelocityTS(ENG_Vector4D currentPos, ENG_Vector4D destPos, float dt, ENG_Vector4D angularVelocityRet) {
        rotateToPositionAngularVelocity(currentPos, destPos, dt, angularVelocityRet,
                new ENG_Vector4D(), new ENG_Vector4D(), new ENG_Vector4D());
    }

    public static void rotateToPositionAngularVelocity(ENG_Vector4D currentPos, ENG_Vector4D destPos, float dt, ENG_Vector4D angularVelocityRet,
                                                       ENG_Vector4D tempVec1, ENG_Vector4D tempVec2, ENG_Vector4D tempVec3) {
        currentPos.normalizedCopy(tempVec1);
        destPos.normalizedCopy(tempVec2);
        tempVec1.crossProduct(tempVec2, tempVec3);
        float len = clamp(tempVec3.length(), 0.0f, 1.0f);
        float theta = asin(len);
        tempVec3.normalize();
        tempVec3.mul(theta / (dt * 3));
        angularVelocityRet.set(tempVec3);
    }

//    public static void quaternionToEulerXYZ(btQuaternion quat, btVector3 euler)
//    {
//        float w=quat.getW();	float x=quat.getX();	float y=quat.getY();	float z=quat.getZ();
//        double sqw = w*w; double sqx = x*x; double sqy = y*y; double sqz = z*z;
//        euler.setZ((atan2(2.0 * (x*y + z*w),(sqx - sqy - sqz + sqw))));
//        euler.setX((atan2(2.0 * (y*z + x*w),(-sqx - sqy + sqz + sqw))));
//        euler.setY((asin(-2.0 * (x*z - y*w))));
//    }
//
//    public static void eulerXYZToQuaternion(btVector3 euler, Quaternion quat)
//    {
//        btMatrix3x3 mat = new btMatrix3x3();
//        mat.setIdentity();
//        mat.setEulerZYX(euler.getX(), euler.getY(), euler.getZ());
//        mat.getRotation(quat);
//
//        //equivalent?
//        //btQuaternion q(euler.getX(), euler.getY(), euler.getZ());
//        //btQuaternion q1(q.getY(), q.getX(), q.getZ(), q.getW());
//        //quat = q1;
//    }

    /**
     * NOT THREAD SAFE!!!
     * @param currentPos
     * @param destPos
     * @param dt
     * @param torqueRet
     * @param maxAngularVelocity
     */
    public static void rotateAwayFromPositionTorque(ENG_Vector4D currentPos, ENG_Vector4D destPos, float dt,
                                              ENG_Vector4D torqueRet, float maxAngularVelocity) {
        rotateAwayFromPositionAngularVelocity(currentPos, destPos, dt, torqueRet);
        torqueRet.normalize();
        torqueRet.mul(maxAngularVelocity);
    }

    /**
     * NOT THREAD SAFE!
     * @param currentPos
     * @param destPos
     * @param dt
     * @param angularVelocityRet
     */
    public static void rotateAwayFromPositionAngularVelocity(ENG_Vector4D currentPos, ENG_Vector4D destPos, float dt, ENG_Vector4D angularVelocityRet) {
        rotateAwayFromPositionAngularVelocity(currentPos, destPos, dt, angularVelocityRet,
                rotateToPositionAngularVelocityTempVec1, rotateToPositionAngularVelocityTempVec2, rotateToPositionAngularVelocityTempVec3);
    }

    /**
     * THREAD SAFE!
     * @param currentPos
     * @param destPos
     * @param dt
     * @param angularVelocityRet
     */
    public static void rotateAwayFromPositionAngularVelocityTS(ENG_Vector4D currentPos, ENG_Vector4D destPos, float dt, ENG_Vector4D angularVelocityRet) {
        rotateAwayFromPositionAngularVelocity(currentPos, destPos, dt, angularVelocityRet,
                new ENG_Vector4D(), new ENG_Vector4D(), new ENG_Vector4D());
    }

    public static void rotateAwayFromPositionAngularVelocity(ENG_Vector4D currentPos, ENG_Vector4D destPos, float dt, ENG_Vector4D angularVelocityRet,
                                                       ENG_Vector4D tempVec1, ENG_Vector4D tempVec2, ENG_Vector4D tempVec3) {
        currentPos.normalizedCopy(tempVec1);
        destPos.normalizedCopy(tempVec2);
        tempVec1.crossProduct(tempVec2, tempVec3);
        float len = clamp(tempVec3.length(), 0.0f, 1.0f);
        float theta = asin(1.0f - len);
        tempVec3.normalize();
        tempVec3.invertInPlace();
        tempVec3.mul(theta / dt);
        angularVelocityRet.set(tempVec3);
    }

    public static ENG_Vector4D generateRandomPositionOnRadius(float radius) {
        return generateRandomPositionOnRadius(null, radius);
    }

    public static ENG_Vector4D generateRandomPositionOnRadius(String s, float radius) {
        ENG_Vector4D ret = new ENG_Vector4D(true);
        generateRandomPositionOnRadius(s, radius, ret);
        return ret;
    }

    public static void generateRandomPositionOnRadius(float radius, ENG_Vector4D ret) {
        generateRandomPositionOnRadius(null, radius, ret);
    }

    public static void generateRandomPositionOnRadius(String s, float radius, ENG_Vector4D ret) {
        float rand = ENG_Math.sqr(radius * 2.0f);
        float xSqr = ENG_Utility.getRandom().nextFloat(s != null ? (s + "_0") : null) * rand;
        float ySqr = ENG_Utility.getRandom().nextFloat(s != null ? (s + "_1") : null) * (rand - xSqr);
        float zSqr = rand - xSqr - ySqr;
        ret.x = ENG_Math.sqrt(xSqr) - radius;
        ret.y = ENG_Math.sqrt(ySqr) - radius;
        ret.z = ENG_Math.sqrt(zSqr) - radius;
    }

    public static boolean nearlyEqual(float a, float b) {
        return nearlyEqual(a, b, FLOAT_EPSILON);
    }

    public static boolean nearlyEqual(float a, float b, float epsilon) {
        final float absA = Math.abs(a);
        final float absB = Math.abs(b);
        final float diff = Math.abs(a - b);

        if (a == b) { // shortcut, handles infinities
            return true;
        } else if (a == 0 || b == 0 || diff < Float.MIN_NORMAL) {
            // a or b is zero or both are extremely close to it
            // relative error is less meaningful here
            return diff < (epsilon * Float.MIN_NORMAL);
        } else { // use relative error
            return diff / Math.min((absA + absB), Float.MAX_VALUE) < epsilon;
        }
    }

    public static float nextFloatUp(float f) {
        if (Float.isInfinite(f) && f > 0.0f) {
            return f;
        }
        if (f == -0.0f) {
            f = 0.0f;
        }
        int i = Float.floatToRawIntBits(f);
        if (f >= 0.0f) {
            ++i;
        } else {
            --i;
        }
        return Float.intBitsToFloat(i);
    }

    public static float nextFloatDown(float f) {
        if (Float.isInfinite(f) && f > 0.0f) {
            return f;
        }
        if (f == -0.0f) {
            f = 0.0f;
        }
        int i = Float.floatToRawIntBits(f);
        if (f <= 0.0f) {
            ++i;
        } else {
            --i;
        }
        return Float.intBitsToFloat(i);
    }

    public static int byteToUnsigned(byte b) {
        return b & 0xFF;
    }

    public static int shortToUnsigned(short b) {
        return b & 0xFFFF;
    }

    public static long intToUnsigned(int b) {
        return b & ((long) 0xFFFFFFFF);
    }
}
