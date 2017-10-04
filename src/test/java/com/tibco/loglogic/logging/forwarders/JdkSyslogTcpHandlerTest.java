/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

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

import org.junit.Test;

/**
 * Created by lpautet on 5/31/17.
 */
public class JdkSyslogTcpHandlerTest {

    private final Logger fLogger = Logger.getLogger( this.getClass().getPackage().getName() );

    @Test
    public void doTheJob()
        throws IOException {
        MiniSyslogTcpServer miniSyslogTcpServer = new MiniSyslogTcpServer();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File( classLoader.getResource("myLogging-tcp.properties").getFile() );

        Properties properties = new Properties();

        FileInputStream in = new FileInputStream( file );
        properties.load( in );

        System.out.println( properties.getProperty( "com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.port" ) );
        properties.setProperty( "com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.port",
                                Integer.toString( miniSyslogTcpServer.getPort() ) );

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
            messagesReceived = miniSyslogTcpServer.getMessages().size();
            try {
                Thread.sleep( 100 );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        // flush is not done for each message, so receiving 1 less is ok
        assertTrue( messagesReceived == 20 );

        String msg1 = miniSyslogTcpServer.getMessages().get( 0 );
        assertTrue( msg1.startsWith( "<134>" ) );
        assertTrue( msg1.contains( "MyMachineForSyslog" ) );
        assertTrue( msg1.contains( "MyAppNameForSyslog" ) );
        assertTrue( msg1.endsWith( "#0" ) );
    }

}
