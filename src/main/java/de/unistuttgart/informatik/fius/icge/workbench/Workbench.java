/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.workbench;

import de.unistuttgart.informatik.fius.icge.event.Event;
import de.unistuttgart.informatik.fius.icge.event.EventDispatcher;
import de.unistuttgart.informatik.fius.icge.event.EventHandler;
import de.unistuttgart.informatik.fius.icge.event.EventListener;
import de.unistuttgart.informatik.fius.icge.simulation.Entity.DespawnEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Entity.EntityEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Entity.MessageEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Entity.SpawnEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Entity.TeleportEvent;
import de.unistuttgart.informatik.fius.icge.simulation.MovableEntity.MoveEvent;
import de.unistuttgart.informatik.fius.icge.simulation.MovableEntity.TurnLeftEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.PauseEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.ResumeEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.SetTerritoryEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.SimulationEvent;
import de.unistuttgart.informatik.fius.icge.simulation.Simulation.TickEvent;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.workbench.swing.SwingView;

public class Workbench {
    
    private final String _name;
    private final WorkbenchView _view;
    private EventHandler _eventHandler = new EventHandler();

    public Workbench(String name) {
        this._name = name;
        this._view = new SwingView(name);

        this._eventHandler.addListener(SimulationEvent.class, this::handle);
    }
    
    public boolean opened() {
        return this._view.opened();
    }
    
    public String name() {
        return this._name;
    }
    
    public Simulation simulation() {
        return this._view.simulation();
    }
    
    public void setSimulation(Simulation sim) {
        this._view.setSimulation(sim);
        EventDispatcher.raise(new SetSimulationEvent(this._view));
    }
    
    public double scale() {
        return this._view.scale();
    }
    
    public void setScale(double scale) {
        this._view.setScale(scale);
    }
    
    public double centeredColumn() {
        return this._view.centeredColumn();
    }
    
    public void setCenteredColumn(double col) {
        this._view.setCenteredColumn(col);
    }
    
    public double centeredRow() {
        return this._view.centeredRow();
    }
    
    public void setCenteredRow(double row) {
        this._view.setCenteredRow(row);
    }
    
    public void setDropDownToolTip(String text) {
        this._view.setDropDownToolTip(text);
    }
    
    public void addDropDownAction(String name, Runnable action) {
        this._view.addDropDownAction(name, action);
    }
    
    public void println(String toPrint) {
        this._view.println(toPrint);
    }
    
    public void print(String toPrint) {
        this._view.print(toPrint);
    }
    
    // private
    
    private boolean handle(Event ev) {
        if (!this._view.opened()) return false;
        if (this.simulation() != ((SimulationEvent) ev).simulation) return true;
        if (ev instanceof SpawnEvent) {
            WorldObject wob = ((SpawnEvent) ev).entity.worldObject();
            this._view.println(((EntityEvent) ev).entity.getClass().getSimpleName() + " :: spawn(" + wob.column + ", " + wob.row
                    + ", " + wob.direction + ");");
        } else if (ev instanceof DespawnEvent) {
            this._view.println(((EntityEvent) ev).entity.getClass().getSimpleName() + " :: despawn();");
        } else if (ev instanceof MoveEvent) {
            this._view.println(((EntityEvent) ev).entity.getClass().getSimpleName() + " :: move();");
        } else if (ev instanceof TeleportEvent) {
            this._view.println(((EntityEvent) ev).entity.getClass().getSimpleName() + " :: teleport();");
        } else if (ev instanceof TurnLeftEvent) {
            this._view.println(((EntityEvent) ev).entity.getClass().getSimpleName() + " :: turnLeft();");
        } else if (ev instanceof SetTerritoryEvent) {
            this._view.println("Simulation :: setTerritory(...)");
        } else if (ev instanceof TickEvent) {
            int tickCount = ((TickEvent) ev).tickCount;
            if ((tickCount % 6000) == 0) {
                this._view.println("Simulation :: tickCount() == " + tickCount);
            }
        } else if (ev instanceof PauseEvent) {
            this._view.println("Simulation :: pause()");
        } else if (ev instanceof ResumeEvent) {
            this._view.println("Simulation :: resume()");
        } else if (ev instanceof MessageEvent) {
            MessageEvent mev = (MessageEvent) ev;
            this._view.println(mev.entity.getClass().getSimpleName() + " :: print");
            this._view.print(" > " + mev.message);
        } else {
            this._view.println("- unknown -");
        }
        this._view.update();
        return true;
    }

    // Events

    /**
     * Intentionally doesn't extend SimulationEvent
     */
    public static class SetSimulationEvent implements Event {
        public final WorkbenchView view;

        public SetSimulationEvent(WorkbenchView view) {
            this.view = view;
        }
    }
}
