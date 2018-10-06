/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionAttribute;
import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionMethod;

/**
 * A mario entity
 * 
 * @author haslersn, neumantm
 */
public class Mario extends GreedyEntity {

    /**
     * Creates a new mario with in given simulation
     * 
     * @param sim
     *            The simulation for the new mario.
     */
    public Mario(Simulation sim) {
        super(sim, EntityType.MARIO);
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
        super(sim, EntityType.MARIO);
        setCoinCount(coinCount);
    }

    @Override
    public boolean canCollectType(EntityType type) {
        return type == EntityType.COIN;
    }

    @Override
    public boolean canDropType(EntityType type) {
        return type == EntityType.COIN;
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
                this._inventory.add(EntityType.COIN);
            }
        } else {
            for (int i = 0; i < -diff; i++) {
                this._inventory.remove(EntityType.COIN);
            }
        }
    }

    /**
     * Checks whether mario can currently collect a coin
     * 
     * @return Whether mario can collect a coin.
     */
    public boolean canCollectCoin() {
        return this.canCollect(EntityType.COIN);
    }

    /**
     * Collect a coin.
     */
    public void collectCoin() {
        this.collect(EntityType.COIN);
    }

    /**
     * Try to collect a coin.
     * 
     * @return Whether it worked.
     */
    @InspectionMethod(name = "collectCoin")
    public boolean tryCollectCoin() {
        return this.tryCollect(EntityType.COIN);
    }

    /**
     * Check whether mario can currently drop a coin
     * 
     * @return Whether mario can collect a coin.
     */
    public boolean canDropCoin() {
        return this.canDrop(EntityType.COIN);
    }

    /**
     * Drop a coin.
     */
    public void dropCoin() {
        this.drop(EntityType.COIN);
    }

    /**
     * Try to drop a coin.
     * 
     * @return Whether it worked.
     */
    @InspectionMethod(name = "dropCoin")
    public boolean tryDropCoin() {
        return this.tryDrop(EntityType.COIN);
    }

}
