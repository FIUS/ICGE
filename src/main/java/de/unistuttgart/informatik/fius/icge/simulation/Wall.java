/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

public class Wall extends Entity {
    public Wall(Simulation sim) {
        super(sim, EntityType.WALL);
    }
    
    /**
     * @see de.unistuttgart.informatik.fius.icge.simulation.Entity#spawn(int, int, de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction)
     */
    @Override
    public void spawn(int column, int row, Direction direction) throws EntityAlreadyAlive, CellBlockedByWall {
        if (!this.simulation().entitiesWith(row, column).isEmpty()) {
            throw new CellNotEmpty();
        }
        
        super.spawn(column, row, direction);
    }
    
    public static class CellNotEmpty extends RuntimeException {
        private static final long serialVersionUID = -4741977214724813309L;
    }
}
