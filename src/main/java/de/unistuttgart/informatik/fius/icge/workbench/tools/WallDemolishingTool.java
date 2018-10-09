/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.simulation.Wall.WallState;
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
    public void apply(Simulation sim, int column, int row) {
        Territory tty = sim.territory();
        tty = internalApply(tty, column, row);
        sim.setTerritory(tty);
    }

    /**
     * @see de.unistuttgart.informatik.fius.icge.workbench.tools.AreaTool#applyToAll(de.unistuttgart.informatik.fius.icge.simulation.Simulation,
     *      int, int, int, int)
     */
    @Override
    public void applyToAll(Simulation sim, int startColumn, int endColumn, int startRow, int endRow) {
        Territory tty = sim.territory();
        for (int row = startRow; row <= endRow; ++row) {
            for (int col = startColumn; col <= endColumn; ++col) {
                tty = internalApply(tty, col, row);
            }
        }
        // check that we don't set the territory if it didn't change
        if (tty != sim.territory()) sim.setTerritory(tty);
    }

    private Territory internalApply(Territory tty, int column, int row) {
        return tty.removeIf(wob -> (wob.column == column) && (wob.row == row) && (wob.state instanceof WallState));
    }

    @Override
    public boolean canApply(Simulation sim, int column, int row) {
        return sim.territory()
                .containsWith(wob -> (wob.column == column) && (wob.row == row) && (wob.state instanceof WallState));
    }

    @Override
    public boolean canApply(Simulation sim, int startColumn, int endColumn, int startRow, int endRow) {
        return sim.territory().containsWith(wob -> (wob.column >= startColumn) && (wob.row <= endColumn)
                && (wob.row >= startRow) && (wob.row <= endRow) && (wob.state instanceof WallState));
    }

}
