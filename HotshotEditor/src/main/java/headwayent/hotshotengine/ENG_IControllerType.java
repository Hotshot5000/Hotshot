package headwayent.hotshotengine;

import java.util.Comparator;

public interface ENG_IControllerType<T> extends Comparator<T> {

    void add(T val);

    void sub(T val);

    void mul(T val);

    void div(T val);

    T get();

    T getLowerLimit();

    T getUpperLimit();

    T getStep();
}
