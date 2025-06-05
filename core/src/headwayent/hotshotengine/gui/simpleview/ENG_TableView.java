/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.blackholedarksun.APP_Game;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.gui.simpleview.ENG_Container.ViewFactory;
import headwayent.hotshotengine.gorillagui.ENG_Layer;
import headwayent.hotshotengine.gorillagui.ENG_MarkupText;
import headwayent.hotshotengine.gorillagui.ENG_Rectangle;
import headwayent.hotshotengine.gorillagui.ENG_Sprite;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class ENG_TableView extends ENG_View {

    private static final ENG_ColorValue SCROLL_ARROWS_COLOR_DEFAULT = ENG_ColorValue.GREEN;

    private static final int TEXTFIELD_NUM_IN_TABLE_DEFAULT = 1;

    private static final float TEXTFIELD_HEIGHT_DEFAULT = 10.0f;

    public static final String BUNDLE_COLUMN_NAME_HEIGHT = "column_name_height";

    public static final String BUNDLE_COLUMN_NAME_LIST = "column_name_list";

    public static final String BUNDLE_COLUMN_COUNT = "column_count";
    private static final int TOP_LAYER_DEPTH = 10;
    private int maxVisibleRowsPerTable;
    private int textFieldMaxVisibleCount;

    public static class TableViewFactory extends ViewFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_View createView(String name, ENG_Layer layer,
                                   ENG_Container parent, Bundle bundle, ENG_View parentView) {

            ENG_TableView tableView = new ENG_TableView(name, layer, parent, bundle, parentView);
            tableView.setViewType(ENG_Container.ViewType.VIEW_TABLEVIEW);
            return tableView;
        }

        @Override
        public void destroyView(ENG_View view) {
            
            view.destroy();
        }

    }

    public enum OverwriteAllowType {
        OVERWRITABLE, NON_OVERWRITABLE
    }

    public enum OverwriteType {
        NORMAL, FORCED
    }

    private static class AllowableOverwriteRowParameters {

        public OverwriteAllowType textSize;
        public OverwriteAllowType visible;
        public OverwriteAllowType cursorWidth;
        public OverwriteAllowType cursorHeight;
        public OverwriteAllowType cursorBlinkTime;
        public OverwriteAllowType cursorBlinking;
        public OverwriteAllowType keyCodeDelay;
        public OverwriteAllowType keyCodeShortDelay;
        public OverwriteAllowType boxRectangleBackgroundColor;
        public OverwriteAllowType cursorRectangleBackgroundColor;
        public OverwriteAllowType boxRectangleBorderWidth;
        public OverwriteAllowType boxRectangleBorderColor;
        public OverwriteAllowType cursorRectangleBorderWidth;
        public OverwriteAllowType cursorRectangleBorderColor;
        public OverwriteAllowType horizontalAlignment;
        public OverwriteAllowType verticalAlignment;

        public AllowableOverwriteRowParameters() {
            setAll(OverwriteAllowType.OVERWRITABLE);
        }

        public void setAll(OverwriteAllowType type) {
            textSize = type;
            visible = type;
            cursorWidth = type;
            cursorHeight = type;
            cursorBlinkTime = type;
            cursorBlinking = type;
            keyCodeDelay = type;
            keyCodeShortDelay = type;
            boxRectangleBackgroundColor = type;
            cursorRectangleBackgroundColor = type;
            boxRectangleBorderWidth = type;
            boxRectangleBorderColor = type;
            cursorRectangleBorderWidth = type;
            cursorRectangleBorderColor = type;
            horizontalAlignment = type;
            verticalAlignment = type;
        }

        public void set(AllowableOverwriteRowParameters a) {
            textSize = a.textSize;
            visible = a.visible;
            cursorWidth = a.cursorWidth;
            cursorHeight = a.cursorHeight;
            cursorBlinkTime = a.cursorBlinkTime;
            cursorBlinking = a.cursorBlinking;
            keyCodeDelay = a.keyCodeDelay;
            keyCodeShortDelay = a.keyCodeShortDelay;
            boxRectangleBackgroundColor = a.boxRectangleBackgroundColor;
            cursorRectangleBackgroundColor = a.cursorRectangleBackgroundColor;
            boxRectangleBorderWidth = a.boxRectangleBorderWidth;
            boxRectangleBorderColor = a.boxRectangleBorderColor;
            cursorRectangleBorderWidth = a.cursorRectangleBorderWidth;
            cursorRectangleBorderColor = a.cursorRectangleBorderColor;
            horizontalAlignment = a.horizontalAlignment;
            verticalAlignment = a.verticalAlignment;
        }
    }

    private static class OverwriteRowParameters {

        public OverwriteType textSize;
        public OverwriteType visible;
        public OverwriteType cursorWidth;
        public OverwriteType cursorHeight;
        public OverwriteType cursorBlinkTime;
        public OverwriteType cursorBlinking;
        public OverwriteType keyCodeDelay;
        public OverwriteType keyCodeShortDelay;
        public OverwriteType boxRectangleBackgroundColor;
        public OverwriteType cursorRectangleBackgroundColor;
        public OverwriteType boxRectangleBorderWidth;
        public OverwriteType boxRectangleBorderColor;
        public OverwriteType cursorRectangleBorderWidth;
        public OverwriteType cursorRectangleBorderColor;
        public OverwriteType horizontalAlignment;
        public OverwriteType verticalAlignment;

        public OverwriteRowParameters() {
            setAll(OverwriteType.NORMAL);
        }

        public void setAll(OverwriteType type) {
            textSize = type;
            visible = type;
            cursorWidth = type;
            cursorHeight = type;
            cursorBlinkTime = type;
            cursorBlinking = type;
            keyCodeDelay = type;
            keyCodeShortDelay = type;
            boxRectangleBackgroundColor = type;
            cursorRectangleBackgroundColor = type;
            boxRectangleBorderWidth = type;
            boxRectangleBorderColor = type;
            cursorRectangleBorderWidth = type;
            cursorRectangleBorderColor = type;
            horizontalAlignment = type;
            verticalAlignment = type;
        }

        public void set(OverwriteRowParameters o) {
            textSize = o.textSize;
            visible = o.visible;
            cursorWidth = o.cursorWidth;
            cursorHeight = o.cursorHeight;
            cursorBlinkTime = o.cursorBlinkTime;
            cursorBlinking = o.cursorBlinking;
            keyCodeDelay = o.keyCodeDelay;
            keyCodeShortDelay = o.keyCodeShortDelay;
            boxRectangleBackgroundColor = o.boxRectangleBackgroundColor;
            cursorRectangleBackgroundColor = o.cursorRectangleBackgroundColor;
            boxRectangleBorderWidth = o.boxRectangleBorderWidth;
            boxRectangleBorderColor = o.boxRectangleBorderColor;
            cursorRectangleBorderWidth = o.cursorRectangleBorderWidth;
            cursorRectangleBorderColor = o.cursorRectangleBorderColor;
            horizontalAlignment = o.horizontalAlignment;
            verticalAlignment = o.verticalAlignment;
        }
    }

    public static class RowParameters {

        public static final int CURSOR_NOT_BLINKING = -1;

        public int textSize;
        public boolean visible;
        public float cursorWidth;
        public float cursorHeight;
        public long cursorBlinkTime;
        public int cursorBlinking;
        public long keyCodeDelay;
        public long keyCodeShortDelay;
        public final ENG_ColorValue boxRectangleBackgroundColor = new ENG_ColorValue();
        public final ENG_ColorValue cursorRectangleBackgroundColor = new ENG_ColorValue();
        public float boxRectangleBorderWidth;
        public final ENG_ColorValue boxRectangleBorderColor = new ENG_ColorValue();
        public float cursorRectangleBorderWidth;
        public final ENG_ColorValue cursorRectangleBorderColor = new ENG_ColorValue();
        public ENG_TextView.HorizontalAlignment horizontalAlignment;
        public ENG_TextView.VerticalAlignment verticalAlignment;

        public RowParameters() {

        }

        public RowParameters(boolean setDefaults) {
            if (setDefaults) {
                setDefaults();
            }
        }

        public RowParameters(int textSize,
                             boolean visible,
                             float cursorWidth,
                             float cursorHeight,
                             long cursorBlinkTime,
                             int cursorBlinking,
                             long keyCodeDelay,
                             long keyCodeShortDelay,
                             ENG_ColorValue boxRectangleBackgroundColor,
                             ENG_ColorValue cursorRectangleBackgroundColor,
                             float boxRectangleBorderWidth,
                             ENG_ColorValue boxRectangleBorderColor,
                             float cursorRectangleBorderWidth,
                             ENG_ColorValue cursorRectangleBorderColor,
                             ENG_TextView.HorizontalAlignment horizontalAlignment,
                             ENG_TextView.VerticalAlignment verticalAlignment) {
            set(textSize,
                    visible,
                    cursorWidth,
                    cursorHeight,
                    cursorBlinkTime,
                    cursorBlinking,
                    keyCodeDelay,
                    keyCodeShortDelay,
                    boxRectangleBackgroundColor,
                    cursorRectangleBackgroundColor,
                    boxRectangleBorderWidth,
                    boxRectangleBorderColor,
                    cursorRectangleBorderWidth,
                    cursorRectangleBorderColor,
                    horizontalAlignment,
                    verticalAlignment);
        }

        public void setDefaults() {
            textSize = APP_Game.GORILLA_DEJAVU_MEDIUM;
            visible = false;
            cursorWidth = ENG_TextField.CURSOR_WIDTH_DEFAULT;
            cursorHeight = ENG_TextField.CURSOR_HEIGHT_DEFAULT;
            cursorBlinkTime = ENG_TextField.CURSOR_BLINK_TIME_DEFAULT;
            cursorBlinking = CURSOR_NOT_BLINKING;
            keyCodeDelay = ENG_TextField.KEY_CODE_DELAY_DEFAULT;
            keyCodeShortDelay = ENG_TextField.KEY_CODE_SHORT_DELAY_DEFAULT;
            boxRectangleBackgroundColor.set(ENG_TextField.BOX_RECTANGLE_BACKGROUND_COLOR_DEFAULT);
            cursorRectangleBackgroundColor.set(ENG_TextField.CURSOR_RECTANGLE_BACKGROUND_COLOR_DEFAULT);
            boxRectangleBorderWidth = ENG_TextField.BOX_RECTANGLE_BORDER_WIDTH_DEFAULT;
            boxRectangleBorderColor.set(ENG_TextField.BOX_RECTANGLE_BORDER_COLOR_DEFAULT);
            cursorRectangleBorderWidth = ENG_TextField.CURSOR_RECTANGLE_BORDER_WIDTH_DEFAULT;
            cursorRectangleBorderColor.set(ENG_TextField.CURSOR_RECTANGLE_BORDER_COLOR_DEFAULT);
            horizontalAlignment = ENG_TextView.HorizontalAlignment.LEFT;
            verticalAlignment = ENG_TextView.VerticalAlignment.CENTER;
        }

        public RowParameters(RowParameters rowParams) {
            set(rowParams);
        }

        public void set(RowParameters rowParams) {
            this.textSize = rowParams.textSize;
            this.visible = rowParams.visible;
            this.cursorWidth = rowParams.cursorWidth;
            this.cursorHeight = rowParams.cursorHeight;
            this.cursorBlinkTime = rowParams.cursorBlinkTime;
            this.cursorBlinking = rowParams.cursorBlinking;
            this.keyCodeDelay = rowParams.keyCodeDelay;
            this.keyCodeShortDelay = rowParams.keyCodeShortDelay;
            this.boxRectangleBackgroundColor.set(rowParams.boxRectangleBackgroundColor);
            this.cursorRectangleBackgroundColor.set(rowParams.cursorRectangleBackgroundColor);
            this.boxRectangleBorderWidth = rowParams.boxRectangleBorderWidth;
            this.boxRectangleBorderColor.set(rowParams.boxRectangleBorderColor);
            this.cursorRectangleBorderWidth = rowParams.cursorRectangleBorderWidth;
            this.cursorRectangleBorderColor.set(rowParams.cursorRectangleBorderColor);
            this.horizontalAlignment = rowParams.horizontalAlignment;
            this.verticalAlignment = rowParams.verticalAlignment;
        }

        public void set(int textSize,
                        boolean visible,
                        float cursorWidth,
                        float cursorHeight,
                        long cursorBlinkTime,
                        int cursorBlinking,
                        long keyCodeDelay,
                        long keyCodeShortDelay,
                        ENG_ColorValue boxRectangleBackgroundColor,
                        ENG_ColorValue cursorRectangleBackgroundColor,
                        float boxRectangleBorderWidth,
                        ENG_ColorValue boxRectangleBorderColor,
                        float cursorRectangleBorderWidth,
                        ENG_ColorValue cursorRectangleBorderColor,
                        ENG_TextView.HorizontalAlignment horizontalAlignment,
                        ENG_TextView.VerticalAlignment verticalAlignment) {
            this.textSize = textSize;
            this.visible = visible;
            this.cursorWidth = cursorWidth;
            this.cursorHeight = cursorHeight;
            this.cursorBlinkTime = cursorBlinkTime;
            this.cursorBlinking = cursorBlinking;
            this.keyCodeDelay = keyCodeDelay;
            this.keyCodeShortDelay = keyCodeShortDelay;
            this.boxRectangleBackgroundColor.set(boxRectangleBackgroundColor);
            this.cursorRectangleBackgroundColor.set(cursorRectangleBackgroundColor);
            this.boxRectangleBorderWidth = boxRectangleBorderWidth;
            this.boxRectangleBorderColor.set(boxRectangleBorderColor);
            this.cursorRectangleBorderWidth = cursorRectangleBorderWidth;
            this.cursorRectangleBorderColor.set(cursorRectangleBorderColor);
            this.horizontalAlignment = horizontalAlignment;
            this.verticalAlignment = verticalAlignment;
        }
    }

    private class Row {
        private final ArrayList<ENG_TextField> columnList = new ArrayList<>();
        //		private boolean visible;
        private final RowParameters rowParams = new RowParameters(true);
        private boolean dirty = true;
        private boolean frozen;
        private final AllowableOverwriteRowParameters allowableOverwriteRowParameters = new AllowableOverwriteRowParameters();
        private int previousLayerDepth;

        public Row(List<String> stringList, int row, RowParameters rowParams, AllowableOverwriteRowParameters allowableOverwriteRowParameters) {
            this(stringList, getName() + "_row_" + row + "_", rowParams, allowableOverwriteRowParameters);
        }

        public Row(List<String> stringList, String baseName, RowParameters rowParams, AllowableOverwriteRowParameters allowableOverwriteRowParameters) {
            this(stringList, baseName);
            setParameters(rowParams);
            setAllowableOverwriteRowParameters(allowableOverwriteRowParameters);
        }

        public Row(List<String> stringList, String baseName) {
            int i = 0;
            for (String s : stringList) {
                ENG_TextField textField = (ENG_TextField) getParent().createView(baseName + (i), "textfield", 0, 0, 0, 0, ENG_TableView.this);
                columnList.add(textField);
                setText(s, i);
                ++i;
                textField.setForceActualCoordinates(true);
                textField.setUpdateManually(true);
                textField.setWriteable(false);
//				textField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
//				textField.setTextSize(textSize);
//				textField.setText(s);
//				textField.setVisible(false);
            }
            setVisible(true);
        }

        public void destroy() {
            for (ENG_TextField tf : columnList) {
//				tf.destroy();
                // Destroy from parent container
                getParent().destroyView(tf.getName());
            }
        }

        public void moveToFrontLayerDepth() {
            for (ENG_TextField tf : columnList) {
                previousLayerDepth = tf.getLayerDepth();
                tf.moveToLayerDepth(TOP_LAYER_DEPTH);
            }
        }

        public void moveToPreviousLayerDepth() {
            for (ENG_TextField tf : columnList) {
                tf.moveToLayerDepth(previousLayerDepth);
            }
        }

        public RowParameters getRowParameters() {
            return rowParams;
        }

        public AllowableOverwriteRowParameters getAllowableOverwriteRowParameters() {
            return allowableOverwriteRowParameters;
        }

        public ArrayList<ENG_TextField> getColumnList() {
            return columnList;
        }

        public void setParameters(RowParameters rowParams) {
            this.rowParams.set(rowParams);
            markDirty();
        }

        public void setAllowableOverwriteRowParameters(AllowableOverwriteRowParameters params) {
            this.allowableOverwriteRowParameters.set(params);
            markDirty();
        }

        private void markDirty() {
            dirty = true;
        }

        public void applyParameters() {
            if (isFrozen()) {
                return;
            }
            if (dirty) {
                applyTextSize();
                applyVisible();
                applyCursorWidth();
                applyCursorHeight();
                applyCursorBlinkTime();
                applyCursorBlinking();
                applyKeyCodeDelay();
                applyKeyCodeShortDelay();
                applyBoxRectangleBackgroundColor();
                applyCursorRectangleBackgroundColor();
                applyBoxRectangleBorder();
                applyCursorRectangleBorder();
                applyHorizontalAlignment();
                applyVerticalAlignment();
                dirty = false;
            }
        }

        public void setColumnsPosition(float left, float top, float columnWidth, float columnHeight) {
            for (ENG_TextField tf : columnList) {
                tf.setActualCorners(left, top, columnWidth + left, columnHeight + top);
                left += columnWidth;
            }
        }

        public void updateColumns() {
            for (ENG_TextField tf : columnList) {
                tf.update(0, 0);
            }
        }

        public int getTextSize() {
            return rowParams.textSize;
        }

        public void setTextSize(int textSize) {
            rowParams.textSize = textSize;
            markDirty();
        }

        public void applyTextSize() {
            for (ENG_TextField tf : columnList) {
                tf.setTextSize(rowParams.textSize);
            }
        }

        public boolean isVisible() {
            return rowParams.visible;//visible;
        }

        public void setVisible(boolean visible) {
            rowParams.visible = visible;
            markDirty();
        }

        public void applyVisible() {
//			if (this.rowParams.visible != visible) {
//				this.rowParams.visible = visible;
            for (ENG_TextField tf : columnList) {
                tf.setVisible(rowParams.visible);
            }
//			}
        }

        public String getText(int column) {
            if (column < 0 || column >= columnList.size()) {
                throw new IllegalArgumentException("Column: " + column + " is invalid. Must be between 0 and " + columnList.size());
            }
            return columnList.get(column).getText();
        }

        public void setText(String text, int column) {
            if (column < 0 || column >= columnList.size()) {
                throw new IllegalArgumentException("Column: " + column + " is invalid. Must be between 0 and " + columnList.size());
            }
            columnList.get(column).setText(text);
        }

        public float getCursorWidth() {
            return rowParams.cursorWidth;//columnList.get(0).getCursorWidth();
        }

        public void setCursorWidth(float cursorWidth) {
            rowParams.cursorWidth = cursorWidth;
            markDirty();
        }

        public void applyCursorWidth() {
            for (ENG_TextField tf : columnList) {
                tf.setCursorWidth(rowParams.cursorWidth);
            }
        }

        public float getCursorHeight() {
            return rowParams.cursorHeight;//columnList.get(0).getCursorHeight();
        }

        public void setCursorHeight(float cursorHeight) {
            rowParams.cursorHeight = cursorHeight;
            markDirty();
        }

        public void applyCursorHeight() {
            for (ENG_TextField tf : columnList) {
                tf.setCursorHeight(rowParams.cursorHeight);
            }
        }

        public long getCursorBlinkTime() {
            return rowParams.cursorBlinkTime;//columnList.get(0).getCursorBlinkTime();
        }

        public void setCursorBlinkTime(long cursorBlinkTime) {
            rowParams.cursorBlinkTime = cursorBlinkTime;
            markDirty();
        }

        public void applyCursorBlinkTime() {
            for (ENG_TextField tf : columnList) {
                tf.setCursorBlinkTime(rowParams.cursorBlinkTime);
            }
        }

        /**
         * @return If we have at least one cursor blinking in this row
         */
        public boolean isCursorBlinking() {
            return rowParams.cursorBlinking != RowParameters.CURSOR_NOT_BLINKING;
//			boolean blinking = false;
//			for (ENG_TextField tf : columnList) {
//				if (tf.isCursorBlinking()) {
//					blinking = true;
//					break;
//				}
//			}
//			return blinking;
        }

        public boolean isCursorBlinking(int column) {
            if (column < 0 || column >= columnList.size()) {
                throw new IllegalArgumentException("Column: " + column + " is invalid. Must be between 0 and " + columnList.size());
            }
            return column == rowParams.cursorBlinking;
//			return columnList.get(column).isCursorBlinking();
        }

        public void setCursorBlinking(boolean cursorBlinking, int column) {
            if (column < 0 || column >= columnList.size()) {
                throw new IllegalArgumentException("Column: " + column + " is invalid. Must be between 0 and " + columnList.size());
            }
            rowParams.cursorBlinking = cursorBlinking ? column : RowParameters.CURSOR_NOT_BLINKING;
            markDirty();
//			if (cursorBlinking) {
//				setCursorBlinkingStop();
//				
//			}
//			columnList.get(column).setCursorBlinking(cursorBlinking);
        }

        public void applyCursorBlinking() {
            setCursorBlinkingStop();
            if (rowParams.cursorBlinking != RowParameters.CURSOR_NOT_BLINKING) {

                columnList.get(rowParams.cursorBlinking).setCursorBlinking(true);
            } else {
//				columnList.get(rowParams.cursorBlinking).setCursorBlinking(false);

            }
        }

        public void stopCursorBlinking() {
            rowParams.cursorBlinking = RowParameters.CURSOR_NOT_BLINKING;
            markDirty();
        }

        private void setCursorBlinkingStop() {
            for (ENG_TextField tf : columnList) {
                tf.setCursorBlinking(false);
            }
        }

        public long getKeyCodeDelay() {
            return rowParams.keyCodeDelay;//columnList.get(0).getKeyCodeDelay();
        }

        public void setKeyCodeDelay(long keyCodeDelay) {
            rowParams.keyCodeDelay = keyCodeDelay;
            markDirty();
        }

        public void applyKeyCodeDelay() {
            for (ENG_TextField tf : columnList) {
                tf.setKeyCodeDelay(rowParams.keyCodeDelay);
            }
        }

        public long getKeyCodeShortDelay() {
            return rowParams.keyCodeShortDelay;//columnList.get(0).getKeyCodeShortDelay();
        }

        public void setKeyCodeShortDelay(long keyCodeShortDelay) {
            rowParams.keyCodeShortDelay = keyCodeShortDelay;
            markDirty();
        }

        public void applyKeyCodeShortDelay() {
            for (ENG_TextField tf : columnList) {
                tf.setKeyCodeShortDelay(rowParams.keyCodeShortDelay);
            }
        }

        public void setBoxRectangleBackgroundColor(ENG_ColorValue c) {
            rowParams.boxRectangleBackgroundColor.set(c);
            markDirty();
        }

        public void applyBoxRectangleBackgroundColor() {
            for (ENG_TextField tf : columnList) {
                tf.setBoxRectangleBackgroundColor(rowParams.boxRectangleBackgroundColor);
            }
        }

        public void setCursorRectangleBackgroundColor(ENG_ColorValue c) {
            rowParams.cursorRectangleBackgroundColor.set(c);
            markDirty();
        }

        public void applyCursorRectangleBackgroundColor() {
            for (ENG_TextField tf : columnList) {
                tf.setCursorRectangleBackgroundColor(rowParams.cursorRectangleBackgroundColor);
            }
        }

        public ENG_ColorValue getBoxRectangleBackgroundColor() {
            return new ENG_ColorValue(rowParams.boxRectangleBackgroundColor);//columnList.get(0).getBoxRectangleBackgroundColor();
        }

        public ENG_ColorValue getCursorRectangleBackgroundColor() {
            return new ENG_ColorValue(rowParams.cursorRectangleBackgroundColor);//columnList.get(0).getCursorRectangleBackgroundColor();
        }

        public void setBoxRectangleBorder(float width, ENG_ColorValue c) {
            rowParams.boxRectangleBorderWidth = width;
            rowParams.boxRectangleBorderColor.set(c);
            markDirty();
        }

        public void applyBoxRectangleBorder() {
            for (ENG_TextField tf : columnList) {
                tf.setBoxRectangleBorder(rowParams.boxRectangleBorderWidth, rowParams.boxRectangleBorderColor);
            }
        }

        public void setCursorRectangleBorder(float width, ENG_ColorValue c) {
            rowParams.cursorRectangleBorderWidth = width;
            rowParams.cursorRectangleBorderColor.set(c);
            markDirty();
        }

        public void applyCursorRectangleBorder() {
            for (ENG_TextField tf : columnList) {
                tf.setCursorRectangleBorder(rowParams.cursorRectangleBorderWidth, rowParams.cursorRectangleBorderColor);
            }
        }

        public float getBoxRectangleBorderWidth() {
            return rowParams.boxRectangleBorderWidth;//columnList.get(0).getBoxRectangleBorderWidth();
        }

        public ENG_ColorValue getBoxRectangleBorderColor() {
            return new ENG_ColorValue(rowParams.boxRectangleBorderColor);//columnList.get(0).getBoxRectangleBorderColor();
        }

        public float getCursorRectangleBorderWidth() {
            return rowParams.cursorRectangleBorderWidth;//columnList.get(0).getCursorRectangleBorderWidth();
        }

        public ENG_ColorValue getCursorRectangleBorderColor() {
            return new ENG_ColorValue(rowParams.cursorRectangleBorderColor);//columnList.get(0).getCursorRectangleBorderColor();
        }

        public void setHorizontalAlignment(ENG_TextView.HorizontalAlignment horizontalAlignment) {
            rowParams.horizontalAlignment = horizontalAlignment;
            markDirty();
        }

        public ENG_TextView.HorizontalAlignment getHorizontalAlignment() {
            return rowParams.horizontalAlignment;
        }

        public void setVerticalAlignment(ENG_TextView.VerticalAlignment verticalAlignment) {
            rowParams.verticalAlignment = verticalAlignment;
            markDirty();
        }

        public ENG_TextView.VerticalAlignment getVerticalAlignment() {
            return rowParams.verticalAlignment;
        }

        public void applyHorizontalAlignment() {
            for (ENG_TextField tf : columnList) {
                tf.setHorizontalAlignment(rowParams.horizontalAlignment);
            }
        }

        public void applyVerticalAlignment() {
            for (ENG_TextField tf : columnList) {
                tf.setVerticalAlignment(rowParams.verticalAlignment);
            }
        }

        public boolean isFrozen() {
            return frozen;
        }

        public void setFrozen(boolean frozen) {
            this.frozen = frozen;
        }


    }

    public interface OnRowClick {
        boolean onSelectedRow(int row);

        boolean onUnselectedRow(int oldRow);
    }

    /**
     * TEXTFIELD_BASED means set the text field height and see how many
     * can fit in the table
     * TABLE_BASED means fit the set number of text fields in the
     * table.
     *
     * @author Sebi
     */
    public enum RowHeightType {
        TEXTFIELD_BASED, TABLE_BASED
    }

    private class ColumnNameAndRowParameters {
        public int column;
        public Row row;

        public ColumnNameAndRowParameters() {

        }

        public ColumnNameAndRowParameters(int column, Row row) {
            this.column = column;
            this.row = row;
        }
    }

    private static final float SCROLL_ARROW_LEN = 40.0f;
    private static final float DEFAULT_COLUMN_NAME_HEIGHT = 20.0f;

    public static final String BUNDLE_COLUMN_NAME_TEXT_SIZE = "column_name_text_size";

    private int rowNum;
    private final int columnNum;
    private final HashMap<String, Integer> columnNameList = new HashMap<>();
    private final ENG_RealRect scrollUpButton = new ENG_RealRect();
    private final ENG_RealRect scrollDownButton = new ENG_RealRect();
    private final ENG_Rectangle tableRectangle;
    private final ENG_Rectangle scrollbarRectangle;
    private final ENG_Rectangle scrollRectangle;
    private final ENG_Rectangle scrollUpRectangle;
    private final ENG_Rectangle scrollDownRectangle;
    private final ArrayList<Row> rowList = new ArrayList<>();
    private final Row columnNames;

    private int textSize;
    private int columnNamesTextSize;
    private boolean scrollButtonsSet;
    private float scrollbarWidth;
    private RowHeightType rowHeightType = RowHeightType.TABLE_BASED;
    private float textFieldHeight = TEXTFIELD_HEIGHT_DEFAULT;
    private int textFieldNumInTable = TEXTFIELD_NUM_IN_TABLE_DEFAULT;
    private float columnNameHeight = textFieldHeight;
    private int currentFirstRow;
    private final RowParameters rowParameters = new RowParameters(true);
    //	private RowParameters columnNamesRowParameters = new RowParameters(true);
    private boolean rowParametersDirty = true;
    private final ENG_ColorValue scrollArrowColor = new ENG_ColorValue(SCROLL_ARROWS_COLOR_DEFAULT);
    private final HashMap<ENG_View, ColumnNameAndRowParameters> viewToRowParameters =
            new HashMap<>();
    private int tempFirstRow;

    private final boolean rectanglesInitialized;
    private final OverwriteRowParameters overwriteRowParameters = new OverwriteRowParameters();
    private final OverwriteRowParameters columnNamesOverwriteRowParameters = new OverwriteRowParameters();
    private final AllowableOverwriteRowParameters allowableOverwriteRowParameters = new AllowableOverwriteRowParameters();
    private final ArrayList<ENG_RealRect> currentRowsRect = new ArrayList<>();
    private final ENG_RealRect totalRowsAreaRect = new ENG_RealRect();
    private int selectedRow = -1;
    private final ENG_RealRect selectedRect = new ENG_RealRect();
    private OnRowClick onRowClick;
    private final RowParameters savedRowParameters = new RowParameters();

    /** @noinspection deprecation*/
    public ENG_TableView(String name, ENG_Layer layer, ENG_Container parent, Bundle bundle, ENG_View parentView) {
        super(name, layer, parent, parentView);
        

        scrollbarWidth = SCROLL_ARROW_LEN * ENG_RenderRoot.getRenderRoot().getScreenDensity();

        tableRectangle = layer.createRectangle(0, 0, 0, 0);

        scrollbarRectangle = layer.createRectangle(0, 0, 0, 0, 1);
        scrollRectangle = layer.createRectangle(0, 0, 0, 0);
        scrollUpRectangle = layer.createRectangle(0, 0, 0, 0);
        scrollDownRectangle = layer.createRectangle(0, 0, 0, 0);
        ENG_Rectangle selectedRectangle = layer.createRectangle(0, 0, 0, 0, 2);

        tableRectangle.backgroundColour(ENG_ColorValue.BLACK);
        scrollbarRectangle.backgroundColour(ENG_ColorValue.GREEN);
        scrollRectangle.backgroundColour(ENG_ColorValue.BLACK);
        scrollUpRectangle.backgroundColour(ENG_ColorValue.BLACK);
        scrollDownRectangle.backgroundColour(ENG_ColorValue.BLACK);

        tableRectangle.border(1.0f, ENG_ColorValue.WHITE);
        scrollbarRectangle.border(1.0f, ENG_ColorValue.WHITE);
        scrollRectangle.border(1.0f, ENG_ColorValue.WHITE);
        rectanglesInitialized = true;

//		tableRectangle.setVisible(false);
//		scrollbarRectangle.setVisible(false);
//		scrollRectangle.setVisible(false);
//		scrollUpRectangle.setVisible(false);
//		scrollDownRectangle.setVisible(false);
//		setVisible(false);
        setRectanglesVisible(false);

        setFocusable(false);

        removeAllEventListeners();

        columnNum = bundle.getInt(BUNDLE_COLUMN_COUNT);
        List<String> columnNameList = (List<String>) bundle.getObject(BUNDLE_COLUMN_NAME_LIST);
        int i = 0;
        for (String s : columnNameList) {
			this.columnNameList.put(s, i++);

        }
        columnNames = new Row(columnNameList, getName() + "_column_name_");//, columnNamesRowParameters);
        float columnNameHeight = bundle.getFloat(BUNDLE_COLUMN_NAME_HEIGHT, 0.0f);
        if (!ENG_Float.isEqual(columnNameHeight, 0.0f)) {
            this.columnNameHeight = columnNameHeight;
//			columnNames.set
        } else {
            this.columnNameHeight = DEFAULT_COLUMN_NAME_HEIGHT * ENG_RenderRoot.getRenderRoot().getScreenDensity();
        }
        int column = 0;
        for (ENG_TextField tf : columnNames.getColumnList()) {
            addViewToRowParameters(tf, column++, columnNames);
        }

        int columnNamesTextSize = bundle.getInt(BUNDLE_COLUMN_NAME_TEXT_SIZE);
        if (columnNamesTextSize != 0) {
            setColumnNamesTextSize(columnNamesTextSize);
        }


        ENG_Sprite up = layer.getAtlas().getSprite(ENG_MarkupText.scrollUpSprite);
        ENG_Sprite down = layer.getAtlas().getSprite(ENG_MarkupText.scrollDownSprite);
        scrollUpRectangle.backgroundImage(up);
        scrollDownRectangle.backgroundImage(down);
        setScrollArrowColor(scrollArrowColor);

        setOnChildChangedListener(child -> {

            if (child instanceof ENG_TextField) {
                ENG_TextField childTextField = (ENG_TextField) child;
                ColumnNameAndRowParameters columnAndRowParams = viewToRowParameters.get(childTextField);
                if (columnAndRowParams == null) {
                    throw new IllegalArgumentException("Child view: " + child.getName() + " has not been found");
                }
                columnAndRowParams.row.setCursorBlinking(childTextField.isCursorBlinking(), columnAndRowParams.column);
            }
        });

        setOnClickListener((x, y) -> {

            boolean ret = false;
            if (scrollUpButton.inside(x, y)) {
                previousRow();
                markDirty();
                ret = true;
//					return true;
            } else if (scrollDownButton.inside(x, y)) {
                nextRow();
                markDirty();
                ret = true;
//					return true;
            } else if (totalRowsAreaRect.inside(x, y)) {
                int i1 = getCurrentFirstRow();
                for (ENG_RealRect rect : currentRowsRect) {
                    if (rect.inside(x, y)) {
                        _setSelectedRow(i1, rect);
                        break;
                    }
                    ++i1;
                }
                ret = true;
            }

            if (!ret && selectedRow != -1) {
                _resetSelectedRow();

            }

            return ret;
        });
    }

    /**
     * For use with automation.
     */
    public void _resetSelectedRow() {
        setUnselectedRowParameters();
        selectedRow = -1;
        selectedRect.setNull();

    }

    /**
     * For use with automation. Get the parameter ENG_RealRect for _setSelectedRow().
     * @return
     */
    public ArrayList<ENG_RealRect> getCurrentRowsRect() {
        return currentRowsRect;
    }

    /**
     * For use with automation.
     * @param row
     * @param rect
     */
    public void _setSelectedRow(int row, ENG_RealRect rect) {
        if (selectedRow != -1) {
            setUnselectedRowParameters();
        }
        selectedRow = row;
        selectedRect.set(rect);
        setSelectedRowParameters();
    }


    private void setSelectedRowParameters() {
        Row row = rowList.get(getSelectedRow());
        row.moveToFrontLayerDepth();
        RowParameters parameters = row.getRowParameters();
        savedRowParameters.set(parameters);
        boolean ret = false;
        if (onRowClick != null) {
            ret = onRowClick.onSelectedRow(getSelectedRow());
        }
        markDirty();
    }

    private void setUnselectedRowParameters() {
        boolean ret = false;
        if (onRowClick != null) {
            ret = onRowClick.onUnselectedRow(getSelectedRow());
        }
        if (!ret) {
            // If the unselect hasn't handled then just replace with the initial params
            Row row = rowList.get(getSelectedRow());
            row.moveToPreviousLayerDepth();
            RowParameters parameters = row.getRowParameters();
            parameters.set(savedRowParameters);
        }
        markDirty();
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public OnRowClick getOnRowClick() {
        return onRowClick;
    }

    public void setOnRowClick(OnRowClick onRowClick) {
        this.onRowClick = onRowClick;
    }

    private void nextRow() {
//		++currentFirstRow;
        ++tempFirstRow;
    }

    private void previousRow() {
//		if (--currentFirstRow < 0) {
//			currentFirstRow = 0;
//		}
        --tempFirstRow;
    }

    @Override
    public void destroy() {
        
        super.destroy();

        // TextFields destruction is handled by ENG_Container
        // no need to also destroy them here.
//		removeAllRows();
//		columnNames.destroy();

        ENG_Layer layer = getLayer();
        layer.destroyRectangle(tableRectangle);
        layer.destroyRectangle(scrollRectangle);
        layer.destroyRectangle(scrollbarRectangle);
        layer.destroyRectangle(scrollUpRectangle);
        layer.destroyRectangle(scrollDownRectangle);
    }

    @Override
    public void setVisible(boolean visible) {
        
        if (visible != isVisible()) {
            super.setVisible(visible);
            if (rectanglesInitialized) {
                setRectanglesVisible(visible);
            }
        }
    }

    private void setRectanglesVisible(boolean visible) {
        tableRectangle.setVisible(visible);
        scrollbarRectangle.setVisible(visible);
        scrollRectangle.setVisible(visible);
        scrollUpRectangle.setVisible(visible);
        scrollDownRectangle.setVisible(visible);
    }

    @Override
    public void update(int screenWidth, int screenHeight) {
        
        super.update(screenWidth, screenHeight);
        setRectanglesVisible(isVisible());
        if (!isVisible()) {
            resetRowsVisibility();
            return;
        }
        float left = getActLeft();
        float top = getActTop();
        float right = getActRight();
        float bottom = getActBottom();
        float width = right - left;
        float height = bottom - top;

        tableRectangle.left(left);
        tableRectangle.top(top);
        tableRectangle.width(width);
        tableRectangle.height(height);
        // _____________________________
        // |________|________|_______|__|
        // |        |        |       |__|
        // |________|________|_______|  |
        // |        |        |       |  |
        // |________|________|_______|  |
        // |        |        |       |__|
        // |________|________|_______|__|
        if (!scrollButtonsSet) {
            float scrollLeft = left + width - scrollbarWidth;
            float scrollTop = top;
            float scrollRight = left + width;
            float scrollBottom = top + scrollbarWidth;
            float scrollWidth = scrollRight - scrollLeft;
            float scrollHeight = scrollBottom - scrollTop;
            scrollUpButton.set(scrollLeft, scrollTop, scrollRight, scrollBottom);
            scrollUpRectangle.left(scrollLeft);
            scrollUpRectangle.top(scrollTop);
            scrollUpRectangle.width(scrollWidth);
            scrollUpRectangle.height(scrollHeight);
            scrollLeft = left + width - scrollbarWidth;
            scrollTop = top + height - scrollbarWidth;
            scrollRight = left + width;
            scrollBottom = top + height;
            scrollWidth = scrollRight - scrollLeft;
            scrollHeight = scrollBottom - scrollTop;
            scrollDownButton.set(scrollLeft, scrollTop, scrollRight, scrollBottom);
            scrollDownRectangle.left(scrollLeft);
            scrollDownRectangle.top(scrollTop);
            scrollDownRectangle.width(scrollWidth);
            scrollDownRectangle.height(scrollHeight);
            scrollButtonsSet = true;
        }

        float widthWithoutScrollbar = width - scrollbarWidth;
        float heightWithoutColumnNames = height - columnNameHeight;
        float textFieldWidth = widthWithoutScrollbar / columnNum;
        float textFieldHeight;


        if (rowHeightType == RowHeightType.TABLE_BASED) {
            maxVisibleRowsPerTable = getTextFieldNumInTable();
            textFieldMaxVisibleCount = Math.min(getRowNum(), getTextFieldNumInTable());
            textFieldHeight = heightWithoutColumnNames / getTextFieldNumInTable();//textFieldMaxVisibleCount;
        } else if (rowHeightType == RowHeightType.TEXTFIELD_BASED) {
            textFieldHeight = getTextFieldHeight();
            textFieldMaxVisibleCount = (int) (heightWithoutColumnNames / textFieldHeight);
            maxVisibleRowsPerTable = textFieldMaxVisibleCount;
            textFieldMaxVisibleCount = Math.min(getRowNum(), textFieldMaxVisibleCount);
        } else {
            throw new IllegalArgumentException();
        }
        float scrollLeft = left + widthWithoutScrollbar + 1.0f;
        float scrollWidth = scrollbarWidth;
        scrollRectangle.left(scrollLeft);
        scrollRectangle.top(top);
        scrollRectangle.width(scrollWidth);
        scrollRectangle.height(height);
        float scrollbarTop = top + scrollUpRectangle.height();
        float scrollbarHeight = height - scrollUpRectangle.height() - scrollDownRectangle.height();
        if (getRowNum() > maxVisibleRowsPerTable) {
//			scrollbarLeft = scrollLeft;
//			scrollbarTop = scrollTop + scrollUpRectangle.height();
//			scrollbarWidth = scrollWidth;
//			scrollbarHeight = scrollHeight - scrollUpRectangle.height() - 
//					scrollDownRectangle.height();
            int scrollbarSteps = getRowNum() - maxVisibleRowsPerTable + 1;
            scrollbarHeight /= scrollbarSteps;
            scrollbarTop += scrollbarHeight * currentFirstRow;
        } else {

        }
        scrollbarRectangle.left(scrollLeft);
        scrollbarRectangle.top(scrollbarTop);
        scrollbarRectangle.width(scrollWidth);
        scrollbarRectangle.height(scrollbarHeight);
        float currentTop = top;
        columnNames.applyParameters();
        columnNames.setColumnsPosition(left, currentTop, textFieldWidth, columnNameHeight);
        columnNames.updateColumns();
        currentTop += columnNameHeight;
        applyParameters();
        for (Row row : rowList) {
            row.applyParameters();
        }
        resetRowsVisibility();
        if (tempFirstRow < 0) {
            tempFirstRow = 0;
        }
        int lastVisibleRow = tempFirstRow + textFieldMaxVisibleCount;
        if (rowNum > maxVisibleRowsPerTable) {
            if (lastVisibleRow > rowNum) {
                tempFirstRow -= lastVisibleRow - rowNum;
                lastVisibleRow = rowNum;

            }
            currentFirstRow = tempFirstRow;
        } else {
            tempFirstRow = currentFirstRow;
            lastVisibleRow = tempFirstRow + textFieldMaxVisibleCount;
        }

        if (getCurrentVisibleRowsCount() > currentRowsRect.size()) {
            int count = getCurrentVisibleRowsCount() - currentRowsRect.size();
            for (int i = 0; i < count; ++i) {
                currentRowsRect.add(new ENG_RealRect());
            }
        }
        int currentRectRow = 0;
        for (int row = currentFirstRow; row < lastVisibleRow; ++row) {

            Row r = rowList.get(row);
            r.setColumnsPosition(left, currentTop, textFieldWidth, textFieldHeight);
            r.setVisible(true);
            r.applyVisible();

            currentRowsRect.get(currentRectRow++).set(left, currentTop, left + textFieldWidth * columnNum, currentTop + textFieldHeight);

            currentTop += textFieldHeight;
        }

        totalRowsAreaRect.setNull();
        for (ENG_RealRect rect : currentRowsRect) {
            totalRowsAreaRect.merge(rect);
        }

        for (Row r : rowList) {
            r.updateColumns();
        }
        markDirty();
    }

    public int getCurrentFirstRow() {
        return currentFirstRow;
    }

    public int getMaxVisibileRowsPerTable() {
        return maxVisibleRowsPerTable;
    }

    public int getCurrentVisibleRowsCount() {
        return textFieldMaxVisibleCount;
    }

    private void resetRowsVisibility() {
        for (int row = 0; row < rowNum; ++row) {
            Row r = rowList.get(row);
            r.setVisible(false);
            r.applyVisible();
        }
    }

    public void addRowByName(HashMap<String, String> columns) {
        insertRowByName(columns, getRowNum());
    }

    public void insertRowByName(HashMap<String, String> columns,
                                int rowPosition) {
        HashMap<Integer, String> map = new HashMap<>();
        for (Entry<String, String> entry : columns.entrySet()) {
            Integer column = columnNameList.get(entry.getKey());
            if (column == null) {
                throw new IllegalArgumentException("Column with name: " + entry.getKey() + " does not exist");
            }
            map.put(column, entry.getValue());
        }
        insertRowByPosition(map, rowPosition);
    }

    public void addRowByPosition(HashMap<Integer, String> columns) {
        insertRowByPosition(columns, getRowNum());
    }

    public void insertRowByPosition(HashMap<Integer, String> columns,
                                    int rowPosition) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < columnNum; ++i) {
            String string = columns.get(i);
            if (string == null) {
                throw new IllegalArgumentException("Column " + i + " missing");
            }
            list.add(string);
        }
        insertRow(list, rowPosition);
    }

    private void addRow(ArrayList<String> list) {
        insertRow(list, getRowNum());
    }

    private void addViewToRowParameters(ENG_TextField tf, int column, Row row) {
        viewToRowParameters.put(tf, new ColumnNameAndRowParameters(column, row));
    }

    private void removeViewToRowParameters(ENG_TextField tf) {
        viewToRowParameters.remove(tf);
    }

    private void insertRow(ArrayList<String> list, int rowPosition) {
        Row row = new Row(list, rowPosition, rowParameters, allowableOverwriteRowParameters);
        rowList.add(rowPosition, row);
        int i = 0;
        for (ENG_TextField tf : row.getColumnList()) {
            addViewToRowParameters(tf, i++, row);
        }
        incrementRowNum();
    }

    public void removeRow(int rowPosition) {
        checkRowValid(rowPosition);
        Row row = rowList.get(rowPosition);
        for (ENG_TextField tf : row.getColumnList()) {
            removeViewToRowParameters(tf);
        }
        row.destroy();
        rowList.remove(rowPosition);
        decrementRowNum();
    }

    public void removeAllRows() {
        int rows = getRowNum();
        for (int i = rows - 1; i >= 0; --i) {
            removeRow(i);
        }
    }

    private void incrementRowNum() {
        ++rowNum;
    }

    private void decrementRowNum() {
        --rowNum;
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getColumnNum() {
        return columnNum;
    }

    private void markRowParametersDirty() {
        rowParametersDirty = true;
    }

//	private void markColumnNamesRowParametersDirty() {
//		columnNamesRowParametersDirty = true;
//	}

    private void markTableDirty() {
        //	private boolean columnNamesRowParametersDirty = true;
        boolean tableDirty = true;
    }

    public int getTextSize() {
        return rowParameters.textSize;//textSize;
    }

    public void setTextSize(int textSize) {
        setTextSize(textSize, OverwriteType.NORMAL);
    }

    public void setTextSize(int textSize, OverwriteType type) {
//		this.textSize = textSize;
        rowParameters.textSize = textSize;
        overwriteRowParameters.textSize = type;
        markRowParametersDirty();
    }

    public float getScrollbarWidth() {
        return scrollbarWidth;
    }

    public void setScrollbarWidth(float scrollbarWidth) {
        this.scrollbarWidth = scrollbarWidth;
        markTableDirty();
    }

    public RowHeightType getRowHeightType() {
        return rowHeightType;
    }

    public void setRowHeightType(RowHeightType rowHeightType) {
        this.rowHeightType = rowHeightType;
        markTableDirty();
    }

    public float getTextFieldHeight() {
        return textFieldHeight;
    }

    public void setTextFieldHeight(float textFieldHeight) {
        this.textFieldHeight = textFieldHeight;
        markTableDirty();
    }

    public int getTextFieldNumInTable() {
        return textFieldNumInTable;
    }

    public void setTextFieldNumInTable(int textFieldNumInTable) {
        this.textFieldNumInTable = textFieldNumInTable;
        markTableDirty();
    }

    public float getColumnNameHeight() {
        return columnNameHeight;
    }

    public void setColumnNameHeight(float columnNameHeight) {
        this.columnNameHeight = columnNameHeight;
        markTableDirty();
    }

    public int getColumnNamesTextSize() {
//		return columnNamesRowParameters.textSize;//columnNamesTextSize;
        return columnNames.getTextSize();
    }

    public void setColumnNamesTextSize(int columnNamesTextSize) {
//		this.columnNamesTextSize = columnNamesTextSize;
//		columnNamesRowParameters.textSize = columnNamesTextSize;
//		markColumnNamesRowParametersDirty();
        columnNames.setTextSize(columnNamesTextSize);
    }

    private boolean isRowVisible(int row) {
        return rowList.get(row).isVisible();
    }

    private void setRowVisible(int row, boolean visible) {
        rowList.get(row).setVisible(visible);
    }

    private int getColumn(String columnName) {
        Integer i = columnNameList.get(columnName);
        if (i == null) {
            throw new IllegalArgumentException("Column name: " + columnName + " does not exist");
        }
        return i;
    }

    public String getColumnNameText(int column) {
        return columnNames.getText(column);
    }

    public void setColumnNameText(int column, String text) {
        columnNames.setText(text, column);
    }

    public String getText(int row, String columnName) {
        return getText(row, getColumn(columnName));
    }

    public String getText(int row, int column) {
        checkRowValid(row);
        return rowList.get(row).getText(column);
    }

    private void checkRowValid(int row) {
        if (row < 0 || row >= rowList.size()) {
            throw new IllegalArgumentException("Row: " + row + " is an invalid row number. It must be between 0 and " + rowList.size());
        }
    }

    public void setText(int row, int column, String text) {
        checkRowValid(row);
        rowList.get(row).setText(text, column);
    }

    public void setText(int row, String column, String text) {
        setText(row, getColumn(column), text);
    }

    public float getColumnNamesCursorWidth() {
//		return textFieldColumnNames.getCursorWidth();
//		return columnNamesRowParameters.cursorWidth;
        return columnNames.getCursorWidth();
    }

    public void setColumnNamesCursorWidth(float cursorWidth) {
//		textFieldColumnNames.setCursorWidth(cursorWidth);
//		columnNamesRowParameters.cursorWidth = cursorWidth;
//		markColumnNamesRowParametersDirty();
        columnNames.setCursorWidth(cursorWidth);
    }

    public boolean isFrozen(int row) {
        checkRowValid(row);
        return rowList.get(row).isFrozen();
    }

    public void setFrozen(int row, boolean frozen) {
        checkRowValid(row);
        rowList.get(row).setFrozen(frozen);
    }

//	public float getColumnNamesCursorWidth() {
//		return columnNamesRowParameters.cursorWidth;
//	}
//	
//	public void setColumnNamesCursorWidth(float cursorWidth) {
//		columnNamesRowParameters.cursorWidth = cursorWidth;
//		markColumnNamesRowParametersDirty();
//	}
//	
//	private void applyColumnNamesCursorWidth() {
//		columnNames.setCursorWidth(columnNamesRowParameters.cursorWidth);
//	}

    public float getCursorWidth() {
        return rowParameters.cursorWidth;
    }

    public float getCursorWidth(int row) {
        checkRowValid(row);
        return rowList.get(row).getCursorWidth();
    }

    public void setCursorWidth(int row, float cursorWidth) {
        setCursorWidth(row, cursorWidth, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setCursorWidth(int row, float cursorWidth, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setCursorWidth(cursorWidth);
        row1.allowableOverwriteRowParameters.cursorWidth = type;
    }

    public void setCursorWidth(float cursorWidth) {
        setCursorWidth(cursorWidth, OverwriteType.NORMAL);
    }

    public void setCursorWidth(float cursorWidth, OverwriteType type) {
        rowParameters.cursorWidth = cursorWidth;
        overwriteRowParameters.cursorWidth = type;
        markRowParametersDirty();
    }

    private void applyCursorWidth() {
        for (Row row : rowList) {
            if (overwriteRowParameters.cursorWidth == OverwriteType.FORCED ||
                    row.allowableOverwriteRowParameters.cursorWidth == OverwriteAllowType.OVERWRITABLE) {
                row.setCursorWidth(rowParameters.cursorWidth);
            }
        }
    }

    public float getColumnNamesCursorHeight() {
//		return textFieldColumnNames.getCursorHeight();
//		return columnNamesRowParameters.cursorHeight;
        return columnNames.getCursorHeight();
    }

    public void setColumnNamesCursorHeight(float cursorHeight) {
//		textFieldColumnNames.setCursorHeight(cursorHeight);
//		columnNamesRowParameters.cursorHeight = cursorHeight;
//		markColumnNamesRowParametersDirty();
        columnNames.setCursorHeight(cursorHeight);
    }

    public float getCursorHeight() {
        return rowParameters.cursorHeight;
    }

    public float getCursorHeight(int row) {
        checkRowValid(row);
        return rowList.get(row).getCursorHeight();
    }

    public void setCursorHeight(int row, float cursorHeight) {
        setCursorHeight(row, cursorHeight, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setCursorHeight(int row, float cursorHeight, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setCursorHeight(cursorHeight);
        row1.allowableOverwriteRowParameters.cursorHeight = type;
    }

    public void setCursorHeight(float cursorHeight, OverwriteType type) {
        rowParameters.cursorHeight = cursorHeight;
        overwriteRowParameters.cursorHeight = type;
        markRowParametersDirty();
    }

    private void applyCursorHeight() {
        for (Row row : rowList) {
            if (overwriteRowParameters.cursorHeight == OverwriteType.FORCED ||
                    row.allowableOverwriteRowParameters.cursorHeight == OverwriteAllowType.OVERWRITABLE) {
                row.setCursorHeight(rowParameters.cursorHeight);
            }
        }
    }

    public long getColumnNamesCursorBlinkTime() {
        return columnNames.getCursorBlinkTime();
    }

    public void setColumnNamesCursorBlinkTime(long blinkTime) {
        columnNames.setCursorBlinkTime(blinkTime);
    }

    public long getCursorBlinkTime() {
        return rowParameters.cursorBlinkTime;
    }

    public long getCursorBlinkTime(int row) {
        checkRowValid(row);
        return rowList.get(row).getCursorBlinkTime();
    }

    public void setCursorBlinkTime(int row, long blinkTime) {
        setCursorBlinkTime(row, blinkTime, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setCursorBlinkTime(int row, long blinkTime, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setCursorBlinkTime(blinkTime);
        row1.allowableOverwriteRowParameters.cursorBlinkTime = type;
    }

    public void setCursorBlinkTime(long blinkTime) {
        setCursorBlinkTime(blinkTime, OverwriteType.NORMAL);
    }

    public void setCursorBlinkTime(long blinkTime, OverwriteType type) {
        rowParameters.cursorBlinkTime = blinkTime;
        overwriteRowParameters.cursorBlinkTime = type;
        markRowParametersDirty();
    }

    private void applyCursorBlinkTime() {
        for (Row row : rowList) {
            if (overwriteRowParameters.cursorBlinkTime == OverwriteType.FORCED ||
                    row.allowableOverwriteRowParameters.cursorBlinkTime == OverwriteAllowType.OVERWRITABLE) {
                row.setCursorBlinkTime(rowParameters.cursorBlinkTime);
            }
        }
    }

    public boolean isColumnNamesCursorBlinking() {
        return columnNames.isCursorBlinking();
    }

    public boolean isColumnNamesCursorBlinking(int column) {
        return columnNames.isCursorBlinking(column);
    }

    public void setColumnNamesCursorBlinking(int column, boolean blink) {
        columnNames.setCursorBlinking(blink, column);
    }

    public void stopColumnNamesCursorBlinking() {
        columnNames.stopCursorBlinking();
    }

    public boolean isCursorBlinking(int row) {
        checkRowValid(row);
        return rowList.get(row).isCursorBlinking();
    }

    public boolean isCursorBlinking(int row, int column) {
        checkRowValid(row);
        return rowList.get(row).isCursorBlinking(column);
    }

    public void setCursorBlinking(int row, int column, boolean blink) {
        setCursorBlinking(row, column, blink, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setCursorBlinking(int row, int column, boolean blink, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setCursorBlinking(blink, column);
        row1.allowableOverwriteRowParameters.cursorBlinking = type;
    }

    public void stopCursorBlinking(int row) {
        checkRowValid(row);
        rowList.get(row).stopCursorBlinking();
    }

    public long getColumnNamesKeyCodeDelay() {
        return columnNames.getKeyCodeDelay();
    }

    public void setColumnNamesKeyCodeDelay(long delay) {
        columnNames.setKeyCodeDelay(delay);
    }

    public long getKeyCodeDelay() {
        return rowParameters.keyCodeDelay;
    }

    public long getKeyCodeDelay(int row) {
        checkRowValid(row);
        return rowList.get(row).getKeyCodeDelay();
    }

    public void setKeyCodeDelay(long delay) {
        setKeyCodeDelay(delay, OverwriteType.NORMAL);
    }

    public void setKeyCodeDelay(long delay, OverwriteType type) {
        rowParameters.keyCodeDelay = delay;
        overwriteRowParameters.keyCodeDelay = type;
        markRowParametersDirty();
    }

    public void setKeyCodeDelay(int row, long delay) {
        setKeyCodeDelay(row, delay, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setKeyCodeDelay(int row, long delay, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setKeyCodeDelay(delay);
        row1.allowableOverwriteRowParameters.keyCodeDelay = type;
    }

    private void applyKeyCodeDelay() {
        for (Row row : rowList) {
            if (overwriteRowParameters.keyCodeDelay == OverwriteType.FORCED ||
                    row.allowableOverwriteRowParameters.keyCodeDelay == OverwriteAllowType.OVERWRITABLE) {
                row.setKeyCodeDelay(rowParameters.keyCodeDelay);
            }
        }
    }

    public long getColumnNamesKeyCodeShortDelay() {
        return columnNames.getKeyCodeShortDelay();
    }

    public void setColumnNamesKeyCodeShortDelay(long delay) {
        columnNames.setKeyCodeShortDelay(delay);
    }

    public long getKeyCodeShortDelay() {
        return rowParameters.keyCodeShortDelay;
    }

    public long getKeyCodeShortDelay(int row) {
        checkRowValid(row);
        return rowList.get(row).getKeyCodeShortDelay();
    }

    public void setKeyCodeShortDelay(long delay) {
        setKeyCodeShortDelay(delay, OverwriteType.NORMAL);
    }

    public void setKeyCodeShortDelay(long delay, OverwriteType type) {
        rowParameters.keyCodeShortDelay = delay;
        overwriteRowParameters.keyCodeShortDelay = type;
        markRowParametersDirty();
    }

    public void setKeyCodeShortDelay(int row, long delay) {
        setKeyCodeShortDelay(row, delay, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setKeyCodeShortDelay(int row, long delay, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setKeyCodeShortDelay(delay);
        row1.allowableOverwriteRowParameters.keyCodeShortDelay = type;
    }

    private void applyKeyCodeShortDelay() {
        for (Row row : rowList) {
            if (overwriteRowParameters.keyCodeShortDelay == OverwriteType.FORCED ||
                    row.allowableOverwriteRowParameters.keyCodeShortDelay == OverwriteAllowType.OVERWRITABLE) {
                row.setKeyCodeShortDelay(rowParameters.keyCodeShortDelay);
            }
        }
    }

    public ENG_ColorValue getColumnNamesBoxRectangleBackgroundColor() {
        return columnNames.getBoxRectangleBackgroundColor();
    }

    public void setColumnNamesBoxRectangleBackgroundColor(ENG_ColorValue c) {
        columnNames.setBoxRectangleBackgroundColor(c);
    }

    public ENG_ColorValue getBoxRectangleBackgroundColor() {
        return new ENG_ColorValue(rowParameters.boxRectangleBackgroundColor);
    }

    public ENG_ColorValue getBoxRectangleBackgroundColor(int row) {
        checkRowValid(row);
        return rowList.get(row).getBoxRectangleBackgroundColor();
    }

    public void setBoxRectangleBackgroundColor(ENG_ColorValue c) {
        setBoxRectangleBackgroundColor(c, OverwriteType.NORMAL);
    }

    public void setBoxRectangleBackgroundColor(ENG_ColorValue c, OverwriteType type) {
        rowParameters.boxRectangleBackgroundColor.set(c);
        overwriteRowParameters.boxRectangleBackgroundColor = type;
        markRowParametersDirty();
    }

    public void setBoxRectangleBackgroundColor(int row, ENG_ColorValue c) {
        setBoxRectangleBackgroundColor(row, c, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setBoxRectangleBackgroundColor(int row, ENG_ColorValue c, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setBoxRectangleBackgroundColor(c);
        row1.allowableOverwriteRowParameters.boxRectangleBackgroundColor = type;
    }

    public void applyBoxRectangleBackgroundColor() {

        for (Row row : rowList) {
            if (overwriteRowParameters.boxRectangleBackgroundColor == OverwriteType.FORCED ||
                    row.allowableOverwriteRowParameters.boxRectangleBackgroundColor == OverwriteAllowType.OVERWRITABLE) {
                row.setBoxRectangleBackgroundColor(rowParameters.boxRectangleBackgroundColor);
            }
        }

    }

    public ENG_ColorValue getColumnNamesCursorRectangleBackgroundColor() {
        return columnNames.getCursorRectangleBackgroundColor();
    }

    public void setColumnNamesCursorRectangleBackgroundColor(ENG_ColorValue c) {
        columnNames.setCursorRectangleBackgroundColor(c);
    }

    public ENG_ColorValue getCursorRectangleBackgroundColor() {
        return new ENG_ColorValue(rowParameters.cursorRectangleBackgroundColor);
    }

    public ENG_ColorValue getCursorRectangleBackgroundColor(int row) {
        checkRowValid(row);
        return rowList.get(row).getCursorRectangleBackgroundColor();
    }

    public void setCursorRectangleBackgroundColor(ENG_ColorValue c) {
        setCursorRectangleBackgroundColor(c, OverwriteType.NORMAL);
    }

    public void setCursorRectangleBackgroundColor(ENG_ColorValue c, OverwriteType type) {
        rowParameters.cursorRectangleBackgroundColor.set(c);
        overwriteRowParameters.cursorRectangleBackgroundColor = type;
        markRowParametersDirty();
    }

    public void setCursorRectangleBackgroundColor(int row, ENG_ColorValue c) {
        setCursorRectangleBackgroundColor(row, c, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setCursorRectangleBackgroundColor(int row, ENG_ColorValue c, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setCursorRectangleBackgroundColor(c);
        row1.allowableOverwriteRowParameters.cursorRectangleBackgroundColor = type;
    }

    public void applyCursorRectangleBackgroundColor() {
        for (Row row : rowList) {
            if (overwriteRowParameters.cursorRectangleBackgroundColor == OverwriteType.FORCED ||
                    row.allowableOverwriteRowParameters.cursorRectangleBackgroundColor == OverwriteAllowType.OVERWRITABLE) {
                row.setCursorRectangleBackgroundColor(rowParameters.cursorRectangleBackgroundColor);
            }
        }
    }

    public float getColumnNamesBoxRectangleBorderWidth() {
        return columnNames.getBoxRectangleBorderWidth();
    }

    public ENG_ColorValue getColumnNamesBoxRectangleBorderColor() {
        return columnNames.getBoxRectangleBorderColor();
    }

    public void setColumnNamesBoxRectangleBorder(float width, ENG_ColorValue c) {
        columnNames.setBoxRectangleBorder(width, c);
    }

    public float getBoxRectangleBorderWidth() {
        return rowParameters.boxRectangleBorderWidth;
    }

    public float getBoxRectangleBorderWidth(int row) {
        checkRowValid(row);
        return rowList.get(row).getBoxRectangleBorderWidth();
    }

    public ENG_ColorValue getBoxRectangleBorderColor() {
        return new ENG_ColorValue(rowParameters.boxRectangleBorderColor);
    }

    public ENG_ColorValue getBoxRectangleBorderColor(int row) {
        checkRowValid(row);
        return rowList.get(row).getBoxRectangleBorderColor();
    }

    public void setBoxRectangleBorder(float width, ENG_ColorValue c) {
        setBoxRectangleBorder(width, c, OverwriteType.NORMAL);
    }

    public void setBoxRectangleBorder(float width, ENG_ColorValue c, OverwriteType type) {
        rowParameters.boxRectangleBorderWidth = width;
        rowParameters.boxRectangleBorderColor.set(c);
        overwriteRowParameters.boxRectangleBorderWidth = type;
        overwriteRowParameters.boxRectangleBorderColor = type;
        markRowParametersDirty();
    }

    public void setBoxRectangleBorder(int row, float width, ENG_ColorValue c) {
        setBoxRectangleBorder(row, width, c, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setBoxRectangleBorder(int row, float width, ENG_ColorValue c, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setBoxRectangleBorder(width, c);
        row1.allowableOverwriteRowParameters.boxRectangleBorderWidth = type;
        row1.allowableOverwriteRowParameters.boxRectangleBorderColor = type;
    }

    public void applyBoxRectangleBorder() {
        for (Row row : rowList) {
            if ((overwriteRowParameters.boxRectangleBorderWidth == OverwriteType.FORCED &&
                    overwriteRowParameters.boxRectangleBorderColor == OverwriteType.FORCED) ||
                    (row.allowableOverwriteRowParameters.boxRectangleBorderWidth == OverwriteAllowType.OVERWRITABLE &&
                            row.allowableOverwriteRowParameters.boxRectangleBorderColor == OverwriteAllowType.OVERWRITABLE)) {
                row.setBoxRectangleBorder(
                        rowParameters.boxRectangleBorderWidth,
                        rowParameters.boxRectangleBorderColor);
            }
        }
    }

    public float getColumnNamesCursorRectangleBorderWidth() {
        return columnNames.getCursorRectangleBorderWidth();
    }

    public ENG_ColorValue getColumnNamesCursorRectangleBorderColor() {
        return columnNames.getCursorRectangleBorderColor();
    }

    public void setColumnNamesCursorRectangleBorder(float width, ENG_ColorValue c) {
        columnNames.setCursorRectangleBorder(width, c);
    }

    public float getCursorRectangleBorderWidth() {
        return rowParameters.cursorRectangleBorderWidth;
    }

    public float getCursorRectangleBorderWidth(int row) {
        checkRowValid(row);
        return rowList.get(row).getCursorRectangleBorderWidth();
    }

    public ENG_ColorValue getCursorRectangleBorderColor() {
        return new ENG_ColorValue(rowParameters.cursorRectangleBorderColor);
    }

    public ENG_ColorValue getCursorRectangleBorderColor(int row) {
        checkRowValid(row);
        return rowList.get(row).getCursorRectangleBorderColor();
    }

    public void setCursorRectangleBorder(float width, ENG_ColorValue c) {
        setCursorRectangleBorder(width, c, OverwriteType.NORMAL);
    }

    public void setCursorRectangleBorder(float width, ENG_ColorValue c, OverwriteType type) {
        rowParameters.cursorRectangleBorderWidth = width;
        rowParameters.cursorRectangleBorderColor.set(c);
        overwriteRowParameters.cursorRectangleBorderWidth = type;
        overwriteRowParameters.cursorRectangleBorderColor = type;
        markRowParametersDirty();
    }

    public void setCursorRectangleBorder(int row, float width, ENG_ColorValue c) {
        setCursorRectangleBorder(row, width, c, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setCursorRectangleBorder(int row, float width, ENG_ColorValue c, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setCursorRectangleBorder(width, c);
        row1.allowableOverwriteRowParameters.cursorRectangleBorderWidth = type;
        row1.allowableOverwriteRowParameters.cursorRectangleBorderColor = type;
    }

    public void applyCursorRectangleBorder() {
        for (Row row : rowList) {
            if ((overwriteRowParameters.cursorRectangleBorderWidth == OverwriteType.FORCED &&
                    overwriteRowParameters.cursorRectangleBorderColor == OverwriteType.FORCED) ||
                    (row.allowableOverwriteRowParameters.cursorRectangleBorderWidth == OverwriteAllowType.OVERWRITABLE &&
                            row.allowableOverwriteRowParameters.cursorRectangleBorderColor == OverwriteAllowType.OVERWRITABLE)) {
                row.setCursorRectangleBorder(
                        rowParameters.cursorRectangleBorderWidth,
                        rowParameters.cursorRectangleBorderColor);
            }
        }
    }

    public ENG_ColorValue getScrollArrowColor() {
        return new ENG_ColorValue(scrollArrowColor);
    }

    public void setScrollArrowColor(ENG_ColorValue scrollArrowColor) {
        this.scrollArrowColor.set(scrollArrowColor);
        scrollUpRectangle.backgroundColour(scrollArrowColor);
        scrollDownRectangle.backgroundColour(scrollArrowColor);
        markDirty();
    }

    public void setHorizontalAlignment(ENG_TextView.HorizontalAlignment horizontalAlignment) {
        setHorizontalAlignment(horizontalAlignment, OverwriteType.NORMAL);
    }

    public void setHorizontalAlignment(ENG_TextView.HorizontalAlignment horizontalAlignment, OverwriteType type) {
        rowParameters.horizontalAlignment = horizontalAlignment;
        overwriteRowParameters.horizontalAlignment = type;
        markRowParametersDirty();
    }

    public ENG_TextView.HorizontalAlignment getHorizontalAlignment() {
        return rowParameters.horizontalAlignment;
    }

    public void setHorizontalAlignment(int row, ENG_TextView.HorizontalAlignment horizontalAlignment) {
        setHorizontalAlignment(row, horizontalAlignment, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setHorizontalAlignment(int row, ENG_TextView.HorizontalAlignment horizontalAlignment, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setHorizontalAlignment(horizontalAlignment);
        row1.allowableOverwriteRowParameters.horizontalAlignment = type;
    }

    public ENG_TextView.HorizontalAlignment getHorizontalAlignment(int row) {
        checkRowValid(row);
        return rowList.get(row).getHorizontalAlignment();
    }

    private void applyHorizontalAlignment() {
        for (Row row : rowList) {
            if (overwriteRowParameters.horizontalAlignment == OverwriteType.FORCED ||
                    row.allowableOverwriteRowParameters.horizontalAlignment == OverwriteAllowType.OVERWRITABLE) {
                row.setHorizontalAlignment(rowParameters.horizontalAlignment);
            }
        }
    }

    public void setColumnNamesHorizontalAlignment(ENG_TextView.HorizontalAlignment horizontalAlignment) {
        columnNames.setHorizontalAlignment(horizontalAlignment);
    }

    public ENG_TextView.HorizontalAlignment getColumnNamesHorizontalAlignment() {
        return columnNames.getHorizontalAlignment();
    }

    public void setVerticalAlignment(ENG_TextView.VerticalAlignment verticalAlignment) {
        setVerticalAlignment(verticalAlignment, OverwriteType.NORMAL);
    }

    public void setVerticalAlignment(ENG_TextView.VerticalAlignment verticalAlignment, OverwriteType type) {
        rowParameters.verticalAlignment = verticalAlignment;
        overwriteRowParameters.verticalAlignment = type;
        markRowParametersDirty();
    }

    public ENG_TextView.VerticalAlignment getVerticalAlignment() {
        return rowParameters.verticalAlignment;
    }

    public void setVerticalAlignment(int row, ENG_TextView.VerticalAlignment verticalAlignment) {
        setVerticalAlignment(row, verticalAlignment, OverwriteAllowType.NON_OVERWRITABLE);
    }

    public void setVerticalAlignment(int row, ENG_TextView.VerticalAlignment verticalAlignment, OverwriteAllowType type) {
        checkRowValid(row);
        Row row1 = rowList.get(row);
        row1.setVerticalAlignment(verticalAlignment);
        row1.allowableOverwriteRowParameters.verticalAlignment = type;
    }

    public ENG_TextView.VerticalAlignment getVerticalAlignment(int row) {
        checkRowValid(row);
        return rowList.get(row).getVerticalAlignment();
    }

    private void applyVerticalAlignment() {
        for (Row row : rowList) {
            if (overwriteRowParameters.verticalAlignment == OverwriteType.FORCED ||
                    row.allowableOverwriteRowParameters.verticalAlignment == OverwriteAllowType.OVERWRITABLE) {
                row.setVerticalAlignment(rowParameters.verticalAlignment);
            }
        }
    }

    public void setColumnNamesVerticalAlignment(ENG_TextView.VerticalAlignment verticalAlignment) {
        columnNames.setVerticalAlignment(verticalAlignment);
    }

    public ENG_TextView.VerticalAlignment getColumnNamesVerticalAlignment() {
        return columnNames.getVerticalAlignment();
    }

    private void applyParameters() {
        if (rowParametersDirty) {
            applyBoxRectangleBackgroundColor();
            applyBoxRectangleBorder();
            applyCursorBlinkTime();
            applyCursorHeight();
            applyCursorRectangleBackgroundColor();
            applyCursorRectangleBorder();
            applyCursorWidth();
            applyHorizontalAlignment();
            applyKeyCodeDelay();
            applyKeyCodeShortDelay();
            applyVerticalAlignment();
            rowParametersDirty = false;
        }
    }

}
