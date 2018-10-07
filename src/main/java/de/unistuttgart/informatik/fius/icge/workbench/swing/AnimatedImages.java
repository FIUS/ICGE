/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.image.BufferedImage;
import java.util.*;

import de.unistuttgart.informatik.fius.icge.territory.EntityState;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

/**
 * A class for storing and managing sprites
 * 
 * @author haslersn
 */
public class AnimatedImages {

    private final Map<String, SpriteCollection> entries = new HashMap<>();

    /**
     * Set sprite(s) for Entity State class.
     * 
     * @param cls
     *            The entity type to set the sprite(s) for
     * @param dir
     *            The direction of the entity, the sprite(s) are for
     * @param imgNames
     *            The names of the sprite(s)
     */
    public void set(Class<? extends EntityState> cls, Direction dir, Collection<String> imgNames) {
        this.set(this.spriteIdFromClass(cls), dir, imgNames);
    }

    /**
     * Set sprite(s) for spriteId.
     * 
     * @param spriteId
     *            The sprite id to set the sprite(s) for
     * @param dir
     *            The direction of the entity, the sprite(s) are for
     * @param imgNames
     *            The names of the sprite(s)
     */
    public void set(String spriteId, Direction dir, Collection<String> imgNames) {
        SpriteCollection coll = this.getCollectionById(spriteId, true);

        for (String name : imgNames) {
            coll.addImage(Images.image(name), dir);
        }
    }

    /**
     * Set sprite(s) for Entity State class.
     * 
     * @param cls
     *            The entity type to set the sprite(s) for
     * @param imgNames
     *            The names of the sprite(s)
     */
    public void set(Class<? extends EntityState> cls, Collection<String> imgNames) {
        this.set(this.spriteIdFromClass(cls), imgNames);
    }

    /**
     * Set sprite(s) for spriteId.
     * 
     * @param spriteId
     *            The sprite id to set the sprite(s) for
     * @param imgNames
     *            The names of the sprite(s)
     */
    public void set(String spriteId, Collection<String> imgNames) {
        SpriteCollection coll = this.getCollectionById(spriteId, true);

        for (String name : imgNames) {
            coll.addImage(Images.image(name));
        }
    }

    /**
     * Set a sprite for Entity State class.
     * 
     * @param cls
     *            The entity type to set the sprite for
     * @param dir
     *            The direction of the entity the sprite is for
     * @param imgName
     *            The name of the sprite
     */
    public void set(Class<? extends EntityState> cls, Direction dir, String imgName) {
        this.set(this.spriteIdFromClass(cls), dir, imgName);
    }

    /**
     * Set a sprite for spriteId.
     * 
     * @param spriteId
     *            The sprite id to set the sprite for
     * @param dir
     *            The direction of the entity the sprite is for
     * @param imgName
     *            The name of the sprite
     */
    public void set(String spriteId, Direction dir, String imgName) {
        SpriteCollection coll = this.getCollectionById(spriteId, true);

        coll.addImage(Images.image(imgName), dir);
    }

    /**
     * Set a sprite for Entity State class.
     * 
     * @param cls
     *            The entity type to set the sprite for
     * @param imgName
     *            The name of the sprite
     */
    public void set(Class<? extends EntityState> cls, String imgName) {
        this.set(this.spriteIdFromClass(cls), imgName);
    }

    /**
     * Set a sprite for spriteId.
     * 
     * @param spriteId
     *            The sprite id to set the sprite for
     * @param imgName
     *            The name of the sprite
     */
    public void set(String spriteId, String imgName) {
        SpriteCollection coll = this.getCollectionById(spriteId, true);

        coll.addImage(Images.image(imgName));
    }

    /**
     * Get Sprite for Entity State class.
     * 
     * @param cls
     *            The entity type to get the sprite for
     * @param dir
     *            The direction of the entity to get the sprite for
     * @return The sprite
     */
    public BufferedImage get(Class<? extends EntityState> cls, Direction dir) {
        return this.get(this.spriteIdFromClass(cls), dir, 0);
    }

