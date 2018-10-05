/*
* This source file is part of the FIUS ICGE project.
* For more information see github.com/neumantm/ICGE
*
* Copyright (c) 2018 the ICGE project authors.
*/

package de.unistuttgart.informatik.fius.icge.territory;

import de.unistuttgart.informatik.fius.icge.simulation.EntityType;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

public class Editor {
    
    private Territory _tty;
    
    public Editor(Territory tty) {
        this._tty = tty;
    }
    
    public Territory result() {
        return this._tty;
    }
    
    public void add(WorldObject wob) {
        this._tty = this._tty.add(wob);
    }
    
    public void add(EntityType type, int column, int row, Direction direction) {
        this.add(new WorldObject(type, column, row, direction));
    }
    
    public void add(EntityType type, int column, int row) {
        this.add(type, column, row, Direction.EAST);
    }
    
    public void clear() {
        this._tty = new Territory();
    }
    
    public void clear(int column, int row) {
        this._tty = this._tty.removeIf(wob -> (wob.column == column) && (wob.row == row));
    }
    
    public void clear(EntityType type) {
        this._tty = this._tty.removeIf(wob -> wob.type == type);
    }
    
    public void clear(EntityType type, int column, int row) {
        this._tty = this._tty.removeIf(wob -> (wob.type == type) && (wob.column == column) && (wob.row == row));
    }
}
