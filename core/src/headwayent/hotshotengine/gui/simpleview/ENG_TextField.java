/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/24/21, 12:31 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui.simpleview;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_StringUtility;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.gui.simpleview.ENG_Container.ViewFactory;
import headwayent.hotshotengine.gorillagui.*;
import headwayent.hotshotengine.input.ENG_InputConvertor.KeyEventType;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import com.badlogic.gdx.Input.Keys;

import java.util.ArrayList;

import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;
import static headwayent.hotshotengine.ENG_Utility.currentTimeMillisReal;

public class ENG_TextField extends ENG_View {

    public static final ENG_ColorValue CURSOR_RECTANGLE_BORDER_COLOR_DEFAULT = ENG_ColorValue.WHITE;
    public static final ENG_ColorValue BOX_RECTANGLE_BORDER_COLOR_DEFAULT = ENG_ColorValue.WHITE;
    public static final float CURSOR_RECTANGLE_BORDER_WIDTH_DEFAULT = 1.0f;
    public static final float BOX_RECTANGLE_BORDER_WIDTH_DEFAULT = 1.0f;
    public static final ENG_ColorValue CURSOR_RECTANGLE_BACKGROUND_COLOR_DEFAULT = ENG_ColorValue.BLACK;
    public static final ENG_ColorValue BOX_RECTANGLE_BACKGROUND_COLOR_DEFAULT = ENG_ColorValue.BLACK;
    private boolean characterShownWithPasswordInWait;

    public static class TextFieldFactory extends ViewFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_View createView(String name, ENG_Layer layer,
                                   ENG_Container parent, Bundle bundle, ENG_View parentView) {

            ENG_TextField textField = new ENG_TextField(name, layer, parent, parentView);
            textField.setViewType(ENG_Container.ViewType.VIEW_TEXTFIELD);
            return textField;
        }

        @Override
        public void destroyView(ENG_View view) {
            
            view.destroy();
        }

    }

    public enum PasswordType {
        SHOW_ASTERISK_DIRECTLY, SHOW_ASTERISK_WITH_DELAY
    }

    public static final float CURSOR_WIDTH_DEFAULT = 16.0f;
    public static final float CURSOR_HEIGHT_DEFAULT = 32.0f;
    private static final float CAPTION_DISTANCE_FROM_BORDER_DEFAULT = 4.0f;
    public static final long CURSOR_BLINK_TIME_DEFAULT = 750;
    public static final long KEY_CODE_DELAY_DEFAULT = 1000;
    public static final long KEY_CODE_SHORT_DELAY_DEFAULT = 100;
    public static final long SPECIAL_KEY_CODE_SHORT_DELAY_DEFAULT = 100;
    private static final long PASSWORD_CHAR_CHANGE_DELAY = 1000;//500;

    private ENG_Caption textCaption;
    private final ENG_Rectangle boxRectangle;
    private final ENG_Rectangle cursorRectangle;
    private final ENG_Vector2D cursorPos = new ENG_Vector2D();
    private int textSize;
    private final StringBuilder text = new StringBuilder();
    private String shownText = "";
    private int beginChar, endChar;
    private int cursorCharPos, cursorShownCharPos;
    private float cursorWidth = CURSOR_WIDTH_DEFAULT;
    private float cursorHeight = CURSOR_HEIGHT_DEFAULT;
    private long cursorBlinkTime = CURSOR_BLINK_TIME_DEFAULT;
    private long currentCursorTime;
    private float captionDistanceFromBorder = CAPTION_DISTANCE_FROM_BORDER_DEFAULT;
    private final ENG_Vector2D calculatedTextSize = new ENG_Vector2D();
    private boolean cursorBlinking;
    private boolean justFocused;
    private long keyCodeDelay = KEY_CODE_DELAY_DEFAULT;
    private long currentKeyCodeTime;
    private long currentSpecialKeyCodeTime;
    private long keyCodeShortDelay = KEY_CODE_SHORT_DELAY_DEFAULT;
    private long specialKeyCodeShortDelay = SPECIAL_KEY_CODE_SHORT_DELAY_DEFAULT;
    private boolean firstDelay = true;
    private int currentKeyCode = INVALID_KEY_CODE;
    private int scrollDir;
    private boolean deleteChar;
    private boolean updateShownText;
    private StringBuilder tempShownText;
    private boolean addCharacterUpdate;
    private boolean allowSpaceKey;
    private boolean password;
    private final StringBuilder passwordText = new StringBuilder();
    private boolean characterAdded;
    private long currentPasswordCharTime;
    private long passwordCharTimeDelay = PASSWORD_CHAR_CHANGE_DELAY;
    private boolean writeable = true;
    private ENG_TextView.HorizontalAlignment horizontalAlignment;
    private ENG_TextView.VerticalAlignment verticalAlignment;
    private PasswordType passwordType;// = PasswordType.SHOW_ASTERISK_WITH_DELAY;
    private final ArrayList<ENG_TextFieldChangeListener> textFieldChangeList = new ArrayList<>();
    private ENG_TextFieldOnReturnPressedListener onReturnPressedListener;
    private ENG_TextFieldUpDownPressedListener onUpDownPressedListener;
    private ENG_TextFieldBackspacePressedListener onBackspacePressedListener;
    private ENG_TextFieldTabPressedListener onTabPressedListener;
