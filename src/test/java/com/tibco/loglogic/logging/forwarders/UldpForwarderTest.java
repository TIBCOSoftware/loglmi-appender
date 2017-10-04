/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import static com.tibco.loglogic.logging.uldpclient.DummyUldpServer.ULDP_LOG_MESSAGE;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage;
import com.tibco.loglogic.logging.forwarders.uldp.UldpForwarder;
import com.tibco.loglogic.logging.forwarders.uldp.UldpForwarderSettings;
import com.tibco.loglogic.logging.uldpclient.DummyUldpServer;
import com.tibco.loglogic.logging.uldpclient.MicroPki;
import com.tibco.loglogic.logging.uldpclient.UldpConnectionSettings;
import com.tibco.loglogic.logging.uldpclient.UldpLogMessage;

/**
 * Created by lpautet on 5/31/17.
 */
public class UldpForwarderTest {

    DummyUldpServer dummyUldpServer;

    private UldpForwarderSettings uldpForwarderSettings;

    public void setupUldpServer()
        throws IOException {
        setupUldpServer( null, null, null );
    }

    public void setupUldpServer( File keystoreFile, String pass, String tlsProtocolName )
        throws IOException {
        dummyUldpServer = new DummyUldpServer( keystoreFile, pass, tlsProtocolName, true );
        System.out.println( "port: " + dummyUldpServer.getPort() );
        uldpForwarderSettings = new UldpForwarderSettings( new UldpConnectionSettings( "localhost",
                                                                                       dummyUldpServer.getPort(),
                                                                                       10 * 1024 ) );
    }

    @Test
    public void testClearCnx()
        throws Exception {
        setupUldpServer();
        doTheTest();
    }

    @Test
    public void testCompressedCnx()
        throws Exception {
        setupUldpServer();
        uldpForwarderSettings.getConnectionSettings().setUseCompression( true );
        doTheTest();
    }

    @Test
    public void testDomainName()
        throws Exception {
        setupUldpServer();
        uldpForwarderSettings.getConnectionSettings().setDomainName( "myDomainName" );
        List<DummyUldpServer.KVP> props = doTheTest();
        assertEquals( "myDomainName", dummyUldpServer.getDomainName() );
    }

    @Test
    public void testTlsv12()
        throws Exception {
        MicroPki microPki = new MicroPki();
        microPki.createCa();
        setupUldpServer( microPki.getCaKeystore(), microPki.getPassword(), "TLSv1.2" );
        uldpForwarderSettings.getConnectionSettings().setUseTls( true );
        uldpForwarderSettings.getConnectionSettings().setKeystorePath( microPki.getCaKeystore().getCanonicalPath() );
        uldpForwarderSettings.getConnectionSettings().setKeystorePassword( microPki.getPassword() );
        uldpForwarderSettings.getConnectionSettings().setTlsProtocolName( "TLSv1.2" );
        doTheTest();
    }

    public List<DummyUldpServer.KVP> doTheTest()
        throws Exception {

        uldpForwarderSettings.setRawMode( true );
        UldpForwarder uldpForwarder = new UldpForwarder( uldpForwarderSettings );

        uldpForwarder.connect();

        Thread.sleep( 100 );

        assert ( dummyUldpServer.getCnxSocket() != null );

        String logLine = "My first message";

        SyslogMessage syslogMessage = new SyslogMessage();

        syslogMessage.setMessage( logLine );

        uldpForwarder.forwardEvent( syslogMessage, true );

        List<DummyUldpServer.KVP> props = dummyUldpServer.readOneMessage();

        assertEquals( ULDP_LOG_MESSAGE, dummyUldpServer.getMsgType() );

        assertEquals( UldpLogMessage.UldpLogType.ULDP_LOG_TYPE_SYSLOG.getLogType(),
                      dummyUldpServer.getLogSourceType() );

        assertEquals( 1, props.size() );

        String msg = (String) props.get( 0 ).getValue();

        assertEquals( logLine, msg );

        uldpForwarder.close();

        return props;
    }

}
