/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.unistuttgart.informatik.fius.icge.animations.SimulationAnimator;
import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.workbench.WorkbenchView;
import de.unistuttgart.informatik.fius.icge.workbench.tools.SimulationController;
import de.unistuttgart.informatik.fius.icge.workbench.tools.ToolHandler;

public class SwingView implements WorkbenchView {
    
    private JFrame _frame;
    private JTextArea _logArea;
    private ToolBar _toolBar;
    private ToolHandler _toolHandler;
    private SimulationController _simulationController;
    private SimPanel _simPanel;
    private Simulation _simulation;
    private JSlider speedSlider;
    private Settings _settings = new Settings(true, null, 60.f, 0, 0);
    
    public SwingView(String name) {
        EventQueue.invokeLater(() -> this.initFrame(name));
    }
    
    @Override
    public boolean opened() {
        return this._settings.opened;
    }
    
    @Override
    public Simulation simulation() {
        if (this._settings.animator == null) return null;
        return this._settings.animator.simulation();
    }
    
    @Override
    public void setSimulation(Simulation sim) {
        if (sim == null) {
            this._settings = this._settings.setAnimator(null);
        } else {
            this._settings = this._settings.setAnimator(new SimulationAnimator(sim));
            this._simulation = sim;
            speedSlider.addChangeListener(new SliderListener(this._simulation));
        }
        this.update();
    }
    
    @Override
    public double scale() {
        return this._settings.scale;
    }
    
    @Override
    public void setScale(double scale) {
        this._settings = this._settings.setScale(scale);
        this.update();
    }
    
    @Override
    public double centeredColumn() {
        return this._settings.centeredCol;
    }
    
    @Override
    public void setCenteredColumn(double col) {
        this._settings = this._settings.setCenteredColumn(col);
        this.update();
    }
    
    @Override
    public double centeredRow() {
        return this._settings.centeredRow;
    }
    
    @Override
    public void setCenteredRow(double row) {
        this._settings = this._settings.setCenteredRow(row);
        this.update();
    }
    
    @Override
    public void setDropDownToolTip(String text) {
        EventQueue.invokeLater(() -> {
            this._toolBar.setDropDownToolTip(text);
        });
        this.update();
    }
    
    @Override
    public void addDropDownAction(String name, Runnable action) {
        EventQueue.invokeLater(() -> {
            this._toolBar.addDropDownAction(name, action);
        });
        this.update();
    }
    
    @Override
    public void print(String toPrint) {
        EventQueue.invokeLater(() -> {
            this._logArea.append(toPrint);
        });
        this.update();
    }
    
    @Override
    public void clearLog() {
        EventQueue.invokeLater(() -> {
            this._logArea.setText(null);
        });
        this.update();
    }
    
    @Override
    public void update() {
        EventQueue.invokeLater(() -> {
            this._frame.validate();
            this._frame.repaint();
        });
    }
    
    // package private
    
    Settings settings() {
        return this._settings;
    }
    
    // private
    
    private void initFrame(String name) {
        this._frame = new JFrame(name);
        this._frame.setSize((10 * 60) + 400, (5 * 60) + 200);
        this._frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this._frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SwingView.this._settings = SwingView.this._settings.setOpened(false);
            }
        });
        
        this.initToolBar(this._frame);
        this.initToolHandler();
        this.initSimulationController();
        this.initMainPanel(this._frame);
        
        this._frame.setVisible(true);
    }
    
    private void initToolBar(JFrame frame) {
        this._toolBar = new ToolBar(this);
        frame.getContentPane().add(BorderLayout.NORTH, this._toolBar);
    }
    
    private void initToolHandler() {
        this._toolHandler = new ToolHandler(this._toolBar);
    }
    
    private void initSimulationController() {
        this._simulationController = new SimulationController(this._toolBar, this);
    }
    
    private void initMainPanel(JFrame frame) {
        JPanel main = new JPanel(new BorderLayout());
        frame.getContentPane().add(BorderLayout.CENTER, main);
        
        this.initSimPanel(main);
        this.initLogPanel(main);
    }
    
    private void initSimPanel(JPanel main) {
        this._simPanel = new SimPanel(this, this._toolHandler);
        JScrollPane scrollPane = new JScrollPane(this._simPanel);
        main.add(BorderLayout.CENTER, scrollPane);
    }
    
    private void initLogPanel(JPanel main) {
        JPanel logPanel = new JPanel(new BorderLayout());
        this._logArea = new JTextArea(0, 22);
        this._logArea.setEditable(false);
        this._logArea.setLineWrap(true);
        this._logArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(this._logArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        logPanel.add(scrollPane);
        
        JButton clearLogButton = new JButton("clear log");
        clearLogButton.addActionListener(e -> this.clearLog());
        logPanel.add(BorderLayout.SOUTH, clearLogButton);

        JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.add(logPanel);

        subPanel.add(BorderLayout.PAGE_END, initSpeedSlider());

        main.add(BorderLayout.EAST, subPanel);
    }

    private JSlider initSpeedSlider() {
        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 30, 25);

        speedSlider.setMajorTickSpacing(5);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setInverted(true);
        return speedSlider;
    }

    /**
     * Listener for the speed changing slider and setting the speed for each entity
     * @param Simulation
     */
    class SliderListener implements ChangeListener {
        private Simulation simulation;
        public SliderListener (Simulation sim)
        {
            this.simulation = sim;
        }
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider)e.getSource();
            if (!source.getValueIsAdjusting()) {
                int currentTickCount = source.getValue();
                if (currentTickCount == 0) // can not be 0, but is available due to nicer layout
                    currentTickCount = 1;
                for (Entity entity : this.simulation.getEntities()) {
                    entity.setDelay(currentTickCount);
                }
            }
        }
    }

}
