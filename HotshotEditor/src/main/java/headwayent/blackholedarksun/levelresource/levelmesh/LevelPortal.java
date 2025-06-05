/*
 * Created by Sebastian Bugiu on 01/07/24, 18:03
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 01/07/24, 18:03
 * Copyright (c) 2024.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelmesh;

import headwayent.hotshotengine.ENG_Vector4D;

public class LevelPortal {

    public enum PortalType {
        PORTAL_TYPE_QUAD("quad"),
        PORTAL_TYPE_AABB("aabb"),
        PORTAL_TYPE_SPHERE("sphere");

        final String name;

        PortalType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static PortalType getType(String name) {
            if (name.equalsIgnoreCase(PORTAL_TYPE_QUAD.getName())) {
                return PORTAL_TYPE_QUAD;
            } else if (name.equalsIgnoreCase(PORTAL_TYPE_AABB.getName())) {
                return PORTAL_TYPE_AABB;
            } else if (name.equalsIgnoreCase(PORTAL_TYPE_SPHERE.getName())) {
                return PORTAL_TYPE_SPHERE;
            }
            throw new IllegalArgumentException(name + " is not a valid portal type");
        }
    }

    public String name;
    public ENG_Vector4D[] corners = new ENG_Vector4D[4];
    public PortalType portalType = PortalType.PORTAL_TYPE_QUAD;

    public LevelPortal() {
        for (int i = 0; i < corners.length; ++i) {
            corners[i] = new ENG_Vector4D(true);
        }
    }

    public String getName() {
        return name;
    }

    public ENG_Vector4D[] getCorners() {
        return corners;
    }

    public PortalType getPortalType() {
        return portalType;
    }
}
