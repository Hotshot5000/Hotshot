//
// Created by sebas on 29-Aug-17.
//

#include "DynamicOverlayBox.h"

Hotshot::DynamicOverlayBox::DynamicOverlayBox(int x, int y, int xLen, int yLen,
                                              Ogre::ColourValue col) {
    rect.left = x;
    rect.top = y;
    rect.right = x + xLen;
    rect.bottom = y + yLen;
    int area = rect.getWidth() * rect.getHeight();
    for (int i = 0; i < area; ++i) {
        color.push_back(col);
    }
}

bool Hotshot::DynamicOverlayBox::operator==(
                                            const Hotshot::DynamicOverlayBox &b) {
    // TODO doesn't look right to compare with contains. Also colors are compared with == between floats. Maybe add epsilon?
    return rect.contains(b.rect) && (color == b.color);
}

bool Hotshot::DynamicOverlayBox::operator!=(
                                            const Hotshot::DynamicOverlayBox &b) {
    return !operator==(b);
}
