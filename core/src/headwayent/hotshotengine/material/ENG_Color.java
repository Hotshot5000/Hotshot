/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/9/22, 11:54 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.material;

@Deprecated
public class ENG_Color {

    public byte r, g, b, a;

    public ENG_Color() {

    }

    /** @noinspection deprecation*/
    public ENG_Color(ENG_Color c) {
        this(c.r, c.g, c.b, c.a);
    }

    public ENG_Color(byte r, byte g, byte b, byte a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public ENG_Color(byte r, byte g, byte b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /** @noinspection deprecation */
    public static void copyColor(ENG_Color dest, ENG_Color src) {
        dest.r = src.r;
        dest.g = src.g;
        dest.b = src.b;
        dest.a = src.a;
    }

    /** @noinspection deprecation */
    public static boolean colorEqual(ENG_Color c0, ENG_Color c1) {
        return (c0.r == c1.r) &&
                (c0.g == c1.g) &&
                (c0.b == c1.b) &&
                (c0.a == c1.a);
    }

    /** @noinspection deprecation */
    public static boolean colorEqualWithoutAlpha(ENG_Color c0, ENG_Color c1) {
        return (c0.r == c1.r) &&
                (c0.g == c1.g) &&
                (c0.b == c1.b);
    }
}
