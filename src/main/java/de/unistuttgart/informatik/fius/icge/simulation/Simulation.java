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
                    this._entityObjects.put(wob.type.createEntity(this), wob);
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
     * @param row
     * @param column
     * @return all entities in given cell
     */
    public ArrayList<Entity> entitiesWith(int row, int column) {
        return this.entitiesWith(ent -> {
            WorldObject wob = this.worldObject(ent);
            return (wob.row == row) && (wob.column == column);
        });
    }

    /**
     * @param row
     * @param column
     * @return all collectable entities in given cell
     */
    public synchronized ArrayList<CollectableEntity> collectablesWith(int row, int column) {
        ArrayList<CollectableEntity> result = new ArrayList<>();
        this._entityObjects.keySet().forEach(ent -> {
            WorldObject wob = this.worldObject(ent);
            if ((ent instanceof CollectableEntity) && (wob.row == row) && (wob.column == column)) {
                result.add((CollectableEntity) ent);
            }
        });
        return result;
    }

    /**
     * @param ent
     *            entity to test
     * @return true if entity is part of this simulation
     */
    public boolean contains(Entity ent) {
        return this._entityObjects.containsKey(ent);
    }

    /**
     * @param pred
     *            predicate to test entities
     * @return true if at least one entity matches the given predicate
     */
    public synchronized boolean containsWith(Predicate<Entity> pred) {
        return this._entityObjects.keySet().stream().filter(pred).findFirst().isPresent();
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
                Simulation.this.tick();
                this.sem.release();
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

    private synchronized void tick() {
        ++this._tickCount;
        EventDispatcher.raise(new TickEvent(this, this._tickCount));
    }

    // Events

    public static abstract class SimulationEvent implements Event {

        public final Simulation simulation;

        SimulationEvent(Simulation sim) {
            this.simulation = sim;
        }
    }

    public static class InitEvent extends SimulationEvent {
        InitEvent(Simulation sim) {
            super(sim);
        }
    }

    public static class PauseEvent extends SimulationEvent {
        PauseEvent(Simulation sim) {
            super(sim);
        }
    }

    public static class ResumeEvent extends SimulationEvent {
        ResumeEvent(Simulation sim) {
            super(sim);
        }
    }

    public static class TickEvent extends SimulationEvent {

        public final int tickCount;

        TickEvent(Simulation sim, int tickCount) {
            super(sim);
            this.tickCount = tickCount;
        }
    }

    public static class SetTerritoryEvent extends SimulationEvent {
        SetTerritoryEvent(Simulation sim) {
            super(sim);
        }
    }
}
