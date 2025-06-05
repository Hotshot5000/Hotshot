/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import org.apache.commons.io.Charsets;

import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;

import java.util.ArrayList;
import java.util.TreeMap;

public class ENG_MarkupText {

    private static class Character {
        public final ENG_Vector2D[] mPosition = new ENG_Vector2D[4];
        public final ENG_Vector2D[] mUV = new ENG_Vector2D[4];
        public final ENG_ColorValue mColour = new ENG_ColorValue();
        public int mIndex;

        public Character() {
            for (int i = 0; i < 4; ++i) {
                mPosition[i] = new ENG_Vector2D();
                mUV[i] = new ENG_Vector2D();
            }
        }
    }

    private static final float SCROLL_ARROW_LEN = 40.0f;

    protected final ENG_Layer mLayer;
    protected final ENG_GlyphData mDefaultGlyphData;
    protected float mLeft, mTop, mWidth, mHeight;
    protected float mMaxTextWidth, mMaxTextHeight;
    protected String mText = "", currentVisibleText, originalText;
    protected final ENG_ColorValue mBackground = new ENG_ColorValue();
    protected boolean mDirty = true, mTextDirty = true;
    final ArrayList<Character> mCharacters = new ArrayList<>();
    final ArrayList<ArrayList<ENG_Vertex>> mVertices = new ArrayList<>();
    protected int mClippedIndex;

    public static final String scrollUpSprite = "scrollbar_arrow_up";
    public static final String scrollDownSprite = "scrollbar_arrow_down";
    protected float textWidth; // For when the scrollbar is active
    protected int currentFirstVisibleCharIndex,
            currentLastVisibleCharindex; // One char past
    // The first char pos of every line
    protected final TreeMap<Integer, Integer> firstCharList = new TreeMap<>();
    // The last char + 1 pos of every line
    protected final TreeMap<Integer, Integer> lastCharList = new TreeMap<>();
    protected int currentLine = -1, maxLineNum, lineNum;
    protected boolean scrollbarActive;
    // We need a first pass in order to know if we actually need
    // the scrollbar and if we do then and only then do we add
    // the view listeners
    private boolean lineBreaksCalculated;
    private final ENG_RealRect scrollUpButton = new ENG_RealRect();
    private final ENG_RealRect scrollDownButton = new ENG_RealRect();
    private final ENG_ColorValue textColor = new ENG_ColorValue();
    private boolean textColorSet;
    private boolean visible = true;
    private int mLayerDepth;
    private final float scrollArrowLen;

    ENG_MarkupText(int defaultGlyphIndex,
                          float left, float top, String text, ENG_Layer l, int layerDepth, int atlasNum) {
        
        mLayer = l;
        mDefaultGlyphData = mLayer._getGlyphData(defaultGlyphIndex);
        if (mDefaultGlyphData == null) {
            throw new IllegalArgumentException(defaultGlyphIndex + " is not a valid glyph data");
        }
        mLayer._markDirty();
        mLeft = left;
        mTop = top;
        mText = text;
        mBackground.a = 0.0f;
        mLayerDepth = layerDepth;
        for (int i = 0; i < atlasNum; ++i) {
            mVertices.add(new ArrayList<>());
        }
        scrollArrowLen = SCROLL_ARROW_LEN * ENG_RenderRoot.getRenderRoot().getScreenDensity();
    }

    public int getLayerDepth() {
        return mLayerDepth;
    }

    public void moveToLayerDepth(int newLayerDepth) {
        mLayer.moveMarkupTextToLayerDepth(this, newLayerDepth);
        mLayerDepth = newLayerDepth;
    }

