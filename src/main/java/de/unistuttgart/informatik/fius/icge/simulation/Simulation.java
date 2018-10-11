/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;

import de.unistuttgart.informatik.fius.icge.event.Event;
import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.territory.Territory;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import sun.util.locale.StringTokenIterator;

/**
 * Simulation managing Entities and Territory.
 */
public class Simulation {

    private final HashMap<Entity, WorldObject> _entityObjects = new HashMap<>();
    private Territory _tty;
    private boolean _running = false;
    private int _tickCount = 0;
    private TimerTask _timerTask;
    private Semaphore _timerTaskSem;
    private int _delay = 10;

    /**
     * Creates a new `Simumlation` from a `Territory`
     *
     * @param tty
     *            The `Territory` representing the initial state of the created `Simulation`
     */
    public Simulation(Territory tty) {
        this.init(tty);
    }

    /**
     * Creates a new `Simulation` from the state of an existing `Simulation`. It does initially have the same `Territory`, tick
     * count and is running if the other `Simulation` is running.
     * 
     * NOTE: All previously existing `Entity` objects will still be associated with the other `Simulation` and no `Entity`
     * objects are shared between the two `Simulation`s
     *
     * @param sim
     *            The `Simulation` to obtain the state from
     */
    public Simulation(Simulation sim) {
        synchronized (sim) {
            this.init(sim._tty);
            this._running = sim._running;
            this._tickCount = sim._tickCount;
            if (this._running) {
                this.startTimer();
            }
        }
    }

    /**
     * Pauses this `Simulation` if it is running
     *
     * @return true iff this `Simulation` was running before the method call
     */
    public synchronized boolean pause() {
        try {
            return this._running;
        } finally {
            this.stopTimer();
            this._running = false;
            EventDispatcher.raise(new PauseEvent(this));
        }
    }

    /**
     * Resumes this `Simulation` if it isn't running
     *
     * @return true iff the `Simulation` was not running before the method call
     */
    public synchronized boolean resume() {
        try {
            return !this._running;
        } finally {
            this._running = true;
            EventDispatcher.raise(new ResumeEvent(this));
            this.startTimer();
        }

    }

    /**
     * @return true iff this `Simulation` is currently running
     */
    public boolean running() {
        return this._running;
    }

    /**
     * Set the simulation territory
     *
     * @param tty
     *            the new territory
     */
    public void setTerritory(Territory tty) {
        SimulationEvent ev = new SetTerritoryEvent(this);
        synchronized (this) {
            this._entityObjects.values().removeIf(wob -> !tty.contains(wob));
            tty.forEach(wob -> {
                if (!this._entityObjects.containsValue(wob)) {
                    this._entityObjects.put(wob.state.createEntity(this), wob);
                }
            });
            this._tty = tty;
            EventDispatcher.raise(ev);
        }
    }

    /**
     * @return All alive `Entity`s within this `Simulation`
     */
    public synchronized ArrayList<Entity> entities() {
        return new ArrayList<>(this._entityObjects.keySet());
    }

    /**
     * Gets all alive `Entity`s within this `Simulation` that match a certain predicate
     * 
     * @param pred
     *            The predicate that the `Entity`s are tested for
     * @return The matching `Entity`s
     */
    public synchronized ArrayList<Entity> entitiesWith(Predicate<Entity> pred) {
        ArrayList<Entity> result = new ArrayList<>();
        this._entityObjects.keySet().forEach(ent -> {
            if (pred.test(ent)) {
                result.add(ent);
            }
        });
        return result;
    }

    /**
     * Gets all alive `Entity`s within this `Simulation` that are in a certain cell
     * 
     * @param column
     *            The column of the cell
     * @param row
     *            The row of the cell
     * @return The `Entity`s that are in the specified cell
     */
    public ArrayList<Entity> entitiesAt(int column, int row) {
        return this.entitiesWith(Entity.predicateIsAt(column, row));
    }

    /**
     * @return All alive `CollectableEntity`s within this `Simulation`
     */
    public ArrayList<CollectableEntity> collectables() {
        return this.collectablesWith(ent -> true);
    }

