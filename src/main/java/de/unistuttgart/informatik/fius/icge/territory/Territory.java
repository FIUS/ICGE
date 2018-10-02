/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.territory;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Territory {

    private ArrayList<WorldObject> _worldObjects = new ArrayList<>();

    public Territory() {}

    public Territory(Territory other) {
        this._worldObjects = new ArrayList<>(other._worldObjects);
        this._worldObjects.sort((wo1, wo2) -> wo1.compareTo(wo2));
    }

    public Territory add(WorldObject wob) {
        Territory result = new Territory(this);
        result._worldObjects.add(wob);
        result._worldObjects.sort((wo1, wo2) -> wo1.compareTo(wo2));
        return result;
    }

    public Territory replace(WorldObject oldWob, WorldObject newWob) {
        if (newWob == null) {
            System.out.println();
        }
        int index = this._worldObjects.indexOf(oldWob);
        if (index == -1) throw new IllegalArgumentException();
        Territory result = new Territory(this);
        result._worldObjects.set(index, newWob);
        result._worldObjects.sort((wo1, wo2) -> wo1.compareTo(wo2));
        return result;
    }

    public Territory remove(WorldObject wob) {
        Territory result = new Territory(this);
        if (!result._worldObjects.remove(wob)) throw new IllegalArgumentException();
        result._worldObjects.sort((wo1, wo2) -> wo1.compareTo(wo2));
        return result;
    }

    public Territory removeIf(Predicate<WorldObject> pred) {
        Territory result = new Territory(this);
        result._worldObjects.removeIf(pred);
        result._worldObjects.sort((wo1, wo2) -> wo1.compareTo(wo2));
        return result._worldObjects.size() == this._worldObjects.size() ? this : result; // return this if nothing changed
    }

    public ArrayList<WorldObject> worldObjects() {
        return new ArrayList<>(this._worldObjects);
    }

    public ArrayList<WorldObject> worldObjectsWith(Predicate<WorldObject> pred) {
        ArrayList<WorldObject> result = new ArrayList<>();
        this._worldObjects.forEach(wob -> {
            if (pred.test(wob)) {
                result.add(wob);
            }
        });
        return result;
    }

    public boolean contains(WorldObject wob) {
        return this._worldObjects.contains(wob);
    }

    public boolean containsWith(Predicate<WorldObject> pred) {
        return this._worldObjects.stream().filter(pred).findFirst().isPresent();
    }

    public void forEach(Consumer<WorldObject> consumer) {
        this._worldObjects.forEach(consumer);
    }
}
