/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import com.badlogic.gdx.Gdx;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.ArrayList;

public class ENG_Rectangle {

    private static final String TAG = ENG_Rectangle.class.getSimpleName();

    protected final ENG_Layer mLayer;
    protected float mLeft, mTop, mRight, mBottom, mBorderWidth;
    protected final ENG_ColorValue[] mBackgroundColour = new ENG_ColorValue[4];
    protected final ENG_ColorValue[] mBorderColour = new ENG_ColorValue[4];
    protected final ENG_Vector2D[] mUV = new ENG_Vector2D[4];
    protected boolean mDirty = true, visible = true;
    final ArrayList<ArrayList<ENG_Vertex>> mVertices = new ArrayList<>();
    protected int mLayerDepth;
    protected ENG_NinePatch mNinePatch;
    protected final ENG_ColorValue[] mNinePatchColour = new ENG_ColorValue[16];
    private int mNinePatchTextureIndex;
    private final int mAtlasNum;

    ENG_Rectangle(float left, float top, float width, float height,
                         ENG_Layer layer, int layerDepth, int atlasNum) {

        for (int i = 0; i < mBackgroundColour.length; ++i) {
            mBackgroundColour[i] = new ENG_ColorValue(ENG_ColorValue.WHITE);
        }
        for (int i = 0; i < mBorderColour.length; ++i) {
            mBorderColour[i] = new ENG_ColorValue();
        }
        for (int i = 0; i < mNinePatchColour.length; ++i) {
            mNinePatchColour[i] = new ENG_ColorValue(ENG_ColorValue.WHITE);
        }
        mLeft = left;
        mTop = top;
        mRight = left + width;
        mBottom = top + height;
        mBorderWidth = 0.0f;
        mLayer = layer;

        ENG_Vector2D v = mLayer._getSolidUV();
        for (int i = 0; i < mUV.length; ++i) {
            mUV[i] = new ENG_Vector2D(v);
        }
        mLayerDepth = layerDepth;
        mAtlasNum = atlasNum;
        for (int i = 0; i < mAtlasNum; ++i) {
            mVertices.add(new ArrayList<>());
        }
    }

    public int getLayerDepth() {
        return mLayerDepth;
    }

    public void moveToLayerDepth(int newLayerDepth) {
        mLayer.moveRectangleLayerDepth(this, newLayerDepth);
        mLayerDepth = newLayerDepth;
    }

    public boolean intersects(ENG_Vector2D coordinates) {
        return ((coordinates.x >= mLeft && coordinates.x <= mRight) &&
                (coordinates.y >= mTop && coordinates.y <= mBottom));
    }

    public ENG_Vector2D position() {
        return new ENG_Vector2D(mLeft, mTop);
    }

    public void positionRet(ENG_Vector2D pos) {
        pos.set(mLeft, mTop);
    }

    public void position(float l, float t) {
        left(l);
        top(t);
    }

    public void position(ENG_Vector2D v) {
        left(v.x);
        top(v.y);
    }

    public float left() {
        return mLeft;
    }

    public void left(float l) {
        mLeft = l;
        mRight = l + width();
        mDirty = true;
        mLayer._markDirty();
    }

    public float top() {
        return mTop;
    }

    public void top(float t) {
        mTop = t;
        mBottom = t + height();
        mDirty = true;
        mLayer._markDirty();
    }

    public float width() {
        return mRight - mLeft;
    }

    public void width(float w) {
        mRight = mLeft + w;
        mDirty = true;
        mLayer._markDirty();
    }

    public float height() {
        return mBottom - mTop;
    }

    public void height(float h) {
        mBottom = mTop + h;
        mDirty = true;
        mLayer._markDirty();
    }

    public void noBackground() {
        mBackgroundColour[0].a = 0.0f;
        mDirty = true;
        mLayer._markDirty();
    }

    public void noBorder() {
        mBorderWidth = 0.0f;
        mDirty = true;
        mLayer._markDirty();
    }

    public ENG_ColorValue ninePatchColour(ENG_NinePatch.NinePatchPoint point) {
        return new ENG_ColorValue(mNinePatchColour[point.getPoint()]);
    }

    public void ninePatchColour(ENG_ColorValue c) {
        for (ENG_ColorValue engColorValue : mNinePatchColour) {
            engColorValue.set(c);
        }
    }

    public void ninePatchColour(ENG_NinePatch.NinePatchPoint point, ENG_ColorValue c) {
        mNinePatchColour[point.getPoint()].set(c);
    }

