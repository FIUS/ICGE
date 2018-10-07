/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import de.unistuttgart.informatik.fius.icge.territory.EntityState;

/**
 * Coin collectable entity
 */
public class Coin extends CollectableEntity {

    /**
     * The state of a coin.
     * 
     * @author haslersn
     */
    public static class CoinState implements EntityState {
        @Override
        public Entity createEntity(Simulation sim) {
            return new Coin(sim);
        }

        @Override
        public boolean isSolid() {
            return false;
        }
    }

    /**
     * Creates a coin in the given simulation
     * 
     * @param sim
     *            The simulation to create the coin in
     */
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
