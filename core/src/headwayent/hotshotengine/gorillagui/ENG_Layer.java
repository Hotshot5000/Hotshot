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
import java.util.Iterator;
import java.util.TreeMap;

public class ENG_Layer {

    protected final int mIndex;
    protected final TreeMap<Integer, ArrayList<ENG_Rectangle>> mRectangles = new TreeMap<>();
    protected final TreeMap<Integer, ArrayList<ENG_Polygon>> mPolygons = new TreeMap<>();
    protected final TreeMap<Integer, ArrayList<ENG_LineList>> mLineLists = new TreeMap<>();
    protected final TreeMap<Integer, ArrayList<ENG_QuadList>> mQuadLists = new TreeMap<>();
    protected final TreeMap<Integer, ArrayList<ENG_Caption>> mCaptions = new TreeMap<>();
    protected final TreeMap<Integer, ArrayList<ENG_MarkupText>> mMarkupTexts = new TreeMap<>();
    protected final ENG_LayerContainer mParent;
    protected boolean mVisible = true;
    protected float mAlphaModifier = 1.0f;
    protected final int mAtlasNum;
    protected byte[] mQueueGroupId; // Per texture atlas.

    ENG_Layer(int index, ENG_LayerContainer parent, int atlasNum) {
        
        mIndex = index;
        mParent = parent;
        mAtlasNum = atlasNum;
    }

    ENG_Layer(int index, ENG_LayerContainer parent, int atlasNum, byte[] queueGroupId) {
        this(index, parent, atlasNum);
        mQueueGroupId = queueGroupId;
    }

    public byte[] getQueueGroupId() {
        return mQueueGroupId;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean b) {
        if (mVisible == b) {
            return;
        }
        mVisible = b;
        _markDirty();
    }

    public void show() {
        if (mVisible) {
            return;
        }
        mVisible = true;
        _markDirty();
    }

    public void hide() {
        if (!mVisible) {
            return;
        }
        mVisible = false;
        _markDirty();
    }

    public void setAlphaModifier(float a) {
        mAlphaModifier = a;
        _markDirty();
    }

    public float getAlphaModifier() {
        return mAlphaModifier;
    }

    public void _markDirty() {
        mParent._requestIndexRedraw(mIndex);
    }

    public void moveRectangleLayerDepth(ENG_Rectangle rectangle, int newLayerDepth) {
        ArrayList<ENG_Rectangle> list = mRectangles.get(rectangle.getLayerDepth());
        boolean remove = list.remove(rectangle);
        if (!remove) {
            throw new IllegalArgumentException("Rectangle does not exist");
        }
        list = mRectangles.get(newLayerDepth);
        if (list == null) {
            list = new ArrayList<>();
            mRectangles.put(newLayerDepth, list);
        }
        list.add(rectangle);
    }

    public ENG_Rectangle createRectangle(float left, float top,
                                         float width, float height) {
        return createRectangle(left, top, width, height, 0);
    }

    public ENG_Rectangle createRectangle(float left, float top,
                                         float width, float height, int layerDepth) {
        ENG_Rectangle rectangle =
                new ENG_Rectangle(left, top, width, height, this, layerDepth, mAtlasNum);
        ArrayList<ENG_Rectangle> list = mRectangles.get(layerDepth);
        if (list == null) {
            list = new ArrayList<>();
            mRectangles.put(layerDepth, list);
        }
        list.add(rectangle);
//		mRectangles.add(rectangle);
        return rectangle;
    }

    public ENG_Rectangle createRectangle(ENG_Vector2D pos,
                                         ENG_Vector2D size) {
        return createRectangle(pos, size, 0);
    }

    public ENG_Rectangle createRectangle(ENG_Vector2D pos,
                                         ENG_Vector2D size, int layerDepth) {
        return createRectangle(pos.x, pos.y, size.x, size.y, layerDepth);
    }

