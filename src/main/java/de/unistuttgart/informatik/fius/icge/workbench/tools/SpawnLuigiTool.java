/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Luigi;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * A tool for spawning luigi.
 */
public class SpawnLuigiTool extends AbstractSpawnEntityTool {

    /**
     * Creates a spawn luigi tool
     */
    public SpawnLuigiTool() {
        super("Spawn a luigi", "luigi/luigi-east-0.png");
    }

    @Override
    protected Entity entityToSpawn(Simulation sim) {
        return new Luigi(sim);
    }

}
