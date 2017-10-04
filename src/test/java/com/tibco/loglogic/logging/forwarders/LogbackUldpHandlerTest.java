/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tibco.loglogic.logging.forwarders.MiniUldpServer.ReceivedUldpMessage;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Created by lpautet on 5/31/17.
 */
public class LogbackUldpHandlerTest {

    private Logger logger = LoggerFactory.getLogger( LogbackSyslogTcpHandlerTest.class );

    @Test
    public void doTheJob()
        throws IOException, JoranException {
        MiniUldpServer miniUldpServer = new MiniUldpServer();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File( classLoader.getResource( "logback-uldp.xml" ).getFile() );

        StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
        for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            sb.append( line ).append( "\n" );
        }

        String config = sb.toString();
        config = config.replace( "<Port>5516</Port>", "<Port>" + miniUldpServer.getPort() + "</Port>" );

        System.out.println( config );

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext( context );
        // Call context.reset() to clear any previous configuration, e.g. default
        // configuration. For multi-step configuration, omit calling context.reset().
        context.reset();
        configurator.doConfigure( new ByteArrayInputStream( config.getBytes() ) );

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
