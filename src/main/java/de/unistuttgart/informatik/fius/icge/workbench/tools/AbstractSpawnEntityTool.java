/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import de.unistuttgart.informatik.fius.icge.simulation.Entity.CellBlockedBySolidEntity;
import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Mario;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * A tool for spawning entities.
 *
 * @author Tim Neumann
 */
public abstract class AbstractSpawnEntityTool extends AbstractTool {

    /**
     * Creates a tool to spawn an entity which is obtained via `entityToSpawn()`
     *
     * @param dToolTip
     *            The description displayed as the tool tip.
     * @param dImgName
     *            The name of the image to use for the button.
     */
    public AbstractSpawnEntityTool(String dToolTip, String dImgName) {
        super(dToolTip, dImgName);
    }

    protected abstract Entity entityToSpawn(Simulation sim);

    /**
     * @see de.unistuttgart.informatik.fius.icge.workbench.tools.Tool#apply(de.unistuttgart.informatik.fius.icge.simulation.Simulation,
     *      int, int)
     */
    @Override
    public void apply(Simulation sim, int column, int row) {
        try {
            entityToSpawn(sim).forceSpawn(column, row);
        } catch(CellBlockedBySolidEntity e) {
            // intentionally do nothing since this was attempted via the GUI
        }
    }

}
