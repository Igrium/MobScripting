package com.igrium.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * A perceptible collection that wraps a base collection.
 */
public class PerceptibleCollection<T, C extends Collection<T>> implements Collection<T> {

    protected final C base;

    protected final Consumer<T> addListener;
    protected final Consumer<Object> removeListener;

    public PerceptibleCollection(C base, Consumer<T> addListener, Consumer<Object> removeListener) {
        this.base = base;
        this.addListener = addListener;
        this.removeListener = removeListener;
    }

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
        return new ForwardingPerceptibleIterator(base.iterator());
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
        boolean success = base.add(e);
        if (success) onAdded(e);
        return success;
    }

    @Override
    public boolean remove(Object o) {
        boolean success = base.remove(o);
        if (success) onRemoved(o);
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
            onAdded(item);
        }
        return success;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean success = base.removeAll(c);
        for (Object item : c) {
            onRemoved(item);
        }
        return success;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        LinkedList<T> items = new LinkedList<>();
        for (T item : this) {
            if (!c.contains(item)) {
                items.add(item);
            }
        }
        boolean success = base.retainAll(c);
        for (T item : items) {
            onRemoved(item);
        }
        return success;
    }

    @Override
    public void clear() {
        for (T item : this) {
            onRemoved(item);
        }
        base.clear();
    }

    protected void onAdded(T item) {
        addListener.accept(item);
    }

    protected void onRemoved(Object item) {
        removeListener.accept(item);
    }
    
    protected class ForwardingPerceptibleIterator implements Iterator<T> {

        protected final Iterator<T> base;

        private T prevItem;

        public ForwardingPerceptibleIterator(Iterator<T> base) {
            this.base = base;
        }

        @Override
        public boolean hasNext() {
            return base.hasNext();
        }

        @Override
        public T next() {
            return prevItem = base.next();
        }

        @Override
        public void remove() {
            base.remove();
            onRemoved(prevItem);
        }
        
        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            base.forEachRemaining(action);
        }
    }
}
