/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.event;

import java.util.ArrayList;

public class EventDispatcher {
    
    private static ArrayList<Listening> _listenings = new ArrayList<>();
    private static ArrayList<EventListener> _toRemove = new ArrayList<>();
    
    public static synchronized EventListener addListener(Class<?> listensFor, EventListener listener) {
        if (!Event.class.isAssignableFrom(listensFor)) throw new IllegalArgumentException();
        _listenings.add(new Listening(listensFor, listener));
        return listener;
    }
    
    public static synchronized boolean removeListener(EventListener listener) {
        if (_listenings.stream().filter(entry -> entry.listener == listener).findFirst().isPresent()) {
            _toRemove.add(listener);
            return true;
        } else
            return false;
    }
    
    public static synchronized void raise(Event e) {
        for (EventListener listener : _toRemove) {
            _listenings.removeIf(entry -> entry.listener == listener);
        }
        _toRemove.clear();
        for (Listening entry : _listenings) {
            if (entry.listensFor.isAssignableFrom(e.getClass())) {
                if (!entry.listener.handle(e)) {
                    removeListener(entry.listener);
                }
            }
        }
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
