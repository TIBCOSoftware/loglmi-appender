/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */

package com.tibco.loglogic.logging.appenders.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarder;
import com.tibco.loglogic.logging.forwarders.shared.LogForwarderSettings;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage.Severity;

/**
 * Log4j Appender generic base
 *
 * @author lpautet@tibco.com
 *
 */
public abstract class LogAppender
    extends AppenderSkeleton {

    private LogForwarder forwarder;

    protected abstract LogForwarderSettings getSettings();

    protected abstract LogForwarder createForwarder();

    public LogAppender() {
    }

    /**
     * Constructor with a layout
     *
     * @param layout the layout to apply to the log event
     */
    public LogAppender( Layout layout ) {
        this();
        this.layout = layout;
    }

    /**
     * Log the message
     */
    @Override
    protected void append( LoggingEvent event ) {

        try {
            if ( forwarder == null ) {
                forwarder = createForwarder();
                forwarder.connect();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
            errorHandler.error( "Couldn't initialize " + this.getClass().getName() + " named \"" + this.name + "\"." );
            return;
        }

        String formatted;
        if ( layout != null ) {
            formatted = layout.format( event );
        }
        else {
            formatted = event.getRenderedMessage();
        }

        // send error stack traces
        if ( layout != null && layout.ignoresThrowable() ) {
            String[] s = event.getThrowableStrRep();
            StringBuilder stackTrace = new StringBuilder();
            if ( s != null ) {
                int len = s.length;
                for ( int i = 0; i < len; i++ ) {
                    stackTrace.append( Layout.LINE_SEP );
                    stackTrace.append( s[i] );
                }
            }
            formatted += stackTrace.toString();
        }

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

        syslogMessage.setTimestamp( event.getTimeStamp() );

        syslogMessage.setProcId( Long.toString( Thread.currentThread().getId() ) );

        syslogMessage.setAppName( event.getThreadName() );

        syslogMessage.setMessage( formatted );

        forwarder.forwardEvent( syslogMessage, true );
    }

    synchronized public void close() {
        closed = true;
        if ( forwarder != null ) {
            try {
                forwarder.close();
                forwarder = null;
            }
            catch ( Exception e ) {
                Thread.currentThread().interrupt();
                forwarder = null;
            }
        }

    }

    public boolean requiresLayout() {
        return true;
    }

    public long getMaxQueueSize() {
        return getSettings().getMaxQueueSize();
    }

    public void setMaxQueueSize( String maxQueueSize ) {
        getSettings().setMaxQueueSize( maxQueueSize );
    }

    public void setAppName( String appName ) {
        getSettings().setAppName( appName );
    }

    public String getAppName( String appName ) {
        return getSettings().getAppName();
    }

    public void setRawMode( boolean rawMode ) {
        getSettings().setRawMode( rawMode );
    }

    public boolean getRawMode() {
        return getSettings().isRawMode();
    }

}
