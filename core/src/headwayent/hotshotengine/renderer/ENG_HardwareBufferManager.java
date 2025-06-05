/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer.IndexType;

public class ENG_HardwareBufferManager extends ENG_HardwareBufferManagerBase {

    public final ENG_HardwareBufferManagerBase impl;
//    private static ENG_HardwareBufferManager ref;

    public ENG_HardwareBufferManager(ENG_HardwareBufferManagerBase impl) {

        this.impl = impl;
//        if (ref == null) {
//            ref = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        ref = this;
    }

    public static ENG_HardwareBufferManager getSingleton() {
//        if (ref == null) {
//            if (MainActivity.isDebugmode()) {
//                throw new NullPointerException("No object initialized");
//            }
//        }
//        return ref;
        return MainApp.getGame().getRenderRoot().getHardwareBufferManager();
    }

    @Override
    public ENG_HardwareIndexBuffer createIndexBuffer(IndexType type,
                                                     int numIndexes, int usage, boolean useShadowBuffer) {

        return impl.createIndexBuffer(type, numIndexes, usage, useShadowBuffer);
    }

    @Override
    public ENG_HardwareVertexBuffer createVertexBuffer(int vertexSize,
                                                       int numVertices, int usage, boolean useShadowBuffer) {

        return impl.createVertexBuffer(vertexSize, numVertices, usage, useShadowBuffer);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#_forceReleaseBufferCopies(headwayEnt.HotshotEngine.Renderer.ENG_HardwareVertexBuffer)
     */
    @Override
    public void _forceReleaseBufferCopies(ENG_HardwareVertexBuffer sourceBuffer) {

        impl._forceReleaseBufferCopies(sourceBuffer);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#_freeUnusedBufferCopies()
     */
    @Override
    public void _freeUnusedBufferCopies() {

        impl._freeUnusedBufferCopies();
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#_notifyIndexBufferDestroyed(headwayEnt.HotshotEngine.Renderer.ENG_HardwareIndexBuffer)
     */
    @Override
    public void _notifyIndexBufferDestroyed(ENG_HardwareIndexBuffer buf) {

        impl._notifyIndexBufferDestroyed(buf);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#_notifyVertexBufferDestroyed(headwayEnt.HotshotEngine.Renderer.ENG_HardwareVertexBuffer)
     */
    @Override
    public void _notifyVertexBufferDestroyed(ENG_HardwareVertexBuffer buf) {

        impl._notifyVertexBufferDestroyed(buf);
    }

    public void _releaseBufferCopies() {
        _releaseBufferCopies(false);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#_releaseBufferCopies(boolean)
     */
    @Override
    public void _releaseBufferCopies(boolean forceFreeUnused) {

        impl._releaseBufferCopies(forceFreeUnused);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#allocateVertexBufferCopy(headwayEnt.HotshotEngine.Renderer.ENG_HardwareVertexBuffer, headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase.BufferLicenseType, headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferLicensee, boolean)
     */
    @Override
    public ENG_HardwareVertexBuffer allocateVertexBufferCopy(
            ENG_HardwareVertexBuffer sourceBuffer,
            BufferLicenseType licenseType, ENG_HardwareBufferLicensee licensee,
            boolean copyData) {

        return impl.allocateVertexBufferCopy(sourceBuffer, licenseType, licensee,
                copyData);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#createVertexBufferBinding()
     */
    @Override
    public ENG_VertexBufferBinding createVertexBufferBinding() {

        return impl.createVertexBufferBinding();
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#createVertexDeclaration()
     */
    @Override
    public ENG_VertexDeclaration createVertexDeclaration() {

        return impl.createVertexDeclaration();
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#destroyVertexBufferBinding(headwayEnt.HotshotEngine.Renderer.ENG_VertexBufferBinding)
     */
    @Override
    public void destroyVertexBufferBinding(ENG_VertexBufferBinding binding, boolean skipGLDelete) {

        impl.destroyVertexBufferBinding(binding, skipGLDelete);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#destroyVertexDeclaration(headwayEnt.HotshotEngine.Renderer.ENG_VertexDeclaration)
     */
    @Override
    public void destroyVertexDeclaration(ENG_VertexDeclaration decl) {

        impl.destroyVertexDeclaration(decl);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#registerVertexBufferSourceAndCopy(headwayEnt.HotshotEngine.Renderer.ENG_HardwareVertexBuffer, headwayEnt.HotshotEngine.Renderer.ENG_HardwareVertexBuffer)
     */
    @Override
    public void registerVertexBufferSourceAndCopy(
            ENG_HardwareVertexBuffer source, ENG_HardwareVertexBuffer copy) {

        impl.registerVertexBufferSourceAndCopy(source, copy);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#releaseVertexBufferCopy(headwayEnt.HotshotEngine.Renderer.ENG_HardwareVertexBuffer)
     */
    @Override
    public void releaseVertexBufferCopy(ENG_HardwareVertexBuffer buffer) {

        impl.releaseVertexBufferCopy(buffer);
    }

    /* (non-Javadoc)
     * @see headwayEnt.HotshotEngine.Renderer.ENG_HardwareBufferManagerBase#touchVertexCopy(headwayEnt.HotshotEngine.Renderer.ENG_HardwareVertexBuffer)
     */
    @Override
    public void touchVertexCopy(ENG_HardwareVertexBuffer bufferCopy) {

        impl.touchVertexCopy(bufferCopy);
    }

}
