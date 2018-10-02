/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Sprite;

public class Mario extends GreedyEntity {
    
    private int coinCount = 0;
    
    public Mario(Simulation sim) {
        super(sim, Sprite.MARIO);
    }
    
    @Override
    public boolean canCollectType(Sprite type) {
        return type == Sprite.COIN;
    }
    
    @Override
    public void collected(Sprite type) {
        this.coinCount++;
    }
    
    @Override
    public boolean canDropType(Sprite type) {
        return type == Sprite.COIN;
    }
    
    @Override
    public void dropped(Sprite type) {
        this.coinCount--;
    }
}
