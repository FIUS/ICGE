/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Images {
    
    private static final HashMap<String, BufferedImage> _images = new HashMap<>();
    
    public static BufferedImage image(String imgName) {
        BufferedImage img = _images.get(imgName);
        return img != null ? img : loadImage(imgName);
    }
    
    private static BufferedImage loadImage(String imgName) {
        URL url = Images.class.getClassLoader().getResource(imgName);
        if (url != null) {
            try {
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    _images.put(imgName, img);
                    return img;
                }
            } catch (IOException e) {}
        }
        return null;
    }
}
