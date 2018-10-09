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
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;

/**
 * A tool for creating walls
 * 
 * @author Tim Neumann
 */
public class WallCreationTool extends AbstractTool implements AreaTool {

    /**
     * Creates a wall creation tool
     */
    public WallCreationTool() {
        super("Construct wall", "wall/wall-default.png");
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
        for (int y = startRow; y <= endRow; ++y) {
            for (int x = startColumn; x <= endColumn; ++x) {
                tty = internalApply(tty, x, y);
            }
        }
        // check that we don't set the territory if it didn't change
        if (tty != sim.territory()) sim.setTerritory(tty);
    }

    private Territory internalApply(Territory tty, int x, int y) {
        final int captX = x;
        final int captY = y;
        if (!tty.containsWith(wob -> (wob.column == captX) && (wob.row == captY))) {
            return tty.add(new WorldObject(new WallState(), x, y));
        }
        return tty;
    }

    @Override
    public boolean canApply(Simulation sim, int column, int row) {
        return sim.entitiesAt(column, row).isEmpty();
    }

    @Override
    public boolean canApply(Simulation sim, int startColumn, int endColumn, int startRow, int endRow) {
        for (int col = startColumn; col <= endColumn; ++col) {
            for (int row = startRow; row <= endRow; ++row) {
                if (sim.entitiesAt(col, row).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

}
