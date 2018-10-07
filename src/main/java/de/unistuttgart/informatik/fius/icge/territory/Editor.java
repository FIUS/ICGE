/*
* This source file is part of the FIUS ICGE project.
* For more information see github.com/neumantm/ICGE
*
* Copyright (c) 2018 the ICGE project authors.
*/

package de.unistuttgart.informatik.fius.icge.territory;

import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

/**
 * A class to create a territory
 * 
 * @author hsalersn
 */
public class Editor {

    private Territory _tty;

    /**
     * Create a editor from the given inital territory
     * 
     * @param tty
     *            The initial territory
     */
    public Editor(Territory tty) {
        this._tty = tty;
    }

    /**
     * @return The resulting territory
     */
    public Territory result() {
        return this._tty;
    }

    public void add(WorldObject wob) {
        this._tty = this._tty.add(wob);
    }

    public void add(EntityState state, int column, int row, Direction direction) {
        this.add(new WorldObject(state, column, row, direction));
    }

    public void add(EntityState state, int column, int row) {
        this.add(state, column, row, Direction.EAST);
    }

    public void clear() {
        this._tty = new Territory();
    }

    public void clear(int column, int row) {
        this._tty = this._tty.removeIf(wob -> (wob.column == column) && (wob.row == row));
    }

    public void clear(Class<? extends EntityState> cls) {
        this._tty = this._tty.removeIf(wob -> wob.state.getClass() == cls);
    }

    public void clear(Class<? extends EntityState> cls, int column, int row) {
        this._tty = this._tty.removeIf(wob -> (wob.state.getClass() == cls) && (wob.column == column) && (wob.row == row));
    }
}
