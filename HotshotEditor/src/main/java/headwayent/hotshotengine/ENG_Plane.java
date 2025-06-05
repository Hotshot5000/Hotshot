package headwayent.hotshotengine;

public class ENG_Plane {

    public enum Side {NO_SIDE, POSITIVE_SIDE, NEGATIVE_SIDE, BOTH_SIDE}

    public final ENG_Vector4D normal = new ENG_Vector4D();
    public double d;

    private final ENG_Vector4D temp0 = new ENG_Vector4D(true);
    private final ENG_Vector4D temp1 = new ENG_Vector4D(true);

    public ENG_Plane() {

    }

    public ENG_Plane(ENG_Plane p) {
        normal.set(p.normal);
        d = p.d;
    }

    public ENG_Plane(ENG_Vector3D normal, ENG_Vector3D point) {
        redefine(normal, point);
    }

    public ENG_Plane(ENG_Vector4D normal, ENG_Vector4D point) {
        redefine(normal, point);
    }

    public ENG_Plane(ENG_Vector3D normal, ENG_Vector3D point0, ENG_Vector3D point1) {
        redefine(normal, point0, point1);
    }

    public ENG_Plane(ENG_Vector4D normal, ENG_Vector4D point0, ENG_Vector4D point1) {
        redefine(normal, point0, point1);
    }

    public ENG_Plane(ENG_Vector3D v, double c) {
        normal.set(v);
        d = -c;
    }

    public ENG_Plane(ENG_Vector4D v, double c) {
        normal.set(v);
        d = -c;
    }

    public ENG_Plane(double a, double b, double c, double _d) {
        normal.set(a, b, c, 0.0f);
        d = _d;
    }

    public void set(ENG_Plane p) {
        normal.set(p.normal);
        d = p.d;
    }

    public void set(ENG_Vector3D v, double c) {
        normal.set(v);
        d = -c;
    }

    public void set(ENG_Vector4D v, double c) {
        normal.set(v);
        d = -c;
    }

    public void set(double a, double b, double c, double _d) {
        normal.set(a, b, c, 0.0f);
        d = _d;
    }

    public double getDistance(ENG_Vector3D v) {
        return (normal.dotProduct(v) + d);
    }

    public double getDistance(ENG_Vector4D v) {
        return (normal.dotProduct(v) + d);
    }

    public Side getSide(ENG_Vector3D v) {
        double dist = getDistance(v);
        if (dist < 0.0f) {
            return Side.NEGATIVE_SIDE;
        }
        if (dist > 0.0f) {
            return Side.POSITIVE_SIDE;
        }
        return Side.NO_SIDE;
    }

    public Side getSide(ENG_Vector4D v) {
        double dist = getDistance(v);
        if (dist < 0.0f) {
            return Side.NEGATIVE_SIDE;
        }
        if (dist > 0.0f) {
            return Side.POSITIVE_SIDE;
        }
        return Side.NO_SIDE;
    }

    public Side getSide(ENG_Vector3D center, ENG_Vector3D halfSize) {
        double dist = getDistance(center);
        double maxAbsDist = normal.absDotProduct(halfSize);
        if (dist < -maxAbsDist) {
            return Side.NEGATIVE_SIDE;
        }
        if (dist > maxAbsDist) {
            return Side.POSITIVE_SIDE;
        }
        return Side.BOTH_SIDE;
    }

    public Side getSide(ENG_Vector4D center, ENG_Vector4D halfSize) {
        double dist = getDistance(center);
        double maxAbsDist = normal.absDotProduct(halfSize);
        if (dist < -maxAbsDist) {
            return Side.NEGATIVE_SIDE;
        }
        if (dist > maxAbsDist) {
            return Side.POSITIVE_SIDE;
        }
        return Side.BOTH_SIDE;
    }

