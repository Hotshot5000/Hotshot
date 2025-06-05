/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Plane;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.android.AndroidRenderWindow;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_BlendMode.LayerBlendModeEx;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendFactor;
import headwayent.hotshotengine.renderer.ENG_BlendMode.SceneBlendOperation;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_Common.CompareFunction;
import headwayent.hotshotengine.renderer.ENG_Common.CullingMode;
import headwayent.hotshotengine.renderer.ENG_Common.FilterOptions;
import headwayent.hotshotengine.renderer.ENG_Common.FilterType;
import headwayent.hotshotengine.renderer.ENG_Common.FogMode;
import headwayent.hotshotengine.renderer.ENG_Common.FrameBufferType;
import headwayent.hotshotengine.renderer.ENG_Common.PolygonMode;
import headwayent.hotshotengine.renderer.ENG_Common.ShadeOptions;
import headwayent.hotshotengine.renderer.ENG_Config;
import headwayent.hotshotengine.renderer.ENG_ConfigOption;
import headwayent.hotshotengine.renderer.ENG_Frustum;
import headwayent.hotshotengine.renderer.ENG_GpuConstantDefinition.GpuParamVariability;
import headwayent.hotshotengine.renderer.ENG_GpuProgram;
import headwayent.hotshotengine.renderer.ENG_GpuProgram.GpuProgramType;
import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters;
import headwayent.hotshotengine.renderer.ENG_HardwareBufferManager;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer.IndexType;
import headwayent.hotshotengine.renderer.ENG_HardwareVertexBuffer;
import headwayent.hotshotengine.renderer.ENG_HighLevelGpuProgramManager;
import headwayent.hotshotengine.renderer.ENG_Light;
import headwayent.hotshotengine.renderer.ENG_MultiRenderTarget;
import headwayent.hotshotengine.renderer.ENG_RenderOperation;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderSystem;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.Capabilities;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.CapabilitiesCategory;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.GPUVendor;
import headwayent.hotshotengine.renderer.ENG_RenderTarget;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;
import headwayent.hotshotengine.renderer.ENG_Texture;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState.TextureAddressingMode;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState.UVWAddressingMode;
import headwayent.hotshotengine.renderer.ENG_VertexBufferBinding;
import headwayent.hotshotengine.renderer.ENG_VertexDeclaration;
import headwayent.hotshotengine.renderer.ENG_VertexElement;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;
import headwayent.hotshotengine.renderer.ENG_Viewport;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.glsles.GLRenderSystemNativeWrapper;
import headwayent.hotshotengine.renderer.opengles.glsl.GLSLProgramFactory;
import headwayent.hotshotengine.renderer.opengles.glsl.GLUtility;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.GL20;

public class GLRenderSystem extends ENG_RenderSystem {

    private boolean mStopRendering;

    private final GLRenderSystemNativeWrapper wrapper;

    /// Last min & mip filtering options, so we can combine them
    private FilterOptions mMinFilter = FilterOptions.FO_LINEAR;
    private FilterOptions mMipFilter = FilterOptions.FO_POINT;

    private final int[] mTextureCoordIndex = new int[ENG_Config.MAX_TEXTURE_LAYERS];
    private final int[] mTextureTypes = new int[ENG_Config.MAX_TEXTURE_LAYERS];

    private GLGpuProgram mCurrentVertexProgram;
    private GLGpuProgram mCurrentFragmentProgram;
    private GLGpuProgram mCurrentGeometryProgram;

    private short mFixedFunctionTextureUnits;

    private boolean mDepthWrite;
    private int mStencilMask;

    private final boolean[] mColourWrite = new boolean[4];

    private GLGpuProgramManager mGpuProgramManager;
    private GLSLProgramFactory mGLSLProgramFactory;

    private GLRTTManager mRTTManager;

    private short mActiveTextureUnit;

    private final ArrayList<ENG_Integer> attribsBound = new ArrayList<>();

    public GLRenderSystem(long renderSystem) {
        
        wrapper = new GLRenderSystemNativeWrapper();
        wrapper.setPtr(renderSystem);
        mDepthWrite = true;
        mStencilMask = 0xFFFFFFFF;
        Arrays.fill(mColourWrite, true);
    }

    /** @noinspection deprecation */
    public void _setTexture(int stage, boolean enabled, ENG_Texture texPtr) {
        GLTexture tex = (GLTexture) texPtr;
        int lastTextureType = mTextureTypes[stage];

        if (!activateGLTextureUnit(stage)) {
            return;
        }

        if (enabled) {
            if (tex != null) {
                mTextureTypes[stage] = tex.getGLTextureTarget();
            } else {
                mTextureTypes[stage] = GL20.GL_TEXTURE_2D;
            }

            if (tex != null) {
                MTGLES20.glBindTexture(mTextureTypes[stage], tex.getGLID());
            } else {
                MTGLES20.glBindTexture(mTextureTypes[stage],
                        ((GLTextureManager) mTextureManager).getWarningTextureID());
            }
        } else {
            MTGLES20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        }
        activateGLTextureUnit(0);
    }

    /** @noinspection deprecation*/
    private boolean activateGLTextureUnit(int unit) {
        
        if (mActiveTextureUnit != unit) {
            if (unit < getCapabilities().getmNumTextureUnits()) {
                MTGLES20.glActiveTexture(GL20.GL_TEXTURE0 + unit);
                mActiveTextureUnit = (short) unit;
                return true;
            } else return unit == 0;
        }
        return true;
    }


    @Override
    public void _applyObliqueDepthProjection(ENG_Matrix4 matrix,
                                             ENG_Plane plane, boolean forGpuProgram) {
        

    }

    /** @noinspection deprecation*/
    @Override
    public void _beginFrame() {
        

        if (mActiveViewport == null) {
            throw new NullPointerException("Cannot begin frame - no viewport selected.");
        }

        MTGLES20.glEnable(GL20.GL_SCISSOR_TEST);
    }

    @Override
    public void _convertProjectionMatrix(ENG_Matrix4 matrix, ENG_Matrix4 dest,
                                         boolean forGpuProgram) {
        

        dest.set(matrix);
    }

    @Override
    public ENG_RenderWindow _createRenderWindow(String name, int width,
                                                int height, boolean fullScreen, TreeMap<String, String> miscParams) {
        
        ENG_RenderWindow win = new AndroidRenderWindow(name, width, height);
        // Create the native window.
        win.create(name, width, height, fullScreen, miscParams);
//        mRealCapabilities = createRenderSystemCapabilities();

        attachRenderTarget(win);
        // use real capabilities if custom capabilities are not available
        if (!mUseCustomCapabilities)
            mCurrentCapabilities = mRealCapabilities;
        initialiseFromRenderSystemCapabilities(mCurrentCapabilities, win);
        return win;
    }

