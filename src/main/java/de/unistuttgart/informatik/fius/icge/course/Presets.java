/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.course;

import de.unistuttgart.informatik.fius.icge.simulation.Coin.CoinState;
import de.unistuttgart.informatik.fius.icge.simulation.Wall.WallState;
import de.unistuttgart.informatik.fius.icge.territory.Editor;
import de.unistuttgart.informatik.fius.icge.territory.Territory;

/**
 * Class containing a few functions to easily add some structures to a Editor.
 */
public class Presets {
    
    /**
     * Function to generate a cage around your playing field
     * 
     * @param width inner width of the cage
     * @param height inner height of the cage
     * @return returns a {@link de.unistuttgart.informatik.fius.icge.territory.Editor}
     */
    public static Editor cage(int width, int height) {
        return cage(new Editor(new Territory()), width, height);
    }
    
    /**
     * Function to generate a cage around your playing field
     * 
     * @param ed a existing editor to add the cage to
     * @param width inner width of the cage
     * @param height inner height of the cage
     * @return returns a {@link de.unistuttgart.informatik.fius.icge.territory.Editor}
     */
    public static Editor cage(Editor ed, int width, int height) {
        for (int x = -1; x <= width; ++x) {
            ed.add(new WallState(), x, -1);
            ed.add(new WallState(), x, height);
        }

        for (int y = 0; y < height; ++y) {
            ed.add(new WallState(), -1, y);
            ed.add(new WallState(), width, y);
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
     * @return returns a {@link de.unistuttgart.informatik.fius.icge.territory.Editor}
     */
    public static Editor loadFromArray(int[][] map) {
        return loadFromArray(new Editor(new Territory()), map, -1, -1);
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
     * @return returns a {@link de.unistuttgart.informatik.fius.icge.territory.Editor}
     */
    public static Editor loadFromArray(int[][] map, int offsetX, int offsetY) {
        return loadFromArray(new Editor(new Territory()), map, offsetX, offsetY);
    }
    
    /**
     * This function generates an Editor with a array defined map with coins.
     * A -1 in a Cell indicates a wall
     * A 0 indicates nothing
     * And 1 upwords indicates the number of coins in that field.
     *  
     * @param ed a existing editor to add the cage to
     * @param map the array containing a predefined map
     * @return returns a {@link de.unistuttgart.informatik.fius.icge.territory.Editor}
     */
    public static Editor loadFromArray(Editor ed, int[][] map) {
        return loadFromArray(ed, map, -1, -1);
    }
    
    /**
     * This function generates an Editor with a array defined map with coins.
     * A -1 in a Cell indicates a wall
     * A 0 indicates nothing
     * And 1 upwords indicates the number of coins in that field.
     *  
     * @param ed a existing editor to add the cage to
     * @param map the array containing a predefined map
     * @param offsetX a X offset applied to the final map
     * @param offsetY a Y offset applied to the final map
     * @return returns a {@link de.unistuttgart.informatik.fius.icge.territory.Editor}
     */
    public static Editor loadFromArray(Editor ed, int[][] map, int offsetX, int offsetY) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == -1) {
                    ed.add(new WallState(), x + offsetX, y + offsetY);
                } else if (map[y][x] > 0) {
                    for (int i = 0; i < map[y][x]; i++) {
                        ed.add(new CoinState(), x + offsetX, y + offsetY);
                    }
                }
            }
        }
        
        return ed;
    }
}