    public void destroyRectangle(ENG_Rectangle r) {
        if (r == null) {
            return;
        }
        boolean remove = false;
        for (ArrayList<ENG_Rectangle> list : mRectangles.values()) {
            remove = list.remove(r);
            if (remove) {
                break;
            }
        }
//		boolean remove = .remove(r);
        if (!remove) {
            throw new IllegalArgumentException();
        }
        _markDirty();
    }

    public void destroyAllRectangles() {
        mRectangles.clear();
    }

    public Iterator<ArrayList<ENG_Rectangle>> getRectangles() {
        return mRectangles.values().iterator();
    }

    public void movePolygonLayerDepth(ENG_Polygon polygon, int newLayerDepth) {
        ArrayList<ENG_Polygon> list = mPolygons.get(polygon.getLayerDepth());
        boolean remove = list.remove(polygon);
        if (!remove) {
            throw new IllegalArgumentException("Polygon not found");
        }
        list = mPolygons.get(newLayerDepth);
        if (list == null) {
            list = new ArrayList<>();
            mPolygons.put(newLayerDepth, list);
        }
        list.add(polygon);
    }

    public ENG_Polygon createPolygon(float left, float top, float radius,
                                     int sides) {
        return createPolygon(left, top, radius, sides, 0);
    }

    public ENG_Polygon createPolygon(float left, float top, float radius,
                                     int sides, int layerDepth) {
        ENG_Polygon polygon = new ENG_Polygon(left, top, radius, sides, this, layerDepth, mAtlasNum);
//		mPolygons.add(polygon);
        ArrayList<ENG_Polygon> list = mPolygons.get(layerDepth);
        if (list == null) {
            list = new ArrayList<>();
            mPolygons.put(layerDepth, list);
        }
        list.add(polygon);
        return polygon;
    }

    public void destroyPolygon(ENG_Polygon p) {
        if (p == null) {
            return;
        }
        boolean remove = false;
        for (ArrayList<ENG_Polygon> list : mPolygons.values()) {
            remove = list.remove(p);
            if (remove) {
                break;
            }
        }
//		boolean remove = .remove(p);
        if (!remove) {
            throw new IllegalArgumentException();
        }
        _markDirty();
    }

    public void destroyAllPolygons() {
        mPolygons.clear();
    }

    public Iterator<ArrayList<ENG_Polygon>> getPolygons() {
        return mPolygons.values().iterator();
    }

    public void moveLineListLayerDepth(ENG_LineList list, int newLayerDepth) {
        ArrayList<ENG_LineList> list2 = mLineLists.get(list.getLayerDepth());
        boolean remove = list2.remove(list);
        if (!remove) {
            throw new IllegalArgumentException("Line list not found");
        }
        list2 = mLineLists.get(newLayerDepth);
        if (list2 == null) {
            list2 = new ArrayList<>();
            mLineLists.put(newLayerDepth, list2);
        }
        list2.add(list);
    }

    public ENG_LineList createLineList() {
        return createLineList(0);
    }

    public ENG_LineList createLineList(int layerDepth) {
        ENG_LineList list = new ENG_LineList(this, layerDepth, mAtlasNum);
//		mLineLists.add(list);
        ArrayList<ENG_LineList> list2 = mLineLists.get(layerDepth);
        if (list2 == null) {
            list2 = new ArrayList<>();
            mLineLists.put(layerDepth, list2);
        }
        list2.add(list);
        return list;
    }

    public void destroyLineList(ENG_LineList list) {
        if (list == null) {
            return;
        }
        boolean remove = false;
        for (ArrayList<ENG_LineList> list2 : mLineLists.values()) {
            remove = list2.remove(list);
            if (remove) {
                break;
            }
        }
//		boolean remove = .remove(list);
        if (!remove) {
            throw new IllegalArgumentException();
        }
        _markDirty();
    }

    public void destroyAllLineLists() {
        mLineLists.clear();
    }

    public Iterator<ArrayList<ENG_LineList>> getLineLists() {
        return mLineLists.values().iterator();
    }

