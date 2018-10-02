/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.animations;

public class Animation {

    public final int begin;
    public final int end;
    public final AnimationType type;

    public static enum AnimationType {
        Move, TurnLeft;
    }

    public Animation(int begin, int end, AnimationType type) {
        this.begin = begin;
        this.end = end;
        this.type = type;
    }
}
