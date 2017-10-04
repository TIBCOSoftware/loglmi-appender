/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Test;

import com.tibco.loglogic.logging.forwarders.MiniUldpServer.ReceivedUldpMessage;

/**
 * Created by lpautet on 5/31/17.
 */
public class Log4jUldpHandlerTest {

    private Logger logger = Logger.getLogger( Log4jSyslogTcpHandlerTest.class );

    @Test
    public void doTheJob()
        throws IOException {
        MiniUldpServer miniUldpServer = new MiniUldpServer();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File( classLoader.getResource( "log4j-uldp.xml" ).getFile() );

        StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
        for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            sb.append( line ).append( "\n" );
        }

        String config = sb.toString();
        config = config.replace( "\"5516\"", "\"" + miniUldpServer.getPort() + "\"" );

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
            messagesReceived = miniUldpServer.getMessages().size();
            try {
                Thread.sleep( 100 );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
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
