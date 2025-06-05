/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/9/22, 11:54 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.material;

@Deprecated
public class ENG_Material {

    /** @noinspection deprecation */
    public final ENG_Color diffuse = new ENG_Color();
    /** @noinspection deprecation */
    public final ENG_Color ambient = new ENG_Color();
    /** @noinspection deprecation */
    public final ENG_Color specular = new ENG_Color();
    /** @noinspection deprecation */
    public final ENG_Color emissive = new ENG_Color();
    public float power;

    public ENG_Material() {

    }

    /** @noinspection deprecation*/
    public ENG_Material(ENG_Material m) {
        this(m.diffuse, m.ambient, m.specular, m.emissive, m.power);
    }

    /** @noinspection deprecation */
    public ENG_Material(ENG_Color diffuse, ENG_Color ambient, ENG_Color specular,
                        ENG_Color emissive, float power) {
        ENG_Color.copyColor(this.diffuse, diffuse);
        ENG_Color.copyColor(this.ambient, ambient);
        ENG_Color.copyColor(this.specular, specular);
        ENG_Color.copyColor(this.emissive, emissive);
        this.power = power;
    }

    /** @noinspection deprecation */
    public static void copyMaterial(ENG_Material dest, ENG_Material src) {
        ENG_Color.copyColor(dest.diffuse, src.diffuse);
        ENG_Color.copyColor(dest.ambient, src.ambient);
        ENG_Color.copyColor(dest.specular, src.specular);
        ENG_Color.copyColor(dest.emissive, src.emissive);
        dest.power = src.power;
    }

    /** @noinspection deprecation */
    public static boolean materialEqual(ENG_Material m0, ENG_Material m1) {
        return ENG_Color.colorEqual(m0.diffuse, m1.diffuse) &&
                ENG_Color.colorEqual(m0.ambient, m1.ambient) &&
                ENG_Color.colorEqual(m0.specular, m1.specular) &&
                ENG_Color.colorEqual(m0.emissive, m1.emissive) &&
                (m0.power == m1.power);
    }
}
