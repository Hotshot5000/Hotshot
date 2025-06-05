/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 4:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.ArrayList;

public class ENG_QuadList {


    private static class Quad {
        public final ENG_Vector2D[] mPosition = new ENG_Vector2D[4];
        public final ENG_Vector2D[] mUV = new ENG_Vector2D[4];
        public final ENG_ColorValue[] mColour = new ENG_ColorValue[4];

        public Quad() {
            for (int i = 0; i < 4; ++i) {
                mPosition[i] = new ENG_Vector2D();
                mUV[i] = new ENG_Vector2D();
                mColour[i] = new ENG_ColorValue();
            }
        }
    }

    protected final ENG_Vector2D mWhiteUV = new ENG_Vector2D();
    protected final ENG_Layer mLayer;
    final ArrayList<Quad> mQuads = new ArrayList<>();
    final ArrayList<ArrayList<ENG_Vertex>> mVertices = new ArrayList<>();
    protected boolean mDirty;
    protected int mLayerDepth;

    ENG_QuadList(ENG_Layer l, int layerDepth, int atlasNum) {
        
        mLayer = l;
        mLayer._getSolidUV(mWhiteUV);
        mLayerDepth = layerDepth;
        for (int i = 0; i < atlasNum; ++i) {
            mVertices.add(new ArrayList<>());
        }
    }

    public int getLayerDepth() {
        return mLayerDepth;
    }

    public void moveToLayerDepth(int newLayerDepth) {
        mLayer.moveQuadListToLayerDepth(this, newLayerDepth);
        mLayerDepth = newLayerDepth;
    }

    public void begin() {
        mDirty = false;
        mQuads.clear();
    }

