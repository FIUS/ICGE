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
     *            The Simulation of the cell to modify
     * @param column
     *            The column of the cell to modify
     * @param row
     *            The row of the cell to modify
     */
    void apply(Simulation sim, int column, int row);
    
    /**
     * @return the button which corresponds to this tool.
     */
    JButton getToolBarButton();
}
