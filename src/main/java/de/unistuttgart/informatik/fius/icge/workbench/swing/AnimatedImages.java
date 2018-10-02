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

import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Direction;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Sprite;

public class AnimatedImages {
    
    private final ArrayList<Entry> entries = new ArrayList<>();
    
    public void set(Sprite sprite, Direction dir, Collection<String> imgNames) {
        ArrayList<BufferedImage> imgList = new ArrayList<>();
        for (String name : imgNames) {
            imgList.add(Images.image(name));
        }
        this.entries.add(new Entry(sprite, dir, imgList));
    }
    
    public void set(Sprite sprite, Collection<String> imgNames) {
        for (Direction dir : Direction.values()) {
            this.set(sprite, dir, imgNames);
        }
    }
    
    public void set(Sprite sprite, Direction dir, String imgName) {
        ArrayList<String> list = new ArrayList<>();
        list.add(imgName);
        this.set(sprite, dir, list);
    }
    
    public void set(Sprite sprite, String imgName) {
        for (Direction dir : Direction.values()) {
            this.set(sprite, dir, imgName);
        }
    }
    
    public BufferedImage get(Sprite sprite, Direction dir) {
        return this.get(sprite, dir, 0);
    }
    
    public BufferedImage get(Sprite sprite, Direction dir, float progress) {
        if ((progress < 0) || (progress > 1)) throw new IllegalArgumentException();
        for (Entry entry : this.entries) {
            if ((entry.sprite == sprite) && (entry.dir == dir)) {
                int size = entry.imgList.size();
                int index = Math.min((int) (progress * size), size - 1);
                return entry.imgList.get(index);
            }
        }
        return null;
    }
    
    private static class Entry {
        
        public final Sprite sprite;
        public final Direction dir;
        public final ArrayList<BufferedImage> imgList;
        
        public Entry(Sprite sprite, Direction dir, ArrayList<BufferedImage> imgList) {
            this.sprite = sprite;
            this.dir = dir;
            this.imgList = imgList;
        }
    }
}
