/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.concurrent.Semaphore;

import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.SimulationEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.TickEvent;
import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionAttribute;
import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionMethod;
import de.unistuttgart.informatik.fius.icge.territory.EntityState;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

/**
 * Base class for all entities
 */
public abstract class Entity {

    private final Simulation _sim;
    private int _delayTicks = 25;
    private int _blockedUntilTick = Integer.MIN_VALUE;

    /**
     * Create a new entity in the given simulation
     * 
     * @param sim
     */
    protected Entity(Simulation sim) {
        this._sim = sim;
        this._delayTicks = this.getStandardDelayTicks();
    }

    /**
     * Get the EntityState object that manages this entity's state
     * 
     * @return
     */
    public abstract EntityState state();

    /**
     * Get the Simulation object this entity is a part of
     * 
     * @return
     */
    public final Simulation simulation() {
        return this._sim;
    }

    /**
     * Get the current WorldObject of this entity
     * 
     * @return
     * @throws EntityNotAlive only spwaned entities have a world object
     */
    public final WorldObject worldObject() throws EntityNotAlive {
        WorldObject result = this.simulation().worldObject(this);
        if (result == null) throw new EntityNotAlive();
        return result;
    }

    /**
     * Get the alive state of this entity
     * 
     * @return true iff the entity is spawned in its simulation
     */
    public final boolean alive() {
        return this.simulation().entities().contains(this);
    }

    /**
     * Get the row of this entity.
     * 
     * @return the row
     */
    @InspectionAttribute
    public int getRow() {
        return this.worldObject().row;
    }

    @Override
    public String toString() {
        return "<Entity " + this.getClass().getSimpleName() + ">";
    }

    /**
     * Get the tick the entity waits for for its last move to complete
     *  
     * @return
     */
    public int getMoveEndTick() {
        return this._blockedUntilTick;
    }

    /**
     * Set the row of this entity.
     * 
     * @param row
     *            the row
     */
    @InspectionAttribute
    private void setRow(int row) {
        WorldObject wobOld = this.worldObject();
        WorldObject wobNew = new WorldObject(wobOld.state, wobOld.column, row, wobOld.z, wobOld.direction);
        this.simulation().setWorldObject(this, wobNew, new TeleportEvent(this._sim, this, wobNew));
    }

    /**
     * Get the column of this entity.
     * 
     * @return the column
     */
    @InspectionAttribute
    public int getColumn() {
        return this.worldObject().column;
    }

    /**
     * Set the column of this entity.
     * 
     * @param column
     *            the column
     */
    @InspectionAttribute
    private void setColumn(int column) {
        WorldObject wobOld = this.worldObject();
        WorldObject wobNew = new WorldObject(wobOld.state, column, wobOld.row, wobOld.z, wobOld.direction);
        this.simulation().setWorldObject(this, wobNew, new TeleportEvent(this._sim, this, wobNew));
    }

    /**
     * Get the z value of this entity used to determine the drawing order of 
     * entities in the same cell.
     * 
     * Entities with higher z are drawn above entities with lower z
     *   
     * @return
     */
    protected float getZ() {
        return 0;
    }

    /**
     * Get the standard delay ticks for actions of this entity
     * 
     * @return
     */
    protected int getStandardDelayTicks() {
        return 25;
    }

    /**
     * Spawn this entity at the given coordinates facing east
     * 
     * @param column
     * @param row
     * @throws EntityAlreadyAlive the entity is already spawned
     * @throws CellBlockedBySolidEntity the spawn position is blocked by a solid entity
     */
    public void spawn(int column, int row) throws EntityAlreadyAlive, CellBlockedBySolidEntity {
        this.spawn(column, row, Direction.EAST);
    }

    /**
     * Spawn this entity at the given coordinates with the given direction
     * 
     * @param column
     * @param row
     * @param direction
     * @throws EntityAlreadyAlive the entity is already spawned
     * @throws CellBlockedBySolidEntity the spawn position is blocked by a solid entity
     */
    public void spawn(int column, int row, Direction direction) throws EntityAlreadyAlive, CellBlockedBySolidEntity {
        this.spawnInternal(column, row, direction, false);
    }

