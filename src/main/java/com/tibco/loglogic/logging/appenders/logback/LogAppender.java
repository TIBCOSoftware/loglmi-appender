/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.appenders.logback;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarder;
import com.tibco.loglogic.logging.forwarders.shared.LogForwarderSettings;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage.Severity;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

/**
 * logback Appender generic base
 *
 * @author lpautet@tibco.com
 *
 */
public abstract class LogAppender
    extends AppenderBase<ILoggingEvent> {

    private Layout<ILoggingEvent> layout;

    private LogForwarder logForwarder;

    protected abstract LogForwarderSettings getSettings();

    protected abstract LogForwarder createForwarder();

    /**
     * Log the message
     */
    @Override
    protected void append( ILoggingEvent event ) {

        if ( logForwarder != null ) {

            String formatted = layout.doLayout( event );

            SyslogMessage syslogMessage = new SyslogMessage();

            if ( event.getLevel() == Level.ERROR ) {
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

            syslogMessage.setProcId( Thread.currentThread().getName() );

            syslogMessage.setMessage( formatted );

            getLogForwarder().forwardEvent( syslogMessage, true );
        }
    }

    /**
     * Initialisation logic
     */
    @Override
    public void start() {

        if ( this.layout == null ) {
            addError( "No layout set for the appender named [" + name + "]." );
            return;
        }

        if ( getLogForwarder() == null ) {
            try {
                logForwarder = createForwarder();
                logForwarder.connect();
            }
            catch ( Exception e ) {
                addError( "Couldn't initialize appender named \"" + this.name + "\".", e );
            }
        }
        super.start();
    }

    /**
     * Clean up resources
     */
    @Override
    public void stop() {
        LogForwarder logForwarder = getLogForwarder();
        if ( logForwarder != null ) {
            try {
                logForwarder.close();
                logForwarder = null;
            }
            catch ( Exception e ) {
                Thread.currentThread().interrupt();
                logForwarder = null;
            }
        }
        super.stop();
    }

    public String getMaxQueueSize() {
        return Long.toString( getSettings().getMaxQueueSize() );
    }

    public void setMaxQueueSize( String maxQueueSize ) {
        getSettings().setMaxQueueSize( maxQueueSize );
    }

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout( Layout<ILoggingEvent> layout ) {
        this.layout = layout;
    }

    protected LogForwarder getLogForwarder() {
        return logForwarder;
    }

    public String getAppName() {
        return getSettings().getAppName();
    }

    public void setAppName( String appName ) {
        getSettings().setAppName( appName );
    }

    public String getSource() {
        return getSettings().getSource();
    }

    public void setSource( String source ) {
        getSettings().setSource( source );
    }

    public void setRawMode( boolean rawMode ) {
        getSettings().setRawMode( rawMode );
    }

    public boolean getRawMode() {
        return getSettings().isRawMode();
    }
}