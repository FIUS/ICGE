package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.function.Function;

/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

public enum EntityType {
    MARIO(Mario::new), LUIGI(Luigi::new), WALL(Wall::new), COIN(Coin::new);

    private Function<Simulation, Entity> _creator;

    EntityType(Function<Simulation, Entity> creator) {
        this._creator = creator;
    }

    public Entity createEntity(Simulation sim) {
        return this._creator.apply(sim);
    }
}