    /**
     * Gets all alive `CollectableEntity`s within this `Simulation` that match a certain predicate
     * 
     * @param pred
     *            The predicate that the `CollectableEntity`s are tested for
     * @return The matching `CollectableEntity`s
     */
    public synchronized ArrayList<CollectableEntity> collectablesWith(Predicate<CollectableEntity> pred) {
        ArrayList<CollectableEntity> result = new ArrayList<>();
        this._entityObjects.keySet().stream().filter(ent -> ent instanceof CollectableEntity)
                .map(ent -> (CollectableEntity) ent).filter(pred).forEach(result::add);
        return result;
    }

    /**
     * Gets all alive `CollectableEntity`s within this `Simulation` that are in a certain cell
     * 
     * @param column
     *            The column of the cell
     * @param row
     *            The row of the cell
     * @return The `CollectableEntity`s that are in the specified cell
     */
    public synchronized ArrayList<CollectableEntity> collectablesAt(int column, int row) {
        ArrayList<CollectableEntity> result = new ArrayList<>();
        this._entityObjects.keySet().stream().filter(ent -> ent instanceof CollectableEntity)
                .filter(Entity.predicateIsAt(column, row)).map(ent -> (CollectableEntity) ent).forEach(result::add);
        return result;
    }

    /**
     * Checks if this `Simulation` contains at least on alive `Entity` that matches a certain predicate
     * 
     * @param pred
     *            The predicate that the `Entity`s are tested for
     * 
     * @return true iff at least one alive `Entity` matches the specified predicate
     */
    public synchronized boolean containsWith(Predicate<Entity> pred) {
        return this._entityObjects.keySet().stream().filter(pred).findFirst().isPresent();
    }

    /**
     * Checks if this `Simulation` contains at least one alive `Entity` that is in a certain cell
     * 
     * @param column
     *            The column of the cell
     * @param row
     *            The row of the cell
     * @return true iff at least one alive `Entity` is in the specified cell
     */
    public boolean containsAt(int column, int row) {
        return this.containsWith(Entity.predicateIsAt(column, row));
    }

    /**
     * Checks if this `Simulation` contains at least on alive `CollectableEntity` that matches a certain predicate
     * 
     * @param pred
     *            The predicate that the `CollectableEntity`s are tested for
     * 
     * @return true iff at least one alive `CollectableEntity` matches the specified predicate
     */
    public synchronized boolean containsCollectableWith(Predicate<CollectableEntity> pred) {
        return this._entityObjects.keySet().stream().filter(ent -> ent instanceof CollectableEntity)
                .map(ent -> (CollectableEntity) ent).filter(pred).findFirst().isPresent();
    }

    /**
     * Checks if this `Simulation` contains at least one alive `CollectableEntity` that is in a certain cell
     * 
     * @param column
     *            The column of the cell
     * @param row
     *            The row of the cell
     * @return true iff at least one alive `CollectableEntity` is in the specified cell
     */
    public synchronized boolean containsCollectableAt(int column, int row) {
        return this._entityObjects.keySet().stream().filter(ent -> ent instanceof CollectableEntity)
                .filter(Entity.predicateIsAt(column, row)).findFirst().isPresent();
    }

    /**
     * Gets the `Territory` that represents the current state of this `Simulation`.
     * 
     * NOTE: When this `Simulation` progresses or has its state changed by other means, the returned `Territory` is obsolete
     * since `Territory` objects are immutable.
     * 
     * @return The current `Territory`
     */
    public Territory territory() {
        return this._tty;
    }

    /**
     * @param ent
     *            An `Entity` that is alive within this `Simulation`
     * @return The `WorldObject` that represents the current state of the specified `Entity`
     */
    public WorldObject worldObject(Entity ent) {
        return this._entityObjects.get(ent);
    }

