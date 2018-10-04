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
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Sprite;

public class AnimationInterpreter {
    
    private final AnimatedTerritory _animated;
    private float _column;
    private float _row;
    private BufferedImage _image;
    
    private static AnimatedImages _noneAnimations = new AnimatedImages();
    private static HashMap<AnimationType, AnimatedImages> _animatedImages = new HashMap<>();
    
    public AnimationInterpreter(AnimatedTerritory animated) {
        this._animated = animated;
    }
    
    public void setObject(WorldObject wob, int currentTick) {
        if (!this._animated.territory().contains(wob)) throw new IllegalArgumentException();
        this._column = wob.column;
        this._row = wob.row;
        Animation animation = this._animated.animation(wob);
        if (animation != null && currentTick < animation.end) {
            if ((currentTick < animation.begin)) throw new IllegalArgumentException();
            float undone = (animation.end - currentTick) / (float) (animation.end - animation.begin);
            float progress = 1 - undone;
            this._image = _animatedImages.get(animation.type).get(wob.sprite, wob.direction, progress);
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
                return;
            }
        }
        this._image = _noneAnimations.get(wob.sprite, wob.direction);
    }
    
    public float column() {
        return this._column;
    }
    
    public float row() {
        return this._row;
    }
    
    public BufferedImage image() {
        return this._image;
    }
    
    static {
        {
            AnimatedImages moveAnimations = new AnimatedImages();
            moveAnimations.set(Sprite.MARIO, Direction.EAST, Arrays.asList("mario/mario-east-0.png", "mario/mario-east-1.png"));
            moveAnimations.set(Sprite.MARIO, Direction.NORTH, Arrays.asList("mario/mario-north-0.png", "mario/mario-north-1.png"));
            moveAnimations.set(Sprite.MARIO, Direction.WEST, Arrays.asList("mario/mario-west-0.png", "mario/mario-west-1.png"));
            moveAnimations.set(Sprite.MARIO, Direction.SOUTH, Arrays.asList("mario/mario-south-0.png", "mario/mario-south-1.png"));
            _animatedImages.put(AnimationType.Move, moveAnimations);
        }
        {
            AnimatedImages turnLeftAnimations = new AnimatedImages();
            turnLeftAnimations.set(Sprite.MARIO, Direction.EAST, Arrays.asList("mario/mario-south-0.png", "mario/mario-east-1.png"));
            turnLeftAnimations.set(Sprite.MARIO, Direction.NORTH, Arrays.asList("mario/mario-east-0.png", "mario/mario-north-1.png"));
            turnLeftAnimations.set(Sprite.MARIO, Direction.WEST, Arrays.asList("mario/mario-north-0.png", "mario/mario-west-1.png"));
            turnLeftAnimations.set(Sprite.MARIO, Direction.SOUTH, Arrays.asList("mario/mario-west-0.png", "mario/mario-south-1.png"));
            _animatedImages.put(AnimationType.TurnLeft, turnLeftAnimations);
        }
        
        _noneAnimations.set(Sprite.MARIO, Direction.EAST, "mario/mario-east-0.png");
        _noneAnimations.set(Sprite.MARIO, Direction.NORTH, "mario/mario-north-0.png");
        _noneAnimations.set(Sprite.MARIO, Direction.WEST, "mario/mario-west-0.png");
        _noneAnimations.set(Sprite.MARIO, Direction.SOUTH, "mario/mario-south-0.png");
        _noneAnimations.set(Sprite.WALL, "wall/wall-default.png");
        _noneAnimations.set(Sprite.COIN, "coin/coin-default.png");
    }
}
