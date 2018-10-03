/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Mario;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * A tool for spawning marios.
 * 
 * @author Tim Neumann
 */
public class SpawnMarioTool extends AbstractTool {
    
    /**
     * Creates a spawn mario tool
     */
    public SpawnMarioTool() {
        super("Spawn a mario", "mario/mario-east-0.png");
    }
    
    /**
     * @see de.unistuttgart.informatik.fius.icge.workbench.tools.Tool#apply(de.unistuttgart.informatik.fius.icge.simulation.Simulation,
     *      int, int)
     */
    @Override
    public void apply(Simulation sim, int row, int column) {
        new Mario(sim).forceSpawn(column, row);
    }
    
}
