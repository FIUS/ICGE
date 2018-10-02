/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
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
        try {
            File rootDir = new File(urls.nextElement().toURI());
            loadSolutionInFile(rootDir, registration, loader, rootDir.getPath());
        } catch (URISyntaxException | ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
    
    private static void loadSolutionInFile(File file, Consumer<Class<? extends TaskTemplate>> registration, ClassLoader loader,
            String rootDir) throws ClassNotFoundException {
        if (!file.exists()) throw new IllegalArgumentException("File does not exist.");
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                loadSolutionInFile(f, registration, loader, rootDir);
            }
        } else {
            String path = file.getPath();
            if (path.endsWith(".class")) {
                String className = convertPathToClassName(path, rootDir);
                className = className.substring(0, className.length() - 6);
                Class<?> cls = loader.loadClass(className);
                if (isValidSolutionClass(cls)) {
                    doRegistration(cls, registration);
                }
            }
        }
    }
    
    private static boolean isValidSolutionClass(Class<?> cls) {
        return (TaskTemplate.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers()));
    }
    
    @SuppressWarnings("unchecked")
    private static void doRegistration(Class<?> cls, Consumer<Class<? extends TaskTemplate>> registration) {
        registration.accept((Class<? extends TaskTemplate>) cls);
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
