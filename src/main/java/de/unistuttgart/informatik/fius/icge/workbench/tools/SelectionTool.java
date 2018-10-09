/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.workbench.swing.EntityInspector;

/**
 * A tool for selecting entities.
 * 
 * @author Tim Neumann
 */
public class SelectionTool extends AbstractTool {

    /**
     * Creates a selection tool.
     */
    public SelectionTool() {
        super("Selection tool", "arrow.png");
    }

    /**
     * @see de.unistuttgart.informatik.fius.icge.workbench.tools.Tool#apply(de.unistuttgart.informatik.fius.icge.simulation.Simulation,
     *      int, int)
     */
    @Override
    public void apply(Simulation sim, int column, int row) {
        if (!sim.entitiesAt(column, row).isEmpty()) {
            EntityInspector eInsp = new EntityInspector(sim, column, row);
            eInsp.show();
        }
    }

    @Override
    public boolean canApply(Simulation sim, int column, int row) {
        return !sim.entitiesAt(column, row).isEmpty();
    }

}
