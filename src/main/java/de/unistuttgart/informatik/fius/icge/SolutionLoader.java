/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

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
    
    public static void loadSolutions(Consumer<Class<? extends TaskTemplate>> registration) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = loader.getResources("");
        List<Class<? extends TaskTemplate>> solutions = new ArrayList<>();
        try {
            File rootDir = new File(urls.nextElement().toURI());
            loadSolutionInFile(rootDir, solutions, loader, rootDir.getPath());
        } catch (URISyntaxException | ClassNotFoundException e) {
            throw new IOException(e);
        }
        solutions.sort((cls1, cls2) -> {
            try {
                Integer nr1 = (Integer) cls1.getMethod("taskNumber").invoke(null);
                Integer nr2 = (Integer) cls2.getMethod("taskNumber").invoke(null);
                if (nr1 < nr2) {
                    return -1;
                }
                if (nr1 > nr2) {
                    return 1;
                }
            } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException e) {}
            return 0;
        });
        solutions.forEach(registration);
    }
    
    @SuppressWarnings("unchecked")
    private static void loadSolutionInFile(File file, List<Class<? extends TaskTemplate>> solutions, ClassLoader loader,
            String rootDir) throws ClassNotFoundException {
        if (!file.exists()) throw new IllegalArgumentException("File does not exist.");
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                loadSolutionInFile(f, solutions, loader, rootDir);
            }
        } else {
            String path = file.getPath();
            if (path.endsWith(".class")) {
                String className = convertPathToClassName(path, rootDir);
                className = className.substring(0, className.length() - 6);
                Class<?> cls = loader.loadClass(className);
                if (isValidSolutionClass(cls)) {
                    solutions.add((Class<? extends TaskTemplate>) cls);
                }
            }
        }
    }
    
    private static boolean isValidSolutionClass(Class<?> cls) {
        return (TaskTemplate.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers()));
    }
    
    private static String convertPathToClassName(String path, String rootDir) {
        if (!path.startsWith(rootDir)) {
            throw new IllegalStateException("File not starting with root dir!");
        }
        String lineSep = System.getProperty("file.separator");
        String relPath = path.substring(rootDir.length());
        if (relPath.startsWith(lineSep)) {
            relPath = relPath.substring(1);
        }
        return relPath.replace(lineSep, ".");
    }
}
