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
import de.unistuttgart.informatik.fius.icge.simulation.Coin.CoinState;
import de.unistuttgart.informatik.fius.icge.simulation.Mario.MarioState;
import de.unistuttgart.informatik.fius.icge.simulation.Wall.WallState;
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
        this._unanimatedImage = this._image = _noneAnimations.get(wob.state.spriteId(), wob.direction);
        Animation animation = animated.animation(wob);
        if (animation != null && currentTick < animation.end) {
            if ((currentTick < animation.begin)) throw new IllegalArgumentException();
            this._inAnimation = true;
            float undone = (animation.end - currentTick) / (float) (animation.end - animation.begin);
            float progress = 1 - undone;
            this._image = _animatedImages.get(animation.type).get(wob.state.spriteId(), wob.direction, progress);
            if (this._image != null) {
                if (animation.type == AnimationType.MOVE) {
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

    public static AnimatedImages accessNoneAnimations() {
        return _noneAnimations;
    }

    public static AnimatedImages accessAnimations(AnimationType type) {
        return _animatedImages.get(type);
    }

    static {
        _animatedImages.put(AnimationType.MOVE, new AnimatedImages());
        _animatedImages.put(AnimationType.TURN_LEFT, new AnimatedImages());

        accessAnimations(AnimationType.MOVE).set(MarioState.class, Direction.EAST,
                Arrays.asList("mario/mario-east-0.png", "mario/mario-east-1.png"));
        accessAnimations(AnimationType.MOVE).set(MarioState.class, Direction.NORTH,
                Arrays.asList("mario/mario-north-0.png", "mario/mario-north-1.png"));
        accessAnimations(AnimationType.MOVE).set(MarioState.class, Direction.WEST,
                Arrays.asList("mario/mario-west-0.png", "mario/mario-west-1.png"));
        accessAnimations(AnimationType.MOVE).set(MarioState.class, Direction.SOUTH,
                Arrays.asList("mario/mario-south-0.png", "mario/mario-south-1.png"));

        accessAnimations(AnimationType.TURN_LEFT).set(MarioState.class, Direction.EAST,
                Arrays.asList("mario/mario-south-0.png", "mario/mario-east-1.png"));
        accessAnimations(AnimationType.TURN_LEFT).set(MarioState.class, Direction.NORTH,
                Arrays.asList("mario/mario-east-0.png", "mario/mario-north-1.png"));
        accessAnimations(AnimationType.TURN_LEFT).set(MarioState.class, Direction.WEST,
                Arrays.asList("mario/mario-north-0.png", "mario/mario-west-1.png"));
        accessAnimations(AnimationType.TURN_LEFT).set(MarioState.class, Direction.SOUTH,
                Arrays.asList("mario/mario-west-0.png", "mario/mario-south-1.png"));

        accessNoneAnimations().set(MarioState.class, Direction.EAST, "mario/mario-east-0.png");
        accessNoneAnimations().set(MarioState.class, Direction.NORTH, "mario/mario-north-0.png");
        accessNoneAnimations().set(MarioState.class, Direction.WEST, "mario/mario-west-0.png");
        accessNoneAnimations().set(MarioState.class, Direction.SOUTH, "mario/mario-south-0.png");
        accessNoneAnimations().set(WallState.class, "wall/wall-default.png");
        accessNoneAnimations().set(CoinState.class, "coin/coin-default.png");
    }
}
