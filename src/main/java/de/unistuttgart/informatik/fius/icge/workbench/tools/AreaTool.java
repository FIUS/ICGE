/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * A tool that supports selecting a whole area of tiles.
 * 
 * @author Tim Neumann
 */
public interface AreaTool extends Tool {
    /**
     * Applies this tool to all cells in the specified area
     * 
     * @param sim
     *            The Simulation to modify
     * @param startRow
     *            The first row to modify
     * @param endRow
     *            The last row to modify
     * @param startColumn
     *            The first column to modify
     * @param endColumn
     *            The last column to modify
     */
    void applyToAll(Simulation sim, int startRow, int endRow, int startColumn, int endColumn);
}
