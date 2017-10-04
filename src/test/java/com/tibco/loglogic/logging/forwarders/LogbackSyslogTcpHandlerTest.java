/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lpautet on 5/31/17.
 */
public class LogbackSyslogTcpHandlerTest {

    private Logger logger = LoggerFactory.getLogger( LogbackSyslogTcpHandlerTest.class );

    @Test
    public void doTheJob()
        throws IOException, JoranException {
        MiniSyslogTcpServer miniSyslogTcpServer = new MiniSyslogTcpServer();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File( classLoader.getResource( "logback-tcp.xml" ).getFile() );

        StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
        for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            sb.append( line ).append( "\n" );
        }

        String config = sb.toString();
        config = config.replace( "<Port>514</Port>", "<Port>" + miniSyslogTcpServer.getPort() + "</Port>" );

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
