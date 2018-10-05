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
}
