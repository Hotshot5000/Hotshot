/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/9/22, 11:54 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.material;

import java.util.ArrayList;

@Deprecated
public abstract class ENG_AbstractSkinManager {

    private int skinNum;
    private int materialNum;
    private int textureNum;
    /** @noinspection deprecation*/
    private final ArrayList<ENG_Skin> skinList = new ArrayList<>();
    /** @noinspection deprecation*/
    private final ArrayList<ENG_Material> materialList = new ArrayList<>();
    /** @noinspection deprecation*/
    private final ArrayList<ENG_Texture> textureList = new ArrayList<>();

    /** @noinspection deprecation */
    public abstract int addSkin(ENG_Color diffuse, ENG_Color ambient,
                                ENG_Color specular, ENG_Color emissive, float power);

    /** @noinspection deprecation*/
    public abstract int addSkin(ENG_Material material);

    /** @noinspection deprecation*/
    public abstract void addTexture(int skinID, byte alpha, boolean _alpha, String name,
                                    ArrayList<ENG_Color> colorKeys, int colorKeysNum);

    /** @noinspection deprecation*/
    public abstract void addTexture(int skinID, ENG_Texture texture, boolean _alpha);

    /** @noinspection deprecation*/
    public abstract ENG_Skin getSkin(int skinID);

    /** @noinspection deprecation*/
    public abstract ENG_Material getMaterial(int matID);

    /** @noinspection deprecation*/
    public abstract ENG_Texture getTexture(int texID);

    public int getSkinNum() {
        return skinNum;
    }

    public void incrementSkinNum() {
        ++skinNum;
    }

    public int getMaterialNum() {
        return materialNum;
    }

    public void incrementMaterialNum() {
        ++materialNum;
    }

    public int getTextureNum() {
        return textureNum;
    }

    public void incrementTextureNum() {
        ++textureNum;
    }

    /** @noinspection deprecation*/
    public ArrayList<ENG_Skin> getSkinList() {
        return skinList;
    }

    /** @noinspection deprecation*/
    public ArrayList<ENG_Material> getMaterialList() {
        return materialList;
    }

    /** @noinspection deprecation*/
    public ArrayList<ENG_Texture> getTextureList() {
        return textureList;
    }
}
