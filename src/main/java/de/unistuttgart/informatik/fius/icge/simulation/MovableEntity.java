/*
* This source file is part of the FIUS ICGE project.
* For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.ArrayDeque;
import java.util.Deque;

import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.event.EventHandler;
import de.unistuttgart.informatik.fius.icge.event.EventListener;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.SimulationEvent;
import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionMethod;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

/**
 * Base class for movable entities
 */
public abstract class MovableEntity extends Entity {

    private final Deque<MoveEvent> _positionStack = new ArrayDeque<>(100);
    private final EventHandler _eventHandler = new EventHandler();

    /**
     * Creates a new movable entity in the given simulation
     * 
     * @param sim
     *            The simulation to create the movable entity in.
     */
    protected MovableEntity(Simulation sim) {
        super(sim);

        this._eventHandler.addListener(SpawnEvent.class, ev -> {
            SpawnEvent se = (SpawnEvent) ev;
            if ((se.simulation == this.simulation()) && (se.entity == this)) {
                this._positionStack.add(new MoveEvent(this.simulation(), this, se.row, se.column));
            }
            return true;
        });
        this._eventHandler.addListener(MoveEvent.class, ev -> {
            MoveEvent me = (MoveEvent) ev;
            if ((me.simulation == this.simulation()) && (me.entity == this)) {
                this._positionStack.add(me);
            }
            return true;
        });
    }

    @Override
    public final float getZ() {
        return 100;
    }

    /**
     * Move this entity one field in the current direction
     * 
     * @throws IllegalMove
     *             the field is occupied by a solid entity
     * @throws EntityNotAlive
     *             the entity is not spawned or already despawned
     */
    public void move() throws IllegalMove, EntityNotAlive {
        this.delayed(() -> {
            WorldObject wobAfter = this.wobAfterMove();
            SimulationEvent ev = new MoveEvent(this.simulation(), this, wobAfter);
            this.simulation().setWorldObject(this, wobAfter, ev);
        });
    }

    /**
     * Check if the entity can move one field in the current direction
     * 
     * @return true iff the entity can move
     */
    @InspectionMethod
    public boolean canMove() {
        try {
            this.wobAfterMove();
            return true;
        } catch (IllegalMove | EntityNotAlive e) {
            return false;
        }
    }

    /**
     * Try to move this entity one field in the current direction
     * 
     * @return true iff the entity has moved
     */
    @InspectionMethod
    public boolean tryMove() {
        try {
            this.move();
            return true;
        } catch (IllegalMove | EntityNotAlive e) {
            return false;
        }
    }

    /**
     * Turn this entity 90° counterclockwise (meaning 1 step to this entity's left)
     * 
     * @throws EntityNotAlive
     *             the entity is not spawned or already despawned
     */
    @InspectionMethod
    public void turnLeft() throws EntityNotAlive {
        SimulationEvent ev = new TurnLeftEvent(this.simulation(), this);
        this.delayed(() -> {
            this.simulation().setWorldObject(this, this.wobAfterTurnLeft(), ev);
        });
    }

    /**
     * Get the first position of this entity as a MoveEvent
     * 
     * The first position is the entity's spawn position
     * 
     * @return The move event for the position
     */
    public MoveEvent firstPosition() {
        return this._positionStack.peekFirst();
    }

    /**
     * Get the last known position of this entity as a MoveEvent
     * 
     * @return The move event for the position
     */
    public MoveEvent lastPosition() {
        return this._positionStack.peekLast();
    }

    /**
     * Get the whole position history of this entity as an Iterable for use in a for loop
     * 
     * The iterable starts with this.firstPosition() and ends with this.lastPosition
     * 
     * @return A iterable of move events
     */
    public Iterable<MoveEvent> positionHistory() {
        return () -> this._positionStack.iterator();
    }

    // private

