/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_HardwareBufferManagerBase.BufferLicenseType;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;

public class ENG_TempBlendedBufferInfo extends ENG_HardwareBufferLicensee {

    private ENG_HardwareVertexBuffer srcPositionBuffer;
    private ENG_HardwareVertexBuffer srcNormalBuffer;
    // Post-blended 
    private ENG_HardwareVertexBuffer destPositionBuffer;
    private ENG_HardwareVertexBuffer destNormalBuffer;
    /// Both positions and normals are contained in the same buffer
    private boolean posNormalShareBuffer;
    private short posBindIndex;
    private short normBindIndex;
    private boolean bindNormals;

    public ENG_TempBlendedBufferInfo() {
        
    }

    public void destroy() {
        if (destPositionBuffer != null) {
            destPositionBuffer.getMgr().releaseVertexBufferCopy(destPositionBuffer);
            destPositionBuffer = null;
        }
        if (destNormalBuffer != null) {
            destNormalBuffer.getMgr().releaseVertexBufferCopy(destNormalBuffer);
            destNormalBuffer = null;
        }
    }

    public void extractFrom(ENG_VertexData sourceData) {
        destroy();
        ENG_VertexDeclaration decl = sourceData.vertexDeclaration;
        ENG_VertexBufferBinding bind = sourceData.vertexBufferBinding;
        ENG_VertexElement posElem = decl.findElementBySemantic(VertexElementSemantic.VES_POSITION, 0);
        ENG_VertexElement normElem = decl.findElementBySemantic(VertexElementSemantic.VES_NORMAL, 0);

        assert (posElem != null);

        posBindIndex = posElem.getSource();
        srcPositionBuffer = bind.getBuffer(posBindIndex);

        if (normElem == null) {
            posNormalShareBuffer = false;
            srcNormalBuffer = null;
        } else {
            normBindIndex = normElem.getSource();
            if (normBindIndex == posBindIndex) {
                posNormalShareBuffer = true;
                srcNormalBuffer = null;
            } else {
                posNormalShareBuffer = false;
                srcNormalBuffer = bind.getBuffer(normBindIndex);
            }
        }
    }

    public void checkoutTempCopies() {
        checkoutTempCopies(true, true);
    }

    public void checkoutTempCopies(boolean positions) {
        checkoutTempCopies(positions, true);
    }

    public void checkoutTempCopies(boolean positions, boolean normals) {
        bindNormals = normals;
        if (positions && destPositionBuffer == null) {
            destPositionBuffer = srcPositionBuffer.getMgr()
                    .allocateVertexBufferCopy(srcPositionBuffer,
                            BufferLicenseType.BLT_AUTOMATIC_RELEASE,
                            this, false);
        }

        if (normals && !posNormalShareBuffer && srcNormalBuffer != null &&
                destNormalBuffer == null) {
            destNormalBuffer = srcNormalBuffer.getMgr()
                    .allocateVertexBufferCopy(srcNormalBuffer,
                            BufferLicenseType.BLT_AUTOMATIC_RELEASE,
                            this, false);
        }
    }

    public boolean buffersCheckedOut() {
        return buffersCheckedOut(true, true);
    }

    public boolean buffersCheckedOut(boolean positions) {
        return buffersCheckedOut(positions, true);
    }

    public boolean buffersCheckedOut(boolean positions, boolean normals) {
        if (positions || (normals && posNormalShareBuffer)) {
            if (destPositionBuffer == null) {
                return false;
            }
            destPositionBuffer.getMgr().touchVertexCopy(destPositionBuffer);
        }
        if (normals && !posNormalShareBuffer) {
            if (destNormalBuffer == null) {
                return false;
            }
            destNormalBuffer.getMgr().touchVertexCopy(destNormalBuffer);
        }
        return true;
    }

    public void bindTempCopies(
            ENG_VertexData targetData, boolean suppressHardwareUpload) {
        destPositionBuffer.supressHardwareUpdate(suppressHardwareUpload);
        targetData.vertexBufferBinding.setBinding(posBindIndex, destPositionBuffer);
        if (bindNormals && !posNormalShareBuffer && destNormalBuffer != null) {
            destNormalBuffer.supressHardwareUpdate(suppressHardwareUpload);
            targetData.vertexBufferBinding.setBinding(normBindIndex, destNormalBuffer);
        }
    }

    @Override
    public void licenseExpired(ENG_HardwareBuffer buffer) {
        

        assert (buffer == destPositionBuffer || buffer == destNormalBuffer);

        if (buffer == destPositionBuffer) {
            destPositionBuffer = null;
        }
        if (buffer == destNormalBuffer) {
            destNormalBuffer = null;
        }
    }

}
