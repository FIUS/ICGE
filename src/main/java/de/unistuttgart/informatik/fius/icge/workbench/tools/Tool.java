/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import javax.swing.JButton;

import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * A tool for manipulating the SimPanel
 * 
 * @author Tim Neumann
 */
public interface Tool {
    /**
     * Applies this tool to the specified cell
     * 
     * @param sim
     *            The Simulation to modify
     * @param column
     *            The column of the cell to modify
     * @param row
     *            The row of the cell to modify
     */
    public void apply(Simulation sim, int column, int row);

    /**
     * @return the button which corresponds to this tool.
     */
    JButton getToolBarButton();

    /**
     * Should return an indicator whether applying this tool right now would be successful. This
     * is however not supposed to be binding and currently only used for better visualization.
     * 
     * @param sim
     *            The Simulation that would be modified
     * @param column
     *            The column of the cell that would be modified
     * @param row
     *            The row of the cell that would be modified
     * @return The described indicator
     */
    public boolean canApply(Simulation sim, int column, int row);

}
