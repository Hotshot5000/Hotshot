package headwayent.hotshotengine;


import headwayent.hotshotengine.ENG_Plane.Side;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Double;

import java.util.ArrayList;

public class ENG_Math {

    public static final double PI = (double) Math.PI;
    public static final double TWO_PI = 2.0f * PI;
    public static final double FOUR_PI = 4.0f * PI;
    public static final double HALF_PI = 0.5f * PI;
    public static final double QUARTER_PI = 0.25f * PI;
    public static final double DEGREES_TO_RADIANS = 0.0174532925f;
    public static final double RADIANS_TO_DEGREES = 57.295779513f;
    public static final double FLOAT_EPSILON = 0.00001f;
    public static final double FLOAT_EPSILON_SQUARED = FLOAT_EPSILON * FLOAT_EPSILON;
    public static final double DOUBLE_EPSILON = 0.00001;
    public static final double DOUBLE_EPSILON_SQUARED = DOUBLE_EPSILON * DOUBLE_EPSILON;
    public static final double MAX_DIST = 3.40282e+38f;

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
    public static final double FLT_EPSILON = 1.192092896e-07F;        /* smallest such that 1.0+FLT_EPSILON != 1.0 */
    public static final int FLT_GUARD = 0;
    public static final int FLT_MANT_DIG = 24;                      /* # of bits in mantissa */
    public static final double FLT_MAX = 3.402823466e+38F;        /* max value */
    public static final int FLT_MAX_10_EXP = 38;                      /* max decimal exponent */
    public static final int FLT_MAX_EXP = 128;                     /* max binary exponent */
    public static final double FLT_MIN = 1.175494351e-38F;        /* min positive value */
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

    public static double acos(double f) {
        return (double) Math.acos(f);
    }

    public static double asin(double f) {
        return (double) Math.asin(f);
    }

    public static double atan(double f) {
        return (double) Math.atan(f);
    }


    public static double atan2(double y, double x) {
        return (double) Math.atan2(y, x);
    }

    public static double cbrt(double f) {
        return (double) Math.cbrt(f);
    }

    public static double ceil(double f) {
        return (double) Math.ceil(f);
    }

    public static double cos(double f) {
        return (double) Math.cos(f);
    }

    public static double cosh(double f) {
        return (double) Math.cosh(f);
    }

    public static double exp(double f) {
        return (double) Math.exp(f);
    }

    public static double expm1(double f) {
        return (double) Math.expm1(f);
    }

    public static double floor(double f) {
        return (double) Math.floor(f);
    }

    public static double hypot(double x, double y) {
        return (double) Math.hypot(x, y);
    }

    public static double log(double f) {
        return (double) Math.log(f);
    }

    public static double log10(double f) {
        return (double) Math.log10(f);
    }

    public static double log1p(double f) {
        return (double) Math.log1p(f);
    }

    public static double pow(double x, double y) {
        return (double) Math.pow(x, y);
    }

    public static double random() {
        return (double) Math.random();
    }

    public static double rint(double f) {
        return (double) Math.rint(f);
    }

    public static double sin(double f) {
        return (double) Math.sin(f);
    }

    public static double sinh(double f) {
        return (double) Math.sinh(f);
    }

    public static double sqrt(double f) {
        return (double) Math.sqrt(f);
    }

    public static double rsqrt(double f) {
        return (1.0f / (double) Math.sqrt(f));
    }

    public static double sqr(double f) {
        return (f * f);
    }

    public static double tan(double f) {
        return (double) Math.tan(f);
    }

    public static double tanh(double f) {
        return (double) Math.tanh(f);
    }

    public static double toDegrees(double angrad) {
        return (double) Math.toDegrees(angrad);
    }

