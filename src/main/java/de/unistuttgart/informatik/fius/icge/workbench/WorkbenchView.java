/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench;

import de.unistuttgart.informatik.fius.icge.simulation.Simulation;

public interface WorkbenchView {
    
    boolean opened();
    
    Simulation simulation();
    
    void setSimulation(Simulation sim);
    
    double scale();
    
    void setScale(double scale);
    
    double centeredColumn();
    
    void setCenteredColumn(double col);
    
    double centeredRow();
    
    void setCenteredRow(double row);
    
    void setDropDownToolTip(String text);
    
    void addDropDownAction(String name, Runnable action);
    
    void print(String toPrint);
    
    default void println(String toPrint) {
        print(toPrint + '\n');
    }
    
    void clearLog();
    
    void update();
}
