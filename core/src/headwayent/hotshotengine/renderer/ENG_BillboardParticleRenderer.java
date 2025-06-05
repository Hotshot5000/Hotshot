/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_ParamCommand;
import headwayent.hotshotengine.ENG_ParamDictionary;
import headwayent.hotshotengine.ENG_ParameterDef;
import headwayent.hotshotengine.ENG_ParameterDef.ParameterType;
import headwayent.hotshotengine.ENG_StringConverter;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_BillboardSet.BillboardOrigin;
import headwayent.hotshotengine.renderer.ENG_BillboardSet.BillboardRotationType;
import headwayent.hotshotengine.renderer.ENG_BillboardSet.BillboardType;
import headwayent.hotshotengine.renderer.ENG_Common.SortMode;
import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;
import headwayent.hotshotengine.renderer.ENG_RenderableImpl.Visitor;

import java.util.LinkedList;

@Deprecated
public class ENG_BillboardParticleRenderer extends ENG_ParticleSystemRenderer {

    public static class CmdBillboardType implements ENG_ParamCommand {

        /** @noinspection deprecation*/
        @Override
        public String doGet(Object target) {

            BillboardType type =
                    ((ENG_BillboardParticleRenderer) target).getBillboardType();
            switch (type) {
                case BBT_POINT:
                    return "point";
                case BBT_ORIENTED_COMMON:
                    return "oriented_common";
                case BBT_ORIENTED_SELF:
                    return "oriented_self";
                case BBT_PERPENDICULAR_COMMON:
                    return "perpendicular_common";
                case BBT_PERPENDICULAR_SELF:
                    return "perpendicular_self";
            }
            return "";
        }

        /** @noinspection deprecation*/
        @Override
        public void doSet(Object target, String val) {

            BillboardType t;
            switch (val) {
                case "point":
                    t = BillboardType.BBT_POINT;
                    break;
                case "oriented_common":
                    t = BillboardType.BBT_ORIENTED_COMMON;
                    break;
                case "oriented_self":
                    t = BillboardType.BBT_ORIENTED_SELF;
                    break;
                case "perpendicular_common":
                    t = BillboardType.BBT_PERPENDICULAR_COMMON;
                    break;
                case "perpendicular_self":
                    t = BillboardType.BBT_PERPENDICULAR_SELF;
                    break;
                default:
                    throw new IllegalArgumentException("not a billboardtype");
            }
            ((ENG_BillboardParticleRenderer) target).setBillboardType(t);
        }

    }

    public static class CmdBillboardOrigin implements ENG_ParamCommand {

        /** @noinspection deprecation*/
        @Override
        public String doGet(Object target) {

            BillboardOrigin p =
                    ((ENG_BillboardParticleRenderer) target).getBillboardOrigin();

            switch (p) {
                case BBO_BOTTOM_CENTER:
                    return "bottom_center";
                case BBO_BOTTOM_LEFT:
                    return "bottom_left";
                case BBO_BOTTOM_RIGHT:
                    return "bottom_right";
                case BBO_CENTER:
                    return "center";
                case BBO_CENTER_LEFT:
                    return "center_left";
                case BBO_CENTER_RIGHT:
                    return "center_right";
                case BBO_TOP_CENTER:
                    return "top_center";
                case BBO_TOP_LEFT:
                    return "top_left";
                case BBO_TOP_RIGHT:
                    return "top_right";
            }
            return "";
        }

        /** @noinspection deprecation*/
        @Override
        public void doSet(Object target, String val) {

            BillboardOrigin o;
            switch (val) {
                case "bottom_center":
                    o = BillboardOrigin.BBO_BOTTOM_CENTER;
                    break;
                case "bottom_left":
                    o = BillboardOrigin.BBO_BOTTOM_LEFT;
                    break;
                case "bottom_right":
                    o = BillboardOrigin.BBO_BOTTOM_RIGHT;
                    break;
                case "center":
                    o = BillboardOrigin.BBO_CENTER;
                    break;
                case "center_left":
                    o = BillboardOrigin.BBO_CENTER_LEFT;
                    break;
                case "center_right":
                    o = BillboardOrigin.BBO_CENTER_RIGHT;
                    break;
                case "top_center":
                    o = BillboardOrigin.BBO_TOP_CENTER;
                    break;
                case "top_left":
                    o = BillboardOrigin.BBO_TOP_LEFT;
                    break;
                case "top_right":
                    o = BillboardOrigin.BBO_TOP_RIGHT;
                    break;
                default:
                    throw new IllegalArgumentException("not a billboardorigin");
            }

            ((ENG_BillboardParticleRenderer) target).setBillboardOrigin(o);
        }

    }

