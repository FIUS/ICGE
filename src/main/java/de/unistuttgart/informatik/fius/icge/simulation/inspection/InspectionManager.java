/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation.inspection;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unistuttgart.informatik.fius.icge.ClassFinder;
import de.unistuttgart.informatik.fius.icge.simulation.Entity;

/**
 * A class for managing the inspections
 * 
 * @author Tim Neumann
 */
public class InspectionManager {
    
    private final Map<Class<?>, InspectionData> inspectableClasses = new HashMap<>();
    
    /**
     * Create a new inspection manager.
     */
    public InspectionManager() {
        try {
            List<Class<?>> classes = ClassFinder.getClassesInClassLoader(c -> true);
            
            for (Class<?> cls : classes) {
                InspectionData d = new InspectionData(cls);
                if (d.hasAnyInspectableElements()) {
                    this.inspectableClasses.put(cls, d);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get's all attribute names of the given entity.
     * 
     * @param entity
     *            The entity to get the names for
     * @return A List of attribute names.
     */
    public List<String> getAttributeNamesOfEntity(Entity entity) {
        InspectionData d = this.inspectableClasses.get(entity.getClass());
        if (d == null) return Collections.emptyList();
        return d.getAttributeNames();
    }
    
    /**
     * Checks whether the attribute with the given name in the given entity is writable.
     * 
     * @param entity
     *            The entity.
     * @param attributeName
     *            The name of the attribute
     * @return Whether the attribute is writable.
     */
    public boolean isAttributeEditable(Entity entity, String attributeName) {
        InspectionData d = this.inspectableClasses.get(entity.getClass());
        if (d == null) return false;
        return !d.isAttributeReadOnly(attributeName);
    }
    
    /**
     * Get's the type of the attribute with the given name in the given entity.
     * 
     * @param entity
     *            The entity.
     * @param attributeName
     *            The name of the attribute
     * @return The type of the attribute.
     */
    public Class<?> getAttributeType(Entity entity, String attributeName) {
        InspectionData d = this.inspectableClasses.get(entity.getClass());
        if (d == null) return null;
        return d.getAttributeType(attributeName);
    }
    
    /**
     * Get the value of the attribute with the given name from the given entity
     * 
     * @param entity
     *            The entity to get the value from
     * @param attributeName
     *            The name of the attribute to get the value from
     * @return The value or null if it didn't work.
     */
    public Object getAttributeValue(Entity entity, String attributeName) {
        InspectionData d = this.inspectableClasses.get(entity.getClass());
        if (d == null) return null;
        return d.getAttributeValue(entity, attributeName);
    }
    
    /**
     * Set the value of the attribute with the given name in the given entity
     * 
     * @param entity
     *            The entity to set the value in
     * @param attributeNmae
     *            The name of the attribute to set the value for.
     * @param value
     *            The value to set.
     * @return Whether it worked.
     */
    public boolean setAttributeValue(Entity entity, String attributeNmae, Object value) {
        InspectionData d = this.inspectableClasses.get(entity.getClass());
        if (d == null) return false;
        return d.setAttributeValue(entity, attributeNmae, value);
    }
    
}
