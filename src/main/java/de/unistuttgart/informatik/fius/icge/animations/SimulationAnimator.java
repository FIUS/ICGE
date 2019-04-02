/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.animations;

import de.unistuttgart.informatik.fius.icge.animations.Animation.AnimationType;
import de.unistuttgart.informatik.fius.icge.event.EventHandler;
import de.unistuttgart.informatik.fius.icge.simulation.MovableEntity.MovableEntityEvent;
import de.unistuttgart.informatik.fius.icge.simulation.MovableEntity.MoveEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.SimulationEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.TickEvent;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;

public class SimulationAnimator {
    
    private final Simulation _sim;
    private AnimatedTerritory _animated;
    private int _delay = 25; // delay is in simulation ticks
    private EventHandler _eventHandler = new EventHandler();

    public SimulationAnimator(Simulation sim) {
        if (sim == null) throw new IllegalArgumentException();
        this._sim = sim;
        this._animated = new AnimatedTerritory(sim.territory());
        this.initListener();
    }
    
    public Simulation simulation() {
        return this._sim;
    }
    
    public AnimatedTerritory animated() {
        return this._animated;
    }
    
    // delay is in simulation ticks
    public int delay() {
        return this._delay;
    }
    
    // delay is in simulation ticks
    public void setDelay(int delay) {
        this._delay = delay;
    }

    private void initListener() {
        this._eventHandler.addListener(SimulationEvent.class, ev -> {
            if (!(ev instanceof SimulationEvent)) return true;
            SimulationEvent se = (SimulationEvent) ev;
            if (se.simulation != this._sim) return true;
            if (se instanceof TickEvent) {
                this._animated = this._animated.removeFinished(se.simulation.tickCount());
            }
            if (se.simulation.territory() == this._animated.territory()) return true;
            AnimatedTerritory nextAnimated = this._animated.setTerritory(se.simulation.territory());
            if (se instanceof MovableEntityEvent) {
                MovableEntityEvent mee = (MovableEntityEvent) se;
                int begin = mee.simulation.tickCount();
                int end = mee.entity.getMoveEndTick();
                if (end <= begin) {
                    end = begin + 1;
                }
                WorldObject wob = mee.entity.worldObject();
                Animation anim = new Animation(begin, end, animationType(mee));
                nextAnimated.setAnimation(wob, anim);
            }
            this._animated = nextAnimated;
            return true;
        });
    }
    
    private static AnimationType animationType(MovableEntityEvent mee) {
        if (mee instanceof MoveEvent) return AnimationType.MOVE;
        // TurnLeftEvent is the only other subclass of abstract MovableEntityEvent
        return AnimationType.TURN_LEFT;
    }
}