    /**
     * Get Sprite for spriteId.
     * 
     * @param spriteId
     *            The sprite id to set the sprite for
     * @param dir
     *            The direction of the entity to get the sprite for
     * @return The sprite
     */
    public BufferedImage get(String spriteId, Direction dir) {
        return this.get(spriteId, dir, 0);
    }

    /**
     * Get Sprite for Entity State class.
     * 
     * @param cls
     *            The entity type to get the sprite for
     * @param dir
     *            The direction of the entity to get the sprite for
     * @param progress
     *            The progress of the animation to get the sprite for
     * @return The sprite
     */
    public BufferedImage get(Class<? extends EntityState> cls, Direction dir, float progress) {
        return this.get(this.spriteIdFromClass(cls), dir, progress);
    }

    /**
     * Get Sprite for spriteId.
     * 
     * @param spriteId
     *            The sprite id to set the sprite for
     * @param dir
     *            The direction of the entity to get the sprite for
     * @param progress
     *            The progress of the animation to get the sprite for
     * @return The sprite
     */
    public BufferedImage get(String spriteId, Direction dir, float progress) {
        if ((progress < 0) || (progress > 1)) throw new IllegalArgumentException("Progress out of bounds");
        SpriteCollection sprites = this.getCollectionById(spriteId, false);
        if (sprites == null) throw new IllegalArgumentException("Unknown sprite id");
        List<BufferedImage> spriteList = sprites.getImages(dir);
        int size = spriteList.size();
        if (size <= 0) return null;
        int index = Math.min((int) (progress * size), size - 1);
        return spriteList.get(index);
    }

    /**
     * Get the sprite collection with the given spriteId
     * 
     * @param spriteId
     *            The sprite id to set the sprite for
     * @param createIfNull
     *            if true and collection is null create a new SpriteCollection
     * @return The sprite collection
     */
    private SpriteCollection getCollectionById(String spriteId, boolean createIfNull) {
        SpriteCollection coll = this.entries.get(spriteId);
        if (createIfNull && coll == null) {
            coll = new SpriteCollection(spriteId);
            this.entries.put(spriteId, coll);
        }
        return coll;
    }

    /**
     * Generate standard spriteId for Entity State class
     * 
     * @param cls
     *            The entity type to generate the sprite id for
     * @return The sprite id
     */
    private String spriteIdFromClass(Class<? extends EntityState> cls) {
        String spriteId = cls.getCanonicalName();
        if (spriteId == null) {
            spriteId = cls.getName();
        }
        return spriteId;
    }

    private static class SpriteCollection {

        public final String spriteId;
        private List<BufferedImage> imagesNorth;
        private List<BufferedImage> imagesEast;
        private List<BufferedImage> imagesSouth;
        private List<BufferedImage> imagesWest;

        public SpriteCollection(String spriteId) {
            this.spriteId = spriteId;
            this.imagesNorth = new ArrayList<>(2);
            this.imagesEast = new ArrayList<>(2);
            this.imagesSouth = new ArrayList<>(2);
            this.imagesWest = new ArrayList<>(2);
        }

        public void addImage(BufferedImage image) {
            for (Direction dir : Direction.values()) {
                this.addImage(image, dir);
            }
        }

        public void addImage(BufferedImage image, Direction dir) {
            switch (dir) {
                case NORTH:
                    this.imagesNorth.add(image);
                break;
                case EAST:
                    this.imagesEast.add(image);
                break;
                case SOUTH:
                    this.imagesSouth.add(image);
                break;
                case WEST:
                    this.imagesWest.add(image);
                break;
                default:
                    this.imagesEast.add(image);
            }
        }

        public List<BufferedImage> getImages() {
            return this.getImages(Direction.EAST);
        }

        public List<BufferedImage> getImages(Direction dir) {
            switch (dir) {
                case NORTH:
                    return this.imagesNorth;
                case EAST:
                    return this.imagesEast;
                case SOUTH:
                    return this.imagesSouth;
                case WEST:
                    return this.imagesWest;
                default:
                    return this.imagesEast;
            }
        }
    }
}
