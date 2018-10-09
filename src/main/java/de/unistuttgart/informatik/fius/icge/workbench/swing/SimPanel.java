/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.unistuttgart.informatik.fius.icge.animations.AnimatedTerritory;
import de.unistuttgart.informatik.fius.icge.simulation.Wall.WallState;
import de.unistuttgart.informatik.fius.icge.territory.Territory;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.workbench.tools.ToolHandler;

public class SimPanel extends JPanel {
    private static final long serialVersionUID = 8651840223154690457L;

    private final SwingView _view;
    private Graphics _g;
    private Settings _s;
    private Rectangle _bounds;
    private double _x0, _y0;
    private int _pressX, _pressY, _currentX, _currentY;
    private int _startCol, _startRow, _endCol, _endRow;
    private boolean _mouseDown = false;
    private boolean _mouseInside = false;
    private AnimatedTerritory _animated;
    private ToolHandler _toolHandler;

    public SimPanel(SwingView view, ToolHandler th) {
        this._toolHandler = th;
        this._view = view;
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    SimPanel.this._mouseDown = true;
                    SimPanel.this.updateSettings();
                    if (SimPanel.this._s.animator != null) {
                        SimPanel.this.handleMousePress();
                    }
                    SimPanel.this._view.update();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    SimPanel.this._mouseDown = false;
                    SimPanel.this.updateSettings();
                    if (SimPanel.this._s.animator != null) {
                        SimPanel.this.handleMouseRelease();
                    }
                    SimPanel.this._view.update();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                SimPanel.this._mouseInside = true;
                SimPanel.this._view.update();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                SimPanel.this._mouseInside = false;
                SimPanel.this._view.update();
            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                SimPanel.this._currentX = e.getX();
                SimPanel.this._currentY = e.getY();
                SimPanel.this._view.update();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                SimPanel.this._currentX = SimPanel.this._pressX = e.getX();
                SimPanel.this._currentY = SimPanel.this._pressY = e.getY();
                SimPanel.this._view.update();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        this._g = g;
        this.updateSettings();
        this.updateExtents();
        if (this._s.animator != null) {
            this.drawGrid();
            this.drawWorldObjects();
            this.drawMouseOverlay();
        }
    }

    private void updateSettings() {
        this._s = this._view.settings();
    }

    private void updateExtents() {
        this._bounds = this._g.getClipBounds();
        this._x0 = (0.5 * this._bounds.width) - (this._s.centeredCol * this._s.scale);
        this._y0 = (0.5 * this._bounds.height) - (this._s.centeredRow * this._s.scale);
        this._startCol = this.convertToColumn(Math.min(this._pressX, this._currentX));
        this._startRow = this.convertToRow(Math.min(this._pressY, this._currentY));
        this._endCol = this.convertToColumn(Math.max(this._pressX, this._currentX));
        this._endRow = this.convertToRow(Math.max(this._pressY, this._currentY));
        this._animated = this._s.animator == null ? null : this._s.animator.animated();
    }

    private int convertToColumn(int x) {
        return (int) Math.round((x - this._x0) / this._s.scale);
    }

    private int convertToRow(int y) {
        return (int) Math.round((y - this._y0) / this._s.scale);
    }

    private Territory addConstruction(Territory tty) {
        for (int y = this._startRow; y <= this._endRow; ++y) {
            for (int x = this._startCol; x <= this._endCol; ++x) {
                int captX = x, captY = y;
                if (!tty.containsWith(wob -> (wob.column == captX) && (wob.row == captY))) {
                    tty = tty.add(new WorldObject(new WallState(), x, y));
                }
            }
        }
        return tty;
    }

    private Territory addDemolishing(Territory tty) {
        Image img = Images.image("cross.png");
        for (int y = this._startRow; y <= this._endRow; ++y) {
            for (int x = this._startCol; x <= this._endCol; ++x) {
                int captX = x, captY = y;
                Territory oldTty = tty;
                tty = oldTty.removeIf(
                        wob -> (wob.column == captX) && (wob.row == captY) && (wob.state.getClass() == WallState.class));
                if (tty != oldTty) {
                    this.drawImage(x, y, img);
                }
            }
        }
        return tty;
    }