    public ENG_ColorValue backgroundColour(ENG_QuadCorner c) {
        return new ENG_ColorValue(mBackgroundColour[c.getCorner()]);
    }

    public void backgroundColour(ENG_ColorValue c) {
        for (ENG_ColorValue engColorValue : mBackgroundColour) {
            engColorValue.set(c);
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public void backgroundColour(ENG_Colour c) {
        mBackgroundColour[0].setAsRGB(c.getColour(), 1.0f);
        for (int i = 1; i < mBackgroundColour.length; ++i) {
            mBackgroundColour[i].set(mBackgroundColour[0]);
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public void backgroundColour(ENG_QuadCorner index, ENG_ColorValue c) {
        mBorderColour[index.getCorner()].set(c);
        mDirty = true;
        mLayer._markDirty();
    }

    public void backgroundGradient(ENG_Gradient gradient,
                                   ENG_ColorValue ca, ENG_ColorValue cb) {
        if (gradient == ENG_Gradient.Gradient_NorthSouth) {
            mBackgroundColour[0].set(ca);
            mBackgroundColour[1].set(ca);
            mBackgroundColour[2].set(cb);
            mBackgroundColour[3].set(cb);
        } else if (gradient == ENG_Gradient.Gradient_WestEast) {
            mBackgroundColour[0].set(ca);
            mBackgroundColour[1].set(cb);
            mBackgroundColour[2].set(cb);
            mBackgroundColour[3].set(ca);
        } else if (gradient == ENG_Gradient.Gradient_Diagonal) {
            ENG_ColorValue avg = new ENG_ColorValue((ca.r + cb.r) * 0.5f,
                    (ca.g + cb.g) * 0.5f,
                    (ca.b + cb.b) * 0.5f,
                    (ca.a + cb.a) * 0.5f);
            mBackgroundColour[0].set(ca);
            mBackgroundColour[1].set(avg);
            mBackgroundColour[2].set(cb);
            mBackgroundColour[3].set(avg);
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public void backgroundImageNinePatch(String ninePatch, String atlas) {
        backgroundImage(mLayer._getNinePatch(ninePatch), atlas);
    }

    public void backgroundImage(ENG_NinePatch ninePatch, String atlas) {
        mNinePatch = ninePatch;
        Integer nameToAtlasIndex = mLayer.getParent().getNameToAtlasIndex(atlas);
        if (nameToAtlasIndex == null) {
            throw new IllegalArgumentException(atlas + " atlas does not exist in the current " +
                    "layer.");
        }
        mNinePatchTextureIndex = nameToAtlasIndex;
        if (mNinePatchTextureIndex >= mAtlasNum) {
            throw new IllegalArgumentException(atlas + " atlas does not exist in the current " +
                    "layer.");
        }
    }

    public void backgroundImage(ENG_Sprite sprite) {
        if (sprite == null) {
            ENG_Vector2D v = mLayer._getSolidUV();
            for (ENG_Vector2D engVector2D : mUV) {
                engVector2D.set(v);
            }
        } else {
            float texelOffsetX = mLayer._getTexelX();
            float texelOffsetY = mLayer._getTexelY();
            ENG_Vector2D tSize = mLayer._getTextureSize();
            texelOffsetX /= tSize.x;
            texelOffsetY /= tSize.y;
            mUV[0].x = mUV[3].x = sprite.uvLeft - texelOffsetX;
            mUV[0].y = mUV[1].y = sprite.uvTop - texelOffsetY;
            mUV[1].x = mUV[2].x = sprite.uvRight + texelOffsetX;
            mUV[2].y = mUV[3].y = sprite.uvBottom + texelOffsetX;
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public void backgroundImage(ENG_Sprite sprite, float widthClip,
                                float heightClip) {
        if (sprite == null) {
            ENG_Vector2D v = mLayer._getSolidUV();
            for (ENG_Vector2D engVector2D : mUV) {
                engVector2D.set(v);
            }
        } else {
            float texelOffsetX = mLayer._getTexelX();
            float texelOffsetY = mLayer._getTexelY();
            ENG_Vector2D tSize = mLayer._getTextureSize();
            texelOffsetX /= tSize.x;
            texelOffsetY /= tSize.y;
            mUV[0].x = mUV[3].x = sprite.uvLeft - texelOffsetX;
            mUV[0].y = mUV[1].y = sprite.uvTop - texelOffsetY;
            mUV[1].x = mUV[2].x = sprite.uvLeft + ((sprite.uvRight - sprite.uvLeft) * widthClip) + texelOffsetX;
            mUV[2].y = mUV[3].y = sprite.uvTop + ((sprite.uvBottom - sprite.uvTop) * heightClip) + texelOffsetY;
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public void backgroundImage(String sprite, float widthClip,
                                float heightClip) {
        if (sprite.isEmpty() || sprite.equals("none")) {
            backgroundImage((ENG_Sprite) null, widthClip, heightClip);
        } else {
            backgroundImage(mLayer._getSprite(sprite), widthClip, heightClip);
        }
    }

    public void backgroundImage(String sprite) {
        if (sprite.isEmpty() || sprite.equals("none")) {
            backgroundImage((ENG_Sprite) null);
        } else {
            backgroundImage(mLayer._getSprite(sprite));
        }
    }

    public ENG_ColorValue borderColour(ENG_Border index) {
        return new ENG_ColorValue(mBorderColour[index.getBorder()]);
    }

    public void borderColour(ENG_ColorValue c) {
        for (ENG_ColorValue engColorValue : mBorderColour) {
            engColorValue.set(c);
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public void borderColour(ENG_Border index, ENG_ColorValue c) {
        mBorderColour[index.getBorder()].set(c);
        mDirty = true;
        mLayer._markDirty();
    }

    public void borderColour(ENG_Colour c) {
        mBorderColour[0].setAsRGB(c.getColour(), 1.0f);
        for (int i = 1; i < mBorderColour.length; ++i) {
            mBorderColour[i].set(mBorderColour[0]);
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public void borderColour(ENG_Border index, ENG_Colour c) {
        mBorderColour[index.getBorder()].setAsRGB(c.getColour(), 1.0f);
        mDirty = true;
        mLayer._markDirty();
    }

    public float borderWidth() {
        return mBorderWidth;
    }

    public void borderWidth(float w) {
        mBorderWidth = w;
        mDirty = true;
        mLayer._markDirty();
    }

    public void border(float width, ENG_ColorValue c) {
        for (ENG_ColorValue engColorValue : mBorderColour) {
            engColorValue.set(c);
        }
        borderWidth(width);
    }

    public void border(float width, ENG_ColorValue north,
                       ENG_ColorValue east, ENG_ColorValue south, ENG_ColorValue west) {
        mBorderColour[ENG_Border.Border_North.getBorder()].set(north);
        mBorderColour[ENG_Border.Border_South.getBorder()].set(south);
        mBorderColour[ENG_Border.Border_East.getBorder()].set(east);
        mBorderColour[ENG_Border.Border_West.getBorder()].set(west);
        borderWidth(width);
    }

    public void border(float width, ENG_Colour c) {
        mBorderColour[0].setAsRGB(c.getColour(), 1.0f);
        for (int i = 1; i < mBorderColour.length; ++i) {
            mBorderColour[i].set(mBorderColour[0]);
        }
        borderWidth(width);
    }

    public void border(float width, ENG_Colour north,
                       ENG_Colour east, ENG_Colour south, ENG_Colour west) {
        mBorderColour[ENG_Border.Border_North.getBorder()].setAsRGB(north.getColour(), 1.0f);
        mBorderColour[ENG_Border.Border_South.getBorder()].setAsRGB(south.getColour(), 1.0f);
        mBorderColour[ENG_Border.Border_East.getBorder()].setAsRGB(east.getColour(), 1.0f);
        mBorderColour[ENG_Border.Border_West.getBorder()].setAsRGB(west.getColour(), 1.0f);
        borderWidth(width);
    }

    protected final ENG_Vector2D a = new ENG_Vector2D();
    protected final ENG_Vector2D b = new ENG_Vector2D();
    protected final ENG_Vector2D c = new ENG_Vector2D();
    protected final ENG_Vector2D d = new ENG_Vector2D();

    protected final ENG_Vector2D i = new ENG_Vector2D();
    protected final ENG_Vector2D j = new ENG_Vector2D();
    protected final ENG_Vector2D k = new ENG_Vector2D();
    protected final ENG_Vector2D l = new ENG_Vector2D();

    protected final ENG_Vector2D uv = new ENG_Vector2D();

    protected ENG_Vector2D topLeft, topMiddleLeft, topMiddleRight, topRight,
            middleTopLeft, middleTopMiddleLeft, middleTopMiddleRight, middleTopRight,
            middleBottomLeft, middleBottomMiddleLeft, middleBottomMiddleRight, middleBottomRight,
            bottomLeft, bottomMiddleLeft, bottomMiddleRight, bottomRight;
    protected boolean ninePatchVectorsDefined;

    /** @noinspection SuspiciousNameCombination */
    public void _redraw() {
        if (!mDirty) {
            return;
        }
        for (ArrayList<ENG_Vertex> vertices : mVertices) {
            vertices.clear();
        }
//		mVertices.clear();
        if (visible) {
            float texelOffsetX = mLayer._getTexelX(),
                    texelOffsetY = mLayer._getTexelY();
            a.set(mLeft + texelOffsetX, mTop + texelOffsetY);
            b.set(mRight + texelOffsetX, mTop + texelOffsetY);
            c.set(mLeft + texelOffsetX, mBottom + texelOffsetY);
            d.set(mRight + texelOffsetX, mBottom + texelOffsetY);

            ArrayList<ENG_Vertex> vertices = mVertices.get(0);

            if (mBorderWidth != 0.0f) {
                i.set(a);
                j.set(b);
                k.set(c);
                l.set(d);
                i.x -= mBorderWidth;
                i.y -= mBorderWidth;
                j.x += mBorderWidth;
                j.y -= mBorderWidth;
                k.x -= mBorderWidth;
                k.y += mBorderWidth;
                l.x += mBorderWidth;
                l.y += mBorderWidth;

                mLayer._getSolidUV(uv);


                // North
                ENG_GorillaUtility.pushTriangle(vertices, a, j, i, uv, mBorderColour[ENG_Border.Border_North.getBorder()]);
                ENG_GorillaUtility.pushTriangle(vertices, a, b, j, uv, mBorderColour[ENG_Border.Border_North.getBorder()]);

                // East
                ENG_GorillaUtility.pushTriangle(vertices, d, j, b, uv, mBorderColour[ENG_Border.Border_East.getBorder()]);
                ENG_GorillaUtility.pushTriangle(vertices, d, l, j, uv, mBorderColour[ENG_Border.Border_East.getBorder()]);

                // South
                ENG_GorillaUtility.pushTriangle(vertices, k, d, c, uv, mBorderColour[ENG_Border.Border_South.getBorder()]);
                ENG_GorillaUtility.pushTriangle(vertices, k, l, d, uv, mBorderColour[ENG_Border.Border_South.getBorder()]);

                // West
                ENG_GorillaUtility.pushTriangle(vertices, k, a, i, uv, mBorderColour[ENG_Border.Border_West.getBorder()]);
                ENG_GorillaUtility.pushTriangle(vertices, k, c, a, uv, mBorderColour[ENG_Border.Border_West.getBorder()]);
            }

            if (mBackgroundColour[0].a != 0.0f) {
                if (mNinePatch == null) {
                    // Triangle A
                    ENG_GorillaUtility.pushVertex(vertices, c.x, c.y, mUV[3], mBackgroundColour[3]);    // Left/Bottom  3
                    ENG_GorillaUtility.pushVertex(vertices, b.x, b.y, mUV[1], mBackgroundColour[1]);    // Right/Top    1
                    ENG_GorillaUtility.pushVertex(vertices, a.x, a.y, mUV[0], mBackgroundColour[0]);    // Left/Top     0

                    // Triangle B
                    ENG_GorillaUtility.pushVertex(vertices, c.x, c.y, mUV[3], mBackgroundColour[3]);    // Left/Bottom   3
                    ENG_GorillaUtility.pushVertex(vertices, d.x, d.y, mUV[2], mBackgroundColour[2]);    // Right/Bottom  2
                    ENG_GorillaUtility.pushVertex(vertices, b.x, b.y, mUV[1], mBackgroundColour[1]);    // Right/Top     1
                } else {
                    if (!ninePatchVectorsDefined) {
                        defineNinePatchVectors();

                        ninePatchVectorsDefined = true;
                    }
                    topLeft.set(a);
                    topRight.set(b);
                    bottomLeft.set(c);
                    bottomRight.set(d);
                    float leftWidth = mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_LEFT.getArea()].spriteWidth;
                    float rightWidth = mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_RIGHT.getArea()].spriteWidth;
                    float topHeight = mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_LEFT.getArea()].spriteHeight;
                    float bottomHeight = mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_LEFT.getArea()].spriteHeight;
                    float centerWidth = width() - leftWidth - rightWidth;
                    float centerHeight = height() - topHeight - bottomHeight;

                    if (centerWidth < 0.0f) {
                        Gdx.app.debug(TAG, "Width < 0 for nine patch");
                        centerWidth = 0.0f;
                    }
                    if (centerHeight < 0.0f) {
                        Gdx.app.debug(TAG, "Height < 0 for nine patch");
                        centerHeight = 0.0f;
                    }

                    topMiddleLeft.set(topLeft.x + leftWidth, topLeft.y);
                    topMiddleRight.set(topMiddleLeft.x + centerWidth, topLeft.y);

                    middleTopLeft.set(topLeft.x, topLeft.y + topHeight);
                    middleTopMiddleLeft.set(middleTopLeft.x + leftWidth, middleTopLeft.y);
                    middleTopMiddleRight.set(middleTopMiddleLeft.x + centerWidth, middleTopLeft.y);
                    middleTopRight.set(topRight.x, middleTopLeft.y);

                    middleBottomLeft.set(topLeft.x, middleTopLeft.y + centerHeight);
                    middleBottomMiddleLeft.set(middleBottomLeft.x + leftWidth, middleBottomLeft.y);
                    middleBottomMiddleRight.set(middleBottomMiddleLeft.x + centerWidth, middleBottomLeft.y);
                    middleBottomRight.set(topRight.x, middleBottomLeft.y);

                    bottomMiddleLeft.set(bottomLeft.x + leftWidth, bottomLeft.y);
                    bottomMiddleRight.set(bottomMiddleLeft.x + centerWidth, bottomLeft.y);

                    ArrayList<ENG_Vertex> ninePatchTextureIndex = mVertices.get(mNinePatchTextureIndex);

                    // TOP TOP LEFT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopLeft.x, middleTopLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_LEFT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, topMiddleLeft.x, topMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_LEFT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.TOP_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, topLeft.x, topLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_LEFT.getArea()].texCoords[0], mNinePatchColour[ENG_NinePatch.NinePatchPoint.TOP_LEFT.getPoint()]);

                    // BOTTOM TOP LEFT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopLeft.x, middleTopLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_LEFT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleLeft.x, middleTopMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_LEFT.getArea()].texCoords[2], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, topMiddleLeft.x, topMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_LEFT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.TOP_MIDDLE_LEFT.getPoint()]);

                    // TOP TOP MIDDLE
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleLeft.x, middleTopMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_MIDDLE.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, topMiddleRight.x, topMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_MIDDLE.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.TOP_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, topMiddleLeft.x, topMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_MIDDLE.getArea()].texCoords[0], mNinePatchColour[ENG_NinePatch.NinePatchPoint.TOP_MIDDLE_LEFT.getPoint()]);

                    // BOTTOM TOP MIDDLE
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleLeft.x, middleTopMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_MIDDLE.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleRight.x, middleTopMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_MIDDLE.getArea()].texCoords[2], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, topMiddleRight.x, topMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_MIDDLE.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.TOP_MIDDLE_RIGHT.getPoint()]);

                    // TOP TOP RIGHT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleRight.x, middleTopMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_RIGHT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, topRight.x, topRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_RIGHT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.TOP_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, topMiddleRight.x, topMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_RIGHT.getArea()].texCoords[0], mNinePatchColour[ENG_NinePatch.NinePatchPoint.TOP_MIDDLE_RIGHT.getPoint()]);

                    // BOTTOM TOP RIGHT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleRight.x, middleTopMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_RIGHT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopRight.x, middleTopRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_RIGHT.getArea()].texCoords[2], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, topRight.x, topRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.TOP_RIGHT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.TOP_RIGHT.getPoint()]);

                    // TOP MIDDLE LEFT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomLeft.x, middleBottomLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_LEFT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleLeft.x, middleTopMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_LEFT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopLeft.x, middleTopLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_LEFT.getArea()].texCoords[0], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_LEFT.getPoint()]);

                    // BOTTOM MIDDLE LEFT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomLeft.x, middleBottomLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_LEFT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleLeft.x, middleBottomMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_LEFT.getArea()].texCoords[2], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleLeft.x, middleTopMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_LEFT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_LEFT.getPoint()]);

                    // TOP MIDDLE MIDDLE
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleLeft.x, middleBottomMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_MIDDLE.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleRight.x, middleTopMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_MIDDLE.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleLeft.x, middleTopMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_MIDDLE.getArea()].texCoords[0], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_LEFT.getPoint()]);

                    // BOTTOM MIDDLE MIDDLE
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleLeft.x, middleBottomMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_MIDDLE.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleRight.x, middleBottomMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_MIDDLE.getArea()].texCoords[2], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleRight.x, middleTopMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_MIDDLE.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_RIGHT.getPoint()]);

                    // TOP MIDDLE RIGHT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleRight.x, middleBottomMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_RIGHT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopRight.x, middleTopRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_RIGHT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopMiddleRight.x, middleTopMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_RIGHT.getArea()].texCoords[0], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_MIDDLE_RIGHT.getPoint()]);

                    // BOTTOM MIDDLE RIGHT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleRight.x, middleBottomMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_RIGHT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomRight.x, middleBottomRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_RIGHT.getArea()].texCoords[2], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleTopRight.x, middleTopRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.MIDDLE_RIGHT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_TOP_RIGHT.getPoint()]);

                    // TOP BOTTOM LEFT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, bottomLeft.x, bottomLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_LEFT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.BOTTOM_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleLeft.x, middleBottomMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_LEFT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomLeft.x, middleBottomLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_LEFT.getArea()].texCoords[0], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_LEFT.getPoint()]);

                    // BOTTOM BOTTOM LEFT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, bottomLeft.x, bottomLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_LEFT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.BOTTOM_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, bottomMiddleLeft.x, bottomMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_LEFT.getArea()].texCoords[2], mNinePatchColour[ENG_NinePatch.NinePatchPoint.BOTTOM_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleLeft.x, middleBottomMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_LEFT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_MIDDLE_LEFT.getPoint()]);

                    // TOP BOTTOM MIDDLE
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, bottomMiddleLeft.x, bottomMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_MIDDLE.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.BOTTOM_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleRight.x, middleBottomMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_MIDDLE.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleLeft.x, middleBottomMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_MIDDLE.getArea()].texCoords[0], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_MIDDLE_LEFT.getPoint()]);

