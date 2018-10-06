/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.course;

import de.unistuttgart.informatik.fius.icge.simulation.EntityType;
import de.unistuttgart.informatik.fius.icge.territory.Editor;
import de.unistuttgart.informatik.fius.icge.territory.Territory;

public class Presets {
    public static Editor cage(int width, int height) {

        Editor ed = new Editor(new Territory());

        for (int x = -1; x <= width; ++x) {
            ed.add(EntityType.WALL, x, -1);
            ed.add(EntityType.WALL, x, height);
        }

        for (int y = 0; y < height; ++y) {
            ed.add(EntityType.WALL, -1, y);
            ed.add(EntityType.WALL, width, y);
        }

        return ed;
    }
    
    /**
     * This function generates an Editor with a array defined map with coins.
     * A -1 in a Cell indicates a wall
     * A 0 indicates nothing
     * And 1 upwords indicates the number of coins in that field.
     *  
     * @param map the array containing a predefined map
     * @return returns an Editor to add further work on a map
     */
    public static Editor loadFromArray(int[][] map) {
        return loadFromArray(map, -1, -1);
    }
    
    /**
     * This function generates an Editor with a array defined map with coins.
     * A -1 in a Cell indicates a wall
     * A 0 indicates nothing
     * And 1 upwords indicates the number of coins in that field.
     *  
     * @param map the array containing a predefined map
     * @param offsetX a X offset applied to the final map
     * @param offsetY a Y offset applied to the final map
     * @return returns an Editor to add further work on a map
     */
    public static Editor loadFromArray(int[][] map, int offsetX, int offsetY) {
        Editor ed = new Editor(new Territory());
        
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == -1) {
                    ed.add(EntityType.WALL, x + offsetX, y + offsetY);
                } else if (map[y][x] > 0) {
                    for (int i = 0; i < map[y][x]; i++) {
                        ed.add(EntityType.COIN, x + offsetX, y + offsetY);
                    }
                }
            }
        }
        
        return ed;
    }
}
