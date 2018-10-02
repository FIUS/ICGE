/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import de.unistuttgart.informatik.fius.icge.workbench.swing.Settings.State;

public class ToolBar extends JToolBar {
    private static final long serialVersionUID = 224051849272358057L;
    
    private final SwingView _view;
    private final JComboBox<String> _dropDown = new JComboBox<>();
    private final ArrayList<Runnable> _dropDownActions = new ArrayList<>();
    
    public ToolBar(SwingView view) {
        this._view = view;
        this.setMargin(new Insets(1, 1, 0, 0));
        this.setFloatable(false);
        this.addButton("mario/mario-east-0.png", State.SPAWN_MARIO, "Spawn a mario");
        this.addButton("cross.png", State.DESPAWN, "Delete");
        this.addButton("wall/wall-default.png", State.CONSTRUCT, "Construct wall");
        this.addButton("wall/wall-delete.png", State.DEMOLISH, "Demolish");
        this._dropDown.setVisible(false);
        this._dropDown.addActionListener(e -> {
            int index = this._dropDown.getSelectedIndex();
            if (index < this._dropDownActions.size()) {
                this._dropDownActions.get(index).run();
            }
        });
        this.add(this._dropDown);
    }
    
    public void setDropDownToolTip(String text) {
        this._dropDown.setToolTipText(text);
    }
    
    public void addDropDownAction(String name, Runnable callback) {
        this._dropDown.setVisible(true);
        this._dropDown.addItem(name);
        this._dropDownActions.add(callback);
    }
    
    private JButton addButton(String imgName, State state, String toolTip) {
        return this.addCustomButton(imgName, state, e -> {
            this._view.setState(this._view.settings().state != state ? state : State.DEFAULT);
        }, toolTip);
    }
    
    private JButton addCustomButton(String imgName, State state, ActionListener listener, String toolTip) {
        JButton button = new JButton() {
            private static final long serialVersionUID = -1620804291688575612L;
            
            @Override
            public void paint(Graphics g) {
                this.setSelected(ToolBar.this._view.settings().state == state);
                super.paint(g);
            }
        };
        Image img = imgName == null ? null : Images.image(imgName);
        if (img != null) {
            button.setIcon(new ImageIcon(img));
        }
        button.addActionListener(listener);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setToolTipText(toolTip);
        this.add(button);
        return button;
    }
}