    private void calculateLineBreaks() {
        if (lineBreaksCalculated) {
            return;
        }
        float screenDensity = ENG_RenderRoot.getRenderRoot().getScreenDensity();
        float cursorX = mLeft, cursorY = mTop, kerning = 0,
                texelOffsetX = mLayer._getTexelX(),
                texelOffsetY = mLayer._getTexelY(),
                right = 0, bottom, left = 0, top;
        int thisChar, lastChar = 0;
        ENG_Glyph glyph;

        ENG_GlyphData glyphData = mDefaultGlyphData;
        float lineHeight = glyphData.mLineHeight * screenDensity;

        boolean fixedWidth = false, modified = false, scrollbarAdded = false;
        boolean newLine = true;

        lineNum = 1;
        currentLine = -1;
        firstCharList.clear();
        lastCharList.clear();
        currentFirstVisibleCharIndex = -1;
        currentLastVisibleCharindex = -1;


        StringBuilder textBuilder = new StringBuilder(originalText);
//		byte[] text = mText.getBytes();

        boolean firstChar = true;

        for (int i = 0; i < textBuilder.length(); ++i) {
            thisChar = textBuilder.charAt(i);

            if (newLine || firstChar) {
                currentFirstVisibleCharIndex = i;
                firstCharList.put(lineNum, i);
                firstChar = false;
            }

            if (thisChar == ' ') {
                lastChar = thisChar;
                cursorX += glyphData.mSpaceLength * screenDensity;
                continue;
            }

            if (thisChar == '\n') {
                if (lastChar == '\n') {
                    textBuilder.deleteCharAt(i);
                }
                lastChar = thisChar;
                cursorX = mLeft;
                cursorY += lineHeight;
                lineHeight = glyphData.mLineHeight * screenDensity;
                newLine = true;

                lastCharList.put(lineNum, i);
                ++lineNum;
                continue;
            } else {
                newLine = false;
            }


            glyph = glyphData.getGlyph(thisChar);
            if (!fixedWidth) {
                kerning = glyph.getKerning(lastChar);
                if (kerning == 0) {
                    kerning = glyphData.mLetterSpacing;
                }
                kerning *= screenDensity;
            }

            top = cursorY + glyph.verticalOffset;
            bottom = top + glyph.glyphHeight * screenDensity + texelOffsetY;

            if (fixedWidth) {
                cursorX += glyphData.mMonoWidth * screenDensity;
            } else {
                cursorX += glyph.glyphAdvance * screenDensity + kerning;
            }

            if (cursorX > mLeft + textWidth) {
                int lastSpacePos = -1;
                for (int j = i; j >= 0; --j) {
                    if (textBuilder.charAt(j) == ' ') {
                        lastSpacePos = j;
                        break;
                    }
                }
                if (lastSpacePos != -1) {
//					text[lastSpacePos] = '\n';
                    textBuilder.replace(lastSpacePos, lastSpacePos + 1, "\n");

                } else {
                    // Just break up the word
                /*	byte[] newText = new byte[text.length + 1];
					for (int j = 0; j < i; ++j) {
						newText[j] = text[j];
					}
					text[i] = '\n';
					for (int j = i; j < text.length; ++j) {
						newText[j + 1] = text[j];
					}
					text = newText;*/
                    textBuilder.insert(i, '\n');

                }
                modified = true;
                i = 0;
                lineNum = 1;
                cursorX = mLeft;
                cursorY = mTop;
                kerning = 0;
                continue;
            }
            if (bottom > mTop + mHeight && lineNum > 1) {
                // We should add a scroll bar
                if (!scrollbarAdded) {
                    // We must restart the whole line break calculation
                    // since we have modified the text width
                    scrollbarActive = true;
                    textWidth = mWidth - scrollArrowLen - 5.0f;
//					text = mText.getBytes();
                    i = 0;
                    maxLineNum = lineNum - 2;
                    scrollbarAdded = true;
                    lineNum = 1;
                    cursorX = mLeft;
                    cursorY = mTop;
                    kerning = 0;
                    // Need to rebuild everything since we don't want double \n
                    // one word next to the other
                    textBuilder = new StringBuilder(mText);
                }
            }
            currentLastVisibleCharindex = i + 1;
            lastChar = thisChar;
        }
        // Don't forget the last character. Might not be a new line
        if (!lastCharList.containsValue(currentLastVisibleCharindex)) {
            lastCharList.put(lineNum, currentLastVisibleCharindex);
        }
        if (!scrollbarAdded) {
            maxLineNum = lineNum - 1;
        }
        if (modified) {
            mText = textBuilder.toString();
        }
        createCurrentText(1);
        lineBreaksCalculated = true;
    }

    private void createCurrentText(int line) {
        if (currentLine == line) {
            return;
        }
        if (line < 1 || line > lineNum) {
            throw new IllegalArgumentException(line + " is out of range 0 " + lineNum);
        }
        int lastLine = line + maxLineNum;
        Integer fc = firstCharList.get(line);
        Integer lc = lastCharList.get(lastLine);
        if (fc != null && lc != null) {
            int firstChar = fc;
            int lastChar = lc;
            if (firstChar == -1 || lastChar == -1) {
                currentVisibleText = "";
            } else {
                currentVisibleText = mText.substring(firstChar, lastChar);
            }
        } else {
            currentVisibleText = "";
        }
        currentLine = line;
        mDirty = true;
        mTextDirty = true;
        mLayer._markDirty();
    }

