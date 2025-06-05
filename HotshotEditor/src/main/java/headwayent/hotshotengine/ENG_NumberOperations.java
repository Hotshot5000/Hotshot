package headwayent.hotshotengine;

public interface ENG_NumberOperations<E> {

    ENG_NumberOperations<E> add(ENG_NumberOperations<E> oth);

    ENG_NumberOperations<E> sub(ENG_NumberOperations<E> oth);

    ENG_NumberOperations<E> mul(ENG_NumberOperations<E> oth);

    ENG_NumberOperations<E> div(ENG_NumberOperations<E> oth);

    ENG_NumberOperations<E> mul(float oth);

    E get();

    void set(E val);
}
