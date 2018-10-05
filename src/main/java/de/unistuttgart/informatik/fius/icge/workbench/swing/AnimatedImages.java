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

import de.unistuttgart.informatik.fius.icge.simulation.EntityType;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

public class AnimatedImages {
    
    private final ArrayList<Entry> entries = new ArrayList<>();
    
    public void set(EntityType type, Direction dir, Collection<String> imgNames) {
        ArrayList<BufferedImage> imgList = new ArrayList<>();
        for (String name : imgNames) {
            imgList.add(Images.image(name));
        }
        this.entries.add(new Entry(type, dir, imgList));
    }
    
    public void set(EntityType type, Collection<String> imgNames) {
        for (Direction dir : Direction.values()) {
            this.set(type, dir, imgNames);
        }
    }
    
    public void set(EntityType type, Direction dir, String imgName) {
        ArrayList<String> list = new ArrayList<>();
        list.add(imgName);
        this.set(type, dir, list);
    }
    
    public void set(EntityType type, String imgName) {
        for (Direction dir : Direction.values()) {
            this.set(type, dir, imgName);
        }
    }
    
    public BufferedImage get(EntityType type, Direction dir) {
        return this.get(type, dir, 0);
    }
    
    public BufferedImage get(EntityType type, Direction dir, float progress) {
        if ((progress < 0) || (progress > 1)) throw new IllegalArgumentException();
        for (Entry entry : this.entries) {
            if ((entry.type == type) && (entry.dir == dir)) {
                int size = entry.imgList.size();
                int index = Math.min((int) (progress * size), size - 1);
                return entry.imgList.get(index);
            }
        }
        return null;
    }
    
    private static class Entry {
        
        public final EntityType type;
        public final Direction dir;
        public final ArrayList<BufferedImage> imgList;
        
        public Entry(EntityType type, Direction dir, ArrayList<BufferedImage> imgList) {
            this.type = type;
            this.dir = dir;
            this.imgList = imgList;
        }
    }
}
