/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public enum ENG_Workflows {
    /// Specular workflow. Many popular PBRs use SpecularAsFresnelWorkflow
    /// though. @see setWorkflow
    SpecularWorkflow(0),

    /// Specular workflow where the specular texture is addressed to the fresnel
    /// instead of kS. This is normally referred as simply Specular workflow
    /// in many other PBRs. @see setWorkflow
    SpecularAsFresnelWorkflow(1),

    //// Metallic workflow. @see setWorkflow
    MetallicWorkflow(2);

    private final byte workflow;

    ENG_Workflows(int i) {
        workflow = (byte) i;
    }

    public byte getWorkflow() {
        return workflow;
    }

    public static ENG_Workflows toWorkflow(int value) {
        switch (value)
        {
            case 0:
                return ENG_Workflows.SpecularWorkflow;
            case 1:
                return ENG_Workflows.SpecularAsFresnelWorkflow;
            case 2:
                return ENG_Workflows.MetallicWorkflow;
            default:
                throw new IllegalArgumentException(value + " is an invalid workflow");
        }
    }
}
