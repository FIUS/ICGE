/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unistuttgart.informatik.fius.icge.territory.EntityState;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

public class AnimatedImages {
    
    private final Map<String, SpriteCollection> entries = new HashMap<>();
    
    /**
     * Set sprite(s) for Entity State class.
     * 
     * @param cls
     * @param dir
     * @param imgNames
     */
    public void set(Class<? extends EntityState> cls, Direction dir, Collection<String> imgNames) {
        this.set(this.spriteIdFromClass(cls), dir, imgNames);
    }
    
    /**
     * Set sprite(s) for spriteId.
     * 
     * @param cls
     * @param dir
     * @param imgNames
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
     * @param imgNames
     */
    public void set(Class<? extends EntityState> cls, Collection<String> imgNames) {
        this.set(this.spriteIdFromClass(cls), imgNames);
    }

    /**
     * Set sprite(s) for spriteId.
     * 
     * @param spriteId
     * @param imgNames
     */
    public void set(String spriteId, Collection<String> imgNames) {
        SpriteCollection coll = this.getCollectionById(spriteId, true);
        
        for (String name : imgNames) {
            coll.addImage(Images.image(name));
        }
    }
    
    /**
     * Set sprite(s) for Entity State class.
     * 
     * @param cls
     * @param dir
     * @param imgName
     */
    public void set(Class<? extends EntityState> cls, Direction dir, String imgName) {
        this.set(this.spriteIdFromClass(cls), dir, imgName);
    }
    
    /**
     * Set sprite(s) for spriteId.
     * 
     * @param spriteId
     * @param dir
     * @param imgName
     */
    public void set(String spriteId, Direction dir, String imgName) {
        SpriteCollection coll = this.getCollectionById(spriteId, true);
        
        coll.addImage(Images.image(imgName), dir);
    }
    
    /**
     * Set sprite(s) for Entity State class.
     * 
     * @param cls
     * @param imgName
     */
    public void set(Class<? extends EntityState> cls, String imgName) {
        this.set(this.spriteIdFromClass(cls), imgName);
    }
    
    /**
     * Set sprite(s) for spriteId.
     * 
     * @param spriteId
     * @param imgName
     */
    public void set(String spriteId, String imgName) {
        SpriteCollection coll = this.getCollectionById(spriteId, true);
        
        coll.addImage(Images.image(imgName));
    }
    
    /**
     * Get Sprite for Entity State class.
     * 
     * @param cls
     * @param dir
     * @return
     */
    public BufferedImage get(Class<? extends EntityState> cls, Direction dir) {
        return this.get(this.spriteIdFromClass(cls), dir, 0);
    }
    
    /**
     * Get Sprite for spriteId.
     * 
     * @param spriteId
     * @param dir
     * @return
     */
    public BufferedImage get(String spriteId, Direction dir) {
        return this.get(spriteId, dir, 0);
    }
    
    /**
     * Get Sprite for Entity State class.
     * 
     * @param cls
     * @param dir
     * @param progress
     * @return
     */
    public BufferedImage get(Class<? extends EntityState> cls, Direction dir, float progress) {
        return this.get(this.spriteIdFromClass(cls), dir, progress);
    }
    
    /**
     * Get Sprite for spriteId.
     * 
     * @param spriteId
     * @param dir
     * @param progress
     * @return
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
     * @param createIfNull if true and collection is null create a new SpriteCollection
     * @return
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
     * @return
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
                case NORTH: this.imagesNorth.add(image);
                    break;
                case EAST: this.imagesEast.add(image);
                    break;
                case SOUTH: this.imagesSouth.add(image);
                    break;
                case WEST: this.imagesWest.add(image);
                    break;
                default: this.imagesEast.add(image);
            }
        }

        public List<BufferedImage> getImages() {
            return this.getImages(Direction.EAST);
        }

        public List<BufferedImage> getImages(Direction dir) {
            switch (dir) {
                case NORTH: return this.imagesNorth;
                case EAST: return this.imagesEast;
                case SOUTH: return this.imagesSouth;
                case WEST: return this.imagesWest;
                default: return this.imagesEast;
            }
        }
    }
}
