/*
* This source file is part of the FIUS ICGE project.
* For more information see github.com/neumantm/ICGE
*
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import java.util.ArrayList;

import de.unistuttgart.informatik.fius.icge.simulation.inspection.InspectionAttribute;
import de.unistuttgart.informatik.fius.icge.territory.EntityState;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject;

/**
 * A entity which is also a collector and has an inventory
 * 
 * @author haslersn, neumantm
 */
public abstract class GreedyEntity extends MovableEntity implements EntityCollector {

    public abstract static class GreedyEntityState implements EntityState {
        protected final ArrayList<Entity> inventory;

        public GreedyEntityState(ArrayList<Entity> inventory) {
            this.inventory = inventory;
        }

        @Override
        public boolean isSolid() {
            return false;
        }
    }

    
    @InspectionAttribute(readOnly = true, name = "Inventory")
    protected final ArrayList<Entity> _inventory;

    public GreedyEntity(Simulation sim, ArrayList<Entity> inventory) {
        super(sim);
        this._inventory = inventory;
    }
    
    protected boolean canCollectEntity(Entity ent) throws EntityNotAlive {
        return (ent instanceof CollectableEntity) && canCollectType(ent.getClass()) && ent.worldObject().isSamePos(this.worldObject());
    }

    protected boolean canDropEntity(Entity ent) throws EntityNotAlive {
        return canDropType(ent.getClass());
    }

    protected abstract boolean canCollectType(Class<? extends Entity> cls);

    protected abstract boolean canDropType(Class<? extends Entity> cls);
    
    private void collectEntity(CollectableEntity ent) throws EntityNotAlive {
        ent.despawn();
        this._inventory.add(ent);
        this.collected(ent);
    }

    @Override
    public boolean canCollect(Class<? extends Entity> cls) throws EntityNotAlive {
        synchronized (this.simulation()) {
            for (Entity ent : this.simulation().entities()) {
                if ((ent.getClass() == cls) && canCollectEntity(ent)) return true;
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
    @Override
    public boolean canCollect() throws EntityNotAlive {
        synchronized (this.simulation()) {
            for (Entity ent : this.simulation().entities()) {
                if (canCollectEntity(ent)) return true;
            }
        }
        return false;
    }
    
    @Override
    public void collect(Class<? extends Entity> cls) throws CanNotCollectException, EntityNotAlive {
        this.delayed(() -> {
            for (Entity ent : this.simulation().entities()) {
                if ((ent.getClass() == cls) && canCollectEntity(ent)) {
                    this.collectEntity((CollectableEntity) ent);
                    return;
                }
            }
            throw new CanNotCollectException("No such entity.");
        });
    }
    
    @Override
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

    @Override
    public boolean canDrop(Class<? extends Entity> cls) throws EntityNotAlive {
        if (!this.alive()) throw new EntityNotAlive();
        for (Entity ent : this._inventory) {
            if (ent.getClass() == cls && this.canDropEntity(ent)) return true;
        }
        return false;
    }
    @Override
    public void drop(Class<? extends Entity> cls) throws CanNotDropException, EntityNotAlive {
        this.delayed(() -> {
            for (Entity ent : this._inventory) {
                if (ent.getClass() == cls && this.canDropEntity(ent)) {
                    this._inventory.remove(ent);
                    WorldObject wob = this.worldObject();
                    ent.spawn(wob.column, wob.row);
                    this.dropped(ent);
                    return;
                }
            }
            throw new CanNotDropException();
        });
    }
    
    /**
     * Informs the instance that a CollectableEntity has been collected. This method exists to be overriden.
     * 
     * @param ent
     *            The entity that has been collected
     */
    void collected(Entity ent) {
        // default implementation: do nothing
    }
    
    /**
     * Informs the instance that a CollectableEntity has been dropped. This method exists to be overriden.
     * 
     * @param ent
     *            The dropped entity
     */
    void dropped(Entity ent) {
        // default implementation: do nothing
    }
}
