/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.scriptcompiler;

import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.exception.ENG_MultipleDeclarationException;
import headwayent.hotshotengine.exception.ENG_UndeclaredIdentifierException;
import headwayent.hotshotengine.exception.ENG_VariableNotSetException;
import headwayent.hotshotengine.resource.ENG_CompositorInputRenderTarget;
import headwayent.hotshotengine.resource.ENG_CompositorPass;
import headwayent.hotshotengine.resource.ENG_CompositorPassClear;
import headwayent.hotshotengine.resource.ENG_CompositorPassStencil;
import headwayent.hotshotengine.resource.ENG_CompositorResource;
import headwayent.hotshotengine.resource.ENG_CompositorTarget;
import headwayent.hotshotengine.resource.ENG_CompositorTechnique;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.resource.ENG_TextureResource;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class ENG_CompositorCompiler extends ENG_AbstractCompiler<ArrayList<ENG_CompositorResource>> {

    protected static boolean init;

    private static final String COMPOSITOR = "compositor";
    private static final String TECHNIQUE = "technique";
    private static final String TEXTURE = "texture";
    private static final String TEXTURE_REF = "texture_ref";
    private static final String COMPOSITOR_LOGIC = "compositor_logic";
    private static final String TARGET = "target";
    private static final String TARGET_OUTPUT = "target_output";
    private static final String TARGET_WIDTH_SCALED = "target_width_scaled";
    private static final String TARGET_HEIGHT_SCALED = "target_height_scaled";
    private static final String TARGET_WIDTH = "target_width";
    private static final String TARGET_HEIGHT = "target_height";
    private static final String POOLED = "pooled";
    private static final String SCOPE = "scope";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String INPUT = "input";
    private static final String INPUT_NONE = "none";
    private static final String INPUT_PREVIOUS = "previous";
    private static final String VISIBILITY_MASK = "visibility_mask";
    private static final String ONLY_ONCE = "only_once";
    private static final String PASS = "pass";
    private static final String MATERIAL = "material";
    private static final String IDENTIFIER = "identifier";
    private static final String FIRST_RENDER_QUEUE = "first_render_queue";
    private static final String LAST_RENDER_QUEUE = "last_render_queue";
    private static final String CLEAR = "clear";
    private static final String BUFFERS = "buffers";
    private static final String COLOR_VALUE = "color_value";
    private static final String DEPTH_VALUE = "depth_value";
    private static final String STENCIL_VALUE = "stencil_value";
    private static final String COLOR = "color";
    private static final String DEPTH = "depth";
    private static final String STENCIL = "stencil";
    private static final String CHECK = "check";
    private static final String FAIL_OP = "fail_op";
    private static final String DEPTH_FAIL_OP = "depth_fail_op";
    private static final String PASS_OP = "pass_op";
    private static final String MASK = "mask";
    private static final String COMP_FUNC = "comp_func";
    private static final String REF_VALUE = "ref_value";
    private static final String TWO_SIDED = "two_sided";

/*	public static final String PF_A8R8G8B8 = 1;
	public static final String PF_R8G8B8A8 = 2;
	public static final String PF_R8G8B8 = 3;
	public static final String PF_FLOAT16_RGBA = 4;
	public static final String PF_FLOAT16_RGB = 5;
	public static final String PF_FLOAT16_R = 6;
	public static final String PF_FLOAT32_RGBA = 7;
	public static final String PF_FLOAT32_RGB = 8;
	public static final String PF_FLOAT32_R = 9;*/

    private static final HashMap<String, ENG_Integer> pixelFormat = new HashMap<>();
    private static final HashMap<String, ENG_Integer> targetOptions = new HashMap<>();
    private static final HashMap<String, ENG_Integer> scopeOptions = new HashMap<>();
    private static final HashMap<String, ENG_Integer> inputOptions = new HashMap<>();
    private static final HashMap<String, ENG_Integer> passOptions = new HashMap<>();
    private static final HashMap<String, ENG_Integer> compFuncOptions = new HashMap<>();
    private static final HashMap<String, ENG_Integer> opOptions = new HashMap<>();
    private static int techniqueName;
    private static int passName;
//	private static boolean targetOutputFound;

    public static void init() {
        init = true;
        pixelFormat.put("pf_a8r8g8b8", new ENG_Integer(ENG_TextureResource.PF_A8R8G8B8));
        pixelFormat.put("pf_r8g8b8a8", new ENG_Integer(ENG_TextureResource.PF_R8G8B8A8));
        pixelFormat.put("pf_r8g8b8", new ENG_Integer(ENG_TextureResource.PF_R8G8B8));
        pixelFormat.put("pf_float16_rgba", new ENG_Integer(ENG_TextureResource.PF_FLOAT16_RGBA));
        pixelFormat.put("pf_float16_rgb", new ENG_Integer(ENG_TextureResource.PF_FLOAT16_RGB));
        pixelFormat.put("pf_float16_r", new ENG_Integer(ENG_TextureResource.PF_FLOAT16_R));
        pixelFormat.put("pf_float32_rgba", new ENG_Integer(ENG_TextureResource.PF_FLOAT32_RGBA));
        pixelFormat.put("pf_float32_rgb", new ENG_Integer(ENG_TextureResource.PF_FLOAT32_RGB));
        pixelFormat.put("pf_float32_r", new ENG_Integer(ENG_TextureResource.PF_FLOAT32_R));

        targetOptions.put("target_width", new ENG_Integer(ENG_TextureResource.TARGET_WIDTH));
        targetOptions.put("target_height", new ENG_Integer(ENG_TextureResource.TARGET_HEIGHT));
        targetOptions.put("target_width_scaled", new ENG_Integer(ENG_TextureResource.TARGET_WIDTH_SCALED));
        targetOptions.put("target_height_scaled", new ENG_Integer(ENG_TextureResource.TARGET_HEIGHT_SCALED));

        scopeOptions.put("local_scope", new ENG_Integer(ENG_TextureResource.LOCAL_SCOPE));
        scopeOptions.put("chain_scope", new ENG_Integer(ENG_TextureResource.CHAIN_SCOPE));
        scopeOptions.put("global_scope", new ENG_Integer(ENG_TextureResource.GLOBAL_SCOPE));

        inputOptions.put("none", new ENG_Integer(ENG_CompositorTarget.INPUT_NONE));
        inputOptions.put("previous", new ENG_Integer(ENG_CompositorTarget.INPUT_PREVIOUS));

        passOptions.put("render_quad", new ENG_Integer(ENG_CompositorPass.TYPE_RENDER_QUAD));
        passOptions.put("clear", new ENG_Integer(ENG_CompositorPass.TYPE_CLEAR));
        passOptions.put("stencil", new ENG_Integer(ENG_CompositorPass.TYPE_STENCIL));
        passOptions.put("render_scene", new ENG_Integer(ENG_CompositorPass.TYPE_RENDER_SCENE));
        passOptions.put("render_custom", new ENG_Integer(ENG_CompositorPass.TYPE_RENDER_CUSTOM));

        compFuncOptions.put("always_fail", new ENG_Integer(ENG_CompositorPassStencil.COMP_FUNC_ALWAYS_FAIL));
        compFuncOptions.put("always_pass", new ENG_Integer(ENG_CompositorPassStencil.COMP_FUNC_ALWAYS_PASS));
        compFuncOptions.put("less", new ENG_Integer(ENG_CompositorPassStencil.COMP_FUNC_LESS));
        compFuncOptions.put("less_equal", new ENG_Integer(ENG_CompositorPassStencil.COMP_FUNC_LESS_EQUAL));
        compFuncOptions.put("not_equal", new ENG_Integer(ENG_CompositorPassStencil.COMP_FUNC_NOT_EQUAL));
        compFuncOptions.put("equal", new ENG_Integer(ENG_CompositorPassStencil.COMP_FUNC_EQUAL));
        compFuncOptions.put("greater_equal", new ENG_Integer(ENG_CompositorPassStencil.COMP_FUNC_GREATER_EQUAL));
        compFuncOptions.put("greater", new ENG_Integer(ENG_CompositorPassStencil.COMP_FUNC_GREATER));

        opOptions.put("keep", new ENG_Integer(ENG_CompositorPassStencil.OP_KEEP));
        opOptions.put("zero", new ENG_Integer(ENG_CompositorPassStencil.OP_ZERO));
        opOptions.put("replace", new ENG_Integer(ENG_CompositorPassStencil.OP_REPLACE));
        opOptions.put("increment", new ENG_Integer(ENG_CompositorPassStencil.OP_INCREMENT));
        opOptions.put("decrement", new ENG_Integer(ENG_CompositorPassStencil.OP_DECREMENT));
        opOptions.put("increment_wrap", new ENG_Integer(ENG_CompositorPassStencil.OP_INCREMENT_WRAP));
        opOptions.put("decrement_wrap", new ENG_Integer(ENG_CompositorPassStencil.OP_DECREMENT_WRAP));
        opOptions.put("invert", new ENG_Integer(ENG_CompositorPassStencil.OP_INVERT));
    }

    public static ENG_TextureResource parseTexture(DataInputStream fp0) {
        ENG_TextureResource texRes = new ENG_TextureResource();
        texRes.name = ENG_CompilerUtil.getNextWord(fp0);
        String s = ENG_CompilerUtil.getNextWord(fp0);
        if (!init) {
            init();
        }
        if (s.equalsIgnoreCase(TARGET_WIDTH_SCALED)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            try {
                texRes.widthFactor = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
        } else if (s.equalsIgnoreCase(TARGET_WIDTH)) {
            ENG_Integer width = targetOptions.get(s.toLowerCase(Locale.US));
            if (width != null) {
                texRes.width = width.getValue();
            }
        } else {
            try {
                texRes.width = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
        }
        s = ENG_CompilerUtil.getNextWord(fp0);
        if (s.equalsIgnoreCase(TARGET_HEIGHT_SCALED)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            try {
                texRes.heightFactor = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
        } else if (s.equalsIgnoreCase(TARGET_HEIGHT)) {
            ENG_Integer height = targetOptions.get(s.toLowerCase(Locale.US));
            if (height != null) {
                texRes.height = height.getValue();
            }
        } else {
            try {
                texRes.height = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new ENG_InvalidFormatParsingException();
            }
        }
        s = ENG_CompilerUtil.getNextWord(fp0);
        String pf = s.toUpperCase(Locale.US);
        if (pf != null) {
            texRes.format = pf;
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
        //	s = ENG_CompilerUtil.getNextWord(fp0);
        //	boolean found = false;
        //	if (s.equalsIgnoreCase(POOLED)) {
        //No longer optional
        s = ENG_CompilerUtil.getNextWord(fp0);
        if (s.equalsIgnoreCase(TRUE)) {
            texRes.pooled = true;
        } else if (s.equalsIgnoreCase(FALSE)) {
            texRes.pooled = false;
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
        //	}
        //	s = ENG_CompilerUtil.getNextWord(fp0);
        //	if (s.equalsIgnoreCase(SCOPE)) {
        //No longer optional
        s = ENG_CompilerUtil.getNextWord(fp0);
        ENG_Integer scope = scopeOptions.get(s.toLowerCase(Locale.US));
        if (scope != null) {
            texRes.scope = scope.getValue();
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
        //	}
        return texRes;
    }

    public static void parseClearPass(DataInputStream fp0, ENG_CompositorPass pass) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
            pass.clear = new ENG_CompositorPassClear();
            boolean colorValueSet = false;
            boolean depthValueSet = false;
            boolean stencilValueSet = false;
            boolean buffersSet = false;
            while (true) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(COLOR_VALUE)) {
                    if (!colorValueSet) {
                        colorValueSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    byte r;
                    byte g;
                    byte b;
                    byte a;
                    try {
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        r = Byte.parseByte(s);
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        g = Byte.parseByte(s);
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        b = Byte.parseByte(s);
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        a = Byte.parseByte(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }
                    //	pass.clear.color = true;
                    pass.clear.r = r;
                    pass.clear.g = g;
                    pass.clear.b = b;
                    pass.clear.a = a;
                } else if (s.equalsIgnoreCase(DEPTH_VALUE)) {
                    if (!depthValueSet) {
                        depthValueSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    try {
                        pass.clear.depthValue = Float.parseFloat(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }

                } else if (s.equalsIgnoreCase(STENCIL_VALUE)) {
                    if (!stencilValueSet) {
                        stencilValueSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    try {
                        pass.clear.stencilValue = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }
                } else if (s.equalsIgnoreCase(BUFFERS)) {
                    buffersSet = true;
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    //We need a lookahead number to know how many getNextWord() to call
                    int len;
                    try {
                        len = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }
                    for (int i = 0; i < len; ++i) {
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        if (s.equalsIgnoreCase(COLOR)) {
                            pass.clear.color = true;
                        } else if (s.equalsIgnoreCase(DEPTH)) {
                            pass.clear.depth = true;
                        } else if (s.equalsIgnoreCase(STENCIL)) {
                            pass.clear.stencil = true;
                        } else {
                            throw new ENG_InvalidFormatParsingException();
                        }
                    }
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            if ((pass.clear.color && (!colorValueSet)) ||
                    (pass.clear.stencil && (!stencilValueSet)) ||
                    (pass.clear.depth && (!depthValueSet))) {
                //	throw new ENG_VariableNotSetException();

            }
            if (!buffersSet) {
                //	throw new ENG_VariableNotSetException();
                pass.clear.color = true;
                pass.clear.depth = true;
            }
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
    }

    public static void parseStencilPass(DataInputStream fp0, ENG_CompositorPass pass) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
            pass.stencil = new ENG_CompositorPassStencil();
            boolean refValueSet = false;
            boolean compFuncSet = false;
            boolean maskSet = false;
            boolean failOpSet = false;
            boolean depthFailOpSet = false;
            boolean passOpSet = false;
            boolean twoSidedSet = false;
            boolean checkSet = false;
            while (true) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(CHECK)) {
                    if (!checkSet) {
                        checkSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    int check;
                    try {
                        check = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }
                    pass.stencil.check = check != 0;
                } else if (s.equals(COMP_FUNC)) {
                    if (!compFuncSet) {
                        compFuncSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    ENG_Integer compFunc = compFuncOptions.get(s);
                    if (compFunc != null) {
                        pass.stencil.compFunc = compFunc.getValue();
                    } else {
                        throw new ENG_InvalidFormatParsingException();
                    }
                } else if (s.equalsIgnoreCase(REF_VALUE)) {
                    if (!refValueSet) {
                        refValueSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    int refValue;
                    try {
                        refValue = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }
                    pass.stencil.refValue = refValue;
                } else if (s.equalsIgnoreCase(MASK)) {
                    if (!maskSet) {
                        maskSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    int mask;
                    try {
                        mask = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }
                    pass.stencil.mask = mask;
                } else if (s.equalsIgnoreCase(FAIL_OP)) {
                    if (!failOpSet) {
                        failOpSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    ENG_Integer failOp = opOptions.get(s);
                    if (failOp != null) {
                        pass.stencil.failOp = failOp.getValue();
                    } else {
                        throw new ENG_InvalidFormatParsingException();
                    }
                } else if (s.equalsIgnoreCase(DEPTH_FAIL_OP)) {
                    if (!depthFailOpSet) {
                        depthFailOpSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    ENG_Integer depthFailOp = opOptions.get(s);
                    if (depthFailOp != null) {
                        pass.stencil.depthFailOp = depthFailOp.getValue();
                    } else {
                        throw new ENG_InvalidFormatParsingException();
                    }
                } else if (s.equalsIgnoreCase(PASS_OP)) {
                    if (!passOpSet) {
                        passOpSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    ENG_Integer passOp = opOptions.get(s);
                    if (passOp != null) {
                        pass.stencil.passOp = passOp.getValue();
                    } else {
                        throw new ENG_InvalidFormatParsingException();
                    }
                } else if (s.equalsIgnoreCase(TWO_SIDED)) {
                    if (!twoSidedSet) {
                        twoSidedSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    int twoSided;
                    try {
                        twoSided = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }
                    pass.stencil.twoSided = twoSided != 0;
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            if (!maskSet) {
                pass.stencil.mask = ENG_CompositorPassStencil.MASK_DEFAULT;
            }
            //Just let the user shoot himself in the foot??
		/*	if ((!compFuncSet) || (!failOpSet) ||
					(!depthFailOpSet) || (!passOpSet)) {
				throw new ENG_VariableNotSetException();
			}*/
        }
    }

    public static ENG_CompositorPass parsePass(DataInputStream fp0) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        ENG_CompositorPass pass = new ENG_CompositorPass();
        ENG_Integer passType = passOptions.get(s);
        if (passType != null) {
            pass.type = passType.getValue();
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
        if (pass.type == ENG_CompositorPass.TYPE_CLEAR) {
            parseClearPass(fp0, pass);
            return pass;
        } else if (pass.type == ENG_CompositorPass.TYPE_STENCIL) {
            parseStencilPass(fp0, pass);
            return pass;
        }
        boolean defName = false;
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            pass.name = String.valueOf(passName++);
            defName = true;
        } else {
            pass.name = s;
        }
        checkNameParsed(fp0, defName);
        boolean inputSet = false;
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(INPUT)) {
                if (pass.type == ENG_CompositorPass.TYPE_RENDER_QUAD) {
                    inputSet = true;
                    if (pass.inputList == null) {
                        pass.inputList =
                                new ArrayList<>();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    int sampler;
                    try {
                        sampler = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    ENG_CompositorInputRenderTarget rt =
                            new ENG_CompositorInputRenderTarget();
                    rt.name = s;
                    //CHECK IF SAMPLER < THAN MAX NUMBER OF SAMPLERS PER FRAGMENT SHADER
                    rt.sampler = sampler;
                    pass.inputList.add(rt);
                } else {
                    throw new ENG_InvalidFormatParsingException();
                }
            } else if (s.equalsIgnoreCase(MATERIAL)) {
                if (pass.type == ENG_CompositorPass.TYPE_RENDER_QUAD) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    pass.material = s;
                } else {
                    throw new ENG_InvalidFormatParsingException();
                }
            } else if (s.equalsIgnoreCase(IDENTIFIER)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                int identifier;
                try {
                    identifier = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
                pass.identifier = identifier;
            } else if (s.equalsIgnoreCase(FIRST_RENDER_QUEUE)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    pass.firstRenderQueue = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
            } else if (s.equalsIgnoreCase(LAST_RENDER_QUEUE)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                try {
                    pass.lastRenderQueue = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    throw new ENG_InvalidFormatParsingException();
                }
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        if ((pass.type == ENG_CompositorPass.TYPE_RENDER_QUAD) && (!inputSet)) {
            throw new ENG_VariableNotSetException();
        }
        return pass;
    }


    public static ENG_CompositorTarget parseTarget(
            DataInputStream fp0, boolean output) {
        String s;

        ENG_CompositorTarget comp = new ENG_CompositorTarget();
        if (output) {
            comp.name = ENG_CompositorTarget.TARGET_OUTPUT_NAME;
            comp.output = true;
        } else {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            comp.name = s;
        }
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
            boolean inputSet = false;
            boolean visibilityMaskSet = false;
            boolean onlyOnceSet = false;
            boolean renderQuadSet = false;
            boolean passFound = false;
            while (true) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(INPUT)) {
                    if (!inputSet) {
                        inputSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    ENG_Integer inputType = inputOptions.get(s);
                    if (inputType != null) {
                        comp.input = inputType.getValue();
                    } else {
                        throw new ENG_InvalidFormatParsingException();
                    }
                } else if (s.equalsIgnoreCase(VISIBILITY_MASK)) {
                    if (!visibilityMaskSet) {
                        visibilityMaskSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    try {
                        comp.visibilityMask = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }

                } else if (s.equalsIgnoreCase(ONLY_ONCE)) {
                    if (!onlyOnceSet) {
                        onlyOnceSet = true;
                    } else {
                        throw new ENG_MultipleDeclarationException();
                    }
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    int value;
                    try {
                        value = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new ENG_InvalidFormatParsingException();
                    }
                    comp.onlyOnce = value != 0;
                } else if (s.equalsIgnoreCase(PASS)) {
                    ENG_CompositorPass pass = parsePass(fp0);
                    if (pass.type == ENG_CompositorPass.TYPE_RENDER_QUAD) {
                        if (!renderQuadSet) {
                            renderQuadSet = true;
                        } else {
                            throw new ENG_MultipleDeclarationException();
                        }
                    }
                    comp.passList.add(pass);
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            //	if (!passFound) {
            //		throw new ENG_VariableNotSetException();
            //	}
            if (!visibilityMaskSet) {
                comp.visibilityMask = ENG_CompositorTarget.VISIBILITY_MASK_DEFAULT;
            }
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
        return comp;
    }

    public static ENG_CompositorTechnique parseTechnique(DataInputStream fp0) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        ENG_CompositorTechnique tech = new ENG_CompositorTechnique();
        boolean defName = false;
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            tech.name = String.valueOf(techniqueName++);
            defName = true;
        } else {
            tech.name = s;
        }
        checkNameParsed(fp0, defName);
        boolean targetOutputFound = false;
        boolean compositorLogicSet = false;
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(TEXTURE)) {
                if (tech.textureList == null) {
                    tech.textureList = new HashMap<>();
                }
                ENG_TextureResource texRes = parseTexture(fp0);
                tech.textureList.put(texRes.name, texRes);
            } else if (s.equalsIgnoreCase(TEXTURE_REF)) {
                //Name of the texture can come from anywhere.
                //Forward declaration allowed.
                //We deviate from the OGRE definition. Don't specify the compositor
                //but make sure that the texture name is unique in the file.
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                //The first is the local name.
                String localName = s;
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                //Next comes the referenced texture name.
                String refTexture = s;
                ENG_TextureResource tex = new ENG_TextureResource();
                tex.name = refTexture;
                if (tech.textureList == null) {
                    tech.textureList = new HashMap<>();
                }
                tech.textureList.put(localName, tex);
            } else if (s.equalsIgnoreCase(TARGET)) {
                passName = 0;
                tech.targetList.add(parseTarget(fp0, false));
            } else if (s.equalsIgnoreCase(TARGET_OUTPUT)) {
                if (!targetOutputFound) {
                    targetOutputFound = true;
                } else {
                    throw new ENG_MultipleDeclarationException();
                }
                tech.targetList.add(parseTarget(fp0, true));
            } else if (s.equalsIgnoreCase(COMPOSITOR_LOGIC)) {
                if (!compositorLogicSet) {
                    compositorLogicSet = true;
                } else {
                    throw new ENG_MultipleDeclarationException();
                }
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                tech.compositorLogic = s;
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        //	if (!targetOutputFound) {
        //		throw new ENG_VariableNotSetException();
        //	}
        return tech;
    }

    public static ENG_CompositorResource parseCompositor(DataInputStream fp0) {
        String s = ENG_CompilerUtil.getNextWord(fp0);
        if ((s == null) || (s.equalsIgnoreCase(BRACKET_OPEN))) {
            throw new ENG_InvalidFormatParsingException();
        }
        ENG_CompositorResource compRes = new ENG_CompositorResource();
        compRes.name = s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        if ((s != null) && (s.equalsIgnoreCase(BRACKET_OPEN))) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException();
        }
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(TECHNIQUE)) {
                compRes.techniqueList.add(parseTechnique(fp0));
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            } else {
                throw new ENG_InvalidFormatParsingException();
            }
        }
        return compRes;
    }

    public ArrayList<ENG_CompositorResource> compileImpl(
            String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path,
                    fromSDCard);
            String s;
            String dir;
            String currentDir = "";
            ArrayList<ENG_CompositorResource> compRes =
                    new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                dir = ENG_CompilerUtil.checkDirChange(s, fp0);
                if (dir != null) {
                }
                if (s.equalsIgnoreCase(COMPOSITOR)) {
                    techniqueName = 0;
                    compRes.add(parseCompositor(fp0));
                }
            }
            fixTextureReferences(compRes);
            return compRes;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    public static void fixTextureReferences(ArrayList<ENG_CompositorResource> compRes) {
        int len = compRes.size();
        int techLen;
        ENG_CompositorResource currentComp;
        HashMap<String, ENG_TextureResource> textureList;
        Set<String> keySet;
        Iterator<String> keyIt;
        String currentTextureName;
        ENG_TextureResource texture;
        for (int i = 0; i < len; ++i) {
            currentComp = compRes.get(i);
            techLen = currentComp.techniqueList.size();
            for (int j = 0; j < techLen; ++j) {
                textureList = currentComp.techniqueList.get(j).textureList;
                keySet = textureList.keySet();
                keyIt = keySet.iterator();
                while (keyIt.hasNext()) {
                    currentTextureName = keyIt.next();
                    texture = textureList.get(currentTextureName);
                    if (!texture.name.equals(currentTextureName)) {
                        //We have a texture reference. Now we must find the referenced
                        //texture.
                        findReference(
                                compRes, textureList, texture, currentTextureName);
                    }
                }
            }
        }
    }

    public static void findReference(
            ArrayList<ENG_CompositorResource> compRes,
            HashMap<String, ENG_TextureResource> textureListToIgnore,
            ENG_TextureResource textureToFind, String name) {
        int len = compRes.size();
        int techLen;
        ENG_CompositorResource currentComp;
        HashMap<String, ENG_TextureResource> textureList;
        Set<String> keySet = null;
        Iterator<String> keyIt = null;
        String currentTextureName = null;
        ENG_TextureResource texture = null;
        boolean found = false;
        for (int i = 0; i < len; ++i) {
            if (found) {
                break;
            }
            currentComp = compRes.get(i);
            techLen = currentComp.techniqueList.size();

            for (int j = 0; j < techLen; ++j) {
                textureList = currentComp.techniqueList.get(j).textureList;
                if (textureList == textureListToIgnore) {
                    continue;
                }
                ENG_TextureResource res = textureList.get(textureToFind.name);
                if ((res != null) && (res.scope != ENG_TextureResource.LOCAL_SCOPE)) {
                    found = true;
                    textureListToIgnore.put(name, res);
                    break;
                }
            }
        }
        if (!found) {
            throw new ENG_UndeclaredIdentifierException();
        }
    }
}
