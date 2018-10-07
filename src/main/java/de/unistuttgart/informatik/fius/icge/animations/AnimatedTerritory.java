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

/**
 * A wrapper for territory which supports annimations.
 * 
 * @author haslersn
 */
public class AnimatedTerritory {

    private final Territory _tty;
    private HashMap<WorldObject, Animation> _runningAnimations = new HashMap<>();

    /**
     * Create a new animated territory with the given territory
     * 
     * @param tty
     *            The territory to use
     */
    public AnimatedTerritory(Territory tty) {
        this._tty = tty;
    }

    /**
     * Get a new animated territory from the current one with a new territory.
     * <p>
     * The new animated territory will contain all animations from the current one, except if the world object does not exist
     * any more.
     * 
     * @param tty
     *            The new territory
     * @return The new animated territory
     */
    public AnimatedTerritory setTerritory(Territory tty) {
        AnimatedTerritory result = new AnimatedTerritory(tty);
        result._runningAnimations = this._runningAnimations;
        result._runningAnimations.keySet().removeIf(wob -> !tty.contains(wob));
        return result;
    }

    /**
     * Get a new animated territory with all animations removed which finish before or at the given tick
     * 
     * @param tick
     *            The tick to check for
     * @return A new animated territory with all finished animations removed
     */
    public AnimatedTerritory removeFinished(int tick) {
        AnimatedTerritory result = this.setTerritory(this._tty);
        this._runningAnimations.values().removeIf(anim -> anim.end <= tick);
        return result;
    }

    /**
     * @return The territory
     */
    public Territory territory() {
        return this._tty;
    }

    /**
     * Get the animation for a given world object
     * 
     * @param wob
     *            The world object to get the animation for
     * @return The animation
     */
    public Animation animation(WorldObject wob) {
        return this._runningAnimations.get(wob);
    }

    /**
     * Sets the given animation for the given world object
     * <p>
     * Don't use this method after this AnimatedTerritory object is made public
     * 
     * @param wob
     *            The world object to set the animation for
     * @param anim
     *            The animation to set
     */
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