    public static class CmdBillboardRotationType implements ENG_ParamCommand {

        /** @noinspection deprecation*/
        @Override
        public String doGet(Object target) {

            BillboardRotationType t =
                    ((ENG_BillboardParticleRenderer) target).getBillboardRotationType();
            switch (t) {
                case BBR_TEXCOORD:
                    return "texcoord";
                case BBR_VERTEX:
                    return "vertex";
            }
            return "";
        }

        /** @noinspection deprecation*/
        @Override
        public void doSet(Object target, String val) {

            BillboardRotationType t;
            switch (val) {
                case "texcoord":
                    t = BillboardRotationType.BBR_TEXCOORD;
                    break;
                case "vertex":
                    t = BillboardRotationType.BBR_VERTEX;
                    break;
                default:
                    throw new IllegalArgumentException("not a billboardrotationtype");
            }
            ((ENG_BillboardParticleRenderer) target).setBillboardRotationType(t);
        }

    }

    public static class CmdCommonDirection implements ENG_ParamCommand {

        /** @noinspection deprecation*/
        @Override
        public String doGet(Object target) {

            return ((ENG_BillboardParticleRenderer) target)
                    .getCommonDirection().toString();
        }

        /** @noinspection deprecation*/
        @Override
        public void doSet(Object target, String val) {

            ((ENG_BillboardParticleRenderer) target).setCommonDirection(
                    ENG_StringConverter.parseVector4(val));
        }

    }

    public static class CmdCommonUpVector implements ENG_ParamCommand {

        /** @noinspection deprecation*/
        @Override
        public String doGet(Object target) {

            return ((ENG_BillboardParticleRenderer) target)
                    .getCommonUpVector().toString();
        }

        /** @noinspection deprecation*/
        @Override
        public void doSet(Object target, String val) {

            ((ENG_BillboardParticleRenderer) target).setCommonUpVector(
                    ENG_StringConverter.parseVector4(val));
        }

    }

    public static class CmdPointRendering implements ENG_ParamCommand {

