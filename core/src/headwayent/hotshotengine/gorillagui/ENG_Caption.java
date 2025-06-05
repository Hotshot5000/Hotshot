/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import org.apache.commons.io.Charsets;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;

import java.util.ArrayList;

public class ENG_Caption {

    protected final ENG_Layer mLayer;
    protected ENG_GlyphData mGlyphData;
    protected float mLeft, mTop, mWidth, mHeight;
    protected ENG_TextAlignment mAlignment;
    protected ENG_VerticalAlignment mVerticalAlign;
    protected String mText = "";
    protected final ENG_ColorValue mColour = new ENG_ColorValue();
    protected final ENG_ColorValue mBackground = new ENG_ColorValue();
    protected boolean mDirty = true, visible = true;
    final ArrayList<ArrayList<ENG_Vertex>> mVertices = new ArrayList<>();
    protected int mClippedLeftIndex, mClippedRightIndex;
    protected boolean mSizeCalculationDirty = true;
    protected int mLayerDepth;

    ENG_Caption(int glyphDataIndex, float left, float top,
                       String caption, ENG_Layer parent, int layerDepth, int atlasNum) {

        mLayer = parent;
        mGlyphData = mLayer._getGlyphData(glyphDataIndex);
        if (mGlyphData == null) {
            throw new IllegalArgumentException(glyphDataIndex +
                    " is not a valid glyph data index for layer");
        }
        mLayer._markDirty();
        mLeft = left;
        mTop = top;
        mWidth = 0.0f;
        mHeight = 0.0f;
        mText = caption;
        mColour.set(ENG_ColorValue.WHITE);
        mBackground.a = 0.0f;
        mAlignment = ENG_TextAlignment.TextAlign_Left;
        mVerticalAlign = ENG_VerticalAlignment.VerticalAlign_Top;
        mLayerDepth = layerDepth;
        for (int i = 0; i < atlasNum; ++i) {
            mVertices.add(new ArrayList<>());
        }
    }

    public int getLayerDepth() {
        return mLayerDepth;
    }

    public void moveToLayerDepth(int newLayerDepth) {
        mLayer.moveCaptionToLayerDepth(this, newLayerDepth);
        mLayerDepth = newLayerDepth;
    }

    /**
     * @param s
     * @param maxLength
     * @return The number of characters that fit the length specified
     */
    public int _calculateCharNum(String s, float maxLength) {
        float cursor = 0, kerning = 0;
        ENG_Glyph glyph;
        byte thisChar, lastChar = 0;
        int charCount = 0;
        byte[] text = s.getBytes(Charsets.US_ASCII);
        float screenDensity = ENG_RenderRoot.getRenderRoot().getScreenDensity();
        for (byte value : text) {
            thisChar = value;
            if (thisChar == ' ') {
                lastChar = thisChar;
                cursor += mGlyphData.mSpaceLength * screenDensity;
                if (cursor > maxLength) {
                    return charCount;
                }
                ++charCount;
                continue;
            }
            if (thisChar < mGlyphData.mRangeBegin ||
                    thisChar > mGlyphData.mRangeEnd) {
                lastChar = 0;
                continue;
            }
            glyph = mGlyphData.getGlyph(thisChar);
            if (glyph == null) {
                continue;
            }
            kerning = glyph.getKerning(lastChar);
            if (kerning == 0.0f) {
                kerning = mGlyphData.mLetterSpacing;
            }
            kerning *= screenDensity;
            cursor += glyph.glyphAdvance * screenDensity + kerning;
            if (cursor > maxLength) {
                return charCount;
            }
            ++charCount;
            lastChar = thisChar;
        }
        cursor -= kerning;
        if (cursor > maxLength) {
            return charCount - 1;
        }
        return charCount;
    }

    public String _fitDrawSize(String textStr, float width, String ellipsizeText, ENG_Vector2D retSize) {
        textStr = textStr.trim();
        String origVersion = textStr;
        int charsToDelete = 1;
        _calculateDrawSize(textStr, retSize);

        while (retSize.x > width) {
            if (textStr.length() > charsToDelete) {
                textStr = origVersion.substring(0, origVersion.length() - (charsToDelete++));
                textStr = textStr.trim();
                textStr += ellipsizeText;
            } else {
                retSize.x = 0.0f;
                break;
            }
            _calculateDrawSize(textStr, retSize);
        }
        return textStr;
    }

