/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.territory;

import java.util.function.Function;

import de.unistuttgart.informatik.fius.icge.simulation.*;

public class WorldObject implements Comparable<WorldObject> {

    public final EntityType type;
    public final int column;
    public final int row;
    public final Direction direction;
    public final float z;

    public WorldObject(EntityType type, int column, int row, float z, Direction direction) {
        this.column = column;
        this.row = row;
        this.type = type;
        this.z = z;
        this.direction = direction;
    }

    public WorldObject(EntityType type, int column, int row, Direction direction) {
        this(type, column, row, 0, direction);
    }

    public WorldObject(EntityType type, int column, int row, float z) {
        this(type, column, row, z, Direction.EAST);
    }

    public WorldObject(EntityType type, int column, int row) {
        this(type, column, row, Direction.EAST);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof WorldObject)) return false;
        WorldObject wob = (WorldObject) other;
        return wob.type == this.type && wob.column == this.column && wob.row == this.row && wob.direction == this.direction && wob.z == this.z;
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
        compareResult = this.row - o.row;
        if (compareResult == 0) {
            compareResult = this.column - o.column;
        }
        if (compareResult == 0) {
            compareResult = this.z - o.z;
        }
        if (compareResult == 0) {
            return this.type.compareTo(o.type);
        }
        if (compareResult < 0) {
            return -1;
        }
        return 1;
    }
}
