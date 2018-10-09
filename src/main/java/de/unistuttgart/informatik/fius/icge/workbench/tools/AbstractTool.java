/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import de.unistuttgart.informatik.fius.icge.workbench.swing.Images;

/**
 * An abstract tool, which implements the creating of the button
 * 
 * @author Tim Neumann
 */
public abstract class AbstractTool implements Tool {

    /** The description of the tool. */
    private String toolTip;
    /** The name of the image for the tool. */
    private String imgName;

    /**
     * Creates a new abstract tool with the given description and image name
     * 
     * @param dToolTip
     *            The description displayed as the tool tip.
     * @param dImgName
     *            The name of the image to use for the button.
     */
    public AbstractTool(String dToolTip, String dImgName) {
        this.toolTip = dToolTip;
        this.imgName = dImgName;
    }

    @Override
    public JButton getToolBarButton() {
        JButton button = new JButton();

        Image img = this.imgName == null ? null : Images.image(this.imgName);
        if (img != null) {
            button.setIcon(new ImageIcon(img));
        }

        button.setMargin(new Insets(0, 0, 0, 0));
        button.setToolTipText(this.toolTip);
        return button;
    }
}
