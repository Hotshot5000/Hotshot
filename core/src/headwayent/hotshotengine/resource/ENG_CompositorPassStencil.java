/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

public class ENG_CompositorPassStencil {

    /*comp_func (always_fail | always_pass | less | less_equal | not_equal |
     *  greater_equal | greater)*/
    public static final int COMP_FUNC_ALWAYS_FAIL = 0;
    public static final int COMP_FUNC_ALWAYS_PASS = 1;
    public static final int COMP_FUNC_LESS = 2;
    public static final int COMP_FUNC_LESS_EQUAL = 3;
    public static final int COMP_FUNC_NOT_EQUAL = 4;
    public static final int COMP_FUNC_EQUAL = 5;
    public static final int COMP_FUNC_GREATER_EQUAL = 6;
    public static final int COMP_FUNC_GREATER = 7;

    /*op (keep | zero | replace | increment | decrement |
     *  increment_wrap | decrement_wrap | invert)*/
    public static final int OP_KEEP = 0;
    public static final int OP_ZERO = 1;
    public static final int OP_REPLACE = 2;
    public static final int OP_INCREMENT = 3;
    public static final int OP_DECREMENT = 4;
    public static final int OP_INCREMENT_WRAP = 5;
    public static final int OP_DECREMENT_WRAP = 6;
    public static final int OP_INVERT = 7;

    public static final int MASK_DEFAULT = -1;
//	public static final int FAIL_OP_DEFAULT = OP_KEEP;
//	public static final int DEPTH_FAIL_OP_DEFAULT = OP_KEEP;
//	public static final int PASS_OP_DEFAULT = OP_KEEP;

    public int refValue;
    public int compFunc;
    public int mask;
    public int failOp;
    public int depthFailOp;
    public int passOp;
    public boolean twoSided;
    public boolean check;
}