    public Side getSide(ENG_AxisAlignedBox a) {
        if (a.isNull()) {
            return Side.NO_SIDE;
        }
        if (a.isInfinite()) {
            return Side.BOTH_SIDE;
        }
        a.getCenter(temp0);
        a.getHalfSize(temp1);
        return getSide(temp0, temp1);
    }

    public void redefine(ENG_Vector3D pt0, ENG_Vector3D pt1, ENG_Vector3D pt2,
                         ENG_Vector3D temp0, ENG_Vector3D temp1) {
        pt1.sub(pt0, temp0);
        pt2.sub(pt0, temp1);
        temp0.crossProduct(temp1, new ENG_Vector3D(normal.x, normal.y, normal.z));
        normal.normalize();
        d = -normal.dotProduct(pt0);
    }

    public void redefine(ENG_Vector4D pt0, ENG_Vector4D pt1, ENG_Vector4D pt2,
                         ENG_Vector4D temp0, ENG_Vector4D temp1) {
        pt1.sub(pt0, temp0);
        pt2.sub(pt0, temp1);
        temp0.crossProduct(temp1, normal);
        normal.normalize();
        d = -normal.dotProduct(pt0);
    }

    public void redefine(ENG_Vector3D pt0, ENG_Vector3D pt1, ENG_Vector3D pt2) {
        ENG_Vector3D temp0 = new ENG_Vector3D();
        ENG_Vector3D temp1 = new ENG_Vector3D();
        redefine(pt0, pt1, pt2, temp0, temp1);
    }

    public void redefine(ENG_Vector4D pt0, ENG_Vector4D pt1, ENG_Vector4D pt2) {
        ENG_Vector4D temp0 = new ENG_Vector4D();
        ENG_Vector4D temp1 = new ENG_Vector4D();
        redefine(pt0, pt1, pt2, temp0, temp1);
    }

    public void redefine(ENG_Vector3D rkNormal, ENG_Vector3D rkPoint) {
        normal.set(rkNormal);
        d = -rkNormal.dotProduct(rkPoint);
    }

    public void redefine(ENG_Vector4D rkNormal, ENG_Vector4D rkPoint) {
        normal.set(rkNormal);
        d = -rkNormal.dotProduct(rkPoint);
    }

    public void projectVector(ENG_Vector3D p, ENG_Matrix3 mat, ENG_Vector3D ret) {
        double[] m = mat.get();

        m[0] = 1.0f - normal.x * normal.x;
        m[1] = -normal.x * normal.y;
        m[2] = -normal.x * normal.z;
        m[3] = -normal.y * normal.x;
        m[4] = 1.0f - normal.y * normal.y;
        m[5] = -normal.y * normal.z;
        m[6] = -normal.z * normal.x;
        m[7] = -normal.z * normal.y;
        m[8] = 1.0f - normal.z * normal.z;
        mat.transform(p, ret);
    }

    public void projectVector(ENG_Vector4D p, ENG_Matrix3 mat, ENG_Vector4D ret) {
        double[] m = mat.get();

        m[0] = 1.0f - normal.x * normal.x;
        m[1] = -normal.x * normal.y;
        m[2] = -normal.x * normal.z;
        m[3] = -normal.y * normal.x;
        m[4] = 1.0f - normal.y * normal.y;
        m[5] = -normal.y * normal.z;
        m[6] = -normal.z * normal.x;
        m[7] = -normal.z * normal.y;
        m[8] = 1.0f - normal.z * normal.z;
        mat.transform(p, ret);
    }

    public void normalize() {
        double len = normal.length();
        if (len > 0.0f) {
            double invLen = 1.0f / len;
            normal.mul(invLen);
            d *= invLen;
        }
    }

    public boolean equals(ENG_Plane p) {
        return (normal.equals(p.normal) && (d == p.d));
    }

    public boolean notEquals(ENG_Plane p) {
        return (normal.notEquals(p.normal) || (d != p.d));
    }

    public String toString() {
        return ("Plane(normal: " + normal.toString() + " d: " + Double.toString(d) + ")");
    }
}