    /** @noinspection deprecation*/
    @Override
    public void _endFrame() {
        

        MTGLES20.glDisable(GL20.GL_SCISSOR_TEST);

        unbindGpuProgram(GpuProgramType.GPT_VERTEX_PROGRAM);
        unbindGpuProgram(GpuProgramType.GPT_FRAGMENT_PROGRAM);
    }

    @Override
    public void _makeOrthoMatrix(ENG_Radian fovy, float aspect,
                                 float nearPlane, float farPlane, ENG_Matrix4 dest,
                                 boolean forGpuProgram) {
        

        float thetaY = fovy.valueRadians() / 2.0f;
        float tanThetaY = ENG_Math.tan(thetaY);

        float tanThetaX = tanThetaY * aspect; //Math::Tan(thetaX);
        float half_w = tanThetaX * nearPlane;
        float half_h = tanThetaY * nearPlane;
        float iw = 1.0f / half_w;
        float ih = 1.0f / half_h;
        float q;
        if (farPlane == 0.0f) {
            q = 0.0f;
        } else {
            q = 2.0f / (farPlane - nearPlane);
        }

        dest.set(ENG_Math.MAT4_ZERO);
        dest.set(0, 0, iw);
        dest.set(1, 1, ih);
        dest.set(2, 2, -q);
        dest.set(2, 3, -(farPlane + nearPlane) / (farPlane - nearPlane));
        dest.set(3, 3, 1.0f);
    }

    @Override
    public void _makeProjectionMatrix(ENG_Radian fovy, float aspect,
                                      float nearPlane, float farPlane, ENG_Matrix4 dest,
                                      boolean forGpuProgram) {
        

        float thetaY = fovy.valueRadians() / 2.0f;
        float tanThetaY = ENG_Math.tan(thetaY);

        float w = (1.0f / tanThetaY) / aspect;
        float h = 1.0f / tanThetaY;
        float q, qn;
        if (farPlane == 0.0f) {
            q = ENG_Frustum.INFINITE_FAR_PLANE_ADJUST - 1.0f;
            qn = nearPlane * (ENG_Frustum.INFINITE_FAR_PLANE_ADJUST - 2.0f);
        } else {
            q = -(farPlane + nearPlane) / (farPlane - nearPlane);
            qn = -2 * (farPlane * nearPlane) / (farPlane - nearPlane);
        }

        dest.set(ENG_Math.MAT4_ZERO);
        dest.set(0, 0, w);
        dest.set(1, 1, h);
        dest.set(2, 2, q);
        dest.set(2, 3, qn);
        dest.set(3, 2, -1.0f);
    }

    @Override
    public void _makeProjectionMatrix(float left, float right, float bottom,
                                      float top, float aspect, float nearPlane, float farPlane,
                                      ENG_Matrix4 dest, boolean forGpuProgram) {
        

        float width = right - left;
        float height = top - bottom;
        float q, qn;
        if (farPlane == 0.0f) {
            // Infinite far plane
            q = ENG_Frustum.INFINITE_FAR_PLANE_ADJUST - 1.0f;
            qn = nearPlane * (ENG_Frustum.INFINITE_FAR_PLANE_ADJUST - 2.0f);
        } else {
            q = -(farPlane + nearPlane) / (farPlane - nearPlane);
            qn = -2.0f * (farPlane * nearPlane) / (farPlane - nearPlane);
        }

        dest.set(ENG_Math.MAT4_ZERO);
        dest.set(0, 0, 2.0f * nearPlane / width);
        dest.set(0, 2, (right + left) / width);
        dest.set(1, 1, 2.0f * nearPlane / height);
        dest.set(1, 2, (top + bottom) / height);
        dest.set(2, 2, q);
        dest.set(2, 3, qn);
        dest.set(3, 2, -1.0f);
    }

    @Override
    public void _setAlphaRejectSettings(CompareFunction func, byte value,
                                        boolean alphaToCoverage) {
        

    }

    /** @noinspection deprecation*/
    @Override
    public void _setColourBufferWriteEnabled(boolean red, boolean green,
                                             boolean blue, boolean alpha) {
        

        MTGLES20.glColorMask(red, green, blue, alpha);

        mColourWrite[0] = red;
        mColourWrite[1] = green;
        mColourWrite[2] = blue;
        mColourWrite[3] = alpha;
    }

    public String getErrorDescription(long errCode) {
        throw new UnsupportedOperationException();
//		return GLU.gluErrorString((int) errCode);
    }

    /** @noinspection deprecation */
    @Override
    public void _setCullingMode(CullingMode mode) {
        

        mCullingMode = mode;
        // NB: Because two-sided stencil API dependence of the front face, we must
        // use the same 'winding' for the front face everywhere. As the OGRE default
        // culling mode is clockwise, we also treat anticlockwise winding as front
        // face for consistently. On the assumption that, we can't change the front
        // face by glFrontFace anywhere.

        int cullMode;

        switch (mode) {
            case CULL_NONE:
                MTGLES20.glDisable(GL20.GL_CULL_FACE);
                return;
            default:
            case CULL_CLOCKWISE:
                if (mActiveRenderTarget != null &&
                        ((mActiveRenderTarget.requiresTextureFlipping() && !mInvertVertexWinding) ||
                                (!mActiveRenderTarget.requiresTextureFlipping() && mInvertVertexWinding))) {
                    cullMode = GL20.GL_FRONT;
                } else {
                    cullMode = GL20.GL_BACK;
                }
                break;
            case CULL_ANTICLOCKWISE:
                if (mActiveRenderTarget != null &&
                        ((mActiveRenderTarget.requiresTextureFlipping() && !mInvertVertexWinding) ||
                                (!mActiveRenderTarget.requiresTextureFlipping() && mInvertVertexWinding))) {
                    cullMode = GL20.GL_BACK;
                } else {
                    cullMode = GL20.GL_FRONT;
                }
                break;
        }

        MTGLES20.glEnable(GL20.GL_CULL_FACE);
        MTGLES20.glCullFace(cullMode);

    }

    /** @noinspection deprecation */
    @Override
    public void _setDepthBias(float constantBias, float slopeScaleBias) {
        

        if (constantBias != 0.0f || slopeScaleBias != 0.0f) {
            MTGLES20.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
            MTGLES20.glPolygonOffset(-slopeScaleBias, -constantBias);
        } else {
            MTGLES20.glDisable(GL20.GL_POLYGON_OFFSET_FILL);
        }
    }

    /** @noinspection deprecation */
    @Override
    public void _setDepthBufferCheckEnabled(boolean enabled) {
        

        if (enabled) {
            MTGLES20.glClearDepthf(1.0f);
            MTGLES20.glEnable(GL20.GL_DEPTH_TEST);
        } else {
            MTGLES20.glDisable(GL20.GL_DEPTH_TEST);
        }
    }

