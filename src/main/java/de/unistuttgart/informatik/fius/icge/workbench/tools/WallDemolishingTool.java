/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.EntityType;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.territory.Territory;

/**
 * A tool for demolishing walls
 * 
 * @author Tim Neumann
 */
public class WallDemolishingTool extends AbstractTool implements AreaTool {
    
    /**
     * Creates a wall demolishing tool
     */
    public WallDemolishingTool() {
        super("Demolish Wall", "wall/wall-delete.png");
    }
    
    /**
     * @see de.unistuttgart.informatik.fius.icge.workbench.tools.Tool#apply(de.unistuttgart.informatik.fius.icge.simulation.Simulation,
     *      int, int)
     */
    @Override
    public void apply(Simulation sim, int row, int column) {
        Territory tty = sim.territory();
        tty = internalApply(tty, column, row);
        sim.setTerritory(tty);
    }
    
    /**
     * @see de.unistuttgart.informatik.fius.icge.workbench.tools.AreaTool#applyToAll(de.unistuttgart.informatik.fius.icge.simulation.Simulation,
     *      int, int, int, int)
     */
    @Override
    public void applyToAll(Simulation sim, int startRow, int endRow, int startColumn, int endColumn) {
        Territory tty = sim.territory();
        for (int y = startRow; y <= endRow; ++y) {
            for (int x = startColumn; x <= endColumn; ++x) {
                tty = internalApply(tty, x, y);
            }
        }
        sim.setTerritory(tty);
    }
    
    private Territory internalApply(Territory tty, int x, int y) {
        final int captX = x;
        final int captY = y;
        return tty.removeIf(wob -> (wob.column == captX) && (wob.row == captY) && (wob.type == EntityType.WALL));
    }
    
}
