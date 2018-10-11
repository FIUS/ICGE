/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.tools;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.border.BevelBorder;

import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.workbench.swing.ToolBar;

/**
 * This class coordinates all the tools
 * 
 * @author Tim Neumann
 */
public class ToolHandler {
    private final List<Tool> _tools = new ArrayList<>();

    private final List<JButton> _buttons = new ArrayList<>();

    private int _currentTool = 0;

    /**
     * Creates a tool handler.
     */
    public ToolHandler() {
        this.addTool(new SelectionTool());
        this.addTool(new SpawnMarioTool());
        this.addTool(new SpawnCoinTool());
        this.addTool(new DespawnTool());
        this.addTool(new WallCreationTool());
        this.addTool(new WallDemolishingTool());
        resetButtonBorders();
        this._buttons.get(0).setBorder(new BevelBorder(1));
    }

    public ArrayList<JButton> buttons() {
        return new ArrayList<>(this._buttons);
    }

    private void addTool(Tool t) {
        final int index = this._tools.size();
        this._tools.add(t);
        JButton button = t.getToolBarButton();
        button.addActionListener(e -> {
            this._currentTool = index;
            this.resetButtonBorders();
            button.setBorder(new BevelBorder(1));
        });
        this._buttons.add(button);
    }

    private void resetButtonBorders() {
        for (JButton b : this._buttons) {
            b.setBorder(new BevelBorder(0));
        }
    }

    /**
     * Handle a mouse pressed event
     * 
     * @param sim
     *            The simulation the mouse was pressed in
     * @param column
     *            The column the mouse was pressed in
     * @param row
     *            The row the mouse was pressed in
     */
    public void onMousePressed(Simulation sim, int column, int row) {
        Tool t = this._tools.get(this._currentTool);
        if (!(t instanceof AreaTool)) {
            synchronized (sim) {
                t.apply(sim, column, row);
            }
        }
    }

    /**
     * Handle a mouse released event
     * 
     * @param sim
     *            The simulation the mouse was pressed and released in
     * @param startRow
     *            The row the mouse was pressed in
     * @param endRow
     *            The column the mouse was pressed in
     * @param startColumn
     *            The row the mouse was released in
     * @param endColumn
     *            The column the mouse was released in
     */
    public void onMouseReleased(Simulation sim, int startColumn, int endColumn, int startRow, int endRow) {
        Tool t = this._tools.get(this._currentTool);
        if (t instanceof AreaTool) {
            AreaTool at = (AreaTool) t;
            synchronized (sim) {
                at.applyToAll(sim, startColumn, endColumn, startRow, endRow);
            }
        }
    }

    /**
     * @return Whether the current tool is a area tool.
     */
    public boolean currentToolIsAreaTool() {
        Tool t = this._tools.get(this._currentTool);
        return (t instanceof AreaTool);
    }

    /**
     * Calls the `canApply()` method on the current tool
     * 
     * @return The result of the `canApply()` call on the current tool
     */
    public boolean canApply(Simulation sim, int column, int row) {
        return this._tools.get(this._currentTool).canApply(sim, column, row);
    }

    /**
     * Calls the `canApply()` method on the current area tool
     * 
     * @return false if the current tool is not an area tool and otherwise the result of the `canApply()` call on the current
     *         tool
     */
    public boolean canApply(Simulation sim, int startColumn, int endColumn, int startRow, int endRow) {
        Tool t = this._tools.get(this._currentTool);
        if (!(t instanceof AreaTool)) return false;
        return ((AreaTool) t).canApply(sim, startColumn, endColumn, startRow, endRow);
    }

}
