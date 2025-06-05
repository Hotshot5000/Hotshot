/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.exception.ENG_BufferLockException;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ENG_HardwareBuffer {

    public enum Usage {
        /**
         * Static buffer which the application rarely modifies once created. Modifying
         * the contents of this buffer will involve a performance hit.
         */
        HBU_STATIC(1),
        /**
         * Indicates the application would like to modify this buffer with the CPU
         * fairly often.
         * Buffers created with this flag will typically end up in AGP memory rather
         * than video memory.
         */
        HBU_DYNAMIC(2),
        /**
         * Indicates the application will never read the contents of the buffer back,
         * it will only ever write data. Locking a buffer with this flag will ALWAYS
         * return a pointer to new, blank memory rather than the memory associated
         * with the contents of the buffer; this avoids DMA stalls because you can
         * write to a new memory area while the previous one is being used.
         */
        HBU_WRITE_ONLY(4),
        /**
         * Indicates that the application will be refilling the contents
         * of the buffer regularly (not just updating, but generating the
         * contents from scratch), and therefore does not mind if the contents
         * of the buffer are lost somehow and need to be recreated. This
         * allows and additional level of optimisation on the buffer.
         * This option only really makes sense when combined with
         * HBU_DYNAMIC_WRITE_ONLY.
         */
        HBU_DISCARDABLE(8),
        /// Combination of HBU_STATIC and HBU_WRITE_ONLY
        HBU_STATIC_WRITE_ONLY(5),
        /**
         * Combination of HBU_DYNAMIC and HBU_WRITE_ONLY. If you use
         * this, strongly consider using HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE
         * instead if you update the entire contents of the buffer very
         * regularly.
         */
        HBU_DYNAMIC_WRITE_ONLY(6),
        /// Combination of HBU_DYNAMIC, HBU_WRITE_ONLY and HBU_DISCARDABLE
        HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE(14);

        private final int usage;

        Usage(int usage) {
            this.usage = usage;
        }

        public int getUsage() {
            return usage;
        }

        public static Usage getUsage(int usage) {
            if ((usage & 14) == 14) {
                return HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE;
            }
            if ((usage & 8) == 8) {
                return HBU_DISCARDABLE;
            }
            if ((usage & 6) == 6) {
                return HBU_DYNAMIC_WRITE_ONLY;
            }
            if ((usage & 5) == 5) {
                return HBU_STATIC_WRITE_ONLY;
            }
            if ((usage & 4) == 4) {
                return HBU_WRITE_ONLY;
            }
            if ((usage & 2) == 2) {
                return HBU_DYNAMIC;
            }
            if ((usage & 1) == 1) {
                return HBU_STATIC;
            }
            throw new IllegalArgumentException("usage " + usage + " not found");
        /*	switch (usage) {
            case 1:
        		return HBU_STATIC;
        	case 2:
        		return HBU_DYNAMIC;
        	case 4:
        		return HBU_WRITE_ONLY;
        	case 8:
        		return HBU_DISCARDABLE;
        	case 5:
        		return HBU_STATIC_WRITE_ONLY; 
        	case 6:
        		return HBU_DYNAMIC_WRITE_ONLY;
        	case 14:
        		return HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE;
        	default:
        		throw new IllegalArgumentException("usage not found");
        	}*/
        }
    }

    public enum LockOptions {
        /**
         * Normal mode, ie allows read/write and contents are preserved.
         */
        HBL_NORMAL,
        /**
         * Discards the <em>entire</em> buffer while locking; this allows optimisation to be
         * performed because synchronisation issues are relaxed. Only allowed on buffers
         * created with the HBU_DYNAMIC flag.
         */
        HBL_DISCARD,
        /**
         * Lock the buffer for reading only. Not allowed in buffers which are created with HBU_WRITE_ONLY.
         * Mandatory on static buffers, i.e. those created without the HBU_DYNAMIC flag.
         */
        HBL_READ_ONLY,
        /**
         * As HBL_NORMAL, except the application guarantees not to overwrite any
         * region of the buffer which has already been used in this frame, can allow
         * some optimisation on some APIs.
         */
        HBL_NO_OVERWRITE;

        public static LockOptions getLockOptions(int i) {
            switch (i) {
                case 0:
                    return HBL_NORMAL;
                case 1:
                    return HBL_DISCARD;
                case 2:
                    return HBL_READ_ONLY;
                case 3:
                    return HBL_NO_OVERWRITE;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid lock option");
            }
        }

    }
	/*size_t mSizeInBytes;
		    Usage mUsage;
		    bool mIsLocked;
			size_t mLockStart;
			size_t mLockSize;
			bool mSystemMemory;
            bool mUseShadowBuffer;
            HardwareBuffer* mpShadowBuffer;
            bool mShadowUpdated;
            bool mSuppressHardwareUpdate;*/

    protected int sizeInBytes;
    protected int usage;
    protected boolean isLocked;
    protected int lockStart;
    protected int lockSize;
    protected final boolean systemMemory;
    protected final boolean useShadowBuffer;
    protected ENG_HardwareBuffer shadowBuffer;
    protected boolean shadowUpdated;
    protected boolean supressHardwareUpdate;
    protected final AtomicInteger bindingCount = new AtomicInteger();

    protected void incrementBindingCount() {
        bindingCount.incrementAndGet();
    }

    protected int decrementBindingCount() {
        return bindingCount.decrementAndGet();
    }

    protected abstract Buffer lockImpl(int offset, int length, LockOptions options);

    protected abstract void unlockImpl();

    /* HardwareBuffer(Usage usage, bool systemMemory, bool useShadowBuffer)
                : mUsage(usage), mIsLocked(false), mSystemMemory(systemMemory),
                mUseShadowBuffer(useShadowBuffer), mpShadowBuffer(NULL), mShadowUpdated(false), 
                mSuppressHardwareUpdate(false) 
            {
                // If use shadow buffer, upgrade to WRITE_ONLY on hardware side
                if (useShadowBuffer && usage == HBU_DYNAMIC)
                {
                    mUsage = HBU_DYNAMIC_WRITE_ONLY;
                }
                else if (useShadowBuffer && usage == HBU_STATIC)
                {
                    mUsage = HBU_STATIC_WRITE_ONLY;
                }
            }*/
    public ENG_HardwareBuffer(int usage, boolean systemMemory,
                              boolean useShadowBuffer) {
        this.usage = usage;
        this.systemMemory = systemMemory;
        this.useShadowBuffer = useShadowBuffer;

        if ((useShadowBuffer) && (usage == Usage.HBU_DYNAMIC.getUsage())) {
            this.usage = Usage.HBU_DYNAMIC_WRITE_ONLY.getUsage();
        } else if ((useShadowBuffer) && (usage == Usage.HBU_STATIC.getUsage())) {
            this.usage = Usage.HBU_STATIC_WRITE_ONLY.getUsage();
        }
    }

    public void destroy(boolean skipGLDelete) {

    }

    public Buffer lock(int offset, int length, LockOptions options) {
        if (isLocked) {
            throw new ENG_BufferLockException("Buffer already locked");
        }
		
		/*void* ret;
				if (mUseShadowBuffer)
                {
					if (options != HBL_READ_ONLY)
					{
						// we have to assume a read / write lock so we use the shadow buffer
						// and tag for sync on unlock()
                        mShadowUpdated = true;
                    }

                    ret = mpShadowBuffer->lock(offset, length, options);
                }
                else
                {
					// Lock the real buffer if there is no shadow buffer 
                    ret = lockImpl(offset, length, options);
                    mIsLocked = true;
                }
				mLockStart = offset;
				mLockSize = length;
                return ret;*/
        Buffer ret;
        if (useShadowBuffer) {
            if (options != LockOptions.HBL_READ_ONLY) {
                shadowUpdated = true;
            }
            ret = shadowBuffer.lock(offset, length, options);
        } else {
            ret = lockImpl(offset, length, options);
            isLocked = true;
        }
        lockStart = offset;
        lockSize = length;
        return ret;
    }

    /**
     * Really bad. Just for GIWS
     *
     * @param options
     * @return
     */
    public ByteBuffer lockAsByteBuffer(int options) {
        return (ByteBuffer) lock(LockOptions.getLockOptions(options));
    }

    public Buffer lock(LockOptions options) {
        return lock(0, sizeInBytes, options);
    }

    public void unlock() {
		/*assert(isLocked() && "Cannot unlock this buffer, it is not locked!");

				// If we used the shadow buffer this time...
                if (mUseShadowBuffer && mpShadowBuffer->isLocked())
                {
                    mpShadowBuffer->unlock();
                    // Potentially update the 'real' buffer from the shadow buffer
                    _updateFromShadow();
                }
                else
                {
					// Otherwise, unlock the real one
                    unlockImpl();
                    mIsLocked = false;
                }*/
        if (!isLocked()) {
            throw new ENG_BufferLockException("Buffer not locked to unlock it");
        }
        if ((useShadowBuffer) && (shadowBuffer.isLocked())) {
            shadowBuffer.unlock();
            _updateFromShadow();
        } else {
            unlockImpl();
            isLocked = false;
        }

    }

    public abstract void readData(int offset, int length, Buffer dest);

    public abstract void writeData(int offset, int length, Buffer source,
                                   boolean discardWholeBuffer);

    public void copyData(ENG_HardwareBuffer srcBuffer, int srcOffset, int destOffset,
                         int length, boolean discardWholeBuffer) {
        Buffer srcData = srcBuffer.lock(srcOffset, length, LockOptions.HBL_READ_ONLY);
        writeData(destOffset, length, srcData, discardWholeBuffer);
        srcBuffer.unlock();
    }

    public void copyData(ENG_HardwareBuffer srcBuffer) {
        int sz = Math.min(sizeInBytes, srcBuffer.getSizeInBytes());
        copyData(srcBuffer, 0, 0, sz, true);
    }

    public void _updateFromShadow() {
		/*if (mUseShadowBuffer && mShadowUpdated && !mSuppressHardwareUpdate)
                {
                    // Do this manually to avoid locking problems
                    const void *srcData = mpShadowBuffer->lockImpl(
    					mLockStart, mLockSize, HBL_READ_ONLY);
					// Lock with discard if the whole buffer was locked, otherwise normal
					LockOptions lockOpt;
					if (mLockStart == 0 && mLockSize == mSizeInBytes)
						lockOpt = HBL_DISCARD;
					else
						lockOpt = HBL_NORMAL;
					
                    void *destData = this->lockImpl(
    					mLockStart, mLockSize, lockOpt);
					// Copy shadow to real
                    memcpy(destData, srcData, mLockSize);
                    this->unlockImpl();
                    mpShadowBuffer->unlockImpl();
                    mShadowUpdated = false;
                }*/
        if ((useShadowBuffer) && (shadowUpdated) && (!supressHardwareUpdate)) {
            Buffer srcData = shadowBuffer.lockImpl(
                    lockStart, lockSize, LockOptions.HBL_READ_ONLY);
            LockOptions lockOpt;
            if ((lockStart == 0) && (lockSize == sizeInBytes)) {
                lockOpt = LockOptions.HBL_DISCARD;
            } else {
                lockOpt = LockOptions.HBL_NORMAL;
            }
            Buffer destData = lockImpl(lockStart, lockSize, lockOpt);
            ENG_Utility.memcpy(destData, srcData, lockSize);
            unlockImpl();
            shadowBuffer.unlock();
            shadowUpdated = false;
        }
    }

    public void supressHardwareUpdate(boolean supress) {
        supressHardwareUpdate = supress;
        if (!supress) {
            _updateFromShadow();
        }
    }

    /**
     * @return the sizeInBytes
     */
    public int getSizeInBytes() {
        return sizeInBytes;
    }

    /**
     * @return the usage
     */
    public int getUsage() {
        return usage;
    }

    /**
     * @return the systemMemory
     */
    public boolean isSystemMemory() {
        return systemMemory;
    }

    /**
     * @return the useShadowBuffer
     */
    public boolean hasShadowBuffer() {
        return useShadowBuffer;
    }


    public boolean isLocked() {
        return ((isLocked) || ((useShadowBuffer) && (shadowBuffer.isLocked())));
    }
}