    /**
     * Create a new world object with the calculated coordinates of one field in
     * front of this entity
     * 
     * @return The world object that would be when this entity would move now
     * @throws IllegalMove
     *             When the move is illegal
     * @throws EntityNotAlive
     *             When the entity is not alive
     */
    private WorldObject wobAfterMove() throws IllegalMove, EntityNotAlive {
        WorldObject wob = this.worldObject();
        int column = wob.column;
        int row = wob.row;
        switch (wob.direction) {
            case NORTH:
                --row;
            break;
            case EAST:
                ++column;
            break;
            case SOUTH:
                ++row;
            break;
            case WEST:
                --column;
            break;
        }
        int newCol = column;
        int newRow = row;
        if (this.simulation().territory().containsWith(
                wall -> (wall.column == newCol) && (wall.row == newRow) && wall.state.isSolid()))
            throw new IllegalMove();
        return new WorldObject(wob.state, column, row, 100, wob.direction);
    }

    /**
     * Create a new WorldObject with the direction after a left turn
     * 
     * @return The world object that would be when this entity would turn left now
     * @throws EntityNotAlive
     *             When the entity is not alive
     */
    private WorldObject wobAfterTurnLeft() throws EntityNotAlive {
        WorldObject wob = this.worldObject();
        Direction dir = wob.direction;
        switch (dir) {
            case NORTH:
                dir = Direction.WEST;
            break;
            case EAST:
                dir = Direction.NORTH;
            break;
            case SOUTH:
                dir = Direction.EAST;
            break;
            case WEST:
                dir = Direction.SOUTH;
            break;
            default:
                dir = Direction.EAST;
        }
        return new WorldObject(wob.state, wob.column, wob.row, 100, dir);
    }

    // Exceptions:

    /**
     * A exception for when a move is illegal
     */
    public static class IllegalMove extends RuntimeException {
        private static final long serialVersionUID = 6992550372582751611L;
    }

    // Events

    /**
     * Base class for events from movable entities
     */
    public static abstract class MovableEntityEvent extends EntityEvent {
        /**
         * Creates a new movable entity event in the given simulation for the given entity
         * 
         * @param sim
         *            The simulation to create the event in
         * @param entity
         *            The simulation to create the event for
         */
        MovableEntityEvent(Simulation sim, MovableEntity entity) {
            super(sim, entity);
        }
    }

    /**
     * Move event recording the target coordinates of the movable entity
     */
    public static class MoveEvent extends MovableEntityEvent {

        /** The column the entity moved to. */
        public final int column;
        /** The row the entity moved to. */
        public final int row;

        /**
         * Creates a new move event in the given simulation for the given entity signaling the move to the given point.
         * 
         * @param sim
         *            The simulation to create the event in.
         * @param entity
         *            The entity to create the event for.
         * @param row
         *            The row the entity moved to.
         * @param column
         *            The column the entity moved to.
         */
        MoveEvent(Simulation sim, MovableEntity entity, int column, int row) {
            super(sim, entity);
            this.row = row;
            this.column = column;
        }

        /**
         * Creates a new move event in the given simulation for the given entity signaling the move to the point
         * described in the given world object.
         * 
         * @param sim
         *            The simulation to create the event in.
         * @param entity
         *            The entity to create the event for.
         * @param wob
         *            The world object to get the location after the move from
         * 
         */
        MoveEvent(Simulation sim, MovableEntity entity, WorldObject wob) {
            super(sim, entity);
            this.row = wob.row;
            this.column = wob.column;
        }
    }

    /**
     * Event for left turns of an entity
     */
    public static class TurnLeftEvent extends MovableEntityEvent {
        /**
         * Creates a new turn left event in the given simulation for the given entity
         * 
         * @param sim
         *            The simulation to create the event in
         * @param entity
         *            The simulation to create the event for
         */
        TurnLeftEvent(Simulation sim, MovableEntity entity) {
            super(sim, entity);
        }
    }
}
