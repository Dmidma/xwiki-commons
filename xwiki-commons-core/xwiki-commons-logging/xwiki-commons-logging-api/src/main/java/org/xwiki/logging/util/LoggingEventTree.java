/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.logging.util;

import java.util.Arrays;
import java.util.Stack;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.xwiki.logging.LoggingEventMessage;

/**
 * Logs organized as a tree.
 *
 * @version $Id$
 * @since 8.0M2
 */
public class LoggingEventTree extends LoggingEventTreeNode implements Logger
{
    /**
     * Serialization identifier.
     */
    private static final long serialVersionUID = 1L;

    private Stack<LoggingEventTreeNode> currentNode = new Stack<LoggingEventTreeNode>();

    /**
     * Default constructor.
     */
    public LoggingEventTree()
    {
        this.currentNode.push(this);
    }

    /**
     * @param level the log level
     * @param format the log message
     * @param arguments the event arguments to insert in the message
     * @return the created {@link LoggingEvent} instance
     */
    protected LoggingEvent log(Level level, String format, Object[] arguments)
    {
        return log(null, level, format, arguments);
    }

    /**
     * @param marker the log marker
     * @param level the log level
     * @param format the log message
     * @param arguments the event arguments to insert in the message
     * @return the created {@link LoggingEvent} instance
     */
    protected LoggingEvent log(Marker marker, Level level, String format, Object[] arguments)
    {
        Object[] actualArray;
        Throwable throwable;
        if (arguments.length > 0 && arguments[arguments.length - 1] instanceof Throwable) {
            actualArray = Arrays.copyOf(arguments, arguments.length - 1);
            throwable = (Throwable) arguments[arguments.length - 1];
        } else {
            actualArray = arguments;
            throwable = null;
        }

        return log(marker, level, format, actualArray, throwable);
    }

    /**
     * @param marker the log marker
     * @param level the log level
     * @param format the log message
     * @param arguments the event arguments to insert in the message
     * @param throwable the throwable associated to the event
     * @return the created {@link LoggingEvent} instance
     * @param loggerName the name of the logger where this log was sent
     * @param threadName the name of the thread from where this log was sent
     */
    protected LoggingEvent log(Marker marker, Level level, String format, Object[] arguments, Throwable throwable)
    {
        LoggingEvent logEvent = new LoggingEventMessage(marker, level, format, arguments, throwable, null);

        log(logEvent);

        return logEvent;
    }

    /**
     * Register passed log.
     * 
     * @param logEvent register
     */
    public void log(LoggingEvent logEvent)
    {
        if (logEvent.getMarker() != null && logEvent.getMarker().contains(LoggingEventMessage.MARKER_BEGIN)) {
            LoggingEventTreeNode node = new LoggingEventTreeNode(logEvent);
            this.currentNode.peek().add(node);
            this.currentNode.push(node);
        } else if (logEvent.getMarker() != null && logEvent.getMarker().contains(LoggingEventMessage.MARKER_END)) {
            this.currentNode.peek().add(logEvent);
            pop();
        } else {
            this.currentNode.peek().add(logEvent);
        }
    }

    /**
     * Make log tree caret go the parent next sibling.
     */
    public void pop()
    {
        if (this.currentNode.peek() != this) {
            this.currentNode.pop();
        }
    }

    // Logger

    @Override
    public String getName()
    {
        return null;
    }

    // TRACE

    @Override
    public boolean isTraceEnabled()
    {
        return true;
    }

