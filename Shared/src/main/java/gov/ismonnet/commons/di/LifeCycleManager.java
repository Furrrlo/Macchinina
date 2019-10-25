package gov.ismonnet.commons.di;

import gov.ismonnet.commons.utils.SneakyThrow;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@Singleton
public class LifeCycleManager {

    private final List<LifeCycle> objects;
    private final AtomicBoolean started;

    @Inject LifeCycleManager() {
        objects = new CopyOnWriteArrayList<>();
        started = new AtomicBoolean(false);
    }

    public void register(Object obj) {
        if(started.get())
            throw new AssertionError("LifeCycle already started");

        if(obj instanceof LifeCycle)
            objects.add((LifeCycle) obj);
    }

    public void start() {
        if(started.getAndSet(true))
            throw new AssertionError("LifeCycle already started");
        objects.forEach(o -> SneakyThrow.runUnchecked(o::start));
    }

    public void stop() {
        IntStream.range(0, objects.size())
                .map(i -> (objects.size() - 1 - i))
                .mapToObj(objects::get)
                .forEach(o -> SneakyThrow.runUnchecked(o::stop));
    }
}