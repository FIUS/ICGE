/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.territory;

import java.util.function.Predicate;

public class WorldObject implements Comparable<WorldObject> {

    public final EntityState state;
    public final int column;
    public final int row;
    public final Direction direction;
    public final float z;

    public static Predicate<WorldObject> predicateIsAt(int column, int row) {
        return wob -> (wob.column == column) && (wob.row == row);
    }

    public WorldObject(EntityState state, int column, int row, float z, Direction direction) {
        this.column = column;
        this.row = row;
        this.state = state;
        this.z = z;
        this.direction = direction;
    }

    public WorldObject(EntityState state, int column, int row, Direction direction) {
        this(state, column, row, 0, direction);
    }

    public WorldObject(EntityState state, int column, int row, float z) {
        this(state, column, row, z, Direction.EAST);
    }

    public WorldObject(EntityState state, int column, int row) {
        this(state, column, row, Direction.EAST);
    }

    /**
     * Checks whether the given object is on the same position as this.
     *
     * @param obj
     *            The object to compare.
     * @return Whether the object is on the same position.
     */
    public boolean isSamePos(WorldObject obj) {
        return this.column == obj.column && this.row == obj.row;
    }

    public static enum Direction {
        EAST(0, 0), NORTH(1, 0.5 * Math.PI), WEST(2, Math.PI), SOUTH(3, -0.5 * Math.PI);

        private int _quarters;
        private double _radians;

        private Direction(int quarters, double radians) {
            this._quarters = quarters;
            this._radians = radians;
        }

        public int quarters() {
            return this._quarters;
        }

        public double radians() {
            return this._radians;
        }
    }

    @Override
    public int compareTo(WorldObject o) {
        float compareResult = 0;
        compareResult = this.z - o.z;
        if (compareResult == 0) {
            compareResult = this.row - o.row;
        }
        if (compareResult == 0) {
            compareResult = this.column - o.column;
        }
        if (compareResult < 0) {
            return -1;
        }
        if (compareResult > 0) {
            return 1;
        }
        return 0;
    }
}
