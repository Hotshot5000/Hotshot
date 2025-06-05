/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public class ENG_Sphere {

    public final ENG_Vector4D center = new ENG_Vector4D(true);
    public float radius;

    private final ENG_Vector4D temp = new ENG_Vector4D(true);

    public ENG_Sphere() {

    }

    @SuppressWarnings("CopyConstructorMissesField")
    public ENG_Sphere(ENG_Sphere sphere) {
        set(sphere);
    }

    public void set(ENG_Sphere sphere) {
        set(sphere.center, sphere.radius);
    }

    public void set(ENG_Vector4D vec, float radius) {
        center.set(vec);
        this.radius = radius;
    }

    public void set(ENG_Vector3D vec, float radius) {
        center.set(vec);
        this.radius = radius;
    }

    public ENG_Sphere(ENG_Vector4D vec, float radius) {
        center.set(vec);
        this.radius = radius;
    }

    public ENG_Sphere(ENG_Vector3D vec, float radius) {
        center.set(vec);
        this.radius = radius;
    }

    public ENG_Sphere(float x, float y, float z, float radius) {
        center.set(x, y, z);
        this.radius = radius;
    }

    public boolean intersects(ENG_Sphere s, ENG_Vector4D temp) {
        temp.set(s.center);
        temp.subInPlace(center);
        return (temp.squaredLength() <= ENG_Math.sqr(s.radius + radius));

    }

    public boolean intersects(ENG_Vector4D v, ENG_Vector4D temp) {
        temp.set(v);
        temp.subInPlace(center);
        return (temp.squaredLength() <= (radius * radius));
    }

    public boolean intersects(ENG_Vector3D v, ENG_Vector4D temp) {
        temp.set(v);
        temp.subInPlace(center);
        return (temp.squaredLength() <= (radius * radius));
    }

    public boolean intersects(ENG_Plane p) {
        return ENG_Math.intersects(this, p);
    }

    public boolean intersects(ENG_AxisAlignedBox a) {
        return ENG_Math.intersects(this, a);
    }

    public boolean intersects(ENG_Vector4D v) {
        v.sub(center, temp);
        return (temp.squaredLength() <= ENG_Math.sqr(radius));
    }

    public void setCenter(ENG_Vector4D transformAffineRet) {
        
        center.set(transformAffineRet);
    }

    public void setRadius(float max) {
        
        radius = max;
    }
}
