/*
 * Copyright 2014, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */
package com.allanbank.mongodb.util.log;

import java.util.logging.Level;

/**
 * AbstractLog implements all of the public log method to delegate to a single
 * {@link #doLog(Level, Throwable, String, Object...)} method.
 * 
 * @api.no This class is <b>NOT</b> part of the drivers API. This class may be
 *         mutated in incompatible ways between any two releases of the driver.
 * @copyright 2014, Allanbank Consulting, Inc., All Rights Reserved
 */
public abstract class AbstractLog implements Log {

	/** The name of this class. */
	protected static final Object CLASS_NAME = AbstractLog.class.getName();

	/**
	 * Delegate method for the {@link Log} implementation.
	 * 
	 * @param level
	 *            The logging level for the message.
	 * @param thrown
	 *            The exception associated with the log message.
	 * @param template
	 *            The message template to log.
	 * @param args
	 *            The arguments to replace the <code>{}</code> entries in the
	 *            template.
	 */
	protected abstract void doLog(Level level, Throwable thrown,
			String template, Object... args);

	public AbstractLog() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#debug(String)
	 */
	@Override
	public final void debug(String message) {
		doLog(Level.FINE, (Throwable) null, message);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#info(String)
	 */
	@Override
	public final void info(String message) {
		doLog(Level.INFO, (Throwable) null, message);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#warn(String)
	 */
	@Override
	public final void warn(String message) {
		doLog(Level.WARNING, (Throwable) null, message);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#error(String)
	 */
	@Override
	public final void error(String message) {
		doLog(Level.SEVERE, (Throwable) null, message);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#log(Level, String)
	 */
	@Override
	public final void log(Level level, String message) {
		doLog(level, (Throwable) null, message);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#debug(String, Object[])
	 */
	@Override
	public final void debug(String template, Object... args) {
		doLog(Level.FINE, null, template, args);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#info(String, Object[])
	 */
	@Override
	public final void info(String template, Object... args) {
		doLog(Level.INFO, null, template, args);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#warn(String, Object[])
	 */
	@Override
	public final void warn(String template, Object... args) {
		doLog(Level.WARNING, null, template, args);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#error(String, Object[])
	 */
	@Override
	public final void error(String template, Object... args) {
		doLog(Level.SEVERE, null, template, args);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#log(Level, String, Object[])
	 */
	@Override
	public final void log(Level level, String template, Object... args) {
		doLog(level, null, template, args);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#debug(Throwable, String, Object[])
	 */
	@Override
	public final void debug(Throwable thrown, String template, Object... args) {
		doLog(Level.FINE, thrown, template, args);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#info(Throwable, String, Object[])
	 */
	@Override
	public final void info(Throwable thrown, String template, Object... args) {
		doLog(Level.INFO, thrown, template, args);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#warn(Throwable, String, Object[])
	 */
	@Override
	public final void warn(Throwable thrown, String template, Object... args) {
		doLog(Level.WARNING, thrown, template, args);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#error(Throwable, String, Object[])
	 */
	@Override
	public final void error(Throwable thrown, String template, Object... args) {
		doLog(Level.SEVERE, thrown, template, args);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to call {@link #doLog(Level, Throwable, String, Object...)}.
	 * </p>
	 * 
	 * @see Log#log(Level, Throwable, String, Object[])
	 */
	@Override
	public final void log(Level level, Throwable thrown, String template,
			Object... args) {
		doLog(level, thrown, template, args);
	}

}