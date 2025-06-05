/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.ArrayList;

public class ENG_LineList {


    protected final ENG_Layer mLayer;
    protected float mThickness;
    protected final ENG_ColorValue mColour = new ENG_ColorValue();
    protected boolean mIsClosed;
    protected final ArrayList<ENG_Vector2D> mPositions = new ArrayList<>();
    final ArrayList<ArrayList<ENG_Vertex>> mVertices = new ArrayList<>();
    protected boolean mDirty, visible = true;
    protected int mLayerDepth;

    ENG_LineList(ENG_Layer l, int layerDepth, int atlasNum) {
        
        mLayer = l;
        mLayerDepth = layerDepth;
        for (int i = 0; i < atlasNum; ++i) {
            mVertices.add(new ArrayList<>());
        }
    }

    public int getLayerDepth() {
        return mLayerDepth;
    }

    public void moveToLayerDepth(int newLayerDepth) {
        mLayer.moveLineListLayerDepth(this, newLayerDepth);
        mLayerDepth = newLayerDepth;
    }

    public void begin(float thickness, ENG_Colour c) {
        mDirty = false;
        mThickness = thickness;
        mPositions.clear();
        mColour.setAsRGB(c.getColour(), 1.0f);
    }

    public void begin(float thickness, ENG_ColorValue c) {
        mDirty = false;
        mThickness = thickness;
        mPositions.clear();
        mColour.set(c);
    }

    public void position(float x, float y) {
        mPositions.add(new ENG_Vector2D(x, y));
    }

    public void position(ENG_Vector2D p) {
        mPositions.add(new ENG_Vector2D(p));
    }

    public void end(boolean isClosed) {
        mDirty = true;
        mIsClosed = isClosed;
    }

    public void end() {
        end(false);
    }

    protected final ENG_Vector2D temp = new ENG_Vector2D();
    protected final ENG_Vector2D perp = new ENG_Vector2D();
    protected final ENG_Vector2D lastLeft = new ENG_Vector2D();
    protected final ENG_Vector2D lastRight = new ENG_Vector2D();
    protected final ENG_Vector2D thisLeft = new ENG_Vector2D();
    protected final ENG_Vector2D thisRight = new ENG_Vector2D();
    protected final ENG_Vector2D uv = new ENG_Vector2D();

    public void _redraw() {
        if (!mDirty) {
            return;
        }
        for (ArrayList<ENG_Vertex> vertices : mVertices) {
            vertices.clear();
        }
//		mVertices.clear();

        if (mPositions.size() < 2) {
            return;
        }
        if (visible) {
            float halfThickness = mThickness * 0.5f;

            mLayer._getSolidUV(uv);

            ArrayList<ENG_Vertex> vertices = mVertices.get(0);

            for (int i = 1; i < mPositions.size(); ++i) {
                mPositions.get(i).sub(mPositions.get(i - 1), temp);
                temp.perpendicular(perp);
                perp.normalize();
                mPositions.get(i - 1).sub(perp, lastLeft);
                lastLeft.mulInPlace(halfThickness);
                mPositions.get(i - 1).add(perp, lastRight);
                lastRight.mulInPlace(halfThickness);
                mPositions.get(i).sub(perp, thisLeft);
                thisLeft.mulInPlace(halfThickness);
                mPositions.get(i).add(perp, thisRight);
                thisRight.mulInPlace(halfThickness);

                // Triangle A
                ENG_GorillaUtility.pushVertex(vertices, lastRight.x, lastRight.y, uv, mColour);       // Left/Bottom
                ENG_GorillaUtility.pushVertex(vertices, thisLeft.x, thisLeft.y, uv, mColour);         // Right/Top
                ENG_GorillaUtility.pushVertex(vertices, lastLeft.x, lastLeft.y, uv, mColour);          // Left/Top
                // Triangle B
                ENG_GorillaUtility.pushVertex(vertices, lastRight.x, lastRight.y, uv, mColour);       // Left/Bottom
                ENG_GorillaUtility.pushVertex(vertices, thisRight.x, thisRight.y, uv, mColour);      // Right/Bottom
                ENG_GorillaUtility.pushVertex(vertices, thisLeft.x, thisLeft.y, uv, mColour);         // Right/Top
            }

            if (mIsClosed) {
                int i = mPositions.size() - 1;
                mPositions.get(0).sub(mPositions.get(i), temp);
                temp.perpendicular(perp);
                perp.normalize();
                mPositions.get(i).sub(perp, lastLeft);
                lastLeft.mulInPlace(halfThickness);
                mPositions.get(i).add(perp, lastRight);
                lastRight.mulInPlace(halfThickness);
                mPositions.get(0).sub(perp, thisLeft);
                thisLeft.mulInPlace(halfThickness);
                mPositions.get(0).add(perp, thisRight);
                thisRight.mulInPlace(halfThickness);

                // Triangle A
                ENG_GorillaUtility.pushVertex(vertices, lastRight.x, lastRight.y, uv, mColour);       // Left/Bottom
                ENG_GorillaUtility.pushVertex(vertices, thisLeft.x, thisLeft.y, uv, mColour);         // Right/Top
                ENG_GorillaUtility.pushVertex(vertices, lastLeft.x, lastLeft.y, uv, mColour);          // Left/Top
                // Triangle B
                ENG_GorillaUtility.pushVertex(vertices, lastRight.x, lastRight.y, uv, mColour);       // Left/Bottom
                ENG_GorillaUtility.pushVertex(vertices, thisRight.x, thisRight.y, uv, mColour);      // Right/Bottom
                ENG_GorillaUtility.pushVertex(vertices, thisLeft.x, thisLeft.y, uv, mColour);         // Right/Top
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
