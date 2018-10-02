/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.event;

import java.util.Deque;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class EventDispatcher {
    
    private static Deque<Listening> _listenings = new ConcurrentLinkedDeque<>();
    private static Executor _eventExecutor = new ThreadPoolExecutor(3, 5, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    
    public static synchronized EventListener addListener(Class<?> listensFor, EventListener listener) {
        if (!Event.class.isAssignableFrom(listensFor)) throw new IllegalArgumentException();
        _listenings.add(new Listening(listensFor, listener));
        return listener;
    }
    
    public static synchronized boolean removeListener(EventListener listener) {
        return _listenings.remove(listener);
    }
    
    public static synchronized void raise(Event e) {
        Runnable fn = () -> {
            Set<EventListener> toRemove = new HashSet();
            for (Listening entry : EventDispatcher._listenings) {
                if (entry.listensFor.isAssignableFrom(e.getClass())) {
                    if (!entry.listener.handle(e)) {
                        toRemove.add(entry.listener);
                    }
                }
            }
            EventDispatcher._listenings.removeIf(entry -> toRemove.contains(entry.listener));
        };
        _eventExecutor.execute(fn);
    }
    
    private static class Listening {
        public Listening(Class<?> listensFor, EventListener listener) {
            this.listensFor = listensFor;
            this.listener = listener;
        }
        
        public final Class<?> listensFor;
        public final EventListener listener;
    }
}
