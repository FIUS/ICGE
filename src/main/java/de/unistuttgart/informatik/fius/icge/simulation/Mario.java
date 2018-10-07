/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.ArrayList;

import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionAttribute;
import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionMethod;
import de.unistuttgart.informatik.fius.icge.territory.EntityState;

/**
 * A mario entity
 * 
 * @author haslersn, neumantm
 */
public class Mario extends GreedyEntity {

    /**
     * The entity state of mario
     */
    public static class MarioState extends GreedyEntityState {
        /**
         * Creates a new mario entity state with the given inventory
         * 
         * @param inventory
         *            The inventory for this state.
         */
        public MarioState(ArrayList<Entity> inventory) {
            super(inventory);
        }

        @Override
        public Entity createEntity(Simulation sim) {
            // This line only works as long as mario can only collect coins
            return new Mario(sim, this.inventory.size());
        }
    }

    /**
     * Creates a new mario with in given simulation
     * 
     * @param sim
     *            The simulation for the new mario.
     */
    public Mario(Simulation sim) {
        super(sim, new ArrayList<>());
    }

    /**
     * Creates a new mario with the given initial coin count in the given simulation.
     * 
     * @param sim
     *            The simulation for the new mario.
     * @param coinCount
     *            The initial count of coins of mario.
     */
    public Mario(Simulation sim, int coinCount) {
        this(sim);
        setCoinCount(coinCount);
    }

    @Override
    public EntityState state() {
        return new MarioState(this._inventory);
    }

    @Override
    protected boolean canCollectType(Class<? extends Entity> cls) {
        return cls == Coin.class;
    }

    @Override
    protected boolean canDropType(Class<? extends Entity> cls) {
        return cls == Coin.class;
    }

    /**
     * Get the coin count of mario.
     * 
     * @return The coin count
     */
    @InspectionAttribute
    public int getCoinCount() {
        return this._inventory.size();
    }

    /**
     * Set the coin count of mario.
     * 
     * @param count
     *            The coin count
     */
    @InspectionAttribute
    protected void setCoinCount(int count) {
        int diff = count - getCoinCount();
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                this._inventory.add(new Coin(simulation()));
            }
        } else {
            for (int i = 0; i < -diff; i++) {
                int lastIndex = this._inventory.size() - 1;
                this._inventory.remove(lastIndex);
            }
        }
    }

    /**
     * Checks whether mario can currently collect a coin
     * 
     * @return Whether mario can collect a coin.
     */
    public boolean canCollectCoin() {
        return this.canCollect(Coin.class);
    }

    /**
     * Collect a coin.
     */
    public void collectCoin() {
        this.collect(Coin.class);
    }

    /**
     * Try to collect a coin.
     * 
     * @return Whether it worked.
     */
    @InspectionMethod(name = "collectCoin")
    public boolean tryCollectCoin() {
        return this.tryCollect(Coin.class);
    }

    /**
     * Check whether mario can currently drop a coin
     * 
     * @return Whether mario can collect a coin.
     */
    public boolean canDropCoin() {
        return this.canDrop(Coin.class);
    }

    /**
     * Drop a coin.
     */
    public void dropCoin() {
        this.drop(Coin.class);
    }

    /**
     * Try to drop a coin.
     * 
     * @return Whether it worked.
     */
    @InspectionMethod(name = "dropCoin")
    public boolean tryDropCoin() {
        return this.tryDrop(Coin.class);
    }

}
