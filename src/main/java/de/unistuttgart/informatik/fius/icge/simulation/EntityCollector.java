/*
 * This source file is part of the FIUS ICGE project.
 * For more information see github.com/neumantm/ICGE
 *
 * Copyright (c) 2018 the ICGE project authors.
 */

package de.unistuttgart.informatik.fius.icge.simulation;

import de.unistuttgart.informatik.fius.icge.simulation.Entity.EntityNotAlive;
import de.unistuttgart.informatik.fius.icge.territory.WorldObject.Sprite;

/**
 * A class implementing this interface can collect entities.
 * 
 * @author neumantm
 */
public interface EntityCollector {

    /**
     * Checks whether this entity can currently collect an entity of the given type
     * 
     * @param type
     *            The type of entity to collect
     * @return Whether this entity can currently collect.
     * @throws EntityNotAlive
     *             When this entity is not alive.
     */
    public boolean canCollect(Sprite type) throws EntityNotAlive;

    /**
     * Checks whether this entity can currently collect an entity.
     * 
     * @return Whether this entity can currently collect.
     * @throws EntityNotAlive
     *             When this entity is not alive.
     */
    public boolean canCollect() throws EntityNotAlive;

    public void collect(Sprite type) throws CanNotCollectException, EntityNotAlive;

    public void collect() throws CanNotCollectException, EntityNotAlive;

    public boolean canDrop(Sprite type) throws EntityNotAlive;

    public void drop(Sprite type) throws CanNotDropException, EntityNotAlive;

    default public boolean tryCollect(Sprite type) {
        try {
            this.collect(type);
            return true;
        } catch (CanNotCollectException | EntityNotAlive e) {
            return false;
        }
    }

    default public boolean tryCollect() {
        try {
            this.collect();
            return true;
        } catch (CanNotCollectException | EntityNotAlive e) {
            return false;
        }
    }

    default public boolean tryDrop(Sprite type) {
        try {
            this.drop(type);
            return true;
        } catch (CanNotDropException | EntityNotAlive e) {
            return false;
        }
    }

    /**
     * A exception thrown when it is impossible for an object to collect a entity..
     * 
     * @author neumantm
     */
    public class CanNotCollectException extends RuntimeException {
        /**
         * generated
         */
        private static final long serialVersionUID = 4812373675702003596L;
        
        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message. The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public CanNotCollectException() {
            super();
        }
        
        /**
         * Constructs a new runtime exception with the specified detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         *
         * @param message
         *            the detail message. The detail message is saved for
         *            later retrieval by the {@link #getMessage()} method.
         */
        public CanNotCollectException(String message) {
            super(message);
        }
        
        /**
         * Constructs a new runtime exception with the specified detail message and
         * cause.
         * <p>
         * Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param message
         *            the detail message (which is saved for later retrieval
         *            by the {@link #getMessage()} method).
         * @param cause
         *            the cause (which is saved for later retrieval by the
         *            {@link #getCause()} method). (A <tt>null</tt> value is
         *            permitted, and indicates that the cause is nonexistent or
         *            unknown.)
         * @since 1.4
         */
        public CanNotCollectException(String message, Throwable cause) {
            super(message, cause);
        }
        
        /**
         * Constructs a new runtime exception with the specified cause and a
         * detail message of <tt>(cause==null ? null : cause.toString())</tt>
         * (which typically contains the class and detail message of
         * <tt>cause</tt>). This constructor is useful for runtime exceptions
         * that are little more than wrappers for other throwables.
         *
         * @param cause
         *            the cause (which is saved for later retrieval by the
         *            {@link #getCause()} method). (A <tt>null</tt> value is
         *            permitted, and indicates that the cause is nonexistent or
         *            unknown.)
         * @since 1.4
         */
        public CanNotCollectException(Throwable cause) {
            super(cause);
        }
        
        /**
         * Constructs a new runtime exception with the specified detail
         * message, cause, suppression enabled or disabled, and writable
         * stack trace enabled or disabled.
         *
         * @param message
         *            the detail message.
         * @param cause
         *            the cause. (A {@code null} value is permitted,
         *            and indicates that the cause is nonexistent or unknown.)
         * @param enableSuppression
         *            whether or not suppression is enabled
         *            or disabled
         * @param writableStackTrace
         *            whether or not the stack trace should
         *            be writable
         *
         * @since 1.7
         */
        protected CanNotCollectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
        
    }
    
    /**
     * A exception thrown when it is impossible for an object to drop a entity.
     * 
     * @author neumantm
     */
    public class CanNotDropException extends RuntimeException {
        
        /**
         * generated
         */
        private static final long serialVersionUID = 1616122776622736935L;
        
        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message. The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public CanNotDropException() {
            super();
        }
        
        /**
         * Constructs a new runtime exception with the specified detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         *
         * @param message
         *            the detail message. The detail message is saved for
         *            later retrieval by the {@link #getMessage()} method.
         */
        public CanNotDropException(String message) {
            super(message);
        }
        
        /**
         * Constructs a new runtime exception with the specified detail message and
         * cause.
         * <p>
         * Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param message
         *            the detail message (which is saved for later retrieval
         *            by the {@link #getMessage()} method).
         * @param cause
         *            the cause (which is saved for later retrieval by the
         *            {@link #getCause()} method). (A <tt>null</tt> value is
         *            permitted, and indicates that the cause is nonexistent or
         *            unknown.)
         * @since 1.4
         */
        public CanNotDropException(String message, Throwable cause) {
            super(message, cause);
        }
        
        /**
         * Constructs a new runtime exception with the specified cause and a
         * detail message of <tt>(cause==null ? null : cause.toString())</tt>
         * (which typically contains the class and detail message of
         * <tt>cause</tt>). This constructor is useful for runtime exceptions
         * that are little more than wrappers for other throwables.
         *
         * @param cause
         *            the cause (which is saved for later retrieval by the
         *            {@link #getCause()} method). (A <tt>null</tt> value is
         *            permitted, and indicates that the cause is nonexistent or
         *            unknown.)
         * @since 1.4
         */
        public CanNotDropException(Throwable cause) {
            super(cause);
        }
        
        /**
         * Constructs a new runtime exception with the specified detail
         * message, cause, suppression enabled or disabled, and writable
         * stack trace enabled or disabled.
         *
         * @param message
         *            the detail message.
         * @param cause
         *            the cause. (A {@code null} value is permitted,
         *            and indicates that the cause is nonexistent or unknown.)
         * @param enableSuppression
         *            whether or not suppression is enabled
         *            or disabled
         * @param writableStackTrace
         *            whether or not the stack trace should
         *            be writable
         *
         * @since 1.7
         */
        protected CanNotDropException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
