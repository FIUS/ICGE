/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

public class ToolBar extends JToolBar {
    private static final long serialVersionUID = 224051849272358057L;
    
    private final SwingView _view;
    private final JComboBox<String> _dropDown = new JComboBox<>();
    private final ArrayList<Runnable> _dropDownActions = new ArrayList<>();
    
    public ToolBar(SwingView view) {
        this._view = view;
        this.setMargin(new Insets(1, 1, 0, 0));
        this.setFloatable(false);
        this._dropDown.setVisible(false);
        this._dropDown.addActionListener(e -> {
            int index = this._dropDown.getSelectedIndex();
            if (index < this._dropDownActions.size()) {
                this._dropDownActions.get(index).run();
            }
        });
        this.add(this._dropDown, -1);
    }
    
    public void setDropDownToolTip(String text) {
        this._dropDown.setToolTipText(text);
    }
    
    public void addDropDownAction(String name, Runnable callback) {
        this._dropDown.setVisible(true);
        this._dropDown.addItem(name);
        this._dropDownActions.add(callback);
    }
    
    /**
     * Adds a button to this toolbar.
     * 
     * @param button
     *            The button to add.
     * @param position
     *            The position of the button
     */
    public void addButton(JButton button, int position) {
        this.add(button, position);
    }
}
