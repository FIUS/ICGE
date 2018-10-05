/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Coin;
import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * A tool for spawning a coin
 * 
 * @author Tim Neumann
 */
public class SpawnCoinTool extends AbstractSpawnEntityTool {
    
    /**
     * Creates a new spawn coin tool.
     */
    public SpawnCoinTool() {
        super("Spawn Coin", "coin/coin-default.png");
    }
    
    @Override
    protected Entity entityToSpawn(Simulation sim) {
        return new Coin(sim);
    }
    
}
