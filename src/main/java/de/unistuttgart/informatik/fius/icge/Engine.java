/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import de.unistuttgart.informatik.fius.icge.course.TaskTemplate;
import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionManager;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.workbench.Workbench;

/**
 * The main engine class
 * 
 * @author haslersn, neumantm
 */
public class Engine {
    private static Engine e;
    
    private Workbench _workbench;
    private InspectionManager _inspectionManager;
    
    private boolean started = false;
    
    /**
     * Runs the engine.
     */
    public void start() {
        if (this.started) throw new IllegalStateException("Already started.");
        if (e != null) throw new IllegalStateException("Only one engine should be running at a time.");
        
        e = this;
        
        this.started = true;
        
        this._inspectionManager = new InspectionManager();
        
        this._workbench = new Workbench("Mario-Simulator");
        this._workbench.setDropDownToolTip("Chose action...");
        this._workbench.addDropDownAction("Chose action...", () -> this._workbench.setSimulation(null));
        
        try {
            SolutionLoader.loadSolutions(this::addTask);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private void addTask(Class<? extends TaskTemplate> task) {
        String name = task.getSimpleName();
        this._workbench.addDropDownAction(name, () -> {
            new Thread(() -> {
                try {
                    TaskTemplate obj = task.newInstance();
                    this._workbench.setSimulation(obj.getSimulation());
                    Collection<WorldObject> wobs = obj.getSimulation().territory().worldObjects();
                    Comparator<WorldObject> compCol = (a, b) -> a.column - b.column;
                    Comparator<WorldObject> compRow = (a, b) -> a.row - b.row;
                    int xMin = Collections.min(wobs, compCol).column;
                    int xMax = Collections.max(wobs, compCol).column;
                    int yMin = Collections.min(wobs, compRow).row;
                    int yMax = Collections.max(wobs, compRow).row;
                    this._workbench.setCenteredColumn(0.5f * (xMax + xMin));
                    this._workbench.setCenteredRow(0.5f * (yMax + yMin));
                    try {
                        obj.test();
                    } catch (AssertionError e) {
                        this._workbench.println("Assertion error!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        this._workbench.println(e.toString());
                    }
                    this._workbench.println(name + " completed :-)");
                } catch (InstantiationException | IllegalAccessException e) {
                    this._workbench.println("Internal error!");
                    e.printStackTrace();
                }
            }).start();
        });
    }
    
    /**
     * Get's {@link #_inspectionManager _inspectionManager}
     * 
     * @return _inspectionManager
     */
    public InspectionManager getInspectionManager() {
        return this._inspectionManager;
    }
    
    /**
     * Get's {@link #e e}
     * 
     * @return e
     */
    public static Engine getEngine() {
        return e;
    }
}
