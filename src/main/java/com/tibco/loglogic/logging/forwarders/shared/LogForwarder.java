/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders.shared;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 * Generic part of a log forwarder. Contains a queue of SyslogMessage. Handles the logic of connecting to the
 * destination, queuing messages if no connection can be established, up to the limit defined.
 *
 * Once the limit is reached, the oldes message is discarded.
 *
 * @author lpautet@tibco.com
 *
 */
public abstract class LogForwarder
    implements AutoCloseable {

    // Using this collection structure to implement the FIFO queue
    private LinkedList<SyslogMessage> queue = new LinkedList<>();

    private long currentQueueSizeInBytes = 0;

    protected final InetAddress localAddress;

    private boolean connected = false;

    private final LogForwarderSettings settings;

    protected LogForwarder( LogForwarderSettings settings ) {
        this.settings = settings;
        try {
            localAddress = InetAddress.getLocalHost();
        }
        catch ( UnknownHostException e ) {
            // unexpected
            throw new RuntimeException( e );
        }
    }

    /**
     * Connect to the destination if possible
     *
     * @throws IOException in case of connection issues
     */
    public void connect()
        throws IOException {
        initializeConnection();
        connected = true;
    }

    protected abstract void initializeConnection()
        throws IOException;

    /**
     * Close the connection to the destination.
     *
     * @throws IOException in case of connection issues
     */
    public abstract void close()
        throws IOException;

    protected abstract void sendMessage( SyslogMessage syslogMessage )
        throws IOException;

    /**
     * Flush the connection
     *
     * @throws IOException in case of connection issues
     */
    public abstract void flush()
        throws IOException;

    /**
     * Send an event via the established connection, otherwise enqueue the message.
     *
     * @param syslogMessage the syslog message to send
     */
    public void forwardEvent( SyslogMessage syslogMessage, boolean flush ) {
        if ( !settings.isRawMode() && !syslogMessage.isRawMode() ) {
            if ( settings.getSource() == null )
                syslogMessage.setSource( localAddress.getHostName() );
            else
                syslogMessage.setSource( settings.getSource() );
            syslogMessage.setFacility( settings.getFacility() );
            syslogMessage.setAppName( settings.getAppName() );
        }
        else {
            syslogMessage.setRawMode( true );
        }

        if ( syslogMessage.isRawMode() ) {
            syslogMessage.setTimestamp( System.currentTimeMillis() );
        }

        try {

            if ( connected ) {
                sendMessage( syslogMessage );
                // flush the queue
                while ( queueContainsEvents() ) {
                    syslogMessage = dequeue();
                    sendMessage( syslogMessage );
                }
                if ( flush )
                    flush();
            }
        }
        catch ( IOException e ) {
            // something went wrong , put message on the queue for retry
            enqueue( syslogMessage );
            try {
                close();
            }
            catch ( Exception ignored) {
            }

            connected = false;

            try {
                initializeConnection();
            }
            catch ( Exception ignored) {
            }
        }
    }

    /**
     * Add an event to the head of the FIFO queue subject, removing oldest event is lacking room
     *
     * @param event the syslog message to enqueue
     */
    private void enqueue( SyslogMessage event ) {

        long eventSize = event.toString().getBytes().length;

        while ( !queueHasCapacity( eventSize ) ) {
            queue.removeLast();
            currentQueueSizeInBytes -= eventSize;
        }

        queue.addFirst( event );
        currentQueueSizeInBytes += eventSize;
    }

    private boolean queueHasCapacity( long eventSize ) {
        return ( currentQueueSizeInBytes + eventSize ) <= settings.getMaxQueueSize();
    }

    private boolean queueContainsEvents() {
        return !queue.isEmpty();
    }

    private SyslogMessage dequeue() {

        if ( queueContainsEvents() ) {
            SyslogMessage event = queue.removeLast();
            currentQueueSizeInBytes -= event.toString().getBytes().length;
            if ( currentQueueSizeInBytes < 0 ) {
                currentQueueSizeInBytes = 0;
            }
            return event;
        }
        return null;
    }

    protected LogForwarderSettings getSettings() {
        return settings;
    }
}