    /**
     * Sets the `WorldObject` that is currently representing the state of an `Entity`.
     * 
     * NOTE: `Entity`s without associated `WorldObject` are considered dead, as opposed to alive `Entity`s which have an
     * associated `WorldObject`.
     * 
     * @param ent
     *            An `Entity` that is associated with this `Simulation` but not necessarily alive
     * @param newWob
     *            The `WorldObject` that will from now represent the specified `Entity`s state. `null` is allowed and has the
     *            effect that the specified `Entity` has no associated `WorldObject`.
     * @param ev
     *            An event that will be synchronously raised at the end of this method call
     */
    public synchronized void setWorldObject(Entity ent, WorldObject newWob, SimulationEvent ev) {
        if (newWob == null) {
            WorldObject oldWob = this._entityObjects.remove(ent);
            this._tty = oldWob == null ? this._tty : this._tty.remove(oldWob);
        } else {
            WorldObject oldWob = this._entityObjects.put(ent, newWob);
            this._tty = oldWob == null ? this._tty.add(newWob) : this._tty.replace(oldWob, newWob);
        }
        EventDispatcher.raise(ev);
    }

    /**
     * @return The number of ticks that have happened in this `Simulation` since its creation
     */
    public int tickCount() {
        return this._tickCount;
    }

    // private

    private void init(Territory tty) {
        this.setTerritory(tty);
        EventDispatcher.raise(new InitEvent(this));
    }

    private void startTimerWithDelay(int delay) {
        this._timerTaskSem = new Semaphore(1);
        this._timerTask = new TimerTask() {
            private final Semaphore sem = Simulation.this._timerTaskSem;

            @Override
            public void run() {
                if (!this.sem.tryAcquire()) {
                    this.cancel();
                }
                Simulation.this.tick(this.sem::release);
            }
        };
        new Timer().schedule(this._timerTask, delay, delay);
    }

    private void startTimer() {startTimerWithDelay(this._delay);}

    public void changeTimer(int delay) {
        this.stopTimer();
        this.startTimerWithDelay(delay);
    }

    private void stopTimer() {
        if (this._timerTaskSem != null) {
            this._timerTaskSem.acquireUninterruptibly();
        }
        this._timerTaskSem = null;
        this._timerTask = null;
    }

    private synchronized void tick(Runnable afterwards) {
        ++this._tickCount;
        EventDispatcher.raise(new TickEvent(this, this._tickCount), afterwards);
    }

    // Events

    /**
     * A simulation event
     */
    public static abstract class SimulationEvent implements Event {
        /** The simulation, this event is for. */
        public final Simulation simulation;

        /**
         * Creates a new simulation event for the given simulation
         * 
         * @param sim
         *            The simulation this event is for
         */
        SimulationEvent(Simulation sim) {
            this.simulation = sim;
        }
    }

    /**
     * An event for when the simulation is initialized
     */
    public static class InitEvent extends SimulationEvent {
        /**
         * Creates a new init event for the given simulation
         * 
         * @param sim
         *            The simulation this event is for
         */
        InitEvent(Simulation sim) {
            super(sim);
        }
    }

    /**
     * An event for when the simulation is paused
     */
    public static class PauseEvent extends SimulationEvent {
        /**
         * Creates a new pause event for the given simulation
         * 
         * @param sim
         *            The simulation this event is for
         */
        PauseEvent(Simulation sim) {
            super(sim);
        }
    }

    /**
     * An event for when the simulation is resumed
     */
    public static class ResumeEvent extends SimulationEvent {
        /**
         * Creates a new resume event for the given simulation
         * 
         * @param sim
         *            The simulation this event is for
         */
        ResumeEvent(Simulation sim) {
            super(sim);
        }
    }

    /**
     * An event for when the simulation ticks
     */
    public static class TickEvent extends SimulationEvent {
        /** The current tick count at the point of this event. */
        public final int tickCount;

        /**
         * Creates a new tick event for the given simulation with the given tick count
         * 
         * @param sim
         *            The simulation this event is for
         * @param tickCount
         *            The current tick count at the point of this event.
         */
        TickEvent(Simulation sim, int tickCount) {
            super(sim);
            this.tickCount = tickCount;
        }
    }

    /**
     * An event for when a new territory is set in a simulation
     */
    public static class SetTerritoryEvent extends SimulationEvent {
        /**
         * Creates a new set territory event for the given simulation
         * 
         * @param sim
         *            The simulation this event is for
         */
        SetTerritoryEvent(Simulation sim) {
            super(sim);
        }
    }

    public void setDelay(int delay) {
        this._delay = delay;
        if (_running)
            changeTimer(delay);
    }
}
