package com.igrium.collections;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Consumer;

public class PerceptibleList<T> extends PerceptibleCollection<T, List<T>> implements List<T> {

    public PerceptibleList(List<T> base, Consumer<T> addListener, Consumer<Object> removeListener) {
        super(base, addListener, removeListener);
    }
    
    @Override
    public T set(int index, T element) {
        T prev = base.set(index, element);
        if (!Objects.equals(element, prev)) {
            onRemoved(prev);
            onAdded(element);
        }
        return prev;
    }

    @Override
    public int indexOf(Object o) {
        return base.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return base.lastIndexOf(o);
    }

    @Override
    public void add(int index, T element) {
        base.add(index, element);
        onAdded(element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean success = base.addAll(index, c);
        for (T item : c) {
            onAdded(item);
        }
        return success;
    }

    @Override
    public T get(int index) {
        return base.get(index);
    }

    @Override
    public T remove(int index) {
        T prev = base.remove(index);
        onRemoved(prev);
        return prev;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new PerceptibleListIterator(base.listIterator());
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new PerceptibleListIterator(base.listIterator(index));
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new PerceptibleList<>(base.subList(fromIndex, toIndex), this.addListener, this.removeListener);
    }

    protected class PerceptibleListIterator implements ListIterator<T> {

        protected final ListIterator<T> base;
        
        public PerceptibleListIterator(ListIterator<T> base) {
            this.base = base;
        }

        private T currentItem;

        @Override
        public boolean hasNext() {
            return base.hasNext();
        }

        @Override
        public T next() {
            return currentItem = base.next();
        }

        @Override
        public boolean hasPrevious() {
            return base.hasPrevious();
        }

        @Override
        public T previous() {
            return currentItem = base.previous();
        }

        @Override
        public int nextIndex() {
            return base.nextIndex();
        }

        @Override
        public int previousIndex() {
            return base.previousIndex();
        }

        @Override
        public void remove() {
            base.remove();
            onRemoved(currentItem);
        }

        @Override
        public void set(T e) {
            base.set(e);
            if (!Objects.equals(currentItem, e)) {
                onRemoved(currentItem);
                onAdded(e);
            }
        }

        @Override
        public void add(T e) {
            base.add(e);
            onAdded(e);
        }
        
    }
}