    public void _calculateDrawSize(String textStr, ENG_Vector2D retSize) {
        retSize.x = 0;
        retSize.y = mGlyphData.mLineHeight * ENG_RenderRoot.getRenderRoot().getScreenDensity();

        float cursor = 0, kerning = 0;
        ENG_Glyph glyph;
        byte thisChar, lastChar = 0;
        float screenDensity = ENG_RenderRoot.getRenderRoot().getScreenDensity();
        byte[] text = textStr.getBytes(Charsets.US_ASCII);
        for (byte value : text) {
            thisChar = value;
            if (thisChar == ' ') {
                lastChar = thisChar;
                cursor += mGlyphData.mSpaceLength * screenDensity;
                continue;
            }
            if (thisChar < mGlyphData.mRangeBegin ||
                    thisChar > mGlyphData.mRangeEnd) {
                lastChar = 0;
                continue;
            }
            glyph = mGlyphData.getGlyph(thisChar);
            if (glyph == null) {
                continue;
            }
            kerning = glyph.getKerning(lastChar);
            if (kerning == 0.0f) {
                kerning = mGlyphData.mLetterSpacing;
            }
            kerning *= screenDensity;
            cursor += glyph.glyphAdvance * screenDensity + kerning;
            lastChar = thisChar;
        }
        if (text.length > 0) {
            retSize.x = cursor - kerning;
        }
    }

    private final ENG_Vector2D drawSize = new ENG_Vector2D();

    public void _calculateDrawSize(ENG_Vector2D retSize) {
        if (!mSizeCalculationDirty) {
            retSize.set(drawSize);
            return;
        }
        _calculateDrawSize(mText, retSize);
        mSizeCalculationDirty = false;
        drawSize.set(retSize);
    }

    protected final ENG_Vector2D uv = new ENG_Vector2D();
    protected final ENG_Vector2D a = new ENG_Vector2D();
    protected final ENG_Vector2D b = new ENG_Vector2D();
    protected final ENG_Vector2D c = new ENG_Vector2D();
    protected final ENG_Vector2D d = new ENG_Vector2D();
    protected final ENG_Vector2D knownSize = new ENG_Vector2D();

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
            mLayer._getSolidUV(uv);

            if (mBackground.a > 0.0f) {
                a.x = mLeft;
                a.y = mTop;
                b.x = mLeft + mWidth;
                b.y = mTop;
                c.x = mLeft;
                c.y = mTop + mHeight;
                d.x = mLeft + mWidth;
                d.y = c.y = mTop + mHeight;

                ENG_GorillaUtility.pushTriangle(vertices, c, b, a, uv, mBackground);
                ENG_GorillaUtility.pushTriangle(vertices, c, d, b, uv, mBackground);
            }

            float screenDensity = ENG_RenderRoot.getRenderRoot().getScreenDensity();
            float left, top, right, bottom,
                    cursorX = 0, cursorY = 0, kerning,
                    texelOffsetX = mLayer._getTexelX(),
                    texelOffsetY = mLayer._getTexelY();
            ENG_Glyph glyph;

            boolean clipLeft = false, clipRight = false;
            float clipLeftPos = 0, clipRightPos = 0;

            if (mAlignment == ENG_TextAlignment.TextAlign_Left) {
                cursorX = mLeft;
                if (mWidth != 0.0f) {
                    clipRight = true;
                    clipRightPos = mLeft + mWidth;
                }
            } else if (mAlignment == ENG_TextAlignment.TextAlign_Centre) {
                _calculateDrawSize(knownSize);
                cursorX = mLeft + (mWidth * 0.5f) - (knownSize.x * 0.5f);
                if (mWidth != 0.0f) {
                    clipLeft = true;
                    clipLeftPos = mLeft;
                    clipRight = true;
                    clipRightPos = mLeft + mWidth;
                }
            } else if (mAlignment == ENG_TextAlignment.TextAlign_Right) {
                _calculateDrawSize(knownSize);
                cursorX = mLeft + mWidth - knownSize.x;
                if (mWidth != 0.0f) {
                    clipLeft = true;
                    clipLeftPos = mLeft;
                }
            }

            if (mVerticalAlign == ENG_VerticalAlignment.VerticalAlign_Top) {
                cursorY = mTop;
            } else if (mVerticalAlign == ENG_VerticalAlignment.VerticalAlign_Middle) {
                cursorY = mTop + (mHeight * 0.5f) - (mGlyphData.mLineHeight * 0.5f * ENG_RenderRoot.getRenderRoot().getScreenDensity());
            } else if (mVerticalAlign == ENG_VerticalAlignment.VerticalAlign_Bottom) {
                cursorY = mTop + mHeight - mGlyphData.mLineHeight * ENG_RenderRoot.getRenderRoot().getScreenDensity();
            }

            byte thisChar, lastChar = 0;

            cursorX = ENG_Math.floor(cursorX);
            cursorY = ENG_Math.floor(cursorY);

            mClippedLeftIndex = -1;
            mClippedRightIndex = -1;

            byte[] text = mText.getBytes(Charsets.US_ASCII);

