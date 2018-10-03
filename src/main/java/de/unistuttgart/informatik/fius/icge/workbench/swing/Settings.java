/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import de.unistuttgart.informatik.fius.icge.animations.SimulationAnimator;

class Settings {
    
    public final boolean opened;
    public final SimulationAnimator animator;
    public final double scale;
    public final double centeredCol;
    public final double centeredRow;
    
    public Settings(boolean opened, SimulationAnimator animator, double scale, double centeredCol, double centeredRow) {
        this.opened = opened;
        this.animator = animator;
        this.scale = scale;
        this.centeredCol = centeredCol;
        this.centeredRow = centeredRow;
    }
    
    public Settings setOpened(boolean opened) {
        return new Settings(opened, this.animator, this.scale, this.centeredCol, this.centeredRow);
    }
    
    public Settings setAnimator(SimulationAnimator animator) {
        return new Settings(this.opened, animator, this.scale, this.centeredCol, this.centeredRow);
    }
    
    public Settings setScale(double scale) {
        return new Settings(this.opened, this.animator, scale, this.centeredCol, this.centeredRow);
    }
    
    public Settings setCenteredColumn(double centeredCol) {
        return new Settings(this.opened, this.animator, this.scale, centeredCol, this.centeredRow);
    }
    
    public Settings setCenteredRow(double centeredRow) {
        return new Settings(this.opened, this.animator, this.scale, this.centeredCol, centeredRow);
    }
}
