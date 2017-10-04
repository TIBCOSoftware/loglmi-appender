/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders.shared;

/**
 * Holds the settings common to all log forwarders
 *
 * Default facility is 16 (local0)
 *
 * Default size of sending Queue is 500KB
 *
 * @author lpautet@tibco.com
 */
public abstract class LogForwarderSettings {

    private String appName;

    private String source;

    private byte facility = 16; // default is local0 facility;

    private boolean rawMode;

    // data size multipliers
    private static final int KB = 1024;

    private static final int MB = KB * 1024;

    private static final int GB = MB * 1024;

    // defaults to 500K
    private long maxQueueSize = 500 * KB;

    /**
     * Set the queue size from the configured property String value. Default value is 500KB
     *
     * @param rawProperty in format [<integer>|<integer>[KB|MB|GB]]
     */
    public void setMaxQueueSize( String rawProperty ) {

        int multiplier;
        int factor;

        if ( rawProperty.endsWith( "KB" ) ) {
            multiplier = KB;
        }
        else if ( rawProperty.endsWith( "MB" ) ) {
            multiplier = MB;
        }
        else if ( rawProperty.endsWith( "GB" ) ) {
            multiplier = GB;
        }
        else {
            return;
        }
        try {
            factor = Integer.parseInt( rawProperty.substring( 0, rawProperty.length() - 2 ) );
        }
        catch ( NumberFormatException e ) {
            return;
        }
        setMaxQueueSize( factor * multiplier );
    }

    /**
     * Get the queue size
     *
     * @return the max queue size in bytes
     */
    public long getMaxQueueSize() {
        return maxQueueSize;
    }

    /**
     * Set the queue size
     *
     * @param maxQueueSize the size of the queue in bytes
     */
    public void setMaxQueueSize( long maxQueueSize ) {
        this.maxQueueSize = maxQueueSize;
    }

    /**
     * The application name to be put in ALL log messages, unless the message contains one already.
     *
     * @return the application name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets the application name to be put in ALL log messages, unless the message contains one already.
     *
     * @param appName the application name
     */
    public void setAppName( String appName ) {
        this.appName = appName;
    }

    /**
     * The source to be put in ALL log messages, unless the message contains one already.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source to be put in ALL log messages, unless the message contains one already.
     *
     * @param source the application name
     */
    public void setSource( String source ) {
        this.source = source;
    }

    /**
     * The facility to be put in ALL log messages.
     *
     * @return the application name
     */
    public byte getFacility() {
        return facility;
    }

    /**
     * Sets the facility to be put in ALL log messages.
     *
     * @param facility the application name
     */
    public void setFacility( byte facility ) {
        this.facility = facility;
    }

    /**
     * If true messages will be sent AS-IS, without any Syslog formatting.
     *
     * @return true if messages will be sent as-is
     */
    public boolean isRawMode() {
        return rawMode;
    }

    /**
     * Sets whether messages will be sent AS-IS, without any Syslog formatting.
     *
     * @param rawMode true if messages will be sent as-is
     *
     */
    public void setRawMode( boolean rawMode ) {
        this.rawMode = rawMode;
    }

}
