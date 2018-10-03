/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Coin;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * A tool for spawning a coin
 * 
 * @author Tim Neumann
 */
public class SpawnCoinTool extends AbstractTool {
    
    /**
     * Creates a new spawn coin tool.
     */
    public SpawnCoinTool() {
        super("Spawn Coin", "coin/coin-default.png");
    }
    
    /**
     * @see de.unistuttgart.informatik.fius.icge.workbench.tools.Tool#apply(de.unistuttgart.informatik.fius.icge.simulation.Simulation,
     *      int, int)
     */
    @Override
    public void apply(Simulation sim, int row, int column) {
        new Coin(sim).forceSpawn(column, row);
    }
    
}