    /** @noinspection deprecation*/
    @Override
    public void _setDepthBufferFunction(CompareFunction func) {
        

        MTGLES20.glDepthFunc(convertCompareFunction(func));
    }

    private int convertCompareFunction(CompareFunction func) {
        
        switch (func) {
            case CMPF_ALWAYS_FAIL:
                return GL20.GL_NEVER;
            case CMPF_ALWAYS_PASS:
                return GL20.GL_ALWAYS;
            case CMPF_LESS:
                return GL20.GL_LESS;
            case CMPF_LESS_EQUAL:
                return GL20.GL_LEQUAL;
            case CMPF_EQUAL:
                return GL20.GL_EQUAL;
            case CMPF_NOT_EQUAL:
                return GL20.GL_NOTEQUAL;
            case CMPF_GREATER_EQUAL:
                return GL20.GL_GEQUAL;
            case CMPF_GREATER:
                return GL20.GL_GREATER;
        }
        // to keep compiler happy
        return GL20.GL_ALWAYS;
    }

    @Override
    public void _setDepthBufferParams(boolean depthTest, boolean depthWrite,
                                      CompareFunction depthFunction) {
        

        _setDepthBufferCheckEnabled(depthTest);
        _setDepthBufferWriteEnabled(depthWrite);
        _setDepthBufferFunction(depthFunction);
    }

    /** @noinspection deprecation*/
    @Override
    public void _setDepthBufferWriteEnabled(boolean enabled) {
        


        MTGLES20.glDepthMask(enabled);
        // Store for reference in _beginFrame
        mDepthWrite = enabled;
    }

    @Override
    public void _setFog(FogMode mode, ENG_ColorValue colour, float expDensity,
                        float linearStart, float linearEnd) {
        

    }

    @Override
    public void _setPointParameters(float size, boolean attenuationEnabled,
                                    float constant, float linear, float quadratic, float minSize,
                                    float maxSize) {
        

    }

    @Override
    public void _setPointSpritesEnabled(boolean enabled) {
        

    }

    @Override
    public void _setPolygonMode(PolygonMode level) {
        

	/*	int glmode;
        switch(level)
		{
		case PM_POINTS:
			glmode = GLES20.GL_POINT;
			break;
		case PM_WIREFRAME:
			glmode = GLES20.GL_LINE;
			break;
		default:
		case PM_SOLID:
			glmode = GLES20.GL_FILL;
			break;
		}
		glPolygonMode(GL_FRONT_AND_BACK, glmode);*/
    }

    @Override
    public void _setProjectionMatrix(ENG_Matrix4 m) {
        

    }

    @Override
    public void _setRenderTarget(ENG_RenderTarget target) {
        

        if (mActiveRenderTarget != null) {
            mRTTManager.unbind(target);
        }

        mActiveRenderTarget = target;
        mRTTManager.bind(target);
    }

    /** @noinspection deprecation */
    @Override
    public void _setSceneBlending(SceneBlendFactor sourceFactor,
                                  SceneBlendFactor destFactor, SceneBlendOperation op) {
        

        int sourceBlend = getBlendMode(sourceFactor);
        int destBlend = getBlendMode(destFactor);

        if (sourceFactor == SceneBlendFactor.SBF_ONE &&
                destFactor == SceneBlendFactor.SBF_ZERO) {
            MTGLES20.glDisable(GL20.GL_BLEND);
        } else {
            MTGLES20.glEnable(GL20.GL_BLEND);
            MTGLES20.glBlendFunc(sourceBlend, destBlend);
        }

        int func = GL20.GL_FUNC_ADD;
        switch (op) {
            case SBO_ADD:
                func = GL20.GL_FUNC_ADD;
                break;
            case SBO_SUBTRACT:
                func = GL20.GL_FUNC_SUBTRACT;
                break;
            case SBO_REVERSE_SUBTRACT:
                func = GL20.GL_FUNC_REVERSE_SUBTRACT;
                break;
            case SBO_MIN:
                //	func = GL20.GL_MIN;
                //	break;
            case SBO_MAX:
                //	func = GL20.GL_MAX;
                //	break;
                throw new IllegalArgumentException("This SceneBlendOperation not supported");
        }

        MTGLES20.glBlendEquation(func);
    }

    /** @noinspection deprecation */
    @Override
    public void _setSeparateSceneBlending(SceneBlendFactor sourceFactor,
                                          SceneBlendFactor destFactor, SceneBlendFactor sourceFactorAlpha,
                                          SceneBlendFactor destFactorAlpha, SceneBlendOperation op,
                                          SceneBlendOperation alphaOp) {
        

        int sourceBlend = getBlendMode(sourceFactor);
        int destBlend = getBlendMode(destFactor);
        int sourceBlendAlpha = getBlendMode(sourceFactorAlpha);
        int destBlendAlpha = getBlendMode(destFactorAlpha);

        if (sourceFactor == SceneBlendFactor.SBF_ONE &&
                destFactor == SceneBlendFactor.SBF_ZERO &&
                sourceFactorAlpha == SceneBlendFactor.SBF_ONE &&
                destFactorAlpha == SceneBlendFactor.SBF_ZERO) {
            MTGLES20.glDisable(GL20.GL_BLEND);
        } else {
            MTGLES20.glEnable(GL20.GL_BLEND);
            MTGLES20.glBlendFuncSeparate(sourceBlend, destBlend,
                    sourceBlendAlpha, destBlendAlpha);
        }

        int func = GL20.GL_FUNC_ADD, alphaFunc = GL20.GL_FUNC_ADD;

        switch (op) {
            case SBO_ADD:
                func = GL20.GL_FUNC_ADD;
                break;
            case SBO_SUBTRACT:
                func = GL20.GL_FUNC_SUBTRACT;
                break;
            case SBO_REVERSE_SUBTRACT:
                func = GL20.GL_FUNC_REVERSE_SUBTRACT;
                break;
            case SBO_MIN:
                //	func = GL_MIN;
                //	break;
            case SBO_MAX:
                //	func = GL_MAX;
                //	break;
                throw new IllegalArgumentException("This SceneBlendOperation not supported");
        }

        switch (alphaOp) {
            case SBO_ADD:
                alphaFunc = GL20.GL_FUNC_ADD;
                break;
            case SBO_SUBTRACT:
                alphaFunc = GL20.GL_FUNC_SUBTRACT;
                break;
            case SBO_REVERSE_SUBTRACT:
                alphaFunc = GL20.GL_FUNC_REVERSE_SUBTRACT;
                break;
            case SBO_MIN:
                //	alphaFunc = GL20.GL_MIN;
                //	break;
            case SBO_MAX:
                //	alphaFunc = GL20.GL_MAX;
                //	break;
                throw new IllegalArgumentException("This SceneBlendOperation not supported");
        }

        MTGLES20.glBlendEquationSeparate(func, alphaFunc);
    }

