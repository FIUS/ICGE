/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.concurrent.Semaphore;

import javax.print.event.PrintEvent;

import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.SimulationEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.TickEvent;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Sprite;

public abstract class Entity {
    
    private final Simulation _sim;
    private final Sprite _sprite;
    private int _delayTicks = 25;
    private int _lastMoveTick = Integer.MIN_VALUE;
    
    protected Entity(Simulation sim, Sprite sprite) {
        this._sim = sim;
        this._sprite = sprite;
        this._delayTicks = this.getStandardDelayTicks();
    }
    
    public final Simulation simulation() {
        return this._sim;
    }
    
    public final Sprite sprite() {
        return this._sprite;
    }
    
    public final WorldObject worldObject() throws EntityNotAlive {
        WorldObject result = this.simulation().worldObject(this);
        if (result == null) throw new EntityNotAlive();
        return result;
    }
    
    public final boolean alive() {
        return this.simulation().entities().contains(this);
    }
    
    protected float getZ() {
        return 0;
    }

    protected int getStandardDelayTicks() {
        return 25;
    }
    
    public void spawn(int column, int row) throws EntityAlreadyAlive, CellBlockedByWall {
        this.spawn(column, row, Direction.EAST);
    }
    
    public void spawn(int column, int row, Direction direction) throws EntityAlreadyAlive, CellBlockedByWall {
        this.spawnInternal(column, row, direction);
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
        this.setDelay(0);
        this.spawnInternal(column, row, direction);
        this.resetDelay();
    }

    public final void despawn() throws EntityNotAlive {
        despawnInternal(false);
    }

    /**
     * The despawn is performed without a delay, and even if the simulation is paused
     */
    public final void forceDespawn() throws EntityNotAlive {
        this.setDelay(0);
        despawnInternal(true);
        this.resetDelay();
    }
    
    /** delay is in simulation ticks */
    public void setDelay(int delay) {
        this._delayTicks = delay;
    }

    /** reset delay to standard */
    public void resetDelay() {
        this._delayTicks = this.getStandardDelayTicks();
    }

    /**
     * Generates a Message Event that gets written to the log Panel.
     * 
     * @param message
     *      The message to print
     */
    public void print(String message) {
        SimulationEvent ev = new MessageEvent(this.simulation(), this, message);
        this.delayed(() -> {
            if (!this.alive()) throw new EntityNotAlive();
            EventDispatcher.raise(ev);
        });
    }

    public void print(boolean message) {
        this.print(String.valueOf(message));
    }

    public void print(byte message) {
        this.print(String.valueOf(message));
    }

    public void print(short message) {
        this.print(String.valueOf(message));
    }

    public void print(int message) {
        this.print(String.valueOf(message));
    }

    public void print(long message) {
        this.print(String.valueOf(message));
    }

    public void print(float message) {
        this.print(String.valueOf(message));
    }

    public void print(double message) {
        this.print(String.valueOf(message));
    }


    /**
     * Generates a Message Event that gets written to the log Panel.
     * Adds a newline Character to the end of the message.
     * 
     * @param message
     *      The message to print
     */
    public void printLn(String message) {
        this.print(message + "\n");
    }

    public void printLn(boolean message) {
        this.printLn(String.valueOf(message));
    }

    public void printLn(byte message) {
        this.printLn(String.valueOf(message));
    }

    public void printLn(short message) {
        this.printLn(String.valueOf(message));
    }

    public void printLn(int message) {
        this.printLn(String.valueOf(message));
    }

    public void printLn(long message) {
        this.printLn(String.valueOf(message));
    }

    public void printLn(float message) {
        this.printLn(String.valueOf(message));
    }

    public void printLn(double message) {
        this.printLn(String.valueOf(message));
    }

    // protected

    protected void spawnInternal(int column, int row, Direction direction) throws EntityAlreadyAlive, CellBlockedByWall {
        for(Entity e : this.simulation().entitiesWith(row, column)) {
            if(e instanceof Wall) throw new CellBlockedByWall();
        }
        
        WorldObject wob = new WorldObject(this.sprite(), column, row, getZ(), direction);
        SimulationEvent ev = new SpawnEvent(this.simulation(), this, wob);
        this.delayed(() -> {
            if (this.alive()) throw new EntityAlreadyAlive();
            this.simulation().setWorldObject(this, wob, ev);
        });
    }

    protected void despawnInternal(boolean force) {
        SimulationEvent ev = new DespawnEvent(this.simulation(), this);
        this.delayed(() -> {
            if (!this.alive()) throw new EntityNotAlive();
            this.simulation().setWorldObject(this, null, ev);
        });
    }

    /**
     * Runs the given runnable after the delay period or immediately
     * 
     * @param fn
     *            The runnable
     */
    protected synchronized void delayed(Runnable fn) {
        this.delayed(fn, this._delayTicks);
    }

    /**
     * Runs the given runnable after the delay period or immediately
     * 
     * @param fn
     *            The runnable
     * @param delay
     *            Delay in Ticks after which fn is run. Use 0 or negative numbers to instantly run fn
     */
    protected synchronized void delayed(Runnable fn, int delay) {
        if (delay > 0) {
            int thisMoveTick = this._lastMoveTick + delay;
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
            fn.run();
            this._lastMoveTick = this.simulation().tickCount();
        }
    }
    
    // Exceptions:
    
    public static class EntityNotAlive extends RuntimeException {
        private static final long serialVersionUID = -21686971924440414L;
    }
    
    public static class CellBlockedByWall extends RuntimeException {
        private static final long serialVersionUID = -7878416133186725145L;
    }
    
    
    public static class EntityAlreadyAlive extends RuntimeException {
        private static final long serialVersionUID = -1865306897160094130L;
    }
    
    // Events:
    
    public static abstract class EntityEvent extends SimulationEvent {
        
        public final Entity entity;
        
        EntityEvent(Simulation sim, Entity entity) {
            super(sim);
            this.entity = entity;
        }
    }
    
    public static class MessageEvent extends EntityEvent {
        
        public final String message;
        
        MessageEvent(Simulation sim, Entity entity, String message) {
            super(sim, entity);
            this.message = message;
        }

    }

    
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
    
    public static class DespawnEvent extends EntityEvent {
        public DespawnEvent(Simulation sim, Entity entity) {
            super(sim, entity);
        }
    }
}
