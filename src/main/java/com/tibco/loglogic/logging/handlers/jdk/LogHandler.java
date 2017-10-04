/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.handlers.jdk;

import static java.util.logging.ErrorManager.FLUSH_FAILURE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarder;
import com.tibco.loglogic.logging.forwarders.shared.LogForwarderSettings;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage.Severity;

/**
 * java.util.logging Handler generic base
 *
 * @author lpautet@tibco.com
 *
 */
public abstract class LogHandler
    extends Handler {

    private LogForwarder forwarder;

    private LogForwarderSettings settings;

    private static final Formatter FORMATTER = new Formatter() {
        @Override
        public String format( LogRecord record ) {
            return record.getMessage();
        }
    };

    protected abstract LogForwarderSettings createSettings();

    protected LogForwarderSettings getSettings() {
        return settings;
    }

    protected abstract LogForwarder createForwarder();

    /**
     * Constructor
     */
    public LogHandler() {

        settings = createSettings();

        configure();

        forwarder = createForwarder();

        try {
            forwarder.connect();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Read in the handler properties from the config file
     */
    protected void configure() {

        LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();

        String maxQueueSizeProperty = cname + ".maxQueueSize";
        if ( manager.getProperty( maxQueueSizeProperty ) != null ) {
            getSettings().setMaxQueueSize( manager.getProperty( maxQueueSizeProperty ) );
        }
        String levelProperty = cname + ".level";
        if ( manager.getProperty( levelProperty ) != null ) {
            setLevel( Level.parse( manager.getProperty( levelProperty ) ) );
        }
        String appNameProperty = cname + ".appName";
        getSettings().setAppName( manager.getProperty( appNameProperty ) );
        String sourceProperty = cname + ".source";
        getSettings().setSource( manager.getProperty( sourceProperty ) );
        String facility = cname + ".facility";
        try {
            byte facilityNum = Byte.parseByte( facility );
            getSettings().setFacility( facilityNum );
        }
        catch ( NumberFormatException ignored ) {
        }

        setFilter( null );
        String managerProperty = cname + ".formatter";
        if ( manager.getProperty( managerProperty ) != null ) {
            try {
                Class formatterClass = Class.forName( manager.getProperty( managerProperty ) );
                Constructor<Formatter> noArgConsructor = formatterClass.getConstructor( null );
                Formatter formatter = noArgConsructor.newInstance( null );
                setFormatter( formatter );
            }
            catch ( ClassNotFoundException e ) {
                e.printStackTrace();
            }
            catch ( NoSuchMethodException e ) {
                e.printStackTrace();
            }
            catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
            catch ( InstantiationException e ) {
                e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
        else {
            setFormatter( FORMATTER );
        }

        try {
            setEncoding( manager.getProperty( cname + ".encoding" ) );
        }
        catch ( Exception ex ) {
            try {
                setEncoding( null );
            }
            catch ( Exception ignored ) {
            }
        }
    }

    /**
     * Clean up resources
     */
    @Override
    synchronized public void close()
        throws SecurityException {

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

    /**
     * Log the message
     */
    @Override
    public void publish( LogRecord record ) {

        if ( !isLoggable( record ) ) {
            return;
        }

        if ( forwarder == null ) {
            return;
        }

        Formatter formatter = getFormatter();
        String formatted = formatter.format( record );

        if ( ( formatted.charAt( formatted.length() - 1 ) ) == '\n' )
            formatted = formatted.substring( 0, formatted.length() - 1 );

        SyslogMessage syslogMessage = new SyslogMessage();

        if ( record.getLevel() == SEVERE ) {
            syslogMessage.setSeverity( Severity.CRITICAL );
        }
        else if ( record.getLevel() == INFO ) {
            syslogMessage.setSeverity( Severity.INFORMATIONAL );
        }
        else if ( record.getLevel() == WARNING ) {
            syslogMessage.setSeverity( Severity.WARNING );
        }
        else {
            syslogMessage.setSeverity( Severity.DEBUG );
        }

        syslogMessage.setTimestamp( record.getMillis() );

        syslogMessage.setProcId( Integer.toString( record.getThreadID() ) );

        syslogMessage.setMsgId( Long.toString( record.getSequenceNumber() ) );

        syslogMessage.setAppName( record.getResourceBundleName() );

        syslogMessage.setMessage( formatted );

        forwarder.forwardEvent( syslogMessage, true );
    }

    @Override
    public void flush() {
        try {
            forwarder.flush();
        }
        catch ( IOException e ) {
            reportError( "Cannot flush", e, FLUSH_FAILURE );
        }
    }

}
