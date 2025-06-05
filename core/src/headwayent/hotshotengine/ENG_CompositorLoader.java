/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_Common.CompareFunction;
import headwayent.hotshotengine.renderer.ENG_Common.FrameBufferType;
import headwayent.hotshotengine.renderer.ENG_CompositionPass;
import headwayent.hotshotengine.renderer.ENG_CompositionPass.PassType;
import headwayent.hotshotengine.renderer.ENG_CompositionTargetPass;
import headwayent.hotshotengine.renderer.ENG_CompositionTargetPass.InputMode;
import headwayent.hotshotengine.renderer.ENG_CompositionTechnique;
import headwayent.hotshotengine.renderer.ENG_CompositionTechnique.TextureDefinition;
import headwayent.hotshotengine.renderer.ENG_CompositionTechnique.TextureScope;
import headwayent.hotshotengine.renderer.ENG_Compositor;
import headwayent.hotshotengine.renderer.ENG_CompositorManager;
import headwayent.hotshotengine.renderer.ENG_PixelUtil;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_RenderSystem.StencilOperation;
import headwayent.hotshotengine.resource.ENG_CompositorInputRenderTarget;
import headwayent.hotshotengine.resource.ENG_CompositorPass;
import headwayent.hotshotengine.resource.ENG_CompositorPassClear;
import headwayent.hotshotengine.resource.ENG_CompositorPassStencil;
import headwayent.hotshotengine.resource.ENG_CompositorResource;
import headwayent.hotshotengine.resource.ENG_CompositorTarget;
import headwayent.hotshotengine.resource.ENG_CompositorTechnique;
import headwayent.hotshotengine.resource.ENG_TextureResource;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;
import headwayent.hotshotengine.scriptcompiler.ENG_CompositorCompiler;

import java.util.ArrayList;
import java.util.Map.Entry;

public class ENG_CompositorLoader {

    public static void loadCompositorList(String fileName, String path, boolean fromSDCard) {
        ArrayList<String> compositorList =
                ENG_CompilerUtil.loadListFromFile(fileName, path);

        for (String mat : compositorList) {
            String[] pathAndFileName = ENG_CompilerUtil.getPathAndFileName(mat);
            loadCompositor(pathAndFileName[1], pathAndFileName[0], fromSDCard);
        }
    }

    public static void loadCompositor(String fileName, String path, boolean fromSDCard) {

        loadCompiledResource(new ENG_CompositorCompiler().compile(fileName, path, fromSDCard));
    }