                    // BOTTOM BOTTOM MIDDLE
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, bottomMiddleLeft.x, bottomMiddleLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_MIDDLE.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.BOTTOM_MIDDLE_LEFT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, bottomMiddleRight.x, bottomMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_MIDDLE.getArea()].texCoords[2], mNinePatchColour[ENG_NinePatch.NinePatchPoint.BOTTOM_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomMiddleRight.x, middleBottomMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_MIDDLE.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_MIDDLE_RIGHT.getPoint()]);

                    // TOP BOTTOM RIGHT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, bottomMiddleRight.x, bottomMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_RIGHT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.BOTTOM_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomRight.x, middleBottomRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_RIGHT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomLeft.x, middleBottomLeft.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_RIGHT.getArea()].texCoords[0], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_LEFT.getPoint()]);

                    // BOTTOM BOTTOM RIGHT
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, bottomMiddleRight.x, bottomMiddleRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_RIGHT.getArea()].texCoords[3], mNinePatchColour[ENG_NinePatch.NinePatchPoint.BOTTOM_MIDDLE_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, bottomRight.x, bottomRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_RIGHT.getArea()].texCoords[2], mNinePatchColour[ENG_NinePatch.NinePatchPoint.BOTTOM_RIGHT.getPoint()]);
                    ENG_GorillaUtility.pushVertex(ninePatchTextureIndex, middleBottomRight.x, middleBottomRight.y, mNinePatch.stretchableArea[ENG_NinePatch.NinePatchArea.BOTTOM_RIGHT.getArea()].texCoords[1], mNinePatchColour[ENG_NinePatch.NinePatchPoint.MIDDLE_BOTTOM_RIGHT.getPoint()]);
                }
            }
        }
        mDirty = false;
    }

    private void defineNinePatchVectors() {
        topLeft = new ENG_Vector2D();
        topMiddleLeft = new ENG_Vector2D();
        topMiddleRight = new ENG_Vector2D();
        topRight = new ENG_Vector2D();

        middleTopLeft = new ENG_Vector2D();
        middleTopMiddleLeft = new ENG_Vector2D();
        middleTopMiddleRight = new ENG_Vector2D();
        middleTopRight = new ENG_Vector2D();

        middleBottomLeft = new ENG_Vector2D();
        middleBottomMiddleLeft = new ENG_Vector2D();
        middleBottomMiddleRight = new ENG_Vector2D();
        middleBottomRight = new ENG_Vector2D();

        bottomLeft = new ENG_Vector2D();
        bottomMiddleLeft = new ENG_Vector2D();
        bottomMiddleRight = new ENG_Vector2D();
        bottomRight = new ENG_Vector2D();
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
