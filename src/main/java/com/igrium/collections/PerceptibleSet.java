package com.igrium.collections;

import java.util.Set;
import java.util.function.Consumer;

public class PerceptibleSet<T> extends PerceptibleCollection<T, Set<T>> implements Set<T> {

    public PerceptibleSet(Set<T> base, Consumer<T> addListener, Consumer<Object> removeListener) {
        super(base, addListener, removeListener);
    }
    
}
