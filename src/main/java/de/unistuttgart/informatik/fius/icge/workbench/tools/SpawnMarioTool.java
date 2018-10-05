/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Mario;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * A tool for spawning marios.
 * 
 * @author Tim Neumann
 */
public class SpawnMarioTool extends AbstractSpawnEntityTool {
    
    /**
     * Creates a spawn mario tool
     */
    public SpawnMarioTool() {
        super("Spawn a mario", "mario/mario-east-0.png");
    }
    
    @Override
    protected Entity entityToSpawn(Simulation sim) {
        return new Mario(sim);
    }
    
}