    public void rectangle(float x, float y, float w, float h,
                          ENG_ColorValue c) {
        Quad q = new Quad();
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].x = x;
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].y = y;
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].x = x + w;
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].y = y;
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].x = x + w;
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].y = y + h;
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].x = x;
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].y = y + h;

        for (int i = 0; i < 4; ++i) {
            q.mColour[i].set(c);
            q.mUV[i].set(mWhiteUV);
        }
        mQuads.add(q);
    }

    public void gradient(float x, float y, float w, float h, ENG_Gradient gradient,
                         ENG_Colour a, ENG_Colour b) {
        ENG_ColorValue A = new ENG_ColorValue();
        ENG_ColorValue B = new ENG_ColorValue();
        A.setAsRGB(a.getColour(), 1.0f);
        B.setAsRGB(b.getColour(), 1.0f);
        gradient(x, y, w, h, gradient, A, B);
    }

    public void gradient(float x, float y, float w, float h, ENG_Gradient gradient,
                         ENG_ColorValue a, ENG_ColorValue b) {
        Quad q = new Quad();
        if (gradient == ENG_Gradient.Gradient_NorthSouth) {
            q.mColour[0].set(a);
            q.mColour[1].set(a);
            q.mColour[2].set(b);
            q.mColour[3].set(b);
        } else if (gradient == ENG_Gradient.Gradient_WestEast) {
            q.mColour[0].set(a);
            q.mColour[3].set(a);
            q.mColour[1].set(b);
            q.mColour[2].set(b);
        } else if (gradient == ENG_Gradient.Gradient_Diagonal) {
            ENG_ColorValue avg = new ENG_ColorValue();
            avg.r = (a.r + b.r) * 0.5f;
            avg.g = (a.g + b.g) * 0.5f;
            avg.b = (a.b + b.b) * 0.5f;
            avg.a = (a.a + b.a) * 0.5f;
            q.mColour[0].set(a);
            q.mColour[3].set(avg);
            q.mColour[1].set(avg);
            q.mColour[2].set(b);
        }

        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].x = x;
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].y = y;
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].x = x + w;
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].y = y;
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].x = x + w;
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].y = y + h;
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].x = x;
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].y = y + h;

        for (int i = 0; i < 4; ++i) {

            q.mUV[i].set(mWhiteUV);
        }
        mQuads.add(q);
    }

    public void sprite(float x, float y, float w, float h,
                       ENG_Sprite sprite) {
        Quad q = new Quad();
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].x = q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].x = x;
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].y = q.mPosition[ENG_QuadCorner.TopRight.getCorner()].y = y;
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].x = q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].x = x + w;
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].y = q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].y = y + h;

        for (int i = 0; i < 4; ++i) {
            q.mColour[i].set(ENG_ColorValue.WHITE);

        }
        q.mUV[ENG_QuadCorner.TopLeft.getCorner()].x = sprite.uvLeft;
        q.mUV[ENG_QuadCorner.TopLeft.getCorner()].y = sprite.uvTop;

        q.mUV[ENG_QuadCorner.TopRight.getCorner()].x = sprite.uvRight;
        q.mUV[ENG_QuadCorner.TopRight.getCorner()].y = sprite.uvTop;

        q.mUV[ENG_QuadCorner.BottomLeft.getCorner()].x = sprite.uvLeft;
        q.mUV[ENG_QuadCorner.BottomLeft.getCorner()].y = sprite.uvBottom;

        q.mUV[ENG_QuadCorner.BottomRight.getCorner()].x = sprite.uvRight;
        q.mUV[ENG_QuadCorner.BottomRight.getCorner()].y = sprite.uvBottom;
        mQuads.add(q);
    }

    public void border(float x, float y, float w, float h,
                       float thickness, ENG_Colour n, ENG_Colour e, ENG_Colour s,
                       ENG_Colour we) {
        ENG_ColorValue north = new ENG_ColorValue();
        ENG_ColorValue east = new ENG_ColorValue();
        ENG_ColorValue south = new ENG_ColorValue();
        ENG_ColorValue west = new ENG_ColorValue();
        north.setAsRGB(n.getColour(), 1.0f);
        east.setAsRGB(e.getColour(), 1.0f);
        south.setAsRGB(s.getColour(), 1.0f);
        west.setAsRGB(we.getColour(), 1.0f);
        border(x, y, w, h, thickness, north, east, south, west);
    }

    public void border(float x, float y, float w, float h,
                       float thickness, ENG_Colour col) {
        ENG_ColorValue c = new ENG_ColorValue();
        c.setAsRGB(col.getColour(), 1.0f);
        border(x, y, w, h, thickness, c, c, c, c);
    }

    public void border(float x, float y, float w, float h,
                       float thickness,
                       ENG_ColorValue c) {
        border(x, y, w, h, thickness, c, c, c, c);
    }

    public void border(float x, float y, float w, float h,
                       float thickness,
                       ENG_ColorValue northColour,
                       ENG_ColorValue eastColour,
                       ENG_ColorValue southColour,
                       ENG_ColorValue westColour) {
        ENG_Vector2D a = new ENG_Vector2D(x, y);
        ENG_Vector2D b = new ENG_Vector2D(x + w, y);
        ENG_Vector2D c = new ENG_Vector2D(x, y + h);
        ENG_Vector2D d = new ENG_Vector2D(x + w, y + h);
        ENG_Vector2D i = new ENG_Vector2D(a);
        ENG_Vector2D j = new ENG_Vector2D(b);
        ENG_Vector2D k = new ENG_Vector2D(c);
        ENG_Vector2D l = new ENG_Vector2D(d);

        Quad q = new Quad();


        // North
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].set(i);
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].set(j);
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].set(a);
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].set(b);
        for (int index = 0; index < 4; ++index) {
            q.mColour[index].set(northColour);
            q.mUV[index].set(mWhiteUV);
        }
        mQuads.add(q);

        q = new Quad();

        // East
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].set(b);
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].set(j);
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].set(d);
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].set(l);
        for (int index = 0; index < 4; ++index) {
            q.mColour[index].set(eastColour);
            q.mUV[index].set(mWhiteUV);
        }
        mQuads.add(q);

        q = new Quad();

        // South
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].set(c);
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].set(d);
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].set(k);
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].set(l);
        for (int index = 0; index < 4; ++index) {
            q.mColour[index].set(southColour);
            q.mUV[index].set(mWhiteUV);
        }
        mQuads.add(q);

        q = new Quad();

        // West
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].set(i);
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].set(a);
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].set(k);
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].set(c);
        for (int index = 0; index < 4; ++index) {
            q.mColour[index].set(westColour);
            q.mUV[index].set(mWhiteUV);
        }
        mQuads.add(q);
    }

    public void glyph(int glyphDataIndex, float x, float y,
                      int character, ENG_Colour c) {
        ENG_ColorValue colorValue = new ENG_ColorValue();
        colorValue.setAsRGB(c.getColour(), 1.0f);
        glyph(glyphDataIndex, x, y, character, colorValue);
    }

    public void glyph(int glyphDataIndex, float x, float y,
                      int character, ENG_ColorValue c) {
        ENG_GlyphData glyphData = mLayer._getGlyphData(glyphDataIndex);
        if (glyphData == null) {
            throw new IllegalArgumentException(glyphDataIndex +
                    " not a valid glyph index");
        }
        ENG_Glyph glyph = glyphData.getGlyph(character);
        if (glyph == null) {
            return;
        }
        y += glyph.verticalOffset;

        Quad q = new Quad();
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].x = x;
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].y = y;
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].x = x + glyph.glyphWidth;
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].y = y;
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].x = x + glyph.glyphWidth;
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].y = y + glyph.glyphHeight;
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].x = x;
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].y = y + glyph.glyphHeight;

        for (int i = 0; i < 4; ++i) {
            q.mColour[i].set(c);
        }

        q.mUV[ENG_QuadCorner.TopLeft.getCorner()].x = glyph.uvLeft;
        q.mUV[ENG_QuadCorner.TopLeft.getCorner()].y = glyph.uvTop;

        q.mUV[ENG_QuadCorner.TopRight.getCorner()].x = glyph.uvRight;
        q.mUV[ENG_QuadCorner.TopRight.getCorner()].y = glyph.uvTop;

        q.mUV[ENG_QuadCorner.BottomRight.getCorner()].x = glyph.uvRight;
        q.mUV[ENG_QuadCorner.BottomRight.getCorner()].y = glyph.uvBottom;

        q.mUV[ENG_QuadCorner.BottomLeft.getCorner()].x = glyph.uvLeft;
        q.mUV[ENG_QuadCorner.BottomLeft.getCorner()].y = glyph.uvBottom;

        mQuads.add(q);
    }

    public void glyph(int glyphDataIndex, float x, float y,
                      float w, float h,
                      int character, ENG_Colour c) {
        ENG_ColorValue colorValue = new ENG_ColorValue();
        colorValue.setAsRGB(c.getColour(), 1.0f);
        glyph(glyphDataIndex, x, y, w, h, character, colorValue);
    }

    public void glyph(int glyphDataIndex, float x, float y,
                      float w, float h,
                      int character, ENG_ColorValue c) {
        ENG_GlyphData glyphData = mLayer._getGlyphData(glyphDataIndex);
        if (glyphData == null) {
            throw new IllegalArgumentException(glyphDataIndex +
                    " not a valid glyph index");
        }
        ENG_Glyph glyph = glyphData.getGlyph(character);
        if (glyph == null) {
            return;
        }
        y += glyph.verticalOffset;

        Quad q = new Quad();
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].x = x;
        q.mPosition[ENG_QuadCorner.TopLeft.getCorner()].y = y;
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].x = x + w;
        q.mPosition[ENG_QuadCorner.TopRight.getCorner()].y = y;
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].x = x + w;
        q.mPosition[ENG_QuadCorner.BottomRight.getCorner()].y = y + h;
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].x = x;
        q.mPosition[ENG_QuadCorner.BottomLeft.getCorner()].y = y + h;

        for (int i = 0; i < 4; ++i) {
            q.mColour[i].set(c);
        }

        q.mUV[ENG_QuadCorner.TopLeft.getCorner()].x = glyph.uvLeft;
        q.mUV[ENG_QuadCorner.TopLeft.getCorner()].y = glyph.uvTop;

        q.mUV[ENG_QuadCorner.TopRight.getCorner()].x = glyph.uvRight;
        q.mUV[ENG_QuadCorner.TopRight.getCorner()].y = glyph.uvTop;

        q.mUV[ENG_QuadCorner.BottomRight.getCorner()].x = glyph.uvRight;
        q.mUV[ENG_QuadCorner.BottomRight.getCorner()].y = glyph.uvBottom;

        q.mUV[ENG_QuadCorner.BottomLeft.getCorner()].x = glyph.uvLeft;
        q.mUV[ENG_QuadCorner.BottomLeft.getCorner()].y = glyph.uvBottom;

        mQuads.add(q);
    }

    public void end() {
        mLayer._markDirty();
        mDirty = true;
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
        for (int i = 0; i < mQuads.size(); ++i) {
            Quad q = mQuads.get(i);
            ENG_GorillaUtility.pushQuad(vertices, q.mPosition, q.mColour,
                    q.mUV);
        }
        mDirty = false;
    }

}
