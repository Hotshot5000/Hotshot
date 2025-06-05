/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/19/18, 3:54 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import headwayent.hotshotengine.renderer.ENG_OverlayElement;


public class ENG_ButtonOverlayElement extends ENG_AbstractButton {

    private final ENG_OverlayElement overlayElement;
    private String originalMaterialName;
    private boolean pressed;
    private boolean materialChanged;

    public ENG_ButtonOverlayElement(
            ENG_OverlayElement overlay) {
        this.overlayElement = overlay;
        //	this.overlay = parentContainer;
    }

/*	public void show() {
		overlay.show();
	}
	
	public void hide() {
		overlay.hide();
	}*/

    public boolean isVisible() {
        return overlayElement.isVisible();
    }

    public boolean isEnabled() {
        return overlayElement.isEnabled();
    }

    public String getName() {
        return overlayElement.getName();
    }
	
/*	public ENG_OverlayElement getElement() {
		return overlayElement;
	}
	
	protected ENG_OverlayContainer getParentOverlayContainer() {
		return overlay;
	}*/

    public boolean isTouched(float x, float y) {
        return overlayElement.findElementAt(x, y) == overlayElement;
    }


    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        setPressed(pressed, true);
    }

    public void setPressed(boolean pressed, boolean updateMaterial) {
//        if (this.pressed != pressed) {
            this.pressed = pressed;
            if (pressed) {
                // Ugly hack because we shouldn't be changing the material here.
                // It probably should be in a listener.
                // In here we must make sure that we don't end up adding _pressed_pressed
                // to the material and crash everything.
                if (updateMaterial) {
                    // We must guard to not set the same material multiple times.
                    if (!materialChanged) {
                        originalMaterialName = overlayElement.getMaterialName();
                        overlayElement.setMaterialName(originalMaterialName + "_pressed");
                        materialChanged = true;
                    }
                }
            } else {
                if (updateMaterial) {
                    if (materialChanged) {
                        overlayElement.setMaterialName(originalMaterialName);
                        materialChanged = false;
                    }
                }
            }
//            System.out.println("Setting overlayElement materialName: " + overlayElement.getMaterialName());
//        }
//        overlayElement.getMaterial()
//                .getTechnique((short) 0).getPass((short) 0)
//                .getTextureUnitState((short) 0).setCurrentFrame(pressed ? 1 : 0);
    }

    public ENG_OverlayElement getOverlayElement() {
        return overlayElement;
    }
}