    @Override
    public void _setSurfaceParams(ENG_ColorValue ambient,
                                  ENG_ColorValue diffuse, ENG_ColorValue specular,
                                  ENG_ColorValue emissive, float shininess, int tracking) {
        

    }

    public int getTextureAddressingMode(TextureAddressingMode tam) {
        switch (tam) {
            default:
            case TAM_WRAP:
                return GL20.GL_REPEAT;
            case TAM_MIRROR:
                return GL20.GL_MIRRORED_REPEAT;
            case TAM_CLAMP:
                return GL20.GL_CLAMP_TO_EDGE;
            case TAM_BORDER:
                throw new IllegalArgumentException("GL_CLAMP_TO_BORDER not supported");
                //return GL20.GL_CLAMP_TO_BORDER;
        }
    }

    /** @noinspection deprecation */
    @Override
    public void _setTextureAddressingMode(int stage, UVWAddressingMode uvw) {
        

        if (!activateGLTextureUnit(stage)) {
            return;
        }
        MTGLES20.glTexParameteri(mTextureTypes[stage], GL20.GL_TEXTURE_WRAP_S,
                getTextureAddressingMode(uvw.u));
        MTGLES20.glTexParameteri(mTextureTypes[stage], GL20.GL_TEXTURE_WRAP_T,
                getTextureAddressingMode(uvw.v));
        //	MTGLES20.glTexParameteri( mTextureTypes[stage], GLES20.GL_TEXTURE_WRAP_R,
        //		getTextureAddressingMode(uvw.w));
        activateGLTextureUnit(0);
    }

    public int getBlendMode(SceneBlendFactor ogreBlend) {
        switch (ogreBlend) {
            case SBF_ONE:
                return GL20.GL_ONE;
            case SBF_ZERO:
                return GL20.GL_ZERO;
            case SBF_DEST_COLOUR:
                return GL20.GL_DST_COLOR;
            case SBF_SOURCE_COLOUR:
                return GL20.GL_SRC_COLOR;
            case SBF_ONE_MINUS_DEST_COLOUR:
                return GL20.GL_ONE_MINUS_DST_COLOR;
            case SBF_ONE_MINUS_SOURCE_COLOUR:
                return GL20.GL_ONE_MINUS_SRC_COLOR;
            case SBF_DEST_ALPHA:
                return GL20.GL_DST_ALPHA;
            case SBF_SOURCE_ALPHA:
                return GL20.GL_SRC_ALPHA;
            case SBF_ONE_MINUS_DEST_ALPHA:
                return GL20.GL_ONE_MINUS_DST_ALPHA;
            case SBF_ONE_MINUS_SOURCE_ALPHA:
                return GL20.GL_ONE_MINUS_SRC_ALPHA;
        }
        // to keep compiler happy
        return GL20.GL_ONE;
    }

    @Override
    public void _setTextureBlendMode(int stage, LayerBlendModeEx bm) {
        

        //Useless.
//        if (stage >= mFixedFunctionTextureUnits) {
//            // Can't do this
//            return;
//        }
//
//        // Check to see if blending is supported
//        if (!mCurrentCapabilities.hasCapability(Capabilities.RSC_BLENDING)) {
//            return;
//        }
//
//        int src1op, src2op, cmd;
//        float cv1[] = new float[4], cv2[] = new float[4];
//
//        if (bm.blendType == LayerBlendType.LBT_COLOUR) {
//            cv1[0] = bm.colourArg1.r;
//            cv1[1] = bm.colourArg1.g;
//            cv1[2] = bm.colourArg1.b;
//            cv1[3] = bm.colourArg1.a;
//            mManualBlendColours[stage][0] = bm.colourArg1;
//
//
//            cv2[0] = bm.colourArg2.r;
//            cv2[1] = bm.colourArg2.g;
//            cv2[2] = bm.colourArg2.b;
//            cv2[3] = bm.colourArg2.a;
//            mManualBlendColours[stage][1] = bm.colourArg2;
//        }
//
//        if (bm.blendType == LayerBlendType.LBT_ALPHA) {
//            cv1[0] = mManualBlendColours[stage][0].r;
//            cv1[1] = mManualBlendColours[stage][0].g;
//            cv1[2] = mManualBlendColours[stage][0].b;
//            cv1[3] = bm.alphaArg1;
//
//            cv2[0] = mManualBlendColours[stage][1].r;
//            cv2[1] = mManualBlendColours[stage][1].g;
//            cv2[2] = mManualBlendColours[stage][1].b;
//            cv2[3] = bm.alphaArg2;
//        }
    }

