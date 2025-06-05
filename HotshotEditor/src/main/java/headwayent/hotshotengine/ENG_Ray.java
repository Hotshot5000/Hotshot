package headwayent.hotshotengine;

import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Double;

public class ENG_Ray {

    public final ENG_Vector4D origin = new ENG_Vector4D(true);
    public final ENG_Vector4D dir = new ENG_Vector4D();

    private final ENG_Vector4D temp0 = new ENG_Vector4D(true);

    public ENG_Ray() {
        origin.set(ENG_Math.PT4_ZERO);
        dir.set(ENG_Math.VEC4_Z_UNIT);
    }

    public ENG_Ray(ENG_Vector4D origin, ENG_Vector4D dir) {
        set(origin, dir);
    }

    public ENG_Vector4D getOrigin() {
        return origin;
    }

    public ENG_Vector4D getDir() {
        return dir;
    }

    public void set(ENG_Vector4D origin, ENG_Vector4D dir) {
        this.origin.set(origin);
        this.dir.set(dir);
    }

    public void getPoint(float t, ENG_Vector4D ret) {
        dir.mul(t, ret);
        ret.addInPlace(origin);
    }

    public void set(ENG_Ray ray) {
        origin.set(ray.origin);
        dir.set(ray.dir);
    }

    public void intersects(ENG_Plane p, ENG_Boolean b, ENG_Double dist) {
        ENG_Math.intersects(p, this, b, dist);
    }

    public void intersects(ENG_Sphere s, ENG_Boolean b, ENG_Double dist) {
        ENG_Math.intersects(this, s, b, dist, true, temp0);
    }

    public void intersects(ENG_AxisAlignedBox a, ENG_Boolean b, ENG_Double dist) {
        ENG_Math.intersects(this, a, b, dist, temp0);
    }
}
