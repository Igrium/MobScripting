package com.igrium.mobscripting.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A list that calls a function when elements are added or removed.
 */
public abstract class PerceptibleList<T> extends AbstractList<T> {

    private final List<T> base;

    public PerceptibleList(List<T> base) {
        this.base = base;
    }

    /**
     * Called after an item is added to the list.
     * @param item The item to be added.
     */
    protected abstract void onAdd(T item);

    /**
     * Called after an item is removed from the list.
     * @param item Item to be removed.
     * @apiNote It's not guaranteed that the item is actually in the list.
     */
    protected abstract void onRemove(Object item);

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return base.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new PerceptibleListIterator(base.listIterator());
    }

    @Override
    public Object[] toArray() {
        return base.toArray();
    }

    @Override
    public <H> H[] toArray(H[] a) {
        return base.toArray(a);
    }

    @Override
    public boolean add(T e) {
        base.add(e);
        onAdd(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        boolean success = base.remove(o);
        if (success) onRemove(o);
        return success;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return base.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean success = base.addAll(c);
        for (T item : c) {
            onAdd(item);
        }
        return success;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean success = base.addAll(index, c);
        for (T item : c) {
            onAdd(item);
        }
        return success;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean success = base.removeAll(c);
        for (Object item : c) {
            onRemove(item);
        }
        return success;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO: implementation based on base.retainAll
        Iterator<T> iterator = this.iterator();
        boolean changed = false;
        while (iterator.hasNext()) {
            T item = iterator.next();

            if (!c.contains(item)) {
                iterator.remove();
                onRemove(item);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        for (T item : this) {
            onRemove(item);
        }
        base.clear();
    }

    @Override
    public T get(int index) {
        return base.get(index);
    }

    @Override
    public T set(int index, T element) {
        // TODO: Is it possible to run onSet & onRemove based on an equality check while
        // still placing them before the list modification?
        T prev = base.set(index, element);
        if (!Objects.equals(element, prev)) {
            onRemove(prev);
            onAdd(element);
        }
        return prev;
    }

    @Override
    public void add(int index, T element) {
        base.add(index, element);
        onAdd(element);

    }

    @Override
    public T remove(int index) {
        T val = base.remove(index);
        onRemove(val);
        return val;
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
    public ListIterator<T> listIterator() {
        return new PerceptibleListIterator(base.listIterator());
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new PerceptibleListIterator(base.listIterator(index));
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'subList'");
    }

    private class PerceptibleListIterator implements ListIterator<T> {

        final ListIterator<T> base;
        T prevVal;
        
        PerceptibleListIterator(ListIterator<T> base) {
            this.base = base;
        }

        @Override
        public boolean hasNext() {
            return base.hasNext();
        }

        @Override
        public T next() {
            return (prevVal = base.next());
        }

        @Override
        public boolean hasPrevious() {
            return base.hasPrevious();
        }

        @Override
        public T previous() {
            return (prevVal = base.previous());
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
            onRemove(prevVal);
        }

        @Override
        public void set(T e) {
            base.set(e);
            if (!Objects.equals(e, prevVal)) {
                onRemove(prevVal);
                onAdd(e);
            }
        }

        @Override
        public void add(T e) {
            base.add(e);
            onAdd(e);
            prevVal = e;
        }
        
        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            base.forEachRemaining(action);
        }
    }
}