    private static void loadCompiledResource(ArrayList<ENG_CompositorResource> list) {
        for (ENG_CompositorResource res : list) {
            ENG_Compositor compositor =
                    ENG_CompositorManager.getSingleton().create(res.name);
            for (ENG_CompositorTechnique tech : res.techniqueList) {
                ENG_CompositionTechnique t = compositor.createTechnique();
                if (tech.compositorLogic != null && !tech.compositorLogic.isEmpty()) {
                    t.setCompositorLogicName(tech.compositorLogic);
                }
                for (Entry<String, ENG_TextureResource> entry :
                        tech.textureList.entrySet()) {
                    String texName = entry.getKey();
                    ENG_TextureResource texRes = entry.getValue();
                    float widthFactor = 1.0f, heightFactor = 1.0f;
                    int width = 0, height = 0;
                    boolean fsaa = true;
                    boolean hwGamma = false;
                    if (texRes.widthFactor != 0.0f) {
                        widthFactor = texRes.widthFactor;
                    }
                    if (texRes.heightFactor != 0.0f) {
                        heightFactor = texRes.heightFactor;
                    }
                    if (texRes.width != ENG_TextureResource.TARGET_WIDTH) {
                        width = texRes.width;
                    }
                    if (texRes.height != ENG_TextureResource.TARGET_HEIGHT) {
                        height = texRes.height;
                    }
                    PixelFormat pf = ENG_PixelUtil.getFormatFromName(
                            texRes.format, true, false);
                    TextureScope scope = getTextureScope(texRes);
                    TextureDefinition texDef = t.createTextureDefinition(texRes.name);
                    texDef.width = width;
                    texDef.height = height;
                    texDef.widthFactor = widthFactor;
                    texDef.heightFactor = heightFactor;
                    texDef.scope = scope;
                    texDef.fsaa = fsaa;
                    texDef.hwGammaWrite = hwGamma;
                    texDef.pooled = texRes.pooled;
                    texDef.formatList.add(pf);
                }

                for (ENG_CompositorTarget compTarget : tech.targetList) {
                    ENG_CompositionTargetPass targetPass;
                    if (compTarget.output) {
                        targetPass = t.getOutputTargetPass();
                    } else {
                        targetPass = t.createTargetPass();
                        targetPass.setOutputName(compTarget.name);
                    }
                    targetPass.setOnlyInitial(compTarget.onlyOnce);
                    targetPass.setVisibilityMask(compTarget.visibilityMask);
                    InputMode im = getInputMode(compTarget);
                    targetPass.setInputMode(im);

                    for (ENG_CompositorPass compPass : compTarget.passList) {
                        ENG_CompositionPass pass = targetPass.createPass();
                        PassType passType = getPassType(compPass);
                        pass.setType(passType);
                        pass.setMaterial(compPass.material);
                        if (compPass.identifier != -1) {
                            pass.setIdentifier(compPass.identifier);
                        }
                        pass.setFirstRenderQueue((byte) compPass.firstRenderQueue);
                        pass.setLastRenderQueue((byte) compPass.lastRenderQueue);
                        if (compPass.inputList != null) {
                            for (ENG_CompositorInputRenderTarget inputRenderTarget :
                                    compPass.inputList) {
                                pass.setInput(inputRenderTarget.sampler,
                                        inputRenderTarget.name, 0);
                            }
                        }
                        if (pass.getType() == PassType.PT_CLEAR) {
                            ENG_CompositorPassClear clear = compPass.clear;
                            int buffers = 0;
                            if (clear.color) {
                                buffers |= FrameBufferType.FBT_COLOUR.getType();
                            }
                            if (clear.depth) {
                                buffers |= FrameBufferType.FBT_DEPTH.getType();
                            }
                            if (clear.stencil) {
                                buffers |= FrameBufferType.FBT_STENCIL.getType();
                            }
                            pass.setClearBuffers(buffers);
                            pass.setClearColour(new ENG_ColorValue(
                                    clear.r, clear.g, clear.b, clear.a));
                            pass.setClearDepth(clear.depthValue);
                            pass.setClearStencil(clear.stencilValue);
                        }
                        if (pass.getType() == PassType.PT_STENCIL) {
                            ENG_CompositorPassStencil stencil = compPass.stencil;

                            pass.setStencilCheck(stencil.check);
                            pass.setStencilRefValue(stencil.refValue);
                            pass.setStencilMask(stencil.mask);
                            pass.setStencilTwoSidedOperation(stencil.twoSided);

                            CompareFunction cmpf = getStencilFunc(stencil);
                            pass.setStencilFunc(cmpf);

                            StencilOperation op = getStencilOperation(stencil.failOp);
                            pass.setStencilFailOp(op);

                            op = getStencilOperation(stencil.depthFailOp);
                            pass.setStencilDepthFailOp(op);

                            op = getStencilOperation(stencil.passOp);
                            pass.setStencilPassOp(op);

                        }
                    }
                }
            }
            compositor.load();
        }
    }

    private static StencilOperation getStencilOperation(int opCode) {
        StencilOperation op;
        switch (opCode) {
            case ENG_CompositorPassStencil.OP_ZERO:
                op = StencilOperation.SOP_ZERO;
                break;
            case ENG_CompositorPassStencil.OP_KEEP:
                op = StencilOperation.SOP_KEEP;
                break;
            case ENG_CompositorPassStencil.OP_REPLACE:
                op = StencilOperation.SOP_REPLACE;
                break;
            case ENG_CompositorPassStencil.OP_INVERT:
                op = StencilOperation.SOP_INVERT;
                break;
            case ENG_CompositorPassStencil.OP_INCREMENT:
                op = StencilOperation.SOP_INCREMENT;
                break;
            case ENG_CompositorPassStencil.OP_INCREMENT_WRAP:
                op = StencilOperation.SOP_INCREMENT_WRAP;
                break;
            case ENG_CompositorPassStencil.OP_DECREMENT:
                op = StencilOperation.SOP_DECREMENT;
                break;
            case ENG_CompositorPassStencil.OP_DECREMENT_WRAP:
                op = StencilOperation.SOP_DECREMENT_WRAP;
                break;
            default:
                throw new ENG_InvalidFieldStateException(
                        "Invalid stencil operation type: " + opCode);
        }
        return op;
    }

