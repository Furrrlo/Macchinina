package gov.ismonnet.commons.utils;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Builder class used to build {@link ThreadFactory}s
 *
 * @author Ferlo
 */
public class ThreadFactoryBuilder {

    /**
     * Thread factory actually making threads.
     * If nothing is specified, {@link Executors#defaultThreadFactory()} is used.
     */
    private ThreadFactory backingThreadFactory;
    /**
     * Function used to name threads.
     * The integer parameter indicates the number of threads this factory produced.
     * If null, it's up to the {@link #backingThreadFactory}
     *
     * @see Thread#setName(String)
     */
    private Function<Integer, String> nameFormat;
    /**
     * Indicates whether the generated threads should be a daemon.
     * If null, it's up to the {@link #backingThreadFactory}
     *
     * @see Thread#setDaemon(boolean)
     */
    private Boolean daemon;
    /**
     * Indicates the generated threads priority.
     * If null, it's up to the {@link #backingThreadFactory}
     *
     * @see Thread#setPriority(int)
     */
    private Integer priority;
    /**
     * Handler invoked when one of the generated threads abruptly terminates.
     * If null, it's up to the {@link #backingThreadFactory}
     *
     * @see Thread#setUncaughtExceptionHandler(UncaughtExceptionHandler) (int)
     */
    private UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
     * Set the thread factory actually making threads
     *
     * If null, {@link Executors#defaultThreadFactory()} is used.
     *
     * @param backingThreadFactory the thread factory making threads
     * @return this builder
     */
    public ThreadFactoryBuilder setBackingThreadFactory(ThreadFactory backingThreadFactory) {
        this.backingThreadFactory = backingThreadFactory;
        return this;
    }

    /**
     * Set the function used to name threads
     *
     * The integer parameter indicates the number of threads this factory produced.
     * If null, it's up to the {@link #backingThreadFactory}
     *
     * @param nameFormat function used to name threads
     * @return this builder
     *
     * @see Thread#setName(String)
     */
    public ThreadFactoryBuilder setNameFormat(Function<Integer, String> nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

    /**
     * Set whether the generated threads should be daemon
     *
     * If null, it's up to the {@link #backingThreadFactory}
     *
     * @param daemon true if the generated threads should be daemon.
     * @return this builder
     *
     * @see Thread#setDaemon(boolean)
     */
    public ThreadFactoryBuilder setDaemon(Boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    /**
     * Set the generated threads priority
     *
     * If null, it's up to the {@link #backingThreadFactory}
     *
     * @param priority generated threads priority
     * @return this builder
     *
     * @see Thread#setPriority(int)
     */
    public ThreadFactoryBuilder setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Set the handler invoked when one of the generated threads abruptly terminates
     *
     * If null, it's up to the {@link #backingThreadFactory}
     *
     * @param uncaughtExceptionHandler handler invoked when one of the generated threads abruptly terminates
     * @return this builder
     *
     * @see Thread#setUncaughtExceptionHandler(UncaughtExceptionHandler)
     */
    public ThreadFactoryBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }

    /**
     * Builds the thread factory from this builder
     *
     * @return the thread factory
     */
    public ThreadFactory build() {

        final ThreadFactory btf = 
            backingThreadFactory == null ? Executors.defaultThreadFactory() : backingThreadFactory;

        return new Factory(
                btf,
                nameFormat,
                daemon,
                priority,
                uncaughtExceptionHandler
        );
    }

    /**
     * Actual thread factory that is built
     */
    private static final class Factory implements ThreadFactory {

        /**
         * Thread factory actually making threads
         */
        private final ThreadFactory backingThreadFactory;

        /**
         * Function used to name threads.
         * The integer parameter indicates the number of threads this factory produced.
         * If null, it's up to the {@link #backingThreadFactory}
         *
         * @see Thread#setName(String)
         */
        private final Function<Integer, String> nameFormat;
        /**
         * Number of threads generated by this factory
         */
        private final AtomicInteger count;

        /**
         * Indicates whether the generated threads should be a daemon.
         * If null, it's up to the {@link #backingThreadFactory}
         *
         * @see Thread#setDaemon(boolean)
         */
        private final Boolean daemon;
        /**
         * Indicates the generated threads priority.
         * If null, it's up to the {@link #backingThreadFactory}
         *
         * @see Thread#setPriority(int)
         */
        private final Integer priority;
        /**
         * Handler invoked when one of the generated threads abruptly terminates.
         * If null, it's up to the {@link #backingThreadFactory}
         *
         * @see Thread#setUncaughtExceptionHandler(UncaughtExceptionHandler) (int)
         */
        private final UncaughtExceptionHandler uncaughtExceptionHandler;

        /**
         * Constructs a new factory with the given parameters
         *
         * @param backingThreadFactory thread factory actually making threads
         * @param nameFormat function used to name threads, or null to use the backingThreadFactory name
         * @param daemon true if the generated threads should be daemon, or null to let the backingThreadFactory choose
         * @param priority the generated threads priority, or null to let the backingThreadFactory choose
         * @param uncaughtExceptionHandler handler invoked when one of the generated threads abruptly terminates,
         *                                 or null to use the backingThreadFactory one
         */
        Factory(ThreadFactory backingThreadFactory,
                Function<Integer, String> nameFormat,
                Boolean daemon,
                Integer priority,
                UncaughtExceptionHandler uncaughtExceptionHandler) {

            this.backingThreadFactory = backingThreadFactory;

            this.nameFormat = nameFormat;
            this.count = nameFormat != null ? new AtomicInteger(1) : null;

            this.daemon = daemon;
            this.priority = priority;

            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        }

        @Override
        public Thread newThread(Runnable r) {

            final Thread th = backingThreadFactory.newThread(r);

            if(nameFormat != null)
                th.setName(nameFormat.apply(count.getAndIncrement()));
            if(daemon != null)
                th.setDaemon(daemon);
            if(priority != null)
                th.setPriority(priority);
            if(uncaughtExceptionHandler != null)
                th.setUncaughtExceptionHandler(uncaughtExceptionHandler);

            return th;
        }
    }
}
