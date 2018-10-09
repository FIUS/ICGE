/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * A tool for despawning entities.
 * 
 * @author Tim Neumann
 */
public class DespawnTool extends AbstractTool {

    /**
     * Creates a despawn tool
     */
    public DespawnTool() {
        super("Despawn", "cross.png");
    }

    /**
     * @see de.unistuttgart.informatik.fius.icge.workbench.tools.Tool#apply(de.unistuttgart.informatik.fius.icge.simulation.Simulation,
     *      int, int)
     */
    @Override
    public void apply(Simulation sim, int column, int row) {
        for (Entity ent : sim.entitiesAt(column, row)) {
            ent.forceDespawn();
        }
    }

    @Override
    public boolean canApply(Simulation sim, int column, int row) {
        return !sim.entitiesAt(column, row).isEmpty();
    }

}
