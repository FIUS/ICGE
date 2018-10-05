/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

public class Coin extends CollectableEntity {
    public Coin(Simulation sim) {
        super(sim, EntityType.COIN);
    }
    
    @Override
    public int requiredSpace() {
        return 0;
    }
}
