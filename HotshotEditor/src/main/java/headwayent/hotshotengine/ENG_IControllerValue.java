package headwayent.hotshotengine;

public interface ENG_IControllerValue<T extends ENG_IControllerType<?>> {

    void setValue(T t);

    T getValue();
}
