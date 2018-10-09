/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;

import de.unistuttgart.informatik.fius.icge.event.Event;
import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.territory.Territory;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;

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

    /**
     * Create simulation from territory.
     *
     * @param tty
     *            Territory
     */
    public Simulation(Territory tty) {
        this.init(tty);
    }

    /**
     * Create new simulation from previous simulation state.
     *
     * @param sim
     *            previous simulation state
     */
    public Simulation(Simulation sim) {
        synchronized (sim) {
            this._running = sim._running;
            this.init(sim._tty);
        }
    }

    /**
     * Pause simulation.
     *
     * @return true if simulation was running before
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
     * Resume a paused simulation.
     *
     * @return true if simulation was paused before
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
     * @return true if simulation is running
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
     * @return all entities within the simulation
     */
    public synchronized ArrayList<Entity> entities() {
        return new ArrayList<>(this._entityObjects.keySet());
    }

    /**
     * @param pred
     *            test function to filter entities
     * @return all entities within the simulation with the given predicate
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
     * Get all entities in the given row and column
     * 
     * @param column
     *            The column the entity must be in
     * @param row
     *            The row the entity must be in
     * @return all entities in given cell
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
     * @return the current territory
     */
    public Territory territory() {
        return this._tty;
    }

    /**
     * @param ent
     *            an entity of this simulation
     * @return the associated WorldObject
     */
    public WorldObject worldObject(Entity ent) {
        return this._entityObjects.get(ent);
    }

    /**
     * @param ent
     *            the source entity for the WorldObject
     * @param newWob
     *            the new WorldObject (use null to remove an old WorldObject)
     * @param ev
     *            the simulation event to raise
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
     * @return current tick count of simulation
     */
    public int tickCount() {
        return this._tickCount;
    }

    // private

    private void init(Territory tty) {
        this.setTerritory(tty);
        EventDispatcher.raise(new InitEvent(this));
    }

    private void startTimer() {
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
        new Timer().schedule(this._timerTask, 10, 10);
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
     * A event for when the simulation is initialized
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
     * A event for when the simulation is paused
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
     * A event for when the simulation is resumed
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
     * A event for when the simulation ticks
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
     * A event for when a new territory is set in a simulation
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
}
