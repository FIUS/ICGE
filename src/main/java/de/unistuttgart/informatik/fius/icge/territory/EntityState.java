/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.territory;

import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

/**
 * Interface for managing basic entity state like sprite or solid status.
 */
public interface EntityState {

    /**
     * Creates a new Entity.
     * 
     * @param sim
     *            The simulation to create the entity in
     * @return The created entity.
     */
    public Entity createEntity(Simulation sim);

    /**
     * Checks wether the entity is considered a solid entity.
     * 
     * The default implementation always returns false.
     * 
     * @return true if entity is solid
     */
    public default boolean isSolid() {
        return false;
    }

    /**
     * Return the sprite id used to determine the sprite to render this Entity with.
     * 
     * @return this.getClass().getCanonicalName();
     */
    public default String spriteId() {
        String spriteId = this.getClass().getCanonicalName();
        if (spriteId == null) {
            spriteId = this.getClass().getName();
        }
        return spriteId;
    }
}
