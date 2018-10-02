/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.animations;

import java.util.HashMap;

import de.unistuttgart.informatik.fius.icge.territory.Territory;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;

public class AnimatedTerritory {
    
    private final Territory _tty;
    private HashMap<WorldObject, Animation> _runningAnimations = new HashMap<>();
    
    public AnimatedTerritory(Territory tty) {
        this._tty = tty;
    }
    
    public AnimatedTerritory setTerritory(Territory tty) {
        AnimatedTerritory result = new AnimatedTerritory(tty);
        result._runningAnimations = this._runningAnimations;
        result._runningAnimations.keySet().removeIf(wob -> !tty.contains(wob));
        return result;
    }
    
    public AnimatedTerritory removeFinished(int tick) {
        AnimatedTerritory result = this.setTerritory(this._tty);
        this._runningAnimations.values().removeIf(anim -> anim.end <= tick);
        return result;
    }
    
    public Territory territory() {
        return this._tty;
    }
    
    public Animation animation(WorldObject wob) {
        return this._runningAnimations.get(wob);
    }
    
    /** Don't use this method after this AnimatedTerritory object is made public */
    public void setAnimation(WorldObject wob, Animation anim) {
        if (wob == null)
            throw new IllegalArgumentException();
        else if (anim == null) {
            this._runningAnimations.remove(wob);
        } else {
            this._runningAnimations.put(wob, anim);
        }
    }
}
