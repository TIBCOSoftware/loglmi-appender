/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Test;

/**
 * Created by lpautet on 5/31/17.
 */
public class Log4jSyslogTcpHandlerTest {

    private Logger logger = Logger.getLogger( Log4jSyslogTcpHandlerTest.class );

    @Test
    public void doTheJob()
        throws IOException {
        MiniSyslogTcpServer miniSyslogTcpServer = new MiniSyslogTcpServer();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File( classLoader.getResource( "log4j-tcp.xml" ).getFile() );

        StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
        for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            sb.append( line ).append( "\n" );
        }

        String config = sb.toString();
        config = config.replace( "\"514\"", "\"" + miniSyslogTcpServer.getPort() + "\"" );

        System.out.println( config );

        ( new DOMConfigurator() ).doConfigure( new StringReader( config ), LogManager.getLoggerRepository() );
        int messagesReceived = 0;

        for ( int i = 0; i < 10; i++ ) {
            logger.info( "This is an informational message TestA8 #" + i );
            logger.error( "Example of an exception TestB3", new RuntimeException( "RuntimeExceptionB3" ) );
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

        assertTrue( messagesReceived == 20 );

        String msg1 = miniSyslogTcpServer.getMessages().get( 0 );
        assertTrue( msg1.startsWith( "<134>" ) );
        assertTrue( msg1.contains( "MyMachineForSyslog" ) );
        assertTrue( msg1.contains( "MyAppNameForSyslog" ) );
        assertTrue( msg1.endsWith( "#0" ) );
    }

}
