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
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import javax.swing.*;

import de.unistuttgart.informatik.fius.icge.Engine;
import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.simulation.Entity;
import de.unistuttgart.informatik.fius.icge.simulation.Entity.EntityEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionManager;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;

/**
 * Window to inspect and manipulate Entities.
 */
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
    private Map<String, JLabel> _methodToLabel;
    
    /**
     * Create new entity inspector which chooses from all available entities
     * 
     * @param sim
     */
    public EntityInspector(Simulation sim) {
        this._simulation = sim;
        this._entities = sim.entities();
        this._inspectionManager = Engine.getEngine().getInspectionManager();
    }
    
    /**
     * Create new entity inspector which chooses from all available entities in (row, column)
     * 
     * @param sim
     * @param row
     * @param column
     */
    public EntityInspector(Simulation sim, int row, int column) {
        this._simulation = sim;
        this._entities = sim.entitiesWith(row, column);
        this._inspectionManager = Engine.getEngine().getInspectionManager();
    }
    
    /** Show the entity inspector window */
    public void show() {
        EventQueue.invokeLater(() -> this.initFrame());
    }
    
    /** Build the main frame */
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
        this._mainPanel = new JPanel(new GridLayout(2, 1));
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
    
    /**
     * Set the specific entity to inspect
     * 
     * @param ent
     */
    private void setEntity(Entity ent) {
        this._selectedEntity = ent;
        this.inspectEntity();
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
        List<String> attributeList = new ArrayList<>(this._inspectionManager.getAttributeNamesOfEntity(this._selectedEntity));
        attributeList.sort((a1, a2) -> a1.compareToIgnoreCase(a2));
        JPanel attributePanel = new JPanel(new GridLayout(attributeList.size(), 3));
        this._mainPanel.add(attributePanel);
        Map<String, JLabel> attributeToLabel = new HashMap<>();
        for (final String attr : attributeList) {
            Class<?> type = this._inspectionManager.getAttributeType(this._selectedEntity, attr);
            attributePanel.add(new JLabel(attr + " (" + type.getSimpleName() + "):"));
            JLabel valueLabel = new JLabel("", SwingConstants.CENTER);
            attributeToLabel.put(attr, valueLabel);
            attributePanel.add(valueLabel);
            if (this._inspectionManager.isAttributeEditable(this._selectedEntity, attr) && this._editableTypes.contains(type)) {
                JButton attrEditButton = new JButton("edit");
                attrEditButton.addActionListener(l -> {
                    EventQueue.invokeLater(() -> this.editAttribute(attr));
                });
                attributePanel.add(attrEditButton);
            } else {
                attributePanel.add(new JPanel());
            }
        }
        this._attributePanel = attributePanel;
        this._attributeList = attributeList;
        this._attributeToLabel = attributeToLabel;
        
        // methodPanel
        List<String> methodList = new ArrayList<>(this._inspectionManager.getMethodNamesOfEntity(this._selectedEntity));
        methodList.sort((a1, a2) -> a1.compareToIgnoreCase(a2));
        JPanel methodPanel = new JPanel(new GridLayout(methodList.size(), 3));
        this._mainPanel.add(methodPanel);
        Map<String, JLabel> methodToLabel = new HashMap<>();
        for (String method : methodList) {
            Method methodDetail = this._inspectionManager.getMethodDetail(this._selectedEntity, method);
            String methodName = method + "(";
            Parameter[] paramDetails = methodDetail.getParameters();
            List<String> params = new ArrayList<>(paramDetails.length);
            boolean paramsArePrimitive = true;
            for (Parameter param : paramDetails) {
                if (!this._editableTypes.contains(param.getType())) {
                    paramsArePrimitive = false;
                }
                params.add(param.getType().getSimpleName() + " " + param.getName());
            }
            methodName += String.join(", ", params);
            methodName += "): " + methodDetail.getReturnType().getSimpleName();
            methodPanel.add(new JLabel(methodName));
            if (paramsArePrimitive) {
                JButton methodCallButton = new JButton("call");
                methodCallButton.addActionListener(l -> {
                    EventQueue.invokeLater(() -> this.callMethod(method));
                });
                methodPanel.add(methodCallButton);
            } else {
                methodPanel.add(new JPanel());
            }
            JLabel valueLabel = new JLabel("", SwingConstants.CENTER);
            methodToLabel.put(method, valueLabel);
            methodPanel.add(valueLabel);
        }
        this._methodPanel = methodPanel;
        this._methodList = methodList;
        this._methodToLabel = methodToLabel;
    }
    
    /** Update UI on value changes in entity */
    private void updateEntityValues() {
        for (String attr : this._attributeList) {
            JLabel valueLabel = this._attributeToLabel.get(attr);
            if (valueLabel == null) continue;
            Object value = this._inspectionManager.getAttributeValue(this._selectedEntity, attr);
            valueLabel.setText(this.objectToString(value));
        }
    }

    private void editAttribute(String attr) {
        Class<?> type = this._inspectionManager.getAttributeType(this._selectedEntity, attr);

        Object userInput = null;
        if (type == Integer.TYPE || type == Integer.class) {
            userInput = getUserInt("Edit attribute \"" + attr + "\"", "Input new value:");
        } else if (type == Long.TYPE || type == Long.class) {
            userInput = getUserLong("Edit attribute \"" + attr + "\"", "Input new value:");
        } else if (type == Float.TYPE || type == Float.class) {
            userInput = getUserFloat("Edit attribute \"" + attr + "\"", "Input new value:");
        } else if (type == Double.TYPE || type ==  Double.class) {
            userInput = getUserDouble("Edit attribute \"" + attr + "\"", "Input new value:");
        } else if (type == String.class) {
            userInput = getUserInput("Edit attribute \"" + attr + "\"", "Input new value:");
        }
        if (userInput == null) return;
        this._inspectionManager.setAttributeValue(this._selectedEntity, attr, userInput);
        this.updateEntityValues();
    }

    private void callMethod(String method) {
        Method methodDetail = this._inspectionManager.getMethodDetail(this._selectedEntity, method);
        Parameter[] params = methodDetail.getParameters();

        Object[] parameterValues = new Object[params.length];
        for (int i = 0; i < params.length; ++i) {
            Class<?> type = params[i].getType();

            Object userInput = null;
            if (type == Integer.TYPE || type == Integer.class) {
                userInput = getUserInt("Input Parameter \"" + params[i].getName() + "\"", "Input parameter value:");
            } else if (type == Long.TYPE || type == Long.class) {
                userInput = getUserLong("Input Parameter \"" + params[i].getName() + "\"", "Input parameter value:");
            } else if (type == Float.TYPE || type == Float.class) {
                userInput = getUserFloat("Input Parameter \"" + params[i].getName() + "\"", "Input parameter value:");
            } else if (type == Double.TYPE || type ==  Double.class) {
                userInput = getUserDouble("Input Parameter \"" + params[i].getName() + "\"", "Input parameter value:");
            } else if (type == String.class) {
                userInput = getUserInput("Input Parameter \"" + params[i].getName() + "\"", "Input parameter value:");
            }
            if (userInput == null) return;
            parameterValues[i] = userInput;
        }

        new Thread(() -> {
            try {
                Object result = this._inspectionManager.invokeMethod(this._selectedEntity, method, parameterValues);
                JLabel valueLabel = this._methodToLabel.get(method);
                if (valueLabel != null) {
                    valueLabel.setText(this.objectToString(result));
                }
            } catch (IllegalStateException e) {}
            this.updateEntityValues();
        }, "Inspector (Call " + method + ")").start();
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
        } else if (obj instanceof Map<?, ?>) {
            String result = "{";
            for (Object ob : ((Map<?, ?>) obj).keySet()) {
                result += this.objectToString(ob) + ": " + this.objectToString(((Map<?, ?>) obj).get(ob)) + ",";
            }
            return result += "}";
        }
        return String.valueOf(obj);
    }
    
    /**
     * Open user input dialog
     * 
     * @param title
     *            title of dialog
     * @param description
     *            description of dialog
     * @return user input
     */
    private String getUserInput(String title, String description) {
        String s = JOptionPane.showInputDialog(this._frame, description, title, JOptionPane.PLAIN_MESSAGE);
        return s;
    }
    
    /**
     * Open user input dialog
     * 
     * @param title
     *            title of dialog
     * @param description
     *            description of dialog
     * @return user input
     */
    private Integer getUserInt(String title, String description) {
        String input = this.getUserInput(title, description);
        if (input == null) return null;
        try {
            return new Integer(Integer.parseInt(input));
        } catch (NumberFormatException e) {}
        return new Integer(0);
    }
    
    /**
     * Open user input dialog
     * 
     * @param title
     *            title of dialog
     * @param description
     *            description of dialog
     * @return user input
     */
    private Long getUserLong(String title, String description) {
        String input = this.getUserInput(title, description);
        if (input == null) return null;
        try {
            return new Long(Long.parseLong(input));
        } catch (NumberFormatException e) {}
        return new Long(0);
    }
    
    /**
     * Open user input dialog
     * 
     * @param title
     *            title of dialog
     * @param description
     *            description of dialog
     * @return user input
     */
    private Float getUserFloat(String title, String description) {
        String input = this.getUserInput(title, description);
        if (input == null) return null;
        try {
            return new Float(Float.parseFloat(input));
        } catch (NumberFormatException e) {}
        return new Float(0);
    }
    
    /**
     * Open user input dialog
     * 
     * @param title
     *            title of dialog
     * @param description
     *            description of dialog
     * @return user input
     */
    private Double getUserDouble(String title, String description) {
        String input = this.getUserInput(title, description);
        if (input == null) return null;
        try {
            return new Double(Double.parseDouble(input));
        } catch (NumberFormatException e) {}
        return new Double(0);
    }
    
    /**
     * Open user input dialog
     * 
     * @param title
     *            title of dialog
     * @param description
     *            description of dialog
     * @return user input
     */
    private Boolean getUserBoolean(String title, String description) {
        String input = this.getUserInput(title, description);
        if (input == null) return null;
        return new Boolean(input.equals("true"));
    }
}
