package headwayent.hotshotengine;


public class ENG_RealRect {

    public float left, top, right, bottom;

    public ENG_RealRect() {

    }

    public ENG_RealRect(float left, float top, float right, float bottom) {
        set(left, top, right, bottom);
    }

    public ENG_RealRect(ENG_RealRect rect) {
        set(rect);
    }

    public void set(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void set(ENG_RealRect rect) {
        left = rect.left;
        top = rect.top;
        right = rect.right;
        bottom = rect.bottom;
    }

    public boolean inside(float x, float y) {
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    public float width() {
        return (right - left);
    }

    public float height() {
        return (bottom - top);
    }

    public boolean isNull() {
        return ((width() == 0.0f) && (height() == 0.0f));
    }

    public void setNull() {
        left = top = right = bottom = 0.0f;
    }

    private static void mergeImpl(ENG_RealRect rect, ENG_RealRect ret) {
        if (ret.isNull()) {
            ret.set(rect);
        } else if (!rect.isNull()) {
            ret.left = Math.min(ret.left, rect.left);
            ret.right = Math.max(ret.right, rect.right);
            ret.top = Math.min(ret.top, rect.top);
            ret.bottom = Math.max(ret.bottom, rect.bottom);
        }
    }

    public void merge(ENG_RealRect rect) {
        mergeImpl(rect, this);
    }

    public void merge(ENG_RealRect rect, ENG_RealRect ret) {
        ret.set(this);
        mergeImpl(rect, ret);
    }

    public ENG_RealRect mergeRet(ENG_RealRect rect) {
        ENG_RealRect ret = new ENG_RealRect(this);
        mergeImpl(rect, ret);
        return ret;
    }

    private static void intersect(ENG_RealRect rect0, ENG_RealRect rect1,
                                  ENG_RealRect ret) {
        if (rect0.isNull() || rect1.isNull()) {
            ret.setNull();
        } else {
            ret.left = Math.max(rect0.left, rect1.left);
            ret.right = Math.min(rect0.right, rect1.right);
            ret.top = Math.max(rect0.top, rect1.top);
            ret.bottom = Math.min(rect0.bottom, rect1.bottom);
        }

        if ((ret.left > ret.right) || (ret.top > ret.bottom)) {
            ret.setNull();
        }
    }

    public void intersect(ENG_RealRect rect, ENG_RealRect ret) {
        intersect(rect, this, ret);
    }

    public ENG_RealRect intersect(ENG_RealRect rect) {
        ENG_RealRect ret = new ENG_RealRect();
        intersect(rect, this, ret);
        return ret;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ENG_RealRect) {
            ENG_RealRect rect = (ENG_RealRect) obj;
            return (left == rect.left) &&
                    (top == rect.top) &&
                    (right == rect.right) &&
                    (bottom == rect.bottom);
        }
        return false;
    }

    public String toString() {
        return "RealRect: left " + left + " top " + top + " right " + right +
                " bottom " + bottom;
    }
}