    @Override
    public void trace(String msg)
    {
        trace(msg, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Override
    public void trace(String format, Object arg)
    {
        trace((Marker) null, format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2)
    {
        trace((Marker) null, format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments)
    {
        trace((Marker) null, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t)
    {
        trace((Marker) null, msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker)
    {
        return true;
    }

    @Override
    public void trace(Marker marker, String msg)
    {
        trace(marker, msg, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Override
    public void trace(Marker marker, String format, Object arg)
    {
        trace(marker, format, new Object[] { arg });
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2)
    {
        trace(marker, format, new Object[] { arg1, arg2 });
    }

    @Override
    public void trace(Marker marker, String format, Object... arguments)
    {
        log(marker, Level.TRACE, format, arguments);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t)
    {
        log(marker, Level.TRACE, msg, ArrayUtils.EMPTY_OBJECT_ARRAY, t);
    }

    // DEBUG

    @Override
    public boolean isDebugEnabled()
    {
        return true;
    }

    @Override
    public void debug(String msg)
    {
        debug((Marker) null, msg);
    }

    @Override
    public void debug(String format, Object arg)
    {
        debug((Marker) null, format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2)
    {
        debug((Marker) null, format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments)
    {
        debug((Marker) null, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t)
    {
        debug((Marker) null, msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker)
    {
        return true;
    }

    @Override
    public void debug(Marker marker, String msg)
    {
        debug(marker, msg, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Override
    public void debug(Marker marker, String format, Object arg)
    {
        debug(marker, format, new Object[] { arg });
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2)
    {
        debug(marker, format, new Object[] { arg1, arg2 });
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments)
    {
        log(marker, Level.DEBUG, format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t)
    {
        log(marker, Level.DEBUG, msg, ArrayUtils.EMPTY_OBJECT_ARRAY, t);
    }

    // INFO

    @Override
    public boolean isInfoEnabled()
    {
        return true;
    }

    @Override
    public void info(String msg)
    {
        info((Marker) null, msg);
    }

    @Override
    public void info(String format, Object arg)
    {
        info((Marker) null, format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2)
    {
        info((Marker) null, format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments)
    {
        info((Marker) null, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t)
    {
        info((Marker) null, msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker)
    {
        return true;
    }

    @Override
    public void info(Marker marker, String msg)
    {
        info(marker, msg, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Override
    public void info(Marker marker, String format, Object arg)
    {
        info(marker, format, new Object[] { arg });
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2)
    {
        info(marker, format, new Object[] { arg1, arg2 });
    }

    @Override
    public void info(Marker marker, String format, Object... arguments)
    {
        log(marker, Level.INFO, format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t)
    {
        log(marker, Level.INFO, msg, ArrayUtils.EMPTY_OBJECT_ARRAY, t);
    }

    // WARN

    @Override
    public boolean isWarnEnabled()
    {
        return true;
    }

    @Override
    public void warn(String msg)
    {
        warn((Marker) null, msg);
    }

    @Override
    public void warn(String format, Object arg)
    {
        warn((Marker) null, format, arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2)
    {
        warn((Marker) null, format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments)
    {
        warn((Marker) null, format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t)
    {
        warn((Marker) null, msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker)
    {
        return true;
    }

    @Override
    public void warn(Marker marker, String msg)
    {
        warn(marker, msg, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Override
    public void warn(Marker marker, String format, Object arg)
    {
        warn(marker, format, new Object[] { arg });
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2)
    {
        warn(marker, format, new Object[] { arg1, arg2 });
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments)
    {
        log(marker, Level.WARN, format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t)
    {
        log(marker, Level.WARN, msg, ArrayUtils.EMPTY_OBJECT_ARRAY, t);
    }

    // ERROR

    @Override
    public boolean isErrorEnabled()
    {
        return true;
    }

    @Override
    public void error(String msg)
    {
        error((Marker) null, msg);
    }

    @Override
    public void error(String format, Object arg)
    {
        error((Marker) null, format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2)
    {
        error((Marker) null, format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments)
    {
        error((Marker) null, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t)
    {
        error((Marker) null, msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker)
    {
        return true;
    }

    @Override
    public void error(Marker marker, String msg)
    {
        error(marker, msg, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Override
    public void error(Marker marker, String format, Object arg)
    {
        error(marker, format, new Object[] { arg });
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2)
    {
        error(marker, format, new Object[] { arg1, arg2 });
    }

    @Override
    public void error(Marker marker, String format, Object... arguments)
    {
        log(marker, Level.ERROR, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t)
    {
        log(marker, Level.ERROR, msg, ArrayUtils.EMPTY_OBJECT_ARRAY, t);
    }
}