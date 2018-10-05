/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Sprite;

public class Wall extends Entity {
    public Wall(Simulation sim) {
        super(sim, Sprite.WALL);
    }
    
    /**
     * @see de.unistuttgart.informatik.fius.icge.simulation.Entity#spawnInternal(int, int, de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction)
     */
    @Override
    protected void spawnInternal(int column, int row, Direction direction) throws EntityAlreadyAlive, CellBlockedByWall {
        for(Entity e : this.simulation().entitiesWith(row, column)) {
            throw new CellNotEmpty();
        }
        
        super.spawnInternal(column, row, direction);
    }
    
    public static class CellNotEmpty extends RuntimeException {
    }
}