    public void moveQuadListToLayerDepth(ENG_QuadList list, int newLayerDepth) {
        ArrayList<ENG_QuadList> list2 = mQuadLists.get(list.getLayerDepth());
        boolean remove = list2.remove(list);
        if (!remove) {
            throw new IllegalArgumentException("Quad list not found");
        }
        list2 = mQuadLists.get(newLayerDepth);
        if (list2 == null) {
            list2 = new ArrayList<>();
            mQuadLists.put(newLayerDepth, list2);
        }
        list2.add(list);
    }

    public ENG_QuadList createQuadList() {
        return createQuadList(0);
    }

    public ENG_QuadList createQuadList(int layerDepth) {
        ENG_QuadList list = new ENG_QuadList(this, layerDepth, mAtlasNum);
//		mQuadLists.add(list);
        ArrayList<ENG_QuadList> list2 = mQuadLists.get(layerDepth);
        if (list2 == null) {
            list2 = new ArrayList<>();
            mQuadLists.put(layerDepth, list2);
        }
        list2.add(list);
        return list;
    }

    public void destroyQuadList(ENG_QuadList list) {
        if (list == null) {
            return;
        }
        boolean remove = false;
        for (ArrayList<ENG_QuadList> list2 : mQuadLists.values()) {
            remove = list2.remove(list);
            if (remove) {
                break;
            }
        }
//		boolean remove = .remove(list);
        if (!remove) {
            throw new IllegalArgumentException();
        }
        _markDirty();
    }

    public void destroyAllQuadLists() {
        mQuadLists.clear();
    }

    public Iterator<ArrayList<ENG_QuadList>> getQuadLists() {
        return mQuadLists.values().iterator();
    }

    public void moveCaptionToLayerDepth(ENG_Caption caption, int newLayerDepth) {
        ArrayList<ENG_Caption> list = mCaptions.get(caption.getLayerDepth());
        boolean remove = list.remove(caption);
        if (!remove) {
            throw new IllegalArgumentException("Caption not found");
        }
        list = mCaptions.get(newLayerDepth);
        if (list == null) {
            list = new ArrayList<>();
            mCaptions.put(newLayerDepth, list);
        }
        list.add(caption);
    }

    public ENG_Caption createCaption(int glyphDataIndex, float x, float y,
                                     String text) {
        return createCaption(glyphDataIndex, x, y, text, 0);
    }

    public ENG_Caption createCaption(int glyphDataIndex, float x, float y,
                                     String text, int layerDepth) {
        ENG_Caption caption = new ENG_Caption(glyphDataIndex, x, y, text, this, layerDepth, mAtlasNum);
//		mCaptions.add(caption);
        ArrayList<ENG_Caption> list = mCaptions.get(layerDepth);
        if (list == null) {
            list = new ArrayList<>();
            mCaptions.put(layerDepth, list);
        }
        list.add(caption);
        return caption;
    }

    public void destroyCaption(ENG_Caption c) {
        if (c == null) {
            return;
        }
        boolean remove = false;
        for (ArrayList<ENG_Caption> list : mCaptions.values()) {
            remove = list.remove(c);
            if (remove) {
                break;
            }
        }
//		boolean remove = .remove(c);
        if (!remove) {
            throw new IllegalArgumentException();
        }
        _markDirty();
    }

    public void destroyAllCaptions() {
        mCaptions.clear();
    }

    public Iterator<ArrayList<ENG_Caption>> getCaptions() {
        return mCaptions.values().iterator();
    }

    public void moveMarkupTextToLayerDepth(ENG_MarkupText markupText, int newLayerDepth) {
        ArrayList<ENG_MarkupText> list = mMarkupTexts.get(markupText.getLayerDepth());
        boolean remove = list.remove(markupText);
        if (!remove) {
            throw new IllegalArgumentException("Markup text not found");
        }
        list = mMarkupTexts.get(newLayerDepth);
        if (list == null) {
            list = new ArrayList<>();
            mMarkupTexts.put(newLayerDepth, list);
        }
        list.add(markupText);
    }

    public ENG_MarkupText createMarkupText(int defaultGlyphIndex,
                                           float x, float y, String text) {
        return createMarkupText(defaultGlyphIndex, x, y, text, 0);
    }