        /** @noinspection deprecation*/
        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_BillboardParticleRenderer) target)
                    .isPointRenderingEnabled());
        }

        /** @noinspection deprecation*/
        @Override
        public void doSet(Object target, String val) {

            ((ENG_BillboardParticleRenderer) target).setPointRenderingEnabled(
                    Boolean.parseBoolean(val));
        }

    }

    public static class CmdAccurateFacing implements ENG_ParamCommand {

        /** @noinspection deprecation*/
        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_BillboardParticleRenderer) target)
                    .getUseAccurateFacing());
        }

        /** @noinspection deprecation*/
        @Override
        public void doSet(Object target, String val) {

            ((ENG_BillboardParticleRenderer) target).setUseAccurateFacing(
                    Boolean.parseBoolean(val));
        }

    }

    protected static final CmdBillboardType msBillboardTypeCmd = new CmdBillboardType();
    protected static final CmdBillboardOrigin msBillboardOriginCmd = new CmdBillboardOrigin();
    protected static final CmdBillboardRotationType msBillboardRotationTypeCmd = new CmdBillboardRotationType();
    protected static final CmdCommonDirection msCommonDirectionCmd = new CmdCommonDirection();
    protected static final CmdCommonUpVector msCommonUpVectorCmd = new CmdCommonUpVector();
    protected static final CmdPointRendering msPointRenderingCmd = new CmdPointRendering();
    protected static final CmdAccurateFacing msAccurateFacingCmd = new CmdAccurateFacing();

    /** @noinspection deprecation */
    protected final ENG_BillboardSet mBillboardSet = new ENG_BillboardSet("", 0, true);

    public ENG_BillboardParticleRenderer() {

        if (getStringInterface().createParamDictionary("BillboardParticleRenderer")) {
            ENG_ParamDictionary dict = getStringInterface().getParamDictionary();
            dict.addParameter(new ENG_ParameterDef("billboard_type",
                            "The type of billboard to use. 'point' means a simulated spherical particle, " +
                                    "'oriented_common' means all particles in the set are oriented around common_direction, " +
                                    "'oriented_self' means particles are oriented around their own direction, " +
                                    "'perpendicular_common' means all particles are perpendicular to common_direction, " +
                                    "and 'perpendicular_self' means particles are perpendicular to their own direction.",
                            ParameterType.PT_STRING),
                    msBillboardTypeCmd);

            dict.addParameter(new ENG_ParameterDef("billboard_origin",
                            "This setting controls the fine tuning of where a billboard appears in relation to it's position. " +
                                    "Possible value are: 'top_left', 'top_center', 'top_right', 'center_left', 'center', 'center_right', " +
                                    "'bottom_left', 'bottom_center' and 'bottom_right'. Default value is 'center'.",
                            ParameterType.PT_STRING),
                    msBillboardOriginCmd);

            dict.addParameter(new ENG_ParameterDef("billboard_rotation_type",
                            "This setting controls the billboard rotation type. " +
                                    "'vertex' means rotate the billboard's vertices around their facing direction." +
                                    "'texcoord' means rotate the billboard's texture coordinates. Default value is 'texcoord'.",
                            ParameterType.PT_STRING),
                    msBillboardRotationTypeCmd);

            dict.addParameter(new ENG_ParameterDef("common_direction",
                            "Only useful when billboard_type is oriented_common or perpendicular_common. " +
                                    "When billboard_type is oriented_common, this parameter sets the common orientation for " +
                                    "all particles in the set (e.g. raindrops may all be oriented downwards). " +
                                    "When billboard_type is perpendicular_common, this parameter sets the perpendicular vector for " +
                                    "all particles in the set (e.g. an aureola around the player and parallel to the ground).",
                            ParameterType.PT_VECTOR3),
                    msCommonDirectionCmd);

            dict.addParameter(new ENG_ParameterDef("common_up_vector",
                            "Only useful when billboard_type is perpendicular_self or perpendicular_common. This " +
                                    "parameter sets the common up-vector for all particles in the set (e.g. an aureola around " +
                                    "the player and parallel to the ground).",
                            ParameterType.PT_VECTOR3),
                    msCommonUpVectorCmd);
            dict.addParameter(new ENG_ParameterDef("point_rendering",
                            "Set whether or not particles will use point rendering " +
                                    "rather than manually generated quads. This allows for faster " +
                                    "rendering of point-oriented particles although introduces some " +
                                    "limitations too such as requiring a common particle size." +
                                    "Possible values are 'true' or 'false'.",
                            ParameterType.PT_BOOL),
                    msPointRenderingCmd);
            dict.addParameter(new ENG_ParameterDef("accurate_facing",
                            "Set whether or not particles will be oriented to the camera " +
                                    "based on the relative position to the camera rather than just " +
                                    "the camera direction. This is more accurate but less optimal. " +
                                    "Cannot be combined with point rendering.",
                            ParameterType.PT_BOOL),
                    msAccurateFacingCmd);
        }
        mBillboardSet.setBillboardsInWorldSpace(true);
    }

    public void destroy(boolean skipGLDelete) {
        mBillboardSet.destroy(skipGLDelete);
    }

    public void setBillboardType(BillboardType t) {
        mBillboardSet.setBillboardType(t);
    }

    public BillboardType getBillboardType() {
        return mBillboardSet.getBillboardType();
    }

    public void setUseAccurateFacing(boolean b) {
        mBillboardSet.setUseAccurateFacing(b);
    }

    public boolean getUseAccurateFacing() {
        return mBillboardSet.getUseAccurateFacing();
    }

    public void setBillboardOrigin(BillboardOrigin o) {
        mBillboardSet.setBillboardOrigin(o);
    }

    public BillboardOrigin getBillboardOrigin() {
        return mBillboardSet.getBillboardOrigin();
    }

    public void setBillboardRotationType(BillboardRotationType t) {
        mBillboardSet.setBillboardRotationType(t);
    }

    public BillboardRotationType getBillboardRotationType() {
        return mBillboardSet.getBillboardRotationType();
    }

    public void setCommonDirection(ENG_Vector4D v) {
        mBillboardSet.setCommonDirection(v);
    }

    public void getCommonDirection(ENG_Vector4D ret) {
        mBillboardSet.getCommonDirection(ret);
    }

    public ENG_Vector4D getCommonDirection() {
        return mBillboardSet.getCommonDirection();
    }

    public void setCommonUpVector(ENG_Vector4D v) {
        mBillboardSet.setCommonUpVector(v);
    }

    public void getCommonUpVector(ENG_Vector4D ret) {
        mBillboardSet.getCommonUpVector(ret);
    }

    public ENG_Vector4D getCommonUpVector() {
        return mBillboardSet.getCommonUpVector();
    }

    public void setPointRenderingEnabled(boolean b) {
        mBillboardSet.setPointRenderingEnabled(b);
    }

    public boolean isPointRenderingEnabled() {
        return mBillboardSet.isPointRenderingEnabled();
    }

    /** @noinspection deprecation*/
    public ENG_BillboardSet getBillboardSet() {
        return mBillboardSet;
    }

    @Override
    public String getType() {

        return "billboard";
    }

    /** @noinspection deprecation */
    private final ENG_Billboard bb = new ENG_Billboard();

    @Override
    public void _updateRenderQueue(ENG_RenderQueue queue,
                                   LinkedList<ENG_Particle> currentParticles, boolean cullIndividually) {


        mBillboardSet.setCullIndividually(cullIndividually);

        mBillboardSet.beginBillboards(currentParticles.size());
        for (ENG_Particle p : currentParticles) {
            bb.mPosition.set(p.position);
            if (mBillboardSet.getBillboardType() == BillboardType.BBT_ORIENTED_SELF ||
                    mBillboardSet.getBillboardType() ==
                            BillboardType.BBT_PERPENDICULAR_SELF) {
                bb.mDirection.set(p.direction);
                bb.mDirection.normalize();
            }
            bb.mColour.set(p.colour);
            bb.mRotation = p.rotation.valueRadians();
            bb.mOwnDimensions = p.mOwnDimensions;
            bb.mWidth = p.mWidth;
            bb.mHeight = p.mHeight;
            mBillboardSet.injectBillboard(bb);
        }

        mBillboardSet.endBillboards();

        // Update the queue
        mBillboardSet._updateRenderQueue(queue);
    }

    @Override
    public void _setMaterial(ENG_Material mat) {


        mBillboardSet.setMaterialName(mat.getName());
    }

    @Override
    public void _notifyCurrentCamera(ENG_Camera cam) {


        mBillboardSet._notifyCurrentCamera(cam);
    }

    @Override
    public void _notifyAttached(ENG_Node parent, boolean isTagPoint) {


        mBillboardSet._notifyAttached(parent, isTagPoint);
    }

    @Override
    public void _notifyParticleQuota(int quota) {


        mBillboardSet.setPoolSize(quota);
    }

    @Override
    public void _notifyDefaultDimensions(float width, float height) {


        mBillboardSet.setDefaultDimensions(width, height);
    }

    @Override
    public void setRenderQueueGroup(byte queueID) {


        assert (queueID <= RenderQueueGroupID.RENDER_QUEUE_MAX.getID());
        mBillboardSet.setRenderQueueGroup(queueID);
    }

    @Override
    public void setKeepParticlesInLocalSpace(boolean keepLocal) {


        mBillboardSet.setBillboardsInWorldSpace(!keepLocal);
    }

    @Override
    public SortMode _getSortMode() {

        return mBillboardSet._getSortMode();
    }

    @Override
    public void visitRenderables(Visitor visitor, boolean debugRenderables) {


        mBillboardSet.visitRenderables(visitor, debugRenderables);
    }

}
