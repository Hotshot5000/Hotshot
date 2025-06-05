//
// Created by sebas on 20-Aug-17.
//

#ifndef BLACKHOLEDARKSUNONLINE5_FRAMEENDLISTENEREXECUTOR_H
#define BLACKHOLEDARKSUNONLINE5_FRAMEENDLISTENEREXECUTOR_H


class FrameEndListenerExecutor {
private:
    bool executeAfterRenderOneFrame;
public:
    FrameEndListenerExecutor() : executeAfterRenderOneFrame(false) {}

    FrameEndListenerExecutor(bool _executeAfterRenderOneFrame) : executeAfterRenderOneFrame(_executeAfterRenderOneFrame) {}
    virtual ~FrameEndListenerExecutor() {}

    virtual char* execute(char* writeBuffer) = 0;

    bool isExecuteAfterRenderOneFrame() const {
        return executeAfterRenderOneFrame;
    }

    void setExecuteAfterRenderOneFrame(bool executeAfterRenderOneFrame) {
        FrameEndListenerExecutor::executeAfterRenderOneFrame = executeAfterRenderOneFrame;
    }

};


#endif //BLACKHOLEDARKSUNONLINE5_FRAMEENDLISTENEREXECUTOR_H
