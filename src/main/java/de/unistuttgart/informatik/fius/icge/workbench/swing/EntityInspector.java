/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import de.unistuttgart.informatik.fius.icge.Engine;
import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Entity.EntityEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionManager;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;

public class EntityInspector {

    private final Set<Class<?>> _editableTypes = new HashSet<>(Arrays.asList(String.class, Integer.TYPE, Integer.class, 
    Double.TYPE, Double.class, Float.TYPE, Float.class, Long.TYPE, Long.class, Boolean.TYPE, Boolean.class));
    
    private final InspectionManager _inspectionManager;
    private final Simulation _simulation;
    private JFrame _frame;
    private JPanel _mainPanel;
    private JPanel _attributePanel;
    private JPanel _methodPanel;
    private final List<Entity> _entities;
    
    private JComboBox<String> _entityChooser;
    
    private Entity _selectedEntity;

    private List<String> _attributeList;
    private Map<String, JLabel> _attributeToLabel;

    private List<String> _methodList;
    
    public EntityInspector(Simulation sim) {
        this._simulation = sim;
        this._entities = sim.entities();
        this._inspectionManager = Engine.getEngine().getInspectionManager();
    }
    
    public EntityInspector(Simulation sim, int row, int column) {
        this._simulation = sim;
        this._entities = sim.entitiesWith(row, column);
        this._inspectionManager = Engine.getEngine().getInspectionManager();
    }
    
    public void show() {
        EventQueue.invokeLater(() -> this.initFrame());
    }
    
    private void initFrame() {
        this._frame = new JFrame("Entity Inspector");
        this._frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this._frame.setLocationByPlatform(true);
        this._frame.setResizable(true);
        this._frame.setSize(400, 300);
        this._frame.setVisible(true);
        this.initMainPanel();
        this.initEntitySelector();
    }

    private void initMainPanel() {
        this._mainPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(this._mainPanel);
        this._frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
    }
    
    private void initEntitySelector() {
        if ((this._entities != null) && (this._entities.size() < 1)) {
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
    
    private void setEntity(final Entity ent) {
        this._selectedEntity = ent;
        EventDispatcher.addListener(EntityEvent.class, event -> {
            EntityEvent ev = (EntityEvent) event;
            if (this._selectedEntity != ent) {
                return false;
            }
            if (ev.entity == ent) {
                this.updateEntityValues();
            }
            return true;
        });
        this.inspectEntity();
        this.updateEntityValues();
    }

    private void inspectEntity() {
        if (this._selectedEntity == null) throw new IllegalStateException("Must set entity before inspecting it.");
        
        if (this._attributePanel != null) {
            this._mainPanel.remove(this._attributePanel);
        }
        if (this._methodPanel != null) {
            this._mainPanel.remove(this._methodPanel);
        }
        
        // attributePanel
        List<String> attributeList = new ArrayList(this._inspectionManager.getAttributeNamesOfEntity(this._selectedEntity));
        attributeList.sort((a1, a2) -> a1.compareToIgnoreCase(a2));
        JPanel attributePanel = new JPanel(new GridLayout(attributeList.size(), 3));
        this._mainPanel.add(BorderLayout.NORTH, attributePanel);
        Map<String, JLabel> attributeToLabel = new HashMap<>();
        for (String attr : attributeList) {
            Class<?> type = this._inspectionManager.getAttributeType(this._selectedEntity, attr);
            attributePanel.add(new JLabel(attr + " (" + type.getSimpleName() + "):"));
            JLabel valueLabel = new JLabel("", JLabel.CENTER);
            attributeToLabel.put(attr, valueLabel);
            attributePanel.add(valueLabel);
            if (this._inspectionManager.isAttributeEditable(this._selectedEntity, attr) 
                    && this._editableTypes.contains(type)) {
                JButton attrEditButton = new JButton("edit");
                attributePanel.add(attrEditButton);
            } else {
                attributePanel.add(new JPanel());
            }
        }
        this._attributePanel = attributePanel;
        this._attributeList = attributeList;
        this._attributeToLabel = attributeToLabel;
        
        // methodPanel
        List<String> methodList = new ArrayList<>();
        methodList.sort((a1, a2) -> a1.compareToIgnoreCase(a2));
        JPanel methodPanel = new JPanel(new GridLayout(methodList.size(), 3));
        for (String method : methodList) {
            // TODO
        }
        this._methodPanel = methodPanel;
        this._methodList = methodList;
    }
    
    private void updateEntityValues() {
        for (String attr : this._attributeList) {
            JLabel valueLabel = this._attributeToLabel.get(attr);
            if (valueLabel == null) continue;
            Object value = this._inspectionManager.getAttributeValue(this._selectedEntity, attr);
            valueLabel.setText(this.objectToString(value));
        }
    }

    /**
     * Get String representation of object.
     * 
     * @param obj
     * @return
     */
    private String objectToString(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) {
            return "\"" + (String) obj + "\"";
        } else if (obj instanceof Array) {
            String result = "[";
            for (Object ob : (Object[]) obj) {
                result += this.objectToString(ob) + ",";
            }
            return result += "]";
        } else if (obj instanceof List<?>) {
            String result = "[";
            for (Object ob : (List<?>) obj) {
                result += this.objectToString(ob) + ",";
            }
            return result += "]";
        } else if (obj instanceof Set<?>) {
            String result = "{";
            for (Object ob : (Set<?>) obj) {
                result += this.objectToString(ob) + ",";
            }
            return result += "}";
        } else if (obj instanceof Map<?,?>) {
            String result = "{";
            for (Object ob : ((Map<?,?>) obj).keySet()) {
                result += this.objectToString(ob) + ": " + this.objectToString(((Map<?,?>) obj).get(ob)) + ",";
            }
            return result += "}";
        } 
        return String.valueOf(obj);
    }
}
