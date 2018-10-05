/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 * 
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * A class for finding classes in the class loader
 * 
 * @author Tim Neumann
 */
public class ClassFinder {
    private ClassFinder() {
        //hide constructor
    }
    
    /**
     * Get all classes in the current context class loader, which match the filter.
     * 
     * @param filter
     *            The filter to check each class against.
     * @return A list of classes
     * @throws IOException
     *             When an IO Error occurs.
     */
    public static List<Class<?>> getClassesInClassLoader(Predicate<Class<?>> filter) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        List<URL> urls = Collections.list(loader.getResources("de"));
        
        List<Class<?>> classes = new ArrayList<>();
        
        for (URL url : urls) {
            try {
                File rootDir = new File(url.toURI()).getParentFile();
                loadClassInFile(rootDir, classes, loader, rootDir.getPath(), filter);
            } catch (URISyntaxException | ClassNotFoundException e) {
                throw new IOException(e);
            }
        }
        
        return classes;
    }
    
    private static void loadClassInFile(File file, List<Class<?>> solutions, ClassLoader loader, String rootDir,
            Predicate<Class<?>> filter) throws ClassNotFoundException {
        if (!file.exists()) throw new IllegalArgumentException("File does not exist.");
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                loadClassInFile(f, solutions, loader, rootDir, filter);
            }
        } else {
            String path = file.getPath();
            if (path.endsWith(".class")) {
                String className = convertPathToClassName(path, rootDir);
                className = className.substring(0, className.length() - 6);
                Class<?> cls = loader.loadClass(className);
                if (filter.test(cls)) {
                    solutions.add(cls);
                }
            }
        }
    }
    
    private static String convertPathToClassName(String path, String rootDir) {
        if (!path.startsWith(rootDir)) throw new IllegalStateException("File not starting with root dir!");
        String lineSep = System.getProperty("file.separator");
        String relPath = path.substring(rootDir.length());
        if (relPath.startsWith(lineSep)) {
            relPath = relPath.substring(1);
        }
        return relPath.replace(lineSep, ".");
    }
}
