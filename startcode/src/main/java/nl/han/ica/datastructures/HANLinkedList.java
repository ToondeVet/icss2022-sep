package nl.han.ica.datastructures;

class HANLinkedListItem<T> {
    T value;
    HANLinkedListItem<T> next;

    HANLinkedListItem(T value) {
        this.value = value;
    }

    public HANLinkedListItem<T> getNext() {
        return next;
    }

    public void setNext(HANLinkedListItem<T> next) {
        this.next = next;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

public class HANLinkedList<T> implements IHANLinkedList<T>{
    HANLinkedListItem<T> FirstItem;

    @Override
    public void addFirst(T value) {
        HANLinkedListItem<T> newItem = new HANLinkedListItem<>(value);
        if(FirstItem != null){
            newItem.setNext(FirstItem);
        }
        FirstItem = newItem;
    }

    @Override
    public void clear() {
        FirstItem = null;
    }

    @Override
    public void insert(int index, T value) {
        HANLinkedListItem<T> insertItem = new HANLinkedListItem<>(value);
        if(index <= 0){
            addFirst(value);
        } else if(this.getSize()-1 < index){
            HANLinkedListItem<T> lastItem = FirstItem;
            for(int i = 1; i <= index; i++) {
                if (i == index) {
                    HANLinkedListItem<T> nextItem = lastItem.getNext();
                    lastItem.setNext(insertItem);
                    insertItem.setNext(nextItem);
                } else{
                    lastItem = lastItem.getNext();
                }
            }
        }
    }

    @Override
    public void delete(int pos) {
        if(pos == 0){
            this.removeFirst();
        } else if(this.getSize()-1 < pos){
            HANLinkedListItem<T> lastItem = null;
            HANLinkedListItem<T> currentItem = FirstItem;
            for(int i = 0; i <= pos; i++) {
                if (i == pos) {
                    // this does not cause issues on pos = 0 with the if-clause. if pos is more than 0, last will be something else then null
                    lastItem.setNext(currentItem.getNext());
                } else{
                    lastItem = currentItem;
                    currentItem = currentItem.getNext();
                }
            }
        }
    }

    @Override
    public T get(int pos) {
        HANLinkedListItem<T> listItem = FirstItem;
        for(int i = 0; i <= pos; i++) {
            if (i == pos) {
                return listItem.getValue();
            } else{
                listItem = listItem.getNext();
                if(listItem == null){
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void removeFirst() {
        FirstItem = FirstItem.getNext();
    }

    @Override
    public T getFirst() {
        return FirstItem.getValue();
    }

    @Override
    public int getSize() {
        HANLinkedListItem<T> listItem = FirstItem;
        int counter = 0;
        while(listItem != null){
            counter++;
            if(listItem.getNext() != null) {
                listItem = listItem.getNext();
            } else{
                return counter;
            }
        }
        return counter;
    }
}