    private static TextureScope getTextureScope(ENG_TextureResource texRes) {
        TextureScope scope;
        switch (texRes.scope) {
            case ENG_TextureResource.GLOBAL_SCOPE:
                scope = TextureScope.TS_GLOBAL;
                break;
            case ENG_TextureResource.CHAIN_SCOPE:
                scope = TextureScope.TS_CHAIN;
                break;
            case ENG_TextureResource.LOCAL_SCOPE:
                scope = TextureScope.TS_LOCAL;
                break;
            default:
                throw new ENG_InvalidFieldStateException(
                        "Invalid scope: " + texRes.scope);
        }
        return scope;
    }

    private static InputMode getInputMode(ENG_CompositorTarget compTarget) {
        InputMode im;
        switch (compTarget.input) {
            case ENG_CompositorTarget.INPUT_NONE:
                im = InputMode.IM_NONE;
                break;
            case ENG_CompositorTarget.INPUT_PREVIOUS:
                im = InputMode.IM_PREVIOUS;
                break;
            default:
                throw new ENG_InvalidFieldStateException(
                        "Invalid input: " + compTarget.input);
        }
        return im;
    }

    private static PassType getPassType(ENG_CompositorPass compPass) {
        PassType passType;
        switch (compPass.type) {
            case ENG_CompositorPass.TYPE_CLEAR:
                passType = PassType.PT_CLEAR;
                break;
            case ENG_CompositorPass.TYPE_STENCIL:
                passType = PassType.PT_STENCIL;
                break;
            case ENG_CompositorPass.TYPE_RENDER_SCENE:
                passType = PassType.PT_RENDERSCENE;
                break;
            case ENG_CompositorPass.TYPE_RENDER_QUAD:
                passType = PassType.PT_RENDERQUAD;
                break;
            case ENG_CompositorPass.TYPE_RENDER_CUSTOM:
                passType = PassType.PT_RENDERCUSTOM;
                break;
            default:
                throw new ENG_InvalidFieldStateException(
                        "Invalid compositor pass: " + compPass.type);
        }
        return passType;
    }

    private static CompareFunction getStencilFunc(
            ENG_CompositorPassStencil stencil) {
        CompareFunction cmpf;
        switch (stencil.compFunc) {
            case ENG_CompositorPassStencil.COMP_FUNC_ALWAYS_FAIL:
                cmpf = CompareFunction.CMPF_ALWAYS_FAIL;
                break;
            case ENG_CompositorPassStencil.COMP_FUNC_LESS:
                cmpf = CompareFunction.CMPF_LESS;
                break;
            case ENG_CompositorPassStencil.COMP_FUNC_LESS_EQUAL:
                cmpf = CompareFunction.CMPF_LESS_EQUAL;
                break;
            case ENG_CompositorPassStencil.COMP_FUNC_GREATER:
                cmpf = CompareFunction.CMPF_GREATER;
                break;
            case ENG_CompositorPassStencil.COMP_FUNC_GREATER_EQUAL:
                cmpf = CompareFunction.CMPF_GREATER_EQUAL;
                break;
            case ENG_CompositorPassStencil.COMP_FUNC_NOT_EQUAL:
                cmpf = CompareFunction.CMPF_NOT_EQUAL;
                break;
            case ENG_CompositorPassStencil.COMP_FUNC_EQUAL:
                cmpf = CompareFunction.CMPF_EQUAL;
                break;
            case ENG_CompositorPassStencil.COMP_FUNC_ALWAYS_PASS:
                cmpf = CompareFunction.CMPF_ALWAYS_PASS;
                break;
            default:
                throw new ENG_InvalidFieldStateException(
                        "Invalid compare function: " +
                                stencil.compFunc);
        }
        return cmpf;
    }
}
