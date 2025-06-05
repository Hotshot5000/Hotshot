/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/16/21, 7:35 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.LockOptions;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_RenderOperation.OperationType;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ENG_TextAreaOverlayElement extends ENG_OverlayElement {

    public enum Alignment {
        Left((byte) 0),
        Right((byte) 1),
        Center((byte) 2);

        private final byte pos;

        Alignment(byte pos) {
            this.pos = pos;
        }

        public byte getPos() {
            return pos;
        }
    }

    private static final short DEFAULT_INITIAL_CHARS = 12;
    private static final short POS_TEX_BINDING = 0;
    private static final short COLOUR_BINDING = 1;
    private static final short UNICODE_NEL = 0x0085;
    private static final short UNICODE_CR = 0x000D;
    private static final short UNICODE_LF = 0x000A;
    private static final short UNICODE_SPACE = 0x0020;
    private static final short UNICODE_ZERO = 0x0030;

    /// The text alignment
    protected Alignment mAlignment = Alignment.Left;

    /// Flag indicating if this panel should be visual or just group things
    protected boolean mTransparent;

    /// Render operation
    protected final ENG_RenderOperation mRenderOp = new ENG_RenderOperation();

    protected ENG_Font mpFont;
    protected float mCharHeight = 0.02f;
    protected short mPixelCharHeight = 12;
    protected float mSpaceWidth;
    protected short mPixelSpaceWidth;
    protected int mAllocSize;
    protected float mViewportAspectCoef = 1.0f;

    /// Colours to use for the vertices
    protected final ENG_ColorValue mColourBottom = new ENG_ColorValue(ENG_ColorValue.WHITE);
    protected final ENG_ColorValue mColourTop = new ENG_ColorValue(ENG_ColorValue.WHITE);
    protected boolean mColoursChanged = true;
    private boolean mInitFloatBuffer;
    private FloatBuffer pVert;

    public ENG_TextAreaOverlayElement(String name) {
        super(name);
        
    }

    @Override
    protected void updatePositionGeometry() {
        

        if (mpFont == null) {
            return;
        }

        int charlen = mCaption.length();
        checkMemoryAllocation(charlen);

        mRenderOp.vertexData.vertexCount = charlen * 6;

        ENG_HardwareVertexBuffer vbuf =
                mRenderOp.vertexData.vertexBufferBinding.getBuffer(
                        POS_TEX_BINDING);

        ByteBuffer buf = (ByteBuffer) vbuf.lock(LockOptions.HBL_DISCARD);

//		pVert = null;
        if (mInitFloatBuffer) {
            pVert = buf.asFloatBuffer();
            mInitFloatBuffer = false;
        } else {
            pVert.rewind();
        }

        float largestWidth = 0;
        float left = _getDerivedLeft() * 2.0f - 1.0f;
        float top = -((_getDerivedTop() * 2.0f) - 1.0f);

        // Derive space with from a number 0
        if (mSpaceWidth == 0) {
            mSpaceWidth = mpFont.getGlyphAspectRatio(UNICODE_ZERO) *
                    mCharHeight * 2.0f * mViewportAspectCoef;
        }
        boolean newLine = true;
        for (int i = 0; i < mCaption.length(); ++i) {


            if (newLine) {
                float len = 0.0f;
                for (int j = i; j < mCaption.length(); ++j) {
                    char character = mCaption.charAt(j);
                    if (character == UNICODE_CR
                            || character == UNICODE_NEL
                            || character == UNICODE_LF) {
                        break;
                    } else if (character == UNICODE_SPACE) {
                        len += mSpaceWidth;
                    } else {
                        len += mpFont.getGlyphAspectRatio(character) *
                                mCharHeight * 2.0f * mViewportAspectCoef;
                    }
                }

                if (mAlignment == Alignment.Right)
                    left -= len;
                else if (mAlignment == Alignment.Center)
                    left -= len * 0.5f;

                newLine = false;
            }

            char character = mCaption.charAt(i);
            if (character == UNICODE_CR
                    || character == UNICODE_NEL
                    || character == UNICODE_LF) {
                left = _getDerivedLeft() * 2.0f - 1.0f;
                top -= mCharHeight * 2.0f;
                newLine = true;
                // Also reduce tri count
                mRenderOp.vertexData.vertexCount -= 6;

                // consume CR/LF in one
                if (character == UNICODE_CR) {
                    int peeki = i;
                    peeki++;
                    if (peeki < mCaption.length() &&
                            mCaption.charAt(peeki) == UNICODE_LF) {
                        i = peeki; // skip both as one newline
                        // Also reduce tri count
                        mRenderOp.vertexData.vertexCount -= 6;
                    }

                }
                continue;
            } else if (character == UNICODE_SPACE) // space
            {
                // Just leave a gap, no tris
                left += mSpaceWidth;
                // Also reduce tri count
                mRenderOp.vertexData.vertexCount -= 6;
                continue;
            }

            float horiz_height = mpFont.getGlyphAspectRatio(character) *
                    mViewportAspectCoef;
            ENG_RealRect uvRect = mpFont.getGlyphTexCoords(character);

            // First tri
            //
            // Upper left

            pVert.put(left);
            pVert.put(top);
            pVert.put(-1.0f);
            pVert.put(uvRect.left);
            pVert.put(uvRect.top);

            top -= mCharHeight * 2.0f;

            pVert.put(left);
            pVert.put(top);
            pVert.put(-1.0f);
            pVert.put(uvRect.left);
            pVert.put(uvRect.bottom);
            // Bottom left


            top += mCharHeight * 2.0f;
            left += horiz_height * mCharHeight * 2.0f;

            pVert.put(left);
            pVert.put(top);
            pVert.put(-1.0f);
            pVert.put(uvRect.right);
            pVert.put(uvRect.top);

            pVert.put(left);
            pVert.put(top);
            pVert.put(-1.0f);
            pVert.put(uvRect.right);
            pVert.put(uvRect.top);

            top -= mCharHeight * 2.0f;
            left -= horiz_height * mCharHeight * 2.0f;

            pVert.put(left);
            pVert.put(top);
            pVert.put(-1.0f);
            pVert.put(uvRect.left);
            pVert.put(uvRect.bottom);


            left += horiz_height * mCharHeight * 2.0f;

            pVert.put(left);
            pVert.put(top);
            pVert.put(-1.0f);
            pVert.put(uvRect.right);
            pVert.put(uvRect.bottom);

            // Go back up with top
            top += mCharHeight * 2.0f;

            float currentWidth = (left + 1) / 2 - _getDerivedLeft();
            if (currentWidth > largestWidth) {
                largestWidth = currentWidth;

            }
        }

        // Unlock vertex buffer
        vbuf.unlock();

        if (mMetricsMode == GuiMetricsMode.GMM_PIXELS) {
            // Derive parametric version of dimensions
            float vpWidth;
            vpWidth = ENG_OverlayManager.getSingleton().getViewportWidth();

            largestWidth *= vpWidth;
        }

        if (getWidth() < largestWidth)
            setWidth(largestWidth);
    }

    @Override
    protected void updateTextureGeometry() {
        

    }

    @Override
    public void _update() {
        

        float vpWidth, vpHeight;
        vpWidth = ENG_OverlayManager.getSingleton().getViewportWidth();
        vpHeight = ENG_OverlayManager.getSingleton().getViewportHeight();

        mViewportAspectCoef = vpHeight / vpWidth;

        // Check size if pixel-based / relative-aspect-adjusted
        switch (mMetricsMode) {
            case GMM_PIXELS:
                if (ENG_OverlayManager.getSingleton().hasViewportChanged() || mGeomPositionsOutOfDate) {
                    // recalculate character size
                    mCharHeight = (float) mPixelCharHeight / vpHeight;
                    mSpaceWidth = (float) mPixelSpaceWidth / vpHeight;
                    mGeomPositionsOutOfDate = true;
                }
                break;

            case GMM_RELATIVE_ASPECT_ADJUSTED:
                if (ENG_OverlayManager.getSingleton().hasViewportChanged() || mGeomPositionsOutOfDate) {
                    // recalculate character size
                    mCharHeight = (float) mPixelCharHeight / 10000.0f;
                    mSpaceWidth = (float) mPixelSpaceWidth / 10000.0f;
                    mGeomPositionsOutOfDate = true;
                }
                break;

            default:
                break;
        }

        super._update();

        if (mColoursChanged && mInitialised) {
            updateColours();
            mColoursChanged = false;
        }
    }

    protected void updateColours() {
        
        ENG_Integer topColour = new ENG_Integer();
        ENG_Integer bottomColour = new ENG_Integer();
        ENG_RenderRoot.getRenderRoot().convertColourValue(mColourTop, topColour);
        ENG_RenderRoot.getRenderRoot().convertColourValue(mColourBottom, bottomColour);

        ENG_HardwareVertexBuffer vbuf =
                mRenderOp.vertexData.vertexBufferBinding.getBuffer(COLOUR_BINDING);

        IntBuffer pDest =
                ((ByteBuffer) vbuf.lock(LockOptions.HBL_DISCARD)).asIntBuffer();

        for (int i = 0; i < mAllocSize; ++i) {
            // First tri (top, bottom, top)

            pDest.put(topColour.getValue());
            pDest.put(bottomColour.getValue());
            pDest.put(topColour.getValue());

            pDest.put(topColour.getValue());
            pDest.put(bottomColour.getValue());
            pDest.put(bottomColour.getValue());

        }
        vbuf.unlock();
    }

    @Override
    public void initialise() {
        

        if (!mInitialised) {
            mRenderOp.vertexData = new ENG_VertexData();

            ENG_VertexDeclaration decl = mRenderOp.vertexData.vertexDeclaration;
            int offset = 0;

            decl.addElement(POS_TEX_BINDING, offset,
                    VertexElementType.VET_FLOAT3, VertexElementSemantic.VES_POSITION);
            offset += ENG_VertexElement.getTypeSize(VertexElementType.VET_FLOAT3);
            decl.addElement(POS_TEX_BINDING, offset,
                    VertexElementType.VET_FLOAT2,
                    VertexElementSemantic.VES_TEXTURE_COORDINATES);
            offset += ENG_VertexElement.getTypeSize(VertexElementType.VET_FLOAT2);

            decl.addElement(COLOUR_BINDING, 0,
                    VertexElementType.VET_COLOUR, VertexElementSemantic.VES_DIFFUSE);

            mRenderOp.operationType = OperationType.OT_TRIANGLE_LIST;
            mRenderOp.useIndexes = false;
            mRenderOp.vertexData.vertexStart = 0;
            // Vertex buffer will be created in checkMemoryAllocation

            checkMemoryAllocation(DEFAULT_INITIAL_CHARS);

            mInitialised = true;
        }
    }

    @Override
    public void destroy(boolean skipGLCall) {
        if (mInitialised) {
            mRenderOp.vertexData.destroy(skipGLCall);
            mInitialised = false;
        }
    }

    protected void checkMemoryAllocation(int numChars) {
        if (mAllocSize < numChars) {
            ENG_VertexDeclaration decl = mRenderOp.vertexData.vertexDeclaration;
            ENG_VertexBufferBinding bind = mRenderOp.vertexData.vertexBufferBinding;

            mRenderOp.vertexData.vertexCount = numChars * 6;
            ENG_HardwareVertexBuffer vbuf =
                    ENG_HardwareBufferManager.getSingleton().createVertexBuffer(
                            decl.getVertexSize(POS_TEX_BINDING),
                            mRenderOp.vertexData.vertexCount,
                            Usage.HBU_DYNAMIC_WRITE_ONLY.getUsage(), true);
            bind.setBinding(POS_TEX_BINDING, vbuf);

            vbuf =
                    ENG_HardwareBufferManager.getSingleton().createVertexBuffer(
                            decl.getVertexSize(COLOUR_BINDING),
                            mRenderOp.vertexData.vertexCount,
                            Usage.HBU_DYNAMIC_WRITE_ONLY.getUsage(), true);
            bind.setBinding(COLOUR_BINDING, vbuf);

            mAllocSize = numChars;
            mColoursChanged = true; // force colour buffer regeneration
            mInitFloatBuffer = true;
        }
    }

    @Override
    public String getTypeName() {
        
        return "TextArea";
    }

    @Override
    public void getRenderOperation(ENG_RenderOperation op) {
        

        op.set(mRenderOp);
    }

    @Override
    public void setCaption(String caption) {
        if (caption == null) {
            return;
        }
        if (mCaption.equals(caption)) {
            return;
        }
        mCaption = caption;
        mGeomPositionsOutOfDate = true;
        mGeomUVsOutOfDate = true;
        ENG_NativeCalls.overlayElement_setCaption(this, caption);
    }

    public void setCharHeight(float height) {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            mPixelCharHeight = (short) (height);
        } else {
            mCharHeight = height;
        }
        mGeomPositionsOutOfDate = true;
    }

    public float getCharHeight() {
        if (mMetricsMode == GuiMetricsMode.GMM_PIXELS) {
            return mPixelCharHeight;
        } else {
            return mCharHeight;
        }
    }

    public void setSpaceWidth(float width) {
        if (mMetricsMode != GuiMetricsMode.GMM_RELATIVE) {
            mPixelSpaceWidth = (short) (width);
        } else {
            mSpaceWidth = width;
        }

        mGeomPositionsOutOfDate = true;
    }

    public float getSpaceWidth() {
        if (mMetricsMode == GuiMetricsMode.GMM_PIXELS) {
            return mPixelSpaceWidth;
        } else {
            return mSpaceWidth;
        }
    }

    public void setFontName(String name) {
        mpFont = ENG_FontManager.getSingleton().getByName(name);
        if (mpFont == null) {
            throw new IllegalArgumentException(name + " is not a valid font");
        }
        mGeomPositionsOutOfDate = true;
        mGeomUVsOutOfDate = true;
    }

    public String getFontName() {
        return mpFont.getName();
    }

    @Override
    public ENG_Material getMaterial() {
        
        if (mpMaterial == null && mpFont != null) {
            mpMaterial = mpFont.getMaterial();
            mpMaterial.setDepthCheckEnabled(false);
            mpMaterial.setLightingEnabled(false);
        }
        return mpMaterial;
    }

    public void setColour(ENG_ColorValue col) {
        mColourBottom.set(col);
        mColourTop.set(col);
        mColoursChanged = true;
    }

    @Override
    public void getColour(ENG_ColorValue ret) {
        
        ret.set(mColourTop);
    }

    @Override
    public ENG_ColorValue getColour() {
        
        return new ENG_ColorValue(mColourTop);
    }

    public void setBottomColour(ENG_ColorValue col) {
        mColourBottom.set(col);
        mColoursChanged = true;
    }

    public void getBottomColour(ENG_ColorValue ret) {
        ret.set(mColourBottom);
    }

    public ENG_ColorValue getBottomColour() {
        return new ENG_ColorValue(mColourBottom);
    }

    public void setTopColour(ENG_ColorValue col) {
        mColourTop.set(col);
        mColoursChanged = true;
    }

    public void getTopColour(ENG_ColorValue ret) {
        ret.set(mColourTop);
    }

    public ENG_ColorValue getTopColour() {
        return new ENG_ColorValue(mColourTop);
    }

    public void setAlignment(Alignment alignment) {
        setAlignment(alignment, true);
    }

    /**
     * Somehow this doesn't work on the native side. Alignment is still left...
     * @param a
     * @param callNative
     */
    public void setAlignment(Alignment a, boolean callNative) {
        mAlignment = a;
        mGeomPositionsOutOfDate = true;
        ENG_NativeCalls.overlayElement_setAlignment(this, a);
    }

    public Alignment getAlignment() {
        return mAlignment;
    }

    @Override
    public void setMetricsMode(GuiMetricsMode gmm) {
        

        float vpWidth, vpHeight;
        vpWidth = ENG_OverlayManager.getSingleton().getViewportWidth();
        vpHeight = ENG_OverlayManager.getSingleton().getViewportHeight();

        mViewportAspectCoef = vpHeight / vpWidth;
        super.setMetricsMode(gmm);

        switch (mMetricsMode) {
            case GMM_PIXELS:
                // set pixel variables based on viewport multipliers
                mPixelCharHeight = (short) (mCharHeight * vpHeight);
                mPixelSpaceWidth = (short) (mSpaceWidth * vpHeight);
                break;

            case GMM_RELATIVE_ASPECT_ADJUSTED:
                // set pixel variables multiplied by the height constant
                mPixelCharHeight = (short) (mCharHeight * 10000.0);
                mPixelSpaceWidth = (short) (mSpaceWidth * 10000.0);
                break;

            default:
                break;
        }
    }

}
