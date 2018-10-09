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
     * @param startColumn
     *            The first column to modify
     * @param endColumn
     *            The last column to modify
     * @param startRow
     *            The first row to modify
     * @param endRow
     *            The last row to modify
     */
    public void applyToAll(Simulation sim, int startColumn, int endColumn, int startRow, int endRow);

    /**
     * Should return an indicator whether applying this tool right now would be successful. This
     * is however not supposed to be binding and currently only used for better visualization.
     * 
     * @param sim
     *            The Simulation that would be modified
     * @param startColumn
     *            The first column that would be modified
     * @param endColumn
     *            The last column that would be modified
     * @param startRow
     *            The first row that would be modified
     * @param endRow
     *            The last row that would be modified
     * @return The described indicator
     */
    public boolean canApply(Simulation sim, int startColumn, int endColumn, int startRow, int endRow);

}
