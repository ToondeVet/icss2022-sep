package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T>{
    HANLinkedList<T> list = new HANLinkedList<>();

    @Override
    public void push(T value) {
        list.addFirst(value);
    }

    @Override
    public T pop() {
        T r = list.getFirst();
        list.removeFirst();
        return r;
    }

    @Override
    public T peek() {
        return list.getFirst();
    }
}
