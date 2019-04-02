package de.unistuttgart.informatik.fius.icge.event;

import java.util.ArrayList;

public class EventHandler {
    private final ArrayList<Listening> _listenings = new ArrayList<>();

    public EventHandler() {
        EventDispatcher.registerHandler(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        EventDispatcher.deregisterHandler(this);
    }

    public EventListener addListener(Class<? extends Event> listensFor, EventListener listener) {
        _listenings.add(new Listening(listensFor, listener));
        return listener;
    }

    public boolean removeListener(EventListener listener) {
        return _listenings.removeIf(entry -> entry.listener == listener);
    }

    void raise(Event e) {
        for (Listening entry : new ArrayList<>(_listenings)) { // new list cause it might get modified concurrently
            if (entry.listensFor.isAssignableFrom(e.getClass())) {
                // we found an event listener that listens for this event
                if (!entry.listener.handle(e)) {
                    removeListener(entry.listener);
                }
            }
        }
    }

    private static class Listening {
        public Listening(Class<? extends Event> listensFor, EventListener listener) {
            this.listensFor = listensFor;
            this.listener = listener;
        }

        public final Class<?> listensFor;
        public final EventListener listener;
    }

}
