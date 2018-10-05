/*
* This source file is part of the FIUS ICGE project.
* For more information see github.com/neumantm/ICGE
*
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.ArrayList;

import de.unistuttgart.informatik.fius.icge.territory.WorldObject;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Sprite;

/**
 * A entity which is also a collector and has an inventory
 * 
 * @author haslersn, neumantm
 */
public abstract class GreedyEntity extends MovableEntity implements EntityCollector {

    public ArrayList<Sprite> _inventory = new ArrayList<>();

    public GreedyEntity(Simulation sim, Sprite sprite) {
        super(sim, sprite);
    }
    
    private boolean canCollectEntity(Entity ent) throws EntityNotAlive {
        if (!(ent instanceof CollectableEntity) || !ent.worldObject().isSamePos(this.worldObject()))
            return false;
        Sprite sprite = ent.sprite();
        return this.canCollectType(sprite);
    }
    
    private void collectEntity(CollectableEntity ent) throws EntityNotAlive {
        Sprite sprite = ent.worldObject().sprite;
        ent.despawn();
        this._inventory.add(sprite);
        this.collected(sprite);
    }
    
    /**
     * Checks whether this entity can currently collect an entity of the given type
     * 
     * @param type
     *            The type of entity to collect
     * @return Whether this entity can currently collect.
     * @throws EntityNotAlive
     *             When this entity is not alive.
     */
    public boolean canCollect(Sprite type) throws EntityNotAlive {
        synchronized (this.simulation()) {
            for (Entity ent : this.simulation().entities()) {
                if ((ent.worldObject().sprite == type) && canCollectEntity(ent)) return true;
            }
        }
        return false;
    }
    
    /**
     * Checks whether this entity can currently collect an entity.
     * 
     * @return Whether this entity can currently collect.
     * @throws EntityNotAlive
     *             When this entity is not alive.
     */
    public boolean canCollect() throws EntityNotAlive {
        synchronized (this.simulation()) {
            for (Entity ent : this.simulation().entities()) {
                if (canCollectEntity(ent)) return true;
            }
        }
        return false;
    }
    
    public void collect(Sprite type) throws CanNotCollectException, EntityNotAlive {
        this.delayed(() -> {
            for (Entity ent : this.simulation().entities()) {
                if ((ent.worldObject().sprite == type) && canCollectEntity(ent)) {
                    this.collectEntity((CollectableEntity) ent);
                    return;
                }
            }
            throw new CanNotCollectException("No such entity.");
        });
    }
    
    public void collect() throws CanNotCollectException, EntityNotAlive {
        this.delayed(() -> {
            for (Entity ent : this.simulation().entities()) {
                if (canCollectEntity(ent)) {
                    this.collectEntity((CollectableEntity) ent);
                    return;
                }
            }
            throw new CanNotCollectException("No such entity.");
        });
    }

    public boolean canDrop(Sprite type) throws EntityNotAlive {
        if (!this.alive()) throw new EntityNotAlive();
        return this.canDropType(type) && this._inventory.contains(type);
    }
    
    public void drop(Sprite type) throws CanNotDropException, EntityNotAlive {
        this.delayed(() -> {
            if (!this.canDrop(type)) throw new CanNotDropException();
            this._inventory.remove(type);
            WorldObject wob = this.worldObject();
            type.createEntity(this.simulation()).spawn(wob.column, wob.row);
            this.dropped(type);
        });
    }
    
    /**
     * Checks whether an entity of the given type can be collected by the instance, in general.
     * 
     * @param type
     *            The type that should be collected
     * @return Whether the entity can be collected
     */
    abstract boolean canCollectType(Sprite type);
    
    /**
     * Informs the instance that a CollectableEntity has been collected.
     * 
     * @param type
     *            The type of collected entity
     */
    abstract void collected(Sprite type);
    
    /**
     * Checks whether an entity of the given type can be dropped by the instance, in general.
     * 
     * @param type
     *            The type of entity that should be dropped
     * @return Whether the entity can be dropped
     */
    abstract boolean canDropType(Sprite type);
    
    /**
     * Informs the instance that a CollectableEntity has been dropped.
     * 
     * @param type
     *            The type of the dropped entity
     */
    abstract void dropped(Sprite type);
}
