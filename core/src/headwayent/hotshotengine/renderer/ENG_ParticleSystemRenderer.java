/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_StringIntefaceInterface;
import headwayent.hotshotengine.ENG_StringInterface;
import headwayent.hotshotengine.renderer.ENG_Common.SortMode;

import java.util.LinkedList;

public abstract class ENG_ParticleSystemRenderer implements
        ENG_StringIntefaceInterface {

    private final ENG_StringInterface stringInterface = new ENG_StringInterface(this);

    public void destroy(boolean skipGLDelete) {

    }

    @Override
    public ENG_StringInterface getStringInterface() {

        return stringInterface;
    }

    public abstract String getType();

    public abstract void _updateRenderQueue(ENG_RenderQueue queue,
                                            LinkedList<ENG_Particle> currentParticles, boolean cullIndividually);

    /**
     * Sets the material this renderer must use; called by ParticleSystem.
     */
    public abstract void _setMaterial(ENG_Material mat);

    /**
     * Delegated to by ParticleSystem::_notifyCurrentCamera
     */
    public abstract void _notifyCurrentCamera(ENG_Camera cam);

    /**
     * Delegated to by ParticleSystem::_notifyAttached
     */
    public void _notifyAttached(ENG_Node parent) {
        _notifyAttached(parent, false);
    }

    public abstract void _notifyAttached(ENG_Node parent, boolean isTagPoint);

    /**
     * Optional callback notified when particles are rotated
     */
    public void _notifyParticleRotated() {
    }

    /**
     * Optional callback notified when particles are resized individually
     */
    public void _notifyParticleResized() {
    }

    /**
     * Tells the renderer that the particle quota has changed
     */
    public abstract void _notifyParticleQuota(int quota);

    /**
     * Tells the renderer that the particle default size has changed
     */
    public abstract void _notifyDefaultDimensions(float width, float height);

    /**
     * Optional callback notified when particle emitted
     */
    public void _notifyParticleEmitted(ENG_Particle particle) {
    }

    /**
     * Optional callback notified when particle expired
     */
    public void _notifyParticleExpired(ENG_Particle particle) {
    }

    /**
     * Optional callback notified when particles moved
     */
    public void _notifyParticleMoved(LinkedList<ENG_Particle> currentParticles) {
    }

    /**
     * Optional callback notified when particles cleared
     */
    public void _notifyParticleCleared(LinkedList<ENG_Particle> currentParticles) {
    }

    /**
     * Create a new ParticleVisualData instance for attachment to a particle.
     *
     * @remarks If this renderer needs additional data in each particle, then this should
     * be held in an instance of a subclass of ParticleVisualData, and this method
     * should be overridden to return a new instance of it. The default
     * behaviour is to return null.
     */
    public Object _createVisualData() {
        return null;
    }

    /**
     * Destroy a ParticleVisualData instance.
     *
     * @remarks If this renderer needs additional data in each particle, then this should
     * be held in an instance of a subclass of ParticleVisualData, and this method
     * should be overridden to destroy an instance of it. The default
     * behaviour is to do nothing.
     */
    public void _destroyVisualData(Object vis) {
        assert (vis == null);
    }

    /**
     * Sets which render queue group this renderer should target with it's
     * output.
     */
    public abstract void setRenderQueueGroup(byte queueID);

    /**
     * Setting carried over from ParticleSystem.
     */
    public abstract void setKeepParticlesInLocalSpace(boolean keepLocal);

    /**
     * Gets the desired particles sort mode of this renderer
     */
    public abstract SortMode _getSortMode();

    /**
     * Required method to allow the renderer to communicate the Renderables
     * it will be using to render the system to a visitor.
     *
     * @see MovableObject::visitRenderables
     */
    public void visitRenderables(ENG_RenderableImpl.Visitor visitor) {
        visitRenderables(visitor, false);
    }

    public abstract void visitRenderables(ENG_RenderableImpl.Visitor visitor,
                                          boolean debugRenderables);

}
