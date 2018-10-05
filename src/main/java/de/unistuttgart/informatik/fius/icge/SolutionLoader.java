/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.unistuttgart.informatik.fius.icge.course.TaskTemplate;

/**
 * This class is responsible for loading all solutions.
 * 
 * @author Tim Neumann
 */
public class SolutionLoader {
    
    private SolutionLoader() {
        //hide constructor
    }
    
    /**
     * Loads all solutions and registers them with the given consumer.
     * 
     * @param registration
     *            The consumer to register each solution with.
     * @throws IOException
     *             When an IO error occurs.
     */
    public static void loadSolutions(Consumer<Class<? extends TaskTemplate>> registration) throws IOException {
        List<Class<? extends TaskTemplate>> solutions = ClassFinder
                .getClassesInClassLoader(SolutionLoader::isValidSolutionClass).stream().map(SolutionLoader::castToCorrectClass)
                .collect(Collectors.toList());
        
        solutions.sort((cls1, cls2) -> {
            try {
                Integer nr1 = (Integer) cls1.getMethod("taskNumber").invoke(null);
                Integer nr2 = (Integer) cls2.getMethod("taskNumber").invoke(null);
                if (nr1 < nr2) return -1;
                if (nr1 > nr2) return 1;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {}
            return 0;
        });
        solutions.forEach(registration);
    }
    
    private static boolean isValidSolutionClass(Class<?> cls) {
        return (TaskTemplate.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers()));
    }
    
    @SuppressWarnings("unchecked")
    private static Class<? extends TaskTemplate> castToCorrectClass(Class<?> cls) {
        if (!isValidSolutionClass(cls)) throw new IllegalArgumentException();
        return (Class<? extends TaskTemplate>) cls;
    }
    
}