    /** @noinspection deprecation */
    public void _render(ENG_RenderOperation op) {
        super._render(op);

        int pBufferData;

        boolean multitexturing = (getCapabilities().getmNumTextureUnits() > 1);

        LinkedList<ENG_VertexElement> decl = op.vertexData.vertexDeclaration.getElementList();

        attribsBound.clear();

        for (ENG_VertexElement elem : decl) {
            if (!op.vertexData.vertexBufferBinding.isBufferBound(elem.getSource())) {
                continue;
            }


            ENG_HardwareVertexBuffer vertexBuffer = op.vertexData.vertexBufferBinding.getBuffer(elem.getSource());

//            if (mCurrentCapabilities.hasCapability(Capabilities.RSC_VBO)) {
            MTGLES20.glBindBuffer(GL20.GL_ARRAY_BUFFER, ((GLHardwareVertexBuffer) vertexBuffer).getGLBufferId());

            GLUtility.checkForGLSLError(
                    "GLSLLinkProgram::GLSLLinkProgram",
                    "Error Before creating GLSL Program Object");
//                pBufferData = elem.getOffset();
//            } else {
//                throw new UnsupportedOperationException("VBO not supported?");
//            }

//            if (op.vertexData.vertexStart != 0) {
//                pBufferData += op.vertexData.vertexStart * vertexBuffer.getVertexSize();
//            }

            int i = 0;
            VertexElementSemantic sem = elem.getSemantic();

            boolean isCustomAttrib = false;
            if (mCurrentVertexProgram != null) {
                isCustomAttrib = mCurrentVertexProgram.isAttributeValid(sem, elem.getIndex());
            }

            if (isCustomAttrib) {
                int attrib = mCurrentVertexProgram.getAttributeIndex(sem, elem.getIndex());
                short typeCount = ENG_VertexElement.getTypeCount(elem.getType());
                boolean normalised = false;

                switch (elem.getType()) {
                    case VET_COLOUR:
                    case VET_COLOUR_ABGR:
                    case VET_COLOUR_ARGB:
                        // Because GL takes these as a sequence of single unsigned bytes, count needs to be 4
                        // VertexElement::getTypeCount treats them as 1 (RGBA)
                        // Also need to normalise the fixed-point data
                        typeCount = 4;
                        normalised = true;
                        break;
                    default:
                        break;
                }

                MTGLES20.glVertexAttribPointer(attrib, typeCount, GLHardwareBufferManager.getGLType(elem.getType()),
                        normalised, vertexBuffer.getVertexSize(), elem.getOffset());
                MTGLES20.glEnableVertexAttribArray(attrib);

                GLUtility.checkForGLSLError(
                        "GLSLLinkProgram::GLSLLinkProgram",
                        "Error Before creating GLSL Program Object");
                attribsBound.add(new ENG_Integer(attrib));
            } else {

            }

        }


        if (multitexturing) {
            MTGLES20.glActiveTexture(GL20.GL_TEXTURE0);
        }

        GLUtility.checkForGLSLError(
                "GLSLLinkProgram::GLSLLinkProgram",
                "Error Before creating GLSL Program Object");

        int primType = -1;

        switch (op.operationType) {
            case OT_POINT_LIST:
                primType = GL20.GL_POINTS;
                break;
            case OT_LINE_LIST:
                primType = GL20.GL_LINES;
                break;
            case OT_LINE_STRIP:
                primType = GL20.GL_LINE_STRIP;
                break;
            case OT_TRIANGLE_LIST:
                primType = GL20.GL_TRIANGLES;
                break;
            case OT_TRIANGLE_STRIP:
                primType = GL20.GL_TRIANGLE_STRIP;
                break;
            case OT_TRIANGLE_FAN:
                primType = GL20.GL_TRIANGLE_FAN;
                break;
        }

        if (op.useIndexes) {
            if (mCurrentCapabilities.hasCapability(Capabilities.RSC_VBO)) {
                MTGLES20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, ((GLHardwareIndexBuffer) op.indexData.indexBuffer).getGLBufferId());
                pBufferData = op.indexData.indexStart * op.indexData.indexBuffer.getIndexSize();
            } else {
                throw new UnsupportedOperationException("VBO not supported?");
            }

            GLUtility.checkForGLSLError(
                    "GLSLLinkProgram::GLSLLinkProgram",
                    "Error Before creating GLSL Program Object");

            int indexType = (op.indexData.indexBuffer.getIndexType() == IndexType.IT_16BIT) ? GL20.GL_UNSIGNED_SHORT : GL20.GL_UNSIGNED_INT;

            // Update derived depth bias
            if (mDerivedDepthBias && mCurrentPassIterationNum > 0) {
                _setDepthBias(mDerivedDepthBiasBase + mDerivedDepthBiasMultiplier * mCurrentPassIterationNum, mDerivedDepthBiasSlopeScale);
            }
            MTGLES20.glDrawElements(primType, op.indexData.indexCount, indexType, pBufferData);

            GLUtility.checkForGLSLError(
                    "GLSLLinkProgram::GLSLLinkProgram",
                    "Error Before creating GLSL Program Object");
        } else {
            // Update derived depth bias
            if (mDerivedDepthBias && mCurrentPassIterationNum > 0) {
                _setDepthBias(mDerivedDepthBiasBase + mDerivedDepthBiasMultiplier * mCurrentPassIterationNum, mDerivedDepthBiasSlopeScale);
            }
            MTGLES20.glDrawArrays(primType, 0, op.vertexData.vertexCount);

            GLUtility.checkForGLSLError(
                    "GLSLLinkProgram::GLSLLinkProgram",
                    "Error Before creating GLSL Program Object");
        }

        for (int i = 0; i < attribsBound.size(); ++i) {
            MTGLES20.glDisableVertexAttribArray(attribsBound.get(i));
        }

