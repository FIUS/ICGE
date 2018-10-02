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
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.workbench.Workbench;

public class Engine {
    
    private Workbench _workbench;
    
    /**
     * Runs the engine.
     */
    public void start() {
        _workbench = new Workbench("Mario-Simulator");
        _workbench.setDropDownToolTip("Chose action...");
        _workbench.addDropDownAction("Chose action...", () -> _workbench.setSimulation(null));
        
        try {
            SolutionLoader.loadSolutions(this::addTask);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void addTask(Class<? extends TaskTemplate> task) {
        String name = task.getSimpleName();
        _workbench.addDropDownAction(name, () -> {
            new Thread(() -> {
                try {
                    TaskTemplate obj = task.newInstance();
                    _workbench.setSimulation(obj.getSimulation());
                    Collection<WorldObject> wobs = obj.getSimulation().territory().worldObjects();
                    Comparator<WorldObject> compCol = (a, b) -> a.column - b.column;
                    Comparator<WorldObject> compRow = (a, b) -> a.row - b.row;
                    int xMin = Collections.min(wobs, compCol).column;
                    int xMax = Collections.max(wobs, compCol).column;
                    int yMin = Collections.min(wobs, compRow).row;
                    int yMax = Collections.max(wobs, compRow).row;
                    _workbench.setCenteredColumn(0.5f * (xMax + xMin));
                    _workbench.setCenteredRow(0.5f * (yMax + yMin));
                    obj.test();
                    _workbench.println(name + " completed :-)");
                } catch (AssertionError e) {
                    _workbench.println("Assertion error!");
                } catch (InstantiationException | IllegalAccessException e) {
                    _workbench.println("Internal error!");
                    e.printStackTrace();
                }
            }).start();
        });
    }
}