//    private boolean specialKeyCodePressed;

    public interface ENG_TextFieldChangeListener {
        void onTextChanged(String text);
    }

    public interface ENG_TextFieldOnReturnPressedListener {
        void onReturnPressed(String text);
    }

    public interface ENG_TextFieldUpDownPressedListener {
        void onUpPressed(String text);
        void onDownPressed(String text);
    }

    public interface ENG_TextFieldTabPressedListener {
        void onTabPressed(String text);
    }


    public interface ENG_TextFieldBackspacePressedListener {
        /**
         *
         * @param text text before the backspace.
         */
        void onBackspacePressed(String text);
    }


    public ENG_TextField(String name, ENG_Layer layer, ENG_Container parent,
                         ENG_View parentView) {
        super(name, layer, parent, parentView);
        autoDetectPasswordType();
        
        boxRectangle = layer.createRectangle(0, 0, 0, 0);
        cursorRectangle = layer.createRectangle(0, 0, 0, 0);

        boxRectangle.backgroundColour(BOX_RECTANGLE_BACKGROUND_COLOR_DEFAULT);
        cursorRectangle.backgroundColour(CURSOR_RECTANGLE_BACKGROUND_COLOR_DEFAULT);

        boxRectangle.border(BOX_RECTANGLE_BORDER_WIDTH_DEFAULT, BOX_RECTANGLE_BORDER_COLOR_DEFAULT);
        cursorRectangle.border(CURSOR_RECTANGLE_BORDER_WIDTH_DEFAULT, CURSOR_RECTANGLE_BORDER_COLOR_DEFAULT);

        // So that first time we start with a visible cursor at blink time
        cursorRectangle.setVisible(false);
        boxRectangle.setVisible(false);

        setFocusable(true);

        removeAllEventListeners();
        setOnClickListener((x, y) -> true);
        setOnFocusListener(focused -> {

            if (!isWriteable()) {
                setCursorBlinking(focused);
                return true;
            }
            MainApp.getGame().setOnscreenKeyboardVisible(focused);
            setCursorBlinking(focused);
            if (focused) {
                cursorCharPos = getText().length();
                cursorShownCharPos = shownText.length();
            }
            justFocused = focused;
            // Force the _redraw to be called every frame
            markDirty();
            return true;
        });
        setOnKeyCodeListener((keyCode, type) -> {

            // Only key DOWN events seem to be sent on Desktop.
            if (type == KeyEventType.DOWN) {
                if (currentKeyCode != keyCode) {
                    currentKeyCode = keyCode;
                    currentKeyCodeTime = currentTimeMillis();
                    currentSpecialKeyCodeTime = currentTimeMillisReal();
                    firstDelay = true;
                    checkKeyCode(keyCode);
//                    specialKeyCodePressed = false;
                    checkSpecialKeyCode(keyCode);
                } else {
                    if (ENG_Utility.hasTimePassed(ENG_FrameInterval.TEXTFIELD_KEY_CODE_DELAY + getName(),
                            currentKeyCodeTime,
                            firstDelay ? keyCodeDelay : keyCodeShortDelay)) {
                        currentKeyCodeTime = currentTimeMillis();
                        firstDelay = false;
                        checkKeyCode(keyCode);
                    }
                    if (ENG_Utility.hasTimePassed(ENG_FrameInterval.TEXTFIELD_SPECIAL_KEY_CODE_DELAY + getName(),
                            currentSpecialKeyCodeTime, specialKeyCodeShortDelay)) {
                        currentSpecialKeyCodeTime = currentTimeMillis();
                        checkSpecialKeyCode(keyCode);
                    }
                }
            } else if (type == KeyEventType.UP) {
                if (currentKeyCode == keyCode) {
                    currentKeyCode = INVALID_KEY_CODE;
                }
//                specialKeyCodePressed = false;
            }
//				if (currentKeyCode == keyCode) {
//					if (ENG_Utility.hasTimePassed(
//								currentKeyCodeTime, keyCodeDelay)) {
//						currentKeyCodeTime = System.currentTimeMillis();
//						checkKeyCode(keyCode);
//					}
//				} else {
//					currentKeyCode = keyCode;
//					currentKeyCodeTime = 0;
//					checkKeyCode(keyCode);
//				}


            return true;
        });
        setOnCharacterListener(character -> {

            addCharacter(character);
//                System.out.println("Character " + character + " added");
            return true;
        });
    }

    private void autoDetectPasswordType() {
        switch (MainApp.PLATFORM) {
            case DESKTOP:
            case HTML:
                passwordType = PasswordType.SHOW_ASTERISK_DIRECTLY;
                break;
            case ANDROID:
            case IOS:
                passwordType = PasswordType.SHOW_ASTERISK_WITH_DELAY;
                break;
            default:
                throw new IllegalStateException("Unknown platform type: " + MainApp.PLATFORM);
        }
    }

    @Override
    public void destroy() {
        
        super.destroy();

        ENG_Layer layer = getLayer();
        layer.destroyRectangle(boxRectangle);
        layer.destroyRectangle(cursorRectangle);
        if (textCaption != null) {
            layer.destroyCaption(textCaption);
        }
    }

    public void moveToLayerDepth(int newLayerDepth) {
        textCaption.moveToLayerDepth(newLayerDepth);
        boxRectangle.moveToLayerDepth(newLayerDepth);
        cursorRectangle.moveToLayerDepth(newLayerDepth);
    }

    public int getLayerDepth() {
        return textCaption.getLayerDepth();
    }

    private void calculateCursorPos(float left, float top, String str) {
        textCaption._calculateDrawSize(
                str,
                calculatedTextSize);
        cursorPos.set(calculatedTextSize.x + left, top);
    }

    private void setTextFromChar(float width) {
        String currentText = text.substring(beginChar);
        int charNum = textCaption._calculateCharNum(currentText, width);
        while (charNum < currentText.length()) {
            currentText = currentText.substring(0, charNum);
            charNum = textCaption._calculateCharNum(currentText, width);
        }
        shownText = currentText;
        // The events are first before the draw update so we need to make
        // sure we at least draw once before we allow the view to get focused
//		cursorShownCharPos = shownText.length();
        endChar = beginChar + shownText.length();
    }

    private void moveCursorLeft() {
        if (cursorCharPos > 0) {
            --cursorCharPos;
            if (cursorShownCharPos > 0) {
                --cursorShownCharPos;
            } else {
                // Keep the cursor at the beginning pos but
                // move the text to the right
                if (beginChar > 0) {
                    --beginChar;
                    scrollDir = -1;
//					--scrollDir;
//					if (scrollDir < -10) {
//						scrollDir = -10;
//					}
                }
            }
        }
    }

    private void moveCursorRight() {
        if (cursorCharPos < text.length()) {
            ++cursorCharPos;
            if (cursorShownCharPos < shownText.length()) {
                ++cursorShownCharPos;
            } else {
                if (endChar < text.length()) {
                    ++endChar;
                    scrollDir = 1;
//					++scrollDir;
//					if (scrollDir > 10) {
//						scrollDir = 10;
//					}
//								++endChar;
                }
            }
        }
    }

    public boolean isAddCharacterUpdate() {
        return addCharacterUpdate;
    }

    public void setAddCharacterUpdate(boolean addCharacterUpdate, String s) {
//        if (addCharacterUpdate == this.addCharacterUpdate) {
//            return;
//        }
//        System.out.println("addCharacterUpdate: " + addCharacterUpdate + " " + s);
//        Thread.dumpStack();
        this.addCharacterUpdate = addCharacterUpdate;
    }

    private void addCharacter(char character) {
        if (isAllowSpaceKey()) {
            if (!ENG_StringUtility.isWordCharacterOrSpaceOrBackspace(character)) {
                return;
            }
        } else {
            if (!ENG_StringUtility.isWordCharacterOrBackspace(character)) {
                //			setText(getText() + character);
                return;
            }
        }
        if (ENG_StringUtility.isBackspace(character)) {
            deleteChar();
        } else {
            addAnyCharacter(character);
        }
    }

    private void addAnyCharacter(char character) {
        if (!isAddCharacterUpdate()) {
            // Only one character added per update call. Just to be sure
            return;
        }
//        System.out.println("Character added: " + character);
        setAddCharacterUpdate(false, "called from addAnyCharacter");
        text.insert(cursorCharPos, character);
        ++cursorCharPos;

//		tempShownText = new StringBuilder(shownText);
//		System.out.println("addCharacter()");
//		try {
//			tempShownText.insert(cursorShownCharPos, character);
//		} catch (ArrayIndexOutOfBoundsException e) {
//			e.printStackTrace();
//		}
        ++cursorShownCharPos;
        updateShownText = true;
        updateTextFieldChangeListeners();

//		++endChar;
//		if (cursorShownCharPos < (endChar - beginChar)) {
////			++cursorShownCharPos;
//			
////			--cursorShownCharPos;
//			scrollDir = -1;
//			updateShownText = true;
//		} else {
////			--cursorShownCharPos;
//			
//			scrollDir = 1;
//		}
//		if (cursorCharPos < text.length()) {
//			++cursorCharPos;
//			if (cursorShownCharPos < shownText.length()) {
//				++cursorShownCharPos;
//				
//			} else {
//				if (endChar < text.length()) {
//					++endChar;
//					scrollDir = 1;
//				}
//			}
//		}
    }

    private void updateText(float width, float widthWithoutCursor) {
        if (updateShownText) {
            scrollLeft(widthWithoutCursor);
            if (cursorShownCharPos >= endChar - beginChar) {
                endChar = beginChar + cursorShownCharPos;
                scrollRight(widthWithoutCursor);
            }
//			characterAdded = true;
//			scrollLeft(width);
//			String currentText = tempShownText.toString();
//			int charNum = textCaption._calculateCharNum(
//					currentText, widthWithoutCursor);
//			if (charNum < currentText.length()) {
//				// If we would need to eliminate the just added char
//				// then we should scrollRight. If we don't need to remove
//				// it then scrollLeft.
//				if (cursorShownCharPos < charNum) {
//					String currentTextViewable = 
//							currentText.substring(0, charNum);
//					int charNumWithoutCursor = textCaption._calculateCharNum(
//							currentTextViewable, widthWithoutCursor);
//					if (charNumWithoutCursor < currentTextViewable.length()) {
//						if (cursorShownCharPos < charNumWithoutCursor) {
//							scrollLeft(width);
//						} else {
//							scrollLeft(widthWithoutCursor);
//						}
//					} else {
//						++endChar;
////						--cursorShownCharPos;
//						int oldBeginChar = beginChar;
//						scrollRight(widthWithoutCursor);
//						cursorShownCharPos -= beginChar - oldBeginChar;
//					}
//				} else {
//					++endChar;
////					--cursorShownCharPos;
//					int oldBeginChar = beginChar;
//					scrollRight(widthWithoutCursor);
//					cursorShownCharPos -= beginChar - oldBeginChar;
//				}
//			} else {
//				++endChar;
//				shownText = tempShownText.toString();
//			}
            updateShownText = false;
        }
    }

    private void scrollLeft(float width) {
        String currentText = text.substring(beginChar);
        int charNum = textCaption._calculateCharNum(currentText, width);
        while (charNum < currentText.length()) {
            currentText = currentText.substring(0, charNum);
            charNum = textCaption._calculateCharNum(currentText, width);
        }
        shownText = currentText;
        // The events are first before the draw update so we need to make
        // sure we at least draw once before we allow the view to get focused
//		cursorShownCharPos = shownText.length();
        endChar = beginChar + shownText.length();
//		cursorShownCharPos = cursorCharPos - beginChar;
    }

    private void scrollRight(float width) {
        String currentText = text.substring(beginChar, endChar);
        int charNum = textCaption._calculateCharNum(currentText, width);
        while (charNum < currentText.length()) {
            currentText = currentText.substring(
                    currentText.length() - charNum);
            charNum = textCaption._calculateCharNum(currentText, width);
        }
        shownText = currentText;
        beginChar = endChar - shownText.length();
//		cursorShownCharPos = cursorCharPos - beginChar;
    }

    private void deleteChar() {
        if (!getText().isEmpty()) {
//				setText(getText().substring(0, getText().length() - 1));
            deleteChar = true;
            markDirty();

        }
    }

    private void removeCharFromText(float width, boolean removeFromPassword) {
        if (deleteChar) {
            if (cursorCharPos > 0) {
                text.deleteCharAt(cursorCharPos - 1);
                if (isPassword() && removeFromPassword) {
                    passwordText.deleteCharAt(cursorCharPos - 1);
                }
                String currentText = text.substring(beginChar);
                int charNum = textCaption._calculateCharNum(currentText, width);
                while (charNum < currentText.length()) {
                    currentText = currentText.substring(
                            currentText.length() - charNum);
                    charNum = textCaption._calculateCharNum(currentText, width);
                }
                shownText = currentText;
                endChar = beginChar + shownText.length();
                --cursorCharPos;
                if (cursorShownCharPos > 0) {
                    --cursorShownCharPos;
                }
                if (cursorShownCharPos == 0) {
                    // Keep the cursor at the beginning pos but
                    // move the text to the right
                    if (beginChar > 0) {
                        --beginChar;
                        scrollLeft(width);
                        ++cursorShownCharPos;
                    }
                }
                updateTextFieldChangeListeners();
            }
            deleteChar = false;
        }
    }

    @Override
    public void update(int screenWidth, int screenHeight) {
        
        super.update(screenWidth, screenHeight);
//		System.out.println("update()");
        createCaption();
        boolean isVisible = isVisible();
        textCaption.setVisible(isVisible);
        boxRectangle.setVisible(isVisible);
        if (!isVisible()) {
            setCursorBlinking(false);
            return;
        }
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
        textCaption.left(left);
        textCaption.top(top);
        textCaption.width(width);
        textCaption.height(height);
        textCaption.text(getText());
//		shownText = text;
        if (isWriteable()) {
            calculateCursorPos(left, top, getText());
            float widthWithoutCursor = width - cursorWidth;
            if (justFocused) {
                justFocused = false;
                // If the text is bigger than the width of the textfield
                if (calculatedTextSize.x + cursorWidth > width) {
                    String currentText = getText();

                    int charNum = textCaption._calculateCharNum(currentText, widthWithoutCursor);
                    beginChar = 0;

                    while (charNum < currentText.length()) {

                        //				if (charNum < text.length()) {
                        beginChar += currentText.length() - charNum;//text.indexOf(currentText);
                        currentText = currentText.substring(currentText.length() - charNum);
                        charNum = textCaption._calculateCharNum(currentText, widthWithoutCursor);
                        //				}
                    }
                    // Now we should have the correctly trimmed text that we must show
                    shownText = currentText;
                    endChar = beginChar + currentText.length();
                    textCaption.text(shownText);
                    calculateCursorPos(left, top, shownText);
                    cursorCharPos = text.length();
                    cursorShownCharPos = shownText.length();
                } else {
                    beginChar = 0;
                    endChar = getText().length();
                    shownText = getText();
                    cursorCharPos = text.length();
                }
            } else {
//			setTextFromChar(widthWithoutCursor);
//			shownText = text.substring(beginChar, endChar);
                if (isPassword()) {
//                    System.out.println("Update called");
                    if (passwordType == PasswordType.SHOW_ASTERISK_WITH_DELAY) {
                        if (updateShownText && (!characterAdded || characterShownWithPasswordInWait)) {
                            // We need to cheat since we add a character this frame.
                            // The * that replaces the real char.
                            currentPasswordCharTime = currentTimeMillis();
                            characterAdded = true;
                            try {
//                            System.out.println("passwordText: " + passwordText + ", text: " + text + ", cursorCharPos: " + cursorCharPos);
                                passwordText.insert(cursorCharPos - 1, text.charAt(cursorCharPos - 1));
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                            if (characterShownWithPasswordInWait) {
//                            System.out.println("characterShownWithPasswordInWait true");
                                setAddCharacterUpdate(true, "called from characterShownWithPasswordInWait");
                                cursorCharPos -= 2;
                                cursorShownCharPos -= 2;
                                text.deleteCharAt(cursorCharPos);
                                addAnyCharacter('*');
                                ++cursorCharPos;
                                ++cursorShownCharPos;
                                characterShownWithPasswordInWait = false;
//                            characterAdded = false;
                            }


//					deleteChar = true;
//					removeCharFromText(scrollWidth, false);

                        } else {

                        }
                        if (characterAdded) {
                            if (ENG_Utility.hasTimePassed(
                                    ENG_FrameInterval.PASSWORD_CHAR_TIME + getName(),
                                    currentPasswordCharTime, passwordCharTimeDelay) || !isFocused()) {
                                characterAdded = false;
                                setAddCharacterUpdate(true, "called from characterAdded");
                                --cursorCharPos;
                                --cursorShownCharPos;
                                text.deleteCharAt(cursorCharPos);
                                addAnyCharacter('*');
                                characterShownWithPasswordInWait = false;
//                            System.out.println("time passed for new added char");
                            } else {
                                characterShownWithPasswordInWait = true;
//                            System.out.println("Showing character in password");
                            }
                        }
                    } else if (passwordType == PasswordType.SHOW_ASTERISK_DIRECTLY) {
                        if (updateShownText) {
                            // We need to cheat since we add a character this frame.
                            // The * that replaces the real char.
                            addCharacterUpdate = true;
                            --cursorCharPos;
                            --cursorShownCharPos;
                            passwordText.insert(cursorCharPos, text.charAt(cursorCharPos));
                            text.deleteCharAt(cursorCharPos);
//					deleteChar = true;
//					removeCharFromText(scrollWidth, false);
                            addAnyCharacter('*');
                        }
                    }

//				}
                }
                updateText(width, widthWithoutCursor);
//			characterAdded = false;
                float scrollWidth = cursorShownCharPos >= shownText.length()
                        ? widthWithoutCursor : width;
                if (scrollDir == -1) {
                    scrollLeft(scrollWidth);
                } else if (scrollDir == 1) {
                    scrollRight(scrollWidth);
                }
                scrollDir = 0;
                removeCharFromText(scrollWidth, true);
                textCaption.text(shownText);
                String str;
                if (isFocused()) {
                    cursorShownCharPos = Math.min(cursorShownCharPos, shownText.length());
                    str = shownText.substring(0, cursorShownCharPos);
                } else {
                    str = shownText;
                }
                calculateCursorPos(left, top, str);
            }
            cursorRectangle.left(cursorPos.x);
            cursorRectangle.top(cursorPos.y);
            cursorRectangle.width(cursorWidth);
            cursorRectangle.height(Math.min(height, cursorHeight));
            if (isCursorBlinking()) {
                if (ENG_Utility.hasTimePassed(currentCursorTime, cursorBlinkTime)) {
                    currentCursorTime = currentTimeMillis();
                    cursorRectangle.setVisible(!cursorRectangle.isVisible());
                }
            } else {
                cursorRectangle.setVisible(false);
                currentCursorTime = 0;
            }
            if (isFocused()) {
                markDirty();
            }
            setAddCharacterUpdate(true, "called from update");
        }

    }

    private void createCaption() {
        if (textCaption == null) {
            textCaption = getLayer().createCaption(getTextSize(), 0, 0, text.toString());
        }
    }


    public int getTextSize() {
        return textSize;
    }

    /**
     * Make sure to set the textsize before calling update.
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public String getText() {
        return text.toString();
    }

    public void setText(String text) {
        if (!this.text.toString().equals(text)) {
            this.text.delete(0, this.text.length());
            this.text.append(text);
            cursorCharPos = text.length();
            shownText = text;
            cursorShownCharPos = shownText.length();
            markDirty();
        }
    }

    public float getCursorWidth() {
        return cursorWidth;
    }

    public void setCursorWidth(float cursorWidth) {
        if (Math.abs(this.cursorWidth - cursorWidth) < ENG_Math.FLOAT_EPSILON) {
            this.cursorWidth = cursorWidth;
            markDirty();
        }
    }

    public float getCursorHeight() {
        return cursorHeight;
    }

    public void setCursorHeight(float cursorHeight) {
        if (Math.abs(this.cursorHeight - cursorHeight) < ENG_Math.FLOAT_EPSILON) {
            this.cursorHeight = cursorHeight;
            markDirty();
        }
    }

    public float getCaptionDistanceFromBorder() {
        return captionDistanceFromBorder;
    }

    public void setCaptionDistanceFromBorder(float captionDistanceFromBorder) {
        this.captionDistanceFromBorder = captionDistanceFromBorder;
    }

    public long getCursorBlinkTime() {
        return cursorBlinkTime;
    }

    public void setCursorBlinkTime(long cursorBlinkTime) {
        this.cursorBlinkTime = cursorBlinkTime;
    }

    public boolean isCursorBlinking() {
        return cursorBlinking;
    }

    public void setCursorBlinking(boolean cursorBlinking) {
        this.cursorBlinking = cursorBlinking;
    }

    private void checkSpecialKeyCode(int keyCode) {
//        if (specialKeyCodePressed) {
//            return;
//        }
        if (keyCode == Keys.BACKSPACE) {
            if (onBackspacePressedListener != null) {
                onBackspacePressedListener.onBackspacePressed(getText());
//                specialKeyCodePressed = true;
            }
        } else if (keyCode == Keys.UP) {
            if (onUpDownPressedListener != null) {
                onUpDownPressedListener.onUpPressed(getText());
//                specialKeyCodePressed = true;
            }
        } else if (keyCode == Keys.DOWN) {
            if (onUpDownPressedListener != null) {
                onUpDownPressedListener.onDownPressed(getText());
//                specialKeyCodePressed = true;
            }
        } else if (keyCode == Keys.ENTER) {
            if (onReturnPressedListener != null) {
                onReturnPressedListener.onReturnPressed(getText());
//                specialKeyCodePressed = true;
            }
        } else if (keyCode == Keys.TAB) {
            if (onTabPressedListener != null) {
                onTabPressedListener.onTabPressed(getText());
            }
        }
    }

    private void checkKeyCode(int keyCode) {
        if (keyCode == Keys.BACKSPACE) {
            deleteChar();
        } else if (keyCode == Keys.LEFT) {
            moveCursorLeft();
            markDirty();
        } else if (keyCode == Keys.RIGHT) {
            moveCursorRight();
            markDirty();
        }
    }

    public long getKeyCodeDelay() {
        return keyCodeDelay;
    }

    public void setKeyCodeDelay(long keyCodeDelay) {
        this.keyCodeDelay = keyCodeDelay;
    }

    public long getKeyCodeShortDelay() {
        return keyCodeShortDelay;
    }

    public void setKeyCodeShortDelay(long keyCodeShortDelay) {
        this.keyCodeShortDelay = keyCodeShortDelay;
    }

    public long getSpecialKeyCodeShortDelay() {
        return specialKeyCodeShortDelay;
    }

    public void setSpecialKeyCodeShortDelay(long specialKeyCodeShortDelay) {
        this.specialKeyCodeShortDelay = specialKeyCodeShortDelay;
    }

    public boolean isAllowSpaceKey() {
        return allowSpaceKey;
    }

    public void setAllowSpaceKey(boolean allowSpaceKey) {
        this.allowSpaceKey = allowSpaceKey;
    }

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public long getPasswordCharTimeDelay() {
        return passwordCharTimeDelay;
    }

    public void setPasswordCharTimeDelay(long passwordCharTimeDelay) {
        this.passwordCharTimeDelay = passwordCharTimeDelay;
    }

    /**
     * Use this to return the password real text if the password is enabled.
     *
     * @return
     */
    public String getPasswordText() {
        return passwordText.toString();
    }

    /**
     * Hack for setting passwords from automation so that getPasswordText() functions correctly.
     * Don't use this in other places as the update() will not update the passwordText correctly
     * as it functions one character at a time. It was never designed to support setText()
     * when the text field is actually a password.
     * @param text
     */
    public void _setPasswordText(String text) {
        passwordText.setLength(0);
        passwordText.append(text);
    }

    public boolean isWriteable() {
        return writeable;
    }

    public void setWriteable(boolean writeable) {
        this.writeable = writeable;
    }

    public void setBoxRectangleBackgroundColor(ENG_ColorValue c) {
        boxRectangle.backgroundColour(c);
        markDirty();
    }

    public void setCursorRectangleBackgroundColor(ENG_ColorValue c) {
        cursorRectangle.backgroundColour(c);
        markDirty();
    }

    public ENG_ColorValue getBoxRectangleBackgroundColor() {
        return boxRectangle.backgroundColour(ENG_QuadCorner.TopLeft);
    }

    public ENG_ColorValue getCursorRectangleBackgroundColor() {
        return cursorRectangle.backgroundColour(ENG_QuadCorner.TopLeft);
    }

    public void setBoxRectangleBorder(float width, ENG_ColorValue c) {
        boxRectangle.border(width, c);
        markDirty();
    }

    public void setCursorRectangleBorder(float width, ENG_ColorValue c) {
        cursorRectangle.border(width, c);
        markDirty();
    }

    public float getBoxRectangleBorderWidth() {
        return boxRectangle.borderWidth();
    }

    public ENG_ColorValue getBoxRectangleBorderColor() {
        return boxRectangle.borderColour(ENG_Border.Border_North);
    }

    public float getCursorRectangleBorderWidth() {
        return cursorRectangle.borderWidth();
    }

    public ENG_ColorValue getCursorRectangleBorderColor() {
        return cursorRectangle.borderColour(ENG_Border.Border_North);
    }

    public ENG_TextView.HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(ENG_TextView.HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        if (isWriteable()) {
            throw new ENG_InvalidFieldStateException("Cannot be writeable");
        }
        createCaption();
        switch (horizontalAlignment) {
            case LEFT:
                textCaption.align(ENG_TextAlignment.TextAlign_Left);
                break;
            case CENTER:
                textCaption.align(ENG_TextAlignment.TextAlign_Centre);
                break;
            case RIGHT:
                textCaption.align(ENG_TextAlignment.TextAlign_Right);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public ENG_TextView.VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(ENG_TextView.VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        if (isWriteable()) {
            throw new ENG_InvalidFieldStateException("Cannot be writeable");
        }
        createCaption();
        switch (verticalAlignment) {
            case TOP:
                textCaption.verticalAlign(ENG_VerticalAlignment.VerticalAlign_Top);
                break;
            case CENTER:
                textCaption.verticalAlign(ENG_VerticalAlignment.VerticalAlign_Middle);
                break;
            case BOTTOM:
                textCaption.verticalAlign(ENG_VerticalAlignment.VerticalAlign_Bottom);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public PasswordType getPasswordType() {
        return passwordType;
    }

    public void setPasswordType(PasswordType passwordType) {
        this.passwordType = passwordType;
    }

    public void addTextFieldChangeListener(ENG_TextFieldChangeListener listener) {
        boolean shouldAdd = true;
        for (ENG_TextFieldChangeListener changeListener : textFieldChangeList) {
            if (changeListener.equals(listener)) {
                shouldAdd = false;
                break;
            }
        }
        if (shouldAdd) {
            textFieldChangeList.add(listener);
        }

    }

    public boolean removeTextFieldChangeListener(ENG_TextFieldChangeListener listener) {
        return textFieldChangeList.remove(listener);
    }

    public void removeAllTextFieldChangeListeners() {
        textFieldChangeList.clear();
    }

    private void updateTextFieldChangeListeners() {
        for (ENG_TextFieldChangeListener changeListener : textFieldChangeList) {
            changeListener.onTextChanged(getText());
        }

    }

    public ENG_TextFieldOnReturnPressedListener getOnReturnPressedListener() {
        return onReturnPressedListener;
    }

    public void setOnReturnPressedListener(ENG_TextFieldOnReturnPressedListener onReturnPressedListener) {
        this.onReturnPressedListener = onReturnPressedListener;
    }

    public ENG_TextFieldUpDownPressedListener getOnUpDownPressedListener() {
        return onUpDownPressedListener;
    }

    public void setOnUpDownPressedListener(ENG_TextFieldUpDownPressedListener onUpDownPressedListener) {
        this.onUpDownPressedListener = onUpDownPressedListener;
    }

    public ENG_TextFieldBackspacePressedListener getOnBackspacePressedListener() {
        return onBackspacePressedListener;
    }

    public void setOnBackspacePressedListener(ENG_TextFieldBackspacePressedListener onBackspacePressedListener) {
        this.onBackspacePressedListener = onBackspacePressedListener;
    }

    public ENG_TextFieldTabPressedListener getOnTabPressedListener() {
        return onTabPressedListener;
    }

    public void setOnTabPressedListener(ENG_TextFieldTabPressedListener onTabPressedListener) {
        this.onTabPressedListener = onTabPressedListener;
    }
}
