/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import de.unistuttgart.informatik.fius.icge.event.Event;
import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.simulation.Entity.EntityEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.PauseEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.ResumeEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.SimulationEvent;
import de.unistuttgart.informatik.fius.icge.workbench.WorkbenchView;
import de.unistuttgart.informatik.fius.icge.workbench.swing.Images;
import de.unistuttgart.informatik.fius.icge.workbench.swing.ToolBar;

/**
 * This class controls simulation playback
 * 
 */
public class SimulationController {
    private final ToolBar tb;
    private final WorkbenchView wb;

    private final JButton playButton;

    private final JButton stepButton;

    private final Image playImage;
    private final Image pauseImage;

    private boolean isRunning = false;

    
    /**
     * Creates a tool handler.
     * 
     * @param toolBar
     *            The ToolBar to use.
     */
    public SimulationController(ToolBar toolBar, WorkbenchView workBench) {
        this.tb = toolBar;
        this.wb = workBench;
        this.playButton = new JButton();
        this.playButton.addActionListener(this::playButtonPressed);
        this.playButton.setMargin(new Insets(0, 0, 0, 0));
        Image stepImage = Images.image("step.png");
        this.tb.addButton(this.playButton, -1);

        this.stepButton = new JButton();
        this.stepButton.addActionListener(this::stepButtonPressed);
        if (stepImage != null) {
            this.stepButton.setIcon(new ImageIcon(stepImage));
        }
        this.playButton.setToolTipText("Simulate unitl next event");
        this.stepButton.setMargin(new Insets(0, 0, 0, 0));
        this.tb.addButton(this.stepButton, -1);
        
        this.playImage = Images.image("play.png");
        this.pauseImage = Images.image("pause.png");

        this.updatePlayButton();

        EventDispatcher.addListener(PauseEvent.class, this::setPaused);
        EventDispatcher.addListener(ResumeEvent.class, this::setRunning);
    }

    private boolean setRunning(Event ev) {
        SimulationEvent sev = (SimulationEvent) ev;
        if (this.wb.simulation() != sev.simulation) return true;
        this.isRunning = true;
        this.updatePlayButton();
        return true;
    }

    private boolean setPaused(Event ev) {
        SimulationEvent sev = (SimulationEvent) ev;
        if (this.wb.simulation() != sev.simulation) return true;
        this.isRunning = false;
        this.updatePlayButton();
        return true;
    }

    private void updatePlayButton() {
        if (this.isRunning) {
            if (this.pauseImage != null) {
                this.playButton.setIcon(new ImageIcon(this.pauseImage));
            }
            this.playButton.setToolTipText("Pause simulation");
        } else {
            if (this.playImage != null) {
                this.playButton.setIcon(new ImageIcon(this.playImage));
            }
            this.playButton.setToolTipText("Start simulation");
        }
    }

    private void playButtonPressed(ActionEvent e) {
        Simulation sim = this.wb.simulation();
        if (sim == null) return;
        if (sim.running()) {
            sim.pause();
        } else {
            sim.resume();
        }
    }

    private void stepButtonPressed(ActionEvent e) {
        Simulation sim = this.wb.simulation();
        if (sim == null) return;
        if (!sim.running()) {
            sim.resume();
        }
        EventDispatcher.addListener(EntityEvent.class, ev -> {
            sim.pause();
            return false;
        });
    }
    
}
