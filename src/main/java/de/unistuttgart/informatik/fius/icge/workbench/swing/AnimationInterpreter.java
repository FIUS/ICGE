/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;

import de.unistuttgart.informatik.fius.icge.animations.AnimatedTerritory;
import de.unistuttgart.informatik.fius.icge.animations.Animation;
import de.unistuttgart.informatik.fius.icge.animations.Animation.AnimationType;
import de.unistuttgart.informatik.fius.icge.simulation.EntityType;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

public class AnimationInterpreter {

    private float _column;
    private float _row;
    private BufferedImage _unanimatedImage;
    private BufferedImage _image;
    private boolean _inAnimation = false;

    private static AnimatedImages _noneAnimations = new AnimatedImages();
    private static HashMap<AnimationType, AnimatedImages> _animatedImages = new HashMap<>();
    
    public AnimationInterpreter(AnimatedTerritory animated, WorldObject wob, int currentTick) {
        if (!animated.territory().contains(wob)) throw new IllegalArgumentException();
        this._column = wob.column;
        this._row = wob.row;
        this._unanimatedImage = this._image = _noneAnimations.get(wob.type, wob.direction);
        Animation animation = animated.animation(wob);
        if (animation != null && currentTick < animation.end) {
            if ((currentTick < animation.begin)) throw new IllegalArgumentException();
            this._inAnimation = true;
            float undone = (animation.end - currentTick) / (float) (animation.end - animation.begin);
            float progress = 1 - undone;
            this._image = _animatedImages.get(animation.type).get(wob.type, wob.direction, progress);
            if (this._image != null) {
                if (animation.type == AnimationType.Move) {
                    switch (wob.direction) {
                        default:
                        case EAST:
                            this._column -= undone;
                        break;
                        case NORTH:
                            this._row += undone;
                        break;
                        case WEST:
                            this._column += undone;
                        break;
                        case SOUTH:
                            this._row -= undone;
                        break;
                    }
                }
            }
        }
    }
    
    public float column() {
        return this._column;
    }
    
    public float row() {
        return this._row;
    }

    public BufferedImage unanimatedImage() {
        return this._unanimatedImage;
    }
    
    public BufferedImage image() {
        return this._image;
    }

    public boolean inAnimation() {
        return _inAnimation;
    }

    public static void addPlayerAnimations(EntityType type) {
        AnimatedImages moveAnimations = _animatedImages.get(AnimationType.Move);
        AnimatedImages turnLeftAnimations = _animatedImages.get(AnimationType.TurnLeft);
        if (moveAnimations == null) moveAnimations = new AnimatedImages();
        if (turnLeftAnimations == null) turnLeftAnimations = new AnimatedImages();

        String name = type.toString().toLowerCase();
        String prefix = name + '/' + name + '-';
        moveAnimations.set(type, Direction.EAST, Arrays.asList(prefix + "east-0.png", prefix + "east-1.png"));
        moveAnimations.set(type, Direction.NORTH, Arrays.asList(prefix + "north-0.png", prefix + "north-1.png"));
        moveAnimations.set(type, Direction.WEST, Arrays.asList(prefix + "west-0.png", prefix + "west-1.png"));
        moveAnimations.set(type, Direction.SOUTH, Arrays.asList(prefix + "south-0.png", prefix + "south-1.png"));
        turnLeftAnimations.set(type, Direction.EAST, Arrays.asList(prefix + "south-0.png", prefix + "east-1.png"));
        turnLeftAnimations.set(type, Direction.NORTH, Arrays.asList(prefix + "east-0.png", prefix + "north-1.png"));
        turnLeftAnimations.set(type, Direction.WEST, Arrays.asList(prefix + "north-0.png", prefix + "west-1.png"));
        turnLeftAnimations.set(type, Direction.SOUTH, Arrays.asList(prefix + "west-0.png", prefix + "south-1.png"));
        _noneAnimations.set(type, Direction.EAST, prefix + "east-0.png");
        _noneAnimations.set(type, Direction.NORTH, prefix + "north-0.png");
        _noneAnimations.set(type, Direction.WEST, prefix + "west-0.png");
        _noneAnimations.set(type, Direction.SOUTH, prefix + "south-0.png");

        _animatedImages.put(AnimationType.Move, moveAnimations);
        _animatedImages.put(AnimationType.TurnLeft, turnLeftAnimations);
    }

    static {
        addPlayerAnimations(EntityType.MARIO);
        addPlayerAnimations(EntityType.LUIGI);

        _noneAnimations.set(EntityType.WALL, "wall/wall-default.png");
        _noneAnimations.set(EntityType.COIN, "coin/coin-default.png");
    }
}
