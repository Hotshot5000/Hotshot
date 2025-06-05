/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer.IndexType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ENG_HardwareBufferManagerBase {

    public final HashSet<ENG_HardwareVertexBuffer> vertexBuffers = new HashSet<>();
    public final HashSet<ENG_HardwareIndexBuffer> indexBuffers = new HashSet<>();
    public final HashSet<ENG_VertexDeclaration> vertexDeclarations = new HashSet<>();
    public final HashSet<ENG_VertexBufferBinding> vertexBufferBindings = new HashSet<>();

    public final ReentrantLock vertexBuffersMutex = new ReentrantLock();
    public final ReentrantLock indexBuffersMutex = new ReentrantLock();
    public final ReentrantLock vertexDeclarationsMutex = new ReentrantLock();
    public final ReentrantLock vertexBufferBindingsMutex = new ReentrantLock();

    public enum BufferLicenseType {
        /// Licensee will only release buffer when it says so
        BLT_MANUAL_RELEASE,
        /// Licensee can have license revoked
        BLT_AUTOMATIC_RELEASE
    }

    protected static class VertexBufferLicense {
        public final ENG_HardwareVertexBuffer originalBuffer;
        public final BufferLicenseType licenseType;
        public long expiredDelay;
        public final ENG_HardwareVertexBuffer buffer;
        public final ENG_HardwareBufferLicensee licensee;

        public VertexBufferLicense(ENG_HardwareVertexBuffer originalBuffer,
                                   BufferLicenseType licenseType, long expiredDelay,
                                   ENG_HardwareVertexBuffer buffer, ENG_HardwareBufferLicensee licensee) {
            this.originalBuffer = originalBuffer;
            this.licenseType = licenseType;
            this.expiredDelay = expiredDelay;
            this.buffer = buffer;
            this.licensee = licensee;
        }
    }

    protected final HashMap<ENG_HardwareVertexBuffer, ArrayList<ENG_HardwareVertexBuffer>> freeTempVertexBufferMap = new HashMap<>();

    protected final HashMap<ENG_HardwareVertexBuffer, VertexBufferLicense> tempVertexBufferLicenses = new HashMap<>();

    protected int underUsedFrameCount;
    protected static final int UNDER_USED_FRAME_THRESHOLD = 30000;
    protected static final int EXPIRED_DELAY_FRAME_THRESHOLD = 5;

    protected final ReentrantLock tempBuffersMutex = new ReentrantLock();

    protected void destroyAllDeclarations() {
        vertexDeclarationsMutex.lock();
        for (ENG_VertexDeclaration vertexDeclaration : vertexDeclarations) {
            destroyVertexDeclarationImpl(vertexDeclaration);
        }
        vertexDeclarations.clear();
        vertexDeclarationsMutex.unlock();
    }

    protected void destroyAllBindings(boolean skipGLDelete) {
        vertexBufferBindingsMutex.lock();
        for (ENG_VertexBufferBinding vertexBufferBinding : vertexBufferBindings) {
            destroyVertexBufferBindingImpl(vertexBufferBinding, skipGLDelete);
        }
        vertexBufferBindings.clear();
        vertexBufferBindingsMutex.unlock();
    }

    protected ENG_VertexDeclaration createVertexDeclarationImpl() {
        return new ENG_VertexDeclaration();
    }

    protected void destroyVertexDeclarationImpl(ENG_VertexDeclaration decl) {

    }

    protected ENG_VertexBufferBinding createVertexBufferBindingImpl() {
        return new ENG_VertexBufferBinding();
    }

    protected void destroyVertexBufferBindingImpl(ENG_VertexBufferBinding binding, boolean skipGLDelete) {
        binding.destroy(skipGLDelete);
    }

    protected ENG_HardwareVertexBuffer makeBufferCopy(ENG_HardwareVertexBuffer source, int usage, boolean useShadowBuffer) {
        return createVertexBuffer(source.getVertexSize(), source.getNumVertices(), usage, useShadowBuffer);
    }

    // For later implementation in future versions
//	public abstract ENG_RenderToVertexBuffer createRenderToVertexBuffer();

    public ENG_HardwareVertexBuffer createVertexBuffer(int vertexSize, int numVertices, int usage) {
        return createVertexBuffer(vertexSize, numVertices, usage, false);
    }

    public abstract ENG_HardwareVertexBuffer createVertexBuffer(int vertexSize, int numVertices, int usage, boolean useShadowBuffer);

    public ENG_HardwareIndexBuffer createIndexBuffer(IndexType type, int numIndexes, int usage) {
        return createIndexBuffer(type, numIndexes, usage, false);
    }

    public abstract ENG_HardwareIndexBuffer createIndexBuffer(IndexType type, int numIndexes, int usage, boolean useShadowBuffer);

    public void destroyVertexBuffer(ENG_HardwareVertexBuffer buf, boolean skipGLDelete) {
        vertexBuffers.remove(buf);
        buf.destroy(skipGLDelete);
    }

    public void destroyIndexBuffer(ENG_HardwareIndexBuffer buf, boolean skipGLDelete) {
        indexBuffers.remove(buf);
        buf.destroy(skipGLDelete);
    }

    public ENG_VertexDeclaration createVertexDeclaration() {
        ENG_VertexDeclaration decl = createVertexDeclarationImpl();
        vertexDeclarationsMutex.lock();
        vertexDeclarations.add(decl);
        vertexDeclarationsMutex.unlock();
        return decl;
    }

    public void destroyVertexDeclaration(ENG_VertexDeclaration decl) {
        vertexDeclarationsMutex.lock();
        vertexDeclarations.remove(decl);
        vertexDeclarationsMutex.unlock();
        destroyVertexDeclarationImpl(decl);
    }

    public ENG_VertexBufferBinding createVertexBufferBinding() {
        ENG_VertexBufferBinding binding = createVertexBufferBindingImpl();
        vertexBufferBindingsMutex.lock();
        vertexBufferBindings.add(binding);
        vertexBufferBindingsMutex.unlock();
        return binding;
    }

    public void destroyVertexBufferBinding(ENG_VertexBufferBinding binding, boolean skipGLDelete) {
        vertexBufferBindingsMutex.lock();
        vertexBufferBindings.remove(binding);
        vertexBufferBindingsMutex.unlock();
        destroyVertexBufferBindingImpl(binding, skipGLDelete);
    }

    public void registerVertexBufferSourceAndCopy(ENG_HardwareVertexBuffer source, ENG_HardwareVertexBuffer copy) {
        tempBuffersMutex.lock();
        ArrayList<ENG_HardwareVertexBuffer> bufList =
                freeTempVertexBufferMap.get(source);
        if (bufList == null) {
            bufList = new ArrayList<>();
            freeTempVertexBufferMap.put(source, bufList);
        }
        bufList.add(copy);
        tempBuffersMutex.unlock();
    }

    public ENG_HardwareVertexBuffer allocateVertexBufferCopy(ENG_HardwareVertexBuffer sourceBuffer, BufferLicenseType licenseType,
            ENG_HardwareBufferLicensee licensee, boolean copyData) {
        vertexBuffersMutex.lock();
        /*OGRE_LOCK_MUTEX(mTempBuffersMutex)
			HardwareVertexBufferSharedPtr vbuf;

			// Locate existing buffer copy in temporary vertex buffers
			FreeTemporaryVertexBufferMap::iterator i = 
				mFreeTempVertexBufferMap.find(sourceBuffer.get());
			if (i == mFreeTempVertexBufferMap.end())
			{
				// copy buffer, use shadow buffer and make dynamic
				vbuf = makeBufferCopy(
					sourceBuffer, 
					HardwareBuffer::HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE, 
					true);
			}
			else
			{
				// Allocate existing copy
				vbuf = i->second;
				mFreeTempVertexBufferMap.erase(i);
			}*/
        tempBuffersMutex.lock();
        ENG_HardwareVertexBuffer vbuf;
        if (!freeTempVertexBufferMap.containsKey(sourceBuffer)) {
            vbuf = makeBufferCopy(sourceBuffer, Usage.HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE.getUsage(), true);
        } else {
            ArrayList<ENG_HardwareVertexBuffer> list = freeTempVertexBufferMap.get(sourceBuffer);
            vbuf = list.get(0);
            freeTempVertexBufferMap.remove(sourceBuffer);
        }
		/*// Copy data?
			if (copyData)
			{
				vbuf->copyData(*(sourceBuffer.get()), 0, 0, sourceBuffer->getSizeInBytes(), true);
			}

			// Insert copy into licensee list
			mTempVertexBufferLicenses.insert(
				TemporaryVertexBufferLicenseMap::value_type(
					vbuf.get(),
					VertexBufferLicense(sourceBuffer.get(), licenseType, EXPIRED_DELAY_FRAME_THRESHOLD, vbuf, licensee)));
					*/
        if (copyData) {
            vbuf.copyData(sourceBuffer, 0, 0, sourceBuffer.getSizeInBytes(), true);
        }
        tempVertexBufferLicenses.put(vbuf, new VertexBufferLicense(sourceBuffer, licenseType, EXPIRED_DELAY_FRAME_THRESHOLD, vbuf, licensee));
        tempBuffersMutex.unlock();
        vertexBuffersMutex.unlock();
        return vbuf;
    }

    public void releaseVertexBufferCopy(ENG_HardwareVertexBuffer buffer) {
		/*OGRE_LOCK_MUTEX(mTempBuffersMutex)

		TemporaryVertexBufferLicenseMap::iterator i =
            mTempVertexBufferLicenses.find(bufferCopy.get());
        if (i != mTempVertexBufferLicenses.end())
        {
            const VertexBufferLicense& vbl = i->second;

            vbl.licensee->licenseExpired(vbl.buffer.get());

            mFreeTempVertexBufferMap.insert(
                FreeTemporaryVertexBufferMap::value_type(vbl.originalBufferPtr, vbl.buffer));
            mTempVertexBufferLicenses.erase(i);
        }*/

        tempBuffersMutex.lock();
        VertexBufferLicense license = tempVertexBufferLicenses.get(buffer);
        if (license != null) {
            license.licensee.licenseExpired(license.buffer);
            ArrayList<ENG_HardwareVertexBuffer> list = freeTempVertexBufferMap.get(license.originalBuffer);
            if (list == null) {
                list = new ArrayList<>();
                freeTempVertexBufferMap.put(license.originalBuffer, list);
            }
            list.add(license.buffer);
            //	freeTempVertexBufferMap.put(
            //			license.originalBuffer, list.add(license.buffer));
            tempVertexBufferLicenses.remove(buffer);
        }
        tempBuffersMutex.unlock();
    }

    public void touchVertexCopy(ENG_HardwareVertexBuffer bufferCopy) {
		/*OGRE_LOCK_MUTEX(mTempBuffersMutex)
        TemporaryVertexBufferLicenseMap::iterator i =
            mTempVertexBufferLicenses.find(bufferCopy.get());
        if (i != mTempVertexBufferLicenses.end())
        {
            VertexBufferLicense& vbl = i->second;
            assert(vbl.licenseType == BLT_AUTOMATIC_RELEASE);

            vbl.expiredDelay = EXPIRED_DELAY_FRAME_THRESHOLD;
        }*/

        tempBuffersMutex.lock();
        VertexBufferLicense license = tempVertexBufferLicenses.get(bufferCopy);
        if (license != null) {
            if (license.licenseType != BufferLicenseType.BLT_AUTOMATIC_RELEASE) {
                throw new ENG_InvalidFieldStateException("licenseType must be BLT_AUTOMATIC_RELEASE");
            }
            license.expiredDelay = EXPIRED_DELAY_FRAME_THRESHOLD;
        }
        tempBuffersMutex.unlock();
    }

    public void _freeUnusedBufferCopies() {
		/*OGRE_LOCK_MUTEX(mTempBuffersMutex)
        size_t numFreed = 0;

        // Free unused temporary buffers
        FreeTemporaryVertexBufferMap::iterator i;
        i = mFreeTempVertexBufferMap.begin();
        while (i != mFreeTempVertexBufferMap.end())
        {
            FreeTemporaryVertexBufferMap::iterator icur = i++;
            // Free the temporary buffer that referenced by ourself only.
            // TODO: Some temporary buffers are bound to vertex buffer bindings
            // but not checked out, need to sort out method to unbind them.
            if (icur->second.useCount() <= 1)
            {
                ++numFreed;
                mFreeTempVertexBufferMap.erase(icur);
            }
        }*/

        tempBuffersMutex.lock();
        int numFreed = 0;
        for (Entry<ENG_HardwareVertexBuffer, ArrayList<ENG_HardwareVertexBuffer>> eng_hardwareVertexBufferArrayListEntry : freeTempVertexBufferMap.entrySet()) {
            freeTempVertexBufferMap.remove(eng_hardwareVertexBufferArrayListEntry.getKey());
        }
        tempBuffersMutex.unlock();
    }

    public void _releaseBufferCopies(boolean forceFreeUnused) {
		/*OGRE_LOCK_MUTEX(mTempBuffersMutex)
        size_t numUnused = mFreeTempVertexBufferMap.size();
        size_t numUsed = mTempVertexBufferLicenses.size();

        // Erase the copies which are automatic licensed out
        TemporaryVertexBufferLicenseMap::iterator i;
        i = mTempVertexBufferLicenses.begin(); 
        while (i != mTempVertexBufferLicenses.end()) 
        {
            TemporaryVertexBufferLicenseMap::iterator icur = i++;
            VertexBufferLicense& vbl = icur->second;
            if (vbl.licenseType == BLT_AUTOMATIC_RELEASE &&
                (forceFreeUnused || --vbl.expiredDelay <= 0))
            {
				vbl.licensee->licenseExpired(vbl.buffer.get());

                mFreeTempVertexBufferMap.insert(
                    FreeTemporaryVertexBufferMap::value_type(vbl.originalBufferPtr, vbl.buffer));
                mTempVertexBufferLicenses.erase(icur);
            }
        }*/

        tempBuffersMutex.lock();
        int numUnused = freeTempVertexBufferMap.size();
        int numUsed = tempVertexBufferLicenses.size();
        for (Entry<ENG_HardwareVertexBuffer, VertexBufferLicense> entry : tempVertexBufferLicenses.entrySet()) {
            VertexBufferLicense vbl = entry.getValue();
            if ((vbl.licenseType == BufferLicenseType.BLT_AUTOMATIC_RELEASE) && (forceFreeUnused || ((--vbl.expiredDelay) <= 0))) {
                vbl.licensee.licenseExpired(vbl.buffer);
                ArrayList<ENG_HardwareVertexBuffer> list = freeTempVertexBufferMap.get(vbl.originalBuffer);
                if (list == null) {
                    list = new ArrayList<>();
                    freeTempVertexBufferMap.put(vbl.originalBuffer, list);
                }
                list.add(vbl.buffer);
                //	freeTempVertexBufferMap.put(vbl.originalBuffer, );
                tempVertexBufferLicenses.remove(entry.getKey());
            }
        }
		
		/*if (forceFreeUnused)
        {
            _freeUnusedBufferCopies();
            mUnderUsedFrameCount = 0;
        }
        else
        {
            if (numUsed < numUnused)
            {
                // Free temporary vertex buffers if too many unused for a long time.
                // Do overall temporary vertex buffers instead of per source buffer
                // to avoid overhead.
                ++mUnderUsedFrameCount;
                if (mUnderUsedFrameCount >= UNDER_USED_FRAME_THRESHOLD)
                {
                    _freeUnusedBufferCopies();
                    mUnderUsedFrameCount = 0;
                }
            }
            else
            {
                mUnderUsedFrameCount = 0;
            }
        }*/

        if (forceFreeUnused) {
            _freeUnusedBufferCopies();
            underUsedFrameCount = 0;
        } else {
            if (numUsed < numUnused) {
                ++underUsedFrameCount;
                if (underUsedFrameCount >= UNDER_USED_FRAME_THRESHOLD) {
                    _freeUnusedBufferCopies();
                    underUsedFrameCount = 0;
                }
            } else {
                underUsedFrameCount = 0;
            }
        }
        tempBuffersMutex.unlock();
    }

    public void _forceReleaseBufferCopies(ENG_HardwareVertexBuffer sourceBuffer) {
		/*OGRE_LOCK_MUTEX(mTempBuffersMutex)
        // Erase the copies which are licensed out
        TemporaryVertexBufferLicenseMap::iterator i;
        i = mTempVertexBufferLicenses.begin();
        while (i != mTempVertexBufferLicenses.end()) 
        {
            TemporaryVertexBufferLicenseMap::iterator icur = i++;
            const VertexBufferLicense& vbl = icur->second;
            if (vbl.originalBufferPtr == sourceBuffer)
            {
                // Just tell the owner that this is being released
                vbl.licensee->licenseExpired(vbl.buffer.get());

                mTempVertexBufferLicenses.erase(icur);
            }
        }*/

        tempBuffersMutex.lock();
        for (Entry<ENG_HardwareVertexBuffer, VertexBufferLicense> entry : tempVertexBufferLicenses.entrySet()) {
            VertexBufferLicense vbl = entry.getValue();
            if (vbl.originalBuffer == sourceBuffer) {
                vbl.licensee.licenseExpired(vbl.buffer);
                tempVertexBufferLicenses.remove(entry.getKey());
            }
        }
        freeTempVertexBufferMap.remove(sourceBuffer);
        tempBuffersMutex.unlock();
    }

    public void _notifyVertexBufferDestroyed(ENG_HardwareVertexBuffer buf) {
        vertexBuffersMutex.lock();
        if (vertexBuffers.remove(buf)) {
            _forceReleaseBufferCopies(buf);
        }
        vertexBuffersMutex.unlock();
    }

    public void _notifyIndexBufferDestroyed(ENG_HardwareIndexBuffer buf) {
        indexBuffersMutex.lock();
        indexBuffers.remove(buf);
        indexBuffersMutex.unlock();
    }


}
