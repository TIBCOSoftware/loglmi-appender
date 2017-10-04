/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders.uldp;

import java.io.IOException;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarder;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage;
import com.tibco.loglogic.logging.uldpclient.UldpSender;
import com.tibco.loglogic.logging.uldpclient.UldpSyslogMessage;

/**
 * Forwards log using ULDP-Syslog protocol
 *
 * @author Tibco LogLogic
 *
 */
public class UldpForwarder
    extends LogForwarder {

    private UldpSender uldpSender;

    public UldpForwarder( UldpForwarderSettings settings ) {
        super( settings );
        uldpSender = new UldpSender( settings.getConnectionSettings() );
    }

    /**
     * open the stream
     *
     */
    @Override
    public void initializeConnection()
        throws IOException {
        ( (UldpForwarderSettings) getSettings() ).getConnectionSettings()
            .setAckPendingQueueSize( (int) getSettings().getMaxQueueSize() );
        uldpSender.connect();
    }

    @Override
    public void flush()
        throws IOException {
        uldpSender.flush();
    }

    /**
     * close the stream
     */
    public void close() {
        try {

            if ( uldpSender != null ) {
                uldpSender.close();
            }
        }
        catch ( Exception e ) {
        }
    }

    /**
     * send an event via stream
     *
     * @param syslogMessage the syslog message to send
     */
    public void sendMessage( SyslogMessage syslogMessage )
        throws IOException {

        String currentMessage = syslogMessage.toString();
        currentMessage = currentMessage.replace( "\r", "\\r" ).replace( "\n", "\\n" );

        uldpSender.sendMessage( new UldpSyslogMessage( syslogMessage.getTimestamp(), localAddress, currentMessage ) );
    }
}