        GLUtility.checkForGLSLError(
                "GLSLLinkProgram::GLSLLinkProgram",
                "Error Before creating GLSL Program Object");
    }

    @Override
    public void _setTextureBorderColour(int unit, ENG_ColorValue colour) {
        

    }

    @Override
    public void _setTextureCoordCalculation(int unit, TexCoordCalcMethod m,
                                            ENG_Frustum frustum) {
        

    }

    @Override
    public void _setTextureCoordSet(int unit, int index) {
        

    }

    @Override
    public void _setTextureLayerAnisotropy(int unit, int maxAnisotropy) {
        

    }

    @Override
    public void _setTextureMatrix(int unit, ENG_Matrix4 xform) {
        

    }

    @Override
    public void _setTextureMipmapBias(int unit, float bias) {
        

    }

    /** @noinspection deprecation */
    @Override
    public void _setTextureUnitFiltering(int unit, FilterType ftype,
                                         FilterOptions fo) {
        

        if (!activateGLTextureUnit(unit)) {
            return;
        }

        switch (ftype) {
            case FT_MIN:
                mMinFilter = fo;
                // Combine with existing mip filter
                MTGLES20.glTexParameteri(
                        mTextureTypes[unit],
                        GL20.GL_TEXTURE_MIN_FILTER,
                        getCombinedMinMipFilter());
                break;
            case FT_MAG:
                switch (fo) {
                    case FO_ANISOTROPIC: // GL treats linear and aniso the same
                    case FO_LINEAR:
                        MTGLES20.glTexParameteri(
                                mTextureTypes[unit],
                                GL20.GL_TEXTURE_MAG_FILTER,
                                GL20.GL_LINEAR);
                        break;
                    case FO_POINT:
                    case FO_NONE:
                        MTGLES20.glTexParameteri(
                                mTextureTypes[unit],
                                GL20.GL_TEXTURE_MAG_FILTER,
                                GL20.GL_NEAREST);
                        break;
                }
                break;
            case FT_MIP:
                mMipFilter = fo;
                // Combine with existing min filter
                MTGLES20.glTexParameteri(
                        mTextureTypes[unit],
                        GL20.GL_TEXTURE_MIN_FILTER,
                        getCombinedMinMipFilter());
                break;
        }

        activateGLTextureUnit(0);
    }

    @Override
    public void _setViewMatrix(ENG_Matrix4 m) {
        

    }

    /** @noinspection deprecation */
    @Override
    public void _setViewport(ENG_Viewport vp) {
        

        // Check if viewport is different
        if (vp != mActiveViewport || vp._isUpdated()) {
            ENG_RenderTarget target = vp.getTarget();

            _setRenderTarget(target);
            mActiveViewport = vp;

            int x, y, w, h;

            // Calculate the "lower-left" corner of the viewport
            w = vp.getActualWidth();
            h = vp.getActualHeight();
            x = vp.getActualLeft();
            y = vp.getActualTop();
            if (!target.requiresTextureFlipping()) {
                // Convert "upper-left" corner to "lower-left"
                y = target.getHeight() - h - y;
            }
            MTGLES20.glViewport(x, y, w, h);

            // Configure the viewport clipping
            MTGLES20.glScissor(x, y, w, h);

            vp._clearUpdateFlag();
        }
    }

    @Override
    public void _setWorldMatrix(ENG_Matrix4 m) {
        

    }

    /** @noinspection deprecation*/
    @Override
    public void _useLights(ArrayList<ENG_Light> lights, short limit) {
        

    }

    @Override
    public void bindGpuProgramParameters(GpuProgramType gptype,
                                         ENG_GpuProgramParameters params, int mask) {
        

        if ((mask & GpuParamVariability.GPV_GLOBAL.getVariability()) != 0) {
            // We could maybe use GL_EXT_bindable_uniform here to produce Dx10-style
            // shared constant buffers, but GPU support seems fairly weak?
            // for now, just copy
            params._copySharedParams();
        }

        switch (gptype) {
            case GPT_VERTEX_PROGRAM:
                mActiveVertexGpuProgramParameters = params;
                mCurrentVertexProgram.bindProgramParameters(params, (short) mask);
                break;
            case GPT_GEOMETRY_PROGRAM:
                mActiveGeometryGpuProgramParameters = params;
                mCurrentGeometryProgram.bindProgramParameters(params, (short) mask);
                break;
            case GPT_FRAGMENT_PROGRAM:
                mActiveFragmentGpuProgramParameters = params;
                mCurrentFragmentProgram.bindProgramParameters(params, (short) mask);
                break;
        }
    }

    @Override
    public void bindGpuProgramPassIterationParameters(GpuProgramType gptype) {
        

    }

    public void bindGpuProgram(ENG_GpuProgram prg) {
        GLGpuProgram glprg = (GLGpuProgram) prg;

        switch (glprg.getType()) {
            case GPT_VERTEX_PROGRAM:
                if (mCurrentVertexProgram != glprg) {
                    if (mCurrentVertexProgram != null) {
                        mCurrentVertexProgram.unbindProgram();
                    }
                    mCurrentVertexProgram = glprg;
                }
                break;
            case GPT_FRAGMENT_PROGRAM:
                if (mCurrentFragmentProgram != glprg) {
                    if (mCurrentFragmentProgram != null)
                        mCurrentFragmentProgram.unbindProgram();
                    mCurrentFragmentProgram = glprg;
                }
                break;
            case GPT_GEOMETRY_PROGRAM:
            default:
                throw new IllegalArgumentException("Geometry program not supported!");
        }

        glprg.bindProgram();
        super.bindGpuProgram(glprg);
    }

    /** @noinspection deprecation */
    @Override
    public void clearFrameBuffer(int buffers, ENG_ColorValue colour,
                                 float depth, short stencil) {
        

        boolean colourMask = !mColourWrite[0] ||
                !mColourWrite[1] ||
                !mColourWrite[2] ||
                !mColourWrite[3];

        int flags = 0;

        if ((buffers & FrameBufferType.FBT_COLOUR.getType()) != 0) {
            flags |= GL20.GL_COLOR_BUFFER_BIT;
            if (colourMask) {
                MTGLES20.glColorMask(true, true, true, true);
            }
            MTGLES20.glClearColor(colour.r, colour.g, colour.b, colour.a);
        }
        if ((buffers & FrameBufferType.FBT_DEPTH.getType()) != 0) {
            flags |= GL20.GL_DEPTH_BUFFER_BIT;

            if (!mDepthWrite) {
                MTGLES20.glDepthMask(true);
            }
            MTGLES20.glClearDepthf(depth);
        }
        if ((buffers & FrameBufferType.FBT_STENCIL.getType()) != 0) {
            flags |= GL20.GL_STENCIL_BUFFER_BIT;

            MTGLES20.glStencilMask(0xFFFFFFFF);

            MTGLES20.glClearStencil(stencil);
        }

        //Scissors test implement later.

        //MTGLES20.glEnable(GL20.GL_SCISSOR_TEST);

        MTGLES20.glClear(flags);

        if (!mDepthWrite && ((buffers & FrameBufferType.FBT_DEPTH.getType()) != 0)) {
            //	MTGLES20.glDisable(GL20.GL_DEPTH_BUFFER_BIT);
            MTGLES20.glDepthMask(false);
        }
        if (colourMask && ((buffers & FrameBufferType.FBT_COLOUR.getType()) != 0)) {
            MTGLES20.glColorMask(mColourWrite[0], mColourWrite[1],
                    mColourWrite[2], mColourWrite[3]);
        }
        if ((buffers & FrameBufferType.FBT_STENCIL.getType()) != 0) {
            MTGLES20.glStencilMask(mStencilMask);
        }
    }

    @Override
    public ENG_MultiRenderTarget createMultiRenderTarget(String name) {
        
        ENG_MultiRenderTarget ret = mRTTManager.createMultiRenderTarget(name);
        attachRenderTarget(ret);
        return ret;
    }

    public void makeGLMatrix(float[] gl_matrix, ENG_Matrix4 m) {
        int x = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                gl_matrix[x++] = m.get(j, i);
            }
        }
    }

    public void makeGLMatrix(FloatBuffer gl_matrix, ENG_Matrix4 m) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                gl_matrix.put(m.get(j, i));
            }
        }
    }

    /** @noinspection deprecation */
    @Override
    public ENG_RenderSystemCapabilities createRenderSystemCapabilities() {
        

        ENG_RenderSystemCapabilities rsc = new ENG_RenderSystemCapabilities();

        rsc.setCategoryRelevant(CapabilitiesCategory.CAPS_CATEGORY_GL, true);
        rsc.setDriverVersion(mDriverVersion);
        rsc.setmDeviceName(MTGLES20.glGetString(GL20.GL_RENDERER));
        rsc.setmRenderSystemName(getName());
        String ver = MTGLES20.glGetString(GL20.GL_VERSION);
        String vendorName = MTGLES20.glGetString(GL20.GL_VENDOR);
        if (vendorName != null) {
            switch (vendorName) {
                case "NVIDIA":
                    rsc.setVendor(GPUVendor.GPU_NVIDIA);
                    break;
                case "ATI":
                    rsc.setVendor(GPUVendor.GPU_ATI);
                    break;
                case "Intel":
                    rsc.setVendor(GPUVendor.GPU_INTEL);
                    break;
                case "S3":
                    rsc.setVendor(GPUVendor.GPU_S3);
                    break;
                case "Matrox":
                    rsc.setVendor(GPUVendor.GPU_MATROX);
                    break;
                case "3DLabs":
                    rsc.setVendor(GPUVendor.GPU_3DLABS);
                    break;
                case "SiS":
                    rsc.setVendor(GPUVendor.GPU_SIS);
                    break;
                default:
                    rsc.setVendor(GPUVendor.GPU_UNKNOWN);
                    break;
            }
        } else {
            rsc.setVendor(GPUVendor.GPU_UNKNOWN);
        }

        IntBuffer texunits = ENG_Utility.allocateDirect(4).asIntBuffer();
        MTGLES20.glGetIntegervImmediate(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, texunits);
        rsc.setmNumTextureUnits((short) texunits.get(0));

        rsc.setCapability(Capabilities.RSC_CUBEMAPPING);
        rsc.setCapability(Capabilities.RSC_POINT_SPRITES);

//        IntBuffer stencil = ENG_Utility.allocateDirect(4).asIntBuffer();
//        MTGLES20.glGetIntegervImmediate(GL20.GL_STENCIL_BITS, stencil);
//        if (stencil.get(0) != 0) {
//            rsc.setCapability(Capabilities.RSC_HWSTENCIL);
//            rsc.setmStencilBufferBitDepth((short) stencil.get(0));
//        }
        rsc.setCapability(Capabilities.RSC_VBO);

        rsc.setCapability(Capabilities.RSC_VERTEX_PROGRAM);

        rsc.setCapability(Capabilities.RSC_FRAGMENT_PROGRAM);

        rsc.addShaderProfile("glsl");

        rsc.setCapability(Capabilities.RSC_SCISSOR_TEST);
        rsc.setCapability(Capabilities.RSC_USER_CLIP_PLANES);

        rsc.setCapability(Capabilities.RSC_TWO_SIDED_STENCIL);

        rsc.setCapability(Capabilities.RSC_VERTEX_FORMAT_UBYTE4);
        rsc.setCapability(Capabilities.RSC_INFINITE_FAR_PLANE);

        //	rsc.setCapability(Capabilities.RSC_FBO);

        rsc.setCapability(Capabilities.RSC_HWRENDER_TO_TEXTURE);

//        FloatBuffer pointSize = ENG_Utility.allocateDirect(8).asFloatBuffer();
//        MTGLES20.glGetFloatvImmediate(GL20.GL_ALIASED_POINT_SIZE_RANGE, pointSize);
//        if (pointSize.get(0) < pointSize.get(1)) {
//            rsc.setmMaxPointSize(pointSize.get(1));
//        } else {
//            rsc.setmMaxPointSize(pointSize.get(0));
//        }

//        IntBuffer units = ENG_Utility.allocateDirect(4).asIntBuffer();
//        MTGLES20.glGetIntegervImmediate(GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, units);
//        rsc.setmNumVertexTextureUnits((short) units.get(0));
//        if (units.get(0) > 0) {
//            rsc.setCapability(Capabilities.RSC_VERTEX_TEXTURE_FETCH);
//        }
        rsc.setmVertexTextureUnitsShared(true);

        rsc.setCapability(Capabilities.RSC_MIPMAP_LOD_BIAS);

        rsc.setCapability(Capabilities.RSC_ADVANCED_BLEND_OPERATIONS);

//        IntBuffer maxRenderbufferSize = ENG_Utility.allocateDirect(4).asIntBuffer();
//        MTGLES20.glGetIntegervImmediate(
//                GL20.GL_MAX_RENDERBUFFER_SIZE, maxRenderbufferSize);
        return rsc;
    }

    @Override
    public VertexElementType getColorVertexElementType() {
        
        return VertexElementType.VET_COLOUR_ABGR;
    }

    @Override
    public TreeMap<String, ENG_ConfigOption> getConfigOptions() {
        
        return null;
    }

    @Override
    public int getDisplayMonitorCount() {
        
        return 0;
    }


    @Override
    public float getHorizontalTexelOffset() {
        
        return 0;
    }

    @Override
    public float getMaximumDepthInputValue() {
        
        return 1.0f;
    }

    @Override
    public float getMinimumDepthInputValue() {
        
        return -1.0f;
    }

    @Override
    public String getName() {
        
        return "OpenGL Rendering Subsystem";
    }

    @Override
    public float getVerticalTexelOffset() {
        
        return 0;
    }

    @Override
    protected void initialiseFromRenderSystemCapabilities(
            ENG_RenderSystemCapabilities caps, ENG_RenderTarget primary) {
        

//        if (!caps.getmRenderSystemName().equals(getName())) {
//            throw new IllegalArgumentException(
//                    "Trying to initialize GLRenderSystem from" + "" +
//                            "RenderSystemCapabilities that do not support OpenGL");
//        }


        ENG_HardwareBufferManager mHardwareBufferManager = new GLHardwareBufferManager();
        ENG_RenderRoot.getRenderRoot().setHardwareBufferManager(mHardwareBufferManager);

        mGpuProgramManager = new GLGpuProgramManager();

//        if (caps.isShaderProfileSupported("glsl")) {
            mGLSLProgramFactory = new GLSLProgramFactory();
            ENG_HighLevelGpuProgramManager.getSingleton().addFactory(mGLSLProgramFactory);
//        }

//        if (caps.hasCapability(Capabilities.RSC_VBO)) {
//            if (caps.hasCapability(Capabilities.RSC_HWRENDER_TO_TEXTURE)) {
                mRTTManager = new GLFBOManager();
//            }
//        }

        mTextureManager = new GLTextureManager();

        boolean mGLInitialised = true;
    }

    public GLGpuProgramManager getGpuProgramManager() {
        return mGpuProgramManager;
    }

    public GLRTTManager getRTTManager() {
        return mRTTManager;
    }

    public GLSLProgramFactory getGLSLProgramFactory() {
        return mGLSLProgramFactory;
    }

