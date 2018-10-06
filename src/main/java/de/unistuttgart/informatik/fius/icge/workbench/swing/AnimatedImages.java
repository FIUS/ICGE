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

import de.unistuttgart.informatik.fius.icge.territory.EntityState;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;

public class AnimatedImages {
    
    private final ArrayList<Entry> entries = new ArrayList<>();
    
    public void set(Class<? extends EntityState> cls, Direction dir, Collection<String> imgNames) {
        ArrayList<BufferedImage> imgList = new ArrayList<>();
        for (String name : imgNames) {
            imgList.add(Images.image(name));
        }
        this.entries.add(new Entry(cls, dir, imgList));
    }
    
    public void set(Class<? extends EntityState> cls, Collection<String> imgNames) {
        for (Direction dir : Direction.values()) {
            this.set(cls, dir, imgNames);
        }
    }
    
    public void set(Class<? extends EntityState> cls, Direction dir, String imgName) {
        ArrayList<String> list = new ArrayList<>();
        list.add(imgName);
        this.set(cls, dir, list);
    }
    
    public void set(Class<? extends EntityState> cls, String imgName) {
        for (Direction dir : Direction.values()) {
            this.set(cls, dir, imgName);
        }
    }
    
    public BufferedImage get(Class<? extends EntityState> cls, Direction dir) {
        return this.get(cls, dir, 0);
    }
    
    public BufferedImage get(Class<? extends EntityState> cls, Direction dir, float progress) {
        if ((progress < 0) || (progress > 1)) throw new IllegalArgumentException();
        for (Entry entry : this.entries) {
            if ((entry.cls == cls) && (entry.dir == dir)) {
                int size = entry.imgList.size();
                int index = Math.min((int) (progress * size), size - 1);
                return entry.imgList.get(index);
            }
        }
        return null;
    }
    
    private static class Entry {
        
        public final Class<? extends EntityState> cls;
        public final Direction dir;
        public final ArrayList<BufferedImage> imgList;
        
        public Entry(Class<? extends EntityState> cls, Direction dir, ArrayList<BufferedImage> imgList) {
            this.cls = cls;
            this.dir = dir;
            this.imgList = imgList;
        }
    }
}
