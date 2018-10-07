/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.course;

import de.unistuttgart.informatik.fius.icge.simulation.Simulation;
import de.unistuttgart.informatik.fius.icge.territory.Territory;

abstract public class TaskTemplate {

    protected final Simulation simulation;

    private final String _name;

    public TaskTemplate(Territory initialTty, String name) {
        this.simulation = new Simulation(initialTty);
        this._name = name;
    }

    final public String name() {
        return this._name;
    }

    /** Has to be implemented by the participants */
    public abstract void solve();

    /** The method to verify the solution */
    public abstract void test();

    /**
     * Get's {@link #simulation simulation}
     *
     * @return simulation
     */
    public Simulation getSimulation() {
        return this.simulation;
    }

}
