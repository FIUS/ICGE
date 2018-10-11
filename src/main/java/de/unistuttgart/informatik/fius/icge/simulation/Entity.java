/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.concurrent.Semaphore;
import java.util.function.Predicate;

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

    public static Predicate<Entity> predicateIsAt(int column, int row) {
        return ent -> WorldObject.predicateIsAt(column, row).test(ent.worldObject());
    }

    /**
     * Create a new entity in the given simulation
     * 
     * @param sim
     *            The simulation to create the entity in
     */
    protected Entity(Simulation sim) {
        this._sim = sim;
        this._delayTicks = this.getStandardDelayTicks();
        // add entity to entities list of simulation
        this._sim.addEntity(this);
    }

    /**
     * @return The EntityState object that manages this entity's state
     */
    public abstract EntityState state();

    /**
     * @return The Simulation object this entity is a part of
     */
    public final Simulation simulation() {
        return this._sim;
    }

    /**
     * @return The current WorldObject of this entity
     * @throws EntityNotAlive
     *             only spwaned entities have a world object
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
     * Get the row at which this entity is currently
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
     * @return The tick the entity waits for for its last move to complete
     */
    public int getMoveEndTick() {
        return this._blockedUntilTick;
    }

    /**
     * Set the row at which this entity is currently
     * 
     * @param row
     *            the row to set
     */
    @InspectionAttribute
    private void setRow(int row) {
        WorldObject wobOld = this.worldObject();
        WorldObject wobNew = new WorldObject(wobOld.state, wobOld.column, row, wobOld.z, wobOld.direction);
        this.simulation().setWorldObject(this, wobNew, new TeleportEvent(this._sim, this, wobNew));
    }

    /**
     * Get the column at which this entity is currently
     * 
     * @return the column
     */
    @InspectionAttribute
    public int getColumn() {
        return this.worldObject().column;
    }

    /**
     * Set the column at which this entity is currently
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
     * <p>
     * Entities with higher z are drawn above entities with lower z
     * 
     * @return The z value of this entity.
     */
    protected float getZ() {
        return 0;
    }

    /**
     * Get the direction in which this entity is currently facing
     * 
     * @return the direction
     */
    @InspectionAttribute
    public Direction getDirection() {
        return this.worldObject().direction;
    }

    /**
     * @return The standard delay ticks for actions of this entity
     */
    protected int getStandardDelayTicks() {
        return 25;
    }

    /**
     * Spawn this entity at the given coordinates facing east
     * 
     * @param column
     *            The column to spawn the entity at
     * @param row
     *            The row to spawn the entity at
     * @throws EntityAlreadyAlive
     *             the entity is already spawned
     * @throws CellBlockedBySolidEntity
     *             the spawn position is blocked by a solid entity
     */
    public void spawn(int column, int row) throws EntityAlreadyAlive, CellBlockedBySolidEntity {
        this.spawn(column, row, Direction.EAST);
    }

    /**
     * Spawn this entity at the given coordinates with the given direction
     * 
     * @param column
     *            The column to spawn the entity at
     * @param row
     *            The row to spawn the entity at
     * @param direction
     *            The direction to spawn the entity in
     * @throws EntityAlreadyAlive
     *             the entity is already spawned
     * @throws CellBlockedBySolidEntity
     *             the spawn position is blocked by a solid entity
     */
    public void spawn(int column, int row, Direction direction) throws EntityAlreadyAlive, CellBlockedBySolidEntity {
        this.spawnInternal(column, row, direction, false);
    }

    /**
     * The spawn is performed without a delay, and even if the simulation is paused
     * 
     * @param column
     *            The column to spawn the entity at
     * @param row
     *            The row to spawn the entity at
     * @throws EntityAlreadyAlive
     *             When the entity is already alive
     */
    public void forceSpawn(int column, int row) throws EntityAlreadyAlive {
        this.forceSpawn(column, row, Direction.EAST);
    }

    /**
     * The spawn is performed without a delay, and even if the simulation is paused
     * 
     * @param column
     *            The column to spawn the entity at
     * @param row
     *            The row to spawn the entity at
     * @param direction
     *            The direction to spawn the entity in
     * @throws EntityAlreadyAlive
     *             When the entity is already alive
     */
    public void forceSpawn(int column, int row, Direction direction) {
        this.spawnInternal(column, row, direction, true);
    }

    /**
     * Despawn this entity
     * 
     * @throws EntityNotAlive
     *             the entity is not spawned
     */
    @InspectionMethod
    public final void despawn() throws EntityNotAlive {
        this.despawnInternal(false);
    }

    /**
     * The despawn is performed without a delay, and even if the simulation is paused
     * 
     * @throws EntityNotAlive
     *             the entity is not spawned
     */
    public final void forceDespawn() throws EntityNotAlive {
        this.despawnInternal(true);
    }

    /**
     * set the delay in ticks for all actions performed by this entity in the future
     * <p>
     * The delay controls how many ticks the entity is in a blocked/busy state
     * after performing an action
     * 
     * @param delay
     *            The amount of ticks
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
    public void println(String message) {
        this.print(message + "\n");
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void println(boolean message) {
        this.println(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void println(byte message) {
        this.println(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void println(short message) {
        this.println(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void println(int message) {
        this.println(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void println(long message) {
        this.println(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void println(float message) {
        this.println(String.valueOf(message));
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *            The message to print
     */
    public void println(double message) {
        this.println(String.valueOf(message));
    }

    // protected

    /**
     * Internal spawn logic for entities
     * 
     ** @param column
     *            The column to spawn the entity at
     * @param row
     *            The row to spawn the entity at
     * @param direction
     *            The direction to spawn the entity in
     * @param force
     *            if true perform spawn with delay set to 0
     * @throws EntityAlreadyAlive
     *             When the entity is already alive
     * @throws CellBlockedBySolidEntity
     *             When the cell the entity is trying to be spawned in is blocked by a solid entity like a wall
     */
    protected void spawnInternal(int column, int row, Direction direction, boolean force)
            throws EntityAlreadyAlive, CellBlockedBySolidEntity {
        for (Entity e : this.simulation().entitiesAt(column, row)) {
            if (e.state().isSolid()) throw new CellBlockedBySolidEntity();
        }

        WorldObject wob = new WorldObject(this.state(), column, row, this.getZ(), direction);
        SimulationEvent ev = new SpawnEvent(this.simulation(), this, wob);
        this.delayed(() -> {
            if (this.alive()) throw new EntityAlreadyAlive();
            this.simulation().setWorldObject(this, wob, ev);
        }, force ? 0 : this._delayTicks);
    }

    /**
     * Internal despawn logic for entities
     * 
     * @param force
     *            if true perform despawn with delay set to 0
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
     * If (this.simulation().tickCount() {@literal >}= this._blockedUntilTick)
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
     * If (this.simulation().tickCount() {@literal >}= this._blockedUntilTick)
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

    /**
     * Exception for when a entity is not alive / not spawned
     */
    public static class EntityNotAlive extends RuntimeException {
        private static final long serialVersionUID = -21686971924440414L;
    }

    /**
     * Exception for when a cell is already blocked by a solid entity
     */
    public static class CellBlockedBySolidEntity extends RuntimeException {
        private static final long serialVersionUID = -7878416133186725145L;
    }

    /**
     * Exception for when a entity is already alive / spawned
     */
    public static class EntityAlreadyAlive extends RuntimeException {
        private static final long serialVersionUID = -1865306897160094130L;
    }

    // Events:

    /**
     * Base class for all entity related events
     */
    public abstract static class EntityEvent extends SimulationEvent {

        /** The entity that caused this event. */
        public final Entity entity;

        /**
         * Creates a new entity event in the given simulation for the given entity
         * 
         * @param sim
         *            The simulation to create the event in.
         * @param entity
         *            The entity to create the event for.
         */
        EntityEvent(Simulation sim, Entity entity) {
            super(sim);
            this.entity = entity;
        }
    }

    /**
     * Event containing a message from an entity
     */
    public static class MessageEvent extends EntityEvent {

        /**
         * The message contained in this event
         */
        public final String message;

        /**
         * Creates a new message event in the given simulation for the given entity containing the given message
         * 
         * @param sim
         *            The simulation to create the event in.
         * @param entity
         *            The entity to create the event for.
         * @param message
         *            The message to be contained in the event
         */
        MessageEvent(Simulation sim, Entity entity, String message) {
            super(sim, entity);
            this.message = message;
        }

    }

    /**
     * Spawn event recording spawn coordinates of the entity
     */
    public static class SpawnEvent extends EntityEvent {

        /** The column the entity was spawned at. */
        public final int column;
        /** The row the entity was spawned at. */
        public final int row;

        /**
         * Creates a new spawn event in the given simulation for the given entity signaling the spawn at the given point.
         * 
         * @param sim
         *            The simulation to create the event in.
         * @param entity
         *            The entity to create the event for.
         * @param column
         *            The column the entity spawned in
         * @param row
         *            The row the entity spawned in
         */
        SpawnEvent(Simulation sim, Entity entity, int column, int row) {
            super(sim, entity);
            this.row = row;
            this.column = column;
        }

        /**
         * Creates a new spawn event in the given simulation for the given entity signaling the spawn at the point described in
         * the given world object.
         * 
         * @param sim
         *            The simulation to create the event in.
         * @param entity
         *            The entity to create the event for.
         * @param wob
         *            The world object to get the spawn location from
         * 
         */
        SpawnEvent(Simulation sim, Entity entity, WorldObject wob) {
            super(sim, entity);
            this.column = wob.column;
            this.row = wob.row;
        }
    }

    /**
     * Teleport event recording target coordinates of this teleport
     */
    public static class TeleportEvent extends EntityEvent {
        /** The column the entity was teleported to. */
        public final int column;
        /** The row the entity was teleported to. */
        public final int row;

        /**
         * Creates a new teleport event in the given simulation for the given entity signaling the teleport to the given point.
         * 
         * @param sim
         *            The simulation to create the event in.
         * @param entity
         *            The entity to create the event for.
         * @param row
         *            The row the entity was teleported to.
         * @param column
         *            The column the entity was teleported to.
         */
        TeleportEvent(Simulation sim, Entity entity, int column, int row) {
            super(sim, entity);
            this.column = column;
            this.row = row;
        }

        /**
         * Creates a new teleport event in the given simulation for the given entity signaling the teleport to the point
         * described in the given world object.
         * 
         * @param sim
         *            The simulation to create the event in.
         * @param entity
         *            The entity to create the event for.
         * @param wob
         *            The world object to get the teleport location from
         * 
         */
        TeleportEvent(Simulation sim, Entity entity, WorldObject wob) {
            super(sim, entity);
            this.column = wob.column;
            this.row = wob.row;
        }
    }

    /**
     * Despawn event
     */
    public static class DespawnEvent extends EntityEvent {
        /**
         * Creates a new despawn event in the given simulation for the given entity
         * 
         * @param sim
         *            The simulation to create the event in.
         * @param entity
         *            The entity to create the event for.
         */
        public DespawnEvent(Simulation sim, Entity entity) {
            super(sim, entity);
        }
    }
}