    public static double toRadians(double angdeg) {
        return (double) Math.toRadians(angdeg);
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
            System.arraycopy(array, 0, array, i, ((len - i) < i) ? (len - i) : (i));
        }
    }

    public static boolean intersects(ENG_Sphere sph, ENG_AxisAlignedBox a) {
        if (a.isNull()) {
            return false;
        }
        if (a.isInfinite()) {
            return true;
        }

        double s;
        double d = 0.0f;
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

    public static void intersects(ENG_Plane p, ENG_Ray r, ENG_Boolean b, ENG_Double dist) {
        double denom = p.normal.dotProduct(r.dir);
        if (Math.abs(denom) < FLOAT_EPSILON) {
            b.setValue(false);
        } else {
            double nom = p.normal.dotProduct(r.origin) + p.d;
            double t = -(nom / denom);
            b.setValue(t >= 0.0f);
            dist.setValue(t);
        }
    }

    public static void intersects(ENG_Ray r, ENG_Sphere s, ENG_Boolean _b, ENG_Double dist,
                                  boolean discardInside, ENG_Vector4D rayorig) {
        r.origin.sub(s.center, rayorig);
        if ((rayorig.squaredLength() <= (ENG_Math.sqr(s.radius))) && (discardInside)) {
            _b.setValue(true);
            dist.setValue(0.0f);
            return;
        }
        double a = r.dir.dotProduct(r.dir);
        double b = 2.0f * r.origin.dotProduct(r.dir);
        double c = r.origin.dotProduct(r.origin) - ENG_Math.sqr(s.radius);

        double d = (ENG_Math.sqr(b) - (4.0f * a * c));
        if (d < 0.0f) {
            _b.setValue(false);
            dist.setValue(0.0f);
        } else {
            double t = (-b - ENG_Math.sqrt(d)) / (2.0f * a);
            if (t < 0.0f) {
                t = (-b + ENG_Math.sqrt(d)) / (2.0f * a);
                _b.setValue(true);
                dist.setValue(t);
            }
        }


    }

    public static void intersects(ENG_Ray r, ENG_AxisAlignedBox a, ENG_Boolean b,
                                  ENG_Double dist, ENG_Vector4D hitpoint) {
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
        double lowt = 0.0f;
        double t;
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
                                     boolean normalIsOutside, ENG_Boolean b, ENG_Double f) {
        /*Plane::Side outside = normalIsOutside ? Plane::POSITIVE_SIDE : Plane::NEGATIVE_SIDE;*/
        Side outside = normalIsOutside ? Side.POSITIVE_SIDE : Side.NEGATIVE_SIDE;
        boolean allInside = true;
        boolean retFirst = false;
        double retSecond = 0.0f;
        boolean endFirst = false;
        double endSecond = 0.0f;
        int num = planesList.size();
        //	ENG_Boolean b = new ENG_Boolean();
        //	ENG_Double f = new ENG_Double();
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

    public static double sign(double v) {
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

    public static double boundingRadiusFromAABB(ENG_AxisAlignedBox aabb) {
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

    public static double DegreesToRadians(double degrees) {
        return degrees * DEGREES_TO_RADIANS;
    }

    public static double RadiansToDegrees(double radians) {
        return radians * RADIANS_TO_DEGREES;
    }

    public static class QuadraticEquationResult {
        public double x1, x2;
        public boolean discNegative;
        public boolean discZero;
    }

    public static void solveQuadraticEquation(double a, double b, double c,
                                              QuadraticEquationResult result) {
        double disc = sqr(b) - 4 * a * c;
        if (disc < 0.0f) {
            result.discNegative = true;
            return;
        }
        if (ENG_Double.compareTo(disc, 0.0f) == ENG_Utility.COMPARE_EQUAL_TO) {
            result.discZero = true;
            result.x1 = result.x2 = -b / (2 * a);
        } else {
            result.x1 = (-b + sqrt(disc)) / (2 * a);
            result.x2 = (-b - sqrt(disc)) / (2 * a);
            result.discNegative = false;
            result.discZero = false;
        }
    }

    public static QuadraticEquationResult solveQuadraticEquation(double a, double b,
                                                                 double c) {
        QuadraticEquationResult result = new QuadraticEquationResult();
        solveQuadraticEquation(a, b, c, result);
        return result;
    }

    public static boolean isVectorInvalid(ENG_Vector4D temp1) {
        return Double.isNaN(temp1.x) ||
                Double.isNaN(temp1.y) ||
                Double.isNaN(temp1.z) ||
                Double.isInfinite(temp1.x) ||
                Double.isInfinite(temp1.y) ||
                Double.isInfinite(temp1.z);
    }

    public static boolean isVectorInvalid(ENG_Vector3D temp1) {
        return Double.isNaN(temp1.x) ||
                Double.isNaN(temp1.y) ||
                Double.isNaN(temp1.z) ||
                Double.isInfinite(temp1.x) ||
                Double.isInfinite(temp1.y) ||
                Double.isInfinite(temp1.z);
    }

    public static boolean isVectorInvalid(ENG_Vector2D temp1) {
        return Double.isNaN(temp1.x) ||
                Double.isNaN(temp1.y) ||
                Double.isInfinite(temp1.x) ||
                Double.isInfinite(temp1.y);
    }

    private static final ENG_Vector4D rotateTowardTempDir = new ENG_Vector4D();
    private static final ENG_Vector4D rotateTowardTempCrossProduct = new ENG_Vector4D();

    public static void rotateTowardPositionRad(ENG_Vector4D targetPos, ENG_Vector4D objPos,
                                               ENG_Vector4D objDir, ENG_Vector4D objUp,
                                               ENG_Quaternion ret, double maxTurnAngle, // in Rad
                                               ENG_Vector4D tempDir, ENG_Vector4D tempCrossProduct) {
        if (maxTurnAngle > PI) {
            maxTurnAngle = PI;
        }
        targetPos.sub(objPos, tempDir);
        //	System.out.println("distance " + tempDir.length());
        tempDir.normalize();
        double rotAngle = objDir.angleBetween(tempDir);// * ENG_Math.RADIANS_TO_DEGREES;
        if (rotAngle < ENG_Math.FLOAT_EPSILON) {
            ret.set(ENG_Math.QUAT_IDENTITY);
            return;
        } else if (Math.abs(rotAngle - ENG_Math.PI) < ENG_Math.FLOAT_EPSILON &&
                Math.abs(maxTurnAngle - ENG_Math.PI) < ENG_Math.FLOAT_EPSILON) {
            ENG_Quaternion.fromAngleAxisRad(ENG_Math.PI > maxTurnAngle ?
                    maxTurnAngle : ENG_Math.PI, objUp, ret);
            return;
        }
        //	EntityProperties entityProperties = WorldManager.getSingleton().getPlayerShip().getComponent(EntityProperties.class);
        //	System.out.println("ship pos: " + entityProperties.getNode().getPosition());
        //	CameraProperties cameraProperties = WorldManager.getSingleton().getPlayerShip().getComponent(CameraProperties.class);
        //	System.out.println("camera pos: " + cameraProperties.getNode().getPosition());
//        rotAngle *= ENG_Math.RADIANS_TO_DEGREES;
        //	System.out.println("rotAngle " + rotAngle);
        //	double rotDir = Math.signum(rotAngle);
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
                        Double.isInfinite(rotAngle) || Double.isNaN(rotAngle)) {
                        throw new IllegalArgumentException("rotAngle: " + rotAngle +
                                " tempCrossProduct: " + tempCrossProduct +
                                " targetPos: " + targetPos +
                                " objPos: " + objPos +
                                " objDir: " + objDir +
                                " objUp: " + objUp +
                                " maxRotationAngle: " + maxTurnAngle);
                }
        }*/

        ENG_Quaternion.fromAngleAxisRad(rotAngle > maxTurnAngle ? maxTurnAngle : rotAngle, tempCrossProduct, ret);
//        ENG_Vector3D axis = new ENG_Vector3D();
//        double angle = ENG_Quaternion.toAngleAxisDeg(ret, axis);
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
                                               ENG_Quaternion ret, double maxTurnAngle, // in Deg
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
                                               ENG_Quaternion ret, double maxTurnAngle) {
        rotateTowardPositionDeg(targetPos, objPos, objDir, objUp, ret, maxTurnAngle, rotateTowardTempDir, rotateTowardTempCrossProduct);
    }

    public static void rotateTowardPositionRad(ENG_Vector4D targetPos, ENG_Vector4D objPos,
                                               ENG_Vector4D objDir, ENG_Vector4D objUp,
                                               ENG_Quaternion ret, double maxTurnAngle) {
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
     */
    public static void rotateAwayFromPositionDeg(ENG_Vector4D targetPos,
                                                 ENG_Vector4D objPos, ENG_Vector4D objFront, double maxTurnAngleDeg,
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
     */
    public static void rotateAwayFromPositionRad(ENG_Vector4D targetPos,
                                                 ENG_Vector4D objPos, ENG_Vector4D objFront, double maxTurnAngleRad,
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
                                                           ENG_Vector4D objPos, ENG_Vector4D objFront, double maxTurnAngleDeg) {
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
                                                           ENG_Vector4D objPos, ENG_Vector4D objFront, double maxTurnAngleRad) {
        ENG_Quaternion ret = new ENG_Quaternion();
        rotateAwayFromPositionRad(targetPos, objPos, objFront, maxTurnAngleRad, ret);
        return ret;
    }

    @Deprecated
    public static void rotateAwayFromPositionDeg(ENG_Vector4D targetPos,
                                                 ENG_Vector4D objPos, ENG_Vector4D objFront, double maxTurnAngleDeg,
                                                 ENG_Quaternion rotation,
                                                 ENG_Vector4D tempVec, ENG_Vector4D crossProductVec) {
        rotateAwayFromPositionRad(targetPos, objPos, objFront,
                maxTurnAngleDeg * ENG_Math.DEGREES_TO_RADIANS, rotation,
                tempVec, crossProductVec);
    }

    @Deprecated
    public static void rotateAwayFromPositionRad(ENG_Vector4D targetPos,
                                                 ENG_Vector4D objPos, ENG_Vector4D objFront, double maxTurnAngleRad,
                                                 ENG_Quaternion rotation,
                                                 ENG_Vector4D tempVec, ENG_Vector4D crossProductVec) {
        targetPos.sub(objPos, tempVec);
        tempVec.normalize();
        tempVec.crossProduct(objFront, crossProductVec);
        crossProductVec.normalize();
        double angleBetween = objFront.angleBetween(tempVec);
        double diffAngle = PI - angleBetween;
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
     */
    @Deprecated
    public static ENG_Quaternion rotateToDirectionDeg(ENG_Vector4D otherFrontDir,
                                                      ENG_Vector4D frontDir, double maxTurnAngleRad) {
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
     */
    @Deprecated
    public static ENG_Quaternion rotateToDirectionRad(ENG_Vector4D otherFrontDir,
                                                      ENG_Vector4D frontDir, double maxTurnAngleRad) {
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
     */
    @Deprecated
    public static void rotateToDirectionRad(ENG_Vector4D otherFrontDir,
                                            ENG_Vector4D frontDir, double maxTurnAngleRad, ENG_Quaternion rotation) {
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
     */
    @Deprecated
    public static void rotateToDirectionDeg(ENG_Vector4D otherFrontDir,
                                            ENG_Vector4D frontDir, double maxTurnAngleDeg, ENG_Quaternion rotation) {
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
     */
    @Deprecated
    public static void rotateToDirectionDeg(ENG_Vector4D otherFrontDir,
                                            ENG_Vector4D frontDir, double maxTurnAngleDeg, ENG_Quaternion rotation,
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
                                            ENG_Vector4D frontDir, double maxTurnAngleRad, ENG_Quaternion rotation,
                                            ENG_Vector4D crossProductTemp) {
        frontDir.crossProduct(otherFrontDir, crossProductTemp);
        crossProductTemp.normalize();
        double angleBetween = frontDir.angleBetween(otherFrontDir);
        if (angleBetween > maxTurnAngleRad) {
            angleBetween = maxTurnAngleRad;
        }
        ENG_Quaternion.fromAngleAxisRad(angleBetween, crossProductTemp, rotation);
    }

    /**
     * NOT THREAD SAFE!!!
     *
     * @param currentPos
     * @param destPos
     * @param dt
     * @param torqueRet
     * @param maxAngularVelocity
     */
    public static void rotateToPositionTorque(ENG_Vector4D currentPos, ENG_Vector4D destPos, double dt,
                                              ENG_Vector4D torqueRet, double maxAngularVelocity) {
        rotateToPositionAngularVelocity(currentPos, destPos, dt, torqueRet);
//        if (torqueRet.length() > maxAngularVelocity) {
        torqueRet.normalize();
        torqueRet.mul(maxAngularVelocity);
//        }
    }

    private static ENG_Vector4D rotateToPositionAngularVelocityTempVec1 = new ENG_Vector4D();
    private static ENG_Vector4D rotateToPositionAngularVelocityTempVec2 = new ENG_Vector4D();
    private static ENG_Vector4D rotateToPositionAngularVelocityTempVec3 = new ENG_Vector4D();

    /**
     * NOT THREAD SAFE!
     *
     * @param currentPos
     * @param destPos
     * @param dt
     * @param angularVelocityRet
     */
    public static void rotateToPositionAngularVelocity(ENG_Vector4D currentPos, ENG_Vector4D destPos, double dt, ENG_Vector4D angularVelocityRet) {
        rotateToPositionAngularVelocity(currentPos, destPos, dt, angularVelocityRet,
                rotateToPositionAngularVelocityTempVec1, rotateToPositionAngularVelocityTempVec2, rotateToPositionAngularVelocityTempVec3);
    }

    /**
     * THREAD SAFE!
     *
     * @param currentPos
     * @param destPos
     * @param dt
     * @param angularVelocityRet
     */
    public static void rotateToPositionAngularVelocityTS(ENG_Vector4D currentPos, ENG_Vector4D destPos, double dt, ENG_Vector4D angularVelocityRet) {
        rotateToPositionAngularVelocity(currentPos, destPos, dt, angularVelocityRet,
                new ENG_Vector4D(), new ENG_Vector4D(), new ENG_Vector4D());
    }

    public static void rotateToPositionAngularVelocity(ENG_Vector4D currentPos, ENG_Vector4D destPos, double dt, ENG_Vector4D angularVelocityRet,
                                                       ENG_Vector4D tempVec1, ENG_Vector4D tempVec2, ENG_Vector4D tempVec3) {
        currentPos.normalizedCopy(tempVec1);
        destPos.normalizedCopy(tempVec2);
        tempVec1.crossProduct(tempVec2, tempVec3);
        double len = clamp(tempVec3.length(), 0.0f, 1.0f);
        double theta = asin(len);
        tempVec3.normalize();
        tempVec3.mul(theta / (dt * 3));
        angularVelocityRet.set(tempVec3);
    }

//    public static void quaternionToEulerXYZ(btQuaternion quat, btVector3 euler)
//    {
//        double w=quat.getW();	double x=quat.getX();	double y=quat.getY();	double z=quat.getZ();
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
     *
     * @param currentPos
     * @param destPos
     * @param dt
     * @param torqueRet
     * @param maxAngularVelocity
     */
    public static void rotateAwayFromPositionTorque(ENG_Vector4D currentPos, ENG_Vector4D destPos, double dt,
                                                    ENG_Vector4D torqueRet, double maxAngularVelocity) {
        rotateAwayFromPositionAngularVelocity(currentPos, destPos, dt, torqueRet);
        torqueRet.normalize();
        torqueRet.mul(maxAngularVelocity);
    }

    /**
     * NOT THREAD SAFE!
     *
     * @param currentPos
     * @param destPos
     * @param dt
     * @param angularVelocityRet
     */
    public static void rotateAwayFromPositionAngularVelocity(ENG_Vector4D currentPos, ENG_Vector4D destPos, double dt, ENG_Vector4D angularVelocityRet) {
        rotateAwayFromPositionAngularVelocity(currentPos, destPos, dt, angularVelocityRet,
                rotateToPositionAngularVelocityTempVec1, rotateToPositionAngularVelocityTempVec2, rotateToPositionAngularVelocityTempVec3);
    }

    /**
     * THREAD SAFE!
     *
     * @param currentPos
     * @param destPos
     * @param dt
     * @param angularVelocityRet
     */
    public static void rotateAwayFromPositionAngularVelocityTS(ENG_Vector4D currentPos, ENG_Vector4D destPos, double dt, ENG_Vector4D angularVelocityRet) {
        rotateAwayFromPositionAngularVelocity(currentPos, destPos, dt, angularVelocityRet,
                new ENG_Vector4D(), new ENG_Vector4D(), new ENG_Vector4D());
    }

    public static void rotateAwayFromPositionAngularVelocity(ENG_Vector4D currentPos, ENG_Vector4D destPos, double dt, ENG_Vector4D angularVelocityRet,
                                                             ENG_Vector4D tempVec1, ENG_Vector4D tempVec2, ENG_Vector4D tempVec3) {
        currentPos.normalizedCopy(tempVec1);
        destPos.normalizedCopy(tempVec2);
        tempVec1.crossProduct(tempVec2, tempVec3);
        double len = clamp(tempVec3.length(), 0.0f, 1.0f);
        double theta = asin(1.0f - len);
        tempVec3.normalize();
        tempVec3.invertInPlace();
        tempVec3.mul(theta / dt);
        angularVelocityRet.set(tempVec3);
    }

    public static ENG_Vector4D generateRandomPositionOnRadius(double radius) {
        return generateRandomPositionOnRadius(null, radius);
    }

    public static ENG_Vector4D generateRandomPositionOnRadius(String s, double radius) {
        ENG_Vector4D ret = new ENG_Vector4D(true);
        generateRandomPositionOnRadius(s, radius, ret);
        return ret;
    }

    public static void generateRandomPositionOnRadius(double radius, ENG_Vector4D ret) {
        generateRandomPositionOnRadius(null, radius, ret);
    }

    public static void generateRandomPositionOnRadius(String s, double radius, ENG_Vector4D ret) {
        double rand = ENG_Math.sqr(radius * 2.0f);
        double xSqr = ENG_Utility.getRandom().nextDouble(s != null ? (s + "_0") : null) * rand;
        double ySqr = ENG_Utility.getRandom().nextDouble(s != null ? (s + "_1") : null) * (rand - xSqr);
        double zSqr = rand - xSqr - ySqr;
        ret.x = ENG_Math.sqrt(xSqr) - radius;
        ret.y = ENG_Math.sqrt(ySqr) - radius;
        ret.z = ENG_Math.sqrt(zSqr) - radius;
    }

    public static boolean nearlyEqual(double a, double b) {
        return nearlyEqual(a, b, FLOAT_EPSILON);
    }

    public static boolean nearlyEqual(double a, double b, double epsilon) {
        final double absA = Math.abs(a);
        final double absB = Math.abs(b);
        final double diff = Math.abs(a - b);

        if (a == b) { // shortcut, handles infinities
            return true;
        } else if (a == 0 || b == 0 || diff < Double.MIN_NORMAL) {
            // a or b is zero or both are extremely close to it
            // relative error is less meaningful here
            return diff < (epsilon * Double.MIN_NORMAL);
        } else { // use relative error
            return diff / Math.min((absA + absB), Double.MAX_VALUE) < epsilon;
        }
    }

    public static double nextDoubleUp(double f) {
        if (Double.isInfinite(f) && f > 0.0f) {
            return f;
        }
        if (f == -0.0f) {
            f = 0.0f;
        }
        long i = Double.doubleToRawLongBits(f);
        if (f >= 0.0f) {
            ++i;
        } else {
            --i;
        }
        return Double.longBitsToDouble(i);
    }

    public static double nextDoubleDown(double f) {
        if (Double.isInfinite(f) && f > 0.0f) {
            return f;
        }
        if (f == -0.0f) {
            f = 0.0f;
        }
        long i = Double.doubleToRawLongBits(f);
        if (f <= 0.0f) {
            ++i;
        } else {
            --i;
        }
        return Double.longBitsToDouble(i);
    }

    /**
     * Converts DIS xyz world coordinates to latitude and longitude (IN RADIANS). This algorithm may not be 100% accurate
     * near the poles. Uses WGS84 , though you can change the ellipsoid constants a and b if you want to use something
     * else. These formulas were obtained from Military Handbook 600008
     *
     * @param xyz A double array with the x, y, and z coordinates, in that order.
     * @return An array with the lat, long, and elevation corresponding to those coordinates.
     * Elevation is in meters, lat and long are in radians
     */
    public static ENG_Vector3D xyzToLatLonRadians(ENG_Vector3D xyz) {
        double x = xyz.x;
        double y = xyz.y;
        double z = xyz.z;
        ENG_Vector3D answer = new ENG_Vector3D();
        double a = 6378137.0; //semi major axis
        double b = 6356752.3142; //semi minor axis

        double eSquared; //first eccentricity squared
        double rSubN; //radius of the curvature of the prime vertical
        double ePrimeSquared;//second eccentricity squared
        double W = Math.sqrt((x * x + y * y));

        eSquared = (a * a - b * b) / (a * a);
        ePrimeSquared = (a * a - b * b) / (b * b);

        /**
         * Get the longitude.
         */
        if (x >= 0) {
            answer.y = Math.atan(y / x);
        } else if (x < 0 && y >= 0) {
            answer.y = Math.atan(y / x) + Math.PI;
        } else {
            answer.y = Math.atan(y / x) - Math.PI;
        }

        /**
         * Longitude calculation done. Now calculate latitude.
         * NOTE: The handbook mentions using the calculated phi (latitude) value to recalculate B
         * using tan B = (1-f) tan phi and then performing the entire calculation again to get more accurate values.
         * However, for terrestrial applications, one iteration is accurate to .1 millimeter on the surface  of the
         * earth (Rapp, 1984, p.124), so one iteration is enough for our purposes
         */

        double tanBZero = (a * z) / (b * W);
        double BZero = Math.atan((tanBZero));
        double tanPhi = (z + (ePrimeSquared * b * (Math.pow(Math.sin(BZero), 3)))) / (W - (a * eSquared * (Math.pow(Math.cos(BZero), 3))));
        double phi = Math.atan(tanPhi);
        answer.x = phi;
        /**
         * Latitude done, now get the elevation. Note: The handbook states that near the poles, it is preferable to use
         * h = (Z / sin phi ) - rSubN + (eSquared * rSubN). Our applications are never near the poles, so this formula
         * was left unimplemented.
         */
        rSubN = (a * a) / Math.sqrt(((a * a) * (Math.cos(phi) * Math.cos(phi)) + ((b * b) * (Math.sin(phi) * Math.sin(phi)))));

        answer.z = (W / Math.cos(phi)) - rSubN;

        return answer;
    }

    /**
     * Converts DIS xyz world coordinates to latitude and longitude (IN DEGREES). This algorithm may not be 100% accurate
     * near the poles. Uses WGS84 , though you can change the ellipsoid constants a and b if you want to use something
     * else. These formulas were obtained from Military Handbook 600008
     *
     * @param xyz A double array with the x, y, and z coordinates, in that order.
     * @return An array with the lat, lon, and elevation corresponding to those coordinates.
     * Elevation is in meters, lat and long are in degrees
     */
    public static ENG_Vector3D xyzToLatLonDegrees(ENG_Vector3D xyz) {
        ENG_Vector3D degrees = xyzToLatLonRadians(xyz);

        degrees.x = degrees.x * RADIANS_TO_DEGREES;
        degrees.y = degrees.y * RADIANS_TO_DEGREES;

        return degrees;

    }

    /**
     * Converts lat long and geodetic height (elevation) into DIS XYZ
     * This algorithm also uses the WGS84 ellipsoid, though you can change the values
     * of a and b for a different ellipsoid. Adapted from Military Handbook 600008
     *
     * @param latitude  The latitude, IN RADIANS
     * @param longitude The longitude, in RADIANS
     * @param height    The elevation, in meters
     * @return a double array with the calculated X, Y, and Z values, in that order
     */
    public static ENG_Vector3D getXYZfromLatLonRadians(double latitude, double longitude, double height) {
        double a = 6378137.0; //semi major axis
        double b = 6356752.3142; //semi minor axis
        double cosLat = Math.cos(latitude);
        double sinLat = Math.sin(latitude);


        double rSubN = (a * a) / Math.sqrt(((a * a) * (cosLat * cosLat) + ((b * b) * (sinLat * sinLat))));

        double X = (rSubN + height) * cosLat * Math.cos(longitude);
        double Y = (rSubN + height) * cosLat * Math.sin(longitude);
        double Z = ((((b * b) / (a * a)) * rSubN) + height) * sinLat;

        return new ENG_Vector3D(X, Y, Z);
    }

    /**
     * Converts lat long IN DEGREES and geodetic height (elevation) into DIS XYZ
     * This algorithm also uses the WGS84 ellipsoid, though you can change the values
     * of a and b for a different ellipsoid. Adapted from Military Handbook 600008
     *
     * @param latitude  The latitude, IN DEGREES
     * @param longitude The longitude, in DEGREES
     * @param height    The elevation, in meters
     * @return a double array with the calculated X, Y, and Z values, in that order
     */
    public static ENG_Vector3D getXYZfromLatLonDegrees(double latitude, double longitude, double height) {
        ENG_Vector3D degrees = getXYZfromLatLonRadians(latitude * DEGREES_TO_RADIANS,
                longitude * DEGREES_TO_RADIANS,
                height);

        return degrees;
    }
}
