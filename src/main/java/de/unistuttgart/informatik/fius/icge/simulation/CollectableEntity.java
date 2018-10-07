/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

/**
 * Base class for collectable entities
 * 
 * @author haslersn, neumantm
 */
public abstract class CollectableEntity extends Entity {
    /**
     * Creates a new collectable entity in the given simulation
     * 
     * @param sim
     *            The simulation to create the entity in
     */
    public CollectableEntity(Simulation sim) {
        super(sim);
    }

    /**
     * @return The space or weight requirements of this collectable entity
     */
    public abstract int requiredSpace();
}
