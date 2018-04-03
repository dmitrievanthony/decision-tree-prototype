package com.dmitrievanthony.tree.core.distributed.criteria;

public interface ImpurityMeasure<T extends ImpurityMeasure<T>> extends Comparable<T> {

    public double impurity();

    public T add(T measure);

    public T subtract(T measure);

    default public int compareTo(T o) {
        return Double.compare(impurity(), o.impurity());
    }
}
