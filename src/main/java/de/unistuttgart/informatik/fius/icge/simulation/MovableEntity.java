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

public abstract class MovableEntity extends Entity {

    private final Deque<MoveEvent> _positionStack = new ArrayDeque<>(100);

    protected MovableEntity(Simulation sim, EntityType type) {
        super(sim, type);
    }

    @Override
    public void spawn(int column, int row, Direction direction) {
        EventDispatcher.addListener(SpawnEvent.class, ev -> {
            SpawnEvent se = (SpawnEvent) ev;
            if ((se.simulation == this.simulation()) && (se.entity == this)) {
                this._positionStack.add(new MoveEvent(this.simulation(), this, se.row, se.column));
                return this.alive();
            }
            return true;
        });
        EventDispatcher.addListener(MoveEvent.class, ev -> {
            MoveEvent me = (MoveEvent) ev;
            if ((me.simulation == this.simulation()) && (me.entity == this)) {
                this._positionStack.add(me);
                return this.alive();
            }
            return true;
        });

        super.spawn(column, row, direction);
    }

    @Override
    public final float getZ() {
        return 100;
    }

    @InspectionMethod
    public void move() throws IllegalMove, EntityNotAlive {
        this.delayed(() -> {
            WorldObject wobAfter = this.wobAfterMove();
            SimulationEvent ev = new MoveEvent(this.simulation(), this, wobAfter);
            this.simulation().setWorldObject(this, wobAfter, ev);
        });
    }

    public boolean canMove() {
        try {
            this.wobAfterMove();
            return true;
        } catch (IllegalMove | EntityNotAlive e) {
            return false;
        }
    }

    public boolean tryMove() {
        try {
            this.move();
            return true;
        } catch (IllegalMove | EntityNotAlive e) {
            return false;
        }
    }

    @InspectionMethod
    public void turnLeft() throws EntityNotAlive {
        SimulationEvent ev = new TurnLeftEvent(this.simulation(), this);
        this.delayed(() -> {
            this.simulation().setWorldObject(this, this.wobAfterTurnLeft(), ev);
        });
    }

    public MoveEvent firstPosition() {
        return this._positionStack.peekFirst();
    }

    public MoveEvent lastPosition() {
        return this._positionStack.peekLast();
    }

    public Iterable<MoveEvent> positionHistory() {
        return () -> this._positionStack.iterator();
    }

    // private

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
                .containsWith(wall -> (wall.column == newCol) && (wall.row == newRow) && (wall.type == EntityType.WALL)))
            throw new IllegalMove();
        return new WorldObject(wob.type, column, row, 100, wob.direction);
    }

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
        return new WorldObject(wob.type, wob.column, wob.row, 100, dir);
    }

    // Exceptions:

    public static class IllegalMove extends RuntimeException {
        private static final long serialVersionUID = 6992550372582751611L;
    }

    // Events

    public static abstract class MovableEntityEvent extends EntityEvent {
        MovableEntityEvent(Simulation sim, MovableEntity entity) {
            super(sim, entity);
        }
    }

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

    public static class TurnLeftEvent extends MovableEntityEvent {
        TurnLeftEvent(Simulation sim, MovableEntity entity) {
            super(sim, entity);
        }
    }
}
