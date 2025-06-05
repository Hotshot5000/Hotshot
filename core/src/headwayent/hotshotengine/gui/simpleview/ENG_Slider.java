/*
 * Created by Sebastian Bugiu on 6/20/23, 10:25 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/20/23, 10:25 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.gorillagui.ENG_Layer;
import headwayent.hotshotengine.gorillagui.ENG_Rectangle;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class ENG_Slider extends ENG_View {

    public static class SliderFactory extends ENG_Container.ViewFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_View createView(String name, ENG_Layer layer,
                                   ENG_Container parent, Bundle bundle, ENG_View parentView) {

            ENG_Slider slider = new ENG_Slider(name, layer, parent, bundle, parentView);
            slider.setViewType(ENG_Container.ViewType.VIEW_SLIDER);
            return slider;
        }

        @Override
        public void destroyView(ENG_View view) {

            view.destroy();
        }

    }

    public enum SliderType {
        HORIZONTAL, VERTICAL;

        public static SliderType getSliderType(String type) {
            if (type.equalsIgnoreCase("horizontal")) {
                return HORIZONTAL;
            } else if (type.equalsIgnoreCase("vertical")) {
                return VERTICAL;
            }
            throw new IllegalArgumentException(type + " is not a valid SliderType");
        }
    }

    public enum SliderValueType {
        INT, FLOAT;

        public static SliderValueType getSliderValueType(String type) {
            if (type.equalsIgnoreCase("int")) {
                return INT;
            } else if (type.equalsIgnoreCase("float")) {
                return FLOAT;
            }
            throw new IllegalArgumentException(type + " is not a valid SliderValueType");
        }
    }

    private class SliderClickListener implements OnClickListener {

        @Override
        public boolean onClick(int x, int y) {
            if (boxRect.inside(x, y)) {
                float oldSliderFinalPercentage = sliderFinalPercentage;
                // Get the precise position as slider percentage and then
                // snap to the closest "grid" position based on itemStep.
                switch (sliderType) {
                    case HORIZONTAL: {
                        float width = boxRect.width();
                        float xDelta = x - boxRect.left;
                        currentPercentage = ENG_Math.clamp(xDelta / width, 0.0f, 1.0f);
                    }
                        break;
                    case VERTICAL: {
                        float height = boxRect.height();
                        float yDelta = boxRect.bottom - y;
                        currentPercentage = ENG_Math.clamp(yDelta / height, 0.0f, 1.0f);
                    }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + sliderType);
                }
                calculateSliderPosition();
                // Only update if something changed to the slider.
                if (!ENG_Float.isEqual(oldSliderFinalPercentage, sliderFinalPercentage)) {
                    markDirty();
                }
                setClicked(true);
                return true;
            }
            setClicked(false);
            return false;
        }
    }

    public static final float SLIDER_SIZE_WIDTH = 100.0f;
    public static final float SLIDER_SIZE_HEIGHT = 100.0f;
    public static final String BUNDLE_SLIDER_TYPE = "slider_type";
    public static final String BUNDLE_ITEM_STEP = "item_step";
    public static final String BUNDLE_MIN_VALUE = "min_value";
    public static final String BUNDLE_MAX_VALUE = "max_value";
    public static final String BUNDLE_VALUE_TYPE = "value_type";
    public static final String BUNDLE_VALUE_TYPE_INT = "value_type_int";
    public static final String BUNDLE_VALUE_TYPE_FLOAT = "value_type_float";
    private SliderType sliderType = SliderType.HORIZONTAL;
    private SliderValueType sliderValueType = SliderValueType.INT;
    private final ENG_Rectangle boxRectangle;
    private final ENG_Rectangle sliderRectangle;
    private final ENG_RealRect boxRect = new ENG_RealRect();
    private final ENG_RealRect sliderRect = new ENG_RealRect();
    private float currentSliderWidth = SLIDER_SIZE_WIDTH;
    private float currentSliderHeight = SLIDER_SIZE_HEIGHT;
    private int minValueInt;
    private int maxValueInt;
    private int itemStepInt;
    private int currentValueInt;
    private float minValueFloat;
    private float maxValueFloat;
    private float itemStepFloat;
    private float currentValueFloat;
    private float currentPercentage;
    private float sliderFinalPercentage;
    private float stepSize;
    private boolean clicked;

    /** @noinspection deprecation*/
    public ENG_Slider(String name, ENG_Layer layer, ENG_Container parent, Bundle bundle, ENG_View parentView) {
        super(name, layer, parent, parentView);

        String sliderTypeString = bundle.getString(BUNDLE_SLIDER_TYPE);
        if (sliderTypeString != null) {
            sliderType = SliderType.getSliderType(sliderTypeString);
        }
        String valueTypeString = bundle.getString(BUNDLE_VALUE_TYPE);
        if (valueTypeString != null) {
            sliderValueType = SliderValueType.getSliderValueType(valueTypeString);
        }

        switch (sliderValueType) {
            case INT: {
                minValueInt = bundle.getInt(BUNDLE_MIN_VALUE, -1);
                maxValueInt = bundle.getInt(BUNDLE_MAX_VALUE, -1);
                itemStepInt = bundle.getInt(BUNDLE_ITEM_STEP, -1);
                if (minValueInt == -1) {
                    throw new IllegalArgumentException("Missing " + BUNDLE_MIN_VALUE);
                }
                if (maxValueInt == -1) {
                    throw new IllegalArgumentException("Missing " + BUNDLE_MAX_VALUE);
                }
                if (itemStepInt == -1) {
                    throw new IllegalArgumentException("Missing " + BUNDLE_ITEM_STEP);
                }
                if (itemStepInt <= 0) {
                    throw new IllegalArgumentException("itemStep should be > 0");
                }
            }
                break;
            case FLOAT: {
                minValueFloat = bundle.getFloat(BUNDLE_MIN_VALUE, -1.0f);
                maxValueFloat = bundle.getFloat(BUNDLE_MAX_VALUE, -1.0f);
                itemStepFloat = bundle.getFloat(BUNDLE_ITEM_STEP, -1.0f);
                if (minValueFloat == -1.0f) {
                    throw new IllegalArgumentException("Missing " + BUNDLE_MIN_VALUE);
                }
                if (maxValueFloat == -1.0f) {
                    throw new IllegalArgumentException("Missing " + BUNDLE_MAX_VALUE);
                }
                if (itemStepFloat == -1.0f) {
                    throw new IllegalArgumentException("Missing " + BUNDLE_ITEM_STEP);
                }
                if (itemStepFloat <= 0.0f) {
                    throw new IllegalArgumentException("itemStep should be > 0");
                }
            }
                break;
            default:
                throw new IllegalStateException(sliderValueType + " is not valid SliderValueType");
        }

        boxRectangle = layer.createRectangle(0, 0, 0, 0);
        sliderRectangle = layer.createRectangle(0, 0, 0, 0, 1);

        boxRectangle.backgroundColour(ENG_ColorValue.BLACK);
        boxRectangle.border(1.0f, ENG_ColorValue.WHITE);

        sliderRectangle.backgroundColour(ENG_ColorValue.GREEN);

        removeAllEventListeners();
        SliderClickListener clickListener = new SliderClickListener();
        setOnClickListener(clickListener);
    }

    @Override
    public void destroy() {
        ENG_Layer layer = getLayer();
        layer.destroyRectangle(boxRectangle);
        layer.destroyRectangle(sliderRectangle);

        super.destroy();
    }

    @Override
    public void update(int screenWidth, int screenHeight) {
        super.update(screenWidth, screenHeight);

        float left = getActLeft();
        float top = getActTop();
        float right = getActRight();
        float bottom = getActBottom();
        float width = right - left;
        float height = bottom - top;

        boxRectangle.left(left);
        boxRectangle.top(top);
        boxRectangle.width(width);
        boxRectangle.height(height);

        boxRect.set(left, top, right, bottom);

        float finalPos = sliderFinalPercentage * stepSize;

        switch (sliderType) {
            case HORIZONTAL: {
                sliderRectangle.left(left + finalPos * width - currentSliderWidth * 0.5f);
                sliderRectangle.top(top + height * 0.5f - currentSliderHeight * 0.5f);
            }
                break;
            case VERTICAL: {
                sliderRectangle.left(left + width * 0.5f - currentSliderWidth * 0.5f);
                sliderRectangle.top(bottom - finalPos * height - currentSliderHeight * 0.5f);
            }
                break;
            default:
                // Should never get here!
                throw new IllegalStateException(sliderType + " not supported sliderType");
        }
        sliderRectangle.width(currentSliderWidth);
        sliderRectangle.height(currentSliderHeight);
    }

    private void calculateSliderPosition() {
        switch (sliderValueType) {
            case INT: {
                int delta = maxValueInt - minValueInt;
                int stepCount = delta / itemStepInt;
                stepSize = 1.0f / stepCount;
                sliderFinalPercentage = Math.round(currentPercentage / stepSize);
                currentValueInt = minValueInt + (int) sliderFinalPercentage;
                System.out.println("currentValueInt: " + currentValueInt);
            }
                break;
            case FLOAT: {
                float delta = maxValueFloat - minValueFloat;
                float stepCount = delta / itemStepFloat;
                stepSize = 1.0f / stepCount;
                sliderFinalPercentage = Math.round(currentPercentage / stepSize);
                currentValueFloat = minValueFloat + sliderFinalPercentage;
                System.out.println("currentValueFloat: " + currentValueFloat);
            }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sliderValueType);
        }
    }

    private void setClicked(boolean clicked) {
        if (this.clicked != clicked) {
            this.clicked = clicked;
            markDirty();
        }
    }

    public boolean isClicked() {
        return clicked;
    }

    private void checkValueLimits() {
        switch (sliderValueType) {
            case INT:
                currentValueInt = ENG_Math.clamp(currentValueInt, minValueInt, maxValueInt);
                break;
            case FLOAT:
                currentValueFloat = ENG_Math.clamp(currentValueFloat, minValueFloat, maxValueFloat);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + sliderValueType);
        }
    }

    public void setSliderValue(Object value) {
        switch (sliderValueType) {
            case INT:
                if (value instanceof Integer || value instanceof Long) {
                    currentValueInt = (int) value;
                } else {
                    throw new IllegalArgumentException(value + " must be an Integer or Long");
                }
                break;
            case FLOAT:
                if (value instanceof Float || value instanceof Double) {
                    currentValueFloat = (float) value;
                } else {
                    throw new IllegalArgumentException(value + " must be a Float or Double");
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + sliderValueType);
        }
        checkValueLimits();
        // Convert to 0.0f..1.0f percentage.
        switch (sliderValueType) {
            case INT: {
                float delta = maxValueInt - minValueInt;
                currentPercentage = ((float) (currentValueInt - minValueInt)) / delta;
            }
                break;
            case FLOAT: {
                float delta = maxValueFloat - minValueFloat;
                currentPercentage = (currentValueFloat - minValueFloat) / delta;
            }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sliderValueType);
        }
        currentPercentage = ENG_Math.clamp(currentPercentage, 0.0f, 1.0f);
        calculateSliderPosition();
        markDirty();
    }

    public Object getCurrentValue() {
        switch (sliderValueType) {
            case INT:
                return currentValueInt;
            case FLOAT:
                return currentValueFloat;
            default:
                throw new IllegalStateException("Unexpected value: " + sliderValueType);
        }
    }

    public Object getMinValue() {
        switch (sliderValueType) {
            case INT:
                return minValueInt;
            case FLOAT:
                return minValueFloat;
            default:
                throw new IllegalStateException("Unexpected value: " + sliderValueType);
        }
    }

    public Object getMaxValue() {
        switch (sliderValueType) {
            case INT:
                return maxValueInt;
            case FLOAT:
                return maxValueFloat;
            default:
                throw new IllegalStateException("Unexpected value: " + sliderValueType);
        }
    }

    public Object getItemStep() {
        switch (sliderValueType) {
            case INT:
                return itemStepInt;
            case FLOAT:
                return itemStepFloat;
            default:
                throw new IllegalStateException("Unexpected value: " + sliderValueType);
        }
    }

    public SliderType getSliderType() {
        return sliderType;
    }

    public SliderValueType getSliderValueType() {
        return sliderValueType;
    }

    public float getCurrentSliderWidth() {
        return currentSliderWidth;
    }

    public void setCurrentSliderWidth(float currentSliderWidth) {
        this.currentSliderWidth = currentSliderWidth;
    }

    public float getCurrentSliderHeight() {
        return currentSliderHeight;
    }

    public void setCurrentSliderHeight(float currentSliderHeight) {
        this.currentSliderHeight = currentSliderHeight;
    }
}