//    public ENG_HardwareBufferManager getHardwareBufferManager() {
//        return mHardwareBufferManager;
//    }

    @Override
    public void postExtraThreadsStarted() {
        

    }

    @Override
    public void preExtraThreadsStarted() {
        

    }

    @Override
    public void registerThread() {
        

    }

    @Override
    public void reinitialize() {
        
        shutDown();
        _initialise(true, "Render Window");
    }

    @Override
    public void setAmbientLight(float r, float g, float b) {
        

    }

    @Override
    protected void setClipPlanesImpl(ArrayList<ENG_Plane> clipPlanes) {
        

    }

    @Override
    public void setConfigOption(String name, String value) {
        
//        ENG_NativeCalls.callRenderSystem_SetConfigOption(name, value);
        wrapper.setConfigOption(name, value);
    }

    @Override
    public void setLightingEnabled(boolean enabled) {
        

    }

    @Override
    public void setNormaliseNormals(boolean normalise) {
        

    }

    /** @noinspection deprecation */
    @Override
    public void setScissorTest(boolean enabled, int left, int top, int right,
                               int bottom) {
        

        // If request texture flipping, use "upper-left", otherwise use "lower-left"
        boolean flipping = mActiveRenderTarget.requiresTextureFlipping();
        int targetHeight = mActiveRenderTarget.getHeight();

        int w, h, x, y;

        if (enabled) {
            MTGLES20.glEnable(GL20.GL_SCISSOR_TEST);

            x = left;
            if (flipping)
                y = top;
            else
                y = targetHeight - bottom;
            w = right - left;
            h = bottom - top;
            MTGLES20.glScissor(x, y, w, h);
        } else {
            MTGLES20.glDisable(GL20.GL_SCISSOR_TEST);
            // GL requires you to reset the scissor when disabling
            w = mActiveViewport.getActualWidth();
            h = mActiveViewport.getActualHeight();
            x = mActiveViewport.getActualLeft();
            if (flipping) {
                y = mActiveViewport.getActualTop();
            } else {
                y = targetHeight - mActiveViewport.getActualTop() - h;
            }
            MTGLES20.glScissor(x, y, w, h);
        }
    }

    @Override
    public void setShadingType(ShadeOptions so) {
        

    }

    public int convertStencilOp(StencilOperation op, boolean invert) {
        switch (op) {
            case SOP_KEEP:
                return GL20.GL_KEEP;
            case SOP_ZERO:
                return GL20.GL_ZERO;
            case SOP_REPLACE:
                return GL20.GL_REPLACE;
            case SOP_INCREMENT:
                return invert ? GL20.GL_DECR : GL20.GL_INCR;
            case SOP_DECREMENT:
                return invert ? GL20.GL_INCR : GL20.GL_DECR;
            case SOP_INCREMENT_WRAP:
                //	return invert ? GL20.GL_DECR_WRAP_EXT : GL20.GL_INCR_WRAP_EXT;
            case SOP_DECREMENT_WRAP:
                //	return invert ? GL20.GL_INCR_WRAP_EXT : GL20.GL_DECR_WRAP_EXT;
                throw new IllegalArgumentException("SOP not supported");
            case SOP_INVERT:
                return GL20.GL_INVERT;
        }
        // to keep compiler happy
        return StencilOperation.SOP_KEEP.ordinal();
    }

    /** @noinspection deprecation */
    @Override
    public void setStencilBufferParams(CompareFunction func, int refValue,
                                       int mask, StencilOperation stencilFailOp,
                                       StencilOperation depthFailOp, StencilOperation passOp,
                                       boolean twoSidedOperation) {
        

        boolean flip;
        mStencilMask = mask;

        if (twoSidedOperation) {
            if (!mCurrentCapabilities.hasCapability(Capabilities.RSC_TWO_SIDED_STENCIL)) {
                throw new IllegalArgumentException("2-sided stencils are not supported");
            }

            flip = (mInvertVertexWinding && !mActiveRenderTarget.requiresTextureFlipping()) ||
                    (!mInvertVertexWinding && mActiveRenderTarget.requiresTextureFlipping());

            // Back
            MTGLES20.glStencilMaskSeparate(GL20.GL_BACK, mask);
            MTGLES20.glStencilFuncSeparate(GL20.GL_BACK, convertCompareFunction(func),
                    refValue, mask);
            MTGLES20.glStencilOpSeparate(GL20.GL_BACK,
                    convertStencilOp(stencilFailOp, !flip),
                    convertStencilOp(depthFailOp, !flip),
                    convertStencilOp(passOp, !flip));
            // Front
            MTGLES20.glStencilMaskSeparate(GL20.GL_FRONT, mask);
            MTGLES20.glStencilFuncSeparate(GL20.GL_FRONT, convertCompareFunction(func),
                    refValue, mask);
            MTGLES20.glStencilOpSeparate(GL20.GL_FRONT,
                    convertStencilOp(stencilFailOp, flip),
                    convertStencilOp(depthFailOp, flip),
                    convertStencilOp(passOp, flip));
        } else {
            flip = false;
            MTGLES20.glStencilMask(mask);
            MTGLES20.glStencilFunc(convertCompareFunction(func), refValue, mask);
            MTGLES20.glStencilOp(
                    convertStencilOp(stencilFailOp, flip),
                    convertStencilOp(depthFailOp, flip),
                    convertStencilOp(passOp, flip));
        }
    }

    public int getCombinedMinMipFilter() {
        switch (mMinFilter) {
            case FO_ANISOTROPIC:
            case FO_LINEAR:
                switch (mMipFilter) {
                    case FO_ANISOTROPIC:
                    case FO_LINEAR:
                        // linear min, linear mip
                        return GL20.GL_LINEAR_MIPMAP_LINEAR;
                    case FO_POINT:
                        // linear min, point mip
                        return GL20.GL_LINEAR_MIPMAP_NEAREST;
                    case FO_NONE:
                        // linear min, no mip
                        return GL20.GL_LINEAR;
                }
                break;
            case FO_POINT:
            case FO_NONE:
                switch (mMipFilter) {
                    case FO_ANISOTROPIC:
                    case FO_LINEAR:
                        // nearest min, linear mip
                        return GL20.GL_NEAREST_MIPMAP_LINEAR;
                    case FO_POINT:
                        // nearest min, point mip
                        return GL20.GL_NEAREST_MIPMAP_NEAREST;
                    case FO_NONE:
                        // nearest min, no mip
                        return GL20.GL_NEAREST;
                }
                break;
        }

        // should never get here
        return 0;
    }

    /** @noinspection deprecation */
    @Override
    public void setStencilCheckEnabled(boolean enabled) {
        

        if (enabled) {
            MTGLES20.glEnable(GL20.GL_STENCIL_TEST);
        } else {
            MTGLES20.glDisable(GL20.GL_STENCIL_TEST);
        }
    }

    @Override
    public void setVertexBufferBinding(ENG_VertexBufferBinding binding) {
        

    }

    @Override
    public void setVertexDeclaration(ENG_VertexDeclaration decl) {
        

    }

    @Override
    public void unbindGpuProgram(GpuProgramType gptype) {
        

        if (gptype == GpuProgramType.GPT_VERTEX_PROGRAM &&
                mCurrentVertexProgram != null) {
            mActiveVertexGpuProgramParameters = null;
            mCurrentVertexProgram.unbindProgram();
            mCurrentVertexProgram = null;
        } else if (gptype == GpuProgramType.GPT_FRAGMENT_PROGRAM &&
                mCurrentFragmentProgram != null) {
            mActiveFragmentGpuProgramParameters = null;
            mCurrentFragmentProgram.unbindProgram();
            mCurrentFragmentProgram = null;
        } else if (gptype == GpuProgramType.GPT_GEOMETRY_PROGRAM &&
                mCurrentGeometryProgram != null) {
            throw new IllegalArgumentException(
                    "program type cannot be geometry program");
        }
        super.unbindGpuProgram(gptype);
    }

    @Override
    public void unregisterThread() {
        

    }

    @Override
    public String validateConfigOptions() {
        
        return null;
    }

}
