/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import de.unistuttgart.informatik.fius.icge.territory.EntityState;

public class Coin extends CollectableEntity {

    public static class CoinState implements EntityState {
        @Override
        public Entity createEntity(Simulation sim) {
            return new Coin(sim);
        }
    }

    public Coin(Simulation sim) {
        super(sim);
    }

    @Override
    public EntityState state() {
        return new CoinState();
    }

    @Override
    public int requiredSpace() {
        return 0;
    }
}