            for (int i = 0; i < text.length; ++i) {
                thisChar = text[i];
                if (thisChar == ' ') {
                    lastChar = thisChar;
                    cursorX += mGlyphData.mSpaceLength * screenDensity;
                    continue;
                }
                if (thisChar < mGlyphData.mRangeBegin ||
                        thisChar > mGlyphData.mRangeEnd) {
                    lastChar = 0;
                    continue;
                }
                glyph = mGlyphData.getGlyph(thisChar);
                if (glyph == null) {
                    continue;
                }
                kerning = glyph.getKerning(lastChar);
                if (kerning == 0.0f) {
                    kerning = mGlyphData.mLetterSpacing;
                }
                kerning *= screenDensity;

                left = cursorX - texelOffsetX;
                top = cursorY - texelOffsetY + glyph.verticalOffset;
                right = left + glyph.glyphWidth * screenDensity + texelOffsetX;
                bottom = top + glyph.glyphHeight * screenDensity + texelOffsetY;

                if (clipLeft) {
                    if (left < clipLeftPos) {
                        if (mClippedLeftIndex == -1) {
                            mClippedLeftIndex = i;
                        }
                        cursorX += glyph.glyphAdvance + kerning;
                        lastChar = thisChar;
                        continue;
                    }
                }
                if (clipRight) {
                    if (right > clipRightPos) {
                        if (mClippedRightIndex == -1) {
                            mClippedRightIndex = i;
                        }
                        cursorX += glyph.glyphAdvance + kerning;
                        lastChar = thisChar;
                        continue;
                    }
                }

                // Triangle A
                ENG_GorillaUtility.pushVertex(vertices, left, bottom, glyph.texCoords[ENG_QuadCorner.BottomLeft.getCorner()], mColour);  // Left/Bottom  3
                ENG_GorillaUtility.pushVertex(vertices, right, top, glyph.texCoords[ENG_QuadCorner.TopRight.getCorner()], mColour); // Right/Top 1
                ENG_GorillaUtility.pushVertex(vertices, left, top, glyph.texCoords[ENG_QuadCorner.TopLeft.getCorner()], mColour);  // Left/Top  0

                // Triangle B
                ENG_GorillaUtility.pushVertex(vertices, left, bottom, glyph.texCoords[ENG_QuadCorner.BottomLeft.getCorner()], mColour);  // Left/Bottom  3
                ENG_GorillaUtility.pushVertex(vertices, right, bottom, glyph.texCoords[ENG_QuadCorner.BottomRight.getCorner()], mColour); // Right/Bottom 2
                ENG_GorillaUtility.pushVertex(vertices, right, top, glyph.texCoords[ENG_QuadCorner.TopRight.getCorner()], mColour); // Right/Top 1

                cursorX += glyph.glyphAdvance * screenDensity + kerning;
                lastChar = thisChar;
            }
        }
        mDirty = false;
    }

    public void font(int fontIndex) {
        mGlyphData = mLayer._getGlyphData(fontIndex);
        if (mGlyphData == null) {
            throw new IllegalArgumentException(fontIndex + " is not a valid " +
                    "font index");
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public boolean intersects(ENG_Vector2D coordinates) {
        return ((coordinates.x >= mLeft && coordinates.x <= mLeft + mWidth) &&
                (coordinates.y >= mTop && coordinates.y <= mTop + mHeight));
    }

    public float left() {
        return mLeft;
    }

    public void left(float l) {
        mLeft = l;
        mDirty = true;
        mLayer._markDirty();
    }

    public float top() {
        return mTop;
    }

    public void top(float t) {
        mTop = t;
        mDirty = true;
        mLayer._markDirty();
    }

    public void size(float width, float height) {
        mWidth = width;
        mHeight = height;
        mDirty = true;
        mLayer._markDirty();
    }

    public float width() {
        return mWidth;
    }

    public void width(float w) {
        mWidth = w;
        mDirty = true;
        mLayer._markDirty();
    }

    public float height() {
        return mHeight;
    }

    public void height(float h) {
        mHeight = h;
        mDirty = true;
        mLayer._markDirty();
    }

    public String text() {
        return mText;
    }

    public void text(String t) {
        if (mText.equals(t)) {
            return;
        }
        mText = t;
        mDirty = true;
        mSizeCalculationDirty = true;
        mLayer._markDirty();
    }

    public ENG_TextAlignment align() {
        return mAlignment;
    }

    public void align(ENG_TextAlignment a) {
        mAlignment = a;
        mDirty = true;
        mLayer._markDirty();
    }

    public ENG_VerticalAlignment verticalAlign() {
        return mVerticalAlign;
    }

    public void verticalAlign(ENG_VerticalAlignment v) {
        mVerticalAlign = v;
        mDirty = true;
        mLayer._markDirty();
    }

    public int clippedLeftIndex() {
        return mClippedLeftIndex;
    }

    public int clippedRightIndex() {
        return mClippedRightIndex;
    }

    public ENG_ColorValue colour() {
        return new ENG_ColorValue(mColour);
    }

    public void colour(ENG_ColorValue c) {
        mColour.set(c);
        mDirty = true;
        mLayer._markDirty();
    }

    public void colour(ENG_Colour c) {
        mColour.setAsRGB(c.getColour(), 1.0f);
        mDirty = true;
        mLayer._markDirty();
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

    public void noBackground() {
        mBackground.a = 0.0f;
        mDirty = true;
        mLayer._markDirty();
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
