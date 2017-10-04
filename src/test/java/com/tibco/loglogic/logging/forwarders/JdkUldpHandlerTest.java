/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.tibco.loglogic.logging.forwarders.MiniUldpServer.ReceivedUldpMessage;
import org.junit.Test;

/**
 * Created by lpautet on 5/31/17.
 */
public class JdkUldpHandlerTest {

    private final Logger fLogger = Logger.getLogger( this.getClass().getPackage().getName() );

    @Test
    public void doTheJob()
        throws IOException {
        MiniUldpServer miniUldpServer = new MiniUldpServer();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File( classLoader.getResource( "myLogging-uldp.properties" ).getFile() );

        Properties properties = new Properties();

        FileInputStream in = new FileInputStream( file );
        properties.load( in );

        System.out.println( properties.getProperty( "com.tibco.loglogic.logging.handlers.jdk.UldpHandler.port" ) );
        properties.setProperty( "com.tibco.loglogic.logging.handlers.jdk.UldpHandler.port",
                                Integer.toString( miniUldpServer.getPort() ) );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        properties.store( byteArrayOutputStream, "" );

        LogManager.getLogManager().readConfiguration( new ByteArrayInputStream( byteArrayOutputStream.toByteArray() ) );

        int messagesReceived = 0;

        for ( int i = 0; i < 10; i++ ) {
            fLogger.log( Level.INFO, "This is an informational message TestA8 #" + i );
            fLogger.log( Level.SEVERE, "Example of an exception TestB3", new RuntimeException( "RuntimeExceptionB3" ) );
            try {
                Thread.sleep( 100 );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }

        for ( int i = 0; i < 30 && messagesReceived < 20; i++ ) {
            messagesReceived = miniUldpServer.getMessages().size();
            try {
                Thread.sleep( 100 );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        // flush is not done for each message, so receiving 1 less is ok

        assertTrue( messagesReceived == 20 );

        ReceivedUldpMessage msg1 = miniUldpServer.getMessages().get( 0 );

        assertEquals( msg1.domainName, "" );
        assertEquals( msg1.kvpList.size(), 1 );
        assertEquals( msg1.msgType, 1 );
        assertEquals( 0, msg1.kvpList.get( 0 ).getKey() );
        String msg = (String) msg1.kvpList.get( 0 ).getValue();

        System.out.println( msg );
        assertTrue( msg.startsWith( "<134>" ) );
        assertTrue( msg.contains( "MyMachineForUldp" ) );
        assertTrue( msg.contains( "MyAppNameForUldp" ) );
        assertTrue( msg.endsWith( "#0" ) );
    }

}
