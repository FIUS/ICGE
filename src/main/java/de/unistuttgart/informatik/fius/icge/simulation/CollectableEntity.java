/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import de.unistuttgart.informatik.fius.icge.territory.EntityState;

/**
 * Base class for collectable entities
 * 
 * @author haslersn, neumantm
 */
public abstract class CollectableEntity extends Entity {
    public CollectableEntity(Simulation sim) {
        super(sim);
    }
    
    /**
     * Returns the space or weight requirements of this collectable entity
     * 
     * @return
     */
    public abstract int requiredSpace();
}