    public void nextLine() {
        if (currentLine + maxLineNum + 1 > lineNum) {
            return;
        }
        createCurrentText(currentLine + 1);
    }

    public void previousLine() {
        if (currentLine <= 1) {
            return;
        }
        createCurrentText(currentLine - 1);
    }

    private void addScrollbar() {
        ENG_Sprite up = mLayer.getAtlas().getSprite(scrollUpSprite);
        ENG_Sprite down = mLayer.getAtlas().getSprite(scrollDownSprite);
        float scrollLeft = mLeft + mWidth - scrollArrowLen;
        float scrollTop = mTop;
        float scrollRight = mLeft + mWidth;
        float scrollBottom = mTop + scrollArrowLen;
        scrollUpButton.set(scrollLeft, scrollTop, scrollRight, scrollBottom);
        mCharacters.add(createCharacter(
                0, scrollLeft, scrollTop, scrollRight, scrollBottom,
                up.texCoords[0], up.texCoords[1],
                up.texCoords[2], up.texCoords[3],
                colour));
        scrollLeft = mLeft + mWidth - scrollArrowLen;
        scrollTop = mTop + mHeight - scrollArrowLen;
        scrollRight = mLeft + mWidth;
        scrollBottom = mTop + mHeight;
        scrollDownButton.set(scrollLeft, scrollTop, scrollRight, scrollBottom);
        mCharacters.add(createCharacter(
                0, scrollLeft, scrollTop, scrollRight, scrollBottom,
                down.texCoords[0], down.texCoords[1],
                down.texCoords[2], down.texCoords[3],
                colour));
    }
	
/*	public void calculateMaxLineSize() {
		mMaxTextWidth = 0;
		mMaxTextHeight = 0;
	}*/

    protected final ENG_ColorValue colour = new ENG_ColorValue();

