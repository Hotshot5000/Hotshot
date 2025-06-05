/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.world;

import java.util.HashMap;

import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_Light;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 13-Feb-18.
 */

public class LightingManager {

    private static final LightingManager mgr = new LightingManager();
    private final HashMap<String, Light> lightMap = new HashMap<>();
    private final AmbientLight currentAmbientLight = new AmbientLight();

    public static class AmbientLight {
        public final ENG_ColorValue upperHemisphere = new ENG_ColorValue();
        public final ENG_ColorValue lowerHemisphere = new ENG_ColorValue();
        public final ENG_Vector3D hemisphereDir = new ENG_Vector3D();
    }

    private LightingManager() {

    }

    public Light createLight(String lightName, String lightNodeName) {
        Light light = new Light(lightName, lightNodeName);
        Light put = lightMap.put(lightName, light);
        if (put != null) {
            throw new IllegalArgumentException(lightName + " node: " + lightNodeName + " already exist int light map");
        }
        return light;
    }

    public void destroyLight(String lightName) {
        Light light = lightMap.remove(lightName);
        if (light == null) {
            throw new IllegalArgumentException(lightName + " does not exist in light map");
        }
        light.destroy();
    }

    public void destroyAllLights() {
        for (Light light : lightMap.values()) {
            light.destroy();
        }
        lightMap.clear();

    }

    /** @noinspection deprecation*/
    public Light createDirectionalLight(String lightName, String lightNodeName,
                                        float powerScale, ENG_ColorValue diffuseColor, ENG_ColorValue specularColor, ENG_Vector4D lightDir) {
        Light light = createLight(lightName, lightNodeName);
        light.getLight().setPowerScale(powerScale);
        light.getLight().setType(ENG_Light.LightTypes.LT_DIRECTIONAL);
        light.getLight().setDiffuseColour( specularColor );
        light.getLight().setSpecularColour( specularColor);
        light.getLight().setDirection(lightDir.normalizedCopy());
        return light;
    }

    /** @noinspection deprecation*/
    public Light createPointLight(String lightName, String lightNodeName,
                                  float powerScale, ENG_ColorValue diffuseColor, ENG_ColorValue specularColor, ENG_Vector4D lightPos) {
        Light light = createLight(lightName, lightNodeName);
        light.getLight().setPowerScale(powerScale);
        light.getLight().setType(ENG_Light.LightTypes.LT_POINT);
        light.getLight().setDiffuseColour( specularColor );
        light.getLight().setSpecularColour( specularColor);
        light.getLightNode().setPosition(lightPos);
        return light;
    }

    /** @noinspection deprecation*/
    public Light createSpotLight(String lightName, String lightNodeName,
                                 float powerScale, ENG_ColorValue diffuseColor, ENG_ColorValue specularColor, ENG_Vector4D lightPos, ENG_Vector4D lightDir) {
        Light light = createLight(lightName, lightNodeName);
        light.getLight().setPowerScale(powerScale);
        light.getLight().setType(ENG_Light.LightTypes.LT_DIRECTIONAL);
        light.getLight().setDiffuseColour( specularColor );
        light.getLight().setSpecularColour( specularColor);
        light.getLightNode().setPosition(lightPos);
        light.getLight().setDirection(lightDir.normalizedCopy());
        return light;
    }

    public void setAmbientLight(ENG_ColorValue upperHemisphere, ENG_ColorValue lowerHemisphere,
                                ENG_Vector3D hemisphereDir) {
        ENG_NativeCalls.sceneManager_setAmbientLight(upperHemisphere, lowerHemisphere, hemisphereDir);
        currentAmbientLight.upperHemisphere.set(upperHemisphere);
        currentAmbientLight.lowerHemisphere.set(lowerHemisphere);
        currentAmbientLight.hemisphereDir.set(hemisphereDir);
    }

    public AmbientLight getCurrentAmbientLight() {
        return currentAmbientLight;
    }

    public static LightingManager getSingleton() {
        return mgr;
    }
}