    /**
     * The spawn is performed without a delay, and even if the simulation is paused
     */
    public void forceSpawn(int column, int row) throws EntityAlreadyAlive {
        this.forceSpawn(column, row, Direction.EAST);
    }

    /**
     * The spawn is performed without a delay, and even if the simulation is paused
     */
    public void forceSpawn(int column, int row, Direction direction) {
        this.spawnInternal(column, row, direction, true);
    }

    /**
     * Despawn this entity
     * 
     * @throws EntityNotAlive the entity is not spawned
     */
    @InspectionMethod
    public final void despawn() throws EntityNotAlive {
        this.despawnInternal(false);
    }

    /**
     * The despawn is performed without a delay, and even if the simulation is paused
     */
    public final void forceDespawn() throws EntityNotAlive {
        despawnInternal(true);
    }

    /**
     * set the delay in ticks for all actions performed by this entity in the future
     * 
     * The delay controls how many ticks the entity is in a blocked/busy state 
     * after performing an action
     * 
     * @param delay
     */
    public void setDelay(int delay) {
        this._delayTicks = delay;
    }

    /** 
     * reset delay to standard standard amount of ticks 
     * 
     * The delay controls how many ticks the entity is in a blocked/busy state 
     * after performing an action
     */
    public void resetDelay() {
        this._delayTicks = this.getStandardDelayTicks();
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * 
     * @param message
     *            The message to print
     */
    public void print(String message) {
        SimulationEvent ev = new MessageEvent(this.simulation(), this, message);
        this.delayed(() -> {
            if (!this.alive()) throw new EntityNotAlive();
            EventDispatcher.raise(ev);
        });
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * 
     * @param message
     *            The message to print
     */
    public void print(boolean message) {
        this.print(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * 
     * @param message
     *            The message to print
     */
    public void print(byte message) {
        this.print(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * 
     * @param message
     *            The message to print
     */
    public void print(short message) {
        this.print(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * 
     * @param message
     *            The message to print
     */
    public void print(int message) {
        this.print(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * 
     * @param message
     *            The message to print
     */
    public void print(long message) {
        this.print(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * 
     * @param message
     *            The message to print
     */
    public void print(float message) {
        this.print(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * 
     * @param message
     *            The message to print
     */
    public void print(double message) {
        this.print(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    @InspectionMethod
    public void printLn(String message) {
        this.print(message + "\n");
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void printLn(boolean message) {
        this.printLn(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void printLn(byte message) {
        this.printLn(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void printLn(short message) {
        this.printLn(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void printLn(int message) {
        this.printLn(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void printLn(long message) {
        this.printLn(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void printLn(float message) {
        this.printLn(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void printLn(double message) {
        this.printLn(String.valueOf(message));
    }

    // protected

    /**
     * Internal spawn logic for entities
     * 
     * @param column
     * @param row
     * @param direction
     * @param force if true perform spawn with delay set to 0
     * @throws EntityAlreadyAlive
     * @throws CellBlockedBySolidEntity
     */
    protected void spawnInternal(int column, int row, Direction direction, boolean force)
            throws EntityAlreadyAlive, CellBlockedBySolidEntity {
        for (Entity e : this.simulation().entitiesWith(row, column)) {
            if (e.state().isSolid()) throw new CellBlockedBySolidEntity();
        }

        WorldObject wob = new WorldObject(this.state(), column, row, getZ(), direction);
        SimulationEvent ev = new SpawnEvent(this.simulation(), this, wob);
        this.delayed(() -> {
            if (this.alive()) throw new EntityAlreadyAlive();
            this.simulation().setWorldObject(this, wob, ev);
        }, force ? 0 : this._delayTicks);
    }

    /**
     * Internal despawn logic for entities
     * 
     * @param force if true perform despawn with delay set to 0
     */
    protected void despawnInternal(boolean force) {
        SimulationEvent ev = new DespawnEvent(this.simulation(), this);
        this.delayed(() -> {
            if (!this.alive()) throw new EntityNotAlive();
            this.simulation().setWorldObject(this, null, ev);
        }, force ? 0 : this._delayTicks);
    }

    /**
     * Runs the given runnable after this entity is free
     * 
     * If (this.simulation().tickCount() >= this._blockedUntilTick)
     * the runnable is executed immediately
     * 
     * @param fn
     *            The runnable
     */
    protected synchronized void delayed(Runnable fn) {
        this.delayed(fn, this._delayTicks);
    }

    /**
     * Runs the given runnable after this entity is free
     * 
     * If (this.simulation().tickCount() >= this._blockedUntilTick)
     * the runnable is executed immediately
     * 
     * @param fn
     *            The runnable
     * @param delay
     *            Delay in Ticks after which the entity is considered busy after executing the runnable.
     *            Use 0 or negative numbers to instantly run fn regardless of busy state.
     */
    protected synchronized void delayed(Runnable fn, int delay) {
        if (delay > 0) {
            int thisMoveTick = this._blockedUntilTick;
            if (this.simulation().tickCount() < thisMoveTick) {
                Semaphore sem = new Semaphore(0);
                EventDispatcher.addListener(TickEvent.class, ev -> {
                    TickEvent te = (TickEvent) ev;
                    if ((te.simulation == this.simulation()) && (te.tickCount >= thisMoveTick)) {
                        sem.release();
                        return false;
                    }
                    return true;
                });
                sem.acquireUninterruptibly();
            }
        }
        synchronized (this.simulation()) {
            this._blockedUntilTick = this.simulation().tickCount() + delay;
            fn.run();
        }
    }

    // Exceptions:

    public static class EntityNotAlive extends RuntimeException {
        private static final long serialVersionUID = -21686971924440414L;
    }

    public static class CellBlockedBySolidEntity extends RuntimeException {
        private static final long serialVersionUID = -7878416133186725145L;
    }

    public static class EntityAlreadyAlive extends RuntimeException {
        private static final long serialVersionUID = -1865306897160094130L;
    }

    // Events:

    /**
     * Base class for all entity related events
     */
    public static abstract class EntityEvent extends SimulationEvent {

        public final Entity entity;

        EntityEvent(Simulation sim, Entity entity) {
            super(sim);
            this.entity = entity;
        }
    }

    /**
     * Event containing a message from an entity
     */
    public static class MessageEvent extends EntityEvent {

        public final String message;

        MessageEvent(Simulation sim, Entity entity, String message) {
            super(sim, entity);
            this.message = message;
        }

    }

    /**
     * Spawn event recording spawn coordinates of the entity
     */
    public static class SpawnEvent extends EntityEvent {

        public final int row;
        public final int column;

        SpawnEvent(Simulation sim, Entity entity, int row, int column) {
            super(sim, entity);
            this.row = row;
            this.column = column;
        }

        SpawnEvent(Simulation sim, Entity entity, WorldObject wob) {
            super(sim, entity);
            this.row = wob.row;
            this.column = wob.column;
        }
    }

    /**
     * Teleport event recording target coordinates of this teleport
     */
    public static class TeleportEvent extends EntityEvent {
        public final int row;
        public final int column;

        TeleportEvent(Simulation sim, Entity entity, int row, int column) {
            super(sim, entity);
            this.row = row;
            this.column = column;
        }

        TeleportEvent(Simulation sim, Entity entity, WorldObject wob) {
            super(sim, entity);
            this.row = wob.row;
            this.column = wob.column;
        }
    }

    /**
     * Despawn event
     */
    public static class DespawnEvent extends EntityEvent {
        public DespawnEvent(Simulation sim, Entity entity) {
            super(sim, entity);
        }
    }
}