    public void _calculateCharacters() {
        if (!mTextDirty) {
            return;
        }
        if (!isVisible()) {
            return;
        }

        calculateLineBreaks();
        float screenDensity = ENG_RenderRoot.getRenderRoot().getScreenDensity();
        float cursorX = mLeft, cursorY = mTop, kerning = 0,
                texelOffsetX = mLayer._getTexelX(),
                texelOffsetY = mLayer._getTexelY(),
                right, bottom, left, top;
        int thisChar, lastChar = 0;
        ENG_Glyph glyph = null;

        mMaxTextWidth = 0;
        mMaxTextHeight = 0;

        mCharacters.clear();

        boolean markupMode = false;
        if (!textColorSet) {
            mLayer._getMarkupColour(0, colour);
        } else {
            colour.set(textColor);
        }
        boolean fixedWidth = false;

        ENG_GlyphData glyphData = mDefaultGlyphData;
        float lineHeight = glyphData.mLineHeight * screenDensity;

        byte[] text = currentVisibleText.getBytes(Charsets.US_ASCII);

        for (int i = 0; i < text.length; ++i) {
            thisChar = text[i];

            if (thisChar == ' ') {
                lastChar = thisChar;
                cursorX += glyphData.mSpaceLength * screenDensity;
                continue;
            }

            if (thisChar == '\n') {
                lastChar = thisChar;
                cursorX = mLeft;
                cursorY += lineHeight;
                lineHeight = glyphData.mLineHeight * screenDensity;
                continue;
            }

            if (thisChar < glyphData.mRangeBegin ||
                    thisChar > glyphData.mRangeEnd) {
                lastChar = 0;
                continue;
            }

            if (thisChar == '%' && !markupMode) {
                markupMode = true;
                continue;
            }

            if (markupMode) {
                if (thisChar == '%') {

                } else {
                    markupMode = false;

                    if (thisChar >= '0' && thisChar <= '9') {
                        if (!textColorSet) {
                            mLayer._getMarkupColour(thisChar - 48, colour);
                        } else {
                            colour.set(textColor);
                        }
                    } else if (thisChar == 'R' || thisChar == 'r') {
                        if (!textColorSet) {
                            mLayer._getMarkupColour(0, colour);
                        } else {
                            colour.set(textColor);
                        }
                    } else if (thisChar == 'M' || thisChar == 'm') {
                        fixedWidth = !fixedWidth;
                    } else if (thisChar == '@') {
                        markupMode = false;
                        boolean foundIt = false;
                        int begin = i;
                        while (i < text.length) {
                            if (text[i] == '%') {
                                foundIt = true;
                                break;
                            }
                            ++i;
                        }
                        if (!foundIt) {
                            return;
                        }
                        int index = Integer.parseInt(
                                currentVisibleText.substring(begin + 1, i));
                        glyphData = mLayer._getGlyphData(index);
                        if (glyphData == null) {
                            return;
                        }
                        lineHeight = Math.max(lineHeight, glyphData.mLineHeight) * screenDensity;
                        continue;
                    } else if (thisChar == ':') {
                        markupMode = false;
                        boolean foundIt = false;
                        int begin = i;
                        while (i < text.length) {
                            if (text[i] == '%') {
                                foundIt = true;
                                break;
                            }
                            ++i;
                        }
                        if (!foundIt) {
                            return;
                        }
                        String spriteName =
                                currentVisibleText.substring(begin + 1, i);
                        ENG_Sprite sprite = mLayer._getSprite(spriteName);
                        if (sprite == null) {
                            continue;
                        }

                        left = cursorX - texelOffsetX;
                        top = cursorY - texelOffsetY + glyph.verticalOffset;
                        right = left + sprite.spriteWidth * screenDensity + texelOffsetX;
                        bottom = top + sprite.spriteHeight * screenDensity + texelOffsetY;

                        mCharacters.add(createCharacter(
                                i, left, top, right, bottom,
                                glyph.texCoords[0], glyph.texCoords[1],
                                glyph.texCoords[2], glyph.texCoords[3], colour));

                        cursorX += sprite.spriteWidth * screenDensity;

                        lineHeight = Math.max(lineHeight, sprite.spriteHeight) * screenDensity;
                        continue;
                    }
                    continue;
                }
                markupMode = false;
            }
            glyph = glyphData.getGlyph(thisChar);
            if (!fixedWidth) {
                kerning = glyph.getKerning(lastChar);
                if (kerning == 0) {
                    kerning = glyphData.mLetterSpacing;
                }
                kerning *= screenDensity;
            }

            left = cursorX;
            top = cursorY + glyph.verticalOffset;
            right = cursorX + glyph.glyphWidth * screenDensity + texelOffsetX;
            bottom = top + glyph.glyphHeight * screenDensity + texelOffsetY;


            mCharacters.add(createCharacter(i, left, top, right, bottom,
                    glyph.texCoords[0], glyph.texCoords[1],
                    glyph.texCoords[2], glyph.texCoords[3], colour));

            if (fixedWidth) {
                cursorX += glyphData.mMonoWidth * screenDensity;
            } else {
                cursorX += glyph.glyphAdvance * screenDensity + kerning;
            }

            if (cursorX > mMaxTextWidth) {
                mMaxTextWidth = cursorX;
            }
            if (bottom > mMaxTextHeight) {
                mMaxTextHeight = bottom;
            }
            lastChar = thisChar;
        }
        mMaxTextWidth -= mLeft;
        mMaxTextHeight -= mTop;

        if (scrollbarActive) {
            addScrollbar();
        }

        mTextDirty = false;
    }