    public ENG_MarkupText createMarkupText(int defaultGlyphIndex,
                                           float x, float y, String text, int layerDepth) {
        ENG_MarkupText markupText =
                new ENG_MarkupText(defaultGlyphIndex, x, y, text, this, layerDepth, mAtlasNum);
//		mMarkupTexts.add(markupText);
        ArrayList<ENG_MarkupText> list = mMarkupTexts.get(layerDepth);
        if (list == null) {
            list = new ArrayList<>();
            mMarkupTexts.put(layerDepth, list);
        }
        list.add(markupText);
        return markupText;
    }

    public void destroyMarkupText(ENG_MarkupText m) {
        if (m == null) {
            return;
        }
        boolean remove = false;
        for (ArrayList<ENG_MarkupText> list : mMarkupTexts.values()) {
            remove = list.remove(m);
            if (remove) {
                break;
            }
        }
//		boolean remove = .remove(m);
        if (!remove) {
            throw new IllegalArgumentException();
        }
        _markDirty();
    }

    public void destroyAllMarkupTexts() {
        mMarkupTexts.clear();
    }

    public Iterator<ArrayList<ENG_MarkupText>> getMarkupTexts() {
        return mMarkupTexts.values().iterator();
    }

    public int getIndex() {
        return mIndex;
    }

    public ENG_Vector2D _getSolidUV() {
        return mParent.getAtlas().getWhitePixel();
    }

    public void _getSolidUV(ENG_Vector2D ret) {
        mParent.getAtlas().getWhitePixel(ret);
    }

    public ENG_Sprite _getSprite(String name) {
        return mParent.getAtlas().getSprite(name);
    }

    public ENG_NinePatch _getNinePatch(String name) {
        return mParent.getAtlas().getNinePatch(name);
    }

    public ENG_GlyphData _getGlyphData(int i) {
        return mParent.getAtlas().getGlyphData(i);
    }

    public ENG_Vector2D _getTextureSize() {
        return mParent.getAtlas().getTextureSize();
    }

    public void _getTextureSize(ENG_Vector2D ret) {
        mParent.getAtlas().getTextureSize(ret);
    }

    public ENG_TextureAtlas getAtlas() {
        return mParent.getAtlas();
    }

    public float _getTexelX() {
        return mParent.getTexelOffsetX();
    }

    public float _getTexelY() {
        return mParent.getTexelOffsetY();
    }

    public ENG_ColorValue _getMarkupColour(int index) {
        return mParent.getAtlas().getMarkupColour(index);
    }

    public void _getMarkupColour(int index, ENG_ColorValue ret) {
        mParent.getAtlas().getMarkupColour(index, ret);
    }

    void _render(ArrayList<ArrayList<ENG_Vertex>> vertices) {
        _render(vertices, false);
    }

