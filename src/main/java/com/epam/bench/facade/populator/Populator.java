package com.epam.bench.facade.populator;

public interface Populator<S, T> {
    void populate(S source, T target);
}
