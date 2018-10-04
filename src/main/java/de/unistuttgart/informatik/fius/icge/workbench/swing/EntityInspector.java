/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.List;

import javax.swing.*;

import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.simulation.Entity.EntityEvent;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;

public class EntityInspector {
    
    private Simulation _simulation; 
    private JFrame _frame;
    private List<Entity> _entities;

    private JComboBox<String> _entityChooser;

    private Entity _selectedEntity;
    
    public EntityInspector(Simulation sim) {
        this._simulation = sim;
        this._entities = sim.entities();
    }
    
    public EntityInspector(Simulation sim, int row, int column) {
        this._simulation = sim;
        this._entities = sim.entitiesWith(row, column);
    }
    
    public void show() {
        EventQueue.invokeLater(() -> this.initFrame());
    }

    private void initFrame() {
        this._frame = new JFrame("Entity Inspector");
        this._frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this._frame.setLocationByPlatform(true);
        this._frame.setResizable(true);
        this._frame.setSize(400, 300);
        this._frame.setVisible(true);
        if (this._entityChooser == null) {
            this.startEntitySelection();
        }
    }

    private void startEntitySelection() {
        if (this._entities != null && this._entities.size() < 1) {
            this._frame.getContentPane().add(new JLabel("No Entities..."));
            return;
        }
        this._entityChooser = new JComboBox<>();
        this._entityChooser.addActionListener(e -> {
            int index = this._entityChooser.getSelectedIndex();
            if (index < this._entities.size()) {
                this.setEntity(this._entities.get(index));
            }
        });
        this._entityChooser.setToolTipText("Choos Entity");
        this._entities.forEach(ent -> {
            String name = ent.getClass().getSimpleName();
            WorldObject wob = ent.worldObject();
            name += " (" + wob.row + ", " + wob.column + ")";
            this._entityChooser.addItem(name);
        });
        this._frame.getContentPane().add(BorderLayout.NORTH, this._entityChooser);
        this.setEntity(this._entities.get(0));
    }

    private void setEntity(Entity ent) {
        this._entityChooser.setSelectedIndex(-1);
        this._selectedEntity = ent;
        EventDispatcher.addListener(EntityEvent.class, entity -> {
            if (entity == ent) {
                this.updateEntityValues();
                return true;
            }
            return false;
        });
        this.updateEntityValues();
    }

    private void updateEntityValues() {
        // TODO update UI to reflect changes of Entity
    }
}
