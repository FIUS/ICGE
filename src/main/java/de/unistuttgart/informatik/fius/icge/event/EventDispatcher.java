/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.event;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * `EventDispatcher` is a class with only static methods where `EventHandler`s can be registered. If an `EventHandler` is
 * registered, every `Event` that is raised via the `EventDispatcher.raise()` method is forwarded to the `handle()` method of
 * the registered `EventHandler`. There can be arbitrarily many `EventHandler`s registered at the same time. The order in which
 * raised `Event`s are forwarded to them is unspecified.
 */
public class EventDispatcher {

    private static ArrayList<WeakReference<EventHandler>> _handlerRefs = new ArrayList<>();
    private static ArrayDeque<Runnable> _afterwards = new ArrayDeque<>();
    private static int _raiseRecursionDepth = 0;

    /**
     * Registers an `EventHandler` to the `EventDispatcher`. For details, see the JavaDoc of `EventDispatcher`
     * 
     * @param handler
     *            The `EventHandler` to register
     * @return true if the specified `EventHandler` wasn't already registered, false if it was already registered
     */
    public static synchronized boolean registerHandler(EventHandler handler) {
        for (WeakReference<EventHandler> ref : _handlerRefs) {
            if (ref.get() == handler) return false;
        }
        _handlerRefs.add(new WeakReference<>(handler));
        return true;
    }

    /**
     * Deregisters an `EventHandler` to the `EventDispatcher`. For details, see to JavaDoc of `EventDispatcher`.
     * 
     * @param handler
     *            The `EventHandler` to deregister
     * @return true if the specified `EventHandler` was registered, false if it wasn't
     */
    public static synchronized boolean deregisterHandler(EventHandler handler) {
        return _handlerRefs.removeIf(ref -> {
            EventHandler candidate = ref.get();
            return candidate == null || candidate == handler;
        });
    }

    /**
     * Raises an event and afterwards runs the tasks that have been scheduled via the `afterwards()` method. This method mustn't
     * be called while another event is handled in the same thread.
     * 
     * NOTE: If you want to raise an event (or perform some action that would raise an event) during the handling of an event,
     * consider to call `EventDispatcher.afterwards()`.
     * 
     * @param e
     *            The event to raise
     */
    public static synchronized void raise(Event e) throws RaiseAlreadyActive {
        EventDispatcher.raise(e, () -> {});
    }

    /**
     * Raises an event, then runs an `afterTask` and finally runs the tasks that have been scheduled via the `afterwards()`
     * method. This method mustn't be called while another event is handled in the same thread.
     * 
     * NOTE: If you want to raise an event (or perform some action that would raise an event) during the handling of an event,
     * consider to call `EventDispatcher.afterwards()`.
     * 
     * @param e
     *            The event to raise
     * @param afterTask
     *            The task to run after the handling of that event
     * @throws RaiseAlreadyActive
     *             if another event is currently handled in the same thread
     */
    public static synchronized void raise(Event e, Runnable afterTask) throws RaiseAlreadyActive {
        if (EventDispatcher._raiseRecursionDepth != 0) {
            throw new RaiseAlreadyActive(); // recursive raise is not supported for now
        }

        // actual event hadling
        ++EventDispatcher._raiseRecursionDepth;
        try {
            for (WeakReference<EventHandler> ref : new ArrayList<>(_handlerRefs)) {
                EventHandler handler = ref.get();
                if (handler == null) {
                    _handlerRefs.remove(ref);
                } else {
                    handler.raise(e);
                }
            }
        } finally {
            --EventDispatcher._raiseRecursionDepth;
        }

        // First run the passed `afterTask` and then the tasks that have been scheduled via `EventDispatcher.afterwards()`
        afterTask.run();
        while (!_afterwards.isEmpty()) {
            _afterwards.pop().run();
        }
    }

    /**
     * Schedules a `Runnable` that is run synchronously after the handling of the currently handled event. This method must be
     * called during the handling of an event (i.e. during a call of `EventHandler.raise()`) and must be called from the thread
     * in which that event is raised (and handled). If multiple `Runnable`s are scheduled this way, they are run in FIFO order
     * (first in, first out).
     * 
     * @param rn
     *            The runnable to schedule
     */
    public static synchronized void afterwards(Runnable rn) {
        if (EventDispatcher._raiseRecursionDepth == 0) {
            throw new RaiseNotActive(); // must have an active raise to schedule via `afterwards()`
        }

        _afterwards.add(rn);
    }

    // Exceptions

    /**
     * Exception that is thrown if an event is raised during the handling of another event in the same thread.
     */
    public static class RaiseAlreadyActive extends RuntimeException {
        private static final long serialVersionUID = 7713141366627046771L;
    }

    /**
     * Exception that is thrown if `EventDispatcher().afterwards()` is called while no event is handled in that thread.
     */
    public static class RaiseNotActive extends RuntimeException {
        private static final long serialVersionUID = 2178481422042432110L;
    }

}