    void _render(ArrayList<ArrayList<ENG_Vertex>> vertices, boolean force) {
        if (mAlphaModifier == 0.0f) {
            return;
        }
        ArrayList<Integer> beginList = new ArrayList<>();
        for (ArrayList<ENG_Vertex> list : vertices) {
            beginList.add(list.size());
        }
//        int begin = vertices.size();

        for (ArrayList<ENG_Rectangle> list : mRectangles.values()) {
            for (ENG_Rectangle elem : list) {
                if (elem.mDirty || force) {
                    elem._redraw();
                }
                for (int i = 0; i < vertices.size(); ++i) {
                    ArrayList<ENG_Vertex> to = vertices.get(i);
                    ArrayList<ENG_Vertex> from = elem.mVertices.get(i);
                    for (ENG_Vertex v : from) {
                        to.add(new ENG_Vertex(v));
                    }
                }
//				for (ENG_Vertex v : elem.mVertices) {
//					vertices.add(new ENG_Vertex(v));
//				}
                //			vertices.addAll(elem.mVertices);
            }
        }

        for (ArrayList<ENG_Polygon> list : mPolygons.values()) {
            for (ENG_Polygon elem : list) {
                if (elem.mDirty || force) {
                    elem._redraw();
                }
                for (int i = 0; i < vertices.size(); ++i) {
                    ArrayList<ENG_Vertex> to = vertices.get(i);
                    ArrayList<ENG_Vertex> from = elem.mVertices.get(i);
                    for (ENG_Vertex v : from) {
                        to.add(new ENG_Vertex(v));
                    }
                }
//				for (ENG_Vertex v : elem.mVertices) {
//					vertices.add(new ENG_Vertex(v));
//				}
                //			vertices.addAll(elem.mVertices);
            }
        }

        for (ArrayList<ENG_LineList> list : mLineLists.values()) {
            for (ENG_LineList elem : list) {
                if (elem.mDirty || force) {
                    elem._redraw();
                }
                for (int i = 0; i < vertices.size(); ++i) {
                    ArrayList<ENG_Vertex> to = vertices.get(i);
                    ArrayList<ENG_Vertex> from = elem.mVertices.get(i);
                    for (ENG_Vertex v : from) {
                        to.add(new ENG_Vertex(v));
                    }
                }
//				for (ENG_Vertex v : elem.mVertices) {
//					vertices.add(new ENG_Vertex(v));
//				}
                //			vertices.addAll(elem.mVertices);
            }
        }

        for (ArrayList<ENG_QuadList> list : mQuadLists.values()) {
            for (ENG_QuadList elem : list) {
                if (elem.mDirty || force) {
                    elem._redraw();
                }
                for (int i = 0; i < vertices.size(); ++i) {
                    ArrayList<ENG_Vertex> to = vertices.get(i);
                    ArrayList<ENG_Vertex> from = elem.mVertices.get(i);
                    for (ENG_Vertex v : from) {
                        to.add(new ENG_Vertex(v));
                    }
                }
//				for (ENG_Vertex v : elem.mVertices) {
//					vertices.add(new ENG_Vertex(v));
//				}
                //			vertices.addAll(elem.mVertices);
            }
        }

        for (ArrayList<ENG_Caption> list : mCaptions.values()) {
            for (ENG_Caption elem : list) {
                if (elem.mDirty || force) {
                    elem._redraw();
                }
                for (int i = 0; i < vertices.size(); ++i) {
                    ArrayList<ENG_Vertex> to = vertices.get(i);
                    ArrayList<ENG_Vertex> from = elem.mVertices.get(i);
                    for (ENG_Vertex v : from) {
                        to.add(new ENG_Vertex(v));
                    }
                }
//				for (ENG_Vertex v : elem.mVertices) {
//					vertices.add(new ENG_Vertex(v));
//				}
                //			vertices.addAll(elem.mVertices);
            }
        }

        for (ArrayList<ENG_MarkupText> list : mMarkupTexts.values()) {
            for (ENG_MarkupText elem : list) {
                if (elem.mTextDirty || force) {
                    elem._calculateCharacters();
                }
                if (elem.mDirty || force) {
                    elem._redraw();
                }
                for (int i = 0; i < vertices.size(); ++i) {
                    ArrayList<ENG_Vertex> to = vertices.get(i);
                    ArrayList<ENG_Vertex> from = elem.mVertices.get(i);
                    for (ENG_Vertex v : from) {
                        to.add(new ENG_Vertex(v));
                    }
                }
//				for (ENG_Vertex v : elem.mVertices) {
//					vertices.add(new ENG_Vertex(v));
//				}
                //			vertices.addAll(elem.mVertices);
            }
        }


        if (mAlphaModifier != 1.0f) {
            int textureIndex = 0;
            for (int begin : beginList) {
                ArrayList<ENG_Vertex> v = vertices.get(textureIndex++);
                for (int i = begin; i < vertices.size(); ++i) {
                    v.get(i).colour.a *= mAlphaModifier;
                }
            }
        }

        ArrayList<Integer> endList = new ArrayList<>();
        for (ArrayList<ENG_Vertex> list : vertices) {
            endList.add(list.size());
        }

        mParent._transform(vertices, beginList, endList);
    }

    public ENG_LayerContainer getParent() {
        return mParent;
    }
}
