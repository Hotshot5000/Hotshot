/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.ArrayList;

public class ENG_Polygon {

    protected ENG_Layer mLayer;
    protected float mLeft, mTop, mRadius, mBorderWidth;
    protected float mAngle;
    protected int mSides;
    protected final ENG_ColorValue mBackgroundColour =
            new ENG_ColorValue(ENG_ColorValue.WHITE);
    protected final ENG_ColorValue mBorderColour = new ENG_ColorValue();
    protected ENG_Sprite mSprite;
    protected boolean mDirty = true, visible = true;
    final ArrayList<ArrayList<ENG_Vertex>> mVertices = new ArrayList<>();
    protected int mLayerDepth;
    protected final int mAtlasNum;

    ENG_Polygon(float left, float top, float radius,
                       int sides, ENG_Layer layer, int layerDepth, int atlasNum) {
        
        mLayer._markDirty();
        mLeft = left;
        mTop = top;
        mRadius = radius;
        mSides = sides;
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
        mLayer.movePolygonLayerDepth(this, newLayerDepth);
        mLayerDepth = newLayerDepth;
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

    public float radius() {
        return mRadius;
    }

    public void radius(float r) {
        mRadius = r;
        mDirty = true;
        mLayer._markDirty();
    }

    public int sides() {
        return mSides;
    }

    public void sides(int sides) {
        if (sides < 3) {
            sides = 3;
        }
        mSides = sides;
        mDirty = true;
        mLayer._markDirty();
    }

    public float angle() {
        return mAngle;
    }

    public void angle(float a) {
        mAngle = a;
        mDirty = true;
        mLayer._markDirty();
    }

    public ENG_Sprite backgroundImage() {
        return mSprite;
    }

    public void backgroundImage(ENG_Sprite s) {
        mSprite = s;
        mDirty = true;
        mLayer._markDirty();
    }

    public void backgroundImage(String name) {
        if (name == null || name.equals("none")) {
            mSprite = null;
        } else {
            mSprite = mLayer._getSprite(name);
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public ENG_ColorValue backgroundColour() {
        return new ENG_ColorValue(mBackgroundColour);
    }

    public void backgroundColour(ENG_ColorValue c) {
        mBackgroundColour.set(c);
        mDirty = true;
        mLayer._markDirty();
    }

    public void border(float width, ENG_ColorValue c) {
        mBorderColour.set(c);
        mBorderWidth = width;
        mDirty = true;
        mLayer._markDirty();
    }

    public void border(float width, ENG_Colour c) {
        if (c == ENG_Colour.None) {
            mBorderColour.a = 0.0f;
            mBorderWidth = 0.0f;
        } else {
            mBorderColour.setAsRGB(c.getColour(), 1.0f);
        }
        mDirty = true;
        mLayer._markDirty();
    }

    public ENG_ColorValue borderColour() {
        return new ENG_ColorValue(mBorderColour);
    }

    public void borderColour(ENG_ColorValue c) {
        mBorderColour.set(c);
        mDirty = true;
        mLayer._markDirty();
    }

    public void borderColour(ENG_Colour c) {
        if (c == ENG_Colour.None) {
            mBorderColour.a = 0.0f;
        } else {
            mBorderColour.setAsRGB(c.getColour(), 1.0f);
        }
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

    public void noBackground() {
        mBackgroundColour.a = 0.0f;
        mDirty = true;
        mLayer._markDirty();
    }

    public void noBorder() {
        mBorderWidth = 0.0f;
        mDirty = true;
        mLayer._markDirty();
    }

    protected final ENG_Vector2D lastVertex = new ENG_Vector2D();
    protected final ENG_Vector2D thisVertex = new ENG_Vector2D();
    protected final ENG_Vector2D lastOuterVertex = new ENG_Vector2D();
    protected final ENG_Vector2D outerVertex = new ENG_Vector2D();
    protected final ENG_Vector2D uv = new ENG_Vector2D();

    protected final ENG_Vector2D centerUV = new ENG_Vector2D();
    protected final ENG_Vector2D lastUV = new ENG_Vector2D();
    protected final ENG_Vector2D thisUV = new ENG_Vector2D();
    protected final ENG_Vector2D baseUV = new ENG_Vector2D();
    protected final ENG_Vector2D texSize = new ENG_Vector2D();

    public void _redraw() {
        if (!mDirty) {
            return;
        }

        for (ArrayList<ENG_Vertex> vertices : mVertices) {
            vertices.clear();
        }
//		mVertices.clear();

        if (visible) {

            float theta = mAngle;
            float inc = ENG_Math.TWO_PI / mSides;

            lastVertex.set(
                    mLeft + (mRadius * ENG_Math.cos(theta)),
                    mTop + (mRadius * ENG_Math.sin(theta)));
            ArrayList<ENG_Vertex> vertices = mVertices.get(0);
            if (mBorderWidth != 0.0f) {
                mLayer._getSolidUV(uv);
                lastOuterVertex.x = mLeft + ((mRadius + mBorderWidth) * ENG_Math.cos(theta));
                lastOuterVertex.y = mTop + ((mRadius + mBorderWidth) * ENG_Math.sin(theta));

                for (int i = 0; i < mSides; ++i) {
                    theta += inc;
                    thisVertex.x = mLeft + (mRadius * ENG_Math.cos(theta));
                    thisVertex.y = mTop + (mRadius * ENG_Math.sin(theta));
                    outerVertex.x = mLeft + ((mRadius + mBorderWidth) * ENG_Math.cos(theta));
                    outerVertex.y = mTop + ((mRadius + mBorderWidth) * ENG_Math.sin(theta));

                    ENG_GorillaUtility.pushTriangle(vertices, lastVertex, outerVertex, lastOuterVertex, uv, mBorderColour);
                    ENG_GorillaUtility.pushTriangle(vertices, lastVertex, thisVertex, outerVertex, uv, mBorderColour);

                    lastVertex.set(thisVertex);
                    lastOuterVertex.set(outerVertex);
                }
            }

            if (mBackgroundColour.a != 0.0f) {
                if (mSprite != null) {
                    float xRadius = mSprite.spriteWidth * 0.5f;
                    float yRadius = mSprite.spriteHeight * 0.5f;
                    mLayer._getTextureSize(texSize);

                    baseUV.x = mSprite.uvLeft * texSize.x;
                    baseUV.y = mSprite.uvTop * texSize.y;
                    baseUV.x += xRadius;
                    baseUV.y += yRadius;

                    baseUV.div(texSize, centerUV);
                    lastUV.set(baseUV);
                    lastUV.x = baseUV.x + (xRadius * ENG_Math.cos(theta));
                    lastUV.y = baseUV.y + (yRadius * ENG_Math.sin(theta));
                    lastUV.divInPlace(texSize);

                    for (int i = 0; i < mSides; ++i) {
                        ENG_GorillaUtility.pushVertex(vertices, mLeft, mTop, centerUV, mBackgroundColour);
                        theta += inc;
                        thisVertex.x = mLeft + (mRadius * ENG_Math.cos(theta));
                        thisVertex.y = mTop + (mRadius * ENG_Math.sin(theta));
                        thisUV.x = baseUV.x + (xRadius * ENG_Math.cos(theta));
                        thisUV.y = baseUV.y + (yRadius * ENG_Math.sin(theta));
                        thisUV.divInPlace(texSize);
                        ENG_GorillaUtility.pushVertex(vertices, thisVertex.x, thisVertex.y, thisUV, mBackgroundColour);
                        ENG_GorillaUtility.pushVertex(vertices, lastVertex.x, lastVertex.y, lastUV, mBackgroundColour);
                        lastVertex.set(thisVertex);
                        lastUV.set(thisUV);
                    }
                } else {
                    mLayer._getSolidUV(uv);
                    for (int i = 0; i < mSides; ++i) {
                        ENG_GorillaUtility.pushVertex(vertices, mLeft, mTop, uv, mBackgroundColour);
                        theta += inc;
                        thisVertex.x = mLeft + (mRadius * ENG_Math.cos(theta));
                        thisVertex.y = mTop + (mRadius * ENG_Math.sin(theta));

                        ENG_GorillaUtility.pushVertex(vertices, thisVertex.x, thisVertex.y, uv, mBackgroundColour);
                        ENG_GorillaUtility.pushVertex(vertices, lastVertex.x, lastVertex.y, uv, mBackgroundColour);
                        lastVertex.set(thisVertex);
                    }
                }
            }
        }
        mDirty = false;

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
