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
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.SimulationEvent;
import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionMethod;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

/**
 * Base class for movable entities
 */
public abstract class MovableEntity extends Entity {

    private final Deque<MoveEvent> _positionStack = new ArrayDeque<>(100);

    protected MovableEntity(Simulation sim) {
        super(sim);

        EventDispatcher.addListener(SpawnEvent.class, ev -> {
            SpawnEvent se = (SpawnEvent) ev;
            if ((se.simulation == this.simulation()) && (se.entity == this)) {
                this._positionStack.add(new MoveEvent(this.simulation(), this, se.row, se.column));
            }
            return true;
        });
        EventDispatcher.addListener(MoveEvent.class, ev -> {
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
     * @throws IllegalMove the field is occupied by a solid entity
     * @throws EntityNotAlive the entity is not spawned or already despawned
     */
    @InspectionMethod
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
     * @throws EntityNotAlive the entity is not spawned or already despawned 
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
     * @return
     */
    public MoveEvent firstPosition() {
        return this._positionStack.peekFirst();
    }


    /**
     * Get the last known position of this entity as a MoveEvent
     * 
     * @return
     */
    public MoveEvent lastPosition() {
        return this._positionStack.peekLast();
    }

    /**
     * Get the whole position history of this entity as an Iterable for use in a for loop
     * 
     * The iterable starts with this.firstPosition() and ends with this.lastPosition
     *  
     * @return
     */
    public Iterable<MoveEvent> positionHistory() {
        return () -> this._positionStack.iterator();
    }

    // private

    /**
     * Create a new worldobject with the calculated coordinates of one field in 
     * front of this entity
     *  
     * @return
     * @throws IllegalMove
     * @throws EntityNotAlive
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
        if (this.simulation().territory()
                .containsWith(wall -> (wall.column == newCol) && (wall.row == newRow) && wall.state.isSolid()))
            throw new IllegalMove();
        return new WorldObject(wob.state, column, row, 100, wob.direction);
    }

    /**
     * Create a new WorldObject with the direction after a left turn
     * 
     * @return
     * @throws EntityNotAlive
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

    public static class IllegalMove extends RuntimeException {
        private static final long serialVersionUID = 6992550372582751611L;
    }

    // Events

    /**
     * Base class for events from movable entities
     */
    public static abstract class MovableEntityEvent extends EntityEvent {
        MovableEntityEvent(Simulation sim, MovableEntity entity) {
            super(sim, entity);
        }
    }

    /**
     * Move event recording the target coordinates of the movable entity
     */
    public static class MoveEvent extends MovableEntityEvent {

        public final int column;
        public final int row;

        MoveEvent(Simulation sim, MovableEntity entity, int column, int row) {
            super(sim, entity);
            this.row = row;
            this.column = column;
        }

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
        TurnLeftEvent(Simulation sim, MovableEntity entity) {
            super(sim, entity);
        }
    }
}
