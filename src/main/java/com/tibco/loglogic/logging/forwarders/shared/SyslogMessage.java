/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders.shared;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Defines the properties of a syslog message (RFC 5424, RFC 3164)
 *
 * @author lpautet@tibco.com
 */
public class SyslogMessage {

    private static final ThreadLocal<DateFormat> THREADLOCAL_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat( DATEFORMATPATTERN );
        }
    };

    private static String getTimeString( Date timestamp ) {
        return THREADLOCAL_FORMAT.get().format( timestamp );
    }

    private static final String DATEFORMATPATTERN = "YYYY-MM-dd'T'HH:mm:ss.SSS'000'ZZ";

    /**
     * Severity of the message as found in RFCs
     */
    public enum Severity {
        CRITICAL(2), ERROR(3), WARNING(4), NOTICE(5), INFORMATIONAL(6), DEBUG(7);

        private int level;

        Severity( int level ) {
            this.level = level;
        }

        int getLevel() {
            return level;
        }
    }

    private boolean rawMode;

    private Severity severity = Severity.INFORMATIONAL;

    private int facility;

    private String source;

    private Date timestamp;

    private String appName;

    private String procId;

    private String msgId;

    private String message;

    /**
     * Get the timestamp of the message
     *
     * @return the timestamp of the message (ms)
     */
    public long getTimestamp() {
        return timestamp.getTime();
    }

    /**
     *
     * Set the timestamp of the message
     *
     * @param timestamp the timestamp of the message (ms)
     */
    public void setTimestamp( long timestamp ) {
        this.timestamp = new Date( timestamp );
    }

    /**
     * Get the application name
     *
     * @return the application name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Set the application name
     *
     * @param appName
     */
    public void setAppName( String appName ) {
        this.appName = appName;
    }

    /**
     * Get the process ID
     *
     * @return the process ID
     */
    public String getProcId() {
        return procId;
    }

    /**
     * Set the process ID
     *
     * @param procId the process ID
     */
    public void setProcId( String procId ) {
        this.procId = procId;
    }

    /**
     * Get the message ID
     *
     * @return the message ID
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Set the message ID
     *
     * @param msgId the message ID
     */
    public void setMsgId( String msgId ) {
        this.msgId = msgId;
    }

    /**
     * Get the facility
     *
     * @return the facility id
     */
    public int getFacility() {
        return facility;
    }

    /**
     * Set the facility
     *
     * @param facility the facility
     */
    public void setFacility( int facility ) {
        this.facility = facility;
    }

    /**
     * Get the severity of the message
     *
     * @return the severity of the message
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * The severity of the message
     *
     * @param severity the severity of the message
     */
    public void setSeverity( Severity severity ) {
        this.severity = severity;
    }

    /**
     * Get The body of the message
     *
     * @return the body of the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the body of the message
     *
     * @param message the body of the message
     */
    public void setMessage( String message ) {
        this.message = message;
    }

    // As per RFC-5424, the string format is
    //
    // HEADER = PRI VERSION SP TIMESTAMP SP HOSTNAME SP APP-NAME SP PROCID SP MSGID

    /**
     * Formats the message as a Syslog message, as per RFC-5424, the string format is SYSLOG-MSG = HEADER SP
     * STRUCTURED-DATA [SP MSG] HEADER = PRI VERSION SP TIMESTAMP SP HOSTNAME SP APP-NAME SP PROCID SP MSGID
     *
     * if the rawMode setting is on, no formating is done and the message body is returned
     *
     * @return the syslog message
     */
    @Override
    public String toString() {
        if ( rawMode )
            return message;
        if ( timestamp == null )
            timestamp = new Date();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "<" ).append( facility * 8 + severity.level ).append( ">1 " );
        stringBuilder.append( getTimeString( timestamp ) ).append( " " );
        if ( source != null ) {
            stringBuilder.append( source ).append( " " );
        }
        else {
            stringBuilder.append( "- " );
        }
        if ( appName != null ) {
            stringBuilder.append( appName ).append( " " );
        }
        else {
            stringBuilder.append( "- " );
        }
        if ( procId != null ) {
            stringBuilder.append( procId ).append( " " );
        }
        else {
            stringBuilder.append( "- " );
        }
        if ( msgId != null ) {
            stringBuilder.append( msgId ).append( " " );
        }
        else {
            stringBuilder.append( "- " );
        }
        stringBuilder.append( "- " ); // structured data placeholder
        stringBuilder.append( message );
        return stringBuilder.toString();
    }

    /**
     * Get the source host/IP
     *
     * @return the source host/IP
     */
    public String getSource() {
        return source;
    }

    /**
     * Set the source host/IP
     *
     * @param source the source host/IP
     */
    public void setSource( String source ) {
        this.source = source;
    }

    /**
     * Get if message is in RAW MODE (no syslog formatting should be applied)
     *
     * @return true if the message is in RAW MODE
     */
    public boolean isRawMode() {
        return rawMode;
    }

    /**
     * Set if message is in RAW MODE (no syslog formatting should be applied)
     *
     * @param rawMode true if the message is in RAW MODE
     */
    public void setRawMode( boolean rawMode ) {
        this.rawMode = rawMode;
    }
}