    private void drawMouseOverlay() {
        if (!this._mouseInside) return;
        int startRow = this._startRow;
        int startCol = this._startCol;
        // When the current tool is not an area tool, we don't want to overlay an area
        if (!this._toolHandler.currentToolIsAreaTool()) {
            startRow = this._endRow;
            startCol = this._endCol;
        }
        for (int row = startRow; row <= this._endRow; ++row) {
            for (int col = startCol; col <= this._endCol; ++col) {
                int l = (int) (this._x0 + (this._s.scale * (col - 0.5f)));
                int r = (int) (this._x0 + (this._s.scale * (col + 0.5f)));
                int t = (int) (this._y0 + (this._s.scale * (row - 0.5f)));
                int b = (int) (this._y0 + (this._s.scale * (row + 0.5f)));

                boolean highlighted;
                if (this._mouseDown) {
                    highlighted = this._toolHandler.canApply(this._s.animator.simulation(), this._startCol, this._endCol,
                            this._startRow, this._endRow);
                } else {
                    highlighted = this._toolHandler.canApply(this._s.animator.simulation(), this._endCol, this._endRow);
                }

                this._g.setColor(highlighted ? new Color(0, 255, 40, 50) : new Color(0, 40, 120, 50));
                this._g.fillRect(l, t, r - l, b - t);
            }
        }
    }

    private void drawGrid() {
        double firstX = remainder(this._x0 - (0.5 * this._s.scale), this._s.scale);
        double firstY = remainder(this._y0 - (0.5 * this._s.scale), this._s.scale);
        for (double x = firstX; x <= this._bounds.width; x += this._s.scale) {
            int ix = (int) x;
            this._g.drawLine(ix, 0, ix, this._bounds.height);
        }
        for (double y = firstY; y <= this._bounds.height; y += this._s.scale) {
            int iy = (int) y;
            this._g.drawLine(0, iy, this._bounds.width, iy);
        }
    }

    private static double remainder(double a, double b) {
        double q = a / b;
        return b * (q - Math.floor(q));
    }

    private void drawWorldObjects() {
        ArrayList<WorldObject> wobs = this._animated.territory().worldObjects();
        if (wobs.isEmpty()) return;
        WorldObject nextWob = wobs.get(0);
        int drawCount = 1;
        for (int i = 1; i <= wobs.size(); ++i) {
            WorldObject lastWob = nextWob;
            drawWorldObject(lastWob);
            if (i != wobs.size()) {
                nextWob = wobs.get(i);
                if (lastWob.state.getClass() == nextWob.state.getClass() && lastWob.isSamePos(nextWob)) {
                    ++drawCount;
                    continue;
                }
            }
            drawCount(lastWob.column, lastWob.row, drawCount);
            drawCount = 1;
        }
    }

    private void drawWorldObject(WorldObject wob) {
        int currentTick = this._s.animator.simulation().tickCount();
        AnimationInterpreter interpreter = new AnimationInterpreter(this._animated, wob, currentTick);

        if (interpreter.inAnimation() && !this._s.animator.simulation().running()) {
            BufferedImage unanimated = interpreter.unanimatedImage();
            BufferedImage grayScale = new BufferedImage(unanimated.getWidth(), unanimated.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            op.filter(unanimated, grayScale);
            this.drawImage(wob.column, wob.row, grayScale);
        }

        BufferedImage img = interpreter.image();
        if (img != null) {
            this.drawImage(interpreter.column(), interpreter.row(), img);
        }
    }

    private void drawCount(int col, int row, int count) {
        if (count > 1) {
            int x = (int) (this._x0 + (this._s.scale * (col - 0.45f)));
            int y = (int) (this._y0 + (this._s.scale * (row + 0.45f)));
            this._g.drawString(String.valueOf(count), x, y);
        }
    }

    private void drawImage(float col, float row, Image img) {
        int l = (int) (this._x0 + (this._s.scale * (col - 0.5f)));
        int r = (int) (this._x0 + (this._s.scale * (col + 0.5f)));
        int t = (int) (this._y0 + (this._s.scale * (row - 0.5f)));
        int b = (int) (this._y0 + (this._s.scale * (row + 0.5f)));
        this._g.drawImage(img, l, t, r - l, b - t, null);
    }

    private void handleMousePress() {
        this._toolHandler.onMousePressed(this._s.animator.simulation(), this._startCol, this._startRow);
    }

    private void handleMouseRelease() {
        this._toolHandler.onMouseReleased(this._s.animator.simulation(), this._startCol, this._endCol, this._startRow,
                this._endRow);
        this._pressX = this._currentX;
        this._pressY = this._currentY;
        this._view.update();
    }
}