    private Character createCharacter(int index, float left,
                                      float top, float right, float bottom,
                                      ENG_Vector2D uv0, ENG_Vector2D uv1,
                                      ENG_Vector2D uv2, ENG_Vector2D uv3,
                                      ENG_ColorValue colour) {
        Character c = new Character();
        c.mIndex = index;
        c.mPosition[ENG_QuadCorner.TopLeft.getCorner()].x = left;
        c.mPosition[ENG_QuadCorner.TopLeft.getCorner()].y = top;
        c.mPosition[ENG_QuadCorner.TopRight.getCorner()].x = right;
        c.mPosition[ENG_QuadCorner.TopRight.getCorner()].y = top;
        c.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].x = left;
        c.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].y = bottom;
        c.mPosition[ENG_QuadCorner.BottomRight.getCorner()].x = right;
        c.mPosition[ENG_QuadCorner.BottomRight.getCorner()].y = bottom;
        c.mUV[0].set(uv0);
        c.mUV[1].set(uv1);
        c.mUV[2].set(uv2);
        c.mUV[3].set(uv3);
        c.mColour.set(colour);
        return c;
    }

    public void _redraw() {
        if (!mDirty) {
            return;
        }
        for (ArrayList<ENG_Vertex> vertices : mVertices) {
            vertices.clear();
        }
//		mVertices.clear();
        ArrayList<ENG_Vertex> vertices = mVertices.get(0);
        if (visible) {
            for (Character c : mCharacters) {
                ENG_GorillaUtility.pushQuad2(vertices,
                        c.mPosition, c.mColour, c.mUV);
            }
        }
        mDirty = false;
    }

    public float left() {
        return mLeft;
    }

    public void left(float l) {
        if (ENG_Float.isEqual(mLeft, l)) {
            return;
        }
        mLeft = l;
        mDirty = true;
        mTextDirty = true;
        lineBreaksCalculated = false;
        mLayer._markDirty();
    }

    public float top() {
        return mTop;
    }

    public void top(float t) {
        if (ENG_Float.isEqual(mTop, t)) {
            return;
        }
        mTop = t;
        mDirty = true;
        mTextDirty = true;
        lineBreaksCalculated = false;
        mLayer._markDirty();
    }

    public void size(float w, float h) {
        if (ENG_Float.isEqual(mWidth, w) && ENG_Float.isEqual(mHeight, h)) {
            return;
        }
        mWidth = w;
        mHeight = h;
        mDirty = true;
        mTextDirty = true;
        lineBreaksCalculated = false;
        mLayer._markDirty();
    }

    public float width() {
        return mWidth;
    }

    public void width(float w) {
        if (ENG_Float.isEqual(mWidth, w)) {
            return;
        }
        mWidth = w;
        textWidth = w;
        mDirty = true;
        mTextDirty = true;
        lineBreaksCalculated = false;
        mLayer._markDirty();
    }

    public float height() {
        return mHeight;
    }

    public void height(float h) {
        if (ENG_Float.isEqual(mHeight, h)) {
            return;
        }
        mHeight = h;
        mDirty = true;
        mTextDirty = true;
        lineBreaksCalculated = false;
        mLayer._markDirty();
    }

    public float maxTextWidth() {
        _calculateCharacters();
        return mMaxTextWidth;
    }

    public float maxTextHeight() {
        _calculateCharacters();
        return mMaxTextHeight;
    }

    /**
     * No idea if this actually works... Wrote this class way too long ago
     *
     * @return
     */
    public int getLineNum() {
        _calculateCharacters();
        return lineNum;
    }

    public String text() {
        return mText;
    }

    public void text(String t) {
        text(t, true);
    }

    public void text(String t, boolean skipNewLines) {
        // Make sure we exit early or else when scrolling the scrollbar
        // we end up setting the same text again which creates the need
        // to recalculate the line breaks
        if (originalText != null && originalText.equals(t)) {
            return;
        }
        originalText = t;
        mText = t;
        if (skipNewLines) {
            mText = mText.replace('\n', ' ');
        }
        mDirty = true;
        mTextDirty = true;
        mLayer._markDirty();
        // We need to wait for the next render pass in order for this
        // to become true
        lineBreaksCalculated = false;
    }

    public ENG_ColorValue background() {
        return new ENG_ColorValue(mBackground);
    }

    public void background(ENG_ColorValue c) {
        mBackground.set(c);
        mDirty = true;
        mLayer._markDirty();
    }

    public void background(ENG_Colour c) {
        if (c == ENG_Colour.None) {
            mBackground.a = 0.0f;
        } else {
            mBackground.setAsRGB(c.getColour(), 1.0f);
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public boolean isScrollbarActive() {
        return scrollbarActive;
    }

    public boolean isLineBreaksCalculated() {
        return lineBreaksCalculated;
    }

    /**
     * For use only in textview. Make sure to not modify
     *
     * @return
     */
    public ENG_RealRect getScrollDownButton() {
        return scrollDownButton;
    }

    /**
     * For use only in textview. Make sure to not modify
     *
     * @return
     */
    public ENG_RealRect getScrollUpButton() {
        return scrollUpButton;
    }

    public boolean isTextColorSet() {
        return textColorSet;
    }

    public void setTextColorSet(boolean textColorSet) {
        this.textColorSet = textColorSet;
    }

    public void setTextColor(ENG_ColorValue c) {
        textColor.set(c);
    }

    public void getTextColor(ENG_ColorValue ret) {
        ret.set(textColor);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            mLayer._markDirty();
            mDirty = true;
        }
    }

}
