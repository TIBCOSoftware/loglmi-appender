/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.appenders.log4j2;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarder;
import com.tibco.loglogic.logging.forwarders.shared.LogForwarderSettings;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage.Severity;

/**
 * Log4j 2.x Appender generic base
 *
 * @author lpautet@tibco.com
 *
 */
public abstract class LogAppender
    extends AbstractAppender {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final Lock readLock = rwLock.readLock();

    private final LogForwarder forwarder;

    protected LogAppender( String name, Filter filter, Layout<? extends Serializable> layout,
                           final boolean ignoreExceptions, LogForwarderSettings settings, LogForwarder forwarder )
        throws Exception {
        super( name, filter, layout, ignoreExceptions );
        this.forwarder = forwarder;
        forwarder.connect();
    }

    public void append( LogEvent event ) {
        readLock.lock();
        try {
            Layout layout = getLayout();
            final byte[] bytes = layout.toByteArray( event );

            SyslogMessage syslogMessage = new SyslogMessage();

            if ( event.getLevel() == Level.FATAL ) {
                syslogMessage.setSeverity( Severity.CRITICAL );
            }
            else if ( event.getLevel() == Level.ERROR ) {
                syslogMessage.setSeverity( Severity.ERROR );
            }
            else if ( event.getLevel() == Level.INFO ) {
                syslogMessage.setSeverity( Severity.INFORMATIONAL );
            }
            else if ( event.getLevel() == Level.WARN ) {
                syslogMessage.setSeverity( Severity.WARNING );
            }
            else {
                syslogMessage.setSeverity( Severity.DEBUG );
            }

            syslogMessage.setTimestamp( event.getTimeMillis() );

            syslogMessage.setProcId( event.getThreadName() );

            syslogMessage.setAppName( event.getThreadName() );

            syslogMessage.setMessage( new String( bytes ) );

            forwarder.forwardEvent( syslogMessage, true );
        }
        catch ( Exception ex ) {
            if ( !ignoreExceptions() ) {
                throw new AppenderLoggingException( ex );
            }
        }
        finally {
            readLock.unlock();
        }
    }

}